package com.easefun.polyvsdk.permission;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Polyv权限
 * targetSdkVersion 设置了23或以上需要实现运行时权限功能，否则无法播放视频
 */
public class PolyvPermission {
    private ResponseCallback responseCallback = null;
    private Activity activity = null;

    public enum OperationType {
        play(100),
        download(101),
        upload(102),
        playAndDownload(103),
        ;

        private final int num;
        private OperationType(int num) {
            this.num = num;
        }

        public int getNum() {
            return num;
        }

        public static OperationType getOperationType(int num) {
            if (num == play.getNum()) {
                return play;
            } else if (num == download.getNum()) {
                return download;
            } else if (num == upload.getNum()) {
                return upload;
            } else if (num == playAndDownload.getNum()) {
                return playAndDownload;
            }

            return play;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void applyPermission(Activity activity, OperationType type) {
        this.activity = activity;
        if (!canMakeSmores()) {
            if (responseCallback != null) {
                responseCallback.callback(type);
            }

            return;
        }

        PLVStoragePermissionCompat.start(activity, new PLVOnPermissionCallback() {
            @Override
            public void onAllGranted() {
                if (responseCallback != null) {
                    responseCallback.callback(type);
                }
            }

            @Override
            public void onPartialGranted(ArrayList<String> grantedPermissions, ArrayList<String> deniedPermissions, ArrayList<String> deniedForeverP) {
                if (deniedForeverP != null && !deniedForeverP.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle("提示");
                    builder.setMessage("需要权限被拒绝，是否跳转到权限设置？");
                    builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Toast.makeText(activity, "点击权限，并打开全部权限", Toast.LENGTH_LONG).show();
                            PLVStoragePermissionCompat.jump2Settings(activity);
                            dialog.dismiss();
                        }
                    });

                    builder.setNegativeButton("取消", null);
                    builder.setCancelable(true);
                    builder.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle("提示");
                    builder.setMessage("请开启功能需要的权限，再使用该功能。");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                        }
                    });

                    builder.setCancelable(true);
                    builder.show();
                }
            }
        });
    }

    public boolean operationHasPermission(int num) {
        OperationType operationType = OperationType.getOperationType(num);
        if (operationType == null) return false;
        switch (operationType) {
            case play:
                return hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        && hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            case download:
                return hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            case upload:
                return hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            case playAndDownload:
                return hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        && hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        return false;
    }

    /**
     * method that will return whether the permission is accepted. By default it is true if the user is using a device below
     * version 23
     * @param permission
     * @return
     */
    @TargetApi(Build.VERSION_CODES.M)
    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            return(activity.checkSelfPermission(permission)== PackageManager.PERMISSION_GRANTED);
        }
        return true;
    }

    /**
     * This method is used to determine the permissions we do not have accepted yet and ones that we have not already
     * bugged the user about.  This comes in handle when you are asking for multiple permissions at once.
     * @param wanted
     * @return
     */
    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<>();
        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    /**
     * this will return us all the permissions we have previously asked for but
     * currently do not have permission to use. This may be because they declined us
     * or later revoked our permission. This becomes useful when you want to tell the user
     * what permissions they declined and why they cannot use a feature.
     * @param wanted
     * @return
     */
    private ArrayList<String> findRejectedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<>();
        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    /**
     * Just a check to see if we have marshmallows (version 23)
     * @return
     */
    public static boolean canMakeSmores() {
        return(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    public void setResponseCallback(ResponseCallback responseCallback) {
        this.responseCallback = responseCallback;
    }

    public interface ResponseCallback {
        void callback(@NonNull OperationType type);
    }
}
