package com.easefun.polyvsdk.view;

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

import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.adapter.PolyvScreencastDeviceListAdapter;
import com.easefun.polyvsdk.screencast.PolyvAllCast;
import com.easefun.polyvsdk.screencast.PolyvIUIUpdateListener;
import com.easefun.polyvsdk.screencast.PolyvScreencastHelper;
import com.easefun.polyvsdk.screencast.utils.PolyvLogger;
import com.easefun.polyvsdk.screencast.utils.PolyvToastUtil;
import com.easefun.polyvsdk.util.PolyvNetworkUtils;
import com.easefun.polyvsdk.video.PolyvVideoType;
import com.easefun.polyvsdk.vo.PolyvVideoVO;
import com.hpplay.common.utils.NetworkUtil;
import com.hpplay.sdk.source.api.IConnectListener;
import com.hpplay.sdk.source.browse.api.ILelinkServiceManager;
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
    private PolyvScreencastHelper screencastHelper;
    private NetworkReceiver networkReceiver;
    private PolyvScreencastStatusLayout screencastStatusLayout;
    private boolean isFirstBrowse;
    private UIHandler delayHandler;

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
                if (null != screencastHelper) {
                    connectInfos = screencastHelper.getConnectInfos();
                }
                if (null != screencastHelper && null != connectInfos && !connectInfos.isEmpty()) {
                    for (LelinkServiceInfo lelinkServiceInfo : connectInfos) {
                        if (lelinkServiceInfo != null && lelinkServiceInfo.getUid() != null && lelinkServiceInfo.getUid().equals(info.getUid())) {
                            return;
                        }
                    }
                }

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

    public void setScreencastHelper(PolyvScreencastHelper screencastHelper) {
        this.screencastHelper = screencastHelper;
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
        return screencastHelper.getCurrentPlayPath();
    }

    public void show() {
        if (getVisibility() == View.VISIBLE)
            return;
        screencastHelper.setUIUpdateListener(iUIUpdateListener);
        screencastHelper.setActivityConenctListener(iConnectListener);
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
        if (null != screencastHelper) {
            connectInfos = screencastHelper.getConnectInfos();
        }
        if (null != screencastHelper && null != connectInfos && !connectInfos.isEmpty()) {
            PolyvLogger.test(TAG, "stop click");
            screencastHelper.stop();
        }
//        else {
//            PolyvToastUtil.show(getApplicationContext(), "请先连接设备");
//        }
    }

    public void voulumeUp() {
        List<LelinkServiceInfo> connectInfos = null;
        if (null != screencastHelper) {
            connectInfos = screencastHelper.getConnectInfos();
        }
        if (null != screencastHelper && null != connectInfos && !connectInfos.isEmpty()) {
            PolyvLogger.test(TAG, "volumeUp click");
            screencastHelper.voulumeUp();
        } else {
            PolyvToastUtil.show(getApplicationContext(), "请先连接设备");
        }
    }

    public void voulumeDown() {
        List<LelinkServiceInfo> connectInfos = null;
        if (null != screencastHelper) {
            connectInfos = screencastHelper.getConnectInfos();
        }
        if (null != screencastHelper && null != connectInfos && !connectInfos.isEmpty()) {
            PolyvLogger.test(TAG, "volumeDown click");
            screencastHelper.voulumeDown();
        } else {
            PolyvToastUtil.show(getApplicationContext(), "请先连接设备");
        }
    }

    public void disConnect() {
        disConnect(true);
    }

    public void disConnect(boolean isSelectNull) {
        LelinkServiceInfo selectInfo = screencastDeviceListAdapter.getSelectInfo();
        if (null != screencastHelper && null != selectInfo) {
            PolyvLogger.test(TAG, "disConnect click:" + selectInfo.getName());
            screencastHelper.disConnect(selectInfo);
        }

        if (isSelectNull)
            selectNull();
    }

    public void selectNull() {
        screencastDeviceListAdapter.setSelectInfo(null);
        screencastDeviceListAdapter.notifyDataSetChanged();
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
        PolyvLogger.test(TAG, "reconnect click:" + screencastHelper.getLastServiceInfo());
        if (null != screencastHelper) {
            PolyvLogger.test(TAG, "start connect:" + screencastHelper.getLastServiceInfo());
            disConnect();
            screencastHelper.connect(screencastHelper.getLastServiceInfo());
            screencastDeviceListAdapter.setSelectInfo(screencastHelper.getLastServiceInfo());
            screencastDeviceListAdapter.notifyDataSetChanged();
        } else {
            PolyvToastUtil.show(getApplicationContext(), "未初始化或未选择设备");
        }
    }

    private void connect(LelinkServiceInfo info) {
        PolyvLogger.test(TAG, "connect click:" + info.getName());
        if (null != screencastHelper) {
            PolyvLogger.test(TAG, "start connect:" + info.getName());
            screencastHelper.connect(info);
        } else {
            PolyvToastUtil.show(getApplicationContext(), "未初始化或未选择设备");
        }
    }

    private void updateBrowseAdapter() {
        if (null != screencastHelper) {
            List<LelinkServiceInfo> connectInfos = screencastHelper.getConnectInfos();
            if (connectInfos != null) {
                for (LelinkServiceInfo lelinkServiceInfo : connectInfos) {
                    if (lelinkServiceInfo != null) {
                        screencastDeviceListAdapter.setSelectInfo(lelinkServiceInfo);
                    }
                }
            }

            List<LelinkServiceInfo> infos = screencastHelper.getInfos();
            screencastDeviceListAdapter.updateDatas(infos);

            ll_search.setVisibility(View.GONE);
        }
    }

    private void browse() {
        PolyvLogger.test(TAG, "btn_browse click");
        String netWorkName = NetworkUtil.getNetWorkName(getApplicationContext());
        boolean isWifi = PolyvNetworkUtils.NETWORK_WIFI == PolyvNetworkUtils.getNetWorkType(getApplicationContext());
        if (isWifi) {
            netWorkName = PolyvNetworkUtils.getWIFISSID(getApplicationContext());
        }
        if ("网络错误".equals(netWorkName) || "有线网络".equals(netWorkName)) {
            return;
        }
        if (null != screencastHelper) {
            int type;
            String text;
            text = "All";
            type = ILelinkServiceManager.TYPE_ALL;
            PolyvLogger.test(TAG, "browse type:" + text);
            PolyvLogger.d(TAG, "browse type:" + text);
            if (!isFirstBrowse) {
                isFirstBrowse = true;
            }
            screencastHelper.browse(type);

            ll_search.setVisibility(View.VISIBLE);
        }
    }

    private void stopBrowse() {
        PolyvLogger.test(TAG, "btn_stop_browse click");
        if (null != screencastHelper) {
            PolyvLogger.test(TAG, "stop browse");
            PolyvLogger.d(TAG, "stop browse");
            isFirstBrowse = false;
            screencastHelper.stopBrowse();

            ll_search.setVisibility(View.GONE);
        }
    }

    private PolyvIUIUpdateListener iUIUpdateListener = new PolyvIUIUpdateListener() {

        @Override
        public void onUpdateState(int state, Object object) {
            PolyvLogger.d(TAG, "IUIUpdateListener state:" + state + " text:" + object);
            switch (state) {
                case PolyvIUIUpdateListener.STATE_SEARCH_SUCCESS:
                    if (isFirstBrowse) {
                        isFirstBrowse = false;
                        if (getVisibility() == View.VISIBLE)
                            PolyvToastUtil.show(getApplicationContext(), "搜索成功");
                        PolyvLogger.test(TAG, "搜索成功");
                    }
                    if (null != delayHandler) {
                        delayHandler.removeCallbacksAndMessages(null);
                        delayHandler.sendEmptyMessageDelayed(PolyvIUIUpdateListener.STATE_SEARCH_SUCCESS,
                                TimeUnit.MILLISECONDS.toMillis(100));
                    }
                    break;
                case PolyvIUIUpdateListener.STATE_SEARCH_ERROR:
                    if (getVisibility() == View.VISIBLE)
                        PolyvToastUtil.show(getApplicationContext(), "Auth错误");
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
                    PolyvLogger.test(TAG, "disConnect success:" + object);
                    PolyvLogger.d(TAG, "PolyvToastUtil " + object);
                    PolyvToastUtil.show(getApplicationContext(), (String) object);
                    screencastDeviceListAdapter.setSelectInfo(null);
                    screencastDeviceListAdapter.notifyDataSetChanged();
                    break;
                case PolyvIUIUpdateListener.STATE_CONNECT_FAILURE:
                    if (object instanceof String && ((String) object).contains("等待"))
                        return;
                    PolyvLogger.test(TAG, "connect failure:" + object);
                    PolyvLogger.d(TAG, "PolyvToastUtil " + object);
                    PolyvToastUtil.show(getApplicationContext(), (String) object);
                    screencastDeviceListAdapter.setSelectInfo(null);
                    screencastDeviceListAdapter.notifyDataSetChanged();

                    disConnect();
                    screencastStatusLayout.callPlayErrorStatus();
                    break;
                case PolyvIUIUpdateListener.STATE_PLAY:
                    PolyvLogger.test(TAG, "callback play");
                    PolyvLogger.d(TAG, "PolyvToastUtil 开始播放");
                    PolyvToastUtil.show(getApplicationContext(), "开始播放");

                    screencastStatusLayout.callOnStart();
                    break;
                case PolyvIUIUpdateListener.STATE_LOADING:
                    PolyvLogger.test(TAG, "callback loading");
                    PolyvLogger.d(TAG, "PolyvToastUtil 开始加载");
                    PolyvToastUtil.show(getApplicationContext(), "开始加载");
                    break;
                case PolyvIUIUpdateListener.STATE_PAUSE:
                    PolyvLogger.test(TAG, "callback pause");
                    PolyvLogger.d(TAG, "PolyvToastUtil 暂停播放");
                    PolyvToastUtil.show(getApplicationContext(), "暂停播放");

                    screencastStatusLayout.callOnPause();
                    break;
                case PolyvIUIUpdateListener.STATE_STOP:
                    PolyvLogger.test(TAG, "callback stop");
                    PolyvLogger.d(TAG, "PolyvToastUtil 播放结束");
                    PolyvToastUtil.show(getApplicationContext(), "播放结束");

                    screencastStatusLayout.hide(true);
                    disConnect();
                    break;
                case PolyvIUIUpdateListener.STATE_SEEK:
                    PolyvLogger.test(TAG, "callback seek:" + object);
                    PolyvLogger.d(TAG, "PolyvToastUtil seek完成:" + object);
                    PolyvToastUtil.show(getApplicationContext(), "seek完成" + object);
                    break;
                case PolyvIUIUpdateListener.STATE_PLAY_ERROR:
                    if (object instanceof String && ((String) object).contains("无响应"))
                        return;
                    PolyvLogger.test(TAG, "callback error:" + object);
                    PolyvToastUtil.show(getApplicationContext(), "播放错误：" + object);

                    screencastStatusLayout.callPlayErrorStatus();
                    break;
                case PolyvIUIUpdateListener.STATE_POSITION_UPDATE:
                    PolyvLogger.test(TAG, "callback position update:" + object);
                    long[] arr = (long[]) object;
                    long duration = arr[0];
                    long position = arr[1];
                    PolyvLogger.d(TAG, "PolyvToastUtil 总长度：" + duration + " 当前进度:" + position);
                    screencastStatusLayout.callPlayProgress(duration, position);
                    break;
                case PolyvIUIUpdateListener.STATE_COMPLETION:
                    PolyvLogger.test(TAG, "callback completion");
                    PolyvLogger.d(TAG, "PolyvToastUtil 播放完成");
                    PolyvToastUtil.show(getApplicationContext(), "播放完成");

                    screencastStatusLayout.hide(true);
                    break;
            }
        }

        @Override
        public void onUpdateText(String msg) {
            PolyvLogger.d(TAG, "onUpdateText : " + msg + "\n\n");
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
                    PolyvLogger.test(TAG, "connect success:" + serviceInfo.getName() + "连接成功");
                    PolyvLogger.d(TAG, "PolyvToastUtil " + serviceInfo.getName() + "连接成功");
                    PolyvToastUtil.show(getApplicationContext(), serviceInfo.getName() + "连接成功");

                    int currentBitrate = screencastStatusLayout.getCurrentPlayBitrate();
                    currentBitrate = Math.max(1, currentBitrate);
                    String playPath = screencastStatusLayout.getVideoView().getPlayPathWithBitRate(currentBitrate);
                    play(playPath, currentBitrate);
                }
            }, 3000);//由于内部还有连接，所以这里延迟3秒
        }

        @Override
        public void onDisconnect(LelinkServiceInfo serviceInfo, int what, int extra) {
        }
    };

    public void seekTo(int progress) {
        screencastHelper.seekTo(progress);
    }

    public void pause() {
        List<LelinkServiceInfo> connectInfos = null;
        if (null != screencastHelper) {
            connectInfos = screencastHelper.getConnectInfos();
        }
        if (null != screencastHelper && null != connectInfos && !connectInfos.isEmpty()) {
            PolyvLogger.test(TAG, "pause click");
            screencastHelper.pause();
        }
    }

    public void resume() {
        List<LelinkServiceInfo> connectInfos = null;
        if (null != screencastHelper) {
            connectInfos = screencastHelper.getConnectInfos();
        }
        if (null != screencastHelper && null != connectInfos && !connectInfos.isEmpty()) {
            PolyvLogger.test(TAG, "resume click");
            // 暂停中
            screencastHelper.resume();
        }
    }

    public void play(String playPath, int bitrate) {
        PolyvLogger.test(TAG, "start play url:" + playPath + " type:" + PolyvAllCast.MEDIA_TYPE_VIDEO);
        if (screencastStatusLayout.getVideoView().isDisableScreenCAP()) {
            PolyvToastUtil.show(getApplicationContext(), "防录屏状态下不能投屏");
            screencastStatusLayout.callPlayErrorStatus();
            return;
        }
        if (TextUtils.isEmpty(playPath)) {
            PolyvToastUtil.show(getApplicationContext(), "获取播放地址失败");
            screencastStatusLayout.callPlayErrorStatus();
            return;
        }
        if (null == screencastHelper) {
            PolyvToastUtil.show(getApplicationContext(), "未初始化或未选择设备");
            screencastStatusLayout.callPlayErrorStatus();
            return;
        }
        List<LelinkServiceInfo> connectInfos = screencastHelper.getConnectInfos();
        if (null == connectInfos || connectInfos.isEmpty()) {
            PolyvToastUtil.show(getApplicationContext(), "请先连接设备");
            screencastStatusLayout.callPlayErrorStatus();
            return;
        }
        screencastStatusLayout.callScreencastingStatus(bitrate);
        PolyvVideoVO videoVO = screencastStatusLayout.getVideoView().getVideo();
        if (videoVO != null && videoVO.getVideoType() == PolyvVideoType.ENCRYPTION_M3U8) {
            screencastHelper.playNetMedia(playPath, videoVO.getVid(), bitrate, videoVO.getSeedConst(), PolyvAllCast.MEDIA_TYPE_VIDEO, "");
        } else {
            screencastHelper.playNetMedia(playPath, PolyvAllCast.MEDIA_TYPE_VIDEO, "");
        }
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
