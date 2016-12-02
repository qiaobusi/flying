package com.wjc.flyinghelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import java.lang.reflect.Method;
import com.android.internal.telephony.ITelephony;

public class PhoneStatReceiver extends BroadcastReceiver {

    TelephonyManager telephonyManager = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        int state = telephonyManager.getCallState();

        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING :
                endCall();

                break;
            case TelephonyManager.CALL_STATE_OFFHOOK :
                break;
            case TelephonyManager.CALL_STATE_IDLE :
                break;

        }
    }

    private void endCall() {
        Class <TelephonyManager> c = TelephonyManager.class;
        Method getITelephonyMethod = null;
        try {
            getITelephonyMethod = c.getDeclaredMethod("getITelephony", (Class[])null);
            getITelephonyMethod.setAccessible(true);

            ITelephony iTelephony = (ITelephony) getITelephonyMethod.invoke(telephonyManager, (Object[]) null);
            iTelephony.endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
