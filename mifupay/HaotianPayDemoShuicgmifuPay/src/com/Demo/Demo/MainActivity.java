package com.Demo.Demo;

import java.util.HashMap;
import java.util.Map;
import com.Demoht_ep.Demo_ep_mifupay.R;
import com.cmnpay.api.Payment;
import com.cmnpay.api.PaymentCallback;
import com.epplus.publics.EPPayHelper;
import com.push2.sdk.PushApplicationInit;
import com.push2.sdk.PushListener;
import com.push2.sdk.PushSDK;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	private EditText manety;
	private EditText name;
	private Button but;

	private MainActivity activity;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			System.out.println(msg);
			switch (msg.what) {

			case 1070:
				Toast.makeText(MainActivity.this, "失败-" + msg.obj.toString(), 1000).show();
				break;
			case 1078:
				Toast.makeText(MainActivity.this, "失败*" + msg.what, 1000).show();
				break;
			case 4001:
				Toast.makeText(MainActivity.this, msg.what + "", 1000).show();
				break;
			case 4002:
				Toast.makeText(MainActivity.this, msg.what + "", 1000).show();
				break;
			case 4010:
				Toast.makeText(MainActivity.this, "初始化成功*" + msg.what, 1000).show();
				break;
			default:
				// Toast.makeText(MainActivity.this, "未知原因*"+msg.what,
				// 1000).show();
				break;
			}

		};
	};
	
	private  String getYmAppKey(Context c) {
		try {
			ApplicationInfo ai = c.getPackageManager().getApplicationInfo(
					c.getPackageName(), PackageManager.GET_META_DATA);
			Object EP_APPKEY = ai.metaData.get("YM_APPKEY");
			if (EP_APPKEY instanceof Integer) {
				long longValue = ((Integer) EP_APPKEY).longValue();
				String value = String.valueOf(longValue);
				return value;
			} else if (EP_APPKEY instanceof String) {
				String value = String.valueOf(EP_APPKEY);
				return value;
			}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
		}
		return null;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		activity = MainActivity.this;
		// 初始化控件
		manety = (EditText) this.findViewById(R.id.shoppingmaney);
		name = (EditText) this.findViewById(R.id.shoppingname);
		but = (Button) this.findViewById(R.id.zhifu);
		but.setOnClickListener(onclick);
		
		System.loadLibrary("yummy");

		// 初始化SDK
		EPPayHelper.getInstance(this).initPay(true, "4001059566");
		EPPayHelper.getInstance(this).setPayListen(handler);
		Payment.init(this);
		
		
		
		/*
		String ymAppkey = getYmAppKey(MainActivity.this);
		YMBillingInterface.init(MainActivity.this, ymAppkey, 0, new YMBillingCallback() {
				@Override
				public void onInitSuccess(String extra) {
					Log.e("test","yubill--psuccess");
				}

				@Override
				public void onInitFail(String extra, int code) {
					Log.e("test","yubill--pfail");
				}

				@Override
				public void onSuccess(String chargepoint) {
				}

				@Override
				public void onCancel(String chargepoint) {
				}

				@Override
				public void onFail(String chargepoint, int code) {
				}
			});*/
		
		
		
		
		/*PushApplicationInit.init(MainActivity.this, "5153", "962e1304594a02f8", "61001",  new PushListener.OnInitListener() {
			
			@Override
			public void onSuccess(Map<String, String> arg0) {
				// TODO Auto-generated method stub
				Log.e("test","init--success");
			}
			
			@Override
			public void onFailure(Map<String, String> arg0) {
				// TODO Auto-generated method stub
				Log.e("test","init--fail");
			}
		});*/
		
		

		// 斯凯
//		EpsApplication payApplication = new EpsApplication();
//		payApplication.onStart(getApplicationContext());

	}

	// 用户单击支付
	OnClickListener onclick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			try {
				int sp_money = 0;
				sp_money = Integer.parseInt(manety.getText().toString());
				String sp_name = name.getText().toString();

				System.out.println("用户单击了");

				EPPayHelper.getInstance(MainActivity.this).pay(sp_money,sp_name,"123");
				//startPay("1",sp_money+"",false);				

				System.out.println("用户单击完毕了");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Toast.makeText(MainActivity.this, "f78e98df8cfc4745b0b1ef88581704e6", 1000).show();
			}
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			EPPayHelper.getInstance(activity).exit();

		} catch (Exception e) {
			// TODO: handle exception
		}

	}
	
	

}
