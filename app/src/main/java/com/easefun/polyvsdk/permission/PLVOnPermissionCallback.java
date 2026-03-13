package com.easefun.polyvsdk.permission;

import java.util.ArrayList;

/**
 * author: HWilliam
 * Date  : 18-9-21
 * 权限请求回调
 */

public interface PLVOnPermissionCallback {
    /**
     * 所有请求都允许
     * 该回调发生后其他回调不再发生。
     */
    void onAllGranted();


    /**
     * 部分请求被允许
     *
     * @param grantedPermissions 被允许的请求的字符串数组
     * @param deniedPermissions  被拒绝的请求的字符串数组
     * @param deniedForeverP     被永远拒绝的请求
     */
    void onPartialGranted(ArrayList<String> grantedPermissions, ArrayList<String> deniedPermissions, ArrayList<String> deniedForeverP);
}
