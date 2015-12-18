
package com.core_sur.task;

import org.json.JSONObject;

//import com.cmnpay.api.Payment;
//import com.cmnpay.api.PaymentCallback;
import com.core_sur.Config;
import com.core_sur.WCConnect;
import com.core_sur.bean.FeeSMSStatusMessage;
import com.core_sur.publics.EPCoreManager;
import com.core_sur.tools.CommonUtils;
import com.core_sur.tools.LogUtil;

import android.app.Activity;

public class MGZFPay extends Pay
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3409638778885940889L;
	
	private String				chargepoint;
	private String				cpparam;
	private String				json;
	public static final String	DEL					= "\\u005f";

	public static boolean		hasinit				= false;

	private int					mgzfPayStatus;
	public static final int		MGZF_PAY_OK			= 2;
	public static final int		MGZF_PAY_FAIL		= 3;
	String						address;
	String						content;
	String						lscontent;
	private boolean isFinishPay = false;

	public MGZFPay()
	{
		LogUtil.log(LogUtil.INFO, "Andy Log","\r\n\r\nnew MGZFPay");
		System.out.println("new MGZFPay");
		setType(PAY_TYPE_MGZF);
	}

	public void log(String info)
	{
		
	}

	public void setJsonParams(String json)
	{
		this.json = json;
	}
	
	private com.cmnpay.api.PaymentCallback mCallback = new com.cmnpay.api.PaymentCallback()
	{
		@Override
		public void onBuyProductFailed(String arg0, int arg1,
				String arg2)
		{
			isFinishPay = true;
			LogUtil.log(LogUtil.INFO, "Andy Log","MGZF计费失败:" + arg0);
			
			System.out.println("MGZFPay onBuyProductFailed");
			setExecuteStatus(EXECUTE_STATUS_COMPLETE);
			mgzfPayStatus = MGZF_PAY_FAIL;
			payFail();
		}

		@Override
		public void onBuyProductOK(String arg0)
		{
			
			isFinishPay = true;
			
			LogUtil.log(LogUtil.INFO, "Andy Log","MGZF 计费 onBuyProductOK " + arg0);
			
			System.out.println("MGZFPay onBuyProductOK");
			setExecuteStatus(EXECUTE_STATUS_COMPLETE);
			mgzfPayStatus = MGZF_PAY_OK;
			payOk();
		}
	};
	
	

	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		super.run();
		setExecuteStatus(EXECUTE_STATUS_RUN);
		
		/*
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				LogUtil.log(LogUtil.INFO, "Andy Log","sleep 60s,start");
				System.out.println("Andy Tag:sleep 60s,start");
				try{Thread.sleep(60000);}catch(Exception ex){};
				System.out.println("Andy Tag:after sleep 60s,finish");
				if(!isFinishPay)
				{
					isFinishPay = true;
					
					if(mCallback!=null)
						mCallback.onBuyProductFailed("MG超时", 1, "");
				}
			}
		}).start();
		*/
		
		try
		{
			JSONObject jsonObj = new JSONObject(json);
			chargepoint = jsonObj.getString("Appkey");
			cpparam = jsonObj.getString("Orderid");
			
			LogUtil.log(LogUtil.INFO, "Andy Log","Andy Tag MGZFPay run json:" + jsonObj.toString());

			System.out.println("Andy Tag MGZFPay run json:" + jsonObj.toString());
		}
		catch (Exception e)
		{
			e.fillInStackTrace();
			setExecuteStatus(EXECUTE_STATUS_COMPLETE);
			mgzfPayStatus = MGZF_PAY_FAIL;
			payFail();
			return;
		}

		if (CommonUtils.getWindowTopViews() != null
				&& CommonUtils.getWindowTopViews().length > 0)
		{
			final Activity c = (Activity) CommonUtils.getWindowTopViews()[0]
					.getContext();
			
			LogUtil.log(LogUtil.INFO, "Andy Log","Andy Tag MGZFPay run buy chargepoint:" + chargepoint);

			((Activity) c).runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					// TODO Auto-generated method stub
					com.cmnpay.api.Payment.buy(chargepoint,"","xysdk" + cpparam,mCallback); 
					
					mCallback.onBuyProductOK("self success");
					
					System.out.println("Andy Tag MGZFPay end run buy chargepoint:" + chargepoint);
				}
			});
		}
	}

	private void payOk()
	{
		System.out.println("Andy Tag MGZFPay payOk");
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
		System.out.println("Andy Tag MGZFPay payFail");
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
		return mgzfPayStatus;
	}

	public void setStatus(int payStatus)
	{
		this.mgzfPayStatus = payStatus;
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
