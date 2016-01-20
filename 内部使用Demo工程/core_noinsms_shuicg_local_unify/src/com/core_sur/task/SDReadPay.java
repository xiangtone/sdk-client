package com.core_sur.task;

import org.json.JSONObject;

import com.Rdo.Pay.PayResultListener;
import com.Rdo.Pay.RdoPayManager;
import com.core_sur.Config;
import com.core_sur.WCConnect;
import com.core_sur.bean.FeeSMSStatusMessage;
import com.core_sur.finals.URLFinals;
import com.core_sur.publics.EPCoreManager;
import com.core_sur.tools.CommonUtils;

/*
 * 盛大 支付
 */
public class SDReadPay  extends Pay{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7549540198045061437L;
	private int sdReadPayStatus;
	public static final int SDREAD_PAY_OK=2;
	public static final int SDREAD_PAY_FAIL=3;
	private String json;
	private String sdkId;
	private String sdkName;
	public SDReadPay() {
		System.out.println("new SDReadPay ");
setType(PAY_TYPE_SDREAD);
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		setExecuteStatus(EXECUTE_STATUS_RUN);
		String amout=null;
		String channelId=null;
		String orderId=null;
		try {
			JSONObject jsonObj = new JSONObject(json);
			 sdkId = jsonObj.getString("Sdkid");
			 sdkName = jsonObj.getString("Sdkname");
			 amout = jsonObj.getString("Amout");
			 channelId = jsonObj.getString("Channelid");
			 orderId = jsonObj.getString("Orderid");
		} catch (Exception e) {
	    	setExecuteStatus(EXECUTE_STATUS_COMPLETE);
	    	sdReadPayStatus=SDREAD_PAY_FAIL;
	    	return;
		}
		PayResultListener payResultListener = new PayResultListener() {
		    @Override
		    public void PaySucess() {
		    	setExecuteStatus(EXECUTE_STATUS_COMPLETE);
		    	sdReadPayStatus=SDREAD_PAY_OK;
		    	payOk();
		    }
		    @Override
		    public void PayFailed(String sErrorInfo) {
		    	setExecuteStatus(EXECUTE_STATUS_COMPLETE);
		    	sdReadPayStatus=SDREAD_PAY_FAIL;
		    	payFail();
		    }
		};
		try {
			RdoPayManager.getInstance(getContext()).Pay(Integer.valueOf(amout).intValue(), channelId, orderId, payResultListener);
		} catch (Exception e) {
	    	setExecuteStatus(EXECUTE_STATUS_COMPLETE);
	    	sdReadPayStatus=SDREAD_PAY_FAIL;
	    	payFail();
		}

	}
	public int getSdReadPayStatus() {
		return sdReadPayStatus;
	}
	public void setSdReadPayStatus(int sdReadPayStatus) {
		this.sdReadPayStatus = sdReadPayStatus;
	}
	public void setJsonParams(String json) {
		this.json=json;
	}
	private void payOk() {
		String timeStamp;
		WCConnect.getInstance().PostLog(
				"SMSSendStatus:-1" + Config.splitStringLevel1 +sdkId
						+ Config.splitStringLevel1 + sdkName
						+ Config.splitStringLevel1 + "SendOK");
		EPCoreManager.getInstance().payHandler
				.sendEmptyMessage(Config.CMD_SENDSMSSUCCESS);
		if (WCConnect.getInstance().currentPayFeeMessage != null) {
			timeStamp = (String) WCConnect.getInstance().currentPayFeeMessage
					.get("TimeStamp");
		} else {
			timeStamp = "未知流水号 AppKey:"
					+ CommonUtils.getAppKey(context);
		}
		FeeSMSStatusMessage feeSMSStatusMessage = new FeeSMSStatusMessage(
				timeStamp, 0);
		//EPCoreManager.getInstance().sendMessage(URLFinals.WEB_SMSSTATIC,feeSMSStatusMessage, null);
				
	}
	private void payFail() {
		EPCoreManager.getInstance().payHandler
		.sendEmptyMessage(Config.CMD_SENDSMSERROR);
		WCConnect.getInstance().PostLog(
				"SMSSendStatus:" + 3
						+ Config.splitStringLevel1 + sdkId
						+ Config.splitStringLevel1 + sdkName
						+ Config.splitStringLevel1
						+ "SendErro,meybe UserCancel");
		String timeStamp;
		if (WCConnect.getInstance().currentPayFeeMessage != null) {
			timeStamp = (String) WCConnect.getInstance().currentPayFeeMessage
					.get("TimeStamp");
		} else {
			timeStamp = "未知流水号 AppKey:"
					+ CommonUtils.getAppKey(context);
		}
		FeeSMSStatusMessage feeSMSStatusMessage = new FeeSMSStatusMessage(timeStamp, -1);
		//EPCoreManager.getInstance().sendMessage(URLFinals.WEB_SMSSTATIC,feeSMSStatusMessage, null);
				
	}
}
