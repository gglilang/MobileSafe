package com.lilang.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

/**
 * Created by 朗 on 2015/3/23.
 */
public class Setup4Activity extends BaseSetupActivity {

    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);
        sp = getSharedPreferences("config", MODE_PRIVATE);
    }

    @Override
    public void showNext() {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("configed", true);
        editor.commit();
        Intent intent = new Intent(this, FindLostActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void showPre() {
        Intent intent = new Intent(this, Setup3Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.activity_left_in,R.anim.activity_right_out);
    }

}
