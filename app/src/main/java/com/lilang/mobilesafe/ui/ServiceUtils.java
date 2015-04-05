package com.lilang.mobilesafe.ui;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by 朗 on 2015/4/4.
 * 检验某个服务是否还活着
 */
public class ServiceUtils {

    /**
     *
     * @param context
     * @param serviceName   传进来的的名称
     * @return
     */
    public static boolean isServiceRunning(Context context, String serviceName){
        //检验服务是否还活着

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> infos =  am.getRunningServices(100);
        for(ActivityManager.RunningServiceInfo info : infos){
            String name = info.service.getClassName();
            if(name.equals(serviceName)){
                return true;
            }
        }
        return false;
    }
}
