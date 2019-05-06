package com.easefun.polyvsdk.util;

import android.support.annotation.NonNull;

import com.easefun.polyvsdk.QuestionVO;
import com.easefun.polyvsdk.player.PolyvPlayerAnswerView;
import com.easefun.polyvsdk.video.listener.IPolyvOnQuestionAnswerTipsCustomListener;
import com.easefun.polyvsdk.vo.PolyvQuestionVO;

import java.util.ArrayList;
import java.util.List;

/**
 * date: 2019/4/11 0011
 *
 * @author hwj
 * description 自定义问答构建器
 */
public class PolyvCustomQuestionBuilder {
    private PolyvPlayerAnswerView answerView;

    private String examId;
    private String question;
    private ChoiceList choiceList;
    private String rightAnswerTip;
    private String wrongAnswerTip;
    private int wrongTime = -1;
    private boolean skip = true;
    private String illustration;


    private PolyvCustomQuestionBuilder(PolyvPlayerAnswerView answerView) {
        this.answerView = answerView;
    }

    public static PolyvCustomQuestionBuilder create(PolyvPlayerAnswerView answerView) {
        return new PolyvCustomQuestionBuilder(answerView);
    }

    /**
     * 必填参数
     *
     * @param examId      问答id。
     * @param question    题目
     * @param choicesList 选项
     * @throws Exception 当必填参数为null时，抛出异常
     */
    public PolyvCustomQuestionBuilder mustParam(@NonNull String examId, @NonNull String question, @NonNull ChoiceList choicesList) throws Exception {
        this.examId = examId;
        this.question = question;
        this.choiceList = choicesList;
        return this;
    }

    /**
     * @param rightAnswerTip 回答正确后的提示
     */
    public PolyvCustomQuestionBuilder rightAnswerTip(String rightAnswerTip) {
        this.rightAnswerTip = rightAnswerTip;
        return this;
    }

    /**
     * @param wrongAnswerTip 回答错误后的提示
     */
    public PolyvCustomQuestionBuilder wrongAnswerTip(String wrongAnswerTip) {
        this.wrongAnswerTip = wrongAnswerTip;
        return this;
    }

    /**
     * @param wrongTime 错误后回退第几秒，-1表示不会退
     */
    public PolyvCustomQuestionBuilder wrongTime(int wrongTime) {
        this.wrongTime = wrongTime;
        return this;
    }

    /**
     * @param skip 能否跳过题目继续播放视频
     */
    public PolyvCustomQuestionBuilder skip(boolean skip) {
        this.skip = skip;
        return this;
    }

    /**
     * @param illustration 问答图片
     */
    public PolyvCustomQuestionBuilder illustration(String illustration) {
        if (illustration == null) {
            return this;
        }
        String headHttps = "https:";
        String headHttp = "http:";

        //剪裁url。
        if (illustration.startsWith(headHttps)) {
            illustration = illustration.substring(headHttps.length());
        } else if (illustration.startsWith(headHttp)) {
            illustration = illustration.substring(headHttp.length());
        }
        this.illustration = illustration;
        return this;
    }

    /**
     * 答题结果回调
     *
     * @param listener 监听器
     */
    public PolyvCustomQuestionBuilder listen(IPolyvOnQuestionAnswerTipsCustomListener listener) {
        answerView.setCustomQuestionAnswerListener(listener);
        return this;
    }


    public void showQuestion() throws Exception {
        //检查AnswerView!=null
        checkNull(answerView, "answerView");

        //检查必填参数
        checkNull(examId, "examId");
        checkNull(question, "question");
        checkNull(choiceList, "choicesList");

        PolyvQuestionVO polyvQuestionVO = new PolyvQuestionVO(examId, "", "", "", 0,
                0, -1, question, new ArrayList<>(choiceList.getChoicesList()), rightAnswerTip, skip,
                PolyvQuestionVO.TYPE_QUESTION, "", wrongTime, 1, 0,
                0, false, wrongAnswerTip, illustration);

        answerView.showCustomQuestion(polyvQuestionVO);
    }

    private void checkNull(Object mayNullObject, String paramName) throws Exception {
        if (mayNullObject == null) {
            throw new Exception(paramName + "不可为Null");
        }
        if (mayNullObject instanceof String) {
            String mayBlank = (String) mayNullObject;
            if (mayBlank.length() == 0) {
                throw new Exception(paramName + "不可为空字符");
            }
        }
    }

    public static class ChoiceList {
        private static final int MAX_CHOICE_NUM = 5;

        private ArrayList<QuestionVO.ChoicesVO> choicesList = new ArrayList<>(MAX_CHOICE_NUM);

        private boolean hasAtLeastOneRightChoice = false;

        public ChoiceList addChoice(String choiceName, boolean isRightChoice) {
            choicesList.add(new QuestionVO.ChoicesVO(choiceName, isRightChoice ? 1 : 0));
            if (isRightChoice) {
                hasAtLeastOneRightChoice = true;
            }
            return ChoiceList.this;
        }

        public ChoiceList addChoice(String choiceName) {
            return this.addChoice(choiceName, false);
        }

        List<QuestionVO.ChoicesVO> getChoicesList() throws Exception {
            if (choicesList.size() > MAX_CHOICE_NUM) {
                throw new Exception("选项数量不可超过" + MAX_CHOICE_NUM + "个");
            }
            if (!hasAtLeastOneRightChoice) {
                throw new Exception("至少得有一个正确选项");
            }
            return choicesList;
        }
    }
}
