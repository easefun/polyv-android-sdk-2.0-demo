package com.easefun.polyvsdk.player;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
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
import com.easefun.polyvsdk.PolyvSDKUtil;
import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.adapter.PolyvAnswerAdapter;
import com.easefun.polyvsdk.util.PolyvScreenUtils;
import com.easefun.polyvsdk.video.PolyvVideoView;
import com.easefun.polyvsdk.vo.PolyvQAFormatVO;
import com.easefun.polyvsdk.vo.PolyvQuestionChoicesVO;
import com.easefun.polyvsdk.vo.PolyvQuestionVO;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.LinkedList;
import java.util.List;

public class PolyvPlayerAnswerView extends RelativeLayout implements View.OnClickListener {
    private TextView answerTitle, answerResponseContent;
    private ScrollView answerResponseScroll;
    private ImageView answerIllustration;
    private RecyclerView answerList;
    private TextView answerKonw;
    private LinearLayout answerBottomLayout;
    private TextView polyvAnswerSkip;
    private TextView polyvAnswerSubmit;

    private PolyvAnswerAdapter polyvAnswerAdapter;
    private boolean isMultiSelected;
    private int rightAnswerNum;
    private LinkedList rightAnswers = new LinkedList();
    private LinkedList wrongAnserSelect = new LinkedList();
    private LinkedList rightAnserSelect = new LinkedList();
    private PolyvQuestionVO polyvQuestionVO;
    private LinearLayout answerContentLayout, answerTipLayout;
    private ImageView answerTipImg;
    private TextView polyvAnswerTipContent;
    private PolyvVideoView polyvVideoView;
    private static final int ANSWER_TIP_STAY_TIME = 3*1000;
    private DisplayImageOptions imageOptions;

    public PolyvPlayerAnswerView(Context context) {
        this(context, null);
    }


