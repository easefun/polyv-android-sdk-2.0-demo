package com.easefun.polyvsdk.player.knowledge.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easefun.polyvsdk.util.PolyvScreenUtils;

/**
 * @author suhongtao
 */
public class PolyvPlayerKnowledgeWordTypeView extends RelativeLayout {

    private TextView wordTypeTextView;

    private Paint selectedPaint;

    public PolyvPlayerKnowledgeWordTypeView(Context context) {
        super(context);
        initView();
    }

    public PolyvPlayerKnowledgeWordTypeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public PolyvPlayerKnowledgeWordTypeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        final int paddingHorizontal = PolyvScreenUtils.dip2px(getContext(), 20);
        setPadding(paddingHorizontal, 0, paddingHorizontal, 0);
        setWillNotDraw(false);

        addView(wordTypeTextView = new AppCompatTextView(getContext()) {{
            setTextColor(Color.WHITE);
            setTextSize(16F);
        }}, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT) {{
            addRule(RelativeLayout.CENTER_IN_PARENT, 1);
        }});

        initSelectedPaint();
    }

    private void initSelectedPaint() {
        selectedPaint = new Paint() {{
            setColor(Color.parseColor("#3990FF"));
            setStyle(Style.FILL);
            setAntiAlias(true);
        }};
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isSelected()) {
            canvas.drawRect(
                    wordTypeTextView.getLeft(),
                    getBottom() - PolyvScreenUtils.dip2px(getContext(), 2),
                    wordTypeTextView.getRight(),
                    getBottom(),
                    selectedPaint);
        }
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected) {
            wordTypeTextView.setAlpha(1F);
        } else {
            wordTypeTextView.setAlpha(0.6F);
        }
    }

    public void setWordTypeName(String wordTypeName) {
        wordTypeTextView.setText(wordTypeName);
    }

}
