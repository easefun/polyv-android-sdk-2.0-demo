package com.easefun.polyvsdk.marquee.animation;

import android.view.View;

import java.util.HashMap;

/**
 * 动画类 的基本类
 */
public abstract class PLVMarqueeAnimation {

    // <editor-fold desc="常量">
    // 生命周期的标识常量
    protected static final int IDLE = 0;
    protected static final int STARTED = 1;
    protected static final int PAUSE = 2;
    protected static final int STOP = 3;

    // 传入参数的标识常量
    public static final int PARAM_VIEW_WIDTH = 0;
    public static final int PARAM_VIEW_HEIGHT = 1;
    public static final int PARAM_SCREEN_WIDTH = 2;
    public static final int PARAM_SCREEN_HEIGHT = 3;
    public static final int PARAM_DURATION = 4;
    public static final int PARAM_LIFE_TIME = 5;
    public static final int PARAM_TWEEN_TIME = 6;
    public static final int PARAM_INTERVAL = 7;
    public static final int PARAM_SECOND_VIEW_WIDTH = 8;
    public static final int PARAM_SECOND_VIEW_HEIGHT = 9;

    public static final int PARAM_ISALWAYS_SHOW_WHEN_RUN = 10;
    public static final int PARAM_HIDE_WHEN_PAUSE = 11;

    public static final int VIEW_MAIN = 20;
    public static final int VIEW_SECOND = 21;
    // </editor-fold>

    // <editor-fold desc="变量">
    // 动画状态
    protected int animationStatus = IDLE;
    // 动画主要参数变量
    protected int viewWidth;
    protected int viewHeight;
    protected int screenWidth;
    protected int screenHeight;

    protected boolean isAlwaysShowWhenRun = false;
    protected boolean isHiddenWhenPause = true;
    // </editor-fold>

    // <editor-fold desc="公共对外API - 参数设置">
    /**
     * 设置要添加动画的view
     *
     * @param viewMap
     */
    public abstract void setViews(HashMap<Integer, View> viewMap);

    /**
     * 设置参数
     *
     * @param paramMap
     */
    public abstract void setParams(HashMap<Integer, Integer> paramMap);
    // </editor-fold>

    // <editor-fold desc="公共对外API - 生命周期的控制">
    /**
     * 开始生成并运行动画
     */
    public abstract void start();

    /**
     * 暂停动画的播放
     */
    public abstract void pause();

    /**
     * 停止动画
     */
    public abstract void stop();

    /**
     * 销毁动画
     */
    public abstract void destroy();

    /**
     * 当画面配置切换时，做修改
     *
     * @param parentView
     */
    public abstract void onParentSizeChanged(View parentView);
    // </editor-fold>
}
