package com.wjc.flyinghelper.util;

import com.wjc.flyinghelper.config.Config;

import java.util.ArrayList;

public class WheelData {
    public static int getWheelDataIndex(String type, String amTimeWheel, String pmTimeWheel, ArrayList<String> hourList, ArrayList<String> minuteList) {
        int index = 0;

        if (type.equals(Config.amHour)) {
            String[] amTimeArray = amTimeWheel.split(":");
            String amTimeHour = amTimeArray[0];
            for (int i = 0; i < hourList.size(); i++) {
                if (hourList.get(i).equals(amTimeHour)) {
                    index = i;
                }
            }
        } else if (type.equals(Config.amMinute)) {
            String[] amTimeArray = amTimeWheel.split(":");
            String amTimeMinute = amTimeArray[1];
            for (int i = 0; i < minuteList.size(); i++) {
                if (minuteList.get(i).equals(amTimeMinute)) {
                    index = i;
                }
            }
        } else if (type.equals(Config.pmHour)) {
            String[] pmTimeArray = pmTimeWheel.split(":");
            String pmTimeHour = pmTimeArray[0];
            for (int i = 0; i < hourList.size(); i++) {
                if (hourList.get(i).equals(pmTimeHour)) {
                    index = i;
                }
            }
        } else if (type.equals(Config.pmMinute)) {
            String[] pmTimeArray = pmTimeWheel.split(":");
            String pmTimeMinute = pmTimeArray[1];
            for (int i = 0; i < minuteList.size(); i++) {
                if (minuteList.get(i).equals(pmTimeMinute)) {
                    index = i;
                }
            }
        }

        return index;
    }

}
