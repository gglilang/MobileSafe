package com.lilang.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.lilang.mobilesafe.ui.SettingItemView;

/**
 * Created by 朗 on 2015/3/23.
 */
public class Setup2Activity extends BaseSetupActivity {
    SettingItemView siv_setup2_sim;
    /**
     * 读取手机sim的信息
     */
    private TelephonyManager tm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);
        siv_setup2_sim = (SettingItemView) findViewById(R.id.siv_setup2_sim);
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        sp = getSharedPreferences("config", MODE_PRIVATE);

        String simNumber = sp.getString("sim", null);
        if(TextUtils.isEmpty(simNumber)){
            //没有绑定
            siv_setup2_sim.setChecked(false);
        }else{
            //已经绑定
            siv_setup2_sim.setChecked(true);
        }
        siv_setup2_sim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sp.edit();
                if(siv_setup2_sim.isChecked()){
                    siv_setup2_sim.setChecked(false);
                    editor.putString("sim", null);
                }else{
                    siv_setup2_sim.setChecked(true);
                    //保存sim卡的序列号
                    String sim = tm.getSimSerialNumber();
                    editor.putString("sim", sim);
                }
                editor.commit();
            }
        });
    }

    @Override
    public void showNext() {
        //检查是否绑定sim卡
        String sim = sp.getString("sim", null);
        if(TextUtils.isEmpty(sim)){
            //没有绑定
            Toast.makeText(this, "sim卡没有绑定", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, Setup3Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.activity_right_in,R.anim.activity_left_out);
    }

    @Override
    public void showPre() {
        Intent intent = new Intent(this, Setup1Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.activity_left_in,R.anim.activity_right_out);
    }
}
