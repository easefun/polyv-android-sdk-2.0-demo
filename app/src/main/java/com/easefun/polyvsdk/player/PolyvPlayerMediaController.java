package com.easefun.polyvsdk.player;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.fragment.PolyvPlayerDanmuFragment;
import com.easefun.polyvsdk.ijk.PolyvPlayerScreenRatio;
import com.easefun.polyvsdk.sub.danmaku.entity.PolyvDanmakuInfo;
import com.easefun.polyvsdk.sub.screenshot.PolyvScreenShot;
import com.easefun.polyvsdk.util.PolyvKeyBoardUtils;
import com.easefun.polyvsdk.util.PolyvScreenUtils;
import com.easefun.polyvsdk.util.PolyvSensorHelper;
import com.easefun.polyvsdk.util.PolyvShareUtils;
import com.easefun.polyvsdk.util.PolyvTimeUtils;
import com.easefun.polyvsdk.video.IPolyvVideoView;
import com.easefun.polyvsdk.video.PolyvBaseMediaController;
import com.easefun.polyvsdk.video.PolyvVideoView;
import com.easefun.polyvsdk.vo.PolyvVideoVO;

import java.util.ArrayList;
import java.util.List;

public class PolyvPlayerMediaController extends PolyvBaseMediaController implements View.OnClickListener {
    private static final String TAG = PolyvPlayerMediaController.class.getSimpleName();
    private Context mContext = null;
    private PolyvVideoView videoView = null;
    private PolyvVideoVO videoVO;
    private PolyvPlayerDanmuFragment danmuFragment;
    //播放器所在的activity
    private Activity videoActivity;
    //播放器的ParentView
    private View parentView;
    //显示的状态
    private boolean isShowing;
    //控制栏显示的时间
    private static final int longTime = 5000;
    private static final int HIDE = 12;
    private static final int SHOW_PROGRESS = 13;
    //controllerView
    private View view;
    /**
     * 竖屏的view
     */
    // 竖屏的控制栏
    private RelativeLayout rl_port;
    // 竖屏的切屏按钮，竖屏的播放/暂停按钮
    private ImageView iv_land, iv_play;
    // 竖屏的显示播放进度控件
    private TextView tv_curtime, tv_tottime;
    // 竖屏的进度条
    private SeekBar sb_play;
    /**
     * 横屏的view
     */
    //横屏的控制栏，顶部布局，底部布局
    private RelativeLayout rl_land, rl_top, rl_bot;
    //横屏的切屏按钮，横屏的播放/暂停按钮,横屏的返回按钮，设置按钮，分享按钮，弹幕开关
    private ImageView iv_port, iv_play_land, iv_finish, iv_set, iv_share, iv_dmswitch;
    // 横屏的显示播放进度控件,视频的标题,选择播放速度按钮，选择码率按钮
    private TextView tv_curtime_land, tv_tottime_land, tv_title, tv_speed, tv_bit;
    // 横屏的进度条
    private SeekBar sb_play_land;
    /**
     * 侧边布局的view
     */
    //侧边布局
    private LinearLayout ll_side;
    //弹幕按钮，截图按钮
    private ImageView iv_danmu, iv_screens;
    /**
     * 设置布局的view
     */
    //设置布局
    private RelativeLayout rl_center_set;
    //调节亮度控件，调节音量控件
    private SeekBar sb_light, sb_volume;
    // 设置播放器银幕比率控件，设置字幕的控件
    private TextView tv_full, tv_fit, tv_sixteennine, tv_fourthree, tv_srt1, tv_srt2, tv_srt3, tv_srtnone;
    // 关闭布局按钮
    private ImageView iv_close_set;
    /**
     * 弹幕布局的view
     */
    //弹幕布局,底部的设置布局
    private RelativeLayout rl_center_danmu, rl_dmbot;
    //设置按钮，返回按钮，选择颜色按钮
    private ImageView iv_dmset, iv_finish_danmu, iv_dmwhite, iv_dmred, iv_dmpurple, iv_dmblue, iv_dmgreen, iv_dmyellow;
    //选择弹幕样式按钮，选择弹幕字体大小按钮，发送弹幕按钮
    private TextView tv_dmroll, tv_dmtop, tv_dmbottom, tv_dmfonts, tv_dmfontm, tv_dmfontl, tv_dmsend;
    //弹幕输入栏
    private EditText et_dmedit;
    //弹幕的配置信息
    private String fontmode, fontsize;
    private int color;
    /**
     * 分享布局的view
     */
    //分享布局
    private RelativeLayout rl_center_share;
    // 分享控件,关闭布局按钮
    private ImageView iv_shareqq, iv_sharewechat, iv_shareweibo, iv_close_share;
    /**
     * 播放速度布局
     */
    //播放速度布局
    private RelativeLayout rl_center_speed;
    //选择播放速度控件
    private TextView tv_speed05, tv_speed10, tv_speed12, tv_speed15, tv_speed20;
    //关闭布局按钮
    private ImageView iv_close_speed;
    /**
     * 播放码率布局
     */
    //播放码率布局
    private RelativeLayout rl_center_bit;
    //选择播放码率的控件
    private TextView tv_sc, tv_hd, tv_flu, tv_auto;
    //关闭布局按钮
    private ImageView iv_close_bit;
    //-----------------------------------------
    // 进度条是否处于拖动的状态
    private boolean status_dragging;
    // 控制栏是否处于一直显示的状态
    private boolean status_showalways;
    // 播放器在显示弹幕布局前的状态
    private boolean status_isPlaying;
    private PolyvSensorHelper sensorHelper;

