package com.epplus.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

/**
 * ��ȡ ���� ��Ϣ
 * @author zgt
 *
 */
public class ConfigUtils {
	
	
	
	public static final String xx_notifyData = "xx_notifyData";
	
	/**֧���� ƽ̨*/
	public static final String ALIPAY = "alipay";
	/**���� ƽ̨*/
	public static final String PLUGIN = "unionpay";
	/**΢�� ƽ̨*/
	public static final String WX = "wx";
	
	
	public static String getNotifyJsonData(Context context,String platform){
		 String json = "{\"channel\":\""+ConfigUtils.getEP_CHANNEL(context)+"\",\"appkey\":\""+ConfigUtils.getEp_APPKEY(context)+"\",\"platform\":\""+platform+"\"}";
		 return json;
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
     * ��ȡapplication��ָ����meta-data
     * @return ���û�л�ȡ�ɹ�(û�ж�Ӧֵ�������쳣)���򷵻�ֵΪ��
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
	

}
