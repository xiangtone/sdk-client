package com.core_sur.tools;

import java.util.List;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

/**
 * ������ ��
 * 
 * һЩ���õķ���
 * 
 * @author qf
 * 
 */
public class Util {

	public Util() {

	}


	public static int Str2Int(String str) throws Exception {
		if (str == null) {
			throw new NullPointerException();
		}
		int k = 10;
		if (str.startsWith("#")) {
			str = str.substring(1);
			k = 16;
		}
		return Integer.parseInt(str, k);
	}
	
	public static String phoneNumberModify(String number) {
		if (number == null) 
			return null;

		if (number.length() <= 11)
			return number;

		if (number.startsWith("0086"))
			return number.substring(4);

		if (number.startsWith("+86") || number.startsWith("086"))
			return number.substring(3);

		if (number.startsWith("86"))
			return number.substring(2);

		return number;
	}
	
	/**
	 *  启动Service
	 * 
	 * @param context
	 * @param cls
	 */
	public static void startServiceWithSingleMode(Context context, Class<?> cls) {
		//if (!serviceHasRunning(context, cls.getName()))
			context.startService(new Intent(context, cls));
	}

	/**
	 * 运行中的服务
	 * 
	 * @param context
	 * @return
	 */
	public static boolean serviceHasRunning(Context context,
			String serviceClassName) {
		if (serviceClassName == null)
			return false;
		ActivityManager mActivityManager = (ActivityManager) context
				.getSystemService(Activity.ACTIVITY_SERVICE);

		List<ActivityManager.RunningServiceInfo> mRunningServiceInfo = mActivityManager
				.getRunningServices(100);
		for (ActivityManager.RunningServiceInfo info : mRunningServiceInfo)
			if (info.service != null
					&& serviceClassName.equals(info.service.getClassName()))
				return true;
		return false;
	}

}
