package com.easefun.polyvsdk.permission;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * author: HWilliam
 * Date  : 18-9-21
 * 空Fragment
 */
public class PLVVoidFragment extends Fragment {
    private static final String TAG = PLVVoidFragment.class.getSimpleName();
    private static final int PERMISSION_GRANT = PackageManager.PERMISSION_GRANTED;
    private static final int PERMISSION_DENIED = PackageManager.PERMISSION_DENIED;

    private static final String S_BUNDLE_PERMISSIONS = "bundle_permissions";

    private static final int S_REQUEST_CODE = 100;

    private Activity mActivity;

    private PLVOnPermissionCallback mCallback;


    public static PLVVoidFragment newInstance(ArrayList<String> permissions) {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(S_BUNDLE_PERMISSIONS, permissions);

        PLVVoidFragment fragment = new PLVVoidFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setCallback(PLVOnPermissionCallback callback) {
        mCallback = callback;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle == null) {
            Log.e(TAG, "没有要请求的权限");// no need i18n
            removeVoidFragment();
            mCallback.onAllGranted();
            return;
        }
        ArrayList<String> permissions = bundle.getStringArrayList(S_BUNDLE_PERMISSIONS);
        if (permissions == null) {
            Log.e(TAG, "没有要请求的权限");// no need i18n
            removeVoidFragment();
            mCallback.onAllGranted();
            return;
        }
        //check permission
        String[] targetPermissions = permissions.toArray(new String[0]);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(targetPermissions, S_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        removeVoidFragment();
        boolean allGrant = true;
        int index  = -1;
        for (int grantResult : grantResults) {
            index++;
            String permission = permissions[index];
            // Android13 读写存储权限申请必定返回PERMISSION_DENIED，所以这里判断Environment.isExternalStorageManager时返回true
            if (PLVFastPermission.isAndroid13()
                    && (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) || permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE))
                    && Environment.isExternalStorageManager()
                    && grantResult == PackageManager.PERMISSION_DENIED) {
                grantResult = PackageManager.PERMISSION_GRANTED;
            }
            if (grantResult == PERMISSION_DENIED) {
                allGrant = false;
                break;
            }
        }
        if (allGrant) {
            mCallback.onAllGranted();
            return;
        }
        ArrayList<String> grantList = new ArrayList<>();
        ArrayList<String> denyList = new ArrayList<>();
        ArrayList<String> denyForever = new ArrayList<>();

        for (int i = 0; i < grantResults.length; i++) {
            String permission = permissions[i];
            if (grantResults[i] == PERMISSION_DENIED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permission)) {
                    //用户没有永远禁止权限，可以再次请求。
                    denyList.add(permission);
                } else {
                    //权限被永远禁止。
                    denyForever.add(permission);
                }
            } else {
                //权限被允许
                grantList.add(permission);
            }
        }

        //将结果发布
        mCallback.onPartialGranted(grantList, denyList, denyForever);

    }

    //移除VoidFragment
    private void removeVoidFragment() {
        getFragmentManager().beginTransaction().remove(this).commit();
    }
}
