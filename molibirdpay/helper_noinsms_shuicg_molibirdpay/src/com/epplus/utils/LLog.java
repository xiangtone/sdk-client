package com.epplus.utils;

import android.util.Log;

public class LLog {
	
	final public static boolean isDebug = false; 
	
	public static void log(String log) {
		if (isDebug) 
			Log.d("ep", log);
	}

	public static void info(String log) {
		if (isDebug)
			Log.i("ep", log);
	}
	
	public static void error(String log) {
		if (isDebug)
			Log.e("ep", log);
	}
}
