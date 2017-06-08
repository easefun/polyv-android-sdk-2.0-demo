package pl.droidsonroids.gif;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

public class RelativeImageSpan extends ImageSpan {
	public static final int ALIGN_BOTTOM = ImageSpan.ALIGN_BOTTOM;
	public static final int ALIGN_BASELINE = ImageSpan.ALIGN_BASELINE;
	public static final int ALIGN_TEXTBOTTOM = 2;
	public static final int ALIGN_CENTER = 3;
	private int status = ALIGN_BOTTOM;
	private float scale = 1.0f;

	public RelativeImageSpan(Drawable d) {
		super(d);
	}

	public RelativeImageSpan(Drawable d, int verticalAlignment) {
		super(d, verticalAlignment);
		status = verticalAlignment;
	}

	public RelativeImageSpan(Drawable d, int verticalAlignment, float spacingScale) {
		super(d, verticalAlignment);
		status = verticalAlignment;
		scale = spacingScale;
	}

	@Override
	public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom,
					 Paint paint) {
		Drawable b = getDrawable();
		canvas.save();
		int transY = 0;
		switch (status) {
			case ALIGN_CENTER:
				// 获得将要显示的文本高度-图片高度除2等居中位置+top(换行情况)
				transY = ((bottom - top) - b.getBounds().bottom) / 2 + top;
				break;
			case ALIGN_BOTTOM:
				transY = bottom - b.getBounds().bottom;
				break;
			case ALIGN_BASELINE:
				transY = bottom - b.getBounds().bottom;
				transY -= paint.getFontMetricsInt().descent;
				break;
			case ALIGN_TEXTBOTTOM:
				transY = bottom - b.getBounds().bottom;
				transY -= paint.getFontMetricsInt().descent - bottom / 4;
				break;
		}
		// 偏移画布后开始绘制
		canvas.translate(x + 3*scale, transY);
		b.draw(canvas);
		canvas.restore();
	}

	@Override
	public int getSize(Paint paint, CharSequence text, int start, int end, FontMetricsInt fm) {
		Drawable d = getDrawable();
		Rect rect = d.getBounds();
		switch (status) {
			case ALIGN_CENTER:
				if (fm != null) {
					FontMetricsInt fmPaint = paint.getFontMetricsInt();
					// 获得文字、图片高度
					int fontHeight = fmPaint.bottom - fmPaint.top;
					int drHeight = rect.bottom - rect.top;
					// 对于这段算法LZ表示也不解，正常逻辑应该同draw中的计算一样但是显示的结果不居中，经过几次调试之后才发现这么算才会居中
					int top = drHeight / 2 - fontHeight / 4;
					int bottom = drHeight / 2 + fontHeight / 4;

					fm.ascent = -bottom;
					fm.top = -bottom;
					fm.bottom = top;
					fm.descent = top;
				}
				break;

			default:
				if (fm != null) {
					fm.ascent = -rect.bottom;
					fm.descent = 0;

					fm.top = fm.ascent;
					fm.bottom = 0;
				}
				break;
		}
		return rect.right + ((int)(3*scale*2));
	}
}
