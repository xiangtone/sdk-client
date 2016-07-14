package com.epplus.bean;

import org.json.JSONException;
import org.json.JSONObject;

public class VersionData {

	private String version; // �汾��
	private String xmlName; // ����汾�ŵ�xml�ļ���(��Ҫ����İ�������һ��)
	private int status; // ״̬(�Ƿ���Ҫ����)
	private String updateSdkUrl; // �����°汾jar��������url
	private String coreClassName; // ��Ҫ����ĺ��İ������class�ļ���(��Ҫ����İ�������һ��)
	private String sectionDexPathName; // ����dex�ļ�·����һ����
	//private String interfaceFileName; // �����ļ���һ���ӿ��ļ�������
	private int initFlag; //��ͬ��jar���������ʼ����һ��  �̴� 0 Ӧ������1


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
