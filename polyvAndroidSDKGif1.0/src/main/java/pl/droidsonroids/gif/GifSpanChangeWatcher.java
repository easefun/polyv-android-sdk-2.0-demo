package pl.droidsonroids.gif;

import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.SpanWatcher;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextWatcher;

public class GifSpanChangeWatcher implements SpanWatcher, TextWatcher {

	private Drawable.Callback mCallback;

	public GifSpanChangeWatcher(Drawable.Callback callback) {
		mCallback = callback;
	}

	public void onSpanChanged(Spannable buf, Object what, int s, int e, int st, int en) {
		// do nothing
	}

	public void onSpanAdded(Spannable buf, Object what, int s, int e) {
		// 设置callback
		if (what instanceof GifImageSpan) {
			((GifImageSpan) what).getDrawable().setCallback(mCallback);
		}
	}

	public void onSpanRemoved(Spannable buf, Object what, int s, int e) {
		// 清空callback
		// 如果清空，则动画效果取消(?)
		if (what instanceof GifImageSpan) {
			((GifImageSpan) what).getDrawable().setCallback(null);
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		if (s != null) {
			s.setSpan(this, 0, s.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE | (100 << Spanned.SPAN_PRIORITY_SHIFT));
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub

	}

}