package com.core_sur.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.util.Log;

/**
 * Http 封装类
 * 
 * 进行apn检查，http联网， post、get数据
 * 
 * @author Administrator
 * 
 */
public class Http
{
	private URL url;
	private DefaultHttpClient http = null;
	private HttpResponse response = null;

	private boolean post;// 是否POST
	private byte[] outData;// 上行的数据
	private String postContentType;// 数据类型
	private HashMap<String, String> header;// 头数据
	private int responseCode;// 返回码
	private int connectTimeout = 50 * 1000;// 连接超时时间
	private int readTimeout = 30 * 1000;// 读取数据超时时间
	private final int listenTimeout = 30 * 1000; // 监听网络切换广播超时时间
	private byte[] inData;// 得到的数据
	private boolean useWap;// 是否以WAP方式联网
	private boolean assignNetType = false; // 是否指定接入点类型
	private String apnName; // 使用的接入点名称
	private final Context context;
	private ConnectivityReceiver receiver;
	// private boolean isApnConnected;
	private final String TAG = "Http";
	private boolean isSaveOutData = true; // 是否保存联网得到的数据
	private static String KEY_COOKIE = "Cookie";

	public static final int NET_TYPE_WAP = 0; // 指定以WAP 方式联网
	public static final int NET_TYPE_NET = 1; // 指定以Net 方式联网
	public static final int NET_TYPE_UNASSIGN = -1; // 不指定联网方式，使用默认

	public static boolean isMobleData;// 是不是在使用完网络关闭数据网络
	public static boolean isWiFiActive;// 是不是在使用完网络关闭wifi

	/**
	 * 构造
	 */
	public Http(Context _context)
	{
		context = _context;
	}

	/**
	 * 构造
	 * 
	 * @param _url
	 *            URL地址
	 * @throws MalformedURLException
	 */
	public Http(Context _context, String _url) throws MalformedURLException
	{
		context = _context;
		if (_url != null)
			setUrl(_url);
	}

	/**
	 * 构造 post方式
	 * 
	 * @param _url
	 *            URL
	 * 
	 * @param params
	 *            参数
	 * @throws MalformedURLException
	 */
	public Http(Context _context, String _url, byte[] params) throws MalformedURLException
	{
		this(_context, _url, params, null);
	}

	/**
	 * 构造 post方式
	 * 
	 * @param _url
	 *            URL
	 * @param params
	 *            POST 参数
	 * @throws MalformedURLException
	 */
	public Http(Context _context, String _url, String params) throws MalformedURLException
	{
		this(_context, _url);
		if (params != null)
		{
			postData(params.getBytes(), null);
		}
	}

	/**
	 * 构造
	 * 
	 * @param _url
	 *            URL
	 * @param _data
	 *            上行数据
	 * @param _contentType
	 *            数据类型
	 * @throws MalformedURLException
	 */
	public Http(Context _context, String _url, byte[] _data, String _contentType) throws MalformedURLException
	{
		this(_context, _url);
		postData(_data, _contentType);
	}

	public static void CloseMobileData(Context aContext)
	{
		// if(!isMobleData){
		// Connect.setMobileDataEnabled(aContext, false);
		// }
		// if(isWiFiActive){
		// Connect.openWifi(aContext);
		// }
	}

	/**
	 * 连接网络
	 */
	public void connect()
	{

		// 判断网络是否可用
		if (Connect.isNetworkAvailable(context))
		{
			if (com.core_sur.Config.IsDebug)
			{
				Log.d(TAG, "the net is usable");
			}
			communication();
		} else
		{
			if (com.core_sur.Config.IsDebug)
			{
				Log.d(TAG, "the net is unusable");
			}
		}

	}

	/**
	 * 连接成功后通信
	 * */
	private void communication()
	{
		int MAX_RETRY_TIMES = 3;
		int retryTimes = 0;
		do
		{
		} while ((responseCode < 200 || responseCode >= 500) && (retryTimes++) < MAX_RETRY_TIMES);

	}

	public String getCookieS()
	{
		if (header != null)
		{
			return header.get(KEY_COOKIE);
		} else
		{
			return null;
		}
	}


	/**
	 * 读数据
	 * 
	 * @throws IOException
	 */
	public void read()
	{
		try
		{
			InputStream is = response.getEntity().getContent();// http.getInputStream();
			if (isSaveOutData)
			{
				inData = Connect.readFully(is);
			} else
			{
				inData = null;
				readNoSave(is);
			}
			is.close();
			is = null;
			// response.getEntity().consumeContent();
		} catch (Exception e)
		{
			if (com.core_sur.Config.IsDebug)
			{
				Log.d(TAG, e.getMessage());
			}
		}
	}

	public void read(int maxSize)
	{
		try
		{
			InputStream is = response.getEntity().getContent();// http.getInputStream();
			if (isSaveOutData)
			{
				inData = Connect.readPart(is, maxSize);
			} else
			{
				inData = null;
				readNoSave(is, maxSize);
			}
			is.close();
			is = null;
		} catch (Exception e)
		{
			if (com.core_sur.Config.IsDebug)
			{
				Log.d(TAG, "the net is unusable");
			}
		}

		// response.getEntity().consumeContent();
	}

