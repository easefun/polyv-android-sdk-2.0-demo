package com.easefun.polyvsdk.cast.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.player.PolyvPlayerMediaController;
import com.easefun.polyvsdk.util.PolyvScreenUtils;
import com.easefun.polyvsdk.util.PolyvTimeUtils;
import com.easefun.polyvsdk.video.PolyvVideoView;
import com.easefun.polyvsdk.vo.PolyvVideoVO;

import net.polyv.android.media.cast.model.vo.PLVMediaCastDevice;

public class PolyvScreencastStatusLayout extends FrameLayout implements View.OnClickListener {
    //连接状态，设备名称
    private TextView tv_status, tv_device_name;
    //重试等交互按钮
    private TextView tv_retry, tv_bit, tv_exit, tv_switch_device;
    //清晰度布局
    private LinearLayout ll_bit_layout;
    private TextView tv_sc, tv_hd, tv_flu;
    //音量布局
    private LinearLayout ll_volume_layout;
    private ImageView iv_volume_add, iv_volume_reduce;
    //控制器布局
    private RelativeLayout rl_port;
    private ImageView iv_play, iv_land;
    private TextView tv_curtime, tv_tottime;
    private SeekBar sb_play;

    private PolyvScreencastSearchLayout screencastSearchLayout, landScreencastSearchLayout;
    private PolyvVideoView videoView;
    private PolyvPlayerMediaController mediaController;
    private PLVMediaCastDevice deviceInfo;

    private int currentPlayBitrate = -1;
    private long maxProgress;

    public PolyvScreencastStatusLayout(@NonNull Context context) {
        this(context, null);
    }

    public PolyvScreencastStatusLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PolyvScreencastStatusLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void setScreencastSearchLayout(PolyvScreencastSearchLayout screencastSearchLayout) {
        this.screencastSearchLayout = screencastSearchLayout;
    }

    public void setLandScreencastSearchLayout(PolyvScreencastSearchLayout screencastSearchLayout) {
        this.landScreencastSearchLayout = screencastSearchLayout;
    }

    private PolyvScreencastSearchLayout getScreencastSearchLayout() {
        return PolyvScreenUtils.isLandscape(getContext()) ? landScreencastSearchLayout : screencastSearchLayout;
    }

    public void setVideoView(PolyvVideoView videoView) {
        this.videoView = videoView;
    }

    public void setMediaController(PolyvPlayerMediaController mediaController) {
        this.mediaController = mediaController;
    }

    public int getCurrentPlayBitrate() {
        return currentPlayBitrate == -1 ? videoView.getBitRate() : currentPlayBitrate;
    }

    public int getCurrentPlayPosition() {
        return videoView == null ? 0 : videoView.getCurrentPosition() / 1000;
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.polyv_screencast_status_layout, this);
        tv_status = (TextView) findViewById(R.id.tv_status);
        tv_device_name = (TextView) findViewById(R.id.tv_device_name);

        tv_retry = (TextView) findViewById(R.id.tv_retry);
        tv_retry.setOnClickListener(this);
        tv_bit = (TextView) findViewById(R.id.tv_bit);
        tv_bit.setOnClickListener(this);
        tv_exit = (TextView) findViewById(R.id.tv_exit);
        tv_exit.setOnClickListener(this);
        tv_switch_device = (TextView) findViewById(R.id.tv_switch_device);
        tv_switch_device.setOnClickListener(this);

        ll_bit_layout = (LinearLayout) findViewById(R.id.ll_bit_layout);
        ll_bit_layout.setOnClickListener(this);
        tv_sc = (TextView) findViewById(R.id.tv_sc);
        tv_sc.setOnClickListener(this);
        tv_hd = (TextView) findViewById(R.id.tv_hd);
        tv_hd.setOnClickListener(this);
        tv_flu = (TextView) findViewById(R.id.tv_flu);
        tv_flu.setOnClickListener(this);

        ll_volume_layout = (LinearLayout) findViewById(R.id.ll_volume_layout);
        iv_volume_add = (ImageView) findViewById(R.id.iv_volume_add);
        iv_volume_add.setOnClickListener(this);
        iv_volume_reduce = (ImageView) findViewById(R.id.iv_volume_reduce);
        iv_volume_reduce.setOnClickListener(this);

