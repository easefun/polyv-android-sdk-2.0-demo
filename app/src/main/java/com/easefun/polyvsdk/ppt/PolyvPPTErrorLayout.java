package com.easefun.polyvsdk.ppt;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.po.ppt.PolyvPptInfo;
import com.easefun.polyvsdk.util.PolyvScreenUtils;
import com.easefun.polyvsdk.video.PolyvVideoView;

public class PolyvPPTErrorLayout extends FrameLayout {
    private PolyvVideoView currentVideoView;

    private OnPPTRegainSuccessListener onPPTRegainSuccessListener;

    private TextView pptTipsOne, pptTipsTwo, regain_ppt;

    public PolyvPPTErrorLayout(@NonNull Context context) {
        this(context, null);
    }

    public PolyvPPTErrorLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PolyvPPTErrorLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.polyv_ppt_error_layout, this);
        initView();
    }

    private void initView() {
        pptTipsOne = (TextView) findViewById(R.id.ppt_tips_one);
        pptTipsTwo = (TextView) findViewById(R.id.ppt_tips_two);
        regain_ppt = (TextView) findViewById(R.id.regain_ppt);
        if (isLandLayout()) {
            pptTipsOne.setTextColor(Color.WHITE);
            pptTipsTwo.setTextColor(Color.WHITE);
            regain_ppt.setBackgroundResource(R.drawable.polyv_regain_ppt_bg_land);
        }
        regain_ppt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLandLayout()) {
                    setVisibility(View.GONE);
                }
                if (currentVideoView == null) {
                    Toast.makeText(getContext(), "videoView is empty, don't regain ppt.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getContext(), "正在获取课件......", Toast.LENGTH_SHORT).show();
                PolyvPPTRegainTask.getInstance().regainPPT(currentVideoView.getCurrentVid(), getContext(), new PolyvPPTRegainTask.OnPPTRegainLisnter() {
                    @Override
                    public void onProgress(int progress) {
                        if (onPPTRegainSuccessListener != null) {
                            onPPTRegainSuccessListener.onProgress(progress);
                        }
                    }

                    @Override
                    public void onSuccess(String vid, PolyvPptInfo pptvo) {
                        if (vid != null && !vid.equals(currentVideoView.getCurrentVid()))
                            return;
                        Toast.makeText(getContext(), "课件获取成功!", Toast.LENGTH_SHORT).show();
                        if (onPPTRegainSuccessListener != null) {
                            onPPTRegainSuccessListener.onSuccess(vid, pptvo);
                        }
                    }

                    @Override
                    public void onFail(String vid, String tips, int errorReason) {
                        if (vid != null && !vid.equals(currentVideoView.getCurrentVid()))
                            return;
                        if (PolyvScreenUtils.isLandscape(getContext()) && isLandLayout()) {
                            setVisibility(View.VISIBLE);
                        }
                        Toast.makeText(getContext(), "课件获取失败(" + tips + ")#" + errorReason, Toast.LENGTH_SHORT).show();
                        if (onPPTRegainSuccessListener != null) {
                            onPPTRegainSuccessListener.onFail(vid, tips, errorReason);
                        }
                    }
                }, !currentVideoView.isLocalPlay());
            }
        });
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLandLayout()) {
                    setVisibility(View.GONE);
                }
            }
        });
    }

    public void setOnPPTRegainSuccessListener(OnPPTRegainSuccessListener listener) {
        this.onPPTRegainSuccessListener = listener;
    }

    public interface OnPPTRegainSuccessListener {
        void onFail(String vid, String tips, int errorReason);
        void onProgress(int progress);
        void onSuccess(String vid, PolyvPptInfo pptvo);
    }

    private boolean isLandLayout() {
        return getAlpha() != 1;
    }

    public void acceptPPTCallback(PolyvVideoView videoView, String vid, boolean hasPPT, PolyvPptInfo pptvo) {
        if (PolyvScreenUtils.isLandscape(getContext()) && isLandLayout()) {
            if (hasPPT) {
                setVisibility(pptvo == null ? View.VISIBLE : View.GONE);
            }
        }
        this.currentVideoView = videoView;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        PolyvPPTRegainTask.getInstance().shutdown();
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT && isLandLayout()) {
            setVisibility(View.GONE);
        }
    }
}
