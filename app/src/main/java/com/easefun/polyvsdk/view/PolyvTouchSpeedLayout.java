package com.easefun.polyvsdk.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.easefun.polyvsdk.R;

public class PolyvTouchSpeedLayout extends FrameLayout {
    TextView ffStatusText;

    public PolyvTouchSpeedLayout(@NonNull Context context) {
        this(context, null);
    }

    public PolyvTouchSpeedLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PolyvTouchSpeedLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.polyv_player_touch_speed_layout, this, true);
        ImageView ffView = findViewById(R.id.ff_view);
        ffStatusText = findViewById(R.id.ff_status_text);
        ffStatusText.setText("快进x2");
        ((AnimationDrawable) ffView.getDrawable()).start();
    }

    public void show() {
        setVisibility(View.VISIBLE);
    }

    public void hide() {
        setVisibility(View.GONE);
    }

    public void updateStatus(boolean isLoading) {
        ffStatusText.setText(isLoading ? "loading" : "快进x2");
    }
}