    //用于处理控制栏的显示状态
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HIDE:
                    hide();
                    break;
                case SHOW_PROGRESS:
                    showProgress();
                    break;
            }
        }
    };

    // 更新显示的播放进度，以及暂停/播放按钮
    private void showProgress() {
        if (isShowing && videoView != null) {
            // 单位：毫秒
            int position = videoView.getCurrentPosition();
            int totalTime = videoView.getDuration() / 1000 * 1000;
            if (videoView.isCompletedState() || position > totalTime)
                position = totalTime;
            int bufPercent = videoView.getBufferPercentage();
            //在拖动进度条的时候，这里不更新
            if (!status_dragging) {
                tv_curtime.setText(PolyvTimeUtils.generateTime(position));
                tv_curtime_land.setText(PolyvTimeUtils.generateTime(position));
                if (totalTime > 0) {
                    sb_play.setProgress((int) (1000L * position / totalTime));
                    sb_play_land.setProgress((int) (1000L * position / totalTime));
                } else {
                    sb_play.setProgress(0);
                    sb_play_land.setProgress(0);
                }
            }
            sb_play.setSecondaryProgress(1000 * bufPercent / 100);
            sb_play_land.setSecondaryProgress(1000 * bufPercent / 100);
            if (videoView.isPlaying()) {
                iv_play.setSelected(false);
                iv_play_land.setSelected(false);
            } else {
                iv_play.setSelected(true);
                iv_play_land.setSelected(true);
            }
            handler.sendMessageDelayed(handler.obtainMessage(SHOW_PROGRESS), 1000 - (position % 1000));
        }
    }

    public PolyvPlayerMediaController(Context context) {
        this(context, null);
    }

    public PolyvPlayerMediaController(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PolyvPlayerMediaController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        this.videoActivity = (Activity) mContext;
        this.view = LayoutInflater.from(getContext()).inflate(R.layout.polyv_controller_media, this);
        findIdAndNew();
        initView();
    }

    /**
     * 初始化控制栏的配置
     *
     * @param parentView 播放器的父控件
     */
    public void initConfig(ViewGroup parentView) {
        this.parentView = parentView;
    }

    public void setDanmuFragment(PolyvPlayerDanmuFragment danmuFragment) {
        this.danmuFragment = danmuFragment;
    }

    private void findIdAndNew() {
        //竖屏的view
        rl_port = (RelativeLayout) view.findViewById(R.id.rl_port);
        iv_land = (ImageView) view.findViewById(R.id.iv_land);
        iv_play = (ImageView) view.findViewById(R.id.iv_play);
        tv_curtime = (TextView) view.findViewById(R.id.tv_curtime);
        tv_tottime = (TextView) view.findViewById(R.id.tv_tottime);
        sb_play = (SeekBar) view.findViewById(R.id.sb_play);
        //横屏的view
        rl_land = (RelativeLayout) view.findViewById(R.id.rl_land);
        rl_top = (RelativeLayout) view.findViewById(R.id.rl_top);
        rl_bot = (RelativeLayout) view.findViewById(R.id.rl_bot);
        iv_port = (ImageView) view.findViewById(R.id.iv_port);
        iv_play_land = (ImageView) view.findViewById(R.id.iv_play_land);
        iv_finish = (ImageView) view.findViewById(R.id.iv_finish);
        tv_curtime_land = (TextView) view.findViewById(R.id.tv_curtime_land);
        tv_tottime_land = (TextView) view.findViewById(R.id.tv_tottime_land);
        sb_play_land = (SeekBar) view.findViewById(R.id.sb_play_land);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        iv_set = (ImageView) view.findViewById(R.id.iv_set);
        iv_share = (ImageView) view.findViewById(R.id.iv_share);
        iv_dmswitch = (ImageView) view.findViewById(R.id.iv_dmswitch);
        tv_speed = (TextView) view.findViewById(R.id.tv_speed);
        tv_bit = (TextView) view.findViewById(R.id.tv_bit);
        //设置布局的view
        rl_center_set = (RelativeLayout) view.findViewById(R.id.rl_center_set);
        sb_light = (SeekBar) view.findViewById(R.id.sb_light);
        sb_volume = (SeekBar) view.findViewById(R.id.sb_volume);
        tv_full = (TextView) view.findViewById(R.id.tv_full);
        tv_fit = (TextView) view.findViewById(R.id.tv_fit);
        tv_sixteennine = (TextView) view.findViewById(R.id.tv_sixteennine);
        tv_fourthree = (TextView) view.findViewById(R.id.tv_fourthree);
        tv_srt1 = (TextView) view.findViewById(R.id.tv_srt1);
        tv_srt2 = (TextView) view.findViewById(R.id.tv_srt2);
        tv_srt3 = (TextView) view.findViewById(R.id.tv_srt3);
        tv_srtnone = (TextView) view.findViewById(R.id.tv_srtnone);
        iv_close_set = (ImageView) view.findViewById(R.id.iv_close_set);
        //侧边布局的view
        ll_side = (LinearLayout) view.findViewById(R.id.ll_side);
        iv_danmu = (ImageView) view.findViewById(R.id.iv_danmu);
        iv_screens = (ImageView) view.findViewById(R.id.iv_screens);
        //弹幕布局的view
        rl_center_danmu = (RelativeLayout) view.findViewById(R.id.rl_center_danmu);
        rl_dmbot = (RelativeLayout) view.findViewById(R.id.rl_dmbot);
        iv_dmset = (ImageView) view.findViewById(R.id.iv_dmset);
        iv_finish_danmu = (ImageView) view.findViewById(R.id.iv_finish_danmu);
        et_dmedit = (EditText) view.findViewById(R.id.et_dmedit);
        iv_dmwhite = (ImageView) view.findViewById(R.id.iv_dmwhite);
        iv_dmblue = (ImageView) view.findViewById(R.id.iv_dmblue);
        iv_dmgreen = (ImageView) view.findViewById(R.id.iv_dmgreen);
        iv_dmpurple = (ImageView) view.findViewById(R.id.iv_dmpurple);
        iv_dmred = (ImageView) view.findViewById(R.id.iv_dmred);
        iv_dmyellow = (ImageView) view.findViewById(R.id.iv_dmyellow);
        tv_dmroll = (TextView) view.findViewById(R.id.tv_dmroll);
        tv_dmtop = (TextView) view.findViewById(R.id.tv_dmtop);
        tv_dmbottom = (TextView) view.findViewById(R.id.tv_dmbottom);
        tv_dmfonts = (TextView) view.findViewById(R.id.tv_dmfonts);
        tv_dmfontm = (TextView) view.findViewById(R.id.tv_dmfontm);
        tv_dmfontl = (TextView) view.findViewById(R.id.tv_dmfontl);
        tv_dmsend = (TextView) view.findViewById(R.id.tv_dmsend);
        //分享布局的view
        rl_center_share = (RelativeLayout) view.findViewById(R.id.rl_center_share);
        iv_shareqq = (ImageView) view.findViewById(R.id.iv_shareqq);
        iv_sharewechat = (ImageView) view.findViewById(R.id.iv_sharewechat);
        iv_shareweibo = (ImageView) view.findViewById(R.id.iv_shareweibo);
        iv_close_share = (ImageView) view.findViewById(R.id.iv_close_share);
        //播放速度布局的view
        rl_center_speed = (RelativeLayout) view.findViewById(R.id.rl_center_speed);
        tv_speed05 = (TextView) view.findViewById(R.id.tv_speed05);
        tv_speed10 = (TextView) view.findViewById(R.id.tv_speed10);
        tv_speed12 = (TextView) view.findViewById(R.id.tv_speed12);
        tv_speed15 = (TextView) view.findViewById(R.id.tv_speed15);
        tv_speed20 = (TextView) view.findViewById(R.id.tv_speed20);
        iv_close_speed = (ImageView) view.findViewById(R.id.iv_close_speed);
        //播放码率布局的view
        rl_center_bit = (RelativeLayout) view.findViewById(R.id.rl_center_bit);
        tv_sc = (TextView) view.findViewById(R.id.tv_sc);
        tv_hd = (TextView) view.findViewById(R.id.tv_hd);
        tv_flu = (TextView) view.findViewById(R.id.tv_flu);
        tv_auto = (TextView) view.findViewById(R.id.tv_auto);
        iv_close_bit = (ImageView) view.findViewById(R.id.iv_close_bit);

        sensorHelper = new PolyvSensorHelper(videoActivity);
    }

    private void initView() {
        iv_land.setOnClickListener(this);
        iv_port.setOnClickListener(this);
        iv_play.setOnClickListener(this);
        iv_play_land.setOnClickListener(this);
        iv_finish.setOnClickListener(this);
        iv_set.setOnClickListener(this);
        iv_danmu.setOnClickListener(this);
        iv_dmset.setOnClickListener(this);
        iv_finish_danmu.setOnClickListener(this);
        iv_share.setOnClickListener(this);
        tv_full.setOnClickListener(this);
        tv_fit.setOnClickListener(this);
        tv_sixteennine.setOnClickListener(this);
        tv_fourthree.setOnClickListener(this);
        iv_dmwhite.setOnClickListener(this);
        //弹幕的初始化配置
        iv_dmwhite.setSelected(true);
        tv_dmroll.setSelected(true);
        tv_dmfontm.setSelected(true);
        color = Color.WHITE;
        fontmode = PolyvDanmakuInfo.FONTMODE_ROLL;
        fontsize = PolyvDanmakuInfo.FONTSIZE_MIDDLE;
        //------------------------------
        iv_dmyellow.setOnClickListener(this);
        iv_dmred.setOnClickListener(this);
        iv_dmpurple.setOnClickListener(this);
        iv_dmgreen.setOnClickListener(this);
        iv_dmblue.setOnClickListener(this);
        tv_dmroll.setOnClickListener(this);
        tv_dmtop.setOnClickListener(this);
        tv_dmbottom.setOnClickListener(this);
        tv_dmfonts.setOnClickListener(this);
        tv_dmfontm.setOnClickListener(this);
        tv_dmfontl.setOnClickListener(this);
        et_dmedit.setOnClickListener(this);
        et_dmedit.setOnEditorActionListener(editorActionListener);
        iv_shareqq.setOnClickListener(this);
        iv_sharewechat.setOnClickListener(this);
        iv_shareweibo.setOnClickListener(this);
        iv_dmswitch.setOnClickListener(this);
        tv_srt1.setOnClickListener(this);
        tv_srt2.setOnClickListener(this);
        tv_srt3.setOnClickListener(this);
        tv_srtnone.setOnClickListener(this);
        tv_speed.setOnClickListener(this);
        tv_speed05.setOnClickListener(this);
        tv_speed10.setOnClickListener(this);
        tv_speed12.setOnClickListener(this);
        tv_speed15.setOnClickListener(this);
        tv_speed20.setOnClickListener(this);
        tv_bit.setOnClickListener(this);
        tv_sc.setOnClickListener(this);
        tv_hd.setOnClickListener(this);
        tv_flu.setOnClickListener(this);
        tv_auto.setOnClickListener(this);
        iv_close_bit.setOnClickListener(this);
        iv_close_set.setOnClickListener(this);
        iv_close_share.setOnClickListener(this);
        iv_close_speed.setOnClickListener(this);
        tv_dmsend.setOnClickListener(this);
        iv_screens.setOnClickListener(this);
        sb_play.setOnSeekBarChangeListener(seekBarChangeListener);
        sb_play_land.setOnSeekBarChangeListener(seekBarChangeListener);
        sb_light.setOnSeekBarChangeListener(seekBarChangeListener);
        sb_volume.setOnSeekBarChangeListener(seekBarChangeListener);
    }

    public void preparedView() {
        if (videoView != null) {
            videoVO = videoView.getVideo();
            if (videoVO != null)
                tv_title.setText(videoVO.getTitle());
            int totalTime = videoView.getDuration();
            tv_tottime.setText(PolyvTimeUtils.generateTime(totalTime));
            tv_tottime_land.setText(PolyvTimeUtils.generateTime(totalTime));
            //初始化播放器的银幕比率的显示控件
            initRatioView(videoView.getCurrentAspectRatio());
            //初始化字幕控件
            initSrtView(videoView.getCurrSRTKey());
            //初始化倍速控件及其可见性
            initSpeedView((int) (videoView.getSpeed() * 10));
            //初始化码率控件及其可见性
            initBitRateView(videoView.getBitRate());
            initBitRateViewVisible(videoView.getBitRate());
        }
        // 视频准备完成后，开启随手势自动切换屏幕
        if (PolyvScreenUtils.isLandscape(mContext))
            sensorHelper.toggle(true, false);
        else
            sensorHelper.toggle(true, true);
    }

    @Override
    public void setMediaPlayer(IPolyvVideoView player) {
        videoView = (PolyvVideoView) player;
    }

    @Override
    public void release() {
        //播放器release时主动调用
    }

    @Override
    public void destroy() {
        //播放器destroy时主动调用
    }

    @Override
    public void hide() {
        if (isShowing) {
            resetSetLayout(View.GONE);
            resetDanmuLayout(View.GONE);
            resetShareLayout(View.GONE);
            resetSpeedLayout(View.GONE);
            resetBitRateLayout(View.GONE);
            isShowing = !isShowing;
            setVisibility(View.GONE);
        }
    }

    @Override
    public boolean isShowing() {
        return isShowing;
    }

    @Override
    public void setAnchorView(View view) {
        //...
    }

    // 关闭监听
    public void pause() {
        sensorHelper.disable();
    }

    // 开启监听
    public void resume() {
        sensorHelper.enable();
    }

    /**
     * 退出播放器的Activity时需调用
     */
    public void disable() {
        hide();
        sensorHelper.disable();
    }

    /**
     * 显示控制栏
     *
     * @param timeout 显示的时间，<0时将一直显示
     */
    @Override
    public void show(int timeout) {
        if (timeout < 0)
            status_showalways = true;
        else
            status_showalways = false;
        if (!isShowing) {
            resetTopBottomLayout(View.VISIBLE);
            resetSideLayout(View.VISIBLE);
            //获取焦点
            requestFocus();
            handler.removeMessages(SHOW_PROGRESS);
            handler.sendEmptyMessage(SHOW_PROGRESS);
            isShowing = !isShowing;
            setVisibility(View.VISIBLE);
        }
        resetHideTime(timeout);
    }

    @Override
    public void show() {
        show(longTime);
    }

    /**
     * 切换到横屏
     */
    public void changeToLandscape() {
        PolyvScreenUtils.setLandscape(videoActivity);
        //初始为横屏时，状态栏需要隐藏
        PolyvScreenUtils.hideStatusBar(videoActivity);
        //初始为横屏时，控制栏的宽高需要设置
        initLandScapeWH();
    }

    private void initLandScapeWH() {
        // 获取横屏下的屏幕宽高
        int[] wh = PolyvScreenUtils.getNormalWH(videoActivity);
        //这里的LayoutParams为parentView的父类的LayoutParams
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(wh[0], wh[1]);
        parentView.setLayoutParams(lp);
        rl_land.setVisibility(View.VISIBLE);
        rl_port.setVisibility(View.GONE);
    }

    /**
     * 切换到竖屏
     */
    public void changeToPortrait() {
        PolyvScreenUtils.setPortrait(videoActivity);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        resetControllerLayout();
    }

    //根据屏幕状态改变控制栏布局
    private void resetControllerLayout() {
        hide();
        PolyvScreenUtils.reSetStatusBar(videoActivity);
        if (PolyvScreenUtils.isLandscape(mContext)) {
            // 横屏下开启自动切换横竖屏
            sensorHelper.toggle(true, true);
            initLandScapeWH();
        } else {
            // 竖屏下开启自动切换横竖屏
            sensorHelper.toggle(true, false);
            //这里宽高设置是polyv_fragment_player.xml布局文件中parentView的初始值
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) mContext.getResources().getDimension(R.dimen.top_center_player_height));
            parentView.setLayoutParams(lp);
            rl_port.setVisibility(View.VISIBLE);
            rl_land.setVisibility(View.GONE);
        }
    }

    //根据视频的播放状态去暂停或播放
    private void playOrPause() {
        if (videoView != null) {
            if (videoView.isPlaying()) {
                danmuFragment.pause();
                videoView.pause();
                status_isPlaying = false;
                iv_play.setSelected(true);
                iv_play_land.setSelected(true);
            } else {
                danmuFragment.resume();
                videoView.start();
                status_isPlaying = true;
                iv_play.setSelected(false);
                iv_play_land.setSelected(false);
            }
        }
    }

    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            if (!b)
                return;
            switch (seekBar.getId()) {
                case R.id.sb_play:
                case R.id.sb_play_land:
                    resetHideTime(longTime);
                    status_dragging = true;
                    if (videoView != null) {
                        int newPosition = (int) (videoView.getDuration() * (long) i / 1000);
                        tv_curtime.setText(PolyvTimeUtils.generateTime(newPosition));
                        tv_curtime_land.setText(PolyvTimeUtils.generateTime(newPosition));
                    }
                    break;
                case R.id.sb_light:
                    if (videoView != null)
                        videoView.setBrightness(i);
                    break;
                case R.id.sb_volume:
                    if (videoView != null)
                        videoView.setVolume(i);
                    break;
            }
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
            switch (seekBar.getId()) {
                case R.id.sb_play:
                case R.id.sb_play_land:
                    if (videoView != null) {
                        videoView.seekTo((int) (videoView.getDuration() * (long) seekBar.getProgress() / seekBar.getMax()));
                        danmuFragment.seekTo();
                        if (videoView.isCompletedState()) {
                            videoView.start();
                            danmuFragment.resume();
                        }
                    }
                    status_dragging = false;
                    break;
            }
        }
    };

    TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i == EditorInfo.IME_ACTION_SEND) {
                sendDanmaku();
                return true;
            }
            return false;
        }
    };

    //重置控制栏的隐藏时间
    private void resetHideTime(int delayedTime) {
        handler.removeMessages(HIDE);
        if (delayedTime >= 0)
            handler.sendMessageDelayed(handler.obtainMessage(HIDE), delayedTime);
    }

    //重置控制栏的顶部和底部布局以及进度条的显示状态
    private void resetTopBottomLayout(int isVisible) {
        resetTopBottomLayout(isVisible, false);
    }

    private void resetTopBottomLayout(int isVisible, boolean onlyHideTop) {
        rl_top.setVisibility(isVisible);
        if (!onlyHideTop) {
            rl_bot.setVisibility(isVisible);
            sb_play_land.setVisibility(isVisible);
        }
    }

    //重置侧边的布局的显示状态
    private void resetSideLayout(int isVisible) {
        ll_side.setVisibility(isVisible);
    }

    //重置设置布局的显示状态
    private void resetSetLayout(int isVisible) {
        if (isVisible == View.VISIBLE) {
            show(-1);
            resetTopBottomLayout(View.GONE);
            resetSideLayout(View.GONE);
            if (videoView != null) {
                sb_light.setProgress(videoView.getBrightness());
                sb_volume.setProgress(videoView.getVolume());
            }
        }
        rl_center_set.setVisibility(isVisible);
    }

    //重置弹幕布局的显示状态
    private void resetDanmuLayout(int isVisible) {
        if (isVisible == View.VISIBLE) {
            show(-1);
            resetTopBottomLayout(View.GONE);
            resetSideLayout(View.GONE);
            resetBitRateLayout(View.GONE, false);
            resetSpeedLayout(View.GONE, false);
            et_dmedit.requestFocus();
            et_dmedit.setText("");
            if (videoView != null) {
                status_isPlaying = videoView.isPlaying();
                videoView.pause(true);
                danmuFragment.pause();
            }
            PolyvKeyBoardUtils.openKeybord(et_dmedit, mContext);
        } else if (rl_center_danmu.getVisibility() == View.VISIBLE) {
            if (videoView != null && status_isPlaying) {
                videoView.start();
                danmuFragment.resume();
            }
            PolyvKeyBoardUtils.closeKeybord(et_dmedit, mContext);
        }
        iv_dmset.setSelected(false);
        rl_dmbot.setVisibility(View.GONE);
        rl_center_danmu.setVisibility(isVisible);
    }

    //重置弹幕布局的底部布局的显示状态
    private void resetDanmuBottomLayout() {
        if (rl_dmbot.getVisibility() == View.VISIBLE) {
            rl_dmbot.setVisibility(View.GONE);
            iv_dmset.setSelected(false);
            PolyvKeyBoardUtils.openKeybord(et_dmedit, mContext);
        } else {
            rl_dmbot.setVisibility(View.VISIBLE);
            iv_dmset.setSelected(true);
            PolyvKeyBoardUtils.closeKeybord(et_dmedit, mContext);
        }
    }

    //重置分享布局的显示状态
    private void resetShareLayout(int isVisible) {
        if (isVisible == View.VISIBLE) {
            show(-1);
            resetTopBottomLayout(View.GONE);
            resetSideLayout(View.GONE);
        }
        rl_center_share.setVisibility(isVisible);
    }

    //重置选择播放器银幕比率控件的状态
    private void resetRatioView(int screenRatio) {
        initRatioView(screenRatio);
        if (videoView != null)
            videoView.setAspectRatio(screenRatio);
    }

    //初始化选择播放器银幕比率控件
    private void initRatioView(int screenRatio) {
        tv_full.setSelected(false);
        tv_fit.setSelected(false);
        tv_sixteennine.setSelected(false);
        tv_fourthree.setSelected(false);
        switch (screenRatio) {
            case PolyvPlayerScreenRatio.AR_ASPECT_FILL_PARENT:
                tv_full.setSelected(true);
                break;
            case PolyvPlayerScreenRatio.AR_ASPECT_FIT_PARENT:
                tv_fit.setSelected(true);
                break;
            case PolyvPlayerScreenRatio.AR_16_9_FIT_PARENT:
                tv_sixteennine.setSelected(true);
                break;
            case PolyvPlayerScreenRatio.AR_4_3_FIT_PARENT:
                tv_fourthree.setSelected(true);
                break;
        }
    }

    //重置弹幕颜色按钮的状态
    private void resetDammuColorView(int color) {
        this.color = color;
        iv_dmblue.setSelected(false);
        iv_dmgreen.setSelected(false);
        iv_dmpurple.setSelected(false);
        iv_dmred.setSelected(false);
        iv_dmwhite.setSelected(false);
        iv_dmyellow.setSelected(false);
        switch (color) {
            case Color.WHITE:
                iv_dmwhite.setSelected(true);
                break;
            case Color.BLUE:
                iv_dmblue.setSelected(true);
                break;
            case Color.GREEN:
                iv_dmgreen.setSelected(true);
                break;
            case Color.YELLOW:
                iv_dmyellow.setSelected(true);
                break;
            case Color.RED:
                iv_dmred.setSelected(true);
                break;
            case Color.MAGENTA:
                iv_dmpurple.setSelected(true);
                break;
        }
    }

    //重置弹幕样式按钮的状态
    private void resetDanmaStyleView(String style) {
        this.fontmode = style;
        tv_dmroll.setSelected(false);
        tv_dmtop.setSelected(false);
        tv_dmbottom.setSelected(false);
        switch (style) {
            case PolyvDanmakuInfo.FONTMODE_ROLL:
                tv_dmroll.setSelected(true);
                break;
            case PolyvDanmakuInfo.FONTMODE_TOP:
                tv_dmtop.setSelected(true);
                break;
            case PolyvDanmakuInfo.FONTMODE_BOTTOM:
                tv_dmbottom.setSelected(true);
                break;
        }
    }

    //重置弹幕字体大小按钮的状态
    private void resetDanmaFontView(String fontSize) {
        this.fontsize = fontSize;
        tv_dmfonts.setSelected(false);
        tv_dmfontm.setSelected(false);
        tv_dmfontl.setSelected(false);
        switch (fontSize) {
            case PolyvDanmakuInfo.FONTSIZE_SMALL:
                tv_dmfonts.setSelected(true);
                break;
            case PolyvDanmakuInfo.FONTSIZE_MIDDLE:
                tv_dmfontm.setSelected(true);
                break;
            case PolyvDanmakuInfo.FONTSIZE_LARGE:
                tv_dmfontl.setSelected(true);
                break;
        }
    }

    //重置选择字幕的控件
    private void resetSrtView(int srtPosition) {
        if (videoView != null)
            videoView.changeSRT(initSrtView(srtPosition));
    }

    private String initSrtView(int srtPosition) {
        tv_srt1.setSelected(false);
        tv_srt2.setSelected(false);
        tv_srt3.setSelected(false);
        tv_srtnone.setSelected(false);
        List<String> srtKeys = new ArrayList<String>();
        if (videoVO != null)
            srtKeys.addAll(videoVO.getVideoSRT().keySet());
        switch (srtPosition) {
            case 0:
                tv_srt1.setSelected(true);
                break;
            case 1:
                tv_srt2.setSelected(true);
                break;
            case 2:
                tv_srt3.setSelected(true);
                break;
            case 3:
                tv_srtnone.setSelected(true);
                break;
        }
        return srtPosition == 3 ? "不显示" : srtKeys.get(srtPosition);
    }

    //初始化选择字幕的控件
    private void initSrtView(String srtKey) {
        tv_srt1.setSelected(false);
        tv_srt2.setSelected(false);
        tv_srt3.setSelected(false);
        tv_srt1.setVisibility(View.VISIBLE);
        tv_srt2.setVisibility(View.VISIBLE);
        tv_srt3.setVisibility(View.VISIBLE);
        tv_srtnone.setSelected(false);
        List<String> srtKeys = new ArrayList<String>();
        if (videoVO != null)
            srtKeys.addAll(videoVO.getVideoSRT().keySet());
        switch (srtKeys.size()) {
            case 0:
                tv_srt1.setVisibility(View.GONE);
                tv_srt2.setVisibility(View.GONE);
                tv_srt3.setVisibility(View.GONE);
                break;
            case 1:
                tv_srt1.setText(srtKeys.get(0));
                tv_srt2.setVisibility(View.GONE);
                tv_srt3.setVisibility(View.GONE);
                break;
            case 2:
                tv_srt1.setText(srtKeys.get(0));
                tv_srt2.setText(srtKeys.get(1));
                tv_srt3.setVisibility(View.GONE);
                break;
            default:
                tv_srt1.setText(srtKeys.get(0));
                tv_srt2.setText(srtKeys.get(1));
                tv_srt3.setText(srtKeys.get(2));
                break;
        }
        if (TextUtils.isEmpty(srtKey)) {
            tv_srtnone.setSelected(true);
            return;
        }
        switch (srtKeys.indexOf(srtKey)) {
            case 0:
                tv_srt1.setSelected(true);
                break;
            case 1:
                tv_srt2.setSelected(true);
                break;
            case 2:
                tv_srt3.setSelected(true);
                break;
        }
    }

    //重置选择播放速度的布局
    private void resetSpeedLayout(int isVisible) {
        resetSpeedLayout(isVisible, true);
    }

    private void resetSpeedLayout(int isVisible, boolean isShowTopBottomLayout) {
        if (isVisible == View.VISIBLE) {
            show(-1);
            resetTopBottomLayout(View.GONE, true);
            resetBitRateLayout(View.GONE, false);
        } else if (isShowTopBottomLayout) {
            resetTopBottomLayout(View.VISIBLE);
            requestFocus();
            resetHideTime(longTime);
        }
        rl_center_speed.setVisibility(isVisible);
    }

    //初始化选择播放速度的控件
    private void initSpeedView(int speed) {
        tv_speed05.setSelected(false);
        tv_speed10.setSelected(false);
        tv_speed12.setSelected(false);
        tv_speed15.setSelected(false);
        tv_speed20.setSelected(false);
        switch (speed) {
            case 5:
                tv_speed05.setSelected(true);
                tv_speed.setText("0.5x");
                break;
            case 10:
                tv_speed10.setSelected(true);
                tv_speed.setText("1x");
                break;
            case 12:
                tv_speed12.setSelected(true);
                tv_speed.setText("1.2x");
                break;
            case 15:
                tv_speed15.setSelected(true);
                tv_speed.setText("1.5x");
                break;
            case 20:
                tv_speed20.setSelected(true);
                tv_speed.setText("2x");
                break;
        }
    }

    //重置选择播放速度的控件
    private void resetSpeedView(int speed) {
        initSpeedView(speed);
        if (videoView != null) {
            videoView.setSpeed(speed / 10f);
        }
        hide();
    }

    //初始化选择码率的控件
    private void initBitRateView(int bitRate) {
        tv_sc.setSelected(false);
        tv_hd.setSelected(false);
        tv_flu.setSelected(false);
        tv_auto.setSelected(false);
        switch (bitRate) {
            case 0:
                tv_bit.setText("自动");
                tv_auto.setSelected(true);
                break;
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
    }

    //初始化选择码率控件的可见性
    private void initBitRateViewVisible(int currentBitRate) {
        tv_sc.setVisibility(View.GONE);
        tv_hd.setVisibility(View.GONE);
        tv_flu.setVisibility(View.GONE);
        tv_auto.setVisibility(View.GONE);
        if (videoVO != null) {
            switch (videoVO.getDfNum()) {
                case 1:
                    tv_flu.setVisibility(View.VISIBLE);
                    tv_auto.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    tv_hd.setVisibility(View.VISIBLE);
                    tv_flu.setVisibility(View.VISIBLE);
                    tv_auto.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    tv_sc.setVisibility(View.VISIBLE);
                    tv_hd.setVisibility(View.VISIBLE);
                    tv_flu.setVisibility(View.VISIBLE);
                    tv_auto.setVisibility(View.VISIBLE);
                    break;
            }
        } else {
            switch (currentBitRate) {
                case 0:
                    tv_auto.setVisibility(View.VISIBLE);
                    break;
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

    //重置播放码率布局的状态
    private void resetBitRateLayout(int isVisible) {
        resetBitRateLayout(isVisible, true);
    }

    private void resetBitRateLayout(int isVisible, boolean isShowTopBottomLayout) {
        if (isVisible == View.VISIBLE) {
            show(-1);
            resetTopBottomLayout(View.GONE, true);
            resetSpeedLayout(View.GONE, false);
        } else if (isShowTopBottomLayout) {
            resetTopBottomLayout(View.VISIBLE);
            requestFocus();
            resetHideTime(longTime);
        }
        rl_center_bit.setVisibility(isVisible);
    }

    //重置选择码率的控件
    private void resetBitRateView(int bitRate) {
        initBitRateView(bitRate);
        if (videoView != null)
            videoView.changeBitRate(bitRate);
        hide();
    }

    //重置显示/隐藏弹幕的控件
    private void resetDmSwitchView() {
        if (iv_dmswitch.isSelected()) {
            iv_dmswitch.setSelected(false);
            danmuFragment.show();
        } else {
            iv_dmswitch.setSelected(true);
            danmuFragment.hide();
        }
    }

    //网络截图
    private void screenshot() {
        String vid = null;
        int bit = 0;
        int currentTime_second = 0;
        int videoVODuration = 0;
        if (videoVO != null) {
            vid = videoVO.getVid();
            bit = videoVO.getDfNum();
            try {
                videoVODuration = (int) Float.parseFloat(videoVO.getDuration()) * 1000;
            } catch (Exception e) {
            }
        } else {
            toastMsg("截图失败：videoVO is null");
            return;
        }
        if (videoView != null) {
            int currentPosition = videoView.getCurrentPosition();
            int totalPosition = videoVODuration > 0 ? videoVODuration : videoView.getDuration();
            currentPosition = currentPosition > totalPosition ? totalPosition : currentPosition;
            currentTime_second = currentPosition / 1000;
        } else {
            toastMsg("截图失败：videoView is null");
            return;
        }
        new PolyvScreenShot(mContext).snapshot(vid, bit, currentTime_second, new PolyvScreenShot.ScreenshotListener() {
            @Override
            public void fail(Throwable throwable) {
                toastMsg("截图失败：" + throwable.getMessage());
            }

            @Override
            public void success(String s) {
                toastMsg("截图成功：" + s);
            }
        });
    }

    //发送弹幕并隐藏布局
    private void sendDanmaku() {
        danmuFragment.send(videoView, et_dmedit.getText().toString(), fontmode, fontsize, color);
        hide();
    }

    private void toastMsg(final String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_land:
                changeToLandscape();
                break;
            case R.id.iv_port:
                changeToPortrait();
                break;
            case R.id.iv_play:
                playOrPause();
                break;
            case R.id.iv_play_land:
                playOrPause();
                break;
            case R.id.iv_finish:
                changeToPortrait();
                break;
            case R.id.iv_set:
                resetSetLayout(View.VISIBLE);
                break;
            case R.id.iv_danmu:
                resetDanmuLayout(View.VISIBLE);
                break;
            case R.id.iv_dmset:
                resetDanmuBottomLayout();
                break;
            case R.id.iv_finish_danmu:
                hide();
                break;
            case R.id.iv_share:
                resetShareLayout(View.VISIBLE);
                break;
            case R.id.tv_full:
                resetRatioView(PolyvPlayerScreenRatio.AR_ASPECT_FILL_PARENT);
                break;
            case R.id.tv_fit:
                resetRatioView(PolyvPlayerScreenRatio.AR_ASPECT_FIT_PARENT);
                break;
            case R.id.tv_sixteennine:
                resetRatioView(PolyvPlayerScreenRatio.AR_16_9_FIT_PARENT);
                break;
            case R.id.tv_fourthree:
                resetRatioView(PolyvPlayerScreenRatio.AR_4_3_FIT_PARENT);
                break;
            case R.id.iv_dmblue:
                resetDammuColorView(Color.BLUE);
                break;
            case R.id.iv_dmgreen:
                resetDammuColorView(Color.GREEN);
                break;
            case R.id.iv_dmpurple:
                resetDammuColorView(Color.MAGENTA);
                break;
            case R.id.iv_dmred:
                resetDammuColorView(Color.RED);
                break;
            case R.id.iv_dmwhite:
                resetDammuColorView(Color.WHITE);
                break;
            case R.id.iv_dmyellow:
                resetDammuColorView(Color.YELLOW);
                break;
            case R.id.tv_dmroll:
                resetDanmaStyleView(PolyvDanmakuInfo.FONTMODE_ROLL);
                break;
            case R.id.tv_dmtop:
                resetDanmaStyleView(PolyvDanmakuInfo.FONTMODE_TOP);
                break;
            case R.id.tv_dmbottom:
                resetDanmaStyleView(PolyvDanmakuInfo.FONTMODE_BOTTOM);
                break;
            case R.id.tv_dmfonts:
                resetDanmaFontView(PolyvDanmakuInfo.FONTSIZE_SMALL);
                break;
            case R.id.tv_dmfontm:
                resetDanmaFontView(PolyvDanmakuInfo.FONTSIZE_MIDDLE);
                break;
            case R.id.tv_dmfontl:
                resetDanmaFontView(PolyvDanmakuInfo.FONTSIZE_LARGE);
                break;
            case R.id.et_dmedit:
                rl_dmbot.setVisibility(View.GONE);
                iv_dmset.setSelected(false);
                break;
            case R.id.iv_shareqq:
                PolyvShareUtils.shareQQFriend(mContext, "", "test", PolyvShareUtils.TEXT, null);
                hide();
                break;
            case R.id.iv_sharewechat:
                PolyvShareUtils.shareWeChatFriend(mContext, "", "test", PolyvShareUtils.TEXT, null);
                hide();
                break;
            case R.id.iv_shareweibo:
                PolyvShareUtils.shareWeiBo(mContext, "", "test", PolyvShareUtils.TEXT, null);
                hide();
                break;
            case R.id.iv_dmswitch:
                resetDmSwitchView();
                break;
            case R.id.tv_srt1:
                resetSrtView(0);
                break;
            case R.id.tv_srt2:
                resetSrtView(1);
                break;
            case R.id.tv_srt3:
                resetSrtView(2);
                break;
            case R.id.tv_srtnone:
                resetSrtView(3);
                break;
            case R.id.tv_speed:
                if (rl_center_speed.getVisibility() == View.GONE)
                    resetSpeedLayout(View.VISIBLE);
                else
                    resetSpeedLayout(View.GONE);
                break;
            case R.id.tv_bit:
                if (rl_center_bit.getVisibility() == View.GONE)
                    resetBitRateLayout(View.VISIBLE);
                else
                    resetBitRateLayout(View.GONE);
                break;
            case R.id.tv_sc:
                resetBitRateView(3);
                break;
            case R.id.tv_hd:
                resetBitRateView(2);
                break;
            case R.id.tv_flu:
                resetBitRateView(1);
                break;
            case R.id.tv_auto:
                resetBitRateView(0);
                break;
            case R.id.tv_speed05:
                resetSpeedView(5);
                break;
            case R.id.tv_speed10:
                resetSpeedView(10);
                break;
            case R.id.tv_speed12:
                resetSpeedView(12);
                break;
            case R.id.tv_speed15:
                resetSpeedView(15);
                break;
            case R.id.tv_speed20:
                resetSpeedView(20);
                break;
            case R.id.iv_close_bit:
                hide();
                break;
            case R.id.iv_close_set:
                hide();
                break;
            case R.id.iv_close_share:
                hide();
                break;
            case R.id.iv_close_speed:
                hide();
                break;
            case R.id.tv_dmsend:
                sendDanmaku();
                break;
            case R.id.iv_screens:
                screenshot();
                break;
        }
        //如果控制栏不是处于一直显示的状态，那么重置控制栏隐藏的时间
        if (!status_showalways)
            resetHideTime(longTime);
    }
}
