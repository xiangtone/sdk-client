package com.epplus.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import com.unionpay.UPPayAssistEx;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler.Callback;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 银联支付工具类
 * @author zgt
 *
 */
public class PluginPayUtil implements Runnable,Callback {
	
	
	//public static final String LOG_TAG = "PayDemo";
	private ProgressDialog mLoadingDialog = null;
	private final String mMode = "00";
	
	private Activity context;
	private Handler mHandler = null;
	 
	 
	 
	// 商户号码，请改成自己申请的商户号或者open上注册得来的777商户号测试
	private final static String merId = "898440379930020";
	
		
	public final static String SERVER_UNIONSDK = URLUtils.getUnionTn();//"http://unionpay-server.n8wan.com:29141/form05_6_2_Consume";//内网测试地址
	//public final static String SERVER_UNIONSDK ="http://192.168.1.124:8080/ACPSample_KongjianServer/form05_6_2_Consume";//内网测试地址
		
	
	
	private String price;
	
	
	private PluginHandler pluginHandler;
	public PluginPayUtil(Activity context,PluginHandler pluginHandler) {
		this.context=context;
		mHandler = new Handler(this);
		this.pluginHandler = pluginHandler;
		
	}
	
	/**
	 * 调用SDK支付
	 * @param subject 商品名称
	 * @param body  商品描述
	 * @param price 商品价格  单位分
	 */
	public  void pay(String price)  {
		 // Log.e(LOG_TAG, " " + v.getTag());
          //mGoodsIdx = (Integer) v.getTag();
		this.price=price;
          mLoadingDialog = ProgressDialog.show(context, // context
                  "", // title
                  "努力加载中,请稍候...", // message
                  true); // 进度是否是不确定的，这只和创建进度条有关

          /*************************************************
           * 步骤1：从网络开始,获取交易流水号即TN
           ************************************************/
          new Thread(this).start();
	}
	

	@SuppressLint("SimpleDateFormat") @Override
	public void run() {
//		 String tn = null;
//	        InputStream is;
//	        try {
//	            String url = TN_URL_00;
//
//	            URL myURL = new URL(url);
//	            URLConnection ucon = myURL.openConnection();
//	            ucon.setConnectTimeout(120000);
//	            is = ucon.getInputStream();
//	            int i = -1;
//	            
//	            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//	            while ((i = is.read()) != -1) {
//	                baos.write(i);
//	            }
//	           
//	            tn = baos.toString();
//	            
//	            Log.e("zgt", "连接上"+tn);
//	            
//	            is.close();
//	            baos.close();
//	        } catch (Exception e) {
//	            e.printStackTrace();
//	            Log.e("zgt", e.toString());
//	        }
	        
	        
	        
//	        String time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
//			
//			String TN_URL_01 = SERVER_UNIONSDK+ "?&txnTime="+ time
//						+ "&merId=" + merId + "&orderId=" + getOutTradeNo() + "&txnAmt=" + price;
	        
	        String time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	        Map<String, String> map = new HashMap<String, String>();
	        map.put("txnTime", time);
	        map.put("merId", merId);
	        map.put("orderId",  getOutTradeNo());
	        map.put("txnAmt", price);
	        
	        //自定义数据
	        map.put(ConfigUtils.xx_notifyData, ConfigUtils.getNotifyJsonData(context,ConfigUtils.PLUGIN));
	        
	        
 	        String tn = HttpUtils.post(SERVER_UNIONSDK, map);
	        
	        

	        Message msg = mHandler.obtainMessage();
	        msg.obj = tn;
	        mHandler.sendMessage(msg);
	}

	@Override
	public boolean handleMessage(Message msg) {

       // Log.e(LOG_TAG, " " + "" + msg.obj);
        if (mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }

        String tn = "";
        if (msg.obj == null || ((String) msg.obj).length() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("错误提示");
            builder.setMessage("网络连接失败,请重试!");
            builder.setNegativeButton("确定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.create().show();
        } else {
            tn = (String) msg.obj;
            /*************************************************
             * 步骤2：通过银联工具类启动支付插件
             ************************************************/
            doStartUnionPayPlugin(context, tn, mMode);
        }

        return false;
    
	}
	
	
	 public  void doStartUnionPayPlugin(Activity activity, String tn,
	            String mode){
		 UPPayAssistEx.startPay(activity, null, null, tn, mode);
	 }
	
	
	
	/**
	 * 支付结果处理
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	public  void onActivityResult(int requestCode, int resultCode, Intent data) {
	        /*************************************************
	         * 步骤3：处理银联手机支付控件返回的支付结果
	         ************************************************/
			if(requestCode!=10){
				return ;
			}
	        if (data == null) {
	            return;
	        }

	        String msg = "";
	        /*
	         * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
	         */
	        String str = data.getExtras().getString("pay_result");
	        if (str.equalsIgnoreCase("success")) {
	            // 支付成功后，extra中如果存在result_data，取出校验
	            msg = "支付成功！";
                this.pluginHandler.pluginPaySuccess(msg, "");
	            
	        } else if (str.equalsIgnoreCase("fail")) {
	            msg = "支付失败！";
	            this.pluginHandler.pluginPayFailed(msg, "");
	        } else if (str.equalsIgnoreCase("cancel")) {
	            msg = "用户取消了支付";
	            this.pluginHandler.pluginPayCancel(msg, "");
	        }

	    }
	
	
	/**
	 * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
	 */
	@SuppressLint("SimpleDateFormat") 
	private  String getOutTradeNo() {
		String key = new SimpleDateFormat("yyyyMMddHHmmss",Locale.getDefault()).format(new Date()) ;		
		Random random = new Random();
		int n = random.nextInt(10000);
		String order =String.valueOf(n)+key;
		return order;  
	}
	
	
	public static abstract class PluginHandler{
		public abstract void pluginPaySuccess(String resultInfo,String resultStatus);
		public abstract void pluginPayFailed(String resultInfo,String resultStatus);
		public  abstract void pluginPayCancel(String resultInfo,String resultStatus);
	}

}