        rl_port = (RelativeLayout) findViewById(R.id.rl_port);
        findViewById(R.id.iv_screencast_search).setVisibility(View.GONE);
        findViewById(R.id.tv_route_portrait).setVisibility(View.GONE);
        findViewById(R.id.tv_bit_portrait).setVisibility(View.GONE);
        findViewById(R.id.tv_speed_portrait).setVisibility(View.GONE);
        findViewById(R.id.iv_vice_status_portrait).setVisibility(View.GONE);
        iv_play = (ImageView) findViewById(R.id.iv_play);
        iv_play.setOnClickListener(this);
        iv_land = (ImageView) findViewById(R.id.iv_land);
        iv_land.setOnClickListener(this);
        tv_curtime = (TextView) findViewById(R.id.tv_curtime);
        tv_tottime = (TextView) findViewById(R.id.tv_tottime);
        sb_play = (SeekBar) findViewById(R.id.sb_play);
        sb_play.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    return;
                }
                int newPosition = (int) (maxProgress * (long) progress / seekBar.getMax());
                tv_curtime.setText(PolyvTimeUtils.generateTime(newPosition * 1000));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (!seekBar.isSelected())
                    seekBar.setSelected(true);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.isSelected())
                    seekBar.setSelected(false);
                int seekToPosition = (int) (maxProgress * (long) seekBar.getProgress() / seekBar.getMax());
                getScreencastSearchLayout().seekTo(seekToPosition);
            }
        });
    }

    public PolyvVideoView getVideoView() {
        return videoView;
    }

    public void callOnPause() {
        iv_play.setSelected(true);
    }

    public void callOnStart() {
        iv_play.setSelected(false);
    }

    public void callConnectStatus(String deviceName) {
        tv_bit.setVisibility(View.GONE);
        tv_retry.setVisibility(View.GONE);
        tv_exit.setBackgroundDrawable(getResources().getDrawable(R.drawable.polyv_tv_lb_corners));
        ll_bit_layout.setVisibility(View.GONE);
        ll_volume_layout.setVisibility(View.GONE);
        tv_status.setTextColor(Color.WHITE);

        tv_curtime.setText("00:00");
        tv_tottime.setText("00:00");
        sb_play.setProgress(0);
        sb_play.setEnabled(false);
        maxProgress = 0;

        iv_play.setSelected(false);
        iv_play.setEnabled(false);

        tv_status.setText("正在连接...");

        tv_device_name.setText(deviceName);
    }

    public void callPlayProgress(long max, long progress) {
        if (max == 0)
            return;
        maxProgress = max;
        tv_curtime.setText(PolyvTimeUtils.generateTime(progress * 1000));
        tv_tottime.setText(PolyvTimeUtils.generateTime(max * 1000));
        sb_play.setProgress((int) (sb_play.getMax() * 1L * progress / max));
        sb_play.setEnabled(true);

        iv_play.setEnabled(true);
    }

    public void callScreencastingStatus(int bitrate) {
        currentPlayBitrate = bitrate;
        tv_status.setTextColor(Color.WHITE);
        tv_status.setText("投屏中");
        tv_status.setTextColor(Color.parseColor("#31ADFE"));

        initBitRateView(bitrate);
        initBitRateViewVisible(bitrate);

        tv_exit.setBackgroundDrawable(getResources().getDrawable(R.drawable.polyv_tv_no_corners));

        ll_volume_layout.setVisibility(View.VISIBLE);
    }

    public void callScreencastStatusTitle(String title) {
        tv_status.setText(title);
        tv_status.setTextColor(Color.parseColor("#31ADFE"));
    }

    public void callPlayErrorStatus() {
        callPlayErrorStatus("投屏失败");
    }

    public void callPlayErrorStatus(String errorMsg) {
        tv_status.setTextColor(Color.RED);
        tv_status.setText(errorMsg);
        tv_status.setTextColor(Color.parseColor("#FF5B5B"));

        tv_bit.setVisibility(View.GONE);
        tv_retry.setVisibility(View.VISIBLE);

        tv_exit.setBackgroundDrawable(getResources().getDrawable(R.drawable.polyv_tv_no_corners));

        ll_volume_layout.setVisibility(View.GONE);
    }

    public void show(PLVMediaCastDevice info) {
        deviceInfo = info;
        callConnectStatus(info.getFriendlyName());
        if (getVisibility() == View.VISIBLE)
            return;
        setVisibility(View.VISIBLE);

        videoView.pause(true);
    }

    public void hide(boolean isStop) {
        if (getVisibility() != View.VISIBLE)
            return;
        currentPlayBitrate = -1;
        setVisibility(View.GONE);
        if (isStop) {
            getScreencastSearchLayout().stop();
//            screencastSearchLayout.disConnect();
//            landScreencastSearchLayout.disConnect();
        }
        castProgressSync();
    }

    /**
     * 同步投屏进度到本地播放器
     */
    private void castProgressSync() {
        if (getScreencastSearchLayout().getCurrentCastPosition() == -1) {
            return;
        }
        long castPosition = getScreencastSearchLayout().getCurrentCastPosition() * 1000;
        videoView.seekTo(castPosition);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_play:
                if (iv_play.isSelected()) {
                    getScreencastSearchLayout().resume();
                } else {
                    getScreencastSearchLayout().pause();
                }
                iv_play.setSelected(!iv_play.isSelected());
                break;
            case R.id.tv_retry:
                callConnectStatus(deviceInfo.getFriendlyName());
                getScreencastSearchLayout().reconnectPlay();
                break;
            case R.id.iv_volume_add:
                getScreencastSearchLayout().volumeUp();
                break;
            case R.id.iv_volume_reduce:
                getScreencastSearchLayout().volumeDown();
                break;
            case R.id.tv_sc:
                changeBitrate(3);
                break;
            case R.id.tv_hd:
                changeBitrate(2);
                break;
            case R.id.tv_flu:
                changeBitrate(1);
                break;
            case R.id.ll_bit_layout:
                ll_bit_layout.setVisibility(View.GONE);
                break;
            case R.id.tv_bit:
                ll_bit_layout.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_exit:
                hide(true);
                getScreencastSearchLayout().removeDelayCastRunnable();
                break;
            case R.id.tv_switch_device:
                getScreencastSearchLayout().show();
                break;
            case R.id.iv_land:
                if (iv_land.isSelected()) {
                    iv_land.setImageResource(R.drawable.polyv_btn_fullscreen);
                    mediaController.changeToSmallScreen();
                } else {
                    iv_land.setImageResource(R.drawable.polyv_btn_exitfulls);
                    mediaController.changeToFullScreen();
                }
                iv_land.setSelected(!iv_land.isSelected());
                break;
        }
    }

    //初始化选择码率的控件
    private void initBitRateView(int bitRate) {
        tv_sc.setSelected(false);
        tv_hd.setSelected(false);
        tv_flu.setSelected(false);
        switch (bitRate) {
            case 0:
            case 1:
                tv_bit.setText("流畅");
                tv_flu.setSelected(true);
                break;
            case 2:
                tv_bit.setText("高清");
                tv_hd.setSelected(true);
                break;
            case 3:
                tv_bit.setText("超清");
                tv_sc.setSelected(true);
                break;
        }
        tv_bit.setVisibility(View.VISIBLE);
    }

    //初始化选择码率控件的可见性
    private void initBitRateViewVisible(int currentBitRate) {
        tv_sc.setVisibility(View.GONE);
        tv_hd.setVisibility(View.GONE);
        tv_flu.setVisibility(View.GONE);
        PolyvVideoVO videoVO = videoView.getVideo();
        if (videoVO != null) {
            switch (videoVO.getDfNum()) {
                case 1:
                    tv_flu.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    tv_hd.setVisibility(View.VISIBLE);
                    tv_flu.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    tv_sc.setVisibility(View.VISIBLE);
                    tv_hd.setVisibility(View.VISIBLE);
                    tv_flu.setVisibility(View.VISIBLE);
                    break;
            }
        } else {
            switch (currentBitRate) {
                case 0:
                case 1:
                    tv_flu.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    tv_hd.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    tv_sc.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    private void changeBitrate(int bitRate) {
        ll_bit_layout.setVisibility(View.GONE);
        if (currentPlayBitrate == bitRate)
            return;
        callScreencastStatusTitle("切换码率");
        initBitRateView(bitRate);
        getScreencastSearchLayout().loadInfoAndPlay(deviceInfo, bitRate);
    }

    //重置选择码率的控件
    public void resetBitRateView(int bitRate) {
        ll_bit_layout.setVisibility(View.GONE);
        if (currentPlayBitrate == bitRate)
            return;
        currentPlayBitrate = bitRate;
        callScreencastStatusTitle("投屏中");
        initBitRateView(bitRate);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (iv_land != null) {
            iv_land.setSelected(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE);
            if (iv_land.isSelected()) {
                iv_land.setImageResource(R.drawable.polyv_btn_exitfulls);
            } else {
                iv_land.setImageResource(R.drawable.polyv_btn_fullscreen);
            }
        }
    }
}
