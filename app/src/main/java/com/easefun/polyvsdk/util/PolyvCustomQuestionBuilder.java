package com.easefun.polyvsdk.util;

import android.support.annotation.NonNull;

import com.easefun.polyvsdk.player.PolyvPlayerAnswerView;
import com.easefun.polyvsdk.vo.PolyvQuestionChoicesVO;
import com.easefun.polyvsdk.vo.PolyvQuestionVO;

import java.security.InvalidParameterException;
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
    private int showTime = 0;

    private PolyvCustomQuestionBuilder(@NonNull PolyvPlayerAnswerView answerView) {
        this.answerView = answerView;
    }

    public static PolyvCustomQuestionBuilder create(@NonNull PolyvPlayerAnswerView answerView) {
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
     * showTime 取值范围： 0~视频时长。
     * 小于0或大于视频时长时不显示。
     * 如果showtime小于当前播放的时间点，则直接弹出题目。比如播放到50s时去调用接口，而showtime = 40 时，直接弹出题目。
     * @param showTime 预先设置题目显示的时间点，默认值0
     */
    public PolyvCustomQuestionBuilder showTime(int showTime) {
        this.showTime = showTime;
        return this;
    }

    /**
     * 设置问答图片
     * @param illustration 问答图片
     */
    public PolyvCustomQuestionBuilder illustration(String illustration) {
        this.illustration = illustration;
        return this;
    }

    /**
     * 答题结果回调
     *
     * @param listener 监听器
     */
    public PolyvCustomQuestionBuilder listen(IPolyvOnCustomQuestionAnswerResultListener listener) {
        this.answerView.setCustomQuestionAnswerResultListener(listener);
        return this;
    }

    /**
     * 显示问答
     */
    public void showQuestion() throws InvalidParameterException {
        //检查AnswerView!=null
        checkNull(answerView, "answerView");

        //检查必填参数
        checkNull(examId, "examId");
        checkNull(question, "question");
        checkNull(choiceList, "choicesList");

        PolyvQuestionVO polyvQuestionVO = new PolyvQuestionVO(examId,
                question,
                choiceList.getChoicesList(),
                rightAnswerTip,
                skip,
                PolyvQuestionVO.TYPE_QUESTION,
                wrongTime,
                wrongAnswerTip,
                illustration);
        polyvQuestionVO.setShowTime(showTime);
        answerView.insertCustomQuestion(polyvQuestionVO);
    }

    public PolyvQuestionVO toPolyvQuestionVO() {
        PolyvQuestionVO polyvQuestionVO = new PolyvQuestionVO(examId,
                question,
                choiceList.getChoicesList(),
                rightAnswerTip,
                skip,
                PolyvQuestionVO.TYPE_QUESTION,
                wrongTime,
                wrongAnswerTip,
                illustration);
        polyvQuestionVO.setShowTime(showTime);
        return polyvQuestionVO;
    }

    private void checkNull(Object mayNullObject, String paramName) throws InvalidParameterException {
        if (mayNullObject == null) {
            throw new InvalidParameterException(paramName + "不可为Null");
        }
        if (mayNullObject instanceof String) {
            String mayBlank = (String) mayNullObject;
            if (mayBlank.length() == 0) {
                throw new InvalidParameterException(paramName + "不可为空字符");
            }
        }
    }

    public static class ChoiceList {
        private static final int MAX_CHOICE_NUM = 5;

        private ArrayList<PolyvQuestionChoicesVO> choicesList = new ArrayList<>(MAX_CHOICE_NUM);

        private boolean hasAtLeastOneRightChoice = false;

        public ChoiceList addChoice(String choiceName, boolean isRightChoice) {
            choicesList.add(new PolyvQuestionChoicesVO(choiceName, isRightChoice));
            if (isRightChoice) {
                hasAtLeastOneRightChoice = true;
            }
            return ChoiceList.this;
        }

        public ChoiceList addChoice(String choiceName) {
            return this.addChoice(choiceName, false);
        }

        List<PolyvQuestionChoicesVO> getChoicesList() throws InvalidParameterException {
            if (choicesList.size() > MAX_CHOICE_NUM) {
                throw new InvalidParameterException("选项数量不可超过" + MAX_CHOICE_NUM + "个");
            }
            if (!hasAtLeastOneRightChoice) {
                throw new InvalidParameterException("至少得有一个正确选项");
            }
            return choicesList;
        }
    }

    /**
     * 自定义问答的答题结果的回调
     */
    public interface IPolyvOnCustomQuestionAnswerResultListener {

        /**
         * 问答结果回调
         * @param polyvQuestionVO 数据实体
         */
        void onAnswerResult(PolyvQuestionVO polyvQuestionVO);
    }
}
