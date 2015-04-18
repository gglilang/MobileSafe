package com.lilang.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by 朗 on 2015/4/17.
 */
public class EnterPwdActivity extends Activity {

    private EditText et_password;
    private TextView tv_name;
    private ImageView iv_icon;
    private String packname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_pwd);
        et_password = (EditText) findViewById(R.id.et_password);
        tv_name = (TextView) findViewById(R.id.tv_name);
        iv_icon = (ImageView) findViewById(R.id.iv_icon);
        Intent intent = getIntent();
        //当前要保护应用程序的包名
        packname = intent.getStringExtra("packname");

        //显示要保护的应用程序的信息
        PackageManager pm = getPackageManager();
        try {
            ApplicationInfo info = pm.getApplicationInfo(packname, 0);
            iv_icon.setImageDrawable(info.loadIcon(pm));
            tv_name.setText(info.loadLabel(pm));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void click(View view){
        String password = et_password.getText().toString().trim();
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        //假设正确的密码是123
        if("123".equals(password)){
            //告诉看门狗这个程序密码输入正确了，可以临时停止保护
            //自定义的广播
            Intent intent = new Intent();
            intent.setAction("com.lilang.mobilesafe.tempstop");
            intent.putExtra("packname", packname);
            sendBroadcast(intent);
            finish();
        }else{
            Toast.makeText(this, "密码错误！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        //回桌面
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.MONKEY");
        startActivity(intent);
        //所有的Activity最小化，不会执行ondestory，只执行onstop方法
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
