package com.lilang.mobilesafe;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by 朗 on 2015/3/26.
 */
public abstract class BaseSetupActivity extends Activity {
    //1.定义手机识别器
    private GestureDetector detector;
    protected SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("config", MODE_PRIVATE);
//        2.实例化这个手机识别器
        detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){
            /**
             * 当我们的手指在上面滑动的时候回调
             * @param e1
             * @param e2
             * @param velocityX
             * @param velocityY
             * @return
             */
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if((e1.getRawX() - e2.getRawX()) > 200){
                    //显示下一个页面
                    showNext();
                }
                if((e2.getRawX() - e1.getRawX()) > 200){
                    //显示上一个页面
                    showPre();
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    public abstract void showNext();
    public abstract void showPre();

    public void next(View view){
        showNext();
    }

    public void pre(View view){
        showPre();
    }
}
