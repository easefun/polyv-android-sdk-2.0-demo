package com.easefun.polyvsdk.player.knowledge;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.player.knowledge.adapter.PolyvPlayerKnowledgePointAdapter;
import com.easefun.polyvsdk.player.knowledge.adapter.PolyvPlayerKnowledgeWordKeyAdapter;
import com.easefun.polyvsdk.player.knowledge.adapter.PolyvPlayerKnowledgeWordTypeAdapter;
import com.easefun.polyvsdk.player.knowledge.vo.PolyvPlayerKnowledgeVO;
import com.easefun.polyvsdk.util.PolyvScreenUtils;

import java.util.List;

/**
 * @author suhongtao
 */
public class PolyvPlayerKnowledgeLayout extends FrameLayout {

    private View rootView;
    private RelativeLayout polyvPlayerKnowledgeWordTypeRl;
    private ImageView polyvPlayerKnowledgeCloseIv;
    private RecyclerView polyvPlayerKnowledgeWordTypeRv;
    private RecyclerView polyvPlayerKnowledgeWordKeyRv;
    private RecyclerView polyvPlayerKnowledgeDetailRv;

    private PolyvPlayerKnowledgeWordTypeAdapter wordTypeAdapter;
    private PolyvPlayerKnowledgeWordKeyAdapter wordKeyAdapter;
    private PolyvPlayerKnowledgePointAdapter knowledgePointAdapter;

    private OnViewActionListener onViewActionListener;

