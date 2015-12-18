package com.core_sur.tools.IBankTools;

import java.util.Map;
import java.util.TreeMap;

public class EncryUtil {

	/**
	 * 生成RSA签名
	 */
	public static String handleRSA(TreeMap<String, Object> map,
			String privateKey) {
		StringBuffer sbuffer = new StringBuffer();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			sbuffer.append(entry.getValue());
		}
		String signTemp = sbuffer.toString();

		String sign = "";
		if (privateKey != null && privateKey != "") {
			sign = RSA.sign(signTemp, privateKey);
		}
		return sign;
	}

//	/**
//	 * 对易宝支付返回的结果进行验签
//	 * 
//	 * @param data
//	 *            易宝支付返回的业务数据密�?
//	 * @param encrypt_key
//	 *            易宝支付返回的对ybAesKey加密后的密文
//	 * @param yibaoPublickKey
//	 *            易宝支付提供的公�?
//	 * @param merchantPrivateKey
//	 *            商户自己的私�?
//	 * @return 验签是否通过
//	 * @throws Exception
//	 */
//
//	public static boolean checkDecryptAndSign(String data, String encrypt_key,
//			String yibaoPublickKey, String merchantPrivateKey) throws Exception {
//
//		/** 1.使用YBprivatekey解开aesEncrypt�? */
//		String AESKey = "";
//		try {
//			AESKey = RSA.decrypt(encrypt_key, merchantPrivateKey);
//		} catch (Exception e) {
//			/** AES密钥解密失败 */
//			e.printStackTrace();
//			return false;
//		}
//
//		/** 2.用aeskey解开data。取得data明文 */
//		String realData = AES.decryptFromBase64(data, AESKey);
//		
//		TreeMap<String, String> map = JSON.parseObject(realData,
//				new TypeReference<TreeMap<String, String>>() {
//				});
//
//		/** 3.取得data明文sign�? */
//		//String sign = StringUtils.trimToEmpty(map.get("sign"));
//		String sign = "";
//		
//		/** 4.对map中的值进行验�? */
//		StringBuffer signData = new StringBuffer();
//		Iterator<Entry<String, String>> iter = map.entrySet().iterator();
//		while (iter.hasNext()) {
//			Entry<String, String> entry = iter.next();
//
//			/** 把sign参数隔过�? */
//			if (((String) entry.getKey()).equals("sign")) {
//				continue;
//			}
//			signData.append(entry.getValue() == null ? "" : entry.getValue());
//		}
//		
//		/** 5. result为true时表明验签�?�过 */
//		boolean result = RSA.checkSign(signData.toString(), sign,
//				yibaoPublickKey);
//
//		return result;
//	}

//	/**
//	 * 生成hmac
//	 */
//	public static String handleHmac(TreeMap<String, String> map, String hmacKey) {
//		StringBuffer sbuffer = new StringBuffer();
//		for (Map.Entry<String, String> entry : map.entrySet()) {
//			sbuffer.append(entry.getValue());
//		}
//		String hmacTemp = sbuffer.toString();
//
//		String hmac = "";
//		if (StringUtils.isNotEmpty(hmacKey)) {
//			hmac = Digest.hmacSHASign(hmacTemp, hmacKey, Digest.ENCODE);
//		}
//		return hmac;
//	}
}