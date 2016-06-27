package com.Demo.Demo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.Demoht_ep.Demo_ep_moliBird.R;
import com.cmnpay.api.Payment;
import com.cmnpay.api.PaymentCallback;
import com.epplus.publics.EPPayHelper;
import com.legame.paysdk.ErrorCode;
import com.legame.paysdk.MbsgameSDK;
import com.legame.paysdk.MbsgameSDK.LegameInitListener;
import com.legame.paysdk.Orientation;
import com.legame.paysdk.LeGamePayMent.MbsPayCallback;
import com.legame.paysdk.exception.InitException;
import com.legame.paysdk.exception.LoginException;
import com.legame.paysdk.listener.LeGameCallbackListener;
import com.skymobi.pay.sdk.normal.zimon.EpsApplication;
import com.skymobi.pay.sdk.normal.zimon.EpsEntry;
import com.skymobi.pay.sdk.normal.zimon.util.SkyPaySignerInfo;

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

		// 初始化SDK
		EPPayHelper.getInstance(this).initPay(true, "4001059566");
		EPPayHelper.getInstance(this).setPayListen(handler);
		Payment.init(this);

		// 斯凯
		EpsApplication payApplication = new EpsApplication();
		payApplication.onStart(getApplicationContext());
		
		//魔力小鸟
		
		try {
			MbsgameSDK.defaultSDK()
			.init(this,
			       Orientation.ORIENTATION_PORTRAIT,
					  false,
			           new LegameInitListener() {
					  @Override
					      public void initFinished(int errorCode, String msg){
						     Toast.makeText(MainActivity.this,
							                 "init:" + errorCode, Toast.LENGTH_LONG).show();
						     try {
									MbsgameSDK.defaultSDK().anonymousLogin(MainActivity.this, new LeGameCallbackListener<String>(){

										@Override
										public void onGameCallback(int status, String data) {
											// TODO Auto-generated method stub
											
										}

										@Override
										public void onGameCallback2(int status, List<String> data) {
											// TODO Auto-generated method stub
											
										}
										
									});
								} catch (LoginException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
					      }
				});
		} catch (InitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		


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
//				startPay("1",sp_money+"",false);
				//startPay(String payPoint, String payPrice, boolean useAppUi)
				
//				MbsgameSDK.defaultSDK().thirdPay(MainActivity.this, (float)sp_money/100, "", "",
//						null, new MbsPayCallback() {
//
//							@Override
//							public void onLeYoPayResult(int status, String msg) {
//								if (ErrorCode.ERROR_SUCCESS == status) {
//									Toast.makeText(MainActivity.this, "成功",
//											Toast.LENGTH_SHORT).show();
//								} else {
//									Toast.makeText(MainActivity.this, msg,
//											Toast.LENGTH_LONG).show();
//								}
//							}
//						});
				
				/*
				 * Payment.buy("MM34375002", "", "xysdk2015654781", new
				 * PaymentCallback() {
				 * 
				 * @Override public void onBuyProductOK(final String itemCode) {
				 * System.out.println("onBuyProductOK(final String itemCode:"+
				 * itemCode +")");
				 * 
				 * runOnUiThread(new Runnable() {
				 * 
				 * @Override public void run() {
				 * Toast.makeText(MainActivity.this, "onBuyProductOK: " +
				 * itemCode, Toast.LENGTH_SHORT).show(); } }); }
				 * 
				 * @Override public void onBuyProductFailed(final String
				 * itemCode,final int errCode,final String errMsg) {
				 * System.out.println("onBuyProductFailed(final String errCode:"
				 * + errCode +";errMsg:"+ errMsg +")");
				 * 
				 * runOnUiThread(new Runnable() {
				 * 
				 * @Override public void run() {
				 * Toast.makeText(MainActivity.this, "onBuyProductFailed: " +
				 * itemCode, Toast.LENGTH_SHORT).show();
				 * Toast.makeText(MainActivity.this, "onBuyProductErr: " +
				 * errCode + ",msg=" + errMsg, Toast.LENGTH_SHORT).show(); } });
				 * } });
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
		
		MbsgameSDK.defaultSDK().logout(MainActivity.this, new LeGameCallbackListener<String>(){

			@Override
			public void onGameCallback(int status, String data) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onGameCallback2(int status, List<String> data) {
				// TODO Auto-generated method stub
				
			}
			
		});

	}
	
	////斯凯接口
	
	private static final String tag = "StartSmsPay";

	// 订单参数
	private static final String ORDER_INFO_PAY_METHOD = "payMethod";
	private static final String ORDER_INFO_SYSTEM_ID = "systemId";
	private static final String ORDER_INFO_CHANNEL_ID = "channelId";
	private static final String ORDER_INFO_PAY_POINT_NUM = "payPointNum";
	private static final String ORDER_INFO_ORDER_DESC = "orderDesc";
	private static final String ORDER_INFO_GAME_TYPE = "gameType";

	private static final String STRING_MSG_CODE = "msg_code";
	private static final String STRING_ERROR_CODE = "error_code";
	private static final String STRING_PAY_STATUS = "pay_status";
	private static final String STRING_PAY_PRICE = "pay_price";
	
	private static final String ORDER_INFO_MERCHANT_ID = "merchantId";
	private static final String ORDER_INFO_APP_ID = "appId";
	private static final String ORDER_INFO_APP_NAME = "appName";
	private static final String ORDER_INFO_APP_VER = "appVersion";
	private static final String ORDER_INFO_PAY_TYPE = "payType";
	private static final String ORDER_INFO_ACCOUNT = "appUserAccount";
	private static final String ORDER_INFO_PRICENOTIFYADDRESS = "priceNotifyAddress";
//	zz$r0oiljy
	private static final String SKYMOBI_MERCHANT_PASSWORD = "zz$r0oiljy";
	
	private EpsEntry mEpsEntry = null;
	
	//请CP替换成在斯凯申请的商户密钥
	private String MerchantPasswd = "szshttz-057896";//SKYMOBI_MERCHANT_PASSWORD;
	
	public void startPay(String payPoint, String payPrice, boolean useAppUi) {
		Log.i(tag, "startPay start");
		// 1.获取付费实例并初始化
		mEpsEntry = EpsEntry.getInstance();

		// 2.从AndroidManifest.xml中读取商户 ID.(请务必填写正确，否则无法结算)
		String merchantId = ConfigurationTools.getMerchantId(activity);
		if (merchantId == null) {
			Log.e(tag, "Fail to pay for not merchantId!");			
			return;
		}
		if (merchantId.equals("ZMMerchantId")){
			Log.w(tag, "警告！当前商户号为斯凯测试商户号!");
		}
		
		//请填写正确的商户密钥，否则支付无法成功
		String merchantPasswd = MerchantPasswd;
		if (merchantPasswd == null){
			
		}
		if (merchantPasswd.equals(SKYMOBI_MERCHANT_PASSWORD)){
			Log.w(tag, "警告！当前商户密钥为斯凯测试商户密钥!");
		}		


		// 3.从AndroidManifest.xml中读取APP ID.(请务必填写正确，否则无法结算)
		String appId = ConfigurationTools.getAppId(activity);
		if (appId == null) {
			Log.e(tag, "Fail to startPay for not appId!");
			return;
		}
		
		if (appId.equals("300001")){
			Log.w(tag, "警告！当前APP ID为斯凯测试APP ID!");
		}	
		
		// 4.付费方式 sms 短代
		String paymethod = "sms";

		// 5.订单号 CP需保存，订单有疑问需通过orderId进行检查
		String orderId = SystemClock.elapsedRealtime() + "";
		String appName = "浩天_NH"; // 游戏名称
		String appVersion = "1.0"; // 游戏版本号

		// 6.系统号 在斯凯申请systemId
		String systemId = "300024";
		
		String channelId = "yourchannel";

		/*
		 * 7.价格 短信付费定价（日限75元，月限150元，单次请求上限20元） 第三方付费定价
		 * 目前第三方不支持指定价格，传进来的price会被忽略，实际付费金额跟用户选择充值卡面额有关，以服务端通知为准。
		 */
		String price = payPrice;

		// 9.计费类型： 0=注册 1=道具 2=积分 3=充值，50=网游小额支付（如果不填，默认是道具）
		String payType = "1";

		String reserved1 = "reserved1";
		String reserved2 = "reserved2";
		String reserved3 = "reserved3|=2/3";

		// 10.自动生成订单签名
		SkyPaySignerInfo skyPaySignerInfo = new SkyPaySignerInfo();

		skyPaySignerInfo.setMerchantPasswd(merchantPasswd);
		skyPaySignerInfo.setMerchantId(merchantId);
		skyPaySignerInfo.setAppId(appId);
		// skyPaySignerInfo.setNotifyAddress("");
		skyPaySignerInfo.setAppName(appName);
		skyPaySignerInfo.setAppVersion(appVersion);
		skyPaySignerInfo.setPayType(payType);
		skyPaySignerInfo.setPrice(price);
		skyPaySignerInfo.setOrderId(orderId);

		skyPaySignerInfo.setReserved1(reserved1, false);
		skyPaySignerInfo.setReserved2(reserved2, false);
		skyPaySignerInfo.setReserved3(reserved3, true);

		String payPointNum = payPoint;
		String gameType = "0"; // 0-单机、1-联网、2-弱联网
		String signOrderInfo = skyPaySignerInfo.getOrderString();

		String orderInfo = ORDER_INFO_PAY_METHOD + "=" + paymethod + "&"
				+ ORDER_INFO_SYSTEM_ID + "=" + systemId + "&"
				+ ORDER_INFO_CHANNEL_ID + "=" + channelId + "&"
				+ ORDER_INFO_PAY_POINT_NUM + "=" + payPointNum + "&"
				+ ORDER_INFO_GAME_TYPE + "=" + gameType + "&"
				+ "useAppUI=" + useAppUi + "&"
				+ signOrderInfo;

		String orderDesc = "";

		orderDesc = "流畅的操作体验，劲爆的超控性能，无与伦比的超级必杀，化身斩妖除魔的英雄，开启你不平凡的游戏人生！！需花费N.NN元。";
		orderInfo += "&" + ORDER_INFO_ORDER_DESC + "=" + orderDesc;

		// 开始计费
		int payRet = mEpsEntry.startPay(activity, orderInfo, mPayHandler);
		if (EpsEntry.PAY_RETURN_SUCCESS == payRet) {
			// 初始化成功
			Toast.makeText(activity, "接口斯凯付费调用成功", Toast.LENGTH_LONG).show();
		} else {
			// 未初始化 \ 传入参数有误 \ 服务正处于付费状态
			Toast.makeText(activity, "调用接口失败" + payRet, Toast.LENGTH_LONG)
					.show();
		}
	}
	
	private Handler mPayHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			Log.w(tag, "msg.what=="+msg.what);
			if (msg.what == EpsEntry.MSG_WHAT_TO_APP) {
				String retInfo = (String) msg.obj;
				Map<String, String> map = new HashMap<String, String>();

				//activity.refreshResult(retInfo);

				String[] keyValues = retInfo.split("&|=");
				for (int i = 0; i < keyValues.length; i = i + 2) {
					map.put(keyValues[i], keyValues[i + 1]);
				}

				int msgCode = Integer.parseInt(map.get(STRING_MSG_CODE));
				// 解析付费状态和已付费价格
				// 使用其中一种方式请删掉另外一种
				if (msgCode == 100) {

					// 短信付费返回
					if (map.get(STRING_PAY_STATUS) != null) {
						int payStatus = Integer.parseInt(map
								.get(STRING_PAY_STATUS));
						int payPrice = Integer.parseInt(map
								.get(STRING_PAY_PRICE));
						int errcrCode = 0;
						if (map.get(STRING_ERROR_CODE) != null) {
							errcrCode = Integer.parseInt(map
									.get(STRING_ERROR_CODE));
						}

						switch (payStatus) {
						case 102:
							Toast.makeText(activity,
									"付费成功" + payPrice / 100 + "元",
									Toast.LENGTH_LONG).show();
							break;
						case 101:
							Toast.makeText(activity, "付费失败！原因：" + errcrCode,
									Toast.LENGTH_LONG).show();
							break;
						}
					}
				} else {
					// 解析错误码
					int errcrCode = Integer
							.parseInt(map.get(STRING_ERROR_CODE));
					Toast.makeText(activity, "付费失败！原因：" + errcrCode,
							Toast.LENGTH_LONG).show();
				}
			}
		}
	};

	

}
