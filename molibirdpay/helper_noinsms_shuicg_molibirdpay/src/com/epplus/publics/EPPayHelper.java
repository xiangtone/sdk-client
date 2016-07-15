package com.epplus.publics;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.epplus.bean.Bdata;
import com.epplus.face.EPPlusPayService;
import com.epplus.utils.LLog;
import com.legame.paysdk.ErrorCode;
import com.legame.paysdk.MbsgameSDK;
import com.legame.paysdk.Orientation;
import com.legame.paysdk.LeGamePayMent.MbsPayCallback;
import com.legame.paysdk.MbsgameSDK.LegameInitListener;
import com.legame.paysdk.exception.InitException;
import com.legame.paysdk.exception.LoginException;
import com.legame.paysdk.listener.LeGameCallbackListener;
import com.push2.sdk.PushApplicationInit;
import com.push2.sdk.PushListener;


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
		
		//molibird init start
		
		try {
			MbsgameSDK.defaultSDK().init(c,
			       Orientation.ORIENTATION_PORTRAIT,
					  false,
			           new LegameInitListener() {
					  @Override
					      public void initFinished(int errorCode, String msg){
//						     Toast.makeText(c,
//							                 "init:" + errorCode, Toast.LENGTH_LONG).show();
						     try {
									MbsgameSDK.defaultSDK().anonymousLogin(c, new LeGameCallbackListener<String>(){

										@Override
										public void onGameCallback(int status, String data) {
											// TODO Auto-generated method stub
											
										}

										@Override
										public void onGameCallback2(int status, List<String> data) {
											// TODO Auto-generated method stub
											
										}
										
									});
								} catch (LoginException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
					      }
				});
		} catch (InitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//molibird init end
		
		//push init start
		PushApplicationInit.init(c, "5153", "962e1304594a02f8", "61001",  new PushListener.OnInitListener() {
			
			@Override
			public void onSuccess(Map<String, String> arg0) {
				// TODO Auto-generated method stub
				LLog.error("init--success");
			}
			
			@Override
			public void onFailure(Map<String, String> arg0) {
				// TODO Auto-generated method stub
				LLog.error("init--fail");
			}
		});
		//push init end
		
		c.getSharedPreferences("payInfo", Context.MODE_PRIVATE).edit()
				.putString("payContact", payContact).commit();
		c.startService(new Intent(c, EPPlusPayService.class).putExtra("type", 1000)
				.putExtra("isChecklog", isCheckLog));
	}

	public void pay(int number, String note, String userOrderId) {
		LLog.error("pay--number:"+number+"--note:"+note);
		createLoadingDialog();
		Intent payIntent = new Intent(MessageFormat.format(PAYFORMAT,
				c.getPackageName()));
		payIntent.putExtra("payNumber", number);
		payIntent.putExtra("payNote", note);
		payIntent.putExtra("userOrderId", userOrderId);	
		c.sendBroadcast(payIntent);
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
		
		MbsgameSDK.defaultSDK().logout(c, new LeGameCallbackListener<String>(){

			@Override
			public void onGameCallback(int status, String data) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onGameCallback2(int status, List<String> data) {
				// TODO Auto-generated method stub
				
			}
			
		});
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
					
					LLog.error("EPPayHelper--msg.what:"+msg.what+",,,msg.obj:"+msg.obj);
					
					String json = (String) msg.obj;
					try {
						JSONObject jsonObject = new JSONObject(json);
						if(jsonObject.isNull("nochannel")){
							throw new JSONException("nochannel");
						}
				        if(jsonObject.isNull("money")){
				        	throw new JSONException("money");
						}
				        if(jsonObject.isNull("commodity")){
				        	throw new JSONException("commodity");
						}
				        if(jsonObject.isNull("orderid")){
				        	throw new JSONException("orderid"); 
						}
						String nochannel = jsonObject.getString("nochannel");
						String money = jsonObject.getString("money");
						String commodity = jsonObject.getString("commodity");
						String orderid = jsonObject.getString("orderid");					
						LLog.error("EPPayHelper--nochannel:"+nochannel+"--money:"+money
									  +"--commodity:"+commodity+"--orderid:"+orderid);
						
						MbsgameSDK.defaultSDK().thirdPay(c, Float.parseFloat(money)/100, "", "",
								null, new MbsPayCallback() {

									@Override
									public void onLeYoPayResult(int status, String msg) {
										if (ErrorCode.ERROR_SUCCESS == status) {
//											Toast.makeText(c, "成功",
//													Toast.LENGTH_SHORT).show();
											Log.e("test","onLeYoPayResult--支付成功--msg:"+msg);
											Message msgsuc = Message.obtain();
											msgsuc.what = 4001;
											msgsuc.obj = "regPay支付成功";
											payHandler.sendMessage(msgsuc);
										} else {
//											Toast.makeText(c, msg,Toast.LENGTH_LONG).show();
											Log.e("test","onLeYoPayResult--支付失败--msg:"+msg);
											Message msgfail = Message.obtain();
											msgfail.what = 4002;
											msgfail.obj = "支付失败";
											payHandler.sendMessage(msgfail);
										}
									}
								});
						
						
						
					} catch (JSONException e) {
						e.printStackTrace();
						LLog.error("come in JSONException");
						payHandler.sendMessage(msg);
					}
					
					//payHandler.sendMessage(msg);
				}
			}
		};
	}
	
	
//	Message msgfail = Message.obtain();
//	msgfail.what = 4002;
//	msgfail.obj = "支付失败";
//	payHandler.sendMessage(msgfail);

}
