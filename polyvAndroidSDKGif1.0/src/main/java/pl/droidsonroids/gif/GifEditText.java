package pl.droidsonroids.gif;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.EditText;

public class GifEditText extends EditText {
	
	private GifSpanChangeWatcher mGifSpanChangeWatcher;
	public GifEditText(Context context) {
		super(context);
		initGifSpanChangeWatcher();
	}

	public GifEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		initGifSpanChangeWatcher();
	}

	public GifEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initGifSpanChangeWatcher();
	}

	private void initGifSpanChangeWatcher() {
		mGifSpanChangeWatcher = new GifSpanChangeWatcher(this);
		addTextChangedListener(mGifSpanChangeWatcher);
	}
	
	@Override
	public void setText(CharSequence text, BufferType type) {

		CharSequence oldText = null;
		try {
			//EditText的默认mText为""，是一个String，但getText()强转为Editable，只能try/catch了
			oldText = getText();
			//首先清空所有旧GifImageSpan的callback和oldText上的GifSpanChangeWatcher
			if (!TextUtils.isEmpty(oldText) && oldText instanceof Spannable) {
				Spannable sp = (Spannable) oldText;
				final GifImageSpan[] spans = sp.getSpans(0, sp.length(), GifImageSpan.class);
		        final int count = spans.length;
		        for (int i = 0; i < count; i++) {
		        	spans[i].getDrawable().setCallback(null);
		        }
		        
		        final GifSpanChangeWatcher[] watchers = sp.getSpans(0, sp.length(), GifSpanChangeWatcher.class);
	            final int count1 = watchers.length;
	            for (int i = 0; i < count1; i++) {
	                sp.removeSpan(watchers[i]);
	            }
			}
		} catch (Exception e) {
			
		}
		
		
		if (!TextUtils.isEmpty(text)) {
			if (!(text instanceof Editable)) {
				text = new SpannableStringBuilder(text);
			}
		}
		
		if (!TextUtils.isEmpty(text) && text instanceof Spannable) {
			Spannable sp = (Spannable) text;
			//设置新text中所有GifImageSpan的callback为当前EditText
			final GifImageSpan[] spans = sp.getSpans(0, sp.length(), GifImageSpan.class);
	        final int count = spans.length;
	        for (int i = 0; i < count; i++) {
	        	spans[i].getDrawable().setCallback(this);
	        }
	        
	        //清空新text上的GifSpanChangeWatcher
	        final GifSpanChangeWatcher[] watchers = sp.getSpans(0, sp.length(), GifSpanChangeWatcher.class);
            final int count1 = watchers.length;
            for (int i = 0; i < count1; i++) {
                sp.removeSpan(watchers[i]);
            }
            
	        if (mGifSpanChangeWatcher == null) {
				mGifSpanChangeWatcher = new GifSpanChangeWatcher(this);
			}
			
	        //设置新text上的GifSpanChangeWatcher
			sp.setSpan(mGifSpanChangeWatcher, 0, text.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE | (100 << Spanned.SPAN_PRIORITY_SHIFT));
		}

		super.setText(text, type);
	}
	
	private GifImageSpan getImageSpan(Drawable drawable) {
		GifImageSpan imageSpan = null;
		CharSequence text = getText();
		if (!TextUtils.isEmpty(text)) {
			if (text instanceof Spanned) {
				Spanned spanned = (Spanned) text;
				GifImageSpan[] spans = spanned.getSpans(0, text.length(), GifImageSpan.class);
				if (spans != null && spans.length > 0) {
					for (GifImageSpan span : spans) {
						if (drawable == span.getDrawable()) {
							imageSpan = span;
						}
					}
				}
			}
		}

		return imageSpan;
	}

	@Override
	public void invalidateDrawable(Drawable drawable) {
		GifImageSpan imageSpan = getImageSpan(drawable);
		if (imageSpan != null) {
			CharSequence text = getText();
			if (!TextUtils.isEmpty(text)) {
				if (text instanceof Editable) {
					Editable editable = (Editable)text;
					int start = editable.getSpanStart(imageSpan);
					int end = editable.getSpanEnd(imageSpan);
					int flags = editable.getSpanFlags(imageSpan);

					editable.setSpan(imageSpan, start, end, flags);
				}
			}
			
		} else {
			super.invalidateDrawable(drawable);
		}
	}
}