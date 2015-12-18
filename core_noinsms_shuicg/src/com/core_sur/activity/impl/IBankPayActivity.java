package com.core_sur.activity.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.TreeMap;
import java.util.UUID;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.core_sur.Config;
import com.core_sur.activity.EActivity;
import com.core_sur.activity.impl.helper.AliPayHelper;
import com.core_sur.activity.impl.helper.PayResult;
import com.core_sur.bean.PayStatusMessage;
import com.core_sur.event.impl.PayCenterEvent;
import com.core_sur.finals.URLFinals;
import com.core_sur.publics.EPCoreManager;
import com.core_sur.tools.CommonUtils;
import com.core_sur.tools.IBankTools.AES;
import com.core_sur.tools.IBankTools.EncryUtil;
import com.core_sur.tools.IBankTools.RSA;

public class IBankPayActivity extends EActivity<PayCenterEvent> {

	public static final String TAG = "IBankPayActivity";
	public static Thread smspayThread;//sdk内部计费线程
	private TextView tittle;
	private WebView webview;
	//易宝一键支付url
	private String url = "https://ok.yeepay.com/paymobile/api/pay/request";
	private int currentView = -1;
	private final int VIEW_CHOSE = 0;
	private final int VIEW_BANK = 1;
	
	public static PayCenterEvent paymessage;//存储计费点信息
	
	/**
	 * -1 </br> 
	 *  0 支付成功</br> 
	 *  1 支付失败</br> 
	 *  2 短信支付</br> 
	 */
	private int isfee = -1;
	ViewGroup view_bankpay = null;
	ViewGroup view_chose = null;
	
	String timeStamp = null;
	
	//tbl_status更新状态
	private static final int PAY_START_YIBAO = 21;//易宝计费开始，
	private static final int PAY_START_ALIPAY = 22;//支付宝计费开始
	
	private static final int PAY_END_CANCEL = 31;//支付宝计费开始
	private static final int PAY_END_SUCCESS = 32;//支付宝计费开始

	public IBankPayActivity(PayCenterEvent messageContent) {
		super(messageContent);
		paymessage = messageContent;
	}

	public static void log(String info){
		Log.i("", "IBankPayActivity===>" + info);
	}
	
