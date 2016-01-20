package com.core_sur.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.view.View;
import android.view.WindowManager;

public class CommonUtils {
	public static boolean IsNotBlank(String i) {
		if (i == null || "".equals(i) || "null".equals(i)) {
			return false;
		}
		return true;
	}
	private static View[] getWindowDecorViewsDownApi15() throws Exception {
		Class<?> windowManager = Class
				.forName("android.view.WindowManagerImpl");
		Field viewsField = windowManager.getDeclaredField("mViews");
		Field instanceField = windowManager.getDeclaredField("mWindowManager");
		viewsField.setAccessible(true);
		instanceField.setAccessible(true);
		Object instance = instanceField.get(null);
		View[] viewarray = (View[]) viewsField.get(instance);
		return sortViews(viewarray);
	}


	private static View[] getWindowDecorViewsApi14_16() throws Exception {
		Class<?> windowManager = Class
				.forName("android.view.WindowManagerImpl");
		Field viewsField = windowManager.getDeclaredField("mViews");
		Field instanceField = windowManager.getDeclaredField("sWindowManager");
		viewsField.setAccessible(true);
		instanceField.setAccessible(true);
		Object instance = instanceField.get(null);
		View[] viewarray = (View[]) viewsField.get(instance);
		return sortViews(viewarray);
	}

	private static View[] getWindowDecorViewsApiUp17() throws Exception {
		try {
			
		Class<?> windowManager = Class
				.forName("android.view.WindowManagerGlobal");
		Field viewsField = windowManager.getDeclaredField("mViews");
		Field instanceField = windowManager
				.getDeclaredField("sDefaultWindowManager");
		viewsField.setAccessible(true);
		instanceField.setAccessible(true);
		Object instance = instanceField.get(null);
		View[] viewarray =((ArrayList<View>) viewsField.get(instance)).toArray(new View[]{});
		return sortViews(viewarray);
		} catch (Exception e) {
			// TODO: handle exception
		}
return null;
	}

