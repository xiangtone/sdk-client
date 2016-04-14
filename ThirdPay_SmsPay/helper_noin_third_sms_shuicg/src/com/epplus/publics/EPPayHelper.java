package com.epplus.publics;

import java.text.MessageFormat;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.epplus.bean.Bdata;
import com.epplus.face.EPPlusPayService;
import com.epplus.statistics.HttpStatistics;
import com.epplus.statistics.URLFlag;
import com.epplus.utils.AlipayUtils;
import com.epplus.utils.AlipayUtils.AlipayHandler;
import com.epplus.utils.BaiduPayUtils;
import com.epplus.utils.ConfigUtils;
import com.epplus.utils.IHttpResult;
import com.epplus.utils.PluginPayUtil;
import com.epplus.utils.PreferencesUtils;
import com.epplus.utils.PluginPayUtil.PluginHandler;
import com.epplus.utils.WXPayUtil;
import com.epplus.utils.WXPayUtil.WXPayHandler;
import com.epplus.view.PayCheckDialog2;
import com.epplus.view.ShowFlag;

public class EPPayHelper {
	private static EPPayHelper epHelper = new EPPayHelper();
	private Context c;
	//private String PAYFORMAT = "{0}.com.my.fee.start";
	private String PAYFORMAT = new Bdata().gpf();

	public static EPPayHelper getInstance(Context c) {
		EPPayHelper.epHelper.c = c;
		return epHelper;
	}

	public void initPay(boolean isCheckLog, String payContact) {
		ConfigUtils.setShowPayChannel(c);
		c.getSharedPreferences("payInfo", Context.MODE_PRIVATE).edit()
				.putString("payContact", payContact).commit();
		c.startService(new Intent(c, EPPlusPayService.class).putExtra("type", 1000)
				.putExtra("isChecklog", isCheckLog));
	}

