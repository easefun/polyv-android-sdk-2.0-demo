package com.easefun.polyvsdk.marquee;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.Nullable;
import android.util.AttributeSet;

import com.easefun.polyvsdk.marquee.model.PLVMarqueeTextVO;

/**
 * 跑马灯view 的实现类
 */
public class PLVMarqueeTextView extends PLVStrokeTextView {

    // <editor-fold desc="变量">
    private PLVMarqueeTextVO textModel;
    // </editor-fold>

    // <editor-fold desc="构造方法">
    public PLVMarqueeTextView(Context context) {
        super(context);
    }

    public PLVMarqueeTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PLVMarqueeTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="View方法重写">

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    // </editor-fold>

    // <editor-fold desc="对外API - 设置参数">
    public void setMarqueeTextModel(PLVMarqueeTextVO textVO) {
        this.textModel = textVO;
        setText(textVO.getContent());
        setFontStyle();
        setFilterStyle();
    }
    // </editor-fold>

    // <editor-fold desc="功能模块 - 设置样式">
    // 设置字体样式
    private void setFontStyle() {
        setTextSize(textModel.getFontSize());
        setTextColor(Color.argb(textModel.getFontAlpha(),
                Color.red(textModel.getFontColor()),
                Color.green(textModel.getFontColor()),
                Color.blue(textModel.getFontColor())));
    }

    // 设置描边样式
    private void setFilterStyle() {
        setHasStroke(textModel.isFilter());

        setStrokeWidth(textModel.getFilterStrength());

        setStrokeBlurX(textModel.getFilterBlurX());
        setStrokeBlurY(textModel.getFilterBlurY());

        int strokeColor = Color.argb((int) (textModel.getFilterAlpha()),
                Color.red(textModel.getFilterColor()),
                Color.green(textModel.getFilterColor()),
                Color.blue(textModel.getFilterColor()));
        setStrokeColor(strokeColor);

        invalidate();
    }
    // </editor-fold>
}
