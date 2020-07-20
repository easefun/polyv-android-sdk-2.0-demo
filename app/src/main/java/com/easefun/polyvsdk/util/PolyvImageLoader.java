package com.easefun.polyvsdk.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

/**
 * 图片二次加载框架
 */
public class PolyvImageLoader {


    // <editor-fold defaultstate="collapsed" desc="单例">
    private static PolyvImageLoader INSTANCE;

    private PolyvImageLoader() {/**/}

    public static PolyvImageLoader getInstance() {
        if (INSTANCE == null) {
            synchronized (PolyvImageLoader.class) {
                if (INSTANCE == null) {
                    INSTANCE = new PolyvImageLoader();
                }
            }
        }
        return INSTANCE;
    }
    // </editor-fold>


    /**
     * 加载图片
     */
    public void loadImage(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .into(imageView);
    }

    /**
     * 加载图片
     */
    public void loadImage(Context context, @DrawableRes Integer resourceId, ImageView imageView) {
        Glide.with(context)
                .load(resourceId)
                .into(imageView);
    }

    /**
     * 预加载
     * @param context
     * @param url
     */
    public void preloadImage(Context context, String url){
        RequestOptions imgOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(Target.SIZE_ORIGINAL);
        Glide.with(context).load(url).apply(imgOptions).preload();
    }

    /**
     * 原始尺寸加载图片
     * @param context
     * @param url
     * @param imageView
     */
    public void loadImageOrigin(Context context, String url, ImageView imageView, @DrawableRes int placeHolderId) {
        RequestOptions options = new RequestOptions().override(Target.SIZE_ORIGINAL)
                .placeholder(placeHolderId)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        loadImageWithOptions(context, url, imageView, options);
    }

    /**
     * 原始尺寸加载图片
     * @param context
     * @param url
     * @param imageView
     */
    public void loadImageOrigin(Context context, String url, ImageView imageView, Drawable drawable) {
        RequestOptions options = new RequestOptions().override(Target.SIZE_ORIGINAL)
                .placeholder(drawable)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        loadImageWithOptions(context, url, imageView, options);
    }

    public void loadImageOriginCircle(Context context, String url, ImageView imageView, @DrawableRes int placeHolderId){
        RequestOptions options = new RequestOptions()
                .placeholder(placeHolderId)
                .bitmapTransform(new CircleCrop())
                .override(Target.SIZE_ORIGINAL).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        loadImageWithOptions(context, url, imageView, options);
    }


    /**
     * 加载图片，开启自动缓存策略
     * @param context
     * @param url
     * @param imageView
     */
    public void loadImageWithCache(Context context, String url, ImageView imageView, @DrawableRes int placeHolderId) {
        RequestOptions options = new RequestOptions().dontAnimate()
                .placeholder(placeHolderId)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
        loadImageWithOptions(context, url, imageView, options);
    }

    /**
     * 加载图片，关闭缓存策略
     * @param context
     * @param url
     * @param imageView
     */
    public void loadImageNoCache(Context context, String url, ImageView imageView, @DrawableRes int placeHolderId) {
        RequestOptions options = new RequestOptions().dontAnimate()
                .placeholder(placeHolderId)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE);
        loadImageWithOptions(context, url, imageView, options);
    }

    /**
     * 加载图片，自定义配置
     * @param context
     * @param url
     * @param imageView
     * @param options 自定义配置
     */
    public void loadImageWithOptions(Context context, String url, ImageView imageView, RequestOptions options){
        Glide.with(context)
                .load(url)
                .apply(options)
                .into(imageView);
    }




}
