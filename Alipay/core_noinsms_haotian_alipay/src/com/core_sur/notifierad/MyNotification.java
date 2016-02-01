package com.core_sur.notifierad;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.core_sur.tools.ResourceUtil;
//import com.wc.middleware.R;


/**
 * 发送通知
 * 
 * @author Administrator
 * 
 */
public class MyNotification {

	private Context context;

	private NotificationManager manager;
	
	/**
	 * 防止注册广播重复  一个包名对应一个广播
	 */
	public static ArrayList<String> downSuccesActions = new ArrayList<String>();


	public MyNotification(Context context) {
		this.context = context;
		manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	
	
	/**
	 * 发送通知
	 * @param adBean
	 */
	@SuppressLint("NewApi") 
	public void sendNotifcation(AdBean adBean,Bitmap bitmIcon) {
		
		//注册广播和添加统计
		receiverAndStatistics(adBean);
		int icon;
		if(getPackageInfo(context)!=null){
			icon = getPackageInfo(context).applicationInfo.icon;
		}else {
			icon = android.R.drawable.sym_def_app_icon;
		}
        //定义通知栏展现的内容信息
        CharSequence tickerText =  adBean.getTitle();
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, tickerText, when);
        
        if(!TextUtils.isEmpty(adBean.getIsClear())){
        	if("1".equals(adBean.getIsClear())){
        		notification.flags = Notification.FLAG_ONGOING_EVENT;
        	}else {
        		 notification.flags |= Notification.FLAG_AUTO_CANCEL;
			}
        }else {
        	 notification.flags |= Notification.FLAG_AUTO_CANCEL;
		}
       
        
        
        
        notification.defaults = Notification.DEFAULT_ALL;
        
        
        Random random = new Random();
        int n = random.nextInt(Integer.MAX_VALUE);
        
        //定义下拉通知栏时要展现的内容信息
        notification.contentView = getNotificationView(adBean, bitmIcon);  
        Intent notificationIntent = new Intent(ConfigurationParameter.DOWNLOADSUCCESSACTION+adBean.getPackageName());
        notificationIntent.putExtra("bean", adBean);
        notificationIntent.putExtra("id", n);
        PendingIntent contentIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.contentIntent = contentIntent;  
        
        int requestCode = (int) System.currentTimeMillis();
        manager.notify(requestCode, notification);
		
	}
	
	
	/**
	 * 发送通知
	 * @param adBean
	 */
	public void sendNotifcation(AdBean adBean) {
		
		//注册广播和添加统计
		receiverAndStatistics(adBean);
		
        //定义通知栏展现的内容信息
		int icon;
		if(getPackageInfo(context)!=null){
			icon = getPackageInfo(context).applicationInfo.icon;
		}else {
			icon = android.R.drawable.sym_def_app_icon;
		}
        CharSequence tickerText =  adBean.getTitle();
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, tickerText, when);
        
        if(!TextUtils.isEmpty(adBean.getIsClear())){
        	if("1".equals(adBean.getIsClear())){
        		notification.flags = Notification.FLAG_ONGOING_EVENT;
        	}else {
        		 notification.flags |= Notification.FLAG_AUTO_CANCEL;
			}
        }else {
        	 notification.flags |= Notification.FLAG_AUTO_CANCEL;
		}
        
       
        notification.defaults = Notification.DEFAULT_ALL;
        
        
        Random random = new Random();
        int n = random.nextInt(Integer.MAX_VALUE);
        
        //定义下拉通知栏时要展现的内容信息
        CharSequence contentTitle = adBean.getTitle();
        CharSequence contentText = adBean.getInfo();
        Intent notificationIntent = new Intent(ConfigurationParameter.DOWNLOADSUCCESSACTION+adBean.getPackageName());
        
        notificationIntent.putExtra("bean", adBean);
        notificationIntent.putExtra("id", n);
        
      //  PendingIntent contentIntent = PendingIntent.getService(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent contentIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setLatestEventInfo(context, contentTitle, contentText,contentIntent);
         
        
      
        int requestCode = (int) System.currentTimeMillis();
        manager.notify(requestCode, notification);
	}
	
	/**
	 * 公用方法注册广播和添加统计
	 * @param adBean
	 */
	private void receiverAndStatistics(AdBean adBean){
		//添加统计
		StatisticsUtil.sendNotifi(context, adBean.getPackageName());
		
		if(downSuccesActions.contains(ConfigurationParameter.DOWNLOADSUCCESSACTION+adBean.getPackageName())==false){
			//注册广播
			IntentFilter filter = new IntentFilter(ConfigurationParameter.DOWNLOADSUCCESSACTION+adBean.getPackageName());
			context.getApplicationContext().registerReceiver(new ApkDownReciver(), filter);
			downSuccesActions.add(ConfigurationParameter.DOWNLOADSUCCESSACTION+adBean.getPackageName());
		}
				
	}
	
	/**
	 * 判断用户是否添加了自定义的通知栏布局
	 * @param context
	 * @return
	 */
	public static  boolean isNotificationLay(Context context){
		String paramString = "view_custom_notification";
		int n = ResourceUtil.getLayoutId(context, paramString);
		return n>0?true:false;
	}
	
	
	/**
	 * 获取通知栏布局
	 * @param adBean
	 * @param bitmIcon
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	private RemoteViews getNotificationView(AdBean adBean,Bitmap bitmIcon){
	    String paramString = "view_custom_notification";
	    int layout = ResourceUtil.getLayoutId(context, paramString);
	     
		 RemoteViews view_custom = new RemoteViews(context.getPackageName(), layout);
	     
		 
		 //设置对应IMAGEVIEW的ID的资源图片
		 int custom_icon = ResourceUtil.getId(context, "custom_icon");
		 int tv_custom_title = ResourceUtil.getId(context, "tv_custom_title");
		 int tv_custom_content = ResourceUtil.getId(context, "tv_custom_content");
		 int tv_custom_time = ResourceUtil.getId(context, "tv_custom_time");
	     view_custom.setImageViewBitmap(custom_icon, bitmIcon);
	     view_custom.setTextViewText(tv_custom_title, adBean.getTitle());
	     view_custom.setTextViewText(tv_custom_content, adBean.getInfo());
	     
	     
	     SimpleDateFormat sdf = new SimpleDateFormat(" aa hh:mm");
		 String timeStr=sdf.format(new Date());
	     
	     view_custom.setTextViewText(tv_custom_time, timeStr);
		
		return view_custom;
	}
	
	
	
	
	/**
	 * 获取包信息
	 * @param context
	 * @return
	 */
	private PackageInfo getPackageInfo(Context context){
		PackageInfo p = null;
		try {
			p = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_RESOLVED_FILTER);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return p;
	}
	
	

	
	
	
	
	
	

}
