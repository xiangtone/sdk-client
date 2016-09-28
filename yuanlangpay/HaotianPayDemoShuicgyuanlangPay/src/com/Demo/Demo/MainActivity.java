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
		
		//System.loadLibrary("yummy");

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
	
	private static final int REQUEST_TYPE = 100;
	
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		Log.e("test", "onActivityResult---requestCode:"+requestCode);
//		
//		if (requestCode == REQUEST_TYPE) {// 请求的requestCode
//			Bundle bundle = data.getExtras();
//			Log.e("test", "---bundle---:"+bundle.toString());
//			if (null != bundle) {
//				boolean is_success = (resultCode == 100);// 是否成功
//				String real_price = "" + bundle.getInt("order_price");// 本次支付费用
//				String user_order_id = "" + bundle.getString("order_id");// 商户定义的订单号
//				String error_code = "" + bundle.getString("pay_result_id");// 支付结果错误编号
//				String error_msg = "" + bundle.getString("pay_result_msg");// 失败时返回的错误原因
//				if (is_success) {
//					// 支付成功（游戏跳转或提示界面）
//					// Toast测试使用，建议游戏端自定义提示窗口
//					Toast.makeText(MainActivity.this, "支付成功："+real_price, Toast.LENGTH_LONG).show();
////					 setExecuteStatus(EXECUTE_STATUS_COMPLETE);
////					 Log.e("test", "支付成功："+",--real_price"+real_price);
////					 ylPayStatus = YL_PAY_OK;
////					 payOk();
//				} else {
//					// 支付失败（游戏跳转或提示界面）
//					// Toast测试使用，建议游戏端自定义提示窗口
//					Toast.makeText(MainActivity.this, "支付失败:" + error_msg+"---"+error_code, Toast.LENGTH_LONG).show();
////					 setExecuteStatus(EXECUTE_STATUS_COMPLETE);
////					 Log.e("test","支付失败："+error_msg+",---编号："+error_code);
////					 ylPayStatus = YL_PAY_FAIL;
////					 payFail();
//					 
//				}
//			} 
//		}
//	}

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
				
//				AppTache.requestPay(MainActivity.this// 请求页面的activity实例
//						, false// 默认false
//						, sp_money// 商品价格，单位分
//						, 1// 商品数量默认1
//						, "P160803TUZ9"// 商品ID，也叫计费点，由支付平台生成
//						, "商店2元金币" // 商品名称，计费点对应名称
//						, System.currentTimeMillis()+"" // 订单号，商户自定义
//						, REQUEST_TYPE// 自定义请求ID，在处理结果处作为requestCode，见2.4.3
//										// 作用类似android的
//										// startActivityForResult里的requestCode
//						);

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
