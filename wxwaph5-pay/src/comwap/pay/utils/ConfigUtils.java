package comwap.pay.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class ConfigUtils {
	
	/**
	 */
	private  static String wap_wxurl ;
	
	/**
	 * AES key
	 */
	public static String aes_key ;
	
	/**
	 * 
	 * @return
	 */
	public static String getWap_wxurl(){
		return wap_wxurl+"WXH5Servlet";
	}
	
	
	public static String getStatisticsurl(){
		return wap_wxurl+"PayOperateCountServlet";
	}
	
	
	
	 static{
	        Properties prop = new Properties();   
	        InputStream in = ConfigUtils.class.getResourceAsStream("/config.properties");   
	        try {   
	            prop.load(in);   
	            wap_wxurl = prop.getProperty("wap_wxurl").trim();   
	            aes_key = prop.getProperty("aes_key").trim();   
	        } catch (IOException e) {   
	            e.printStackTrace();   
	        } 
	    }
	
	

}
