package com.lilang.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class BootCompleteReceiver extends BroadcastReceiver {
    public BootCompleteReceiver() {
    }

    private SharedPreferences sp;
    private TelephonyManager tm;
    @Override
    public void onReceive(Context context, Intent intent) {

        sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //读取之前保存的SIM卡信息
        String saveSim = sp.getString("sim", "");

        //读取当前的sim卡消息
        String realSim = tm.getSimSerialNumber() + "ldj";

        //比较是否一样
        if(saveSim.equals(realSim)){
            //sim卡没有变更
        }else{
            //sim卡已经变更，发一个短信给安全号码
            System.out.println("sim卡已经变更");
            Toast.makeText(context, "sim卡已经变更", Toast.LENGTH_LONG).show();
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("test", "lskfjlksfjlskfjlsk");
            editor.commit();
        }
    }
}
