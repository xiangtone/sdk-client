package comwap.pay.statistics;

import java.util.HashMap;

import org.apache.http.util.TextUtils;

import com.alibaba.fastjson.JSONObject;

import comwap.pay.utils.ConfigUtils;
import comwap.pay.utils.HttpUtils;

/**
 * @author zgt
 */
public class HttpStatistics {
	
	private HttpStatistics() {
		
	}
	
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	public static StatisticsBean getStatisticsBean() {
//		DeviceUtil deviceUtil = new DeviceUtil(context);
//		StatisticsBean bean = new StatisticsBean();
//		bean.setAppVersion(deviceUtil.getAppVersion());
//		bean.setImei(deviceUtil.getImei());
//		bean.setImsi(deviceUtil.getImsi());
//		bean.setMac(deviceUtil.getMac());
//		bean.setModel(deviceUtil.getPhoneModel());
//		bean.setNetType(deviceUtil.getNetType());
//		bean.setPackageName(deviceUtil.getPackageName());
//		bean.setPhoneSdkInt(deviceUtil.getPhoneSdkInt());
//		bean.setPhoneVersion(deviceUtil.getPhoneVersion());
//		bean.setSdkVersion(deviceUtil.getSdkVersion());
//		//bean.setUuid(ConfigurationParameter.getUUID(context));
//		bean.setFlagId(SDKUtils.getFlagId(context));
//		bean.setAppkey(ConfigUtils.getEp_APPKEY(context));
//		bean.setChannel(ConfigUtils.getEP_CHANNEL(context));
		
		StatisticsBean bean = new StatisticsBean();
		return bean;
	}
	
	
	private  static HashMap<String, String> getBaseMap(String appkey,String channel,String cpOrderId,int payOperateCode,String gameType,String payParams){
		HashMap<String, String> map = new HashMap<String, String>();
		StatisticsBean bean = getStatisticsBean();
		bean.setUserOrderId(cpOrderId);
		bean.setAppkey(appkey);
		bean.setChannel(channel);
		bean.setGameType(gameType);
		if(TextUtils.isEmpty(payParams)){
			payParams = "";
		}
		bean.setPayParams(payParams);
		
		String json = JSONObject.toJSONString(bean);
		
		String encodeData =EncodeUtils.encode(json);
		
		map.put("op_notifyData", encodeData);
		map.put("sdkVersion", SDKVersion.SDK_VERSION);
		map.put("payOperateCode", String.valueOf(payOperateCode));
		return map;
	}
	
	
//	/**
//	 * post 
//	 * @param url
//	 * @param map
//	 */
//	public static void statistics(final String url,final HashMap<String, String> map){
//		
//		HttpUtils.post(url, map);
//		
//	
//	}
	
	
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
	 * 
	 * 
	 * // wapH5
		parm.put("801", "WxH5Click");
		parm.put("802", "WxH5Cancel");
		parm.put("803", "WxH5Success");
		parm.put("804", "WxH5Fail");

	 * 
	 *  post 
	 * @param context
	 * @param urid
	 * @param falgCode
	 * @param gameType 
	 * @param payParams
	 */
	public static void statistics(String appkey,String channel,final int falgCode,final String gameType,final PayParams payParams ){
		payParams.setPayChannel(channel);
		payParams.setAppKey(appkey);
		String cpOrderId = payParams.getCpOrderId();
		String payParamsJson = JSONObject.toJSONString(payParams);
		
		String url = ConfigUtils.getStatisticsurl();
		//String url = "http://192.168.0.101:8080/thirdpay-webhook/PayOperateCountServlet";
		HttpUtils.post(url, getBaseMap(appkey,channel,cpOrderId,falgCode,gameType,payParamsJson));
	} 
	

	
	
	
	
	

}