	@Override
	public void onCreate() {
		
		view_chose = findViewByFileName("activity_choice");
		if(view_chose == null){
			Toast.makeText(getContext(), "activity_chose布局获取失败", 0).show(); 
			getContext().finish();
			return;
		}

		view_bankpay = findViewByFileName("activity_ibankpay");
		if(view_bankpay == null){
			Toast.makeText(getContext(), "activity_ibankpay布局获取失败", 0).show(); 
			getContext().finish();
			return;
		}
		
		setCView(VIEW_CHOSE);
		
		initChoice();
		initIBank();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (currentView != VIEW_CHOSE) {
				if (isfee == -1) {
					sendMsg(1001,null);
					setCView(VIEW_CHOSE);
					return true;
				}
			}
		}
		return false;
	}
	
	//选择界面一共有7个可用textview，默认为GONE，便于扩展
	//对应的tag为从btn1 到 btn7
	@SuppressWarnings({ "deprecation"})
	private void initChoice(){

		Drawable bg_green = getXmlDrawble("text_bg_green");
		Drawable bg_grey = getXmlDrawble("text_bg_grey");
		Drawable bg_top = getXmlDrawble("top_tittle_green");
		Drawable close = getDrawble("close");
		
		RelativeLayout top = (RelativeLayout) view_chose.findViewWithTag("top");
		top.setBackgroundDrawable(bg_top);
		ImageView img_close = (ImageView) view_chose.findViewWithTag("close");
		img_close.setImageDrawable(close);		
		
		TextView btn_ibank = (TextView) view_chose.findViewWithTag("btn1");
		btn_ibank.setBackgroundDrawable(bg_green);
		btn_ibank.setTextColor(Color.rgb(0x17, 0xad, 0x03));
		btn_ibank.setText("银行卡支付");
		btn_ibank.setVisibility(View.VISIBLE);
		TextView btn_sms = (TextView) view_chose.findViewWithTag("btn2");
		btn_sms.setBackgroundDrawable(bg_green);
		btn_sms.setTextColor(Color.rgb(0x17, 0xad, 0x03));
		btn_sms.setText("话费支付");
		btn_sms.setVisibility(View.VISIBLE);
		TextView btn_alipay = (TextView) view_chose.findViewWithTag("btn3");
		btn_alipay.setBackgroundDrawable(bg_green);
		btn_alipay.setTextColor(Color.rgb(0x17, 0xad, 0x03));
		btn_alipay.setText("支付宝支付");
		btn_alipay.setVisibility(View.VISIBLE);
		
		TextView btn_cancle = (TextView) view_chose.findViewWithTag("cancle");
		btn_cancle.setBackgroundDrawable(bg_grey);
		btn_cancle.setTextColor(Color.rgb(0xb3, 0xb4, 0xb8));
		btn_cancle.setText("取消计费");
		btn_cancle.setVisibility(View.VISIBLE);
		
		btn_ibank.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setCView(VIEW_BANK);
				sendMsg(PAY_START_YIBAO,null);
				loadBankWeb();
			}
		});
		
		btn_sms.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getContext().finish();
				isfee = 2;
				//开始sdk通道计费
				sendMsg(0,null);
			}
		});
		
		btn_alipay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//setCView(VIEW_BANK);
				sendMsg(PAY_START_ALIPAY,null);
				//loadCardWeb();
				//开始支付宝计费
				alipay();
			}
		});
		
		btn_cancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getContext().finish();
			}
		});
		
		img_close.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getContext().finish();
			}
		});
	}
	
	private void initIBank(){
		
		Drawable bg_top = getXmlDrawble("top_tittle_green");
		Drawable close = getDrawble("close");
		
		RelativeLayout top = (RelativeLayout) view_bankpay.findViewWithTag("top");
		top.setBackgroundDrawable(bg_top);
		ImageView img_close = (ImageView) view_bankpay.findViewWithTag("close");
		img_close.setImageDrawable(close);
		
		tittle = (TextView) view_bankpay.findViewWithTag("tittle");
		webview = (WebView) view_bankpay.findViewWithTag("webview");
		//tittle.setText("加载中...");
	
		webview.loadDataWithBaseURL(null, "加载中...", "text/html", "utf-8", null);
		webview.setScrollbarFadingEnabled(false);
		webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		
		webview.getSettings().setJavaScriptEnabled(true);
		webview.addJavascriptInterface(new JavaScriptObject(getContext()), "myObj"); 
		webview.setWebViewClient(new WebViewClient() {
			
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				view.loadUrl(url);
				return true;
			}
			
			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
				//log("onPageFinished," + System.currentTimeMillis()/1000);
				view.loadUrl("javascript:window.myObj.showSource('<head>'+"  
						+ "document.getElementsByTagName('html')[0].innerHTML+'</head>');");  

			}
			
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// TODO Auto-generated method stub
				//log("onPageStarted," + System.currentTimeMillis()/1000);
				super.onPageStarted(view, url, favicon);
			}
			
		});
		
		img_close.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getContext().finish();
			}
		});
	}
	/**
	 * 支付宝计费调用
	 */
	private void alipay(){
		
		timeStamp = UUID.randomUUID().toString();
		
		String orderInfo = AliPayHelper.getOrderInfo(timeStamp,getMessage().getPayPoint(), "商品详情描述", Float.valueOf(getMessage().getPayNumber()) * 1.0 / 100 + "");

		// 对订单做RSA 签名
		String sign = AliPayHelper.sign(orderInfo);
		try {
			// 仅需对sign 做URL编码
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// 完整的符合支付宝参数规范的订单信息
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"+ AliPayHelper.getSignType();
				
		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(getContext());
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo);
				PayResult payResult = new PayResult(result);
				log("payresult" + result);
				// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
				String resultInfo = payResult.getResult();
				String resultStatus = payResult.getResultStatus();
				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					isfee = 0;
					getContext().finish();
				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					isfee = 1;
					getContext().finish();
				}
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}
	
	/**
	 * 易宝一键支付
	 */
	private void loadBankWeb() {
		String[] params = getBankParams(getMessage().getPayNumber(),getMessage().getPayPoint(),getIpAddress());
		String mUrl = "";
		try {
			mUrl = url + "?data=" + URLEncoder.encode(params[0],"utf-8") 
					+ "&encryptkey=" + URLEncoder.encode(params[1],"utf-8")
					+ "&merchantaccount=" + URLEncoder.encode(params[2],"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//log("mUrl:" + mUrl);
		webview.loadUrl(mUrl);
		
		//test
//		String data = "";
//		String encryptkey = "";
//		try {
//			data = URLEncoder.encode("Je1hbdWhvfCnniN2MLCoHfI1lEuzOS8RwjKRd7BoFfOjmxVNPXTADL+Ae/WFnqUpaLufnH08o1oe5qcJHO07gawS6FPnpn3AFLIw5sahUZkzf7DFXqbENY1ozVq3T23GcwG1sg9kJcSWuiwmbW8RewhjOEflAqS+WTiAxBIU3noA6f0UPtDdobS+Dioxa0EUsrLjGTTKMTzzOw0H5nfRFLBFieF4wxx7/afddW0yuV1dCAuB/DoG8qs4wDPIr4DrCnSoHipHLbBXCxBno6Q7dyJTaIC90e10h60+53cCNbpJoIy+WKiz+/mv1R7YUWRMb+Tzc7I//eWzyK0HhSMFcJLvzIoFJo9REw6EWVqj0k5GqkQq5KD35DHZEQ8AviqMNKDZJKRwIEcN7fyDVWKpzix0yrvKCeEQLEUoiQ33lZzvt7qlAM2K3bFrD1jwKmbnSxOfkMKaKy34/vHS6pfRPwtvziyW+TADhIeLG0y43i8LbiSIhje3oQghhbxdCKAF+wN3Yx2KKJtRMW8uSLv4Gw==","utf-8");
//			encryptkey = URLEncoder.encode("ch3RImMZdGSKUkbM3nBKkeGVZftFS0+P4cJKytbp4yMnlqGAbrmI8+Q3xL2m8UH4Q96W5ss2cYFk9O5TI9okf7MEaN0GM+sJnrles9f8UT9i9gSUbS/Adwk5RRytxuChu8tMRUwLbOApTPZslmiQy3bk4pyl0qlV4g40fSq+cME=","utf-8");
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		webview.loadUrl("http://121.40.16.65:833/YiBaoPay/PageCallBackUrl.aspx?data=" + data + "&encryptkey=" +encryptkey);
	}
	
	/**
	 * 易宝非银行卡支付
	 */
	private void loadCardWeb() {
		String[] params = getBankParams(getMessage().getPayNumber(),getMessage().getPayPoint(),getIpAddress());
		String mUrl = "";
		try {
			mUrl = url + "?data=" + URLEncoder.encode(params[0],"utf-8") 
					+ "&encryptkey=" + URLEncoder.encode(params[1],"utf-8")
					+ "&merchantaccount=" + URLEncoder.encode(params[2],"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//log("mUrl:" + mUrl);
		webview.loadUrl(mUrl);
	}
	
	@Override
	public void onDestroy() {
		if (isfee == 1) {
			sendMsg(PAY_END_CANCEL,timeStamp);
		}else if (isfee == 0) {
			sendMsg(PAY_END_SUCCESS,timeStamp);
		}else if (isfee == -1) {
			sendMsg(1078,null);
		}
	}
	
	public void setCView(int v) {
		// TODO Auto-generated method stub
		if (v == VIEW_CHOSE) {
			currentView = VIEW_CHOSE;
			super.setContentView(view_chose);
		}else if (v == VIEW_BANK) {
			currentView = VIEW_BANK;
			super.setContentView(view_bankpay);
		}
		
	}
	
	/**
	 * 调用epcoerManager的属性和方法
	 * 31 fail  32 success 0开启sms计费线程
	 * @param what
	 * @param msg 
	 */
	private void sendMsg(int what,String msg){
		Intent intent = new Intent(MessageFormat.format("{0}.com.pay.activitypay", getContext().getPackageName()));
		intent.putExtra("tag", TAG);
		intent.putExtra("what", what);
		intent.putExtra("msg", msg + "");
		getContext().sendBroadcast(intent);
	}
	
	
	/**
	 * 统一回调方法，必须是static
	 * @param what
	 * @param msg 
	 */
	public static void callBak(int what,String info){
		//log("callBak:" + info);
		if (what == PAY_START_YIBAO) {
			//易宝一键支付计费
			changeStatus(PAY_START_YIBAO,null);
		}else if (what == PAY_START_ALIPAY) {
			//支付宝计费开始
			changeStatus(PAY_START_ALIPAY,null);
		}else if (what == PAY_END_CANCEL) {
			//更改计费状态
			changeStatus(PAY_END_CANCEL,info);
			Message msg = Message.obtain();
			msg.what = 4002;
			msg.obj = "支付失败";
			EPCoreManager.getInstance().payHandler.sendMessage(msg);
			//关闭壳dialog
			Message msgClose = Message.obtain();
			msgClose.what = 10000;
			EPCoreManager.getInstance().payHandler.sendMessage(msgClose);
		}else if (what == PAY_END_SUCCESS) {
			//更改计费状态
			changeStatus(PAY_END_SUCCESS,info);
			Message msg = Message.obtain();
			msg.what = 4001;
			msg.obj = "支付成功";
			EPCoreManager.getInstance().payHandler.sendMessage(msg);
			//关闭壳dialog
			Message msgClose = Message.obtain();
			msgClose.what = 10000;
			EPCoreManager.getInstance().payHandler.sendMessage(msgClose);
		}else if (what == 1078) {
			//更改计费状态
			Message msg = Message.obtain();
			msg.what = 1078;
			msg.obj = "支付失败";
			EPCoreManager.getInstance().payHandler.sendMessage(msg);
			//关闭壳dialog
			Message msgClose = Message.obtain();
			msgClose.what = 10000;
			EPCoreManager.getInstance().payHandler.sendMessage(msgClose);
		}else if (what == 1000) {
			//取消计费，关闭dialog
			Message msgClose = Message.obtain();
			msgClose.what = 10000;
			EPCoreManager.getInstance().payHandler.sendMessage(msgClose);
		}else if (what == 1001) {
			//取消银行卡支付，在银行卡支付界面返回计费选择界面调用，同步tblstatic数据
			changeStatus(PAY_END_CANCEL,null);
		}else if (what == 0) {
			//更改SMS计费
			if (smspayThread != null) {
				smspayThread.start();
			}
		}
			
	}
	

	
	/**
	 * 2X：申请某个网银计费（给10个值，不够在拓展，比如 21代表 支付宝 22代表微信）</br> 
	 * 31：网银支付取消</br> 
	 * 32：网银支付成功</br>
	 * </br> 
	 *  21:申请易宝一键支付</br> 
	 *  22:申请易宝非银行卡支付</br> 
	 * 
	 * @param payStatus
	 */
	public static void changeStatus(int payStatus,String timestamp) {
		EPCoreManager.getInstance().sendMessenger(URLFinals.WEB_STATISTICAL,
			new PayStatusMessage(payStatus,timestamp), null);
		//log("PayStatusMessage:" + new PayStatusMessage(payStatus,timestamp).getJsonString());
	}

	public String[] getBankParams(String payCount,String payPoint,String ip){
		
		//time 2015-10-27 11:39:47
		String merchantAESKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCblTA3cQDUtEZ" +
					"2NHtcJ82VW/GVvu7ltYJUN28cukjkvql4BxbVwJX0Lg9/es2cQRcTgKpENdb7i8mH/" +
					"apuDDybB+0miJM+XUhfsWeyJ3H0+h5Ro7JCqHLlKndPE+FZTDDvI3mU5VGKuKpLdJG" +
					"vUTrvjZAiGH/BCAz6+Czm5+Z5fQIDAQAB";

		String merchantPrivateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoG" +
				"BAIxQZ40/JUe0F6pPvXDNifPg3zZvu3Y0FvgZ9Ltqidyja20vnlfSaZwuxh0mKnfWKR" +
				"7UR5SvUB26Ga5Hnwb7hZyto34+QbJEunoev08m1djHn8Ri2iL/ksyjbZpX8exLe+JCA" +
				"U3oOOVhb5vKz/I4Rp0mFXlZfIvEUdxQMBr13El9AgMBAAECgYAu3B9pJaCOrM+GenD6" +
				"/jnHL9977568oDLXktuPx8nY3N7grRmFnOjWVyykyX0/Xk+HUz/AoTfxZZh0AslSbKe" +
				"3J++Je6w/fAbt6b4tDN0lLYH0AgWyV25xBuypFWB8dSHuDLV8W7IaI0xVJdaYYuaKtW" +
				"S5GlAK4y8TWhDWEAj8AQJBAMaPRW4efBZuanOfuK9aXJJmbQHeYHGzqCVS7fb6/X+sU" +
				"9XB7Nb7Bt2ZD+GWAYw8ZKWXEJSxu6MrZSmY/Tqg5n0CQQC056RTAx1hlMdXbDCvD8Ku" +
				"2o8c1+r2wZSNSjImiMEXRYxDYrmbdv8et4K6MqyL0SuIBXBY/ASA5dMbRBTj718BAkA" +
				"dqT1X69nhh/EDALMYDsmRXRefldaWZZ4SpUbDBE9mCRok7mkw3CL0a28B5eSpTEREsE" +
				"Jb1AkRoOPlyOLbJToJAkBfLUAXLJxYqp32MMWsVO5ocDS7SB4mlNJDQ7X+8BQm2kBhc" +
				"5+TW5f94S62J9L//Jyag8IIDw+PgVsADWGwSlABAkBnS+tgTjF6CYXQpa6FN4dO7RSh" +
				"IJV9NfrMIpuNYY04pL/AjCzxgiJLT0TdqUBlTVrJ34y2COoIa83+BtGDTMy8";

		String merchantaccount = "10012462675";
		
		String yeepayPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCv+9I5T2YAM8UlzlI" +
						"4tm9GJ4ShAy6Ev/nzXFv9YzVpMHt8e4cLDhEFvj0Q66MlPv9V1vSA8pQZ675vVTb" +
						"Bv7A33MW7bk5PHmPsQPVeX7MMr+RmjhsGtiUDCSelBdBGNZ6D5HXfH0tYTwOTIAj" +
						"9Ui0Iwp6KsDOYGFgbjabDD0neUwIDAQAB";

		String AESKey = "0123456789abcdef";
		
		
		//测试
//		String merchantPrivateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGB" +
//				"ALD0Tou2w7EHbP3q5wi5PG5xrvC0CBawXxSI1PlZAGo2iFYhaBK6SsB5UiYT64fSR3Ye" +
//				"mQGS2vSqQii5vYdOfrffvvDprrr7Vo7BziS6sJQ9B0/DzwN2zY7jJBCz55CLMBsZCtuq" +
//				"DNVxTcsOcZnrgSSMqnhk+usuR4hPoV9qABeHAgMBAAECgYAfnth2UOdxN/F7AkHcpjUt" +
//				"SzVGn/UeENA8vCLKl+PiFvKP6ZJOXmnDMSrD0SVydNn+OoN+634i4FXIL0C18Anmh4Il" +
//				"QM9hj+rFTg1bMSUHvSPKoZpoEfjR0R+3TQF8PycBbaIWgLV/5NA8dMld0DvF5d8bbqpg" +
//				"H6FzEXZPvF8OgQJBANwHRhCu+o/JoCoH0coVhNFuobVYZU0pQRlfDaE4ph0+daiJ4HlT" +
//				"630JrBFb728Ga7E81dsfGMSi1N6QSipJMEECQQDN4kb+O/ecDNQrEsjA0LqDXkaKsRP6" +
//				"iU/HVNyr4Z/7ojHws0F5Vypj1euCII+V6U7StMKRbSaB1GI8Bs34llXHAkEAnIc0KiRB" +
//				"Lk+S+LOtZGVgoplgwyEKmBUUMdd0W9BwJHfNvkOwBMBV1BMwbP0JXeOkc2dDAGqj9Sed" +
//				"5mOhz2lXwQJAVeA0TIcm2Ohg9zZ2ljZ6FaGVOvRxqObtZ+91vBv4ZzVYL1YV0U8SV2I7" +
//				"QaPjQFx4jFrpbU9h6HV2JCOSdkX+sQJBAJ+PfNA0b25HuY9n4cTk/hLc2TCWVDsPnONu" +
//				"hNpuRpXqxu9L0p2aHX5JLf1kTUoYxqmlEjx6IYcObcB9Snw0Tf0=";
//
//		String AESKey = "0123456789abcdef";
//		
//		String yeepayPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCKcSa7wS6OMUL" +
//				"4oTzJLCBsE5KTkPz9OTSiOU6356BsR6gzQ9kf/xa+Wi1ZANTeNuTYFyhlCI7ZCLW7QNz" +
//				"wAYSFStKzP3UlUzsfrV7zge8gTgJSwC/avsZPCWMDrniC3HiZ70l1mMBK5pL0H6NbBFJ" +
//				"6XgDIw160aO9AxFZa5pfCcwIDAQAB";
//		
//		String merchantaccount = "10000418926";
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String str = formatter.format(new Date());
		String data = "";
		String encryptkey = "";	
		
		TreeMap<String, Object> dataMap = new TreeMap<String, Object>();
		dataMap.put("merchantaccount", merchantaccount);
		dataMap.put("orderid", UUID.randomUUID().toString());
		dataMap.put("productcatalog", "1");//������Ʒ
		dataMap.put("productname", payPoint);
		dataMap.put("identityid", CommonUtils.getImei(getContext()));//imei
		dataMap.put("userip", ip);
		dataMap.put("terminalid", CommonUtils.getImei(getContext()));//imei
		//dataMap.put("userua", userua);
		dataMap.put("transtime", (int)(System.currentTimeMillis()/1000));//ʱ���
		dataMap.put("amount", Integer.parseInt(payCount));
		dataMap.put("identitytype", 0);//imei类型
		dataMap.put("terminaltype", 0);//imei类型
		//dataMap.put("productdesc", "");//商品描述
		dataMap.put("fcallbackurl", Config.NOTIFY_URL_YIBFPAY);//SDK回调页面
		dataMap.put("callbackurl", Config.NOTIFY_URL_YIBPAY);//服务器同步接口
		//dataMap.put("paytypes", "");
		//dataMap.put("currency", );
		//dataMap.put("orderexpdate", "");
		//dataMap.put("version", "");
		//dataMap.put("cardno", "");
		//dataMap.put("idcardtype", "");
		//dataMap.put("idcard", "");
		//dataMap.put("owner", "");
		String sign = EncryUtil.handleRSA(dataMap, merchantPrivateKey);
		dataMap.put("sign", sign);
		try {
			//String jsonStr =JSON.toJSONString(dataMap);
			String jsonStr = new JSONObject(dataMap).toString();
			//log("jsonStr:" + jsonStr);
			data = AES.encryptToBase64(jsonStr, AESKey);
			encryptkey = RSA.encrypt(AESKey, yeepayPublicKey);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return new String[]{data,encryptkey,merchantaccount};
	}
	
	public static String getIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()
							&& inetAddress instanceof Inet4Address) {
						// if (!inetAddress.isLoopbackAddress() && inetAddress
						// instanceof Inet6Address) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "172.0.0.1";
	}
	
	public String getIP(){
		HttpURLConnection urlConn;
		String ip = "127.0.0.1";
		try {
			URL uri = new URL("http://www.ip.cn/");
			urlConn = (HttpURLConnection) uri.openConnection();
			urlConn.setDoInput(true);
			urlConn.setConnectTimeout(10000);
			urlConn.setReadTimeout(20000);
			urlConn.setDoOutput(true);//使用 URL 连接进行输出
			urlConn.setDoInput(true);//使用 URL 连接进行输入
			urlConn.connect();
			
			if (urlConn.getResponseCode() != 200) {
				return ip;
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line = null;
			while (((line = in.readLine()) != null)) {
				sb.append(line);
			}
			in.close();
			
			String res = sb.toString().trim();
			ip = res.substring(res.indexOf("<code>") + 6, res.indexOf("</code>"));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ip;
	}
	
	public class JavaScriptObject {  
	    Context mContxt;  
	    
	    public JavaScriptObject(Context mContxt) {  
	        this.mContxt = mContxt;  
	    }  
	  
	    //页面调用本地方法
	    public void fun1FromAndroid(String name) {  
	    	//log("fun1FromAndroid," + name);
	        //Toast.makeText(mContxt, name, Toast.LENGTH_LONG).show();
	        if ("SUCCESS".equals(name)) {
	        	isfee = 0;
	        	getContext().finish();
			}else{
				isfee = 1;
				getContext().finish();
			}
	    }  
	  
	    //抓取页面内容
	    public void showSource(String html) {  
	    	//log("html," + html);
	    	if (html.contains("SUCCESS")) {
	    		isfee = 0;
			}else if(html.contains("FAIL")){
				isfee = 1;
			}
	    } 

	    public void fun2(String name) {  
	        Toast.makeText(mContxt, "调用fun2:" + name, Toast.LENGTH_SHORT).show();  
	    }  
	} 
}
