package com.easefun.polyvsdk.player;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.easefun.polyvsdk.PolyvQuestionUtil;
import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.adapter.PolyvAnswerAdapter;
import com.easefun.polyvsdk.fragment.PolyvPlayerDanmuFragment;
import com.easefun.polyvsdk.question.PolyvQuestionDoneAction;
import com.easefun.polyvsdk.util.PolyvCustomQuestionBuilder;
import com.easefun.polyvsdk.util.PolyvImageLoader;
import com.easefun.polyvsdk.util.PolyvScreenUtils;
import com.easefun.polyvsdk.video.IPolyvVideoView;
import com.easefun.polyvsdk.video.PolyvVideoView;
import com.easefun.polyvsdk.video.listener.IPolyvOnQuestionListener;
import com.easefun.polyvsdk.vo.PolyvQAFormatVO;
import com.easefun.polyvsdk.vo.PolyvQuestionVO;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 问答视图
 */
public class PolyvPlayerAnswerView extends RelativeLayout implements View.OnClickListener {
    // <editor-fold defaultstate="collapsed" desc="视图控件">
    private LinearLayout answerContentLayout;
    private TextView answerTitle, answerResponseContent;
    private ScrollView answerResponseScroll;
    private ImageView answerIllustration;
    private RecyclerView answerList;
    private TextView answerKnow;
    private LinearLayout answerBottomLayout;
    private TextView polyvAnswerSkip;
    private TextView polyvAnswerSubmit;

    private LinearLayout answerTipLayout;
    private ImageView answerTipImg;
    private TextView polyvAnswerTipContent;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="成员变量">
    /**
     * 回答选择
     */
    private LinkedList<Integer> answerSelect = new LinkedList<>();
    /**
     * 问答值对象
     */
    private PolyvQuestionVO polyvQuestionVO = null;
    /**
     * 播放器
     */
    private PolyvVideoView polyvVideoView = null;
    /**
     * 语音问答控件
     */
    private PolyvPlayerAuditionView auditionView = null;
    /**
     * 弹幕控件
     */
    private PolyvPlayerDanmuFragment danmuFragment = null;
    /**
     * 自定义问答的答题结果监听器
     */
    private PolyvCustomQuestionBuilder.IPolyvOnCustomQuestionAnswerResultListener answerResultListener = null;
    /**
     * 图片回答提示显示时间
     */
    private static final int ANSWER_TIP_STAY_TIME = 3 * 1000;

    /**
     * 我知道了逻辑使用，是否回答正确
     */
    private boolean isAnswerRight = false;
    /**
     * 我知道了逻辑使用，回退点，单位毫秒
     */
    private int seek = 0;

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="构造方法">
    public PolyvPlayerAnswerView(Context context) {
        this(context, null);
    }

