package com.easefun.polyvsdk.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

/**
 * 与屏幕相关的工具类
 */
public class PolyvScreenUtils {

	// 是否竖屏
	public static boolean isPortrait(Context context) {
		return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
	}

	// 是否横屏
	public static boolean isLandscape(Context context) {
		return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
	}

	// 设置竖屏
	public static void setPortrait(Activity activity) {
		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
	}

	// 设置横屏
	public static void setLandscape(Activity activity) {
		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
	}

	/**
	 * 获取包含状态栏的屏幕宽度和高度
	 * 
	 * @param activity
	 * @return {宽,高}
	 */
	public static int[] getNormalWH(Activity activity) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			DisplayMetrics dm = new DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
			return new int[] { dm.widthPixels, dm.heightPixels };
		} else {
			Point point = new Point();
			WindowManager wm = activity.getWindowManager();
			wm.getDefaultDisplay().getSize(point);
			return new int[] { point.x, point.y };
		}
	}

	// 重置状态栏
	public static void reSetStatusBar(Activity activity) {
		if (isLandscape(activity)) {
			hideStatusBar(activity);
		} else {
			setDecorVisible(activity);
		}
	}

	// 隐藏状态栏
	public static void hideStatusBar(Activity activity) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
			activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		} else {
			View decorView = activity.getWindow().getDecorView();
			activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
			int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
					| View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
			decorView.setSystemUiVisibility(uiOptions);
		}
	}

	// 恢复为不全屏状态
	public static void setDecorVisible(Activity activity) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
			activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		} else {
			View decorView = activity.getWindow().getDecorView();
			activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
			decorView.setSystemUiVisibility(uiOptions);
		}
	}
}
