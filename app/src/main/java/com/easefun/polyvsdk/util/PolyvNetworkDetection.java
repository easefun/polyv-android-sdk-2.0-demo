package com.easefun.polyvsdk.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

public class PolyvNetworkDetection {
    private static boolean isAllowMobile;
    private static final String WIFI_AP_STATE_CHANGED_ACTION = "android.net.wifi.WIFI_AP_STATE_CHANGED";
    private NetworkReceiver networkReceiver;
    private Context context;
    private IOnNetworkChangedListener onNetworkChangedListener;

    public PolyvNetworkDetection(Context context) {
        this.context = context;
        registerNetworkReceiver(context);
    }

    public void allowMobile() {
        isAllowMobile = true;
    }

    public boolean isAllowMobile() {
        return isAllowMobile;
    }

    public boolean isMobileType() {
        return getNetWorkType() != PolyvNetworkUtils.NETWORK_WIFI && getNetWorkType() != PolyvNetworkUtils.NETWORK_NO;
    }

    public boolean isWifiType() {
        return getNetWorkType() == PolyvNetworkUtils.NETWORK_WIFI;
    }

    public int getNetWorkType() {
        return PolyvNetworkUtils.getNetWorkType(context);
    }

    public void setOnNetworkChangedListener(IOnNetworkChangedListener onNetworkChangedListener) {
        this.onNetworkChangedListener = onNetworkChangedListener;
    }

    public void destroy() {
        if (null != networkReceiver) {
            context.unregisterReceiver(networkReceiver);
            networkReceiver = null;
        }
        onNetworkChangedListener = null;
    }

    private void registerNetworkReceiver(Context context) {
        networkReceiver = new NetworkReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WIFI_AP_STATE_CHANGED_ACTION);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(networkReceiver, intentFilter);
    }

    private class NetworkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ConnectivityManager.CONNECTIVITY_ACTION.equalsIgnoreCase(action) ||
                    "android.net.wifi.WIFI_AP_STATE_CHANGED".equalsIgnoreCase(action)) {
                if (onNetworkChangedListener != null) {
                    onNetworkChangedListener.onChanged(getNetWorkType());
                }
            }
        }
    }

    public interface IOnNetworkChangedListener {
        void onChanged(int networkType);
    }
}
