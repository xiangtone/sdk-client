package com.core_sur.bean;

public class RegResponse
{
	private int uid;// 为用户编号，后续接口，均需要此参数
	private String cryptkey;// 用户临时会话密钥
	private String feeurl;// 计费接口
	private String callbackurl;// 回调接口
	private int winwidth;// 窗体的宽
	private int winheight;// 窗体的高
	private int heartconn = 30 * 60 * 1000;// 心跳时间毫秒
	private String heartconnurl;
	private String cacheurl;// Cache地址
	private int feewindowmode;

	private String MainIp;// 用户临时会话密钥
	private String LinkIp;// 计费接口
	private String LinkDomain;// 回调接口
	
	//useless
	private int isSupportIB;// 是否支持网银
	

	public int getisSupportIB()
	{
		return isSupportIB;
	}

	public void setisSupportIB(int isSupportIB)
	{
		this.isSupportIB = isSupportIB;
	}
	
	public String getMainIp()
	{
		return MainIp;
	}

	public void setMainIp(String MainIp)
	{
		this.MainIp = MainIp;
	}

	
	public String getLinkIp()
	{
		return LinkIp;
	}

	public void setLinkIp(String LinkIp)
	{
		this.LinkIp = LinkIp;
	}

	
	public String getLinkDomain()
	{
		return LinkDomain;
	}

	public void setLinkDomain(String LinkDomain)
	{
		this.LinkDomain = LinkDomain;
	}

	
	
	public String getCacheUrl()
	{
		return cacheurl;
	}

	public void setCacheUrl(String cacheUrl)
	{
		this.cacheurl = cacheUrl;
	}

	public String getFeeurl()
	{
		return feeurl;
	}

	public void setFeeurl(String feeurl)
	{
		this.feeurl = feeurl;
	}

	public Integer getFeeWindowMode()
	{
		return feewindowmode;
	}

	public void setFeeWindowMode(Integer feewindowmode)
	{
		this.feewindowmode = feewindowmode;
	}

	public String getCallbackurl()
	{
		return callbackurl;
	}

	public void setCallbackurl(String callbackurl)
	{
		this.callbackurl = callbackurl;
	}

	public String getHeartconnurl()
	{
		return heartconnurl;
	}

	public void setHeartconnurl(String heartconnurl)
	{
		this.heartconnurl = heartconnurl;
	}

	public int getUid()
	{
		return uid;
	}

	public void setUid(int uid)
	{
		this.uid = uid;
	}

	public String getCryptkey()
	{
		return cryptkey;
	}

	public void setCryptkey(String cryptkey)
	{
		this.cryptkey = cryptkey;
	}

	public int getWinwidth()
	{
		return winwidth;
	}

	public void setWinwidth(int winwidth)
	{
		this.winwidth = winwidth;
	}

	public int getWinheight()
	{
		return winheight;
	}

	public void setWinheight(int winheight)
	{
		this.winheight = winheight;
	}

	public int getHeartconntime()
	{
		return heartconn;
	}

	public void setHeartconntime(int heartconntime)
	{
		this.heartconn = heartconntime;
	}

}
