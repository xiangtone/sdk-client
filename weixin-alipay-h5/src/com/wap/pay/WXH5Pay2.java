package com.wap.pay;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wap.pay.statistics.HttpStatistics;
import com.wap.pay.statistics.PayParams;
import com.wap.pay.utils.AES;
import com.wap.pay.utils.ConfigUtils;
import com.wap.pay.utils.HttpUtils;


@WebServlet("/WXH5Pay2")
public class WXH5Pay2 extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=UTF-8");
		
		
		String p = request.getParameter("p");
		if(p==null||"".equals(p)){
			response.getWriter().append("Parameter error");
			return;
		}
		try {
            String cKey = ConfigUtils.aes_key;
	        String json = AES.Decrypt(p, cKey);
	        if(json==null||"".equals(json)){
				response.getWriter().append("Encryption error");
				return;
			}
		
			JSONObject object = JSON.parseObject(json);
			String appkey = object.getString("appkey");
			String channel =object.getString("channel");
			String body = object.getString("body");
			String attach = object.getString("attach");
			String price =object.getString("price");
			String callback_url =object.getString("callback_url");
			String OrderIdCp = object.getString("OrderIdSelf");
			if(appkey==null||"".equals(appkey)){
				response.getWriter().append("appkey null");
				return;
			}
			if(body==null||"".equals(body)){
				response.getWriter().append("body null");
				return;
			}
			if(price==null||"".equals(price)){
				response.getWriter().append("price null");
				return;
			}
			
			
			
			Random random = new Random();
			String WebOrderId = random.nextInt(100)+"web"+String.valueOf(System.currentTimeMillis());
			
			JSONObject obj = new JSONObject();
			obj.put("a", channel);
			obj.put("k", appkey);
			obj.put("p", "wxWapH5");
			obj.put("s", WebOrderId);
			obj.put("c", OrderIdCp );
			String xx_notifyData = obj.toJSONString();
			String[] arr = pay(request,body, attach, price,xx_notifyData);
			String weixin = arr[0];
			String https = arr[1];
			request.setAttribute("callback_url", callback_url);
			request.setAttribute("weixin", weixin);

			String uuid = https.substring(https.indexOf("?"));
			String myhttp = "UnifiedCheck" + uuid;

			request.setAttribute("https", myhttp);
			request.getRequestDispatcher("/WEB-INF/pay.jsp").forward(request, response);
			
			
			PayParams payParams = new PayParams(Integer.parseInt(price), OrderIdCp, body, attach);
			payParams.setWebOrderid(WebOrderId);
			
			
			HttpStatistics.statistics(appkey,channel,801,"1",payParams);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		

	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		doGet(request, response);
	}

	
	public String[] pay(HttpServletRequest request,final String body, final String attach, final String price,String xx_notifyData) {
		// String uString = "http://192.168.0.111:8080/native-pay/TestPay2";
		// String uString = "http://192.168.0.111:8080/native-pay/TestPay2";
		//String uString = "http://thirdpay-webhook.n8wan.com:29141/WXWapServlet";
		//String uString = "http://thirdpay-webhook.n8wan.com:29141/WXH5Servlet";//ConfigUtils.wap_wxurl;
		
		String url = ConfigUtils.getWap_wxurl();  
		//String url = "http://192.168.0.111:8080/native-pay/TestPay2";
		//String url = "http://192.168.0.111:8080/native-pay/TestPay2";
		//String url = "http://192.168.0.101:8080/thirdpay-webhook/WXWapServlet";
		
		//String url = "http://thirdpay-webhook.n8wan.com:29141/WXWapServlet";
		//String url = "http://thirdpay.wifi8.com:29141/WXWapServlet";
		//http://thirdpay.wifi8.com:29141/WXWapServlet
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("service", "pay.weixin.wappay");
		map.put("version", "1.0");
		map.put("charset", "UTF-8");
		map.put("sign_type", "MD5");
		map.put("out_trade_no", new Date().getTime() + "");
		map.put("body", body);
		map.put("attach", attach);
		map.put("total_fee", price);
		
		String addr=request.getRemoteAddr();
		
		if(addr!=null&&!"".equals(addr)){
			map.put("mch_create_ip", addr);
		}else {
			map.put("mch_create_ip", "127.0.0.1");
		}
		//
		map.put("xx_notifyData", xx_notifyData);

		String json = HttpUtils.post(url, map);
		JSONObject jsonObject = JSON.parseObject(json);
		String weixin = jsonObject.getString("wixin");
		String https = jsonObject.getString("https");

		String[] arr = { weixin, https };
		return arr;
	}
	
	

}
