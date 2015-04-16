package com.lilang.mobilesafe.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

public class KillAllReceiver extends BroadcastReceiver {
    public KillAllReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infos = am.getRunningAppProcesses();
        for(ActivityManager.RunningAppProcessInfo info : infos){
            am.killBackgroundProcesses(info.processName);
        }
    }
}
