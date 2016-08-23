package com.core_sur.notifierad;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
/**
 * 统计安装成功
 * @author Administrator
 *
 */
public class ApkInstallSuccessReciver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		String packageName = intent.getExtras().getString("packageName");
		//添加统计
		StatisticsUtil.installSuccess(context, packageName);
		
		context.unregisterReceiver(this);
	}

}
