package com.epplus.utils;

import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.text.TextUtils;

/**
 * ����ͨ΢��֧��
 * 
 * @author zgt
 * 
 */
public class WXSwiftPayUtil {

	private Activity activity;

	private String xx_notifyData;

	private WxSwiftPayHandler handler;
	/**
	 * 
	 * @param activity
	 * @param OrderIdSelf
	 *            OrderIdSelf���������Լ��� WebOrderid
	 * @param OrderIdCp
	 * @param handler
	 */
	public WXSwiftPayUtil(Activity activity, String OrderIdSelf,
			String OrderIdCp, WxSwiftPayHandler handler) {
		this.handler = handler;
		this.activity = activity;

		xx_notifyData = ConfigUtils.getNotifyJsonData(activity,ConfigUtils.WXSwiftWAP, OrderIdSelf, OrderIdCp);

	}

	/**
	 * 
	 * @param body
	 *            ��Ʒ����
	 * @param attach
	 *            ������Ϣ
	 * @param price
	 *            �۸񣨷֣�
	 */
	public void pay(final String body, final String attach, final String price) {
		goToPay(body, attach, price);
	}

	private void goToPay(final String body, final String attach,
			final String price) {

		//String url = "http://192.168.0.111:8080/native-pay-swiftpass/WXSwiftPay";
		//String url = "http://thirdpay.youkala.com:29141/WXSwiftPay";
		String url = URLUtils.wxSwiftSignUrl(activity);
		HashMap<String, String> map = new HashMap<String, String>();

		map.put("body", body);
		map.put("attach", attach);
		map.put("total_fee", price);

		map.put("xx_notifyData", xx_notifyData);

		String ip = getLocalIpAddress(activity);
		if (!TextUtils.isEmpty(ip)) {
			map.put("mch_create_ip", ip);
		} else {
			map.put("mch_create_ip", "127.0.0.1");
		}
		HttpUtils.asyPost(url, map, new IHttpResult() {

			@Override
			public void result(Object obj) {
				if (obj == null) {
					return;
				}
				String jsonstr = obj.toString();
				try {
					JSONObject json = new JSONObject(jsonstr);
					String status = json.getString("status");
					String token_id = json.getString("token_id");
					String PAY_WX_WAP = "pay.weixin.wappay";
					if (status.equalsIgnoreCase("0")) {
						// �ɹ�
						try {
							Class msg_class = Class
									.forName("com.switfpass.pay.bean.RequestMsg");
							Object msg = msg_class.newInstance();
							@SuppressWarnings("unchecked")
							Method setTokenId = msg_class.getMethod(
									"setTokenId", String.class);
							setTokenId.invoke(msg, token_id);
							@SuppressWarnings("unchecked")
							Method setTradeType = msg_class.getMethod(
									"setTradeType", String.class);
							setTradeType.invoke(msg, PAY_WX_WAP);

							// ����֧��
							@SuppressWarnings("rawtypes")
							Class PayPlugin_Class = Class
									.forName("com.switfpass.pay.activity.PayPlugin");
							@SuppressWarnings("unchecked")
							Method unifiedH5Pay = PayPlugin_Class.getMethod(
									"unifiedH5Pay", Activity.class, msg_class);
							unifiedH5Pay.invoke(null, activity, msg);

						} catch (Exception e) {
							e.printStackTrace();
						}

					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

	}

	// ֧���ص�
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (data == null) {
			return;
		}

		String respCode = data.getExtras().getString("resultCode");
		if (!TextUtils.isEmpty(respCode)&& respCode.equalsIgnoreCase("success")) {
			this.handler.wxWapSuccess("success", "200");

			// ��ʾ֧���ɹ�
			// Toast.makeText(activity, "֧���ɹ�", Toast.LENGTH_LONG).show();
		} else { // ����״̬NOPAY״̬��ȡ��֧����δ֧����״̬
					// Toast.makeText(activity, "δ֧��",
					// Toast.LENGTH_LONG).show();
			this.handler.wxWapFailed("cancel", "204");

		}

	}

	public static abstract class WxSwiftPayHandler {
		public abstract void wxWapSuccess(String resultInfo, String resultStatus);

		// public abstract void wxWapCancel(String resultInfo,String
		// resultStatus);
		public abstract void wxWapFailed(String resultInfo, String resultStatus);
	}

	/**
	 * ��ȡ��ǰip��ַ
	 * 
	 * @param context
	 * @return
	 */
	public static String getLocalIpAddress(Context context) {
		try {
			WifiManager wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			int i = wifiInfo.getIpAddress();
			return int2ip(i);
		} catch (Exception ex) {
			return null;
		}
		// return null;
	}

	/**
	 * ��ip��������ʽת����ip��ʽ
	 * 
	 * @param ipInt
	 * @return
	 */
	public static String int2ip(int ipInt) {
		StringBuilder sb = new StringBuilder();
		sb.append(ipInt & 0xFF).append(".");
		sb.append((ipInt >> 8) & 0xFF).append(".");
		sb.append((ipInt >> 16) & 0xFF).append(".");
		sb.append((ipInt >> 24) & 0xFF);
		return sb.toString();
	}

}
