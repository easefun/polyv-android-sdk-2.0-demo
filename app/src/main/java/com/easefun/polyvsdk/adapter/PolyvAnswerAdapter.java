package com.easefun.polyvsdk.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.vo.PolyvQuestionChoicesVO;

import java.util.List;

/**
 * 问答选项适配器
 */
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
        viewHolder.check.setImageResource(isMultiSelect ? R.drawable.polyv_answer_choice_selector_multi : R.drawable.polyv_answer_choice_selector);
        viewHolder.check.setSelected(questionChoicesVOs.get(position).isSelected()); //切换横竖屏时，还保留选中
        viewHolder.content.setText((char)(a+position) +"."+questionChoicesVOs.get(position).getAnswer());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected());
                getItemData(position).setSelected(v.isSelected());
                if (answerSelectCallback != null) {
                    answerSelectCallback.onSelectAnswer(position, v.isSelected());
                }

                if (isMultiSelect) {
                    //多选，选中当前选项就可以了
                    return;
                }

                //单选，并且当前选项是选中，取消上一个选项，选中当前选项
                if (lastSelectedView != null && v.isSelected()) {
                    lastSelectedView.setSelected(false);
                    int lastPos = (int) lastSelectedView.getTag();
                    if (answerSelectCallback != null) {
                        answerSelectCallback.onSelectAnswer(lastPos, lastSelectedView.isSelected());
                    }
                }

                lastSelectedView = v;
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

    public void setAnswerSelectCallback(AnswerSelectCallback answerSelectCallback) {
        this.answerSelectCallback = answerSelectCallback;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView content;
        ImageView check;

        ViewHolder(View itemView) {
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
