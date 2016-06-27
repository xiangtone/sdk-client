package com.epplus.utils;

import android.content.Context;

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
	public static final String WEB_BASE_URL="http://thirdpay.youkala.com:29141/";
	
	/**
	 * 百度支付请求url
	 */
	//创世
	//public static final String WEB_BAIDU_URL="http://baidupay.oss.vanggame.com:29141/";
	//有卡
	public static final String WEB_BAIDU_URL="http://baidupay.youkala.com:29141/";
	
	/**
	 * 银联支付请求url
	 */
	//创世
	//public static final String WEB_UNIONPAY_URL="http://unionpay.oss.vanggame.com:29141/";
	//有卡
	public static final String WEB_UNIONPAY_URL="http://unionpay.youkala.com:29141/";
	
	
	//支付宝签名
	public static final String WEB_ALISIGN_URL="http://thirdpay.youkala.com:29141/AlipaySign";
	
	
	/**
	 * 获取银联的Tn
	 * @return
	 */
	public static String getUnionTn(){
		StringBuilder builder = new StringBuilder();
		builder.append(WEB_UNIONPAY_URL+"form05_6_2_Consume");
		return builder.toString();
	}
	
	
	
	/**
	 *  支付宝回调url 
	 * @param c
	 * @param gameType
	 * @return
	 */
	public static String notifyUrlAlipy(Context c,String OrderIdSelf,String OrderIdCp){
		StringBuilder builder = getBaseUrl(ShowFlag.gameType);
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
		StringBuilder builder = getBaseUrl(ShowFlag.gameType);
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
		String url  = WEB_BASE_URL+"WXWapServlet";
		return url;
	}
	
	
	/**
	 * 百度回调url 
	 * @param c
	 * @param gameType
	 * @return
	 */
	public static String notifyUrlBaidu(Context c,String OrderIdSelf,String OrderIdCp){
		StringBuilder builder = getBaseUrl(ShowFlag.gameType);
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
	public static String payStatis(){
		StringBuilder builder = getBaseUrl(ShowFlag.gameType);
		builder.append("PayOperateCountServlet");
		return builder.toString();
	}
	
	/**
	 * 支付渠道
	 * @param c
	 * @param gameType
	 * @return
	 */
	public static String payChannle(){
		StringBuilder builder = getBaseUrl(ShowFlag.gameType);
		builder.append("CpInfoServlet");
		return builder.toString();
	}
	
	
	
	


	private static StringBuilder getBaseUrl(String gameType) {
		StringBuilder builder = new StringBuilder();
//		if(ShowFlag.wangyou.equals(gameType)){
//			builder.append(W_BASE_URL);
//		}else if (ShowFlag.danji.equals(gameType)) {
//			builder.append(D_BASE_URL);
//		}
		
		//暂时为 有卡的测试 
		builder.append(WEB_BASE_URL);
		
		
		return builder;
	}
	
	
	
	
	
	
	
	
	
	
	
}
