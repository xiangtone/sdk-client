package com.epplus.utils;

import android.content.Context;

import com.epplus.view.ShowFlag;

/**
 * url 工具
 * @author zgt
 *
 */
public class URLUtils {

	/*
	//网游
	支付宝 : http://thirdpay-cs.n8wan.com:29141/AlipayCountServlet
	银联 : http://thirdpay-cs.n8wan.com:29141/UnionpayCountServlet
	微信 : http://thirdpay-cs.n8wan.com:29141/WechatpayCountServlet
	百度 : http://thirdpay-cs.n8wan.com:29141/BaidupayCountServlet
	支付操作统计 : http://thirdpay-cs.n8wan.com:29141/PayOperateCountServlet
	支付渠道 : http://thirdpay-cs.n8wan.com:29141/CpInfoServlet
	银联获取tn : http://unionpay-cs.n8wan.com:29141/form05_6_2_Consume

	//单机
	支付宝 : http://thirdpay-webhook.n8wan.com:29141/AlipayCountServlet
	银联 : http://thirdpay-webhook.n8wan.com:29141/UnionpayCountServlet
	微信 : http://thirdpay-webhook.n8wan.com:29141/WechatpayCountServlet
	百度 : http://thirdpay-webhook.n8wan.com:29141/BaidupayCountServlet
	支付操作统计 : http://thirdpay-webhook.n8wan.com:29141/PayOperateCountServlet
	支付渠道 : http://thirdpay-webhook.n8wan.com:29141/CpInfoServlet
	银联获取tn : http://unionpay-server.n8wan.com:29141/form05_6_2_Consume

	 */

	
	
	
	/**
	 * 网游 基础url
	 */
	public static final String W_BASE_URL = "http://thirdpay-cs.n8wan.com:29141/";
	/**
	 * 单机   基础url
	 */
	public static final String D_BASE_URL = "http://thirdpay-webhook.n8wan.com:29141/";
	
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
	
	/**
	 * 获取银联的Tn
	 * @return
	 */
	public static String getUnionTn(){
		StringBuilder builder = new StringBuilder();
		if(ShowFlag.wangyou.equals(ShowFlag.gameType)){
			//网游  银联获取tn
			builder.append("http://unionpay-cs.n8wan.com:29141/form05_6_2_Consume");
		}else if (ShowFlag.danji.equals(ShowFlag.gameType)) {
			//单机   银联获取tn
			builder.append("http://unionpay-server.n8wan.com:29141/form05_6_2_Consume");
		}
		return builder.toString();
	}
	

	/**
	 * 获取银联的Tn
	 * @return
	 */
	public static String getUnionTn(){
		StringBuilder builder = new StringBuilder();
		if(ShowFlag.wangyou.equals(ShowFlag.gameType)){
			//网游  银联获取tn
			builder.append("http://unionpay-cs.n8wan.com:29141/form05_6_2_Consume");
		}else if (ShowFlag.danji.equals(ShowFlag.gameType)) {
			//单机   银联获取tn
			builder.append("http://unionpay-server.n8wan.com:29141/form05_6_2_Consume");
		}
		return builder.toString();
	}
	
	
	private static StringBuilder getBaseUrl(String gameType) {
		StringBuilder builder = new StringBuilder();
		if(ShowFlag.wangyou.equals(gameType)){
			builder.append(W_BASE_URL);
		}else if (ShowFlag.danji.equals(gameType)) {
			builder.append(D_BASE_URL);
		}
		return builder;
	}
	
	
	
	
	
	
	
	
	
	
	
}
