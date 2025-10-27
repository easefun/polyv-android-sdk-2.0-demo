package com.easefun.polyvsdk.util;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Android防抖工具类 - Java版本
 * 支持多种防抖场景：点击防抖、搜索防抖、网络请求防抖等
 */
public class PLVDebounceUtil {

    private static final Handler handler = new Handler(Looper.getMainLooper());
    private static final ConcurrentHashMap<String, Runnable> runnableMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Long> lastClickTimeMap = new ConcurrentHashMap<>();

    /**
     * 点击防抖 - 防止重复点击
     * @param key 防抖标识，通常使用View的id或自定义标识
     * @param delayMillis 防抖间隔时间（毫秒），默认500ms
     * @param action 要执行的操作
     */
    public static void clickDebounce(String key, long delayMillis, Runnable action) {
        long currentTime = System.currentTimeMillis();
        Long lastClickTime = lastClickTimeMap.get(key);
        if (lastClickTime == null) {
            lastClickTime = 0L;
        }

        if (currentTime - lastClickTime >= delayMillis) {
            lastClickTimeMap.put(key, currentTime);
            action.run();
        }
    }

    /**
     * 点击防抖 - 使用默认500ms间隔
     * @param key 防抖标识
     * @param action 要执行的操作
     */
    public static void clickDebounce(String key, Runnable action) {
        clickDebounce(key, 500L, action);
    }

    /**
     * 延迟防抖 - 延迟执行，如果在延迟期间再次调用则重新计时
     * @param key 防抖标识
     * @param delayMillis 延迟时间（毫秒）
     * @param action 要执行的操作
     */
    public static void delayDebounce(final String key, long delayMillis, final Runnable action) {
        // 取消之前的任务
        Runnable oldRunnable = runnableMap.get(key);
        if (oldRunnable != null) {
            handler.removeCallbacks(oldRunnable);
        }

        // 创建新的任务
        Runnable newRunnable = new Runnable() {
            @Override
            public void run() {
                action.run();
                runnableMap.remove(key);
            }
        };

        runnableMap.put(key, newRunnable);
        handler.postDelayed(newRunnable, delayMillis);
    }

    /**
     * 搜索防抖 - 适用于搜索框输入防抖
     * @param key 防抖标识
     * @param delayMillis 防抖间隔时间（毫秒），默认300ms
     * @param action 要执行的操作
     */
    public static void searchDebounce(String key, long delayMillis, Runnable action) {
        delayDebounce(key, delayMillis, action);
    }

    /**
     * 搜索防抖 - 使用默认300ms间隔
     * @param key 防抖标识
     * @param action 要执行的操作
     */
    public static void searchDebounce(String key, Runnable action) {
        searchDebounce(key, 300L, action);
    }

    /**
     * 网络请求防抖 - 防止重复网络请求
     * @param key 防抖标识
     * @param delayMillis 防抖间隔时间（毫秒），默认1000ms
     * @param action 要执行的操作
     */
    public static void networkDebounce(String key, long delayMillis, Runnable action) {
        clickDebounce(key, delayMillis, action);
    }

    /**
     * 网络请求防抖 - 使用默认1000ms间隔
     * @param key 防抖标识
     * @param action 要执行的操作
     */
    public static void networkDebounce(String key, Runnable action) {
        networkDebounce(key, 1000L, action);
    }

    /**
     * 取消指定key的防抖任务
     * @param key 防抖标识
     */
    public static void cancelDebounce(String key) {
        Runnable runnable = runnableMap.get(key);
        if (runnable != null) {
            handler.removeCallbacks(runnable);
        }
        runnableMap.remove(key);
        lastClickTimeMap.remove(key);
    }

    /**
     * 取消所有防抖任务
     */
    public static void cancelAllDebounce() {
        for (Runnable runnable : runnableMap.values()) {
            handler.removeCallbacks(runnable);
        }
        runnableMap.clear();
        lastClickTimeMap.clear();
    }

    /**
     * 检查指定key是否在防抖期间
     * @param key 防抖标识
     * @param delayMillis 防抖间隔时间（毫秒）
     * @return true表示在防抖期间，false表示可以执行
     */
    public static boolean isInDebounce(String key, long delayMillis) {
        long currentTime = System.currentTimeMillis();
        Long lastClickTime = lastClickTimeMap.get(key);
        if (lastClickTime == null) {
            lastClickTime = 0L;
        }
        return currentTime - lastClickTime < delayMillis;
    }

    /**
     * 检查指定key是否在防抖期间 - 使用默认500ms间隔
     * @param key 防抖标识
     * @return true表示在防抖期间，false表示可以执行
     */
    public static boolean isInDebounce(String key) {
        return isInDebounce(key, 500L);
    }

    /**
     * View扩展工具类 - 为View添加防抖点击
     */
    public static class ViewExtension {

        /**
         * 为View设置防抖点击监听器
         * @param view 目标View
         * @param delayMillis 防抖间隔时间（毫秒）
         * @param listener 点击监听器
         */
        public static void setDebounceClickListener(final View view, final long delayMillis, final View.OnClickListener listener) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    PLVDebounceUtil.clickDebounce(String.valueOf(view.getId()), delayMillis, new Runnable() {
                        @Override
                        public void run() {
                            listener.onClick(v);
                        }
                    });
                }
            });
        }

        /**
         * 为View设置防抖点击监听器 - 使用默认500ms间隔
         * @param view 目标View
         * @param listener 点击监听器
         */
        public static void setDebounceClickListener(View view, View.OnClickListener listener) {
            setDebounceClickListener(view, 500L, listener);
        }
    }

    /**
     * 字符串工具类 - 生成防抖标识
     */
    public static class StringExtension {

        /**
         * 为字符串添加防抖前缀
         * @param str 原始字符串
         * @return 带防抖前缀的字符串
         */
        public static String toDebounceKey(String str) {
            return "debounce_" + str;
        }
    }
}