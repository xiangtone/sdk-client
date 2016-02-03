package com.core_sur.notifierad;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.core_sur.tools.DownloadInfo;
import com.core_sur.tools.Downloader;
import com.core_sur.tools.HttpUtils;
import com.core_sur.tools.PointInfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

/**
 * http请求
 * 
 * @author Administrator
 * 
 */
public class HttpRequest {

	/** 系统的线程池 */
	private ThreadPoolExecutor executorService;

	public final static int POOLNUM = 10;
	
	private final int GETREQ = 20;
	private final int POSTREQ = 21;
	private final int DOWNREQ = 1;
	
	
   
	
	public static HttpRequest newInstance(Context context){
			HttpRequest  httpRequest = new HttpRequest(context);
		return httpRequest;
	}
	
	
	private HashMap<String, HttpRequest.Callback> hashMap;
	

	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case GETREQ:
			case POSTREQ:
				getHandlerResponse(msg);
				break;
			

			default:
				break;
			}
		}
	};
	
	
	
	private void getHandlerResponse(android.os.Message msg) {
		Special special = (Special) msg.obj;
		HttpRequest.Callback callback =  hashMap.get(special.getUrl());
		String json = (String) special.getObj();
		if(TextUtils.isEmpty(json)){
			callback.onFail();
		}else {
			callback.onSuccess(json);
		}
		hashMap.remove(special.getUrl());
		
	};
	

	private Context context;
	
	private  HttpRequest(Context context) {
		this.context=context;
		init(POOLNUM);
	}

	private void init(int poolNum) {
		hashMap = new HashMap<String, HttpRequest.Callback>();
		if (executorService == null) {
			executorService = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		}
	}

	private void addTask(Runnable runnable) {
		executorService.submit(runnable);
	}

	public void get(final String uri, final Callback callback) {

		addTask(new Runnable() {
			@Override
			public void run() {
				String json = HttpUtils.get(context, uri);
				hashMap.put(uri, callback);
				Message msg = handler.obtainMessage();
				msg.what = GETREQ;
				Special special = new Special();
				special.setObj(json);
				special.setUrl(uri);
				msg.obj = special;
				handler.sendMessage(msg);
			}
		});
	}
	
	public void post(final String uri,final Map<String, String> map, final Callback callback){
		addTask(new Runnable() {
			@Override
			public void run() {
				String json =post(uri, map);
				hashMap.put(uri, callback);
				Message msg = handler.obtainMessage();
				msg.what = POSTREQ;
				Special special = new Special();
				special.setObj(json);
				special.setUrl(uri);
				msg.obj = special;
				handler.sendMessage(msg);
			}
		});
	}
	
	
    public void downloadApk(final PointInfo pointInfo,final File file,final Callback callback){
		
		final Handler downHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				callback.downloadSuccess(msg.arg1, msg.arg2, (DownloadInfo)msg.obj);
			}
		};
		
		addTask(new Runnable() {
			@Override
			public void run() {
				Downloader downloader = new Downloader(pointInfo, file,context, downHandler);
				downloader.download();
				
			}
		});
	}
	
	
	
	
    
    
  
    
	

	public static abstract  class Callback {
		/**
		 * 获取数据成功
		 * @param json
		 */
		public  void  onSuccess(String json){
			
		}
		/**
		 * 请求失败
		 */
		public void onFail() {
			
		}
		/***
		 * 只用于下载
		 * @param json
		 */
		public  void  downloadSuccess(int  complete,int fileSize,DownloadInfo downloadInfo){
			
		}
		
	}
	
	
	
	private class Special{
		private String url;
		private Object obj;
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public Object getObj() {
			return obj;
		}
		public void setObj(Object obj) {
			this.obj = obj;
		}
		
	}
	
	
	
	
	
	
	
	
	//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	
	
	
	/**
	 * post请求
	 * @param uri
	 * @param map
	 * @return
	 */
	private  String post(String uri,Map<String, String> map)  {
		//1.创建 HttpClient 的实例
		HttpClient client = new DefaultHttpClient();
		//2. 创建某种连接方法的实例，在这里是HttpPost。在 HttpPost 的构造函数中传入待连接的地址
		HttpPost httpPost = new HttpPost(uri);
		try {
			//封装传递参数的集合
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			//往这个集合中添加你要传递的参数
			Iterator<Entry<String, String>> iter = map.entrySet().iterator();
			while(iter.hasNext()){
				Map.Entry<String, String> entry = iter.next();
				String key = entry.getKey(); 
				String value = entry.getValue();
				parameters.add(new BasicNameValuePair(key, value));
			}
			//创建传递参数封装 实体对象
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters, "UTF-8");//设置传递参数的编码
			//把实体对象存入到httpPost对象中
			httpPost.setEntity(entity);
			//3. 调用第一步中创建好的实例的 execute 方法来执行第二步中创建好的 method 实例
			HttpResponse response = client.execute(httpPost); //HttpUriRequest的后代对象 //在浏览器中敲一下回车
			//4. 读 response
			if(response.getStatusLine().getStatusCode()==200){//判断状态码
				String result = EntityUtils.toString(response.getEntity());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			//6. 释放连接。无论执行方法是否成功，都必须释放连接
			client.getConnectionManager().shutdown();
		}	
		return null;
	}
	
	
	
	
	
	
	
	/**
	 * 通过httpClient中的GET方式处理的
	 * @param userName
	 * @param userPass
	 */
	private  String get(String uri){
		// 1.创建 HttpClient 的实例 打开一个浏览器
		HttpClient client = new DefaultHttpClient(); // DefaultHttpClient extends AbstractHttpClient
		try {
			HttpGet httpGet = new HttpGet(uri);
			// 3. 调用第一步中创建好的实例的 execute 方法来执行第二步中创建好的 method 实例
			HttpResponse response = client.execute(httpGet); // 在浏览器中敲了一下回车
			// 4. 读 response
			int statusCode = response.getStatusLine()
					.getStatusCode();// 读取状态行中的状态码
			if (statusCode == 200) { //如果等于200 一切ok
				String result = EntityUtils.toString(response.getEntity());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			// 5.释放连接。无论执行方法是否成功，都必须释放连接
			client.getConnectionManager().shutdown();// 释放链接
		}
		
		return null;
	}
	
	
	
	
	
	
	
	
	

}
