package com.Demo.Demo;

import com.cmnpay.api.Payment;
import com.epplus.publics.EPPayHelper;
import com.tinyhorse.parkour2.wjwz1.R;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
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
		// ��ʼ���ؼ�
		manety = (EditText) this.findViewById(R.id.shoppingmaney);
		name = (EditText) this.findViewById(R.id.shoppingname);
		but = (Button) this.findViewById(R.id.zhifu);
		but.setOnClickListener(onclick);
		
		//System.loadLibrary("yummy");

		// ��ʼ��SDK
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
		
		

		// ˹��
//		EpsApplication payApplication = new EpsApplication();
//		payApplication.onStart(getApplicationContext());

	}
	
	private static final int REQUEST_TYPE = 100;
	
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		Log.e("test", "onActivityResult---requestCode:"+requestCode);
//		
//		if (requestCode == REQUEST_TYPE) {// �����requestCode
//			Bundle bundle = data.getExtras();
//			Log.e("test", "---bundle---:"+bundle.toString());
//			if (null != bundle) {
//				boolean is_success = (resultCode == 100);// �Ƿ�ɹ�
//				String real_price = "" + bundle.getInt("order_price");// ����֧������
//				String user_order_id = "" + bundle.getString("order_id");// �̻�����Ķ�����
//				String error_code = "" + bundle.getString("pay_result_id");// ֧�����������
//				String error_msg = "" + bundle.getString("pay_result_msg");// ʧ��ʱ���صĴ���ԭ��
//				if (is_success) {
//					// ֧���ɹ�����Ϸ��ת����ʾ���棩
//					// Toast����ʹ�ã�������Ϸ���Զ�����ʾ����
//					Toast.makeText(MainActivity.this, "֧���ɹ���"+real_price, Toast.LENGTH_LONG).show();
////					 setExecuteStatus(EXECUTE_STATUS_COMPLETE);
////					 Log.e("test", "֧���ɹ���"+",--real_price"+real_price);
////					 ylPayStatus = YL_PAY_OK;
////					 payOk();
//				} else {
//					// ֧��ʧ�ܣ���Ϸ��ת����ʾ���棩
//					// Toast����ʹ�ã�������Ϸ���Զ�����ʾ����
//					Toast.makeText(MainActivity.this, "֧��ʧ��:" + error_msg+"---"+error_code, Toast.LENGTH_LONG).show();
////					 setExecuteStatus(EXECUTE_STATUS_COMPLETE);
////					 Log.e("test","֧��ʧ�ܣ�"+error_msg+",---��ţ�"+error_code);
////					 ylPayStatus = YL_PAY_FAIL;
////					 payFail();
//					 
//				}
//			} 
//		}
//	}

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
				
//				AppTache.requestPay(MainActivity.this// ����ҳ���activityʵ��
//						, false// Ĭ��false
//						, sp_money// ��Ʒ�۸񣬵�λ��
//						, 1// ��Ʒ����Ĭ��1
//						, "P160803TUZ9"// ��ƷID��Ҳ�мƷѵ㣬��֧��ƽ̨����
//						, "�̵�2Ԫ���" // ��Ʒ���ƣ��Ʒѵ��Ӧ����
//						, System.currentTimeMillis()+"" // �����ţ��̻��Զ���
//						, REQUEST_TYPE// �Զ�������ID���ڴ���������ΪrequestCode����2.4.3
//										// ��������android��
//										// startActivityForResult���requestCode
//						);

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
