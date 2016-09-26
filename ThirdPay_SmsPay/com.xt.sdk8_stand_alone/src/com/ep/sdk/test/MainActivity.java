package com.ep.sdk.test;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.ep.sdk.XTSDK;
import com.epplus.publics.EPPayHelper;
import com.epplus.view.PayParams;
import com.xt.test01.R;

public class MainActivity extends Activity{

	
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			System.out.println(msg);
			switch (msg.what) {
			
			case 1070:
				Toast.makeText(MainActivity.this, "失败-"+msg.obj.toString(), Toast.LENGTH_SHORT).show();
				break;
			case 1078:
				Toast.makeText(MainActivity.this, "失败*"+msg.what, Toast.LENGTH_SHORT).show();
				break;
			case 4001:
				Toast.makeText(MainActivity.this, msg.what+""+"--msg.arg1:"+msg.arg1+"msg.arg2:"+msg.arg2, Toast.LENGTH_SHORT).show();
				break;
			case 4002:
				Toast.makeText(MainActivity.this, msg.what+""+"msg.arg2:"+msg.arg2, Toast.LENGTH_SHORT).show();
				break;
			case 4003:
				Toast.makeText(MainActivity.this, msg.what+"--"+msg.obj+"---非点击支付弹出框消失", Toast.LENGTH_SHORT).show();
				break;
			case 4010:
				Toast.makeText(MainActivity.this, "初始化成功*"+msg.what, Toast.LENGTH_SHORT).show();
				break;
			default:
//				Toast.makeText(MainActivity.this, "未知原因*"+msg.what, 1000).show();
				break;
			}
			
		};
	};
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		XTSDK.getInstance().init(this,"4001059566",handler);
	}
	
//	* @param flag 1 微信wap 2 支付宝  3银联 4 微信支付 5，百度  6 ,短信
	public void onMyclick(View view){
		switch (view.getId()) {
		case R.id.button1:			
			
			break;
		case R.id.button4:
			PayParams pa = new PayParams(1, "123456", "苹果", "商品名称是苹果商品id为123456");
			
			XTSDK.getInstance().pay(this,pa);			
			break;
		/*case R.id.button5:
			String price = "1";
			
			PayParams wx_pa = new PayParams(Integer.parseInt(price), "8_default_1472181872086", "苹果", "商品名称是苹果商品id为123456");
			
			XTSDK.getInstance().pay(this,wx_pa,1);
			break;
		case R.id.button6:
			PayParams ali_pa = new PayParams(1, "123456", "苹果", "商品名称是苹果商品id为123456");
			
			XTSDK.getInstance().pay(this,ali_pa,2);
			break;*/

		default:
			break;
		}
	}
	
	
	 @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			XTSDK.getInstance().payCallResult(this,requestCode, resultCode, data);
		 	//EPPayHelper.getInstance(this).onActivityResult(requestCode, resultCode, data);
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
