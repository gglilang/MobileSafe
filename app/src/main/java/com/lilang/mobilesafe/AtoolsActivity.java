package com.lilang.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by 朗 on 2015/3/29.
 */
public class AtoolsActivity extends Activity {
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
}
