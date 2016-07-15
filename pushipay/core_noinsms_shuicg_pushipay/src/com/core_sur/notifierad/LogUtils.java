package com.core_sur.notifierad;

import android.util.Log;

/**
 * log 管理
 * @author Administrator
 *
 */
public class LogUtils {
	
	private static final String tag = "zgt";
	
	private static final boolean showLog = false;
	
	public static void e(String msg){
		if(showLog)Log.e(tag, msg);
	}

}
