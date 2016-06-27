package com.Demo.Demo;

import java.io.IOException;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Environment;
import android.util.Log;

public class ConfigurationTools {
	private static final String tag = "[Tools]";
	
	private static final String META_DATA_AppId      = "ZMAppId";
	private static final String META_DATA_MerchantId = "ZMMerchantId";
	
	public static boolean isPortrait(Context context) {
		Configuration config = context.getResources().getConfiguration();
		if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			return false;
		}
		return true;
	}
	

	
	public static String getAppId(Context context) {
		try {
			ApplicationInfo appInfo = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);
			String appidStr = appInfo.metaData.get(META_DATA_AppId).toString();
			int appid = Integer.parseInt(appidStr);
			Log.i(tag, "getAppId = " + appid);
			return appid + "";
		} catch (Exception e) {
			Log.w(tag, "getAppId error", e);
		}
		return null;
	}
	
	
	
	public static String getMerchantId(Context context) {
		try {
			ApplicationInfo appInfo = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);
			String appidStr = appInfo.metaData.get(META_DATA_MerchantId).toString();
			int appid = Integer.parseInt(appidStr);
			Log.i(tag, "getMerchantId = " + appid);
			return appid + "";
		} catch (Exception e) {
			Log.w(tag, "getMerchantId error", e);
		}
		return null;
	}	
}
