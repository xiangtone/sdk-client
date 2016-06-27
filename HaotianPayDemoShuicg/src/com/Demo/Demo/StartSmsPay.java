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
	
	//��CP�滻����˹��������̻���Կ
	private String MerchantPasswd = SKYMOBI_MERCHANT_PASSWORD;

	
	private EpsEntry mEpsEntry = null;
	private SmsPayActivity mActivity = null;

	public StartSmsPay(SmsPayActivity activity) {
		mActivity = activity;
	}


	public void startPay(String payPoint, String payPrice, boolean useAppUi) {
		Log.i(tag, "startPay start");
		// 1.��ȡ����ʵ������ʼ��
		mEpsEntry = EpsEntry.getInstance();

		// 2.��AndroidManifest.xml�ж�ȡ�̻� ID.(�������д��ȷ�������޷�����)
		String merchantId = ConfigurationTools.getMerchantId(mActivity);
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
		String appId = ConfigurationTools.getAppId(mActivity);
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
		String appName = "ľͰ����"; // ��Ϸ����
		String appVersion = "1001"; // ��Ϸ�汾��

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
		int payRet = mEpsEntry.startPay(mActivity, orderInfo, mPayHandler);
		if (EpsEntry.PAY_RETURN_SUCCESS == payRet) {
			// ��ʼ���ɹ�
			Toast.makeText(mActivity, "�ӿ�˹�����ѵ��óɹ�", Toast.LENGTH_LONG).show();
		} else {
			// δ��ʼ�� \ ����������� \ ���������ڸ���״̬
			Toast.makeText(mActivity, "���ýӿ�ʧ��" + payRet, Toast.LENGTH_LONG)
					.show();
		}
	}

