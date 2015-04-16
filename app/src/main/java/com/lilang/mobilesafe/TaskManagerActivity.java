package com.lilang.mobilesafe;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lilang.mobilesafe.domain.TaskInfo;
import com.lilang.mobilesafe.engine.TaskInfoProvider;
import com.lilang.mobilesafe.utils.SystemInfoUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 朗 on 2015/4/12.
 */
public class TaskManagerActivity extends Activity{

    private TextView tv_process_count;
    private TextView tv_mem_info;
    private LinearLayout ll_loading;
    private ListView lv_task_manager;
    private TextView tv_status;

    private List<TaskInfo> allTaskInfos;
    private List<TaskInfo> userTaskInfos;
    private List<TaskInfo> systemTaskInfos;

    private TaskManagerAdapter adapter;

    int processCount;   //总进程数
    long availMem;  //剩余内存
    long totalMem;  //总内存
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        lv_task_manager = (ListView) findViewById(R.id.lv_task_manager);
        tv_process_count = (TextView) findViewById(R.id.tv_process_count);
        tv_mem_info = (TextView) findViewById(R.id.tv_mem_info);
        setTitle();

        fillData();

        tv_status = (TextView) findViewById(R.id.tv_status);
        lv_task_manager.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(userTaskInfos != null && systemTaskInfos != null){
                    if(firstVisibleItem > userTaskInfos.size()){
                        tv_status.setText("系统进程：" + systemTaskInfos.size() + "个");
                    }else{
                        tv_status.setText("用户进程：" + userTaskInfos.size() + "个");
                    }
                }

            }
        });

        lv_task_manager.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TaskInfo taskInfo;
                if(position == 0){  //用户进程的标签
                    return;
                }else if(position == userTaskInfos.size() + 1){ //系统进程标签
                   return;
                }else if(position <= userTaskInfos.size()){
                    taskInfo = userTaskInfos.get(position - 1);
                }else{
                    taskInfo = systemTaskInfos.get(position - 1 - userTaskInfos.size() - 1);
                }
                if(taskInfo.getPackname().equals(getPackageName())){
                    return;
                }
                ViewHolder holder = (ViewHolder) view.getTag();
                if(taskInfo.isChecked()){
                    taskInfo.setChecked(false);
                    holder.cb_status.setChecked(false);
                }else{
                    taskInfo.setChecked(true);
                    holder.cb_status.setChecked(true);
                }
            }
        });
    }

    private void setTitle() {
        processCount = SystemInfoUtils.getRunningProcessCount(this);
        tv_process_count.setText("运行中的进程：" + processCount + "个");
        availMem = SystemInfoUtils.getAvailMen(this);
        totalMem = SystemInfoUtils.getTotalMen(this);
        tv_mem_info.setText("剩余/总内存：" + Formatter.formatFileSize(this, availMem) + "/"
        + Formatter.formatFileSize(this, totalMem));
    }

    /**
     * 填充数据
     */
    private void fillData() {
        ll_loading.setVisibility(View.VISIBLE);
        new Thread(){
            @Override
            public void run() {
                super.run();
                allTaskInfos = TaskInfoProvider.getTaskInfos(getApplicationContext());
                userTaskInfos = new ArrayList<TaskInfo>();
                systemTaskInfos = new ArrayList<TaskInfo>();
                for(TaskInfo taskInfo : allTaskInfos){
                    if(taskInfo.isUserTask()){
                        userTaskInfos.add(taskInfo);
                    }else{
                        systemTaskInfos.add(taskInfo);
                    }
                }
                //更新设置页面
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ll_loading.setVisibility(View.INVISIBLE);
                        if(adapter == null) {
                            adapter = new TaskManagerAdapter();
                            lv_task_manager.setAdapter(adapter);
                        }else{
                            adapter.notifyDataSetChanged();
                        }
                        setTitle();
                    }
                });
            }
        }.start();
    }

    private class TaskManagerAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
            if(sp.getBoolean("showsystem", false)) {
                return userTaskInfos.size() + 1 + systemTaskInfos.size() + 1;
            }else{
                return userTaskInfos.size() + 1;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TaskInfo taskInfo;

            if(position == 0){  //用户进程的标签
                TextView textView = new TextView(getApplicationContext());
                textView.setText("用户进程：" + userTaskInfos.size() + "个");
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                return textView;
            }else if(position == userTaskInfos.size() + 1){ //系统进程标签
                TextView textView = new TextView(getApplicationContext());
                textView.setText("系统进程：" + systemTaskInfos.size() + "个");
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                return textView;
            }else if(position <= userTaskInfos.size()){
                taskInfo = userTaskInfos.get(position - 1);
            }else{
                taskInfo = systemTaskInfos.get(position - 1 - userTaskInfos.size() - 1);
            }
            View view;
            ViewHolder holder;
            if(convertView != null && convertView instanceof RelativeLayout){
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }else{
                view = View.inflate(getApplicationContext(), R.layout.list_item_taskinfo, null);
                holder = new ViewHolder();
                holder.iv_icon = (ImageView) view.findViewById(R.id.iv_task_icon);
                holder.tv_name = (TextView) view.findViewById(R.id.tv_task_name);
                holder.tv_memsize = (TextView) view.findViewById(R.id.tv_task_memsize);
                holder.cb_status = (CheckBox) view.findViewById(R.id.cb_status);
                view.setTag(holder);
            }
            holder.iv_icon.setImageDrawable(taskInfo.getIcon());
            holder.tv_name.setText(taskInfo.getName());
            holder.tv_memsize.setText("内存占用：" + Formatter.formatFileSize(getApplicationContext(), taskInfo.getMemsize()));
            holder.cb_status.setChecked(taskInfo.isChecked());
            if(taskInfo.getPackname().equals(getPackageName())){
                holder.cb_status.setVisibility(View.INVISIBLE);
            }else{
                holder.cb_status.setVisibility(View.VISIBLE);   //view对象被复用时，保持一致
            }
            return view;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }

    /**
     * 子孩子id的容器
     */
    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_memsize;
        CheckBox cb_status;
    }

    /**
     * 选中全部
     * @param view
     */
    public void selectAll(View view){
        for(TaskInfo taskInfo : allTaskInfos){

            if(taskInfo.getPackname().equals(getPackageName())){
                continue;
            }
            taskInfo.setChecked(true);
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 反向选中
     * @param view
     */
    public void selectOppo(View view){
        for(TaskInfo taskInfo : allTaskInfos){

            if(taskInfo.getPackname().equals(getPackageName())){
                continue;
            }
            taskInfo.setChecked(!taskInfo.isChecked());
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 杀死所有进程
     * @param view
     */
    public void killAll(View view){
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        int count = 0;
        long savedMem = 0;
        List<TaskInfo> killedTaskInfos = new ArrayList<>();
        for(TaskInfo taskInfo : allTaskInfos){
            if(taskInfo.isChecked()){   //被勾选的，杀死这个进程
                am.killBackgroundProcesses(taskInfo.getPackname());
                if(taskInfo.isUserTask()){
                    userTaskInfos.remove(taskInfo);
                }else{
                    systemTaskInfos.remove(taskInfo);
                }
                count++;
                savedMem += taskInfo.getMemsize();
                killedTaskInfos.add(taskInfo);
            }
        }
        allTaskInfos.removeAll(killedTaskInfos);
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "杀死了" + count + "个进程，释放了" + Formatter.formatFileSize(getApplicationContext(), savedMem)
        + "的内存", Toast.LENGTH_SHORT).show();
        processCount -= count;
        availMem += savedMem;
        tv_process_count.setText("运行中的进程：" + processCount + "个");
        tv_mem_info.setText("剩余/总内存：" + Formatter.formatFileSize(this, availMem) + "/"
                + Formatter.formatFileSize(this, totalMem));
    }

    /**
     * 进入设置
     * @param view
     */
    public void enterSetting(View view){
        Intent intent = new Intent(this, TaskSettingActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fillData();
    }
}
