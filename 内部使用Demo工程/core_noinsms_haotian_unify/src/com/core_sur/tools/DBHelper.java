package com.core_sur.tools;
 

import android.content.Context;
 
import android.database.sqlite.SQLiteDatabase;
 
import android.database.sqlite.SQLiteOpenHelper;
 

/**
 
* 建立一个数据库帮助类
 
*/
 
public class DBHelper extends SQLiteOpenHelper {
 
        // download.db-->数据库名
 
        public DBHelper(Context context) {
 
                super(context, "helper.db", null, 1);
 
        }
 

        /**
 
         * 在download.db数据库下创建一个download_info表存储下载信息
 
         */
 
        @Override
 
        public void onCreate(SQLiteDatabase db) {
                db.execSQL("create table download_info(_id integer PRIMARY KEY AUTOINCREMENT, adKey varchar(255), "
                                + "fileSize integer, complete integer,url varchar(255),localfile varchar(255))");
            	db.execSQL("create table pointtask(_id integer PRIMARY KEY AUTOINCREMENT, adKey varchar(255),packageName varchar(255),ispoint integer)");
        }
 

        @Override
 
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
 

        }
 

}
 