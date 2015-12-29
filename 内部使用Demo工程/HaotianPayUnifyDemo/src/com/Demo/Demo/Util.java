package com.Demo.Demo;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class Util {
	

	public static boolean isServiceRunning(Context context) {
		//ACTIVITY_SERVICE		
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if ("com.epplus.face.EPPlusPayService".equals(service.service.getClassName())) {
				return true;
			}
		}
			return false;
		}

}
