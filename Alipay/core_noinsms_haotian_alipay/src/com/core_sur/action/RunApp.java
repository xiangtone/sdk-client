package com.core_sur.action;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.core_sur.Config;
import com.core_sur.bean.RevBean;

public class RunApp extends BaseAction
{

	public RunApp(Context context, Handler handler, RevBean revBean)
	{
		super(context, handler, revBean);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		String tempArray[] = getRevBean().getMsg().split(Config.splitStringLevel1);
		ComponentName comp = new ComponentName(tempArray[0], tempArray[1]);
		Intent i = new Intent();
		i.setComponent(comp);
		i.setAction("android.intent.action.MAIN");
		getContext().startActivity(i);

		sendMsg(0, getRevBean());
	}
}
