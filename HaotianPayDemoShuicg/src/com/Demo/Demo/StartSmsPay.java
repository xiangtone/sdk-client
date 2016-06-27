package com.Demo.Demo;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;


import com.skymobi.pay.sdk.normal.zimon.EpsEntry;
import com.skymobi.pay.sdk.normal.zimon.util.SkyPaySignerInfo;

public class StartSmsPay {
	private static final String tag = "[StartSmsPay]";

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
	
	//请CP替换成在斯凯申请的商户密钥
	private String MerchantPasswd = SKYMOBI_MERCHANT_PASSWORD;

	
	private EpsEntry mEpsEntry = null;
	private SmsPayActivity mActivity = null;

	public StartSmsPay(SmsPayActivity activity) {
		mActivity = activity;
	}


	public void startPay(String payPoint, String payPrice, boolean useAppUi) {
		Log.i(tag, "startPay start");
		// 1.获取付费实例并初始化
		mEpsEntry = EpsEntry.getInstance();

		// 2.从AndroidManifest.xml中读取商户 ID.(请务必填写正确，否则无法结算)
		String merchantId = ConfigurationTools.getMerchantId(mActivity);
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
		String appId = ConfigurationTools.getAppId(mActivity);
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
		String appName = "木桶忍者"; // 游戏名称
		String appVersion = "1001"; // 游戏版本号

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
		int payRet = mEpsEntry.startPay(mActivity, orderInfo, mPayHandler);
		if (EpsEntry.PAY_RETURN_SUCCESS == payRet) {
			// 初始化成功
			Toast.makeText(mActivity, "接口斯凯付费调用成功", Toast.LENGTH_LONG).show();
		} else {
			// 未初始化 \ 传入参数有误 \ 服务正处于付费状态
			Toast.makeText(mActivity, "调用接口失败" + payRet, Toast.LENGTH_LONG)
					.show();
		}
	}

//	public void cancelPay() {
//		// 1.获取付费实例并初始化
//		mEpsEntry = EpsEntry.getInstance();
//
//		// 2.从AndroidManifest.xml中读取商户 ID.(请务必填写正确，否则无法结算)
//		String merchantId = ConfigurationTools.getMerchantId(mActivity);
//		if (merchantId == null) {
//			Log.e(tag, "Fail to cancelPay for not merchantId!");			
//			return;
//		}
//		if (merchantId.equals("ZMMerchantId")){
//			Log.w(tag, "警告！当前商户号为斯凯测试商户号!");
//		}
//		
//		//请填写正确的商户密钥，否则支付无法成功
//		String merchantPasswd = MerchantPasswd;
//		if (merchantPasswd == null){
//			
//		}
//		if (merchantPasswd.equals(SKYMOBI_MERCHANT_PASSWORD)){
//			Log.w(tag, "警告！当前商户密钥为斯凯测试商户密钥!");
//		}		
//
//
//		// 3.从AndroidManifest.xml中读取APP ID.(请务必填写正确，否则无法结算)
//		String appId = ConfigurationTools.getAppId(mActivity);
//		if (appId == null) {
//			Log.e(tag, "Fail to cancelPay for not appId!");
//			return;
//		}
//		
//		if (appId.equals("300001")){
//			Log.w(tag, "警告！当前APP ID为斯凯测试APP ID!");
//		}	
//		
//		// 4.付费方式 sms 短代
//		String paymethod = "sms";
//
//		// 5.订单号 CP需保存，订单有疑问需通过orderId进行检查
//		String orderId = SystemClock.elapsedRealtime() + "";
//		String appName = "木桶忍者"; // 游戏名称
//		String appVersion = "1001"; // 游戏版本号
//
//		// 6.系统号 在斯凯申请systemId
//		String systemId = "300024";
//
//		/*
//		 * 7.价格 短信付费定价（日限75元，月限150元，单次请求上限20元） 第三方付费定价
//		 * 目前第三方不支持指定价格，传进来的price会被忽略，实际付费金额跟用户选择充值卡面额有关，以服务端通知为准。
//		 */
//
//		// 9.计费类型： 0=注册 1=道具 2=积分 3=充值，50=网游小额支付（如果不填，默认是道具）
//		String payType = "1";
//
//		String reserved1 = "reserved1";
//		String reserved2 = "reserved2";
//		String reserved3 = "reserved3|=2/3";
//
//		// 10.自动生成订单签名
//		SkyPaySignerInfo skyPaySignerInfo = new SkyPaySignerInfo();
//
//		skyPaySignerInfo.setMerchantPasswd(merchantPasswd);
//		skyPaySignerInfo.setMerchantId(merchantId);
//		skyPaySignerInfo.setAppId(appId);
//		// skyPaySignerInfo.setNotifyAddress("");
//		skyPaySignerInfo.setAppName(appName);
//		skyPaySignerInfo.setAppVersion(appVersion);
//		skyPaySignerInfo.setPayType(payType);
//		skyPaySignerInfo.setOrderId(orderId);
//
//		skyPaySignerInfo.setReserved1(reserved1, false);
//		skyPaySignerInfo.setReserved2(reserved2, false);
//		skyPaySignerInfo.setReserved3(reserved3, true);
//
//		String gameType = "0"; // 0-单机、1-联网、2-弱联网
//		String signOrderInfo = skyPaySignerInfo.getOrderString();
//
//		String orderInfo = ORDER_INFO_PAY_METHOD + "=" + paymethod + "&"
//				+ ORDER_INFO_SYSTEM_ID + "=" + systemId + "&"
//				+ ORDER_INFO_GAME_TYPE + "=" + gameType + "&"
//				+ signOrderInfo;
//
//		String orderDesc = "";
//
//		orderDesc = "流畅的操作体验，劲爆的超控性能，无与伦比的超级必杀，化身斩妖除魔的英雄，开启你不平凡的游戏人生！！需花费N.NN元。";
//		orderInfo += "&" + ORDER_INFO_ORDER_DESC + "=" + orderDesc;
//		Toast.makeText(mActivity,mOrderInfo, Toast.LENGTH_LONG).show();
//		int payRet = mEpsEntry.cancelPay(mActivity, orderInfo);
//		if (EpsEntry.PAY_RETURN_SUCCESS == payRet) {
//			// 初始化成功
//			Toast.makeText(mActivity, "接口斯凯取消付费调用成功", Toast.LENGTH_LONG).show();
//		} else {
//			// 未初始化 \ 传入参数有误 \ 服务正处于付费状态
//			Toast.makeText(mActivity, "取消调用接口失败" + payRet, Toast.LENGTH_LONG)
//					.show();
//		}
//	}

