package com.core_sur.tools;

/**
 * tag = mdebug
 * @author Wang
 *
 */
public class mlog {
	
	private static boolean debug = true;
	private static String tag = "xmxtpay";
	
	public static void i(String s1) {
		if (debug){
			if(s1 == null){
				android.util.Log.i(tag, "null");
			}else{
				android.util.Log.i(tag, s1);
			}
		}
	}

	public static void w(String s1) {
		if (debug){
			if(s1 == null){
				android.util.Log.w(tag, "null");
			}else{
				android.util.Log.w(tag, s1);
			}
		}
	}

	public static void v(String s1) {
		if (debug){
			if(s1 == null){
				android.util.Log.v(tag, "null");
			}else{
				android.util.Log.v(tag, s1);
			}
		}
	}

	public static void e(String s1) {
		if (debug){
			if(s1 == null){
				android.util.Log.e(tag, "null");
			}else{
				android.util.Log.e(tag, s1);
			}
		}
	}
	
	public static void d(String s1) {
		if (debug){
			if(s1 == null){
				android.util.Log.d(tag, "null");
			}else{
				android.util.Log.d(tag, s1);
			}
		}
	}

}
