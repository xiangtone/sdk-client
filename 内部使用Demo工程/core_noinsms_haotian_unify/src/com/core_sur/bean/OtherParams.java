package com.core_sur.bean;

public class OtherParams {
	String channelKey;
	String user_orderid;

	public OtherParams() {
		// TODO Auto-generated constructor stub
	}

	public OtherParams(String channelKey, String user_orderid) {
		super();
		this.channelKey = channelKey;
		this.user_orderid = user_orderid;
	}

	public String getChannelKey() {
		return channelKey;
	}

	public void setChannelKey(String channelKey) {
		this.channelKey = channelKey;
	}

	public String getUser_orderid() {
		return user_orderid;
	}

	public void setUser_orderid(String user_orderid) {
		this.user_orderid = user_orderid;
	}
}