	/**
	 * 只读取返回结果，不存储
	 * 
	 * @throws IOException
	 */
	private void readNoSave(InputStream is)
	{
		try
		{
			byte[] buffer = new byte[1024];
			while (is.read(buffer) != -1)
			{
			}
		} catch (Exception e)
		{
			if (com.core_sur.Config.IsDebug)
			{
				Log.d(TAG, "the net is unusable");
			}
		}

	}

	/**
	 * 只读取返回结果，不存储
	 * 
	 * @throws IOException
	 */
	private void readNoSave(InputStream is, int maxSize)
	{
		try
		{
			int len = -1;
			int totalLen = 0;
			byte[] buffer = new byte[1024];
			while ((len = is.read(buffer)) != -1)
			{
				totalLen += len;
				if (totalLen > maxSize)
					break;
			}
		} catch (Exception e)
		{
			if (com.core_sur.Config.IsDebug)
			{
				Log.d(TAG, "the net is unusable");
			}
		}

	}

	/**
	 * 设置URL
	 * 
	 * @param _url
	 * @throws MalformedURLException
	 */
	public void setUrl(String _url) throws MalformedURLException
	{
		url = new URL(_url);
	}

	/**
	 * 设置连接方式
	 * 
	 * @param _post
	 *            是否post
	 */
	public void setRequestMethod(boolean _post)
	{
		post = _post;
	}

