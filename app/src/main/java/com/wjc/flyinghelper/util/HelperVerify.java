package com.wjc.flyinghelper.util;

import java.util.Arrays;
import java.util.HashMap;
import android.util.Base64;

public class HelperVerify {

    public static String helperkey = "2oWt1grdVKjQbnlzsfc2PeCOvLcfPix1";

    public static String sign(HashMap<String, String> hashMap) {
        String sign = "helperkey=" + helperkey + "&";

        Object[] array = hashMap.keySet().toArray();
        Arrays.sort(array);
        for (Object keyKey : array) {
            String key = (String) keyKey;
            String value = hashMap.get(key);

            sign += key + "=" + value + "&";
        }

        sign = sign.substring(0, sign.length() - 1);

        try {
            sign = Base64.encodeToString(sign.getBytes("UTF-8"), Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sign;
    }

}
