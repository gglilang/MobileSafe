package com.lilang.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.lilang.mobilesafe.utils.StreamTools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SplashActivity extends Activity {

    private static final String TAG = "SplashActivity";
    private static final int SHOW_UPDATE_DIALOG = 0;
    private static final int ENTER_HOME = 1;
    private static final int URL_ERROR = 2;
    private static final int NETWORK_ERROR = 3;
    private static final int JSON_ERROR = 4;
    private TextView tv_splash_version;

    private String description;
    //新版本的下载地址
    private String apkurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
        tv_splash_version.setText("版本号" + getVersionName());

        //检查升级
        checkUpdate();
        AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
        aa.setDuration(500);
        findViewById(R.id.rl_root_splash).setAnimation(aa);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SHOW_UPDATE_DIALOG:    //显示升级的对话框
                    Log.i(TAG, "显示升级的对话框");
                    break;
                case ENTER_HOME:            //进入主页面
                    enterHome();
                    break;
                case URL_ERROR:             //URL错误
                    enterHome();
                    Toast.makeText(getApplicationContext(), "URL错误", Toast.LENGTH_SHORT).show();
                    break;
                case NETWORK_ERROR:         //网络异常
                    enterHome();
                    Toast.makeText(getApplicationContext(), "网络异常", Toast.LENGTH_SHORT).show();
                    break;
                case JSON_ERROR:            //JSON解析出租哦
                    enterHome();
                    Toast.makeText(SplashActivity.this, "JSON解析出错", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void enterHome() {

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        //关闭当前页面
        finish();
    }

    /**
     * 检查是否有新版本，如果有就升级
     */

    private void checkUpdate() {

        new Thread() {
            @Override
            public void run() {

                Message mes = Message.obtain();
                long startTime = System.currentTimeMillis();
                try {
                    //URLhttp://127.0.0.1:8080/updateinfo.html
                    URL url = new URL(getString(R.string.server));
                    //联网
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(4000);
                    int code = conn.getResponseCode();
                    if (code == 200) {
                        //联网成功
                        InputStream is = conn.getInputStream();
                        //把流转换成String
                        String result = StreamTools.readFromStream(is);
                        Log.i(TAG, "联网成功" + result);
                        //json解析
                        JSONObject obj = new JSONObject(result);
                        //得到服务器的版本信息
                        String version = (String) obj.get("version");
                        description = (String) obj.get("description");
                        apkurl = (String) obj.get("apkurl");

                        //检验是否有新版本
                        if (getVersionName().equals(version)) {
                            //版本一致，没有新版本，进入主页面
                            mes.what = ENTER_HOME;
                        } else {
                            //有新版本，弹出一升级对话框
                            mes.what = SHOW_UPDATE_DIALOG;
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    mes.what = URL_ERROR;
                } catch (IOException e) {
                    e.printStackTrace();
                    mes.what = NETWORK_ERROR;
                } catch (JSONException e) {
                    e.printStackTrace();
                    mes.what = JSON_ERROR;
                }finally {
                    long endTime = System.currentTimeMillis();
                    //我们花了多少时间
                    long dTime = endTime - startTime;
                    //20000
                    if(dTime < 2000){
                        try {
                            Thread.sleep(2000 - dTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    handler.sendMessage(mes);
                }
                super.run();
            }
        }.start();
    }

    /**
     * 得到应用程序的版本名称
     */
    private String getVersionName() {
        //用来管理手机的APK
        PackageManager pm = getPackageManager();
        //得到指定APK的功能清单文件
        try {
            PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

}
