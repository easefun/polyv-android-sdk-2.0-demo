package com.easefun.polyvsdk.util;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * 存储工具类
 * @author Lionel 2018-6-12
 */
public class PolyvStorageUtils {

    private final static String MOUNTED = "mounted";

    /**
     * 获取可使用的外部存储目录<br/>
     * 列表中的第一个条目被视为外部主存储，当Android API 大于等于19时，列表中包含了可移除的存储介质（例如 SD 卡）的路径。
     * 当用户卸载您的应用时，此目录列表中的目录及其内容将被删除。
     * 详细请看:https://developer.android.com/guide/topics/data/data-storage?hl=zh-cn#filesExternal
     * @param context
     * @return 外部存储目录列表
     */
    @NonNull
    public static ArrayList<File> getExternalFilesDirs(@NonNull Context context) {
        File[] files;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //列表中包含了可移除的存储介质（例如 SD 卡）的路径。
            files = context.getExternalFilesDirs(null);
        } else {
            files = ContextCompat.getExternalFilesDirs(context, null);
        }

        ArrayList<File> storageList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //判断存储设备可用性
            for (File file : files) {
                if (file != null) {
                    String state = Environment.getExternalStorageState(file);
                    if (MOUNTED.equals(state)) {
                        storageList.add(file);
                    }
                }
            }
        } else {
            storageList.addAll(Arrays.asList(files));
        }

        return storageList;
    }
}
