package com.easefun.polyvsdk.player.knowledge.widget;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.util.PolyvTimeUtils;

/**
 * @author suhongtao
 */
public class PolyvPlayerKnowledgePointView extends FrameLayout {

    private View rootView;
    private ImageView polyvPlayerKnowledgePointIv;
    private TextView polyvPlayerKnowledgePointDescTv;
    private TextView polyvPlayerKnowledgePointTimeTv;

    public PolyvPlayerKnowledgePointView(@NonNull Context context) {
        super(context);
        initView();
    }

    public PolyvPlayerKnowledgePointView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public PolyvPlayerKnowledgePointView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.polyv_player_knowledge_point_item, this);
        findView();
    }

    private void findView() {
        polyvPlayerKnowledgePointIv = (ImageView) rootView.findViewById(R.id.polyv_player_knowledge_point_iv);
        polyvPlayerKnowledgePointDescTv = (TextView) rootView.findViewById(R.id.polyv_player_knowledge_point_desc_tv);
        polyvPlayerKnowledgePointTimeTv = (TextView) rootView.findViewById(R.id.polyv_player_knowledge_point_time_tv);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected) {
            polyvPlayerKnowledgePointIv.setImageResource(R.drawable.polyv_player_knowledge_selected_icon);
            polyvPlayerKnowledgePointDescTv.setTextColor(Color.parseColor("#3990FF"));
            polyvPlayerKnowledgePointTimeTv.setTextColor(Color.parseColor("#3990FF"));
            polyvPlayerKnowledgePointDescTv.setFocusable(true);
        } else {
            polyvPlayerKnowledgePointIv.setImageResource(R.drawable.polyv_player_knowledge_unselected_icon);
            polyvPlayerKnowledgePointDescTv.setTextColor(Color.parseColor("#CCFFFFFF"));
            polyvPlayerKnowledgePointTimeTv.setTextColor(Color.parseColor("#99FFFFFF"));
            polyvPlayerKnowledgePointDescTv.setFocusable(false);
        }
    }

    public void setDescription(String name) {
        polyvPlayerKnowledgePointDescTv.setText(name);
    }

    public void setTime(int timeInSecond) {
        final String timeText = PolyvTimeUtils.generateTime(timeInSecond * 1000L, true);
        polyvPlayerKnowledgePointTimeTv.setText(timeText);
    }

    public void showDescription(boolean show) {
        polyvPlayerKnowledgePointDescTv.setVisibility(show ? VISIBLE : GONE);
    }
}
