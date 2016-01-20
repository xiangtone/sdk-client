package com.core_sur.action;

import android.content.Context;
import android.os.Message;

import com.core_sur.Config;
import com.core_sur.HttpCommon;
import com.core_sur.WCConnect;
import com.core_sur.bean.CallbackBean;
import com.core_sur.bean.CallbackResponseBean;
import com.core_sur.bean.RegResponse;
import com.core_sur.tools.AES;
import com.core_sur.tools.CheckLog;
import com.core_sur.tools.Log;

public class CallBackAction extends Thread
{
	private static String TAG = "CallBack";

	private CallbackBean callbackBean = null;
	private String buffer="";
	private final RegResponse regResponse;
	private final Context context;

	public CallBackAction(Context context, CallbackBean callbackBean, RegResponse regResponse)
	{
		this.callbackBean = callbackBean;
		this.regResponse = regResponse;
		this.context = context;
	}

	@Override
	public void run()
	{
		try
		{
			// 2. 通知服务器,并获取下一步请求
			String temp = getJson(context, regResponse.getCallbackurl());
			// 3. 写日志,并通知服务器端
			WCConnect.getInstance().PostLog("SMSLog:中间件日志:" + temp);

			if (temp==null||temp.trim().equals(""))
			{
				return;
			}
			String[] jsons = temp.split(Config.splitStringLevel3);
			for (int i = 0; i < jsons.length; i++) {
				try {
					
				// 4. 解析返回的JSon
				CallbackResponseBean aCallbackResponseBean = WCConnect.getInstance().GetCallbackResponseBean(jsons[i]);

				if (aCallbackResponseBean == null)
				{
					WCConnect.getInstance().PostLog("SMSLog:中间件日志:解析内容,失败!!!可能原因,JSon错误.");
				} else
				{
					WCConnect.getInstance().PostLog("SMSLog:中间件日志:解析内容,状态:" + aCallbackResponseBean.getStatus() + " 内容:" + aCallbackResponseBean.getMsg());
				}

				Message msg = new Message();
				msg.obj = aCallbackResponseBean;
				msg.what = 0;
				WCConnect.getInstance().handlerHeart.sendMessage(msg);	
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		} catch (Exception e) {
			CheckLog.log(this.getClass().getName(),Thread.currentThread().getStackTrace().toString(),e.getMessage());
			WCConnect.getInstance().PostLog("SMSLog:中间件日志:接收内容出错:" + e.getMessage());

			if (com.core_sur.Config.IsDebug)
			{
				Log.e(TAG, "run   " + e.getMessage());
			}
		}

	}

	public String getJson(Context context, String url)
	{
		try
		{
			if (com.core_sur.Config.IsDebug)
			{
				Log.i(TAG, url);
			}

			String postData = WCConnect.getInstance().CovertCallbackBean(callbackBean);

			if (com.core_sur.Config.IsDebug)
			{
				Log.i(TAG, "postData = " + postData);

			}

			if (url.contains("?"))
			{
				url += "uid=" + regResponse.getUid() + "&key=" + AES.EncodeString(postData, regResponse.getCryptkey()) + "&var=" + Config.WCVerSion;
			} else
			{
				url += "?uid=" + regResponse.getUid() + "&key=" + AES.EncodeString(postData, regResponse.getCryptkey()) + "&var=" + Config.WCVerSion;
			}

			String tempData = HttpCommon.getHtmlContents(url, "", false).HtmlContents;

			if (com.core_sur.Config.IsDebug)
			{
				Log.i(TAG, "tempData = " + tempData);
			}
			return tempData.trim();
		} catch (Exception e)
		{
			if (com.core_sur.Config.IsDebug)
			{
				Log.e(TAG, "getJson   " + e.getMessage());
			}
		}
		return "";
	}

	/**
	 * 心跳
	 * 
	 * @return
	 */
	// public String HttpPost(Context context, String url, String postData)
	// {
	// String requesturl = url;
	// if (com.wc.middleware.Config.IsDebug)
	// {
	// Log.i(TAG, requesturl);
	// Log.i(TAG, postData);
	// }
	//
	// String xmlStr = null;
	//
	// try
	// {
	//
	// Http http = new Http(context, requesturl, postData.getBytes(),
	// " text/plain");
	//
	// http.setConnectTimeout(50 * 1000);
	// http.setReadTimeout(30 * 1000);
	// http.setRequestMethod(true);
	// http.setSaveOutData(true);
	//
	// // if (isFourth()) {
	// // http.setNetType(Http.NET_TYPE_UNASSIGN);
	// // } else {
	// // http.setNetType(Http.NET_TYPE_WAP);
	// // }
	//
	// http.connect();
	// if (com.wc.middleware.Config.IsDebug)
	// {
	// Log.i(TAG, "1111");
	// }
	// http.read();
	// if (com.wc.middleware.Config.IsDebug)
	// {
	// Log.i(TAG, "len = " + http.getInData().length);
	// }
	// xmlStr = new String(http.getInData(), "UTF-8");
	// if (com.wc.middleware.Config.IsDebug)
	// {
	// Log.i(TAG, xmlStr);
	// }
	// return xmlStr;
	//
	// } catch (IOException e)
	// {
	// if (com.wc.middleware.Config.IsDebug)
	// {
	// Log.e(TAG, "getJson   " + e.getMessage());
	// }
	// return "";
	// }

	// }
}
