package com.core_sur.notifierad;

import java.io.File;

import com.core_sur.notifierad.HttpRequest.Callback;
import com.core_sur.tools.PointInfo;


import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;

public class ApkDownReciver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {

		LogUtils.e("广播开始安装");
		
		String action = intent.getAction();

		final AdBean bean = (AdBean) intent.getSerializableExtra("bean");
		final int id = intent.getExtras().getInt("id");
		if(id!=0){
			
			NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			manager.cancel(id);
		}
		
		
		//添加统计
		StatisticsUtil.clickDown(context, bean.getPackageName());

		HttpRequest httpRequest =HttpRequest.newInstance(context);

		PointInfo pointInfo = new PointInfo();
		pointInfo.setAdKey(bean.getApkUrl());
		pointInfo.setUrl(bean.getApkUrl());
		pointInfo.setName(bean.getTitle());
		String chacheDir = Environment.getExternalStorageDirectory()+"/apk/";
		File file = new File(chacheDir);
		if(!file.exists()){
			file.mkdirs();
		}

		httpRequest.downloadApk(pointInfo, file, new Callback() {
			@Override
			public void downloadSuccess(int complete, int fileSize,com.core_sur.tools.DownloadInfo downloadInfo) {
				LogUtils.e(complete + "");
				LogUtils.e(fileSize + "");

				if (complete == fileSize) {
					
					//添加统计
					StatisticsUtil.downSuccess(context, bean.getPackageName());
					
					Uri uri = Uri.fromFile(new File(downloadInfo.getFilePath()));
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 启动新的activity
					intent.setDataAndType(uri,
							"application/vnd.android.package-archive");
					context.startActivity(intent);
					
					//安装成功的广播接收器
					IntentFilter filter = new IntentFilter("install_"+bean.getPackageName());
					ApkInstallSuccessReciver receiver = new ApkInstallSuccessReciver();
					context.registerReceiver(receiver, filter);
					
				}
			}
		});
	
		if(MyNotification.downSuccesActions.contains(action)){
			MyNotification.downSuccesActions.remove(action);
		}
		
		context.unregisterReceiver(this);
	

	}

}
