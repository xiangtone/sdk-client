package com.example.testdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.account.bean.UserInfo;
import com.epplus.publics.EPPayHelper2;
import com.epplus.view.PayParams;


public class MainActivity extends Activity{
	
	
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
			case 4011:
				UserInfo arg0 = (UserInfo) msg.obj;
				Toast.makeText(MainActivity.this, "�Զ���½�ɹ�*"+msg.what+
						arg0.getUserID()+arg0.getUserName(), Toast.LENGTH_SHORT).show();
				break;
			case 4012:
				UserInfo arg1 = (UserInfo) msg.obj;
				Toast.makeText(MainActivity.this, "��½�ɹ�*"+msg.what+
						arg1.getUserID()+arg1.getUserName(), Toast.LENGTH_SHORT).show();
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
		XTSDK.getInstance().init(this,"4001059566",handler);
		
		Log.e("zgt", "onCreate");
	}
	
	
	public void onMyclick(View view){
		switch (view.getId()) {
		case R.id.button1:
			 
			break;
		case R.id.button2:
			XTSDK.getInstance().login(this);
			break;
		case R.id.button3:
			XTSDK.getInstance().logout();
			Toast.makeText(this, "�˳�", Toast.LENGTH_SHORT).show();
			break;
		case R.id.button4:
			PayParams pa = new PayParams(1, "12345", "ƻ��", "��Ʒ������ƻ����ƷidΪ123456");
			boolean b = XTSDK.getInstance().pay(this,pa);
			if(!b){
				XTSDK.getInstance().login(this);
				Toast.makeText(this, "�㻹δ��¼", Toast.LENGTH_SHORT).show();
			}
			break;
			
			case R.id.button5:
				PayParams params = new PayParams(1, "12345", "ƻ��", "��Ʒ������ƻ����ƷidΪ123456");
				EPPayHelper2.getInstance(this).setPayListen(handler);
				EPPayHelper2.getInstance(this).pay(params);
				break;

		default:
			break;
		}
	}
	
	
	 @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			XTSDK.getInstance().payCallResult(this,requestCode, resultCode, data);
			super.onActivityResult(requestCode, resultCode, data);
		}
	 
	 @Override  
	    protected void onDestroy() {  
	        super.onDestroy(); 
	        try {
	        	XTSDK.getInstance().exit(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
	    }  

}
