package com.epplus.publics;

import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.epplus.statistics.HttpStatistics;
import com.epplus.statistics.StatisURL;
import com.epplus.statistics.URLFlag;
import com.epplus.utils.AlipayUtils;
import com.epplus.utils.AlipayUtils.AlipayHandler;
import com.epplus.utils.PluginPayUtil;
import com.epplus.utils.PluginPayUtil.PluginHandler;
import com.epplus.utils.WXPayUtil;
import com.epplus.utils.WXPayUtil.WXPayHandler;
import com.epplus.view.PayCheckDialog;

public class EPPayHelper {
	
	/**支付宝支付*/
	private static final int Pay_AliPay = 1;
	/**银联支付*/
	private static final int Pay_UPPay = 2;
	/**微信支付*/
	private static final int Pay_WXPay = 3;
	
	//选择那个平台支付
	private int  payselect = 0;
	
	
	private static EPPayHelper epHelper = new EPPayHelper();
	private Context c;

	public static EPPayHelper getInstance(Context c) {
		EPPayHelper.epHelper.c = c;
		return epHelper;
	}

//	public void initPay(boolean isCheckLog, String payContact) {
//	}

	public void pay(int number, String note, String userOrderId) {
		if(c instanceof Activity){
			Activity activity = (Activity) c;
			PayCheckDialog payCheckDialog = new PayCheckDialog(activity, this, number, note, userOrderId);
			payCheckDialog.show();
			payselect=0;
		}
	}

	private Handler payHandler;
	public void initPay(Handler handler) {
		this.payHandler = handler;
	}


	/**
	 * 支付宝支付
	 * @param msg
	 * @param jsonObject
	 * @throws JSONException
	 */
	public  void alipay( String nochannel, String money,String commodity, String orderid) {
		
		final Message msg = payHandler.obtainMessage();
		if(c instanceof Activity){
			payselect = EPPayHelper.Pay_AliPay;
			final Activity activity = (Activity)c;
			AlipayUtils alipayUtils = new AlipayUtils(activity, new AlipayHandler() {
				
				@Override
				public void aliPaySuccess(String resultInfo, String resultStatus) {
					//Toast.makeText(activity, "aliPaySuccess>>"+resultStatus+">>"+resultInfo, 0).show();
					//HttpStatistics.newInstance().statistics(HttpStatistics.BASEURL+"?f=aliPaySuccess("+resultStatus+":"+resultInfo+")");
					
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
			//HttpStatistics.newInstance().statistics(HttpStatistics.BASEURL+"?f=pay");
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
					//HttpStatistics.newInstance().statistics(HttpStatistics.BASEURL+"?f=aliPaySuccess("+resultStatus+":"+resultInfo+")");
					
					HttpStatistics.statistics(activity,URLFlag.UnionpaySuccess);
					
					msg.what = 4001; 
					msg.obj = resultStatus;
					payHandler.sendMessage(msg);
					
				}

				@Override
				public void pluginPayFailed(String resultInfo,
						String resultStatus) {
					HttpStatistics.statistics(activity,URLFlag.UnionpayFail);
					//HttpStatistics.newInstance().statistics(HttpStatistics.BASEURL+"?f=aliPaySuccess("+resultStatus+":"+resultInfo+")");
					msg.what = 4002; 
					msg.obj = resultStatus;
					payHandler.sendMessage(msg);
					
				}

				@Override
				public void pluginPayCancel(String resultInfo,
						String resultStatus) {
					HttpStatistics.statistics(activity,URLFlag.UnionpayCancel);
					//HttpStatistics.newInstance().statistics(HttpStatistics.BASEURL+"?f=aliPaySuccess("+resultStatus+":"+resultInfo+")");
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
