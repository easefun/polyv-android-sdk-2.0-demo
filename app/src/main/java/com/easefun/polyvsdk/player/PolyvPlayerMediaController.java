package com.easefun.polyvsdk.player;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.PictureInPictureParams;
import android.app.RemoteAction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.easefun.polyvsdk.PolyvDownloader;
import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.application.PolyvSettings;
import com.easefun.polyvsdk.fragment.PolyvPlayerDanmuFragment;
import com.easefun.polyvsdk.fragment.PolyvPlayerTopFragment;
import com.easefun.polyvsdk.ijk.PolyvPlayerScreenRatio;
import com.easefun.polyvsdk.player.knowledge.PolyvPlayerKnowledgeLayout;
import com.easefun.polyvsdk.player.knowledge.vo.PolyvPlayerKnowledgeVO;
import com.easefun.polyvsdk.ppt.PolyvPPTDirLayout;
import com.easefun.polyvsdk.ppt.PolyvViceScreenLayout;
import com.easefun.polyvsdk.sub.auxilliary.IOUtil;
import com.easefun.polyvsdk.sub.auxilliary.SDCardUtil;
import com.easefun.polyvsdk.sub.danmaku.entity.PolyvDanmakuInfo;
import com.easefun.polyvsdk.sub.screenshot.PolyvScreenShot;
import com.easefun.polyvsdk.util.PolyvKeyBoardUtils;
import com.easefun.polyvsdk.util.PolyvNetworkDetection;
import com.easefun.polyvsdk.util.PolyvSPUtils;
import com.easefun.polyvsdk.util.PolyvScopedStorageUtil;
import com.easefun.polyvsdk.util.PolyvScreenUtils;
import com.easefun.polyvsdk.util.PolyvSensorHelper;
import com.easefun.polyvsdk.util.PolyvShareUtils;
import com.easefun.polyvsdk.util.PolyvTimeUtils;
import com.easefun.polyvsdk.video.IPolyvVideoView;
import com.easefun.polyvsdk.video.PolyvBaseMediaController;
import com.easefun.polyvsdk.video.PolyvVideoUtil;
import com.easefun.polyvsdk.video.PolyvVideoView;
import com.easefun.polyvsdk.video.listener.IPolyvOnVideoSizeChangedListener2;
import com.easefun.polyvsdk.view.PolyvTickSeekBar;
import com.easefun.polyvsdk.view.PolyvTickTips;
import com.easefun.polyvsdk.vo.PolyvSRTItemVO;
import com.easefun.polyvsdk.vo.PolyvVideoVO;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private ImageView iv_land, iv_play, iv_vice_status_portrait, iv_pip_portrait;
    // 竖屏的显示播放进度控件，切换清晰度按钮，切换倍速按钮，切换线路按钮
    private TextView tv_curtime, tv_tottime, tv_bit_portrait, tv_speed_portrait, tv_route_portrait;
    // 竖屏的进度条
    private SeekBar sb_play;
    private RelativeLayout controllerCodecPortraitRl;
    private TextView controllerMediaCodecPortraitTv;
    private TextView controllerAvCodecPortraitTv;
    private TextView tvCodecPortrait;
    /**
     * 竖屏的播放速度布局
     */
    //播放速度布局
    private RelativeLayout rl_center_speed_portrait;
    //选择播放速度控件
    private TextView tv_speed05_portrait, tv_speed10_portrait, tv_speed12_portrait, tv_speed15_portrait, tv_speed20_portrait;
    /**
     * 竖屏的播放码率布局
     */
    //播放码率布局
    private RelativeLayout rl_center_bit_portrait;
    //选择播放码率的控件
    private TextView tv_sc_portrait, tv_hd_portrait, tv_flu_portrait, tv_auto_portrait;
    /**
     * 竖屏的播放线路布局
     */
    //播放线路布局
    private RelativeLayout rl_center_route_portrait;
    //选择播放码率的控件
    private TextView tv_route1_portrait, tv_route2_portrait, tv_route3_portrait;
    /**
     * 横屏的view
     */
    //横屏的控制栏，顶部布局，底部布局
    private RelativeLayout rl_land, rl_top, rl_bot;
    //横屏的切屏按钮，横屏的播放/暂停按钮,横屏的返回按钮，设置按钮，分享按钮，弹幕开关
    private ImageView iv_port, iv_play_land, iv_finish, iv_set, iv_share, iv_dmswitch, iv_vice_status, iv_pip;
    // 横屏的显示播放进度控件,视频的标题,选择播放速度按钮，选择码率按钮，选择线路按钮
    private TextView tv_curtime_land, tv_tottime_land, tv_title, tv_speed, tv_bit, tv_route, tv_ppt_dir, tvKnowledge;
    // 横屏的进度条
    private PolyvTickSeekBar sb_play_land;
    private RelativeLayout controllerCodecRl;
    private ImageView controllerCodecCloseIv;
    private TextView controllerMediaCodecTv;
    private TextView controllerAvCodecTv;
    private TextView tvCodecLand;
    /**
     * 侧边布局的view
     */
    //侧边布局
    private LinearLayout ll_side;
    //弹幕按钮，截图按钮
    private ImageView iv_danmu, iv_screens;
    /**
     * 左侧边布局的view
     */
    //左侧边布局
    private LinearLayout ll_left_side, ll_left_side_land, ll_left_side_t, ll_left_side_t_land;
    //视频/音频切换按钮
    private ImageView iv_video, iv_video_land, iv_audio, iv_audio_land;
    private TextView tv_video, tv_video_land, tv_audio, tv_audio_land;
    /**
     * 设置布局的view
     */
    //设置布局
    private RelativeLayout rl_center_set;
    //调节亮度控件，调节音量控件
    private SeekBar sb_light, sb_volume;
    // 设置播放器银幕比率控件，设置字幕的控件
    private LinearLayout ll_adaptive_mode, ll_subtitle;
    private TextView tv_full, tv_fit, tv_sixteennine, tv_fourthree;
    // 关闭布局按钮
    private ImageView iv_close_set;
    private GridLayout lg_subtitle_b;
    private TextView tv_srtnone, srt_change_mode_tv;
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
     * 横屏的播放速度布局
     */
    //播放速度布局
    private RelativeLayout rl_center_speed;
    //选择播放速度控件
    private TextView tv_speed05, tv_speed10, tv_speed12, tv_speed15, tv_speed20;
    //关闭布局按钮
    private ImageView iv_close_speed;
    /**
     * 横屏的播放码率布局
     */
    //播放码率布局
    private RelativeLayout rl_center_bit;
    //选择播放码率的控件
    private TextView tv_sc, tv_hd, tv_flu, tv_auto;
    //关闭布局按钮
    private ImageView iv_close_bit;
    /**
     * 横屏的播放线路布局
     */
    //播放线路布局
    private RelativeLayout rl_center_route;
    //选择播放码率的控件
    private TextView tv_route1, tv_route2, tv_route3;
    //关闭布局按钮
    private ImageView iv_close_route;
    // 知识清单布局
    private PolyvPlayerKnowledgeLayout knowledgeLayout;
    //-----------------------------------------
    // 进度条是否处于拖动的状态
    private boolean status_dragging;
    // 控制栏是否处于一直显示的状态
    private boolean status_showalways;
    // 播放器在显示弹幕布局前的状态
    private boolean status_isPlaying;
    private PolyvSensorHelper sensorHelper;
    private PolyvPlayerAudioCoverView coverView;
    private PolyvTickTips tickTips;

    private ImageView polyvScreenLock, polyvScreenLockAudio;

    //网络监测
    private PolyvNetworkDetection networkDetection;
    private LinearLayout flowPlayLayout;
    private View flowButton, cancelFlowButton;
    private int fileType;

    //副屏布局
    private PolyvViceScreenLayout viceLayout;
    private PolyvPPTDirLayout landPptDirLayout;

    private PictureInPictureParams.Builder pipBuilder;
    private boolean isViceHideInPipMode;

    private PolyvSettings videoSetting = new PolyvSettings(getContext());

    //全屏策略
    private static final int FULLSCREEN_RATIO = 0;//根据视频宽高判断，当宽>=高时，使用横屏全屏
    private static final int FULLSCREEN_LANDSCAPE = 1;//使用横屏全屏
    private static final int FULLSCREEN_PORTRAIT = 2;//使用竖屏全屏
    private int fullScreenStrategy = FULLSCREEN_RATIO;
    private int videoWidth, videoHeight;
    private boolean isFullScreen;

    //进度条拖拽跳转播放进度策略
    public static final int DRAG_SEEK_ALLOW = 0;//允许拖动进度条跳转进度
    public static final int DRAG_SEEK_BAN = 1;//禁止拖动进度条跳转进度
    public static final int DRAG_SEEK_PLAYED = 2;//只允许在已播放进度区域拖动跳转播放进度
    private int dragSeekStrategy = DRAG_SEEK_PLAYED;
    private OnDragSeekListener onDragSeekListener;

    private static final int SAVE_PROGRESS = 30;

    // 字幕模式
    private boolean isSrtSingleMode = true;

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
                case SAVE_PROGRESS:
                    saveProgress();
                    break;
            }
        }
    };

    private int getPosition() {
        if (videoView != null) {
            // 单位：毫秒
            int position = videoView.getCurrentPosition();
            int totalTime = videoView.getDuration() / 1000 * 1000;
            if (!videoView.isExceptionCompleted() && (videoView.isCompletedState() || position > totalTime))
                position = totalTime;
            return position;
        }
        return 0;
    }

    private int getSavePosition() {
        if (videoView != null && videoView.getCurrentVid() != null) {
            //保存当前播放进度
            return PolyvSPUtils.getInstance(getContext(), "videoProgress").getInt(videoView.getCurrentVid());
        }
        return 0;
    }

    @Override
    public boolean canDragSeek(int seekPosition) {
        boolean canDragSeek = true;
        if (dragSeekStrategy == DRAG_SEEK_PLAYED) {
            canDragSeek = seekPosition <= getSavePosition();
        } else if (dragSeekStrategy == DRAG_SEEK_BAN) {
            canDragSeek = false;
        }

        if (onDragSeekListener == null || videoView == null) {
            return canDragSeek;
        }
        if (canDragSeek) {
            onDragSeekListener.onDragSeekSuccess(videoView.getCurrentPosition(), seekPosition);
        } else {
            onDragSeekListener.onDragSeekBan(dragSeekStrategy);
        }
        return canDragSeek;
    }

    private void saveProgress() {
        if (videoView != null && videoView.getCurrentVid() != null) {
            // 单位：毫秒
            int position = getPosition();
            //保存当前播放进度
            int maxPosition = PolyvSPUtils.getInstance(getContext(), "videoProgress").getInt(videoView.getCurrentVid());
            if (position > maxPosition) {
                PolyvSPUtils.getInstance(getContext(), "videoProgress").put(videoView.getCurrentVid(), position);
            }
            handler.sendEmptyMessageDelayed(SAVE_PROGRESS, 3000);
        }
    }

    // 更新显示的播放进度，以及暂停/播放按钮
    private void showProgress() {
        if (isShowing && videoView != null) {
            // 单位：毫秒
            int position = getPosition();
            int totalTime = videoView.getDuration() / 1000 * 1000;
            int bufPercent = videoView.getBufferPercentage();
            //在拖动进度条的时候，这里不更新
            if (!status_dragging) {
                tv_curtime.setText(PolyvTimeUtils.generateTime(position));
                tv_curtime_land.setText(PolyvTimeUtils.generateTime(position));
                if (totalTime > 0) {
                    sb_play.setProgress((int) (sb_play.getMax() * 1L * position / totalTime));
                    sb_play_land.setProgress((int) (sb_play_land.getMax() * 1L * position / totalTime));
                } else {
                    sb_play.setProgress(0);
                    sb_play_land.setProgress(0);
                }
            }
            sb_play.setSecondaryProgress(sb_play.getMax() * bufPercent / 100);
            sb_play_land.setSecondaryProgress(sb_play_land.getMax() * bufPercent / 100);
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

    public void setPolyvNetworkDetetion(PolyvNetworkDetection networkDetetion, LinearLayout layout, View button, View cancelButton, int fileType) {
        this.networkDetection = networkDetetion;
        this.flowPlayLayout = layout;
        this.flowButton = button;
        this.cancelFlowButton = cancelButton;
        this.fileType = fileType;
    }

    public void setPPTLayout(PolyvViceScreenLayout viceLayout, PolyvPPTDirLayout landPptDirLayout) {
        this.viceLayout = viceLayout;
        this.landPptDirLayout = landPptDirLayout;
    }

    public void setDanmuFragment(PolyvPlayerDanmuFragment danmuFragment) {
        this.danmuFragment = danmuFragment;
    }

    public void setAudioCoverView(PolyvPlayerAudioCoverView coverView) {
        this.coverView = coverView;
    }

    private void findIdAndNew() {
        //竖屏的view
        rl_port = (RelativeLayout) view.findViewById(R.id.rl_port);
        iv_land = (ImageView) view.findViewById(R.id.iv_land);
        iv_play = (ImageView) view.findViewById(R.id.iv_play);
        iv_vice_status_portrait = (ImageView) view.findViewById(R.id.iv_vice_status_portrait);
        iv_pip_portrait = (ImageView) view.findViewById(R.id.iv_pip_portrait);
        tv_curtime = (TextView) view.findViewById(R.id.tv_curtime);
        tv_tottime = (TextView) view.findViewById(R.id.tv_tottime);
        tv_bit_portrait = (TextView) view.findViewById(R.id.tv_bit_portrait);
        tv_speed_portrait = (TextView) view.findViewById(R.id.tv_speed_portrait);
        tv_route_portrait = (TextView) view.findViewById(R.id.tv_route_portrait);
        sb_play = (SeekBar) view.findViewById(R.id.sb_play);
        controllerCodecPortraitRl = findViewById(R.id.plv_controller_codec_portrait_rl);
        controllerMediaCodecPortraitTv = findViewById(R.id.plv_controller_media_codec_portrait_tv);
        controllerAvCodecPortraitTv = findViewById(R.id.plv_controller_av_codec_portrait_tv);
        tvCodecPortrait = findViewById(R.id.tv_codec_portrait);
        //竖屏的播放速度布局的view
        rl_center_speed_portrait = (RelativeLayout) view.findViewById(R.id.rl_center_speed_portrait);
        tv_speed05_portrait = (TextView) view.findViewById(R.id.tv_speed05_portrait);
        tv_speed10_portrait = (TextView) view.findViewById(R.id.tv_speed10_portrait);
        tv_speed12_portrait = (TextView) view.findViewById(R.id.tv_speed12_portrait);
        tv_speed15_portrait = (TextView) view.findViewById(R.id.tv_speed15_portrait);
        tv_speed20_portrait = (TextView) view.findViewById(R.id.tv_speed20_portrait);
        //竖屏的播放码率布局的view
        rl_center_bit_portrait = (RelativeLayout) view.findViewById(R.id.rl_center_bit_portrait);
        tv_sc_portrait = (TextView) view.findViewById(R.id.tv_sc_portrait);
        tv_hd_portrait = (TextView) view.findViewById(R.id.tv_hd_portrait);
        tv_flu_portrait = (TextView) view.findViewById(R.id.tv_flu_portrait);
        tv_auto_portrait = (TextView) view.findViewById(R.id.tv_auto_portrait);
        //视频的播放线路布局的view
        rl_center_route_portrait = (RelativeLayout) view.findViewById(R.id.rl_center_route_portrait);
        tv_route1_portrait = (TextView) view.findViewById(R.id.tv_route1_portrait);
        tv_route2_portrait = (TextView) view.findViewById(R.id.tv_route2_portrait);
        tv_route3_portrait = (TextView) view.findViewById(R.id.tv_route3_portrait);
        //横屏的view
        rl_land = (RelativeLayout) view.findViewById(R.id.rl_land);
        rl_top = (RelativeLayout) view.findViewById(R.id.rl_top);
        rl_bot = (RelativeLayout) view.findViewById(R.id.rl_bot);
        iv_port = (ImageView) view.findViewById(R.id.iv_port);
        iv_play_land = (ImageView) view.findViewById(R.id.iv_play_land);
        iv_finish = (ImageView) view.findViewById(R.id.iv_finish);
        iv_vice_status = (ImageView) view.findViewById(R.id.iv_vice_status);
        iv_pip = (ImageView) view.findViewById(R.id.iv_pip);
        tv_curtime_land = (TextView) view.findViewById(R.id.tv_curtime_land);
        tv_tottime_land = (TextView) view.findViewById(R.id.tv_tottime_land);
        sb_play_land = (PolyvTickSeekBar) view.findViewById(R.id.sb_play_land);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        iv_set = (ImageView) view.findViewById(R.id.iv_set);
        iv_share = (ImageView) view.findViewById(R.id.iv_share);
        iv_dmswitch = (ImageView) view.findViewById(R.id.iv_dmswitch);
        tv_speed = (TextView) view.findViewById(R.id.tv_speed);
        tv_bit = (TextView) view.findViewById(R.id.tv_bit);
        tv_route = (TextView) view.findViewById(R.id.tv_route);
        tv_ppt_dir = (TextView) view.findViewById(R.id.tv_ppt_dir);
        tvKnowledge = (TextView) view.findViewById(R.id.tv_knowledge);
        controllerCodecRl = findViewById(R.id.plv_controller_codec_rl);
        controllerCodecCloseIv = findViewById(R.id.plv_controller_codec_close_iv);
        controllerMediaCodecTv = findViewById(R.id.plv_controller_media_codec_tv);
        controllerAvCodecTv = findViewById(R.id.plv_controller_av_codec_tv);
        tvCodecLand = findViewById(R.id.tv_codec_land);
        //设置布局的view
        rl_center_set = (RelativeLayout) view.findViewById(R.id.rl_center_set);
        sb_light = (SeekBar) view.findViewById(R.id.sb_light);
        sb_volume = (SeekBar) view.findViewById(R.id.sb_volume);
        tv_full = (TextView) view.findViewById(R.id.tv_full);
        tv_fit = (TextView) view.findViewById(R.id.tv_fit);
        tv_sixteennine = (TextView) view.findViewById(R.id.tv_sixteennine);
        tv_fourthree = (TextView) view.findViewById(R.id.tv_fourthree);
        iv_close_set = (ImageView) view.findViewById(R.id.iv_close_set);
        ll_adaptive_mode = (LinearLayout) findViewById(R.id.ll_adaptive_mode);
        ll_subtitle = (LinearLayout) findViewById(R.id.ll_subtitle);
        lg_subtitle_b = (GridLayout) findViewById(R.id.lg_subtitle_b);
        tv_srtnone = (TextView) view.findViewById(R.id.tv_srtnone);
        srt_change_mode_tv = (TextView) view.findViewById(R.id.srt_change_mode_tv);
        //侧边布局的view
        ll_side = (LinearLayout) view.findViewById(R.id.ll_side);
        iv_danmu = (ImageView) view.findViewById(R.id.iv_danmu);
        iv_screens = (ImageView) view.findViewById(R.id.iv_screens);
        //左侧边布局的view
        ll_left_side = (LinearLayout) view.findViewById(R.id.ll_left_side);
        ll_left_side_land = (LinearLayout) view.findViewById(R.id.ll_left_side_land);
        ll_left_side_t = (LinearLayout) view.findViewById(R.id.ll_left_side_t);
        ll_left_side_t_land = (LinearLayout) view.findViewById(R.id.ll_left_side_t_land);
        iv_video = (ImageView) view.findViewById(R.id.iv_video);
        iv_video_land = (ImageView) view.findViewById(R.id.iv_video_land);
        iv_audio = (ImageView) view.findViewById(R.id.iv_audio);
        iv_audio_land = (ImageView) view.findViewById(R.id.iv_audio_land);
        tv_video = (TextView) view.findViewById(R.id.tv_video);
        tv_video_land = (TextView) view.findViewById(R.id.tv_video_land);
        tv_audio = (TextView) view.findViewById(R.id.tv_audio);
        tv_audio_land = (TextView) view.findViewById(R.id.tv_audio_land);
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
        //横屏的播放速度布局的view
        rl_center_speed = (RelativeLayout) view.findViewById(R.id.rl_center_speed);
        tv_speed05 = (TextView) view.findViewById(R.id.tv_speed05);
        tv_speed10 = (TextView) view.findViewById(R.id.tv_speed10);
        tv_speed12 = (TextView) view.findViewById(R.id.tv_speed12);
        tv_speed15 = (TextView) view.findViewById(R.id.tv_speed15);
        tv_speed20 = (TextView) view.findViewById(R.id.tv_speed20);
        iv_close_speed = (ImageView) view.findViewById(R.id.iv_close_speed);
        //横屏的播放码率布局的view
        rl_center_bit = (RelativeLayout) view.findViewById(R.id.rl_center_bit);
        tv_sc = (TextView) view.findViewById(R.id.tv_sc);
        tv_hd = (TextView) view.findViewById(R.id.tv_hd);
        tv_flu = (TextView) view.findViewById(R.id.tv_flu);
        tv_auto = (TextView) view.findViewById(R.id.tv_auto);
        iv_close_bit = (ImageView) view.findViewById(R.id.iv_close_bit);
        //横屏的播放线路布局的view
        rl_center_route = (RelativeLayout) view.findViewById(R.id.rl_center_route);
        tv_route1 = (TextView) view.findViewById(R.id.tv_route1);
        tv_route2 = (TextView) view.findViewById(R.id.tv_route2);
        tv_route3 = (TextView) view.findViewById(R.id.tv_route3);
        iv_close_route = (ImageView) view.findViewById(R.id.iv_close_route);
        // 知识清单布局
        knowledgeLayout = (PolyvPlayerKnowledgeLayout) view.findViewById(R.id.knowledge_layout);

        sensorHelper = new PolyvSensorHelper(videoActivity);
        tickTips = (PolyvTickTips) view.findViewById(R.id.fl_tt);
        tickTips.setOnSeekClickListener(new PolyvTickTips.OnSeekClickListener() {
            @Override
            public void onSeekClick(PolyvTickSeekBar.TickData tickData) {
                if (videoView != null) {
                    int seekPosition = tickData.getKeyTime() * 1000;
                    if (canDragSeek(seekPosition)) {
                        videoView.seekTo(seekPosition);
                    }
                    tickTips.hide();
                }
            }
        });

        knowledgeLayout.setOnViewActionListener(new PolyvPlayerKnowledgeLayout.OnViewActionListener() {
            @Override
            public void onReady() {
                tvKnowledge.setVisibility(VISIBLE);
            }

            @Override
            public void onClickClose() {
                if (isShowing() && status_showalways) {
                    show(longTime);
                }
            }

            @Override
            public void onClickKnowledgePoint(PolyvPlayerKnowledgeVO.WordType.WordKey.KnowledgePoint knowledgePoint) {
                if (knowledgePoint != null && knowledgePoint.getTime() != null) {
                    videoView.seekTo(knowledgePoint.getTime() * 1000);
                }
            }

            @Override
            public void onAutoClose() {
                if (isShowing() && status_showalways) {
                    show(longTime);
                }
            }
        });

        polyvScreenLock = (ImageView) view.findViewById(R.id.polyv_screen_lock);
        polyvScreenLockAudio = (ImageView) view.findViewById(R.id.polyv_screen_lock_audio);

        if (Build.VERSION.SDK_INT >= 26) {
            pipBuilder = new PictureInPictureParams.Builder();
            iv_pip_portrait.setVisibility(View.VISIBLE);
            iv_pip.setVisibility(View.VISIBLE);
        }
    }

    public void showAudioLock(boolean show) {
        polyvScreenLockAudio.setVisibility(show ? VISIBLE : GONE);
        polyvScreenLock.setVisibility(show ? GONE : VISIBLE);

        polyvScreenLock.setSelected(false);
        polyvScreenLockAudio.setSelected(false);
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
        tv_speed.setOnClickListener(this);
        tv_speed_portrait.setOnClickListener(this);
        tv_speed05.setOnClickListener(this);
        tv_speed05_portrait.setOnClickListener(this);
        tv_speed10.setOnClickListener(this);
        tv_speed10_portrait.setOnClickListener(this);
        tv_speed12.setOnClickListener(this);
        tv_speed12_portrait.setOnClickListener(this);
        tv_speed15.setOnClickListener(this);
        tv_speed15_portrait.setOnClickListener(this);
        tv_speed20.setOnClickListener(this);
        tv_speed20_portrait.setOnClickListener(this);
        tv_bit.setOnClickListener(this);
        tv_bit_portrait.setOnClickListener(this);
        tv_sc.setOnClickListener(this);
        tv_sc_portrait.setOnClickListener(this);
        tv_hd.setOnClickListener(this);
        tv_hd_portrait.setOnClickListener(this);
        tv_flu.setOnClickListener(this);
        tv_flu_portrait.setOnClickListener(this);
        tv_auto.setOnClickListener(this);
        tv_auto_portrait.setOnClickListener(this);
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
        iv_video.setOnClickListener(this);
        iv_video_land.setOnClickListener(this);
        iv_audio.setOnClickListener(this);
        iv_audio_land.setOnClickListener(this);
        polyvScreenLock.setOnClickListener(this);
        polyvScreenLockAudio.setOnClickListener(this);
        tv_route.setOnClickListener(this);
        tv_route_portrait.setOnClickListener(this);
        tv_route1.setOnClickListener(this);
        tv_route1_portrait.setOnClickListener(this);
        tv_route2.setOnClickListener(this);
        tv_route2_portrait.setOnClickListener(this);
        tv_route3.setOnClickListener(this);
        tv_route3_portrait.setOnClickListener(this);
        iv_close_route.setOnClickListener(this);
        iv_vice_status_portrait.setOnClickListener(this);
        iv_vice_status.setOnClickListener(this);
        tv_ppt_dir.setOnClickListener(this);
        tvKnowledge.setOnClickListener(this);
        iv_pip_portrait.setOnClickListener(this);
        iv_pip.setOnClickListener(this);
        controllerAvCodecPortraitTv.setOnClickListener(this);
        controllerMediaCodecPortraitTv.setOnClickListener(this);
        tvCodecPortrait.setOnClickListener(this);
        controllerAvCodecTv.setOnClickListener(this);
        controllerMediaCodecTv.setOnClickListener(this);
        controllerCodecCloseIv.setOnClickListener(this);
        tvCodecLand.setOnClickListener(this);
        tv_srtnone.setOnClickListener(this);
        srt_change_mode_tv.setOnClickListener(this);
    }

    //是否显示左侧边的切换音视频的布局
    private boolean canShowLeftSideView() {
        //是否可以获取到音频的播放地址
        return videoVO != null && videoVO.hasAudioPath();
    }

    public void resetView() {
        if (ll_subtitle != null) {
            ll_subtitle.setVisibility(View.GONE);
        }
        if (lg_subtitle_b != null) {
            lg_subtitle_b.setVisibility(View.GONE);
        }
    }

    public void preparedSRT(IPolyvVideoView videoView) {
        if (videoView != null) {
            this.videoView = (PolyvVideoView) videoView;
            this.videoVO = videoView.getVideo();
            //初始化字幕控件
            initSrtView(videoView.getCurrSRTKey());

            int visibility = PolyvVideoVO.MODE_AUDIO.equals(videoView.getCurrentMode())
                    || !videoVO.getPlayer().isSubtitlesEnabled()  ? View.GONE : View.VISIBLE;
            ll_subtitle.setVisibility(visibility);
            lg_subtitle_b.setVisibility(visibility);

            srt_change_mode_tv.setVisibility(videoVO.getPlayer().isSubtitleDoubleEnable() ? View.VISIBLE : View.GONE);
            if (videoVO.getPlayer().isSubtileDefaultDouble()) {
                changeSrtMode(false);
            }
        }
    }

    public void preparedView() {
        if (videoView != null) {
            videoVO = videoView.getVideo();
            videoWidth = videoView.getVideoWidth();
            videoHeight = videoView.getVideoHeight();

            this.videoView.setOnVideoSizeChangedListener(new IPolyvOnVideoSizeChangedListener2() {
                @Override
                public void onVideoSizeChanged(int width, int height, int sarNum, int sarDen) {
                    if (width != videoWidth || height != videoHeight) {
                        videoWidth = width;
                        videoHeight = height;
                        videoView.getRenderView().setVideoSize(width, height);
                    }
                }
            });

            showAudioLock(canShowLeftSideView());
            if (videoVO != null)
                tv_title.setText(videoVO.getTitle());
            int totalTime = videoView.getDuration();
            tv_tottime.setText(PolyvTimeUtils.generateTime(totalTime));
            tv_tottime_land.setText(PolyvTimeUtils.generateTime(totalTime));
            //初始化播放器的银幕比率的显示控件
            initRatioView(videoView.getCurrentAspectRatio());
            //初始化倍速控件及其可见性
            initSpeedView((int) (videoView.getSpeed() * 10));
            //初始化码率控件及其可见性
            initBitRateView(videoView.getBitRate());
            initBitRateViewVisible(videoView.getBitRate());
            //初始化切换线路及其可见性
            initRouteView();
            //非全屏和全屏的控制栏的切换线路按钮默认不可见，如需更改为可见，注释这两行代码即可
            tv_route_portrait.setVisibility(View.GONE);
            tv_route.setVisibility(View.GONE);

            //音频模式下，隐藏切换码率/填充模式/字幕/截图的按钮
            int visibility = PolyvVideoVO.MODE_AUDIO.equals(videoView.getCurrentMode()) ? View.GONE : View.VISIBLE;
            if (visibility == View.GONE) {
                rl_center_bit.setVisibility(visibility);
                rl_center_bit_portrait.setVisibility(visibility);
                rl_center_route.setVisibility(visibility);
                rl_center_route_portrait.setVisibility(visibility);
            }
            tv_bit.setVisibility(visibility);
            tv_bit_portrait.setVisibility(visibility);
            ll_adaptive_mode.setVisibility(visibility);
            iv_screens.setVisibility(visibility);
            //设置进度条的打点位置
            if (PolyvVideoVO.MODE_VIDEO.equals(videoView.getCurrentMode())) {
                List<PolyvVideoVO.Videokeyframe> videokeyframes;
                if (videoVO != null && (videokeyframes = videoVO.getVideokeyframes()) != null) {
                    int maxProgress = videoView.getDuration() / 1000;
                    double rate = 1;
                    if (maxProgress < 1000) {//最大进度最小为1000
                        rate = 1000 * 1.0 / maxProgress;
                        maxProgress = 1000;
                    }
                    List<PolyvTickSeekBar.TickData> tickDataList = new ArrayList<>();
                    for (PolyvVideoVO.Videokeyframe videokeyframe : videokeyframes) {
                        //打点的颜色请设置和seekBar的thumb的颜色一致，因为打点是在thumb上层的
                        tickDataList.add(new PolyvTickSeekBar.TickData(videokeyframe.getKeytime(), (float) (videokeyframe.getKeytime() * rate), Color.WHITE, videokeyframe));
                    }
                    sb_play_land.setMax(maxProgress);
                    sb_play_land.setTicks(tickDataList);
                    sb_play_land.setOnTickClickListener(new PolyvTickSeekBar.OnTickClickListener() {
                        @Override
                        public void onTickClick(PolyvTickSeekBar.TickData tickData) {
                            tickTips.show(tickData);
                            resetHideTime(longTime);
                        }

                        @Override
                        public boolean onSeekBarClick() {
                            tickTips.hide();
                            resetHideTime(longTime);
                            return true;//false：点击非打点处不触发onProgressChanged
                        }
                    });
                }
            }

            //如果可以获取到音频的播放地址，则显示切换到音频的按钮
            if (canShowLeftSideView()) {
                resetLeftSideView(View.VISIBLE);
                if (PolyvVideoVO.MODE_VIDEO.equals(videoView.getCurrentMode())) {
                    resetModeView(true);
                } else {
                    resetModeView(false);
                }
            } else {
                resetLeftSideView(View.GONE);
            }
            //ppt状态
            if (videoVO != null && videoVO.hasPPT() && videoView.isPPTEnabled()) {
                iv_vice_status_portrait.setVisibility(View.VISIBLE);
                iv_vice_status.setVisibility(View.VISIBLE);
                tv_ppt_dir.setVisibility(View.VISIBLE);
            } else {
                iv_vice_status_portrait.setVisibility(View.GONE);
                iv_vice_status.setVisibility(View.GONE);
                tv_ppt_dir.setVisibility(View.GONE);
            }
            updateCodecName();
        }
        // 视频准备完成后，开启随手势自动切换屏幕
        if (PolyvScreenUtils.isLandscape(mContext))
            sensorHelper.toggle(isAutoSwitchOrientation(), false);
        else
            sensorHelper.toggle(isAutoSwitchOrientation(), true);
        //视频准备完成后，定时记录当前播放的最大进度(用于禁止进度条拖拽功能)
        handler.removeMessages(SAVE_PROGRESS);
        handler.sendEmptyMessage(SAVE_PROGRESS);
        PolyvSPUtils.getInstance(getContext(), "dragSeekStrategy").put("dragSeekStrategy", dragSeekStrategy);
    }

    private boolean isAutoSwitchOrientation() {
        return fullScreenStrategy == FULLSCREEN_LANDSCAPE || (fullScreenStrategy == FULLSCREEN_RATIO && videoWidth >= videoHeight);
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
            handler.removeMessages(HIDE);
            handler.removeMessages(SHOW_PROGRESS);
            resetPopupLayout();
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

    public void hideTickTips() {
        tickTips.hide();
    }

    // 关闭监听
    public void pause() {
        sensorHelper.disable();
    }

    // 开启监听
    public void resume() {
        sensorHelper.enable();
        if (PolyvScreenUtils.isLandscape(videoActivity)){
            PolyvScreenUtils.hideNavigationBar(videoActivity);
        }
    }

    /**
     * 退出播放器的Activity时需调用
     */
    public void disable() {
        hide();
        sensorHelper.disable();
        handler.removeCallbacksAndMessages(null);
    }

    private void resetPopupLayout(){
        resetSetLayout(View.GONE);
        resetDanmuLayout(View.GONE);
        resetShareLayout(View.GONE);
        resetSpeedLayout(View.GONE);
        resetBitRateLayout(View.GONE);
        resetRouteLayout(View.GONE);
        resetCodecLayout(View.GONE);
        hidePortraitPopupView();
        tickTips.hide();
        knowledgeLayout.show(false);
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
        if (isFullScreen && (polyvScreenLock.isSelected() || polyvScreenLockAudio.isSelected())) {
            setVisibility(View.VISIBLE);

            updateLockStatus();
            resetPopupLayout();
            resetSideLayout(View.GONE);
            resetLeftSideView(View.GONE);
            resetTopBottomLayout(View.GONE);
            isShowing = true;
        } else {
            if (!isShowing) {
                resetTopBottomLayout(View.VISIBLE);
                resetSideLayout(View.VISIBLE);
                if (canShowLeftSideView()) {
                    resetLeftSideView(View.VISIBLE);
                }
                //获取焦点
                requestFocus();
                handler.removeMessages(SHOW_PROGRESS);
                handler.sendEmptyMessage(SHOW_PROGRESS);
                isShowing = !isShowing;
                setVisibility(View.VISIBLE);
            }
            sensorHelper.toggle(isAutoSwitchOrientation(), PolyvScreenUtils.isLandscape(getContext()));
        }


        resetHideTime(timeout);
    }

    private void updateLockStatus() {
        boolean show = canShowLeftSideView();
        polyvScreenLockAudio.setVisibility(show ? VISIBLE : GONE);
        polyvScreenLock.setVisibility(show ? GONE : VISIBLE);

        sensorHelper.toggle(!polyvScreenLockAudio.isSelected() && !polyvScreenLock.isSelected() && isAutoSwitchOrientation(), true);
    }

    @Override
    public void show() {
        show(longTime);
    }

    public boolean isFullScreen() {
        return isFullScreen;
    }

    /**
     * 切换到全屏
     */
    public void changeToFullScreen() {
        if (fullScreenStrategy == FULLSCREEN_PORTRAIT) {
            changeToFullPortrait();
        } else if (fullScreenStrategy == FULLSCREEN_LANDSCAPE) {
            changeToFullLandscape();
        } else {
            if (videoWidth >= videoHeight) {
                changeToFullLandscape();
            } else {
                changeToFullPortrait();
            }
        }
    }

    /**
     * 切换到横屏全屏
     */
    public void changeToFullLandscape() {
        PolyvScreenUtils.setLandscape(videoActivity);
        //初始为横屏时，状态栏需要隐藏
        PolyvScreenUtils.hideStatusBar(videoActivity);
        //初始为横屏时，导航栏需要隐藏
        PolyvScreenUtils.hideNavigationBar(videoActivity);
        //初始为横屏时，控制栏的宽高需要设置
        initFullScreenWH();
    }

    private void initFullScreenWH() {
        isFullScreen = true;
        ViewGroup.LayoutParams vlp = parentView.getLayoutParams();
        vlp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        vlp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        rl_land.setVisibility(View.VISIBLE);
        rl_port.setVisibility(View.GONE);
    }

    /**
     * 切换到竖屏全屏
     */
    public void changeToFullPortrait() {
        PolyvScreenUtils.hideStatusBar(videoActivity);
        PolyvScreenUtils.hideNavigationBar(videoActivity);
        initFullScreenWH();
    }

    /**
     * 切换到竖屏小窗
     */
    public void changeToSmallScreen() {
        PolyvScreenUtils.setPortrait(videoActivity);
        initSmallScreenWH();
    }

    private void initSmallScreenWH() {
        isFullScreen = false;
        ViewGroup.LayoutParams vlp = parentView.getLayoutParams();
        vlp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        vlp.height = PolyvScreenUtils.getHeight16_9();
        rl_port.setVisibility(View.VISIBLE);
        rl_land.setVisibility(View.GONE);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        resetControllerLayout();
    }

    //根据屏幕状态改变控制栏布局
    private void resetControllerLayout() {
        if(polyvScreenLock.isSelected() || polyvScreenLockAudio.isSelected()){
            return;
        }
        hide();
        PolyvScreenUtils.reSetStatusBar(videoActivity);
        if (PolyvScreenUtils.isLandscape(mContext)) {
            // 横屏下开启自动切换横竖屏
            sensorHelper.toggle(isAutoSwitchOrientation(), true);
            initFullScreenWH();
        } else {
            // 竖屏下开启自动切换横竖屏
            sensorHelper.toggle(isAutoSwitchOrientation(), false);
            initSmallScreenWH();
        }
    }

    //根据视频的播放状态去暂停或播放
    public void playOrPause() {
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
            tickTips.hide();
            switch (seekBar.getId()) {
                case R.id.sb_play:
                case R.id.sb_play_land:
                    resetHideTime(longTime);
                    status_dragging = true;
                    if (videoView != null) {
                        int newPosition = (int) (videoView.getDuration() * (long) i / seekBar.getMax());
                        tv_curtime.setText(PolyvTimeUtils.generateTime(newPosition));
                        tv_curtime_land.setText(PolyvTimeUtils.generateTime(newPosition));
                    }
                    break;
                case R.id.sb_light:
                    if (videoView != null)
                        videoView.setBrightness(videoActivity, i);
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
                        int seekToPosition = (int) (videoView.getDuration() * (long) seekBar.getProgress() / seekBar.getMax());
                        if (!videoView.isCompletedState()) {
                            if (canDragSeek(seekToPosition)) {
                                videoView.seekTo(seekToPosition);
                                danmuFragment.seekTo();
                            }
                        } else if (videoView.isCompletedState() && seekToPosition / seekBar.getMax() * seekBar.getMax() < videoView.getDuration() / seekBar.getMax() * seekBar.getMax()) {
                            if (canDragSeek(seekToPosition)) {
                                videoView.seekTo(seekToPosition);
                                danmuFragment.seekTo();
                                videoView.start();
                                danmuFragment.resume();
                            }
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
            resetLeftSideView(View.GONE);
            if (videoView != null) {
                sb_light.setProgress(videoView.getBrightness(videoActivity));
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
            resetLeftSideView(View.GONE);
            resetBitRateLayout(View.GONE, false);
            resetSpeedLayout(View.GONE, false);
            resetRouteLayout(View.GONE, false);
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
            resetLeftSideView(View.GONE);
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

    //切换单/双字幕
    private void changeSrtMode(boolean isSingle) {
        if (!isSingle) {
            selectSrt(-1);
            srt_change_mode_tv.setSelected(true);
            List<String> srtKeys = new ArrayList<String>();
            for (PolyvSRTItemVO srtvo : videoVO.getVideoSRTList()) {
                srtKeys.add(srtvo.getTitle());
            }
            if (srtKeys.size() != 0) {
            videoView.changeSRT(srtKeys.get(0));
            }
        }
        isSrtSingleMode = isSingle;
        if (videoView != null) {
            videoView.changeSRTMode(isSrtSingleMode);
        }
    }

    //初始化选择字幕的控件
    void initSrtView(String srtKey) {
        List<String> srtKeys = new ArrayList<String>();
        if (videoVO != null) {
            for (PolyvSRTItemVO srtItemVO : videoVO.getVideoSRTList()) {
                srtKeys.add(srtItemVO.getTitle());
            }
        }
        for (int i = 0; i < srtKeys.size(); i++) {
            insertTextView(srtKeys.get(i));
        }
        // 选择默认字幕
        selectSrt(srtKeys.indexOf(srtKey) + 2);
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
            resetRouteLayout(View.GONE, false);
            resetCodecLayout(View.GONE, false);
        } else if (isShowTopBottomLayout) {
            resetTopBottomLayout(View.VISIBLE);
            requestFocus();
            resetHideTime(longTime);
        }
        rl_center_speed.setVisibility(isVisible);
    }

    //初始化选择播放速度的控件
    public void initSpeedView(int speed) {
        tv_speed05.setSelected(false);
        tv_speed05_portrait.setSelected(false);
        tv_speed10.setSelected(false);
        tv_speed10_portrait.setSelected(false);
        tv_speed12.setSelected(false);
        tv_speed12_portrait.setSelected(false);
        tv_speed15.setSelected(false);
        tv_speed15_portrait.setSelected(false);
        tv_speed20.setSelected(false);
        tv_speed20_portrait.setSelected(false);
        switch (speed) {
            case 5:
                tv_speed05.setSelected(true);
                tv_speed05_portrait.setSelected(true);
                tv_speed.setText("0.5x");
                tv_speed_portrait.setText("0.5x");
                break;
            case 10:
                tv_speed10.setSelected(true);
                tv_speed10_portrait.setSelected(true);
                tv_speed.setText("1x");
                tv_speed_portrait.setText("1x");
                break;
            case 12:
                tv_speed12.setSelected(true);
                tv_speed12_portrait.setSelected(true);
                tv_speed.setText("1.2x");
                tv_speed_portrait.setText("1.2x");
                break;
            case 15:
                tv_speed15.setSelected(true);
                tv_speed15_portrait.setSelected(true);
                tv_speed.setText("1.5x");
                tv_speed_portrait.setText("1.5x");
                break;
            case 20:
                tv_speed20.setSelected(true);
                tv_speed20_portrait.setSelected(true);
                tv_speed.setText("2x");
                tv_speed_portrait.setText("2x");
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
        tv_sc_portrait.setSelected(false);
        tv_hd.setSelected(false);
        tv_hd_portrait.setSelected(false);
        tv_flu.setSelected(false);
        tv_flu_portrait.setSelected(false);
        tv_auto.setSelected(false);
        tv_auto_portrait.setSelected(false);
        switch (bitRate) {
            case 0:
                tv_bit.setText("自动");
                tv_bit_portrait.setText("自动");
                tv_auto.setSelected(true);
                tv_auto_portrait.setSelected(true);
                break;
            case 1:
                tv_bit.setText("流畅");
                tv_bit_portrait.setText("流畅");
                tv_flu.setSelected(true);
                tv_flu_portrait.setSelected(true);
                break;
            case 2:
                tv_bit.setText("高清");
                tv_bit_portrait.setText("高清");
                tv_hd.setSelected(true);
                tv_hd_portrait.setSelected(true);
                break;
            case 3:
                tv_bit.setText("超清");
                tv_bit_portrait.setText("超清");
                tv_sc.setSelected(true);
                tv_sc_portrait.setSelected(true);
                break;
        }
    }

    //初始化选择码率控件的可见性
    private void initBitRateViewVisible(int currentBitRate) {
        tv_sc.setVisibility(View.GONE);
        tv_sc_portrait.setVisibility(View.GONE);
        tv_hd.setVisibility(View.GONE);
        tv_hd_portrait.setVisibility(View.GONE);
        tv_flu.setVisibility(View.GONE);
        tv_flu_portrait.setVisibility(View.GONE);
        tv_auto.setVisibility(View.GONE);
        tv_auto_portrait.setVisibility(View.GONE);
        if (videoVO != null) {
            switch (videoVO.getDfNum()) {
                case 1:
                    tv_flu.setVisibility(View.VISIBLE);
                    tv_flu_portrait.setVisibility(View.VISIBLE);
                    tv_auto.setVisibility(View.VISIBLE);
                    tv_auto_portrait.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    tv_hd.setVisibility(View.VISIBLE);
                    tv_hd_portrait.setVisibility(View.VISIBLE);
                    tv_flu.setVisibility(View.VISIBLE);
                    tv_flu_portrait.setVisibility(View.VISIBLE);
                    tv_auto.setVisibility(View.VISIBLE);
                    tv_auto_portrait.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    tv_sc.setVisibility(View.VISIBLE);
                    tv_sc_portrait.setVisibility(View.VISIBLE);
                    tv_hd.setVisibility(View.VISIBLE);
                    tv_hd_portrait.setVisibility(View.VISIBLE);
                    tv_flu.setVisibility(View.VISIBLE);
                    tv_flu_portrait.setVisibility(View.VISIBLE);
                    tv_auto.setVisibility(View.VISIBLE);
                    tv_auto_portrait.setVisibility(View.VISIBLE);
                    break;
            }
        } else {
            switch (currentBitRate) {
                case 0:
                    tv_auto.setVisibility(View.VISIBLE);
                    tv_auto_portrait.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    tv_flu.setVisibility(View.VISIBLE);
                    tv_flu_portrait.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    tv_hd.setVisibility(View.VISIBLE);
                    tv_hd_portrait.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    tv_sc.setVisibility(View.VISIBLE);
                    tv_sc_portrait.setVisibility(View.VISIBLE);
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
            resetRouteLayout(View.GONE, false);
            resetCodecLayout(View.GONE, false);
        } else if (isShowTopBottomLayout) {
            resetTopBottomLayout(View.VISIBLE);
            requestFocus();
            resetHideTime(longTime);
        }
        rl_center_bit.setVisibility(isVisible);
    }

    private boolean checkNetworkType(int bitRate, int fileType, OnClickListener onClickListener) {
        if (networkDetection.isMobileType() && !networkDetection.isAllowMobile()) {
            if (PolyvDownloader.FILE_VIDEO == fileType) {
                if (bitRate != 0 && !PolyvVideoUtil.validateLocalVideo(videoView.getCurrentVid(), bitRate).hasLocalVideo() ||
                        (bitRate == 0 && !PolyvVideoUtil.validateLocalVideo(videoView.getCurrentVid()).hasLocalVideo())) {
                    flowButton.setOnClickListener(onClickListener);
                    flowPlayLayout.setVisibility(View.VISIBLE);
                    hide();
                    cancelFlowButton.setVisibility(View.VISIBLE);
                    return true;
                }
            } else {
                if (bitRate != 0 && PolyvVideoUtil.validateMP3Audio(videoView.getCurrentVid(), bitRate) == null && !PolyvVideoUtil.validateLocalVideo(videoView.getCurrentVid(), bitRate).hasLocalVideo() ||
                        (bitRate == 0 && PolyvVideoUtil.validateMP3Audio(videoView.getCurrentVid()).size() == 0 && !PolyvVideoUtil.validateLocalVideo(videoView.getCurrentVid()).hasLocalVideo())) {
                    flowButton.setOnClickListener(onClickListener);
                    flowPlayLayout.setVisibility(View.VISIBLE);
                    hide();
                    cancelFlowButton.setVisibility(View.VISIBLE);
                    return true;
                }
            }
        }
        return false;
    }

    //重置选择码率的控件
    private void resetBitRateView(final int bitRate) {
        if (checkNetworkType(bitRate, fileType, new OnClickListener() {
            @Override
            public void onClick(View v) {
                networkDetection.allowMobile();
                flowPlayLayout.setVisibility(View.GONE);
                resetBitRateView(bitRate);
            }
        })) {
            videoView.pause(true);
            return;
        }

        boolean isChangeSuccess = false;
        if (videoView != null)
            isChangeSuccess = videoView.changeBitRate(bitRate);
        if (isChangeSuccess) {
            initBitRateView(bitRate);
            hide();
        }
    }

    private void changeRoute(int route) {
        boolean isChangeSuccess = false;
        if (videoView != null)
            isChangeSuccess = videoView.changeRoute(route);
        if (isChangeSuccess) {
            initSelectedRouteView(route);
            hide();
        }
    }

    private void resetRouteLayout(int isVisible) {
        resetRouteLayout(isVisible, true);
    }

    private void resetRouteLayout(int isVisible, boolean isShowTopBottomLayout) {
        if (isVisible == View.VISIBLE) {
            show(-1);
            resetTopBottomLayout(View.GONE, true);
            resetSpeedLayout(View.GONE, false);
            resetBitRateLayout(View.GONE, false);
            resetCodecLayout(View.GONE, false);
        } else if (isShowTopBottomLayout) {
            resetTopBottomLayout(View.VISIBLE);
            requestFocus();
            resetHideTime(longTime);
        }
        rl_center_route.setVisibility(isVisible);
    }

    private void initRouteView() {
        if (videoView.getRouteCount() > 1) {
            tv_route.setVisibility(View.VISIBLE);
            tv_route_portrait.setVisibility(View.VISIBLE);

            tv_route1.setVisibility(View.VISIBLE);
            tv_route1_portrait.setVisibility(View.VISIBLE);
            tv_route2.setVisibility(View.VISIBLE);
            tv_route2_portrait.setVisibility(View.VISIBLE);
            if (videoView.getRouteCount() > 2) {
                tv_route3.setVisibility(View.VISIBLE);
                tv_route3_portrait.setVisibility(View.VISIBLE);
            } else {
                tv_route3.setVisibility(View.GONE);
                tv_route3_portrait.setVisibility(View.GONE);
            }

            initSelectedRouteView(videoView.getCurrentRoute());
        } else {
            tv_route.setVisibility(View.GONE);
            tv_route_portrait.setVisibility(View.GONE);
        }
    }

    private void initSelectedRouteView(int currentRoute) {
        tv_route1_portrait.setSelected(false);
        tv_route1.setSelected(false);
        tv_route2_portrait.setSelected(false);
        tv_route2.setSelected(false);
        tv_route3_portrait.setSelected(false);
        tv_route3.setSelected(false);

        if (currentRoute == 1) {
            tv_route1_portrait.setSelected(true);
            tv_route1.setSelected(true);
        } else if (currentRoute == 2) {
            tv_route2_portrait.setSelected(true);
            tv_route2.setSelected(true);
        } else {
            tv_route3_portrait.setSelected(true);
            tv_route3.setSelected(true);
        }
    }

    private void resetCodecLayout(int isVisible) {
        resetCodecLayout(isVisible, true);
    }

    private void resetCodecLayout(int isVisible, boolean isShowTopBottomLayout) {
        if (isVisible == View.VISIBLE) {
            show(-1);
            resetTopBottomLayout(View.GONE, true);
            resetSpeedLayout(View.GONE, false);
            resetBitRateLayout(View.GONE, false);
            resetRouteLayout(View.GONE, false);
        } else if (isShowTopBottomLayout) {
            resetTopBottomLayout(View.VISIBLE);
            requestFocus();
            resetHideTime(longTime);
        }
        controllerCodecRl.setVisibility(isVisible);
    }

    //重置显示/隐藏弹幕的控件
    private void resetDmSwitchView() {
        if (iv_dmswitch.isSelected()) {
            iv_dmswitch.setSelected(false);
            danmuFragment.show();
            iv_danmu.setVisibility(View.VISIBLE);
        } else {
            iv_dmswitch.setSelected(true);
            danmuFragment.hide();
            iv_danmu.setVisibility(View.GONE);
        }
    }

    //本地截图
    private void localScreenshot() {
        if (videoView != null) {
            //该方法可能会稍微耗时，可以使用screenshot(width, height)较小的宽高来避免，也可以截图完成后弹出截取的图片，减少卡顿的感知
            Bitmap bitmap = null;
            if (viceLayout != null && viceLayout.isPPTInMinScreen() && viceLayout.getPPTView() != null) {
                bitmap = viceLayout.getPPTView().getImg();
            } else {
                bitmap = videoView.screenshot();
            }
            if (bitmap != null) {
                String fileName = videoView.getCurrentVid() + "_" + PolyvTimeUtils.generateTime(videoView.getCurrentPosition()) + "_" + new SimpleDateFormat("yyyy-MM-dd_kk:mm:ss").format(new Date()) + ".jpg";
                File saveFile;
                if(Build.VERSION.SDK_INT < 29) {
                    String savePath = SDCardUtil.createPathPF(mContext, "polyvsnapshot");
                    saveFile = new File(savePath, fileName);
                } else {
                    //保存到私有目录，再复制到Pictures下
                    saveFile = new File(mContext.getExternalCacheDir(), fileName);
                }
                FileOutputStream fileOutputStream = null;
                try {
                    fileOutputStream = new FileOutputStream(saveFile);
                    boolean compressResult = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                    if (compressResult) {
                        if(Build.VERSION.SDK_INT >= 29){
                            Uri uri = PolyvScopedStorageUtil.createUriInMediaStore(null, saveFile.getName(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            boolean b = PolyvScopedStorageUtil.saveFileToMediaStore(saveFile, uri);
                            saveFile.delete();
                            if(!b){
                                toastMsg("截图失败：bitmap save fail");
                                return;
                            }
                        }
                        toastMsg("截图成功：" + saveFile.getAbsolutePath());
                    } else {
                        toastMsg("截图失败：bitmap compress fail");
                    }
                } catch (Exception e) {
                    toastMsg("截图失败：" + e.getMessage());
                } finally {
                    IOUtil.closeIO(fileOutputStream);
                }
            } else {
                toastMsg("截图失败：bitmap is null");
            }
        } else {
            toastMsg("截图失败：videoView is null");
        }
    }

    //网络截图
    @Deprecated
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

    private void resetLeftSideView(int visibility) {
        ll_left_side.setVisibility(visibility);
        ll_left_side_land.setVisibility(visibility);
        ll_left_side_t.setVisibility(visibility);
        ll_left_side_t_land.setVisibility(visibility);
    }

    private void resetModeView(boolean isVideo) {
        iv_video.setSelected(isVideo);
        iv_video_land.setSelected(isVideo);
        iv_audio.setSelected(!isVideo);
        iv_audio_land.setSelected(!isVideo);
        tv_video.setSelected(isVideo);
        tv_video_land.setSelected(isVideo);
        tv_audio.setSelected(!isVideo);
        tv_audio_land.setSelected(!isVideo);
    }

    private void hidePortraitPopupView() {
        rl_center_bit_portrait.setVisibility(View.GONE);
        rl_center_speed_portrait.setVisibility(View.GONE);
        rl_center_route_portrait.setVisibility(View.GONE);
        controllerCodecPortraitRl.setVisibility(View.GONE);
    }

    public boolean isLocked() {
        return isFullScreen &&
                (polyvScreenLock.isSelected() || polyvScreenLockAudio.isSelected());
    }

    private void changeVideoMode() {
        //如果当前已经是优先视频模式，则不再切换
        if (videoView != null && !PolyvVideoVO.MODE_VIDEO.equals(videoView.getPriorityMode())) {
            if (checkNetworkType(videoView.getBitRate(), PolyvDownloader.FILE_VIDEO, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    networkDetection.allowMobile();
                    flowPlayLayout.setVisibility(View.GONE);
                    changeVideoMode();
                }
            })) {
                videoView.pause(true);
                return;
            }

            resetModeView(true);
            showAudioLock(true);
            videoView.changeMode(PolyvVideoVO.MODE_VIDEO);
            if (coverView != null)
                coverView.hide();
        }
    }

    private void changeAudioMode() {
        //如果当前已经是优先音频模式，则不再切换
        if (videoView != null && !PolyvVideoVO.MODE_AUDIO.equals(videoView.getPriorityMode())) {
            if (checkNetworkType(videoView.getBitRate(), PolyvDownloader.FILE_AUDIO, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    networkDetection.allowMobile();
                    flowPlayLayout.setVisibility(View.GONE);
                    changeAudioMode();
                }
            })) {
                videoView.pause(true);
                return;
            }

            resetModeView(false);
            showAudioLock(true);
            videoView.changeMode(PolyvVideoVO.MODE_AUDIO);
        }
    }

    private void changeCodecType(boolean isMediaCodec) {
        videoSetting.setUsingMediaCodec(isMediaCodec);
        videoView.changeMode(videoView.getPriorityMode());
        updateCodecName();
    }

    private void updateCodecName() {
        final boolean isMediaCodec = videoSetting.getUsingMediaCodec();
        controllerMediaCodecPortraitTv.setSelected(isMediaCodec);
        controllerAvCodecPortraitTv.setSelected(!isMediaCodec);
        controllerMediaCodecTv.setSelected(isMediaCodec);
        controllerAvCodecTv.setSelected(!isMediaCodec);
        tvCodecPortrait.setText(isMediaCodec ? "硬解" : "软解");
        tvCodecLand.setText(isMediaCodec ? "硬解" : "软解");
    }

    public void updatePictureInPictureActions(@DrawableRes int iconId, String title, int controlType, int requestCode) {
        if (Build.VERSION.SDK_INT < 26)
            return;
        final ArrayList<RemoteAction> actions = new ArrayList<>();

        // This is the PendingIntent that is invoked when a user clicks on the action item.
        // You need to use distinct request codes for play and pause, or the PendingIntent won't
        // be properly updated.
        final PendingIntent intent =
                PendingIntent.getBroadcast(
                        videoActivity,
                        requestCode,
                        new Intent("media_control").putExtra("control_type", controlType),
                        0);
        final Icon icon = Icon.createWithResource(videoActivity, iconId);
        actions.add(new RemoteAction(icon, title, title, intent));

        pipBuilder.setActions(actions);

        // This is how you can update action items (or aspect ratio) for Picture-in-Picture mode.
        // Note this call can happen even when the app is not in PiP mode. In that case, the
        // arguments will be used for at the next call of #enterPictureInPictureMode.
        try {
            if (!videoActivity.isDestroyed()) {
                videoActivity.setPictureInPictureParams(pipBuilder.build());
            }
        } catch (Exception e) {
        }
    }

    public boolean isViceHideInPipMode() {
        return isViceHideInPipMode;
    }

    public void setOnDragSeekListener(OnDragSeekListener listener) {
        this.onDragSeekListener = listener;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.polyv_screen_lock:
            case R.id.polyv_screen_lock_audio:
                view.setSelected(!view.isSelected());
                show();
                break;
            case R.id.iv_land:
                changeToFullScreen();
                break;
            case R.id.iv_port:
                changeToSmallScreen();
                break;
            case R.id.iv_play:
                playOrPause();
                break;
            case R.id.iv_play_land:
                playOrPause();
                break;
            case R.id.iv_finish:
                changeToSmallScreen();
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
                PolyvShareUtils.shareQQFriend(mContext, "", PolyvPlayerTopFragment.SHARE_TEXT, PolyvShareUtils.TEXT, null);
                hide();
                break;
            case R.id.iv_sharewechat:
                PolyvShareUtils.shareWeChatFriend(mContext, "", PolyvPlayerTopFragment.SHARE_TEXT, PolyvShareUtils.TEXT, null);
                hide();
                break;
            case R.id.iv_shareweibo:
                PolyvShareUtils.shareWeiBo(mContext, "", PolyvPlayerTopFragment.SHARE_TEXT, PolyvShareUtils.TEXT, null);
                hide();
                break;
            case R.id.iv_dmswitch:
                resetDmSwitchView();
                break;
            case R.id.tv_srtnone:
                selectSrt(-1);
                tv_srtnone.setSelected(true);
                videoView.changeSRT("不显示");
                break;
            case R.id.srt_change_mode_tv:
                selectSrt(-1);
                srt_change_mode_tv.setSelected(true);
                changeSrtMode(false);
                break;
            case R.id.tv_speed_portrait:
                boolean isVisible = rl_center_speed_portrait.getVisibility() == View.VISIBLE;
                hidePortraitPopupView();
                if (!isVisible) {
                    rl_center_speed_portrait.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tv_speed:
                if (rl_center_speed.getVisibility() == View.GONE)
                    resetSpeedLayout(View.VISIBLE);
                else
                    resetSpeedLayout(View.GONE);
                break;
            case R.id.tv_bit_portrait:
                boolean isVisibleBit = rl_center_bit_portrait.getVisibility() == View.VISIBLE;
                hidePortraitPopupView();
                if (!isVisibleBit) {
                    rl_center_bit_portrait.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tv_bit:
                if (rl_center_bit.getVisibility() == View.GONE)
                    resetBitRateLayout(View.VISIBLE);
                else
                    resetBitRateLayout(View.GONE);
                break;
            case R.id.tv_sc_portrait:
            case R.id.tv_sc:
                resetBitRateView(3);
                break;
            case R.id.tv_hd_portrait:
            case R.id.tv_hd:
                resetBitRateView(2);
                break;
            case R.id.tv_flu_portrait:
            case R.id.tv_flu:
                resetBitRateView(1);
                break;
            case R.id.tv_auto_portrait:
            case R.id.tv_auto:
                resetBitRateView(0);
                break;
            case R.id.tv_speed05_portrait:
            case R.id.tv_speed05:
                resetSpeedView(5);
                break;
            case R.id.tv_speed10_portrait:
            case R.id.tv_speed10:
                resetSpeedView(10);
                break;
            case R.id.tv_speed12_portrait:
            case R.id.tv_speed12:
                resetSpeedView(12);
                break;
            case R.id.tv_speed15_portrait:
            case R.id.tv_speed15:
                resetSpeedView(15);
                break;
            case R.id.tv_speed20_portrait:
            case R.id.tv_speed20:
                resetSpeedView(20);
                break;
            case R.id.iv_close_bit:
                hide();
                break;
            case R.id.tv_route_portrait:
                boolean isVisibleRoute = rl_center_route_portrait.getVisibility() == View.VISIBLE;
                hidePortraitPopupView();
                if (!isVisibleRoute) {
                    rl_center_route_portrait.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tv_route:
                if (rl_center_route.getVisibility() == View.GONE)
                    resetRouteLayout(View.VISIBLE);
                else
                    resetRouteLayout(View.GONE);
                break;
            case R.id.tv_route1_portrait:
            case R.id.tv_route1:
                changeRoute(1);
                break;
            case R.id.tv_route2_portrait:
            case R.id.tv_route2:
                changeRoute(2);
                break;
            case R.id.tv_route3_portrait:
            case R.id.tv_route3:
                changeRoute(3);
                break;
            case R.id.iv_close_route:
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
                localScreenshot();
                break;
            case R.id.iv_video:
            case R.id.iv_video_land:
                changeVideoMode();
                break;
            case R.id.iv_audio:
            case R.id.iv_audio_land:
                changeAudioMode();
                break;
            case R.id.iv_vice_status_portrait:
            case R.id.iv_vice_status:
                if (viceLayout != null) {
                    if (viceLayout.getVisibility() == View.VISIBLE) {
                        viceLayout.fromUserHide();
                    } else {
                        viceLayout.fromUserShow();
                    }
                    iv_vice_status_portrait.setSelected(viceLayout.getVisibility() != View.VISIBLE);
                    iv_vice_status.setSelected(viceLayout.getVisibility() != View.VISIBLE);
                }
                break;
            case R.id.tv_ppt_dir:
                if (landPptDirLayout != null) {
                    landPptDirLayout.showLandLayout();
                    hide();
                }
                break;
            case R.id.iv_pip_portrait:
            case R.id.iv_pip:
                if (Build.VERSION.SDK_INT < 26)
                    return;
                boolean result = videoActivity.enterPictureInPictureMode(pipBuilder.build());
                if (!result) {
                    Toast.makeText(videoActivity, "请允许画中画权限后重试！", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent("android.settings.PICTURE_IN_PICTURE_SETTINGS");
                    Uri uri = Uri.fromParts("package", videoActivity.getPackageName(), null);
                    intent.setData(uri);
                    videoActivity.startActivityForResult(intent, 3);
                } else {
                    if (viceLayout != null) {
                        if (viceLayout.isPPTInMinScreen()) {
                            viceLayout.switchLocation(false);
                        }
                        isViceHideInPipMode = viceLayout.isVisibible();
                        viceLayout.hide();
                    }
                }
                break;
            case R.id.tv_knowledge:
                knowledgeLayout.show(!knowledgeLayout.isShowing());
                show(-1);
                break;
            case R.id.tv_codec_portrait:
                final boolean showCodecLayout = controllerCodecPortraitRl.getVisibility() != View.VISIBLE;
                hidePortraitPopupView();
                controllerCodecPortraitRl.setVisibility(showCodecLayout ? View.VISIBLE : View.GONE);
                break;
            case R.id.tv_codec_land:
                resetCodecLayout(controllerCodecRl.getVisibility() != View.VISIBLE ? View.VISIBLE : View.GONE);
                break;
            case R.id.plv_controller_av_codec_portrait_tv:
            case R.id.plv_controller_av_codec_tv:
                changeCodecType(false);
                hide();
                break;
            case R.id.plv_controller_media_codec_portrait_tv:
            case R.id.plv_controller_media_codec_tv:
                changeCodecType(true);
                hide();
                break;
            case R.id.plv_controller_codec_close_iv:
                hide();
                break;
            default:
        }
        //如果控制栏不是处于一直显示的状态，那么重置控制栏隐藏的时间
        if (!status_showalways)
            resetHideTime(longTime);
    }

    private void insertTextView(final String str) {
        final TextView textView = new TextView(mContext);
        textView.setText(str);
        textView.setClickable(true);
        textView.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);

        float size = getResources().getDimension(R.dimen.center_text_size);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        textView.setTextColor(getResources().getColorStateList(R.color.polyv_bit_text_color));
        int padding = PolyvScreenUtils.dip2px(mContext, getResources().getDimension(R.dimen.talk_common_margin));
        textView.setPadding(padding, padding, padding, padding);
        textView.setGravity(Gravity.CENTER);

        final int childCount = lg_subtitle_b.getChildCount();
        GridLayout.Spec rowSpec = GridLayout.spec((childCount) / 5, 1.0f);
        GridLayout.Spec columnSpec = GridLayout.spec((childCount) % 5, 1.0f);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, columnSpec);
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lg_subtitle_b.addView(textView, params);

        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                selectSrt(childCount);
                changeSrtMode(true);
                videoView.changeSRT(str);
            }
        });
    }

    private void selectSrt(int index) {
        int count = lg_subtitle_b.getChildCount();
        if (index == -1) {
            for (int i = 0; i < count; i++) {
                View textView = lg_subtitle_b.getChildAt(i);
                textView.setSelected(false);
            }
            return;
        }
        if (index >= count) {
            return;
        }
        for (int i = 0; i < count; i++) {
            View textView = lg_subtitle_b.getChildAt(i);
            if (i == index) {
                textView.setSelected(true);
                continue;
            }
            textView.setSelected(false);
        }
    }

    public interface OnDragSeekListener {
        void onDragSeekSuccess(int positionBeforeSeek, int positionAfterSeek);

        void onDragSeekBan(int dragSeekStrategy);
    }
}
