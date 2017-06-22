package com.easefun.polyvsdk.service;

import android.content.Intent;
import android.util.Log;

import com.easefun.polyvsdk.server.AndroidService;

/**
 * 播放视频已改版，不再需要service来播放视频
 */
@Deprecated
public class PolyvPlayService extends AndroidService {

	private static final String TAG = PolyvPlayService.class.getSimpleName();

	// 无参数构造函数，调用父类的super(String name)
	public PolyvPlayService() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		// Log.i("TAG","service started");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		// Log.i("TAG","service onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

	}

	@Override
	protected void onHandleIntent(Intent intent) {
		super.onHandleIntent(intent);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i(TAG, "server destory");
	}
}
