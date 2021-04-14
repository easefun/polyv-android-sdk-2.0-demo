package com.easefun.polyvsdk.player.fillblank;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easefun.polyvsdk.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static android.text.InputType.TYPE_CLASS_TEXT;

/**
 * 填空题
 * Created by yangle on 2017/9/2.
 */

public class FillBlankView extends RelativeLayout {
    private static final String TAG = "FillBlankView";
    private TextView tvContent;
    private ObservableScrollView scrollView;
    private Context context;
    // 答案集合
    private LinkedList<String> rightAnswerList;
    // 答案范围集合
    private List<AnswerRange> rangeList = new ArrayList<>();
    //答案最小长度
    private List<Integer> rangeMinWidth = new ArrayList<>();
    //编辑框集合
    private List<EditText> editTexts = new ArrayList<>();
    // 填空题内容
    private SpannableStringBuilder content;

    private boolean isNeedToChangeEditText = true;

    private int tvContentWidth = 0;

    //单行显示的内容
    private int singlePoint = 0;

    public FillBlankView(Context context) {
        this(context, null);
    }

    public FillBlankView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FillBlankView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.layout_fill_blank, this);
        scrollView = findViewById(R.id.scrollView);
        tvContent = (TextView) findViewById(R.id.tv_content);
        scrollView.setScrollViewListener(new ScrollViewListener() {
            @Override
            public void onScrollChanged(ObservableScrollView observableScrollView, int x, int y, int oldx, int oldy) {
                Log.i(TAG, "onScrollChanged: x:" + x + " y:" + y);
                if (rangeList.size() == editTexts.size()) {
                    changeEditTextList();
                }
            }
        });
    }

    /**
     * 设置数据
     *
     * @param originContent 源数据
     */
    public void setData(SpannableStringBuilder originContent) {
        isNeedToChangeEditText = true;
        // 获取课文内容
//        content = new SpannableStringBuilder(originContent);
        addSpaceForContent(originContent);
        if (content != null && content.toString().endsWith(originContent.toString())) {
            return;
        }

        content = originContent;
        caculateAnswerRange();
        rangeMinWidth.clear();
        for (AnswerRange range : rangeList) {
            int value = range.end - range.start + 1;
            rangeMinWidth.add(value);
        }

        if (TextUtils.isEmpty(originContent) || rangeList == null
                || rangeList.isEmpty()) {
            return;
        }

        if (editTexts.size() > 0) {
            for (EditText editText : editTexts) {
                FillBlankView.this.removeView(editText);
            }
            editTexts.clear();
        }

        // 答案集合
        rightAnswerList = new LinkedList<>();
        for (int i = 0; i < rangeList.size(); i++) {
            rightAnswerList.add("");
        }

        // 设置此方法后，点击事件才能生效
        tvContent.setMovementMethod(LinkMovementMethod.getInstance());
//        tvContent.setText(new Spanned()originContent);
        tvContent.setText(content);

        tvContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (tvContentWidth != tvContent.getWidth()) {
                    isNeedToChangeEditText = true;
                }
                if (isNeedToChangeEditText) {
                    changeEditTextList();
                }
            }
        });
//        if (originContent.equals(content)) {
//            if (editTexts.size() > 0) {
//                for (int i = 0; i < editTexts.size(); i++) {
//                    resetTvContent(i, editTexts.get(i));
//                }
//            }
//        }
    }

    private void changeEditTextList() {
        //判断是否有空白处已改变
        int isNeedToChangeNum = 0;
        for (int i = 0; i < rangeList.size(); i++) {
            AnswerRange answerRange = rangeList.get(i);
            final int[] points = new int[3];
            Layout textViewLayout = tvContent.getLayout();
            calcClickPosition(textViewLayout, tvContent, points, answerRange);
            if (editTexts.size() < i + 1) {
                final EditText editText = new EditText(context);
                editTexts.add(editText);
                FillBlankView.this.addView(editText);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    editText.setBackground(null);
                }
                editText.setInputType(TYPE_CLASS_TEXT);
                editText.setSingleLine();
                editText.setTextSize(14);
                editText.setTextColor(getResources().getColorStateList(R.color.center_fill_blank_color_blue));
                editText.setSelection(editText.getText().length(), editText.getText().length());
                editText.setIncludeFontPadding(false);
                editText.setPadding(0, 0, 0, 0);
                editText.setLineSpacing(0f, 0f);
                final int finalI = i;
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (singlePoint == 0)
                            singlePoint = points[2] / (rangeList.get(finalI).end - rangeList.get(finalI).start + 1);
                        isNeedToChangeEditText = resetTvContent(finalI, editText);
                    }
                });
                editText.setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
