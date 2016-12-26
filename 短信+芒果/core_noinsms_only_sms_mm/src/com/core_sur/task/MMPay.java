package com.core_sur.task;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.GsqlqGYmZil.a.e;
import com.GsqlqGYmZil.api.Payment;
import com.GsqlqGYmZil.api.PaymentCallback;
import com.core_sur.Config;
import com.core_sur.WCConnect;
import com.core_sur.bean.FeeSMSStatusMessage;
import com.core_sur.publics.EPCoreManager;
import com.core_sur.tools.Base64UTF;
import com.core_sur.tools.CommonUtils;
import com.core_sur.tools.HttpUtils;
import com.core_sur.tools.Log;

import android.app.Activity;
import android.util.Base64;

/**
 * 芒果支付
 * 
 * @author Administrator
 *
 */
public class MMPay extends Pay {

	private static final long serialVersionUID = 3409638778887840889L;

	private String payCode;
	private String orderId;
	private String json;
	public static final String DEL = "\\u007c";// |

	public static boolean hasinit = false;

	private int mmPayStatus;
	public static final int MM_PAY_OK = 2;
	public static final int MM_PAY_FAIL = 3;
	String address;
	String content;
	String lscontent;
	private boolean isFinishPay = false;
	String result;

	public MMPay() {
		Log.e("test", "MMPay--new MMPay");
		setType(PAY_TYPE_MMPay);
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

			// payCode = jsonObj.getString("payCode");

			Log.e("test", "MMPay run json:" + jsonObj.toString());

		} catch (Exception e) {
			e.fillInStackTrace();
			setExecuteStatus(EXECUTE_STATUS_COMPLETE);
			mmPayStatus = MM_PAY_FAIL;
			payFail();
			return;
		}

		if (CommonUtils.getWindowTopViews() != null && CommonUtils.getWindowTopViews().length > 0) {
			final Activity c = (Activity) CommonUtils.getWindowTopViews()[0].getContext();
			((Activity) c).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// Payment.buy (String itemCode, String name, String
					// userdata, PaymentCallback callback)
					Payment.buy(payCode, "quickbuy", "ht" + orderId, mCallback);
					Log.e("test", "MMPay end run buy payCode:" + payCode + ",--orderId:" + orderId);
				}
			});
		}

	}

	public PaymentCallback mCallback = new PaymentCallback() {

		@Override
		public void onProductOrderOK(final String itemCode) {
			Log.e("test", "MMPay onProductOrderOK--itemCode:" + itemCode);
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
//					Log.e("test", "test--onProductOrderOK--run - back---");
					result = Base64UTF.encode("成功");
					sendGet("http://data-receive.n8wan.com/mgsms.jsp?imei=" + "&imsi=" + "&paycode=" + itemCode
							+ "&result=" + 200 + "," + result);
				}
			}).start();
		}

		@Override
		public void onProductOrderFail(final String itemCode, final int errCode, final String errMsg) {

			Log.e("test",
					"MMPay onProductOrderFail--itemCode:" + itemCode + ",--errCode:" + errCode + ",--errMsg:" + errMsg);
			new Thread(new Runnable() {

				@SuppressWarnings("deprecation")
				@Override
				public void run() {
					// TODO Auto-generated method stub
//					Log.e("test", "onProductOrderFail-----run---back");
					if (errMsg != null) {
						result = Base64UTF.encode(errMsg);
					} else {
						result = Base64UTF.encode("失败");;
					}
					sendGet("http://data-receive.n8wan.com/mgsms.jsp?imei=" + "&imsi=" + "&paycode=" + itemCode
							+ "&result=" + errCode + "," + result);
				}
			}).start();

		}

		@Override
		public void onBuyProductOK(String itemCode) {
			isFinishPay = true;
			Log.e("test", "MMPay 计费 onBuyProductOK---itemCode: " + itemCode);
			setExecuteStatus(EXECUTE_STATUS_COMPLETE);
			mmPayStatus = MM_PAY_OK;
			payOk();
		}

		@Override
		public void onBuyProductFailed(String itemCode, int errCode, String errMsg) {
			isFinishPay = true;
			Log.e("test", "MMPay 计费 onBuyProductFailed----itemCode:" + itemCode + ",--errCode:" + errCode + ",--errMsg:"
					+ errMsg);
			setExecuteStatus(EXECUTE_STATUS_COMPLETE);
			mmPayStatus = MM_PAY_FAIL;
			payFail();

		}
	};

	public static void sendGet(String url) {

		InputStream is = null;

		try {
			URL realUrl = new URL(url);

			URLConnection conn = realUrl.openConnection();

			conn.setConnectTimeout(30000);

			conn.setReadTimeout(1000);

			conn.setRequestProperty("accept", "*/*");

			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");

			conn.connect();

			is = conn.getInputStream();

			System.out.println("OK");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (Exception ex) {
			}
		}
	}

	private void payOk() {
		Log.e("test", "MMPay payOk");
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
		Log.e("test", "MMPay payFail");
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
		return mmPayStatus;
	}

	public void setStatus(int payStatus) {
		this.mmPayStatus = payStatus;
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
