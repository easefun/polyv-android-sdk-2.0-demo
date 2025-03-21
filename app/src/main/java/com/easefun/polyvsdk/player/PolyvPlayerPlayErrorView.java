package com.easefun.polyvsdk.player;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.util.PolyvErrorMessageUtils;
import com.easefun.polyvsdk.video.PolyvPlayErrorReason;
import com.easefun.polyvsdk.video.PolyvVideoView;

/**
 * 视频播放错误提示界面
 * @author Lionel 2019-3-20
 */
public class PolyvPlayerPlayErrorView extends FrameLayout {
    // <editor-fold defaultstate="collapsed" desc="成员变量">
    private static final String TAG = PolyvPlayerPlayErrorView.class.getSimpleName();
    private TextView playErrorTv;
    private ImageView playErrorIv;
    private PolyvPlayerLinePopupView linePopupView;

    private IRetryPlayListener retryPlayListener;
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
        LayoutInflater.from(context).inflate(R.layout.polyv_player_play_error_view_new, this);
        findIdAndNew();
        addListener();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="初始化方法">
    private void findIdAndNew() {
        playErrorTv = findViewById(R.id.plv_play_error_tv);
        playErrorIv = findViewById(R.id.plv_play_error_iv);
        linePopupView = new PolyvPlayerLinePopupView(this);
    }

    private void addListener() {
        playErrorIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                hide();
            }
        });
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="功能方法">

    /**
     * 显示界面
     *
     * @param playErrorReason 错误码
     * @param videoView       播放器
     */
    public void show(@PolyvPlayErrorReason.PlayErrorReason int playErrorReason, @NonNull PolyvVideoView videoView, String vid, int bitrate) {
        PolyvErrorMessageUtils.ErrorMessage message = PolyvErrorMessageUtils.getErrorMessage(playErrorReason);
        if (message.changeStatus == PolyvErrorMessageUtils.ErrorMessage.CHANGE_STATUS_NONE) {
            playErrorTv.setText(message.message);
        } else {
            SpannableString spannable = new SpannableString(message.message);
            int start = message.message.length() - 2;
            int end = message.message.length();

            if (start >= 0) {
                spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#4C75F4")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        if (message.changeStatus == PolyvErrorMessageUtils.ErrorMessage.CHANGE_STATUS_RETRY) {
                            hide();
                            if (retryPlayListener != null) {
                                retryPlayListener.onRetry();
                            }
                        } else if (message.changeStatus == PolyvErrorMessageUtils.ErrorMessage.CHANGE_STATUS_LINE){
                            linePopupView.show(videoView, vid, bitrate, true);
                        }
                    }

                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(Color.parseColor("#4C75F4"));  // 强制蓝色
                        ds.setUnderlineText(false); // 去除下划线
                    }
                };
                spannable.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                playErrorTv.setText(spannable);
                playErrorTv.setMovementMethod(LinkMovementMethod.getInstance());
                playErrorTv.setHighlightColor(Color.TRANSPARENT); // 去除点击背景
            } else {
                playErrorTv.setText(message.message);
            }
        }
        setVisibility(View.VISIBLE);
    }

    public void show(String tips, String code, @NonNull PolyvVideoView videoView) {
        playErrorTv.setText(tips);
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
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="监听回调接口定义">

    /**
     * 重试播放监听回调
     *
     * @author Lionel 2019-3-20
     */
    public interface IRetryPlayListener {
        void onRetry();
    }
    // </editor-fold>
}
