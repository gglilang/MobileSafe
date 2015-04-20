package com.lilang.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 病毒数据库查询业务类
 * Created by 朗 on 2015/4/19.
 */
public class AntiVirusDao {
    /**
     * 查询一个MD5是否在病毒数据库里面存在
     * @param md5
     * @return
     */
    public static boolean isVirus(String md5){
        boolean result = false;
        String path = "/data/data/com.lilang.mobilesafe/files/antivirus.db";
        //打开病毒数据库文件
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.rawQuery("select * from datable where md5=?", new String[]{md5});
        if(cursor.moveToNext()){
            result = true;
        }
        cursor.close();
        db.close();

        return result;
    }
}
