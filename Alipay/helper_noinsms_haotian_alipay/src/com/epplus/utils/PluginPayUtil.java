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
 * ����֧��������
 * @author zgt
 *
 */
public class PluginPayUtil implements Runnable,Callback {
	
	
	//public static final String LOG_TAG = "PayDemo";
	private ProgressDialog mLoadingDialog = null;
	private final String mMode = "00";
	
	private Activity context;
	private Handler mHandler = null;
	 
	 
	 
	// �̻����룬��ĳ��Լ�������̻��Ż���open��ע�������777�̻��Ų���
	private final static String merId = "898440379930020";
	
		
	public final static String SERVER_UNIONSDK ="http://unionpay-server.n8wan.com:29141/form05_6_2_Consume";//�������Ե�ַ
		
	private String TN_URL_00;
	
	
	private PluginHandler pluginHandler;
	public PluginPayUtil(Activity context,PluginHandler pluginHandler) {
		this.context=context;
		mHandler = new Handler(this);
		this.pluginHandler = pluginHandler;
		
	}
	
	/**
	 * ����SDK֧��
	 * @param subject ��Ʒ����
	 * @param body  ��Ʒ����
	 * @param price ��Ʒ�۸�  ��λ��
	 */
	public  void pay(String price)  {
		 // Log.e(LOG_TAG, " " + v.getTag());
          //mGoodsIdx = (Integer) v.getTag();
          mLoadingDialog = ProgressDialog.show(context, // context
                  "", // title
                  "Ŭ��������,���Ժ�...", // message
                  true); // �����Ƿ��ǲ�ȷ���ģ���ֻ�ʹ����������й�
          
          this.TN_URL_00 = getPluginPayUrl(price);

          /*************************************************
           * ����1�������翪ʼ,��ȡ������ˮ�ż�TN
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
            builder.setTitle("������ʾ");
            builder.setMessage("��������ʧ��,������!");
            builder.setNegativeButton("ȷ��",
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
             * ����2��ͨ����������������֧�����
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
	 * ֧���������
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	public  void onActivityResult(int requestCode, int resultCode, Intent data) {
	        /*************************************************
	         * ����3�����������ֻ�֧���ؼ����ص�֧�����
	         ************************************************/
	        if (data == null) {
	            return;
	        }

	        String msg = "";
	        /*
	         * ֧���ؼ������ַ���:success��fail��cancel �ֱ����֧���ɹ���֧��ʧ�ܣ�֧��ȡ��
	         */
	        String str = data.getExtras().getString("pay_result");
	        if (str.equalsIgnoreCase("success")) {
	            // ֧���ɹ���extra���������result_data��ȡ��У��
	            // result_data�ṹ��c��result_data����˵��
	            if (data.hasExtra("result_data")) {
	                String result = data.getExtras().getString("result_data");
	                try {
	                    JSONObject resultJson = new JSONObject(result);
	                    String sign = resultJson.getString("sign");
	                    String dataOrg = resultJson.getString("data");
	                    // ��ǩ֤��ͬ��̨��ǩ֤��
	                    // �˴���verify���̻�����ȥ�̻���̨����ǩ
	                    boolean ret = RSAUtil.verify(dataOrg, sign, mMode);
	                    if (ret) {
	                        // ��֤ͨ������ʾ֧�����
	                        msg = "֧���ɹ���";
	                        this.pluginHandler.pluginPaySuccess(msg, "");
	                    } else {
	                        // ��֤��ͨ����Ĵ���
	                        // ����ͨ���̻���̨��ѯ֧�����
	                        msg = "֧��ʧ�ܣ�";
	                        this.pluginHandler.pluginPayFailed(msg, "");
	                    }
	                } catch (JSONException e) {
	                }
	            } else {
	                // δ�յ�ǩ����Ϣ
	                // ����ͨ���̻���̨��ѯ֧�����
	                msg = "֧���ɹ���";
	                this.pluginHandler.pluginPaySuccess(msg, "");
	            }
	        } else if (str.equalsIgnoreCase("fail")) {
	            msg = "֧��ʧ�ܣ�";
	            this.pluginHandler.pluginPayFailed(msg, "");
	        } else if (str.equalsIgnoreCase("cancel")) {
	            msg = "�û�ȡ����֧��";
	            this.pluginHandler.pluginPayFailed(msg, "");
	        }

	    }
	
	/**
	 * ��ȡ֧���� url
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
	 * get the out_trade_no for an order. �����̻������ţ���ֵ���̻���Ӧ����Ψһ�����Զ����ʽ�淶��
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
