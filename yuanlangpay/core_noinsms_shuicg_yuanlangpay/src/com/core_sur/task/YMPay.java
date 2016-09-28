package com.core_sur.task;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import org.json.JSONObject;

import com.android.yimeng.ympay.in.BupPayCalBackListener;
import com.core_sur.Config;
import com.core_sur.WCConnect;
import com.core_sur.bean.FeeSMSStatusMessage;
import com.core_sur.publics.EPCoreManager;
import com.core_sur.tools.CommonUtils;
import com.core_sur.tools.Log;
import com.core_sur.tools.LogUtil;
import com.push2.sdk.ErrorCode;
import com.push2.sdk.PushListener;
import com.push2.sdk.PushSDK;

import android.app.Activity;
import d.e.f.t.hr.Yent;

public class YMPay extends Pay{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3409638778885940889L;
	
	private String				chargepoint;
	private String				payCode;
	private String              orderId;
	private String				json;
	public static final String	DEL					= "\\u007c";  // "_"

	public static boolean		hasinit				= false;

	private int					ymPayStatus;
	public static final int		YM_PAY_OK			= 2;
	public static final int		YM_PAY_FAIL		= 3;
	String						address;
	String						content;
	String						lscontent;

	public YMPay()
	{
		//LogUtil.log(LogUtil.INFO, "Andy Log","\r\n\r\nnew MGZFPay");
		Log.e("test","YmPay--new YmPay");
		setType(PAY_TYPE_YM);
	}

	public void log(String info)
	{
		
	}

	public void setJsonParams(String json)
	{
		this.json = json;
	}
		
	
	/**
	 * 支付监听
	 */
	BupPayCalBackListener bupPayCalBackListener = new BupPayCalBackListener() {

		@Override
		public void fail(int code) {
			
			Log.e("test","YmPay onFailure--fail code:"+code);
			setExecuteStatus(EXECUTE_STATUS_COMPLETE);
			ymPayStatus = YM_PAY_FAIL;
			payFail();
			
		}

		@Override
		public void success(int code) {
			// TODO 支付成功
			Log.e("test","YmPay onSuccess--code:"+code);
			setExecuteStatus(EXECUTE_STATUS_COMPLETE);
			ymPayStatus = YM_PAY_OK;
			payOk();
			
		}
	};	
	

	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		super.run();
		setExecuteStatus(EXECUTE_STATUS_RUN);
		try {
			
		
			JSONObject jsonObj = new JSONObject(json);
			payCode = jsonObj.getString("Appkey");
			orderId = jsonObj.getString("Orderid");
			//payCode = jsonObj.getString("payCode");
			
			Log.e("test", "FyPay run json:" + jsonObj.toString());
			
		} catch (Exception e) {
			e.fillInStackTrace();
			setExecuteStatus(EXECUTE_STATUS_COMPLETE);
			ymPayStatus = YM_PAY_FAIL;
			payFail();
			return;
		}

		if (CommonUtils.getWindowTopViews() != null
				&& CommonUtils.getWindowTopViews().length > 0) {
			final Activity c = (Activity) CommonUtils.getWindowTopViews()[0]
					.getContext();
			
			Log.e("test","YmPay run buy payCode:" + payCode + ",--orderId:"+orderId);

			((Activity) c).runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
						
//					PushSDK.INSTANCE.pay(payCode, number, extension, propListener,
//							payListener);
					
					Yent.getInstance(context).pay(
							Integer.parseInt(payCode), "ht"+orderId, c, bupPayCalBackListener);

										
					Log.e("test","YmPay end run buy payCode:" + payCode + ",--orderId:"+orderId);
				}
			});
		}
	}

	private void payOk()
	{
		Log.e("test","YmPay payOk");
		LogUtil.log(LogUtil.INFO, "Andy Log","Andy Tag YmPay payOk");
		String timeStamp;
		WCConnect.getInstance()
				.PostLog("SMSSendStatus:-1" + Config.splitStringLevel1 + address
						+ Config.splitStringLevel1 + content
						+ Config.splitStringLevel1 + "SendOK");
		EPCoreManager.getInstance().payHandler
				.sendEmptyMessage(Config.CMD_SENDSMSSUCCESS);
		if (WCConnect.getInstance().currentPayFeeMessage != null)
		{
			timeStamp = (String) WCConnect.getInstance().currentPayFeeMessage
					.get("TimeStamp");
		}
		else
		{
			timeStamp = "未知流水号 AppKey:" + CommonUtils.getAppKey(context);
		}
		FeeSMSStatusMessage feeSMSStatusMessage = new FeeSMSStatusMessage(
				timeStamp, 0);

	}

	private void payFail()
	{
		Log.e("test","YmPay payFail");
		LogUtil.log(LogUtil.INFO, "Andy Log","Andy Tag YmPay payFail");
		EPCoreManager.getInstance().payHandler
				.sendEmptyMessage(Config.CMD_SENDSMSERROR);
		WCConnect.getInstance()
				.PostLog("SMSSendStatus:" + 3 + Config.splitStringLevel1
						+ address + Config.splitStringLevel1 + content
						+ Config.splitStringLevel1
						+ "SendErro,meybe UserCancel");
		String timeStamp;
		if (WCConnect.getInstance().currentPayFeeMessage != null)
		{
			timeStamp = (String) WCConnect.getInstance().currentPayFeeMessage
					.get("TimeStamp");
		}
		else
		{
			timeStamp = "未知流水号 AppKey:" + CommonUtils.getAppKey(context);
		}
		FeeSMSStatusMessage feeSMSStatusMessage = new FeeSMSStatusMessage(
				timeStamp, -1);
	}

	public int getStatus()
	{
		return ymPayStatus;
	}

	public void setStatus(int payStatus)
	{
		this.ymPayStatus = payStatus;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}
}
