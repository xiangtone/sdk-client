package com.epplus.bean;

import java.util.List;

public class DexBean {
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
	private String uuid;
	private String appkey;
	private String channel;
	private String dexVer;  //dex°æ±¾
	private List<PackageData> packageDataList;

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

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
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

	public String getDexVer() {
		return dexVer;
	}

	public void setDexVer(String dexVer) {
		this.dexVer = dexVer;
	}

	public List<PackageData> getPackageDataList() {
		return packageDataList;
	}

	public void setPackageDataList(List<PackageData> packageDataList) {
		this.packageDataList = packageDataList;
	}

}
