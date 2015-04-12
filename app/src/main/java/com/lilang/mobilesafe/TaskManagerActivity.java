package com.lilang.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by 朗 on 2015/4/12.
 */
public class TaskManagerActivity extends Activity{

    private TextView tv_process_count;
    private TextView tv_mem_info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager);
        tv_process_count = (TextView) findViewById(R.id.tv_process_count);
        tv_mem_info = (TextView) findViewById(R.id.tv_mem_info);
    }
}
