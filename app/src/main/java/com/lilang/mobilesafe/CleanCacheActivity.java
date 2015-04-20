package com.lilang.mobilesafe;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.UserHandle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by 朗 on 2015/4/19.
 */
public class CleanCacheActivity extends Activity {
    private ProgressBar pb;
    private TextView tv_scan_status;
    private PackageManager pm;
    private LinearLayout ll_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean_cache);
        pb = (ProgressBar) findViewById(R.id.pb);
        tv_scan_status = (TextView) findViewById(R.id.tv_scan_status);
        ll_container = (LinearLayout) findViewById(R.id.ll_container);

        scanCache();
    }

    /**
     * 扫描手机里面所有应用程序的缓存信息
     */
    private void scanCache() {
        pm = getPackageManager();
        new Thread() {
            @Override
            public void run() {
                super.run();
                Method getPackageSizeInfoMetod = null;
                Method[] methods = PackageManager.class.getMethods();
                for (Method method : methods) {
                    if ("getPackageSizeInfo".equals(method.getName())) {
                        getPackageSizeInfoMetod = method;
                    }
                }
                List<PackageInfo> infos = pm.getInstalledPackages(0);
                pb.setMax(infos.size());
                int progress = 0;
                for (PackageInfo info : infos) {
                    try {
                        getPackageSizeInfoMetod.invoke(pm, info.packageName, new MyDataObserver());
                        Thread.sleep(50);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    progress++;
                    pb.setProgress(progress);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_scan_status.setText("扫描完成");
                    }
                });
            }
        }.start();

    }

    private class MyDataObserver extends IPackageStatsObserver.Stub {

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            final long cacheSize = pStats.cacheSize;
            final String packageName = pStats.packageName;
            try {
                final ApplicationInfo info = pm.getApplicationInfo(packageName, 0);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_scan_status.setText("正在扫描：" + info.loadLabel(pm));
                        if (cacheSize > 0) {
                            View view = View.inflate(getApplicationContext(), R.layout.list_item_cacheinfo, null);
                            TextView tv_name = (TextView) view.findViewById(R.id.tv_app_name);
                            TextView tv_cache_size = (TextView) view.findViewById(R.id.tv_cache_size);
                            tv_name.setText(info.loadLabel(pm));
                            tv_cache_size.setText("缓存大小：" + Formatter.formatFileSize(getApplicationContext(), cacheSize));
                            ll_container.addView(view, 0);
                        }
                    }
                });
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 清理手机的全部缓存
     * @param view
     */
    public void clearAll(View view){
        Method[] methods = PackageManager.class.getMethods();
        for(Method method : methods){
            if("freeStorageAndNotify".equals(method.getName())){
                try {
                    method.invoke(pm, Integer.MAX_VALUE, new MypackDataObserver());
                    ll_container.removeAllViews();
                    Toast.makeText(this, "全部清理完成", Toast.LENGTH_SHORT).show();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                return;
            }
        }
    }

    private class MypackDataObserver extends IPackageDataObserver.Stub{

        @Override
        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
            System.out.println(packageName + succeeded);
        }
    }
}
