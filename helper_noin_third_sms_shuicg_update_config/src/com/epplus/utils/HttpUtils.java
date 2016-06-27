package com.epplus.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
/**
 * HttpUtils 
 * @author zgt
 *
 */
public class HttpUtils {

	 
	/**
	 * 异步回调请求
	 * @param uri 
	 * @param map
	 * @param result
	 */
	public static void asyPost(final String uri,final Map<String, String> map,final IHttpResult iResult){
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				return post(uri, map);
			}
			@Override
			protected void onPostExecute(String result) {
				if(iResult!=null)iResult.result(result);
			}
		}.execute();
	}
	
	
	
	/** 
	 * post请求
	 * @param uri 
	 * @param map
	 * @return
	 */
	public static   String post(String uri,Map<String, String> map)  {
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
	
	
}
