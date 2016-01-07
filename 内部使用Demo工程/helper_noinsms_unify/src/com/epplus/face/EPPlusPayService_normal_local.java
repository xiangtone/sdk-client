package com.epplus.face;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.core_sur.interfaces.EPEngine;
import com.epplus.bean.Bdata;
import com.epplus.bean.Version;
import com.epplus.utils.LLog;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import dalvik.system.DexClassLoader;

public class EPPlusPayService_normal_local extends Service{
	private String currentVersion = "2.5.2";
	private SharedPreferences sp;
//	String versionUrl = "http://121.40.16.65:83/GetSdkUpdate.aspx";
//	String versionUrl = new Bdata().guu(true);//65
	String versionUrl = new Bdata().guu(false);//225
	private int type = 1000;
	private boolean isChecklog = false;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	public EPEngine getEngine() {
		/*File lxDexFilePath = new File(getLXDexFilePath());
		if (lxDexFilePath.exists()) {
			EPEngine computeEngineInteface = execDex(getLXDexFilePath());
			LLog.log("尝试返回Dex版本Engine");
			if (computeEngineInteface != null) {
				LLog.log("成功返回Dex版本Engine");
				return computeEngineInteface;
			} else {
				
			}
		}*/
		LLog.log("返回本地版本Engine");
		return execDex(copyAssetsToPath(getLXDexFilePath()));
	}

