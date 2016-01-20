package com.core_sur.tools;
 

import java.io.File;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
/**
 
  *
 
  * 一个业务类
 
  */
public class DownDao {
     private DBHelper dbHelper;
     public DownDao(Context context) {
         dbHelper = new DBHelper(context);
     }
	public String selectFilePath(String adKey) {
		SQLiteDatabase x = dbHelper.getWritableDatabase();
		Cursor rawQuery = x.rawQuery("select * from download_info where adKey=?", new String[]{adKey});
		while (rawQuery.moveToNext()) {
			String filePath = rawQuery.getString(rawQuery.getColumnIndex("localfile"));	
			rawQuery.close();
			x.close();
			return 		filePath;
		}
		return null;
		
		// TODO Auto-generated method stub
	
	}
	public void closeDb() {
		dbHelper.close();		
	}
	public void removeDownloadInfoForAdKey(String adKey){
		 SQLiteDatabase x = dbHelper.getWritableDatabase();
		x.delete("download_info", "adKey=?", new String[]{adKey});
	}
	public DownloadInfo findDownloadInfo(String adKey) {
		SQLiteDatabase x = dbHelper.getWritableDatabase();
		Cursor rawQuery = x.rawQuery("select * from download_info where adKey=?", new String[]{adKey});
		while (rawQuery.moveToNext()) {
			int complete = rawQuery.getInt(rawQuery.getColumnIndex("complete"));	
			int fileSize = rawQuery.getInt(rawQuery.getColumnIndex("fileSize"));	
			String downUrl = rawQuery.getString(rawQuery.getColumnIndex("url"));	
			String filePath = rawQuery.getString(rawQuery.getColumnIndex("localfile"));	
			DownloadInfo downloadInfo = new DownloadInfo(fileSize, complete, adKey, filePath, downUrl);
			rawQuery.close();
			x.close();
			if(!new File(filePath).exists()){
				removeDownloadInfoForAdKey(adKey);
				return null;
			}
			return 	downloadInfo;
		}
		return null;
	}
	public void addDownloadInfo(DownloadInfo downloadInfo) {
		// TODO Auto-generated method stub
		SQLiteDatabase x = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("adKey", downloadInfo.getAdKey());
		values.put("localfile", downloadInfo.getFilePath());
		values.put("url", downloadInfo.getDownurl());
		values.put("fileSize", downloadInfo.getFileSize());
		values.put("complete", downloadInfo.getCompleteSize());
		x.insert("download_info", null, values);
		x.close();
	}
	public void updateComplete(String adKey, int complete) {
		// TODO Auto-generated method stub
		SQLiteDatabase x = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("complete", complete);
		x.update("download_info", values, "adKey=?", new String[]{adKey});
		x.close();
	}
	public static ArrayList<DownloadInfo> getDownloadInfos(Context c) {
		ArrayList<DownloadInfo> downloads = new ArrayList<DownloadInfo>();
		SQLiteDatabase x = new DBHelper(c).getWritableDatabase();
		Cursor rawQuery = x.rawQuery("select * from download_info ",new String[]{});
		while (rawQuery.moveToNext()) {
			int complete = rawQuery.getInt(rawQuery.getColumnIndex("complete"));	
			int fileSize = rawQuery.getInt(rawQuery.getColumnIndex("fileSize"));	
			String downUrl = rawQuery.getString(rawQuery.getColumnIndex("url"));	
			String filePath = rawQuery.getString(rawQuery.getColumnIndex("localfile"));	
			String adKey = rawQuery.getString(rawQuery.getColumnIndex("adKey"));	
			DownloadInfo downloadInfo = new DownloadInfo(fileSize, complete, adKey, filePath, downUrl);
			downloads.add(downloadInfo);
		}
		return downloads;
	}
	public void updateFilePath(String adKey,String filePath) {
		SQLiteDatabase x = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("localfile", filePath);
		x.update("download_info", values, "adKey=?", new String[]{adKey});
	}
 
 
}
 