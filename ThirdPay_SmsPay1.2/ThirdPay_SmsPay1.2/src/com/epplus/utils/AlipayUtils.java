package com.epplus.utils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils; 


//import com.alipay.sdk.app.PayTask;
/**
 *  ֧����֧��
 * @author zgt
 *
 */
public class AlipayUtils {

	// �̻�PID
	private  static final String PARTNER = "2088021159783167";
	// �̻��տ��˺�
	private static final String SELLER = "xtgame@bjxiangtone.com";
	// �̻�˽Կ��pkcs8��ʽ
	private static final String RSA_PRIVATE = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAM4reycyjGhOM/uEvTsva0fZbCAMD8zD+n+BMRtLAVNDczIsyThCby8Awrhr7tVa1r8TvtTDbcpMHBEMkDSiTP0r19uTLiyt+ei8GK7q/2v1xnfDJfsiRjUwdd9xSPlCLRACd/9iklbKZT60gHzcBov7S/8OvgebLVtCANoGVdDzAgMBAAECgYBN31qK+arTEwbLb93R5x1MbDFNAYFORI/vbSrRNklv28A9KXFvkJhSVqU15360k7UdQyYHUzG7AXhwcCBf4RXWtFIFRu1YTX+vtwOJoxo5q27byh9rOKg+4J4b5ojt78dAjYmmj59GhdUcr9gSBi11gDw5DbM80ytbzCuP6biM4QJBAPxT+O0NZRNBquP5PF0aTR6Ex2Hp4KByaNRDFXn0lnSX0zWAcdYnw9v0MKG7wT6K360SnxPPYmH3NDFhvwFSQesCQQDRK4zYB+0S/pgpdUxNxwnkWAO5arXW8hAm3bZUr71+NT3ozg+NTrDpO6+kpD2L6E5nz//tPJiffVshVyWODuMZAkBSJKgZy82Gyk7urlmXWZOXhtQ9rNyifvxfdYNNU3GTfUWV2j204Pci6MjYLf5H9P/CIRjGYzH9AHPuS4rZzESHAkBMVze1VNc62n7QisYJkP5UP6dEUeUCCSDJ/ptgNy/S0z3ALQzSBwlcZnNJhMQNvwB2tRx0CmytsQPEnFjRiy9pAkBfIoi7dj3n9M5iW31DAUvhLFxm5uBGgXg5yCQUSmqNSApYF7GhiHr3Cf227iglgHD3OO3ngCp8jHQ3MbA+zumH";
	// ֧������Կ
	//private static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCdA+n4L9+INfZZ7yfJyzbsknMgW2CsW3BFplPR L8/rXdk9v2lRNAEewJZxLhqcINCJ/dOu7Wi7WFqm293U76QLR3LgAXf18xogJLhHH9P/U/KFkalU 2byEJrqpt809N51G2RakdD4sx/yvpmm+Rk0a8W6y0AzL40KPzB70oEStQwIDAQAB";
	private static final int SDK_PAY_FLAG = 1;

	private static final int SDK_CHECK_FLAG = 2;
	
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				PayResult payResult = new PayResult((String) msg.obj);

				// ֧�������ش˴�֧���������ǩ�������֧����ǩ����Ϣ��ǩԼʱ֧�����ṩ�Ĺ�Կ����ǩ
				String resultInfo = payResult.getResult();

				String resultStatus = payResult.getResultStatus();

