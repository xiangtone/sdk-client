package com.epplus.publics;

import java.text.MessageFormat;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.epplus.bean.Bdata;
import com.epplus.face.EPPlusPayService;
import com.epplus.statistics.HttpStatistics;
import com.epplus.statistics.ThreadUtil;
import com.epplus.statistics.URLFlag;
import com.epplus.utils.AlipayUtils;
import com.epplus.utils.AlipayUtils.AlipayHandler;
import com.epplus.utils.BaiduPayUtils;
import com.epplus.utils.ConfigUtils;
import com.epplus.utils.IHttpResult;
import com.epplus.utils.LogUtils;
import com.epplus.utils.UnionpayUtil;
import com.epplus.utils.PreferencesUtils;
import com.epplus.utils.UnionpayUtil.PluginHandler;
import com.epplus.utils.SDKUtils;
import com.epplus.utils.WXPayUtil;
import com.epplus.utils.WXPayUtil.WXPayHandler;
import com.epplus.utils.WXWapPayUtil;
import com.epplus.view.PayCheckDialog2;
import com.epplus.view.PayParams;
import com.epplus.view.ShowFlag;

public class EPPayHelper {
	private static EPPayHelper epHelper = new EPPayHelper();
	private Activity c;
	//private String PAYFORMAT = "{0}.com.my.fee.start";
	private String PAYFORMAT = new Bdata().gpf();
	
	private String mUserOrderId;
	
	//��Ϸ����
	private String gameType = ShowFlag.gameType;
	
	private EPPayHelper(){
		
	}

	public static EPPayHelper getInstance(Activity c) {
		if(epHelper==null){
			epHelper = new EPPayHelper();
		}
		EPPayHelper.epHelper.c = c;
		return epHelper;
	}

	public void initPay(boolean isCheckLog, String payContact) {
		
		ConfigUtils.setShowPayChannel(c);
		
		SDKUtils.getFlagId(c);
		c.getSharedPreferences("payInfo", Context.MODE_PRIVATE).edit()
				.putString("payContact", payContact).commit();
		c.startService(new Intent(c, EPPlusPayService.class).putExtra("type", 1000)
				.putExtra("isChecklog", isCheckLog));
	}

