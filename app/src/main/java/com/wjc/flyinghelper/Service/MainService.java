package com.wjc.flyinghelper.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;

import com.wjc.flyinghelper.Config.Config;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class MainService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Calendar calendar = Calendar.getInstance();
                        String hour = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY));
                        String minute = String.format("%02d", calendar.get(Calendar.MINUTE));

                        SharedPreferences sharedPreferences = getSharedPreferences(Config.name, Context.MODE_PRIVATE);

                        String amText = sharedPreferences.getString(Config.amTime, "");
                        String pmText = sharedPreferences.getString(Config.pmTime, "");
                        int switchText = sharedPreferences.getInt(Config.switchCompat, 0);

                        if (switchText == 1) {
                            if (hour.equals(amText) && minute.equals(pmText)) {
                                if (Build.VERSION.SDK_INT < 17) {
                                    boolean isEnabled = Settings.System.getInt(getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 1;
                                    Settings.System.putInt(getContentResolver(), Settings.System.AIRPLANE_MODE_ON, isEnabled ? 0 : 1);
                                    Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
                                    intent.putExtra("state", !isEnabled);
                                    sendBroadcast(intent);
                                } else {
                                    boolean isEnabled = Settings.System.getInt(getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) == 1;
                                    Settings.System.putInt(getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, isEnabled ? 0 : 1);
                                    Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
                                    intent.putExtra("state", !isEnabled);
                                    sendBroadcast(intent);
                                }

                            }
                        }
                    }
                }, 30);
            }
        });
        thread.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