	/**
	 * 上行数据
	 * 
	 * @param _data
	 *            数据
	 * @param _contentType
	 *            类型
	 */
	public void postData(byte[] _data, String _contentType)
	{
		if (_data != null)
		{// 若上行数据不为null
			outData = _data;
			if (_contentType == null || _contentType.trim().equals(""))
			{
				postContentType = "*/*";// "application/octet-stream";
			} else
			{
				postContentType = _contentType;
			}
			try
			{
				if (com.core_sur.Config.IsDebug)
				{
					Log.d(TAG, "postData:" + new String(outData, "utf-8"));
				}
			} catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
				if (com.core_sur.Config.IsDebug)
				{
					Log.e(TAG, "postData ERROR:" + e.toString());
				}
			}
			post = true;// 为POST方式
			addHeader("Content-Type", postContentType);// 设置contentType

		} else
		{
			post = false;// 为GET方式
		}
	}

	/**
	 * 添加头参数
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 */
	public void addHeader(String key, String value)
	{
		if (header == null)
			header = new HashMap<String, String>();
		header.put(key, value);
	}

	/**
	 * 返回头字符串
	 * 
	 * @return
	 */
	public String GetHeaders()
	{
		StringBuffer sb = new StringBuffer("");
		Header[] header = response.getAllHeaders();
		for (int i = 0; i < header.length; i++)
		{
			if (com.core_sur.Config.IsDebug)
			{
				Log.d(TAG, "HEADERS : " + header[i].getName() + ":" + header[i].getValue());
			}
			sb.append(header[i].getName()).append(":").append(header[i].getValue()).append("\r\n");
		}
		return sb.toString();
	}

	/**
	 * 关闭HTTP
	 */
	public void close()
	{
		if (http != null && http.getConnectionManager() != null)
		{
			http.getConnectionManager().shutdown();
			http = null;
		}
		if (header != null)
		{
			header.clear();
			header = null;
		}
		url = null;
		outData = null;
		postContentType = null;
		inData = null;
	}

	/**
	 * 得到网络连接超时时间
	 * 
	 * @return
	 */
	public int getConnectTimeout()
	{
		return connectTimeout;
	}

	/**
	 * 设置网络连接超时时间
	 * 
	 * @param connectTimeout
	 *            超时时间 单位:毫秒
	 */
	public void setConnectTimeout(int _connectTimeout)
	{
		connectTimeout = _connectTimeout;
	}

	/**
	 * 得到读取数据超时时间
	 * 
	 * @return
	 */
	public int getReadTimeout()
	{
		return readTimeout;
	}

	/**
	 * 设置读取数据超时时间
	 * 
	 * @param readTimeout
	 *            超时时间 单位:毫秒
	 */
	public void setReadTimeout(int _readTimeout)
	{
		readTimeout = _readTimeout;
	}

	/**
	 * 得到HTTP
	 * 
	 * @return
	 */
	public DefaultHttpClient getHttp()
	{
		return http;
	}

	/**
	 * 得到数据
	 * 
	 * @return
	 */
	public byte[] getInData()
	{
		return inData;
	}

	/**
	 * 得到返回码
	 * 
	 * @return
	 */
	public int getResponseCode()
	{
		return responseCode;
	}

	public void setNetType(int type)
	{
		if (type == NET_TYPE_WAP)
		{
			setUseWap(true);
		} else if (type == NET_TYPE_NET)
		{
			setUseWap(false);
		} else
		{
			setAssignNetType(false);
		}
	}

	/**
	 * 当前是否以wap方式联网
	 * 
	 * @return
	 */
	public boolean isUseWap()
	{
		return useWap;
	}

	/**
	 * 设置当前是否以wap方式联网
	 * 
	 * @param wap
	 */
	private void setUseWap(boolean wap)
	{
		this.useWap = wap;
		this.assignNetType = true;
	}

	public void setSaveOutData(boolean isSaveOutData)
	{
		this.isSaveOutData = isSaveOutData;
	}

	public void setAssignNetType(boolean assignNetType)
	{
		this.assignNetType = assignNetType;
	}

	/**
	 * 注册监听网络连接状况
	 */
	private void registerListen()
	{
		IntentFilter mFilter01 = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		receiver = new ConnectivityReceiver();
		if (com.core_sur.Config.IsDebug)
		{
			Log.d(TAG, "registerListen android.net.conn.CONNECTIVITY_CHANGE");
		}
		context.registerReceiver(receiver, mFilter01);
	}

	/**
	 * 取消监听网络连接状况
	 */
	private void unregisterListen()
	{
		context.unregisterReceiver(receiver);
		receiver = null;
	}

	/**
	 * 网络连接状况广播接收
	 * 
	 * 
	 */
	public class ConnectivityReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			try
			{
				if (com.core_sur.Config.IsDebug)
				{
					Log.d(TAG, " receive connect msg ");
				}
				Bundle bundle = intent.getExtras();
				NetworkInfo info = (NetworkInfo) bundle.get("networkInfo");
				if (com.core_sur.Config.IsDebug)
				{
					Log.d(TAG, "ConnectivityReceiver" + info.toString());
				}
				String netNameNet = apnName.substring(0, 2) + "net";
				String netNameWAP = apnName.substring(0, 2) + "wap";

				if (info.getState() == State.CONNECTED)
				{// 若连接成功，唤醒
					String extraInfo = info.getExtraInfo();
					if (com.core_sur.Config.IsDebug)
					{
						Log.d(TAG, " connected :" + extraInfo);
					}
					if (extraInfo != null && (((useWap && extraInfo.indexOf(netNameWAP) != -1)) || (!useWap && extraInfo.indexOf(netNameNet) != -1)))
					{
						// isApnConnected = true;
						if (com.core_sur.Config.IsDebug)
						{
							Log.d(TAG, "ConnectivityReceiver apnchanged ok " + info.toString());
						}
						try
						{
							synchronized (Http.this)
							{
								if (com.core_sur.Config.IsDebug)
								{
									Log.d(TAG, "notify connect waiting ");
								}
								Http.this.notifyAll();
							}
						} catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				}
			} catch (Exception e)
			{
				if (com.core_sur.Config.IsDebug)
				{
					Log.d(TAG, e.getMessage());
				}
			}
		}
	}

	/**
	 * 通过图片url返回图片Bitmap
	 * 
	 * @param url
	 * @return
	 */
	public static InputStream GetInputStreamFromURL(String path)
	{
		URL url = null;
		InputStream is = null;
		try
		{
			url = new URL(path);
		} catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		try
		{
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();// 利用HttpURLConnection对象,我们可以从网络中获取网页数据.
			conn.setDoInput(true);
			conn.connect();
			is = conn.getInputStream(); // 得到网络返回的输入流

		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return is;
	}

	// public static String getHtmlContents(String url)
	// {
	// String htmlContents = "";
	//
	// try
	// {
	// URL uri = new URL(url.trim());
	//
	// // 使用HttpURLConnection打开连接
	// HttpURLConnection urlConn = (HttpURLConnection) uri.openConnection();
	//
	// urlConn.setDoInput(true);
	// urlConn.setConnectTimeout(30000);
	// urlConn.setRequestMethod("GET");
	// urlConn.setRequestProperty("accept", "*/*");
	// urlConn.connect();
	// if (urlConn.getResponseCode() != 200)
	// {
	// if (com.wc.middleware.Config.IsDebug)
	// {
	// Log.d("HttpErro", url + "获取失败.");
	// }
	// return "";
	// }
	// try
	// {
	// // 得到读取的内容(流)
	// InputStreamReader in = new InputStreamReader(urlConn.getInputStream());
	// // 为输出创建BufferedReader
	// BufferedReader buffer = new BufferedReader(in);
	// String inputLine = null;
	// // 使用循环来读取获得的数据
	// while (((inputLine = buffer.readLine()) != null))
	// {
	// // 我们在每一行后面加上一个"\n"来换行
	// htmlContents += inputLine + "\n";
	// }
	// // 关闭InputStreamReader
	// in.close();
	// // 关闭http连接
	// urlConn.disconnect();
	//
	// } catch (IOException e)
	// {
	// // Log.d("调用网页1", e.getMessage());
	// }
	// } catch (Exception ee)
	// {
	// ee.printStackTrace();
	// if (com.wc.middleware.Config.IsDebug)
	// {
	// Log.d("HttpErro", ee.getMessage());
	// }
	// }
	//
	// return htmlContents;
	// }
}
