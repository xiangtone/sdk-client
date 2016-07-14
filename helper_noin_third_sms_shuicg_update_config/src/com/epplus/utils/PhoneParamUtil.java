package com.epplus.utils;

import java.util.UUID;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class PhoneParamUtil {
	private Context context;

	TelephonyManager tm = null;
	
	public static final String UUIDKey = "dex_uuid_key";
	public static final String DEXVER = "1.1"; //只有1.0版本不需要下载jar包

	public PhoneParamUtil(Context context) {
		this.context = context;
		tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
	}

	public String getImsi() {
		return Util.getString(tm.getSubscriberId(), "");
	}

	public String getImei() {
		return Util.getString(tm.getDeviceId(), "");
	}

	public String getSdkVersion() {
		return Util.getString(android.os.Build.VERSION.RELEASE, "");
	}

	public String getAppVersion() {
		String packageName = context.getPackageName();

		try {
			return context.getPackageManager().getPackageInfo(packageName, 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return "";
	}

	public String getPackageName() {
		return context.getPackageName();
	}

	public String getPhoneModel() {
		return android.os.Build.MODEL;
	}

	public String getPhoneVersion() {
		return Build.VERSION.RELEASE;
	}

	public String getPhoneSdkInt() {
		return "android:" + android.os.Build.VERSION.SDK_INT;
	}

	public String getNetType() {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info == null) {
			return "UNKNOW";
		}
		int netType = info.getType();
		if (netType == ConnectivityManager.TYPE_WIFI) {
			return "WIFi";
		}

		if (netType == ConnectivityManager.TYPE_MOBILE) {
			if (info.getExtraInfo().equalsIgnoreCase("cmnet")) {
				return "CMNET";
			} else {
				return "CMWAP";
			}

		}
		return "UNKNOW2";
	}

	public String getMac() {
		WifiManager wifi = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}

	/**
	 * EP_CHANNEL
	 * @param context
	 * @return
	 */
	public String getEP_CHANNEL(Context context){
		return getAppMetaData(context, "EP_CHANNEL");
	}
	
	/**
	 * EP_APPKEY
	 * @param context
	 * @return
	 */
	public String getEp_APPKEY(Context context){
		return getAppMetaData(context, "EP_APPKEY");
	}


 /**
     * 获取application中指定的meta-data
     * @return 如果没有获取成功(没有对应值，或者异常)，则返回值为空
     */
    public String getAppMetaData(Context ctx, String key) {
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
	
	 /**
     * 获取uuid
     * @param context
     * @return
     */
    public  String getUUID(Context context){
    	String uuid=PreferenceUtils.getString(context, UUIDKey, null);
    	if(TextUtils.isEmpty(uuid)){
    		uuid = UUID.randomUUID().toString(); 
    		PreferenceUtils.putString(context, UUIDKey, uuid);
    	}
    	return uuid;
    }
    
    public String getDexVer(){
    	return DEXVER;
    }
}
