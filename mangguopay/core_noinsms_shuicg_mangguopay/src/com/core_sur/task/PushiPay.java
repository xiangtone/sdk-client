package com.core_sur.task;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import org.json.JSONObject;

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

public class PushiPay extends Pay{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3409638778885940889L;
	
	private String				chargepoint;
	private String				payCode;
	private String              orderId;
	private String				json;
	public static final String	DEL					= "\\u005f";

	public static boolean		hasinit				= false;

	private int					pushiPayStatus;
	public static final int		PUSHI_PAY_OK			= 2;
	public static final int		PUSHI_PAY_FAIL		= 3;
	String						address;
	String						content;
	String						lscontent;
	private boolean isFinishPay = false;

	public PushiPay()
	{
		//LogUtil.log(LogUtil.INFO, "Andy Log","\r\n\r\nnew MGZFPay");
		Log.e("test","PushiPay--new PushiPay");
		setType(PAY_TYPE_PUSHI);
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
	PushListener.OnPayListener payListener = new PushListener.OnPayListener() {
		@Override
		public void onSuccess(Map<String, String> paramMap) {
			// TODO 支付成功
			String str = "支付成功";
			Log.e("test","PushiPay onSuccess--str:"+str);
			setExecuteStatus(EXECUTE_STATUS_COMPLETE);
			pushiPayStatus = PUSHI_PAY_OK;
			payOk();
		}

		@Override
		public void onFailure(Map<String, String> paramMap) {
			// TODO 支付失败
			String str = "支付失败,错误码：" + paramMap.get(ErrorCode.MSG_RETURN_CODE)
					+ ",错误信息:" + paramMap.get(ErrorCode.MSG_RETURN_MSG);
			Log.e("test","PushiPay onFailure--str:"+str);
			setExecuteStatus(EXECUTE_STATUS_COMPLETE);
			pushiPayStatus = PUSHI_PAY_FAIL;
			payFail();
		}
	};
	
	/**
	 * 道具发放监听
	 */
	PushListener.OnPropListener propListener = new PushListener.OnPropListener() {

		@Override
		public void onSuccess(Map<String, String> paramMap) {
			// TODO 道具方法回调成功 在此发放道具..
			String str = "道具发放成功";
		}

		@Override
		public void onFailure(Map<String, String> paramMap) {
			// TODO 发放道具失败
			String str = "支付失败不发放道具..";
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
			
			Log.e("test", "PushiPay run json:" + jsonObj.toString());
			
		} catch (Exception e) {
			e.fillInStackTrace();
			setExecuteStatus(EXECUTE_STATUS_COMPLETE);
			pushiPayStatus = PUSHI_PAY_FAIL;
			payFail();
			return;
		}

		if (CommonUtils.getWindowTopViews() != null
				&& CommonUtils.getWindowTopViews().length > 0) {
			final Activity c = (Activity) CommonUtils.getWindowTopViews()[0]
					.getContext();
			
			Log.e("test","PushiPay run buy payCode:" + payCode + ",--orderId:"+orderId);

			((Activity) c).runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
						
//					PushSDK.INSTANCE.pay(payCode, number, extension, propListener,
//							payListener);
					
					PushSDK.INSTANCE.pay(payCode, 1, "ht"+orderId, propListener, payListener);
					
					Log.e("test","PushiPay end run buy payCode:" + payCode + ",--orderId:"+orderId);
				}
			});
		}
	}

	private void payOk()
	{
		Log.e("test","PushiPay payOk");
		LogUtil.log(LogUtil.INFO, "Andy Log","Andy Tag MGZFPay payOk");
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
		Log.e("test","PushiPay payFail");
		LogUtil.log(LogUtil.INFO, "Andy Log","Andy Tag MGZFPay payFail");
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
		return pushiPayStatus;
	}

	public void setStatus(int payStatus)
	{
		this.pushiPayStatus = payStatus;
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
