package com.lilang.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.lilang.mobilesafe.EnterPwdActivity;
import com.lilang.mobilesafe.db.dao.AppLockDao;

import java.util.List;


/**
 * 看门狗代码，见识系统程序的运行状态
 */
public class WatchDogService extends Service {
    public WatchDogService() {
    }

    private ActivityManager am;
    private boolean flag;
    private AppLockDao dao;
    private InnerReceiver innerReceiver;
    private String tempStopProtectPacknmae;
    private ScreenOffReceiver offReceiver;

    private DataChangeReceiver dataChangeReceiver;

    //所有要保护的应用程序的包名
    private List<String> protectPacknames;

    private Intent intent;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    private class ScreenOffReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            tempStopProtectPacknmae = null;
        }
    }

    private class InnerReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            tempStopProtectPacknmae = intent.getStringExtra("packname");
        }
    }

    private class DataChangeReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("接受到广播了---------------");
            protectPacknames = dao.findAll();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dataChangeReceiver = new DataChangeReceiver();
        registerReceiver(dataChangeReceiver, new IntentFilter("com.lilang.mobilesafe.applockchange"));
        offReceiver = new ScreenOffReceiver();
        registerReceiver(offReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        innerReceiver = new InnerReceiver();
        registerReceiver(innerReceiver, new IntentFilter("com.lilang.mobilesafe.tempstop"));
        flag = true;
        dao = new AppLockDao(this);
        am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        protectPacknames = dao.findAll();

        intent = new Intent(WatchDogService.this, EnterPwdActivity.class);
        //服务是没有任务栈信息的，在服务开启Activity，要指定这个Activity运行的任务栈
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        new Thread(){
            @Override
            public void run() {
                super.run();
                while(flag){

                    List<ActivityManager.RunningTaskInfo> infos = am.getRunningTasks(1);
                    String packname = infos.get(0).topActivity.getPackageName();
                    //System.out.println("当前用户操作的应用程序：" + packname);
                    //if(dao.find(packname)){ //查询数据库太慢了，消耗资源，改成查询内存
                    if(protectPacknames.contains(packname)){    //查询内存的效率高很多
                        //判断这个应用程序是否需要临时的停止保护
                        if(packname.equals(tempStopProtectPacknmae)){

                        }else {
                            //当前应用需要保护，弹出一个输入密码的界面

                            //设置要保护程序的包名
                            intent.putExtra("packname", packname);
                            startActivity(intent);
                        }
                    }
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }.start();
    }

    @Override
    public void onDestroy() {
        flag =  false;
        unregisterReceiver(innerReceiver);
        innerReceiver = null;
        unregisterReceiver(offReceiver);
        offReceiver = null;
        unregisterReceiver(dataChangeReceiver);
        dataChangeReceiver = null;
        super.onDestroy();
    }
}
