package com.epplus.bean;

import org.json.JSONException;
import org.json.JSONObject;

public class VersionData {

	private String version; // 版本号
	private String xmlName; // 保存版本号的xml文件名(需要与核心包的名称一致)
	private int status; // 状态(是否需要更新)
	private String updateSdkUrl; // 更新新版本jar包的下载url
	private String coreClassName; // 需要反射的核心包里面的class文件名(需要与核心包的名称一致)
	private String sectionDexPathName; // 保存dex文件路径的一部分
	//private String interfaceFileName; // 核心文件的一个接口文件的名称
	private int initFlag; //不同的jar包升级后初始化不一样  短代 0 应用外广告1


	public VersionData() {

	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getXmlName() {
		return xmlName;
	}

	public void setXmlName(String xmlName) {
		this.xmlName = xmlName;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getUpdateSdkUrl() {
		return updateSdkUrl;
	}

	public void setUpdateSdkUrl(String updateSdkUrl) {
		this.updateSdkUrl = updateSdkUrl;
	}

	public String getCoreClassName() {
		return coreClassName;
	}

	public void setCoreClassName(String coreClassName) {
		this.coreClassName = coreClassName;
	}

	public String getSectionDexPathName() {
		return sectionDexPathName;
	}

	public void setSectionDexPathName(String sectionDexPathName) {
		this.sectionDexPathName = sectionDexPathName;
	}

	/*public String getInterfaceFileName() {
		return interfaceFileName;
	}

	public void setInterfaceFileName(String interfaceFileName) {
		this.interfaceFileName = interfaceFileName;
	}*/
	
	public int getInitFlag() {
		return initFlag;
	}

	public void setInitFlag(int initFlag) {
		this.initFlag = initFlag;
	}

	public VersionData(JSONObject object) {
		try {

			this.version = object.getString("version");
			this.status = object.getInt("status");
			this.updateSdkUrl = object.getString("updateSdkUrl");
			this.xmlName = object.getString("xmlName");
			this.coreClassName = object.getString("coreClassName");
			this.sectionDexPathName = object.getString("sectionDexPathName");
			//this.interfaceFileName = object.getString("interfaceFileName");
			this.initFlag = object.getInt("initFlag");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
