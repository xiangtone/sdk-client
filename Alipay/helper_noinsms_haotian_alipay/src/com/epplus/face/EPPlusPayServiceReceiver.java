package com.epplus.face;

import java.text.MessageFormat;
import java.util.List;

import com.epplus.utils.LLog;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Process;

public class EPPlusPayServiceReceiver extends BroadcastReceiver {
	private Context context;
	public void startService() {
		Intent service = new Intent();
		service.setClassName(context.getPackageName(),EPPlusPayService.class.getName());
		context.startService(service);
		
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			Intent in = new Intent(MessageFormat.format("{0}.ep.network.change",context.getPackageName()));
			context.sendBroadcast(in);		
		}
		
		if(!checkServiceRunning(context, EPPlusPayService.class.getName())){
			startService();
		}else{
			
		}
	}

	public boolean checkServiceRunning(Context c, String packageName) {
		ActivityManager am = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> runningServices = am.getRunningServices(50);
		for (RunningServiceInfo runningServiceInfo : runningServices) {
			if (runningServiceInfo.pid == Process.myPid()) {
				if (runningServiceInfo.service.getClassName().equals(packageName)) {
					return true;
				}
			}
		}
		return false;
	}

}
