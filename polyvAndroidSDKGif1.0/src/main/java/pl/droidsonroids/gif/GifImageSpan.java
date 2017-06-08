package pl.droidsonroids.gif;

import android.graphics.drawable.Drawable;

public class GifImageSpan extends RelativeImageSpan{

	private Drawable mDrawable = null;

	public GifImageSpan(Drawable d) {
		super(d);
		mDrawable = d;
	}

	public GifImageSpan(Drawable d, int verticalAlignment) {
		super(d, verticalAlignment);
		mDrawable = d;
	}

	public GifImageSpan(Drawable d, int verticalAlignment,float spacingScale) {
		super(d, verticalAlignment,spacingScale);
		mDrawable = d;
	}

	@Override
	public Drawable getDrawable() {
		return mDrawable;
	}
}
