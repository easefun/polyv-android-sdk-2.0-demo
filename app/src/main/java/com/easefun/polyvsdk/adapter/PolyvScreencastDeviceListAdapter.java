package com.easefun.polyvsdk.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.apowersoft.dlnasender.api.bean.DeviceInfo;
import com.easefun.polyvsdk.R;

import java.util.ArrayList;
import java.util.List;

public class PolyvScreencastDeviceListAdapter extends AbsRecyclerViewAdapter {
    private List<DeviceInfo> mDatas;
    private OnItemClickListener mItemClickListener;
    private DeviceInfo mSelectInfo;
    private int mLayoutId;

    public PolyvScreencastDeviceListAdapter(RecyclerView recyclerView, int layoutId) {
        super(recyclerView);
        mDatas = new ArrayList<>();
        mLayoutId = layoutId;
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        this.mItemClickListener = l;
    }

    public void updateDatas(List<DeviceInfo> infos) {
        if (null != infos) {
            mDatas.clear();
            mDatas.addAll(infos);
            notifyDataSetChanged();
        }
    }

    public DeviceInfo getSelectInfo() {
        return mSelectInfo;
    }

    public void setSelectInfo(DeviceInfo selectInfo) {
        mSelectInfo = selectInfo;
    }

    private View.OnClickListener mOnItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag(R.id.id_position);
            DeviceInfo info = (DeviceInfo) v.getTag(R.id.id_info);
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
            DeviceInfo info = mDatas.get(position);
            if (null == info) {
                return;
            }
            String item = info.getName()/* + " isOnLine:" + info.isOnLine() + " uid:" + info.getUid() + " types:" + info.getTypes()*/;
            itemViewHolder.tv_device_name.setText(item);
            if (info == mSelectInfo ||
                    (mSelectInfo != null && info.getMediaID() != null && info.getMediaID().equals(mSelectInfo.getMediaID()))) {
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
        void onClick(int position, DeviceInfo pInfo);
    }
}
