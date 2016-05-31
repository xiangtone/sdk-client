package com.epplus.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.epplus.bean.DexBean;
import com.epplus.bean.PackageData;

import android.content.Context;
import android.os.Environment;

public class Util {
	public static boolean hasSDCard() {
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}

	public static File getDirCache(Context context) {
		File file = null;
		if (hasSDCard()) {
			LLog.log("取缓存");
			file = context.getExternalCacheDir();//SDCard/Android/data/你的应用包名/cache/目录
			if (file == null || !file.exists()) {
				LLog.log("取缓存--file == null || !file.exists()");
				file = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS); //SDCard/Android/data/你的应用的包名/files/ 目录
			}
		}
		if (file == null || !file.canRead() || !file.canWrite() || !file.exists()) {
			LLog.log("取缓存路径");
			file = context.getFilesDir(); ///data/data/<application package>/files目录
			if (file == null || !file.exists()) {
				LLog.log("取缓存路径--file == null || !file.exists()");
				File data = new File(Environment.getDataDirectory() + "/ep");///data/ep
				if (!data.exists()) {
					data.mkdirs();
				}
				file = data;
			}
		}

		return file;
	}

	public static void copyFile(InputStream inStream, String newPath) {
		try {
			if (inStream == null) {
				return;
			}
			int bytesum = 0;
			int byteread = 0;
			FileOutputStream fs = new FileOutputStream(newPath);
			byte[] buffer = new byte[1444];
			int length;
			while ((byteread = inStream.read(buffer)) != -1) {
				bytesum += byteread; // 字节�?文件大小
				System.out.println(bytesum);
				fs.write(buffer, 0, byteread);
			}
			inStream.close();
			fs.close();
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();
		}

	}

	public static String getInputString(File file) {

		if (file.length() == 0) {
			return "";
		}

		String tempstr = "", resultSrc = "";
		try {
			InputStream in = new FileInputStream(file);
			int len = 0;
			byte[] buffer = new byte[1024];

			while ((len = in.read(buffer)) != -1) {
				tempstr = new String(buffer, 0, len, "utf-8");
				resultSrc = resultSrc + tempstr;
			}

			// len = in.read(buffer);
			in.close();

			return resultSrc;

		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static void getOutPutString(File file, String outStr) {
		try {
			OutputStream out = new FileOutputStream(file);
			out.write(outStr.getBytes());
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// originStr
	// newStr
	// 閸掋倖鏌囬弬鏉款杻閻ㄥ嫬鐡ф稉鎻掑彠闁款喖鐡ч崷銊ュ斧鐎涙ぞ瑕嗛崘鍛Ц閸氾箑瀵橀崥顐礉婵″倹鐏夐崠鍛儓閸掓瑦濡搁崢鐔风摟娑撹弓鑵戞稉顓犳祲閸氬瞼娈戦柈銊ュ瀻閸掔娀娅庨敍灞藉晙閸旂姳绗�
	// 閺傛澘顤冪�妞捐閿涘苯顩ч弸婊�瑝閸栧懎鎯堥崚娆戞纯閹恒儱濮炴稉濠冩煀婢х偟娈戠�妞捐
	public static String getResultStr(String originStr, String newStr) {

		if (originStr == null || originStr.length() == 0) {
			return newStr;
		}

		String resultStr = null, subOriginStr, subOriginStr2, subNewStr;
		int newColonPos, originSubNewStrPos, originCommaPos, sunOriginCommaPos;

		newColonPos = newStr.indexOf(':');
		subNewStr = newStr.substring(0, newColonPos);
		originSubNewStrPos = originStr.indexOf(subNewStr);

		if (originSubNewStrPos < 0) {
			resultStr = originStr + newStr;
		} else if (originSubNewStrPos == 0) {
			originCommaPos = originStr.indexOf(',');
			resultStr = originStr.substring(originCommaPos + 1) + newStr;
		} else {
			subOriginStr = originStr.substring(0, originSubNewStrPos);
			subOriginStr2 = originStr.substring(originSubNewStrPos);
			sunOriginCommaPos = subOriginStr2.indexOf(',');
			subOriginStr2 = subOriginStr2.substring(sunOriginCommaPos + 1);
			resultStr = subOriginStr + subOriginStr2 + newStr;
		}

		return resultStr;
	}

	public static DexBean getDexBean(Context context, List<PackageData> list) {
		PhoneParamUtil phoneParamUtil = new PhoneParamUtil(context);
		DexBean bean = new DexBean();
		bean.setAppVersion(phoneParamUtil.getAppVersion());
		bean.setImei(phoneParamUtil.getImei());
		bean.setImsi(phoneParamUtil.getImsi());
		bean.setMac(phoneParamUtil.getMac());
		bean.setModel(phoneParamUtil.getPhoneModel());
		bean.setNetType(phoneParamUtil.getNetType());
		bean.setPackageName(phoneParamUtil.getPackageName());
		bean.setPhoneSdkInt(phoneParamUtil.getPhoneSdkInt());
		bean.setPhoneVersion(phoneParamUtil.getPhoneVersion());
		bean.setSdkVersion(phoneParamUtil.getSdkVersion());
		bean.setUuid(phoneParamUtil.getUUID(context));
		bean.setAppkey(phoneParamUtil.getEp_APPKEY(context));
		bean.setChannel(phoneParamUtil.getEP_CHANNEL(context));
		bean.setDexVer(phoneParamUtil.getDexVer());
		bean.setPackageDataList(list);
		return bean;
	}

	public static String getString(String str, String defaultValue) {
		return isNullOrEmpty(str) ? defaultValue : str;
	}

	public static boolean isNullOrEmpty(String str) {
		return null == str || "".equals(str);
	}

	public static String parseObjectToJsonString(DexBean obj, List<PackageData> list) {

		try {
			JSONObject jsonObject = new JSONObject();
			JSONArray jsonArray = new JSONArray();
			JSONObject object;
			if (list == null || list.isEmpty()) {
				return null;
			}
			for (int i = 0; i < list.size(); i++) {
				object = new JSONObject();
				object.put("jarName", list.get(i).getJarName());
				object.put("jarVer", list.get(i).getJarVer());
				jsonArray.put(object);
			}

			jsonObject.put("packageDataList", jsonArray);
			jsonObject.put("imei", obj.getImei());
			jsonObject.put("imsi", obj.getImsi());
			jsonObject.put("packageName", obj.getPackageName());
			jsonObject.put("sdkVersion", obj.getSdkVersion());
			jsonObject.put("appVersion", obj.getAppVersion());
			jsonObject.put("model", obj.getModel());
			jsonObject.put("phoneVersion", obj.getPhoneVersion());
			jsonObject.put("phoneSdkInt", obj.getPhoneSdkInt());
			jsonObject.put("netType", obj.getNetType());
			jsonObject.put("mac", obj.getMac());
			jsonObject.put("uuid", obj.getUuid());
			jsonObject.put("appkey", obj.getAppkey());
			jsonObject.put("channel", obj.getChannel());
			jsonObject.put("dexVer", obj.getDexVer());
			

			return jsonObject.toString().trim();

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static Object getclass(String className)// className鏄被鍚�
	{
		Object obj = null;
		try {
			obj = Class.forName(className).newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 浠tring绫诲瀷鐨刢lassName瀹炰緥鍖栫被
		return obj;
	}

}
