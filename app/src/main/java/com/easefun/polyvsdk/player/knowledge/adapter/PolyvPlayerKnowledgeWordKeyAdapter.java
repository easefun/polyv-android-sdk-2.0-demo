package com.easefun.polyvsdk.player.knowledge.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.easefun.polyvsdk.player.knowledge.vo.PolyvPlayerKnowledgeVO;
import com.easefun.polyvsdk.player.knowledge.widget.PolyvPlayerKnowledgeWordKeyView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author suhongtao
 */
public class PolyvPlayerKnowledgeWordKeyAdapter extends RecyclerView.Adapter<PolyvPlayerKnowledgeWordKeyAdapter.WordKeyViewHolder> {

    private List<PolyvPlayerKnowledgeVO.WordType.WordKey> wordKeyList = new ArrayList<>();

    private PolyvPlayerKnowledgeVO.WordType.WordKey selectedWordKey;

    private OnItemClickListener onItemClickListener;

    public PolyvPlayerKnowledgeWordKeyAdapter() {

    }

    @NonNull
    @Override
    public WordKeyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new WordKeyViewHolder(new PolyvPlayerKnowledgeWordKeyView(viewGroup.getContext()));
    }

    @Override
    public void onBindViewHolder(@NonNull WordKeyViewHolder wordKeyViewHolder, int i) {
        final PolyvPlayerKnowledgeVO.WordType.WordKey wordKey = wordKeyList.get(i);
        wordKeyViewHolder.bind(wordKey);
        wordKeyViewHolder.wordKeyView.setSelected(wordKey.equals(selectedWordKey));
        wordKeyViewHolder.wordKeyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedWordKey = wordKey;
                if (onItemClickListener != null) {
                    onItemClickListener.onClick(wordKey);
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return wordKeyList.size();
    }

    public void setWordKeyList(List<PolyvPlayerKnowledgeVO.WordType.WordKey> list) {
        this.wordKeyList.clear();
        if (list != null) {
            this.wordKeyList.addAll(list);
        }
        notifyDataSetChanged();
    }

    public void setSelectedWordKey(PolyvPlayerKnowledgeVO.WordType.WordKey wordKey) {
        this.selectedWordKey = wordKey;
    }

    public PolyvPlayerKnowledgeVO.WordType.WordKey getSelectedWordKey() {
        return selectedWordKey;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public static class WordKeyViewHolder extends RecyclerView.ViewHolder {

        private PolyvPlayerKnowledgeWordKeyView wordKeyView;

        public WordKeyViewHolder(PolyvPlayerKnowledgeWordKeyView itemView) {
            super(itemView);
            this.wordKeyView = itemView;
        }

        public void bind(PolyvPlayerKnowledgeVO.WordType.WordKey wordKey) {
            List<PolyvPlayerKnowledgeVO.WordType.WordKey.KnowledgePoint> points = wordKey.getKnowledgePoints();
            final int size = points == null ? 0 : points.size();
            wordKeyView.setWordKey(wordKey.getName());
            wordKeyView.setKnowledgePointCount(size);
        }
    }

    public interface OnItemClickListener {
        void onClick(PolyvPlayerKnowledgeVO.WordType.WordKey wordKey);
    }

}
