package com.easefun.polyvsdk.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.vo.PolyvQuestionChoicesVO;

import java.util.List;

public class PolyvAnswerAdapter extends RecyclerView.Adapter<PolyvAnswerAdapter.ViewHolder> {
    private List<PolyvQuestionChoicesVO> questionChoicesVOs;
    private Context context;
    private char a='A';
    private boolean isMultiSelect;
    private View lastSelectedView;

    public PolyvAnswerAdapter(List<PolyvQuestionChoicesVO> questionChoicesVOs, Context context, boolean isMultiSelect) {
        this.questionChoicesVOs = questionChoicesVOs;
        this.context = context;
        this.isMultiSelect = isMultiSelect;
    }

    @Override
    public PolyvAnswerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View child = View.inflate(context, R.layout.polyv_answer_choice_item, null);
        return new ViewHolder(child);
    }

    @Override
    public void onBindViewHolder(PolyvAnswerAdapter.ViewHolder viewHolder, final int position) {

        viewHolder.check.setSelected(questionChoicesVOs.get(position).isSelected());
        viewHolder.content.setText((char)(a+position) +"."+questionChoicesVOs.get(position).getAnswer());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isMultiSelect && lastSelectedView != null) {
                    lastSelectedView.setSelected(false);
                    int lastPos = (int) lastSelectedView.getTag();
                    PolyvQuestionChoicesVO polyvQuestionChoicesVO = getItemData(lastPos);
                    if (polyvQuestionChoicesVO != null) {
                        polyvQuestionChoicesVO.setSelected(false);
                    }
                    if(answerSelectCallback != null){
                        answerSelectCallback.onSelectAnswer(lastPos,lastSelectedView.isSelected());
                    }
                }

                v.setSelected(!v.isSelected());
                lastSelectedView = v;

                if(answerSelectCallback != null){
                    answerSelectCallback.onSelectAnswer(position,v.isSelected());
                }

                PolyvQuestionChoicesVO polyvQuestionChoicesVO = getItemData(position);
                if (polyvQuestionChoicesVO != null) {
                    polyvQuestionChoicesVO.setSelected(v.isSelected());
                }
            }
        });
        viewHolder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        if (questionChoicesVOs == null) {
            return 0;
        }
        return questionChoicesVOs.size();
    }

    private PolyvQuestionChoicesVO getItemData(int pos) {
        if (questionChoicesVOs != null && (questionChoicesVOs.size() - 1 >= pos)) {
            return questionChoicesVOs.get(pos);
        }
        return null;
    }

    public void updateDatas(List<PolyvQuestionChoicesVO> polyvQuestionChoicesVOS){
        this.questionChoicesVOs = polyvQuestionChoicesVOS;
    }
    public void setAnswerSelectCallback(AnswerSelectCallback answerSelectCallback) {
        this.answerSelectCallback = answerSelectCallback;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView content;
        public ImageView check;

        public ViewHolder(View itemView) {
            super(itemView);
            content = (TextView) itemView.findViewById(R.id.answer_content);
            check = (ImageView) itemView.findViewById(R.id.answer_check);
        }
    }

    private AnswerSelectCallback answerSelectCallback;

    public interface AnswerSelectCallback {
        void onSelectAnswer(Integer pos, boolean isSelected);
    }
}