//                        scrollView.scrollTo(0,  editText.getTop());
                        return false;
                    }
                });
            }
            editTexts.get(i).setX(points[0]);
            editTexts.get(i).setY(points[1]);
            if (editTexts.get(i).getWidth() != tvContent.getWidth()) {

                if (singlePoint == 0)
                    singlePoint = points[2] / (rangeList.get(i).end - rangeList.get(i).start + 1);
                editTexts.get(i).setWidth(tvContent.getWidth()-singlePoint);
                editTexts.get(i).setFilters(new InputFilter[]{new InputFilter.LengthFilter(200)});
                boolean isNeedToChange = resetTvContent(i, editTexts.get(i));
                if (isNeedToChange) {
                    isNeedToChangeNum++;
                }
            }
        }
        if (isNeedToChangeNum > 0) {
            isNeedToChangeEditText = true;
        }
        tvContentWidth = tvContent.getWidth();
    }

    /**
     * 重新调整题目的下划线长度
     * @param index
     * @param editText
     * @return
     */
    private boolean resetTvContent(int index, EditText editText) {
        boolean isNeedToChange = false;
        Editable s = editText.getText();
        AnswerRange answerRange = rangeList.get(index);
        int[] newPoints = new int[3];
        Layout textViewLayout = tvContent.getLayout();
        calcClickPosition(textViewLayout, tvContent, newPoints, answerRange);

        Layout layout = editText.getLayout();
        if (layout == null) {
            return false;
        }
        double pointStart = layout.getPrimaryHorizontal(0);
        double pointEnd = layout.getPrimaryHorizontal(s.length());
        editText.setMovementMethod(ScrollingMovementMethod.getInstance());
        editText.setSelection(s.length());

        int needPoint = (int) ((pointEnd - pointStart) / singlePoint);
        if ((pointEnd - pointStart) % singlePoint > 0) {
            needPoint++;
        }
        if (needPoint > tvContent.getWidth() / singlePoint) {
            needPoint = tvContent.getWidth() / singlePoint;
        }

        if (needPoint > answerRange.end - answerRange.start + 1) {
            int addCount = needPoint - (answerRange.end - answerRange.start + 1);
            for (int i = 0; i < addCount; i++) {
                content.insert(answerRange.end, "_");
                answerRange.end++;
            }
            isNeedToChange = true;

        } else if (needPoint < answerRange.end - answerRange.start + 1) {
            if (needPoint <= rangeMinWidth.get(index)) {
                if (answerRange.end - answerRange.start + 1 > rangeMinWidth.get(index)) {
                    int deleteCount = answerRange.end + 1 - (answerRange.start + rangeMinWidth.get(index));
                    content.delete(answerRange.start, answerRange.start + deleteCount);
                    answerRange.end = answerRange.start + rangeMinWidth.get(index) - 1;
                    isNeedToChange = true;
                } else {
                    isNeedToChange = false;
                }
            } else if (needPoint > rangeMinWidth.get(index)) {
                int deleteCount = (answerRange.end - answerRange.start + 1) - needPoint;
                content.delete(answerRange.start, answerRange.start + deleteCount);
                answerRange.end = answerRange.end - deleteCount;
                isNeedToChange = true;
            }
        } else {
            isNeedToChange = false;
        }
        caculateAnswerRange();
        tvContent.setText(content);
        return isNeedToChange;
    }

    /**
     * 给每个下划线前后添加空格
     */
    public void addSpaceForContent(SpannableStringBuilder originContent){
        int i =0;
        int start = -1;
        int end = -1;
        while (i<originContent.length()) {
            if (originContent.charAt(i)!='_') {
                if (start!=-1 && end!=-1) {
                    if (end - start >= 2) {
                        originContent.insert(start, "\r");
                        originContent.insert(i+1, "\r");
                    }
                }
                start = -1;
                end = -1;
            } else {
                if (start == -1) {
                    start = i;
                } else {
                    end = i;
                }
            }
        i++;
        }
    }
    /**
     * 重新计算下划线所在的位置
     */
    public void caculateAnswerRange() {
        rangeList.clear();
        int i = 0;
        int start = -1;
        int end = -1;
        while (i < content.length()) {
            if (content.charAt(i) != '_') {
                if (start != -1 && end != -1) {
                    if (end - start >= 2) {
                        if (rangeList.size() < 5) {
                            rangeList.add(new AnswerRange(start, end));
                        }
                    }
                    start = -1;
                    end = -1;
                }
            } else {
                if (start == -1) {
                    start = i;
                } else {
                    end = i;
                }
            }
            if (i == content.length() - 1 && content.charAt(i) == '_') {
                if (start != -1 && end != -1) {
                    if (end - start >= 2) {
                        if (rangeList.size() < 5)
                            rangeList.add(new AnswerRange(start, end));
                    }
                    start = -1;
                    end = -1;
                }
            }
            i++;
        }
    }

    /**
     * 获取答案列表
     *
     * @return 答案列表
     */
    public LinkedList<String> getRightAnswerList() {
        rightAnswerList.clear();
        for (EditText editText : editTexts) {
            String text = editText.getText().toString();
            if (!TextUtils.isEmpty(text)) {
                rightAnswerList.add(editText.getText().toString());
            }
        }
        return rightAnswerList;
    }

    /**
     * dp转px
     *
     * @param dp dp值
     * @return px值
     */
    private int dp2px(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    // 计算点击的单词的坐标，首先计算单词在整个短文中的起始位置，即可获取到该单词的x偏移。通过起始位置
    // 还可以获取该单词所在的行索引。进而可以获取该行所在的矩形区域。即可获取到该单词的y偏移。在计算
    // padding和scroll的影响，最终可以获得点击位置的屏幕坐标
    private void calcClickPosition(Layout textViewLayout, View widget, int[] pos, AnswerRange answerRange) {
        TextView textView = (TextView) widget;
        Rect parentTextViewRect = new Rect();

        int start = answerRange.start;
        int end = answerRange.end + 1;
        // 获取点击单词所在的行数
        int lineOffset = textViewLayout.getLineForOffset(start);
        // 获取点击单词所在的x坐标
        double pointStart = textViewLayout.getPrimaryHorizontal(start);
        double pointEnd = textViewLayout.getPrimaryHorizontal(end);

        // 获取点击单词所在一行的矩形
        textViewLayout.getLineBounds(lineOffset, parentTextViewRect);
        int[] parentTextViewLocation = {0, 0};
        // 获取TextView左上角的坐标

        textView.getLocationOnScreen(parentTextViewLocation);
        // 加入scroll和padding的偏移的计算
        parentTextViewRect.top += textView.getY() + textView.getCompoundPaddingTop() - scrollView.getScrollY();
        parentTextViewRect.left += textView.getX() + pointStart + textView.getCompoundPaddingLeft() - scrollView.getScrollX();
        pos[0] = parentTextViewRect.left;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            pos[1] = (parentTextViewRect.top);
        }

        pos[2] = (int) (pointEnd - pointStart);

    }
}