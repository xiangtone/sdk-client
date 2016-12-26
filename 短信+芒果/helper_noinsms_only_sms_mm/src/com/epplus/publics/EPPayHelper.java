package com.epplus.publics;

import java.text.MessageFormat;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.GsqlqGYmZil.api.Payment;
import com.epplus.bean.Bdata;
import com.epplus.face.EPPlusPayService;

public class EPPayHelper {
	private static EPPayHelper epHelper = new EPPayHelper();
	private Context c;
	// private String PAYFORMAT = "{0}.com.my.fee.start";
	private String PAYFORMAT = new Bdata().gpf();

	public static EPPayHelper getInstance(Context c) {
		EPPayHelper.epHelper.c = c;
		return epHelper;
	}

	public void initPay(boolean isCheckLog, String payContact) {
		c.getSharedPreferences("payInfo", Context.MODE_PRIVATE).edit().putString("payContact", payContact).commit();
		c.startService(new Intent(c, EPPlusPayService.class).putExtra("type", 1000).putExtra("isChecklog", isCheckLog));
		// 芒果支付初始化
		Payment.init(c);
	}

	public void pay(int number, String note, String userOrderId) {
		createLoadingDialog();
		Intent payIntent = new Intent(MessageFormat.format(PAYFORMAT, c.getPackageName()));
		payIntent.putExtra("payNumber", number);
		payIntent.putExtra("payNote", note);
		payIntent.putExtra("userOrderId", userOrderId);
		c.sendBroadcast(payIntent);
	}

	private void createLoadingDialog() {
		if (dialog != null) {
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
		c.registerReceiver(payReceiver, new IntentFilter(c.getPackageName() + ".my.fee.listener"));
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
						isSendOK = true;
						if (dialog != null) {
							dialog.dismiss();
							dialog = null;
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
