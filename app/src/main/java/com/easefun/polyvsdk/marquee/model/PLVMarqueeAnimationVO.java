package com.easefun.polyvsdk.marquee.model;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 跑马灯 动画样式的描述类
 */
public class PLVMarqueeAnimationVO {

    // <editor-fold desc="变量">
    // 动画类型
    public static final int ROLL = 1;
    public static final int FLICK = 2;
    public static final int MERGE_ROLL_FLICK = 3;
    public static final int ROLL_15PERCENT = 4;
    public static final int FLICK_15PERCENT = 5;
    public static final int ROLL_ADVANCE = 6;
    public static final int FLICK_ADVANCE = 7;

    @IntDef({ROLL, FLICK, MERGE_ROLL_FLICK, ROLL_15PERCENT, FLICK_15PERCENT, ROLL_ADVANCE, FLICK_ADVANCE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AnimationType {
    }

    // 动画类型的选择
    @AnimationType
    private int animationType;

    // 跑马灯文字移动指定像素所需时间,单位为(秒 /10)
    private int speed;
    // 跑马灯文本隐藏间隔时间,单位为秒
    private int interval;
    // 跑马灯文本显示时间,单位为秒
    private int lifeTime;
    // 跑马灯文本渐隐渐现时间,单位为秒
    private int tweenTime;
    // 当跑马灯暂停时，设置跑马灯是否隐藏
    private boolean isHiddenWhenPause = true;
    // 当跑马灯运行时，是否总是显示
    private boolean isAlwaysShowWhenRun = false;

    // </editor-fold>

    // <editor-fold desc="构造方法">
    public PLVMarqueeAnimationVO() {
        animationType = ROLL;
        speed = 200;
        interval = 5;
        lifeTime = 3;
        tweenTime = 1;
    }
    // </editor-fold>

    // <editor-fold desc="对外API - 参数的设置和使用">
    public int getAnimationType() {
        return animationType;
    }

    public PLVMarqueeAnimationVO setAnimationType(@AnimationType int animationType) {
        this.animationType = animationType;
        return this;
    }

    public PLVMarqueeAnimationVO setSpeed(int speed) {
        if (speed > 0) {
            this.speed = speed;
        }
        return this;
    }

    public PLVMarqueeAnimationVO setInterval(int interval) {
        if (interval >= 0) {
            this.interval = interval;
        }
        return this;
    }

    public PLVMarqueeAnimationVO setLifeTime(int lifeTime) {
        if (lifeTime >= 0) {
            this.lifeTime = lifeTime;
        }
        return this;
    }

    public PLVMarqueeAnimationVO setTweenTime(int tweenTime) {
        if (tweenTime >= 0) {
            this.tweenTime = tweenTime;
        }
        return this;
    }

    public PLVMarqueeAnimationVO setAlwaysShowWhenRun(boolean isAlwaysShowWhenRun) {
        this.isAlwaysShowWhenRun = isAlwaysShowWhenRun;
        return this;
    }

    public PLVMarqueeAnimationVO setHiddenWhenPause(boolean hiddenWhenPause) {
        this.isHiddenWhenPause = hiddenWhenPause;
        return this;
    }

    public int getSpeed() {
        return speed;
    }

    public int getInterval() {
        return interval;
    }

    public int getLifeTime() {
        return lifeTime;
    }

    public int getTweenTime() {
        return tweenTime;
    }

    public boolean isAlwaysShowWhenRun(){
        return isAlwaysShowWhenRun;
    }

    public boolean isHiddenWhenPause(){
        return isHiddenWhenPause;
    }

    // </editor-fold>

}