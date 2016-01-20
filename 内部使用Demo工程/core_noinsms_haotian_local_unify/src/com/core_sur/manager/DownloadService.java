package com.core_sur.manager;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.core_sur.listener.DownloadListener;
import com.core_sur.tools.CheckLog;

  
public class DownloadService {  
    public static final String TAG = "tag";  
    private DBHelper dbHelper;  
    public int fileSize;  
    private int block;  
    private File savedFile;  
    private String path;  
    public boolean isPause;  
    private MultiThreadDownload[] threads;  
    private Map<Integer, Integer> downloadedLength = new ConcurrentHashMap<Integer, Integer>();
	private int downloading;
	private int completeSize;
  
    public DownloadService(String target, File destination, int thread_size, Context context) throws Exception {  
        dbHelper = new DBHelper(context);  
        this.threads = new MultiThreadDownload[thread_size];  
        this.path = target;  
        URL url = new URL(target);  
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
        conn.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows 2000)");
        conn.setConnectTimeout(5000);  
        conn.setRequestMethod("GET");  
        if (conn.getResponseCode() != 200) {  
            throw new RuntimeException("server no response!");  
        }
        fileSize = conn.getContentLength();  
        if (fileSize <= 0) {  
        	CheckLog.log(this.getClass().getName(),new Exception().getStackTrace()[new Exception().getStackTrace().length-1].toString(),"fileSize:"+fileSize);
            throw new RuntimeException("file is incorrect!");  
        }  
        String fileName = getFileName(conn);  
        if (!destination.exists()) destination.mkdirs();  
        // ����һ��ͬ���С���ļ�  
        this.savedFile = new File(destination, fileName);  
        RandomAccessFile doOut = new RandomAccessFile(savedFile, "rwd");  
        doOut.setLength(fileSize);  
        doOut.close();  
        conn.disconnect();  
        // ����ÿ���߳���Ҫ���ص���ݳ���  
        this.block = fileSize % thread_size == 0 ? fileSize / thread_size : fileSize / thread_size + 1;  
        // ��ѯ�Ѿ����صļ�¼  
        downloadedLength = this.getDownloadedLength(path);  
    }  
    private Map<Integer, Integer> getDownloadedLength(String path) {  
        SQLiteDatabase db = dbHelper.getReadableDatabase();  
        String sql = "SELECT threadId,downLength FROM fileDownloading WHERE downPath=?";  
        Cursor cursor = db.rawQuery(sql, new String[] { path });  
        Map<Integer, Integer> data = new HashMap<Integer, Integer>();  
        while (cursor.moveToNext()) {  
            data.put(cursor.getInt(0), cursor.getInt(1));  
        }  
        db.close();  
        return data;  
    }  
  
    public String getFileName(HttpURLConnection conn) {  
        String fileName = path.substring(path.lastIndexOf("/") + 1, path.length());  
        if (fileName == null || "".equals(fileName.trim())) {  
            String content_disposition = null;  
            for (Entry<String, List<String>> entry : conn.getHeaderFields().entrySet()) {  
                if ("content-disposition".equalsIgnoreCase(entry.getKey())) {  
                    content_disposition = entry.getValue().toString();  
                }  
            }  
            try {  
                Matcher matcher = Pattern.compile(".*filename=(.*)").matcher(content_disposition);  
                if (matcher.find()) fileName = matcher.group(1);  
            } catch (Exception e) { CheckLog.log(this.getClass().getName(),new Exception().getStackTrace()[new Exception().getStackTrace().length-1].toString(),e.getMessage());   
                fileName = UUID.randomUUID().toString() + ".tmp"; // Ĭ����  
            }  
        }  
        return fileName;  
    }  
    public void download(DownloadListener listener) throws Exception{  
    	completeSize=0;
    	for (int i = 0; i < threads.length; i++) {  
            threads[i] = new MultiThreadDownload(i, savedFile, block, path, downloadedLength.get(i), this);
            completeSize+=threads[i].currentDownloadSize;
        }  
    	CheckLog.log(this.getClass().getName(),new Exception().getStackTrace()[new Exception().getStackTrace().length-1].toString(),completeSize+":completeSize"+fileSize+":fileSize");
    	if(completeSize>=fileSize&&savedFile.exists()){
            if (listener != null){
             listener.onDownload(completeSize,path,fileSize);  
            }
            return;
    	}
for (int j = 0; j < threads.length; j++) {
	new Thread(threads[j]).start();  
	
}
        this.saveDownloading(threads);  
        while (!isFinish(threads)) {  
        	downloading = 1;
            Thread.sleep(900);  
            if (listener != null) listener.onDownload(getDownloadedSize(threads),path,fileSize);  
            this.updateDownloading(threads);  
        }
        downloading=0;
        if (!this.isPause){
        if(getDownloadedSize(threads)<=fileSize){
        	 this.deleteDownloading();
        	 throw new Exception("����");
        }else{
        	return;
        }
        } ; 
    } 
  public int getCompleteSize() {
		return completeSize;
	}
public DownloadService(int completeSize) {
		this.completeSize = completeSize;
	}
public boolean getDownloading(){
	  return downloading==1;
  }
    private void saveDownloading(MultiThreadDownload[] threads) {  
        SQLiteDatabase db = dbHelper.getWritableDatabase();  
        try {
            db.beginTransaction();
            for (MultiThreadDownload thread : threads) {  
                String sql = "INSERT INTO fileDownloading(downPath,threadId,downLength) values(?,?,?)";  
                db.execSQL(sql, new Object[] { path, thread.id, 0 });  
            }  
            db.setTransactionSuccessful();  
        } finally {  
            db.endTransaction();  
            db.close();  
        }  
    }  
  
    private void deleteDownloading() {  
        SQLiteDatabase db = dbHelper.getWritableDatabase();  
        String sql = "DELETE FROM fileDownloading WHERE downPath=?";  
        db.execSQL(sql, new Object[] { path });  
        db.close();  
    }  
  
    private void updateDownloading(MultiThreadDownload[] threads) {  
        SQLiteDatabase db = dbHelper.getWritableDatabase();  
        try {  
            db.beginTransaction();  
            for (MultiThreadDownload thread : threads) {  
                String sql = "UPDATE fileDownloading SET downLength=? WHERE threadId=? AND downPath=?";  
                db.execSQL(sql, new String[] { thread.currentDownloadSize + "", thread.id + "", path });  
            }  
            db.setTransactionSuccessful();  
        } finally {  
            db.endTransaction();  
            db.close();  
        }  
    }  
  
    private int getDownloadedSize(MultiThreadDownload[] threads) {  
        int sum = 0;  
        for (int len = threads.length, i = 0; i < len; i++) {  
            sum += threads[i].currentDownloadSize;  
        }  
        return sum;  
    }  
  
    private boolean isFinish(MultiThreadDownload[] threads) {  
        try {  
            for (int len = threads.length, i = 0; i < len; i++) {  
                if (!threads[i].finished) {  
                    return false;  
                }  
            }  
            return true;  
        } catch (Exception e) { CheckLog.log(this.getClass().getName(),new Exception().getStackTrace()[new Exception().getStackTrace().length-1].toString(),e.getMessage());   
            return false;  
        }  
    }  
}  