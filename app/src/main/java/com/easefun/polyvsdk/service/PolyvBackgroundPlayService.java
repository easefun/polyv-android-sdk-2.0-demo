package com.easefun.polyvsdk.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.easefun.polyvsdk.activity.PolyvPlayerActivity;

/**
 * 后台播放服务
 */
public class PolyvBackgroundPlayService extends Service {
    public static final String TAG = "PolyvBgPlayService";
    public static final String CHANNEL_ID = "bgPlayChannel";
    public static final String CHANNEL_NAME = "后台播放服务";
    public static final int NOTIFICATION_ID = 0x111;
    public static final int REQUEST_CODE_PLAY_ACTIVITY = 0x222;

    public static void bindService(Context context, ServiceConnection serviceConnection) {
        Intent intent = new Intent();
        intent.setClass(context, PolyvBackgroundPlayService.class);
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public static void startService(Context context) {
        Intent intent = new Intent(context, PolyvBackgroundPlayService.class);
        context.startService(intent);//startForegroundService 5s
    }

    public class PlayBinder extends Binder {
        public void start(String title, String text, int icon) {
            startForeground(NOTIFICATION_ID, createNotification(title, text, icon));
        }

        public void stop() {
            stopForeground(true);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
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

    public Notification createNotification(String title, String text, int icon) {
        Intent intent = new Intent(this, PolyvPlayerActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, REQUEST_CODE_PLAY_ACTIVITY, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setContentIntent(pendingIntent);
        builder.setShowWhen(false);
        builder.setSmallIcon(icon);
        builder.setContentTitle(title);
        builder.setContentText(text);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setVisibility(NotificationCompat.VISIBILITY_SECRET);
        return builder.build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new PlayBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
