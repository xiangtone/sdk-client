package com.core_sur.tools;

 

public class DownloadInfo {
private int fileSize=0;
private int completeSize=0;
private String adKey;
private String filePath;
private String downurl;
public int getFileSize() {
	return fileSize;
}
public DownloadInfo(int fileSize, int completeSize, String adKey, String filePath,String downurl) {
	super();
	this.fileSize = fileSize;
	this.downurl=downurl;
	this.completeSize = completeSize;
	this.adKey = adKey;
	this.filePath = filePath;
}
@Override
public String toString() {
	return "DownloadInfo [fileSize=" + fileSize + ", completeSize="
			+ completeSize + ", adKey=" + adKey + ", filePath=" + filePath
			+ ", downurl=" + downurl + "]";
}
public void setFileSize(int fileSize) {
	this.fileSize = fileSize;
}
public int getCompleteSize() {
	return completeSize;
}
public void setCompleteSize(int completeSize) {
	this.completeSize = completeSize;
}
public String getDownurl() {
	return downurl;
}
public void setDownurl(String downurl) {
	this.downurl = downurl;
}
public String getAdKey() {
	return adKey;
}
public void setAdKey(String adKey) {
	this.adKey = adKey;
}
public String getFilePath() {
	return filePath;
}
public void setFilePath(String filePath) {
	this.filePath = filePath;
}
}
 