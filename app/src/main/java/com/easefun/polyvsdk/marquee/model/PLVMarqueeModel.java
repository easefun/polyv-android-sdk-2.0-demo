package com.easefun.polyvsdk.marquee.model;


import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 跑马灯 设置的字体样式 和 动画样式的描述类
 */
public class PLVMarqueeModel {

    // <editor-fold desc="变量">
    private static final String TAG = "PLVMarqueeModel";
    private PLVMarqueeTextVO mainTextVO;
    private PLVMarqueeTextVO secondTextVO;

    private PLVMarqueeAnimationVO animationVO;
    // </editor-fold>

    // <editor-fold desc="构造方法">
    public PLVMarqueeModel() {
        mainTextVO = new PLVMarqueeTextVO();
        secondTextVO = new PLVMarqueeTextVO();
        animationVO = new PLVMarqueeAnimationVO();
    }
    // </editor-fold>

    // <editor-fold desc="对外API - 参数的设置和使用">
    public PLVMarqueeTextVO getMainTextVO() {
        return mainTextVO;
    }

    public PLVMarqueeTextVO getSecondTextVO() {
        return secondTextVO;
    }

    public PLVMarqueeAnimationVO getAnimationVO() {
        return animationVO;
    }

    public PLVMarqueeModel setUserName(String userName) {
        mainTextVO.setContent(userName);
        return this;
    }

    public PLVMarqueeModel setFontColor(int color) {
        mainTextVO.setFontColor(color);
        return this;
    }

    public PLVMarqueeModel setFontSize(int fontSize) {
        mainTextVO.setFontSize(fontSize);
        return this;
    }

    public PLVMarqueeModel setFontAlpha(int fontAlpha) {
        mainTextVO.setFontAlpha(fontAlpha);
        return this;
    }

    public PLVMarqueeModel setFilter(boolean isFilter) {
        mainTextVO.setFilter(isFilter);
        return this;
    }

    public PLVMarqueeModel setFilterColor(int color) {
        mainTextVO.setFilterColor(color);
        return this;
    }

    public PLVMarqueeModel setFilterAlpha(int filterAlpha) {
        mainTextVO.setFilterAlpha(filterAlpha);
        return this;
    }

    public PLVMarqueeModel setFilterStrength(int strength) {
        mainTextVO.setFilterStrength(strength);
        return this;
    }

    public PLVMarqueeModel setFilterBlurX(int filterBlurX) {
        mainTextVO.setFilterBlurX(filterBlurX);
        return this;
    }

    public PLVMarqueeModel setFilterBlurY(int filterBlurY) {
        mainTextVO.setFilterBlurY(filterBlurY);
        return this;
    }

    @PLVMarqueeAnimationVO.AnimationType
    public int getSetting() {
        return animationVO.getAnimationType();
    }

    public PLVMarqueeModel setSetting(@PLVMarqueeAnimationVO.AnimationType int animationType) {
        animationVO.setAnimationType(animationType);
        return this;
    }

    public PLVMarqueeModel setSpeed(int speed) {
        animationVO.setSpeed(speed);
        return this;
    }

    public PLVMarqueeModel setInterval(int interval) {
        animationVO.setInterval(interval);
        return this;
    }

    public PLVMarqueeModel setLifeTime(int lifeTime) {
        animationVO.setLifeTime(lifeTime);
        return this;
    }

    public PLVMarqueeModel setTweenTime(int tweenTime) {
        animationVO.setTweenTime(tweenTime);
        return this;
    }

    public PLVMarqueeModel setAlwaysShowWhenRun(boolean isAlwaysShowWhenRun) {
        animationVO.setAlwaysShowWhenRun(isAlwaysShowWhenRun);
        return this;
    }

    public PLVMarqueeModel setHiddenWhenPause(boolean hiddenWhenPause) {
        animationVO.setHiddenWhenPause(hiddenWhenPause);
        return this;
    }

