package com.core_sur.notifierad;
/**
 * 广告对象
 * @author Administrator
 *
 */
public class AdBean  implements java.io.Serializable{
	
	private String title;
	private String apkUrl;
	private String info;
	
	private String icon;
	
	private String packageName;
	
	private long nextTime;
	
	//是否清除通知栏
	private String isClear;
	
	/***
	 * 游戏的id
	 */
	private int id;
	
	
	
	public AdBean() {
		// TODO Auto-generated constructor stub
	}
	
	
	

	public AdBean(String title, String apkUrl, String info) {
		super();
		this.title = title;
		this.apkUrl = apkUrl;
		this.info = info;
	}

	



	






	public int getId() {
		return id;
	}




	public void setId(int id) {
		this.id = id;
	}




	public String getIsClear() {
		return isClear;
	}




	public void setIsClear(String isClear) {
		this.isClear = isClear;
	}




	public long getNextTime() {
		return nextTime;
	}




	public void setNextTime(long nextTime) {
		this.nextTime = nextTime;
	}




	public String getPackageName() {
		return packageName;
	}




	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}




	public String getIcon() {
		return icon;
	}




	public void setIcon(String icon) {
		this.icon = icon;
	}




	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getApkUrl() {
		return apkUrl;
	}

	public void setApkUrl(String apkUrl) {
		this.apkUrl = apkUrl;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
	
	
	

}
