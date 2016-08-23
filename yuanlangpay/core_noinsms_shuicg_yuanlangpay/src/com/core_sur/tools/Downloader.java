package com.core_sur.tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class Downloader {

	public static final Object PAUSE = null;
	public static final int STOP = 1;
	private File chacheDir;
	private Context context;
	private Handler handler;
	private int isdownloading;
	private DownDao dao;
	private int complete;
	private int fileSize;
	private DownloadInfo downloadInfo;
	private String filePath;
	private boolean allowDownload =true;
	public int status;
	private PointInfo pointInfo;
	public Downloader(PointInfo pointInfo, File chacheDir, Context context,
			Handler handler) {
		this.pointInfo=pointInfo;
		this.chacheDir = chacheDir;
		this.context = context;
		this.handler = handler;
		init();
	}
	private void init() {
		String adKey = pointInfo.getAdKey();
		String url = pointInfo.getUrl();
		dao = new DownDao(context);
		downloadInfo = dao.findDownloadInfo(adKey);
		fileSize=getUrlFileSize();
		if(fileSize==0){
			this.allowDownload=false;
			CheckLog.log(this.getClass().getName(),new Exception().getStackTrace()[new Exception().getStackTrace().length-1].toString(),"下载出错不进行下载");
			return;
		}
		if(downloadInfo==null){
			filePath = new File(chacheDir,UUID.randomUUID().toString()+".apk").getAbsolutePath();
          downloadInfo = new DownloadInfo(fileSize, 0, adKey,filePath ,url);
		dao.addDownloadInfo(downloadInfo);
		}else{
			filePath = downloadInfo.getFilePath();
			if(!new File(filePath).exists()){
				filePath = new File(chacheDir,UUID.randomUUID().toString()+".apk").getAbsolutePath();
				dao.updateFilePath(adKey,filePath);
			}
		}
	}
	public void downloadcancel(){
		stopDownload();
		
	}
	public void download(){
		String adKey = pointInfo.getAdKey();
		complete = downloadInfo.getCompleteSize();
		if(complete>=fileSize){
			CheckLog.log(this.getClass().getName(),new Exception().getStackTrace()[new Exception().getStackTrace().length-1].toString(),"已下载完毕不进行下载操作");
			sendMessage( pointInfo);
			this.allowDownload=false;
			return ;
		}		
		URL url;
		try {
			if(!allowDownload){
				return;
			}
			isdownloading=1;
			
			if(!new File(filePath).exists()){
				new File(filePath).createNewFile();
			}
			url = new URL( pointInfo.getUrl());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Range", "bytes=" + complete + "-"+fileSize);
			conn.connect();
	            //使用java中的RandomAccessFile 对文件进行随机读写操作
	            RandomAccessFile fos = new RandomAccessFile(filePath, "rw");
	            InputStream is = conn.getInputStream();
	            int len=0;
	            byte[] b = new byte[1024*8];
	            fos.seek(complete);
	            while ((len=is.read(b))!=-1) {
	        if(status==STOP){
	        	isdownloading=0;
	        	return;
	        }
	            	complete+=len;
	            fos.write(b, 0, len);
	       dao.updateComplete(adKey, complete);
	       sendMessage( pointInfo);
	            }
	        	isdownloading=0;
	        	fos.close();
	        	
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public int getUrlFileSize(){ 
		try {
			URL url = new URL( pointInfo.getUrl());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.connect();
			int contentLength = conn.getContentLength();
			return contentLength;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	public void stopDownload(){
		status=STOP;
	}

	public void sendMessage(PointInfo pointInfo){
	Message message = Message.obtain();
	message.what=1;
	//message.obj=pointInfo;
	message.obj=downloadInfo;
	message.arg1=complete;
	message.arg2=fileSize;
	handler.sendMessage(message);
	}
	public boolean isdownloading() {
		return isdownloading == 1;
	}
	public static ArrayList<DownloadInfo> geDownloadInfos(Context c){
		return DownDao.getDownloadInfos(c);
		
	}
	public DownloadInfo getDownloaderInfo() {
		// TODO Auto-generated method stub
		return downloadInfo;
	}

}