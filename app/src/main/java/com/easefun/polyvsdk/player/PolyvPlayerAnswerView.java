package com.easefun.polyvsdk.player;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.easefun.polyvsdk.player.fillblank.FillBlankView;
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
    private ScrollView choiceLayout;
    private RelativeLayout fillBankLayout;
    private FillBlankView fillBankContent;
    private TextView choiceQuestionContent;
    private TextView answerResponseTitle;
    private TextView answerResponseContent;
    private ScrollView answerResponseScroll;
    private RelativeLayout answerResponseLayout;
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
     * 选择题的回答选择
     */
    private LinkedList<Integer> answerSelect = new LinkedList<>();
    /**
     * 填空题的回答
     */
    private LinkedList<String> answerSupplyBlankList = new LinkedList<>();

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
        choiceLayout = findViewById(R.id.sc_choice);
        fillBankLayout = findViewById(R.id.rl_fillbank);
        fillBankContent = findViewById(R.id.fill_blank_text);

//        // 答案范围集合

        choiceQuestionContent = (TextView) findViewById(R.id.choice_question_content);
        answerResponseContent = (TextView) findViewById(R.id.answer_response_content);
        answerResponseTitle = (TextView) findViewById(R.id.answer_response_title);
        answerResponseScroll = (ScrollView) findViewById(R.id.answer_response_scroll);
        answerResponseLayout = findViewById(R.id.answer_response_layout);
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

    // <editor-fold defaultstate="collapsed" desc="View方法重写">

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 避免全屏情况下事件传递至播放器，引起弹出问答窗口后仍可播放视频
        return true;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="功能方法">

    /**
     * 插入自定义的问答，详细请看{@link IPolyvVideoView#insertQuestion(PolyvQuestionVO)}方法注释。
     *
     * @param questionVO 问答值对象
     */
    public void insertCustomQuestion(@NonNull PolyvQuestionVO questionVO) {
        polyvVideoView.insertQuestion(questionVO);
    }

    /**
     * 替换问答，详细请看{@link IPolyvVideoView#changeQuestion(int, ArrayList)}方法注释
     *
     * @param showTime       题目出现时间点
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
        setQuestionContentAndAnswerIllustration(questionVO);
        if (questionVO.getType() != PolyvQuestionVO.TYPE_FILL_BLANK) {
            intialAdapter(questionVO);
        }
    }

    /**
     * 重置视图状态
     */
    private void resetViewStatus(@NonNull PolyvQuestionVO questionVO) {
        answerBottomLayout.setVisibility(VISIBLE);
        answerKnow.setVisibility(GONE);
        answerResponseLayout.setVisibility(GONE);
        answerIllustration.setVisibility(VISIBLE);
        answerList.setVisibility(VISIBLE);
        polyvAnswerSkip.setVisibility(questionVO.isSkip() ? VISIBLE : GONE);

        answerContentLayout.setVisibility(VISIBLE);
        answerTipLayout.setVisibility(GONE);
        if (questionVO.getType() == PolyvQuestionVO.TYPE_FILL_BLANK) {
            choiceLayout.setVisibility(GONE);
            fillBankLayout.setVisibility(VISIBLE);
        } else {
            choiceLayout.setVisibility(VISIBLE);
            fillBankLayout.setVisibility(GONE);
        }
    }

    /**
     * 重置属性
     */
    private void resetProperty() {
        answerSelect.clear();
        answerSupplyBlankList.clear();
    }

    /**
     * 设置题目内容和 图片
     *
     * @param questionVO
     */
    private void setQuestionContentAndAnswerIllustration(PolyvQuestionVO questionVO) {
        //格式化问题，因为后台支持在问题中配置图片。
        List<PolyvQAFormatVO> list = PolyvQuestionUtil.parseQA2(questionVO.getQuestion());
        StringBuilder questionMainContent = new StringBuilder();
        String imgUrl = "";
        for (PolyvQAFormatVO polyvQAFormatVO : list) {
            if (polyvQAFormatVO.getStringType().ordinal() == PolyvQAFormatVO.StringType.STRING.ordinal()) {
                questionMainContent.append(polyvQAFormatVO.getStr());
            }

            if (polyvQAFormatVO.getStringType().ordinal() == PolyvQAFormatVO.StringType.URL.ordinal()
                    && TextUtils.isEmpty(imgUrl)) {
                //如果问题中配置了图片，只显示第一张图片
                imgUrl = polyvQAFormatVO.getStr();
            }
        }

        boolean isMultiSelected = questionVO.isMultiSelected();
        SpannableStringBuilder span;
        if (questionVO.getType() == PolyvQuestionVO.TYPE_QUESTION) {
            span = new SpannableStringBuilder(isMultiSelected ? "【多选题】" : "【单选题】");
        } else {
            span = new SpannableStringBuilder("【填空题】");
        }

        span.append(questionMainContent);
        span.setSpan(new ForegroundColorSpan(Color.parseColor("#333333")), 0, span.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (questionVO.getType() == PolyvQuestionVO.TYPE_QUESTION) {
            choiceQuestionContent.setText(span);
        } else {
            fillBankContent.setData(new SpannableStringBuilder(span));
        }

        if (questionVO.getType() == PolyvQuestionVO.TYPE_QUESTION) {
            if (!questionVO.illustrationIsEmpty()) {
                answerIllustration.setVisibility(VISIBLE);
                PolyvImageLoader.getInstance().loadImageOrigin(getContext(), fixUrl(questionVO.getIllustration()), answerIllustration, R.drawable.polyv_loading);
            } else if (!TextUtils.isEmpty(imgUrl)) {
                answerIllustration.setVisibility(VISIBLE);
                PolyvImageLoader.getInstance().loadImageOrigin(getContext(), fixUrl(imgUrl), answerIllustration, R.drawable.polyv_loading);
            } else {
                answerIllustration.setVisibility(GONE);
            }
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

        }

        answerList.setLayoutManager(new LinearLayoutManager(getContext()));
//        } else {
//            answerList.setLayoutManager(new GridLayoutManager(getContext(), 2));
//        }

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
        if (polyvQuestionVO == null) {
            return;
        }
        switch (polyvQuestionVO.getType()) {
            case PolyvQuestionVO.TYPE_QUESTION:
            case PolyvQuestionVO.TYPE_AUDITION:
                if (!isAnswerSelect()) {
                    Toast.makeText(getContext(), R.string.no_choice, Toast.LENGTH_LONG).show();
                    return;
                }
                hide();
                polyvVideoView.answerQuestion2(answerSelect);
                break;
            case PolyvQuestionVO.TYPE_FILL_BLANK:
                answerSupplyBlankList = fillBankContent.getRightAnswerList();
                if (!isAnswerSupplyBlank()) {
                    Toast.makeText(getContext(), R.string.no_answer_supply_blank, Toast.LENGTH_LONG).show();
                    return;
                }
                hide();
                polyvVideoView.answerQuestion3(answerSupplyBlankList);
                break;
        }

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
                polyvAnswerTipContent.setTextColor(Color.parseColor("#4A90E2"));
            } else {// 错误
                answerTipImg.setImageResource(R.drawable.polyv_answer_wrong);
                polyvAnswerTipContent.setText(R.string.answer_wrong);
                polyvAnswerTipContent.setTextColor(Color.parseColor("#F95652"));
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
            answerContentLayout.setVisibility(GONE);
            answerResponseLayout.setVisibility(VISIBLE);
            answerResponseTitle.setText(isAnswerRight ? R.string.answer_right : R.string.answer_wrong);
            answerResponseTitle.setTextColor(Color.parseColor(isAnswerRight ? "#6FAB32" : "#F95652"));
            answerResponseContent.setText(msg);


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
                    case PolyvQuestionVO.TYPE_FILL_BLANK:
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
     *
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
    private boolean isAnswerSelect() {
        return !answerSelect.isEmpty();
    }

    private boolean isAnswerSupplyBlank() {
        return !answerSupplyBlankList.isEmpty();
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