package com.easefun.polyvsdk.cast;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.Nullable;

import com.easefun.polyvsdk.log.PolyvCommonLog;

import net.polyv.android.common.libs.kava.Nullables;
import net.polyv.android.media.cast.model.vo.PLVMediaCastDevice;
import net.polyv.android.media.cast.model.vo.PLVMediaCastResource;
import net.polyv.android.media.cast.rx.PLVMediaCastManagerAdapterRxJava;

import java.util.List;

import kotlin.jvm.functions.Function0;

/**
 * 投屏封装工具类
 * 封装了投屏的基本使用，开发者在使用投屏时可直接集成
 */
public class PolyvScreencastManager {

    private static final String TAG = PolyvScreencastManager.class.getSimpleName();

    private volatile static PolyvScreencastManager INSTANCE;
    private final UIHandler mUIHandler = new UIHandler(Looper.getMainLooper());
    private final PolyvAllCast mAllCast = new PolyvAllCast();
    // 数据
    private PLVMediaCastDevice lastConnectedDeviceInfo;

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

    private PolyvScreencastManager() {

    }

    public void setUIUpdateListener(PolyvIUIUpdateListener pUIUpdateListener) {
        mUIHandler.setUIUpdateListener(pUIUpdateListener);
    }

    public List<PLVMediaCastDevice> getInfos() {
        return PLVMediaCastManagerAdapterRxJava.getListenerRegistry().getDevices().getValue();
    }

    public PLVMediaCastDevice getConnectInfos() {
        return Nullables.of(new Function0<PLVMediaCastDevice>() {
            @Override
            public PLVMediaCastDevice invoke() {
                return mAllCast.getCastController().getController().getDevice();
            }
        }).getOrNull();
    }

    public void initService() {
        mAllCast.setPlayerListener(mPlayerListener);
    }

    public void browse() {
        mAllCast.browse();
    }

    public PLVMediaCastDevice getLastConnectedDeviceInfo() {
        return lastConnectedDeviceInfo;
    }

    public void playNetMedia(PLVMediaCastDevice device, PLVMediaCastResource resource, int startSeconds) {
        this.lastConnectedDeviceInfo = device;
        mAllCast.playNetMedia(device, resource, startSeconds);
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

    /**
     * 投屏播放监听
     */
    private final IPLVScreencastListener mPlayerListener = new IPLVScreencastListener() {

        @Override
        public void onDeviceScanned(List<PLVMediaCastDevice> devices) {
            mUIHandler.sendMessage(buildTextMessage("设备扫描结束"));
            mUIHandler.sendMessage(buildStateMessage(PolyvIUIUpdateListener.STATE_SEARCH_SUCCESS));
        }

        @Override
        public void onStart() {
            PolyvCommonLog.d(TAG, "onStart:");
            mUIHandler.sendMessage(buildTextMessage("开始播放"));
            mUIHandler.sendMessage(buildStateMessage(PolyvIUIUpdateListener.STATE_PLAY));
        }

        @Override
        public void onPause() {
            PolyvCommonLog.d(TAG, "onPause");
            mUIHandler.sendMessage(buildTextMessage("暂停播放"));
            mUIHandler.sendMessage(buildStateMessage(PolyvIUIUpdateListener.STATE_PAUSE));
        }

        @Override
        public void onStop() {
            PolyvCommonLog.d(TAG, "onStop");
            mUIHandler.sendMessage(buildTextMessage("播放结束"));
            mUIHandler.sendMessage(buildStateMessage(PolyvIUIUpdateListener.STATE_STOP));
        }

        @Override
        public void onComplete() {
            PolyvCommonLog.d(TAG, "onComplete");
            mUIHandler.sendMessage(buildTextMessage("播放结束"));
            mUIHandler.sendMessage(buildStateMessage(PolyvIUIUpdateListener.STATE_COMPLETION));
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
            mUIHandler.sendMessage(buildStateMessage(PolyvIUIUpdateListener.STATE_POSITION_UPDATE, position));
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

    public void release() {
        mAllCast.stopPlay();
    }

}
