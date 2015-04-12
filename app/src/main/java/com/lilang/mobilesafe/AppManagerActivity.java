package com.lilang.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lilang.mobilesafe.domain.AppInfo;
import com.lilang.mobilesafe.engine.AppInfoProvider;
import com.lilang.mobilesafe.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 朗 on 2015/4/10.
 */
public class AppManagerActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "AppManagerActivity";
    private TextView tv_avail_rom;
    private TextView tv_avail_sd;
    private TextView tv_status;

    private ListView lv_app_manager;
    private LinearLayout ll_loading;

    /**
     * 所有应用程序包的信息
     */
    private List<AppInfo> appInfos;

    /**
     * 用户应用程序的集合
     */
    private List<AppInfo> userAppInfos;

    /**
     * 系统应用程序的集合
     */
    private List<AppInfo> systemAppInfos;

    /**
     * 弹出悬浮窗体
     */
    private PopupWindow popupWindow;

    //开启
    private LinearLayout ll_start;
    //卸载
    private LinearLayout ll_uninstall;
    //分享
    private LinearLayout ll_share;

    //被点击的条目
    AppInfo appInfo;

    AppManagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        tv_avail_rom = (TextView) findViewById(R.id.tv_avail_rom);
        tv_avail_sd = (TextView) findViewById(R.id.tv_avail_sd);
        tv_status = (TextView) findViewById(R.id.tv_status);
        long romSize = getAvailSpace(Environment.getExternalStorageDirectory().getAbsolutePath());
        long sdSize = getAvailSpace(Environment.getDataDirectory().getAbsolutePath());

        tv_avail_sd.setText("SD卡可用空间：" + Formatter.formatFileSize(this, sdSize));
        tv_avail_rom.setText("内存可用空间：" + Formatter.formatFileSize(this, romSize));

        lv_app_manager = (ListView) findViewById(R.id.lv_app_manager);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);

        ll_loading.setVisibility(View.VISIBLE);
        fillData();


        //给ListView注册一个滚动的监听器
        lv_app_manager.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            //滚动的时候调用的方法
            //firstVisibleItem 第一个可见条目在ListView集合里面的位置
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                dismissPopupWindow();
                if (userAppInfos != null && systemAppInfos != null) {
                    if (firstVisibleItem > userAppInfos.size()) {
                        tv_status.setText("系统应用程序：" + systemAppInfos.size() + "个");
                    } else {
                        tv_status.setText("用户应用程序：" + userAppInfos.size() + "个");
                    }
                }

            }
        });

        /**
         * 设置ListView的点击事件
         */
        lv_app_manager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {  //显示的是应用程序有多少个的小标签
                    return;
                } else if (position == (userAppInfos.size() + 1)) {
                    return;
                } else if (position <= userAppInfos.size()) {  //用户程序
                    int newPosition = position - 1;
                    appInfo = userAppInfos.get(newPosition);
                } else {  //系统程序
                    int newPosition = position - userAppInfos.size() - 1 - 1;
                    appInfo = systemAppInfos.get(newPosition);
                }
                dismissPopupWindow();


                View contentView = View.inflate(getApplicationContext(), R.layout.popup_app_item, null);

                ll_start = (LinearLayout) contentView.findViewById(R.id.ll_start);
                ll_uninstall = (LinearLayout) contentView.findViewById(R.id.ll_uninstall);
                ll_share = (LinearLayout) contentView.findViewById(R.id.ll_share);

                ll_start.setOnClickListener(AppManagerActivity.this);
                ll_uninstall.setOnClickListener(AppManagerActivity.this);
                ll_share.setOnClickListener(AppManagerActivity.this);

                popupWindow = new PopupWindow(contentView, -2, -2);

                //动画效果的播放必须要求窗体有背景颜色
                //透明颜色也是颜色
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                int[] location = new int[2];
                view.getLocationInWindow(location);
                int dp = 60;
                int px = DensityUtil.dip2px(getApplicationContext(), dp);
                popupWindow.showAtLocation(parent, Gravity.TOP | Gravity.LEFT, px, location[1]);
                ScaleAnimation sa = new ScaleAnimation(0.3f, 1.0f, 0.3f, 1.0f, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0.5f);
                sa.setDuration(1000);
                AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
                aa.setDuration(1000);
                AnimationSet set = new AnimationSet(false);
                set.addAnimation(sa);
                set.addAnimation(aa);
                contentView.startAnimation(set);
            }
        });

    }

    private void fillData() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                appInfos = AppInfoProvider.getAppInfo(AppManagerActivity.this);
                userAppInfos = new ArrayList<AppInfo>();
                systemAppInfos = new ArrayList<AppInfo>();
                //判断应用程序是否为用户应用程序，并加入到不同的集合中
                for (AppInfo appInfo : appInfos) {
                    if (appInfo.isUserApp()) {
                        userAppInfos.add(appInfo);
                    } else {
                        systemAppInfos.add(appInfo);
                    }
                }
                //加载ListView的数据适配器
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(adapter == null){
                            adapter = new AppManagerAdapter();
                            lv_app_manager.setAdapter(adapter);
                        }else{
                            adapter.notifyDataSetChanged();
                        }

                        ll_loading.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }.start();
    }

    private void dismissPopupWindow() {
        //把旧的弹出窗体关闭掉
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    @Override
    public void onClick(View v) {
        dismissPopupWindow();
        switch (v.getId()) {
            case R.id.ll_start:
                Log.i(TAG, "启动：" + appInfo.getName());
                startApplication();
                break;
            case R.id.ll_uninstall:
                if(appInfo.isUserApp()) {
                    Log.i(TAG, "卸载：" + appInfo.getName());
                    uninstallApplication();
                }else{
                    Toast.makeText(this, "系统应用只有获取root权限才可以卸载", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ll_share:
                Log.i(TAG, "分享：" + appInfo.getName());
                shareApplication();
                break;
        }
    }

    /**
     * 分享一个应用程序
     */
    private void shareApplication() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.SEND");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "推荐您使用一款软件，名称叫：" + appInfo.getName());
        startActivity(intent);
    }

    /**
     * 卸载一个应用程序
     */
    private void uninstallApplication() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setAction("android.intent.action.DELETE");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse("package:" + appInfo.getPackname()));
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //刷新界面
        fillData();
    }

    /**
     * 开启一个应用程序
     */
    private void startApplication() {
        //查询这个应用程序的入口Activity，把他开启起来
        PackageManager pm = getPackageManager();
//        Intent intent = new Intent();
//        intent.setAction("android.intent.action.MAIN");
//        intent.addCategory("android.intent.category.LAUNCHER");
//        //查询出来了所有的手机上具有启动能力的Activity
//        List<ResolveInfo> infos = pm.queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS);
        Intent intent = pm.getLaunchIntentForPackage(appInfo.getPackname());
        if(intent != null) {
            startActivity(intent);
        }else{
            Toast.makeText(getApplicationContext(), "不能启动该应用", Toast.LENGTH_SHORT).show();
        }
    }

    private class AppManagerAdapter extends BaseAdapter {

        //控制ListView有多少个条目
        @Override
        public int getCount() {
            return userAppInfos.size() + 1 + systemAppInfos.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AppInfo appInfo;

            if (position == 0) {  //显示的是应用程序有多少个的小标签
                TextView textView = new TextView(getApplicationContext());
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                textView.setText("用户应用程序：" + userAppInfos.size() + "个");
                return textView;
            } else if (position == (userAppInfos.size() + 1)) {
                TextView textView = new TextView(getApplicationContext());
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                textView.setText("系统应用程序：" + systemAppInfos.size() + "个");
                return textView;
            } else if (position <= userAppInfos.size()) {  //用户程序
                int newPosition = position - 1; //因为多了一个TextView的文本占用了位置
                appInfo = userAppInfos.get(newPosition);
            } else {  //系统程序
                int newPosition = position - 1 - userAppInfos.size() - 1;
                appInfo = systemAppInfos.get(newPosition);
            }
            View view;
            ViewHolder holder;

            if (convertView != null && convertView instanceof RelativeLayout) {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            } else {
                view = View.inflate(AppManagerActivity.this, R.layout.list_item_appinfo, null);
                holder = new ViewHolder();
                holder.icon = (ImageView) view.findViewById(R.id.iv_app_icon);
                holder.tv_name = (TextView) view.findViewById(R.id.tv_app_name);
                holder.tv_location = (TextView) view.findViewById(R.id.tv_app_location);
                view.setTag(holder);
            }

            holder.icon.setImageDrawable(appInfo.getIcon());
            holder.tv_name.setText(appInfo.getName());
            if (appInfo.isInRom()) {
                holder.tv_location.setText("手机内存");
            } else {
                holder.tv_location.setText("外部存储");
            }

            return view;
        }
    }

    static class ViewHolder {
        TextView tv_name;
        TextView tv_location;
        ImageView icon;
    }

    /**
     * 获取某个目录的可用空间
     *
     * @param path
     * @return
     */
    private long getAvailSpace(String path) {
        StatFs statFs = new StatFs(path);
        statFs.getBlockCount(); //获取分区的个数
        long size = statFs.getBlockSize();  //获取分区的大小
        long count = statFs.getAvailableBlocks();    //获取可用的区块的个数
        return size * count;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissPopupWindow();
    }
}
