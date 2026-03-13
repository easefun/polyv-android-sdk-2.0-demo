package com.easefun.polyvsdk.permission;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;

public class PLVStorageFragmentCompat13 extends Fragment {
    public static int ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION_REQUEST_CODE = 9032;
    private static PLVOnPermissionCallback permissionListener;

    public static void requestPermission(Activity activity, PLVOnPermissionCallback listener) {
        permissionListener = listener;
        FragmentManager fragmentManager = activity.getFragmentManager();
        if (fragmentManager.findFragmentByTag(activity.getLocalClassName()) == null) {
            fragmentManager.beginTransaction()
                    .add(new PLVStorageFragmentCompat13(), activity.getLocalClassName())
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
        startActivityForResult(intent, ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, Intent data) {
        if (requestCode == ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION_REQUEST_CODE) {
            // 需要延迟执行，不然即使授权，仍有部分机型获取不到权限
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (PLVFastPermission.hasStoragePermissionCompat13(getActivity())) {
                        if (permissionListener != null) {
                            permissionListener.onAllGranted();
                        }
                    } else {
                        if (permissionListener != null) {
                            permissionListener.onPartialGranted(new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(Collections.singletonList(Manifest.permission.WRITE_EXTERNAL_STORAGE)));
                        }
                    }
                    permissionListener = null;
                    getFragmentManager().beginTransaction().remove(PLVStorageFragmentCompat13.this).commitAllowingStateLoss();
                }
            }, 500);
        }

    }
}
