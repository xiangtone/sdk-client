package com.core_sur.activity.impl.helper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import com.alipay.sdk.app.PayTask;
import com.core_sur.Config;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
/**
 * 支付宝相关数据
 * @author KKK
 *
 */
public class AliPayHelper {
	
	private static final int SDK_PAY_FLAG = 1;

	private static final int SDK_CHECK_FLAG = 2;
	// 商户PID
	private static final String PARTNER = "2088911908396547";
	// 商户收款账号
	private static final String SELLER = "sdkjs@bjxiangtone.com";
	// 商户私钥，pkcs8格式
	private static final String RSA_PRIVATE = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGB" +
			"AMWDsXSsxB06wgYNuT73iOTywNWNXFIbD/qHPQz0fZ2dADvY8nlbAXHdUc/qnY+re9wqRmLmaGn03tTXQ" +
			"5InaOnPXMezLOHsNi7N2Jkr7wtv7CM5thfjQv99CAUlNt4sZmY6hld4GjcxbMDVWcsq08vn+Q48LgsyUQ" +
			"rivE8MzNjVAgMBAAECgYAtb5IO6P9kSfQAOH/4wDC5mi5J8e7e5GLUmu+sX0HWVBuNqOZ+jDTU4LjFP49" +
			"nUWd4yjDO53n1heMMy0g8AtzbJBHUy3EAuVm81YUbGiicZs9VGuq0pnA/v814o4ldZf94fMqepG8c4bcI" +
			"zUBEUnX70UlOXefvUU0wKCxIEFPzAQJBAP9S3pEZaTheM7mNtsATeswwHo75VfgOVhyMipjJ9KsH4BRDB" +
			"VSpCAgsphg/xXs7Rjk+Nokr+O2WcYhJRxLPEjECQQDGCZ/StkdrMVRo9X7KnU3zEo2PgOzTjavA1/tQAh" +
			"8+LdlrBAbUFC0Knmf2NNMsVI+ck1FLQFXl9+iHIon4SwPlAkApnZ49VED8OQ6BPgH9iK9JPMaeUzGZ/uw" +
			"2NwoYIULJ5by2UL47sFloy64+4ZYUofKuPd+xuAMY5p6Wsirte+bhAkAWZCwv9PAqTI+QtoE5g4O2whhD" +
			"a35bhR8wVLtTlonVy/VTRkdHX2igVLzId7yQvJboSzfb3FBaCie+b8dNzZ2JAkAvtRXPRtZxM/66Jn2pm" +
			"aXy4f9BH7a1z/xnwvjZwpEJuafkgC4jyowXJ/VcjC1oEOsh9NJNhgSpL0SjlocFmSpo";
	// 支付宝公钥
	private static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfg" +
			"oUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/" +
			"VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
	
	//同步地址
	private static final String notify_url = Config.NOTIFY_URL_ALIPAY;
	
	/**
	 * check whether the device has authentication alipay account.
	 * 查询终端设备是否存在支付宝认证账户
	 * 
	 */
	public void check(final Activity activity,final Handler handler) {
		Runnable checkRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask payTask = new PayTask(activity);
				// 调用查询接口，获取查询结果
				boolean isExist = payTask.checkAccountIfExist();
				Message msg = new Message();
				msg.what = SDK_CHECK_FLAG;
				msg.obj = isExist;
				handler.sendMessage(msg);
			}
		};

		Thread checkThread = new Thread(checkRunnable);
		checkThread.start();

	}

	/**
	 * get the sdk version. 获取SDK版本号
	 * 
	 */
	public static void getSDKVersion(Activity activity) {
		PayTask payTask = new PayTask(activity);
		String version = payTask.getVersion();
		//Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
	}

	/**
	 * create the order info. 创建订单信息
	 * 
	 */
	public static String getOrderInfo(String orderId,String subject, String body, String price) {

		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + PARTNER + "\"";
		
		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";
		
		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + orderId + "\"";
		
		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";
		
		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";
		
		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";
		
		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\"" + notify_url + "\"";
		
		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";
		
		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";
		
		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";
		
		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";
		
		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";
		
		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		orderInfo += "&return_url=\"m.alipay.com\"";
		
		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";
		return orderInfo;
	}

	/**
	 * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
	 * 
	 */
	public static String getOutTradeNo() {
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
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	public static String sign(String content) {
		return SignUtils.sign(content, RSA_PRIVATE);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	public static String getSignType() {
		return "sign_type=\"RSA\"";
	}
}
