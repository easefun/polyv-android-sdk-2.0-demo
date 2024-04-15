package com.easefun.polyvsdk.cast.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apowersoft.dlnasender.api.Constant;
import com.apowersoft.dlnasender.api.bean.DeviceInfo;
import com.apowersoft.dlnasender.api.bean.MediaInfo;
import com.apowersoft.dlnasender.api.listener.DLNADeviceConnectListener;
import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.Video;
import com.easefun.polyvsdk.adapter.PolyvScreencastDeviceListAdapter;
import com.easefun.polyvsdk.cast.PolyvIUIUpdateListener;
import com.easefun.polyvsdk.cast.PolyvScreencastManager;
import com.easefun.polyvsdk.log.PolyvCommonLog;
import com.easefun.polyvsdk.screencast.PolyvScreencastHelper;
import com.easefun.polyvsdk.util.PolyvNetworkUtils;
import com.easefun.polyvsdk.video.PolyvVideoView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PolyvScreencastSearchLayout extends FrameLayout implements View.OnClickListener {
    private static final String TAG = PolyvScreencastSearchLayout.class.getSimpleName();
    private static final String WIFI_AP_STATE_CHANGED_ACTION = "android.net.wifi.WIFI_AP_STATE_CHANGED";
    private OnVisibilityChangedListener onVisibilityChangedListener;
    private LinearLayout ll_search;
    private TextView tv_cancel, tv_wifi_name;
    private ImageView iv_refresh, iv_wifi_icon;
    private RecyclerView rv_devices;
    private PolyvScreencastDeviceListAdapter screencastDeviceListAdapter;
    private Runnable runnable;
    //screencast
    private PolyvScreencastManager screencastManager;
    private NetworkReceiver networkReceiver;
    private PolyvScreencastStatusLayout screencastStatusLayout;
    private boolean isFirstBrowse;
    private UIHandler delayHandler;
    private int currentCastPosition = -1;//当前投屏进度

    private boolean isLandLayout;

    private static class UIHandler extends Handler {
        private WeakReference<PolyvScreencastSearchLayout> reference;

        UIHandler(PolyvScreencastSearchLayout rf) {
            reference = new WeakReference<>(rf);
        }

        @Override
        public void handleMessage(Message msg) {
            PolyvScreencastSearchLayout searchLayout = reference.get();
            if (searchLayout == null) {
                return;
            }
            switch (msg.what) {
                case PolyvIUIUpdateListener.STATE_SEARCH_SUCCESS:
                    searchLayout.updateBrowseAdapter();
                    break;
            }
            super.handleMessage(msg);
        }
    }

    public PolyvScreencastSearchLayout(@NonNull Context context) {
        this(context, null);
    }

    public PolyvScreencastSearchLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PolyvScreencastSearchLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        registerNetworkReceiver();
        refreshWifiName();
    }

    public void setScreencastStatusLayout(PolyvScreencastStatusLayout screencastStatusLayout) {
        this.screencastStatusLayout = screencastStatusLayout;
    }

    private Context getApplicationContext() {
        return getContext().getApplicationContext();
    }

    private void initView() {
        isLandLayout = getAlpha() != 1;
        LayoutInflater.from(getContext()).inflate(!isLandLayout ? R.layout.polyv_screencast_search_layout : R.layout.polyv_screencast_search_layout_land, this);
        ll_search = (LinearLayout) findViewById(R.id.ll_search);
        iv_wifi_icon = (ImageView) findViewById(R.id.iv_wifi_icon);
        tv_wifi_name = (TextView) findViewById(R.id.tv_wifi_name);
        iv_refresh = (ImageView) findViewById(R.id.iv_refresh);
        iv_refresh.setOnClickListener(this);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        if (tv_cancel != null) {
            tv_cancel.setOnClickListener(this);
        }
        rv_devices = (RecyclerView) findViewById(R.id.rv_devices);
        screencastDeviceListAdapter = new PolyvScreencastDeviceListAdapter(rv_devices, !isLandLayout ? R.layout.polyv_recyclerview_device_item : R.layout.polyv_recyclerview_device_item_land);
        rv_devices.setAdapter(screencastDeviceListAdapter);
        screencastDeviceListAdapter.setOnItemClickListener(new PolyvScreencastDeviceListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position, final DeviceInfo info) {
                //检测是否点击的相同的设备
                List<DeviceInfo> connectInfos = null;
                if (null != screencastManager) {
                    connectInfos = screencastManager.getConnectInfos();
                }
                if (null != screencastManager && null != connectInfos && !connectInfos.isEmpty()) {
                    for (DeviceInfo deviceInfo : connectInfos) {
                        if (deviceInfo != null && deviceInfo.getMediaID() != null && deviceInfo.getMediaID().equals(info.getMediaID())) {
                            if (!screencastStatusLayout.isShown()) {
                                screencastStatusLayout.show(deviceInfo);
                                final int currentBitrate = Math.max(1, screencastStatusLayout.getCurrentPlayBitrate());
                                loadInfoAndPlay(currentBitrate);
                            }
                            return;
                        }
                    }
                }

                //重制投屏进度记录的状态
                currentCastPosition = -1;

                removeCallbacks(runnable);

                stop();
                disConnect();
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 延迟连接，避免在连接成功后返回stop回调
                        connect(info);
                        screencastDeviceListAdapter.setSelectInfo(info);
                        screencastDeviceListAdapter.notifyDataSetChanged();

                        screencastStatusLayout.show(info);
                        hide(true);
                    }
                }, 200);
            }
        });
        delayHandler = new UIHandler(this);
    }

    public void setScreencastManager(PolyvScreencastManager screencastManager) {
        this.screencastManager = screencastManager;
    }

    private void registerNetworkReceiver() {
        networkReceiver = new NetworkReceiver(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WIFI_AP_STATE_CHANGED_ACTION);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        getContext().registerReceiver(networkReceiver, intentFilter);
    }

    public void destroy() {
        if (null != networkReceiver) {
            getContext().unregisterReceiver(networkReceiver);
            networkReceiver = null;
        }
    }

    public int getCurrentCastPosition() {
        return currentCastPosition;
    }

    public void show() {
        if (getVisibility() == View.VISIBLE)
            return;
        screencastManager.setUIUpdateListener(iUIUpdateListener);
        screencastManager.setActivityConnectListener(iConnectListener);
        setVisibility(View.VISIBLE);
        if (onVisibilityChangedListener != null) {
            onVisibilityChangedListener.onVisibilityChanged(this, View.VISIBLE);
        }
        //搜索设备
        PolyvScreencastManager.getInstance(getContext()).initService();
        browse();
    }

    public void hide(boolean isStopBrowse) {
        if (getVisibility() != View.VISIBLE)
            return;
        setVisibility(View.GONE);
        if (onVisibilityChangedListener != null) {
            onVisibilityChangedListener.onVisibilityChanged(this, View.GONE);
        }
        if (isStopBrowse) {
            stopBrowse();
        }
    }

    public void stop() {
        List<DeviceInfo> connectInfos = null;
        if (null != screencastManager) {
            connectInfos = screencastManager.getConnectInfos();
        }
        if (null != screencastManager && null != connectInfos && !connectInfos.isEmpty()) {
            PolyvCommonLog.d(TAG, "stop click");
            screencastManager.stopPlay();
        }
//        else {
//            PolyvToastUtil.show(getApplicationContext(), "请先连接设备");
//        }
    }

    public void volumeUp() {
        List<DeviceInfo> connectInfos = null;
        if (null != screencastManager) {
            connectInfos = screencastManager.getConnectInfos();
        }
        if (null != screencastManager && null != connectInfos && !connectInfos.isEmpty()) {
            PolyvCommonLog.d(TAG, "volumeUp click");
            screencastManager.volumeUp();
        } else {
            Toast.makeText(getContext(), "请先连接设备", Toast.LENGTH_SHORT);
        }
    }

    public void volumeDown() {
        List<DeviceInfo> connectInfos = null;
        if (null != screencastManager) {
            connectInfos = screencastManager.getConnectInfos();
        }
        if (null != screencastManager && null != connectInfos && !connectInfos.isEmpty()) {
            PolyvCommonLog.d(TAG, "volumeDown click");
            screencastManager.volumeDown();
        } else {
            Toast.makeText(getContext(), "请先连接设备", Toast.LENGTH_SHORT).show();
        }
    }

    public void disConnect() {
        disConnect(true);
    }

    public void disConnect(boolean isSelectNull) {
        DeviceInfo selectInfo = screencastDeviceListAdapter.getSelectInfo();
        if (null != screencastManager && null != selectInfo) {
            PolyvCommonLog.d(TAG, "disConnect click:" + selectInfo.getName());
            screencastManager.disConnect(selectInfo);
        }

        if (isSelectNull)
            selectNull();
    }

    public void selectNull() {
        screencastDeviceListAdapter.setSelectInfo(null);
        screencastDeviceListAdapter.notifyDataSetChanged();
    }

    public void removeDelayCastRunnable() {
        removeCallbacks(runnable);
    }

    public void refreshWifiName() {
        String netWorkName = PolyvNetworkUtils.getNetworkOperatorName(getApplicationContext());
        boolean isWifi = PolyvNetworkUtils.NETWORK_WIFI == PolyvNetworkUtils.getNetWorkType(getApplicationContext());
        if (isWifi) {
            netWorkName = PolyvNetworkUtils.getWIFISSID(getApplicationContext());
        }
        if ("网络错误".equals(netWorkName)
                || "有线网络".equals(netWorkName)) {
            iv_wifi_icon.setSelected(false);
            tv_wifi_name.setText("当前是非 WIFI 环境，无法使用投屏功能");
            stopBrowse();

            ll_search.setVisibility(View.GONE);
            rv_devices.setVisibility(View.INVISIBLE);
        } else {
            iv_wifi_icon.setSelected(true);
            tv_wifi_name.setText(netWorkName);
            if (getVisibility() == View.VISIBLE || (screencastStatusLayout != null && screencastStatusLayout.isShown())) {
                browse();
            }

            ll_search.setVisibility(View.VISIBLE);
            rv_devices.setVisibility(View.VISIBLE);
        }
    }

    public void reconnectPlay() {
        PolyvCommonLog.d(TAG, "reconnect click:" + screencastManager.getLastConnectedDeviceInfo());
        if (null != screencastManager) {
            PolyvCommonLog.d(TAG, "start connect:" + screencastManager.getLastConnectedDeviceInfo());
            disConnect();
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    screencastManager.connect(screencastManager.getLastConnectedDeviceInfo());
                    screencastDeviceListAdapter.setSelectInfo(screencastManager.getLastConnectedDeviceInfo());
                    screencastDeviceListAdapter.notifyDataSetChanged();
                }
            }, 200);
        } else {
            Toast.makeText(getContext(), "未初始化或未选择设备", Toast.LENGTH_SHORT).show();
        }
    }

    private void connect(DeviceInfo info) {
        PolyvCommonLog.d(TAG, "connect click:" + info.getName());
        if (null != screencastManager) {
            PolyvCommonLog.d(TAG, "start connect:" + info.getName());
            screencastManager.connect(info);
        } else {
            Toast.makeText(getContext(), "未初始化或未选择设备", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateBrowseAdapter() {
        if (null != screencastManager) {
            List<DeviceInfo> connectInfos = screencastManager.getConnectInfos();
            if (connectInfos != null) {
                for (DeviceInfo DeviceInfo : connectInfos) {
                    if (DeviceInfo != null) {
                        screencastDeviceListAdapter.setSelectInfo(DeviceInfo);
                    }
                }
            }

            List<DeviceInfo> infos = screencastManager.getInfos();
            screencastDeviceListAdapter.updateDatas(infos);

            ll_search.setVisibility(View.GONE);
        }
    }

    private void browse() {
        PolyvCommonLog.d(TAG, "btn_browse click");
        String netWorkName = PolyvNetworkUtils.getNetworkOperatorName(getApplicationContext());
        boolean isWifi = PolyvNetworkUtils.NETWORK_WIFI == PolyvNetworkUtils.getNetWorkType(getApplicationContext());
        if (isWifi) {
            netWorkName = PolyvNetworkUtils.getWIFISSID(getApplicationContext());
        }
        if ("网络错误".equals(netWorkName) || "有线网络".equals(netWorkName)) {
            return;
        }
        if (null != screencastManager) {
            if (!isFirstBrowse) {
                isFirstBrowse = true;
            }
            screencastManager.browse();

            ll_search.setVisibility(View.VISIBLE);
        }
    }

    private void stopBrowse() {
        PolyvCommonLog.d(TAG, "btn_stop_browse click");
        if (null != screencastManager) {
            PolyvCommonLog.d(TAG, "stop browse");
            PolyvCommonLog.d(TAG, "stop browse");
            isFirstBrowse = false;
            screencastManager.stopBrowse();

            ll_search.setVisibility(View.GONE);
        }
    }

    private PolyvIUIUpdateListener iUIUpdateListener = new PolyvIUIUpdateListener() {

        @Override
        public void onUpdateState(int state, Object object) {
            PolyvCommonLog.d(TAG, "IUIUpdateListener state:" + state + " text:" + object);
            switch (state) {
                case PolyvIUIUpdateListener.STATE_SEARCH_SUCCESS:
                    if (isFirstBrowse) {
                        isFirstBrowse = false;
                        if (getVisibility() == View.VISIBLE)
                            Toast.makeText(getContext(), "搜索成功", Toast.LENGTH_SHORT).show();
                        PolyvCommonLog.d(TAG, "搜索成功");
                    }
                    if (null != delayHandler) {
                        delayHandler.removeCallbacksAndMessages(null);
                        delayHandler.sendEmptyMessageDelayed(PolyvIUIUpdateListener.STATE_SEARCH_SUCCESS,
                                TimeUnit.MILLISECONDS.toMillis(100));
                    }
                    break;
                case PolyvIUIUpdateListener.STATE_SEARCH_ERROR:
                    if (getVisibility() == View.VISIBLE)
                        Toast.makeText(getContext(), "Auth错误", Toast.LENGTH_SHORT).show();
                    break;
                case PolyvIUIUpdateListener.STATE_SEARCH_NO_RESULT:
                    if (null != delayHandler) {
                        delayHandler.removeCallbacksAndMessages(null);
                        delayHandler.sendEmptyMessageDelayed(PolyvIUIUpdateListener.STATE_SEARCH_SUCCESS,
                                TimeUnit.MILLISECONDS.toMillis(100));
                    }
                    break;
                case PolyvIUIUpdateListener.STATE_CONNECT_SUCCESS:
                    break;
                case PolyvIUIUpdateListener.STATE_DISCONNECT:
                    PolyvCommonLog.d(TAG, "disConnect success:" + object);

                    Toast.makeText(getContext(), (String) object, Toast.LENGTH_SHORT).show();
                    screencastDeviceListAdapter.setSelectInfo(null);
                    screencastDeviceListAdapter.notifyDataSetChanged();
                    break;
                case PolyvIUIUpdateListener.STATE_CONNECT_FAILURE:
                    if (object instanceof String && ((String) object).contains("等待"))
                        return;
                    PolyvCommonLog.d(TAG, "connect failure:" + object);

                    Toast.makeText(getContext(), (String) object, Toast.LENGTH_SHORT).show();
                    screencastDeviceListAdapter.setSelectInfo(null);
                    screencastDeviceListAdapter.notifyDataSetChanged();

                    disConnect();
                    screencastStatusLayout.callPlayErrorStatus();
                    break;
                case PolyvIUIUpdateListener.STATE_PLAY:
                    PolyvCommonLog.d(TAG, "callback play");

                    Toast.makeText(getContext(), "开始播放", Toast.LENGTH_SHORT).show();
                    screencastStatusLayout.callOnStart();
                    break;
                case PolyvIUIUpdateListener.STATE_LOADING:
                    PolyvCommonLog.d(TAG, "callback loading");

                    Toast.makeText(getContext(), "开始加载", Toast.LENGTH_SHORT).show();
                    break;
                case PolyvIUIUpdateListener.STATE_PAUSE:
                    PolyvCommonLog.d(TAG, "callback pause");

                    Toast.makeText(getContext(), "暂停播放", Toast.LENGTH_SHORT).show();
                    screencastStatusLayout.callOnPause();
                    break;
                case PolyvIUIUpdateListener.STATE_STOP:
                    PolyvCommonLog.d(TAG, "callback stop");

                    Toast.makeText(getContext(), "播放结束", Toast.LENGTH_SHORT).show();
                    screencastStatusLayout.hide(true);
                    disConnect();
                    break;
                case PolyvIUIUpdateListener.STATE_SEEK:
                    PolyvCommonLog.d(TAG, "callback seek:" + object);

                    Toast.makeText(getContext(), "seek完成", Toast.LENGTH_SHORT).show();
                    break;
                case PolyvIUIUpdateListener.STATE_PLAY_ERROR:
                    if (object instanceof String && ((String) object).contains("无响应"))
                        return;
                    PolyvCommonLog.d(TAG, "callback error:" + object);
                    Toast.makeText(getContext(), "播放错误：" + object, Toast.LENGTH_SHORT).show();
                    screencastStatusLayout.callPlayErrorStatus();
                    break;
                case PolyvIUIUpdateListener.STATE_POSITION_UPDATE:
                    PolyvCommonLog.d(TAG, "callback position update:" + object);
                    long position = (long) object;
                    long duration = position;
                    Video video = screencastStatusLayout.getVideoView().getVideo();
                    if (video != null) {
                        duration = video.getDuration2Millisecond() / 1000;
                    }
                    PolyvCommonLog.d(TAG, "总长度：" + duration + " 当前进度:" + position);
                    currentCastPosition = (int) position;
                    screencastStatusLayout.callPlayProgress(duration, position);
                    break;
                case PolyvIUIUpdateListener.STATE_COMPLETION:
                    PolyvCommonLog.d(TAG, "callback completion");
                    PolyvCommonLog.d(TAG, "PolyvToastUtil 播放完成");
                    Toast.makeText(getContext(), "播放完成", Toast.LENGTH_SHORT).show();
                    screencastStatusLayout.hide(true);
                    break;
            }
        }

        @Override
        public void onUpdateText(String msg) {
            PolyvCommonLog.d(TAG, "onUpdateText : " + msg + "\n\n");
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(runnable);
    }

    /**
     * 根据连接的设备
     */
    private DLNADeviceConnectListener iConnectListener = new DLNADeviceConnectListener() {
        @Override
        public void onConnect(final DeviceInfo serviceInfo, int extra) {
            if (TextUtils.isEmpty(serviceInfo.getName())) {
                return;
            }
            removeCallbacks(runnable);

            if (screencastDeviceListAdapter.getSelectInfo() == null) {
                return;
            }
            PolyvCommonLog.d(TAG, "connect success:" + serviceInfo.getName() + "连接成功");

            Toast.makeText(getContext(), serviceInfo.getName() + "连接成功", Toast.LENGTH_SHORT).show();
            final int currentBitrate = Math.max(1, screencastStatusLayout.getCurrentPlayBitrate());
            loadInfoAndPlay(currentBitrate);
        }

        @Override
        public void onDisconnect(DeviceInfo serviceInfo, int errorCode) {
            final String text = serviceInfo.getName() + "连接断开";
            post(new Runnable() {
                @Override
                public void run() {
                    screencastStatusLayout.callPlayErrorStatus(text);
                }
            });
        }
    };

    public void seekTo(int progress) {
        screencastManager.seekTo(progress);
    }

    public void pause() {
        List<DeviceInfo> connectInfos = null;
        if (null != screencastManager) {
            connectInfos = screencastManager.getConnectInfos();
        }
        if (screencastManager != null && connectInfos != null && !connectInfos.isEmpty()) {
            PolyvCommonLog.d(TAG, "pause click");
            screencastManager.pause();
        }
    }

    public void resume() {
        List<DeviceInfo> connectInfos = null;
        if (null != screencastManager) {
            connectInfos = screencastManager.getConnectInfos();
        }
        if (null != screencastManager && null != connectInfos && !connectInfos.isEmpty()) {
            PolyvCommonLog.d(TAG, "resume click");
            // 暂停中
            screencastManager.resume();
        }
    }


    public void play(MediaInfo playerInfo, String playPath, int bitrate, int seconds) {
        PolyvCommonLog.d(TAG, "start play url:" + playPath);
        if (screencastStatusLayout.getVideoView().isDisableScreenCAP()) {
            Toast.makeText(getContext(), "防录屏状态下不能投屏", Toast.LENGTH_SHORT).show();
            screencastStatusLayout.callPlayErrorStatus();
            return;
        }
        if (TextUtils.isEmpty(playPath)) {
            Toast.makeText(getContext(), "获取播放地址失败", Toast.LENGTH_SHORT).show();
            screencastStatusLayout.callPlayErrorStatus();
            return;
        }
        if (null == screencastManager) {
            Toast.makeText(getContext(), "未初始化或未选择设备", Toast.LENGTH_SHORT).show();
            screencastStatusLayout.callPlayErrorStatus();
            return;
        }
        List<DeviceInfo> connectInfos = screencastManager.getConnectInfos();
        if (null == connectInfos || connectInfos.isEmpty()) {
            Toast.makeText(getContext(), "请先连接设备", Toast.LENGTH_SHORT).show();
            screencastStatusLayout.callPlayErrorStatus();
            return;
        }
        screencastStatusLayout.callScreencastingStatus(bitrate);

        final MediaInfo.MediaUrl mediaUrl = playerInfo.getMediaUrls().get(0);

        mediaUrl.setMediaID(Base64.encodeToString(playPath.getBytes(), Base64.NO_WRAP));
        mediaUrl.setUri(playPath);
        mediaUrl.setMediaType(Constant.MediaType.VIDEO);
        screencastManager.playNetMedia(playerInfo);
    }

    public void loadInfoAndPlay(final int bitrate) {
        screencastStatusLayout.getVideoView().getPlayPathWithBitRateAsync(bitrate, new PolyvVideoView.Consumer<String>() {
            @Override
            public void accept(final String playPath) {
                if (screencastStatusLayout == null) {
                    return;
                }

                Video video = screencastStatusLayout.getVideoView().getVideo();
                final MediaInfo mediaInfo = new MediaInfo();
                if (video != null) {
                    mediaInfo.setMediaName(video.getTitle());
                }
                final MediaInfo.MediaUrl mediaUrl = new MediaInfo.MediaUrl();
                final List<MediaInfo.MediaUrl> mediaUrlList = new ArrayList<>();
                mediaUrlList.add(mediaUrl);
                mediaInfo.setMediaUrls(mediaUrlList);

                PolyvScreencastHelper.getInstance().transformPlayObject(mediaUrl, video,
                        bitrate, playPath, new PolyvScreencastHelper.PolyvCastTransformCallback() {
                            @Override
                            public void onSucceed(Object object, String newPlayPath) {
                                PolyvCommonLog.d(TAG, "cast: " + newPlayPath);
                                int videoPosition = screencastStatusLayout.getCurrentPlayPosition();
                                mediaUrlList.clear();
                                mediaUrlList.add((MediaInfo.MediaUrl) object);
                                play(mediaInfo, newPlayPath, bitrate, videoPosition);
                                screencastStatusLayout.resetBitRateView(bitrate);
                            }

                            @Override
                            public void onFailed(Throwable e) {
                                e.printStackTrace();
                                Toast.makeText(getContext(), e.getMessage() + "", Toast.LENGTH_SHORT).show();
                                screencastStatusLayout.callPlayErrorStatus(e.getMessage() + "");
                            }
                        });


            }
        });
    }

    private static class NetworkReceiver extends BroadcastReceiver {
        private WeakReference<PolyvScreencastSearchLayout> reference;

        public NetworkReceiver(PolyvScreencastSearchLayout rf) {
            reference = new WeakReference<>(rf);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (null == reference || null == reference.get()) {
                return;
            }
            PolyvScreencastSearchLayout searchLayout = reference.get();
            String action = intent.getAction();
            if (ConnectivityManager.CONNECTIVITY_ACTION.equalsIgnoreCase(action) ||
                    PolyvScreencastSearchLayout.WIFI_AP_STATE_CHANGED_ACTION.equalsIgnoreCase(action)) {
                searchLayout.refreshWifiName();
            }
        }
    }

    public void setOnVisibilityChangedListener(OnVisibilityChangedListener onVisibilityChangedListener) {
        this.onVisibilityChangedListener = onVisibilityChangedListener;
    }

    public interface OnVisibilityChangedListener {
        void onVisibilityChanged(@NonNull View changedView, int visibility);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                hide(true);
                break;
            case R.id.iv_refresh:
                browse();
                break;
        }
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (!isLandLayout) {
                hide(true);
            }
        } else if (isLandLayout) {
            hide(true);
        }
    }
}
