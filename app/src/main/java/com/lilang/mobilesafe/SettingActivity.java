package com.lilang.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.lilang.mobilesafe.service.AddressService;
import com.lilang.mobilesafe.service.CallSmsSafeService;
import com.lilang.mobilesafe.service.WatchDogService;
import com.lilang.mobilesafe.ui.ServiceUtils;
import com.lilang.mobilesafe.ui.SettingClickView;
import com.lilang.mobilesafe.ui.SettingItemView;

/**
 * Created by 朗 on 2015/3/21.
 */
public class SettingActivity extends Activity {

    //设置是否开启自动更新
    private SettingItemView siv_update;
    private SharedPreferences sp;

    //设置是否开启显示归属地
    private SettingItemView siv_show_address;
    private Intent showAddress;

    //黑名单拦截设置
    private SettingItemView siv_callsms_safe;
    private Intent callSmsSafeIntent;

    //设置是否开启程序锁
    private SettingItemView siv_watch_dog;
    private Intent watchDogIntent;

    //设置归属地显示框背景
    private SettingClickView scv_changebg;

    @Override
    protected void onResume() {
        super.onResume();
        boolean isServiceRunning = ServiceUtils.isServiceRunning(SettingActivity.this, "com.lilang.mobilesafe.service.AddressService");
        if(isServiceRunning){
            //监听来电的服务是开启的
            siv_show_address.setChecked(true);
        }
        else{
            siv_show_address.setChecked(false);
        }

        //设置服务是否已经开启
        boolean isCallSmsServiceRunning = ServiceUtils.isServiceRunning(SettingActivity.this, "com.lilang.mobilesafe.service.CallSmsSafeService");
        siv_callsms_safe.setChecked(isCallSmsServiceRunning);

        boolean isWatchDogServiceRunning = ServiceUtils.isServiceRunning(SettingActivity.this, "com.lilang.mobilesafe.service.WatchDogService");
        siv_watch_dog.setChecked(isWatchDogServiceRunning);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        sp = getSharedPreferences("config", MODE_PRIVATE);

        //初始设置是否自动更新
        siv_update = (SettingItemView) findViewById(R.id.siv_update);

        boolean update = sp.getBoolean("update", false);
        if(update){
            //自动升级已经开启
            siv_update.setChecked(true);
        }else{
            //自动升级已经关闭
            siv_update.setChecked(false);
        }

        siv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor =sp.edit();
                //判断是否有选中
                // 已经打开自动升级
                if(siv_update.isChecked()){

                    siv_update.setChecked(false);
                    editor.putBoolean("update", false);
                }else {//没有打开自动升级

                    siv_update.setChecked(true);
                    editor.putBoolean("update", true);
                }
                editor.commit();
            }
        });

        //设置号码归属地显示控件
        siv_show_address = (SettingItemView) findViewById(R.id.siv_show_address);
        showAddress = new Intent(this, AddressService.class);
        boolean isServiceRunning = ServiceUtils.isServiceRunning(SettingActivity.this, "com.lilang.mobilesafe.service.AddressService");
        if(isServiceRunning){
            //监听来电的服务是开启的
            siv_show_address.setChecked(true);
        }
        else{
            siv_show_address.setChecked(false);
        }
        siv_show_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(siv_show_address.isChecked()){
                    //关闭显示号码归属地
                    siv_show_address.setChecked(false);
                    stopService(showAddress);
                }else{
                    //打开显示号码归属地
                    siv_show_address.setChecked(true);
                    startService(showAddress);
                }
            }
        });

        //黑名单拦截设置
        siv_callsms_safe = (SettingItemView) findViewById(R.id.siv_callsms_safe);
        callSmsSafeIntent = new Intent(this, CallSmsSafeService.class);
        siv_callsms_safe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(siv_callsms_safe.isChecked()){
                    //关闭显示号码归属地
                    siv_callsms_safe.setChecked(false);
                    stopService(callSmsSafeIntent);
                }else{
                    //打开显示号码归属地
                    siv_callsms_safe.setChecked(true);
                    startService(callSmsSafeIntent);
                }
            }
        });

        //设置号码归属地的背景
        scv_changebg = (SettingClickView) findViewById(R.id.scv_changebg);
        scv_changebg.setTitle("归属地提示框风格");
        final String[] items = {"半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿"};

        int which = sp.getInt("which", 0);
        scv_changebg.setDesc(items[which]);
        scv_changebg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int dd = sp.getInt("which", 0);
                //弹出一个对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setTitle("归属地提示框风格");
                builder.setSingleChoiceItems(items, dd, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //保存选择参数
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("which", which);
                        editor.commit();
                        scv_changebg.setDesc(items[which]);

                        //取消对话框
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
            }
        });


        //程序锁设置
        siv_watch_dog = (SettingItemView) findViewById(R.id.siv_watch_dog);
        watchDogIntent = new Intent(this, WatchDogService.class);
        siv_watch_dog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(siv_watch_dog.isChecked()){
                    //关闭看门狗
                    siv_watch_dog.setChecked(false);
                    stopService(watchDogIntent);
                }else{
                    //打开看门狗显示号码归属地
                    siv_watch_dog.setChecked(true);
                    startService(watchDogIntent);
                }
            }
        });

    }
}
