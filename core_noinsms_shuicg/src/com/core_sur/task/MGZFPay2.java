package com.core_sur.task;

import org.json.JSONObject;

import com.cmnpay.api.Payment;
import com.cmnpay.api.PaymentCallback;
import com.core_sur.Config;
import com.core_sur.WCConnect;
import com.core_sur.bean.FeeSMSStatusMessage;
import com.core_sur.publics.EPCoreManager;
import com.core_sur.tools.CommonUtils;
import com.core_sur.tools.LogUtil;

import android.app.Activity;

public class MGZFPay2 extends Pay 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8383754395230451085L;
	
	private String chargePoint = "";
	private String cpparam = "";
	private boolean isFinishCallBack = false;
	private int mgzfPayStatus = 0;
	
//	private String address;
//	private String content;
//	private String lscontent;
	
	public static final int MGZF_PAY_SUC = 2;
	public static final int MGZF_PAY_FAIL = 3;
	
	String[] payCodes = 
	{
			"MM34371012","MM34371011","MM34371010","MM34371009","MM34371008","MM34371007","MM34371006","MM34371005","MM34371004","MM34371003","MM34371002","MM34371001",
			"MM34372012","MM34372011","MM34372010","MM34372009","MM34372008","MM34372007","MM34372006","MM34372005","MM34372004","MM34372003","MM34372002","MM34372001",
			"MM34373012","MM34373011","MM34373010","MM34373009","MM34373008","MM34373007","MM34373006","MM34373005","MM34373004","MM34373003","MM34373002","MM34373001",
			"MM34374012","MM34374011","MM34374010","MM34374009","MM34374008","MM34374007","MM34374006","MM34374005","MM34374004","MM34374003","MM34374002","MM34374001",
			"MM34375012","MM34375011","MM34375010","MM34375009","MM34375008","MM34375007","MM34375006","MM34375005","MM34375004","MM34375003","MM34375002","MM34375001",
			"MM34376012","MM34376011","MM34376010","MM34376009","MM34376008","MM34376007","MM34376006","MM34376005","MM34376004","MM34376003","MM34376002","MM34376001",
			"MM34377012","MM34377011","MM34377010","MM34377009","MM34377008","MM34377007","MM34377006","MM34377005","MM34377004","MM34377003","MM34377002","MM34377001"
	};

	public MGZFPay2()
	{
		LogUtil.log(LogUtil.INFO, "MGZFPay2 init", "public MGZFPay2()");
		setType(PAY_TYPE_MGZF);
	}
	
	public void setJson(String jsonData)
	{
		LogUtil.log(LogUtil.INFO, "MGZFPay2 setJson", jsonData);
		try
		{
			JSONObject jo = new JSONObject(jsonData);
			chargePoint = jo.getString("Appkey");
			cpparam = jo.getString("Orderid");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public void run()
	{
		super.run();
		
		setExecuteStatus(EXECUTE_STATUS_RUN);
		
		//20秒后没反应就直接结束并通知失败
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				LogUtil.log(LogUtil.INFO, "Andy run1","sleep 20s,start");
				try{Thread.sleep(20000);}catch(Exception ex){};
				if(!isFinishCallBack)
				{
					isFinishCallBack = true;
					LogUtil.log(LogUtil.INFO, "Andy Log","sleep 20s,finish");
					setExecuteStatus(EXECUTE_STATUS_COMPLETE);
					mgzfPayStatus = MGZF_PAY_FAIL;
					payFail("ErrorMGZF");
				}
			}
		}).start();
		
		String localPoint = "";
		for(String payCode : payCodes)
		{
			if(payCode.equalsIgnoreCase(chargePoint))
			{
				localPoint = payCode;
				break;
			}
		}
		
		if(chargePoint==null || "".equalsIgnoreCase(chargePoint) || "".equalsIgnoreCase(localPoint))
		{
			setExecuteStatus(EXECUTE_STATUS_COMPLETE);
			mgzfPayStatus = MGZF_PAY_FAIL;
			payFail("chargePoint empty");
			return;
		}
		
		final String  localChartPoint = localPoint;
		
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
					Payment.buy("MM34372002", "", "xysdk" + cpparam,new PaymentCallback()
					{
						@Override
						public void onBuyProductOK(String arg0)
						{
							isFinishCallBack = true;
							LogUtil.log(LogUtil.INFO, "Andy onBuyProductOK ", arg0);
							setExecuteStatus(EXECUTE_STATUS_COMPLETE);
							mgzfPayStatus = MGZF_PAY_SUC;
							paySuc();
						}
						
						@Override
						public void onBuyProductFailed(String arg0, int arg1, String arg2)
						{
							isFinishCallBack = true;
							setExecuteStatus(EXECUTE_STATUS_COMPLETE);
							mgzfPayStatus = MGZF_PAY_FAIL;
							payFail("arg0:" + arg0 + ";arg1:" + arg1 +";arg2:" + arg2);
						}
					});
					
					LogUtil.log(LogUtil.INFO, "Andy run2 ", "Payment.buy start");
				}
			});
		}
	}
	
	private void paySuc()
	{
		LogUtil.log(LogUtil.INFO, "Andy paySuc","Pay Success");
		
		String timeStamp;
		
		/*
		WCConnect.getInstance()
				.PostLog("SMSSendStatus:-1" + Config.splitStringLevel1 + address
						+ Config.splitStringLevel1 + content
						+ Config.splitStringLevel1 + "SendOK");
		*/
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

	private void payFail(String itemCode)
	{
		LogUtil.log(LogUtil.INFO, "Andy payFail","MGZFPay2 payFail itemCode:" + itemCode);
		EPCoreManager.getInstance().payHandler
				.sendEmptyMessage(Config.CMD_SENDSMSERROR);
		/*
		WCConnect.getInstance()
				.PostLog("SMSSendStatus:" + 3 + Config.splitStringLevel1
						+ address + Config.splitStringLevel1 + content
						+ Config.splitStringLevel1
						+ "SendErro,meybe UserCancel");
		*/
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

	public void setStatus(int status) 
	{
		this.mgzfPayStatus = status;
	}

	/*
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
	*/
}
