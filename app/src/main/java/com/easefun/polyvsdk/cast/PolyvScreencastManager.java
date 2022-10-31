package com.easefun.polyvsdk.cast;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;

import com.apowersoft.dlnasender.api.DLNASender;
import com.apowersoft.dlnasender.api.bean.DeviceInfo;
import com.apowersoft.dlnasender.api.bean.MediaInfo;
import com.apowersoft.dlnasender.api.listener.DLNADeviceConnectListener;
import com.apowersoft.dlnasender.api.listener.DLNARegistryListener;
import com.apowersoft.dlnasender.api.listener.WxDlnaSenderInitCallback;
import com.easefun.polyvsdk.log.PolyvCommonLog;

import java.util.List;

/**
 * 投屏封装工具类
 * 封装了投屏的基本使用，开发者在使用投屏时可直接集成
 */
public class PolyvScreencastManager {

    private static final String TAG = PolyvScreencastManager.class.getSimpleName();

    private volatile static PolyvScreencastManager INSTANCE;
    private UIHandler mUIHandler;
    private PolyvAllCast mAllCast;
    // 数据
    private List<DeviceInfo> mInfos;
    private DeviceInfo connectedDeviceInfo;
    private DeviceInfo lastConnectedDeviceInfo;

    // 监听器
    private DLNADeviceConnectListener mActivityConnectListener;

