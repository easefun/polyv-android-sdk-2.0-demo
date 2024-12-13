package com.easefun.polyvsdk.player;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easefun.polyvsdk.PolyvDownloader;
import com.easefun.polyvsdk.PolyvDownloaderErrorReason;
import com.easefun.polyvsdk.PolyvDownloaderManager;
import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.download.constant.PolyvDownloaderVideoStatus;
import com.easefun.polyvsdk.download.listener.IPolyvDownloaderProgressListener2;
import com.easefun.polyvsdk.download.util.PolyvDownloaderUtils;
import com.easefun.polyvsdk.download.vo.PolyvDownloaderVideoVO;
import com.easefun.polyvsdk.util.PolyvErrorMessageUtils;
import com.easefun.polyvsdk.video.PolyvPlayErrorReason;
import com.easefun.polyvsdk.video.PolyvVideoUtil;
import com.easefun.polyvsdk.video.PolyvVideoView;
import com.easefun.polyvsdk.vo.PolyvValidateLocalVideoVO;

import java.util.ArrayList;

/**
 * 视频播放错误提示界面
 * @author Lionel 2019-3-20
 */
public class PolyvPlayerPlayErrorView extends LinearLayout {
    // <editor-fold defaultstate="collapsed" desc="成员变量">
    private static final String TAG = PolyvPlayerPlayErrorView.class.getSimpleName();
    /**
     * 错误提示内容
     */
    private TextView videoErrorContent;
    /**
     * 点击重试按钮
     */
    private TextView videoErrorRetry;
    /**
     * 线路切换按钮
     */
    private TextView videoErrorRoute;
    private TextView videoErrorFixOne;
    private TextView videoErrorFixAll;

    private IRetryPlayListener retryPlayListener;
    private IShowRouteViewListener showRouteViewListener;
    private IFixFileListener fixFileListener;

    private int needFixSize = 0;
    private int successFixSize = 0;
    private int failFixSize = 0;
// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="构造方法">
    public PolyvPlayerPlayErrorView(Context context) {
        this(context, null);
    }

    public PolyvPlayerPlayErrorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PolyvPlayerPlayErrorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.polyv_player_play_error_view, this);
        findIdAndNew();
        addListener();
    }
// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="初始化方法">
    private void findIdAndNew() {
        videoErrorContent = (TextView) findViewById(R.id.video_error_content);
        videoErrorRetry = (TextView) findViewById(R.id.video_error_retry);
        videoErrorRoute = (TextView) findViewById(R.id.video_error_route);
        videoErrorFixOne = (TextView) findViewById(R.id.video_error_fix_one);
        videoErrorFixAll = (TextView) findViewById(R.id.video_error_fix_all);
    }

    private void addListener() {
        videoErrorRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
                if (retryPlayListener != null) {
                    retryPlayListener.onRetry();
                }
            }
        });

        videoErrorRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showRouteViewListener != null) {
                    showRouteViewListener.onShow();
                }
            }
        });

        videoErrorFixOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fixFileListener != null) {
                    fixFileListener.onFixOne();
                }
            }
        });

        videoErrorFixAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fixFileListener != null) {
                    fixFileListener.onFixAll();
                }
            }
        });
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="功能方法">
    /**
     * 显示界面
     * @param playErrorReason 错误码
     * @param videoView 播放器
     */
    public void show(@PolyvPlayErrorReason.PlayErrorReason int playErrorReason, @NonNull PolyvVideoView videoView, String vid, int bitrate) {
        String message = PolyvErrorMessageUtils.getPlayErrorMessage(playErrorReason);
        message += "(error code " + playErrorReason + ")";
        videoErrorContent.setText(message);

        //播放异常才能切换线路
        if (playErrorReason == PolyvPlayErrorReason.VIDEO_ERROR) {
            //判断是否有线路可以切换
            videoErrorRoute.setVisibility(videoView.getRouteCount() > 1 ? View.VISIBLE : View.GONE);
        } else {
            videoErrorRoute.setVisibility(View.GONE);
        }
        videoErrorRetry.setVisibility(VISIBLE);
        PolyvValidateLocalVideoVO localVideoVO = PolyvVideoUtil.validateLocalVideo(vid, bitrate);
        Log.e(TAG, "是否有本地视频：" + localVideoVO.hasLocalVideo());
        if (localVideoVO.hasLocalVideo()) {
            videoErrorFixOne.setVisibility(View.VISIBLE);
            videoErrorFixAll.setVisibility(View.VISIBLE);
        }
        setVisibility(View.VISIBLE);
    }

    public void show(String tips, String code, @NonNull PolyvVideoView videoView){
        videoErrorContent.setText(tips);
        videoErrorRetry.setVisibility(GONE);
        videoErrorRoute.setVisibility(View.GONE);
        setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏界面
     */
    public void hide() {
        setVisibility(View.GONE);
    }

    public void fixOne(String vid, int bitrate) {
        PolyvDownloader downloader = PolyvDownloaderManager.getPolyvDownloader(vid, bitrate);
        downloader.setPolyvDownloadSpeedListener(null);
        downloader.setPolyvDownloadStartListener2(null);
        downloader.setPolyvDownloadWaitingListener(null);
        downloader.setPolyvDownloadProressListener2(new IPolyvDownloaderProgressListener2() {
            @Override
            public void onDownload(long current, long total) {

            }

            @Override
            public void onDownloadSuccess(int bitrate) {
                Toast.makeText(getContext(), "修复成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDownloadFail(@NonNull PolyvDownloaderErrorReason errorReason) {
                Toast.makeText(getContext(), "修复失败", Toast.LENGTH_SHORT).show();
            }
        });
        downloader.startFixKeyFile(getContext());
    }

    public void fixAll() {
        needFixSize = 0;
        successFixSize = 0;
        failFixSize = 0;
        ArrayList<PolyvDownloaderVideoVO> downloadVideoList = PolyvDownloaderUtils.getDownloadVideoList();
        Log.e(TAG, "本地视频列表：" + downloadVideoList);
        for (PolyvDownloaderVideoVO polyvDownloaderVideoVO : downloadVideoList) {
            if (polyvDownloaderVideoVO.getDownloaderVideoStatus() == PolyvDownloaderVideoStatus.VIDEO_CORRECT) {
                needFixSize++;
                PolyvDownloader downloader = PolyvDownloaderManager.getPolyvDownloader(polyvDownloaderVideoVO.getVideoId(), polyvDownloaderVideoVO.getBitrate());
                downloader.setPolyvDownloadSpeedListener(null);
                downloader.setPolyvDownloadStartListener2(null);
                downloader.setPolyvDownloadWaitingListener(null);
                downloader.setPolyvDownloadProressListener2(new IPolyvDownloaderProgressListener2() {
                    @Override
                    public void onDownload(long current, long total) {

                    }

                    @Override
                    public void onDownloadSuccess(int bitrate) {
                        successFixSize++;
                        if ((successFixSize + failFixSize) >= needFixSize) {
                            Toast.makeText(getContext(), "修复成功：" + successFixSize + "/" + needFixSize, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onDownloadFail(@NonNull PolyvDownloaderErrorReason errorReason) {
                        failFixSize++;
                        if (failFixSize >= needFixSize) {
                            Toast.makeText(getContext(), "修复失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                downloader.startFixKeyFile(getContext());
            }
        }
    }
// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="设置监听回调">
    public void setRetryPlayListener(IRetryPlayListener retryPlayListener) {
        this.retryPlayListener = retryPlayListener;
    }

    public void setShowRouteViewListener(IShowRouteViewListener showRouteViewListener) {
        this.showRouteViewListener = showRouteViewListener;
    }

    public void setFixFileListener(IFixFileListener fixFileListener) {
        this.fixFileListener = fixFileListener;
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="监听回调接口定义">
    /**
     * 重试播放监听回调
     * @author Lionel 2019-3-20
     */
    public interface IRetryPlayListener {
        void onRetry();
    }

    /**
     * 显示线路切换试图监听回调
     * @author Lionel 2019-3-20
     */
    public interface IShowRouteViewListener {
        void onShow();
    }

    public interface IFixFileListener {
        void onFixOne();

        void onFixAll();
    }
// </editor-fold>
}
