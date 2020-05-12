package com.example.myfinalwork.utils;

import android.content.Intent;

import java.util.Base64;

public class Base64Util {

    public static String encrypt(byte[] data) {
        // 对字节数组Base64编码
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(data);
    }
}
