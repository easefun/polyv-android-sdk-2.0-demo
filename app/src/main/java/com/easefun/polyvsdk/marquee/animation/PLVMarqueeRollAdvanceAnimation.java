package com.easefun.polyvsdk.marquee.animation;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Build;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;

import java.util.HashMap;

/**
 * 跑马灯 滚动 增强的动画类
 */
public class PLVMarqueeRollAdvanceAnimation extends PLVMarqueeRollAnimation {
    private static final String TAG = "PLVMarqueeRollAdvanceAn";

    // <editor-fold desc="变量">
    @Nullable
    private View secondView;

    protected ObjectAnimator secondAnimator = new ObjectAnimator();

    private boolean isSetSecondParams = false;
    // </editor-fold>

    // <editor-fold desc="对外API - 参数设置">
    @Override
    public void setViews(HashMap<Integer, View> viewMap) {
        super.setViews(viewMap);

        secondView = viewMap.get(VIEW_SECOND);
        if (secondView == null) {
            return;
        }
        secondView.setAlpha(0F);
    }
    // </editor-fold>

    // <editor-fold desc="对外API - 生命周期控制">
    @Override
    public void start() {
        if (secondView == null) {
            return;
        }
        if (animationStatus == PAUSE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                secondAnimator.resume();
            } else {
                secondAnimator.start();
            }
            if (secondAnimator.isRunning()) {
                secondView.setAlpha(1F);
            }
        } else {
            setSecondAnimation();
            secondAnimator.start();
        }
        super.start();
    }

    @Override
    public void pause() {
        super.pause();
        if (secondView == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            secondAnimator.pause();
        } else {
            stopSecondAnimator();
        }
        secondView.setAlpha(0F);
    }

    @Override
    public void stop() {
        super.stop();
        stopSecondAnimator();
    }

    @Override
    public void destroy() {
        super.destroy();
        secondAnimator = null;
    }

    @Override
    public void onParentSizeChanged(final View parentView) {
        super.onParentSizeChanged(parentView);
        if (secondView == null) {
            return;
        }
        secondView.clearAnimation();
        secondView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                screenWidth = parentView.getWidth();
                screenHeight = parentView.getHeight();
                if (Build.VERSION.SDK_INT >= 16) {
                    secondView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    secondView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                if (animationStatus == STARTED) {
                    stopSecondAnimator();
                    setSecondAnimation();
                    secondAnimator.start();
                } else if (animationStatus == PAUSE) {
                    stopSecondAnimator();
                    setSecondAnimation();
                }
            }
        });
    }
    // </editor-fold>

    // <editor-fold desc="功能模块 - 设置动画样式和位置">
    private void setSecondAnimation() {
        if (isSetSecondParams) {
            return;
        }
        if (secondView == null) {
            return;
        }
        isSetSecondParams = true;
        final float startX = isAlwaysShowWhenRun ? screenWidth - secondView.getWidth() : screenWidth;
        final float endX = isAlwaysShowWhenRun ? 0 : -secondView.getWidth();
        secondAnimator = ObjectAnimator.ofFloat(secondView, "translationX", startX, endX);
        secondAnimator.setDuration(duration);
        secondAnimator.setStartDelay(isAlwaysShowWhenRun ? 0 : interval);
        secondAnimator.setInterpolator(new LinearInterpolator());
        secondAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                secondView.setAlpha(1F);
                setSecondActiveRect();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                secondView.setAlpha(0F);
                if (animationStatus == STARTED
                        || (mainAnimator != null && mainAnimator.isStarted())) {
                    animation.start();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    protected void setSecondActiveRect() {
        if (secondView == null) {
            return;
        }
        MarginLayoutParams lp = (MarginLayoutParams) secondView.getLayoutParams();
        lp.topMargin = (int) (Math.random() * (screenHeight - Math.min(screenHeight, secondView.getHeight())));
        secondView.setLayoutParams(lp);
    }

    private void stopSecondAnimator() {
        secondAnimator.cancel();
        secondAnimator.end();
        isSetSecondParams = false;
    }
    // </editor-fold>

}
