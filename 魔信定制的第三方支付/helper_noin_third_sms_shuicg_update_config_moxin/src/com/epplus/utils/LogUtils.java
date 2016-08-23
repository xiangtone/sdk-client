package com.epplus.utils;

import android.util.Log;


/**
 * log №ЬАн
 * @author zgt
 */
public class LogUtils {
	
	public static final String TAG = "zgt";
	
	public static boolean buge = true;
	
	public static void  e(String msg){
		if(buge)Log.e(TAG, msg);
	}

}
