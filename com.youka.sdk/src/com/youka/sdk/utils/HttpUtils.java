package com.youka.sdk.utils;

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
	 * �첽�ص�����
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
	 * post����
	 * @param uri 
	 * @param map
	 * @return
	 */
	public static   String post(String uri,Map<String, String> map)  {
		//1.���� HttpClient ��ʵ��
		HttpClient client = new DefaultHttpClient();
		//2. ����ĳ�����ӷ�����ʵ������������HttpPost���� HttpPost �Ĺ��캯���д�������ӵĵ�ַ
		HttpPost httpPost = new HttpPost(uri);
		try {
			//��װ���ݲ����ļ���
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			//����������������Ҫ���ݵĲ���
			Iterator<Entry<String, String>> iter = map.entrySet().iterator();
			while(iter.hasNext()){
				Map.Entry<String, String> entry = iter.next();
				String key = entry.getKey(); 
				String value = entry.getValue();
				parameters.add(new BasicNameValuePair(key, value));
			}
			//�������ݲ�����װ ʵ�����
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters, "UTF-8");//���ô��ݲ����ı���
			//��ʵ�������뵽httpPost������
			httpPost.setEntity(entity);
			//3. ���õ�һ���д����õ�ʵ���� execute ������ִ�еڶ����д����õ� method ʵ��
			HttpResponse response = client.execute(httpPost); //HttpUriRequest�ĺ������ //�����������һ�»س�
			//4. �� response
			if(response.getStatusLine().getStatusCode()==200){//�ж�״̬��
				String result = EntityUtils.toString(response.getEntity());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			//6. �ͷ����ӡ�����ִ�з����Ƿ�ɹ����������ͷ�����
			client.getConnectionManager().shutdown();
		}	
		return null;
	}
	
	
}
