package com.easefun.polyvsdk.view;

import com.easefun.polyvsdk.R;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

public class PolyvGrayImageView extends ImageView {

	public PolyvGrayImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public PolyvGrayImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PolyvGrayImageView(Context context) {
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (isPressed())
			// canvas.drawColor(0x33000000);
			canvas.drawColor(getResources().getColor(R.color.commom_click_color_gray_half));
	}

	@Override
	protected void dispatchSetPressed(boolean pressed) {
		super.dispatchSetPressed(pressed);
		invalidate();
	}
}
