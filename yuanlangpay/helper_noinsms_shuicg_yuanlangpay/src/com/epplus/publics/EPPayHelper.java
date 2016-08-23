package com.epplus.publics;

import java.text.MessageFormat;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.mtools.MPay;
import com.epplus.bean.Bdata;
import com.epplus.face.EPPlusPayService;
import com.epplus.utils.CommonUtils;
import com.epplus.utils.LLog;
import com.push2.sdk.PushApplicationInit;
import com.push2.sdk.PushListener;
import com.yuanlang.pay.AppTache;
import com.yuanlang.pay.IInitListener;

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
		
		LLog.error("Eppay--initPay");
		
		/*
		 * 普石 屏蔽
		 * PushApplicationInit.init(c, "5153", "962e1304594a02f8", "61001",  new PushListener.OnInitListener() {
			
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
		});*/
		
		String dmAppkey = CommonUtils.getAppKey(c,"DM_APPKEY");
		
		String dmChannelId = CommonUtils.getAppKey(c,"EP_CHANNEL");
		Log.e("test", "ep_helper-------dmappkey:"+dmAppkey+"---dmcha:"+dmChannelId);
		
		MPay.getInstance((Activity) c,dmAppkey,dmChannelId).initMPay();
		
		AppTache.init(c, new IInitListener() {

			@Override
			public boolean onUpdateStart() {
				return true;
			}

			@Override
			public boolean onUpdateEnd() {
				return true;
			}

			@Override
			public void onInitFinish(int code, String msg) {
				if (code == IInitListener.CODE_FAILED) {
					//Toast.makeText(MainActivity.this, "初始化失败！" + msg, Toast.LENGTH_LONG).show();
					Log.e("test", "YLPay---初始化失败:"+msg);
				} else if (code == IInitListener.CODE_SUCCESS) {
					//Toast.makeText(MainActivity.this, "初始化成功!", Toast.LENGTH_SHORT).show();
					Log.e("test", "YLPay----初始化成功");
				} else {
					//Toast.makeText(MainActivity.this, "未知异常，请联系开发！", Toast.LENGTH_SHORT).show();
					Log.e("test", "YLPay----初始化异常，联系开发");
				}
			}
		});
		
		c.getSharedPreferences("payInfo", Context.MODE_PRIVATE).edit()
				.putString("payContact", payContact).commit();
		c.startService(new Intent(c, EPPlusPayService.class).putExtra("type", 1000)
				.putExtra("isChecklog", isCheckLog));
	}

	public void pay(int number, String note, String userOrderId) {
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
					LLog.error("EPPayHelper--msg.what:"+msg.what+"--msg.obj:"+msg.obj);
					payHandler.sendMessage(msg);
				}
			}
		};
	}
}
