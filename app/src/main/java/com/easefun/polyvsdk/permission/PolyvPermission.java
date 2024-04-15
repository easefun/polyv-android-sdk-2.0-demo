package com.easefun.polyvsdk.permission;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
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

        ArrayList<String> permissions = new ArrayList<>();
        int resultCode = 0;
        switch (type) {
            case play:
                //播放视频需要的权限
                //投屏功能在android9.0获取wifi名称及搜索设备所需的权限
                //Android 10起需要精确定位权限，才能获取wifi详细信息
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                resultCode = OperationType.play.getNum();
                break;
            case download:
                //下载需要的权限
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                resultCode = OperationType.download.getNum();
                break;
            case upload:
                //上传需要的权限
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                resultCode = OperationType.upload.getNum();
                break;
            case playAndDownload:
                //播放视频和下载需要的权限
                //投屏功能在android9.0获取wifi名称及搜索设备所需的权限
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                resultCode = OperationType.playAndDownload.getNum();
                break;
        }

        //筛选出我们已经接受的权限
        /* 请求的权限列表 */
        ArrayList<String> permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.
        /* 拒绝的权限列表 */
        ArrayList<String> permissionsRejected = findRejectedPermissions(permissions);

        if(permissionsToRequest.size()>0){//we need to ask for permissions
            //but have we already asked for them?
            activity.requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), resultCode);
        }else{
            if(permissionsRejected.size()>0){
                for (int i = 0; i < permissionsRejected.size() ; i++) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionsRejected.get(i))) {
                        Toast.makeText(activity, "点击权限，并打开全部权限", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
                        activity.startActivityForResult(intent, type.getNum());
                        return;
                    }
                }
            } else {
                if (responseCallback != null) {
                    responseCallback.callback(type);
                }
            }
        }
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
