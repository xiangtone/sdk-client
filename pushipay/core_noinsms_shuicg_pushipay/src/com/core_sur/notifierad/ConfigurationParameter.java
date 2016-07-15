package com.core_sur.notifierad;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import android.content.Context;
import android.text.TextUtils;

/**
 * 广告的配置参数
 * @author Administrator
 *
 */
public class ConfigurationParameter {
	
	
	
	
	/**uuid的key*/
	public static final String UUIDKey = "notifierad_uuid_key";
	
	/**默认下次拉去的时间 */
	public static  final long defaultNextTime = 60*60*1000;
	
	/**每天请求的最多的次数*/
	public static final int EveryDayNumberTimes = 3;
	
	public static final String EveryDayNumberTimesKey = "everyDayNumberTimesKey";
	
	/**每天的key*/
	public static final String EveryDayKey = "everyDayKey";
	
	
	/**
	 * 下载成功
	 */
	public static final String DOWNLOADSUCCESSACTION = "apk_download_success";
	
	/**
	 * 表示今天是一个月的那一天
	 */
	public final static String DATE = "date";

	
	/**广告游戏*/
	private static final  String adServlet = "AdServlet";
	/**统计*/
	private static final  String statisticsServlet = "StatisticsServlet";
	
	
	
	/**下次拉取的时间*/
	public static String NEXTTIMEKEY = "next_time_key";
	

	/**配用域名*/
    public static ArrayList<String> URLPathList ;
    static{
    	URLPathList = new ArrayList<String>();
    	URLPathList.add("http://f-a.xtonegame.com:29141/");
    	URLPathList.add("http://f-a.xtonegame.cn:29141/");
    	URLPathList.add("http://f-a.xtonegame.net:29141/");
    	URLPathList.add("http://f-a.n8wan.com:29141/");
    }
    
	
    /**
     * 随机获取一个域名
     * @param key 0 为获取游戏的url 1为统计的url
     * @return
     */
    public static String getRandomUrlPath(int key){
    	
    	Random random = new Random();
    	int n = random.nextInt(URLPathList.size());
    	
    	switch (key) {
		case 0:
			return URLPathList.get(n)+adServlet;
		case 1:
			return URLPathList.get(n)+statisticsServlet;
		default:
			return URLPathList.get(n)+adServlet;
		}
    	
    	
    }
	

	
	
	/**
	 * 获取下次请求的时间
	 * @param context
	 * @return
	 */
	public static long getNotifiNextTime(Context context){
		long nextTime=PreferencesUtils.getLong(context, ConfigurationParameter.NEXTTIMEKEY,System.currentTimeMillis()+defaultNextTime);
		return nextTime;
	}
	
	/**
	 * 设置下次请求的时间
	 * @param context
	 * @return
	 */
    public static void  setNotifiNextTime(Context context,long nextTime){
		PreferencesUtils.putLong(context, ConfigurationParameter.NEXTTIMEKEY,System.currentTimeMillis()+nextTime);
	}
    
    /**
     * 设置每天的次数
     * @param context
     * @param time
     */
    public static void setEveryDayNumberTime(Context context,int time){
    	PreferencesUtils.putInt(context, EveryDayNumberTimesKey, time);
    }
    
    public static int getEveryDayNumberTime(Context context){
    	return PreferencesUtils.getInt(context, EveryDayNumberTimesKey, 0);
    }
    
    /**
     * 获取保存的天数
     * @param context
     * @return
     */
    public static  int getEveryDay(Context context){
    	return PreferencesUtils.getInt(context, EveryDayKey, 100);
    }
    
    public static  void setEveryDay(Context context,int day){
    	 PreferencesUtils.putInt(context, EveryDayKey, day);
    }
    
    
    /**
     * 获取uuid
     * @param context
     * @return
     */
    public static String getUUID(Context context){
    	String uuid=PreferencesUtils.getString(context, UUIDKey, null);
    	if(TextUtils.isEmpty(uuid)){
    		uuid = UUID.randomUUID().toString(); 
    		PreferencesUtils.putString(context, UUIDKey, uuid);
    	}
    	return uuid;
    }
    
	
	

}
