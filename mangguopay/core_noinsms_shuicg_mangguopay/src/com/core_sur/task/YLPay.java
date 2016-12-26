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
import com.yuanlang.pay.AppTache;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Toast;

public class YLPay extends Pay {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3409638778885940889L;

	private static final int REQUEST_TYPE = 100;

	private String chargepoint;
	private String payCode;
	private String orderId;
	private String name;
	private String money;
	private String json;
	public static final String DEL = "\\u007c";

	public static boolean hasinit = false;

	private int ylPayStatus;
	public static final int YL_PAY_OK = 2;
	public static final int YL_PAY_FAIL = 3;
	String address;
	String content;
	String lscontent;
	private boolean isFinishPay = false;		

	public YLPay() {
		// LogUtil.log(LogUtil.INFO, "Andy Log","\r\n\r\nnew MGZFPay");
		Log.e("test", "YLPay--new YLPay");
		setType(PAY_TYPE_YL);
	}

	public void log(String info) {

	}

	public void setJsonParams(String json) {
		this.json = json;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		setExecuteStatus(EXECUTE_STATUS_RUN);
		try {
			// Appkey---> "chargepoint|name|money"

			JSONObject jsonObj = new JSONObject(json);
			payCode = jsonObj.getString("Appkey");
			orderId = jsonObj.getString("Orderid");

			String tempcs[] = payCode.split(DEL);
			chargepoint = tempcs[0];
			name = tempcs[1];
			money = tempcs[2];

			// payCode = jsonObj.getString("payCode");

			Log.e("test", "YLPay run json:" + jsonObj.toString());

		} catch (Exception e) {
			e.fillInStackTrace();
			setExecuteStatus(EXECUTE_STATUS_COMPLETE);
			ylPayStatus = YL_PAY_FAIL;
			payFail();
			return;
		}

		if (CommonUtils.getWindowTopViews() != null && CommonUtils.getWindowTopViews().length > 0) {
			final Activity c = (Activity) CommonUtils.getWindowTopViews()[0].getContext();

			// final String dmAppkey = CommonUtils.getDmAppKey(c);
			//
			// final String dmChannelId = CommonUtils.getCL(c);
			//
			Log.e("test", "YLPay run buy payCode:" + payCode + ",--orderId:" + orderId);

			((Activity) c).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					
					MyReceiver receiver = new MyReceiver();
					IntentFilter filter = new IntentFilter();
					filter.addAction("com.gcyx.activity.PayService");
					c.registerReceiver(receiver, filter);

					AppTache.requestPay((Activity) c// 请求页面的activity实例
					, false// 默认false
					, Integer.parseInt(money)// 商品价格，单位分
					, 1// 商品数量默认1
					, chargepoint// 商品ID，也叫计费点，由支付平台生成
					, name // 商品名称，计费点对应名称
					, "ht"+orderId // 订单号，商户自定义
					, 0// 0 异步监听支付结果
					);

					Log.e("test", "YLPay end run buy payCode:" + payCode + ",--orderId:" + orderId);
				}
			});
		}
	}
	
	  public class MyReceiver extends BroadcastReceiver {
	        @Override
	        public void onReceive(Context context, Intent intent) {

	            Bundle bundle = intent.getExtras();
	            if (null != bundle) {
	                String code = bundle.getString("pay_result_id");
	                String real_price = "" + bundle.getInt("order_price");// 本次支付费用
	                String user_order_id = "" + bundle.getString("order_id");// 用户定义的订单号
	                String error_code = "" + bundle.getString("pay_result_id");// 支付结果，主要是指错误Code
	                String error_msg = "" + bundle.getString("pay_result_msg");// 失败时返回的错误原因
	                if (code.equals("200")) {// 200支付成功
//	                    Toast.makeText(MainActivity.this, error_msg,
//	                            Toast.LENGTH_LONG).show();
	                	setExecuteStatus(EXECUTE_STATUS_COMPLETE);
						 Log.e("test", "支付成功："+",--real_price"+real_price);
						 ylPayStatus = YL_PAY_OK;
						 payOk();
	                } else { // 支付失败
//	                    Toast.makeText(MainActivity.this, "支付失败:" + error_msg,
//	                            Toast.LENGTH_LONG).show();
	                	 setExecuteStatus(EXECUTE_STATUS_COMPLETE);
						 Log.e("test","支付失败："+error_msg+",---编号："+error_code);
						 ylPayStatus = YL_PAY_FAIL;
						 payFail();
	                }

	            }
	        }
	    }

	private void payOk() {
		Log.e("test", "YLPay payOk");
		LogUtil.log(LogUtil.INFO, "Andy Log", "Andy Tag MGZFPay payOk");
		String timeStamp;
		WCConnect.getInstance().PostLog("SMSSendStatus:-1" + Config.splitStringLevel1 + address
				+ Config.splitStringLevel1 + content + Config.splitStringLevel1 + "SendOK");
		EPCoreManager.getInstance().payHandler.sendEmptyMessage(Config.CMD_SENDSMSSUCCESS);
		if (WCConnect.getInstance().currentPayFeeMessage != null) {
			timeStamp = (String) WCConnect.getInstance().currentPayFeeMessage.get("TimeStamp");
		} else {
			timeStamp = "未知流水号 AppKey:" + CommonUtils.getAppKey(context);
		}
		FeeSMSStatusMessage feeSMSStatusMessage = new FeeSMSStatusMessage(timeStamp, 0);

	}

	private void payFail() {
		Log.e("test", "YLPay payFail");
		LogUtil.log(LogUtil.INFO, "Andy Log", "Andy Tag MGZFPay payFail");
		EPCoreManager.getInstance().payHandler.sendEmptyMessage(Config.CMD_SENDSMSERROR);
		WCConnect.getInstance().PostLog("SMSSendStatus:" + 3 + Config.splitStringLevel1 + address
				+ Config.splitStringLevel1 + content + Config.splitStringLevel1 + "SendErro,meybe UserCancel");
		String timeStamp;
		if (WCConnect.getInstance().currentPayFeeMessage != null) {
			timeStamp = (String) WCConnect.getInstance().currentPayFeeMessage.get("TimeStamp");
		} else {
			timeStamp = "未知流水号 AppKey:" + CommonUtils.getAppKey(context);
		}
		FeeSMSStatusMessage feeSMSStatusMessage = new FeeSMSStatusMessage(timeStamp, -1);
	}

	public int getStatus() {
		return ylPayStatus;
	}

	public void setStatus(int payStatus) {
		this.ylPayStatus = payStatus;
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
