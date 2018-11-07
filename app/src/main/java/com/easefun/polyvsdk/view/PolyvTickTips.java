package com.easefun.polyvsdk.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.vo.PolyvVideoVO;

public class PolyvTickTips extends FrameLayout {
    private View view;
    private TextView tv_time, tv_context;
    private ImageView iv_seek;
    private Runnable runnable;
    private PolyvTickSeekBar.TickData tickData;

    public PolyvTickTips(@NonNull Context context) {
        this(context, null);
    }

    public PolyvTickTips(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PolyvTickTips(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        view = LayoutInflater.from(context).inflate(R.layout.polyv_tick_tips, this);
        initView();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(runnable);
    }

    private void initView() {
        tv_time = (TextView) view.findViewById(R.id.tv_time);
        tv_context = (TextView) view.findViewById(R.id.tv_context);
        iv_seek = (ImageView) view.findViewById(R.id.iv_seek);
        iv_seek.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSeekClickListener != null)
                    onSeekClickListener.onSeekClick(tickData);
            }
        });
    }

    public void setOnSeekClickListener(OnSeekClickListener onSeekClickListener) {
        this.onSeekClickListener = onSeekClickListener;
    }

    private OnSeekClickListener onSeekClickListener;

    public interface OnSeekClickListener {
        void onSeekClick(PolyvTickSeekBar.TickData tickData);
    }

    public boolean isShow() {
        return getVisibility() == View.VISIBLE;
    }

    public void hide() {
        removeCallbacks(runnable);
        setVisibility(View.INVISIBLE);
    }

    private void handleData(final PolyvTickSeekBar.TickData tickData) {
        if (tickData == null || !(tickData.getTag() instanceof PolyvVideoVO.Videokeyframe))
            return;
        MarginLayoutParams mlp = (MarginLayoutParams) getLayoutParams();
        mlp.leftMargin = 0;
        setLayoutParams(mlp);//先把margin调整为0，避免textview的文本由于上次的margin显示不全
        PolyvVideoVO.Videokeyframe videokeyframe = (PolyvVideoVO.Videokeyframe) tickData.getTag();
        StringBuilder stringBuilder = new StringBuilder();
        if (videokeyframe.getHouts() > 9) {
            stringBuilder.append(videokeyframe.getHouts()).append(":");
        } else if (videokeyframe.getHouts() > 0) {
            stringBuilder.append(0).append(videokeyframe.getHouts()).append(":");
        }
        if (videokeyframe.getMinutes() > 9) {
            stringBuilder.append(videokeyframe.getMinutes()).append(":");
        } else if (videokeyframe.getMinutes() >= 0) {
            stringBuilder.append(0).append(videokeyframe.getMinutes()).append(":");
        }
        if (videokeyframe.getSeconds() > 9) {
            stringBuilder.append(videokeyframe.getSeconds());
        } else if (videokeyframe.getSeconds() >= 0) {
            stringBuilder.append(0).append(videokeyframe.getSeconds());
        }
        tv_time.setText(stringBuilder.toString());
        tv_context.setMaxWidth(((ViewGroup) getParent()).getWidth() * 3 / 4);//调整最大的宽度，不然iv_seek可能会显示不了
        tv_context.setText(videokeyframe.getKeycontext());
        tv_context.post(runnable = new Runnable() {
            @Override
            public void run() {
                //调整位置
                int parentWidth = ((ViewGroup) getParent()).getWidth();
                int width = getWidth();
                float cx = tickData.getCx();
                MarginLayoutParams mlp = (MarginLayoutParams) getLayoutParams();
                int halfWidth = width / 2;
                if (cx > halfWidth) {
                    int leftMargin = (int) (cx - halfWidth);
                    if (leftMargin + width > parentWidth) {
                        if (cx > parentWidth / 2) {
                            leftMargin = parentWidth - width;
                        } else {
                            leftMargin = 0;//中间及左边都为0
                        }
                    }
                    mlp.leftMargin = leftMargin;
                } else {
                    mlp.leftMargin = 0;
                }
                setLayoutParams(mlp);
                setVisibility(View.VISIBLE);
            }
        });
    }

    public void show(final PolyvTickSeekBar.TickData tickData) {
        if (this.tickData != tickData || !isShow()) {
            this.tickData = tickData;
            removeCallbacks(runnable);
            if (isShow()) {
                hide();
                postDelayed(runnable = new Runnable() {
                    @Override
                    public void run() {
                        handleData(tickData);
                    }
                }, 200);
            } else {
                handleData(tickData);
            }
        }
    }
}
