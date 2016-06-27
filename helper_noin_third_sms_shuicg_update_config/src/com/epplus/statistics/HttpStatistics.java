package com.epplus.statistics;

import java.util.ArrayList;
import java.util.HashMap;
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

import com.epplus.utils.ConfigUtils;
import com.epplus.utils.DeviceUtil;
import com.epplus.utils.HttpUtils;
import com.epplus.utils.LogUtils;
import com.epplus.utils.SDKUtils;
import com.epplus.utils.SDKVersion;
import com.epplus.utils.URLUtils;
import com.epplus.view.PayParams;
import com.epplus.view.ShowFlag;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

/**
 * ͳ��
 * @author zgt
 *
 */
public class HttpStatistics {
	
	//public static final String BASEURL = "";
	
	private static HttpStatistics httpStatistics ;
	
	private  static HttpStatistics newInstance(){
	      if(httpStatistics == null){
	    	  httpStatistics = new HttpStatistics();
	      }
		return httpStatistics;
	}
	
	private HttpStatistics() {
		
	}
	
	
	/**
	 * ��������
	 * @param context
	 * @return
	 */
	public static StatisticsBean getStatisticsBean(Context context) {
		DeviceUtil deviceUtil = new DeviceUtil(context);
		StatisticsBean bean = new StatisticsBean();
		bean.setAppVersion(deviceUtil.getAppVersion());
		bean.setImei(deviceUtil.getImei());
		bean.setImsi(deviceUtil.getImsi());
		bean.setMac(deviceUtil.getMac());
		bean.setModel(deviceUtil.getPhoneModel());
		bean.setNetType(deviceUtil.getNetType());
		bean.setPackageName(deviceUtil.getPackageName());
		bean.setPhoneSdkInt(deviceUtil.getPhoneSdkInt());
		bean.setPhoneVersion(deviceUtil.getPhoneVersion());
		bean.setSdkVersion(deviceUtil.getSdkVersion());
		//bean.setUuid(ConfigurationParameter.getUUID(context));
		bean.setFlagId(SDKUtils.getFlagId(context));
		bean.setAppkey(ConfigUtils.getEp_APPKEY(context));
		bean.setChannel(ConfigUtils.getEP_CHANNEL(context));
		return bean;
	}
	
	
	public static HashMap<String, String> getBaseMap(Context context,String userOrderId,int payOperateCode,String gameType,String payParams){
		HashMap<String, String> map = new HashMap<String, String>();
		StatisticsBean bean = getStatisticsBean(context);
		bean.setUserOrderId(userOrderId);
		bean.setGameType(gameType);
		if(TextUtils.isEmpty(payParams)){
			payParams = "";
		}
		bean.setPayParams(payParams);
		String json = JSON.toJsonString(bean);
		LogUtils.e( "json:"+json);
		String encodeData =EncodeUtils.encode(json);
		
		map.put("op_notifyData", encodeData);
		map.put("sdkVersion", SDKVersion.SDK_VERSION);
		map.put("payOperateCode", String.valueOf(payOperateCode));
		return map;
	}
	
	
	/**
	 * post ͳ�� 
	 * @param url
	 * @param map
	 */
	public static void statistics(final String url,final HashMap<String, String> map){
		
		ThreadUtil.start(new Runnable() {
			@Override
			public void run() {
				HttpStatistics.newInstance().post(url, map);
			}
		});
		
	
	}
	
	
	/**
	 * 10  PayGuiShow <br/>
	 * 11  PayGuiCancel<br/>
	 * 201 AlipayClick<br/>
	 * 202 AlipayCancel<br/>
	 * 203 AlipaySuccess<br/>
	 * 204 AlipayFail<br/>
	 * 
	 * 301 UnionpayClick<br/>
	 * 302 UnionpayCancel<br/>
	 * 303 UnionpaySuccess<br/>
	 * 304 UnionpayFail<br/>
	 * 
	 * 401 WeChatPayClick<br/>
	 * 402 WeChatPayCancel<br/>
	 * 403 WeChatpaySuccess<br/>
	 * 404 WeChatpayFail<br/>
	 * 
	 * 501 BaidupayClick<br/>
	 * 502 BaidupayCancel<br/>
	 * 503 BaidupaySuccess<br/>
	 * 504 BaidupayFail<br/>
	 *  post ͳ�� �����������
	 * @param context
	 * @param urid
	 * @param falgCode
	 * @param gameType ��Ϸ����
	 * @param payParams ֧������
	 */
	public static void statistics(final Context context,final String urid,final int falgCode,final String gameType,final PayParams payParams ){
		ThreadUtil.start(new Runnable() {
			@Override
			public void run() {
				String str =urid;
				if(TextUtils.isEmpty(urid)){str = "";}
				String payParamsJson = "";
				if(payParams!=null)payParamsJson = JSON.toJsonString(payParams);
//				if(ShowFlag.danji.equals(gameType)){
//					payParamsJson = "";
//				}else if (ShowFlag.wangyou.equals(gameType)) {
//					if(payParams!=null)payParamsJson = JSON.toJsonString(payParams);
//				}
				//HttpStatistics.newInstance().post(StatisURL.BASEURL, getBaseMap(context,str,falgCode,gameType,payParamsJson));
				LogUtils.e(falgCode+">>>");
				HttpStatistics.newInstance().post(URLUtils.payStatis(), getBaseMap(context,str,falgCode,gameType,payParamsJson));
			}
		});
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
