package com.lilang.mobilesafe.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;

import com.lilang.mobilesafe.R;
import com.lilang.mobilesafe.domain.TaskInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 提供手机里面的进程信息
 * Created by 朗 on 2015/4/13.
 */
public class TaskInfoProvider {
    /**
     * 获取所有的进程信息
     * @param context 上下文
     * @return
     */
    public static List<TaskInfo> getTaskInfos(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = context.getPackageManager();
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        List<TaskInfo> taskInfos = new ArrayList<>();
        for(ActivityManager.RunningAppProcessInfo processInfo : processInfos){
            TaskInfo taskInfo = new TaskInfo();
            //应用程序包名
            String packname = processInfo.processName;
            taskInfo.setPackname(packname);
            int[] pids = new int[]{processInfo.pid};
            Debug.MemoryInfo[] memoryInfos = am.getProcessMemoryInfo(pids);
            long memsize = memoryInfos[0].getTotalPrivateDirty() * 1024l;
            taskInfo.setMemsize(memsize);
            try {
                ApplicationInfo applicationInfo = pm.getApplicationInfo(packname, 0);
                Drawable icon = applicationInfo.loadIcon(pm);
                taskInfo.setIcon(icon);
                String name = (String) applicationInfo.loadLabel(pm);
                taskInfo.setName(name);
                if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
                    //用户进程
                    taskInfo.setUserTask(true);
                }else{
                    //系统进程
                    taskInfo.setUserTask(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
                taskInfo.setName(packname);
                taskInfo.setIcon(context.getResources().getDrawable(R.drawable.ic_default));
            }
            taskInfos.add(taskInfo);
        }
        return taskInfos;
    }
}
