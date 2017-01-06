package com.wjc.flyinghelper.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wjc.flyinghelper.util.FlyingState;


public class SmsStateReceiver extends BroadcastReceiver {

    private static final String SMS_ACTION = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (FlyingState.getFlyingState(context)) {
            if (intent.getAction().equals(SMS_ACTION)) {
                abortBroadcast();
            }
        }
    }
}
