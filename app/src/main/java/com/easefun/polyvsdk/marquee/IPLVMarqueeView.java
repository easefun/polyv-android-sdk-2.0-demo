package com.easefun.polyvsdk.marquee;

import com.easefun.polyvsdk.marquee.model.PLVMarqueeModel;

/**
 * 跑马灯 对外调用的接口
 */
public interface IPLVMarqueeView {
    /**
     * 更新跑马灯字体和动画样式
     *
     * @param marqueeVO
     */
    void setPLVMarqueeModel(PLVMarqueeModel marqueeVO);

    /**
     * 开始播放跑马灯
     */
    void start();

    /**
     * 暂停播放跑马灯
     */
    void pause();

    /**
     * 停止播放跑马灯
     */
    void stop();
}
