package com.epplus.face;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
/**
 * 统计安装的apk
 * @author Administrator
 *
 */
public class InstallReciver extends BroadcastReceiver{

	private  static final String INSTALL = "install_";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		/*
		String action = intent.getAction();
		if (action.equals("android.intent.action.PACKAGE_ADDED")
				|| action.equals("android.intent.action.PACKAGE_INSTALL")) {

			String packageName = intent.getDataString().substring(8);
			
			Intent i = new Intent(INSTALL+packageName);
			i.putExtra("packageName", packageName);
			context.sendBroadcast(i);
		}*/
	}



}
