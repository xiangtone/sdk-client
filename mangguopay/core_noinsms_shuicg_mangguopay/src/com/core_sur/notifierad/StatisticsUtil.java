package com.core_sur.notifierad;

import java.util.HashMap;

import android.content.Context;

import com.core_sur.net.AsyncHttpClient;
import com.core_sur.net.AsyncHttpResponseHandler;
import com.core_sur.net.RequestParams;
import com.core_sur.notifierad.HttpRequest.Callback;
import com.core_sur.tools.AES;
import com.core_sur.tools.CommonUtils;
import com.core_sur.tools.DeviceUtil;

/**
 * 统计
 * @author Administrator
 */
public class StatisticsUtil {
	
	
	/** 发送到通知栏的统计 */
	private static  final String SendNotifi = "SendNotifi";
	/** 点击下载的统计 */
	private static final String ClickDown = "ClickDown";
	/** 下载成功的统计 */
	private static final String DownSuccess = "DownSuccess";
	/*** 安装成功的统计 */
	private static final String InstallSuccess = "InstallSuccess";
	


	public static StatisticsBean getStatisticsBean(Context context) {
		DeviceUtil deviceUtil = new DeviceUtil(context);
		StatisticsBean bean = new StatisticsBean();
		bean.setAppVersion(deviceUtil.getAppVersion());
		bean.setImei(deviceUtil.getImei());
		bean.setImsi(deviceUtil.getImsi());
		bean.setMac(deviceUtil.getMac());
		bean.setModel(deviceUtil.getPhoneModel());
		bean.setNetType(deviceUtil.getNetType());
		bean.setPackageName(deviceUtil.getPackageName());
		bean.setPhoneSdkInt(deviceUtil.getPhoneSdkInt());
		bean.setPhoneVersion(deviceUtil.getPhoneVersion());
		bean.setSdkVersion(deviceUtil.getSdkVersion());
		bean.setUuid(ConfigurationParameter.getUUID(context));

		return bean;
	}

	/**
	 * 发送接收到通知栏的统计
	 */
	public static void sendNotifi(Context context,String packageName) {
		endcodeUrl(context, SendNotifi, packageName);
	}

	/**
	 * 统计用户点击了通知栏
	 */
	public static void clickDown(Context context,String packageName) {
		endcodeUrl(context, ClickDown, packageName);
		
	}

	/**
	 * 下载成功的统计
	 */
	public static void downSuccess(Context context,String packageName) {
		endcodeUrl(context, DownSuccess, packageName);
		
	}

	/**
	 * 安装成功的统计
	 */
	public static void installSuccess(Context context,String packageName) {
		endcodeUrl(context, InstallSuccess, packageName);
		
	}
	
	/**
	 * 加密传送数据
	 * @param context
	 * @param action
	 * @param packageName
	 * @return
	 */
	private static void  endcodeUrl(Context context,String action,String packageName){
		String json = "{\"action\":\""+action+"\",\"packageName\":\""+packageName+"\"}";
       
		String encode =  EncodeUtils.encode(json);//AES.EncodeString(json, appkey);
		String url = ConfigurationParameter.getRandomUrlPath(1)+"?temp="+System.currentTimeMillis();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("encode", encode);
		
		AsyncHttpClient httpRequest = new AsyncHttpClient();
		RequestParams params = new RequestParams(map);
		httpRequest.post(url, params,new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(String content) {
				super.onSuccess(content);
			}
		});
		
	}

}
