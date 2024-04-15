package com.easefun.polyvsdk.player.knowledge.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.easefun.polyvsdk.player.knowledge.vo.PolyvPlayerKnowledgeVO;
import com.easefun.polyvsdk.player.knowledge.widget.PolyvPlayerKnowledgePointView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author suhongtao
 */
public class PolyvPlayerKnowledgePointAdapter extends RecyclerView.Adapter<PolyvPlayerKnowledgePointAdapter.KnowledgePointViewHolder> {

    private List<PolyvPlayerKnowledgeVO.WordType.WordKey.KnowledgePoint> knowledgePoints = new ArrayList<>();

    private PolyvPlayerKnowledgeVO.WordType.WordKey.KnowledgePoint selectedKnowledgePoint;
    private boolean showKnowledgePointDescription;

    private OnItemClickListener onItemClickListener;

    public PolyvPlayerKnowledgePointAdapter() {

    }

    @NonNull
    @Override
    public KnowledgePointViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new KnowledgePointViewHolder(new PolyvPlayerKnowledgePointView(viewGroup.getContext()));
    }

    @Override
    public void onBindViewHolder(@NonNull KnowledgePointViewHolder knowledgePointViewHolder, int i) {
        final PolyvPlayerKnowledgeVO.WordType.WordKey.KnowledgePoint knowledgePoint = knowledgePoints.get(i);
        knowledgePointViewHolder.bind(knowledgePoint);
        knowledgePointViewHolder.knowledgePointView.showDescription(showKnowledgePointDescription);
        knowledgePointViewHolder.knowledgePointView.setSelected(knowledgePoint.equals(selectedKnowledgePoint));
        knowledgePointViewHolder.knowledgePointView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedKnowledgePoint = knowledgePoint;
                if (onItemClickListener != null) {
                    onItemClickListener.onClick(knowledgePoint);
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return knowledgePoints.size();
    }

    public void setKnowledgePoints(List<PolyvPlayerKnowledgeVO.WordType.WordKey.KnowledgePoint> list) {
        this.knowledgePoints.clear();
        if (list != null) {
            knowledgePoints.addAll(list);
        }
        notifyDataSetChanged();
    }

    public void setSelectedKnowledgePoint(PolyvPlayerKnowledgeVO.WordType.WordKey.KnowledgePoint selectedKnowledgePoint) {
        this.selectedKnowledgePoint = selectedKnowledgePoint;
    }

    public void setShowKnowledgePointDescription(boolean show) {
        this.showKnowledgePointDescription = show;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public static class KnowledgePointViewHolder extends RecyclerView.ViewHolder {

        private PolyvPlayerKnowledgePointView knowledgePointView;

        public KnowledgePointViewHolder(PolyvPlayerKnowledgePointView itemView) {
            super(itemView);
            this.knowledgePointView = itemView;
        }

        public void bind(PolyvPlayerKnowledgeVO.WordType.WordKey.KnowledgePoint knowledgePoint) {
            knowledgePointView.setDescription(knowledgePoint.getName());
            knowledgePointView.setTime(knowledgePoint.getTime());
        }
    }

    public interface OnItemClickListener {
        void onClick(PolyvPlayerKnowledgeVO.WordType.WordKey.KnowledgePoint knowledgePoint);
    }

}
