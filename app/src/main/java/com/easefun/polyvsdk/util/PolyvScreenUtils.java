package com.easefun.polyvsdk.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import java.util.List;

/**
 * 与屏幕相关的工具类
 */
public class PolyvScreenUtils {
	private static int height16_9;

	// 生成竖屏下w:h=16:9的高
	public static int generateHeight16_9(Activity activity) {
		return height16_9 != 0 ? height16_9 : (height16_9 = getNormalWH(activity)[isPortrait(activity) ? 0 : 1] * 9 / 16);
	}

	// 获取竖屏下w:h=16:9的高
	public static int getHeight16_9() {
		return height16_9;
	}


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
			hideNavigationBar(activity);
		} else {
			setDecorVisible(activity);
		}
	}

	// 隐藏状态栏
	public static void hideStatusBar(Activity activity) {
		activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
				WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			View decorView = activity.getWindow().getDecorView();
			int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
					| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
					| View.SYSTEM_UI_FLAG_IMMERSIVE
					| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
			decorView.setSystemUiVisibility(uiOptions);
		}
	}

	// 恢复为不全屏状态
	public static void setDecorVisible(Activity activity) {
		activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			View decorView = activity.getWindow().getDecorView();
			int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
			decorView.setSystemUiVisibility(uiOptions);
		}
	}

	//由于画中画的activity为singleInstance模式，如果需要在画中画的activity退出时退出singleInstance模式，那么可以调用该方法
	public static void removePIPSingleInstanceTask(Context context, String pipActivityName, boolean isInPictureInPictureMode) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			ActivityManager activityManager = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
			if (activityManager == null)
				return;
			List<ActivityManager.AppTask> tasks = activityManager.getAppTasks();
			if (tasks == null)
			    return;
			for (ActivityManager.AppTask task : tasks) {
				ActivityManager.RecentTaskInfo info = task.getTaskInfo();
				if (info != null && info.baseIntent != null && info.baseIntent.getComponent() != null) {
					if (pipActivityName.equals(info.baseIntent.getComponent().getClassName())) {
						if (isInPictureInPictureMode || info.numActivities == 0) {
							task.finishAndRemoveTask();
						}
					}
				}
			}
		}
	}

	public static void hideNavigationBar(Activity activity){
		int uiOptions=activity.getWindow().getDecorView().getSystemUiVisibility();
		activity.getWindow().getDecorView().setSystemUiVisibility(uiOptions|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
	}

	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue / scale + 0.5f);
	}
}
