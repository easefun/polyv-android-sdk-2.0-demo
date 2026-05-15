package com.easefun.polyvsdk.player.gesture;

import android.view.View;

import com.easefun.polyvsdk.player.PolyvPlayerMediaController;
import com.easefun.polyvsdk.util.PolyvScreenUtils;
import com.easefun.polyvsdk.video.PolyvVideoView;
import com.easefun.polyvsdk.video.listener.IPolyvOnGestureScaleListener;

/**
 * 视频缩放/平移手势处理类
 */
public class PolyvScaleGestureHandler implements IPolyvOnGestureScaleListener {

    // 最小缩放比例
    private static final float MIN_SCALE = 0.25f;
    // 最大缩放比例
    private static final float MAX_SCALE = 4.0f;

    private final PolyvPlayerMediaController mediaController;
    private final View handleView;

    // 当前状态
    private boolean isHandling = false;
    private float currentOffsetX = 0f;
    private float currentOffsetY = 0f;
    private float currentScale = 1f;

    // 用于计算的手势点
    private float lastPointX1 = 0f;
    private float lastPointX2 = 0f;
    private float lastPointY1 = 0f;
    private float lastPointY2 = 0f;

    public PolyvScaleGestureHandler(PolyvVideoView videoView, PolyvPlayerMediaController mediaController) {
        boolean hasChildView = videoView.getChildCount() > 0;
        // 需要缩放其子view，避免缩放videoView后影响到手势触摸区域变小
        this.handleView = hasChildView ? videoView.getChildAt(0) : videoView;
        this.mediaController = mediaController;
        this.mediaController.setOnResetScaleClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
                mediaController.setResetScaleViewShow(false);
            }
        });
    }

    @Override
    public void onScaleStart(int pointerCount) {
        if (mediaController.isLocked() || PolyvScreenUtils.isPortrait(handleView.getContext())) {
            return;
        }
        if (pointerCount >= 2) {
            isHandling = true;
            // 保存当前状态
            currentOffsetX = handleView.getTranslationX();
            currentOffsetY = handleView.getTranslationY();
            currentScale = handleView.getScaleX();
        }
    }

    @Override
    public void onScale(float offsetX, float offsetY, float scale, int pointerCount) {
        if (mediaController.isLocked() || PolyvScreenUtils.isPortrait(handleView.getContext())) {
            return;
        }
        if (!isHandling || pointerCount < 2) {
            return;
        }

        // 应用平移
        float newOffsetX = currentOffsetX + offsetX;
        float newOffsetY = currentOffsetY + offsetY;

        // 应用缩放（限制范围）
        float newScale = Math.max(MIN_SCALE, Math.min(MAX_SCALE, currentScale * scale));

        // 更新 View 的变换属性
        handleView.setScaleX(newScale);
        handleView.setScaleY(newScale);
        handleView.setTranslationX(newOffsetX);
        handleView.setTranslationY(newOffsetY);

        // 更新当前状态
        currentOffsetX = newOffsetX;
        currentOffsetY = newOffsetY;
        currentScale = newScale;

        mediaController.setResetScaleViewShow(true);
    }

    @Override
    public void onScaleEnd() {
        if (mediaController.isLocked() || PolyvScreenUtils.isPortrait(handleView.getContext())) {
            return;
        }
        isHandling = false;
        handleView.setTranslationX(0);
        handleView.setTranslationY(0);
    }

    /**
     * 重置视频视图的缩放和平移
     */
    public void reset() {
        handleView.setScaleX(1f);
        handleView.setScaleY(1f);
        handleView.setTranslationX(0f);
        handleView.setTranslationY(0f);
        currentOffsetX = 0f;
        currentOffsetY = 0f;
        currentScale = 1f;
    }

    /**
     * 获取当前缩放比例
     */
    public float getCurrentScale() {
        return currentScale;
    }

    /**
     * 获取当前X轴偏移
     */
    public float getCurrentOffsetX() {
        return currentOffsetX;
    }

    /**
     * 获取当前Y轴偏移
     */
    public float getCurrentOffsetY() {
        return currentOffsetY;
    }
}
