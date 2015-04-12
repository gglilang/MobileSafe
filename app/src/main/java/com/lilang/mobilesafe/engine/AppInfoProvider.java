package com.lilang.mobilesafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.lilang.mobilesafe.domain.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 业务方法，提供手机里面安装的所有的应用程序信息
 * Created by 朗 on 2015/4/10.
 */
public class AppInfoProvider {

    /**
     * 获取所有安装的应用程序的信息
     * @param context 上下文
     * @return
     */
    public static List<AppInfo> getAppInfo(Context context){
        PackageManager pm = context.getPackageManager();
        //所有的安装在系统上的应用程序包信息
        List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
        List<AppInfo> appInfos = new ArrayList<>();
        for(PackageInfo packageInfo : packageInfos){
            AppInfo appInfo = new AppInfo();
            //packInfo相当于一个应用程序apk包的清单文件
            String packname = packageInfo.packageName;
            Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
            String name = packageInfo.applicationInfo.loadLabel(pm).toString();
            int flags = packageInfo.applicationInfo.flags;  //应用程序信息的标记
            if((flags& ApplicationInfo.FLAG_SYSTEM) == 0){
                //用户程序
                appInfo.setUserApp(true);
            }else{
                //系统程序
                appInfo.setUserApp(false);
            }

            if((flags& ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0){
                //手机内存
                appInfo.setInRom(true);
            }else{
                //手机外存储设备
                appInfo.setInRom(false);
            }

            appInfo.setIcon(icon);
            appInfo.setName(name);
            appInfo.setPackname(packname);
            appInfos.add(appInfo);
        }
        return appInfos;
    }
}
