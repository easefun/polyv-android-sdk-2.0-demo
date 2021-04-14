package com.easefun.polyvsdk.cast.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.Video;
import com.easefun.polyvsdk.adapter.PolyvScreencastDeviceListAdapter;
import com.easefun.polyvsdk.cast.PolyvAllCast;
import com.easefun.polyvsdk.cast.PolyvIUIUpdateListener;
import com.easefun.polyvsdk.cast.PolyvScreencastManager;
import com.easefun.polyvsdk.log.PolyvCommonLog;
import com.easefun.polyvsdk.screencast.PolyvScreencastHelper;
import com.easefun.polyvsdk.util.PolyvNetworkUtils;
import com.easefun.polyvsdk.video.PolyvVideoView;
import com.hpplay.common.utils.NetworkUtil;
import com.hpplay.sdk.source.api.IConnectListener;
import com.hpplay.sdk.source.api.LelinkPlayerInfo;
import com.hpplay.sdk.source.browse.api.LelinkServiceInfo;

import java.lang.ref.WeakReference;
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
            public void onClick(int position, LelinkServiceInfo info) {
                //检测是否点击的相同的设备
                List<LelinkServiceInfo> connectInfos = null;
                if (null != screencastManager) {
                    connectInfos = screencastManager.getConnectInfos();
                }
                if (null != screencastManager && null != connectInfos && !connectInfos.isEmpty()) {
                    for (LelinkServiceInfo lelinkServiceInfo : connectInfos) {
                        if (lelinkServiceInfo != null && lelinkServiceInfo.getUid() != null && lelinkServiceInfo.getUid().equals(info.getUid())) {
                            if (!screencastStatusLayout.isShown()) {
                                screencastStatusLayout.show(lelinkServiceInfo);
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
                connect(info);
                screencastDeviceListAdapter.setSelectInfo(info);
                screencastDeviceListAdapter.notifyDataSetChanged();

                screencastStatusLayout.show(info);
                hide(true);
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

    public String getCurrentPlayPath() {
        return screencastManager.getCurrentPlayPath();
    }

    public int getCurrentCastPosition() {
        return currentCastPosition;
    }

    public void show() {
        if (getVisibility() == View.VISIBLE)
            return;
        screencastManager.setUIUpdateListener((com.easefun.polyvsdk.cast.PolyvIUIUpdateListener) iUIUpdateListener);
        screencastManager.setActivityConenctListener(iConnectListener);
        setVisibility(View.VISIBLE);
        if (onVisibilityChangedListener != null) {
            onVisibilityChangedListener.onVisibilityChanged(this, View.VISIBLE);
        }
        //搜索设备
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
        List<LelinkServiceInfo> connectInfos = null;
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

    public void voulumeUp() {
        List<LelinkServiceInfo> connectInfos = null;
        if (null != screencastManager) {
            connectInfos = screencastManager.getConnectInfos();
        }
        if (null != screencastManager && null != connectInfos && !connectInfos.isEmpty()) {
            PolyvCommonLog.d(TAG, "volumeUp click");
            screencastManager.voulumeUp();
        } else {
            Toast.makeText(getContext(), "请先连接设备", Toast.LENGTH_SHORT);
        }
    }

    public void voulumeDown() {
        List<LelinkServiceInfo> connectInfos = null;
        if (null != screencastManager) {
            connectInfos = screencastManager.getConnectInfos();
        }
        if (null != screencastManager && null != connectInfos && !connectInfos.isEmpty()) {
            PolyvCommonLog.d(TAG, "volumeDown click");
            screencastManager.voulumeDown();
        } else {
            Toast.makeText(getContext(), "请先连接设备", Toast.LENGTH_SHORT).show();
        }
    }

    public void disConnect() {
        disConnect(true);
    }

    public void disConnect(boolean isSelectNull) {
        LelinkServiceInfo selectInfo = screencastDeviceListAdapter.getSelectInfo();
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
        String netWorkName = NetworkUtil.getNetWorkName(getApplicationContext());
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
            if (getVisibility() == View.VISIBLE) {
                browse();
            }

            ll_search.setVisibility(View.VISIBLE);
            rv_devices.setVisibility(View.VISIBLE);
        }
    }

    public void reconnectPlay() {
        PolyvCommonLog.d(TAG, "reconnect click:" + screencastManager.getLastServiceInfo());
        if (null != screencastManager) {
            PolyvCommonLog.d(TAG, "start connect:" + screencastManager.getLastServiceInfo());
            disConnect();
            screencastManager.connect(screencastManager.getLastServiceInfo());
            screencastDeviceListAdapter.setSelectInfo(screencastManager.getLastServiceInfo());
            screencastDeviceListAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(getContext(), "未初始化或未选择设备", Toast.LENGTH_SHORT).show();
        }
    }

    private void connect(LelinkServiceInfo info) {
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
            List<LelinkServiceInfo> connectInfos = screencastManager.getConnectInfos();
            if (connectInfos != null) {
                for (LelinkServiceInfo lelinkServiceInfo : connectInfos) {
                    if (lelinkServiceInfo != null) {
                        screencastDeviceListAdapter.setSelectInfo(lelinkServiceInfo);
                    }
                }
            }

            List<LelinkServiceInfo> infos = screencastManager.getInfos();
            screencastDeviceListAdapter.updateDatas(infos);

            ll_search.setVisibility(View.GONE);
        }
    }

    private void browse() {
        PolyvCommonLog.d(TAG, "btn_browse click");
        String netWorkName = NetworkUtil.getNetWorkName(getApplicationContext());
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
                    long[] arr = (long[]) object;
                    long duration = arr[0];
                    long position = arr[1];
                    PolyvCommonLog.d(TAG, "PolyvToastUtil 总长度：" + duration + " 当前进度:" + position);
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
    private IConnectListener iConnectListener = new IConnectListener() {
        @Override
        public void onConnect(final LelinkServiceInfo serviceInfo, int extra) {
            if (TextUtils.isEmpty(serviceInfo.getName())) {
                // pin码，则全部去掉
                return;
            }
            removeCallbacks(runnable);
            postDelayed(runnable = new Runnable() {
                @Override
                public void run() {
                    if (screencastDeviceListAdapter.getSelectInfo() == null)
                        return;
                    PolyvCommonLog.d(TAG, "connect success:" + serviceInfo.getName() + "连接成功");

                    Toast.makeText(getContext(), serviceInfo.getName() + "连接成功", Toast.LENGTH_SHORT).show();
                    final int currentBitrate = Math.max(1, screencastStatusLayout.getCurrentPlayBitrate());
                    loadInfoAndPlay(currentBitrate);
                }
            }, 3000);//由于内部还有连接，所以这里延迟3秒
        }

        @Override
        public void onDisconnect(LelinkServiceInfo serviceInfo, int what, int extra) {
            String text = null;
            if (what == IConnectListener.WHAT_HARASS_WAITING) {// 防骚扰，等待用户确认
                // 乐播投屏防骚扰等待消息，请开发者务必处理该消息
                final String finalText = serviceInfo.getName() + "等待用户确认";
                post(new Runnable() {
                    @Override
                    public void run() {
                        screencastStatusLayout.callScreencastStatusTitle(finalText);
                    }
                });
                return;
            } else if (what == IConnectListener.WHAT_DISCONNECT) {
                switch (extra) {
                    case IConnectListener.EXTRA_HARASS_REJECT:// 防骚扰，用户拒绝投屏
                        text = serviceInfo.getName() + "连接被拒绝";
                        break;
                    case IConnectListener.EXTRA_HARASS_TIMEOUT:// 防骚扰，用户响应超时
                        text = serviceInfo.getName() + "防骚扰响应超时";
                        break;
                    case IConnectListener.EXTRA_HARASS_BLACKLIST:// 防骚扰，该用户被加入黑名单
                        text = serviceInfo.getName() + "已被加入投屏黑名单";
                        break;
                    default:
                        text = serviceInfo.getName() + "连接断开";
                        break;
                }
            } else if (what == IConnectListener.WHAT_CONNECT_FAILED) {
                switch (extra) {
                    case IConnectListener.EXTRA_CONNECT_DEVICE_OFFLINE:
                        text = serviceInfo.getName() + "不在线";
                        break;
                    default:
                        text = serviceInfo.getName() + "连接失败";
                        break;
                }
            }
            final String finalText = text;
            post(new Runnable() {
                @Override
                public void run() {
                    screencastStatusLayout.callPlayErrorStatus(finalText);
                }
            });

        }
    };

    public void seekTo(int progress) {
        screencastManager.seekTo(progress);
    }

    public void pause() {
        List<LelinkServiceInfo> connectInfos = null;
        if (null != screencastManager) {
            connectInfos = screencastManager.getConnectInfos();
        }
        if (null != screencastManager && null != connectInfos && !connectInfos.isEmpty()) {
            PolyvCommonLog.d(TAG, "pause click");
            screencastManager.pause();
        }
    }

    public void resume() {
        List<LelinkServiceInfo> connectInfos = null;
        if (null != screencastManager) {
            connectInfos = screencastManager.getConnectInfos();
        }
        if (null != screencastManager && null != connectInfos && !connectInfos.isEmpty()) {
            PolyvCommonLog.d(TAG, "resume click");
            // 暂停中
            screencastManager.resume();
        }
    }


    public void play(LelinkPlayerInfo playerInfo, String playPath, int bitrate, int seconds) {
        PolyvCommonLog.d(TAG, "start play url:" + playPath + " type:" + PolyvAllCast.MEDIA_TYPE_VIDEO);
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
        List<LelinkServiceInfo> connectInfos = screencastManager.getConnectInfos();
        if (null == connectInfos || connectInfos.isEmpty()) {
            Toast.makeText(getContext(), "请先连接设备", Toast.LENGTH_SHORT).show();
            screencastStatusLayout.callPlayErrorStatus();
            return;
        }
        screencastStatusLayout.callScreencastingStatus(bitrate);
        screencastManager.playNetMedia(playerInfo, playPath, PolyvAllCast.MEDIA_TYPE_VIDEO, "", seconds);

    }

    public void loadInfoAndPlay(final int bitrate) {
        screencastStatusLayout.getVideoView().getPlayPathWithBitRateAsync(bitrate, new PolyvVideoView.Consumer<String>() {
            @Override
            public void accept(final String playPath) {
                if (screencastStatusLayout == null) {
                    return;
                }

                Video video = screencastStatusLayout.getVideoView().getVideo();
                LelinkPlayerInfo lelinkPlayerInfo = new LelinkPlayerInfo();
                PolyvScreencastHelper.getInstance().transformPlayObject(lelinkPlayerInfo, video,
                        bitrate, playPath, new PolyvScreencastHelper.PolyvCastTransformCallback() {
                            @Override
                            public void onSucceed(Object object, String newPlayPath) {
                                PolyvCommonLog.d(TAG, "cast: " + playPath);
                                int videoPosition = screencastStatusLayout.getCurrentPlayPosition();
                                play((LelinkPlayerInfo) object, newPlayPath, bitrate, videoPosition);
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
