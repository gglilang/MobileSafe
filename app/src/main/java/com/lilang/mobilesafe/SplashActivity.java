package com.lilang.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.lilang.mobilesafe.utils.StreamTools;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
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
    private TextView tv_update_info;

    private String description;
    //新版本的下载地址
    private String apkurl;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
        tv_splash_version.setText("版本号" + getVersionName());
        tv_update_info = (TextView) findViewById(R.id.tv_update_info);
        boolean update = sp.getBoolean("update", true);

        //安装快捷方式
        installShortCut();


        //拷贝数据库
        copyDB("address.db");
        copyDB("antivirus.db");

        if (update) {
            //检查升级
            checkUpdate();
        } else {
            //自动升级已经关闭
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //进入主页面
                    enterHome();
                }
            }, 2000);
        }

        AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
        aa.setDuration(500);
        findViewById(R.id.rl_root_splash).setAnimation(aa);
    }

    /**
     * 创建快捷方式图标
     */
    private void installShortCut() {
        boolean shortcut = sp.getBoolean("shortcut", false);
        if (shortcut) return;
        SharedPreferences.Editor editor = sp.edit();
        //发送广播意图，告诉桌面要创建快捷方式
        Intent intent = new Intent();
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        //快捷方式，要包含3个重要的信息 1.名称 2.图标， 2.干什么事情
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "手机卫士");
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        //桌面点击图标对应的意图
        Intent shortcutIntent = new Intent();
        shortcutIntent.setAction("android.intent.action.MAIN");
        shortcutIntent.addCategory("android.intent.category.LAUNCHER");
        shortcutIntent.setClassName(getPackageName(), "com.lilang.mobilesafe.SplashActivity");
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        sendBroadcast(intent);
        editor.putBoolean("shortcut", true);
        editor.commit();
    }

    /**
     * 吧address.db这个数据库拷贝到data/data/<包名>/files/address.db
     */
    private void copyDB(String filename) {
        //只要拷贝了一个次，就不需要再拷贝
        try {
            File file = new File(getFilesDir(), filename);
            if (file.exists() && file.length() > 0) {
                //已经拷贝过了，不需要拷贝
                Log.i(TAG, "不需要拷贝");

            } else {
                InputStream is = getAssets().open(filename);
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                is.close();
                fos.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_UPDATE_DIALOG:    //显示升级的对话框
                    Log.i(TAG, "显示升级的对话框");
                    showUpdateDialog();
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

    /**
     * 弹出升级对话框
     */
    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示升级");
        builder.setMessage(description);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                enterHome();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("立刻升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //升级代码
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    FinalHttp http = new FinalHttp();
                    //mnt/sdcard/update.apk
                    http.download(apkurl, Environment.getExternalStorageDirectory().getAbsolutePath() + "/mobilesafe2.0.apk",
                            new AjaxCallBack<File>() {
                                @Override
                                public void onFailure(Throwable t, int errorNo, String strMsg) {
                                    t.printStackTrace();
                                    Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_LONG).show();
                                    super.onFailure(t, errorNo, strMsg);
                                }

                                @Override
                                public void onLoading(long count, long current) {
                                    super.onLoading(count, current);
                                    tv_update_info.setVisibility(View.VISIBLE);
                                    //当前下载百分比
                                    int progress = (int) (current * 100 / count);
                                    tv_update_info.setText("下载进度：" + progress + "%");
                                }

                                @Override
                                public void onSuccess(File file) {
                                    super.onSuccess(file);
                                    installAPK(file);
                                }

                                /**
                                 * 安装APK
                                 * @param t
                                 */
                                private void installAPK(File t) {
                                    Intent intent = new Intent();
                                    intent.setAction("android.intent.action.VIEW");
                                    intent.addCategory("android.intent.category.DEFAULT");
                                    intent.setDataAndType(Uri.fromFile(t), "application/vnd.android.package-archive");

                                    startActivity(intent);
                                }
                            });
                } else {
                    Toast.makeText(getApplicationContext(), "没有sdcard，请安装再试试", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
        builder.setPositiveButton("下次再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                enterHome();
            }
        });
        builder.show();
    }

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
                } finally {
                    long endTime = System.currentTimeMillis();
                    //我们花了多少时间
                    long dTime = endTime - startTime;
                    //20000
                    if (dTime < 2000) {
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
