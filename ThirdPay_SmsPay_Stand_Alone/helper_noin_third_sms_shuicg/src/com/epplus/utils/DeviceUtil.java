package com.epplus.utils;

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

/**
 * Éè±¸×°ÖÃ
 * 
 * @author Administrator
 * 
 */
public class DeviceUtil {

	private Context _context;

	TelephonyManager tm = null;

	public DeviceUtil(Context context) {
		this._context = context;
		tm = (TelephonyManager) _context
				.getSystemService(Context.TELEPHONY_SERVICE);
	}

	public String getImsi() {
		return StringUtil.getString(tm.getSubscriberId(), "");
	}

	public String getImei() {
		return StringUtil.getString(tm.getDeviceId(), "");
	}

	public String getSdkVersion() {
		return StringUtil.getString(android.os.Build.VERSION.RELEASE, "");
	}

	public String getAppVersion() {
		String packageName = _context.getPackageName();

		try {
			return _context.getPackageManager().getPackageInfo(packageName, 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return "";
	}

	public String getPackageName() {
		return _context.getPackageName();
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
		ConnectivityManager manager = (ConnectivityManager) _context
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
		WifiManager wifi = (WifiManager) _context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}

	public String getAppMetaValue(String key) {
		ApplicationInfo appInfo;
		try {
			appInfo = _context.getPackageManager().getApplicationInfo(
					getPackageName(), PackageManager.GET_META_DATA);
			return appInfo.metaData.getString(key);
		} catch (Exception ex) {
			System.out.println("no qi dao app key");
		}
		return "";
	}

}
