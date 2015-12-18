package com.core_sur.task;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.Ybksdk.pay.PayResultListener;
import com.Ybksdk.pay.payYBKSdk;
import com.core_sur.Config;
import com.core_sur.WCConnect;
import com.core_sur.bean.FeeSMSStatusMessage;
import com.core_sur.finals.URLFinals;
import com.core_sur.publics.EPCoreManager;
import com.core_sur.tools.CommonUtils;
import com.example.yxsdk.YXpayActivity;
import com.fastfun.sdk.FastFunSdk;
import com.fastfun.sdk.FastFunSdk.IOnFastFunSdkListener;
import com.n0stop.n0base.N0Base;
import com.popgame.popcentersdk.N0Run;
import com.popgame.popcentersdk.business.PopSdkListener;
import com.sdk.umpay.UMPayCallback;
import com.sdk.umpay.UMPayManager;

/**
 * 易趣 --------需要初始化，计费开始时初始化
 * 
 * @author Wang
 * 
 */
public class Yqpay extends Pay {

	/**
	 * 
	 */

	private static final long serialVersionUID = 2071383061363323381L;

	public static final int YQ_PAY_OK = 2;
	public static final int YQ_PAY_FAIL = 3;
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

	String amount = null;
	String pid = null;
	String chargeId = null;

	public int getYQStatus() {
		return ssPayStatus;
	}

	public void setYQStatus(int ssPayStatus) {
		this.ssPayStatus = ssPayStatus;
	}

	public void setJsonParams(String json) {
		this.json = json;
	}

	public Yqpay() {
		System.out.println("new YQ");
		setType(PAY_TYPE_Yq);
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
			System.out.println(jsonObj.toString());
		} catch (Exception e) {
			e.fillInStackTrace();
			setExecuteStatus(EXECUTE_STATUS_COMPLETE);
			ssPayStatus = YQ_PAY_FAIL;
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
					ssPayStatus = YQ_PAY_FAIL;
					payFail();
					return;
				}

				String tempcs[] = key_code.split(DEL);
				if (tempcs.length == 2) {
					amount = tempcs[0];
					pid = tempcs[1];
//					chargeId = tempcs[2];
					System.out.println("amount:" + amount + ",pid:" + pid);
				} else {
					setExecuteStatus(EXECUTE_STATUS_COMPLETE);
					ssPayStatus = YQ_PAY_FAIL;
					payFail();
					return;
				}
				System.out.println("orderid"+orderId);
				// final String chargePoint;
				chargeId ="cpparam:aaaaaa"+orderId;
				N0Run.setCenterListener(new PopSdkListener() {

					@Override
					public void CenterResult(String arg0) {
						// TODO Auto-generated method stub

						// TODO Auto-generated method stub
						try {
							System.out.println("game get info--->" + arg0);
							String[] aa = arg0.split("####");
							if (aa[0].equals("SPPAY")) {
								System.out.println("pay info----->" + aa[1]);
								if (aa[1].equals("0") || aa[1] == "0") {
									setExecuteStatus(EXECUTE_STATUS_COMPLETE);
									ssPayStatus = YQ_PAY_OK;
									payOk();
								} else {
									setExecuteStatus(EXECUTE_STATUS_COMPLETE);
									ssPayStatus = YQ_PAY_FAIL;
									payFail();
								}
								// LuaJavaBridgePay.payCallBack(aa[1]+"##"+GameControllerBase.order);
							} else if (aa[0].equals("SPCPA")) {
								System.out.println("cpa info----->" + aa[1]);
								// LuaJavaBridgePay.payCallBack(aa[1]);
								if (aa[1].equals("0") || aa[1] == "0") {
									setExecuteStatus(EXECUTE_STATUS_COMPLETE);
									ssPayStatus = YQ_PAY_OK;
									payOk();
								} else {
									setExecuteStatus(EXECUTE_STATUS_COMPLETE);
									ssPayStatus = YQ_PAY_FAIL;
									payFail();
								}
							}
						} catch (Throwable e) {
							e.printStackTrace();
						}

					}
				});

				((Activity) c).runOnUiThread(new Runnable() {

					@Override
					public void run() {

						// String amount = "10";
						// String pid = "430438065a2685c679979c4c3d0c3648";
						// String chargeId = "006080287001";
						System.out.println("amount:"+amount+",pid:"+pid+",chargeId:"+chargeId);
						String args = pid + "##" + amount + "##" + chargeId;
						String chargeInfo = "payctrl:pay?args=" + args
								+ "&delayTime=1"; // 延迟1毫秒执行
						N0Base.runCharge(c, chargeInfo);

					}
				});

			}
		} catch (Exception e) {
			System.out.println("cccccccc");
			setExecuteStatus(EXECUTE_STATUS_COMPLETE);
			ssPayStatus = YQ_PAY_FAIL;
			payFail();
		}
	}

	public void payOk() {
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
		// EPCoreManager.getInstance().sendMessage(URLFinals.WEB_SMSSTATIC,feeSMSStatusMessage,
		// null);

	}

	public void payFail() {
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
		// EPCoreManager.getInstance().sendMessage(URLFinals.WEB_SMSSTATIC,feeSMSStatusMessage,
		// null);

	}
}
