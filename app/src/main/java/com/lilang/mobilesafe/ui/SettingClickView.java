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
public class SettingClickView extends RelativeLayout {

    private TextView tv_title;
    private TextView tv_desc;

    private String desc_on, desc_off;

    /**
     * 初始化布局文件
     * @param context
     */
    private void iniView(Context context) {
        //把一个布局文件---》view 并且加载到SettingItemView
        View.inflate(context, R.layout.setting_click_view, SettingClickView.this);
        tv_desc = (TextView) findViewById(R.id.tv_desc);
        tv_title = (TextView) findViewById(R.id.tv_title);
    }
    public SettingClickView(Context context) {
        super(context);
        iniView(context);
    }

    /**
     * 带有两个参数的构造方法，布局文件使用的时候调用
     * @param context
     * @param attrs
     */
    public SettingClickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        iniView(context);
        String title = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "title1");
        desc_on = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "desc_on");
        desc_off = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "desc_off");
        tv_title.setText(title);
        setDesc(desc_on);
    }

    public SettingClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        iniView(context);
    }



    /**
     * 设置组合控件的状态
     */

    public void setChecked(boolean checked){
        if(checked){
            setDesc(desc_on);
        }else {
            setDesc(desc_off);
        }
    }

    /**
     * 设置组合控件的描述信息
     */

    public void setDesc(String text){
        tv_desc.setText(text);
    }

    /**
     * 设置组合控件的标题
     */
    public void setTitle(String title){
        tv_title.setText(title);
    }
}
