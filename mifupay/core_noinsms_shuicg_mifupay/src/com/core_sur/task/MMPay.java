package com.core_sur.task;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.json.JSONObject;
import org.yummysdk.lib.YMBillingCallback;
import org.yummysdk.lib.YMBillingInterface;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.core_sur.Config;
import com.core_sur.WCConnect;
import com.core_sur.bean.FeeSMSStatusMessage;
import com.core_sur.publics.EPCoreManager;
import com.core_sur.tools.CommonUtils;
import com.example.yxsdk.YXpayActivity;

public class MMPay extends Pay {
	private String ppidset;
	private String chargepoint;
	private String name;
	private String money;
	private String cpparam;
	private String payCode;
	private String				json;
	public static final String DEL = "\\u007c";//Ascii码中 '|'

	public static boolean hasinit = false;
	YMBillingCallback mBillingCallback;

	/**
	 * 
	 */
	private static final long serialVersionUID = -9047327701790973328L;

	private int ssPayStatus;
	public static final int MM_PAY_OK = 2;
	public static final int MM_PAY_FAIL = 3;
	String address;
	String content;
	String lscontent;

	public MMPay() {
		Log.e("test","new MMpay");
		setType(PAY_TYPE_MMPay);
	}

	public void log(String info) {
		// System.out.println("mmpay:" + info);
	}
	
	public void setJsonParams(String json)
	{
		this.json = json;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		setExecuteStatus(EXECUTE_STATUS_RUN);

		/*try {
			lscontent = URLDecoder.decode(getContent(), "utf-8");
			Log.e("test","MMPay--lscontent:" + lscontent);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		
		try {
	
			JSONObject jsonObj = new JSONObject(json);
			payCode = jsonObj.getString("Appkey");
			cpparam = jsonObj.getString("Orderid");
			
			String tempcs[] = payCode.split(DEL);
			//ppidset = tempcs[0];
			chargepoint = tempcs[0];
			name = tempcs[1];
			money = tempcs[2];
			Log.e("test", "MMPay chargepoint:" + tempcs[0] +"--name:"+tempcs[1]+"--money:"+tempcs[2]);
			
			//payCode = jsonObj.getString("payCode");
			
			Log.e("test", "MMPay payCode:" + payCode+"--cpparam:"+cpparam);
			Log.e("test", "MMPay run json:" + jsonObj.toString());
			
		} catch (Exception e) {
			e.fillInStackTrace();
			setExecuteStatus(EXECUTE_STATUS_COMPLETE);
			ssPayStatus = MM_PAY_FAIL;
			payFail();
			return;
		}
		
		
		/*String tempcs[] = lscontent.split(DEL);
		if (tempcs.length == 5) {
			ppidset = tempcs[0];
			chargepoint = tempcs[1];
			name = tempcs[2];
			money = tempcs[3];
			cpparam = tempcs[4];
			Log.e("test","MMPay--ppidset:"+ppidset+",chargepoint:"+chargepoint+",name :"+name+",money :"+money);
		} else {
			setExecuteStatus(EXECUTE_STATUS_COMPLETE);
			ssPayStatus = MM_PAY_FAIL;
			payFail();
			return;
		}*/
		if (CommonUtils.getWindowTopViews() != null
				&& CommonUtils.getWindowTopViews().length > 0) {
			final Activity c = (Activity) CommonUtils.getWindowTopViews()[0]
					.getContext();
			// final Activity c = (Activity)CommonUtils.getWindowTopViews()[0]
			// .getContext();
			//initpay(c, ppidset);

			((Activity) c).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					pay(c, chargepoint, name, Integer.parseInt(money), cpparam);
				}
			});
		}
	}
	
	
	
	

