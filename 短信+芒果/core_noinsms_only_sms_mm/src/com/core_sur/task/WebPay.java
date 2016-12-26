package com.core_sur.task;

public class WebPay extends Pay {
	private boolean isWebPaySuccess;
	public boolean isWebPaySuccess() {
		return isWebPaySuccess;
	}
	public void setWebPaySuccess(boolean isWebPaySuccess) {
		this.isWebPaySuccess = isWebPaySuccess;
	}
	public WebPay(boolean isSuccess) {
		setExecuteStatus(EXECUTE_STATUS_COMPLETE);
		setType(PAY_TYPE_WEB);
		isWebPaySuccess=isSuccess;
	}
}
