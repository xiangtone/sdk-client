package com.epplus.bean;

public class PackageData {
	private String jarName; // jar�������Q
	private String jarVer; // jar���İ汾
	
	public PackageData(){
		
	}
	
	public PackageData(String jarName, String jarVer) {
		this.jarName = jarName;
		this.jarVer = jarVer;
	}

	public String getJarName() {
		return jarName;
	}

	public void setJarName(String jarName) {
		this.jarName = jarName;
	}

	public String getJarVer() {
		return jarVer;
	}

	public void setJarVer(String jarVer) {
		this.jarVer = jarVer;
	}

}
