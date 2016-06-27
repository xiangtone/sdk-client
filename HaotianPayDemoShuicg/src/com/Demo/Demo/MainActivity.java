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

		// ˹��
		EpsApplication payApplication = new EpsApplication();
		payApplication.onStart(getApplicationContext());
		
		//ħ��С��
		
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
//				startPay("1",sp_money+"",false);
				//startPay(String payPoint, String payPrice, boolean useAppUi)
				
//				MbsgameSDK.defaultSDK().thirdPay(MainActivity.this, (float)sp_money/100, "", "",
//						null, new MbsPayCallback() {
//
//							@Override
//							public void onLeYoPayResult(int status, String msg) {
//								if (ErrorCode.ERROR_SUCCESS == status) {
//									Toast.makeText(MainActivity.this, "�ɹ�",
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
	
	////˹���ӿ�
	
	private static final String tag = "StartSmsPay";

	// ��������
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
	
	//��CP�滻����˹��������̻���Կ
	private String MerchantPasswd = "szshttz-057896";//SKYMOBI_MERCHANT_PASSWORD;
	
	public void startPay(String payPoint, String payPrice, boolean useAppUi) {
		Log.i(tag, "startPay start");
		// 1.��ȡ����ʵ������ʼ��
		mEpsEntry = EpsEntry.getInstance();

		// 2.��AndroidManifest.xml�ж�ȡ�̻� ID.(�������д��ȷ�������޷�����)
		String merchantId = ConfigurationTools.getMerchantId(activity);
		if (merchantId == null) {
			Log.e(tag, "Fail to pay for not merchantId!");			
			return;
		}
		if (merchantId.equals("ZMMerchantId")){
			Log.w(tag, "���棡��ǰ�̻���Ϊ˹�������̻���!");
		}
		
		//����д��ȷ���̻���Կ������֧���޷��ɹ�
		String merchantPasswd = MerchantPasswd;
		if (merchantPasswd == null){
			
		}
		if (merchantPasswd.equals(SKYMOBI_MERCHANT_PASSWORD)){
			Log.w(tag, "���棡��ǰ�̻���ԿΪ˹�������̻���Կ!");
		}		


		// 3.��AndroidManifest.xml�ж�ȡAPP ID.(�������д��ȷ�������޷�����)
		String appId = ConfigurationTools.getAppId(activity);
		if (appId == null) {
			Log.e(tag, "Fail to startPay for not appId!");
			return;
		}
		
		if (appId.equals("300001")){
			Log.w(tag, "���棡��ǰAPP IDΪ˹������APP ID!");
		}	
		
		// 4.���ѷ�ʽ sms �̴�
		String paymethod = "sms";

		// 5.������ CP�豣�棬������������ͨ��orderId���м��
		String orderId = SystemClock.elapsedRealtime() + "";
		String appName = "����_NH"; // ��Ϸ����
		String appVersion = "1.0"; // ��Ϸ�汾��

		// 6.ϵͳ�� ��˹������systemId
		String systemId = "300024";
		
		String channelId = "yourchannel";

		/*
		 * 7.�۸� ���Ÿ��Ѷ��ۣ�����75Ԫ������150Ԫ��������������20Ԫ�� ���������Ѷ���
		 * Ŀǰ��������֧��ָ���۸񣬴�������price�ᱻ���ԣ�ʵ�ʸ��ѽ����û�ѡ���ֵ������йأ��Է����֪ͨΪ׼��
		 */
		String price = payPrice;

		// 9.�Ʒ����ͣ� 0=ע�� 1=���� 2=���� 3=��ֵ��50=����С��֧����������Ĭ���ǵ��ߣ�
		String payType = "1";

		String reserved1 = "reserved1";
		String reserved2 = "reserved2";
		String reserved3 = "reserved3|=2/3";

		// 10.�Զ����ɶ���ǩ��
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
		String gameType = "0"; // 0-������1-������2-������
		String signOrderInfo = skyPaySignerInfo.getOrderString();

		String orderInfo = ORDER_INFO_PAY_METHOD + "=" + paymethod + "&"
				+ ORDER_INFO_SYSTEM_ID + "=" + systemId + "&"
				+ ORDER_INFO_CHANNEL_ID + "=" + channelId + "&"
				+ ORDER_INFO_PAY_POINT_NUM + "=" + payPointNum + "&"
				+ ORDER_INFO_GAME_TYPE + "=" + gameType + "&"
				+ "useAppUI=" + useAppUi + "&"
				+ signOrderInfo;

		String orderDesc = "";

		orderDesc = "�����Ĳ������飬�����ĳ������ܣ������ױȵĳ�����ɱ������ն����ħ��Ӣ�ۣ������㲻ƽ������Ϸ���������軨��N.NNԪ��";
		orderInfo += "&" + ORDER_INFO_ORDER_DESC + "=" + orderDesc;

		// ��ʼ�Ʒ�
		int payRet = mEpsEntry.startPay(activity, orderInfo, mPayHandler);
		if (EpsEntry.PAY_RETURN_SUCCESS == payRet) {
			// ��ʼ���ɹ�
			Toast.makeText(activity, "�ӿ�˹�����ѵ��óɹ�", Toast.LENGTH_LONG).show();
		} else {
			// δ��ʼ�� \ ����������� \ ���������ڸ���״̬
			Toast.makeText(activity, "���ýӿ�ʧ��" + payRet, Toast.LENGTH_LONG)
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
				// ��������״̬���Ѹ��Ѽ۸�
				// ʹ������һ�ַ�ʽ��ɾ������һ��
				if (msgCode == 100) {

					// ���Ÿ��ѷ���
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
									"���ѳɹ�" + payPrice / 100 + "Ԫ",
									Toast.LENGTH_LONG).show();
							break;
						case 101:
							Toast.makeText(activity, "����ʧ�ܣ�ԭ��" + errcrCode,
									Toast.LENGTH_LONG).show();
							break;
						}
					}
				} else {
					// ����������
					int errcrCode = Integer
							.parseInt(map.get(STRING_ERROR_CODE));
					Toast.makeText(activity, "����ʧ�ܣ�ԭ��" + errcrCode,
							Toast.LENGTH_LONG).show();
				}
			}
		}
	};

	

}
