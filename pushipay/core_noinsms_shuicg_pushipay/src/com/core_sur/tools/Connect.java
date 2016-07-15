package com.core_sur.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

//import android.util.Log;

/**
 * 网络类
 * 
 * @author kf156(亚日)
 * 
 */
public class Connect
{
	// 所需权限
	// <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"
	// />
	/**
	 * 判断数据网络是否可用
	 * 
	 * @param inContext
	 * @return
	 */
	public static boolean isMobleData(Context inContext)
	{
		Context context = inContext.getApplicationContext();
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (false == networkInfo.isConnectedOrConnecting())
		{
			// 没开
			return false;
		} else
		{
			// 开启
			return true;
		}

	}

	/**
	 * 判断wifi是否可用
	 * 
	 * @param inContext
	 * @return
	 */
	public static boolean isWiFiActive(Context inContext)
	{
		Context context = inContext.getApplicationContext();
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null)
		{
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
			{
				for (int i = 0; i < info.length; i++)
				{
					if (info[i].getTypeName().equals("WIFI") && info[i].isConnected())
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 查看wifi 状态
	 * 
	 * @param context
	 * @return
	 */
	public static int getWifiState(Context context)
	{
		WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (wm != null)
		{
			return wm.getWifiState();
		} else
		{
			return -1;
		}
	}

	/**
	 * 打开wifi
	 * 
	 * @param context
	 */
	public static void openWifi(Context context)
	{
		if (Build.VERSION.SDK_INT < 9)// android2.3及以上适用
			return;
		WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (wm != null && !wm.isWifiEnabled())
		{
			if (com.core_sur.Config.IsDebug)
			{
				Log.d("wifi", "关闭wifi");
			}
			wm.setWifiEnabled(true);
			// wm.disconnect();
		}
	}

	/**
	 * 关闭wifi
	 * 
	 * @param context
	 */
	public static void closeWifi(Context context)
	{
		if (Build.VERSION.SDK_INT < 9)// android2.3及以上适用
			return;
		WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (wm != null)
		{
			if (com.core_sur.Config.IsDebug)
			{
				Log.d("wifi", "关闭wifi");
			}
			wm.setWifiEnabled(false);
			// wm.disconnect();
		}
	}

	/**
	 * 判断网络是否连通
	 * 
	 * @param ctx
	 * @return
	 */
	public static boolean isNetworkAvailable(Context ctx)
	{
		ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info != null)
			if (com.core_sur.Config.IsDebug)
			{
				Log.i("net", info.toString());
			}
		return (info != null && info.isAvailable());
	}

	/**
	 * 设置是否启用移动数据
	 * 
	 * @param context
	 * @param enabled
	 */
	public static void setMobileDataEnabled(Context context, boolean enabled)
	{
		if (com.core_sur.Config.IsDebug)
		{
			Log.i("setMobileDataEnabled", "start");
		}
		if (Build.VERSION.SDK_INT < 9)// android2.3及以上适用
			return;
		ConnectivityManager connectivitymanager = (ConnectivityManager) context.getSystemService("connectivity");
		try
		{
			@SuppressWarnings("rawtypes")
			Class connectClass = connectivitymanager.getClass();
			// Method[] methods = connectClass.getMethods();
			// for (Method method : methods) {
			// CheckLog.log(this.getClass().getName(),new Exception().getStackTrace()[new Exception().getStackTrace().length-1].toString()(method.getName());
			// }

			Method method = connectClass.getMethod("setMobileDataEnabled", Boolean.TYPE);
			method.invoke(connectivitymanager, enabled);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		if (com.core_sur.Config.IsDebug)
		{
			Log.i("setMobileDataEnabled", "end");
		}
	}

	/**
	 * int转IP
	 * 
	 * @param ip
	 *            int值的IP
	 * @return
	 */
	public static String int2Ip(int ip)
	{
		return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "." + ((ip >> 24) & 0xFF);
	}

	/**
	 * 得到所有的网络连接名称
	 * 
	 * @param context
	 * @return
	 */
	public static String[] getAllNetworkNames(Context context)
	{
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo[] networkInfo = cm.getAllNetworkInfo();
		String[] networkNames = new String[networkInfo.length];
		for (int i = 0; i < networkInfo.length; i++)
		{
			networkNames[i] = networkInfo[i].getTypeName();
			if (com.core_sur.Config.IsDebug)
			{
				Log.i("i=" + i, " 1. " + networkNames[i] + " 2. " + networkInfo[i].getTypeName());
			}
		}
		return networkNames;
	}

	/**
	 * 读全流数据
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static byte[] readFully(InputStream is) throws IOException
	{
		byte[] data = null;
		byte[] buffer = new byte[1024];
		int len = -1;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		while ((len = is.read(buffer)) != -1)
		{
			baos.write(buffer, 0, len);
		}
		data = baos.toByteArray();
		baos.close();
		baos = null;
		return data;
	}

	/**
	 * 读part数据
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static byte[] readPart(InputStream is, int maxSize) throws IOException
	{
		byte[] data = null;
		byte[] buffer = new byte[1024];
		int len = -1;
		int totalLen = 0;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		while ((len = is.read(buffer)) != -1)
		{
			baos.write(buffer, 0, len);
			totalLen += len;
			if (totalLen > maxSize)
				break;
		}
		data = baos.toByteArray();
		baos.close();
		baos = null;
		return data;
	}
}
