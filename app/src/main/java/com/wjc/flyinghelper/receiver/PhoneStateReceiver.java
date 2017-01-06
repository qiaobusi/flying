package com.wjc.flyinghelper.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.TelephonyManager;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;
import com.wjc.flyinghelper.util.FlyingState;

public class PhoneStateReceiver extends BroadcastReceiver {

    private AudioManager audioManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (FlyingState.getFlyingState(context)) {
            audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

            if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
                //拨打电话
            } else {
                String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                //String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

                if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    int nowMode = audioManager.getRingerMode();

                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

                    try {
                        ITelephony iTelephony = getITelephony(context);
                        iTelephony.endCall();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    audioManager.setRingerMode(nowMode);
                }
            }
        }

    }

    private ITelephony getITelephony(Context context) {
        ITelephony iTelephony = null;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Class<TelephonyManager> c = TelephonyManager.class;
        Method getITelephonyMethod = null;
        try {
            getITelephonyMethod = c.getDeclaredMethod("getITelephony", (Class[]) null); // 获取声明的方法
            getITelephonyMethod.setAccessible(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        try {
            iTelephony = (ITelephony) getITelephonyMethod.invoke(telephonyManager, (Object[]) null); // 获取实例
            return iTelephony;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return iTelephony;
    }



}
