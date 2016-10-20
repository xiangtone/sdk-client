package com.wap.pay.statistics;
/**
 * @author Administrator
 *
 */
public class EncodeUtils {
	
	
	/**
	 * @param data
	 * @return
	 */
	public static String encode(String data){
		 return encodeData(data);
	} 
	
	/**
	 * @param contents
	 * @return
	 */
	private static String encodeData(String contents) {
		StringBuilder builder = new StringBuilder();
		char [] chars = contents.toCharArray();
	
		for (int i = 0; i < chars.length; i++) {
			chars[i]^=520;
			builder.append(chars[i]);
		}
		return builder.toString();
		
	}

}
