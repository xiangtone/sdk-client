package com.core_sur.tools;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.core_sur.Config;

import android.annotation.SuppressLint;

public class LogUtil
{
	public static final int DEBUG = 1;
	public static final int INFO = 2;
	public static final int ERROR = 3;
	
	@SuppressLint("SimpleDateFormat")
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static void log(int type,String tag,String value)
	{
		if(!Config.IS_DEBUG)
			return;
		
		switch(type)
		{
			case DEBUG:
				android.util.Log.d(tag, value);
				break;
			
			case INFO:
				android.util.Log.i(tag, value);
				break;
				
			case ERROR:
				android.util.Log.e(tag, value);
				break;
			
			default:
				break;
		}
		
		log("["+ tag +"]" + value);
	}
	
	private static void log(String value)
	{
		FileWriter fw = null;
		try
		{
			File file = new File(Config.DEBUG_FILE_LOG + "/ht.log");
			if(!file.exists())
			{
				file.createNewFile();
			}
			fw = new FileWriter(file,true);
			fw.append(sdf.format(new Date()) + ":" + value.toString() + "\r\n");
		}
		catch(Exception ex)
		{
			
		}
		finally
		{
			try{if(fw!=null)fw.close();}catch(Exception ex){}
		}
	}
}
