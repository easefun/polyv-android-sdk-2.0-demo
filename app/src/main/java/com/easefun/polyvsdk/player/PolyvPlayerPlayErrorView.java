package com.easefun.polyvsdk.player;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.util.PolyvErrorMessageUtils;
import com.easefun.polyvsdk.video.PolyvPlayErrorReason;
import com.easefun.polyvsdk.video.PolyvVideoView;

/**
 * 视频播放错误提示界面
 * @author Lionel 2019-3-20
 */
public class PolyvPlayerPlayErrorView extends LinearLayout {
// <editor-fold defaultstate="collapsed" desc="成员变量">
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

    private IRetryPlayListener retryPlayListener;
    private IShowRouteViewListener showRouteViewListener;
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
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="功能方法">
    /**
     * 显示界面
     * @param playErrorReason 错误码
     * @param videoView 播放器
     */
    public void show(@PolyvPlayErrorReason.PlayErrorReason int playErrorReason, @NonNull PolyvVideoView videoView) {
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
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="设置监听回调">
    public void setRetryPlayListener(IRetryPlayListener retryPlayListener) {
        this.retryPlayListener = retryPlayListener;
    }

    public void setShowRouteViewListener(IShowRouteViewListener showRouteViewListener) {
        this.showRouteViewListener = showRouteViewListener;
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
// </editor-fold>
}
