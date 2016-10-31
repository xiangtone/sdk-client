package com.thirdpay.publics;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.thirdpay.statistics.HttpStatistics;
import com.thirdpay.statistics.JSON;
import com.thirdpay.statistics.ThreadUtil;
import com.thirdpay.statistics.URLFlag;
import com.thirdpay.utils.AlipayUtils;
import com.thirdpay.utils.ApkUtils;
import com.thirdpay.utils.BaiduPayUtils;
import com.thirdpay.utils.ConfigUtils;
import com.thirdpay.utils.IHttpResult;
import com.thirdpay.utils.LogUtils;
import com.thirdpay.utils.PreferencesUtils;
import com.thirdpay.utils.SDKUtils;
import com.thirdpay.utils.UnionpayUtil;
import com.thirdpay.utils.WXPayUtil;
import com.thirdpay.utils.WXSwiftPayUtil;
import com.thirdpay.utils.WXWapPayUtil;
import com.thirdpay.utils.AlipayUtils.AlipayHandler;
import com.thirdpay.utils.UnionpayUtil.PluginHandler;
import com.thirdpay.utils.WXPayUtil.WXPayHandler;
import com.thirdpay.utils.WXWapPayUtil.WxWapHandler;
import com.thirdpay.view.PayCheckDialogActivity;
import com.thirdpay.view.PayParams;
import com.thirdpay.view.ShowFlag;

public class ThirdPayHelper {
	private static ThirdPayHelper epHelper = new ThirdPayHelper();
	private Activity c;
	
	private String mUserOrderId;
	
	//游戏类型
	private String gameType = ShowFlag.gameType;
	
	private ThirdPayHelper(){
		
	}

	public static ThirdPayHelper getInstance(Activity c) {
		if(epHelper==null){
			epHelper = new ThirdPayHelper();
		}
		ThirdPayHelper.epHelper.c = c;
		return epHelper;
	}

