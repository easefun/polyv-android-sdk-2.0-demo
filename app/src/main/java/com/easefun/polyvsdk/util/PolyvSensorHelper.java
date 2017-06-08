package com.easefun.polyvsdk.util;

import android.app.Activity;
import android.view.OrientationEventListener;

import java.lang.ref.WeakReference;

public class PolyvSensorHelper {
    private OrientationEventListener eventListener;
    private boolean switchFlag;
    private boolean isLandscape;

    public PolyvSensorHelper(final Activity activity) {
        final WeakReference<Activity> wr_activity = new WeakReference<Activity>(activity);
        eventListener = new OrientationEventListener(activity.getApplicationContext()) {
            @Override
            public void onOrientationChanged(int orientation) {
                Activity activity1 = wr_activity.get();
                if (activity1 != null) {
                    boolean isPortrait = PolyvScreenUtils.isPortrait(activity1);
                    if ((orientation > -1 && orientation <= 10) || orientation >= 350 || (orientation <= 190 && orientation >= 170)) {
                        if (!isPortrait && switchFlag && !isLandscape)
                            PolyvScreenUtils.setPortrait(activity1);
                        if (isPortrait && !isLandscape)
                            isLandscape = !isLandscape;
                    } else if ((orientation <= 100 && orientation >= 80) || (orientation <= 280 && orientation >= 260)) {
                        if (isPortrait && switchFlag && isLandscape)
                            PolyvScreenUtils.setLandscape(activity1);
                        if (!isPortrait && isLandscape)
                            isLandscape = !isLandscape;
                    }
                }
            }
        };
    }

    // 开启监听
    public void enable() {
        eventListener.enable();
    }

    // 关闭监听
    public void disable() {
        eventListener.disable();
    }

    /**
     * 屏幕方向切换之后，是否开启随手势自动切换屏幕方向
     *
     * @param switchFlag  是否开启自动切换
     * @param isLandscape 屏幕切换之后的方向(非自动切换，屏幕方向需要和手动切换之后的方向一致之后，自动切换才会再次生效)
     */
    public void toggle(boolean switchFlag, boolean isLandscape) {
        this.switchFlag = switchFlag;
        this.isLandscape = isLandscape;
    }
}
