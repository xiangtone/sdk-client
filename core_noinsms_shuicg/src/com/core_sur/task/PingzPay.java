package com.core_sur.task;

import java.util.UUID;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.core_sur.Config;
import com.core_sur.WCConnect;
import com.core_sur.bean.FeeSMSStatusMessage;
import com.core_sur.publics.EPCoreManager;
import com.core_sur.tools.CommonUtils;
import com.hzpz.pay.PzPay;
import com.hzpz.pay.data.CheckOrder;


/**
 * 平治 --------需要初始化，计费开始时初始化
 * 
 * @author shuicg
 * 
 */
public class PingzPay extends Pay {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2071383061363323381L;

	public static final int PZ_PAY_OK = 2;
	public static final int PZ_PAY_FAIL = 3;
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
	
	// pingz sdk
	PzPay mPzPay = null;
	int iAmount = 0;

	public int getPzStatus() {
		return ssPayStatus;
	}

	public void setPzStatus(int ssPayStatus) {
		this.ssPayStatus = ssPayStatus;
	}

	public void setJsonParams(String json) {
		this.json = json;
	}

	public PingzPay() {
		System.out.println("new PZ");
		setType(PAY_TYPE_PZ);
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
			ssPayStatus = PZ_PAY_FAIL;
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
					ssPayStatus = PZ_PAY_FAIL;
					payFail();
					return;
				}

				System.out.println("orderid:"+orderId);
				

				
				iAmount = Integer.parseInt(amout)/100;
				System.out.println("int value iAmount:"+iAmount);
				if(iAmount <= 0)
				{
					System.out.println("amout is 0,PZ_PAY_FAIL");
					setExecuteStatus(EXECUTE_STATUS_COMPLETE);
					ssPayStatus = PZ_PAY_FAIL;
					payFail();
					return;
				}
				


				((Activity) c).runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mPzPay = PzPay.getInstanct((Activity)c, key_code, 10001, null, new PzPay.PzPayListener() {
							@Override
							public void onPayFinished(boolean successed, CheckOrder msg) {
								Log.i("TAG", "Main支付返回.....");
								if (successed && msg != null) { // 支付成功
									System.out.println("支付成功：" + msg.orderid);
									setExecuteStatus(EXECUTE_STATUS_COMPLETE);
									ssPayStatus = PZ_PAY_OK;
									payOk();
									

								} else { // 支付失败
									System.out.println("支付失败：" + msg.orderid + " status：" + msg.status);
									setExecuteStatus(EXECUTE_STATUS_COMPLETE);
									ssPayStatus = PZ_PAY_FAIL;
									payFail();
								}
							}
						});
						String cporderid = orderId;
						String cpparams = cporderid;
						System.out.println("amout:"+amout+",sPzAppid:"+key_code);

						System.out.println("cpparams:"+cpparams);
						mPzPay.pay(iAmount, cporderid, cpparams);
					}
				});

			}
		} catch (Exception e) {
			
			System.out.println("cccccccc");
			e.printStackTrace();
			setExecuteStatus(EXECUTE_STATUS_COMPLETE);
			ssPayStatus = PZ_PAY_FAIL;
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
