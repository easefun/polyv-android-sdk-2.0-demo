package com.easefun.polyvsdk.permission;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;

public class PLVStoragePermissionCompat {

    public static boolean hasPermission(Context context) {
        return PLVFastPermission.hasStoragePermissionCompat13(context);
    }

    public static void start(final Context context, final PLVOnPermissionCallback callback) {
        PLVFastPermission.hasStoragePermissionCompat13(context, new PLVOnStoragePermissionCallback() {

            @Override
            public void onResult(boolean isGrant, boolean isAPI13) {
                if (isGrant) {
                    callback.onAllGranted();
                } else {
                    requestPermissionWhenNoGrand(context, isAPI13, callback);
                }
            }
        });
    }

    public static void jump2Settings(Context context) {
        if (PLVFastPermission.isAndroid13()) {
            PLVFastPermission.getInstance().jump2FilesAccessPermission(context);
        } else {
            PLVFastPermission.getInstance().jump2Settings(context);
        }
    }

    public static void requestPermissionWhenNoGrand(final Context context, boolean isAPI13, final PLVOnPermissionCallback callback) {
        if (isAPI13) {
            new AlertDialog.Builder(context).setMessage("视频播放、下载需要文件访问权限，是否前往开启权限？").setPositiveButton("是", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startStoragePermissionCompat13(context, callback);
                }
            }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            }).setCancelable(false).show();
        } else {
            startStoragePermissionCompat13(context, callback);
        }
    }

    private static void startStoragePermissionCompat13(Context context, final PLVOnPermissionCallback callback) {
        PLVFastPermission.getInstance().startStoragePermissionCompat13((Activity) context, new PLVOnPermissionCallback() {
            @Override
            public void onAllGranted() {
                if (callback != null) {
                    callback.onAllGranted();
                }
            }

            @Override
            public void onPartialGranted(ArrayList<String> grantedPermissions, ArrayList<String> deniedPermissions, ArrayList<String> deniedForeverP) {
                if (callback != null) {
                    callback.onPartialGranted(grantedPermissions, deniedPermissions, deniedForeverP);
                }
            }
        });
    }
}
