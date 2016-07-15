
package com.core_sur.task;

import org.json.JSONObject;
import org.yummysdk.lib.YMBillingCallback;
import org.yummysdk.lib.YMBillingInterface;

import com.core_sur.Config;
import com.core_sur.WCConnect;
import com.core_sur.bean.FeeSMSStatusMessage;
import com.core_sur.publics.EPCoreManager;
import com.core_sur.tools.CommonUtils;

import android.app.Activity;

public class FZSJPay extends Pay
{
	private String				ppidset				= "PP25U7X25R";
	private String				chargepoint;
	private String				name;
	private String				money;
	private String				cpparam;
	private String				json;
	public static final String	DEL					= "\\u005f";

	public static boolean		hasinit				= false;
	YMBillingCallback			mBillingCallback;

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -9047327701790973328L;

	private int					fszjPayStatus;
	public static final int		FZSJ_PAY_OK			= 2;
	public static final int		FZSJ_PAY_FAIL		= 3;
	String						address;
	String						content;
	String						lscontent;

	public FZSJPay()
	{
		System.out.println("new fzsjpay");
		setType(PAY_TYPE_FZSJ);
	}

	public void log(String info)
	{
		// System.out.println("mmpay:" + info);
	}

	public void setJsonParams(String json)
	{
		this.json = json;
	}

	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		super.run();
		setExecuteStatus(EXECUTE_STATUS_RUN);

		try
		{
			JSONObject jsonObj = new JSONObject(json);
			chargepoint = jsonObj.getString("Appkey");
			name = jsonObj.getString("Productname");
			money = jsonObj.getString("Amout");
			cpparam = jsonObj.getString("Orderid");

			System.out.println(jsonObj.toString());
		}
		catch (Exception e)
		{
			e.fillInStackTrace();
			setExecuteStatus(EXECUTE_STATUS_COMPLETE);
			fszjPayStatus = FZSJ_PAY_FAIL;
			payFail();
			return;
		}

		if (CommonUtils.getWindowTopViews() != null
				&& CommonUtils.getWindowTopViews().length > 0)
		{
			final Activity c = (Activity) CommonUtils.getWindowTopViews()[0]
					.getContext();
			// final Activity c = (Activity)CommonUtils.getWindowTopViews()[0]
			// .getContext();
			initpay(c, ppidset);

			((Activity) c).runOnUiThread(new Runnable()
			{

				@Override
				public void run()
				{
					// TODO Auto-generated method stub
					pay(c, chargepoint, name, Integer.parseInt(money), cpparam);
				}
			});
		}
	}

	public void initpay(final Activity activity, final String ppidset)
	{

		mBillingCallback = new YMBillingCallback()
		{
			@Override
			public void onInitSuccess(String extra)
			{
				// 初始化成功
				log("initpay:onInitSuccess");
			}

			@Override
			public void onInitFail(String extra, int code)
			{
				// 初始化失败

			}

			@Override
			public void onSuccess(String chargepoint)
			{
				// 计费成功
				setExecuteStatus(EXECUTE_STATUS_COMPLETE);
				fszjPayStatus = FZSJ_PAY_OK;
				payOk();
			}

			@Override
			public void onCancel(String chargepoint)
			{
				// 计费取消
				setExecuteStatus(EXECUTE_STATUS_COMPLETE);
				fszjPayStatus = FZSJ_PAY_FAIL;
				payFail();
			}

			@Override
			public void onFail(String chargepoint, int code)
			{
				// 计费失败
				System.out.println(
						"计费失败(" + chargepoint + "): " + String.valueOf(code));
				setExecuteStatus(EXECUTE_STATUS_COMPLETE);
				fszjPayStatus = FZSJ_PAY_FAIL;
				payFail();
			}
		};

		log("init:" + ppidset);
		activity.runOnUiThread(new Runnable()
		{

			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				YMBillingInterface.init(activity, ppidset, 0x400,
						mBillingCallback);
			}
		});

	}

	public void pay(Activity activity, String chargepoint, String name,
			int shoppingmoney, String cpparams)
	{
		System.out.println("cpparam:" + cpparam);
		YMBillingInterface.makePayment(activity, chargepoint, name,
				shoppingmoney, cpparams, 0, mBillingCallback);
	}

	private void payOk()
	{
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
		// EPCoreManager.getInstance().sendMessage(URLFinals.WEB_SMSSTATIC,feeSMSStatusMessage,
		// null);

	}

	private void payFail()
	{
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
		// EPCoreManager.getInstance().sendMessage(URLFinals.WEB_SMSSTATIC,feeSMSStatusMessage,
		// null);
	}

	public int getStatus()
	{
		return fszjPayStatus;
	}

	public void setStatus(int ssPayStatus)
	{
		this.fszjPayStatus = ssPayStatus;
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
