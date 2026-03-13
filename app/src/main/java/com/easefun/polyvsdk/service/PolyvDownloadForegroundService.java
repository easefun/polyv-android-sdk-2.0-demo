package com.easefun.polyvsdk.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;


import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.activity.PolyvDownloadActivity;

public class PolyvDownloadForegroundService extends Service {
    private static final String TAG = PolyvDownloadForegroundService.class.getSimpleName();
    private static final int NOTIFICATION_ID = 1301;
    private static final String CHANNEL_ID = "DownloadServiceChannel";
    private static final String CHANNEL_NAME = "后台下载服务";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        createNotificationChannel();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(NOTIFICATION_ID, createNotification(""), ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);
            } else {
                startForeground(NOTIFICATION_ID, createNotification(""));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        if (intent != null) {
            String title = intent.getStringExtra("title");
            if (!TextUtils.isEmpty(title)) {
                updateNotification(title);
            }
        }
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    private void createNotificationChannel() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
            if (notificationManager != null) {
                // 注册通道，注册后除非卸载再安装否则不改变
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private Notification createNotification(String title) {
        Intent intent = new Intent(this, PolyvDownloadActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setContentIntent(pendingIntent);
        builder.setShowWhen(false);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(TextUtils.isEmpty(title) ? "正在后台下载视频" : title);
        builder.setContentText("点击进入下载页面");
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setVisibility(NotificationCompat.VISIBILITY_SECRET);
        return builder.build();
    }

    public static void startService(Context context) {
        startService(context, "");
    }

    public static void startService(Context context, String title) {
        try {
            Intent serviceIntent = new Intent(context, PolyvDownloadForegroundService.class);
            serviceIntent.putExtra("title", title);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent);
            } else {
                context.startService(serviceIntent);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void stopService(Context context) {
        try {
            Intent serviceIntent = new Intent(context, PolyvDownloadForegroundService.class);
            context.stopService(serviceIntent);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void updateNotification(String title) {
        Notification notification = createNotification(title);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(NOTIFICATION_ID, notification);
        }
    }
}
