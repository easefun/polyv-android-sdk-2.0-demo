package com.easefun.polyvsdk.marquee.animation;

/**
 * 跑马灯 在总区域上下15%部分闪烁的动画类
 */
public class PLVMarqueeFlick15PercentAnimation extends PLVMarqueeFlickAnimation {

    // <editor-fold desc="功能模块 - 设置动画样式和位置">
    // 设置活动区域
    @Override
    protected void setMainActiveRect() {
        if (layoutParams == null) {
            return;
        }
        boolean showOnTopArea = Math.random() < 0.5;
        float availableViewHeight = screenHeight * 0.15F;
        if (showOnTopArea) {
            // 在上面显示
            if (viewHeight < availableViewHeight) {
                int space = (int) (availableViewHeight - viewHeight);
                layoutParams.topMargin = (int) (Math.random() * space);
            } else {
                layoutParams.topMargin = 0;
            }
        } else {
            // 在下面显示
            if (viewHeight < availableViewHeight) {
                int bottomAreaTop = (int) (screenHeight - availableViewHeight);
                int space = (int) (availableViewHeight - viewHeight);
                layoutParams.topMargin = (int) (bottomAreaTop + Math.random() * space);
            } else {
                layoutParams.topMargin = screenHeight - Math.min(screenHeight, viewHeight);
            }
        }
        layoutParams.leftMargin = (int) (Math.random() * (screenWidth - Math.min(screenWidth, viewWidth)));
    }
    // </editor-fold>

}