    private static final int AUTO_HIDE_IN_NO_OPERATE_TIMEOUT = 10 * 1000;
    private static final int MSG_WHAT_HIDE = 0;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_WHAT_HIDE:
                    show(false);
                    if (onViewActionListener != null) {
                        onViewActionListener.onAutoClose();
                    }
                    break;
                default:
            }
        }
    };

    public PolyvPlayerKnowledgeLayout(@NonNull Context context) {
        super(context);
        initView();
    }

    public PolyvPlayerKnowledgeLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public PolyvPlayerKnowledgeLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.polyv_player_knowledge_layout, this);
        findView();
        initRecyclerView();
        setOnClickCloseListener();
    }

    private void findView() {
        polyvPlayerKnowledgeWordTypeRl = (RelativeLayout) rootView.findViewById(R.id.polyv_player_knowledge_word_type_rl);
        polyvPlayerKnowledgeCloseIv = (ImageView) rootView.findViewById(R.id.polyv_player_knowledge_close_iv);
        polyvPlayerKnowledgeWordTypeRv = (RecyclerView) rootView.findViewById(R.id.polyv_player_knowledge_word_type_rv);
        polyvPlayerKnowledgeWordKeyRv = (RecyclerView) rootView.findViewById(R.id.polyv_player_knowledge_word_key_rv);
        polyvPlayerKnowledgeDetailRv = (RecyclerView) rootView.findViewById(R.id.polyv_player_knowledge_detail_rv);
    }

    private void initRecyclerView() {
        wordTypeAdapter = new PolyvPlayerKnowledgeWordTypeAdapter();
        polyvPlayerKnowledgeWordTypeRv.setAdapter(wordTypeAdapter);
        polyvPlayerKnowledgeWordTypeRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        wordTypeAdapter.setOnItemClickListener(new PolyvPlayerKnowledgeWordTypeAdapter.OnItemClickListener() {
            @Override
            public void onClick(PolyvPlayerKnowledgeVO.WordType wordType) {
                List<PolyvPlayerKnowledgeVO.WordType.WordKey> wordKeys = wordType.getWordKeys();
                PolyvPlayerKnowledgeVO.WordType.WordKey selectedWordKey = wordKeyAdapter.getSelectedWordKey();
                wordKeyAdapter.setWordKeyList(wordKeys);
                if (selectedWordKey != null && wordKeys.contains(selectedWordKey)) {
                    knowledgePointAdapter.setKnowledgePoints(wordKeyAdapter.getSelectedWordKey().getKnowledgePoints());
                } else {
                    knowledgePointAdapter.setKnowledgePoints(null);
                }
            }
        });

        wordKeyAdapter = new PolyvPlayerKnowledgeWordKeyAdapter();
        polyvPlayerKnowledgeWordKeyRv.setAdapter(wordKeyAdapter);
        polyvPlayerKnowledgeWordKeyRv.setLayoutManager(new LinearLayoutManager(getContext()));
        wordKeyAdapter.setOnItemClickListener(new PolyvPlayerKnowledgeWordKeyAdapter.OnItemClickListener() {
            @Override
            public void onClick(PolyvPlayerKnowledgeVO.WordType.WordKey wordKey) {
                knowledgePointAdapter.setKnowledgePoints(wordKey.getKnowledgePoints());
            }
        });

        knowledgePointAdapter = new PolyvPlayerKnowledgePointAdapter();
        polyvPlayerKnowledgeDetailRv.setAdapter(knowledgePointAdapter);
        polyvPlayerKnowledgeDetailRv.setLayoutManager(new LinearLayoutManager(getContext()));
        knowledgePointAdapter.setOnItemClickListener(new PolyvPlayerKnowledgePointAdapter.OnItemClickListener() {
            @Override
            public void onClick(PolyvPlayerKnowledgeVO.WordType.WordKey.KnowledgePoint knowledgePoint) {
                if (onViewActionListener != null) {
                    onViewActionListener.onClickKnowledgePoint(knowledgePoint);
                }
            }
        });
    }

    private void setOnClickCloseListener() {
        polyvPlayerKnowledgeCloseIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                show(false);
                if (onViewActionListener != null) {
                    onViewActionListener.onClickClose();
                }
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        setAutoHideInNoOperate(AUTO_HIDE_IN_NO_OPERATE_TIMEOUT);
        return super.dispatchTouchEvent(ev);
    }

    public void setKnowledgeData(PolyvPlayerKnowledgeVO vo) {
        wordTypeAdapter.setWordTypeList(null);
        wordKeyAdapter.setWordKeyList(null);
        knowledgePointAdapter.setKnowledgePoints(null);

        if (vo == null) {
            return;
        }

        final boolean fullScreen = vo.getFullScreenStyle() != null && vo.getFullScreenStyle();
        setFullScreenStyle(fullScreen);
        knowledgePointAdapter.setShowKnowledgePointDescription(fullScreen);

        wordTypeAdapter.setWordTypeList(vo.getWordTypes());
        if (vo.getWordTypes() != null
                && vo.getWordTypes().size() > 0
                && vo.getWordTypes().get(0) != null) {
            wordTypeAdapter.setSelectedWordType(vo.getWordTypes().get(0));
            wordKeyAdapter.setWordKeyList(vo.getWordTypes().get(0).getWordKeys());
            if (vo.getWordTypes().get(0).getWordKeys() != null
                    && vo.getWordTypes().get(0).getWordKeys().size() > 0
                    && vo.getWordTypes().get(0).getWordKeys().get(0) != null) {
                wordKeyAdapter.setSelectedWordKey(vo.getWordTypes().get(0).getWordKeys().get(0));
                knowledgePointAdapter.setKnowledgePoints(vo.getWordTypes().get(0).getWordKeys().get(0).getKnowledgePoints());
            }
        }
        wordTypeAdapter.notifyItemChanged(0);
        wordKeyAdapter.notifyItemChanged(0);

        if (onViewActionListener != null) {
            onViewActionListener.onReady();
        }
    }

    public void show(boolean show) {
        if (show) {
            setVisibility(VISIBLE);
            setAutoHideInNoOperate(AUTO_HIDE_IN_NO_OPERATE_TIMEOUT);
        } else {
            setVisibility(GONE);
        }
    }

    public boolean isShowing() {
        return getVisibility() == VISIBLE;
    }

    public void setOnViewActionListener(OnViewActionListener onViewActionListener) {
        this.onViewActionListener = onViewActionListener;
    }

    private void setFullScreenStyle(boolean isFullScreen) {
        ViewGroup.LayoutParams lp = getLayoutParams();
        if (isFullScreen) {
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        } else {
            lp.width = PolyvScreenUtils.dip2px(getContext(), 380);
        }
        setLayoutParams(lp);
    }

    private void setAutoHideInNoOperate(int timeoutInMillis) {
        handler.removeMessages(MSG_WHAT_HIDE);
        handler.sendEmptyMessageDelayed(MSG_WHAT_HIDE, timeoutInMillis);
    }

    public interface OnViewActionListener {
        void onReady();

        void onClickClose();

        void onClickKnowledgePoint(PolyvPlayerKnowledgeVO.WordType.WordKey.KnowledgePoint knowledgePoint);

        void onAutoClose();
    }

}
