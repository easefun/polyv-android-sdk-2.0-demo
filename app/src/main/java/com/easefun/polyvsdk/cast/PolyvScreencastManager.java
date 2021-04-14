package com.easefun.polyvsdk.cast;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.easefun.polyvsdk.log.PolyvCommonLog;
import com.hpplay.common.utils.LeLog;
import com.hpplay.sdk.source.api.BuildConfig;
import com.hpplay.sdk.source.api.IConnectListener;
import com.hpplay.sdk.source.api.ILelinkPlayerListener;
import com.hpplay.sdk.source.api.LelinkPlayerInfo;
import com.hpplay.sdk.source.browse.api.IAPI;
import com.hpplay.sdk.source.browse.api.IBrowseListener;
import com.hpplay.sdk.source.browse.api.LelinkServiceInfo;

import java.util.List;

/**
 * 投屏封装工具类
 * 封装了乐播投屏的基本使用，开发者在使用投屏时可直接集成
 */
public class PolyvScreencastManager {

    private static final String TAG = "PolyvScreencastHelper";

    private static String APPKEY = "";
    private static String APPSECRET = "";

    private static PolyvScreencastManager sLelinkHelper;
    private Context mContext;
    private UIHandler mUIHandler;
    private PolyvAllCast mAllCast;
    // 数据
    private List<LelinkServiceInfo> mInfos;


    // 监听器
    private IConnectListener mActivityConnectListener;

    public static PolyvScreencastManager getInstance(Context context) {
        if (sLelinkHelper == null) {
            sLelinkHelper = new PolyvScreencastManager(context);
        }
        return sLelinkHelper;
    }

    private String currentPlayPath;
    private LelinkServiceInfo serviceInfo, lastServiceInfo;


    public static void init(String appKey, String appSecret) {
        APPKEY = appKey;
        APPSECRET = appSecret;
        LeLog.enableTrace(false);
    }

    public static boolean isInited() {
        return !(TextUtils.isEmpty(APPKEY) || TextUtils.isEmpty(APPSECRET));
    }

    private PolyvScreencastManager(Context context) {
        mContext = context;
        mUIHandler = new UIHandler(Looper.getMainLooper());
        mAllCast = new PolyvAllCast(context.getApplicationContext(), APPKEY, APPSECRET);
        mAllCast.setOnBrowseListener(mBrowseListener);
        mAllCast.setConnectListener(mConnectListener);
        mAllCast.setPlayerListener(mPlayerListener);
    }

    public String getCurrentPlayPath() {
        return currentPlayPath;
    }

    public void setUIUpdateListener(PolyvIUIUpdateListener pUIUpdateListener) {
        mUIHandler.setUIUpdateListener(pUIUpdateListener);
    }

    public void setActivityConenctListener(IConnectListener connectListener) {
        this.mActivityConnectListener = connectListener;
    }

    public List<LelinkServiceInfo> getInfos() {
        return mInfos;
    }

    public List<LelinkServiceInfo> getConnectInfos() {
        return mAllCast.getConnectInfos();
    }

    public void addPinCodeServiceInfo(String pinCode) {
        mAllCast.addPinCodeServiceInfo(pinCode);
    }


    public void browse() {
        mAllCast.browse();
    }

    public void stopBrowse() {
        mAllCast.stopBrowse();
    }

    public void connect(LelinkServiceInfo info) {
        if (null != mUIHandler) {
            mUIHandler.sendMessage(buildTextMessage("选中了:" + info.getName()
                    + " type:" + info.getTypes()));
        }
        mAllCast.connect(info);
        serviceInfo = info;
        lastServiceInfo = info;
    }

    public LelinkServiceInfo getLastServiceInfo() {
        return lastServiceInfo;
    }

    public void disConnect(LelinkServiceInfo info) {
        mAllCast.disConnect(info);
        serviceInfo = null;
    }



    public boolean canPlayMedia(LelinkServiceInfo info){
        return mAllCast.canPlayMedia(info);
    }

    public void playLocalMedia(String url, int mediaType, String screencode) {
        currentPlayPath = url;
        mAllCast.playLocalMedia(url, mediaType, screencode);
    }

