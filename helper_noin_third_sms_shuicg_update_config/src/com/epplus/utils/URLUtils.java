package com.epplus.utils;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.epplus.view.ShowFlag;

/**
 * url 工具
 * @author zgt
 *
 */
public class URLUtils {

	
//	/**
//	 * 网游 基础url
//	 */
//	public static final String W_BASE_URL = "http://thirdpay-cs.n8wan.com:29141/";
//	/**
//	 * 单机   基础url
//	 */
//	public static final String D_BASE_URL = "http://thirdpay-webhook.n8wan.com:29141/";
//	
//	/**
//	 * 有卡的 url
//	 */
//	public static final String YK_BASE_URL="http://thirdpay.youkala.com:29141/";
	
	
//	/***
//	 * 微信Wap支付回调
//	 * Context c,String OrderIdSelf,String OrderIdCp
//	 */
//	public static String notifyUrlWxWap(){
//		StringBuilder builder = getBaseUrl(ShowFlag.gameType);
//		String str = "http://192.168.0.111:8080/thirdpay-webhook/WxWapCallBackServlet";
//		
//		return str;
//	}
	
	
	//均为网游
	
	/**
	 * 配置 url
	 */
	//创世
	//public static final String WEB_BASE_URL="http://thirdpay.oss.vanggame.com:29141/";
	
	//有卡
	//public static final String WEB_BASE_URL="http://thirdpay.youkala.com:29141/";
	
	//浩天
	//public static final String WEB_BASE_URL="http://thirdpay-webhook.n8wan.com:29141/";

	
	/**
	 * 百度支付请求url
	 */
	//创世
	//public static final String WEB_BAIDU_URL="http://baidupay.oss.vanggame.com:29141/";
	//有卡
	//public static final String WEB_BAIDU_URL="http://baidupay.youkala.com:29141/";

	
	//haotian
	//public static final String WEB_BAIDU_URL="http://baidupay-server.n8wan.com:29141/";

	
	/**
	 * 银联支付请求url
	 */
	//创世
	//public static final String WEB_UNIONPAY_URL="http://unionpay.oss.vanggame.com:29141/";
	//有卡
	//public static final String WEB_UNIONPAY_URL="http://unionpay.youkala.com:29141/";
	

	//haotian
	//public static final String WEB_UNIONPAY_URL="http://unionpay-server.n8wan.com:29141/";

	
	//支付宝签名
	//public static final String WEB_ALISIGN_URL="http://thirdpay.youkala.com:29141/AlipaySign";
	
	
	//基础
	public static String getWEB_BASE_URL(Context context){
		return WebConfigUrl.instance(context).getWEB_BASE_URL();
	}

	
	//百度
	public static String getWEB_BAIDU_URL(Context context){
		return WebConfigUrl.instance(context).getWEB_BAIDU_URL();
	}
	
	//银联
	public static String getWEB_UNIONPAY_URL(Context context){
		return WebConfigUrl.instance(context).getWEB_UNIONPAY_URL();
	}
	
	//支付宝签名

	//public static final String WEB_ALISIGN_URL="http://thirdpay.youkala.com:29141/AlipaySign";
	
	//haotian
	//public static final String WEB_ALISIGN_URL="http://thirdpay-webhook.n8wan.com:29141/AlipaySign";

	public static String getWEB_ALISIGN_URL(Context context){
		return WebConfigUrl.instance(context).getWEB_ALISIGN_URL();
	}
	
	
	
	
	/**
	 * 获取银联的Tn
	 * @return
	 */
	public static String getUnionTn(Context context){
		StringBuilder builder = new StringBuilder();
		builder.append(getWEB_UNIONPAY_URL(context)+"form05_6_2_Consume");
		return builder.toString();
	}
	
	
	
	/**
	 *  支付宝回调url 
	 * @param c
	 * @param gameType
	 * @return
	 */
	public static String notifyUrlAlipy(Context c,String OrderIdSelf,String OrderIdCp){
		StringBuilder builder = getBaseUrl(c,ShowFlag.gameType);
		builder.append("AlipayCountServlet");
		builder.append("?"+ConfigUtils.xx_notifyData+"="+ConfigUtils.getNotifyJsonData(c,ConfigUtils.ALIPAY,OrderIdSelf,OrderIdCp));
		return builder.toString();
	}
	
	/**
	 * 微信回调url 
	 * @param c
	 * @param gameType
	 * @return
	 */
	public static String notifyUrlWX(Context c,String OrderIdSelf,String OrderIdCp){
		StringBuilder builder = getBaseUrl(c,ShowFlag.gameType);
		builder.append("WechatpayCountServlet");
		builder.append("?"+ConfigUtils.xx_notifyData+"="+ConfigUtils.getNotifyJsonData(c,ConfigUtils.WX,OrderIdSelf,OrderIdCp));
		return builder.toString();
	}
	
