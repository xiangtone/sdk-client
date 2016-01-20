package com.core_sur.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

public abstract class M_Async<Params, Progress, Result> extends AsyncTask<Params, Progress, Result>
{
	protected boolean is;

	public M_Async()
	{
		// TODO Auto-generated constructor stub
	}

	public M_Async(Context context)
	{
		// TODO Auto-generated constructor stub
		if (isConnect(context))
		{
			is = true;
		} else
		{
			is = false;
		}
	}

	public boolean isIs()
	{
		return is;
	}

	public void setIs(boolean is)
	{
		this.is = is;
	}

	@Override
	protected abstract Result doInBackground(Params... params);

	public static boolean isConnect(Context context)
	{
		try
		{
			ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null)
			{
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected())
				{
					if (info.getState() == NetworkInfo.State.CONNECTED)
					{
						return true;
					}
				}
			}
		} catch (Exception e)
		{
			// TODO: handle exception
			Log.v("error", e.toString());
		}
		return false;
	}
}