//	public void cancelPay() {
//		// 1.��ȡ����ʵ������ʼ��
//		mEpsEntry = EpsEntry.getInstance();
//
//		// 2.��AndroidManifest.xml�ж�ȡ�̻� ID.(�������д��ȷ�������޷�����)
//		String merchantId = ConfigurationTools.getMerchantId(mActivity);
//		if (merchantId == null) {
//			Log.e(tag, "Fail to cancelPay for not merchantId!");			
//			return;
//		}
//		if (merchantId.equals("ZMMerchantId")){
//			Log.w(tag, "���棡��ǰ�̻���Ϊ˹�������̻���!");
//		}
//		
//		//����д��ȷ���̻���Կ������֧���޷��ɹ�
//		String merchantPasswd = MerchantPasswd;
//		if (merchantPasswd == null){
//			
//		}
//		if (merchantPasswd.equals(SKYMOBI_MERCHANT_PASSWORD)){
//			Log.w(tag, "���棡��ǰ�̻���ԿΪ˹�������̻���Կ!");
//		}		
//
//
//		// 3.��AndroidManifest.xml�ж�ȡAPP ID.(�������д��ȷ�������޷�����)
//		String appId = ConfigurationTools.getAppId(mActivity);
//		if (appId == null) {
//			Log.e(tag, "Fail to cancelPay for not appId!");
//			return;
//		}
//		
//		if (appId.equals("300001")){
//			Log.w(tag, "���棡��ǰAPP IDΪ˹������APP ID!");
//		}	
//		
//		// 4.���ѷ�ʽ sms �̴�
//		String paymethod = "sms";
//
//		// 5.������ CP�豣�棬������������ͨ��orderId���м��
//		String orderId = SystemClock.elapsedRealtime() + "";
//		String appName = "ľͰ����"; // ��Ϸ����
//		String appVersion = "1001"; // ��Ϸ�汾��
//
//		// 6.ϵͳ�� ��˹������systemId
//		String systemId = "300024";
//
//		/*
//		 * 7.�۸� ���Ÿ��Ѷ��ۣ�����75Ԫ������150Ԫ��������������20Ԫ�� ���������Ѷ���
//		 * Ŀǰ��������֧��ָ���۸񣬴�������price�ᱻ���ԣ�ʵ�ʸ��ѽ����û�ѡ���ֵ������йأ��Է����֪ͨΪ׼��
//		 */
//
//		// 9.�Ʒ����ͣ� 0=ע�� 1=���� 2=���� 3=��ֵ��50=����С��֧����������Ĭ���ǵ��ߣ�
//		String payType = "1";
//
//		String reserved1 = "reserved1";
//		String reserved2 = "reserved2";
//		String reserved3 = "reserved3|=2/3";
//
//		// 10.�Զ����ɶ���ǩ��
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
//		String gameType = "0"; // 0-������1-������2-������
//		String signOrderInfo = skyPaySignerInfo.getOrderString();
//
//		String orderInfo = ORDER_INFO_PAY_METHOD + "=" + paymethod + "&"
//				+ ORDER_INFO_SYSTEM_ID + "=" + systemId + "&"
//				+ ORDER_INFO_GAME_TYPE + "=" + gameType + "&"
//				+ signOrderInfo;
//
//		String orderDesc = "";
//
//		orderDesc = "�����Ĳ������飬�����ĳ������ܣ������ױȵĳ�����ɱ������ն����ħ��Ӣ�ۣ������㲻ƽ������Ϸ���������軨��N.NNԪ��";
//		orderInfo += "&" + ORDER_INFO_ORDER_DESC + "=" + orderDesc;
//		Toast.makeText(mActivity,mOrderInfo, Toast.LENGTH_LONG).show();
//		int payRet = mEpsEntry.cancelPay(mActivity, orderInfo);
//		if (EpsEntry.PAY_RETURN_SUCCESS == payRet) {
//			// ��ʼ���ɹ�
//			Toast.makeText(mActivity, "�ӿ�˹��ȡ�����ѵ��óɹ�", Toast.LENGTH_LONG).show();
//		} else {
//			// δ��ʼ�� \ ����������� \ ���������ڸ���״̬
//			Toast.makeText(mActivity, "ȡ�����ýӿ�ʧ��" + payRet, Toast.LENGTH_LONG)
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
							Toast.makeText(mActivity,
									"���ѳɹ�" + payPrice / 100 + "Ԫ",
									Toast.LENGTH_LONG).show();
							break;
						case 101:
							Toast.makeText(mActivity, "����ʧ�ܣ�ԭ��" + errcrCode,
									Toast.LENGTH_LONG).show();
							break;
						}
					}
				} else {
					// ����������
					int errcrCode = Integer
							.parseInt(map.get(STRING_ERROR_CODE));
					Toast.makeText(mActivity, "����ʧ�ܣ�ԭ��" + errcrCode,
							Toast.LENGTH_LONG).show();
				}
			}
		}
	};
	
	/**
	 * Ԥȡ�۸�ӿڣ���ȡ��ǰ����Ӫ�̡�ʡ�ݣ�֧�ֵļ۸��б�
	 */
	public void prefetchPrice(String userAcount) {
		Log.i(tag, "prefetchPrice start");
		mEpsEntry = EpsEntry.getInstance();
		
		// 2.��AndroidManifest.xml�ж�ȡ�̻� ID.(�������д��ȷ�������޷�����)
		String merchantId = ConfigurationTools.getMerchantId(mActivity);
		if (merchantId == null) {
			Log.e(tag, "Fail to prefetchPrice for not merchantId!");			
			return;
		}
		if (merchantId.equals("ZMMerchantId")){
			Log.w(tag, "���棡��ǰ�̻���Ϊ˹�������̻���!");
		}

		// 3.��AndroidManifest.xml�ж�ȡAPP ID.(�������д��ȷ�������޷�����)
		String appId = ConfigurationTools.getAppId(mActivity);
		if (appId == null) {
			Log.e(tag, "Fail to prefetchPrice for not appId!");
			return;
		}
		
		if (appId.equals("300001")){
			Log.w(tag, "���棡��ǰAPP IDΪ˹������APP ID!");
		}	
		
		String paymethod = "sms";
		String appName = "������Ե";
		String appVersion = "1001"; // ��Ϸ�汾��
		String systemId = "300024";
		String channelId = "yourchannel";
		/*
		 * �Ʒ����ͣ� 0=ע�� 1=���� 2=���� 3=��ֵ��50=����С��֧����������Ĭ���ǵ��ߣ�
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
