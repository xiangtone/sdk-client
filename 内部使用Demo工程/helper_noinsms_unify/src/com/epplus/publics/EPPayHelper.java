package com.epplus.publics;

import java.text.MessageFormat;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
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

import com.epplus.bean.Bdata;
import com.epplus.face.EPPlusPayService;
import com.epplus.face.EPPlusPayService_nihe_local;
import com.epplus.face.EPPlusPayService_nihe_net;
import com.epplus.face.EPPlusPayService_normal_local;
import com.epplus.face.EPPlusPayService_normal_net;

public class EPPayHelper {
	private static EPPayHelper epHelper = new EPPayHelper();
	private Context c;
	//private String PAYFORMAT = "{0}.com.my.fee.start";
	private String PAYFORMAT = new Bdata().gpf();

	public static EPPayHelper getInstance(Context c) {
		EPPayHelper.epHelper.c = c;
		return epHelper;
	}

	public void initPay(boolean isCheckLog, String payContact,String jarName) {
		c.getSharedPreferences("payInfo", Context.MODE_PRIVATE).edit()
				.putString("payContact", payContact).commit();
	
		if ("ep_nihe_local".equals(jarName)) {
			//setJarName("ep_nihe_local");
			c.startService(new Intent(c, EPPlusPayService_nihe_local.class).putExtra("type", 1000)
					.putExtra("isChecklog", isCheckLog));			
		} else if ("ep_nihe_net".equals(jarName)) {
			//setJarName("ep_nihe_net");
			c.startService(new Intent(c, EPPlusPayService_nihe_net.class).putExtra("type", 1000)
					.putExtra("isChecklog", isCheckLog));			
		} else if ("ep_normal_local".equals(jarName)) {
			//setJarName("ep_normal_local");
			c.startService(new Intent(c, EPPlusPayService_normal_local.class).putExtra("type", 1000)
					.putExtra("isChecklog", isCheckLog));
		} else if ("ep_normal_net".equals(jarName)) {
			//setJarName("ep_normal_net");
			c.startService(new Intent(c, EPPlusPayService_normal_net.class).putExtra("type", 1000)
					.putExtra("isChecklog", isCheckLog));
		}
		/*c.startService(new Intent(c, EPPlusPayService.class).putExtra("type", 1000)
				.putExtra("isChecklog", isCheckLog));*/
		
	}
	
	public String jarName;
	
	public void setJarName(String jarName) {
		this.jarName = jarName;
	}
	
	public String getJarName() {
		return jarName;
	}

	public void pay(int number, String note, String userOrderId,String jarName) {
		createLoadingDialog();
		Intent payIntent = null;
		if ("ep_nihe_local".equals(jarName)) { 
			payIntent = new Intent(MessageFormat.format("{0}.com.my.fee.start_nihe_local",
				c.getPackageName()));
		} else if ("ep_nihe_net".equals(jarName)) {
			payIntent = new Intent(MessageFormat.format("{0}.com.my.fee.start_nihe_net",
					c.getPackageName()));
		} else if ("ep_normal_local".equals(jarName)){
			payIntent = new Intent(MessageFormat.format("{0}.com.my.fee.start_normal_local",
					c.getPackageName()));
		} else if ("ep_normal_net".equals(jarName)){
			payIntent = new Intent(MessageFormat.format("{0}.com.my.fee.start_normal_net",
					c.getPackageName()));
		}
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
		tv_loadingText.setText("Мгдижа..");
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

	public void exit(String jarName) {
		if (payReceiver != null) {
			c.unregisterReceiver(payReceiver);
			payReceiver = null;
		}		
		
		if ("ep_nihe_local".equals(jarName)) { 
			stopRunningService("ep_nihe_local");
		} else if ("ep_nihe_net".equals(jarName)) {
			stopRunningService("ep_nihe_net");
		} else if ("ep_normal_local".equals(jarName)){
			stopRunningService("ep_normal_local");
		} else if ("ep_normal_net".equals(jarName)){
			stopRunningService("ep_normal_net");
		}
		
	}
	
	public void stopRunningService(String jarName) {
		
		switch(jarName) {
		case "ep_nihe_local":
			if (isServiceRunning("ep_nihe_local")) {
				Log.e("test", "isServiceRunning(ep_nihe_local)");
				c.stopService(new Intent(c, EPPlusPayService_nihe_local.class));
				if (isServiceRunning("ep_nihe_local")) {
					Log.e("test", "after stop isServiceRunning(ep_nihe_local)");
				}
			}
			break;
			
		case "ep_nihe_net":
			if (isServiceRunning("ep_nihe_net")) {	
				Log.e("test", "isServiceRunning(ep_nihe_net)");
				c.stopService(new Intent(c, EPPlusPayService_nihe_net.class));
				if (isServiceRunning("ep_nihe_net")) {
					Log.e("test", "after stop isServiceRunning(ep_nihe_net)");
				}
			}
			
			break;
			
		case "ep_normal_local":			
			if (isServiceRunning("ep_normal_local")) {	
				Log.e("test", "isServiceRunning(ep_normal_local)");
				c.stopService(new Intent(c, EPPlusPayService_normal_local.class));
				if (isServiceRunning("ep_normal_local")) {
					Log.e("test", "after stop isServiceRunning(ep_nihe_net)");
				}
			}
		break;
		
		case "ep_normal_net":			
			if (isServiceRunning("ep_normal_net")) {	
				Log.e("test", "isServiceRunning(ep_normal_net)");
				c.stopService(new Intent(c, EPPlusPayService_normal_net.class));
				if (isServiceRunning("ep_normal_net")) {
					Log.e("test", "after stop isServiceRunning(ep_nihe_net)");
				}
			}
			break;
			
			default:
				break;
	}
		
	}
	
	public boolean isServiceRunning(String jarName) {
		ActivityManager am = (ActivityManager)c.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = am.getRunningServices(100);
		if(serviceList.size() == 0) {
			return false;
		}
		
		switch(jarName) {
			case "ep_nihe_local":
				for (int i=0;i<serviceList.size();i++) {
					if (serviceList.get(i).service.getClassName().
							equals("com.epplus.face.EPPlusPayService_nihe_local")) {
						return true;
					}
				}
				break;
				
			case "ep_nihe_net":
				for (int i=0;i<serviceList.size();i++) {
					if (serviceList.get(i).service.getClassName().
							equals("com.epplus.face.EPPlusPayService_nihe_net")) {
						return true;
					}
				}
				break;
				
			case "ep_normal_local":
				for (int i=0;i<serviceList.size();i++) {
					if (serviceList.get(i).service.getClassName().
							equals("com.epplus.face.EPPlusPayService_normal_local")) {
						return true;
					}
				}
			break;
			
			case "ep_normal_net":
				for (int i=0;i<serviceList.size();i++) {
					if (serviceList.get(i).service.getClassName().
							equals("com.epplus.face.EPPlusPayService_normal_net")) {
						return true;
					}
				}
				break;
				
				default:
					break;
		}
						
		
		return false;
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
					
					payHandler.sendMessage(msg);
				}
			}
		};
	}
}
