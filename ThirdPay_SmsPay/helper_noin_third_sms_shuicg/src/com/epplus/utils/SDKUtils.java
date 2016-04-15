package com.epplus.utils;

import java.io.UnsupportedEncodingException;

import android.content.Context;
import android.text.TextUtils;

/**
 * sdk 工具
 * @author zgt
 *
 */
public class SDKUtils {
	
	private final static String FLAGID = "flag_id";
	
	/**
	 * 获取用户唯一id
	 * @param context
	 * @return
	 */
	public static String  getFlagId(Context context){
		String flagid = PreferencesUtils.getString(context, FLAGID,null);
		if(!TextUtils.isEmpty(flagid)){
			return flagid;
		}
		String str = String.valueOf(System.currentTimeMillis());
		String defaultValue = null;
		try {
			defaultValue = MD5.getMessageDigest(str.toString().getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		PreferencesUtils.putString(context, FLAGID, defaultValue);
		return defaultValue;
	}
	
	
	
	
	/**
	 * 检测百度是否配置了
	 * @param context
	 * @return
	 */
	public static boolean checkBaiduConfig(){
		String rClazz = "com.baidu.paysdk.lib.R";
		 try {
			Class.forName(rClazz);
			return true;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	
	
	
	
	

}
