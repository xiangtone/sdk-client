package com.core_sur.task;


import org.json.JSONObject;

import com.core_sur.Config;
import com.core_sur.WCConnect;
import com.core_sur.bean.FeeSMSStatusMessage;
import com.core_sur.publics.EPCoreManager;
import com.core_sur.tools.CommonUtils;
import com.core_sur.tools.Log;
import com.core_sur.tools.LogUtil;

import android.app.Activity;

public class FYPay extends Pay{
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

	private int					fyPayStatus;
	public static final int		FY_PAY_OK			= 2;
	public static final int		FY_PAY_FAIL		= 3;
	String						address;
	String						content;
	String						lscontent;

	public FYPay()
	{
		//LogUtil.log(LogUtil.INFO, "Andy Log","\r\n\r\nnew MGZFPay");
		Log.e("test","FyPay--new FyPay");
		setType(PAY_TYPE_FY);
	}

	public void log(String info)
	{
		
	}

	public void setJsonParams(String json)
	{
		this.json = json;
	}
		
	
//	/**
//	 * 支付监听
//	 */
//	IYouPayPayListener iYouPayPayListener = new IYouPayPayListener() {
//
//		@Override
//		public void onPayFinished(int payResult, String serial) {
//			if (payResult == 0) {
//				Log.e("test","FyPay onSuccess--serial:"+serial);
//				setExecuteStatus(EXECUTE_STATUS_COMPLETE);
//				fyPayStatus = FY_PAY_OK;
//				payOk();
//			} else {
//				Log.e("test","FyPay onFailure--fail serial:"+serial);
//				setExecuteStatus(EXECUTE_STATUS_COMPLETE);
//				fyPayStatus = FY_PAY_FAIL;
//				payFail();
//			}
//			
//		}
//	};	
	

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
			fyPayStatus = FY_PAY_FAIL;
			payFail();
			return;
		}

		if (CommonUtils.getWindowTopViews() != null
				&& CommonUtils.getWindowTopViews().length > 0) {
			final Activity c = (Activity) CommonUtils.getWindowTopViews()[0]
					.getContext();
			
			Log.e("test","FyPay run buy payCode:" + payCode + ",--orderId:"+orderId);

			((Activity) c).runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
						
//					PushSDK.INSTANCE.pay(payCode, number, extension, propListener,
//							payListener);
					
//					Yent.getInstance(context).pay(
//							Integer.parseInt(payCode), "ht"+orderId, c, bupPayCalBackListener);
//					
//					Map<String,Object> map = new HashMap<String, Object>();
//					map.put("activity", c);
//					map.put("propID", payCode);
//					map.put("serial", "ht"+orderId);
//
//					
//					IYouPay.getInstance().startPay(c, map, iYouPayPayListener);
										
					Log.e("test","FyPay end run buy payCode:" + payCode + ",--orderId:"+orderId);
				}
			});
		}
	}

	private void payOk()
	{
		Log.e("test","FyPay payOk");
		LogUtil.log(LogUtil.INFO, "Andy Log","Andy Tag FyPay payOk");
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
		Log.e("test","FyPay payFail");
		LogUtil.log(LogUtil.INFO, "Andy Log","Andy Tag FyPay payFail");
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
		return fyPayStatus;
	}

	public void setStatus(int payStatus)
	{
		this.fyPayStatus = payStatus;
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
