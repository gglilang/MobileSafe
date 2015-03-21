package com.lilang.mobilesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lilang.mobilesafe.R;

/**
 * Created by 朗 on 2015/3/21.
 * 自定义的组合空间，它里面包含R.layout.setting_item.view里面的所有控件
 */
public class SettingItemView extends RelativeLayout {

    private CheckBox cb_status;
    private TextView tv_title;
    private TextView tv_desc;

    /**
     * 初始化布局文件
     * @param context
     */
    private void iniView(Context context) {
        //把一个布局文件---》view 并且加载到SettingItemView
        View.inflate(context, R.layout.setting_item_view, SettingItemView.this);
        cb_status = (CheckBox) this.findViewById(R.id.cb_status);
        tv_desc = (TextView) findViewById(R.id.tv_desc);
        tv_title = (TextView) findViewById(R.id.tv_item);
    }
    public SettingItemView(Context context) {
        super(context);
        iniView(context);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        iniView(context);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        iniView(context);
    }

    /**
     * 校验组合控件是否有选中
     */
    public boolean isChecked(){
        return cb_status.isChecked();
    }

    /**
     * 设置组合控件的状态
     */

    public void setChecked(boolean checked){
        cb_status.setChecked(checked);
    }

    /**
     * 设置组合控件的描述信息
     */

    public void setDesc(String text){
        tv_desc.setText(text);
    }
}
