package com.core_sur.bean;

import android.graphics.Bitmap;

public class NFWEBS extends NF {
	public String webUrl;
	public String getWebUrl() {
		return webUrl;
	}
	public void setWebUrl(String webUrl) {
		this.webUrl = webUrl;
	}
	public NFWEBS(String title, Bitmap icon, String content, String webUrl) {
		super(title, icon, content);
		this.webUrl=webUrl;
		// TODO Auto-generated constructor stub
	}

}
