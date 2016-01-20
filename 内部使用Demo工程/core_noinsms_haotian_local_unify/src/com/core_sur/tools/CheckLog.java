package com.core_sur.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;
import android.util.Log;

public class CheckLog {
	static boolean openCheck = false;

	public static void setOpenCheckConfigIs(boolean isOpen) {
		openCheck = isOpen;
	}

	private CheckLog() {
		// TODO Auto-generated constructor stub
	}

	public static String getDirCache() {
		File file = null;
		if (hasSDCard()) {
			file = Environment.getExternalStorageDirectory();
			if (file == null || !file.exists()) {
				file = Environment.getExternalStorageDirectory();
			}
			if (file == null || !file.exists()) {
				return null;
			}
			file = new File(file, "/ep/");
			if (file == null || !file.exists()) {
				file.mkdirs();
			}
			if (!file.exists()) {
				return null;
			}
			if (!file.canWrite() || !file.canRead()) {
				return null;
			}
			return file.getAbsolutePath();
		} else {
			return null;
		}
	}

	public static boolean hasSDCard() {
		return Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState());
	}

	private static DateFormat formatter = new SimpleDateFormat(
			"yyyy-MM-dd-HH:mm:ss");

	public static void log(String className, String method, String content) {

		String logFormat = new String(
				"time={0}:\nclassName={1}:\nmethod={2}:\ncontent={3}:");
		String time = formatter.format(new Date());
		String dirCache = getDirCache();
		String log = null;
		if (dirCache == null) {
			log = MessageFormat.format(logFormat, time, className, method,
					content, "notExists");
		} else {
			log = MessageFormat.format(logFormat, time, className, method,
					content, new File(dirCache, "log.txt").getAbsolutePath());
			if (com.core_sur.Config.IsDebug) {
				appendStr(log + "\n");
			}
		}
		if (openCheck) {
			Log.i("EP_Server", log);
		}
	}

	public static void appendStr(String str) {

		FileWriter fw = null;
		try {
			// 如果文件存在，则追加内容；如果文件不存在，则创建文件
			if (getDirCache() == null) {
				return;
			}
			File f = new File(getDirCache(), "log.txt");
			fw = new FileWriter(f, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		PrintWriter pw = new PrintWriter(fw);
		pw.println(str);
		pw.flush();
		try {
			fw.flush();
			pw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
