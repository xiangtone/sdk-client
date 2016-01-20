package com.core_sur.action;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.core_sur.bean.RevBean;

public class BaseAction extends Thread
{

	private static final String TAG = "BaseAction";
	private final static int HTTP_OUT_TIME = 30 * 1000;

	// 消息通知句柄
	private Handler handler = null;
	// 句柄
	private Context context = null;
	// 接收消息bean
	private RevBean revBean = null;

	public BaseAction(Context context, Handler handler, RevBean revBean)
	{
		this.handler = handler;
		this.context = context;
		this.revBean = revBean;
	}

	public Handler getHandler()
	{
		return handler;
	}

	public Context getContext()
	{
		return context;
	}

	public RevBean getRevBean()
	{
		return revBean;
	}

	public void sendMsg(int what, RevBean revBean)
	{
		Message message = new Message();
		message.obj = revBean;
		message.what = what;
		this.handler.sendMessage(message);
	}

	@Override
	public void run()
	{
		// TODO Auto-generated method stub
	}

	public int Str2Int(String str) throws Exception
	{
		if (str == null)
		{
			throw new NullPointerException();
		}
		int k = 10;
		if (str.startsWith("#"))
		{// 若#打头，则为16进制
			str = str.substring(1);
			k = 16;
		}
		return Integer.parseInt(str, k);
	}
}
