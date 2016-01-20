package com.epplus.bean;
public class Version {
	public String version="0";
	public int Status=0;
	public String getVersion(){
		return version;
	}
	public void setVersion(String version){
		this.version=version;
	}
	public Version() {
	}
	public int getStatus() {
		return Status;
	}
	public void setStatus(int status) {
		Status = status;
	}
	public String getUpdateSdkUrl() {
		return updateSdkUrl;
	}
	public void setUpdateSdkUrl(String updateSdkUrl) {
		this.updateSdkUrl = updateSdkUrl;
	}
	@Override
	public String toString() {
		return "Version [version=" + version + ", updateSdkUrl="
				+ updateSdkUrl + "]";
	}
	public String updateSdkUrl;
}