package com.easefun.polyvsdk;

import android.os.AsyncTask;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.easefun.polyvsdk.service.PolyvDownloadForegroundService;
import com.easefun.polyvsdk.util.PLVDebounceUtil;
import com.easefun.polyvsdk.util.PolyvSPUtils;

//继承的类是为了解决64K 引用限制
public class PolyvApplication extends MultiDexApplication {

	public static final String TAG = PolyvApplication.class.getSimpleName();

	private static final String defaultConfig = "yQRmgnzPyCUYDx6weXRATIN8gkp7BYGAl3ATjE/jHZunrULx8CoKa1WGMjfHftVChhIQlCA9bFeDDX+ThiuBHLjsNRjotqxhiz97ZjYaCQH/MhUrbEURv58317PwPuGEf3rbLVPOa4c9jliBcO+22A==";

	@Override
	public void onCreate() {
		super.onCreate();

		initPolyvCilent();

		//仅用于Demo测试设置加密串使用，开发者无需集成
		debugSetConfig();
	}

	//加密秘钥和加密向量，在后台->设置->API接口中获取，用于解密SDK加密串
	//值修改请参考https://github.com/easefun/polyv-android-sdk-demo/wiki/10.%E5%85%B3%E4%BA%8E-SDK%E5%8A%A0%E5%AF%86%E4%B8%B2-%E4%B8%8E-%E7%94%A8%E6%88%B7%E9%85%8D%E7%BD%AE%E4%BF%A1%E6%81%AF%E5%8A%A0%E5%AF%86%E4%BC%A0%E8%BE%93
	/** 加密秘钥 */
	private String aeskey = "VXtlHmwfS2oYm0CZ";
	/** 加密向量 */
	private String iv = "2u9gDPKdX6GyQJKU";

	public void initPolyvCilent() {
		//网络方式取得SDK加密串，（推荐）
		//网络获取到的SDK加密串可以保存在本地SharedPreference中，下次先从本地获取
//		new LoadConfigTask().execute();
		PolyvSDKClient client = PolyvSDKClient.getInstance();
		// 打开多用户开关，设置用户id，根据学员账号进行设置
//		openMultiAccount();

		//使用SDK加密串来配置
		client.settingsWithConfigString(defaultConfig, aeskey, iv);
		//初始化SDK设置
		client.initSetting(getApplicationContext());


		//默认开启了HttpDns，使用IPV4
//		client.enableHttpDns(true);
		//如果需要支持IPV6/IPV4，请开启IPV6开关。开启后自动关闭HttpDns，采用域名访问
//		client.enableIPV6(false);

		//启动Bugly
		client.initCrashReport(getApplicationContext());
		//启动Bugly后，在学员登录时设置学员id
//		client.crashReportSetUserId(userId);
		initDownloadDir();
		// 设置下载队列总数，多少个视频能同时下载。(默认是1，设置负数和0是没有限制)
		PolyvDownloaderManager.setDownloadQueueCount(1);
		// 监听下载中队列变化
		PolyvDownloaderManager.addOnDownloadingListSizeChangeListener(new PolyvDownloaderManager.OnDownloadingListSizeChangeListener() {
			@Override
			public void onSizeChanged(int newSize) {
				PLVDebounceUtil.delayDebounce("download_onSizeChanged_key", 500, new Runnable() {
					@Override
					public void run() {
						if (newSize == 0) {
							PolyvDownloadForegroundService.stopService(PolyvApplication.this);
						} else {
							PolyvDownloadForegroundService.startService(PolyvApplication.this);
						}
					}
				});
			}
		});
	}

	private void openMultiAccount() {
		PolyvSDKClient.getInstance().openMultiDownloadAccount(true);
	}

	private void initDownloadDir() {
		//TODO: Android Q 开始仅限下载在私有目录
		if (PolyvSDKClient.getInstance().isMultiDownloadAccount()) {
			// TODO: 2019/4/16 accountid 填入登录用户的id
			PolyvUserClient.getInstance().login("viewerId", this);
		}else{
			PolyvUserClient.getInstance().initDownloadDir(this);
		}
	}

	private class LoadConfigTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			String config = PolyvSDKUtil.getUrl2String("https://demo.polyv.net/demo/appkey.php");
			if (TextUtils.isEmpty(config)) {
				try {
					throw new Exception("没有取到数据");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			return config;
		}

		@Override
		protected void onPostExecute(String config) {
			PolyvSDKClient client = PolyvSDKClient.getInstance();
			client.setConfig(config, aeskey, iv);
		}
	}


	/**
	 * Demo调试使用的设置加密串方法
	 * 开发者无需集成
	 */
	private void debugSetConfig() {
		String sdkConfig = PolyvSPUtils.getInstance(this).getString("SDKConfig");
		if(!TextUtils.isEmpty(sdkConfig)){
			PolyvSDKClient.getInstance().settingsWithConfigString(sdkConfig,aeskey,iv);
		}
	}
}