    public PolyvPlayerAnswerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PolyvPlayerAnswerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="初始化方法">

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.polyv_player_question_view_refactor, this);

        answerContentLayout = (LinearLayout) findViewById(R.id.answer_content_layout);
        answerTitle = (TextView) findViewById(R.id.answer_title);
        answerResponseContent = (TextView) findViewById(R.id.answer_response_content);
        answerResponseScroll = (ScrollView) findViewById(R.id.answer_response_scroll);
        answerIllustration = (ImageView) findViewById(R.id.answer_illustration);
        answerList = (RecyclerView) findViewById(R.id.answer_list);
        answerKnow = (TextView) findViewById(R.id.answer_know);
        answerBottomLayout = (LinearLayout) findViewById(R.id.answer_bottom_layout);
        polyvAnswerSkip = (TextView) findViewById(R.id.polyv_answer_skip);
        polyvAnswerSubmit = (TextView) findViewById(R.id.polyv_answer_submit);

        answerTipLayout = (LinearLayout) findViewById(R.id.answer_tip_layout);
        answerTipImg = (ImageView) findViewById(R.id.answer_tip_img);
        polyvAnswerTipContent = (TextView) findViewById(R.id.polyv_answer_tip_content);

        addListener();
    }

    private void addListener() {
        answerKnow.setOnClickListener(this);
        polyvAnswerSkip.setOnClickListener(this);
        polyvAnswerSubmit.setOnClickListener(this);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="功能方法">
    /**
     * 插入自定义的问答，详细请看{@link IPolyvVideoView#insertQuestion(PolyvQuestionVO)}方法注释。
     * @param questionVO 问答值对象
     */
    public void insertCustomQuestion(@NonNull PolyvQuestionVO questionVO) {
        polyvVideoView.insertQuestion(questionVO);
    }

    /**
     * 替换问答，详细请看{@link IPolyvVideoView#changeQuestion(int, ArrayList)}方法注释
     * @param showTime 题目出现时间点
     * @param questionVOList 问答值对象列表
     */
    public void changeQuestion(int showTime, @Nullable ArrayList<PolyvQuestionVO> questionVOList) {
        polyvVideoView.changeQuestion(showTime, questionVOList);
    }

    /**
     * 显示问答
     *
     * @param questionVO 问答值对象
     */
    private void showQuestion(@NonNull PolyvQuestionVO questionVO) throws InvalidParameterException {
        if (questionVO == null) {
            throw new InvalidParameterException("参数错误");
        }

        if (questionVO.getChoicesList2() == null) {
            throw new InvalidParameterException("请设置问答选项");
        }

        polyvQuestionVO = questionVO;

        show();
        resetViewStatus(questionVO);
        resetProperty();
        setTitle(questionVO);
        intialAdapter(questionVO);
    }

    /**
     * 重置视图状态
     */
    private void resetViewStatus(@NonNull PolyvQuestionVO questionVO) {
        answerBottomLayout.setVisibility(VISIBLE);
        answerKnow.setVisibility(GONE);
        answerResponseScroll.setVisibility(GONE);
        answerIllustration.setVisibility(VISIBLE);
        answerList.setVisibility(VISIBLE);
        polyvAnswerSkip.setVisibility(questionVO.isSkip() ? VISIBLE : GONE);

        answerContentLayout.setVisibility(VISIBLE);
        answerTipLayout.setVisibility(GONE);
    }

    /**
     * 重置属性
     */
    private void resetProperty() {
        answerSelect.clear();
    }

    /**
     * 设置标题
     *
     * @param questionVO
     */
    private void setTitle(PolyvQuestionVO questionVO) {
        //格式化问题，因为后台支持在问题中配置图片。
        List<PolyvQAFormatVO> list = PolyvQuestionUtil.parseQA2(questionVO.getQuestion());
        StringBuilder title = new StringBuilder();
        String imgUrl = "";
        for (PolyvQAFormatVO polyvQAFormatVO : list) {
            if (polyvQAFormatVO.getStringType().ordinal() == PolyvQAFormatVO.StringType.STRING.ordinal()) {
                title.append(polyvQAFormatVO.getStr());
            }

            if (polyvQAFormatVO.getStringType().ordinal() == PolyvQAFormatVO.StringType.URL.ordinal()
                    && TextUtils.isEmpty(imgUrl)) {
                //如果问题中配置了图片，只显示第一张图片
                imgUrl = polyvQAFormatVO.getStr();
            }
        }

        boolean isMultiSelected = questionVO.isMultiSelected();
        SpannableStringBuilder span = new SpannableStringBuilder(isMultiSelected ? "【多选题】" : "【单选题】");
        span.setSpan(new ForegroundColorSpan(Color.parseColor("#4A90E2")), 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.append(title);
        answerTitle.setText(span);

        if (!questionVO.illustrationIsEmpty()) {
            answerIllustration.setVisibility(VISIBLE);
            PolyvImageLoader.getInstance().loadImageOrigin(getContext(), questionVO.getIllustration(), answerIllustration, R.drawable.polyv_avatar_def);
        } else if (!TextUtils.isEmpty(imgUrl)) {
            answerIllustration.setVisibility(VISIBLE);
            PolyvImageLoader.getInstance().loadImageOrigin(getContext(), imgUrl, answerIllustration, R.drawable.polyv_avatar_def);

        } else {
            answerIllustration.setVisibility(GONE);
        }
    }

    private String fixUrl(String url) {
        if (url != null && url.startsWith("//")) {
            return "https:" + url;
        }
        return url;
    }

    /**
     * 初始化答题选项控件
     *
     * @param questionVO 问答值对象
     */
    private void intialAdapter(PolyvQuestionVO questionVO) {
        final boolean isMultiSelected = questionVO.isMultiSelected();
        PolyvAnswerAdapter polyvAnswerAdapter = new PolyvAnswerAdapter(questionVO.getChoicesList2(), getContext(), isMultiSelected);
        polyvAnswerAdapter.setAnswerSelectCallback(new PolyvAnswerAdapter.AnswerSelectCallback() {
            @Override
            public void onSelectAnswer(Integer pos, boolean isSelected) {
                //单选项，只能选择一个选项，如果是选中，要删除以前的选项
                if (!isMultiSelected && isSelected) {
                    answerSelect.clear();
                }

                //重选
                if (answerSelect.contains(pos)) {
                    answerSelect.remove(pos);
                }

                if (isSelected) {
                    answerSelect.add(pos);
                }
            }
        });

        if (answerIllustration.getVisibility() == VISIBLE || PolyvScreenUtils.isLandscape(getContext())) {
            answerList.setLayoutManager(new LinearLayoutManager(getContext()));
        } else {
            answerList.setLayoutManager(new GridLayoutManager(getContext(), 2));
        }

        answerList.setAdapter(polyvAnswerAdapter);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.answer_know:
                iKnow();
                break;
            case R.id.polyv_answer_skip:
                skip();
                break;
            case R.id.polyv_answer_submit:
                submitAnswer();
                break;
        }
    }

    /**
     * 我知道了，继续播放
     */
    private void iKnow() {
        seekPlay(seek);
        polyvVideoView.doneQuestion(isAnswerRight ?
                PolyvQuestionDoneAction.ANSWOER_SUCCESS : PolyvQuestionDoneAction.ANSWOER_FAILURE);
    }

    /**
     * 跳过当前问答
     */
    private void skip() {
        polyvVideoView.skipQuestion2();
    }

    /**
     * 提交答案
     */
    private void submitAnswer() {
        if (!isAnswer()) {
            Toast.makeText(getContext(), R.string.no_choice, Toast.LENGTH_LONG).show();
            return;
        }

        hide();

        if (polyvQuestionVO == null) {
            return;
        }

        polyvVideoView.answerQuestion2(answerSelect);
        //自定义问答的答题结果回调
        if (answerResultListener != null) {
            answerResultListener.onAnswerResult(polyvQuestionVO);
        }
    }

    /**
     * 显示答案提示
     *
     * @param msg 内容
     */
    private void showAnswerTips(final boolean isAnswerRight, String msg, final int seek) {
        show();
        answerIllustration.setVisibility(GONE);
        answerList.setVisibility(GONE);

        if (TextUtils.isEmpty(msg)) {//如果没有提示语
            answerContentLayout.setVisibility(GONE);
            answerTipLayout.setVisibility(VISIBLE);
            if (isAnswerRight) {//正确
                answerTipImg.setImageResource(R.drawable.polyv_answer_right);
                polyvAnswerTipContent.setText(R.string.answer_right);
            } else {// 错误
                answerTipImg.setImageResource(R.drawable.polyv_answer_wrong);
                polyvAnswerTipContent.setText(R.string.answer_wrong);
            }

            answerTipLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    seekPlay(seek);
                    PolyvPlayerAnswerView.this.polyvVideoView.doneQuestion(isAnswerRight ?
                            PolyvQuestionDoneAction.ANSWOER_SUCCESS : PolyvQuestionDoneAction.ANSWOER_FAILURE);
                }
            }, ANSWER_TIP_STAY_TIME);
        } else {
            this.isAnswerRight = isAnswerRight;
            this.seek = seek;

            answerBottomLayout.setVisibility(GONE);
            answerKnow.setVisibility(VISIBLE);
            answerResponseScroll.setVisibility(VISIBLE);

            answerResponseContent.setText(msg);
            answerTitle.setText(isAnswerRight ? R.string.answer_right : R.string.answer_wrong);
        }
    }

    /**
     * 设置播放器
     *
     * @param polyvVideoView
     */
    public void setPolyvVideoView(@NonNull PolyvVideoView polyvVideoView) {
        this.polyvVideoView = polyvVideoView;
        polyvVideoView.setOnQuestionListener(new IPolyvOnQuestionListener() {
            @Override
            public void onPopUp(@NonNull PolyvQuestionVO questionVO) {
                switch (questionVO.getType()) {
                    case PolyvQuestionVO.TYPE_QUESTION:
                        showQuestion(questionVO);
                        break;

                    case PolyvQuestionVO.TYPE_AUDITION:
                        if (auditionView != null) {
                            auditionView.show(questionVO);
                        }

                        break;
                }
            }

            @Override
            public void onAnswerResult(final boolean isAnswerRight, @NonNull PolyvQuestionVO questionVO, @NonNull String msg, int seek) {
                showAnswerTips(isAnswerRight, msg, seek);
            }

            @Override
            public void onSkipCallback(@NonNull PolyvQuestionVO questionVO) {
                play();
                PolyvPlayerAnswerView.this.polyvVideoView.doneQuestion(PolyvQuestionDoneAction.SKIP_QUESTION);
            }
        });
    }

    /**
     * 播放
     */
    private void play() {
        hide();
        polyvVideoView.start();
        if (danmuFragment != null) {
            danmuFragment.resume();
        }
    }

    /**
     * 跳跃播放
     * @param seek
     */
    private void seekPlay(int seek) {
        hide();
        if (seek >= 0) {
            polyvVideoView.seekTo(seek);
        }

        polyvVideoView.start();
        if (danmuFragment != null) {
            danmuFragment.resume();
        }
    }

    /**
     * 设置语音问答控件
     *
     * @param auditionView
     */
    public void setAuditionView(PolyvPlayerAuditionView auditionView) {
        this.auditionView = auditionView;
    }

    /**
     * 设置弹幕控件
     *
     * @param danmuFragment
     */
    public void setDanmuFragment(PolyvPlayerDanmuFragment danmuFragment) {
        this.danmuFragment = danmuFragment;
    }

    /**
     * 隐藏视图
     */
    public void hide() {
        setVisibility(GONE);
    }

    /**
     * 显示视图
     */
    public void show() {
        setVisibility(VISIBLE);
    }

    /**
     * 是否回答了问题
     *
     * @return {@code true}:正确，{@code false}:错误
     */
    private boolean isAnswer() {
        return !answerSelect.isEmpty();
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getChildCount() == 0) {
            return;
        }
        if (PolyvScreenUtils.isLandscape(getContext())) {
            answerContentLayout.setBackgroundResource(R.drawable.polyv_answer_back);
            ViewGroup.LayoutParams layoutParams = answerContentLayout.getLayoutParams();
            layoutParams.width = PolyvScreenUtils.dip2px(getContext(), 420);
            layoutParams.height = PolyvScreenUtils.dip2px(getContext(), 240);

            ViewGroup.LayoutParams answerTipLayoutParams = answerTipLayout.getLayoutParams();
            answerTipLayoutParams.width = PolyvScreenUtils.dip2px(getContext(), 200);
            answerTipLayoutParams.height = PolyvScreenUtils.dip2px(getContext(), 200);

            answerList.setLayoutManager(new LinearLayoutManager(getContext()));
        } else {
            answerContentLayout.setBackgroundResource(android.R.color.white);
            ViewGroup.LayoutParams layoutParams = answerContentLayout.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;

            ViewGroup.LayoutParams answerTipLayoutParams = answerTipLayout.getLayoutParams();
            answerTipLayoutParams.width = PolyvScreenUtils.dip2px(getContext(), 150);
            answerTipLayoutParams.height = PolyvScreenUtils.dip2px(getContext(), 150);

            if (answerIllustration.getVisibility() == VISIBLE) {
                answerList.setLayoutManager(new LinearLayoutManager(getContext()));
            } else {
                answerList.setLayoutManager(new GridLayoutManager(getContext(), 2));
            }
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="设置监听回调">
    /**
     * 设置自定义问答的答题结果回调
     *
     * @param listener
     */
    public void setCustomQuestionAnswerResultListener(PolyvCustomQuestionBuilder.IPolyvOnCustomQuestionAnswerResultListener listener) {
        this.answerResultListener = listener;
    }
    // </editor-fold>
}