    public void playNetMedia(String url, int mediaType, String screencode) {
        currentPlayPath = url;
        mAllCast.playNetMedia(url, mediaType, screencode);
    }

    public void playNetMediaWithPosition(String url, int mediaType, int seconds) {
        currentPlayPath = url;
        mAllCast.playNetMediaWithPosition(url, mediaType, seconds);
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

    public void voulumeUp() {
        mAllCast.voulumeUp();
    }

    public void voulumeDown() {
        mAllCast.voulumeDown();
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
     * 投屏搜索监听
     */
    private IBrowseListener mBrowseListener = new IBrowseListener() {

        @Override
        public void onBrowse(int resultCode, List<LelinkServiceInfo> list) {
            PolyvCommonLog.d(TAG, "onSuccess size:" + (list == null ? 0 : list.size()));
            mInfos = list;
            if (resultCode == IBrowseListener.BROWSE_SUCCESS) {
                PolyvCommonLog.d(TAG, "browse success");
                StringBuffer buffer = new StringBuffer();
                if (null != mInfos) {
                    for (LelinkServiceInfo info : mInfos) {
                        buffer.append("name：").append(info.getName())
                                .append(" uid: ").append(info.getUid())
                                .append(" type:").append(info.getTypes()).append("\n");
                    }
                    buffer.append("---------------------------\n");
                    if (null != mUIHandler) {
                        // 发送文本信息
                        mUIHandler.sendMessage(buildTextMessage(buffer.toString()));
                        if (mInfos.isEmpty()) {
                            mUIHandler.sendMessage(buildStateMessage(PolyvIUIUpdateListener.STATE_SEARCH_NO_RESULT));
                        } else {
                            mUIHandler.sendMessage(buildStateMessage(PolyvIUIUpdateListener.STATE_SEARCH_SUCCESS));
                        }
                    }
                }
            } else {
                if(resultCode == IBrowseListener.BROWSE_STOP){
                    return;
                }
                if (null != mUIHandler) {
                    // 发送文本信息
                    PolyvCommonLog.d(TAG, "browse error:Auth error");
                    String text = "";
                    if(resultCode == IBrowseListener.BROWSE_ERROR_AUTH){
                        text = "授权失败";
                    } else if(resultCode == IBrowseListener.BROWSE_ERROR_AUTH_TIME){
                        text = "授权失败次数超限";
                    } else {
                        text = "搜索错误";
                    }
                    mUIHandler.sendMessage(buildTextMessage(text));
                    mUIHandler.sendMessage(buildStateMessage(PolyvIUIUpdateListener.STATE_SEARCH_ERROR));
                }
            }

        }

    };

    /**
     * 投屏连接状态监听
     */
    private IConnectListener mConnectListener = new IConnectListener() {

        @Override
        public void onConnect(final LelinkServiceInfo serviceInfo, final int extra) {
            PolyvCommonLog.d(TAG, "onConnect:" + serviceInfo.getName());
            if (null != mUIHandler) {
                String type = extra == TYPE_LELINK ? "Lelink" : extra == TYPE_DLNA ? "DLNA" : extra == TYPE_NEW_LELINK ? "NEW_LELINK" : "IM";
                String text;
                if (TextUtils.isEmpty(serviceInfo.getName())) {
                    text = "pin码连接" + type + "成功";
                } else {
                    text = serviceInfo.getName() + "连接" + type + "成功";
                }
                mUIHandler.sendMessage(buildTextMessage(text));
                mUIHandler.sendMessage(buildStateMessage(PolyvIUIUpdateListener.STATE_CONNECT_SUCCESS, text));
            }
            mUIHandler.post(new Runnable() {

                @Override
                public void run() {
                    if (mActivityConnectListener != null) {
                        mActivityConnectListener.onConnect(serviceInfo, extra);
                    }
                }

            });
        }

        @Override
        public void onDisconnect(LelinkServiceInfo serviceInfo, int what, int extra) {
            PolyvCommonLog.d(TAG, "onDisconnect:" + serviceInfo.getName() + " disConnectType:" + what + " extra:" + extra);
            if (what == IConnectListener.CONNECT_INFO_DISCONNECT) {
                if (null != mUIHandler) {
                    String text;
                    if (TextUtils.isEmpty(serviceInfo.getName())) {
                        text = "pin码连接断开";
                    } else {
                        text = serviceInfo.getName() + "连接断开";
                    }
                    mUIHandler.sendMessage(buildTextMessage(text));
                    mUIHandler.sendMessage(buildStateMessage(PolyvIUIUpdateListener.STATE_DISCONNECT, text));
                }
            } else if (what == IConnectListener.CONNECT_ERROR_FAILED) {
                String text = null;
                if (extra == IConnectListener.CONNECT_ERROR_IO) {
                    text = serviceInfo.getName() + "连接失败";
                } else if (extra == IConnectListener.CONNECT_ERROR_IM_WAITTING) {
                    text = serviceInfo.getName() + "等待确认";
                } else if (extra == IConnectListener.CONNECT_ERROR_IM_REJECT) {
                    text = serviceInfo.getName() + "连接拒绝";
                } else if (extra == IConnectListener.CONNECT_ERROR_IM_TIMEOUT) {
                    text = serviceInfo.getName() + "连接超时";
                } else if (extra == IConnectListener.CONNECT_ERROR_IM_BLACKLIST) {
                    text = serviceInfo.getName() + "连接黑名单";
                }
                if (null != mUIHandler) {
                    mUIHandler.sendMessage(buildTextMessage(text));
                    mUIHandler.sendMessage(buildStateMessage(PolyvIUIUpdateListener.STATE_CONNECT_FAILURE, text));
                }
            }
            if (mActivityConnectListener != null) {
                mActivityConnectListener.onDisconnect(serviceInfo, what, extra);
            }
        }

    };

    /**
     * 投屏播放监听
     */
    private ILelinkPlayerListener mPlayerListener = new ILelinkPlayerListener() {

        @Override
        public void onLoading() {
            if (null != mUIHandler) {
                mUIHandler.sendMessage(buildTextMessage("开始加载"));
                mUIHandler.sendMessage(buildStateMessage(PolyvIUIUpdateListener.STATE_LOADING));
            }
        }

        @Override
        public void onStart() {
            PolyvCommonLog.d(TAG, "onStart:");
            if (null != mUIHandler) {
                mUIHandler.sendMessage(buildTextMessage("开始播放"));
                mUIHandler.sendMessage(buildStateMessage(PolyvIUIUpdateListener.STATE_PLAY));
            }
        }

        @Override
        public void onPause() {
            PolyvCommonLog.d(TAG, "onPause");
            if (null != mUIHandler) {
                mUIHandler.sendMessage(buildTextMessage("暂停播放"));
                mUIHandler.sendMessage(buildStateMessage(PolyvIUIUpdateListener.STATE_PAUSE));
            }
        }

        @Override
        public void onCompletion() {
            PolyvCommonLog.d(TAG, "onCompletion");
            if (null != mUIHandler) {
                mUIHandler.sendMessage(buildTextMessage("播放完成"));
                mUIHandler.sendMessage(buildStateMessage(PolyvIUIUpdateListener.STATE_COMPLETION));
            }
        }

        @Override
        public void onStop() {
            PolyvCommonLog.d(TAG, "onStop");
            if (null != mUIHandler) {
                //polyv
                List<LelinkServiceInfo> connectInfos = getConnectInfos();
                if (connectInfos == null || connectInfos.isEmpty()) {
                    return;
                }
                mUIHandler.sendMessage(buildTextMessage("播放结束"));
                mUIHandler.sendMessage(buildStateMessage(PolyvIUIUpdateListener.STATE_STOP));
            }
        }

        @Override
        public void onSeekComplete(int pPosition) {
            PolyvCommonLog.d(TAG, "onSeekComplete position:" + pPosition);
            mUIHandler.sendMessage(buildTextMessage("设置进度"));
            mUIHandler.sendMessage(buildStateMessage(PolyvIUIUpdateListener.STATE_SEEK));
        }

        @Override
        public void onInfo(int what, int extra) {
            PolyvCommonLog.d(TAG, "onInfo what:" + what + " extra:" + extra);
        }

        @Override
        public void onInfo(int what, String s) {
            PolyvCommonLog.d(TAG, "onInfo what:" + what + " extra:" + s);
        }

        @Override
        public void onError(int what, int extra) {
            PolyvCommonLog.d(TAG, "onError what:" + what + " extra:" + extra);
            String text = null;
            if (what == ILelinkPlayerListener.PUSH_ERROR_INIT) {
                if (extra == ILelinkPlayerListener.PUSH_ERRROR_FILE_NOT_EXISTED) {
                    text = "文件不存在";
                } else if (extra == ILelinkPlayerListener.PUSH_ERROR_IM_OFFLINE) {
                    text = "IM TV不在线";
                } else if (extra == ILelinkPlayerListener.PUSH_ERROR_IMAGE) {

                } else if (extra == ILelinkPlayerListener.PUSH_ERROR_IM_UNSUPPORTED_MIMETYPE) {
                    text = "IM不支持的媒体类型";
                } else {
                    text = "未知";
                }
            } else if (what == ILelinkPlayerListener.MIRROR_ERROR_INIT) {
                if (extra == ILelinkPlayerListener.MIRROR_ERROR_UNSUPPORTED) {
                    text = "不支持镜像";
                } else if (extra == ILelinkPlayerListener.MIRROR_ERROR_REJECT_PERMISSION) {
                    text = "镜像权限拒绝";
                } else if (extra == ILelinkPlayerListener.MIRROR_ERROR_DEVICE_UNSUPPORTED) {
                    text = "设备不支持镜像";
                } else if (extra == ILelinkPlayerListener.NEED_SCREENCODE) {
                    text = "请输入投屏码";
                }
            } else if (what == ILelinkPlayerListener.MIRROR_ERROR_PREPARE) {
                if (extra == ILelinkPlayerListener.MIRROR_ERROR_GET_INFO) {
                    text = "获取镜像信息出错";
                } else if (extra == ILelinkPlayerListener.MIRROR_ERROR_GET_PORT) {
                    text = "获取镜像端口出错";
                } else if (extra == ILelinkPlayerListener.NEED_SCREENCODE) {
                    text = "请输入投屏码";
                    mUIHandler.sendMessage(buildTextMessage(text));
                    mUIHandler.sendMessage(buildStateMessage(PolyvIUIUpdateListener.STATE_INPUT_SCREENCODE, text));
                    return;
                } else if (extra == PREEMPT_UNSUPPORTED) {
                    text = "投屏码模式不支持抢占";
                }
            } else if (what == ILelinkPlayerListener.PUSH_ERROR_PLAY) {
                if (extra == ILelinkPlayerListener.PUSH_ERROR_NOT_RESPONSED) {
                    text = "播放无响应";
                } else if (extra == ILelinkPlayerListener.NEED_SCREENCODE) {
                    text = "请输入投屏码";
                    mUIHandler.sendMessage(buildTextMessage(text));
                    mUIHandler.sendMessage(buildStateMessage(PolyvIUIUpdateListener.STATE_INPUT_SCREENCODE, text));
                    return;
                } else if (extra == ILelinkPlayerListener.RELEVANCE_DATA_UNSUPPORTED) {
                    text = "老乐联不支持数据透传,请升级接收端的版本！";
                    mUIHandler.sendMessage(buildTextMessage(text));
                    mUIHandler.sendMessage(buildStateMessage(PolyvIUIUpdateListener.RELEVANCE_DATA_UNSUPPORT, text));
                    return;
                } else if (extra == ILelinkPlayerListener.PREEMPT_UNSUPPORTED) {
                    text = "投屏码模式不支持抢占";
                }
            } else if (what == ILelinkPlayerListener.PUSH_ERROR_STOP) {
                if (extra == ILelinkPlayerListener.PUSH_ERROR_NOT_RESPONSED) {
                    text = "退出 播放无响应";
                }
            } else if (what == ILelinkPlayerListener.PUSH_ERROR_PAUSE) {
                if (extra == ILelinkPlayerListener.PUSH_ERROR_NOT_RESPONSED) {
                    text = "暂停无响应";
                }
            } else if (what == ILelinkPlayerListener.PUSH_ERROR_RESUME) {
                if (extra == ILelinkPlayerListener.PUSH_ERROR_NOT_RESPONSED) {
                    text = "恢复无响应";
                }
            } else if (what == MIRROR_PLAY_ERROR) {
                if (extra == MIRROR_ERROR_FORCE_STOP) {
                    text = "接收端断开";
                } else if (extra == MIRROR_ERROR_PREEMPT_STOP) {
                    text = "镜像被抢占";
                }
            } else if (what == MIRROR_ERROR_CODEC) {
                if (extra == MIRROR_ERROR_NETWORK_BROKEN) {
                    text = "镜像网络断开";
                }
            } else if (what == ILelinkPlayerListener.AUTH_ERROR) {
                text = "授权失败";
                if (extra == ILelinkPlayerListener.AUTH_ERROR_TIME_OUT) {
                    text = "授权失败，授权次数超限";
                }
            }
            mUIHandler.sendMessage(buildTextMessage(text));
            mUIHandler.sendMessage(buildStateMessage(PolyvIUIUpdateListener.STATE_PLAY_ERROR, text));
        }

        /**
         * 音量变化回调
         *
         * @param percent 当前音量
         */
        @Override
        public void onVolumeChanged(float percent) {
            PolyvCommonLog.d(TAG, "onVolumeChanged percent:" + percent);
        }

        /**
         * 进度更新回调
         *
         * @param duration 媒体资源总长度
         * @param position 当前进度
         */
        @Override
        public void onPositionUpdate(long duration, long position) {
            PolyvCommonLog.d(TAG, "onPositionUpdate duration:" + duration + " position:" + position);
            long[] arr = new long[]{duration, position};
            if (null != mUIHandler) {
                mUIHandler.sendMessage(buildStateMessage(PolyvIUIUpdateListener.STATE_POSITION_UPDATE, arr));
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

    public void playNetMediaAndPassthHeader(String url, int type) {
        currentPlayPath = url;
        mAllCast.playNetMediaWithHeader(url, type);
    }

    public void playNetMediaAndPassthMediaAsset(String url, int type) {
        currentPlayPath = url;
        mAllCast.playNetMediaWithAsset(url, type);
    }

    public void startWithLoopMode(String url, boolean isLocalFile) {
        currentPlayPath = url;
        mAllCast.startWithLoopMode(url, isLocalFile);
    }

    public void startNetVideoWith3rdMonitor(String netVideoUrl) {
        currentPlayPath = netVideoUrl;
        mAllCast.startNetVideoWith3rdMonitor(netVideoUrl);
    }


    public void startMirror(Activity activity, LelinkServiceInfo info, int resolutionLevel,
                            int bitrateLevel, boolean audioEnable, String screencode) {
        mAllCast.startMirror(activity, info, resolutionLevel, bitrateLevel, audioEnable, screencode);
    }

    public void playNetMedia(LelinkPlayerInfo lelinkPlayerInfo, String playPath, int type, String screenCode) {
        playNetMedia(lelinkPlayerInfo, playPath,type, screenCode, 0);
    }

    public void playNetMedia(LelinkPlayerInfo lelinkPlayerInfo, String playPath, int type, String screenCode, int seconds) {
        currentPlayPath = playPath;
        lelinkPlayerInfo.setType(type);
        lelinkPlayerInfo.setUrl(playPath);
        String userAgent = "PolyvAndroidScreencast-lelink" + BuildConfig.VERSION_NAME;
        lelinkPlayerInfo.setHeader("{\"user-agent\":\" " + userAgent + "\"}");
        lelinkPlayerInfo.setLoopMode(LelinkPlayerInfo.LOOP_MODE_DEFAULT);
        lelinkPlayerInfo.setOption(IAPI.OPTION_6, screenCode);
        lelinkPlayerInfo.setStartPosition(seconds);
        mAllCast.playNetMediaWithHeader(lelinkPlayerInfo);
    }
    // </editor-fold >





    public void release() {
        mAllCast.stopBrowse();
        mAllCast.stopPlay();
        if (serviceInfo != null) {
            disConnect(serviceInfo);
        }
    }

}
