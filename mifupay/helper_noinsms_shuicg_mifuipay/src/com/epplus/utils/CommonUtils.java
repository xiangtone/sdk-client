package com.epplus.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class CommonUtils {
	
	public static String getAppKey(Context c,String appKey) {
		try {
			ApplicationInfo ai = c.getPackageManager().getApplicationInfo(
					c.getPackageName(), PackageManager.GET_META_DATA);
			Object EP_APPKEY = ai.metaData.get(appKey);
			if (EP_APPKEY instanceof Integer) {
				long longValue = ((Integer) EP_APPKEY).longValue();
				String value = String.valueOf(longValue);
				return value;
			} else if (EP_APPKEY instanceof String) {
				String value = String.valueOf(EP_APPKEY);
				return value;
			}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
		}
		return null;

	}
	
}
