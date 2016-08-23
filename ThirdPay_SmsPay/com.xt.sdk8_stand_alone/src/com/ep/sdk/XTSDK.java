package com.ep.sdk;


import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import com.epplus.publics.EPPayHelper;
import com.epplus.view.PayParams;

public class XTSDK {
	
	private static XTSDK xtsdk;
	
	private boolean isInit;
	
	
	private XTSDK(){
		isInit = false;
	} 
	
	/**
	 * 获取XTSDK
	 * @return
	 */
	public static XTSDK getInstance(){
		if(xtsdk==null){
			xtsdk = new XTSDK();
		}
		return xtsdk;
	}
	
	/**
	 * 初始化
	 */
	public void init(final Activity ac,String payContact,Handler handler){
		EPPayHelper.getInstance(ac).initPay(true,payContact);//"4001059566"
		EPPayHelper.getInstance(ac).setPayListen(handler);
		//Payment.init(ac);
		isInit = true;
		
	}
	
	
	/**
	 * 有界面支付 
	 */
	public boolean pay(Activity ac,PayParams params){
		
		if (isInit) {
			EPPayHelper.getInstance(ac).pay(params);
		}
		return true;
	}
	
	/**
	 * 无界面支付 
	 * * @param flag 1 微信wap 2 支付宝  3银联 4 微信支付 5，百度  6 ,短信
	 */
	public boolean pay(Activity ac,PayParams params,int flag){
		
		if (isInit) {
			EPPayHelper.getInstance(ac).pay(params,flag);
		}
		return true;
	}
	
	
	
	
	/**
	 * 退出
	 */
	public void exit(Activity ac){
		try {
			EPPayHelper.getInstance(ac).exit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 支付回调结果
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	public void payCallResult(Activity ac,int requestCode, int resultCode, Intent data){
		EPPayHelper.getInstance(ac).onActivityResult(requestCode, resultCode, data);
	}
	

}
