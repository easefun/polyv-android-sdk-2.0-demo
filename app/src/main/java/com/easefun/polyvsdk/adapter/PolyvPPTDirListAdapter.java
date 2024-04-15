package com.easefun.polyvsdk.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.po.ppt.PolyvPptPageInfo;
import com.easefun.polyvsdk.util.PolyvImageLoader;
import com.easefun.polyvsdk.util.PolyvTimeUtils;

import java.util.List;

public class PolyvPPTDirListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<PolyvPptPageInfo> pptPageInfoList;
    private boolean isLandLayout;

    private OnItemClickListener onItemClickListener;

    public PolyvPPTDirListAdapter(List<PolyvPptPageInfo> pptPageInfoList) {
        this.pptPageInfoList = pptPageInfoList;
    }

    public void set(List<PolyvPptPageInfo> pptPageInfoList) {
        this.pptPageInfoList = pptPageInfoList;
    }

    public List<PolyvPptPageInfo> getPptPageInfoList() {
        return pptPageInfoList;
    }

    public void clear() {
        pptPageInfoList.clear();
    }

    public void setLandLayout(boolean isLandLayout) {
        this.isLandLayout = isLandLayout;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        return new PPTViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.polyv_ppt_dir_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof PPTViewHolder) {
            PolyvPptPageInfo pptPageInfo = pptPageInfoList.get(position);
            final PPTViewHolder pptViewHolder = (PPTViewHolder) holder;
            pptViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(position, pptViewHolder);
                    }
                }
            });
            PolyvImageLoader.getInstance().loadImageWithCache(context, pptPageInfo.getImg(), pptViewHolder.pptImg, R.drawable.polyv_demo);
            pptViewHolder.pptIndex.setText(position + 1 + "");
            pptViewHolder.pptTitle.setText(pptPageInfo.getTitle());
            if (isLandLayout) {
                pptViewHolder.itemView.setBackgroundResource(R.drawable.polyv_touch_bg_land);
                pptViewHolder.pptTitle.setTextColor(Color.WHITE);
            }
            pptViewHolder.pptTime.setText(PolyvTimeUtils.generateTime(pptPageInfo.getSec() * 1000, true));
        }
    }

    public static class PPTViewHolder extends RecyclerView.ViewHolder {
        private ImageView pptImg;
        private TextView pptIndex, pptTitle, pptTime;

        public PPTViewHolder(View itemView) {
            super(itemView);
            pptImg = itemView.findViewById(R.id.ppt_img);
            pptIndex = itemView.findViewById(R.id.ppt_index);
            pptTitle = itemView.findViewById(R.id.ppt_title);
            pptTime = itemView.findViewById(R.id.ppt_time);
        }
    }

    @Override
    public int getItemCount() {
        return pptPageInfoList.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, PPTViewHolder holder);
    }

    public static class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = 0;
            outRect.right = 0;
            outRect.bottom = 0;

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.top = 0;
            } else {
                outRect.top = space;
            }
        }
    }
}
