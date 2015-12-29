package com.Demo.Demo;

import com.Demoht_ep.Demo_ep_unify.R;
import com.cmnpay.api.Payment;
import com.cmnpay.api.PaymentCallback;
import com.epplus.publics.EPPayHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener{
	
	Button btn_nihe_net, btn_nihe_local, btn_normal_net, btn_normal_local;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
	}
	
	private void initView() {
		btn_nihe_net = (Button)findViewById(R.id.nihe_net);
		btn_nihe_local = (Button)findViewById(R.id.nihe_local);
		btn_normal_net = (Button)findViewById(R.id.normal_net);
		btn_normal_local = (Button)findViewById(R.id.normal_local);
		
		btn_nihe_net.setOnClickListener(this);
		btn_nihe_local.setOnClickListener(this);
		btn_normal_net.setOnClickListener(this);
		btn_normal_local.setOnClickListener(this);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
			case R.id.nihe_net: //拟合服务端
		
				startActivity(new Intent(this,NiheNetSdkDemo.class));
				
				break;
				
			case R.id.nihe_local: //拟合本地端
				startActivity(new Intent(this,NiheLocalSdkDemo.class));
				break;
				
			case R.id.normal_net: //普通服务端
				startActivity(new Intent(this,NormalNetSdkDemo.class));
				break;
				
			case R.id.normal_local: //普通本地端
				startActivity(new Intent(this,NormalLocalSdkDemo.class));
				break;
			default:
				break;
		}
		
	}
}
