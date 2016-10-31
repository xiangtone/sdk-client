package com.thirdpay.utils;

import android.util.Log;

public class LLog {
	
	final public static boolean isDebug = true; 
	
	public static void log(String log) {
		if (isDebug) 
			Log.d("test", log);
	}

	public static void info(String log) {
		if (isDebug)
			Log.i("test", log);
	}
	
	public static void error(String log) {
		if (isDebug)
			Log.e("test", log);
	}
}
