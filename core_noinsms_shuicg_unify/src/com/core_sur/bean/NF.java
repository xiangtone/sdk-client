package com.core_sur.bean;

import android.graphics.Bitmap;

public class NF {
String title;
Bitmap icon;
String content;
public String getTitle() {
	return title;
}
public NF(String title, Bitmap icon, String content) {
	super();
	this.title = title;
	this.icon = icon;
	this.content = content;
}
public void setTitle(String title) {
	this.title = title;
}
public Bitmap getIcon() {
	return icon;
}
public void setIcon(Bitmap icon) {
	this.icon = icon;
}
public String getContent() {
	return content;
}
public void setContent(String content) {
	this.content = content;
}
}
