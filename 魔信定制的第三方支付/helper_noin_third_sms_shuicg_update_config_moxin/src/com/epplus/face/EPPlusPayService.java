package com.epplus.face;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.core_sur.interfaces.EPEngine;
import com.epplus.bean.Bdata;
import com.epplus.bean.DexBean;
import com.epplus.bean.EncodeUtils;
import com.epplus.bean.PackageData;
import com.epplus.bean.VersionData;
import com.epplus.publics.EPPayHelper;
import com.epplus.utils.LLog;
import com.epplus.utils.Util;

import dalvik.system.DexClassLoader;

public class EPPlusPayService extends Service {
	private SharedPreferences sp;
	// String versionUrl = "http://121.40.16.65:83/GetSdkUpdate.aspx";
	// String versionUrl = new Bdata().guu(true);//65
	// String versionUrl = new Bdata().guu(false);//225
	//http://dx.n8wan.com/
//	String versionUrl = "http://192.168.1.210:8080/xtone-interface-package-manager/r";
//	String resultUrl = "http://192.168.1.210:8080/xtone-interface-package-manager/d";
	String versionUrl = new Bdata().gver();//"http://dx.n8wan.com/r";
	String resultUrl = new Bdata().gresult();//"http://dx.n8wan.com/d";
	
	
	
	
	
	private int type = 1000;
	private boolean isChecklog = false;
	List<VersionData> versionDataList = new ArrayList<VersionData>(); // 保存服务端返回的数据
	List<PackageData> packageDataList = new ArrayList<PackageData>(); // 发起http请求的jar包的数据列表(名称+版本号)
	List<PackageData> uploadDataList = new ArrayList<PackageData>(); // 版本下载更新成功后jar包的数据列表(名称+版本号)

	PackageData packageData = new PackageData();
	File file = null;
	private int initCount = 0; // 初始化的次数，如果失败则继续初始化，5次后不再初始化。
	private String jsonParamStr, jsonUploadDataParamStr, jsonParamEncodeStr;

	private static final String APPLICATION_JSON = "application/json";
	private static final String CONTENT_TYPE_TEXT_JSON = "text/json";

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	public EPEngine getEngine(VersionData versionData) {
		// 数据不为空的时候
		if (versionData != null) {
			LLog.log("versionData != null");
			File lxDexFile = new File(getLXDexFilePath(versionData.getSectionDexPathName()));
			LLog.log("lxDexFile.exists():" + lxDexFile.exists());
			if (lxDexFile.exists()) {
				EPEngine computeEngineInteface = execDex(getLXDexFilePath(versionData.getSectionDexPathName()),
						versionData.getCoreClassName());
				LLog.log("尝试返回Dex版本Engine");
				if (computeEngineInteface != null) {
					LLog.log("成功返回Dex版本Engine");
					return computeEngineInteface;
				}
			}
			LLog.log("返回本地版本Engine");
			return execDex(
					copyAssetsToPath(versionData.getXmlName(), getLXDexFilePath(versionData.getSectionDexPathName())),
					versionData.getCoreClassName());
		} else { // 数据为空的时候
			LLog.log("versionData == null");
			File lxDexFile = new File(getLXDexFilePath(null));
			LLog.log("111---lxDexFile.exists():" + lxDexFile.exists());
			if (lxDexFile.exists()) {
				EPEngine computeEngineInteface = execDex(getLXDexFilePath(null), "com.core_sur.publics.EPCoreManager");
				LLog.log("111---尝试返回Dex版本Engine");
				if (computeEngineInteface != null) {
					LLog.log("111---成功返回Dex版本Engine");
					return computeEngineInteface;
				}
			}
			LLog.log("111---返回本地版本Engine");
			return execDex(copyAssetsToPath("ep", getLXDexFilePath(null)), "com.core_sur.publics.EPCoreManager");
		}
	}

