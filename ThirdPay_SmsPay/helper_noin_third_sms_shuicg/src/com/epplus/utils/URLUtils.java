package com.epplus.utils;

import android.content.Context;

import com.epplus.view.ShowFlag;

/**
 * url ����
 * @author zgt
 *
 */
public class URLUtils {

	/*
	//����
	֧���� : http://thirdpay-cs.n8wan.com:29141/AlipayCountServlet
	���� : http://thirdpay-cs.n8wan.com:29141/UnionpayCountServlet
	΢�� : http://thirdpay-cs.n8wan.com:29141/WechatpayCountServlet
	�ٶ� : http://thirdpay-cs.n8wan.com:29141/BaidupayCountServlet
	֧������ͳ�� : http://thirdpay-cs.n8wan.com:29141/PayOperateCountServlet
	֧������ : http://thirdpay-cs.n8wan.com:29141/CpInfoServlet
	������ȡtn : http://unionpay-cs.n8wan.com:29141/form05_6_2_Consume

	//����
	֧���� : http://thirdpay-webhook.n8wan.com:29141/AlipayCountServlet
	���� : http://thirdpay-webhook.n8wan.com:29141/UnionpayCountServlet
	΢�� : http://thirdpay-webhook.n8wan.com:29141/WechatpayCountServlet
	�ٶ� : http://thirdpay-webhook.n8wan.com:29141/BaidupayCountServlet
	֧������ͳ�� : http://thirdpay-webhook.n8wan.com:29141/PayOperateCountServlet
	֧������ : http://thirdpay-webhook.n8wan.com:29141/CpInfoServlet
	������ȡtn : http://unionpay-server.n8wan.com:29141/form05_6_2_Consume

	 */

	
	
	
	/**
	 * ���� ����url
	 */
	public static final String W_BASE_URL = "http://thirdpay-cs.n8wan.com:29141/";
	/**
	 * ����   ����url
	 */
	public static final String D_BASE_URL = "http://thirdpay-webhook.n8wan.com:29141/";
	
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
	
	/**
	 * ��ȡ������Tn
	 * @return
	 */
	public static String getUnionTn(){
		StringBuilder builder = new StringBuilder();
		if(ShowFlag.wangyou.equals(ShowFlag.gameType)){
			//����  ������ȡtn
			builder.append("http://unionpay-cs.n8wan.com:29141/form05_6_2_Consume");
		}else if (ShowFlag.danji.equals(ShowFlag.gameType)) {
			//����   ������ȡtn
			builder.append("http://unionpay-server.n8wan.com:29141/form05_6_2_Consume");
		}
		return builder.toString();
	}
	

	/**
	 * ��ȡ������Tn
	 * @return
	 */
	public static String getUnionTn(){
		StringBuilder builder = new StringBuilder();
		if(ShowFlag.wangyou.equals(ShowFlag.gameType)){
			//����  ������ȡtn
			builder.append("http://unionpay-cs.n8wan.com:29141/form05_6_2_Consume");
		}else if (ShowFlag.danji.equals(ShowFlag.gameType)) {
			//����   ������ȡtn
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
