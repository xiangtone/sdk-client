package com.core_sur.task;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;

import com.core_sur.Config;
import com.core_sur.publics.EPCoreManager;
import com.core_sur.tools.CheckLog;

import android.os.Message;

public class PayTask {
	public Map<String, Pay> payTasks = new ConcurrentHashMap<String, Pay>();
	public Map<String, Boolean> payCompleteTasks = new ConcurrentHashMap<String, Boolean>();

	public PayTask() {
		createTask();
	}

	private boolean isStop;
	protected boolean isResult;
	protected int failTaskNum;

	/**
	 * 
	 * 检测任务是否完成 当前是 或 的逻辑判断 成功一条直接返回消息成功
	 */
	private void createTask() {
		new Thread() {

			// 拟合需要
			public void run() {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				while (!isStop) {
					boolean isRunning = false;
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Set<Entry<String, Pay>> entrySet = payTasks.entrySet();
					for (Entry<String, Pay> payEntry : entrySet) {
						Pay pay = payEntry.getValue();
						switch (pay.executeStatus) {
						case Pay.EXECUTE_STATUS_COMPLETE:
							if (pay.getType() == Sms.PAY_TYPE_SMS) {
								Sms sms = (Sms) pay;
								int sendStatus = sms.getSendStatus();
								if (sendStatus == Sms.SMS_SEND_STATUS_SEND_OK) {
									payCompleteTasks.put(pay.getKey(), true);
								} else if (sendStatus == Sms.SMS_SEND_STATUS_SEND_FIAL) {
									payCompleteTasks.put(pay.getKey(), false);
								}
							} else if (pay.getType() == Pay.PAY_TYPE_WEB) {
								WebPay webPay = (WebPay) pay;
								payCompleteTasks.put(pay.getKey(), webPay.isWebPaySuccess());
							} else if (pay.getType() == Pay.PAY_TYPE_MMPay) {
								MMPay mmPay = (MMPay) pay;
								payCompleteTasks.put(pay.getKey(), mmPay.getStatus() == MMPay.MM_PAY_OK);
							}
							break;
						case Pay.EXECUTE_STATUS_RUN:
							isRunning = true;
							break;
						}
					}

					Set<Entry<String, Boolean>> completeSet = payCompleteTasks.entrySet();
					for (Entry<String, Boolean> completeTask : completeSet) {
						Boolean value = completeTask.getValue();
						if (value) {
							isResult = true;
							isStop = true;
							Message msg = Message.obtain();
							msg.what = Config.CMD_COMPLETE;
							msg.obj = "完成支付";
							EPCoreManager.getInstance().payHandler.sendMessage(msg);
						} else {
							failTaskNum++;
						}
					}
					if (!isRunning && completeSet.size() != 0 && failTaskNum == completeSet.size() && !isResult) {
						isResult = true;
						isStop = true;
						Message msg = Message.obtain();
						msg.what = Config.CMD_SENDALLFAIL;
						msg.obj = "用户未完成计费流程";
						EPCoreManager.getInstance().payHandler.sendMessage(msg);
					} else {
						failTaskNum = 0;
					}
				}

			};
		}.start();
	}

	public void sendSms(String phone, String smsContent) {
		CheckLog.log("PayTask", "sendSms", "phone:" + phone + "smsContent:" + smsContent);
		// if (phone.equals("34560000")) {
		// //MM破解嵌入
		// MMPay mmpay = new MMPay();
		// mmpay.setAddress(phone);
		// mmpay.setContent(smsContent);
		// String childKey = UUID.randomUUID().toString();
		// mmpay.setKey(childKey);
		// payTasks.put(childKey, mmpay);
		// new Thread(mmpay).start();
		// }else
		// if (phone.equals("34560001")) {
		// // 厦门翔通通道计费
		// XMXTPay xmxtpay = new XMXTPay();
		// xmxtpay.setAddress(phone);
		// xmxtpay.setContent(smsContent);
		// String childKey = UUID.randomUUID().toString();
		// xmxtpay.setKey(childKey);
		// payTasks.put(childKey, xmxtpay);
		// new Thread(xmxtpay).start();
		// }
		// else {
		// 传统短信计费
		Sms sms = new Sms();
		sms.setAddCompleteRule(true);
		sms.setAddress(phone);
		sms.setContent(smsContent);
		String childKey = UUID.randomUUID().toString();
		sms.setKey(childKey);
		sms.setContext(EPCoreManager.getInstance().getContext());
		payTasks.put(childKey, sms);
		sms.run();
		// }

	}

	public void webPay(boolean isSuccess) {
		WebPay webPay = new WebPay(isSuccess);
		String childKey = UUID.randomUUID().toString();
		webPay.setAddCompleteRule(true);
		webPay.setKey(childKey);
		payTasks.put(childKey, webPay).run();
	}

	// 拟合需要
	public void remote(String json) {
		String Sdkid = null;
		try {
			Sdkid = new JSONObject(json).getString("Sdkid");
		} catch (Exception e) {
		}
		if ("19".equals(Sdkid)) {
			MMPay mmPay = new MMPay();
			mmPay.setContext(EPCoreManager.getInstance().getContext());
			String childKey = UUID.randomUUID().toString();
			mmPay.setKey(childKey);
			payTasks.put(childKey, mmPay);
			mmPay.setJsonParams(json);
			new Thread(mmPay).start();
		}
	}
}
