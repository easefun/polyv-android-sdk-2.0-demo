package com.easefun.polyvsdk.view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.util.PolyvScreenUtils;
import com.easefun.polyvsdk.video.PolyvVideoView;

import java.util.Locale;

public class PolyvLoadingLayout extends FrameLayout {
    private ProgressBar loadingProgress;
    private TextView loadingSpeed;

    private PolyvVideoView videoView;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (videoView != null && !videoView.isLocalPlay()) {
                    loadingSpeed.setVisibility(View.VISIBLE);
                    loadingSpeed.setText(formatedSpeed(videoView.getTcpSpeed(), 1000));

                    handler.sendEmptyMessageDelayed(1, 500);
                }
            }
        }
    };

    private static String formatedSpeed(long bytes, long elapsed_milli) {
        if (elapsed_milli <= 0) {
            return "0 B/S";
        }

        if (bytes <= 0) {
            return "0 B/S";
        }

        float bytes_per_sec = ((float) bytes) * 1000.f / elapsed_milli;
        if (bytes_per_sec >= 1000 * 1000) {
            return String.format(Locale.US, "%.2f MB/S", ((float) bytes_per_sec) / 1000 / 1000);
        } else if (bytes_per_sec >= 1000) {
            return String.format(Locale.US, "%.2f KB/S", ((float) bytes_per_sec) / 1000);
        } else {
            return String.format(Locale.US, "%d B/S", (long) bytes_per_sec);
        }
    }

    public PolyvLoadingLayout(@NonNull Context context) {
        this(context, null);
    }

    public PolyvLoadingLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PolyvLoadingLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.polyv_player_loading_layout, this);
        initView();
    }

    private void initView() {
        loadingProgress = (ProgressBar) findViewById(R.id.loading_progress);
        loadingSpeed = (TextView) findViewById(R.id.loading_speed);
    }

    public void bindVideoView(PolyvVideoView videoView) {
        this.videoView = videoView;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        acceptVisibilityChange(getVisibility());
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        acceptVisibilityChange(visibility);
    }

    private void acceptVisibilityChange(int visibility) {
        handler.removeCallbacksAndMessages(null);
        if (visibility == View.VISIBLE) {
            handler.sendEmptyMessage(1);
        } else {
            loadingSpeed.setVisibility(View.GONE);
        }
    }

    public void fitLocationChange(boolean isInMainScreen) {
        if (loadingProgress == null)
            return;
        MarginLayoutParams rlp = (MarginLayoutParams) loadingProgress.getLayoutParams();
        if (isInMainScreen) {
            rlp.width = MarginLayoutParams.WRAP_CONTENT;
            rlp.height = MarginLayoutParams.WRAP_CONTENT;
        } else {
            rlp.width = PolyvScreenUtils.dip2px(getContext(), 32);
            rlp.height = PolyvScreenUtils.dip2px(getContext(), 32);
        }
        loadingProgress.setLayoutParams(rlp);
    }
}
