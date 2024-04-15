package com.easefun.polyvsdk.player.knowledge.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.easefun.polyvsdk.player.knowledge.vo.PolyvPlayerKnowledgeVO;
import com.easefun.polyvsdk.player.knowledge.widget.PolyvPlayerKnowledgeWordTypeView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author suhongtao
 */
public class PolyvPlayerKnowledgeWordTypeAdapter extends RecyclerView.Adapter<PolyvPlayerKnowledgeWordTypeAdapter.WordTypeViewHolder> {

    private List<PolyvPlayerKnowledgeVO.WordType> wordTypeList = new ArrayList<>();

    private PolyvPlayerKnowledgeVO.WordType selectedWordType;

    private OnItemClickListener onItemClickListener;

    @NonNull
    @Override
    public WordTypeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new WordTypeViewHolder(new PolyvPlayerKnowledgeWordTypeView(viewGroup.getContext()));
    }

    @Override
    public void onBindViewHolder(@NonNull WordTypeViewHolder wordTypeViewHolder, int i) {
        final PolyvPlayerKnowledgeVO.WordType wordType = wordTypeList.get(i);
        wordTypeViewHolder.bind(wordType);
        wordTypeViewHolder.wordTypeView.setSelected(wordType.equals(selectedWordType));
        wordTypeViewHolder.wordTypeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedWordType = wordType;
                if (onItemClickListener != null) {
                    onItemClickListener.onClick(wordType);
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return wordTypeList.size();
    }

    public void setWordTypeList(List<PolyvPlayerKnowledgeVO.WordType> wordTypes) {
        this.wordTypeList.clear();
        if (wordTypes != null) {
            wordTypeList.addAll(wordTypes);
        }
        notifyDataSetChanged();
    }

    public void setSelectedWordType(PolyvPlayerKnowledgeVO.WordType wordType) {
        this.selectedWordType = wordType;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public static class WordTypeViewHolder extends RecyclerView.ViewHolder {

        private PolyvPlayerKnowledgeWordTypeView wordTypeView;

        public WordTypeViewHolder(PolyvPlayerKnowledgeWordTypeView itemView) {
            super(itemView);
            this.wordTypeView = itemView;
        }

        public void bind(PolyvPlayerKnowledgeVO.WordType wordType) {
            wordTypeView.setWordTypeName(wordType.getName());
        }

    }

    public interface OnItemClickListener {
        void onClick(PolyvPlayerKnowledgeVO.WordType wordType);
    }

}
