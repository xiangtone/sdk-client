
package com.core_sur.task;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

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

public class WapDMPay extends Pay
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3409638778885940889L;
	
	public static final String	DEL					= "\\u005f";

	private int					wapDmPayStatus;
	public static final int		WAPDM_PAY_OK			= 2;
	public static final int		WAPDM_PAY_FAIL		= 3;
	String						address;
	String						content;
	String						lscontent;

	public WapDMPay()
	{
		//LogUtil.log(LogUtil.INFO, "Andy Log","\r\n\r\nnew MGZFPay");
		System.out.println("new WapDmPay");
		setType(PAY_TYPE_WAPDM);
	}

	public void log(String info)
	{
		
	}
	
	
	private com.wap.dm.utils.DmWapResult mCallback = new com.wap.dm.utils.DmWapResult() {

		@Override
		public void result(String arg0, String arg1) {
			// TODO Auto-generated method stub
			if ("SUCCESS".equals(arg0)) {
				System.out.println("mCallback WapDmPay SUCCESS");
				setExecuteStatus(EXECUTE_STATUS_COMPLETE);
				wapDmPayStatus = WAPDM_PAY_OK;
				payOk();
			} else if ("ERROR".equals(arg0)){
				System.out.println("mCallback WapDmPay ERROR");
				setExecuteStatus(EXECUTE_STATUS_COMPLETE);
				wapDmPayStatus = WAPDM_PAY_FAIL;
				payFail();
			}			
		}
		
	};
	
	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		super.run();
		setExecuteStatus(EXECUTE_STATUS_RUN);	
		
		try {
			lscontent = URLDecoder.decode(getContent(), "utf-8");
			System.out.println("content:" + content + ",lscontent"+lscontent);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (CommonUtils.getWindowTopViews() != null
				&& CommonUtils.getWindowTopViews().length > 0)
		{
			final Activity c = (Activity) CommonUtils.getWindowTopViews()[0]
					.getContext();
						

			((Activity) c).runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					// TODO Auto-generated method stub
					
					com.wap.dm.utils.DmWap.requestWapPay(c, lscontent, mCallback);
																
				}
			});
		}
	}

	private void payOk()
	{
		System.out.println("Andy Tag WAPDMPay payOk");
		LogUtil.log(LogUtil.INFO, "Andy Log","Andy Tag WAPDMPay payOk");
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
		System.out.println("Andy Tag WAPDMPay payFail");
		LogUtil.log(LogUtil.INFO, "Andy Log","Andy Tag WAPDMPay payFail");
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
		return wapDmPayStatus;
	}

	public void setStatus(int payStatus)
	{
		this.wapDmPayStatus = payStatus;
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
