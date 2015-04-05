package com.lilang.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lilang.mobilesafe.db.dao.NumberAddressQueryUtils;

/**
 * Created by 朗 on 2015/3/29.
 */
public class NumberAddressQueryActivity extends Activity {

    private static final String TAG = "NumberQueryActivity";
    private EditText et_phone;
    private TextView tv_result;
    private Vibrator vibrator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_address_query);
        et_phone = (EditText) findViewById(R.id.et_phone);
        tv_result = (TextView) findViewById(R.id.tv_result);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        et_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s != null && s.length() >= 3){
                    //查询数据库并显示结果
                    String address = NumberAddressQueryUtils.queryNumber(s.toString());
                    tv_result.setText(address);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * 查询号码归属地
     * @param view
     */
    public void numberAddressQuery(View view){
        String phone = et_phone.getText().toString().trim();
        if(TextUtils.isEmpty(phone)){
            Toast.makeText(this, "号码为空", Toast.LENGTH_SHORT).show();
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            vibrator.vibrate(1000);
            et_phone.startAnimation(shake);
            return;
        }else {
            //去数据库查询号码归属地
            //写一个工具类，去查询数据库
            String address = NumberAddressQueryUtils.queryNumber(phone);
            tv_result.setText(address);
            Log.i(TAG, "您要查询的电话号码=" + phone);
        }
    }
}