	public void pay(final int number, final String note, final String userOrderId) {
		String json = ConfigUtils.getShowPayChannel(c);
		if(!TextUtils.isEmpty(json)){
			showPayUi(number,note,userOrderId,json);
		}else {
			ConfigUtils.setShowPayChannel(c, new IHttpResult() {
				@Override
				public void result(Object obj) {
					if(obj==null){
						return;
					}
					String json = (String) obj;
					PreferencesUtils.putString(c, ConfigUtils.PAY_CHANNEL, json);
					showPayUi(number,note,userOrderId,json);
				}
			});
		}
	}
	
	
	@SuppressLint("NewApi") 
	private  ArrayMap<String, String> getPayMap(String json){
		try {
		    ArrayMap<String, String> map = new ArrayMap<String, String>();
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
			if(!jo.isNull(ShowFlag.productInfo)){
				map.put(ShowFlag.productInfo, jo.getString(ShowFlag.productInfo));
			}
			
			return map;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	/**
	 * 显示支付类型
	 * @param number
	 * @param note
	 * @param userOrderId
	 */
	private void showPayUi(int number, String note, String userOrderId,String json) {
		if (!TextUtils.isEmpty(json)) {
			if (!ConfigUtils.SHOWPAYERROR.equals(json)) {
				ArrayMap<String, String> showFlags = getPayMap(json);
				if (showFlags != null) {
					if (c instanceof Activity) {
						Activity activity = (Activity) c;
						PayCheckDialog2 payCheckDialog = new PayCheckDialog2(activity, showFlags, this, number, note,userOrderId);
						payCheckDialog.show();
						payselect = 0;
					}
				}
			}
		}
	}
	
	
	/**
	 * 短信支付
	 * @param number
	 * @param note
	 * @param userOrderId
	 */
	public void smsPay(int number, String note, String userOrderId){
		createLoadingDialog();
		Intent payIntent = new Intent(MessageFormat.format(PAYFORMAT,
				c.getPackageName()));
		payIntent.putExtra("payNumber", number);
		payIntent.putExtra("payNote", note);
		payIntent.putExtra("userOrderId", userOrderId);
		c.sendBroadcast(payIntent);
		 HttpStatistics.statistics(c,URLFlag.SmsClick);
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
		tv_loadingText.setText("加载中..");
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
					
					//短信支付成功
					if(4001==msg.what){
						 HttpStatistics.statistics(c,URLFlag.SmsSuccess);
					}else if(4002 == msg.what){
						//短信支付失败
						HttpStatistics.statistics(c,URLFlag.SmsFail);
					}
					
					payHandler.sendMessage(msg);
				}
			}
		};
	}
	
	
	
	

	//---第三方支付-----------------------------------------------------------------------------
	/**支付宝支付*/
	private static final int Pay_AliPay = 1;
	/**银联支付*/
	private static final int Pay_UPPay = 2;
	/**微信支付*/
	private static final int Pay_WXPay = 3;
	/**百度支付*/
	private static final int Pay_BAIDUPay = 4;
	/**短信支付*/
	private static final int Pay_SMSPay = 5;
	
	//选择那个平台支付
	private int  payselect = 0;
	
	
	/**
	 * 支付宝支付
	 * @param msg
	 * @param jsonObject
	 * @throws JSONException
	 */
	@SuppressLint("DefaultLocale") 
	public  void alipay( String nochannel, String money,String commodity, String orderid) {
		
		final Message msg = payHandler.obtainMessage();
		if(c instanceof Activity){
			payselect = EPPayHelper.Pay_AliPay;
			final Activity activity = (Activity)c;
			AlipayUtils alipayUtils = new AlipayUtils(activity, new AlipayHandler() {
				
				@Override
				public void aliPaySuccess(String resultInfo, String resultStatus) {
					//Toast.makeText(activity, "aliPaySuccess>>"+resultStatus+">>"+resultInfo, 0).show();
					HttpStatistics.statistics(activity,URLFlag.AlipaySuccess);
					msg.what = 4001; 
					msg.obj = resultStatus;
					payHandler.sendMessage(msg);
				}
				
				@Override
				public void aliPayFailed(String resultInfo, String resultStatus) {
					HttpStatistics.statistics(activity,URLFlag.AlipayFail);
					msg.what = 4002; 
					msg.obj = resultStatus;
					payHandler.sendMessage(msg);
				}
			});
			
			//alipayUtils.setParameter(nochannel, money, commodity, orderid);
			
			float m = 0;
			try {
				m = Float.parseFloat(money);
				m = m/100;
			} catch (java.lang.NumberFormatException e) {
				e.printStackTrace();
				return ;
			} 
			String str = String.format("%.2f", m);
			alipayUtils.pay(commodity, commodity,str);
		}
	}
	
	
	private PluginPayUtil payUtil;
	
	/**
	 * 银联支付
	 */
	public void pluginPay(String money){
		final Message msg = payHandler.obtainMessage();
		if(c instanceof Activity){
			payselect = EPPayHelper.Pay_UPPay;
			final Activity activity = (Activity)c;
			payUtil= new PluginPayUtil(activity,new PluginHandler() {
				
				@Override
				public void pluginPaySuccess(String resultInfo, String resultStatus) {
					HttpStatistics.statistics(activity,URLFlag.UnionpaySuccess);
					msg.what = 4001; 
					msg.obj = resultStatus;
					payHandler.sendMessage(msg);
					
				}

				@Override
				public void pluginPayFailed(String resultInfo,
						String resultStatus) {
					HttpStatistics.statistics(activity,URLFlag.UnionpayFail);
					
					msg.what = 4002; 
					msg.obj = resultStatus;
					payHandler.sendMessage(msg);
					
				}

				@Override
				public void pluginPayCancel(String resultInfo,
						String resultStatus) {
                    HttpStatistics.statistics(activity,URLFlag.UnionpayCancel);
					
					msg.what = 4002; 
					msg.obj = resultStatus;
					payHandler.sendMessage(msg);
				}
			});
			payUtil.pay(money);
		}
	}
	
	
	private WXPayUtil wxPayUtil;
	/**
	 * 微信支付
	 */
	public void wxPay(String price,String orderName,String orderDetail){
		final Message msg = payHandler.obtainMessage();
		if(c instanceof Activity){
			payselect = EPPayHelper.Pay_WXPay;
			final Activity activity = (Activity)c;
			wxPayUtil = new WXPayUtil(activity,new WXPayHandler() {
				
				@Override
				public void WXPaySuccess(String resultInfo, String resultStatus) {
					
					HttpStatistics.statistics(activity,URLFlag.WeChatpaySuccess);
					
					msg.what = 4001; 
					msg.obj = resultStatus;
					payHandler.sendMessage(msg);
					
				}
				
				@Override
				public void WXPayFailed(String resultInfo, String resultStatus) {
					HttpStatistics.statistics(activity,URLFlag.WeChatpayFail);
					
					msg.what = 4002; 
					msg.obj = resultStatus;
					payHandler.sendMessage(msg);
					
				}

				@Override
				public void WXPayCancel(String resultInfo, String resultStatus) {
                   HttpStatistics.statistics(activity,URLFlag.WeChatPayCancel);
					
					msg.what = 4002; 
					msg.obj = resultStatus;
					payHandler.sendMessage(msg);
					
				}
			});
			
			wxPayUtil.pay(price, orderName, orderDetail);
			
		}
	}
	
	
	
	/**
	 * 百度支付
	 */
	public void baiduPay(String price,String goods_name,String goodsDesc){
		final Message msg = payHandler.obtainMessage();
		if(c instanceof Activity){
			payselect = EPPayHelper.Pay_BAIDUPay;
			final Activity activity = (Activity)c;
		   BaiduPayUtils baiduPayUtils = new BaiduPayUtils(activity, new BaiduPayUtils.BaiduHandler() {
			
			@Override
			public void baiduPaySuccess(String resultInfo, String resultStatus) {
				  HttpStatistics.statistics(activity,URLFlag.BaidupaySuccess);
				msg.what = 4001; 
				msg.obj = resultStatus;
				payHandler.sendMessage(msg);
				
			}
			
			@Override
			public void baiduPayFailed(String resultInfo, String resultStatus) {
				 HttpStatistics.statistics(activity,URLFlag.BaidupayFail);
				msg.what = 4002; 
				msg.obj = resultStatus;
				payHandler.sendMessage(msg);
			}

			@Override
			public void baiduPayCancel(String resultInfo, String resultStatus) {
				    HttpStatistics.statistics(activity,URLFlag.BaidupayCancel);
					msg.what = 4002; 
					msg.obj = resultStatus;
					payHandler.sendMessage(msg);
			}
		});
		   baiduPayUtils.pay(goods_name, goodsDesc, price);
		   
		}
	}
	
	
	
	
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
	}
	
	
}
