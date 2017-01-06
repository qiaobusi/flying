package com.wjc.flyinghelper.util;


import android.content.Context;
import android.content.SharedPreferences;
import com.wjc.flyinghelper.config.Config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FlyingState {
    public static boolean getFlyingState(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Config.name, Context.MODE_PRIVATE);

        int switchCompat = sharedPreferences.getInt(Config.switchCompat, 0);
        if (switchCompat == 1) {
            String amTime = sharedPreferences.getString(Config.amTime, "");
            String pmTime = sharedPreferences.getString(Config.pmTime, "");

            String beginTimeString = "", endTimeString = "";
            long beginTime = 0, endTime = 0;
            boolean phoneState = false;

            String[] amTimeArray = amTime.split(":");
            String[] pmTimeArray = pmTime.split(":");

            int amTimeHour = Integer.valueOf(amTimeArray[0]);
            int amTimeMinute = Integer.valueOf(amTimeArray[1]);

            int pmTimeHour = Integer.valueOf(pmTimeArray[0]);
            int pmTimeMinute = Integer.valueOf(pmTimeArray[1]);

            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int second = 0;

            long nowTime = calendar.getTime().getTime();

            SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            if (amTimeHour > pmTimeHour) {
                beginTimeString = year + "-" + month + "-" + day + " " + amTimeHour + ":" + amTimeMinute + ":" + second;
                day = day + 1;
                endTimeString = year + "-" + month + "-" + day + " " + pmTimeHour + ":" + pmTimeMinute + ":" + second;

                phoneState = true;
            } else if (amTimeHour == pmTimeHour) {
                if (amTimeMinute > pmTimeMinute) {
                    beginTimeString = year + "-" + month + "-" + day + " " + amTimeHour + ":" + amTimeMinute + ":" + second;
                    day = day + 1;
                    endTimeString = year + "-" + month + "-" + day + " " + pmTimeHour + ":" + pmTimeMinute + ":" + second;

                    phoneState = true;
                } else if (amTimeMinute == pmTimeMinute) {

                } else {
                    beginTimeString = year + "-" + month + "-" + day + " " + amTimeHour + ":" + amTimeMinute + ":" + second;
                    endTimeString = year + "-" + month + "-" + day + " " + pmTimeHour + ":" + pmTimeMinute + ":" + second;

                    phoneState = true;
                }
            } else {
                beginTimeString = year + "-" + month + "-" + day + " " + amTimeHour + ":" + amTimeMinute + ":" + second;
                endTimeString = year + "-" + month + "-" + day + " " + pmTimeHour + ":" + pmTimeMinute + ":" + second;

                phoneState = true;
            }

            try {
                Date beginDate = format.parse(beginTimeString);
                beginTime = beginDate.getTime();

                Date endDate = format.parse(endTimeString);
                endTime = endDate.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (phoneState && nowTime >= beginTime && nowTime <= endTime) {
                return true;
            }
        }

        return false;
    }
}
