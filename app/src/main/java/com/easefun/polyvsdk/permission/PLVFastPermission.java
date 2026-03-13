package com.easefun.polyvsdk.permission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * author: HWilliam
 * Date  : 18-9-21
 * 权限请求器
 */

public class PLVFastPermission {

    // <editor-fold defaultstate="collapsed" desc="单例">
    private PLVFastPermission() {
    }

    private static class SingletonHolder {
        private static final PLVFastPermission INSTANCE = new PLVFastPermission();
    }

    public static PLVFastPermission getInstance() {
        return SingletonHolder.INSTANCE;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="API">

    /**
     * 进行权限请求，并将请求结果回调
     *
     * @param activity    Activity上下文
     * @param permissions 需要请求的权限，参考：{@link Manifest.permission}
     * @param callback    回调
     */
    public void start(Activity activity, List<String> permissions, PLVOnPermissionCallback callback) {
        if (activity == null) {
            Log.e("PLVFastPermission", new Throwable("activity=null").getMessage());
            return;
        }
        if (permissions == null) {
            Log.e("PLVFastPermission", new Throwable("permissions=null").getMessage());
            return;
        }
        if (callback == null) {
            Log.e("PLVFastPermission", new Throwable("callback=null").getMessage());
            return;
        }

        //api<23则不需要动态请求权限。
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            callback.onAllGranted();
            return;
        }
        ArrayList<String> target = new ArrayList<>(permissions.size());
        for (int i = 0; i < permissions.size(); i++) {
            String permission = permissions.get(i);
            if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                target.add(permission);
            }
        }
        //如果所有请求都被允许，那么直接返回，不要去创建Fragment。
        if (target.isEmpty()) {
            callback.onAllGranted();
            return;
        }
        PLVVoidFragment fragment = PLVVoidFragment.newInstance(target);
        fragment.setCallback(callback);
        activity.getFragmentManager().beginTransaction().add(fragment, activity.getClass().getSimpleName()).commit();
    }

    public void startStoragePermissionCompat13(Activity activity, PLVOnPermissionCallback callback) {
        if (activity == null) {
            return;
        }
        if (PLVFastPermission.isAndroid13()) {
            if (!Environment.isExternalStorageManager()) {
                PLVStorageFragmentCompat13.requestPermission(activity, callback);
            } else {
                if (callback != null) {
                    callback.onAllGranted();
                }
            }
        } else {
            start(activity, Collections.singletonList(Manifest.permission.WRITE_EXTERNAL_STORAGE), callback);
        }
    }

    /**
     * 跳转到当前app的系统设置页面，用于在权限被永久禁用的情况下，让用户手动开启权限
     *
     * @param context 任何上下文
     */
    public void jump2Settings(Context context) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.parse("package:" + context.getApplicationInfo().packageName));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void jump2FilesAccessPermission(Context context) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
        context.startActivity(intent);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="通用方法">
    public static boolean isAndroid13() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU;
    }

    public static boolean isAndroidTarget13(Context context) {
        return isAndroid13() && context != null && context.getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.TIRAMISU;
    }

    public static boolean hasStoragePermissionCompat13(Context context) {
        return hasStoragePermissionCompat13(context, null);
    }

    public static boolean hasStoragePermissionCompat13(Context context, PLVOnStoragePermissionCallback callback) {
        boolean result = hasPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (PLVFastPermission.isAndroid13()) {
            result = result || Environment.isExternalStorageManager();
        }
        if (callback != null) {
            callback.onResult(result, PLVFastPermission.isAndroid13());
        }
        return result;
    }

    public static int checkSelfPermissionCompat13(Context context, String permission, int originalResult) {
        if (context == null || permission == null) {
            return PackageManager.PERMISSION_DENIED;
        }
        if (PLVFastPermission.isAndroid13()
                && (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) || permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE))
                && Environment.isExternalStorageManager()) {
            return PackageManager.PERMISSION_GRANTED;
        }
        return originalResult;
    }

    public static boolean hasPermission(Context context, String... permissions) {
        for (String s : permissions) {
            if (ActivityCompat.checkSelfPermission(context, s) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasPermission(Context context, List<String> permissions) {
        for (String s : permissions) {
            if (ActivityCompat.checkSelfPermission(context, s) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    // </editor-fold>

}
