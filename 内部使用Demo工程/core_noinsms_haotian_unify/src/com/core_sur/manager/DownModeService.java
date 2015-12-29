package com.core_sur.manager;

import java.io.File;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.core_sur.finals.CommonFinals;
import com.core_sur.listener.DownloadListener;
import com.core_sur.tools.CheckLog;
import com.core_sur.tools.PointInfo;

public class DownModeService {
	public static DownModeService downModeService;
	public boolean isopen = true;
	private final int DOWNLOADING = 10;
	private Map<String, DownloadService> downloaders = new HashMap<String, DownloadService>();
	Handler handler = new Handler() {
		float currentComplete = 0;

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWNLOADING:
				PointInfo pointInfo = (PointInfo) msg.obj;
				int completeSize = msg.getData().getInt("completeSize");
				int fileSize = msg.getData().getInt("fileSize");
				String adKey = pointInfo.getAdKey();
				String name = pointInfo.getName();
				String url = pointInfo.getUrl();
				String packagename = pointInfo.getPackagename();
				float completePercent = (float) ((((float) completeSize)
						/ ((float) fileSize) * 1.0) * 100.0f);
				if (completePercent != currentComplete || completePercent >= 99) {
					currentComplete = completePercent;
					if (isopen && pointInfo != null) {
						Intent intent = new Intent("com.point");
						intent.putExtra("action", "updateProgress");
						intent.putExtra("completeSize", completePercent);
						intent.putExtra("adKey", adKey);
						c.sendBroadcast(intent);
					}
					String filename = url.substring(url.lastIndexOf("/") + 1,
							url.length());
					String filePath = new File(new File(getPath()), filename)
							.getAbsolutePath();
					/*
					 * if (completePercent == 100) { Intent intent = new
					 * Intent("com.point"); intent.putExtra("action",
					 * "downSuccess"); intent.putExtra("adKey", adKey);
					 * c.sendBroadcast(intent); PointTaskDao dao = new
					 * PointTaskDao(downModeService.c); dao.addTask(adKey,
					 * packagename, pointInfo.isPoint()); }
					 */
					//NotificationUtils.getInstance(downModeService.c)
					//		.progressNotification(adKey, name, 100,
					//				completePercent, filePath,
					//				pointInfo.getIcon());
					break;
				}
			}
			;
		}
	};
	private Context c;
	private Intent registDownload;
	private BroadcastReceiver downReceiver;

	public static DownModeService getInstance(Context c) {
		if (downModeService == null) {
			downModeService = new DownModeService();
		}
		downModeService.c = c;
		return downModeService;
	}

	public DownModeService() {
	}

	public String getPath() {
		return getSDPath();
	}

	public String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);

		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();
			return sdDir.getAbsolutePath();
		}
		return null;
	}

	public void startDownload(final PointInfo pointInfo) {
		new Thread() {
			private DownloadService service;

			@Override
			public void run() {
				super.run();
				try {
					if (downloaders.get(pointInfo.getUrl()) == null) {
						service = new DownloadService(pointInfo.getUrl(),
								new File(getPath()), 3, downModeService.c);
						downloaders.put(pointInfo.getUrl(), service);
					} else {
						service = downloaders.get(pointInfo.getUrl());
						if (service.getDownloading() && !service.isPause) {
							return;
						}
					}
					if (service.isPause) {
						service = new DownloadService(pointInfo.getUrl(),
								new File(getPath()), 3, downModeService.c);
						downloaders.put(pointInfo.getUrl(), service);
					}
					service.download(new DownloadListener() {
						@Override
						public void onDownload(int downloaded_size,
								String fileUrl, int fileSize) {
							Message msg = Message.obtain();
							msg.obj = pointInfo;
							msg.what = DOWNLOADING;
							msg.getData().putInt("completeSize",
									downloaded_size);
							msg.getData().putInt("fileSize", fileSize);
							handler.sendMessage(msg);
						}
					});
				} catch (Exception e) {
					CheckLog.log(this.getClass().getName(),
							new Exception().getStackTrace()[new Exception()
									.getStackTrace().length - 1].toString(), e
									.getMessage());
					e.printStackTrace();
				}
			}
		}.start();

	}

	public void destory() {
		if (downReceiver != null)
			c.unregisterReceiver(downReceiver);
	}

	public void init() {

		downReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context c, Intent intent) {
				PointInfo pointInfo = new PointInfo();
				String adKey = intent.getExtras().getString("adKey");
				if (adKey != null) {
					pointInfo.setAdKey(adKey);
				} else {
					pointInfo.setAdKey(UUID.randomUUID().toString());
				}
				pointInfo.setIcon((Bitmap) intent.getExtras().get("icon"));
				pointInfo.setPoint(intent.getExtras().getBoolean("isPoint"));
				pointInfo.setPackagename(intent.getExtras().getString(
						"packageName"));
				pointInfo.setName(intent.getExtras().getString("appName"));
				pointInfo.setUrl(intent.getExtras().getString("downloadUrl"));
				startDownload(pointInfo);
			}
		};
		c.registerReceiver(
				downReceiver,
				new IntentFilter(MessageFormat.format(
						CommonFinals.ACTION_DOWNLOADER, c.getPackageName())));
	}
}
