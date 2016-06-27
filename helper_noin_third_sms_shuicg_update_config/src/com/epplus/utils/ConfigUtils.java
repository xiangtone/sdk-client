package com.epplus.utils;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.epplus.view.ShowFlag;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

/**
 * 读取 配置 信息
 * @author zgt
 *
 */
public class ConfigUtils {
	
	
	
	public static final String xx_notifyData = "xx_notifyData";
	
	/**支付宝 平台*/
	public static final String ALIPAY = "alipay";
	/**银联 平台*/
	public static final String PLUGIN = "unionpay";
	/**微信 平台*/
	public static final String WX = "wx";
	/**百度平台*/
	public static final String BAIDU = "baidu";
	/**
	 * 微信WAp 平台
	 */
	public static final String WXWAP = "wxWap";
	
	
	/**
	 * 回调的数据
	 * @param context
	 * @param platform
	 * @param OrderIdSelf
	 * @param OrderIdCp
	 * @return
	 * 
	 * {"channel":"10010","appkey":"f17d2fb4eff547c8bebc1e7cc4dcd43c","platform":"wx","OrderIdSelf":"1461902020543","OrderIdCp":"12345"}
		a = channel
		k = appkey
		p =platform
		s = OrderIdSelf
		c = OrderIdCp
	 * 
	 */
	public static String getNotifyJsonData(Context context,String platform,String OrderIdSelf,String OrderIdCp){
		if(ShowFlag.gameType.equals(ShowFlag.danji)){
			OrderIdSelf = "";
		}
		JSONObject obj = new JSONObject();
		try {
			obj.put("a", ConfigUtils.getEP_CHANNEL(context));
			obj.put("k", ConfigUtils.getEp_APPKEY(context));
			obj.put("p",platform);
			if(!TextUtils.isEmpty(OrderIdSelf)){
				obj.put("s",OrderIdSelf);
			}
			if(!TextUtils.isEmpty(OrderIdCp)){
				obj.put("c",OrderIdCp);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		String json = obj.toString();
		LogUtils.e("send_Json: "+json);
		//String json = "{\"channel\":\""+ConfigUtils.getEP_CHANNEL(context)+"\",\"appkey\":\""+ConfigUtils.getEp_APPKEY(context)+"\",\"platform\":\""+platform+"\"}";
		return json;
	}
	
	
	/**
	 * 百度回调地址参数 由于回掉地址参与签名的问题必要这样传递参数
	 * @param context
	 * @param platform
	 * @return
	 * 
	 *  a = channel
		k = appkey
		p =platform
		s = OrderIdSelf
		c = OrderIdCp
	 */
	public static String getNotifyBaiduPramData(Context context,String OrderIdSelf,String OrderIdCp){
		String strSelf="";
		String srtCp="";
		if(ShowFlag.gameType.equals(ShowFlag.danji)){
			strSelf = "";
		}else {
			if(!TextUtils.isEmpty(OrderIdSelf)){
				strSelf = "-s:"+OrderIdSelf;
			}
		}
		
		if(!TextUtils.isEmpty(OrderIdCp)){
			srtCp = "-c:"+OrderIdCp;
		}
		
		String pram = "?xx_notifyData=a:"+ConfigUtils.getEP_CHANNEL(context)
				       +"-k:"+ConfigUtils.getEp_APPKEY(context)
				       +"-p:"+BAIDU
				       +strSelf
				       +srtCp;
		LogUtils.e("baidu_send_Json: "+pram);
		return pram;
	}
	
	
	/**
	 * EP_CHANNEL
	 * @param context
	 * @return
	 */
	public static String getEP_CHANNEL(Context context){
		return getAppMetaData(context, "EP_CHANNEL");
	}
	
	/**
	 * EP_APPKEY
	 * @param context
	 * @return
	 */
	public static String getEp_APPKEY(Context context){
		return getAppMetaData(context, "EP_APPKEY");
	}
	
	
	
	
	  /**
     * 获取application中指定的meta-data
     * @return 如果没有获取成功(没有对应值，或者异常)，则返回值为空
     */
    public static String getAppMetaData(Context ctx, String key) {
        if (ctx == null || TextUtils.isEmpty(key)) {
            return null;
        }
        String resultData = null;
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        resultData = applicationInfo.metaData.getString(key);
                        if(TextUtils.isEmpty(resultData)){
                        		resultData = applicationInfo.metaData.getInt(key)+"";
                        }
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return resultData;
    }
    
    /**
     * 支付渠道的配置key
     */
    public static final String PAY_CHANNEL = "show_pay_channel";
    
    /**
     * 显示支付渠道为 error
     */
    public static final String SHOWPAYERROR = "-1";
    
//    /**
//     * 支付渠道 url
//     */
    //public static final String SHOW_PAY_CHANNEL_URL = "http://192.168.0.101:8080/thirdpay-webhook/CpInfoServlet";
    //public static final String SHOW_PAY_CHANNEL_URL = "http://thirdpay-webhook.n8wan.com:29141/CpInfoServlet";
  
    
    //配置显示的支付渠道
    /**
     * 获取支付渠道
     * @param context
     * @return
     */
    public static String getShowPayChannel(Context context){
		return PreferencesUtils.getString(context, PAY_CHANNEL, null);
    }
    
    /**
     * 初始化配置是否显示的支付渠道
     * @param c
     */
    public static void setShowPayChannel(final Context c){
		setShowPayChannel(c,new IHttpResult() {
			@Override
			public void result(Object obj) {
				if(obj==null){
					return;
				}
				String json = (String) obj;
				if(!TextUtils.isEmpty(json)){
					PreferencesUtils.putString(c, PAY_CHANNEL, json);
				}
				LogUtils.e( "ShowPay : "+json);
				
			}
		});
    }
    
    public static void setShowPayChannel(Context c,IHttpResult result){
    	String appkey=ConfigUtils.getEp_APPKEY(c);
    	if(TextUtils.isEmpty(appkey))return;
    	String uri = URLUtils.payChannle();//SHOW_PAY_CHANNEL_URL;
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("Appkey",appkey );
		
		LogUtils.e("url:"+uri+">>Appkey:"+appkey);
		
		HttpUtils.asyPost(uri, map, result);
    }
    
    
    
    
    
    
    
    
    
    
    
    
	

}
