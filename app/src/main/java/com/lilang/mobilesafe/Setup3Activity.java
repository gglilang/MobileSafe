package com.lilang.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.lilang.mobilesafe.R;

/**
 * Created by 朗 on 2015/3/23.
 */
public class Setup3Activity extends BaseSetupActivity {
    private EditText et_setup3_phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);
        et_setup3_phone = (EditText) findViewById(R.id.et_setup3_phone);
        et_setup3_phone.setText(sp.getString("safenumber", ""));
    }

    @Override
    public void showNext() {
        String phone = et_setup3_phone.getText().toString().trim();
        if(TextUtils.isEmpty(phone)){
            Toast.makeText(this, "安全号码还没有设置", Toast.LENGTH_SHORT).show();
            return;
        }
        //应该保存一下安全号码
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("safenumber", phone);
        editor.commit();

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data == null)
            return;
        String phone = data.getStringExtra("phone").replace("-", "");
        et_setup3_phone.setText(phone);
    }
}
