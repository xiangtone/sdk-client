package com.core_sur.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;

public class HttpClientUtils
{
	private final HttpClient client;

	public HttpClientUtils(int time)
	{
		// TODO Auto-generated constructor stub
		client = new DefaultHttpClient();
		client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, time);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, time);
	}

	public HttpClientUtils()
	{
		// TODO Auto-generated constructor stub
		client = new DefaultHttpClient();
	}

	public void setOverTimeOut(int time)
	{
		client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, time);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, time);
	}

	public byte[] getBytes(String url)
	{
		InputStream in = getInputStream(url);
		byte[] bs = StreamUtils.StreamtoBytes(in);
		return bs;
	}

	public InputStream getInputStream(String url)
	{
		InputStream in = getInputStream(url, null);
		return in;
	}

	public InputStream getInputStream(String url, boolean isGET)
	{
		if (isGET)
		{
			return getInputStreamIsGET(url);
		} else
		{
			return getInputStream(url);
		}
	}

	public InputStream getInputStreamIsGET(String url)
	{
		HttpGet get = new HttpGet(url);
		try
		{
			HttpResponse result = client.execute(get);
			if (result.getStatusLine().getStatusCode() == 200)
			{
				InputStream in = result.getEntity().getContent();
				return in;
			}
		} catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e)
		{
			// TODO Auto-generated catch block

			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally
		{
		}
		return null;
	}

	public InputStream getInputStreamByList(String url, LinkedList<BasicNameValuePair> params)
	{
		try
		{
			HttpPost post = new HttpPost(url);
			if (params != null)
			{
				post.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
			}
			HttpResponse result = client.execute(post);
			if (result.getStatusLine().getStatusCode() == 200)
			{
				InputStream in = result.getEntity().getContent();
				return in;
			}
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public InputStream getInputStream(String url, String param)
	{
		try
		{
			HttpPost post = new HttpPost(url);
			if (param != null)
			{
				LinkedList<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
				BasicNameValuePair basicNameValuePair = new BasicNameValuePair("request", param);
				params.add(basicNameValuePair);
				post.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
			}
			HttpResponse result = client.execute(post);
			if (result.getStatusLine().getStatusCode() == 200)
			{
				InputStream in = result.getEntity().getContent();
				return in;
			}
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String getString(String url)
	{
		return StreamUtils.StreamtoString(getInputStream(url));
	}

	public String getString(String url, String param)
	{
		return StreamUtils.StreamtoString(getInputStream(url, param));
	}

	public String getString(String url, LinkedList<BasicNameValuePair> params)
	{
		return StreamUtils.StreamtoString(getInputStreamByList(url, params));

	}
}