	public void initPay() {
		
		ConfigUtils.setShowPayChannel(c);
		
		SDKUtils.getFlagId(c);
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
			progressDialog.setMessage("支付获取中...");
			//progressDialog.setCancelable(false);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.show();
			ConfigUtils.setShowPayChannel(c, new IHttpResult() {
				@Override
				public void result(Object obj) { 
					progressDialog.dismiss();
					if(obj==null){
						//TODO
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
	
	
	//===start 支付无界面==============================================================================================
		/**
		 * 1 微信wap 2 支付宝  3银联 4 微信支付 5，百度  6 ,短信
		 * @param params
		 * @param flag
		 */
		public void pay(final PayParams params,final int flag) {
			if(!checkConfig(params)){
				return ;
			}
			this.mUserOrderId = params.getCpOrderId();
			String json = ConfigUtils.getShowPayChannel(c);
			//if(!TextUtils.isEmpty(json))gameType = getGameType(json);
			if(!TextUtils.isEmpty(json)&&ShowFlag.danji.equals(gameType)&&!"-1".equals(json)){
				showPayUi(json,params,flag);
			}else {
				final ProgressDialog progressDialog = new ProgressDialog(c);
				progressDialog.setMessage("支付获取中...");
				//progressDialog.setCancelable(false);
				progressDialog.setCanceledOnTouchOutside(false);
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
						showPayUi(json,params,flag);
					}
				});
			}
		}
		
		
		
		/**
		 * 
		 * @param json
		 * @param params
		 * @param flag 1 微信wap 2 支付宝  3银联 4 微信支付 5，百度  6 ,短信
		 */
		private void showPayUi(String json, PayParams params, int flag) {
			if (!TextUtils.isEmpty(json)) {
				if (!ConfigUtils.SHOWPAYERROR.equals(json)) {
					HashMap<String, String> showFlags = getPayMap(json);
					if (showFlags != null) {
						if (c instanceof Activity) {
							Activity activity = (Activity) c;
							payselect = 0;
							if(showFlags.containsKey(ShowFlag.webOrderid)){
						   		 params.setWebOrderid(showFlags.get(ShowFlag.webOrderid));
						   	 }
							switch (flag) {
							case 1:
								wxWapPay(activity,params);
								break;
							case 2:
								AlipayPay(activity,params);					
								break;
							case 3:
								unionPay(activity,params);		
								break;
							case 4:
								weChatPay(activity,params);		
								break;
							case 5:
								baiduPay(activity,params);		
								break;
							case 7:
								wxSwiftPay(activity, params);
								break;

							default:
								break;
							}
							
						}
					}
				}
			}
			
		}
		
		
		/**
		 * 威富通微信wap支付
		 */
		private void wxSwiftPay(Context context,PayParams mPayParams) {
			HttpStatistics.statistics(context,mPayParams.getCpOrderId(),URLFlag.WxSwiftPayClick,gameType,mPayParams);
			wxSwifPay(mPayParams);
			
		}
		
		
		/**
		 * 微信wap支付
		 */
		private void wxWapPay(Context context,PayParams mPayParams) {
			HttpStatistics.statistics(context,mPayParams.getCpOrderId(),URLFlag.WxWapClick,gameType,mPayParams);
			wxWapPay(mPayParams);
			
		}

		/**
		 * 微信支付
		 */
		private void weChatPay(Context context,PayParams mPayParams) {
			HttpStatistics.statistics(context,mPayParams.getCpOrderId(),URLFlag.WeChatPayClick,gameType,mPayParams);
			wxPay(mPayParams);
		}

		/**
		 * 百度支付
		 */
		private void baiduPay(Context context,PayParams mPayParams) {
			HttpStatistics.statistics(context,mPayParams.getCpOrderId(),URLFlag.BaidupayClick,gameType,mPayParams);
			baiduPay(mPayParams);
		}

		/**
		 * 银联支付
		 */
		private void unionPay(Context context,PayParams mPayParams) {
			HttpStatistics.statistics(context,mPayParams.getCpOrderId(),URLFlag.UnionpayClick,gameType,mPayParams);
			pluginPay(mPayParams);
		}

		/**
		 * 支付宝支付
		 */
		private void AlipayPay(Context context,PayParams mPayParams) {
			HttpStatistics.statistics(context,mPayParams.getCpOrderId(),URLFlag.AlipayClick,gameType,mPayParams);
			alipay(mPayParams);
			
		}
		//===end 支付无界面==============================================================================================
	
	
	/**
	 * 检测配置是否配置是否ok
	 * @return
	 */
	private boolean checkConfig(PayParams params) {
		if(params==null){
			Toast.makeText(c, "支付参数为null", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(payHandler==null){
			Toast.makeText(c, "setPayListen监听没有设置", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(TextUtils.isEmpty(ConfigUtils.getEP_CHANNEL(c))){
			Toast.makeText(c, "EP_CHANNEL没有配置", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(TextUtils.isEmpty(ConfigUtils.getEp_APPKEY(c))){
			Toast.makeText(c, "EP_APPKEY没有配置", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(TextUtils.isEmpty(params.getCpOrderId())){
			Toast.makeText(c, "CpOrderId不能null", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(TextUtils.isEmpty(params.getProductName())){
			Toast.makeText(c, "productName不能null", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(params.getPrice()<=0){
			Toast.makeText(c, "Price不能为0或小于0", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		//判断渠道号不能大于8位
		if(ConfigUtils.getEP_CHANNEL(c).length()>8){
			Toast.makeText(c, "EP_CHANNEL不能大于8位", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		//cp 订单号不能大于32为
		if(params.getCpOrderId().length()>32){
			Toast.makeText(c, "CpOrderId不能大于32位", Toast.LENGTH_SHORT).show();
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
			//网络orderid
			if(!jo.isNull(ShowFlag.webOrderid)){
				map.put(ShowFlag.webOrderid, jo.getString(ShowFlag.webOrderid));
			}
			
			
			//微信WAP  wxwap
			if(!jo.isNull(ShowFlag.wxWapPay)){
				map.put(ShowFlag.wxWapPay, jo.getString(ShowFlag.wxWapPay));
			}
			
			
			//添加swfitpay
			if(!jo.isNull(ShowFlag.wxSwiftPay)){
				map.put(ShowFlag.wxSwiftPay, jo.getString(ShowFlag.wxSwiftPay));
			}
			
			//测试
			//map.put(ShowFlag.wxWapPay, "1");
			
//			//获取游戏类型
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
	 * 显示支付类型
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
						//PayCheckDialog2 payCheckDialog = new PayCheckDialog2(activity, showFlags, this,gameType,params,payHandler);
						//payCheckDialog.show();
						
						Intent intent = new Intent(activity,PayCheckDialogActivity.class);
						
						intent.putExtra("params", JSON.toJsonString(params));
						intent.putExtra("mShowFlags",JSON.toMapJson(showFlags));
						intent.putExtra("gameType", gameType);
						
						activity.startActivity(intent);
						if(showFlags.containsKey(ShowFlag.webOrderid)){
					   		 params.setWebOrderid(showFlags.get(ShowFlag.webOrderid));
					   	 }
						HttpStatistics.statistics(c,params.getCpOrderId(),URLFlag.PayGuiShow,gameType,params);
						
						
						payselect = 0;
					}
				}
			}
		}
	}
	

	private Handler payHandler;

	public void setPayListen(Handler handler) {
		this.payHandler = handler;				
	}

	public void exit() {
		ThreadUtil.clearThreadsta();
	}


	//---第三方支付-----------------------------------------------------------------------------
	/**支付宝支付*/
	private static final int Pay_AliPay = 11;
	/**银联支付*/
	private static final int Pay_UPPay = 12;
	/**微信支付*/
	private static final int Pay_WXPay = 13;
	/**百度支付*/
	private static final int Pay_BAIDUPay = 14;
	/**
	 * 微信Wap支付
	 */
	private static final int Pay_WxWapPay = 15;
	
	/**
	 * 威富通 微信Wap支付
	 */
	private static final int Pay_WxWapSwiftPay = 16;
	
	//选择那个平台支付
	private int  payselect = 0;
	
	
	/**
	 * 支付宝支付
	 * @param msg
	 * @param jsonObject
	 * @throws JSONException
	 */
	@SuppressLint("DefaultLocale") 
	public  void alipay( final PayParams params) {
		
		final Message msg = payHandler.obtainMessage();
		if(c instanceof Activity){
			payselect = ThirdPayHelper.Pay_AliPay;
			final Activity activity = (Activity)c;
			AlipayUtils alipayUtils = new AlipayUtils(activity,params.getWebOrderid(),params.getCpOrderId(), new AlipayHandler() {
				
				@Override
				public void aliPaySuccess(String resultInfo, String resultStatus) {
					//Toast.makeText(activity, "aliPaySuccess>>"+resultStatus+">>"+resultInfo, 0).show();
					HttpStatistics.statistics(activity,mUserOrderId,URLFlag.AlipaySuccess,gameType,params);
					LogUtils.e("aliPaySuccess--msg.arg2 = EPPayHelper.Pay_AliPay");
					msg.what = 5001; 
					msg.arg1 = params.getPrice();
					msg.arg2 = ThirdPayHelper.Pay_AliPay;
					msg.obj = resultStatus;
					payHandler.sendMessage(msg);
					PayCheckDialogActivity.sendBroadcast(c);
				}
				
				@Override
				public void aliPayFailed(String resultInfo, String resultStatus) {
					HttpStatistics.statistics(activity,mUserOrderId,URLFlag.AlipayFail,gameType,params);
					LogUtils.e("aliPayFailed--msg.arg2 = EPPayHelper.Pay_AliPay");
					msg.what = 5002; 
					msg.arg2 = ThirdPayHelper.Pay_AliPay;
					msg.obj = resultStatus;
					payHandler.sendMessage(msg);
					PayCheckDialogActivity.sendBroadcast(c);
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
	 * 银联支付
	 */
	public void pluginPay(final PayParams params){
		final Message msg = payHandler.obtainMessage();
		if(c instanceof Activity){
			payselect = ThirdPayHelper.Pay_UPPay;
			final Activity activity = (Activity)c;
			payUtil= new UnionpayUtil(activity,params.getWebOrderid(),params.getCpOrderId(),new PluginHandler() {
				
				@Override
				public void pluginPaySuccess(String resultInfo, String resultStatus) {
					HttpStatistics.statistics(activity,mUserOrderId,URLFlag.UnionpaySuccess,gameType,params);
					LogUtils.e("pluginPaySuccess--msg.arg2 = EPPayHelper.Pay_UPPay");
					msg.what = 5001; 
					msg.arg1 = params.getPrice();
					msg.arg2 = ThirdPayHelper.Pay_UPPay;
					msg.obj = resultStatus;
					payHandler.sendMessage(msg);
					PayCheckDialogActivity.sendBroadcast(c);
					
				}

				@Override
				public void pluginPayFailed(String resultInfo,
						String resultStatus) {
					HttpStatistics.statistics(activity,mUserOrderId,URLFlag.UnionpayFail,gameType,params);
					LogUtils.e("pluginPayFailed--msg.arg2 = EPPayHelper.Pay_UPPay");
					msg.what = 5002; 
					msg.arg2 = ThirdPayHelper.Pay_UPPay;
					msg.obj = resultStatus;
					payHandler.sendMessage(msg);
					PayCheckDialogActivity.sendBroadcast(c);
					
				}

				@Override
				public void pluginPayCancel(String resultInfo,
						String resultStatus) {
                    HttpStatistics.statistics(activity,mUserOrderId,URLFlag.UnionpayCancel,gameType,params);
                    LogUtils.e("pluginPayCancel--msg.arg2 = EPPayHelper.Pay_UPPay");
					msg.what = 5002; 
					msg.arg2 = ThirdPayHelper.Pay_UPPay;
					msg.obj = resultStatus;
					payHandler.sendMessage(msg);
					PayCheckDialogActivity.sendBroadcast(c);
				}
			});
			payUtil.pay(String.valueOf(params.getPrice()));
		}
	}
	
	
	private WXPayUtil wxPayUtil;
	/**
	 * 微信支付
	 */
	public void wxPay(final PayParams params){
		final Message msg = payHandler.obtainMessage();
		if(c instanceof Activity){
			payselect = ThirdPayHelper.Pay_WXPay;
			final Activity activity = (Activity)c;
			wxPayUtil = new WXPayUtil(activity,params.getWebOrderid(),params.getCpOrderId(),new WXPayHandler() {
				
				@Override
				public void WXPaySuccess(String resultInfo, String resultStatus) {
					
					HttpStatistics.statistics(activity,mUserOrderId,URLFlag.WeChatpaySuccess,gameType,params);
					LogUtils.e("WXPaySuccess--msg.arg2 = EPPayHelper.Pay_WXPay");
					msg.what = 5001; 
					msg.arg1 = params.getPrice();
					msg.arg2 = ThirdPayHelper.Pay_WXPay;
					msg.obj = resultStatus;
					payHandler.sendMessage(msg);
					PayCheckDialogActivity.sendBroadcast(c);
					
				}
				
				@Override
				public void WXPayFailed(String resultInfo, String resultStatus) {
					HttpStatistics.statistics(activity,mUserOrderId,URLFlag.WeChatpayFail,gameType,params);
					LogUtils.e("WXPayFailed--msg.arg2 = EPPayHelper.Pay_WXPay");
					msg.what = 5002; 
					msg.arg2 = ThirdPayHelper.Pay_WXPay;
					msg.obj = resultStatus;
					payHandler.sendMessage(msg);
					PayCheckDialogActivity.sendBroadcast(c);
					
				}

				@Override
				public void WXPayCancel(String resultInfo, String resultStatus) {
                   HttpStatistics.statistics(activity,mUserOrderId,URLFlag.WeChatPayCancel,gameType,params);
                   LogUtils.e("WXPayCancel--msg.arg2 = EPPayHelper.Pay_WXPay");
					msg.what = 5002; 
					msg.arg2 = ThirdPayHelper.Pay_WXPay;
					msg.obj = resultStatus;
					payHandler.sendMessage(msg);
					PayCheckDialogActivity.sendBroadcast(c);
					
				}
			});
			
			wxPayUtil.pay(String.valueOf(params.getPrice()), params.getProductName(), params.getProductDesc());
			
		}
	}
	
	
	
	/**
	 * 百度支付
	 */
	public void baiduPay(final PayParams params){
		final Message msg = payHandler.obtainMessage();
		if(c instanceof Activity){
			payselect = ThirdPayHelper.Pay_BAIDUPay;
			final Activity activity = (Activity)c;
		   BaiduPayUtils baiduPayUtils = new BaiduPayUtils(activity,params.getWebOrderid(),params.getCpOrderId(), new BaiduPayUtils.BaiduHandler() {
			
			@Override
			public void baiduPaySuccess(String resultInfo, String resultStatus) {
				  HttpStatistics.statistics(activity,mUserOrderId,URLFlag.BaidupaySuccess,gameType,params);
				  LogUtils.e("baiduPaySuccess--msg.arg2 = EPPayHelper.Pay_BAIDUPay");
				msg.what = 5001; 
				msg.arg1 = params.getPrice();
				msg.arg2 =  ThirdPayHelper.Pay_BAIDUPay;			
				msg.obj = resultStatus;
				payHandler.sendMessage(msg);
				PayCheckDialogActivity.sendBroadcast(c);
				
			}
			
			@Override
			public void baiduPayFailed(String resultInfo, String resultStatus) {
				 HttpStatistics.statistics(activity,mUserOrderId,URLFlag.BaidupayFail,gameType,params);
				 LogUtils.e("baiduPayFailed--msg.arg2 = EPPayHelper.Pay_BAIDUPay");
				msg.what = 5002; 
				msg.arg2 =  ThirdPayHelper.Pay_BAIDUPay;
				msg.obj = resultStatus;
				payHandler.sendMessage(msg);
				PayCheckDialogActivity.sendBroadcast(c);
			}

			@Override
			public void baiduPayCancel(String resultInfo, String resultStatus) {
				    HttpStatistics.statistics(activity,mUserOrderId,URLFlag.BaidupayCancel,gameType,params);
				    LogUtils.e("baiduPayCancel--msg.arg2 = EPPayHelper.Pay_BAIDUPay");
					msg.what = 5002; 
					msg.arg2 =  ThirdPayHelper.Pay_BAIDUPay;
					msg.obj = resultStatus;
					payHandler.sendMessage(msg);
					PayCheckDialogActivity.sendBroadcast(c);
			}
		});
		   baiduPayUtils.pay(params.getProductName(), params.getProductDesc(), String.valueOf(params.getPrice()));
		   
		}
	}
	
	WXWapPayUtil wxWapPayUtil;
	WxWapHandler wxWapHandler;
	
	/**
	 * 微信wap支付
	 */
	public void wxWapPay(final PayParams params){
		
		
		final Message msg = payHandler.obtainMessage();
		if(c instanceof Activity){
			final Activity activity =c;
			payselect = ThirdPayHelper.Pay_WxWapPay;
			
			final ProgressDialog progressDialog = new ProgressDialog(activity);;
			progressDialog.setMessage("支付结果获取中...");
			//progressDialog.setCancelable(false);
			progressDialog.setCanceledOnTouchOutside(false);
			
			wxWapHandler = new WXWapPayUtil.WxWapHandler() {
				
				@Override
				public void wxWapSuccess(String resultInfo, String resultStatus) {
					HttpStatistics.statistics(activity,mUserOrderId,URLFlag.WxWapSuccess,gameType,params);
					LogUtils.e("wxWapSuccess--msg.arg2 = EPPayHelper.Pay_WxWapPay");
					msg.what = 5001; 
					msg.obj = resultStatus;
					msg.arg1 = params.getPrice();
					msg.arg2 = ThirdPayHelper.Pay_WxWapPay;
					payHandler.sendMessage(msg);
					if (progressDialog != null &&progressDialog.isShowing())
						progressDialog.dismiss();
					PayCheckDialogActivity.sendBroadcast(c);
					
					
				}
				
				@Override
				public void wxWapFailed(String resultInfo, String resultStatus) {
					HttpStatistics.statistics(activity,mUserOrderId,URLFlag.WxWapCancel,gameType,params);
					LogUtils.e("wxWapFailed--msg.arg2 = EPPayHelper.Pay_WxWapPay");
					msg.what = 5002; 
					msg.arg2 = ThirdPayHelper.Pay_WxWapPay;
					msg.obj = resultStatus;
					payHandler.sendMessage(msg);
					if (progressDialog != null &&progressDialog.isShowing())
						progressDialog.dismiss();
					PayCheckDialogActivity.sendBroadcast(c);
				}
			};
			
			if(ApkUtils.isWeixinAvilible(c)==false){
				Toast.makeText(activity, "手机没有安装微信，请先安装微信", Toast.LENGTH_SHORT).show();
				wxWapHandler.wxWapFailed("204", "微信没有安装");
				PayCheckDialogActivity.sendBroadcast(c);
				return;
			}

			progressDialog.show();
			
			wxWapPayUtil= new WXWapPayUtil(activity,params.getWebOrderid(),params.getCpOrderId(), wxWapHandler);
			
			wxWapPayUtil.pay(params.getProductName(), params.getProductDesc(), String.valueOf(params.getPrice()));
		}
		
	
	}
	
	
	
	
	private WXSwiftPayUtil wXSwiftPayUtil; 
	/**
	 *威富通微信支付
	 * @param params
	 */
	public void wxSwifPay(final PayParams params){
		final Message msg = payHandler.obtainMessage();
		if(c instanceof Activity){
			final Activity activity =c;
			payselect = ThirdPayHelper.Pay_WxWapSwiftPay;
			
			final ProgressDialog progressDialog = new ProgressDialog(activity);;
			progressDialog.setMessage("支付结果获取中...");
			//progressDialog.setCancelable(false);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.show();
			wXSwiftPayUtil= new WXSwiftPayUtil(activity,params.getWebOrderid(),params.getCpOrderId(), new WXSwiftPayUtil.WxSwiftPayHandler() {
				
				@Override
				public void wxWapSuccess(String resultInfo, String resultStatus) {
					HttpStatistics.statistics(activity,mUserOrderId,URLFlag.WxSwiftPaySuccess,gameType,params);
					msg.what = 5001; 
					msg.obj = resultStatus;
					msg.arg1 = params.getPrice();
					msg.arg2 = ThirdPayHelper.Pay_WxWapSwiftPay;
					payHandler.sendMessage(msg);
					progressDialog.dismiss();
					PayCheckDialogActivity.sendBroadcast(c);
					
				}
				
				@Override
				public void wxWapFailed(String resultInfo, String resultStatus) {
					HttpStatistics.statistics(activity,mUserOrderId,URLFlag.WxSwiftPayCancel,gameType,params);
					msg.what = 5002; 
					msg.obj = resultStatus;
					msg.arg2 = ThirdPayHelper.Pay_WxWapSwiftPay;
					payHandler.sendMessage(msg);
					progressDialog.dismiss();
					PayCheckDialogActivity.sendBroadcast(c);
				}
			});
			
			wXSwiftPayUtil.pay(params.getProductName(), params.getProductDesc(), String.valueOf(params.getPrice()));
		}
	}
	
	
	
	/**
	 * 微信和银联的支付回调
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	public  void onActivityResult(int requestCode, int resultCode, Intent data){
		if(payselect == ThirdPayHelper.Pay_UPPay){
			if(payUtil!=null){
				payUtil.onActivityResult(requestCode, resultCode, data);
			}
		}
		
		if(payselect == ThirdPayHelper.Pay_WXPay){
			if(wxPayUtil!=null){
				wxPayUtil.onActivityResult(requestCode, resultCode, data);
			}
		}
		
		if(payselect == ThirdPayHelper.Pay_WxWapPay){
			if(wxWapPayUtil!=null){
				wxWapPayUtil.onActivityResult(requestCode, resultCode, data);
			}
		}
		
		//威富通微信支付
		if(payselect == ThirdPayHelper.Pay_WxWapSwiftPay){
			if(wXSwiftPayUtil!=null){
				wXSwiftPayUtil.onActivityResult(requestCode, resultCode, data);
			}
		}
	}
	
	
}
