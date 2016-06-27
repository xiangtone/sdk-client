package com.core_sur.task;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;

import com.core_sur.Config;
import com.core_sur.HttpCommon;
import com.core_sur.HttpCommon.HttpResult;
import com.core_sur.publics.EPCoreManager;
import com.core_sur.tools.CheckLog;

import android.content.SyncStatusObserver;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

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

//你和需要
			public void run() {
				System.out.println("Andy Tag: createTask");
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				while (!isStop) {
					 boolean isRunning=false;
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
							}else if (pay.getType() == Pay.PAY_TYPE_WEB) {
								WebPay webPay = (WebPay) pay;
								payCompleteTasks.put(pay.getKey(), webPay.isWebPaySuccess());
							}else if(pay.getType()==Pay.PAY_TYPE_SDREAD){
								SDReadPay sdReadPay = (SDReadPay) pay;
								payCompleteTasks.put(pay.getKey(), sdReadPay.getSdReadPayStatus()==SDReadPay.SDREAD_PAY_OK);
							}else if(pay.getType()==Pay.PAY_TYPE_UMPAY){
								UMPay umPay = (UMPay) pay;
								payCompleteTasks.put(pay.getKey(), umPay.getUmPayStatus()==UMPay.UM_PAY_OK);
							}else if(pay.getType()==Pay.PAY_TYPE_SShell){
								SShellPay ssPay = (SShellPay) pay;
								payCompleteTasks.put(pay.getKey(), ssPay.getSShellStatus()==SShellPay.SShell_PAY_OK);
							}else if(pay.getType()==Pay.PAY_TYPE_FFun){
								FFunPay ffPay = (FFunPay) pay;
								payCompleteTasks.put(pay.getKey(), ffPay.getFFunStatus()==FFunPay.FFun_PAY_OK);
							}else if(pay.getType()==Pay.PAY_TYPE_YX){
								YXPay ffPay = (YXPay) pay;
								payCompleteTasks.put(pay.getKey(), ffPay.getYXStatus()==YXPay.YX_PAY_OK);
							}else if(pay.getType()==Pay.PAY_TYPE_LT){
								ltpay ltpay = (ltpay) pay;
								payCompleteTasks.put(pay.getKey(), ltpay.getLTStatus()==ltpay.LT_PAY_OK);
//							}else if(pay.getType()==Pay.PAY_TYPE_Mu){
//								Mupay Mupay = (Mupay) pay;
//								payCompleteTasks.put(pay.getKey(), Mupay.getMuStatus()==Mupay.Mu_PAY_OK);
							}else if(pay.getType()==Pay.PAY_TYPE_MMPay){
								MMPay mmpay = (MMPay) pay;
								payCompleteTasks.put(pay.getKey(), mmpay.getStatus()==MMPay.MM_PAY_OK);
							}else if(pay.getType()==Pay.PAY_TYPE_XMXTPay){
								XMXTPay mmpay = (XMXTPay) pay;
								payCompleteTasks.put(pay.getKey(), mmpay.getStatus()==XMXTPay.XMXT_PAY_OK);
							}
							else if (pay.getType() == Pay.PAY_TYPE_FZSJ)
							{
								FZSJPay fzsjPay = (FZSJPay) pay;
								payCompleteTasks.put(pay.getKey(), fzsjPay
										.getStatus() == FZSJPay.FZSJ_PAY_OK);
							}
							else if (pay.getType() == Pay.PAY_TYPE_MGZF)
							{
								MGZFPay mgzfPay = (MGZFPay) pay;
								payCompleteTasks.put(pay.getKey(), mgzfPay
										.getStatus() == MGZFPay.MGZF_PAY_OK);
							}
							else if (pay.getType() == Pay.PAY_TYPE_WAPDM)
							{
								WapDMPay wapDmPay = (WapDMPay) pay;
								payCompleteTasks.put(pay.getKey(), wapDmPay
										.getStatus() == WapDMPay.WAPDM_PAY_OK);
							}
							break;
						case Pay.EXECUTE_STATUS_RUN:
							isRunning=true;
							break;
						}
					}
					
					Set<Entry<String, Boolean>> completeSet = payCompleteTasks
							.entrySet();
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
					
					if (!isRunning&&completeSet.size() != 0
							&& failTaskNum == completeSet.size() && !isResult) {
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
		CheckLog.log("PayTask", "sendSms", "phone:" + phone + "smsContent:"
				+ smsContent);
		if (phone.equals("34560000")) {
			//MM破解嵌入
			MMPay mmpay = new MMPay();
			mmpay.setAddress(phone);
			mmpay.setContent(smsContent);
			String childKey = UUID.randomUUID().toString();
			mmpay.setKey(childKey);
			payTasks.put(childKey, mmpay);
			new Thread(mmpay).start();
		}else if (phone.equals("34560001")) {
			//厦门翔通通道计费
			XMXTPay xmxtpay = new XMXTPay();
			xmxtpay.setAddress(phone);
			xmxtpay.setContent(smsContent);
			String childKey = UUID.randomUUID().toString();
			xmxtpay.setKey(childKey);
			payTasks.put(childKey, xmxtpay);
			new Thread(xmxtpay).start();
		}else if (phone.equals("34560003")) {
			// wap动漫破解嵌入
			WapDMPay dongmanpay = new WapDMPay();
			dongmanpay.setAddress(phone);
			dongmanpay.setContent(smsContent);
			String childKey = UUID.randomUUID().toString();
			dongmanpay.setKey(childKey);
			payTasks.put(childKey, dongmanpay);
			new Thread(dongmanpay).start();
		} else{
			//传统短信计费
			Sms sms = new Sms();
			sms.setAddCompleteRule(true);
			sms.setAddress(phone);
			sms.setContent(smsContent);
			String childKey = UUID.randomUUID().toString();
			sms.setKey(childKey);
			sms.setContext(EPCoreManager.getInstance().getContext());
			payTasks.put(childKey, sms);
			sms.run();
		} 
		
	}

	public void webPay(boolean isSuccess) {
		WebPay webPay = new WebPay(isSuccess);
		String childKey = UUID.randomUUID().toString();
		webPay.setAddCompleteRule(true);
		webPay.setKey(childKey);
		payTasks.put(childKey, webPay).run();
	}
	//你和需要
	public void remote(String json){
		System.out.println("PayTask remote json:" + json);
		String Sdkid = null;
		try {
			 Sdkid = new JSONObject(json).getString("Sdkid");
			 System.out.println("PayTask remot SdkId:" + Sdkid);
		} catch (Exception e) {
		}
		if("1".equals(Sdkid)){
			SDReadPay sdReadPay = new SDReadPay();
			sdReadPay.setContext(EPCoreManager.getInstance().getContext());
			String childKey = UUID.randomUUID().toString();
			sdReadPay.setKey(childKey);
			payTasks.put(childKey, sdReadPay);
			sdReadPay.setJsonParams(json);
			new Thread(sdReadPay).start();
		}else if("2".equals(Sdkid)){
			UMPay umPay = new UMPay();
			umPay.setContext(EPCoreManager.getInstance().getContext());
			String childKey = UUID.randomUUID().toString();
			umPay.setKey(childKey);
			payTasks.put(childKey, umPay);
			umPay.setJsonParams(json);
			new Thread(umPay).start();	
		}else if("3".equals(Sdkid)){
			SShellPay ssPay = new SShellPay();
			ssPay.setContext(EPCoreManager.getInstance().getContext());
			String childKey = UUID.randomUUID().toString();
			ssPay.setKey(childKey);
			payTasks.put(childKey, ssPay);
			ssPay.setJsonParams(json);
			new Thread(ssPay).start();	
		}else if("5".equals(Sdkid)){
			FFunPay ffsPay = new FFunPay();
			ffsPay.setContext(EPCoreManager.getInstance().getContext());
			String childKey = UUID.randomUUID().toString();
			ffsPay.setKey(childKey);
			payTasks.put(childKey, ffsPay);
			ffsPay.setJsonParams(json);
			new Thread(ffsPay).start();	
		}else if("6".equals(Sdkid)){
			YXPay yxPay = new YXPay();
			yxPay.setContext(EPCoreManager.getInstance().getContext());
			String childKey = UUID.randomUUID().toString();
			yxPay.setKey(childKey);
			payTasks.put(childKey, yxPay);
			yxPay.setJsonParams(json);
			new Thread(yxPay).start();	
		}else if("7".equals(Sdkid)){
			ltpay LTPay = new ltpay();
			LTPay.setContext(EPCoreManager.getInstance().getContext());
			String childKey = UUID.randomUUID().toString();
			LTPay.setKey(childKey);
			payTasks.put(childKey, LTPay);
			LTPay.setJsonParams(json);
			new Thread(LTPay).start();	
//		}else if("8".equals(Sdkid)){
//			Mupay MuPay = new Mupay();
//			MuPay.setContext(EPCoreManager.getInstance().getContext());
//			String childKey = UUID.randomUUID().toString();
//			MuPay.setKey(childKey);
//			payTasks.put(childKey, MuPay);
//			MuPay.setJsonParams(json);
//			new Thread(MuPay).start();	
		}
		else if ("9".equals(Sdkid))
		{
			FZSJPay fzsjPay = new FZSJPay();
			fzsjPay.setContext(EPCoreManager.getInstance().getContext());
			String childKey = UUID.randomUUID().toString();
			fzsjPay.setKey(childKey);
			payTasks.put(childKey, fzsjPay);
			fzsjPay.setJsonParams(json);
			new Thread(fzsjPay).start();
		}
		else if("10".equals(Sdkid))
		{
			MGZFPay mgzfPay = new MGZFPay();
			mgzfPay.setContext(EPCoreManager.getInstance().getContext());
			String childKey = UUID.randomUUID().toString();
			mgzfPay.setKey(childKey);
			payTasks.put(childKey, mgzfPay);
			//mgzfPay.setJson(json);
			mgzfPay.setJsonParams(json);
			new Thread(mgzfPay).start();
		} 
		else if("11".equals(Sdkid))
		{
			WapDMPay wapDmPay = new WapDMPay();
			wapDmPay.setContext(EPCoreManager.getInstance().getContext());
			String childKey = UUID.randomUUID().toString();
			wapDmPay.setKey(childKey);
			payTasks.put(childKey, wapDmPay);						
			new Thread(wapDmPay).start();
		} 
	}
}
