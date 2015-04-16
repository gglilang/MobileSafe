package com.lilang.mobilesafe.service;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

import com.lilang.mobilesafe.R;
import com.lilang.mobilesafe.receiver.MyWidget;
import com.lilang.mobilesafe.utils.SystemInfoUtils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class UpdateWidgetService extends Service {
    private static final String TAG = "UpdateWidgetService";

    public UpdateWidgetService() {
    }

    private Timer timer;
    private TimerTask timerTask;
    private ScreenOffReceiver offReceiver;
    private ScreenOnReceiver onReceiver;
    AppWidgetManager awm;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        offReceiver = new ScreenOffReceiver();
        onReceiver = new ScreenOnReceiver();
        registerReceiver(offReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        registerReceiver(onReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
        awm = AppWidgetManager.getInstance(this);
        startTimer();
    }

    private void startTimer() {
        if(timer == null && timerTask == null) {
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    Log.i(TAG, "更新Widget");
                    //设置更新的组件
                    ComponentName provider = new ComponentName(UpdateWidgetService.this, MyWidget.class);
                    RemoteViews views = new RemoteViews(getPackageName(), R.layout.process_widget);
                    views.setTextViewText(R.id.process_count, "正在运行软件:"
                            + SystemInfoUtils.getRunningProcessCount(getApplicationContext()) + "个");
                    long size = SystemInfoUtils.getAvailMen(getApplicationContext());
                    views.setTextViewText(R.id.process_memory, "可用内存:" + Formatter.formatFileSize(getApplicationContext(), size));
                    //描述一个动作，这个动作是由另一个应用程序执行的
                    //自定义一个广播事件，杀死后台进程的事件
                    Intent intent = new Intent();
                    intent.setAction("com.lilang.mobilesafe.killAll");
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
                    awm.updateAppWidget(provider, views);

                }
            };
            timer.schedule(timerTask, 0, 3000);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(offReceiver);
        unregisterReceiver(onReceiver);
        onReceiver = null;
        onReceiver = null;
        stopTimer();
    }

    private void stopTimer() {
        if(timer != null && timerTask != null) {
            timer.cancel();
            timerTask.cancel();
            timer = null;
            timerTask = null;
        }
    }

    private class ScreenOffReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "屏幕锁屏了。。。");
            stopTimer();

        }
    }

    private class ScreenOnReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "屏幕解锁了。。。");
            startTimer();

        }
    }
}
