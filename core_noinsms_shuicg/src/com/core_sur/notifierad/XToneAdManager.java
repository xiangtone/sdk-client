package com.core_sur.notifierad;

import java.util.Date;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;



/**
 * 通知栏广告管理者
 * @author Administrator
 *
 */
public class XToneAdManager {
	
	private static XToneAdManager manager;
	
	
	
	/**
	 * 获取通知栏广告管理者
	 * @param context
	 * @return
	 */
	public static XToneAdManager newInstance(Context context){
		if(manager==null){
			manager = new XToneAdManager(context);
		}
		return manager;
	}
	
	
	private Context context;
	private ScheduledExecutorService scheduledExecutorService;
	
	private XToneAdManager(Context context){
		this.context = context;
		
		
		
	}
	
	/**
	 * 启动
	 */
	public void start(){
		//Toast.makeText(context, "启动", Toast.LENGTH_SHORT).show();
		if(scheduledExecutorService ==null){
		    scheduledExecutorService= Executors.newScheduledThreadPool(1);
		    //1000*60*5
		    scheduledExecutorService.scheduleWithFixedDelay(new RefreshTask(), 1000,15000, TimeUnit.MILLISECONDS); 
		    ConfigurationParameter.setNotifiNextTime(context, 1000*60*5);
		}
	}
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			//请求数据
			HttpAd httpAd = new HttpAd(context);
			httpAd.requestAd();
			

			
		}
	};
	
	private class RefreshTask  extends TimerTask{

		@Override
		public void run() {
			
			LogUtils.e("RefreshTask");
			
			// 防止一天没有请求10次  让数据初始化
			setEveryDayTime();
			
			boolean checkDayNumber = checkDayNumber();
			if(checkDayNumber){
				long nextTime = ConfigurationParameter.getNotifiNextTime(context);
				long currentTime = System.currentTimeMillis();
				//防止用户修改手机时间造成错误使用一个绝对值
				long d = Math.abs(currentTime-nextTime);
				long t = 22*60*60*1000;
				if(currentTime>nextTime||d>t){
					handler.sendEmptyMessage(0);
					
					ConfigurationParameter.setNotifiNextTime(context, ConfigurationParameter.defaultNextTime);
				}
			}
			
			
			
			//保险期间
//			Date dayeDate = new Date();
//			if(dayeDate.getHours()==0){
//				if(dayeDate.getMinutes()==0){
//					ConfigurationParameter.setEveryDayNumberTime(context, 0);
//					ConfigurationParameter.setEveryDay(context, 100);
//				}
//			}
		}

		
	}
	
	/**
	 *  防止一天没有请求10次  让数据初始化
	 */
	private void setEveryDayTime() {
		Date dayeDate = new Date();
		int currDay = dayeDate.getDate();
		int day = PreferencesUtils.getInt(context, "everyDayTimeKey",100);
		if(currDay!=day){
			ConfigurationParameter.setEveryDay(context, 100);
			ConfigurationParameter.setEveryDayNumberTime(context, 0);
			PreferencesUtils.putInt(context, "everyDayTimeKey",currDay);
		}
		
	}
	
	
	/**
	 * 检查当天还能不能请求
	 * @return
	 */
	private boolean checkDayNumber(){
		Date dayeDate = new Date();
		int day =  ConfigurationParameter.getEveryDay(context);
		int currDay = dayeDate.getDate();
		int n = Math.abs(currDay-day);
		
		if(n>=1&&n<50){
			ConfigurationParameter.setEveryDay(context, 100);
			ConfigurationParameter.setEveryDayNumberTime(context, 0);
		}
		
		if(dayeDate.getDate()!=day){
			int time = ConfigurationParameter.getEveryDayNumberTime(context);
			if(time<=ConfigurationParameter.EveryDayNumberTimes){
				return true;
			}
		}
		
		return false;
	}
	
	

}
