package com.easefun.polyvsdk.player.knowledge.widget;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easefun.polyvsdk.R;

/**
 * @author suhongtao
 */
public class PolyvPlayerKnowledgeWordKeyView extends FrameLayout {

    private View rootView;
    private LinearLayout polyvPlayerKnowledgeWordKeyLl;
    private TextView polyvPlayerKnowledgeWordKeyTv;
    private TextView polyvPlayerKnowledgePointCountTv;
    private View polyvPlayerKnowledgeWordKeySeparatorView;

    public PolyvPlayerKnowledgeWordKeyView(@NonNull Context context) {
        super(context);
        initView();
    }

    public PolyvPlayerKnowledgeWordKeyView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public PolyvPlayerKnowledgeWordKeyView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.polyv_player_knowledge_word_key_item, this);
        findView();
    }

    private void findView() {
        polyvPlayerKnowledgeWordKeyLl = (LinearLayout) rootView.findViewById(R.id.polyv_player_knowledge_word_key_ll);
        polyvPlayerKnowledgeWordKeyTv = (TextView) rootView.findViewById(R.id.polyv_player_knowledge_word_key_tv);
        polyvPlayerKnowledgePointCountTv = (TextView) rootView.findViewById(R.id.polyv_player_knowledge_point_count_tv);
        polyvPlayerKnowledgeWordKeySeparatorView = (View) rootView.findViewById(R.id.polyv_player_knowledge_word_key_separator_view);
    }

    public void setWordKey(String wordKey) {
        polyvPlayerKnowledgeWordKeyTv.setText(wordKey);
    }

    public void setKnowledgePointCount(int count) {
        polyvPlayerKnowledgePointCountTv.setText("(" + count + ")");
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected) {
            setBackgroundColor(Color.parseColor("#14FFFFFF"));
            polyvPlayerKnowledgeWordKeySeparatorView.setVisibility(GONE);
            polyvPlayerKnowledgeWordKeyTv.setAlpha(1F);
            polyvPlayerKnowledgePointCountTv.setAlpha(1F);
            polyvPlayerKnowledgeWordKeyTv.setFocusable(true);
        } else {
            setBackgroundColor(Color.TRANSPARENT);
            polyvPlayerKnowledgeWordKeySeparatorView.setVisibility(VISIBLE);
            polyvPlayerKnowledgeWordKeyTv.setAlpha(0.6F);
            polyvPlayerKnowledgePointCountTv.setAlpha(0.6F);
            polyvPlayerKnowledgeWordKeyTv.setFocusable(false);
        }
    }
}