    /**
     * 注意，该方法需要在确定了animationType 和 mainTextModel后，再执行
     * 只被PLVMarqueeView调用
     */
    public void setSecondDefaultTextVO() {
        switch (animationVO.getAnimationType()) {
            case PLVMarqueeAnimationVO.ROLL_ADVANCE:
            case PLVMarqueeAnimationVO.FLICK_ADVANCE:
                secondTextVO.setContent(mainTextVO.getContent());
                secondTextVO.setFontColor(mainTextVO.getFontColor());
                secondTextVO.setFontSize(mainTextVO.getFontSize());
                secondTextVO.setFontAlpha((int) (0.01F * 255));
                secondTextVO.setFilter(mainTextVO.isFilter());
                secondTextVO.setFilterBlurY(mainTextVO.getFilterBlurY());
                secondTextVO.setFilterBlurX(mainTextVO.getFilterBlurX());
                secondTextVO.setFilterStrength(mainTextVO.getFilterStrength());
                secondTextVO.setFilterAlpha((int) (0.01F * 255));
                secondTextVO.setFilterColor(mainTextVO.getFilterColor());
                break;
            default:
                break;
        }
    }

    public void setParamsByJsonObject(JSONObject jsonObject) {
        if (jsonObject != null) {
            try {
                if (!jsonObject.isNull("userName")) {
                    Log.i(TAG, "setParamsByJsonObject: userName:" + jsonObject.getString("userName"));
                    setUserName(jsonObject.getString("userName"));
                }
                if (!jsonObject.isNull("fontSize")) {
                    Log.i(TAG, "setFontSize: fontSize:" + jsonObject.getInt("fontSize"));
                    setFontSize(jsonObject.getInt("fontSize"));
                }
                if (!jsonObject.isNull("fontColor")) {
                    Log.i(TAG, "setFontColor: fontColor:" + jsonObject.getInt("fontColor"));
                    setFontColor(jsonObject.getInt("fontColor"));
                }
                if (!jsonObject.isNull("fontAlpha")) {
                    Log.i(TAG, "setFontAlpha: fontAlpha:" + jsonObject.getInt("fontAlpha"));
                    setFontAlpha(jsonObject.getInt("fontAlpha"));
                }
                if (!jsonObject.isNull("filter")) {
                    Log.i(TAG, "setFilter: filter:" + jsonObject.getBoolean("filter"));
                    setFilter(jsonObject.getBoolean("filter"));
                }
                if (!jsonObject.isNull("filterColor")) {
                    Log.i(TAG, "setFilterColor: filterColor:" + jsonObject.getInt("filterColor"));
                    setFilterColor(jsonObject.getInt("filterColor"));
                }
                if (!jsonObject.isNull("filterBlurX")) {
                    Log.i(TAG, "setFilterBlurX: filterBlurX:" + jsonObject.getInt("filterBlurX"));
                    setFilterBlurX(jsonObject.getInt("filterBlurX"));
                }
                if (!jsonObject.isNull("filterBlurY")) {
                    Log.i(TAG, "setFilterBlurX: filterBlurY:" + jsonObject.getInt("filterBlurY"));
                    setFilterBlurY(jsonObject.getInt("filterBlurY"));
                }
                if (!jsonObject.isNull("filterAlpha")) {
                    Log.i(TAG, "setFilterAlpha: filterAlpha:" + jsonObject.getInt("filterAlpha"));
                    setFilterAlpha(jsonObject.getInt("filterAlpha"));
                }
                if (!jsonObject.isNull("filterStrength")) {
                    Log.i(TAG, "setFilterStrength: filterStrength:" + jsonObject.getInt("filterStrength"));
                    setFilterStrength(jsonObject.getInt("filterStrength"));
                }
                if (!jsonObject.isNull("setting")) {
                    Log.i(TAG, "setSetting: setting:" + jsonObject.getInt("setting"));
                    setSetting(jsonObject.getInt("setting"));
                }
                if (!jsonObject.isNull("speed")) {
                    Log.i(TAG, "setSpeed: speed:" + jsonObject.getInt("speed"));
                    setSpeed(jsonObject.getInt("speed"));
                }
                if (!jsonObject.isNull("interval")) {
                    Log.i(TAG, "setInterval: interval:" + jsonObject.getInt("interval"));
                    setInterval(jsonObject.getInt("interval"));
                }
                if (!jsonObject.isNull("lifeTime")) {
                    Log.i(TAG, "setLifeTime: lifeTime:" + jsonObject.getInt("lifeTime"));
                    setLifeTime(jsonObject.getInt("lifeTime"));
                }
                if (!jsonObject.isNull("tweenTime")) {
                    Log.i(TAG, "setTweenTime: tweenTime:" + jsonObject.getInt("tweenTime"));
                    setTweenTime(jsonObject.getInt("tweenTime"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    // </editor-fold>
}
