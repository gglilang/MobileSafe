package com.lilang.mobilesafe;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.lilang.mobilesafe.utils.SmsUtils;

import java.io.IOException;

/**
 * Created by 朗 on 2015/3/29.
 */
public class AtoolsActivity extends Activity {
    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools);
    }

    /**
     * 点击事件，进入号码归属地查询页面
     * @param view
     */
    public void numberQuery(View view){
        Intent intent = new Intent(AtoolsActivity.this, NumberAddressQueryActivity.class);
        startActivity(intent);
    }

    /**
     * 点击事件，短信的备份
     * @param view
     */
    public void smsBackup(View view){

        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("正在备份短信");
        pd.show();

        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    SmsUtils.backupSms(AtoolsActivity.this, new SmsUtils.BackupCallBack() {
                        @Override
                        public void beforeBackup(int max) {
                            pd.setMax(max);
                        }

                        @Override
                        public void onSmsBackup(int progress) {
                            pd.setProgress(progress);
                        }
                    });
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AtoolsActivity.this, "备份成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AtoolsActivity.this, "备份失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }finally {
                    pd.dismiss();
                }
            }
        }.start();
    }

    /**
     * 点击事件，短信的还原
     */
    public void smsRestore(View view){

        Uri uri = Uri.parse("content://sms/");
        getContentResolver().delete(uri, null, null);
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    System.out.println("开始还原");
                    SmsUtils.restoreSms(AtoolsActivity.this);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AtoolsActivity.this, "短信还原成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
