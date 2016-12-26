package com.core_sur.tools;

import android.graphics.Bitmap;

public class PointInfo {
	private String packagename;
private boolean isPoint;
private String name;
private Bitmap icon;
public Bitmap getIcon() {
	return icon;
}
public void setIcon(Bitmap icon) {
	this.icon = icon;
}
public boolean isPoint() {
	return isPoint;
}
public void setPoint(boolean isPoint) {
	this.isPoint = isPoint;
}
private String adKey;
private String url;
public String getPackagename() {
	return packagename;
}
public void setPackagename(String packagename) {
	this.packagename = packagename;
}
public String getName() {
	return name;
}
@Override
public String toString() {
	return "PointInfo [packagename=" + packagename + ", name=" + name
			+ ", adKey=" + adKey + ", url=" + url + "]";
}
public void setName(String name) {
	
	this.name = name;
}
public String getUrl() {
	return url;
}
public void setUrl(String url) {
	this.url = url;
}
public String getAdKey() {
	return adKey;
}
public void setAdKey(String adKey) {
	this.adKey = adKey;
}
}