				// �ж�resultStatus Ϊ��9000�������֧���ɹ�������״̬�������ɲο��ӿ��ĵ�
				if (TextUtils.equals(resultStatus, "9000")) {
					alipayHandler.aliPaySuccess(resultInfo, resultStatus);
					//Toast.makeText(activity, "֧���ɹ�",Toast.LENGTH_SHORT).show();
				} else {
					// �ж�resultStatus Ϊ�ǡ�9000����������֧��ʧ��
					// ��8000������֧�������Ϊ֧������ԭ�����ϵͳԭ���ڵȴ�֧�����ȷ�ϣ����ս����Ƿ�ɹ��Է�����첽֪ͨΪ׼��С����״̬��
					if (TextUtils.equals(resultStatus, "8000")) {
						alipayHandler.aliPayConfirmed(resultInfo, resultStatus);
						//Toast.makeText(activity, "֧�����ȷ����",Toast.LENGTH_SHORT).show();

					} else {
						// ����ֵ�Ϳ����ж�Ϊ֧��ʧ�ܣ������û�����ȡ��֧��������ϵͳ���صĴ���
						alipayHandler.aliPayFailed(resultInfo, resultStatus);
						//Toast.makeText(activity, "֧��ʧ��",Toast.LENGTH_SHORT).show();

					}
				}
				break;
			}
			case SDK_CHECK_FLAG: {
				//Toast.makeText(activity, "�����Ϊ��" + msg.obj,Toast.LENGTH_SHORT).show();
				alipayHandler.aliPayCheck((Boolean)msg.obj);
				break;
			}
			default:
				break;
			}
		};
	};

	
	private Activity activity;
	private AlipayHandler alipayHandler;
	

	
	private String notify_url;
	
	
	
	
	public AlipayUtils(Activity activity,String OrderIdSelf,String OrderIdCp,AlipayHandler alipayHandler) {
		this.activity =activity;
		this.alipayHandler = alipayHandler;
		//String baseUrl = ConfigUtils.Notify_Url_Alipy;//"http://thirdpay-webhook.n8wan.com:29141/thirdpayCountServlet";
		
		this.notify_url = URLUtils.notifyUrlAlipy(activity,OrderIdSelf,OrderIdCp);//baseUrl+"?"+ConfigUtils.xx_notifyData+"="+ConfigUtils.getNotifyJsonData(activity,ConfigUtils.ALIPAY);
		
	}
	

	
	/**
	 * call alipay sdk pay. ����SDK֧��
	 * @param subject ��Ʒ����
	 * @param body   ��Ʒ����
	 * @param price  ��Ʒ�۸�  eg: 0.01
	 */
	public void pay(final String subject, final String body, final String price) {
		
		// ����
		String orderInfo = getOrderInfo(subject, body, price);

		// �Զ�����RSA ǩ��
		String sign = sign(orderInfo);
		try {
			// �����sign ��URL����
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// �����ķ���֧���������淶�Ķ�����Ϣ
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
				+ getSignType();

		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				try {
//					String SERVICE_ALIPAY = "http://192.168.0.111:8080/alipaySign/AlipaySign";
//					
//					String time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
//			        Map<String, String> map = new HashMap<String, String>();
//			        map.put("subject", subject);
//			        map.put("body", body);
//			        map.put("price", price);
//			        //�Զ�������
//			        map.put("notify_url",notify_url);
//		 	        String mPayInfo = HttpUtils.post(SERVICE_ALIPAY, map);
					
					Class PayTaskClass = Class.forName("com.alipay.sdk.app.PayTask");
					Constructor constructor = PayTaskClass.getDeclaredConstructor(Activity.class);
					Object PayTask_ = constructor.newInstance(activity);
					
					Method pay = PayTaskClass.getDeclaredMethod("pay", String.class);
					String result = (String) pay.invoke(PayTask_, payInfo);
					
					Message msg = new Message();
					msg.what = SDK_PAY_FLAG;
					msg.obj = result;
					mHandler.sendMessage(msg);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
//				// ����PayTask ����
//				PayTask alipay = new PayTask(activity);
//				// ����֧���ӿڣ���ȡ֧�����
//				String result = alipay.pay(payInfo);
//
//				Message msg = new Message();
//				msg.what = SDK_PAY_FLAG;
//				msg.obj = result;
//				mHandler.sendMessage(msg);
			}
		};

		// �����첽����
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	/**
	 * check whether the device has authentication alipay account.
	 * ��ѯ�ն��豸�Ƿ����֧������֤�˻�
	 */
	public void check() {
		Runnable checkRunnable = new Runnable() {

			@Override
			public void run() {
				
				
				try {
					Class PayTaskClass = Class.forName("com.alipay.sdk.app.PayTask");
					Constructor constructor = PayTaskClass.getDeclaredConstructor(Activity.class);
					Object PayTask_ = constructor.newInstance(activity);
					
					Method checkAccountIfExist = PayTaskClass.getDeclaredMethod("checkAccountIfExist");
					
					boolean isExist = (Boolean) checkAccountIfExist.invoke(PayTask_);
					Message msg = new Message();
					msg.what = SDK_CHECK_FLAG;
					msg.obj = isExist;
					mHandler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
//				// ����PayTask ����
//				PayTask payTask = new PayTask(activity);
//				// ���ò�ѯ�ӿڣ���ȡ��ѯ���
//				boolean isExist = payTask.checkAccountIfExist();
//
//				Message msg = new Message();
//				msg.what = SDK_CHECK_FLAG;
//				msg.obj = isExist;
//				mHandler.sendMessage(msg);
			}
		};

		Thread checkThread = new Thread(checkRunnable);
		checkThread.start();

	}

	/**
	 * get the sdk version. ��ȡSDK�汾��
	 * 
	 */
	public String getSDKVersion(Activity activity) {
		
		try {
			Class PayTaskClass = Class.forName("com.alipay.sdk.app.PayTask");
			Constructor constructor = PayTaskClass.getDeclaredConstructor(Activity.class);
			Object PayTask_ = constructor.newInstance(activity);
			
			Method getVersion = PayTaskClass.getDeclaredMethod("getVersion");
			String version = (String) getVersion.invoke(PayTask_);
			return version;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
//		PayTask payTask = new PayTask(activity);
//		String version = payTask.getVersion();
//		return version;
	}

	/**
	 * create the order info. ����������Ϣ
	 * 
	 */
	private  String getOrderInfo(String subject, String body, String price) {

		// ǩԼ���������ID
		String orderInfo = "partner=" + "\"" + PARTNER + "\"";

		// ǩԼ����֧�����˺�
		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

		// �̻���վΨһ������
		orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";

		// ��Ʒ����
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// ��Ʒ����
		orderInfo += "&body=" + "\"" + body + "\"";

		// ��Ʒ���
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		// �������첽֪ͨҳ��·��
		//orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm"
		orderInfo += "&notify_url=" + "\"" + notify_url
				+ "\"";

		// ����ӿ����ƣ� �̶�ֵ
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// ֧�����ͣ� �̶�ֵ
		orderInfo += "&payment_type=\"1\"";

		// �������룬 �̶�ֵ
		orderInfo += "&_input_charset=\"utf-8\"";

		// ����δ����׵ĳ�ʱʱ��
		// Ĭ��30���ӣ�һ����ʱ���ñʽ��׾ͻ��Զ����رա�
		// ȡֵ��Χ��1m��15d��
		// m-���ӣ�h-Сʱ��d-�죬1c-���죨���۽��׺�ʱ����������0��رգ���
		// �ò�����ֵ������С���㣬��1.5h����ת��Ϊ90m��
		orderInfo += "&it_b_pay=\"30m\"";

		// extern_tokenΪ���������Ȩ��ȡ����alipay_open_id,���ϴ˲����û���ʹ����Ȩ���˻�����֧��
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// ֧��������������󣬵�ǰҳ����ת���̻�ָ��ҳ���·�����ɿ�
		orderInfo += "&return_url=\"m.alipay.com\"";

		// �������п�֧���������ô˲���������ǩ���� �̶�ֵ ����ҪǩԼ���������п����֧��������ʹ�ã�
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}

	/**
	 * get the out_trade_no for an order. �����̻������ţ���ֵ���̻���Ӧ����Ψһ�����Զ����ʽ�淶��
	 * 
	 */
	private  String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
				Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);

		Random r = new Random();
		key = key + r.nextInt();
		key = key.substring(0, 15);
		return key;
	}

	/**
	 * sign the order info. �Զ�����Ϣ����ǩ��
	 * 
	 * @param content
	 *            ��ǩ��������Ϣ
	 */
	public String sign(String content) {
		return SignUtils.sign(content, RSA_PRIVATE);
	}

	/**
	 * get the sign type we use. ��ȡǩ����ʽ
	 * 
	 */
	public String getSignType() {
		return "sign_type=\"RSA\"";
	}


	/**
	 * ֧���ص�
	 * @author Administrator
	 *
	 */
	public static abstract class AlipayHandler{
		/**
		 * ֧���ɹ�
		 * @param resultInfo  ֧�������ش˴�֧�����
		 * @param resultStatus  ֧�������ش˴�֧��״̬
		 */
		public abstract void aliPaySuccess(String resultInfo,String resultStatus);
		/**
		 * ֧��ʧ��
		 * @param resultInfo ֧�������ش˴�֧�����
		 * @param resultStatus ֧�������ش˴�֧��״̬
		 */
		public abstract void aliPayFailed(String resultInfo,String resultStatus);
		/**
		 * ֧�������Ϊ֧������ԭ�����ϵͳԭ���ڵȴ�֧�����ȷ�ϣ����ս����Ƿ�ɹ��Է�����첽֪ͨΪ׼��С����״̬��
		 * @param resultInfo ֧�������ش˴�֧�����
		 * @param resultStatus ֧�������ش˴�֧��״̬
		 */
		public void aliPayConfirmed(String resultInfo,String resultStatus){
			
		}
		
		/**
		 * ��ѯ�ն��豸�Ƿ����֧������֤�˻� 
		 * @param isExist �����
		 */
		public void aliPayCheck(boolean isExist){
			
		}
	}

	
	
}
