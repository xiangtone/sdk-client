package com.Demo.Demo;

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

import com.Demoht_ep.Demo_ep.R;
import com.epplus.publics.EPPayHelper;

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
				Toast.makeText(MainActivity.this, "ʧ��-"+msg.obj.toString(), 1000).show();
				break;
			case 1078:
				Toast.makeText(MainActivity.this, "ʧ��*"+msg.what, 1000).show();
				break;
			case 4001:
				Toast.makeText(MainActivity.this, msg.what+"", 1000).show();
				break;
			case 4002:
				Toast.makeText(MainActivity.this, msg.what+"", 1000).show();
				break;
			case 4010:
				Toast.makeText(MainActivity.this, "��ʼ���ɹ�*"+msg.what, 1000).show();
				break;
			default:
//				Toast.makeText(MainActivity.this, "δ֪ԭ��*"+msg.what, 1000).show();
				break;
			}
			
			
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		activity =MainActivity.this;
		// ��ʼ���ؼ�
		manety = (EditText) this.findViewById(R.id.shoppingmaney);
		name = (EditText) this.findViewById(R.id.shoppingname);
		but = (Button) this.findViewById(R.id.zhifu);
		but.setOnClickListener(onclick);
		
		// ��ʼ��SDK
		EPPayHelper.getInstance(this).initPay(true,"4001059566");
		EPPayHelper.getInstance(this).setPayListen(handler);
		
	}

	// �û�����֧��
	OnClickListener onclick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			try {
			int sp_money =0;
			sp_money=Integer.parseInt(manety.getText().toString());
			String sp_name =name.getText().toString();
			
			EPPayHelper.getInstance(MainActivity.this).pay(sp_money,sp_name,"123");
			System.out.println("�û�������");
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
	  
	  
	  @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {		  		
		  
		EPPayHelper.getInstance(this).onActivityResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}
	  
	 
}
