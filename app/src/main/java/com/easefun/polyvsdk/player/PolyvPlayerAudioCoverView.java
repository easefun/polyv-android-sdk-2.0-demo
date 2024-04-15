package com.easefun.polyvsdk.player;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.util.PolyvDownloadDirUtil;
import com.easefun.polyvsdk.util.PolyvImageLoader;
import com.easefun.polyvsdk.util.PolyvScreenUtils;
import com.easefun.polyvsdk.video.PolyvVideoView;
import com.easefun.polyvsdk.vo.PolyvVideoVO;

import java.io.File;

public class PolyvPlayerAudioCoverView extends FrameLayout {
    /**
     * 播放音频时的封面图
     */
    private ImageView iv_audio_cover = null;
    private ImageView iv_audio_cover_m = null;
    private FrameLayout fl_cover = null;
    private ObjectAnimator animator;
    private float currentValue;
    private String currentMode;

    private boolean isShowFilm;

    public PolyvPlayerAudioCoverView(@NonNull Context context) {
        this(context, null);
    }

    public PolyvPlayerAudioCoverView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PolyvPlayerAudioCoverView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PolyvPlayerAudioCoverView);
        isShowFilm = typedArray.getBoolean(R.styleable.PolyvPlayerAudioCoverView_show_film, true);
        typedArray.recycle();
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.polyv_player_audio_cover, this);
        iv_audio_cover = (ImageView) findViewById(R.id.iv_audio_cover);
        iv_audio_cover_m = (ImageView) findViewById(R.id.iv_audio_cover_m);
        fl_cover = (FrameLayout) findViewById(R.id.fl_cover);
        if (!isShowFilm) {
            iv_audio_cover_m.setScaleType(ImageView.ScaleType.FIT_CENTER);
            fl_cover.setVisibility(View.GONE);
        } else {
            iv_audio_cover_m.setAlpha(0.4f);
        }
    }

    private void showCover(PolyvVideoView videoView, ImageView imageView, boolean isRotateView) {
        imageView.setImageBitmap(null);
        imageView.setVisibility(View.VISIBLE);
        PolyvVideoVO videoVO = videoView.getVideo();
        if (videoVO != null)
            if (!videoView.isLocalPlay()) {
                PolyvImageLoader.getInstance().loadImageOrigin(getContext(),videoVO.getFirstImage(),imageView,
                        isRotateView ? R.drawable.polyv_rotate_cover_default : R.drawable.polyv_bg_cover_default);
            } else {
                int index = 0;
                if (videoVO.getFirstImage().contains("/")) {
                    index = videoVO.getFirstImage().lastIndexOf("/");
                }
                String fileName = videoVO.getFirstImage().substring(index);
                File file = PolyvDownloadDirUtil.getFileFromExtraResourceDir(videoVO.getVid(), fileName);
                if (file != null)
                    imageView.setImageURI(Uri.parse(file.getAbsolutePath()));
                else
                    imageView.setImageResource(isRotateView ? R.drawable.polyv_rotate_cover_default : R.drawable.polyv_bg_cover_default);//显示默认的图片
            }
        else
            imageView.setImageResource(isRotateView ? R.drawable.polyv_rotate_cover_default : R.drawable.polyv_bg_cover_default);//显示默认的图片
    }

    //用于音视频切换
    public void changeModeFitCover(PolyvVideoView videoView, String changedMode) {
        this.currentMode = changedMode;
        if (PolyvVideoVO.MODE_AUDIO.equals(changedMode)) {
            currentValue = 0;
            startAnimation();
            fl_cover.setVisibility(View.VISIBLE);
            showCover(videoView, iv_audio_cover_m, false);
            showCover(videoView, iv_audio_cover, true);
        } else {
            hide();
        }
    }

    public void hide() {
        if (animator != null)
            animator.cancel();
        fl_cover.setVisibility(View.GONE);
        iv_audio_cover.setVisibility(View.GONE);
        iv_audio_cover_m.setVisibility(View.GONE);
    }

    public void fitLocationChange(boolean isInMainScreen) {
        if (fl_cover == null)
            return;
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) fl_cover.getLayoutParams();
        if (isInMainScreen) {
            rlp.width = PolyvScreenUtils.dip2px(getContext(), 150);
            rlp.height = PolyvScreenUtils.dip2px(getContext(),150);
        } else {
            rlp.width = PolyvScreenUtils.dip2px(getContext(), 70);
            rlp.height = PolyvScreenUtils.dip2px(getContext(), 70);
        }
        fl_cover.setLayoutParams(rlp);
    }

    //用于mp3源文件播放
    public void onlyShowCover(PolyvVideoView videoView) {
        showCover(videoView, iv_audio_cover_m, false);
    }

    public void stopAnimation() {
        if (PolyvVideoVO.MODE_AUDIO.equals(currentMode)) {
            if (animator != null)
                animator.cancel();
        }
    }

    public void startAnimation() {
        stopAnimation();
        if (PolyvVideoVO.MODE_AUDIO.equals(currentMode)) {
            animator = ObjectAnimator.ofFloat(fl_cover, "rotation", currentValue - 360, currentValue);
            animator.setDuration(15000);
            animator.setRepeatMode(ValueAnimator.RESTART);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setInterpolator(new LinearInterpolator());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    currentValue = (float) animation.getAnimatedValue();
                }
            });
            animator.start();
        }
    }
}