	private Handler mPayHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.what == EpsEntry.MSG_WHAT_TO_APP) {
				String retInfo = (String) msg.obj;
				Map<String, String> map = new HashMap<String, String>();

				mActivity.refreshResult(retInfo);

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
							Toast.makeText(mActivity,
									"付费成功" + payPrice / 100 + "元",
									Toast.LENGTH_LONG).show();
							break;
						case 101:
							Toast.makeText(mActivity, "付费失败！原因：" + errcrCode,
									Toast.LENGTH_LONG).show();
							break;
						}
					}
				} else {
					// 解析错误码
					int errcrCode = Integer
							.parseInt(map.get(STRING_ERROR_CODE));
					Toast.makeText(mActivity, "付费失败！原因：" + errcrCode,
							Toast.LENGTH_LONG).show();
				}
			}
		}
	};
	
	/**
	 * 预取价格接口：获取当前（运营商、省份）支持的价格列表
	 */
	public void prefetchPrice(String userAcount) {
		Log.i(tag, "prefetchPrice start");
		mEpsEntry = EpsEntry.getInstance();
		
		// 2.从AndroidManifest.xml中读取商户 ID.(请务必填写正确，否则无法结算)
		String merchantId = ConfigurationTools.getMerchantId(mActivity);
		if (merchantId == null) {
			Log.e(tag, "Fail to prefetchPrice for not merchantId!");			
			return;
		}
		if (merchantId.equals("ZMMerchantId")){
			Log.w(tag, "警告！当前商户号为斯凯测试商户号!");
		}

		// 3.从AndroidManifest.xml中读取APP ID.(请务必填写正确，否则无法结算)
		String appId = ConfigurationTools.getAppId(mActivity);
		if (appId == null) {
			Log.e(tag, "Fail to prefetchPrice for not appId!");
			return;
		}
		
		if (appId.equals("300001")){
			Log.w(tag, "警告！当前APP ID为斯凯测试APP ID!");
		}	
		
		String paymethod = "sms";
		String appName = "剑侠棋缘";
		String appVersion = "1001"; // 游戏版本号
		String systemId = "300024";
		String channelId = "yourchannel";
		/*
		 * 计费类型： 0=注册 1=道具 2=积分 3=充值，50=网游小额支付（如果不填，默认是道具）
		 */
		String payType = "1";
		String account = userAcount;
		String priceNotifyAddress = "http://charge.mo-sky.cn:10206/android/test/pay/result/recv.do?mockRet=1";
		String priceNotifyAddressEncode = null;
		if (priceNotifyAddress != null) {
			try {
				priceNotifyAddressEncode = URLEncoder.encode(
						priceNotifyAddress, "utf-8");
			} catch (UnsupportedEncodingException e) {
			}
		}

		String orderInfo = ORDER_INFO_MERCHANT_ID + "=" + merchantId + "&"
				+ ORDER_INFO_APP_ID + "=" + appId + "&" + ORDER_INFO_PAY_METHOD
				+ "=" + paymethod + "&" + ORDER_INFO_APP_NAME + "=" + appName
				+ "&" + ORDER_INFO_APP_VER + "=" + appVersion + "&"
				+ ORDER_INFO_SYSTEM_ID + "=" + systemId + "&"
				+ ORDER_INFO_CHANNEL_ID + "=" + channelId + "&"
				+ ORDER_INFO_PAY_TYPE + "=" + payType + "&"
				+ ORDER_INFO_ACCOUNT + "=" + account + "&"
				+ ORDER_INFO_PRICENOTIFYADDRESS + "="
				+ priceNotifyAddressEncode;
		
		mEpsEntry.prefetchPrice(mActivity, orderInfo);
	}
	
	public String getPriceList() {
		mEpsEntry = EpsEntry.getInstance();
		return mEpsEntry.getPriceList();
	}
		
	public String getMerchantId(Context context)
	{
		String merchantid  = null;
		try {
			ApplicationInfo appInfo;			
			appInfo = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
								PackageManager.GET_META_DATA);
			merchantid = appInfo.metaData
					.get("ZMMerchantId").toString();			
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.i(tag, "getMerchantId from metaData= " + merchantid);		
		return merchantid;		
	}
	
}
