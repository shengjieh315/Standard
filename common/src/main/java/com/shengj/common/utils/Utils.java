package com.shengj.common.utils;

import android.text.TextUtils;

/**
 * 主要工具类
 */
public class Utils {

    private static String deviceId = "";

    public static String getDeviceId() {
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = PhoneHelper.getInstance().getIME();
        }
        return deviceId;
    }

}
