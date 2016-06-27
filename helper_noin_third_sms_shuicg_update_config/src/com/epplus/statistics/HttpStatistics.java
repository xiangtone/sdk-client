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
 * 统计
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
	 * 基本数据
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
	 * post 统计 
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
	 *  post 统计 带入基础数据
	 * @param context
	 * @param urid
	 * @param falgCode
	 * @param gameType 游戏类型
	 * @param payParams 支付参数
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
