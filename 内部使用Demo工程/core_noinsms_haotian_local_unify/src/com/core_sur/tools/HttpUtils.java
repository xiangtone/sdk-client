package com.core_sur.tools;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class HttpUtils {

	private final static String TAG = "HttpUtils";

	private final static int HTTP_OK = 200;

	static public String get(Context cntx, String uri) {
		try {
			DefaultHttpClient client = getDefaultHttpClient(cntx);
			if (client == null)
				return null;
			HttpResponse response = client.execute(new HttpGet(uri));
			if (response.getStatusLine().getStatusCode() == HTTP_OK) {
				String entity = EntityUtils.toString(response.getEntity());
				Log.i(TAG, entity);
				return entity;
			} else {
				Log.e(TAG, String.format("HttpStatus ERROR, code=%d", response
						.getStatusLine().getStatusCode()));
			}
		} catch (Exception e) { CheckLog.log( Thread.currentThread() .getStackTrace()[1].getClassName(),new Exception().getStackTrace()[new Exception().getStackTrace().length-1].toString(),e.getMessage()); 
			e.printStackTrace();
		}
		return null;
	}

	static public String postXML(Context cntx, String uri, String content) {
		return post(cntx, uri, content, "application/atom+xml");
	}

	static public String postJson(Context cntx, String uri, String content) {
		return post(cntx, uri, content, "application/json");
	}

	static public String postText(Context cntx, String uri, String content) {
		return post(cntx, uri, content, "text/xml");
	}

	static public String post(Context cntx, String uri, String content,
			String contentType) {
		try {
			DefaultHttpClient client = getDefaultHttpClient(cntx);
			if (client == null)
				return "";
			HttpPost post = new HttpPost(uri);
			StringEntity entity = new StringEntity(content, "UTF-8");
			entity.setContentType(contentType);
			post.setEntity(entity);
			HttpResponse response = client.execute(post);
			if (response.getStatusLine().getStatusCode() == HTTP_OK) {
				return EntityUtils.toString(response.getEntity());
			} else {
				Log.e(TAG, String.format("HttpStatus ERROR, code=%d", response
						.getStatusLine().getStatusCode()));
			}

		} catch (Exception e) { CheckLog.log( Thread.currentThread() .getStackTrace()[1].getClassName(),new Exception().getStackTrace()[new Exception().getStackTrace().length-1].toString(),e.getMessage()); 
			e.printStackTrace();
		}

		return null;
	}

	// --------------------------------------------------------------------------------------------
	private static DefaultHttpClient getDefaultHttpClient(Context cntx) {
		if (cntx == null)
			return null;
		DefaultHttpClient client = new DefaultHttpClient();
		HttpHost proxy = getProxy(cntx);
		if (proxy != null) {
			client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
					proxy);
		}
		return client;
	}
	
	private static HttpHost getProxy(Context context) {
		HttpHost proxy = null;
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni != null && ni.isAvailable()
				&& ni.getType() == ConnectivityManager.TYPE_MOBILE) {
			String proxyHost = android.net.Proxy.getDefaultHost();
			int proxyPort = android.net.Proxy.getDefaultPort();
			if (proxyHost != null) {
				proxy = new HttpHost(proxyHost, proxyPort);
				Log.i(TAG, "UsingProxy---->(" + proxyHost + ":" + proxyPort
						+ ")");
			}
		}
		return proxy;
	}

}
