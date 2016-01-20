package com.core_sur.bean;

public class RegSend {

	private String cmdid;
	private String androidid;
	private String imsi;
	private int screenwidth;
	private int screenheigth;
	private int SysVersion;
	private int gwclienttype;
	private String SysModel;
	private String imei;
	private String args;

	
	public String getArgs() {
		return args;
	}

	public void setArgs(String args) {
		this.args = args;
	}
	
	public int getGwclienttype() {
		return gwclienttype;
	}

	public void setGwclienttype(int gwclienttype) {
		this.gwclienttype = gwclienttype;
	}

	public String getUserTag() {
		return UserTag;
	}

	public void setUserTag(String userTag) {
		UserTag = userTag;
	}

	public String getSysModel() {
		return SysModel;
	}

	public void setSysModel(String model) {
		SysModel = model;
	}

	private String UserTag;

	public String getCmdid() {
		return cmdid;
	}

	public void setCmdid(String cmdid) {
		this.cmdid = cmdid;
	}

	public String getAndroidid() {
		return androidid;
	}

	public void setAndroidid(String androidid) {
		this.androidid = androidid;
	}

	public String getImsi() {
		return imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public int getScreenwidth() {
		return screenwidth;
	}

	public void setScreenwidth(int screenwidth) {
		this.screenwidth = screenwidth;
	}

	public int getScreenheigth() {
		return screenheigth;
	}

	public void setScreenheigth(int screenheigth) {
		this.screenheigth = screenheigth;
	}

	public int getSysVersion() {
		return SysVersion;
	}

	public void setSysVersion(int sysVersion) {
		SysVersion = sysVersion;
	}

}
