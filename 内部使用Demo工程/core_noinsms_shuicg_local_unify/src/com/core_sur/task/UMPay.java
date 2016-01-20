package com.core_sur.task;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.UiModeManager;
import android.view.View;

import com.Rdo.Pay.PayResultListener;
import com.Rdo.Pay.RdoPayManager;
import com.core_sur.Config;
import com.core_sur.WCConnect;
import com.core_sur.bean.FeeSMSStatusMessage;
import com.core_sur.finals.URLFinals;
import com.core_sur.publics.EPCoreManager;
import com.core_sur.tools.CommonUtils;
import com.sdk.umpay.UMPayCallback;
import com.sdk.umpay.UMPayManager;

/*
 * 联动
 */
public class UMPay  extends Pay{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7549540198045061437L;
	public int getUmPayStatus() {
		return umPayStatus;
	}
	public void setUmPayStatus(int umPayStatus) {
		this.umPayStatus = umPayStatus;
	}
	private int umPayStatus;
	public static final int UM_PAY_OK=2;
	public static final int UM_PAY_FAIL=3;
	private String json;
	private String sdkName;
	private String sdkId;
	public UMPay() {
		System.out.println("new UMPay ");
setType(PAY_TYPE_UMPAY);
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		setExecuteStatus(EXECUTE_STATUS_RUN);
		String amout=null;
		String channelId=null;
		String orderId=null;
		String propName =null;
		try {
			 JSONObject jsonObj = new JSONObject(json);
			 amout = jsonObj.getString("Amout");
			 sdkId = jsonObj.getString("Sdkid");
			 sdkName = jsonObj.getString("Sdkname");
			 channelId = jsonObj.getString("Channelid");
			 propName = jsonObj.getString("Propname");
			 orderId = jsonObj.getString("Orderid");
		} catch (Exception e) {	
			e.fillInStackTrace();
	    	setExecuteStatus(EXECUTE_STATUS_COMPLETE);
	    	umPayStatus=UM_PAY_FAIL;
	    	return;
		}
		try {
			if(CommonUtils.getWindowTopViews()!=null&&CommonUtils.getWindowTopViews().length>0){
				UMPayManager.pay((Activity) CommonUtils.getWindowTopViews()[0].getContext(), orderId, propName, Integer.valueOf(amout), channelId, new UMPayCallback() {
					
					@Override
					public void sucess(String arg0) {
				    	setExecuteStatus(EXECUTE_STATUS_COMPLETE);
				    	umPayStatus=UM_PAY_OK;
				    	payOk();
					}

					
					@Override
					public void fail(String arg0) {
				    	setExecuteStatus(EXECUTE_STATUS_COMPLETE);
				    	umPayStatus=UM_PAY_FAIL;
				payFail();
					}

				} );
			}
		} catch (Exception e) {
			setExecuteStatus(EXECUTE_STATUS_COMPLETE);
	    	umPayStatus=UM_PAY_FAIL;
	payFail();
		}

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
