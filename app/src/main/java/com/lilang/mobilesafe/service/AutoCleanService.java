package com.lilang.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.lilang.mobilesafe.domain.TaskInfo;
import com.lilang.mobilesafe.engine.TaskInfoProvider;

import java.util.List;

public class AutoCleanService extends Service {
    public AutoCleanService() {
    }

    private ScreenOffReceiver receiver;
    private ActivityManager am;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        receiver = new ScreenOffReceiver();
        registerReceiver(receiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private class ScreenOffReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //清理进程
            am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> infos = am.getRunningAppProcesses();
            for(ActivityManager.RunningAppProcessInfo info : infos){
                am.killBackgroundProcesses(info.processName);
            }
        }
    }
}
