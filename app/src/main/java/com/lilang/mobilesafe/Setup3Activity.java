package com.lilang.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lilang.mobilesafe.R;

/**
 * Created by 朗 on 2015/3/23.
 */
public class Setup3Activity extends BaseSetupActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);
    }

    @Override
    public void showNext() {
        Intent intent = new Intent(this, Setup4Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.activity_right_in,R.anim.activity_left_out);
    }

    @Override
    public void showPre() {
        Intent intent = new Intent(this, Setup2Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.activity_left_in,R.anim.activity_right_out);
    }

    /**
     * 选择联系人的点击事件
     * @param view
     */
    public void selectContact(View view){
        Intent intent = new Intent(this, SelectContactActivity.class);
        startActivityForResult(intent, 0);
    }
}
