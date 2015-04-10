package com.lilang.mobilesafe.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 短信的工具类
 * Created by 朗 on 2015/4/9.
 */
public class SmsUtils {


    /**
     * 备份短信的回调接口
     */
    public interface BackupCallBack{

        /**
         * 开始备份的时候，设置进度条的最大值
         * @param max 总进度
         */
        public void beforeBackup (int max);

        /**
         * 备份过程中，增加进度
         * @param progress 当前进度
         */
        public void onSmsBackup(int progress);
    }

    /**
     * 备份用户的短信
     * @param context 上下文
     * @param callBack 备份短信的接口
     */
    public static void backupSms(Context context, BackupCallBack callBack) throws IOException, InterruptedException {
        ContentResolver resolver = context.getContentResolver();
        File file = new File(Environment.getExternalStorageDirectory(), "backup.xml");
        FileOutputStream fos = new FileOutputStream(file);
        //把用户的短信一条一条读出来，按照一定的格式写到文件里
        XmlSerializer serializer = Xml.newSerializer();//获取xml文件的生成器（序列化器）
        //初始化生成器
        serializer.setOutput(fos, "utf-8");
        serializer.startDocument("utf-8", true);
        serializer.startTag(null, "smss");
        Uri uri = Uri.parse("content://sms/");
        Cursor cursor = resolver.query(uri, new String[]{"body", "address", "type", "date"}, null, null, null);
        //开始备份的时候设置进度条的最大值
        int max = cursor.getCount();
        serializer.attribute(null, "max", max + "");
        callBack.beforeBackup(max);
        //当前的进度值
        int process = 0;
        while(cursor.moveToNext()){
            Thread.sleep(500);
            String body = cursor.getString(0);
            String address = cursor.getString(1);
            String type = cursor.getString(2);
            String date = cursor.getString(3);
            serializer.startTag(null, "sms");

            serializer.startTag(null, "body");
            serializer.text(body);
            serializer.endTag(null, "body");

            serializer.startTag(null, "address");
            serializer.text(address);
            serializer.endTag(null, "address");

            serializer.startTag(null, "type");
            serializer.text(type);
            serializer.endTag(null, "type");

            serializer.startTag(null, "date");
            serializer.text(date);
            serializer.endTag(null, "date");

            serializer.endTag(null, "sms");
            //备份过程中，增加进度
            process++;
            callBack.onSmsBackup(process);
        }

        serializer.endTag(null, "smss");
        serializer.endDocument();
        fos.close();
    }

    /**
     * 还原短信
     * @param context
     */
    public static void restoreSms(Context context) throws Exception{
        //读取sd卡上的xml文件
        File file = new File(Environment.getExternalStorageDirectory(), "backup.xml");
        InputStream is = new FileInputStream(file);
        XmlPullParser pullParser = Xml.newPullParser();
        pullParser.setInput(is, "utf-8");
        int eventType = pullParser.getEventType();
        int max = 0;    //表示xml文件中短信的数量
        while(eventType != XmlPullParser.END_DOCUMENT){
            switch (eventType){
                //如果是开始标签
                case XmlPullParser.START_TAG:
                    if("smss".equals(pullParser.getName())){
                        max = Integer.parseInt(pullParser.getAttributeValue(null, "max"));
                        eventType = pullParser.next();
                        //解析每条短信
                        //每条短信都按照<sms><body></body<address></address><type></type><date></date></sms>的格式
                        int tags = 14;  //每条短信的所有tag和内容
                        String body = null;
                        String address = null;
                        String type = null;
                        String date = null;
                        for(int i = 0; i < max; i++){
                            for(int j = 0; j < tags; j++){
                                if(j == 2){
                                    body = pullParser.getText();
                                }else if(j == 5){
                                    address = pullParser.getText();
                                }else if(j == 8){
                                    type = pullParser.getText();
                                }if(j == 11){
                                    date = pullParser.getText();
                                }
                                eventType = pullParser.next();
                            }
                            //把短信插入到短信应用
                            Uri uri = Uri.parse("content://sms/");
                            ContentValues values = new ContentValues();
                            values.put("body", body);
                            values.put("address", address);
                            values.put("type", type);
                            values.put("date", date);
                            context.getContentResolver().insert(uri, values);

                        }
                    }
            }
            eventType = pullParser.next();
        }
        is.close();
    }
}
