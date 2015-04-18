package com.lilang.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 朗 on 2015/4/5.
 */
public class AppLockDBOpenHelper extends SQLiteOpenHelper{
    /**
     * 数据库创建的构造方法 数据库名称 applock.db
     * @param context
     */
    public AppLockDBOpenHelper(Context context) {
        super(context, "applock.db", null, 1);
    }

    //初始化数据库的表的结构
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table applock (_id integer primary key autoincrement,packname varchar(20))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}