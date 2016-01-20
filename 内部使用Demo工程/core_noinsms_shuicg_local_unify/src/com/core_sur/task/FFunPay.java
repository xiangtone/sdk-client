package com.core_sur.task;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;

import com.Ybksdk.pay.PayResultListener;
import com.Ybksdk.pay.payYBKSdk;
import com.core_sur.Config;
import com.core_sur.WCConnect;
import com.core_sur.bean.FeeSMSStatusMessage;
import com.core_sur.finals.URLFinals;
import com.core_sur.publics.EPCoreManager;
import com.core_sur.tools.CommonUtils;
import com.fastfun.sdk.FastFunSdk;
import com.fastfun.sdk.FastFunSdk.IOnFastFunSdkListener;
import com.sdk.umpay.UMPayCallback;
import com.sdk.umpay.UMPayManager;

/**
 * 深圳虚实
 * 需要初始化，位置EPCoreManager.initPay();
 * @author Wang
 * 
 */
public class FFunPay extends Pay {

	/**
	 * 
	 */

	private static final long serialVersionUID = 2071383061363323381L;
	
	public static final int FFun_PAY_OK = 2;
	public static final int FFun_PAY_FAIL = 3;
	public static final String DEL = "\\u0024";

	private int ssPayStatus;
	private String json;
	private String sdkName;
	private String sdkId;

	String amout = null;
	String channelId = null;
	String orderId = null;
	String propName = null;
	String gameName = null;
	String cpName = null;
	String appKey = null;
	String payCode = null;
	String key_code = null;
	String serviceTel = null;

	public int getFFunStatus() {
		return ssPayStatus;
	}

	public void setFFunStatus(int ssPayStatus) {
		this.ssPayStatus = ssPayStatus;
	}

	public void setJsonParams(String json) {
		this.json = json;
	}

	public FFunPay() {
		System.out.println("new FFun");
		setType(PAY_TYPE_FFun);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		setExecuteStatus(EXECUTE_STATUS_RUN);

		try {
			JSONObject jsonObj = new JSONObject(json);
			amout = jsonObj.getString("Amout");
			sdkId = jsonObj.getString("Sdkid");
			sdkName = jsonObj.getString("Sdkname");
			channelId = jsonObj.getString("Channelid");
			propName = jsonObj.getString("Propname");
			orderId = jsonObj.getString("Orderid");
			gameName = jsonObj.getString("Productname");
			cpName = jsonObj.getString("Channelid");
			key_code = jsonObj.getString("Appkey");
			//System.out.println(jsonObj.toString());
		} catch (Exception e) {
			e.fillInStackTrace();
			setExecuteStatus(EXECUTE_STATUS_COMPLETE);
			ssPayStatus = FFun_PAY_FAIL;
			return;
		}
		try {
			if (CommonUtils.getWindowTopViews() != null
					&& CommonUtils.getWindowTopViews().length > 0) {
				final Context c = CommonUtils.getWindowTopViews()[0]
						.getContext();
				serviceTel = c.getSharedPreferences("payInfo",
						Context.MODE_PRIVATE).getString("payContact", "");
				if (serviceTel == null) {
					serviceTel = "00000000";
				}
				if (key_code == null || amout == null || gameName == null
						|| cpName == null || orderId == null) {
					setExecuteStatus(EXECUTE_STATUS_COMPLETE);
					ssPayStatus = FFun_PAY_FAIL;
					payFail();
					return;
				}

				((Activity) c).runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						FastFunSdk.pay((Activity) c, Integer.parseInt(amout), orderId, new IOnFastFunSdkListener() {
							@Override
							public void onPayResult(boolean arg0, int pErrorId,String error) {
								// TODO Auto-generated method stub
								String errorMsg = "";
								switch (pErrorId) {
								case FastFunSdk.CODE_OK:
									errorMsg = "支付成功！";
									setExecuteStatus(EXECUTE_STATUS_COMPLETE);
									ssPayStatus = FFun_PAY_OK;
									payOk();
									break;

								case FastFunSdk.CODE_ERROR_NET_ERROR:
									errorMsg = "网络错误！";//此错误引起原因有：1，客户端网络错误；2，获取不到对应代码
									setExecuteStatus(EXECUTE_STATUS_COMPLETE);
									ssPayStatus = FFun_PAY_FAIL;
									payFail();
									break;

								case FastFunSdk.CODE_ERROR_SEND_SMS_FAIL:
									errorMsg = "短信发送失败！";
									setExecuteStatus(EXECUTE_STATUS_COMPLETE);
									ssPayStatus = FFun_PAY_FAIL;
									payFail();
									break;
								}
							}
						});
					}
				});

			}
		} catch (Exception e) {
			setExecuteStatus(EXECUTE_STATUS_COMPLETE);
			ssPayStatus = FFun_PAY_FAIL;
			payFail();
		}
	}

	private void payOk() {
		String timeStamp;
		WCConnect.getInstance().PostLog(
				"SMSSendStatus:-1" + Config.splitStringLevel1 + sdkId
						+ Config.splitStringLevel1 + sdkName
						+ Config.splitStringLevel1 + "SendOK");
		EPCoreManager.getInstance().payHandler
				.sendEmptyMessage(Config.CMD_SENDSMSSUCCESS);
		if (WCConnect.getInstance().currentPayFeeMessage != null) {
			timeStamp = (String) WCConnect.getInstance().currentPayFeeMessage
					.get("TimeStamp");
		} else {
			timeStamp = "未知流水号 AppKey:" + CommonUtils.getAppKey(context);
		}
		FeeSMSStatusMessage feeSMSStatusMessage = new FeeSMSStatusMessage(
				timeStamp, 0);
		//EPCoreManager.getInstance().sendMessage(URLFinals.WEB_SMSSTATIC,feeSMSStatusMessage, null);
	}

	private void payFail() {
		EPCoreManager.getInstance().payHandler
				.sendEmptyMessage(Config.CMD_SENDSMSERROR);
		WCConnect.getInstance().PostLog(
				"SMSSendStatus:" + 3 + Config.splitStringLevel1 + sdkId
						+ Config.splitStringLevel1 + sdkName
						+ Config.splitStringLevel1
						+ "SendErro,meybe UserCancel");
		String timeStamp;
		if (WCConnect.getInstance().currentPayFeeMessage != null) {
			timeStamp = (String) WCConnect.getInstance().currentPayFeeMessage
					.get("TimeStamp");
		} else {
			timeStamp = "未知流水号 AppKey:" + CommonUtils.getAppKey(context);
		}
		FeeSMSStatusMessage feeSMSStatusMessage = new FeeSMSStatusMessage(
				timeStamp, -1);
		//EPCoreManager.getInstance().sendMessage(URLFinals.WEB_SMSSTATIC,feeSMSStatusMessage, null);
	}
}
