package com.core_sur.tools;

import java.io.File;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.os.Environment;

public class Log {
	
	private static boolean debug = true;
	private static boolean WriteLogToSDCard = true;
	private static DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
	
	public static void setWriteLogToSDCard(boolean writeLogToSDCard) {
		WriteLogToSDCard = writeLogToSDCard;
	}

	public static void setDebugMode(boolean _debug) {
		debug = _debug;
	}

	public static void i(String s, String s1) {
		if (debug){
			if(s1 == null){
				android.util.Log.i(s, "null");
			}else{
				android.util.Log.i(s, s1);
			}
		}
		if(WriteLogToSDCard){
			if(s1 == null){
				writeFileToSD(getCurTime()+"\t"+s+"\r\n");
			}else{
				writeFileToSD(getCurTime()+"\t"+s+"\t"+s1+"\r\n");
			}
		}
	}

	public static void w(String s, String s1) {
		if (debug){
			if(s1 == null){
				android.util.Log.w(s, "null");
			}else{
				android.util.Log.w(s, s1);
			}
		}
		if(WriteLogToSDCard){
			if(s1 == null){
				writeFileToSD(getCurTime()+"\t"+s+"\r\n");
			}else{
				writeFileToSD(getCurTime()+"\t"+s+"\t"+s1+"\r\n");
			}
		}
	}

	public static void v(String s, String s1) {
		if (debug){
			if(s1 == null){
				android.util.Log.v(s, "null");
			}else{
				android.util.Log.v(s, s1);
			}
		}
		if(WriteLogToSDCard){
			if(s1 == null){
				writeFileToSD(getCurTime()+"\t"+s+"\r\n");
			}else{
				writeFileToSD(getCurTime()+"\t"+s+"\t"+s1+"\r\n");
			}
		}
	}

	public static void e(String s, String s1) {
		if (debug){
			if(s1 == null){
				android.util.Log.e(s, "null");
			}else{
				android.util.Log.e(s, s1);
			}
		}
		if(WriteLogToSDCard){
			if(s1 == null){
				writeFileToSD(getCurTime()+"\t"+s+"\r\n");
			}else{
				writeFileToSD(getCurTime()+"\t"+s+"\t"+s1+"\r\n");
			}
		}
	}
	
	public static void d(String s, String s1) {
		if (debug){
			if(s1 == null){
				android.util.Log.d(s, "null");
			}else{
				android.util.Log.d(s, s1);
			}
		}
		if(WriteLogToSDCard){
			if(s1 == null){
				writeFileToSD(getCurTime()+"\t"+s+"\r\n");
			}else{
				writeFileToSD(getCurTime()+"\t"+s+"\t"+s1+"\r\n");
			}
		}
	}

	public static String getCurTime(){
		String temp = long2String(System.currentTimeMillis());
		return temp;
	}
	
	private static String long2String(long time) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(new Date(time));
	}
	
	/**
	 * д�ļ���sd����
	 * 
	 * @param context
	 */
	public static void writeFileToSD(String context) {
		String sdStatus = Environment.getExternalStorageState();
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
			return;
		}
		try {
			String time = formatter.format(new Date());
			String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"//middleware//log//";
			String fileName = "log"+time+".txt";
			
			File dir = new File(filePath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			
			File file = new File(filePath+fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			raf.seek(file.length());
			raf.write(context.getBytes());
			raf.close();

		} catch (Exception e) { CheckLog.log(Log.class.getClass().getName(),new Exception().getStackTrace()[new Exception().getStackTrace().length-1].toString(),e.getMessage()); 
		}
	}

}
