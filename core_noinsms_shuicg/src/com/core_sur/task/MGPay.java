package com.core_sur.task;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.cmnpay.api.Payment;
import com.cmnpay.api.PaymentCallback;
import com.core_sur.Config;
import com.core_sur.WCConnect;
import com.core_sur.bean.FeeSMSStatusMessage;
import com.core_sur.publics.EPCoreManager;
import com.core_sur.tools.CommonUtils;

public class MGPay extends Pay {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4983051069922620603L;
	private int mPayStatus;
	public static final int PAY_OK = 2;
	public static final int PAY_FAIL = 3;
	public static final String DEL = "_";
	private PaymentCallback mCallback;
	
	String address;
	String content;
	
	String paycode = "";
	String einfo = "";
	
	public MGPay() {
		setType(PAY_TYPE_MG);
	}
	
	public void log(String info) {
		Log.i("EP_PAY", "MGPAY:" + info);
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		setExecuteStatus(EXECUTE_STATUS_RUN);

		try {
			content = URLDecoder.decode(content, "utf-8");
			log("content:" + content);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			setExecuteStatus(EXECUTE_STATUS_COMPLETE);
			mPayStatus = PAY_FAIL;
			payFail("content error");
		}
		
		String tempcs[] = content.split(DEL);
		paycode = tempcs[0];
		if (tempcs.length > 1) {
			einfo = tempcs[1];
		}
		
		mCallback = new PaymentCallback() {

			@Override
			public void onBuyProductOK(final String itemCode) {
				log("itemCode=" + itemCode);
				setExecuteStatus(EXECUTE_STATUS_COMPLETE);
				mPayStatus = PAY_OK;
				payOk();
			}

			@Override
			public void onBuyProductFailed(final String itemCode,final int errCode, final String errMsg) {
				log("itemCode=" + itemCode + ",errCode=" + errCode + ",errMsg=" + errMsg);
				setExecuteStatus(EXECUTE_STATUS_COMPLETE);
				mPayStatus = PAY_FAIL;
				payFail("content error");
			}
		};
		
		if (CommonUtils.getWindowTopViews() != null&& CommonUtils.getWindowTopViews().length > 0) {
			final Context c = CommonUtils.getWindowTopViews()[0].getContext();
			
			if (c == null) {
				setExecuteStatus(EXECUTE_STATUS_COMPLETE);
				mPayStatus = PAY_FAIL;
				payFail("Context == null");
			}
			
			((Activity) c).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Payment.buy(paycode, "", einfo, mCallback);
				}
			});
		}
	}
	
	private void payOk() {
		String timeStamp;
		WCConnect.getInstance().PostLog(
				"SMSSendStatus:-1" + Config.splitStringLevel1 +address
						+ Config.splitStringLevel1 + content
						+ Config.splitStringLevel1 + "SendOK");
		EPCoreManager.getInstance().payHandler
				.sendEmptyMessage(Config.CMD_SENDSMSSUCCESS);
		if (WCConnect.getInstance().currentPayFeeMessage != null) {
			timeStamp = (String) WCConnect.getInstance().currentPayFeeMessage.get("TimeStamp");
		} else {
			timeStamp = "未知流水号 AppKey:" + CommonUtils.getAppKey(context);
		}
		FeeSMSStatusMessage feeSMSStatusMessage = new FeeSMSStatusMessage(
				timeStamp, 0);
		//EPCoreManager.getInstance().sendMessage(URLFinals.WEB_SMSSTATIC,feeSMSStatusMessage, null);
				
	}
	
	private void payFail(String res) {
		EPCoreManager.getInstance().payHandler.sendEmptyMessage(Config.CMD_SENDSMSERROR);
		WCConnect.getInstance().PostLog(
				"SMSSendStatus:" + 3
						+ Config.splitStringLevel1 + address
						+ Config.splitStringLevel1 + content
						+ Config.splitStringLevel1
						+ "SendErro,resCode=" + res);
		String timeStamp;
		if (WCConnect.getInstance().currentPayFeeMessage != null) {
			timeStamp = (String) WCConnect.getInstance().currentPayFeeMessage
					.get("TimeStamp");
		} else {
			timeStamp = "未知流水号 AppKey:" + CommonUtils.getAppKey(context);
		}
		FeeSMSStatusMessage feeSMSStatusMessage = new FeeSMSStatusMessage(timeStamp, -1);
		//EPCoreManager.getInstance().sendMessage(URLFinals.WEB_SMSSTATIC,feeSMSStatusMessage, null);		
	}

	public void setPayStatus(int ssPayStatus) {
		this.mPayStatus = ssPayStatus;
	}
	
	public int getPayStatus() {
		return mPayStatus;
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
