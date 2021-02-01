package com.easefun.polyvsdk.util;

import android.content.Context;
import android.provider.Settings;
import android.support.annotation.NonNull;

/**
 * 工具类 获取唯一标识符
 */
public class PolyvIdUtil {

    public static String getAndroidId(@NonNull Context context) {
        return Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
    }

}
