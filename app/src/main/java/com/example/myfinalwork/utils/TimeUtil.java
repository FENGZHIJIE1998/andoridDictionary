package com.example.myfinalwork.utils;

import java.util.Calendar;
import java.util.TimeZone;

public class TimeUtil {

    public static String getUTCTimeStr() throws Exception {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = TimeZone.getTimeZone("GMT");
        cal.setTimeZone(tz);
        return String.valueOf(cal.getTimeInMillis() / 1000);// 返回的UTC时间戳
    }

}
