package com.lilang.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lilang.mobilesafe.db.BlackNumberDBOpenHelper;
import com.lilang.mobilesafe.domain.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 黑名单数据库的增删改查业务类
 * Created by 朗 on 2015/4/5.
 */
public class BlackNumberDao {

    private BlackNumberDBOpenHelper helper;

    /**
     * 构造方法
     *
     * @param context 上下文
     */
    public BlackNumberDao(Context context) {
        super();
        helper = new BlackNumberDBOpenHelper(context);
    }

    /**
     * 查询黑名单号码是否存在
     *
     * @param number
     * @return
     */
    public boolean find(String number) {
        boolean result = false;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from blacknumber where number=?", new String[]{number});
        if (cursor.moveToNext()) {
            result = true;
        }
        cursor.close();
        db.close();
        return result;
    }

    /**
     * 查询黑名单号码的拦截模式
     *
     * @param number
     * @return 返回号码的拦截模式，不是黑名单号码，返回空
     */
    public String findMode(String number) {
        String result = null;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select mode from blacknumber where number=?", new String[]{number});
        if (cursor.moveToNext()) {
            result = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return result;
    }

    /**
     * 查询全部黑名单号码
     *
     * @return
     */
    public List<BlackNumberInfo> findAll() {
        List<BlackNumberInfo> result = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select number,mode from blacknumber order by _id desc", null);
        while (cursor.moveToNext()) {
            BlackNumberInfo info = new BlackNumberInfo();
            info.setNumber(cursor.getString(0));
            info.setMode(cursor.getString(1));
            result.add(info);
        }
        cursor.close();
        db.close();
        return result;
    }

    /**
     * 查询部分黑名单号码
     *
     * @param offset    重哪个位置开始获取数据
     * @param maxNumber 一次最多获取多少记录
     * @return
     */
    public List<BlackNumberInfo> findPart(int offset, int maxNumber) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<BlackNumberInfo> result = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select number,mode from blacknumber order by _id desc limit ? offset ?", new String[]{String.valueOf(maxNumber), String.valueOf(offset)});
        while (cursor.moveToNext()) {
            BlackNumberInfo info = new BlackNumberInfo();
            info.setNumber(cursor.getString(0));
            info.setMode(cursor.getString(1));
            result.add(info);
        }
        cursor.close();
        db.close();
        return result;
    }

    /**
     * 添加黑名单号码
     *
     * @param number 黑名单号码
     * @param mode   拦截模式1.电话拦截 2.短信拦截 2.全部拦截
     */
    public void add(String number, String mode) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("number", number);
        values.put("mode", mode);
        db.insert("blacknumber", null, values);
        db.close();
    }

    /**
     * 修改黑名单号码的拦截模式
     *
     * @param number 要修改的黑名单号码
     * @param mode   新的拦截模式
     */
    public void update(String number, String mode) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("mode", mode);
        db.update("blacknumber", values, "number=?", new String[]{number});
        db.close();
    }

    /**
     * 删除黑名单号码
     *
     * @param number 要删除的黑名单号码
     */
    public void delete(String number) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("blacknumber", "number=?", new String[]{number});
        db.close();
    }
}
