package com.easefun.polyvsdk.marquee.animation;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Build;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import java.util.HashMap;

/**
 * 跑马灯  闪烁动画类
 */
public class PLVMarqueeFlickAnimation extends PLVMarqueeAnimation {
    private static final String TAG = "PLVMarqueeFlickAnimatio";

    // <editor-fold desc="变量">
    protected int lifeTime = 0;
    protected int interval = 0;
    protected int tweenTime = 0;

    private int repeatCount = Animation.INFINITE;
    private int repeatMode = Animation.RESTART;
    @Nullable
    protected RelativeLayout.LayoutParams layoutParams;
    @Nullable
    protected View mainView;

    protected ObjectAnimator flickObjectAnimator1 = new ObjectAnimator();
    protected ObjectAnimator flickObjectAnimator2 = new ObjectAnimator();
    // </editor-fold>

    // <editor-fold desc="对外API - 参数设置">
    @Override
    public void setViews(HashMap<Integer, View> viewMap) {
        mainView = viewMap.get(VIEW_MAIN);
        if (mainView == null) {
            return;
        }
        layoutParams = (RelativeLayout.LayoutParams) mainView.getLayoutParams();
        mainView.setAlpha(0);
    }

    @Override
    public void setParams(HashMap<Integer, Integer> paramMap) {
        this.viewWidth = paramMap.containsKey(PARAM_VIEW_WIDTH) ? paramMap.get(PARAM_VIEW_WIDTH) : 0;
        this.viewHeight = paramMap.containsKey(PARAM_VIEW_HEIGHT) ? paramMap.get(PARAM_VIEW_HEIGHT) : 0;
        this.screenWidth = paramMap.containsKey(PARAM_SCREEN_WIDTH) ? paramMap.get(PARAM_SCREEN_WIDTH) : 0;
        this.screenHeight = paramMap.containsKey(PARAM_SCREEN_HEIGHT) ? paramMap.get(PARAM_SCREEN_HEIGHT) : 0;
        this.lifeTime = paramMap.containsKey(PARAM_LIFE_TIME) ? paramMap.get(PARAM_LIFE_TIME) : 0;
        this.interval = paramMap.containsKey(PARAM_INTERVAL) ? paramMap.get(PARAM_INTERVAL) : 0;
        this.tweenTime = paramMap.containsKey(PARAM_TWEEN_TIME) ? paramMap.get(PARAM_TWEEN_TIME) : 0;
        if (paramMap.containsKey(PARAM_ISALWAYS_SHOW_WHEN_RUN)) {
            this.isAlwaysShowWhenRun = paramMap.get(PARAM_ISALWAYS_SHOW_WHEN_RUN) == 1;
        }
        if (paramMap.containsKey(PARAM_HIDE_WHEN_PAUSE)) {
            this.isHiddenWhenPause = paramMap.get(PARAM_HIDE_WHEN_PAUSE) == 1;
        }
    }
    // </editor-fold>

    // <editor-fold desc="对外API - 生命周期控制">
    @Override
    public void start() {
        if (mainView == null) {
            return;
        }
        if (animationStatus == PAUSE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (flickObjectAnimator1.isPaused()) {
                    flickObjectAnimator1.resume();
                } else if (flickObjectAnimator2.isPaused()) {
                    flickObjectAnimator2.resume();
                }
            } else {
                flickObjectAnimator1.start();
            }

            animationStatus = STARTED;
        } else {
            setAnimation();
            flickObjectAnimator1.start();
        }
    }

    @Override
    public void pause() {
        if (mainView == null) {
            return;
        }
        animationStatus = PAUSE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (flickObjectAnimator1.isStarted()) {
                flickObjectAnimator1.pause();
            }
            if (flickObjectAnimator2.isStarted()) {
                flickObjectAnimator2.pause();
            }
        } else {
            stop();
        }
        if (isHiddenWhenPause) {
            mainView.setAlpha(0);
        }
    }

    @Override
    public void stop() {
        if (mainView == null) {
            return;
        }
        animationStatus = STOP;
        flickObjectAnimator1.cancel();
        flickObjectAnimator1.end();
        flickObjectAnimator2.cancel();
        flickObjectAnimator2.end();
    }

    @Override
    public void destroy() {
        flickObjectAnimator1 = null;
        flickObjectAnimator2 = null;
    }

    @Override
    public void onParentSizeChanged(final View parentView) {
        if (mainView == null) {
            return;
        }
        mainView.clearAnimation();
        mainView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                screenWidth = parentView.getWidth();
                screenHeight = parentView.getHeight();
                if (Build.VERSION.SDK_INT >= 16) {
                    mainView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    mainView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                if (animationStatus == STARTED) {
                    stop();
                    start();
                } else if (animationStatus == PAUSE) {
                    stop();
                    setAnimation();
                }
            }
        });
    }
    // </editor-fold>

    // <editor-fold desc="功能模块 - 设置动画样式和位置">
    protected void setAnimation() {
        if (animationStatus == STARTED) {
            return;
        }
        if (mainView == null) {
            return;
        }
        animationStatus = STARTED;
        float minAlpha = 0;
        if (isAlwaysShowWhenRun) {
            minAlpha = 0.1f;
        }
        flickObjectAnimator1 = ObjectAnimator.ofFloat(mainView, "alpha", minAlpha, 1f);
        flickObjectAnimator1.setDuration(tweenTime);
        flickObjectAnimator1.setStartDelay(isAlwaysShowWhenRun ? 0 : interval);
        flickObjectAnimator1.setInterpolator(new LinearInterpolator());
        flickObjectAnimator1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mainView.setAlpha(0);
                setMainActiveRect();
                mainView.setLayoutParams(layoutParams);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mainView.setAlpha(1);
                if (animationStatus == STARTED) {
                    flickObjectAnimator2.start();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        flickObjectAnimator2 = ObjectAnimator.ofFloat(mainView, "alpha", 1f, minAlpha);
        flickObjectAnimator2.setDuration(tweenTime);
        flickObjectAnimator2.setStartDelay(lifeTime);
        flickObjectAnimator2.setInterpolator(new LinearInterpolator());
        flickObjectAnimator2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mainView.setAlpha(1);

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mainView.setAlpha(0);
                if (animationStatus == STARTED) {
                    flickObjectAnimator1.start();
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
    protected void setMainActiveRect() {
        if (layoutParams == null) {
            return;
        }
        layoutParams.topMargin = (int) (Math.random() * (screenHeight - Math.min(screenHeight, viewHeight)));
        layoutParams.leftMargin = (int) (Math.random() * (screenWidth - Math.min(screenWidth, viewWidth)));
    }
    // </editor-fold>
}
