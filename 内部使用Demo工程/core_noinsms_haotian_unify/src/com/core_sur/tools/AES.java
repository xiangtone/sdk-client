package com.core_sur.tools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class AES
{
	// 加密
	public static String EncodeString(String Contents, String KeyOfAES)
	{
		if (Contents == null || Contents.equals(""))
		{
			return "";
		}

		SimpleDateFormat sdf = new SimpleDateFormat("", Locale.SIMPLIFIED_CHINESE);

		sdf.applyPattern("MMddHHmmss");

		String timeStr = sdf.format(new Date());

		Contents = AESCode.encrypt(timeStr + Contents, KeyOfAES);

		return Contents;
	}

	// 解密：
	public static String DecodeString(String Contents, String KeyOfAES)
	{
		CheckLog.log(Thread.currentThread() .getStackTrace()[1].getClassName(),"DecodeString 加密",Contents);
		if (Contents == null || Contents.equals(""))
		{
			return "";
		}

		Contents = AESCode.decrypt(Contents, KeyOfAES);
		if (Contents == null)
		{
			return "";
		}
		if (Contents.length() < 11)
		{
			return "";
		}

		Contents = Contents.substring(10);
		CheckLog.log(Thread.currentThread() .getStackTrace()[1].getClassName(),"DecodeString 解密",Contents);

		return Contents;
	}
}