    public static PolyvScreencastManager getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (PolyvScreencastManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new PolyvScreencastManager();
                }
            }
        }
        return INSTANCE;
    }

    public static void init(Application application, String appId, String appSecret, final WxDlnaSenderInitCallback initCallback) {
        DLNASender.init(application, appId, appSecret, new WxDlnaSenderInitCallback() {
            @Override
            public void onSuccess() {
                if (initCallback != null) {
                    initCallback.onSuccess();
                }
            }

            @Override
            public void onFail(int i, String s) {
                if (initCallback != null) {
                    initCallback.onFail(i, s);
                }
            }
        });
    }

    private PolyvScreencastManager() {
        mUIHandler = new UIHandler(Looper.getMainLooper());
        mAllCast = new PolyvAllCast();
    }

    public void setUIUpdateListener(PolyvIUIUpdateListener pUIUpdateListener) {
        mUIHandler.setUIUpdateListener(pUIUpdateListener);
    }

    public void setActivityConnectListener(DLNADeviceConnectListener connectListener) {
        this.mActivityConnectListener = connectListener;
    }

    public List<DeviceInfo> getInfos() {
        return mInfos;
    }

    public List<DeviceInfo> getConnectInfos() {
        return mInfos;
    }

    public void initService() {
        mAllCast.initService(dlnaRegistryListener);
        mAllCast.setPlayerListener(mPlayerListener);
    }

    public void browse() {
        mAllCast.browse();
        callOnDeviceChange();
    }

    public void stopBrowse() {
        mAllCast.stopBrowse();
    }

    public void connect(DeviceInfo info) {
        mAllCast.connect(info, dlnaDeviceConnectListener);
        connectedDeviceInfo = info;
        lastConnectedDeviceInfo = info;
    }

    public DeviceInfo getLastConnectedDeviceInfo() {
        return lastConnectedDeviceInfo;
    }

    public void disConnect(DeviceInfo info) {
        mAllCast.disConnect(info);
        connectedDeviceInfo = null;
    }

    public void resume() {
        mAllCast.resume();
    }

    public void pause() {
        mAllCast.pause();
    }

    public void stopPlay(){
        mAllCast.stopPlay();
    }

    public void seekTo(int progress) {
        mAllCast.seekTo(progress);
    }

    public void setVolume(int percent) {
        mAllCast.setVolume(percent);
    }

    public void volumeUp() {
        mAllCast.volumeUp();
    }

    public void volumeDown() {
        mAllCast.volumeDown();
    }

    private Message buildTextMessage(String text) {
        Message message = Message.obtain();
        message.what = UIHandler.MSG_TEXT;
        message.obj = text;
        return message;
    }

    private Message buildStateMessage(int state) {
        return buildStateMessage(state, null);
    }

    private Message buildStateMessage(int state, Object object) {
        Message message = Message.obtain();
        message.what = UIHandler.MSG_STATE;
        message.arg1 = state;
        if (null != object) {
            message.obj = object;
        }
        return message;
    }

    // <editor-fold defaultstate="collapsed" desc="投屏监听器">

    private DLNARegistryListener dlnaRegistryListener = new DLNARegistryListener() {
        @Override
        public void onDeviceChanged(List<DeviceInfo> list) {
            mInfos = list;
            callOnDeviceChange();
        }
    };

    private void callOnDeviceChange() {
        if (mUIHandler != null) {
            if (mInfos == null || mInfos.isEmpty()) {
                mUIHandler.sendMessage(buildStateMessage(PolyvIUIUpdateListener.STATE_SEARCH_NO_RESULT));
            } else {
                mUIHandler.sendMessage(buildStateMessage(PolyvIUIUpdateListener.STATE_SEARCH_SUCCESS));
            }
        }
    }

    private DLNADeviceConnectListener dlnaDeviceConnectListener = new DLNADeviceConnectListener() {
        @Override
        public void onConnect(final DeviceInfo deviceInfo, final int errorCode) {
            if (mUIHandler != null) {
                final String text = deviceInfo.getName() + "连接成功";
                mUIHandler.sendMessage(buildTextMessage(text));
                mUIHandler.sendMessage(buildStateMessage(PolyvIUIUpdateListener.STATE_CONNECT_SUCCESS, text));
                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mActivityConnectListener != null) {
                            mActivityConnectListener.onConnect(deviceInfo, errorCode);
                        }
                    }
                });
            }
        }

        @Override
        public void onDisconnect(final DeviceInfo deviceInfo, final int errorCode) {
            if (mUIHandler != null) {
                final String text = deviceInfo.getName() + "连接断开";
                mUIHandler.sendMessage(buildTextMessage(text));
                mUIHandler.sendMessage(buildStateMessage(PolyvIUIUpdateListener.STATE_DISCONNECT, text));
                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mActivityConnectListener != null) {
                            mActivityConnectListener.onDisconnect(deviceInfo, errorCode);
                        }
                    }
                });
            }
        }
    };

    /**
     * 投屏播放监听
     */
    private IPLVScreencastPlayerListener mPlayerListener = new IPLVScreencastPlayerListener() {

        @Override
        public void onStart() {
            PolyvCommonLog.d(TAG, "onStart:");
            if (mUIHandler != null) {
                mUIHandler.sendMessage(buildTextMessage("开始播放"));
                mUIHandler.sendMessage(buildStateMessage(PolyvIUIUpdateListener.STATE_PLAY));
            }
        }

        @Override
        public void onPause() {
            PolyvCommonLog.d(TAG, "onPause");
            if (mUIHandler != null) {
                mUIHandler.sendMessage(buildTextMessage("暂停播放"));
                mUIHandler.sendMessage(buildStateMessage(PolyvIUIUpdateListener.STATE_PAUSE));
            }
        }

        @Override
        public void onStop() {
            PolyvCommonLog.d(TAG, "onStop");
            if (mUIHandler != null) {
                mUIHandler.sendMessage(buildTextMessage("播放结束"));
                mUIHandler.sendMessage(buildStateMessage(PolyvIUIUpdateListener.STATE_STOP));
            }
        }

        @Override
        public void onComplete() {
            PolyvCommonLog.d(TAG, "onComplete");
            if (mUIHandler != null) {
                mUIHandler.sendMessage(buildTextMessage("播放结束"));
                mUIHandler.sendMessage(buildStateMessage(PolyvIUIUpdateListener.STATE_COMPLETION));
            }
        }

        @Override
        public void onError(String method, int errorCode, @Nullable String errorMsg) {
            mUIHandler.sendMessage(buildTextMessage(errorMsg));
            mUIHandler.sendMessage(buildStateMessage(PolyvIUIUpdateListener.STATE_PLAY_ERROR, errorMsg));
        }

        /**
         * 进度更新回调
         *
         * @param position 当前进度
         */
        @Override
        public void onPositionUpdate(long position) {
            PolyvCommonLog.d(TAG, "onPositionUpdate position:" + position);
            if (mUIHandler != null) {
                mUIHandler.sendMessage(buildStateMessage(PolyvIUIUpdateListener.STATE_POSITION_UPDATE, position));
            }
        }

    };
    // </editor-fold >



    private static class UIHandler extends Handler {

        private static final int MSG_TEXT = 1;
        private static final int MSG_STATE = 2;
        private PolyvIUIUpdateListener mUIUpdateListener;

        private UIHandler(Looper pMainLooper) {
            super(pMainLooper);
        }

        private void setUIUpdateListener(PolyvIUIUpdateListener pUIUpdateListener) {
            mUIUpdateListener = pUIUpdateListener;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_TEXT:
                    String text = (String) msg.obj;
                    if (null != mUIUpdateListener) {
                        mUIUpdateListener.onUpdateText(text);
                    }
                    break;
                case MSG_STATE:
                    int state = msg.arg1;
                    Object obj = msg.obj;
                    if (null != mUIUpdateListener) {
                        mUIUpdateListener.onUpdateState(state, obj);
                    }
                    break;
            }
        }

    }

    // <editor-fold defaultstate="collapsed" desc="投屏播放">

    public void playNetMedia(MediaInfo mediaInfo) {
        mAllCast.playNetMediaWithHeader(mediaInfo);
    }
    // </editor-fold >

    public void release() {
        mAllCast.stopBrowse();
        mAllCast.stopPlay();
        if (connectedDeviceInfo != null) {
            disConnect(connectedDeviceInfo);
        }
    }

}
