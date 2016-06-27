package com.epplus.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

public class util {
	
	 public static String getAppMetaData(Context ctx, String key) {
	        if (ctx == null || TextUtils.isEmpty(key)) {
	            return null;
	        }
	        String resultData = null;
	        try {
	            PackageManager packageManager = ctx.getPackageManager();
	            if (packageManager != null) {
	                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
	                if (applicationInfo != null) {
	                    if (applicationInfo.metaData != null) {
	                        resultData = applicationInfo.metaData.getString(key);
	                        if(TextUtils.isEmpty(resultData)){
	                        		resultData = applicationInfo.metaData.getInt(key)+"";
	                        }
	                    }
	                }
	            }
	        } catch (PackageManager.NameNotFoundException e) {
	            e.printStackTrace();
	        }
	        return resultData;
	    }
}
