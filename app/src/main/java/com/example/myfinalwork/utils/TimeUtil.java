package com.example.myfinalwork.utils;

import java.util.Calendar;
import java.util.TimeZone;

public class TimeUtil {

    public static String getUTCTimeStr() throws Exception {
        return String.valueOf(System.currentTimeMillis() / 1000);// 返回的UTC时间戳
    }

}
