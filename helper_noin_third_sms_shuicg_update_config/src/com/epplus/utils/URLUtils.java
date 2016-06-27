package com.epplus.utils;

import android.content.Context;

import com.epplus.view.ShowFlag;

/**
 * url ����
 * @author zgt
 *
 */
public class URLUtils {

	
//	/**
//	 * ���� ����url
//	 */
//	public static final String W_BASE_URL = "http://thirdpay-cs.n8wan.com:29141/";
//	/**
//	 * ����   ����url
//	 */
//	public static final String D_BASE_URL = "http://thirdpay-webhook.n8wan.com:29141/";
//	
//	/**
//	 * �п��� url
//	 */
//	public static final String YK_BASE_URL="http://thirdpay.youkala.com:29141/";
	
	
//	/***
//	 * ΢��Wap֧���ص�
//	 * Context c,String OrderIdSelf,String OrderIdCp
//	 */
//	public static String notifyUrlWxWap(){
//		StringBuilder builder = getBaseUrl(ShowFlag.gameType);
//		String str = "http://192.168.0.111:8080/thirdpay-webhook/WxWapCallBackServlet";
//		
//		return str;
//	}
	
	
	//��Ϊ����
	
	/**
	 * ���� url
	 */
	//����
	//public static final String WEB_BASE_URL="http://thirdpay.oss.vanggame.com:29141/";
	
	//�п�
	public static final String WEB_BASE_URL="http://thirdpay.youkala.com:29141/";
	
	/**
	 * �ٶ�֧������url
	 */
	//����
	//public static final String WEB_BAIDU_URL="http://baidupay.oss.vanggame.com:29141/";
	//�п�
	public static final String WEB_BAIDU_URL="http://baidupay.youkala.com:29141/";
	
	/**
	 * ����֧������url
	 */
	//����
	//public static final String WEB_UNIONPAY_URL="http://unionpay.oss.vanggame.com:29141/";
	//�п�
	public static final String WEB_UNIONPAY_URL="http://unionpay.youkala.com:29141/";
	
	
	//֧����ǩ��
	public static final String WEB_ALISIGN_URL="http://thirdpay.youkala.com:29141/AlipaySign";
	
	
	/**
	 * ��ȡ������Tn
	 * @return
	 */
	public static String getUnionTn(){
		StringBuilder builder = new StringBuilder();
		builder.append(WEB_UNIONPAY_URL+"form05_6_2_Consume");
		return builder.toString();
	}
	
	
	
	/**
	 *  ֧�����ص�url 
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
	 * ΢�Żص�url 
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
	 * ��ȡWap΢��֧���� ����΢���ַ�
	 * @param c
	 * @return
	 */
	public static String wxWapStartApp(Context c) {
		String url  = WEB_BASE_URL+"WXWapServlet";
		return url;
	}
	
	
	/**
	 * �ٶȻص�url 
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
	 * ֧������ͳ��
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
	 * ֧������
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
		
		//��ʱΪ �п��Ĳ��� 
		builder.append(WEB_BASE_URL);
		
		
		return builder;
	}
	
	
	
	
	
	
	
	
	
	
	
}
