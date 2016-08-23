package com.core_sur.task;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import org.json.JSONObject;

import com.android.mtools.MPay;
import com.android.mtools.MPayListener;
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

public class DMPay extends Pay{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3409638778885940889L;
	
	private String				chargepoint;
	private String				payCode;
	private String              orderId;
	private String 				money;
	private String				json;
	public static final String	DEL					= "\\u007c";

	public static boolean		hasinit				= false;

	private int					dmPayStatus;
	public static final int		DM_PAY_OK			= 2;
	public static final int		DM_PAY_FAIL		= 3;
	String						address;
	String						content;
	String						lscontent;
	private boolean isFinishPay = false;

	public DMPay()
	{
		//LogUtil.log(LogUtil.INFO, "Andy Log","\r\n\r\nnew MGZFPay");
		Log.e("test","DMPay--new DMPay");
		setType(PAY_TYPE_DM);
	}

	public void log(String info)
	{
		
	}

	public void setJsonParams(String json)
	{
		this.json = json;
	}

	public class MyListener implements MPayListener{

		@Override
		public void callBack(String gid, String cpOid, int code, int detail) {
			// TODO 支付结果回调方法
			Log.e("test","MyListener--callBack--code:"+code);
			if (code==1001) {
				Log.e("test","支付成功");
				setExecuteStatus(EXECUTE_STATUS_COMPLETE);
				dmPayStatus = DM_PAY_OK;
				payOk();
			}else {
				Log.e("test","支付失败，代码："+detail);
				setExecuteStatus(EXECUTE_STATUS_COMPLETE);
				dmPayStatus = DM_PAY_FAIL;
				payFail();
			}
		}
    }
	
	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		super.run();
		setExecuteStatus(EXECUTE_STATUS_RUN);
		try {
			 // Appkey--->  "chargepoint|name|money"
		
			JSONObject jsonObj = new JSONObject(json);
			payCode = jsonObj.getString("Appkey");
			orderId = jsonObj.getString("Orderid");
			
			String tempcs[] = payCode.split(DEL);
			chargepoint = tempcs[0];
			money = tempcs[1];
			
			//payCode = jsonObj.getString("payCode");
			
			Log.e("test", "DMPay run json:" + jsonObj.toString());
			
		} catch (Exception e) {
			e.fillInStackTrace();
			setExecuteStatus(EXECUTE_STATUS_COMPLETE);
			dmPayStatus = DM_PAY_FAIL;
			payFail();
			return;
		}

		if (CommonUtils.getWindowTopViews() != null
				&& CommonUtils.getWindowTopViews().length > 0) {
			final Activity c = (Activity) CommonUtils.getWindowTopViews()[0]
					.getContext();
			
			final String dmAppkey = CommonUtils.getDmAppKey(c);
			
			final String dmChannelId = CommonUtils.getCL(c);
			
			Log.e("test","DMPay run buy payCode:" + payCode + 
					",--orderId:"+orderId+"---MAI_MSA:"+dmAppkey+"----MAI_CHAN:"+dmChannelId);

			((Activity) c).runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					
					
//					MPay.getInstance(activity,MAI_MSA,MAI_CHANNELID)
//					.xPay("cjclzk4ujBc5bB7yfd8g9eiz", ""+System.currentTimeMillis(),100,new MyListener());
					MPay.getInstance(c,dmAppkey,dmChannelId)
					.xPay(chargepoint, "ht"+orderId,Integer.parseInt(money),new MyListener());						
					
					Log.e("test","DMPay end run buy payCode:" + payCode + ",--orderId:"+orderId);
				}
			});
		}
	}

	private void payOk()
	{
		Log.e("test","DMPay payOk");
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
		Log.e("test","DMPay payFail");
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
		return dmPayStatus;
	}

	public void setStatus(int payStatus)
	{
		this.dmPayStatus = payStatus;
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
