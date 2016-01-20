package com.example.testdemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;

import com.epplus.publics.EPPayHelper;

public class MainActivity extends Activity implements OnClickListener {
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				new Thread() {
					public void run() {
						EPPayHelper.getInstance(MainActivity.this).pay(100,
								"test", null,"ep_nihe_net");
					};
				}.start();
				break;

			default:
				break;
			}
			System.out.println(msg);
		};
	};

	protected void onDestroy() {
		super.onDestroy();
		EPPayHelper.getInstance(this).exit("ep_nihe_net");
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EPPayHelper.getInstance(this).initPay(true, "0000","ep_nihe_net");
		EPPayHelper.getInstance(this).setPayListen(handler);
		EPPayHelper.getInstance(this).pay(1800, "jb", null,"ep_nihe_net");
	}

	@Override
	public void onClick(View v) {
		handler.sendEmptyMessage(0);
	}
}
