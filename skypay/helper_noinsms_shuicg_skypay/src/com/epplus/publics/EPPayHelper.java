package com.epplus.publics;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.epplus.bean.Bdata;
import com.epplus.face.EPPlusPayService;
import com.epplus.utils.ConfigurationTools;
import com.epplus.utils.LLog;
import com.epplus.utils.util;
import com.skymobi.pay.sdk.normal.zimon.EpsApplication;
import com.skymobi.pay.sdk.normal.zimon.EpsEntry;
import com.skymobi.pay.sdk.normal.zimon.util.SkyPaySignerInfo;

public class EPPayHelper {
	private static EPPayHelper epHelper = new EPPayHelper();
	private Context c;
	//private String PAYFORMAT = "{0}.com.my.fee.start";
	private String PAYFORMAT = new Bdata().gpf();

	public static EPPayHelper getInstance(Context c) {
		EPPayHelper.epHelper.c = c;
		return epHelper;
	}

	public void initPay(boolean isCheckLog, String payContact) {
		
		//sky init
		EpsApplication payApplication = new EpsApplication();
		payApplication.onStart(c.getApplicationContext());
		
		c.getSharedPreferences("payInfo", Context.MODE_PRIVATE).edit()
				.putString("payContact", payContact).commit();
		c.startService(new Intent(c, EPPlusPayService.class).putExtra("type", 1000)
				.putExtra("isChecklog", isCheckLog));
	}

	public void pay(int number, String note, String userOrderId, String skyPayPoint) {
		createLoadingDialog();
		Intent payIntent = new Intent(MessageFormat.format(PAYFORMAT,
				c.getPackageName()));
		payIntent.putExtra("payNumber", number);
		payIntent.putExtra("payNote", note);
		payIntent.putExtra("userOrderId", userOrderId);
		payIntent.putExtra("skyPayPoint",skyPayPoint);
		c.sendBroadcast(payIntent);
	}

	private void createLoadingDialog() {
		if(dialog!=null){
			return;
		}
		AlertDialog.Builder builder = new Builder(c);
		dialog = builder.create();
		LinearLayout ll_loading = new LinearLayout(c);
		ll_loading.setLayoutParams(new LayoutParams(-1, -1));
		ll_loading.setOrientation(LinearLayout.VERTICAL);
		ll_loading.setGravity(Gravity.CENTER);
		ll_loading.setPadding(40, 20, 40, 20);
		ll_loading.addView(new ProgressBar(c));
		TextView tv_loadingText = new TextView(c);
		tv_loadingText.setText("加载中..");
		tv_loadingText.setLayoutParams(new LayoutParams(480, -2));
		tv_loadingText.setGravity(Gravity.CENTER);
		ll_loading.addView(tv_loadingText);
		dialog.setView(ll_loading, -1, -1, -1, -1);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		new TimeoutSendPay().start();
	}

	private boolean isSendOK;

	class TimeoutSendPay extends Thread {
		private int timeout;

		@Override
		public void run() {
			while (timeout < 30 && !isSendOK) {
				timeout++;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (!isSendOK && timeout >= 30) {
					if (dialog != null) {
						dialog.cancel();
						dialog = null;
						System.out.println("dialog cancel");
					}
				}
			}
		}
	}

	public BroadcastReceiver payReceiver;
	private Handler payHandler;
	private AlertDialog dialog;

	public void setPayListen(Handler handler) {
		this.payHandler = handler;
		regPay();
		c.registerReceiver(payReceiver, new IntentFilter(c.getPackageName()
				+ ".my.fee.listener"));
	}

	public void exit() {
		if (payReceiver != null) {
			c.unregisterReceiver(payReceiver);
			payReceiver = null;
		}
	}

	public void regPay() {
		payReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (payHandler != null && intent.getExtras() != null) {
					if (intent.getIntExtra("sendPaySuccess", 0) == 1) {
						isSendOK=true;
						if(dialog!=null){
							dialog.dismiss();
							dialog=null;
						}
						return;
					}
					
					Message msg = Message.obtain();
					msg.what = intent.getExtras().getInt("msg.what");
					msg.obj = intent.getExtras().getString("msg.obj");
					
					Log.e("test", "EPPayHelper--msg.what:"+msg.what+",,,msg.obj:"+msg.obj);
					
					String json = (String) msg.obj;
					try {
						JSONObject jsonObject = new JSONObject(json);
						if(jsonObject.isNull("nochannel")){
							throw new JSONException("nochannel");
						}
				        if(jsonObject.isNull("money")){
				        	throw new JSONException("money");
						}
				        if(jsonObject.isNull("commodity")){
				        	throw new JSONException("commodity");
						}
				        if(jsonObject.isNull("orderid")){
				        	throw new JSONException("orderid"); 
						}
						String nochannel = jsonObject.getString("nochannel");
						String money = jsonObject.getString("money");
						String commodity = jsonObject.getString("commodity");
						String orderid = jsonObject.getString("orderid");
						String skyPayPoint = jsonObject.getString("skyPayPoint");
						Log.e("test", "EPPayHelper--nochannel:"+nochannel+"--money:"+money
									  +"--commodity:"+commodity+"--orderid:"+orderid+
									  "--skyPayPoint:"+skyPayPoint);
						startPay(skyPayPoint,money,false);
					} catch (JSONException e) {
						e.printStackTrace();
						LLog.error("come in JSONException");
						payHandler.sendMessage(msg);
					}
					
					//payHandler.sendMessage(msg);
				}
			}
		};
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
		String merchantId = ConfigurationTools.getMerchantId(c);
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
		String appId = ConfigurationTools.getAppId(c);
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
		//String appName = "浩天_NH"; // 游戏名称
		//String appVersion = "1"; // 游戏版本号
		
		String appName = util.getAppMetaData(c,"APP_NAME");
		String appVersion = util.getAppMetaData(c, "APP_VERSION");
		
		Log.e("test", "EpPayHelper--appName:"+appName+"--appVersion:"+appVersion);

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
		int payRet = mEpsEntry.startPay((Activity)c, orderInfo, mPayHandler);
		if (EpsEntry.PAY_RETURN_SUCCESS == payRet) {
			// 初始化成功
			//Toast.makeText(c, "接口斯凯付费调用成功", Toast.LENGTH_LONG).show();
		} else {
			// 未初始化 \ 传入参数有误 \ 服务正处于付费状态
			Toast.makeText(c, "调用接口失败" + payRet, Toast.LENGTH_LONG)
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
							Toast.makeText(c,
									"付费成功" + payPrice / 100 + "元",
									Toast.LENGTH_LONG).show();
							Message msgsuc = Message.obtain();
							msgsuc.what = 4001;
							msgsuc.obj = "支付成功";
							payHandler.sendMessage(msgsuc);
							break;
						case 101:
							Toast.makeText(c, "付费失败！原因：" + errcrCode,
									Toast.LENGTH_LONG).show();
							Message msgfail = Message.obtain();
							msgfail.what = 4002;
							msgfail.obj = "支付失败";
							payHandler.sendMessage(msgfail);
							break;
						}
					}
				} else {
					// 解析错误码
					int errcrCode = Integer
							.parseInt(map.get(STRING_ERROR_CODE));
					Toast.makeText(c, "付费失败！原因：" + errcrCode,
							Toast.LENGTH_LONG).show();
					Message msgfail = Message.obtain();
					msgfail.what = 4002;
					msgfail.obj = "支付失败";
					payHandler.sendMessage(msgfail);
							
				}
			}
		}
	};

}
