package com.lilang.mobilesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

import com.lilang.mobilesafe.R;
import com.lilang.mobilesafe.service.GPSService;

public class SMSReceiver extends BroadcastReceiver {
    private static final String TAG = "SMSReceiver";
    private SharedPreferences sp;

    //设备策略服务
    private DevicePolicyManager dpm;

    public SMSReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        dpm = (DevicePolicyManager) context.getSystemService(context.DEVICE_POLICY_SERVICE);


        //写接收短信的代码
        Object[] objs = (Object[]) intent.getExtras().get("pdus");
        sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);

        for (Object b : objs) {
            //具体的某一条短信
            SmsMessage sms = SmsMessage.createFromPdu((byte[]) b);
            //发送者
            String sender = sms.getOriginatingAddress();
            String safenumber = sp.getString("safenumber", "");
            String body = sms.getMessageBody();
            System.out.println("xx"+safenumber +"======" + sender);

            if (sender.contains(safenumber)) {
                if ("#*location*#".equals(body)) {
                    //得到手机的GPS
                    Log.i(TAG, "得到手机的GPS");
                    //启动服务
                    Intent intent1 = new Intent(context, GPSService.class);
                    context.startService(intent1);
                    String lastLocation = sp.getString("lastLocation", null);
                    System.out.println(lastLocation);
                    if(TextUtils.isEmpty(lastLocation)){
                        //位置没有得到
                        SmsManager.getDefault().sendTextMessage(sender, null, "正在获取位置", null, null);
                    }else{
                        //得到位置
                        SmsManager.getDefault().sendTextMessage(sender, null, lastLocation, null, null);
                    }
                    abortBroadcast();
                } else if ("#*alarm*#".equals(body)) {
                    //播放报警音乐
                    Log.i(TAG, "播放报警音乐");
                    MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
                    player.setLooping(false);
                    player.setVolume(1.0f, 1.0f);
                    player.start();
                    abortBroadcast();
                } else if ("#*wipedata*#".equals(body)) {
                    //远程清除数据
                    Log.i(TAG, "远程清除数据");
                    dpm.wipeData(0);
                    abortBroadcast();
                } else if ("#*lockscreen*#".equals(body)) {
                    //远程锁屏
                    Log.i(TAG, "远程锁屏");
                    dpm.lockNow();
                    abortBroadcast();
                }
            }


        }
    }
}
