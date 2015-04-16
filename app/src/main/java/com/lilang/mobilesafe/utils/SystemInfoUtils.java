package com.lilang.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * 系统信息的工具类
 * Created by 朗 on 2015/4/12.
 */
public class SystemInfoUtils {
    /**
     * 获取正在运行中进程的数量
     * @param context 上下文
     * @return
     */
    public static int getRunningProcessCount(Context context){
        //PackageManager 包管理器 相当于程序管理器，静态的内容
        //ActivityManager 进程管理器，管理手机的活动信息, 动态的内容
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infos = am.getRunningAppProcesses();
        return infos.size();
    }

    /**
     * 获取手机可用的剩余内存
     * @param context 上下文
     * @return
     */
    public static long getAvailMen(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outinfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(outinfo);
        return outinfo.availMem;
    }

    /**
     * 获取手机可用的总内存
     * @param context 上下文
     * @return long byte
     */
    public static long getTotalMen(Context context){
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        ActivityManager.MemoryInfo outinfo = new ActivityManager.MemoryInfo();
//        am.getMemoryInfo(outinfo);
//        return outinfo.totalMem;
        File file = new File("/proc/meminfo");
        try {
            FileInputStream fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line = br.readLine();
            //MemTotal:            xxxx KB
            StringBuilder sb = new StringBuilder();
            for(char c : line.toCharArray()){
                if(c >= '0' && c <= '9'){
                    sb.append(c);
                }
            }
            return Long.parseLong(sb.toString()) * 1024 ;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
