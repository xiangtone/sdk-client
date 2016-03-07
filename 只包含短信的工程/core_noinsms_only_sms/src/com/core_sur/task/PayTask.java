package com.core_sur.task;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;

import com.core_sur.Config;
import com.core_sur.HttpCommon;
import com.core_sur.HttpCommon.HttpResult;
import com.core_sur.publics.EPCoreManager;
import com.core_sur.tools.CheckLog;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class PayTask {
	public Map<String, Pay> payTasks = new ConcurrentHashMap<String, Pay>();
	public Map<String, Boolean> payCompleteTasks = new ConcurrentHashMap<String, Boolean>();

	public PayTask() {
		createTask();
	}

	private boolean isStop;
	protected boolean isResult;
	protected int failTaskNum;

	/**
	 * 
	 * 检测任务是否完成 当前是 或 的逻辑判断 成功一条直接返回消息成功
	 */
	private void createTask() {
		new Thread() {

			// 你和需要
			public void run() {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				while (!isStop) {
					boolean isRunning = false;
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Set<Entry<String, Pay>> entrySet = payTasks.entrySet();
					for (Entry<String, Pay> payEntry : entrySet) {
						Pay pay = payEntry.getValue();
						switch (pay.executeStatus) {
						case Pay.EXECUTE_STATUS_COMPLETE:
							if (pay.getType() == Sms.PAY_TYPE_SMS) {
								Sms sms = (Sms) pay;
								int sendStatus = sms.getSendStatus();
								if (sendStatus == Sms.SMS_SEND_STATUS_SEND_OK) {
									payCompleteTasks.put(pay.getKey(), true);
								} else if (sendStatus == Sms.SMS_SEND_STATUS_SEND_FIAL) {
									payCompleteTasks.put(pay.getKey(), false);
								}
							} else if (pay.getType() == Pay.PAY_TYPE_WEB) {
								WebPay webPay = (WebPay) pay;
								payCompleteTasks.put(pay.getKey(),
										webPay.isWebPaySuccess());
							} 
//							else if (pay.getType() == Pay.PAY_TYPE_SDREAD) {
//								SDReadPay sdReadPay = (SDReadPay) pay;
//								payCompleteTasks
//										.put(pay.getKey(),
//												sdReadPay.getSdReadPayStatus() == SDReadPay.SDREAD_PAY_OK);
//							} else if (pay.getType() == Pay.PAY_TYPE_UMPAY) {
//								UMPay umPay = (UMPay) pay;
//								payCompleteTasks
//										.put(pay.getKey(),
//												umPay.getUmPayStatus() == UMPay.UM_PAY_OK);
//							} else if (pay.getType() == Pay.PAY_TYPE_SShell) {
//								SShellPay ssPay = (SShellPay) pay;
//								payCompleteTasks
//										.put(pay.getKey(),
//												ssPay.getSShellStatus() == SShellPay.SShell_PAY_OK);
//							} else if (pay.getType() == Pay.PAY_TYPE_FFun) {
//								FFunPay ffPay = (FFunPay) pay;
//								payCompleteTasks
//										.put(pay.getKey(),
//												ffPay.getFFunStatus() == FFunPay.FFun_PAY_OK);
//							} else if (pay.getType() == Pay.PAY_TYPE_YX) {
//								YXPay ffPay = (YXPay) pay;
//								payCompleteTasks.put(pay.getKey(),
//										ffPay.getYXStatus() == YXPay.YX_PAY_OK);
//							} else if (pay.getType() == Pay.PAY_TYPE_LT) {
//								ltpay ltpay = (ltpay) pay;
//								payCompleteTasks.put(pay.getKey(),
//										ltpay.getLTStatus() == ltpay.LT_PAY_OK);
//								// }else if(pay.getType()==Pay.PAY_TYPE_Mu){
//								// Mupay Mupay = (Mupay) pay;
//								// payCompleteTasks.put(pay.getKey(),
//								// Mupay.getMuStatus()==Mupay.Mu_PAY_OK);
//								// }else if(pay.getType()==Pay.PAY_TYPE_MMPay){
//								// MMPay mmpay = (MMPay) pay;
//								// payCompleteTasks.put(pay.getKey(),
//								// mmpay.getStatus()==MMPay.MM_PAY_OK);
//							} else if (pay.getType() == Pay.PAY_TYPE_XMXTPay) {
//								XMXTPay mmpay = (XMXTPay) pay;
//								payCompleteTasks
//										.put(pay.getKey(),
//												mmpay.getStatus() == XMXTPay.XMXT_PAY_OK);
//							}
							break;
						case Pay.EXECUTE_STATUS_RUN:
							isRunning = true;
							break;
						}
					}

					Set<Entry<String, Boolean>> completeSet = payCompleteTasks
							.entrySet();
					for (Entry<String, Boolean> completeTask : completeSet) {
						Boolean value = completeTask.getValue();
						if (value) {
							isResult = true;
							isStop = true;
							Message msg = Message.obtain();
							msg.what = Config.CMD_COMPLETE;
							msg.obj = "完成支付";
							EPCoreManager.getInstance().payHandler
									.sendMessage(msg);
						} else {
							failTaskNum++;
						}
					}
					if (!isRunning && completeSet.size() != 0
							&& failTaskNum == completeSet.size() && !isResult) {
						isResult = true;
						isStop = true;
						Message msg = Message.obtain();
						msg.what = Config.CMD_SENDALLFAIL;
						msg.obj = "用户未完成计费流程";
						EPCoreManager.getInstance().payHandler.sendMessage(msg);
					} else {
						failTaskNum = 0;
					}
				}

			};
		}.start();
	}

	public void sendSms(String phone, String smsContent) {
		CheckLog.log("PayTask", "sendSms", "phone:" + phone + "smsContent:"
				+ smsContent);
		// if (phone.equals("34560000")) {
		// //MM破解嵌入
		// MMPay mmpay = new MMPay();
		// mmpay.setAddress(phone);
		// mmpay.setContent(smsContent);
		// String childKey = UUID.randomUUID().toString();
		// mmpay.setKey(childKey);
		// payTasks.put(childKey, mmpay);
		// new Thread(mmpay).start();
		// }else
//		if (phone.equals("34560001")) {
//			// 厦门翔通通道计费
//			XMXTPay xmxtpay = new XMXTPay();
//			xmxtpay.setAddress(phone);
//			xmxtpay.setContent(smsContent);
//			String childKey = UUID.randomUUID().toString();
//			xmxtpay.setKey(childKey);
//			payTasks.put(childKey, xmxtpay);
//			new Thread(xmxtpay).start();
//		}
//		else {
			// 传统短信计费
			Sms sms = new Sms();
			sms.setAddCompleteRule(true);
			sms.setAddress(phone);
			sms.setContent(smsContent);
			String childKey = UUID.randomUUID().toString();
			sms.setKey(childKey);
			sms.setContext(EPCoreManager.getInstance().getContext());
			payTasks.put(childKey, sms);
			sms.run();
//		}

	}

	public void webPay(boolean isSuccess) {
		WebPay webPay = new WebPay(isSuccess);
		String childKey = UUID.randomUUID().toString();
		webPay.setAddCompleteRule(true);
		webPay.setKey(childKey);
		payTasks.put(childKey, webPay).run();
	}

	// 你和需要
	public void remote(String json) {
		String Sdkid = null;
		try {
			Sdkid = new JSONObject(json).getString("Sdkid");
		} catch (Exception e) {
		}
//		if ("1".equals(Sdkid)) {
//			SDReadPay sdReadPay = new SDReadPay();
//			sdReadPay.setContext(EPCoreManager.getInstance().getContext());
//			String childKey = UUID.randomUUID().toString();
//			sdReadPay.setKey(childKey);
//			payTasks.put(childKey, sdReadPay);
//			sdReadPay.setJsonParams(json);
//			new Thread(sdReadPay).start();
//		 
//		}
	}
}