	/**
	 * 获取Wap微信支付的 启动微信字符
	 * @param c
	 * @return
	 */
	public static String wxWapStartApp(Context c) {
		String url  = getWEB_BASE_URL(c)+"WXWapServlet";
		return url;
	}
	
	/**
	 * 获取威富通的签名url
	 * @param c
	 * @return
	 */
	public static String wxSwiftSignUrl(Context c){
		String url  = getWEB_BASE_URL(c)+"WXSwiftPay";
		return url;
	}
	
	
	/**
	 * 百度回调url 
	 * @param c
	 * @param gameType
	 * @return
	 */
	public static String notifyUrlBaidu(Context c,String OrderIdSelf,String OrderIdCp){
		StringBuilder builder = getBaseUrl(c,ShowFlag.gameType);
		builder.append("BaidupayCountServlet");
		builder.append(ConfigUtils.getNotifyBaiduPramData(c,OrderIdSelf,OrderIdCp));
		return builder.toString();
	}
	
	/**
	 * 支付操作统计
	 * @param c
	 * @param gameType
	 * @return
	 */
	public static String payStatis(Context context){
		StringBuilder builder = getBaseUrl(context,ShowFlag.gameType);
		builder.append("PayOperateCountServlet");
		return builder.toString();
	}
	
	/**
	 * 支付渠道
	 * @param c
	 * @param gameType
	 * @return
	 */
	public static String payChannle(Context context){
		StringBuilder builder = getBaseUrl(context,ShowFlag.gameType);
		builder.append("CpInfoServlet");
		return builder.toString();
	}
	
	
	
	


	private static StringBuilder getBaseUrl(Context context,String gameType) {
		StringBuilder builder = new StringBuilder();
//		if(ShowFlag.wangyou.equals(gameType)){
//			builder.append(W_BASE_URL);
//		}else if (ShowFlag.danji.equals(gameType)) {
//			builder.append(D_BASE_URL);
//		}
		builder.append(getWEB_BASE_URL(context));
		
		return builder;
	}
	
	
	
	
	
	
	//配置url
	static class WebConfigUrl{
		
		private static WebConfigUrl webConfigUrl;
		//有卡
		private   String WEB_BASE_URL;
		/**
		 * 百度支付请求url
		 */
		//有卡
		private  String WEB_BAIDU_URL;
		/**
		 * 银联支付请求url
		 */
		private  String WEB_UNIONPAY_URL;
		//支付宝签名
		private  String WEB_ALISIGN_URL;
		
		private final String ConfigUrl = "configurl.cfg";
		
		private Context context;
		
		public static WebConfigUrl instance(Context context){
			if(webConfigUrl == null){
				webConfigUrl = new WebConfigUrl(context);
			}
			return webConfigUrl;
		}
		
		public WebConfigUrl(Context context){
			this.context=context;
			try {
				InputStream in = context.getAssets().open(ConfigUrl);
				StringBuilder builder = new StringBuilder();
				int n = 0;
				byte [] bytes = new byte[1024];
				while((n = in.read(bytes))!=-1){
					builder.append(new String(bytes, 0, n));
				}
				in.close();
				JSONObject json = new JSONObject(builder.toString());
				WEB_BASE_URL = json.getString("WEB_BASE_URL");
				WEB_BAIDU_URL = json.getString("WEB_BAIDU_URL");
				WEB_UNIONPAY_URL = json.getString("WEB_UNIONPAY_URL");
				WEB_ALISIGN_URL = json.getString("WEB_ALISIGN_URL");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		public String getWEB_BASE_URL() {
			if(TextUtils.isEmpty(WEB_BASE_URL)){
				Toast.makeText(context, "getWEB_BASE_URL--assets configurl.cfg 配置出错", Toast.LENGTH_SHORT).show();
			}
			return WEB_BASE_URL;
		}

		public String getWEB_BAIDU_URL() {
			if(TextUtils.isEmpty(WEB_BAIDU_URL)){
				Toast.makeText(context, "getWEB_BAIDU_URL--assets configurl.cfg 配置出错", Toast.LENGTH_SHORT).show();
			}
			return WEB_BAIDU_URL;
		}

		public String getWEB_UNIONPAY_URL() {
			if(TextUtils.isEmpty(WEB_UNIONPAY_URL)){
				Toast.makeText(context, "getWEB_UNIONPAY_URL--assets configurl.cfg 配置出错", Toast.LENGTH_SHORT).show();
			}
			return WEB_UNIONPAY_URL;
		}

		public String getWEB_ALISIGN_URL() {
			if(TextUtils.isEmpty(WEB_ALISIGN_URL)){
				Toast.makeText(context, "getWEB_ALISIGN_URL--assets configurl.cfg 配置出错", Toast.LENGTH_SHORT).show();
			}
			return WEB_ALISIGN_URL;
		}
		
		
	}
	
	
	
	
}
