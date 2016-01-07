package com.core_sur;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import android.util.Log;

public class HttpCommon {
	/*
	 * 1级分割
	 */
	public static String SPLIT_STRING_LEVEL1 = "№Ⅰ";

	/*
	 * 2级分割
	 */
	public static String SPLIT_STRING_LEVEL2 = "№Ⅱ";

	/*
	 * 3级分割
	 */
	public static String SPLIT_STRING_LEVEL3 = "№Ⅲ";

	/**
	 * 抽取自己想要的内容, 返回多个group,group之间用cls_ClassLib.splitStringLevel1分割
	 * */
	public static ArrayList<String> GetContents(String HtmlContent,
			String regexString, final int groupNumber) {
		final ArrayList<String> aList = new ArrayList<String>();

		// if (IsLeft2Right)
		// {
		// regexString = "(?<=)" + regexString;
		// }

		final Pattern pattern = Pattern.compile(regexString);
		final Matcher matcher = pattern.matcher(HtmlContent);

		while (matcher.find()) {
			String strTemp = "";
			for (int i = 1; i < groupNumber + 1; i++) {
				strTemp += SPLIT_STRING_LEVEL1 + matcher.group(i);
			}

			aList.add(strTemp.substring(SPLIT_STRING_LEVEL1.length()));
		}
		return aList;
	}

	public static Long GetNowMillis() {
		final Calendar calendar = Calendar.getInstance();
		return calendar.getTimeInMillis();
	}

	public static String GetDomainFromUrl(String Url) {
		if (Url == null || Url.equals("")) {
			return "";
		}

		String Domain = "";
		Url = Url.toLowerCase();
		if (Url.startsWith("http://")) {
			Domain = Url.split("/")[2];
		} else {
			Domain = Url.split("/")[0];
		}
		return Domain;
	}

	// / <summary>
	// / 抓取网页的返回结构
	// / </summary>
	public static class HttpResult {
		// / <summary>
		// / Http状态
		// / </summary>
		public Integer StatusCode = 500;

		// / <summary>
		// / 内容
		// / </summary>
		public String HtmlContents = "";

		// / <summary>
		// / 用时
		// / </summary>
		public long UseTime = 0;

		// / <summary>
		// / 错误信息
		// / </summary>
		public String ErroMsg = "";

		@Override
		public String toString() {
			return "HttpResult [StatusCode=" + StatusCode + ", HtmlContents="
					+ HtmlContents + ", UseTime=" + UseTime + ", ErroMsg="
					+ ErroMsg + ", RealUrl=" + RealUrl + ", Cookie=" + Cookie
					+ "]";
		}

		// / <summary>
		// / 真实地址
		// / </summary>
		public String RealUrl = "";

		/*
		 * 接收Cookie
		 */
		public String Cookie = "";
	}

	public static HttpResult getHtmlContents(String url, String params,
			Boolean IsFakeUA) {
		return getHtmlContents(url, params, IsFakeUA, null);
	}