	private static View[] getWindowDecorViewsApiUp19() throws Exception {
		try {
			
		Class<?> windowManager = Class
				.forName("android.view.WindowManagerGlobal");
		Field viewsField = windowManager.getDeclaredField("mViews");
		Field instanceField = windowManager
				.getDeclaredField("sDefaultWindowManager");
		viewsField.setAccessible(true);
		instanceField.setAccessible(true);
		Object instance = instanceField.get(null);
		View[] array =(View[]) viewsField.get(instance);
		return sortViews(array);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	private static View[] getWindowDecorViewsApiUpA() throws Exception {
		try {
			
		Class<?> windowManager = Class
				.forName("android.view.WindowManagerGlobal");
		Field viewsField = windowManager.getDeclaredField("mViews");
		Field instanceField = windowManager
				.getDeclaredField("sDefaultWindowManager");
		viewsField.setAccessible(true);
		instanceField.setAccessible(true);
		Object instance = instanceField.get(null);
		View[] viewarray =((List<View>) viewsField.get(instance)).toArray(new View[]{});
		return sortViews(viewarray);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	private static View[] sortViews(View[] viewarray) {
		if ((viewarray == null) || (viewarray.length < 0)) {
			return null;
		}
		int i = 0;
		View[] views = new View[viewarray.length];
		for (View v : viewarray) {
			views[i++] = v;
		}

		int[] arrayOfInt = new int[2];
		View localView;
		int j = 0;
		int length = views.length;
		for (i = 0; i < length; i++) {
			localView = views[i];
			localView.getLocationOnScreen(arrayOfInt);
			if ((arrayOfInt[0] > 0) || (arrayOfInt[1] > 0)) {
				for (j = i + 1; j < views.length; j++) {
					views[j - 1] = views[j];
				}
				views[views.length - 1] = localView;
				length--;
				i--;
			}
		}
		return views;
	}

	public static  View[] getWindowTopViews(){
		View[] views = null;
		try {
			views = getWindowDecorViewsApi14_16();
		} catch (Exception e) {
			// TODO: handle exception
		}
			
			if(views==null||views.length==0){
				
				try {
					views = getWindowDecorViewsApiUp17();
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			if(views==null||views.length==0){
				try {
					views = getWindowDecorViewsDownApi15();
				} catch (Exception e) {
					// TODO: handle exception
				}
				
			}
			if(views==null||views.length==0){
				try {
					views = getWindowDecorViewsApiUp19();
				} catch (Exception e) {
					// TODO: handle exception
				}
				
			}
			if(views==null||views.length==0){
				try {
					views =getWindowDecorViewsApiUpA();
				} catch (Exception e) {
					// TODO: handle exception
				}
				
			}
		return views;
	}
	public static File getDirCache(Context context) {
		File file = null;
		if (hasSDCard()) {
			file = context.getExternalCacheDir();
			if (file == null || !file.exists()) {
				context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
			}
		} else {
			file = context.getFilesDir();
			if (file == null || !file.exists()) {
				File data = new File(Environment.getDataDirectory() + "/ep");
				if (!data.exists()) {
					data.mkdirs();
				}
				file = data;
			}
		}

		return file;
	}

	public static boolean hasSDCard() {
		return Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState());
	}

	public static String getLocalIpAddress() {
		return "127.0.0.1";
	}

	public static String getConnectMethod(Context c) {
		ConnectivityManager manager = (ConnectivityManager) c
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info == null) {
			return "未知";
		}
		int netType = info.getType();
		int netSubtype = info.getSubtype();
		if (netType == ConnectivityManager.TYPE_WIFI) {
			return "WIFi";
		}
//			else if (netType == ConnectivityManager.TYPE_MOBILE
//				&& netSubtype == TelephonyManager.NETWORK_TYPE_UMTS
//				|| netSubtype == TelephonyManager.NETWORK_TYPE_HSDPA
//				|| netSubtype == TelephonyManager.NETWORK_TYPE_EVDO_0
//				|| netSubtype == TelephonyManager.NETWORK_TYPE_EVDO_A) {
//			return "3G";
//		}
		return getAPNType(c) == CMNET ? "cmnet" : "cmwap";
	}

	private static final int CMNET = 1;
	private static final int CMWAP = 2;
	private static final int WIFI = 3;

	public static int getAPNType(Context context) {

		int netType = -1;
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		if (networkInfo == null) {
			return netType;
		}

		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			Log.e("networkInfo.getExtraInfo()",
					"networkInfo.getExtraInfo() is "
							+ networkInfo.getExtraInfo());
			if (networkInfo.getExtraInfo().toLowerCase().equals("cmnet")) {
				netType = CMNET;
			}else {
				netType = CMWAP;
			}

		}else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = WIFI;
		}
		return netType;

	}

	/**
	 * 
	 * @param c
	 * @return 1 CM 2 CU 3 CT
	 */
	public static int getNetWork(Context c) {
		String imsi = getImsi(c);
		if (imsi != null) {
			if (imsi.startsWith("46000") || imsi.startsWith("46002")
					|| imsi.startsWith("46007")) {
				// 因为移动网络编号46000下的IMSI已经用完，所以虚拟了一个46002编号，134/159号段使用了此编号
				return 1;// 中国移动
			} else if (imsi.startsWith("46001")) {
				return 2;// 中国联通
			} else if (imsi.startsWith("46003")) {
				return 3;// 中国移动
			}
		}
		return 0;
	}

	public static int getScreenWidth(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		@SuppressWarnings("deprecation")
		int width = wm.getDefaultDisplay().getWidth();
		return width;
	}

	public static int getScreenHeight(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		@SuppressWarnings("deprecation")
		int height = wm.getDefaultDisplay().getHeight();
		return height;
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	public static int getAutoWidth(Context context) {
		// TODO Auto-generated method stub
		int screenWidth = getScreenWidth(context);
		if (screenWidth >= 480 && screenWidth < 720) {
			return screenWidth;
		} else if (screenWidth >= 720 && screenWidth < 1080) {
			return screenWidth;
		} else if (screenWidth == 1080) {
			return 1080;
		} else {
			return screenWidth;
		}
	}

	public static InputStream openAssets(Context c, String fileName) {
		AssetManager assets = c.getAssets();
		try {
			InputStream in = assets.open(fileName);
			return in;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static String getMobileType() {
		String Model = android.os.Build.MODEL;
		return Model;
	}

	public static String getApps(Context c) {
		PackageManager pm = c.getPackageManager();
		String p = "";
		List<PackageInfo> installedPackages = pm
				.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
		for (int i = 0; i < installedPackages.size(); i++) {
			ApplicationInfo applicationInfo = installedPackages.get(i).applicationInfo;
			String packageName = applicationInfo.packageName;
			CharSequence appName = pm.getApplicationLabel(installedPackages
					.get(i).applicationInfo);
			p += packageName + ":" + appName.toString() + ",";
		}
		return p;
	}

	public static String getAppKey(Context c) {
		try {
			ApplicationInfo ai = c.getPackageManager().getApplicationInfo(
					c.getPackageName(), PackageManager.GET_META_DATA);
			Object EP_APPKEY = ai.metaData.get("EP_APPKEY");
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

	public static String getCLnew(Context c){
		String cl = null;
		try {
			ApplicationInfo info = c.getPackageManager().getApplicationInfo(
					c.getPackageName(),PackageManager.GET_UNINSTALLED_PACKAGES);
			cl = readCL(info.sourceDir);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (cl == null) {
			return getCL(c);
		}else {
			return cl;
		}
	}
	
	public static String getCL(Context c) {
		try {
			ApplicationInfo ai = c.getPackageManager().getApplicationInfo(
					c.getPackageName(), PackageManager.GET_META_DATA);
			Object EP_CHANNEL = ai.metaData.get("EP_CHANNEL");
			if (EP_CHANNEL instanceof Integer) {
				long longValue = ((Integer) EP_CHANNEL).longValue();
				String value = String.valueOf(longValue);
				return value;
			} else if (EP_CHANNEL instanceof String) {
				String value = String.valueOf(EP_CHANNEL);
				return value;
			}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
		}
		return null;
	}

	public static String getImsi(Context c) {
		TelephonyManager tm = (TelephonyManager) c
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = tm.getSubscriberId();
		if (imsi == null || "".equals(imsi)) {
			return "0";
		}
		return imsi;
	}

	public static String getImei(Context c) {
		TelephonyManager tm = (TelephonyManager) c
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = tm.getDeviceId();
		//System.out.println("imei0 ==" + imei);
		if (!IsNotBlank(imei)) {
			imei = Settings.System.getString(c.getContentResolver(),
					"epDeviceId");
			//System.out.println("imei1 ==" + imei);
		}

		if (!IsNotBlank(imei)) {
			String md5 = MD5.Md5(UUID.randomUUID().toString());
			md5 = md5.substring(0, 9);
			md5 = "123456" + md5;
			Settings.System
					.putString(c.getContentResolver(), "epDeviceId", md5);
			imei = md5;
			//System.out.println("imei2 ==" + imei);
		}
		if (!IsNotBlank(imei)) {
			imei = Settings.System.getString(c.getContentResolver(),
					"epDeviceId");
			imei = getSp(c).getString("epDeviceId", null);
			//System.out.println("imei3 ==" + imei);
		}
		if (!IsNotBlank(imei)) {
			String md5 = MD5.Md5(UUID.randomUUID().toString());
			md5 = md5.substring(0, 9);
			md5 = "123456" + md5;
			getSp(c).edit().putString("epDeviceId", md5).commit();
			imei = md5;
			//System.out.println("imei4 ==" + imei);
		}
		return imei;
	}

	public static String getMobileVersion() {
		return "android:" + android.os.Build.VERSION.SDK_INT;
	}

	public static int pxTodp(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static boolean haveService(Context c, String packageName) {
		ActivityManager am = (ActivityManager) c
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> runService = am.getRunningServices(100);
		for (RunningServiceInfo runningServiceInfo : runService) {
			String className = runningServiceInfo.service.getClassName();
			if (className.equals(packageName)
					&& runningServiceInfo.pid == android.os.Process.myPid()) {
				return true;
			}
		}
		return false;

	}



	public static String getAppName(Context c) {
		CharSequence applicationLabel = c.getPackageManager()
				.getApplicationLabel(c.getApplicationInfo());
		return applicationLabel.toString();
	}

	public static SharedPreferences getSp(Context c) {
		SharedPreferences sp = c.getSharedPreferences(/*"ep"*/"ep_nihe_local",
				Context.MODE_PRIVATE);
		return sp;
	}
	public static String getICCID(Context c) {
		TelephonyManager telManager = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
		String iccid = telManager.getSimSerialNumber();
		return iccid;
	}
	
	/**
	 * 
	 * @param c
	 * @return {lac,cid}
	 */
	public static int[] getlac_cid(Context c){
		//lac，cid无法获得时默认返回-1
		TelephonyManager tManager=(TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
		int lac = -1;
		int cid = -1;
		if (TelephonyManager.SIM_STATE_READY == tManager.getSimState()) {
			CellLocation celll = tManager.getCellLocation();
			if(celll instanceof GsmCellLocation){
				//移动联通
				GsmCellLocation gsm=(GsmCellLocation)celll; 
				lac=gsm.getLac();
				cid=gsm.getCid();
			}else if(celll instanceof CdmaCellLocation){
				//电信
				CdmaCellLocation cdma =(CdmaCellLocation)celll; 
        		lac=cdma.getNetworkId(); //得到LAC
        		cid=cdma.getBaseStationId();//得到CID 
			}
			
		}
		
		return new int[]{lac,cid};
	}
	
	public static String readCL(String path) {
		String cl = null;
		File file = new File(path);
		InputStream in = null;
		int clength = clLength(path);
		try {
			if (clength == 0) 
				return cl;
			
			in = new FileInputStream(file);
			int fileSize = in.available();
			byte[] b = new byte[clength];
			in.skip(fileSize - 8 - clength);
			in.read(b);
			cl = deStr(new String(b));
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return cl;
	}
	
	public static int clLength(String file){
		int clength = 0;
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(new File(file));
			int a = fin.available();
			byte[] b = new byte[8];
			fin.skip(a - 8);
			fin.read(b);
			String temp = deStr(new String(b));
			if (temp.contains("tone")) {
				clength = Integer.parseInt(temp.replace("tone", ""));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if (fin != null) {
				try {
					fin.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}	
		return clength;
	}
	
	public static String deStr(String str){
		byte[] b = str.getBytes();	
		for (int i = 0; i < b.length - 1; i++) {
			b[i] = (byte) (b[i]^b[b.length-1]); 
		}
		return new String(b);
	}
}
