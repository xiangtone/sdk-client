package com.core_sur.event.impl;

import com.core_sur.event.MessageContent;

public class PayCenterEvent extends MessageContent{
String  title="标题";
String  payPoint;
String  appName;
public String getTitle() {
	return title;
}
public void setTitle(String title) {
	this.title = title;
}
public String getPayPoint() {
	return payPoint;
}
public void setPayPoint(String payPoint) {
	this.payPoint = payPoint;
}
public String getAppName() {
	return appName;
}
public PayCenterEvent(String payPoint, String appName,
		String payNumber) {
	super();
	this.payPoint = payPoint;
	this.appName = appName;
	this.payNumber = payNumber;
}
public void setAppName(String appName) {
	this.appName = appName;
}
public String getPayNumber() {
	return payNumber;
}
public void setPayNumber(String payNumber) {
	this.payNumber = payNumber;
}
String  payNumber;
}
