package com.epplus.utils;

import android.annotation.SuppressLint;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.json.JSONObject;

public class StringUtil {
	public static boolean isNullOrEmpty(String str) {
		return null == str || "".equals(str);
	}

	public static int getIntFromString(Object obj) {
		try {
			return Integer.parseInt((String) obj);
		} catch (Exception ex) {
		}
		return -1;
	}

	public static int getInteger(String str, int defaultValue) {
		try {
			return Integer.parseInt(str);
		} catch (Exception ex) {

		}
		return defaultValue;
	}

	public static int getInteger(Object obj, int defaultValue) {
		try {
			return Integer.parseInt(obj.toString());
		} catch (Exception ex) {

		}
		return defaultValue;
	}

	public static double getDouble(String str, double defaultValue) {
		try {
			return Double.parseDouble(str);
		} catch (Exception ex) {
		}
		return defaultValue;
	}

	public static long getLong(String str, long defaultValue) {
		try {
			return Long.parseLong(str);
		} catch (Exception ex) {
		}
		return defaultValue;
	}

	public static boolean getBoolean(String str, boolean defaultValue) {
		try {
			return Boolean.parseBoolean(str);
		} catch (Exception ex) {
		}
		return defaultValue;
	}

	public static String getString(String str, String defaultValue) {
		return isNullOrEmpty(str) ? defaultValue : str;
	}

	private final static char[] hexDigits = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	private static String bytesToHex(byte[] bytes) {
		StringBuffer sb = new StringBuffer();
		int t;
		for (int i = 0; i < 16; i++) {
			t = bytes[i];
			if (t < 0)
				t += 256;
			sb.append(hexDigits[(t >>> 4)]);
			sb.append(hexDigits[(t % 16)]);
		}
		return sb.toString();
	}

	public static String getMd5String(String input, int bit) {
		try {
			if (StringUtil.isNullOrEmpty(input))
				return "";

			MessageDigest md = MessageDigest.getInstance(System.getProperty(
					"MD5.algorithm", "MD5"));

			if (bit == 32)
				return bytesToHex(md.digest(input.getBytes("utf-8")));

			if (bit == 24)
				return bytesToHex(md.digest(input.getBytes("utf-8")))
						.substring(0, 24);

			if (bit == 16)
				return bytesToHex(md.digest(input.getBytes("utf-8")))
						.substring(8, 24);

			if (bit == 8) {
				return bytesToHex(md.digest(input.getBytes("utf-8")))
						.substring(8, 16);
			}

			return bytesToHex(md.digest(input.getBytes("utf-8")));
		} catch (Exception e) {

		}
		return "";
	}

	@SuppressLint("SimpleDateFormat")
	public static String genLinkId() {
		Random rand = new Random();
		return new SimpleDateFormat("yyyyMMddHHmmsssSSS").format(new Date())
				+ (1000000 + rand.nextInt(8999999));
	}

	public static JSONObject getJSONObjectFromString(String json) {
		try {
			return new JSONObject(json);
		} catch (Exception ex) {

		}
		return null;
	}

	public static String getStringFromJSONObject(JSONObject jo, String key,
			String defaultValue) {
		try {
			return getString(jo.getString(key), defaultValue);
		} catch (Exception ex) {

		}
		return defaultValue;
	}

	public static int getIntegerFromJSONObject(JSONObject jo, String key,
			int defaultValue) {
		try {
			return getInteger(String.valueOf(jo.getInt(key)), defaultValue);
		} catch (Exception ex) {

		}
		return defaultValue;
	}

	public static boolean getBooleanFromJSONObject(JSONObject jo, String key,
			boolean defaultValue) {
		try {
			return getBoolean(String.valueOf(jo.getBoolean(key)), defaultValue);
		} catch (Exception ex) {

		}
		return defaultValue;
	}

	public static long getLongFromJSONObject(JSONObject jo, String key,
			long defaultValue) {
		try {
			return getLong(String.valueOf(jo.getLong(key)), defaultValue);
		} catch (Exception ex) {

		}
		return defaultValue;
	}

}
