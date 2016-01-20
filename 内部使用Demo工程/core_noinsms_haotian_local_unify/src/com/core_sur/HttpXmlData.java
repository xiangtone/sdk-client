package com.core_sur;

import org.json.JSONObject;

import android.content.Context;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import com.core_sur.bean.RegSend;
import com.core_sur.tools.AES;
import com.core_sur.tools.CommonUtils;
import com.core_sur.tools.Log;

public class HttpXmlData {

	public static String IMEI;// IMEI
	public static String IMSI;// IMSI
	public static String NO;// 手机号
	public static String ID;// 推广id
	public static String SMSC;// 短信中心

	public static String getJson(Context context, String url, int gwclienttype,
			String UserTag) {
		try {
			if (Config.IsDebug) {
				Log.i("getJson", url);
			}
			// 组织第一次登陆数据
			RegSend regSend = new RegSend();
			regSend.setCmdid("1019");
			String androidid = Secure.getString(context.getContentResolver(),
					Secure.ANDROID_ID);

			if (androidid == null) {
				androidid = ((TelephonyManager) context
						.getSystemService(Context.TELEPHONY_SERVICE))
						.getDeviceId();
			}

			regSend.setAndroidid(androidid);
			regSend.setImsi(getIMSI(context));
			regSend.setImei(getIMEI(context));
			regSend.setGwclienttype(gwclienttype);
			regSend.setUserTag(UserTag);
			regSend.setSysModel(getModel());
			regSend.setArgs(Config.Args);

			DisplayMetrics dm = new DisplayMetrics();
			dm = context.getResources().getDisplayMetrics();
			if (Config.IsDebug) {
				Log.i("getJson", "DisplayMetrics dm");
			}

			int screenWidth = dm.widthPixels; // 屏幕宽（像素，如：480px）
			int screenHeight = dm.heightPixels; // 屏幕高（像素，如：800px）
			{
				if (Config.IsDebug) {
					Log.i("getJson", "screenWidth = " + screenWidth);
					Log.i("getJson", "screenHeight = " + screenHeight);
				}
			}
			regSend.setScreenheigth(screenHeight);
			regSend.setScreenwidth(screenWidth);

			regSend.setSysVersion(getOS());

			JSONObject json = new JSONObject();
			json.put("CMDId", regSend.getCmdid());
			json.put("AndroidId", regSend.getAndroidid());
			json.put("IMSI", regSend.getImsi());
			json.put("GWClientType", regSend.getGwclienttype());
			json.put("UserTag", regSend.getUserTag());
			json.put("SysModel", regSend.getSysModel());
			json.put("Screenheigth", regSend.getScreenheigth());
			json.put("Screenwidth", regSend.getScreenwidth());
			json.put("SysVersion", regSend.getSysVersion());
			json.put("imei", regSend.getImei());
			json.put("args", regSend.getArgs());

			// json.put("CMDId", regSend.getCmdid());
			// json.put("CMDId", regSend.getCmdid());
			// String postData = gson.toJson(regSend);
			String postData = json.toString();

			if (Config.IsDebug) {
				Log.i("getJson", "postData = " + postData);
			}

			// 加密后访问网络，用初始的秘钥1054
			// String urlPart = "unid=" + Config.UnionId + "&key=" +
			// AES.EncodeString(postData, Config.AesKey);
			String urlPart = "appkey=" + Config.AppKey + "&key="
					+ AES.EncodeString(postData, Config.AppKey);

			if (url.contains("?")) {
				url += urlPart;
			} else {
				url += "?" + urlPart;
			}

			String tempData = HttpCommon.getHtmlContents(url, "", false).HtmlContents
					.trim();

			if (Config.IsDebug) {
				Log.i("getJson", "tempData = " + tempData);
			}

			if (tempData == null) {
				return "";
			}

			// 用初始的秘钥解密
			String Aesdata = AES.DecodeString(tempData, Config.AppKey);
			if (Config.IsDebug) {
				Log.i("getJson", Aesdata);
			}
			return Aesdata;
		} catch (Exception e) {
			if (Config.IsDebug) {
				Log.d("getJson", e.getMessage());
			}
		}

		return "";
	}

	/**
	 * 获取第一次通道
	 * 
	 * @return
	 */
	// public static String HttpPost(Context context, String url, String
	// postData)
	// {
	//
	// String requesturl = url;
	//
	// if (Config.IsDebug)
	// {
	// Log.i("postHttpJson", requesturl);
	// Log.i("postHttpJson", postData);
	// }
	// String xmlStr = null;
	//
	// try
	// {
	//
	// Http http = new Http(context, requesturl, postData.getBytes(),
	// " text/plain");
	//
	// http.setConnectTimeout(50 * 1000);
	// http.setReadTimeout(30 * 1000);
	// http.setRequestMethod(true);
	// http.setSaveOutData(true);
	//
	// // if (isFourth()) {
	// // http.setNetType(Http.NET_TYPE_UNASSIGN);
	// // } else {
	// // http.setNetType(Http.NET_TYPE_WAP);
	// // }
	//
	// http.connect();
	// if (Config.IsDebug)
	// {
	// Log.i("postHttpJson", "1111");
	// }
	// http.read();
	// if (Config.IsDebug)
	// {
	// Log.i("postHttpJson", "len = " + http.getInData().length);
	// }
	// xmlStr = new String(http.getInData(), "UTF-8");
	// if (Config.IsDebug)
	// {
	// Log.i("postHttpJson", xmlStr);
	// }
	// return xmlStr;
	//
	// } catch (Exception e)
	// {
	// if (Config.IsDebug)
	// {
	// Log.d("postHttpJson", e.getMessage());
	// }
	// }
	//
	// return "";
	// }

	/**
	 * 取系统版本
	 * 
	 * @return
	 */
	public static int getOS() {
		int temp = Build.VERSION.SDK_INT;

		return temp;
	}

	/**
	 * 判断手机版本是不是4.0
	 * 
	 * @return
	 */
	public static boolean isFourth() {
		if (Build.VERSION.SDK_INT >= 14) {
			return true;
		}
		return false;
	}

	/**
	 * 取手机型号
	 * 
	 * @return
	 */
	public static String getModel() {
		return android.os.Build.MODEL;
	}

	/**
	 * 获取IMSI
	 * 
	 * @param context
	 * @return
	 */
	public static String getIMSI(Context context) {
		return CommonUtils.getImsi(context);
	}

	/**
	 * 获取IMEI
	 * 
	 * @param context
	 * @return
	 */
	public static String getIMEI(Context context) {
		return CommonUtils.getImei(context);
	}

}
