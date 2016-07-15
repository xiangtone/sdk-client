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
				Toast.makeText(MainActivity.this, "ʧ��-" + msg.obj.toString(), 1000).show();
				break;
			case 1078:
				Toast.makeText(MainActivity.this, "ʧ��*" + msg.what, 1000).show();
				break;
			case 4001:
				Toast.makeText(MainActivity.this, msg.what + "", 1000).show();
				break;
			case 4002:
				Toast.makeText(MainActivity.this, msg.what + "", 1000).show();
				break;
			case 4010:
				Toast.makeText(MainActivity.this, "��ʼ���ɹ�*" + msg.what, 1000).show();
				break;
			default:
				// Toast.makeText(MainActivity.this, "δ֪ԭ��*"+msg.what,
				// 1000).show();
				break;
			}

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		activity = MainActivity.this;
		// ��ʼ���ؼ�
		manety = (EditText) this.findViewById(R.id.shoppingmaney);
		name = (EditText) this.findViewById(R.id.shoppingname);
		but = (Button) this.findViewById(R.id.zhifu);
		but.setOnClickListener(onclick);

		// ��ʼ��SDK
		EPPayHelper.getInstance(this).initPay(true, "4001059566");
		EPPayHelper.getInstance(this).setPayListen(handler);
		Payment.init(this);
		
		
		
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
		
		

		// ˹��
//		EpsApplication payApplication = new EpsApplication();
//		payApplication.onStart(getApplicationContext());

	}

	// �û�����֧��
	OnClickListener onclick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			try {
				int sp_money = 0;
				sp_money = Integer.parseInt(manety.getText().toString());
				String sp_name = name.getText().toString();

				System.out.println("�û�������");

				EPPayHelper.getInstance(MainActivity.this).pay(sp_money,sp_name,"123");
				//startPay("1",sp_money+"",false);				

				System.out.println("�û����������");
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
