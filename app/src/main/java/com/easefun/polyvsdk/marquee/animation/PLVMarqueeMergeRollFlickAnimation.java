package com.easefun.polyvsdk.marquee.animation;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Build;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import java.util.HashMap;

/**
 * 跑马灯  滚动和闪烁混合动画类
 */
public class PLVMarqueeMergeRollFlickAnimation extends PLVMarqueeRollAnimation {

    // <editor-fold desc="变量">
    private static final String TAG = "PLVMarqueeMergeRollFlic";
    private int tweenTime = 0;
    private ObjectAnimator flickObjectAnimation1 = new ObjectAnimator();
    private ObjectAnimator flickObjectAnimation2 = new ObjectAnimator();
    // </editor-fold>

    // <editor-fold desc="对外API - 参数设置">
    @Override
    public void setParams(HashMap<Integer, Integer> paramMap) {
        super.setParams(paramMap);
        tweenTime = paramMap.containsKey(PARAM_TWEEN_TIME) ? paramMap.get(PARAM_TWEEN_TIME) : 0;
    }
    // </editor-fold>

    // <editor-fold desc="对外API - 生命周期控制">
    @Override
    public void start() {
        super.start();
        if (mainView == null) {
            return;
        }
        if (animationStatus == PAUSE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (flickObjectAnimation1.isPaused()) {
                    flickObjectAnimation1.resume();
                } else if (flickObjectAnimation2.isPaused()) {
                    flickObjectAnimation2.resume();
                }
            } else {
                flickObjectAnimation1.start();
            }
            animationStatus = STARTED;
        } else {
            setAnimation();
            flickObjectAnimation1.start();
        }
    }

    @Override
    public void pause() {
        super.pause();
        if (mainView == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (flickObjectAnimation1.isStarted()) {
                flickObjectAnimation1.pause();
            }
            if (flickObjectAnimation2.isStarted()) {
                flickObjectAnimation2.pause();
            }
        } else {
            stop();
        }
    }

    @Override
    public void stop() {
        super.stop();
        if (mainView == null) {
            return;
        }
        flickObjectAnimation1.cancel();
        flickObjectAnimation1.end();
        flickObjectAnimation2.cancel();
        flickObjectAnimation2.end();
    }

    @Override
    public void destroy() {
        super.destroy();
        flickObjectAnimation1 = null;
        flickObjectAnimation2 = null;
    }
    // </editor-fold>

    // <editor-fold desc="功能模块 - 设置动画样式和位置">
    @Override
    protected void setAnimation() {
        super.setAnimation();
        final float minAlpha = isAlwaysShowWhenRun ? 0.1F : 0F;
        flickObjectAnimation1 = ObjectAnimator.ofFloat(mainView, "alpha", minAlpha, 1F);
        flickObjectAnimation1.setDuration(tweenTime);
        flickObjectAnimation1.setInterpolator(new LinearInterpolator());
        flickObjectAnimation1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.i(TAG, "onAnimationEnd: ");
                if (animationStatus == STARTED) {
                    flickObjectAnimation2.start();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        flickObjectAnimation2 = ObjectAnimator.ofFloat(mainView, "alpha", 1F, minAlpha);
        flickObjectAnimation2.setDuration(tweenTime);
        flickObjectAnimation2.setStartDelay(isAlwaysShowWhenRun ? 0 : interval);
        flickObjectAnimation2.setInterpolator(new LinearInterpolator());
        flickObjectAnimation2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (animationStatus == STARTED) {
                    flickObjectAnimation1.start();
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
    // </editor-fold>
}
