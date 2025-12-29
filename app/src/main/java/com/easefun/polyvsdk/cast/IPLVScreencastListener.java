package com.easefun.polyvsdk.cast;

import androidx.annotation.Nullable;

import net.polyv.android.media.cast.model.vo.PLVMediaCastDevice;

import java.util.List;

/**
 * @author Hoshiiro
 */
public interface IPLVScreencastListener {

    void onDeviceScanned(List<PLVMediaCastDevice> devices);

    void onStart();

    void onPause();

    void onStop();

    void onComplete();

    void onError(String method, int errorCode, @Nullable String errorMsg);

    void onPositionUpdate(long position);

}
