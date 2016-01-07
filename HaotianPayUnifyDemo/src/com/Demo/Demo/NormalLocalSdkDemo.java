package com.Demo.Demo;

import com.Demoht_ep.Demo_ep_unify.R;
import com.cmnpay.api.Payment;
import com.epplus.publics.EPPayHelper;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NormalLocalSdkDemo extends Activity{


	private EditText manety;
	private EditText name;
	private Button but;
	private TextView text_title;
	
	private Context mContext;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			System.out.println(msg);
			switch (msg.what) {
			
			case 1070:
				Toast.makeText(mContext, "ʧ��-"+msg.obj.toString(), 1000).show();
				break;
			case 1078:
				Toast.makeText(mContext, "ʧ��*"+msg.what, 1000).show();
				break;
			case 4001:
				Toast.makeText(mContext, msg.what+"", 1000).show();
				break;
			case 4002:
				Toast.makeText(mContext, msg.what+"", 1000).show();
				break;
			case 4010:
				Toast.makeText(mContext, "��ʼ���ɹ�*"+msg.what, 1000).show();
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
		setContentView(R.layout.ht_sdk_demo);
		mContext =NormalLocalSdkDemo.this;
		// ��ʼ���ؼ�
		text_title = (TextView)findViewById(R.id.textView2);
		manety = (EditText) this.findViewById(R.id.shoppingmaney);
		name = (EditText) this.findViewById(R.id.shoppingname);
		but = (Button) this.findViewById(R.id.zhifu);
		but.setOnClickListener(onclick);	
		text_title.setText("���������Sdk_Demo");
		// ��ʼ��SDK
		EPPayHelper.getInstance(this).initPay(true,"4001059566","ep_normal_local");
		EPPayHelper.getInstance(this).setPayListen(handler);
		Payment.init(this);
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
			
			System.out.println("�û�������");
			
			EPPayHelper.getInstance(mContext).pay(sp_money,sp_name,"123");
			
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
			
			System.out.println("�û����������");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Toast.makeText(mContext, "f78e98df8cfc4745b0b1ef88581704e6", 1000).show();
			}
		}
	};
	  @Override  
	    protected void onDestroy() {  
	        super.onDestroy(); 
	        try {
	        	EPPayHelper.getInstance(mContext).exit();
			
			} catch (Exception e) {
				// TODO: handle exception
			}
			
	    }  
	  
	 





}