package com.easefun.polyvsdk.util;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.widget.TextView;

import com.easefun.polyvsdk.sub.auxilliary.cache.image.ImageData;
import com.easefun.polyvsdk.sub.auxilliary.cache.image.ImageLoader;

import java.lang.ref.WeakReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.droidsonroids.gif.GifTextView;
import pl.droidsonroids.gif.RelativeImageSpan;

public class PolyvTextImageLoader {
    private final String DEFAULT_STRING = "[图片]";
    private Context context;
    private ImageLoader imageLoader;

    public PolyvTextImageLoader(Context context) {
        this.context = context;
        this.imageLoader = ImageLoader.getInstance(context, 3, ImageLoader.Type.LIFO);
    }

    public static class MyImageLoaderListener implements ImageLoader.ImageLoaderListener {

        private CharSequence charSequence;
        private WeakReference<TextView> wr_textView;
        private SpannableStringBuilder span;
        private int start;
        private int end;

        public MyImageLoaderListener(TextView textView, CharSequence charSequence, SpannableStringBuilder span, int start, int end) {
            this.wr_textView = new WeakReference<TextView>(textView);
            this.charSequence = charSequence;
            this.span = span;
            this.start = start;
            this.end = end;
        }

        @Override
        public void fail(Throwable throwable) {
        }

        @Override
        public void success(ImageData imageData, String s) {
            TextView textView = wr_textView.get();
            if (textView != null && textView.getTag() != null && textView.getTag().equals(charSequence)) {
                ImageSpan imageSpan = ImageLoader.toImageSpan(imageData.getDrawable(), s, RelativeImageSpan.ALIGN_CENTER);
                span.setSpan(imageSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView.clearComposingText();
                textView.setText(span);
            }
        }
    }

    // 显示网络图片
    public void displayTextImage(CharSequence charSequence, GifTextView textView) {
        textView.setTag(charSequence);
        float size = textView.getTextSize();
        int reqWidth;
        int reqHeight;
        reqWidth = reqHeight = (int) size;
        SpannableStringBuilder span = new SpannableStringBuilder(charSequence);
        int start = 0;
        int end = 0;
        int oldLength = span.length();
        int newLength = 0;
        Pattern pattern = Pattern.compile("<img[^<]*/>");
        Matcher matcher = pattern.matcher(charSequence);
        while (matcher.find()) {
            start = matcher.start();
            end = matcher.end();
            String group = matcher.group();
            Pattern pattern_src = Pattern.compile("src\\S*");
            Matcher matcher_src = pattern_src.matcher(group);
            if (matcher_src.find()) {
                String group_src = matcher_src.group();
                // 图片的url
                String url = group_src.substring(5, group_src.length() - 1);
                newLength = span.length();
                if (newLength < oldLength)
                    span.replace(start = start - (oldLength - newLength), end - (oldLength - newLength), DEFAULT_STRING);
                else
                    span.replace(start, end, DEFAULT_STRING);
                ImageData imageData = imageLoader.start(context, new int[]{reqWidth, reqHeight}, url, new MyImageLoaderListener(textView, charSequence, span, start, start + DEFAULT_STRING.length()));
                if (imageData != null) {
                    if (textView.getTag() != null && textView.getTag().equals(charSequence)) {
                        ImageSpan imageSpan = ImageLoader.toImageSpan(imageData.getDrawable(), url, RelativeImageSpan.ALIGN_CENTER);
                        span.setSpan(imageSpan, start, start + DEFAULT_STRING.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        textView.setText(span);
                    }
                }
            }
        }
        textView.setText(span);
    }
}
