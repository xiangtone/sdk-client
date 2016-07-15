package com.core_sur.manager;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import com.core_sur.tools.CheckLog;

import android.util.Log;

public final class MultiThreadDownload implements Runnable {
	public int id;
	private BufferedRandomAccessFile savedFile;
	private String path;
	public int currentDownloadSize = 0;
	public boolean finished;
	private final DownloadService downloadService;
	public int start;
	private int end;

	public MultiThreadDownload(int id, File savedFile, int block, String path,
			Integer downlength, DownloadService downloadService)
			throws Exception {
		this.id = id;
		this.path = path;
		if (downlength != null)
			this.currentDownloadSize = downlength;
		this.savedFile = new BufferedRandomAccessFile(savedFile, "rwd",
				1024 * 2);
		this.downloadService = downloadService;
		start = id * block + currentDownloadSize;
		end = (id + 1) * block;
	}

	@Override
	public void run() {
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(path)
					.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestProperty("Range", "bytes=" + start + "-" + end); // ���û�ȡ��ݵķ�Χ
			conn.setRequestMethod("GET");
			conn.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 6.3; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0");
			conn.connect();
			String responseMessage = conn.getResponseMessage();
			CheckLog.log(this.getClass().getName(), Thread.currentThread()
					.getStackTrace()[1].getMethodName(), responseMessage + ":"
					+ conn.getResponseCode());
			InputStream in = conn.getInputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			savedFile.seek(start);
			while (!downloadService.isPause && (len = in.read(buffer)) != -1) {
				savedFile.write(buffer, 0, len);
				currentDownloadSize += len;
			}
			savedFile.close();
			in.close();
			conn.disconnect();
			if (!downloadService.isPause)
				Log.i(DownloadService.TAG, "Thread " + (this.id + 1)
						+ "finished");
			finished = true;
		} catch (Exception e) {
			CheckLog.log(this.getClass().getName(), Thread.currentThread()
					.getStackTrace()[1].getMethodName(), e.getMessage());
			e.printStackTrace();
		}
	}
}