	private String copyAssetsToPath(String jarName, String lxDexFilePath) {
		LLog.log("come in copyAssetsToPath");
		/*if (lxDexFilePath != null) {
			try {
				Util.copyFile(getAssets().open("ep/" + jarName + ".jar"), new File(lxDexFilePath).getAbsolutePath());
				return lxDexFilePath;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/
		return null;
	}

	private void init(VersionData versionData) {

		if (versionData == null) {
			return;
		}

		//LLog.error("come in versionData.getInitFlag()==0");
		EPEngine engine = getEngine(versionData);
		if (engine == null) {
			LLog.log("come in engine==null");
			/*initCount++;
			if (initCount < 5) {
				initCheckVersion();
			}*/
			return;
		}
		engine.init(type, this, isChecklog);

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
	private EPEngine execDex(String path, String coreClassName) {
		LLog.log("execDex---path:" + path + ",,,coreClassName:" + coreClassName);
		if (path == null) {
			return null;
		}

		/*try {
			DexClassLoader cl = new DexClassLoader(path, getLXDexPath(), null, getClassLoader());
			Class<?> c = cl.loadClass(coreClassName);
			Method m = c.getMethod("getInstance");
			EPEngine cEngineInteface = (EPEngine) m.invoke(c);
			return cEngineInteface;
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		LLog.log("EPPluspayService---onCreate");
		File data = getApplication().getFilesDir();
		LLog.log("oncreate---data:" + data.getAbsolutePath());
		file = new File(data.getAbsolutePath() + "/ep.txt");
		if (!file.exists()) {
			LLog.log("file not exists");
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			LLog.log("file exists");
		}
		initCheckVersion();
	}

	public void initCheckVersion() {
		packageDataList.clear();
		String resultStr = Util.getInputString(file);
		// data/data/files下的ep.txt不为空则走if分支，反之走else分支
		if (resultStr.length() > 0 && !resultStr.isEmpty()) {
			LLog.log("come in resultStr.length() > 0");
			String jarData[] = resultStr.split(",");
			for (int i = 0; i < jarData.length; i++) {
				int splitPos = jarData[i].indexOf(':');
				// 包的名称和版本号
				packageDataList
						.add(new PackageData(jarData[i].substring(0, splitPos), jarData[i].substring(splitPos + 1)));
			}
		} else {
			LLog.log("come in resultStr.length() == 0");
			// 包的名称和版本好(默认的是ep 可以修改)
			packageDataList.add(new PackageData("ep", "0.0"));
		}

		LLog.log("packageDataList.size()=" + packageDataList.size());

		DexBean dexBean = new DexBean();
		dexBean = Util.getDexBean(this, packageDataList);// 得到DexBean实例
		jsonParamStr = Util.parseObjectToJsonString(dexBean, packageDataList);// 将Dexbean对象转化为json
																				// String
		LLog.log("jsonParamStr:" + jsonParamStr);
		jsonParamEncodeStr = EncodeUtils.encode(jsonParamStr);
		LLog.log("jsonParamEncodeStr:" + jsonParamEncodeStr);
		CheckVersionTask checkVersionTask = new CheckVersionTask();
		checkVersionTask.execute(); // 该方法只能在UI线程使用。
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//initCount = 0; // 初始化次数置为0
		Intent localIntent = new Intent();
		localIntent.setClass(this, this.getClass());
		this.startService(localIntent);
	}

	public String getLXDexFilePath(String sectionDexPathName) {
		String cache = Util.getDirCache(getApplication()).getAbsolutePath();
		if (sectionDexPathName == null) {
			return new File(cache, getPackageName() + ".ep.dex").getAbsolutePath();
		} else {
			return new File(cache, getPackageName() + sectionDexPathName).getAbsolutePath();
		}
	}

	// data/data/包名/app_dex
	public String getLXDexPath() {
		return this.getDir("dex", 0).getAbsolutePath();
	}

	public void updateVersion(List<VersionData> versionData) {
		if (versionData == null || versionData.size() == 0) {
			init(null);
			LLog.log("版本验证失败");
			return;
		}
		for (int i = 0; i < versionData.size(); i++) {

			// 有新的版本需要更新则走if分支反之走else分支
			if (versionData.get(i).getStatus() == 0) {
				LLog.log("更新新版本的Dex[" + i + "]:" + versionData.get(i).getXmlName());
				UpdateVersionTask updateVersionTask = new UpdateVersionTask(versionData.get(i));
				updateVersionTask.execute();
			} else {
				LLog.log("当前本地版本是最新的版本 不用更新:" + versionData.get(i).getXmlName());
				// 因为没有初始话版本号，所以，不需要更新版本的时候就把服务端给的版本号写入xml文件
				sp = getApplication().getSharedPreferences(versionData.get(i).getXmlName(), Context.MODE_PRIVATE);
				sp.edit().putString("version", versionData.get(i).getVersion()).commit();
				init(versionData.get(i));
			}
		}

	};

	private class CheckVersionTask extends AsyncTask<Void, String, List<VersionData>> {

		public CheckVersionTask() {
		}

		@Override
		protected List<VersionData> doInBackground(Void... arg0) {

			try {
				LLog.log("url:" + versionUrl);
				String encoderJson = URLEncoder.encode(jsonParamEncodeStr, HTTP.UTF_8);				
				HttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(versionUrl);
				httpPost.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
				StringEntity se = new StringEntity(encoderJson);
				se.setContentType(CONTENT_TYPE_TEXT_JSON);
				se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON));
				httpPost.setEntity(se);
				// Log.e("test","doInBackground---0.0");
				HttpResponse response = httpClient.execute(httpPost);
				// Log.e("test","doInBackground---0");
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
				LLog.log("version:" + version);
				if (version == null) {
					return null;
				}
				versionDataList.clear();
				JSONObject jsonObject = new JSONObject(version);

				JSONArray jsonArray = jsonObject.getJSONArray("result");
				LLog.log("jsonArray length:" + jsonArray.length());
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jb = jsonArray.getJSONObject(i);
					versionDataList.add(new VersionData(jb));
				}

				if (versionDataList != null && versionDataList.size() > 0) {
					for (int i = 0; i < versionDataList.size(); i++) {
						LLog.log("i=" + i + "--version:" + versionDataList.get(i).getVersion());
					}
				}

				return versionDataList;

			} catch (Exception e1) {
				LLog.error("CheckVersionTask---catch (Exception e1):" + e1.getMessage());
				/*Message msg = Message.obtain();
				msg.what = 4011;
				msg.obj = "初始化失败";
				initHandler.sendMessage(msg);*/
				e1.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<VersionData> versionData) {
			super.onPostExecute(versionData);
			if (versionData != null) {
				LLog.log("come in onPostExecute:" + versionData.size());
			}
			
			/*Message msg = Message.obtain();
			msg.what = 4010;
			msg.obj = "初始化成功";
			initHandler.sendMessage(msg);*/
			
			updateVersion(versionData);
		}

	}

	private class UpdateVersionTask extends AsyncTask<String, Void, Boolean> {
		private VersionData versionData;

		public UpdateVersionTask(VersionData versionData) {
			this.versionData = versionData;
		}

		@Override
		protected Boolean doInBackground(String... arg0) {
			try {
				LLog.log("versionData.getUpdateSdkUrl():" + versionData.getUpdateSdkUrl());
				URL url = new URL(versionData.getUpdateSdkUrl());
				LLog.log("UpdateVersionTask---0");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				LLog.log("UpdateVersionTask---1");
				conn.setReadTimeout(3000);
				conn.setConnectTimeout(3000);
				LLog.log("UpdateVersionTask---2");
				conn.connect();
				LLog.log("UpdateVersionTask---3");
				InputStream in = conn.getInputStream();
				LLog.log("UpdateVersionTask---4");
				File file = new File(getLXDexFilePath(versionData.getSectionDexPathName()) + "_temp");
				FileOutputStream ops = new FileOutputStream(file.getAbsolutePath());
				int len = 0;
				byte[] b = new byte[255];
				while ((len = in.read(b)) != -1) {
					ops.write(b, 0, len);
				}
				ops.close();
				in.close();
				return true;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
				LLog.error("e.getMessage():" + e.getMessage());
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result) {
				String lxDexFilePath = getLXDexFilePath(versionData.getSectionDexPathName());
				File tempFile = new File(lxDexFilePath + "_temp");
				File file = new File(lxDexFilePath);
				try {
					FileInputStream in = new FileInputStream(tempFile);
					FileOutputStream ops = new FileOutputStream(file.getAbsolutePath());
					int len = 0;
					byte[] b = new byte[255];
					while ((len = in.read(b)) != -1) {
						ops.write(b, 0, len);
					}
					ops.close();
					in.close();
					updateVersionSuccess(versionData);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				LLog.log("更新失败");
				init(versionData);
			}
		}

		public void updateVersionSuccess(VersionData versionData) {
			Log.e("test", "come in updateVersionSuccess");
			sp = getApplication().getSharedPreferences(versionData.getXmlName(), Context.MODE_PRIVATE);
			sp.edit().putString("version", versionData.getVersion()).commit();
			// TODO 将版本号写入/data/data/包名/files/ep.txt里面格式xmlName:version
			// originStr ep.txt文件中的内容 Util.getResultStr第二个参数的格式：xxx：yyy,
			// 其中xxx为包名 yyy为包的版本好
			String originStr = Util.getInputString(file);

			String resultStr = Util.getResultStr(originStr,
					versionData.getXmlName() + ":" + versionData.getVersion() + ",");
			// resultStr:得到最新的ep.txt文件的内容，
			Util.getOutPutString(file, resultStr); // 将resultStr 写入到ep.txt文件内

			// TODO http请求将下载成功的数据回传到服务端，写入日志数据库内(Dexbean对象)
			uploadDataList.clear();
			uploadDataList.add(new PackageData(versionData.getXmlName(), versionData.getVersion()));

			//backResultToServer();
			Log.e("test", "updateVersionSuccess--initFlag=" + versionData.getInitFlag());
			init(versionData);
		}

	}

	private class BackResultToServerTask extends AsyncTask<Void, String, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			LLog.log("BackResultToServerTask--tempResult:" + resultUrl);
			try {
				String encoderJson = URLEncoder.encode(jsonUploadDataParamStr, HTTP.UTF_8);
				LLog.log("url:" + resultUrl);
				HttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(resultUrl);
				httpPost.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
				StringEntity se = new StringEntity(encoderJson);
				se.setContentType(CONTENT_TYPE_TEXT_JSON);
				se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON));
				httpPost.setEntity(se);
				LLog.log("BackResultToServerTask---doInBackground---0.0");
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
				String result = ops.toString("UTF-8");
				if (result == null) {
					return null;
				}
				LLog.log("BackResultToServerTask--result:" + result);

			} catch (Exception e1) {
				e1.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			// updateVersion(version);
		}

	}

	private void backResultToServer() {

		DexBean dexBean = new DexBean();
		dexBean = Util.getDexBean(this, uploadDataList);
		jsonUploadDataParamStr = Util.parseObjectToJsonString(dexBean, uploadDataList);
		LLog.log("jsonUploadDataParamStr:" + jsonUploadDataParamStr);

		BackResultToServerTask backResultToServerTask = new BackResultToServerTask();
		backResultToServerTask.execute();
	}
	
	/*public Handler initHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(android.os.Message msg) {
			Intent intent = new Intent(getPackageName()
					+ ".my.init.listener");
			
			intent.putExtra("msg.what", msg.what);
			intent.putExtra("msg.obj", msg.obj + "");
			
			Log.e("test", "initHandler--msg.what:"+msg.what+"--msg.obj:"+msg.obj);
			
			sendBroadcast(intent);
		};
	};*/
}
