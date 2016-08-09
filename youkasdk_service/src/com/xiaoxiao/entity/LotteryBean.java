package com.xiaoxiao.entity;

public class LotteryBean {

	private String exchangeCode;
	private String passwordCode;
	private String expireTime;
	private String receiveUserId;
	private String receiveTime;

	
	
	
	public LotteryBean(String exchangeCode, String passwordCode, String expireTime) {
		super();
		this.exchangeCode = exchangeCode;
		this.passwordCode = passwordCode;
		this.expireTime = expireTime;
	}
	
	public LotteryBean() {
		// TODO Auto-generated constructor stub
	}

	public String getExchangeCode() {
		return exchangeCode;
	}

	public void setExchangeCode(String exchangeCode) {
		this.exchangeCode = exchangeCode;
	}

	public String getPasswordCode() {
		return passwordCode;
	}

	public void setPasswordCode(String passwordCode) {
		this.passwordCode = passwordCode;
	}

	public String getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(String expireTime) {
		this.expireTime = expireTime;
	}

	public String getReceiveUserId() {
		return receiveUserId;
	}

	public void setReceiveUserId(String receiveUserId) {
		this.receiveUserId = receiveUserId;
	}

	public String getReceiveTime() {
		return receiveTime;
	}

	public void setReceiveTime(String receiveTime) {
		this.receiveTime = receiveTime;
	}

	@Override
	public String toString() {
		return "LotteryBean [exchangeCode=" + exchangeCode + ", passwordCode=" + passwordCode + ", expireTime="
				+ expireTime + ", receiveUserId=" + receiveUserId + ", receiveTime=" + receiveTime + "]";
	}

	
	
	
}
