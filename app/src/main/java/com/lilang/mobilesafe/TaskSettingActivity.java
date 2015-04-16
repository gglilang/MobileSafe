package com.lilang.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Adapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.lilang.mobilesafe.service.AutoCleanService;
import com.lilang.mobilesafe.ui.ServiceUtils;

/**
 * Created by 朗 on 2015/4/14.
 */
public class TaskSettingActivity extends Activity {
    private CheckBox cb_show_system;
    private CheckBox cb_auto_clean;
    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_setting);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        cb_show_system = (CheckBox) findViewById(R.id.cb_show_system);
        cb_auto_clean = (CheckBox) findViewById(R.id.cb_auto_clean);
        cb_show_system.setChecked(sp.getBoolean("showsystem", false));
        cb_show_system.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("showsystem", isChecked);
                editor.commit();
            }
        });

        cb_auto_clean.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //锁屏的广播事件时一个特殊的广播事件，在清单文件配置广播接收者不会生效
                //只能在代码里面注册才会生效
                Intent intent = new Intent(getApplicationContext(), AutoCleanService.class);

                if(isChecked) {
                    startService(intent);
                }else{
                    stopService(intent);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        boolean running = ServiceUtils.isServiceRunning(this, "com.lilang.mobilesafe.service.AutoCleanService");
        cb_auto_clean.setChecked(running);
    }
}
