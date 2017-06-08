package com.easefun.polyvsdk.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.sub.danmaku.auxiliary.BilibiliDanmakuTransfer;
import com.easefun.polyvsdk.sub.danmaku.auxiliary.PolyvDanmakuTransfer;
import com.easefun.polyvsdk.sub.danmaku.entity.PolyvDanmakuEntity;
import com.easefun.polyvsdk.sub.danmaku.entity.PolyvDanmakuInfo;
import com.easefun.polyvsdk.sub.danmaku.main.PolyvDanmakuManager;
import com.easefun.polyvsdk.video.PolyvVideoView;

import java.util.HashMap;

import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;

public class PolyvPlayerDanmuFragment extends Fragment {
    private static final String TAG = PolyvPlayerDanmuFragment.class.getSimpleName();
    private static final int SEEKTOFITTIME = 12;
    private static final int PAUSE = 13;
    private boolean status_canauto_resume = true;
    private boolean status_pause_fromuser = true;
    private boolean status_pause;
    //danmuLayoutView
    private View view;
    private IDanmakuView iDanmakuView;
    private DanmakuContext mContext;
    private DrawHandler.Callback callback;
    private PolyvDanmakuManager danmakuManager;
    private PolyvDanmakuManager.GetDanmakuListener getDanmakuListener;
    private PolyvDanmakuManager.SendDanmakuListener sendDanmakuListener;
    private PolyvVideoView videoView;
    private String vid;
    private int limit;
    private boolean isPrepare;
    private boolean isStart;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SEEKTOFITTIME:
                    seekToFitTime();
                    break;
                case PAUSE:
                    if (status_pause)
                        pause();
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null)
            view = inflater.inflate(R.layout.polyv_fragment_player_danmu, container, false);
        return view;
    }

    public void setVid(String vid, PolyvVideoView videoView) {
        setVid(vid, -1, videoView);
    }

    public void setVid(String vid, int limit, PolyvVideoView videoView) {
        this.videoView = videoView;
        this.vid = vid;
        this.limit = limit;
        prepareDanmaku(vid, limit);
    }

    private void findIdAndNew() {
        iDanmakuView = (IDanmakuView) view.findViewById(R.id.dv_danmaku);
    }

    private void initView() {
        danmakuManager = new PolyvDanmakuManager(getContext());

        //-------------------仅对加载的弹幕有效-------------------//
        // 设置最大显示行数
        HashMap<Integer, Integer> maxLinesPair = new HashMap<Integer, Integer>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 5); // 滚动弹幕最大显示5行
        maxLinesPair.put(BaseDanmaku.TYPE_FIX_TOP, 2);
        maxLinesPair.put(BaseDanmaku.TYPE_FIX_BOTTOM, 2);
        // 设置是否禁止重叠
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<Integer, Boolean>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_BOTTOM, true);
        //--------------------------------------------------------//

        mContext = DanmakuContext.create();
        mContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 2).setDuplicateMergingEnabled(false)
                .setScrollSpeedFactor(1.6f).setScaleTextSize(1.3f)
                // .setCacheStuffer(new SpannedCacheStuffer(),
                // mCacheStufferAdapter) // 图文混排使用SpannedCacheStuffer
                //.setCacheStuffer(new BackgroundCacheStuffer(), mCacheStufferAdapter) // 绘制背景使用BackgroundCacheStuffer
                .setMaximumLines(maxLinesPair).preventOverlapping(overlappingEnablePair);
        iDanmakuView.showFPS(false);
        iDanmakuView.enableDanmakuDrawingCache(false);

        getDanmakuListener = new PolyvDanmakuManager.GetDanmakuListener() {

            @Override
            public void fail(Throwable throwable) {
                toastMsg(throwable.getMessage());
            }

            @Override
            public void success(BaseDanmakuParser baseDanmakuParser, PolyvDanmakuEntity entity) {
                toastMsg("获取弹幕成功，总数" + entity.getAllDanmaku().size());
                if (iDanmakuView != null)
                    iDanmakuView.prepare(baseDanmakuParser, mContext);
            }
        };
        sendDanmakuListener = new PolyvDanmakuManager.SendDanmakuListener() {

            @Override
            public void fail(Throwable throwable) {
                toastMsg(throwable.getMessage());
            }

            @Override
            public void success(String s) {
                toastMsg("发送成功");
            }
        };
        callback = new DrawHandler.Callback() {
            @Override
            public void prepared() {
                if (iDanmakuView != null) {
                    iDanmakuView.start((long) videoView.getCurrentPosition());
                    if (status_pause)
                        handler.sendEmptyMessageDelayed(PAUSE, 30);
                }
            }

            @Override
            public void updateTimer(DanmakuTimer danmakuTimer) {
            }

            @Override
            public void danmakuShown(BaseDanmaku baseDanmaku) {
            }

            @Override
            public void drawingFinished() {
            }
        };
        if (isPrepare)
            prepareDanmaku(vid, limit);
        if (isStart)
            start();
    }

    private void toastMsg(final String msg) {
        if (getContext() != null)
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    //准备弹幕，limit=0时获取全部
    private void prepareDanmaku(String vid, int limit) {
        if (danmakuManager != null) {
            if (iDanmakuView != null)
                iDanmakuView.release();
            danmakuManager.getDanmaku(vid, limit, getDanmakuListener);
        } else {
            isPrepare = true;
        }
    }

    //开始弹幕
    public void start() {
        if (iDanmakuView != null) {
            if (!iDanmakuView.isPrepared()) {
                iDanmakuView.setCallback(callback);
            } else {
                iDanmakuView.start((long) videoView.getCurrentPosition());
                if (status_pause)
                    handler.sendEmptyMessageDelayed(PAUSE, 30);
            }
        } else {
            isStart = true;
        }
    }

    public void hide() {
        if (iDanmakuView != null) {
            iDanmakuView.hide();
        }
    }

    public void show() {
        if (iDanmakuView != null) {
            iDanmakuView.show();
        }
    }

    public void pause() {
        pause(true);
    }

    public void pause(boolean fromuser) {
        if (!fromuser)
            status_pause_fromuser = false;
        else
            status_canauto_resume = false;
        status_pause = true;
        if (iDanmakuView != null && iDanmakuView.isPrepared()) {
            iDanmakuView.pause();
        }
    }

    public void resume() {
        resume(true);
    }

    public void resume(boolean fromuser) {
        if (status_pause_fromuser && fromuser || (!status_pause_fromuser && !fromuser)) {
            status_pause = false;
            if (iDanmakuView != null && iDanmakuView.isPrepared() && iDanmakuView.isPaused()) {
                if (!status_pause_fromuser) {
                    status_pause_fromuser = true;
                    seekTo();
                    if (status_canauto_resume)
                        iDanmakuView.resume();
                } else {
                    status_canauto_resume = true;
                    iDanmakuView.resume();
                }
            }
        }
    }

    private void release() {
        handler.removeMessages(SEEKTOFITTIME);
        handler.removeMessages(PAUSE);
        if (iDanmakuView != null) {
            iDanmakuView.release();
            iDanmakuView = null;
        }
    }

    public void seekTo() {
        if (iDanmakuView != null) {
            seekToFitTime();
        }
    }

    private long seekToTime = -1;
    private long updateTime = -1;

    //获取视频稳定后的时间再seekTo
    private void seekToFitTime() {
        if (videoView != null) {
            if (seekToTime == -1)
                seekToTime = videoView.getCurrentPosition();
            long currentTime = videoView.getCurrentPosition();
            if (currentTime < seekToTime || (updateTime != -1 && currentTime > updateTime)) {
                if (iDanmakuView != null) {
                    iDanmakuView.seekTo(currentTime);
                    if (status_pause)
                        handler.sendEmptyMessageDelayed(PAUSE, 30);
                }
                seekToTime = -1;
                updateTime = -1;
                return;
            } else if (currentTime >= seekToTime) {
                updateTime = currentTime;
            }
            handler.removeMessages(SEEKTOFITTIME);
            handler.sendMessageDelayed(handler.obtainMessage(SEEKTOFITTIME), 300);
        }
    }

    /**
     * 发送弹幕
     *
     * @param videoView
     * @param msg
     * @param fontmode  取值有"roll","top","bottom"
     * @param fontsize  取值有16,18,24
     * @param color
     */
    public void send(PolyvVideoView videoView, String msg, @PolyvDanmakuInfo.FontMode String fontmode, @PolyvDanmakuInfo.FontSize String fontsize, @ColorInt int color) {
        if (msg.trim().length() == 0) {
            toastMsg("发送信息不能为空！");
            return;
        }
        // 显示弹幕
        addDanmaku(msg, fontmode, fontsize, color);
        String time = "00:00:00";
        if (videoView != null)
            time = PolyvDanmakuTransfer.toPolyvDanmakuTime(videoView.getCurrentPosition());
        danmakuManager.sendDanmaku(new PolyvDanmakuInfo(vid, msg, time, fontsize, fontmode, color), sendDanmakuListener);
    }

    // 显示弹幕
    private void addDanmaku(CharSequence msg, String fontmode, String fontsize, int color) {
        BaseDanmaku danmaku = mContext.mDanmakuFactory.createDanmaku(BilibiliDanmakuTransfer.toBilibiliFontMode(fontmode));
        if (danmaku == null || iDanmakuView == null) {
            return;
        }
        danmaku.text = msg;
        danmaku.padding = 5;
        danmaku.priority = 1; // 0:可能会被各种过滤器过滤并隐藏显示
        danmaku.isLive = false;
        danmaku.setTime(iDanmakuView.getCurrentTime() + 100);
        danmaku.textSize = Integer.parseInt(fontsize) * (mContext.getDisplayer().getDensity() - 0.6f);
        danmaku.textColor = color;
        if (color != Color.BLACK)
            danmaku.textShadowColor = Color.BLACK; // 重要：如果有图文混排，最好不要设置描边(设textShadowColor=0)，否则会进行两次复杂的绘制导致运行效率降低
        else
            danmaku.textShadowColor = Color.WHITE;
        danmaku.underlineColor = Color.GREEN;
//        danmaku.borderColor = Color.BLUE;
        iDanmakuView.addDanmaku(danmaku);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findIdAndNew();
        initView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
    }
}
