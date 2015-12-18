package com.core_sur.bean;

public class CallbackBean
{
	private int cmdid = 1003;
	private long linkid;
	private int status;// 0�ǳɹ� 1��ʧ��
	private String msg;

	public int getCmdid()
	{
		return cmdid;
	}

	public void setCmdid(int cmdid)
	{
		this.cmdid = cmdid;
	}

	public long getLinkid()
	{
		return linkid;
	}

	public void setLinkid(long linkid)
	{
		this.linkid = linkid;
	}

	public int getStatus()
	{
		return status;
	}

	public void setStatus(int status)
	{
		this.status = status;
	}

	public String getMsg()
	{
		return msg;
	}

	public void setMsg(String msg)
	{
		this.msg = msg;
	}
}