    public PolyvPlayerAnswerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PolyvPlayerAnswerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        initial();
    }

    private void initial() {
        if (imageOptions == null) {
            imageOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.polyv_avatar_def) // 设置图片在下载期间显示的图片
                    .showImageForEmptyUri(R.drawable.polyv_avatar_def)// 设置图片Uri为空或是错误的时候显示的图片
                    .showImageOnFail(R.drawable.polyv_avatar_def) // 设置图片加载/解码过程中错误时候显示的图片
                    .bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型
                    .cacheInMemory(true)// 设置下载的图片是否缓存在内存中
                    .cacheOnDisk(true)// 设置下载的图片是否缓存在SD卡中
                    .displayer(new FadeInBitmapDisplayer(100))// 是否图片加载好后渐入的动画时间
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();// 构建完成
        }
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.polyv_player_question_view_refactor, this);

        answerTitle = (TextView) findViewById(R.id.answer_title);
        answerResponseContent = (TextView) findViewById(R.id.answer_response_content);
        answerResponseScroll = (ScrollView) findViewById(R.id.answer_response_scroll);
        answerIllustration = (ImageView) findViewById(R.id.answer_illustration);
        answerList = (RecyclerView) findViewById(R.id.answer_list);
        answerKonw = (TextView) findViewById(R.id.answer_konw);
        answerBottomLayout = (LinearLayout) findViewById(R.id.answer_bottom_layout);
        polyvAnswerSkip = (TextView) findViewById(R.id.polyv_answer_skip);
        polyvAnswerSubmit = (TextView) findViewById(R.id.polyv_answer_submit);


        answerContentLayout = (LinearLayout) findViewById(R.id.answer_content_layout);
        answerTipLayout = (LinearLayout) findViewById(R.id.answer_tip_layout);
        answerTipImg = (ImageView) findViewById(R.id.answer_tip_img);
        polyvAnswerTipContent = (TextView) findViewById(R.id.polyv_answer_tip_content);

        addListener();
    }

    private void addListener() {
        answerKonw.setOnClickListener(this);
        polyvAnswerSkip.setOnClickListener(this);
        polyvAnswerSubmit.setOnClickListener(this);
    }

    public void showAnswerContent(PolyvQuestionVO questionVO) {
        if (questionVO == null || questionVO.getChoicesList2() == null) {
            return;
        }
        show();
        resetViewStatus();

        polyvQuestionVO = questionVO;
        if (!questionVO.isSkip()) {
            polyvAnswerSkip.setVisibility(GONE);
        }

        setTitle(questionVO);

        initialChoiceStatus(questionVO);

        intialAdapter(questionVO);

    }

    private void intialAdapter(PolyvQuestionVO questionVO) {
        polyvAnswerAdapter = new PolyvAnswerAdapter(questionVO.getChoicesList2(), getContext(), isMultiSelected);
        polyvAnswerAdapter.setAnswerSelectCallback(new PolyvAnswerAdapter.AnswerSelectCallback() {
            @Override
            public void onSelectAnswer(Integer pos, boolean isSelected) {

                //如果是多选项累加，
                //单选项 删除上一个 添加最新的
                if (isSelected) {
                    if (!rightAnswers.contains(pos)) {
                        wrongAnserSelect.add(pos);
                    } else {
                        rightAnserSelect.add(rightAnswers.remove(pos));
                    }
                } else {
                    if (rightAnserSelect.contains(pos)) {
                        rightAnswers.add(rightAnserSelect.remove(pos));
                    } else {
                        wrongAnserSelect.remove(pos);
                    }
                }
            }
        });


        if (answerIllustration.getVisibility() == VISIBLE) {
            answerList.setLayoutManager(new LinearLayoutManager(getContext()));
        } else {
            answerList.setLayoutManager(new GridLayoutManager(getContext(), 2));
        }
        answerList.setAdapter(polyvAnswerAdapter);
    }

    private void resetViewStatus() {
        answerBottomLayout.setVisibility(VISIBLE);
        answerKonw.setVisibility(GONE);
        answerResponseScroll.setVisibility(GONE);
        answerIllustration.setVisibility(VISIBLE);
        answerList.setVisibility(VISIBLE);

        answerContentLayout.setVisibility(VISIBLE);
        answerTipLayout.setVisibility(GONE);
    }

    private void initialChoiceStatus(PolyvQuestionVO questionVO) {
        if (questionVO == null) {
            return;
        }
        resetStatus();

        //单个正确答案是单选
        //多个正确答案是多选
        int rightAnswerNum = 0;
        List<PolyvQuestionChoicesVO> choicesList = questionVO.getChoicesList2();
        int length = choicesList.size();
        for (int i = 0; i < length; i++) {
            PolyvQuestionChoicesVO choicesVO = choicesList.get(i);
            if (choicesVO.getRightAnswer() == 1) {
                rightAnswerNum++;
                rightAnswers.add(i);
            }
        }

        this.rightAnswerNum = rightAnswerNum;
        isMultiSelected = (rightAnswerNum > 1);
    }

    private void resetStatus() {
        rightAnswers.clear();
        wrongAnserSelect.clear();
        rightAnswerNum = 0;
        isMultiSelected = false;
    }

    private void setTitle(PolyvQuestionVO questionVO) {
        List<PolyvQAFormatVO> list = PolyvQuestionUtil.parseQA2(questionVO.getQuestion());
        StringBuilder title = new StringBuilder();
        String imgUrl = "";
        for (PolyvQAFormatVO polyvQAFormatVO : list) {
            if (polyvQAFormatVO.getStringType().ordinal() == PolyvQAFormatVO.StringType.STRING.ordinal()) {
                title.append(polyvQAFormatVO.getStr());
            }
            if (polyvQAFormatVO.getStringType().ordinal() == PolyvQAFormatVO.StringType.URL.ordinal()
                    && TextUtils.isEmpty(imgUrl)) {
                imgUrl = polyvQAFormatVO.getStr();
            }
        }
        answerTitle.setText(title);

        if (!TextUtils.isEmpty(questionVO.getIllustration()) && !"null".equals(questionVO.getIllustration())) {
            answerIllustration.setVisibility(VISIBLE);
            String url = String.format("http:%s",questionVO.getIllustration());
            ImageLoader.getInstance().displayImage(url, answerIllustration,
                    imageOptions, new PolyvAnimateFirstDisplayListener());
        } else if (!TextUtils.isEmpty(imgUrl)) {
            answerIllustration.setVisibility(VISIBLE);
            ImageLoader.getInstance().displayImage(imgUrl, answerIllustration,
                    imageOptions,new PolyvAnimateFirstDisplayListener());
        } else {
            answerIllustration.setVisibility(GONE);
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.answer_konw:
                hide();
                continuePlay(0);
                break;
            case R.id.polyv_answer_skip:
                hide();
                if (polyvVideoView != null) {
                    polyvVideoView.skipQuestion();
                }
                break;
            case R.id.polyv_answer_submit:
                submitAnswer();
                break;
        }

    }

    private void continuePlay(int seek) {
        int seconds = PolyvSDKUtil.formatToSecond(polyvQuestionVO.getHours(),
                polyvQuestionVO.getMinutes(), polyvQuestionVO.getSeconds()) * 1000;

        boolean answerRight = rightAnswers.isEmpty() && wrongAnserSelect.isEmpty();
        if(!answerRight){
             seconds =  polyvQuestionVO.getWrongTime() * 1000;
        }
        if(seconds >= 0){
            polyvVideoView.seekTo(seconds);
        }
        polyvVideoView.start();
    }

    private void submitAnswer() {
        if (rightAnswers.size() == rightAnswerNum && wrongAnserSelect.isEmpty()) {
            Toast.makeText(getContext(), R.string.no_choice, Toast.LENGTH_LONG).show();
            return;
        }

        changeRightAnswer();
    }

    private void changeRightAnswer() {
        hide();

        if (polyvQuestionVO == null) {
            return;
        }
        polyvVideoView.answerQuestion(rightAnswers.isEmpty() && wrongAnserSelect.isEmpty(),
                rightAnswers.isEmpty() ? polyvQuestionVO.getAnswer() : polyvQuestionVO.getWrongAnswer());
    }

    public void showAnswerTips(String msg) {
        showAnswerTips(msg,0);
    }

    /**
     * 显示答案提示
     *
     * @param msg
     */
    public void showAnswerTips(String msg, final int seek) {

        show();
        answerIllustration.setVisibility(GONE);
        answerList.setVisibility(GONE);

        boolean answerRight = rightAnswers.isEmpty() && wrongAnserSelect.isEmpty();
        if (TextUtils.isEmpty(msg)) {//如果没有正确错误的提示语
            answerContentLayout.setVisibility(GONE);
            answerTipLayout.setVisibility(VISIBLE);
            if (answerRight) {//正确
                answerTipImg.setImageResource(R.drawable.polyv_answer_right);
                polyvAnswerTipContent.setText(R.string.answer_right);
            } else {// 错误
                answerTipImg.setImageResource(R.drawable.polyv_answer_wrong);
                polyvAnswerTipContent.setText(R.string.answer_wrong);
            }

            answerTipLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setVisibility(GONE);
                    continuePlay(seek);
                }
            }, ANSWER_TIP_STAY_TIME);
        } else {
            answerBottomLayout.setVisibility(GONE);
            answerKonw.setVisibility(VISIBLE);
            answerResponseScroll.setVisibility(VISIBLE);

            answerResponseContent.setText(msg);
            answerTitle.setText(answerRight ? R.string.answer_right : R.string.answer_wrong);
        }

    }

    public void setPolyvVideoView(PolyvVideoView polyvVideoView) {
        this.polyvVideoView = polyvVideoView;
    }

    public void hide() {
        setVisibility(INVISIBLE);
    }

    public void show() {
        setVisibility(VISIBLE);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(getChildCount() == 0){
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
        } else {
            answerContentLayout.setBackgroundResource(android.R.color.white);
            ViewGroup.LayoutParams layoutParams = answerContentLayout.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;

            ViewGroup.LayoutParams answerTipLayoutParams = answerTipLayout.getLayoutParams();
            answerTipLayoutParams.width = PolyvScreenUtils.dip2px(getContext(), 150);
            answerTipLayoutParams.height = PolyvScreenUtils.dip2px(getContext(), 150);
        }
    }
}