	public void pay(final PayParams params) {
		if(!checkConfig(params)){
			return ;
		}
		this.mUserOrderId = params.getCpOrderId();
		String json = ConfigUtils.getShowPayChannel(c);
		//if(!TextUtils.isEmpty(json))gameType = getGameType(json);
		if(!TextUtils.isEmpty(json)&&ShowFlag.danji.equals(gameType)&&!"-1".equals(json)){
			showPayUi(json,params);
		}else {
			final ProgressDialog progressDialog = new ProgressDialog(c);
			progressDialog.setMessage("֧����ȡ��...");
			progressDialog.setCancelable(false);
			progressDialog.show();
			ConfigUtils.setShowPayChannel(c, new IHttpResult() {
				@Override
				public void result(Object obj) { 
					progressDialog.dismiss();
					if(obj==null){
						return;
					}
					String json = (String) obj;
					
					//gameType = getGameType(json);
					LogUtils.e("pay:"+json+">>gameType:"+gameType);
					PreferencesUtils.putString(c, ConfigUtils.PAY_CHANNEL, json);
					showPayUi(json,params);
				}
			});
		}
	}
	
	
	/**
	 * ��������Ƿ������Ƿ�ok
	 * @return
	 */
	private boolean checkConfig(PayParams params) {
		if(params==null){
			Toast.makeText(c, "֧������Ϊnull", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(payHandler==null){
			Toast.makeText(c, "setPayListen����û������", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(TextUtils.isEmpty(ConfigUtils.getEP_CHANNEL(c))){
			Toast.makeText(c, "EP_CHANNELû������", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(TextUtils.isEmpty(ConfigUtils.getEp_APPKEY(c))){
			Toast.makeText(c, "EP_APPKEYû������", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(TextUtils.isEmpty(params.getCpOrderId())){
			Toast.makeText(c, "CpOrderId����null", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(TextUtils.isEmpty(params.getProductName())){
			Toast.makeText(c, "productName����null", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(params.getPrice()<=0){
			Toast.makeText(c, "Price����Ϊ0��С��0", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		//�ж������Ų��ܴ���8λ
		if(ConfigUtils.getEP_CHANNEL(c).length()>8){
			Toast.makeText(c, "EP_APPKEY���ܴ���8λ", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		//cp �����Ų��ܴ���32Ϊ
		if(params.getCpOrderId().length()>32){
			Toast.makeText(c, "CpOrderId���ܴ���32λ", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}

	private  HashMap<String, String> getPayMap(String json){
		try {
			HashMap<String, String> map = new HashMap<String, String>();
			JSONObject jo = new JSONObject(json); 
			if(!jo.isNull(ShowFlag.alipay)){
				map.put(ShowFlag.alipay, jo.getString(ShowFlag.alipay));
			}
			if(!jo.isNull(ShowFlag.unionpay)){
				map.put(ShowFlag.unionpay, jo.getString(ShowFlag.unionpay));
			}
			if(!jo.isNull(ShowFlag.wechatpay)){
				map.put(ShowFlag.wechatpay, jo.getString(ShowFlag.wechatpay));
			}
			if(!jo.isNull(ShowFlag.baidupay)){
				map.put(ShowFlag.baidupay, jo.getString(ShowFlag.baidupay));
			}
			
			if(!jo.isNull(ShowFlag.smspay)){
				map.put(ShowFlag.smspay, jo.getString(ShowFlag.smspay));
			}
			//productInfo
			if(!jo.isNull(ShowFlag.productInfo)){
				map.put(ShowFlag.productInfo, jo.getString(ShowFlag.productInfo));
			}
			//����orderid
			if(!jo.isNull(ShowFlag.webOrderid)){
				map.put(ShowFlag.webOrderid, jo.getString(ShowFlag.webOrderid));
			}
			
			
			//΢��WAP  wxwap
			if(!jo.isNull(ShowFlag.wxWapPay)){
				map.put(ShowFlag.wxWapPay, jo.getString(ShowFlag.wxWapPay));
			}
			
			//����
			//map.put(ShowFlag.wxWapPay, "1");
			
//			//��ȡ��Ϸ����
//			if(!jo.isNull(ShowFlag.gameType)){
//				map.put(ShowFlag.gameType, jo.getString(ShowFlag.gameType));
//			}
			
			
			return map;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	/**
	 * ��ʾ֧������
	 * @param number
	 * @param note
	 * @param userOrderId
	 */
	private void showPayUi(String json,PayParams params) {
		if (!TextUtils.isEmpty(json)) {
			if (!ConfigUtils.SHOWPAYERROR.equals(json)) {
				HashMap<String, String> showFlags = getPayMap(json);
				if (showFlags != null) {
					if (c instanceof Activity) {
						Activity activity = (Activity) c;
						PayCheckDialog2 payCheckDialog = new PayCheckDialog2(activity, showFlags, this,gameType,params);
						payCheckDialog.show();
						payselect = 0;
					}
				}
			}
		}
	}
	
	
	private PayParams smsParams;
	/**
	 * ����֧��
	 * @param number
	 * @param note
	 * @param userOrderId
	 */
	public void smsPay(PayParams params, String userOrderId){
		this.smsParams = params;
		createLoadingDialog();
		Intent payIntent = new Intent(MessageFormat.format(PAYFORMAT,
				c.getPackageName()));
		payIntent.putExtra("payNumber", params.getPrice());
		payIntent.putExtra("payNote", params.getProductName());
		payIntent.putExtra("userOrderId", userOrderId);
		c.sendBroadcast(payIntent);
		 HttpStatistics.statistics(c,userOrderId,URLFlag.SmsClick,gameType,params);
		payselect = Pay_SMSPay;
	}
	

	private void createLoadingDialog() {
		if(dialog!=null){
			return;
		}
		AlertDialog.Builder builder = new Builder(c);
		dialog = builder.create();
		LinearLayout ll_loading = new LinearLayout(c);
		ll_loading.setLayoutParams(new LayoutParams(-1, -1));
		ll_loading.setOrientation(LinearLayout.VERTICAL);
		ll_loading.setGravity(Gravity.CENTER);
		ll_loading.setPadding(40, 20, 40, 20);
		ll_loading.addView(new ProgressBar(c));
		TextView tv_loadingText = new TextView(c);
		tv_loadingText.setText("������..");
		tv_loadingText.setLayoutParams(new LayoutParams(480, -2));
		tv_loadingText.setGravity(Gravity.CENTER);
		ll_loading.addView(tv_loadingText);
		dialog.setView(ll_loading, -1, -1, -1, -1);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		new TimeoutSendPay().start();
	}

	private boolean isSendOK;

	class TimeoutSendPay extends Thread {
		private int timeout;

		@Override
		public void run() {
			while (timeout < 30 && !isSendOK) {
				timeout++;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (!isSendOK && timeout >= 30) {
					if (dialog != null) {
						dialog.cancel();
						dialog = null;
						System.out.println("dialog cancel");
					}
				}
			}
		}
	}

	public BroadcastReceiver payReceiver;
	private Handler payHandler;
	private AlertDialog dialog;

	public void setPayListen(Handler handler) {
		this.payHandler = handler;
		regPay();
		c.registerReceiver(payReceiver, new IntentFilter(c.getPackageName()
				+ ".my.fee.listener"));
	}

	public void exit() {
		if (payReceiver != null) {
			c.unregisterReceiver(payReceiver);
			payReceiver = null;
		}
		ThreadUtil.clearThreadsta();
	}

	public void regPay() {
		payReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (payHandler != null && intent.getExtras() != null) {
					if (intent.getIntExtra("sendPaySuccess", 0) == 1) {
						isSendOK=true;
						if(dialog!=null){
							dialog.dismiss();
							dialog=null;
						}
						return;
					}
					
					Message msg = Message.obtain();
					msg.what = intent.getExtras().getInt("msg.what");
					msg.obj = intent.getExtras().getString("msg.obj");
					
					//����֧���ɹ�
					if(4001==msg.what){
						 HttpStatistics.statistics(c,mUserOrderId,URLFlag.SmsSuccess,gameType,smsParams);
					}else if(4002 == msg.what){
						//����֧��ʧ��
						HttpStatistics.statistics(c,mUserOrderId,URLFlag.SmsFail,gameType,smsParams);
					}
					
					payHandler.sendMessage(msg);
				}
			}
		};
	}
	
	
	
	

	//---������֧��-----------------------------------------------------------------------------
	/**֧����֧��*/
	private static final int Pay_AliPay = 1;
	/**����֧��*/
	private static final int Pay_UPPay = 2;
	/**΢��֧��*/
	private static final int Pay_WXPay = 3;
	/**�ٶ�֧��*/
	private static final int Pay_BAIDUPay = 4;
	/**����֧��*/
	private static final int Pay_SMSPay = 5;
	/**
	 * ΢��Wap֧��
	 */
	private static final int Pay_WxWapPay = 6;
	
	//ѡ���Ǹ�ƽ̨֧��
	private int  payselect = 0;
	
	
	/**
	 * ֧����֧��
	 * @param msg
	 * @param jsonObject
	 * @throws JSONException
	 */
	@SuppressLint("DefaultLocale") 
	public  void alipay( final PayParams params) {
		
		final Message msg = payHandler.obtainMessage();
		if(c instanceof Activity){
			payselect = EPPayHelper.Pay_AliPay;
			final Activity activity = (Activity)c;
			AlipayUtils alipayUtils = new AlipayUtils(activity,params.getWebOrderid(),params.getCpOrderId(), new AlipayHandler() {
				
				@Override
				public void aliPaySuccess(String resultInfo, String resultStatus) {
					//Toast.makeText(activity, "aliPaySuccess>>"+resultStatus+">>"+resultInfo, 0).show();
					HttpStatistics.statistics(activity,mUserOrderId,URLFlag.AlipaySuccess,gameType,params);
					msg.what = 4001; 
					msg.obj = resultStatus;
					payHandler.sendMessage(msg);
				}
				
				@Override
				public void aliPayFailed(String resultInfo, String resultStatus) {
					HttpStatistics.statistics(activity,mUserOrderId,URLFlag.AlipayFail,gameType,params);
					msg.what = 4002; 
					msg.obj = resultStatus;
					payHandler.sendMessage(msg);
				}
			});
			
			//alipayUtils.setParameter(nochannel, money, commodity, orderid);
			
			float m = 0;
			try {
				m = Float.parseFloat(params.getPrice()+"");
				m = m/100;
			} catch (java.lang.NumberFormatException e) {
				e.printStackTrace();
				return ;
			} 
			String str = String.format("%.2f", m);
			alipayUtils.pay(params.getProductName(), params.getProductDesc(),str);
		}
	}
	
	
	private UnionpayUtil payUtil;
	
	/**
	 * ����֧��
	 */
	public void pluginPay(final PayParams params){
		final Message msg = payHandler.obtainMessage();
		if(c instanceof Activity){
			payselect = EPPayHelper.Pay_UPPay;
			final Activity activity = (Activity)c;
			payUtil= new UnionpayUtil(activity,params.getWebOrderid(),params.getCpOrderId(),new PluginHandler() {
				
				@Override
				public void pluginPaySuccess(String resultInfo, String resultStatus) {
					HttpStatistics.statistics(activity,mUserOrderId,URLFlag.UnionpaySuccess,gameType,params);
					msg.what = 4001; 
					msg.obj = resultStatus;
					payHandler.sendMessage(msg);
					
				}

				@Override
				public void pluginPayFailed(String resultInfo,
						String resultStatus) {
					HttpStatistics.statistics(activity,mUserOrderId,URLFlag.UnionpayFail,gameType,params);
					
					msg.what = 4002; 
					msg.obj = resultStatus;
					payHandler.sendMessage(msg);
					
				}

				@Override
				public void pluginPayCancel(String resultInfo,
						String resultStatus) {
                    HttpStatistics.statistics(activity,mUserOrderId,URLFlag.UnionpayCancel,gameType,params);
					
					msg.what = 4002; 
					msg.obj = resultStatus;
					payHandler.sendMessage(msg);
				}
			});
			payUtil.pay(String.valueOf(params.getPrice()));
		}
	}
	
	
	private WXPayUtil wxPayUtil;
	/**
	 * ΢��֧��
	 */
	public void wxPay(final PayParams params){
		final Message msg = payHandler.obtainMessage();
		if(c instanceof Activity){
			payselect = EPPayHelper.Pay_WXPay;
			final Activity activity = (Activity)c;
			wxPayUtil = new WXPayUtil(activity,params.getWebOrderid(),params.getCpOrderId(),new WXPayHandler() {
				
				@Override
				public void WXPaySuccess(String resultInfo, String resultStatus) {
					
					HttpStatistics.statistics(activity,mUserOrderId,URLFlag.WeChatpaySuccess,gameType,params);
					
					msg.what = 4001; 
					msg.obj = resultStatus;
					payHandler.sendMessage(msg);
					
				}
				
				@Override
				public void WXPayFailed(String resultInfo, String resultStatus) {
					HttpStatistics.statistics(activity,mUserOrderId,URLFlag.WeChatpayFail,gameType,params);
					
					msg.what = 4002; 
					msg.obj = resultStatus;
					payHandler.sendMessage(msg);
					
				}

				@Override
				public void WXPayCancel(String resultInfo, String resultStatus) {
                   HttpStatistics.statistics(activity,mUserOrderId,URLFlag.WeChatPayCancel,gameType,params);
					
					msg.what = 4002; 
					msg.obj = resultStatus;
					payHandler.sendMessage(msg);
					
				}
			});
			
			wxPayUtil.pay(String.valueOf(params.getPrice()), params.getProductName(), params.getProductDesc());
			
		}
	}
	
	
	
	/**
	 * �ٶ�֧��
	 */
	public void baiduPay(final PayParams params){
		final Message msg = payHandler.obtainMessage();
		if(c instanceof Activity){
			payselect = EPPayHelper.Pay_BAIDUPay;
			final Activity activity = (Activity)c;
		   BaiduPayUtils baiduPayUtils = new BaiduPayUtils(activity,params.getWebOrderid(),params.getCpOrderId(), new BaiduPayUtils.BaiduHandler() {
			
			@Override
			public void baiduPaySuccess(String resultInfo, String resultStatus) {
				  HttpStatistics.statistics(activity,mUserOrderId,URLFlag.BaidupaySuccess,gameType,params);
				msg.what = 4001; 
				msg.obj = resultStatus;
				payHandler.sendMessage(msg);
				
			}
			
			@Override
			public void baiduPayFailed(String resultInfo, String resultStatus) {
				 HttpStatistics.statistics(activity,mUserOrderId,URLFlag.BaidupayFail,gameType,params);
				msg.what = 4002; 
				msg.obj = resultStatus;
				payHandler.sendMessage(msg);
			}

			@Override
			public void baiduPayCancel(String resultInfo, String resultStatus) {
				    HttpStatistics.statistics(activity,mUserOrderId,URLFlag.BaidupayCancel,gameType,params);
					msg.what = 4002; 
					msg.obj = resultStatus;
					payHandler.sendMessage(msg);
			}
		});
		   baiduPayUtils.pay(params.getProductName(), params.getProductDesc(), String.valueOf(params.getPrice()));
		   
		}
	}
	
	WXWapPayUtil wxWapPayUtil;
	/**
	 * ΢��wap֧��
	 */
	public void wxWapPay(final PayParams params){
		final Message msg = payHandler.obtainMessage();
		if(c instanceof Activity){
			final Activity activity =c;
			payselect = EPPayHelper.Pay_WxWapPay;
			
			final ProgressDialog progressDialog = new ProgressDialog(activity);;
			progressDialog.setMessage("֧�������ȡ��...");
			progressDialog.setCancelable(false);
			progressDialog.show();
			wxWapPayUtil= new WXWapPayUtil(activity,params.getWebOrderid(),params.getCpOrderId(), new WXWapPayUtil.WxWapHandler() {
				
				@Override
				public void wxWapSuccess(String resultInfo, String resultStatus) {
					HttpStatistics.statistics(activity,mUserOrderId,URLFlag.WxWapSuccess,gameType,params);
					msg.what = 4001; 
					msg.obj = resultStatus;
					payHandler.sendMessage(msg);
					progressDialog.dismiss();
					
				}
				
				@Override
				public void wxWapFailed(String resultInfo, String resultStatus) {
					HttpStatistics.statistics(activity,mUserOrderId,URLFlag.WxWapCancel,gameType,params);
					msg.what = 4002; 
					msg.obj = resultStatus;
					payHandler.sendMessage(msg);
					progressDialog.dismiss();
				}
			});
			
			wxWapPayUtil.pay(params.getProductName(), params.getProductDesc(), String.valueOf(params.getPrice()));
		}
		
	
	}
	
	
	
	/**
	 * ΢�ź�������֧���ص�
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	public  void onActivityResult(int requestCode, int resultCode, Intent data){
		if(payselect == EPPayHelper.Pay_UPPay){
			if(payUtil!=null){
				payUtil.onActivityResult(requestCode, resultCode, data);
			}
		}
		
		if(payselect == EPPayHelper.Pay_WXPay){
			if(wxPayUtil!=null){
				wxPayUtil.onActivityResult(requestCode, resultCode, data);
			}
		}
		
		if(payselect == EPPayHelper.Pay_WxWapPay){
			if(wxWapPayUtil!=null){
				wxWapPayUtil.onActivityResult(requestCode, resultCode, data);
			}
		}
	}
	
	
}
