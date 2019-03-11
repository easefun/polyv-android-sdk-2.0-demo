package com.easefun.polyvsdk.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easefun.polyvsdk.R;
import com.hpplay.sdk.source.browse.api.LelinkServiceInfo;

import java.util.ArrayList;
import java.util.List;

public class PolyvScreencastDeviceListAdapter extends AbsRecyclerViewAdapter {
    private List<LelinkServiceInfo> mDatas;
    private OnItemClickListener mItemClickListener;
    private LelinkServiceInfo mSelectInfo;
    private int mLayoutId;

    public PolyvScreencastDeviceListAdapter(RecyclerView recyclerView, int layoutId) {
        super(recyclerView);
        mDatas = new ArrayList<>();
        mLayoutId = layoutId;
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        this.mItemClickListener = l;
    }

    public void updateDatas(List<LelinkServiceInfo> infos) {
        if (null != infos) {
            mDatas.clear();
            mDatas.addAll(infos);
            notifyDataSetChanged();
        }
    }

    public LelinkServiceInfo getSelectInfo() {
        return mSelectInfo;
    }

    public void setSelectInfo(LelinkServiceInfo selectInfo) {
        mSelectInfo = selectInfo;
    }

    private View.OnClickListener mOnItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag(R.id.id_position);
            LelinkServiceInfo info = (LelinkServiceInfo) v.getTag(R.id.id_info);
            if (null != mItemClickListener) {
                mItemClickListener.onClick(position, info);
            }
        }
    };

    @Override
    public ClickableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        bindContext(parent.getContext());
        return new ItemViewHolder(LayoutInflater.from(getContext()).
                inflate(mLayoutId, parent, false));
    }

    @Override
    public void onBindViewHolder(ClickableViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            LelinkServiceInfo info = mDatas.get(position);
            if (null == info) {
                return;
            }
            String item = info.getName()/* + " isOnLine:" + info.isOnLine() + " uid:" + info.getUid() + " types:" + info.getTypes()*/;
            itemViewHolder.tv_device_name.setText(item);
            if (info == mSelectInfo ||
                    (mSelectInfo != null && info.getUid() != null && info.getUid().equals(mSelectInfo.getUid()))) {
                itemViewHolder.tv_device_name.setSelected(true);
            } else {
                itemViewHolder.tv_device_name.setSelected(false);
            }
            itemViewHolder.tv_device_name.setTag(R.id.id_position, position);
            itemViewHolder.tv_device_name.setTag(R.id.id_info, info);
            itemViewHolder.tv_device_name.setOnClickListener(mOnItemClickListener);
        }
    }

    @Override
    public int getItemCount() {
        return null == mDatas ? 0 : mDatas.size();
    }

    public class ItemViewHolder extends AbsRecyclerViewAdapter.ClickableViewHolder {
        TextView tv_device_name;

        public ItemViewHolder(View itemView) {
            super(itemView);
            tv_device_name = $(R.id.tv_device_name);
        }
    }

    public interface OnItemClickListener {
        void onClick(int position, LelinkServiceInfo pInfo);
    }
}
