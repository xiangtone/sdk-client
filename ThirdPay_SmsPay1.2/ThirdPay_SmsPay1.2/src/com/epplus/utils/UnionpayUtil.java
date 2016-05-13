package com.epplus.utils;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;

//import com.unionpay.UPPayAssistEx;

/**
 * ����֧��������
 * @author zgt
 *
 */
public class UnionpayUtil implements Runnable,Callback {
	
	
	//public static final String LOG_TAG = "PayDemo";
	private ProgressDialog mLoadingDialog = null;
	private final String mMode = "00";
	
	private Activity context;
	private Handler mHandler = null;
	 
	 
	 
	// �̻����룬��ĳ��Լ�������̻��Ż���open��ע�������777�̻��Ų���
	private final static String merId = "898440379930020";
	
		
	private final  String SERVER_UNIONSDK= URLUtils.getUnionTn() ;//="http://unionpay-server.n8wan.com:29141/form05_6_2_Consume";//�������Ե�ַ
	
	private String price;
	
	private PluginHandler pluginHandler;
	private String notifyData;
	public UnionpayUtil(Activity context,String OrderIdSelf,String OrderIdCp,PluginHandler pluginHandler) {
		this.context=context;
		mHandler = new Handler(this);
		this.pluginHandler = pluginHandler;
		
		notifyData = ConfigUtils.getNotifyJsonData(context,ConfigUtils.PLUGIN,OrderIdSelf,OrderIdCp);
		
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
		this.price=price;
          mLoadingDialog = ProgressDialog.show(context, // context
                  "", // title
                  "Ŭ��������,���Ժ�...", // message
                  true); // �����Ƿ��ǲ�ȷ���ģ���ֻ�ʹ����������й�

          /*************************************************
           * ����1�������翪ʼ,��ȡ������ˮ�ż�TN
           ************************************************/
          new Thread(this).start();
	}
	

	@SuppressLint("SimpleDateFormat") @Override
	public void run() {
	        String time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	        Map<String, String> map = new HashMap<String, String>();
	        map.put("txnTime", time);
	        map.put("merId", merId);
	        map.put("orderId",  getOutTradeNo());
	        map.put("txnAmt", price);
	        
	        //�Զ�������
	        map.put(ConfigUtils.xx_notifyData,notifyData);
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
		 
		 try {
			Class UPPayAssistExClass = Class.forName("com.unionpay.UPPayAssistEx");
			Method startPay = UPPayAssistExClass.getDeclaredMethod("startPay", Context.class,String.class,String.class,String.class,String.class);
			startPay.invoke(null, activity, null, null, tn, mode);
		 } catch (Exception e) {
			e.printStackTrace();
		}
		 
		 //UPPayAssistEx.startPay(activity, null, null, tn, mode);
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
			if(requestCode!=10){
				return ;
			}
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
	            msg = "֧���ɹ���";
                this.pluginHandler.pluginPaySuccess(msg, "");
	            
	        } else if (str.equalsIgnoreCase("fail")) {
	            msg = "֧��ʧ�ܣ�";
	            this.pluginHandler.pluginPayFailed(msg, "");
	        } else if (str.equalsIgnoreCase("cancel")) {
	            msg = "�û�ȡ����֧��";
	            this.pluginHandler.pluginPayCancel(msg, "");
	        }

	    }
	
	
	/**
	 * get the out_trade_no for an order. �����̻������ţ���ֵ���̻���Ӧ����Ψһ�����Զ����ʽ�淶��
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
