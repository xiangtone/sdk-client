package com.epplus.utils;

import java.util.Date;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

/**
 * 微信wap支付
 * @author zgt
 *
 */
public class WXWapPayUtil {
	
	private final int REQUESTCODE = 1232;
	
	@SuppressLint("HandlerLeak") 
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			sendWixin();
		}
    };
    
    private String _https;
    private String _weixin;
	
    private Activity context;
    
    //次数
    private int times = 0;
    //总次数
    private int timeCount = 1;
    
    private WxWapHandler wapHandler;
    
    private String xx_notifyData;
    
	public WXWapPayUtil(Activity context,String OrderIdSelf,String OrderIdCp,WxWapHandler wapHandler){
		this.context=context;
		this.wapHandler=wapHandler;
		xx_notifyData = ConfigUtils.getNotifyJsonData(context, ConfigUtils.WXWAP, OrderIdSelf, OrderIdCp);
	}
	
	
	
	/**
	 * 支付成功回调后台
	 */
//	private void paySuccessCall(){
//	
//		HashMap<String, String> map = new HashMap<String, String>();
//		map.put("xx_notifyData", xx_notifyData);
//		map.put("wexinInfo", _weixin);
//		HttpUtils.asyPost(URLUtils.notifyUrlWxWap(), map, new IHttpResult() {
//			
//			@Override
//			public void result(Object obj) {
//				
//				
//			}
//		});
//	}
	
	
	/**
	 * 
	 * @param body 商品名称
	 * @param attach 附加信息
	 * @param price 价格（分）
	 */
	public void pay(final String body, final String attach, final String price){
		
		//String uString = "http://192.168.0.111:8080/native-pay/TestPay2";
		//thirdpay-cs.n8wan.com/WXWapServlet
	//	String uString = "http://192.168.0.111:8080/thirdpay-cs.n8wan.com/WXWapServlet";
		String uString = URLUtils.wxWapStartApp(context);
		LogUtils.e("微信wap:"+uString);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("service", "pay.weixin.wappay");
		map.put("version", "1.0");
		map.put("charset", "UTF-8");
		map.put("sign_type", "MD5");
		map.put("out_trade_no", new Date().getTime()+"");
		map.put("body", body);
		map.put("attach", attach);
		map.put("total_fee", price);
		String ip = getLocalIpAddress(context);
		if(!TextUtils.isEmpty(ip)){
			map.put("mch_create_ip", ip);
		}else {
			map.put("mch_create_ip", "127.0.0.1");
		}
		
		//
		map.put("xx_notifyData", xx_notifyData);
		
		HttpUtils.asyPost(uString, map, new IHttpResult() {
			
			@Override
			public void result(Object obj) {
               if(obj!=null){
            	   try {
            		   LogUtils.e("微信wap--obj.toString():"+obj.toString());
					JSONObject jsonObject = new JSONObject(obj.toString());
					String weixin= jsonObject.getString("wixin");
					final String https= jsonObject.getString("https");
					LogUtils.e("微信wap--obj.toString()--weixin:"+weixin);
					LogUtils.e("微信wap--obj.toString()--https:"+https);
					_https = https;
					_weixin= weixin;
                    Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(weixin));
                    
                    LogUtils.e("微信wap1111");
					context.startActivityForResult(it, REQUESTCODE);
					times = 0;
					
				} catch (JSONException e) {
					e.printStackTrace();
					wapHandler.wxWapFailed("失败", "204");
				}
				}else {
					LogUtils.e("微信wap:"+"obj==null");
					wapHandler.wxWapFailed("失败", "204");
				}
			}
		});
	
	}
	
	
	
	
    private void sendWixin(){
    	LogUtils.e("WXWapPayUtil--sendWixin _https:"+_https);
    	if(TextUtils.isEmpty(_https)){
    		wapHandler.wxWapFailed("支付失败数据为null", "204");
    		return;
    	}
    	HttpUtils.asyPost( _https, new HashMap<String, String>(), new IHttpResult() {
			@Override
			public void result(Object obj) {
				if(obj==null){
					if(times>=timeCount){
						 wapHandler.wxWapFailed("支付失败数据为null", "204");
					}else {
						times++;
						handler.sendEmptyMessage(0);		
					}
				}else {
					try {
						JSONObject object = new JSONObject(obj.toString());
						String msg=object.getString("message");
						String status=object.getString("status");
						LogUtils.e("WXWapPayUtil--info:"+msg+"  status:"+status);
						if("支付成功".equals(msg)){
							    LogUtils.e("WXWapPayUtil--支付成功");
								wapHandler.wxWapSuccess(msg, status);
							//	paySuccessCall();
						}else if ("201".equals(status)) {
							 LogUtils.e("WXWapPayUtil--支付成功");
							 wapHandler.wxWapSuccess(msg, status);
						}else {
							if(times>=timeCount){
								 LogUtils.e("WXWapPayUtil--支付失败");
								 wapHandler.wxWapFailed(msg, status);
							}else {
								times++;
								handler.sendEmptyMessage(0);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
    }
	
	
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		LogUtils.e("WXWapPayUtil--onActivityResult--微信wap支付:"+requestCode+">>"+data);
		if(REQUESTCODE == requestCode){			
			handler.sendEmptyMessage(0);
		}
	}
	
	
	public static abstract class WxWapHandler{
		public abstract void wxWapSuccess(String resultInfo,String resultStatus);
		public abstract void wxWapFailed(String resultInfo,String resultStatus);
	}
	
	
	
	
	
	
	/** 
     * 获取当前ip地址 
     *  
     * @param context 
     * @return 
     */  
    public static String getLocalIpAddress(Context context) {  
        try {  
            WifiManager wifiManager = (WifiManager) context  
                    .getSystemService(Context.WIFI_SERVICE);  
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();  
            int i = wifiInfo.getIpAddress();  
            return int2ip(i);  
        } catch (Exception ex) {  
            return null;
        }  
        // return null;  
    }  
	
    
    /** 
     * 将ip的整数形式转换成ip形式 
     *  
     * @param ipInt 
     * @return 
     */  
    public static String int2ip(int ipInt) {  
        StringBuilder sb = new StringBuilder();  
        sb.append(ipInt & 0xFF).append(".");  
        sb.append((ipInt >> 8) & 0xFF).append(".");  
        sb.append((ipInt >> 16) & 0xFF).append(".");  
        sb.append((ipInt >> 24) & 0xFF);  
        return sb.toString();  
    }  
  
	

}
