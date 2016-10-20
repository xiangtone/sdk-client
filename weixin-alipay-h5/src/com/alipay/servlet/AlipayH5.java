package com.alipay.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.util.TextUtils;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alipay.config.AlipayConfig;
import com.alipay.util.AlipaySubmit;
import com.wap.pay.statistics.HttpStatistics;
import com.wap.pay.statistics.PayParams;
import com.wap.pay.utils.AES;


/**
 * Servlet implementation class AlipayH5
 */
@WebServlet("/AlipayH5")
public class AlipayH5 extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AlipayH5() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
		

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=UTF-8");
		
		
		
//		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
//		 //商户订单号，商户网站订单系统中唯一订单号，必填
//        String out_trade_no =format.format(new Date()); //new String(request.getParameter("WIDout_trade_no").getBytes("ISO-8859-1"),"UTF-8");
//		
//        //订单名称，必填
//        String subject ="测试"; //new String(request.getParameter("WIDsubject").getBytes("ISO-8859-1"),"UTF-8");
//		
//        //付款金额，必填
//        String total_fee = "0.01";//new String(request.getParameter("WIDtotal_fee").getBytes("ISO-8859-1"),"UTF-8");
//		
//        //收银台页面上，商品展示的超链接，必填
//        String show_url = "http://www.baidu.com";//new String(request.getParameter("WIDshow_url").getBytes("ISO-8859-1"),"UTF-8");
//		
//        //商品描述，可空
//        String body = "商品描述";//new String(request.getParameter("WIDbody").getBytes("ISO-8859-1"),"UTF-8");
//		
        
        
        
         String out_trade_no =request.getParameter("WIDout_trade_no");
		
        //订单名称，必填
        String subject =request.getParameter("WIDsubject");
		
        //付款金额，必填  分
        String total_fee = request.getParameter("WIDtotal_fee");
		
        //收银台页面上，商品展示的超链接，必填
        String show_url = request.getParameter("WIDshow_url");
		 
        //商品描述，可空
        String body = request.getParameter("WIDbody");
        
        //return_url
        String return_url = request.getParameter("WIDreturn_url");
		
        
		// 基本信息
        String appkey = request.getParameter("appkey");
        String channel = request.getParameter("channel");
        String OrderIdCp = out_trade_no;//request.getParameter("OrderIdSelf");
        
        if(TextUtils.isEmpty(appkey)){
        	response.getWriter().append("appkey 不能为null");
        	return;
        }
        if(TextUtils.isEmpty(channel)){
        	response.getWriter().append("channel 不能为null");
        	return;
        }
        if(TextUtils.isEmpty(OrderIdCp)){
        	response.getWriter().append("WIDout_trade_no 不能为null");
        	return;
        }
		
        Random random = new Random();
		String WebOrderId = random.nextInt(100)+"web"+String.valueOf(System.currentTimeMillis());
        
        String xx_notifyData = getNotifyJsonData(channel, appkey, "alipayWapH5", WebOrderId, OrderIdCp);
		System.out.println("xx_notifyData = "+xx_notifyData);
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());

		//////////////////////////////////////////////////////////////////////////////////

		// 把请求参数打包成数组
		Map<String, String> sParaTemp = new HashMap<String, String>();
		sParaTemp.put("service", AlipayConfig.service);
		sParaTemp.put("partner", AlipayConfig.partner);
		sParaTemp.put("seller_id", AlipayConfig.seller_id);
		sParaTemp.put("_input_charset", AlipayConfig.input_charset);
		sParaTemp.put("payment_type", AlipayConfig.payment_type);
		//sParaTemp.put("notify_url", AlipayConfig.notify_url);//+"?xx_notifyData="+xx_notifyData
		sParaTemp.put("notify_url", AlipayConfig.notify_url);
		sParaTemp.put("return_url", return_url);//AlipayConfig.return_url
		sParaTemp.put("out_trade_no", out_trade_no);
		sParaTemp.put("subject", subject);
		
		
		
		float money = 0;
		int price = 0;
		try {
			price =Integer.parseInt(total_fee);
			money = Float.parseFloat(total_fee);
			money = money/100;
		} catch (Exception e) {
			e.printStackTrace();
			response.getWriter().append("WIDtotal_fee 为正数 ");
			return ;
		} 
		
		String total_fee_str = String.format("%.2f", money);
		
		sParaTemp.put("total_fee", total_fee_str);
		sParaTemp.put("show_url", show_url);
		// sParaTemp.put("app_pay","Y");//启用此参数可唤起钱包APP支付。
		//sParaTemp.put("body", body);
		try {
			String data = AES.Encrypt(xx_notifyData, AlipayConfig.aes_key);
			sParaTemp.put("body", data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// 其他业务参数根据在线开发文档，添加参数.文档地址:https://doc.open.alipay.com/doc2/detail.htm?spm=a219a.7629140.0.0.2Z6TSk&treeId=60&articleId=103693&docType=1
		// 如sParaTemp.put("参数名","参数值");
		
		PayParams payParams = new PayParams(price, OrderIdCp, subject, body);
		payParams.setWebOrderid(WebOrderId);
		HttpStatistics.statistics(appkey, channel, 901, "1", payParams);
 
		// 建立请求
		String sHtmlText = AlipaySubmit.buildRequest(sParaTemp, "get", "确认");
		
		response.getWriter().println(sHtmlText);
		System.out.println(sHtmlText);

	}
	
	
	public static String getNotifyJsonData(String channel,String appkey,String platform,String WebOrderId,String OrderIdCp){
		
		JSONObject obj = new JSONObject();
		try {
			obj.put("a", channel);
			obj.put("k", appkey);
			obj.put("p",platform);
			obj.put("s",WebOrderId);
			obj.put("c",OrderIdCp);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		String json = obj.toString();
		return json;
	}

}
