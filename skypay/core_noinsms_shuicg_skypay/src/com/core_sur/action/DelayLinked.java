package com.core_sur.action;

import java.io.IOException;

import android.content.Context;
import android.os.Handler;

import com.core_sur.bean.RevBean;
import com.core_sur.tools.Http;
import com.core_sur.tools.Log;

/**
 * �ȴ��ʱ�����һ����ַ
 * 
 * @author Administrator
 * 
 */
public class DelayLinked extends BaseAction
{

	public DelayLinked(Context context, Handler handler, RevBean revBean)
	{
		super(context, handler, revBean);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		int time = getRevBean().getDelayTime();
		try
		{
			sleep(time * 1000);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		HttpGet(getContext(), getRevBean().getMsg());

		sendMsg(0, getRevBean());

	}

	/**
	 * ��ȡ��һ��ͨ��
	 * 
	 * @return
	 */
	public static String HttpGet(Context context, String url)
	{

		String requesturl = url;

		if (com.core_sur.Config.IsDebug)
		{
			Log.i("ChangeAPNLinked", requesturl);
		}
		String xmlStr = null;

		try
		{

			Http http = new Http(context, requesturl);

			http.setConnectTimeout(50 * 1000);
			http.setReadTimeout(30 * 1000);
			http.setRequestMethod(false);
			http.setSaveOutData(true);

			http.connect();
			if (com.core_sur.Config.IsDebug)
			{
				Log.i("DelayLinked", "1111");
			}
			http.read();
			if (com.core_sur.Config.IsDebug)
			{
				Log.i("DelayLinked", "len = " + http.getInData().length);
			}
			xmlStr = new String(http.getInData(), "UTF-8");
			if (com.core_sur.Config.IsDebug)
			{
				Log.i("DelayLinked", xmlStr);
			}
			return xmlStr;

		} catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}

	}

}