	public static HttpResult getHtmlContents(String url, String params,
			Boolean IsFakeUA, String Cookie) {
		if (url.startsWith("/")) {
			url = "http://" + Config.MainIp + url;
		}

		long BeginTime = GetNowMillis();

		HttpResult hr = new HttpResult();

		if (url == null || url.length() == 0) {
			return hr;
		}
		Boolean IsZip = false;

		String htmlContents = "";

		StringBuilder sb = new StringBuilder();

		String encodeString = "GBK";
		try {
			URL uri = new URL(url);

			// 使用HttpURLConnection打开连接
			HttpURLConnection urlConn = (HttpURLConnection) uri
					.openConnection();

			urlConn.setDoInput(true);
			urlConn.setConnectTimeout(10000);
			urlConn.setReadTimeout(20000);

			urlConn.setRequestMethod("GET");
			urlConn.setRequestProperty("accept", "*/*");

			if (Cookie != null && Cookie.length() > 5) {
				urlConn.setRequestProperty("Cookie", Cookie);
			}

			// if (SiteInfo.UserAgent == null || SiteInfo.UserAgent.length() ==
			// 0)
			// {
			// urlConn.setRequestProperty("User-Agent",
			// "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:28.0) Gecko/20100101 Firefox/28.0");
			// } else
			// {
			// urlConn.setRequestProperty("User-Agent", SiteInfo.UserAgent);
			// }

			// urlConn.setRequestProperty("User-Agent",
			// "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:28.0) Gecko/20100101 Firefox/28.0");

			urlConn.setRequestProperty("Accept-Encoding", "gzip, deflate");

			HttpURLConnection.setFollowRedirects(true);

			String Referer = "http://" + GetDomainFromUrl(url);

			urlConn.setRequestProperty("Referer", Referer);

			if (params != null && params.length() > 0) {
				urlConn.setRequestMethod("POST");
				urlConn.setDoOutput(true);
				byte[] bypes = params.toString().getBytes("utf-8");
				urlConn.getOutputStream().write(bypes);// 输入参数
			} else {
				urlConn.setRequestMethod("GET");
			}
			hr.StatusCode = urlConn.getResponseCode();
			if (hr.StatusCode != 200) {
				hr.UseTime = (GetNowMillis() - BeginTime) / 1000;

				return hr;
			}

			urlConn.connect();
			
			hr.StatusCode = urlConn.getResponseCode();
			
			urlConn.getHeaderField("Location");

			// 是否压缩urlConn.getContentEncoding();

			try {
				hr.Cookie = urlConn.getHeaderField("Cookie");

				String ContentType = urlConn.getHeaderField("Content-Type");
				if (ContentType != null && ContentType.length() > 0) {
					if (ContentType.toLowerCase().contains("utf-8")) {
						encodeString = "UTF-8";
					} else if (ContentType.toLowerCase().contains("gb2312")) {
						encodeString = "GB2312";
					} else if (ContentType.toLowerCase().contains("gbk")) {
						encodeString = "GBK";
					}
				}
				String ContentEncoding = urlConn
						.getHeaderField("Content-Encoding");

				if (ContentEncoding != null && ContentEncoding.equals("gzip")) {
					IsZip = true;
				} else {
					IsZip = false;
				}

				// 得到读取的内容(流)
				InputStream dis = new DataInputStream(urlConn.getInputStream());

				if (IsZip) {
					dis = new GZIPInputStream(dis);
				}

				ByteArrayOutputStream os = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int len;
				while ((len = dis.read(buffer)) > -1) {
					os.write(buffer, 0, len);
				}
				os.flush();

				dis.close();

				// BufferedReader reader = new BufferedReader(new
				// InputStreamReader(inputStream, "GBK"), 10 * 1024);

				BufferedReader in = new BufferedReader(new InputStreamReader(
						new ByteArrayInputStream(os.toByteArray()), "UTF-8"),
						10 * 1024);

				String inputLine = null;
				// 使用循环来读取获得的数据
				while (((inputLine = in.readLine()) != null)) {
					// 我们在每一行后面加上一个"\r\n"来换行
					sb.append(inputLine);
				}
				// 关闭InputStreamReader
				in.close();

				htmlContents = sb.toString().trim();

				sb = new StringBuilder();

				if (encodeString.equals("UTF-8")) {
					urlConn.disconnect();

					os.close();

					hr.HtmlContents = htmlContents;

					hr.UseTime = (GetNowMillis() - BeginTime) / 1000;

					return hr;
				}

				ArrayList<String> htmlEncodeString = GetContents(htmlContents,
						"<meta(.*?)>", 1);

				for (int i = 0; i < htmlEncodeString.size(); i++) {
					if (htmlEncodeString.get(i).toLowerCase().contains("utf-8")) {
						encodeString = "UTF-8";
						break;
					}
					if (htmlEncodeString.get(i).toLowerCase()
							.contains("gb2312")) {
						encodeString = "GB2312";
						break;
					}
					if (htmlEncodeString.get(i).toLowerCase().contains("gbk")) {
						encodeString = "GBK";
						break;
					}
				}

				if (encodeString.equals("UTF-8")) {
					// 关闭http连接
					urlConn.disconnect();

					os.close();

					hr.HtmlContents = htmlContents;
					hr.UseTime = (GetNowMillis() - BeginTime) / 1000;

					return hr;
				}

				if (encodeString.equals("")) {
					os.close();
					hr.HtmlContents = htmlContents;
					hr.UseTime = (GetNowMillis() - BeginTime) / 1000;
					return hr;
				} else {
					in = new BufferedReader(new InputStreamReader(
							new ByteArrayInputStream(os.toByteArray()),
							encodeString), 10 * 1024);
					inputLine = null;
					// 使用循环来读取获得的数据
					while (((inputLine = in.readLine()) != null)) {
						// 我们在每一行后面加上一个"\r\n"来换行
						sb.append(inputLine);
					}
					// 关闭InputStreamReader
					in.close();
					os.close();

					htmlContents = sb.toString().trim();

					hr.HtmlContents = htmlContents;
					hr.UseTime = (GetNowMillis() - BeginTime) / 1000;
					return hr;
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		hr.UseTime = (GetNowMillis() - BeginTime) / 1000;

		if (hr.HtmlContents != null) {
			hr.HtmlContents = hr.HtmlContents.trim();
		}
		return hr;
	}

	public static String post(String actionUrl, Map<String, String> params,
			Map<String, File> files, String MyCookie) throws IOException {
		String BOUNDARY = java.util.UUID.randomUUID().toString();
		String PREFIX = "--", LINEND = "\r\n";
		String MULTIPART_FROM_DATA = "multipart/form-data";
		String CHARSET = "UTF-8";

		URL uri = new URL(actionUrl);
		HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
		conn.setReadTimeout(30 * 1000); // 缓存的最长时间
		conn.setDoInput(true);// 允许输入
		conn.setDoOutput(true);// 允许输出
		conn.setUseCaches(false); // 不允许使用缓存
		conn.setRequestMethod("POST");
		conn.setRequestProperty("connection", "keep-alive");
		conn.setRequestProperty("Charsert", "UTF-8");
		conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
				+ ";boundary=" + BOUNDARY);

		if (MyCookie != null && MyCookie.length() > 5) {
			conn.setRequestProperty("Cookie", MyCookie);
		}

		// 首先组拼文本类型的参数
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			sb.append(PREFIX);
			sb.append(BOUNDARY);
			sb.append(LINEND);
			sb.append("Content-Disposition: form-data; name=\""
					+ entry.getKey() + "\"" + LINEND);
			sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
			// sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
			sb.append(LINEND);
			sb.append(entry.getValue());
			sb.append(LINEND);
		}

		DataOutputStream outStream = new DataOutputStream(
				conn.getOutputStream());
		outStream.write(sb.toString().getBytes());
		// 发送文件数据
		if (files != null) {
			int i = 0;
			for (Map.Entry<String, File> file : files.entrySet()) {
				StringBuilder sb1 = new StringBuilder();
				sb1.append(PREFIX);
				sb1.append(BOUNDARY);
				sb1.append(LINEND);
				sb1.append("Content-Disposition: form-data; name=\"file"
						+ (i++) + "\"; filename=\"" + file.getKey() + "\""
						+ LINEND);
				sb1.append("Content-Type: application/octet-stream; charset="
						+ CHARSET + LINEND);
				sb1.append(LINEND);
				outStream.write(sb1.toString().getBytes());

				InputStream is = new FileInputStream(file.getValue());
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					outStream.write(buffer, 0, len);
				}

				is.close();
				outStream.write(LINEND.getBytes());
			}
		}

		// 请求结束标志
		byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
		outStream.write(end_data);
		outStream.flush();

		// 得到响应码
		int res = conn.getResponseCode();
		// InputStream in = null;
		String htmlContents = "";
		if (res == 200) {
			try {
				InputStream stream = conn.getInputStream();
				byte[] data = new byte[102400];
				int length = stream.read(data);
				htmlContents = new String(data, 0, length);
			} catch (Exception e) {
				Log.v("httpErro", "Erro:" + e.getMessage());
			}

			conn.disconnect();

			// in = conn.getInputStream();
			// int ch;
			// StringBuilder sb2 = new StringBuilder();
			// while ((ch = in.read()) != -1)
			// {
			// sb2.append((char) ch);
			// }
		}
		// return in == null ? null : in.toString();

		return htmlContents.trim();
	}
}
