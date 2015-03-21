package com.lilang.mobilesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by 朗 on 2015/3/21.
 * 自定义一个TextView，一出生就有焦点
 */
public class FocusedTextView extends TextView {

    public FocusedTextView(Context context) {
        super(context);
    }

    public FocusedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FocusedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 当前并没有焦点，只是欺骗了Android系统
     * @return
     */
    @Override
    public boolean isFocused() {
        return true;
    }
}
