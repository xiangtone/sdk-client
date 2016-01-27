package com.epplus.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
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
	
		
	public final static String SERVER_UNIONSDK ="http://unionpay-server.n8wan.com:29141/form05_6_2_Consume";//内网测试地址
		
	private String TN_URL_00;
	
	
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
          mLoadingDialog = ProgressDialog.show(context, // context
                  "", // title
                  "努力加载中,请稍候...", // message
                  true); // 进度是否是不确定的，这只和创建进度条有关
          
          this.TN_URL_00 = getPluginPayUrl(price);

          /*************************************************
           * 步骤1：从网络开始,获取交易流水号即TN
           ************************************************/
          new Thread(this).start();
	}
	

	@Override
	public void run() {
		 String tn = null;
	        InputStream is;
	        try {
	            String url = TN_URL_00;

	            URL myURL = new URL(url);
	            URLConnection ucon = myURL.openConnection();
	            ucon.setConnectTimeout(120000);
	            is = ucon.getInputStream();
	            int i = -1;
	            ByteArrayOutputStream baos = new ByteArrayOutputStream();
	            while ((i = is.read()) != -1) {
	                baos.write(i);
	            }

	            tn = baos.toString();
	            is.close();
	            baos.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

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
	            // result_data结构见c）result_data参数说明
	            if (data.hasExtra("result_data")) {
	                String result = data.getExtras().getString("result_data");
	                try {
	                    JSONObject resultJson = new JSONObject(result);
	                    String sign = resultJson.getString("sign");
	                    String dataOrg = resultJson.getString("data");
	                    // 验签证书同后台验签证书
	                    // 此处的verify，商户需送去商户后台做验签
	                    boolean ret = RSAUtil.verify(dataOrg, sign, mMode);
	                    if (ret) {
	                        // 验证通过后，显示支付结果
	                        msg = "支付成功！";
	                        this.pluginHandler.pluginPaySuccess(msg, "");
	                    } else {
	                        // 验证不通过后的处理
	                        // 建议通过商户后台查询支付结果
	                        msg = "支付失败！";
	                        this.pluginHandler.pluginPayFailed(msg, "");
	                    }
	                } catch (JSONException e) {
	                }
	            } else {
	                // 未收到签名信息
	                // 建议通过商户后台查询支付结果
	                msg = "支付成功！";
	                this.pluginHandler.pluginPaySuccess(msg, "");
	            }
	        } else if (str.equalsIgnoreCase("fail")) {
	            msg = "支付失败！";
	            this.pluginHandler.pluginPayFailed(msg, "");
	        } else if (str.equalsIgnoreCase("cancel")) {
	            msg = "用户取消了支付";
	            this.pluginHandler.pluginPayFailed(msg, "");
	        }

	    }
	
	/**
	 * 获取支付的 url
	 * @return
	 */
	@SuppressLint("SimpleDateFormat") 
	private String getPluginPayUrl(String price){
		String time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		
		String TN_URL_01 = SERVER_UNIONSDK+ "?&txnTime="+ time
					+ "&merId=" + merId + "&orderId=" + getOutTradeNo() + "&txnAmt=" + price;
		return TN_URL_01;
	}
	
	/**
	 * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
	 */
	private  String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
				Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);

		Random r = new Random();
		key = key + r.nextInt();
		key = key.substring(0, 15);
		return key;
	}
	
	
	public static abstract class PluginHandler{
		public abstract void pluginPaySuccess(String resultInfo,String resultStatus);
		public abstract void pluginPayFailed(String resultInfo,String resultStatus);
	}

}
