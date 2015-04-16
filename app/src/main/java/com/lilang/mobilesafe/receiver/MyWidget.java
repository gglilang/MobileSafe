package com.lilang.mobilesafe.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.lilang.mobilesafe.service.UpdateWidgetService;


/**
 * Created by 朗 on 2015/4/16.
 */
public class MyWidget extends AppWidgetProvider {


    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Intent i = new Intent(context, UpdateWidgetService.class);
        context.startService(i);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Intent intent = new Intent(context, UpdateWidgetService.class);
        context.startService(intent);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Intent intent = new Intent(context, UpdateWidgetService.class);
        context.stopService(intent);
    }
}
