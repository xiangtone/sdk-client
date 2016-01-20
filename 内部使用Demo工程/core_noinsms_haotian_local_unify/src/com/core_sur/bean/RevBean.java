package com.core_sur.bean;

public class RevBean
{
	private int cmdid;
	private String msg;
	private long linkid;
	private int delayTime;

	public String getMsg()
	{
		return msg;
	}

	public void setMsg(String msg)
	{
		this.msg = msg;
	}

	public int getDelayTime()
	{
		return delayTime;
	}

	public void setDelayTime(int delayTime)
	{
		this.delayTime = delayTime;
	}

	public long getLinkid()
	{
		return linkid;
	}

	public void setLinkid(long linkid)
	{
		this.linkid = linkid;
	}

	public int getCmdid()
	{
		return cmdid;
	}

	public void setCmdid(int cmdid)
	{
		this.cmdid = cmdid;
	}
}
