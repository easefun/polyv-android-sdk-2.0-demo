package com.easefun.polyvsdk.cast;

import com.apowersoft.dlnasender.api.Constant;
import com.apowersoft.dlnasender.api.DLNASender;
import com.apowersoft.dlnasender.api.bean.DeviceInfo;
import com.apowersoft.dlnasender.api.bean.MediaInfo;
import com.apowersoft.dlnasender.api.bean.PositionInfo;
import com.apowersoft.dlnasender.api.listener.DLNADeviceConnectListener;
import com.apowersoft.dlnasender.api.listener.DLNARegistryListener;
import com.apowersoft.dlnasender.api.listener.WXDLNAMethodCallback;
import com.easefun.polyvsdk.log.PolyvCommonLog;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class PolyvAllCast {

    private static final String TAG = PolyvAllCast.class.getSimpleName();

    private IPLVScreencastPlayerListener screencastPlayerListener;

    private Timer getPositionTimer;

    public PolyvAllCast() {

    }

    public void initService(DLNADeviceConnectListener dlnaDeviceConnectListener, DLNARegistryListener dlnaRegistryListener) {
        DLNASender.getInstance().initService(dlnaDeviceConnectListener, dlnaRegistryListener);
    }

    public void setPlayerListener(IPLVScreencastPlayerListener listener) {
        this.screencastPlayerListener = listener;
    }

    public void browse() {
        DLNASender.getInstance().startBrowser();
    }

    public void stopBrowse() {

    }

    public void connect(DeviceInfo pInfo) {
        DLNASender.getInstance().addCallback(callback);
        DLNASender.getInstance().connectDevice(pInfo);
    }

    public void disConnect(DeviceInfo pInfo) {
        stopGetPositionTimer();
        DLNASender.getInstance().stopDLNA();
        DLNASender.getInstance().removeCallback(callback);
    }

    // <editor-fold defaultstate="collapsed" desc="播放相关方法">

    public void playNetMediaWithHeader(MediaInfo mediaInfo) {
        DLNASender.getInstance().setDataSource(mediaInfo);
        DLNASender.getInstance().startDLNACast();
        startGetPositionTimer();
    }

    public void resume() {
        DLNASender.getInstance().play();
    }

    public void pause() {
        DLNASender.getInstance().pause();
    }

    public void stopPlay() {
        stopGetPositionTimer();
        DLNASender.getInstance().stopDLNA();
    }

    public void seekTo(int position) {
        DLNASender.getInstance().seekTo(position);
    }

    public void setVolume(int percent) {
        DLNASender.getInstance().setCurrentVolume(percent);
    }

    public void volumeUp() {
        DLNASender.getInstance().setVolume(true, 5);
    }

    public void volumeDown() {
        DLNASender.getInstance().setVolume(false, 5);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="回调">

    private final WXDLNAMethodCallback callback = new WXDLNAMethodCallback() {
        private long lastNonZeroTrackDurationSeconds = 0;
        @Override
        public void onSuccess(String method, Object obj) {
            if (screencastPlayerListener == null) {
                return;
            }
            switch (method) {
                case Constant.Action.START:
                    screencastPlayerListener.onStart();
                    break;
                case Constant.Action.PAUSE:
                    screencastPlayerListener.onPause();
                    break;
                case Constant.Action.STOP:
                    screencastPlayerListener.onStop();
                    break;
                case Constant.Action.GET_POSITION:
                    try {
                        PositionInfo positionInfo = (PositionInfo) obj;
                        screencastPlayerListener.onPositionUpdate(positionInfo.getTrackElapsedSeconds());
                        if (positionInfo.getTrackDurationSeconds() > 0) {
                            lastNonZeroTrackDurationSeconds = positionInfo.getTrackDurationSeconds();
                        }
                        if (lastNonZeroTrackDurationSeconds > 0 && positionInfo.getTrackElapsedSeconds() >= lastNonZeroTrackDurationSeconds) {
                            screencastPlayerListener.onComplete();
                        }
                    } catch (Exception e) {
                        PolyvCommonLog.e(TAG, "onPositionUpdate error" + e.getMessage());
                    }
                    break;
                default:
            }
        }

        // dlna消息发送异常
        private int ERROR_CODE_DLNA_SEND_MESSAGE_ERROR = 4;
        // dlna服务异常
        private int ERROR_CODE_DLNA_SERVICE_ERROR = 5;
        // dlna状态异常
        private int ERROR_CODE_DLNA_STATUS_ERROR = 6;
        // dlna接收端不支持此命令
        private int ERROR_CODE_RECEIVER_NOT_SUPPORT_ACTION = 8;

        @Override
        public void onFailure(String method, int errorCode, String errorMsg) {
            if (screencastPlayerListener != null) {
                screencastPlayerListener.onError(method, errorCode, errorMsg);
            }
        }
    };

    // </editor-fold>

    private void startGetPositionTimer() {
        if (getPositionTimer != null) {
            getPositionTimer.cancel();
        }
        getPositionTimer = new Timer();
        getPositionTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                DLNASender.getInstance().getPosition();
            }
        }, new Date(), 500);
    }

    private void stopGetPositionTimer() {
        if (getPositionTimer != null) {
            getPositionTimer.cancel();
        }
    }

}
