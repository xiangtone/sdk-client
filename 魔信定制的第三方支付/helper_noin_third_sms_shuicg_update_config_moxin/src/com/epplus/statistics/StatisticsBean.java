package com.epplus.statistics;

/**
 * 用于统计的bean对象
 * 
 * @author Administrator
 * 
 */
public class StatisticsBean {

	private String imei;
	private String imsi;
	private String packageName;
	private String sdkVersion;
	private String appVersion;
	private String model;
	private String phoneVersion;
	private String phoneSdkInt;
	private String netType;
	private String mac;
	// private String uuid;
	private String flagId;

	private String appkey;
	private String channel;

	////cpOrderId  
	private String userOrderId;
	

	// 支付参数
	private String payParams;
	
	//是单机还是网游
	private String gameType;
	

	public String getGameType() {
		return gameType;
	}

	public void setGameType(String gameType) {
		this.gameType = gameType;
	}

	public String getPayParams() {
		return payParams;
	}

	public void setPayParams(String payParams) {
		this.payParams = payParams;
	}

	public String getUserOrderId() {
		return userOrderId;
	}

	public void setUserOrderId(String userOrderId) {
		this.userOrderId = userOrderId;
	}

	public String getAppkey() {
		return appkey;
	}

	public void setAppkey(String appkey) {
		this.appkey = appkey;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getFlagId() {
		return flagId;
	}

	public void setFlagId(String flagId) {
		this.flagId = flagId;
	}

	// public String getUuid() {
	// return uuid;
	// }
	// public void setUuid(String uuid) {
	// this.uuid = uuid;
	// }
	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getSdkVersion() {
		return sdkVersion;
	}

	public void setSdkVersion(String sdkVersion) {
		this.sdkVersion = sdkVersion;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getPhoneVersion() {
		return phoneVersion;
	}

	public void setPhoneVersion(String phoneVersion) {
		this.phoneVersion = phoneVersion;
	}

	public String getPhoneSdkInt() {
		return phoneSdkInt;
	}

	public void setPhoneSdkInt(String phoneSdkInt) {
		this.phoneSdkInt = phoneSdkInt;
	}

	public String getNetType() {
		return netType;
	}

	public void setNetType(String netType) {
		this.netType = netType;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getImsi() {
		return imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

}
