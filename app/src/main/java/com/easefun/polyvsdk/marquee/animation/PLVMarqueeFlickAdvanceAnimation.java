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
 * 跑马灯 闪烁增强动画类
 */
public class PLVMarqueeFlickAdvanceAnimation extends PLVMarqueeFlickAnimation {

    // <editor-fold desc="变量">
    private static final String TAG = "PLVMarqueeFlickAdvanceA";

    @Nullable
    private View secondView;

    protected ObjectAnimator secondFlickObjectAnimator1 = new ObjectAnimator();
    protected ObjectAnimator secondFlickObjectAnimator2 = new ObjectAnimator();

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
        secondView.setAlpha(0);
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
                if (secondFlickObjectAnimator1.isPaused()) {
                    secondFlickObjectAnimator1.resume();
                } else if (secondFlickObjectAnimator2.isPaused()) {
                    secondFlickObjectAnimator2.resume();
                }
            } else {
                secondFlickObjectAnimator1.start();
            }
        } else {
            setSecondAnimation();
            secondFlickObjectAnimator1.start();
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
            if (secondFlickObjectAnimator1.isStarted()) {
                secondFlickObjectAnimator1.pause();
            }
            if (secondFlickObjectAnimator2.isStarted()) {
                secondFlickObjectAnimator2.pause();
            }
        } else {
            stopSecondAnimator();
        }
        secondView.setAlpha(0);
    }

    @Override
    public void stop() {
        super.stop();
        if (secondView == null) {
            return;
        }
        stopSecondAnimator();
    }

    @Override
    public void destroy() {
        super.destroy();
        secondFlickObjectAnimator1 = null;
        secondFlickObjectAnimator2 = null;
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
                    secondFlickObjectAnimator1.start();
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
        secondFlickObjectAnimator1 = ObjectAnimator.ofFloat(secondView, "alpha", 0f, 1f);
        secondFlickObjectAnimator1.setDuration(tweenTime);
        secondFlickObjectAnimator1.setStartDelay(isAlwaysShowWhenRun ? 0 : interval);
        secondFlickObjectAnimator1.setInterpolator(new LinearInterpolator());
        secondFlickObjectAnimator1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                setSecondActiveRect();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (animationStatus == STARTED
                        || (flickObjectAnimator1 != null && flickObjectAnimator1.isStarted())
                        || (flickObjectAnimator2 != null && flickObjectAnimator2.isStarted())) {
                    secondFlickObjectAnimator2.start();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        secondFlickObjectAnimator2 = ObjectAnimator.ofFloat(secondView, "alpha", 1f, 0f);
        secondFlickObjectAnimator2.setDuration(tweenTime);
        secondFlickObjectAnimator2.setStartDelay(lifeTime);
        secondFlickObjectAnimator2.setInterpolator(new LinearInterpolator());
        secondFlickObjectAnimator2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (animationStatus == STARTED
                        || (flickObjectAnimator1 != null && flickObjectAnimator1.isStarted())
                        || (flickObjectAnimator2 != null && flickObjectAnimator2.isStarted())) {
                    secondFlickObjectAnimator1.start();
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

    // 设置活动区域
    protected void setSecondActiveRect() {
        if (secondView == null) {
            return;
        }
        MarginLayoutParams lp = (MarginLayoutParams) secondView.getLayoutParams();
        lp.topMargin = (int) (Math.random() * (screenHeight - Math.min(screenHeight, secondView.getHeight())));
        lp.leftMargin = (int) (Math.random() * (screenWidth - Math.min(screenWidth, secondView.getWidth())));
        secondView.setLayoutParams(lp);
    }

    private void stopSecondAnimator() {
        secondFlickObjectAnimator1.cancel();
        secondFlickObjectAnimator1.end();
        secondFlickObjectAnimator2.cancel();
        secondFlickObjectAnimator2.end();
        isSetSecondParams = false;
    }
    // </editor-fold>

}
