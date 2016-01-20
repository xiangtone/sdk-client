package com.core_sur.bean;

public class BannerAd {
private String appName;
private String  picUrl;
private String clickUrl;
private String packagename;
private String type;
public String getType() {
	return type;
}
public void setType(String type) {
	this.type = type;
}
public String getPackagename() {
	return packagename;
}
public void setPackagename(String packagename) {
	this.packagename = packagename;
}
private int id;
public String getAppName() {
	return appName;
}
public void setAppName(String appName) {
	this.appName = appName;
}
@Override
public String toString() {
	return "BannerAd [appName=" + appName + ", picUrl=" + picUrl
			+ ", clickUrl=" + clickUrl + ", id=" + id + "]";
}
public String getClickUrl() {
	return clickUrl;
}
public void setClickUrl(String clickUrl) {
	this.clickUrl = clickUrl;
}
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public String getPicUrl() {
	return picUrl;
}
public void setPicUrl(String picUrl) {
	this.picUrl = picUrl;
}
}
