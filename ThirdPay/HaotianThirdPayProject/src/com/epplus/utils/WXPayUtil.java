package com.epplus.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.ipaynow.plugin.api.IpaynowPlugin;
import com.xqt.now.paysdk.XqtPay;
import com.xqt.now.paysdk.XqtPay.XqtPayListener;

/**
 * ΢��֧��
 * @author zgt
 *
 */
public class WXPayUtil implements XqtPayListener{

	private  Activity act = null;
	private static ProgressDialog progressDialog = null;
	
	// �̻���Կ
	private static final String key = "88c1a59b8fa9d217c8c632c2921ef286";
	
	private  String notifyUrl;
	
	private WXPayHandler wxpayHandler;
	
	public WXPayUtil(Activity act,WXPayHandler wxpayHandler) {
		this.act =act;
		this.wxpayHandler = wxpayHandler;
		
		String baseUrl = "http://thirdpay-webhook.n8wan.com:29141/thirdpayCountServlet";
		this.notifyUrl = baseUrl+"?"+ConfigUtils.xx_notifyData+"="+ConfigUtils.getNotifyJsonData(act,ConfigUtils.WX);
	}
	
	
	/**
	 * ֧��
	 * @param price  ��Ʒ��� ���֣�
	 * @param orderName   ��Ʒ����
	 * @param orderDetail  ��Ʒ����
	 */
	public  void pay(String price,String orderName,String orderDetail){
		prePayMessage(price, orderName, orderDetail);
		XqtPay.mhtOrderNo = new SimpleDateFormat("yyyyMMddHHmmss",
				Locale.CHINA).format(new Date());
		XqtPay.payChannelType = "13";
		XqtPay.sign = Sign();
		goToPay();
		
		
	}
	
	
	private void goToPay() {
		ConnectivityManager manager = (ConnectivityManager)act.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			progressDialog = new ProgressDialog(act);
			progressDialog.setTitle("������ʾ");
			progressDialog.setMessage("֧����ȫ����ɨ��");
			progressDialog.setCancelable(false);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.show();
			// ��ȡ֧������
			XqtPay.Transit(act, this);

		} else {
			Builder builder = new AlertDialog.Builder(act);
			builder.setTitle("����״̬");
			builder.setMessage("û�п�������,�Ƿ�����������");
			builder.setPositiveButton("��",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							act.startActivity(new Intent(
									android.provider.Settings.ACTION_WIRELESS_SETTINGS));
						}
					});
			builder.setNegativeButton("��",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Toast.makeText(act, "����ʧ��", Toast.LENGTH_SHORT)
									.show();
						}
					});
			builder.create().show();
		}
	}
	
	
	
	@Override
	public void success(String str) {
		progressDialog.dismiss();
		// �Ƿ��δ֧�������ٴ�֧����ʾ
		IpaynowPlugin.setShowConfirmDialog(true);
		// ����֧������
		IpaynowPlugin.pay(act, str);
		
	}
	
	
	@Override
	public void error(String str) {
		progressDialog.dismiss();
		//Toast.makeText(act, str, 1).show();
		
	}

	
	
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null) {
			return;
		}
		String respCode = data.getExtras().getString("respCode");
		String respMsg = data.getExtras().getString("respMsg");
		
		StringBuilder temp = new StringBuilder();
		if (respCode.equals("00")) {
			temp.append("����״̬:�ɹ�");
			wxpayHandler.WXPaySuccess(temp.toString(), "");
		}

		if (respCode.equals("02")) {
			temp.append("����״̬:ȡ��");
			wxpayHandler.WXPayCancel(temp.toString(), "");
		}

		if (respCode.equals("01")) {
			temp.append("����״̬:ʧ��").append("\n").append("ԭ��:" + respMsg);
			wxpayHandler.WXPayFailed(temp.toString(), "");
		}

		if (respCode.equals("03")) {
			temp.append("����״̬:δ֪").append("\n").append("ԭ��:" + respMsg);
			wxpayHandler.WXPayFailed(temp.toString(), "");
		}
		
	}


	
	
	
	
	private void prePayMessage(String price,String orderName,String orderDetail) {
		
		XqtPay.consumerId = "154345";
		XqtPay.mhtOrderName = orderName;
		XqtPay.mhtOrderAmt = price;
		XqtPay.mhtOrderDetail = orderDetail;
		XqtPay.notifyUrl =notifyUrl;
		XqtPay.superid = "100000";
		
		IpaynowPlugin.setShowConfirmDialog(false);
	}
	
	
	private String Sign() {
		String str = "customerid=" + XqtPay.consumerId + "&sdcustomno="
				+ XqtPay.mhtOrderNo + "&orderAmount=" + XqtPay.mhtOrderAmt
				+ key;
		return getMD5(str).toUpperCase();
	}
	
	public static String getMD5(String content) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(content.getBytes());
			return getHashString(digest);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static String getHashString(MessageDigest digest) {
		StringBuilder builder = new StringBuilder();
		for (byte b : digest.digest()) {
			builder.append(Integer.toHexString((b >> 4) & 0xf));
			builder.append(Integer.toHexString(b & 0xf));
		}
		return builder.toString();
	}


	public static abstract class WXPayHandler{
		public abstract void WXPaySuccess(String resultInfo,String resultStatus);
		public abstract void WXPayFailed(String resultInfo,String resultStatus);
		public abstract void WXPayCancel(String resultInfo,String resultStatus);
	}

	
	
}
