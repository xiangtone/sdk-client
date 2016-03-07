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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * ͳ��
 * @author zgt
 *
 */
public class HttpStatistics {
	
	public static final String BASEURL = "";
	
	private static HttpStatistics httpStatistics ;
	
	public static HttpStatistics newInstance(){
	      if(httpStatistics == null){
	    	  httpStatistics = new HttpStatistics();
	      }
		return httpStatistics;
	}
	
	private HttpStatistics() {
		
	}
	
	/**
	 * ͳ��
	 * @param url
	 */
	public void statistics(final String url){
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				get(url);
//			}
//		}).start();
	}
	

	
	
	/**
	 * post����
	 * @param uri
	 * @param map
	 * @return
	 */
	private  String post(String uri,Map<String, String> map)  {
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
	
	
	
	
	
	
	
	/**
	 * ͨ��httpClient�е�GET��ʽ�����
	 * @param userName
	 * @param userPass
	 */
	private  String get(String uri){
		// 1.���� HttpClient ��ʵ�� ��һ�������
		HttpClient client = new DefaultHttpClient(); // DefaultHttpClient extends AbstractHttpClient
		try {
			HttpGet httpGet = new HttpGet(uri);
			
			// 3. ���õ�һ���д����õ�ʵ���� execute ������ִ�еڶ����д����õ� method ʵ��
			HttpResponse response = client.execute(httpGet); // �������������һ�»س�
			// 4. �� response
			int statusCode = response.getStatusLine()
					.getStatusCode();// ��ȡ״̬���е�״̬��
			if (statusCode == 200) { //�������200 һ��ok
				String result = EntityUtils.toString(response.getEntity());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			// 5.�ͷ����ӡ�����ִ�з����Ƿ�ɹ����������ͷ�����
			client.getConnectionManager().shutdown();// �ͷ�����
		}
		
		return null;
	}
	
	
	

}
