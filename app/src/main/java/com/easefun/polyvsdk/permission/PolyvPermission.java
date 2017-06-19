package com.easefun.polyvsdk.permission;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;

import java.util.ArrayList;

/**
 * Polyv权限
 * targetSdkVersion 设置了23或以上需要实现运行时权限功能，否则无法播放视频
 */
public class PolyvPermission {
    private ResponseCallback responseCallback = null;
    private SharedPreferences sharedPreferences = null;
    private Activity activity = null;

    /** 请求的权限列表 */
    private ArrayList<String> permissionsToRequest = null;
    /** 拒绝的权限列表 */
    private ArrayList<String> permissionsRejected = null;

    public enum OperationType {
        play(100),
        download(101),
        upload(102);

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
            }

            return null;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void applyPermission(Activity activity, OperationType type) {
        this.activity = activity;
        if (!canMakeSmores()) {
            if (responseCallback != null) {
                responseCallback.callback();
            }

            return;
        }

        ArrayList<String> permissions = new ArrayList<String>();
        int resultCode = 0;
        switch (type) {
            case play:
                //播放视频需要的权限
                permissions.add(Manifest.permission.READ_PHONE_STATE);
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                resultCode = OperationType.play.getNum();
                break;
            case download:
                //下载需要的权限
                permissions.add(Manifest.permission.READ_PHONE_STATE);
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                resultCode = OperationType.download.getNum();
                break;
            case upload:
                //上传需要的权限
                permissions.add(Manifest.permission.READ_PHONE_STATE);
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                resultCode = OperationType.upload.getNum();
                break;
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        //筛选出我们已经接受的权限
        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.
        permissionsRejected = findRejectedPermissions(permissions);

        if(permissionsToRequest.size()>0){//we need to ask for permissions
            //but have we already asked for them?
            activity.requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), resultCode);
            //mark all these as asked..
            for(String perm : permissionsToRequest){
                markAsAsked(perm);
            }
        }else{
            if(permissionsRejected.size()>0){
                //we have none to request but some previously rejected..tell the user.
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("提示");
                builder.setMessage("需要权限被拒绝，是否允许再次提示权限申请？");
                builder.setPositiveButton("允许", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        for(String perm: permissionsRejected){
                            clearMarkAsAsked(perm);
                        }

                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("取消", null);
                builder.setCancelable(true);
                builder.show();
            } else {
                if (responseCallback != null) {
                    responseCallback.callback();
                }
            }
        }
    }

    public boolean operationHasPermission(int num) {
        OperationType operationType = OperationType.getOperationType(num);
        if (operationType == null) return false;
        switch (operationType) {
            case play:
                return hasPermission(Manifest.permission.READ_PHONE_STATE)
                        && hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            case download:
                return hasPermission(Manifest.permission.READ_PHONE_STATE)
                        && hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            case upload:
                return hasPermission(Manifest.permission.READ_PHONE_STATE)
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
     * method to determine whether we have asked
     * for this permission before.. if we have, we do not want to ask again.
     * They either rejected us or later removed the permission.
     * @param permission
     * @return
     */
    private boolean shouldWeAsk(String permission) {
        return(sharedPreferences.getBoolean(permission, true));
    }

    /**
     * we will save that we have already asked the user
     * @param permission
     */
    private void markAsAsked(String permission) {
        sharedPreferences.edit().putBoolean(permission, false).apply();
    }

    /**
     * We may want to ask the user again at their request.. Let's clear the
     * marked as seen preference for that permission.
     * @param permission
     */
    private void clearMarkAsAsked(String permission) {
        sharedPreferences.edit().putBoolean(permission, true).apply();
    }


    /**
     * This method is used to determine the permissions we do not have accepted yet and ones that we have not already
     * bugged the user about.  This comes in handle when you are asking for multiple permissions at once.
     * @param wanted
     * @return
     */
    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm) && shouldWeAsk(perm)) {
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
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm) && !shouldWeAsk(perm)) {
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

    /**
     * a method that will centralize the showing of a snackbar
     */
    public void makePostRequestSnack() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("提示");
        builder.setMessage("需要权限被拒绝，是否允许再次提示权限申请？");
        builder.setPositiveButton("允许", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                for(String perm: permissionsRejected){
                    clearMarkAsAsked(perm);
                }

                dialog.dismiss();
            }
        });

        builder.setNegativeButton("取消", null);
        builder.setCancelable(true);
        builder.show();
    }

    public void setResponseCallback(ResponseCallback responseCallback) {
        this.responseCallback = responseCallback;
    }

    public interface ResponseCallback {
        void callback();
    }
}