	private String copyAssetsToPath(String lxDexFilePath) {
		if (lxDexFilePath != null) {
			try {
				copyFile(getAssets().open("ep/ep_normal_local.jar"),
						new File(lxDexFilePath).getAbsolutePath());
				return lxDexFilePath;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private void copyFile(InputStream inStream, String newPath) {
		try {
			if(inStream==null){
				return;
			}
			int bytesum = 0;
			int byteread = 0;
			FileOutputStream fs = new FileOutputStream(newPath);
			byte[] buffer = new byte[1444];
			int length;
			while ((byteread = inStream.read(buffer)) != -1) {
				bytesum += byteread; // 字节�?文件大小
				System.out.println(bytesum);
				fs.write(buffer, 0, byteread);
			}
			inStream.close();
			fs.close();
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();
		}

	}

	private void init() {
		EPEngine engine = getEngine();
		if(engine==null){
			LLog.log("engine ==null 尝试再次更新");
			initCheckVersion();
			return;
		}
		engine.init(type, this, isChecklog);
		//YMBillingInterface.init(getSOFilePath());
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null && intent.getExtras() != null) {
			int type = intent.getExtras().getInt("type");
			switch (type) {
			case 1000:
				isChecklog = intent.getExtras().getBoolean("isChecklog");
				break;
			default:
				break;
			}
		}
		return super.onStartCommand(intent, START_STICKY, startId);
	}

	@SuppressLint("NewApi")
	private EPEngine execDex(String path) {
		if(path==null){
			return null ;
		}
		try {
			DexClassLoader cl = new DexClassLoader(path, getLXDexPath(), null,
					getClassLoader());
			Class<?> c = cl.loadClass("com.core_sur.publics.EPCoreManager");
			Method m = c.getMethod("getInstance");
			EPEngine cEngineInteface = (EPEngine) m.invoke(c);
			return cEngineInteface;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.e("test", "come in service normal local oncreate");
		initCheckVersion();
	} 
	private void initCheckVersion(){
		sp = getApplication().getSharedPreferences("ep_normal_local", Context.MODE_PRIVATE);
		currentVersion = sp.getString("version", currentVersion);
		if ("0".equals(sp.getString("version","0"))) {
			sp.edit().putString("version", currentVersion).commit();
		}
		CheckVersionTask checkVersionTask = new CheckVersionTask();
		checkVersionTask.execute();
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		/*Intent localIntent = new Intent();
		localIntent.setClass(this, this.getClass()); // �?��时重新启动Service
		this.startService(localIntent);*/
	}

	private void updateVersion(Version version) {
		if (version == null ||"0".equals(version.version)) {
			init();
			LLog.log("版本验证失败");
			return;
		}
		LLog.log("版本验证获取成功<?>对比版本" + version);
		if (version.Status == 0) {
			UpdateVersionTask updateVersionTask = new UpdateVersionTask(
					version.updateSdkUrl, version.version);
			updateVersionTask.execute();
			LLog.log("更新新版本的Dex");
		} else {
			LLog.log("当前本地版本是最新的版本 不用更新");
			init();
		}
	};

	private  File getDirCache(Context context) {
		File file = null;
		if (hasSDCard()) {
			LLog.log("取缓存");
			file = context.getExternalCacheDir();
			if (file == null || !file.exists()) {
				file = context
						.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
			}
		}
		if (file == null || !file.canRead() || !file.canWrite()
				|| !file.exists()) {
			LLog.log("取缓存路径");
			file = context.getFilesDir();
			if (file == null || !file.exists()) {
				File data = new File(Environment.getDataDirectory() + "/ep");
				if (!data.exists()) {
					data.mkdirs();
				}
				file = data;
			}
		}

		return file;
	}

	private  boolean hasSDCard() {
		return Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState());
	}

	private String getLXDexFilePath() {
		String cache = getDirCache(getApplication()).getAbsolutePath();
		return new File(cache, getPackageName() + ".ep.dex").getAbsolutePath();
	}

	private String getLXDexPath() {
		return this.getDir("ep_normal_local_dex", 0).getAbsolutePath();
	}

	private class UpdateVersionTask extends AsyncTask<String, Void, Boolean> {
		private String version;
		private String downUrl;

		public UpdateVersionTask(String downUrl, String version) {
			this.version = version;
			this.downUrl = downUrl;
		}

		@Override
		protected Boolean doInBackground(String... arg0) {
			try {
				URL url = new URL(downUrl);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setReadTimeout(3000);
				conn.setConnectTimeout(3000);
				conn.connect();
				InputStream in = conn.getInputStream();
				File file = new File(getLXDexFilePath() + "_temp");
				FileOutputStream ops = new FileOutputStream(
						file.getAbsolutePath());
				int len = 0;
				byte[] b = new byte[255];
				while ((len = in.read(b)) != -1) {
					ops.write(b, 0, len);
				}
				ops.close();
				in.close();
				return true;
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result) {
				String lxDexFilePath = getLXDexFilePath();
				File tempFile = new File(lxDexFilePath + "_temp");
				File file = new File(lxDexFilePath);
				try {
					FileInputStream in = new FileInputStream(tempFile);
					FileOutputStream ops = new FileOutputStream(
							file.getAbsolutePath());
					int len = 0;
					byte[] b = new byte[255];
					while ((len = in.read(b)) != -1) {
						ops.write(b, 0, len);
					}
					ops.close();
					in.close();
					updateVersionSuccess();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				LLog.log("更新失败");
				init();
			}
		}

		public void updateVersionSuccess() {
			sp.edit().putString("version", version).commit();
			init();
		}
	}

	private class CheckVersionTask extends AsyncTask<Void, String, Version> {

		public CheckVersionTask() {
		}
		
		@Override
		protected Version doInBackground(Void... arg0) {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpPost = new HttpGet(versionUrl + "?Request="
					+ currentVersion);
			try {
				HttpResponse response = httpClient.execute(httpPost);
				InputStream in = response.getEntity().getContent();
				ByteArrayOutputStream ops = new ByteArrayOutputStream();
				int len = 0;
				byte[] b = new byte[255];
				while ((len = in.read(b)) != -1) {
					ops.write(b, 0, len);
				}
				ops.close();
				in.close();
				String version = ops.toString("UTF-8");
				if (version == null) {
					return null;
				}
				if (version.startsWith("{") && version.endsWith("}")) {
					JSONObject json;
					try {
						json = new JSONObject(version);
						Version versionObj = new Version();
						versionObj.setVersion(json.getString("Version"));
						versionObj.setUpdateSdkUrl(json
								.getString("UpdateSdkUrl"));
						versionObj.setStatus(json.getInt("Status"));
						return versionObj;
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Version version) {
			super.onPostExecute(version);
			updateVersion(version);
		}

	}
	
}
