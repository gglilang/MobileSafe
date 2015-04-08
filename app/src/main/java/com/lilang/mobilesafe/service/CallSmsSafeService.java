package com.lilang.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.lilang.mobilesafe.db.dao.BlackNumberDao;

import java.lang.reflect.Method;

public class CallSmsSafeService extends Service {
    private static final String TAG = "CallSmsSafeService";
    private InnerSmsReceiver receiver;
    private BlackNumberDao dao;
    private TelephonyManager tm;
    private MyListener listener;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class InnerSmsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "内部广播接收者，短信到来了");
            //检查发件人是否是黑名单号码，设置短信拦截，全部拦截
            Object[] objs = (Object[]) intent.getExtras().get("pdus");
            for (Object obj : objs) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
                //得到短信发件人
                String sender = smsMessage.getOriginatingAddress();
                String result = dao.findMode(sender);
                if ("2".equals(result) || "3".equals(result)) {
                    Log.i(TAG, "拦截短信");
                    abortBroadcast();
                }
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dao = new BlackNumberDao(this);
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener = new MyListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        receiver = new InnerSmsReceiver();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        receiver = null;
        tm.listen(listener, PhoneStateListener.LISTEN_NONE);
    }

    private class MyListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:   //铃响状态
                    String result = dao.findMode(incomingNumber);
                    if ("1".equals(result) || "3".equals(result)) {
                        //删除呼叫记录
                        //另外一个应用程序联系人的应用的私有数据库
                        //deleteCallLog(incomingNumber);
                        //观察呼叫记录数据库内容的变化
                        Uri uri = Uri.parse("content://call_log/calls");
                        getContentResolver().registerContentObserver(uri, true, new CallLogObserver(incomingNumber, new Handler()));
                        endCall();  //另外一个进程里面运行的远程服务的方法。方法调用后，呼叫记录可能还没有生成

                    }
                    break;
            }
        }
    }

    private class CallLogObserver extends ContentObserver {

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        String incomingNumber;
        public CallLogObserver(String incomingNumber, Handler handler) {
            super(handler);
            this.incomingNumber = incomingNumber;
        }

        @Override
        public void onChange(boolean selfChange) {
            Log.i(TAG, "数据库的内容发生变化了，产生呼叫记录");
            deleteCallLog(incomingNumber);
            getContentResolver().unregisterContentObserver(this);
            super.onChange(selfChange);
        }
    }

    /**
     * 利用内容提供者删除呼叫记录
     * @param incomingNumber
     */
    private void deleteCallLog(String incomingNumber) {
        ContentResolver resolver = getContentResolver();
        Uri uri = Uri.parse("content://call_log/calls");
        resolver.delete(uri,"number=?", new String[]{incomingNumber});
    }

    private void endCall() {
        try {
            Log.i(TAG, "开始挂断电话");
            Class clazz = CallSmsSafeService.class.getClassLoader().loadClass("android.os.ServiceManager");
            Method method = clazz.getDeclaredMethod("getService", String.class);
            IBinder ibinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
            ITelephony.Stub.asInterface(ibinder).endCall();
            Log.i(TAG, "已经挂断电话");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
