package com.lilang.mobilesafe;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lilang.mobilesafe.db.dao.AntiVirusDao;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.List;

/**
 * Created by 朗 on 2015/4/19.
 */
public class AntiVirusActivity extends Activity {
    private static final int SCANING = 0;
    private static final int FINISH = 2;
    private ImageView iv_scan;
    private ProgressBar progressBar;
    private PackageManager pm;
    private TextView tv_scan_status;
    private LinearLayout ll_container;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SCANING:
                    ScanInfo scanInfo = (ScanInfo) msg.obj;
                    tv_scan_status.setText("正在扫描：" + scanInfo.name);
                    TextView textView = new TextView(getApplicationContext());
                    if(scanInfo.isVirus){
                        textView.setText("发现病毒：" + scanInfo.name);
                        textView.setTextColor(Color.RED);
                    }else{
                        textView.setText("扫描安全：" + scanInfo.name);
                        textView.setTextColor(Color.BLUE);
                    }
                    ll_container.addView(textView, 0);
                    break;
                case FINISH:
                    tv_scan_status.setText("扫描完成");
                    iv_scan.clearAnimation();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anti_virus);
        ll_container = (LinearLayout) findViewById(R.id.ll_container);
        tv_scan_status = (TextView) findViewById(R.id.tv_scan_status);
        iv_scan = (ImageView) findViewById(R.id.iv_scan);
        RotateAnimation ra = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(1000);
        ra.setRepeatCount(Animation.INFINITE);
        iv_scan.startAnimation(ra);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        scanVirus();
    }

    /**
     * 扫描病毒
     */
    private void scanVirus() {
        pm = getPackageManager();
        tv_scan_status.setText("正在初始化杀毒引擎。。。");

        new Thread() {
            @Override
            public void run() {
                super.run();
                List<PackageInfo> infos = pm.getInstalledPackages(0);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                progressBar.setMax(infos.size());
                int progress = 0;
                for (PackageInfo info : infos) {
                    //apk文件的完整路径
                    String sourceDir = info.applicationInfo.sourceDir;
                    String md5 = getFileMd5(sourceDir);
                    ScanInfo scanInfo = new ScanInfo();
                    scanInfo.name = info.applicationInfo.loadLabel(pm).toString();
                    scanInfo.packname = info.packageName;
                    //查询MD5信息，是否在病毒数据库里面
                    if (AntiVirusDao.isVirus(md5)) {
                        //发现病毒
                        scanInfo.isVirus = true;
                    } else {
                        //扫描安全
                        scanInfo.isVirus = false;
                    }
                    Message msg = Message.obtain();
                    msg.obj = scanInfo;
                    msg.what = SCANING;
                    handler.sendMessage(msg);
                    progress++;
                    progressBar.setProgress(progress);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Message msg = Message.obtain();
                msg.what = FINISH;
                handler.sendMessage(msg);
            }
        }.start();
    }

    /**
     * 扫描信息的内部类
     */
    class ScanInfo{
        String packname;
        String name;
        boolean isVirus;
    }

    /**
     * 获取文件的MD5值
     *
     * @param path 文件的全路径名称
     * @return
     */
    private String getFileMd5(String path) {
        //获取一个文件的特征信息，签名信息
        File file = new File(path);
        //md5
        try {
            MessageDigest digest = MessageDigest.getInstance("md5");
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            }
            byte[] result = digest.digest();
            StringBuffer sb = new StringBuffer();
            for (byte b : result) {
                //与运算
                int number = b & 0xff;
                String str = Integer.toHexString(number);
                if (str.length() == 1) {
                    sb.append("0");
                }
                sb.append(str);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