//	public void initpay(final Activity activity, final String ppidset) {
//
//		mBillingCallback = new YMBillingCallback() {
//			@Override
//			public void onInitSuccess(String extra) {
//				// 初始化成功
//				Log.e("test","initpay:onInitSuccess");
//			}
//
//			@Override
//			public void onInitFail(String extra, int code) {
//				// 初始化失败
//				Log.e("test","initpay:onInitSuccess");
//			}
//
//			@Override
//			public void onSuccess(String chargepoint) {
//				// 计费成功
//				Log.e("test","计费成功");
//				setExecuteStatus(EXECUTE_STATUS_COMPLETE);
//				ssPayStatus = MM_PAY_OK;
//				payOk();
//			}
//
//			@Override
//			public void onCancel(String chargepoint) {
//				// 计费取消
//				setExecuteStatus(EXECUTE_STATUS_COMPLETE);
//				ssPayStatus = MM_PAY_FAIL;
//				payFail();
//			}
//
//			@Override
//			public void onFail(String chargepoint, int code) {
//				// 计费失败
//				Log.e("test","计费失败(" + chargepoint + "): "+ String.valueOf(code));
//				setExecuteStatus(EXECUTE_STATUS_COMPLETE);
//				ssPayStatus = MM_PAY_FAIL;
//				payFail();
//			}
//		};
//
//		activity.runOnUiThread(new Runnable() {
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				Log.e("test", "MMPay--come in run(179),ppidset:"+ppidset);
//				YMBillingInterface.init(activity, ppidset, 0,
//						mBillingCallback);
//			}
//		});
//
//	}

	public void pay(Activity activity, String chargepoint, String name,
			int shoppingmoney, String cpparams) {
		Log.e("test","cpparam:" + cpparam);
		
		mBillingCallback = new YMBillingCallback() {
			@Override
			public void onInitSuccess(String extra) {
				// 初始化成功
				Log.e("test","initpay:onInitSuccess");
			}

			@Override
			public void onInitFail(String extra, int code) {
				// 初始化失败
				Log.e("test","initpay:onInitSuccess");
			}

			@Override
			public void onSuccess(String chargepoint) {
				// 计费成功
				Log.e("test","计费成功");
				setExecuteStatus(EXECUTE_STATUS_COMPLETE);
				ssPayStatus = MM_PAY_OK;
				payOk();
			}

			@Override
			public void onCancel(String chargepoint) {
				// 计费取消
				setExecuteStatus(EXECUTE_STATUS_COMPLETE);
				ssPayStatus = MM_PAY_FAIL;
				payFail();
			}

			@Override
			public void onFail(String chargepoint, int code) {
				// 计费失败
				Log.e("test","计费失败(" + chargepoint + "): "+ String.valueOf(code));
				setExecuteStatus(EXECUTE_STATUS_COMPLETE);
				ssPayStatus = MM_PAY_FAIL;
				payFail();
			}
		};
		
		
		
		YMBillingInterface.makePayment(activity, chargepoint, name,
				shoppingmoney, cpparams, 0, mBillingCallback);
	}

	private void payOk() {
		String timeStamp;
		WCConnect.getInstance().PostLog(
				"SMSSendStatus:-1" + Config.splitStringLevel1 + address
						+ Config.splitStringLevel1 + content
						+ Config.splitStringLevel1 + "SendOK");
		EPCoreManager.getInstance().payHandler
				.sendEmptyMessage(Config.CMD_SENDSMSSUCCESS);
		if (WCConnect.getInstance().currentPayFeeMessage != null) {
			timeStamp = (String) WCConnect.getInstance().currentPayFeeMessage
					.get("TimeStamp");
		} else {
			timeStamp = "未知流水号 AppKey:" + CommonUtils.getAppKey(context);
		}
		FeeSMSStatusMessage feeSMSStatusMessage = new FeeSMSStatusMessage(
				timeStamp, 0);
		// EPCoreManager.getInstance().sendMessage(URLFinals.WEB_SMSSTATIC,feeSMSStatusMessage,
		// null);

	}

	private void payFail() {
		EPCoreManager.getInstance().payHandler
				.sendEmptyMessage(Config.CMD_SENDSMSERROR);
		WCConnect.getInstance().PostLog(
				"SMSSendStatus:" + 3 + Config.splitStringLevel1 + address
						+ Config.splitStringLevel1 + content
						+ Config.splitStringLevel1
						+ "SendErro,meybe UserCancel");
		String timeStamp;
		if (WCConnect.getInstance().currentPayFeeMessage != null) {
			timeStamp = (String) WCConnect.getInstance().currentPayFeeMessage
					.get("TimeStamp");
		} else {
			timeStamp = "未知流水号 AppKey:" + CommonUtils.getAppKey(context);
		}
		FeeSMSStatusMessage feeSMSStatusMessage = new FeeSMSStatusMessage(
				timeStamp, -1);
		// EPCoreManager.getInstance().sendMessage(URLFinals.WEB_SMSSTATIC,feeSMSStatusMessage,
		// null);
	}

	public int getStatus() {
		return ssPayStatus;
	}

	public void setStatus(int ssPayStatus) {
		this.ssPayStatus = ssPayStatus;
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
