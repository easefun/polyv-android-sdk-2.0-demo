package com.easefun.polyvsdk.marquee.model;

import android.graphics.Color;

/**
 * 跑马灯 字体样式的描述类
 */
public class PLVMarqueeTextVO {

    // <editor-fold desc="变量">
    private String content;

    private int fontColor;
    private int fontSize;
    private int fontAlpha;

    private boolean isFilter;
    private float filterAlpha;
    private int filterColor;
    private int filterBlurX;
    private int filterBlurY;
    private int filterStrength;
    // </editor-fold>

    // <editor-fold desc="构造方法">
    public PLVMarqueeTextVO() {
        content = "Polyv跑马灯";

        fontColor = Color.BLACK;
        fontSize = 30;
        fontAlpha = 255;

        isFilter = false;
        filterColor = Color.BLACK;
        filterAlpha = 255;
        filterStrength = 4;
        filterBlurX = 2;
        filterBlurY = 2;
    }
    // </editor-fold>

    // <editor-fold desc="对外API - 参数的设置和使用">
    public int getFontColor() {
        return fontColor;
    }

    public PLVMarqueeTextVO setFontColor(int fontColor) {
        this.fontColor = fontColor;
        return this;
    }

    public int getFontSize() {
        return fontSize;
    }

    public PLVMarqueeTextVO setFontSize(int fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    public int getFontAlpha() {
        return fontAlpha;
    }

    public PLVMarqueeTextVO setFontAlpha(int fontAlpha) {
        this.fontAlpha = fontAlpha;
        return this;
    }

    public boolean isFilter() {
        return isFilter;
    }

    public PLVMarqueeTextVO setFilter(boolean filter) {
        isFilter = filter;
        return this;
    }

    public float getFilterAlpha() {
        return filterAlpha;
    }

    public PLVMarqueeTextVO setFilterAlpha(float filterAlpha) {
        this.filterAlpha = filterAlpha;
        return this;
    }

    public int getFilterColor() {
        return filterColor;
    }

    public PLVMarqueeTextVO setFilterColor(int filterColor) {
        this.filterColor = filterColor;
        return this;
    }

    public int getFilterBlurX() {
        return filterBlurX;
    }

    public PLVMarqueeTextVO setFilterBlurX(int filterBlurX) {
        this.filterBlurX = filterBlurX;
        return this;
    }

    public int getFilterBlurY() {
        return filterBlurY;
    }

    public PLVMarqueeTextVO setFilterBlurY(int filterBlurY) {
        this.filterBlurY = filterBlurY;
        return this;
    }

    public int getFilterStrength() {
        return filterStrength;
    }

    public PLVMarqueeTextVO setFilterStrength(int filterStrength) {
        this.filterStrength = filterStrength;
        return this;
    }

    public String getContent() {
        return content;
    }

    public PLVMarqueeTextVO setContent(String content) {
        this.content = content;
        return this;
    }
    // </editor-fold>
}

