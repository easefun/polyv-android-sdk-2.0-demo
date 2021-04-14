package com.easefun.polyvsdk.cast;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.hpplay.sdk.source.api.IBindSdkListener;
import com.hpplay.sdk.source.api.IConnectListener;
import com.hpplay.sdk.source.api.ILelinkPlayerListener;
import com.hpplay.sdk.source.api.LelinkPlayerInfo;
import com.hpplay.sdk.source.api.LelinkSourceSDK;
import com.hpplay.sdk.source.bean.MediaAssetBean;
import com.hpplay.sdk.source.browse.api.IAPI;
import com.hpplay.sdk.source.browse.api.IBrowseListener;
import com.hpplay.sdk.source.browse.api.IParceResultListener;
import com.hpplay.sdk.source.browse.api.LelinkServiceInfo;

import java.util.List;


public class PolyvAllCast {

    private static final String TAG = "PolyvAllCast";

    private boolean isMirror;
    public static final int MEDIA_TYPE_VIDEO = LelinkPlayerInfo.TYPE_VIDEO;
    public static final int MEDIA_TYPE_AUDIO = LelinkPlayerInfo.TYPE_AUDIO;
    public static final int MEDIA_TYPE_IMAGE = LelinkPlayerInfo.TYPE_IMAGE;

    public PolyvAllCast(Context context, String appid, String appSecret) {
        initLelinkService(context, appid, appSecret);
    }

    public void setOnBrowseListener(IBrowseListener listener) {
        LelinkSourceSDK.getInstance().setBrowseResultListener(listener);
    }

    public void setConnectListener(IConnectListener listener) {
        LelinkSourceSDK.getInstance().setConnectListener(listener);
    }

    public void setPlayerListener(ILelinkPlayerListener listener) {
        LelinkSourceSDK.getInstance().setPlayListener(listener);
    }

    private void initLelinkService(Context context, String appid, String appSecret) {
        LelinkSourceSDK.getInstance()
                .setBindSdkListener(new IBindSdkListener() {
                    @Override
                    public void onBindCallback(boolean result) {
                        Log.e(TAG, "Polyv Cast SDK Init Result :" + result);
                        if (result) {
                            LelinkSourceSDK.getInstance().setOption(IAPI.OPTION_5, false);
                            LelinkSourceSDK.getInstance().setDebugMode(true);
                            LelinkSourceSDK.getInstance().enableLogCache(true);
                        }
                    }
                })
                .setSdkInitInfo(context, appid, appSecret)
                .bindSdk();
    }

    public List<LelinkServiceInfo> getConnectInfos() {
        return LelinkSourceSDK.getInstance().getConnectInfos();
    }


    public void addQRServiceInfo(String qrCode, IParceResultListener listener) {
        LelinkSourceSDK.getInstance().addQRCodeToLelinkServiceInfo(qrCode, listener);
    }

    public void addPinCodeServiceInfo(String pinCode) {
        LelinkSourceSDK.getInstance().addPinCodeToLelinkServiceInfo(pinCode, new IParceResultListener() {
            @Override
            public void onParceResult(int resultCode, LelinkServiceInfo lelinkServiceInfo) {
                if (resultCode == IParceResultListener.PARCE_SUCCESS) {
                    connect(lelinkServiceInfo);
                }
            }
        });
    }

    public void browse() {
        LelinkSourceSDK.getInstance().startBrowse();
    }

    public void stopBrowse() {
        LelinkSourceSDK.getInstance().stopBrowse();
    }

    public void connect(LelinkServiceInfo pInfo) {
        LelinkSourceSDK.getInstance().connect(pInfo);
    }

    public void disConnect(LelinkServiceInfo pInfo) {
        LelinkSourceSDK.getInstance().disConnect(pInfo);
    }

    public boolean canPlayMedia(LelinkServiceInfo info) {
        return LelinkSourceSDK.getInstance().canPlayLocalMedia(info);
    }


    public boolean canPlayScreen(LelinkServiceInfo serviceInfo) {
        return LelinkSourceSDK.getInstance().canPlayScreen(serviceInfo);
    }

    // <editor-fold defaultstate="collapsed" desc="播放相关方法">

    public void playLocalMedia(String url, int type, String screenCode) {
        LelinkPlayerInfo lelinkPlayerInfo = new LelinkPlayerInfo();
        lelinkPlayerInfo.setType(type);
        lelinkPlayerInfo.setLocalPath(url);
        lelinkPlayerInfo.setOption(IAPI.OPTION_6, screenCode);
        // lelinkPlayerInfo.setStartPosition(8);

        LelinkSourceSDK.getInstance().startPlayMedia(lelinkPlayerInfo);
    }

    public void playNetMedia(String url, int type, String screenCode) {
        LelinkPlayerInfo lelinkPlayerInfo = new LelinkPlayerInfo();
        lelinkPlayerInfo.setType(type);
        lelinkPlayerInfo.setUrl(url);
        lelinkPlayerInfo.setOption(IAPI.OPTION_6, screenCode);
        // lelinkPlayerInfo.setStartPosition(15);

        LelinkSourceSDK.getInstance().startPlayMedia(lelinkPlayerInfo);
    }

    public void playNetMediaWithPosition(String url, int type, int seconds) {
        LelinkPlayerInfo lelinkPlayerInfo = new LelinkPlayerInfo();
        lelinkPlayerInfo.setType(type);
        lelinkPlayerInfo.setUrl(url);
        lelinkPlayerInfo.setStartPosition(seconds);

        LelinkSourceSDK.getInstance().startPlayMedia(lelinkPlayerInfo);
    }

