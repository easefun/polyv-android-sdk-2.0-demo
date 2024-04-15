package com.easefun.polyvsdk.marquee;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ScaleXSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.easefun.polyvsdk.util.PolyvScreenUtils;

/**
 * 增强显示 的 文字类
 */
public class PLVStrokeTextView extends TextView {

    // <editor-fold desc="变量">
    private float spacing;
    private CharSequence srcText;
    private boolean hasStroke;
    private int strokeWidth;
    private int strokeColor;
    private int strokeBlurX = 0;
    private int strokeBlurY = 0;
    // </editor-fold>

    // <editor-fold desc="构造方法">
    public PLVStrokeTextView(Context context) {
        super(context);
    }

    public PLVStrokeTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PLVStrokeTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    // </editor-fold>

    // <editor-fold desc="对外API">
    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setSpacing(float spacing) {
        this.spacing = spacing;
        if (spacing > 0) {
            applySpacing();
        }
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        srcText = text;
        if (spacing > 0) {
            applySpacing();
        } else {
            super.setText(text, type);
        }
    }
    // </editor-fold>

    // <editor-fold desc="功能模块- 参数设置">
    protected void setHasStroke(boolean hasStroke) {
        this.hasStroke = hasStroke;
    }

    protected void setStrokeBlurX(int strokeBlurX) {
        this.strokeBlurX = strokeBlurX;
    }

    protected void setStrokeBlurY(int strokeBlurY) {
        this.strokeBlurY = strokeBlurY;
    }

    protected void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    protected void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
    }
    // </editor-fold>

    // <editor-fold desc="功能模块- 绘制操作">
    private void applySpacing() {
        if (srcText == null) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < srcText.length(); i++) {
            builder.append(srcText.charAt(i));
            if (i + 1 < srcText.length()) {
                // \u00A0 不间断空格
                // 追加空格
                builder.append("\u00A0");
            }
        }
        SpannableString finalText = new SpannableString(builder.toString());
        if (builder.toString().length() > 1) { // 如果当前TextView内容长度大于1，则进行空格添加
            for (int i = 1; i < builder.toString().length(); i += 2) { // 小demo：100  1 0 0
                // 按照x轴等比例进行缩放 通过我们设置的字间距+1除以10进行等比缩放
                finalText.setSpan(new ScaleXSpan(spacing), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        super.setText(finalText, BufferType.SPANNABLE);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        if (hasStroke) {
            // first get because strokeColor will set.
            ColorStateList states = getTextColors();
            getPaint().setStyle(Paint.Style.STROKE);
            getPaint().setStrokeWidth(/*strokeWidth*/PolyvScreenUtils.dip2px(getContext(), strokeWidth));
            // BlurMaskFilter.Blur.INNER textColor red "e" has a problem.and all is has...if "e" textSize is small.
            if (strokeBlurX > 0 && strokeBlurY > 0) {
                int strokeBlur = Math.max(strokeBlurX, strokeBlurY);
                // if no set,it mask can cover other textView same text.
                disableHardwareRendering(this);
                getPaint().setMaskFilter(
                        new BlurMaskFilter(/*strokeWidth*/PolyvScreenUtils.dip2px(getContext(),
                                strokeBlur),
                                BlurMaskFilter.Blur.SOLID));
            }
            setTextColor(strokeColor);
            super.onDraw(canvas);

            getPaint().setStyle(Paint.Style.FILL);
            setTextColor(states);
        }
        super.onDraw(canvas);
    }

    public static void disableHardwareRendering(View v) {
        v.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }
    // </editor-fold>
}
