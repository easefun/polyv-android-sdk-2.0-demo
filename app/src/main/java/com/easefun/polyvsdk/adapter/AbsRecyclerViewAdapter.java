package com.easefun.polyvsdk.adapter;

import android.content.Context;
import androidx.annotation.IdRes;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public abstract class AbsRecyclerViewAdapter extends
        RecyclerView.Adapter<AbsRecyclerViewAdapter.ClickableViewHolder> {

    private Context context;

    protected RecyclerView mRecyclerView;

    private List<RecyclerView.OnScrollListener> mListeners = new ArrayList<>();


    public AbsRecyclerViewAdapter(RecyclerView recyclerView) {

        this.mRecyclerView = recyclerView;
        this.mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView rv, int newState) {

                for (RecyclerView.OnScrollListener listener : mListeners) {
                    listener.onScrollStateChanged(rv, newState);
                }
            }


            @Override
            public void onScrolled(RecyclerView rv, int dx, int dy) {

                for (RecyclerView.OnScrollListener listener : mListeners) {
                    listener.onScrolled(rv, dx, dy);
                }
            }
        });
    }


    public void addOnScrollListener(RecyclerView.OnScrollListener listener) {

        mListeners.add(listener);
    }


    public interface OnItemClickListener {

        void onItemClick(int position, ClickableViewHolder holder);
    }

    interface OnItemLongClickListener {

        boolean onItemLongClick(int position, ClickableViewHolder holder);
    }

    private OnItemClickListener itemClickListener;

    private OnItemLongClickListener itemLongClickListener;


    public void setOnItemClickListener(OnItemClickListener listener) {

        this.itemClickListener = listener;
    }


    public void setOnItemLongClickListener(OnItemLongClickListener listener) {

        this.itemLongClickListener = listener;
    }


    public void bindContext(Context context) {

        this.context = context;
    }


    public Context getContext() {

        return this.context;
    }


    @Override
    public void onBindViewHolder(final ClickableViewHolder holder, final int position) {

        holder.getParentView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(position, holder);
                }
            }
        });
        holder.getParentView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return itemLongClickListener != null
                        && itemLongClickListener.onItemLongClick(position, holder);
            }
        });
    }


    public static class ClickableViewHolder extends RecyclerView.ViewHolder {

        private View parentView;


        public ClickableViewHolder(View itemView) {

            super(itemView);
            this.parentView = itemView;
        }


        View getParentView() {

            return parentView;
        }


        @SuppressWarnings("unchecked")
        public <T extends View> T $(@IdRes int id) {

            return (T) parentView.findViewById(id);
        }
    }
}