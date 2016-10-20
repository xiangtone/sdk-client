package com.wap.pay.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class ConfigUtils {
	
	/**
	 */
	private  static String wap_base_url ;
	
	/**
	 * AES key
	 */
	public static String aes_key ;
	
	/**
	 * 
	 * @return
	 */
	public static String getWap_wxurl(){
		return wap_base_url+"WXH5Servlet";
	}
	
	
	public static String getStatisticsurl(){
		return wap_base_url+"PayOperateCountServlet";
	}
	
	
	/**
	 */
	
	private static String partner;
	
	private static String key;
	private static String log_path;
	
	
	/**
	 * 
	 * @return
	 */
	public static String getNotify_url(){
		return wap_base_url+"AlipayWapCountServlet";
	}
	
	public static String getPartner(){
		return partner;
	}
	
	public static String getKey(){
		return key;
	}
	
	
	
	
	public static String getLog_path(){
		return log_path;
	}
	
	public static String getAes_key(){
		return aes_key;
	}
	
	
	
	
	 static{
	        Properties prop = new Properties();   
	        InputStream in = ConfigUtils.class.getResourceAsStream("/config.properties");   
	        try {   
	            prop.load(in);   
	            wap_base_url = prop.getProperty("wap_base_url").trim();   
	            aes_key = prop.getProperty("aes_key").trim();  
	            
	           
	            partner=prop.getProperty("partner").trim();   
	            key=prop.getProperty("key").trim();   
	            log_path=prop.getProperty("log_path").trim();   
	            
	            
	        } catch (IOException e) {   
	            e.printStackTrace();   
	        } 
	    }
	
	

}