    public void playNetMediaWithAsset(String url, int type) {
        LelinkPlayerInfo lelinkPlayerInfo = new LelinkPlayerInfo();
        lelinkPlayerInfo.setType(type);
        lelinkPlayerInfo.setUrl(url);
        MediaAssetBean mediaAssetBean = new MediaAssetBean();
        mediaAssetBean.setActor("qiuju");
        mediaAssetBean.setDirector("zhangyimou");
        mediaAssetBean.setId("xxxxx");
        mediaAssetBean.setName("qiujudaguansi");
        lelinkPlayerInfo.setMediaAsset(mediaAssetBean);

        LelinkSourceSDK.getInstance().startPlayMedia(lelinkPlayerInfo);
    }

    public void playNetMediaWithHeader(String url, int type) {
        LelinkPlayerInfo lelinkPlayerInfo = new LelinkPlayerInfo();
        lelinkPlayerInfo.setType(type);
//        lelinkPlayerInfo.setHeader("{\"header\":\"header data\"}");
        lelinkPlayerInfo.setAesKey("bf465ebdb8ae575c5efe4e6a54f2147c");
        lelinkPlayerInfo.setAesIv("9ad37d28d3f3e74e4040a9cdd6ebdffd");
        lelinkPlayerInfo.setUrl("http://hls.videocc.net/3704709a81/f/3704709a81455e99119c8a261d6c186f_1.m3u8?pid=1545874882540X1041774");
        lelinkPlayerInfo.setLoopMode(LelinkPlayerInfo.LOOP_MODE_SINGLE);
        // lelinkPlayerInfo.setStartPosition(15);

        LelinkSourceSDK.getInstance().startPlayMedia(lelinkPlayerInfo);
    }

    public void playNetMediaWithHeader(LelinkPlayerInfo lelinkPlayerInfo) {

        LelinkSourceSDK.getInstance().startPlayMedia(lelinkPlayerInfo);
    }

    // </editor-fold>

    public void startWithLoopMode(String url, boolean isLocalFile) {
        LelinkPlayerInfo playerInfo = new LelinkPlayerInfo();
        if (isLocalFile) {
            playerInfo.setLocalPath(url);
        } else {
            playerInfo.setUrl(url);
        }
        playerInfo.setType(MEDIA_TYPE_VIDEO);
        playerInfo.setLoopMode(LelinkPlayerInfo.LOOP_MODE_SINGLE);

        LelinkSourceSDK.getInstance().startPlayMedia(playerInfo);
    }

    public void startNetVideoWith3rdMonitor(String netVideoUrl) {
        LelinkPlayerInfo playerInfo = new LelinkPlayerInfo();
        playerInfo.setUrl(netVideoUrl);
        playerInfo.setType(MEDIA_TYPE_VIDEO);
        playerInfo.putMonitor(LelinkPlayerInfo.MONITOR_START, "http://aa.qiyi.com/report?u=_UID_&h=_HID_&m=_MAC_&t=_TIME_&model=_MODEL_&a=_APPID_&p=_POS_&i=_IP_");
        playerInfo.putMonitor(LelinkPlayerInfo.MONITOR_STOP, "http://aa.qiyi.com/report?u=_UID_&h=_HID_&m=_MAC_&t=_TIME_&model=_MODEL_&a=_APPID_&p=_POS_&i=_IP_");
        playerInfo.putMonitor(LelinkPlayerInfo.MONITOR_RESUME, "http://aa.qiyi.com/report?u=_UID_&h=_HID_&m=_MAC_&t=_TIME_&model=_MODEL_&a=_APPID_&p=_POS_&i=_IP_");
        playerInfo.putMonitor(LelinkPlayerInfo.MONITOR_PAUSE, "http://aa.qiyi.com/report?u=_UID_&h=_HID_&m=_MAC_&t=_TIME_&model=_MODEL_&a=_APPID_&p=_POS_&i=_IP_");

        LelinkSourceSDK.getInstance().startMirror(playerInfo);
    }

    public void resume() {
        LelinkSourceSDK.getInstance().resume();
    }

    public void pause() {
        LelinkSourceSDK.getInstance().pause();
    }

    public void stopPlay() {
        LelinkSourceSDK.getInstance().stopPlay();
    }

    public void seekTo(int position) {
        LelinkSourceSDK.getInstance().seekTo(position);
    }


    public void setVolume(int percent) {
        LelinkSourceSDK.getInstance().setVolume(percent);
    }

    public void voulumeUp() {
        LelinkSourceSDK.getInstance().addVolume();
    }

    public void voulumeDown() {
        LelinkSourceSDK.getInstance().subVolume();
    }

    public void startMirror(Activity pActivity, LelinkServiceInfo lelinkServiceInfo,
                            int resolutionLevel, int bitrateLevel, boolean isAudioEnnable, String screenCode) {

        isMirror = true;
        LelinkPlayerInfo lelinkPlayerInfo = new LelinkPlayerInfo();
        lelinkPlayerInfo.setType(LelinkPlayerInfo.TYPE_MIRROR);
        lelinkPlayerInfo.setOption(IAPI.OPTION_6, screenCode);
        lelinkPlayerInfo.setLelinkServiceInfo(lelinkServiceInfo);
        lelinkPlayerInfo.setMirrorAudioEnable(isAudioEnnable);
        lelinkPlayerInfo.setResolutionLevel(resolutionLevel);
        lelinkPlayerInfo.setBitRateLevel(bitrateLevel);

        LelinkSourceSDK.getInstance().startMirror(lelinkPlayerInfo);
    }


}
