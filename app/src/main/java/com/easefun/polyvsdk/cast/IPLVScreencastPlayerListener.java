package com.easefun.polyvsdk.cast;

import android.support.annotation.Nullable;

/**
 * @author Hoshiiro
 */
public interface IPLVScreencastPlayerListener {

    void onStart();

    void onPause();

    void onStop();

    void onComplete();

    void onError(String method, int errorCode, @Nullable String errorMsg);

    void onPositionUpdate(long position);

}
