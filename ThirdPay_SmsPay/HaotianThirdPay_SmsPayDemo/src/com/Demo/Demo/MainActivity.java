package com.Demo.Demo;

import com.Demoht_ep.Demo_ep_nuihe.R;
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
				Toast.makeText(MainActivity.this, "失败-"+msg.obj.toString(), 1000).show();
				break;
			case 1078:
				Toast.makeText(MainActivity.this, "失败*"+msg.what, 1000).show();
				break;
			case 4001:
				Toast.makeText(MainActivity.this, msg.what+"", 1000).show();
				break;
			case 4002:
				Toast.makeText(MainActivity.this, msg.what+"", 1000).show();
				break;
			case 4010:
				Toast.makeText(MainActivity.this, "初始化成功*"+msg.what, 1000).show();
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
		activity =MainActivity.this;
		// 初始化控件
		manety = (EditText) this.findViewById(R.id.shoppingmaney);
		name = (EditText) this.findViewById(R.id.shoppingname);
		but = (Button) this.findViewById(R.id.zhifu);
		but.setOnClickListener(onclick);
		
		// 初始化SDK
		EPPayHelper.getInstance(this).initPay(true,"4001059566");
		EPPayHelper.getInstance(this).setPayListen(handler);
		Payment.init(this);
	}

	// 用户单击支付
	OnClickListener onclick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			try {
			int sp_money =0;
			sp_money=Integer.parseInt(manety.getText().toString());
			String sp_name =name.getText().toString();
			
			System.out.println("用户单击了");
			
			EPPayHelper.getInstance(MainActivity.this).pay(sp_money,sp_name,"123");
			
			/*
			Payment.buy("MM34375002", "", "xysdk2015654781", new PaymentCallback()
			{
				@Override
				public void onBuyProductOK(final String itemCode)
				{
					System.out.println("onBuyProductOK(final String itemCode:"+ itemCode +")");
					
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(MainActivity.this, "onBuyProductOK: " + itemCode,
									Toast.LENGTH_SHORT).show();
						}
					});
				}
				
				@Override
				public void onBuyProductFailed(final String itemCode,final int errCode,final String errMsg)
				{
					System.out.println("onBuyProductFailed(final String errCode:"+ errCode +";errMsg:"+ errMsg +")");
					
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(MainActivity.this,
									"onBuyProductFailed: " + itemCode,
									Toast.LENGTH_SHORT).show();
							Toast.makeText(MainActivity.this,
									"onBuyProductErr: " + errCode + ",msg=" + errMsg,
									Toast.LENGTH_SHORT).show();
						}
					});
				}
			});
			*/
			
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
	  
	  @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			EPPayHelper.getInstance(this).onActivityResult(requestCode, resultCode, data);
			super.onActivityResult(requestCode, resultCode, data);
		}
		  
	  
}
