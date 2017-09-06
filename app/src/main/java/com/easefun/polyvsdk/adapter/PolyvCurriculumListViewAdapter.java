package com.easefun.polyvsdk.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.easefun.polyvsdk.PolyvDownloader;
import com.easefun.polyvsdk.PolyvDownloaderErrorReason;
import com.easefun.polyvsdk.PolyvDownloaderManager;
import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.bean.PolyvDownloadInfo;
import com.easefun.polyvsdk.database.PolyvDownloadSQLiteHelper;
import com.easefun.polyvsdk.download.listener.IPolyvDownloaderProgressListener;
import com.easefun.polyvsdk.util.PolyvVlmsHelper;
import com.easefun.polyvsdk.vo.PolyvVideoVO;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class PolyvCurriculumListViewAdapter extends BaseAdapter {
    private List<PolyvVlmsHelper.CurriculumsDetail> lists;
    private static Context appContext;
    private LayoutInflater inflater;
    private ViewHolder viewHolder;
    private boolean isVisible;
    private SparseBooleanArray selBit1Positions;
    private SparseBooleanArray selBit2Positions;
    private SparseBooleanArray selBit3Positions;
    private DisplayImageOptions options;
    private PolyvDownloadSQLiteHelper downloadSQLiteHelper;
    private int currentSelcetBitrate;

    public PolyvCurriculumListViewAdapter(List<PolyvVlmsHelper.CurriculumsDetail> lists, Context context) {
        // lists所指向的对象更新时，这里也会更新
        this.lists = lists;
        this.appContext = context.getApplicationContext();
        this.inflater = LayoutInflater.from(context);
        this.selBit1Positions = new SparseBooleanArray();
        this.selBit2Positions = new SparseBooleanArray();
        this.selBit3Positions = new SparseBooleanArray();
        this.downloadSQLiteHelper = PolyvDownloadSQLiteHelper.getInstance(context);
        this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.polyv_demo) // resource
                // or
                // drawable
                .showImageForEmptyUri(R.drawable.polyv_demo) // resource or drawable
                .showImageOnFail(R.drawable.polyv_demo) // resource or drawable
                .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true).cacheOnDisk(true).build();
    }

    public void cancelSideIconSelected() {
        for (int i = 0; i < lists.size(); i++) {
            for (int j = 1; j <= 3; j++) {
                PolyvDownloadInfo downloadInfo = generateDownloadInfo(i, j);
                if (!downloadSQLiteHelper.isAdd(downloadInfo))
                    putSideIconStatus(j, i, false);
            }
        }
    }

    public void putCurBitSideIconSelected(int bitrate) {
        for (int i = 0; i < lists.size(); i++)
            putSideIconStatus(bitrate, i, true);
    }

    public void putSideIconStatus(int bitrate, int position, boolean isSelected) {
        PolyvDownloadInfo downloadInfo = generateDownloadInfo(position, 3);
        // 如果可以下载的码率<=选择下载的码率
        if (downloadInfo.getBitrate() <= bitrate)
            mulPutSideIconStatus(3, position, isSelected);
        else
            mulPutSideIconStatus(bitrate, position, isSelected);
    }

    //设置侧边图标的选择状态
    private void mulPutSideIconStatus(int bitrate, int position, boolean isSelected) {
        SparseBooleanArray temp = null;
        switch (bitrate) {
            case 1:
                temp = selBit1Positions;
                selBit1Positions.put(position, isSelected);
                break;
            case 2:
                temp = selBit2Positions;
                selBit2Positions.put(position, isSelected);
                break;
            case 3:
                temp = selBit3Positions;
                selBit3Positions.put(position, isSelected);
                break;
        }
        PolyvDownloadInfo downloadInfo = generateDownloadInfo(position, bitrate);
        if (bitrate != downloadInfo.getBitrate()) {
            // 如果可以下载的码率与当前选择的码率不同，那么把当前item的状态同步到那个item
            mulPutSideIconStatus(downloadInfo.getBitrate(), position, isSelected);
        }
        // 遍历集合，把当前bit及vid相同的项设置为相同的状态
        for (int i = 0; i < lists.size(); i++) {
            if (lists.get(i).lecture.vid.equals(downloadInfo.getVid()))
                temp.put(i, isSelected);
        }
        notifyDataSetChanged();
    }

    public boolean isHasSelected(boolean exceptIsAdded) {
        for (int i = 0; i < lists.size(); i++) {
            for (int j = 1; j <= 3; j++)
                if (getSideIconStatus(j, i, exceptIsAdded))
                    return true;
        }
        return false;
    }

    //获取侧边图标是否是选择状态
    public boolean getSideIconStatus(int bitrate, int position, boolean exceptIsAdded) {
        boolean isSelected = false;
        switch (bitrate) {
            case 1:
                isSelected = selBit1Positions.get(position, false);
                break;
            case 2:
                isSelected = selBit2Positions.get(position, false);
                break;
            case 3:
                isSelected = selBit3Positions.get(position, false);
                break;
        }
        PolyvDownloadInfo downloadInfo = generateDownloadInfo(position, bitrate);
        boolean isAdded = downloadSQLiteHelper.isAdd(downloadInfo);
        return exceptIsAdded ? (isAdded ? false : isSelected) : isSelected;
    }

    //初始化选择状态
    public void initSelect(int bitrate) {
        currentSelcetBitrate = bitrate;
        for (int i = 0; i < lists.size(); i++) {
            if (downloadSQLiteHelper.isAdd(generateDownloadInfo(i, bitrate)) || getSideIconStatus(bitrate, i, false)) {
                putSideIconStatus(bitrate, i, true);
            } else {
                putSideIconStatus(bitrate, i, false);
            }
        }
        notifyDataSetChanged();
    }

    // 设置侧边图标是否可见
    public void setSideIconVisible(boolean isVisible) {
        this.isVisible = isVisible;
        notifyDataSetChanged();
    }

    // 获取侧边图标是否可见
    public boolean getSideIconVisible() {
        return isVisible;
    }

    //生成下载信息对象
    private PolyvDownloadInfo generateDownloadInfo(int position, int bitrate) {
        PolyvVlmsHelper.CurriculumsDetail curriculum = lists.get(position);
        PolyvVideoVO videoVO = curriculum.videoVO;
        long filesize = -1;
        // 获取可以下载码率的文件大小
        while (filesize <= 0 && bitrate > 0)
            filesize = videoVO.getFileSizeMatchVideoType(bitrate--);
        return new PolyvDownloadInfo(videoVO.getVid(), videoVO.getDuration(), filesize, bitrate + 1, curriculum.lecture.title);
    }

    private static class MyDownloadListener implements IPolyvDownloaderProgressListener {
        private PolyvDownloadInfo downloadInfo;
        private long total;

        public MyDownloadListener(PolyvDownloadInfo downloadInfo) {
            this.downloadInfo = downloadInfo;
        }

        @Override
        public void onDownload(long current, long total) {
            this.total = total;
        }

        @Override
        public void onDownloadSuccess() {
            if (total == 0)
                total = 1;
            PolyvDownloadSQLiteHelper.getInstance(appContext).update(downloadInfo, total, total);
        }

        @Override
        public void onDownloadFail(@NonNull PolyvDownloaderErrorReason errorReason) {
        }
    }

    public void downloadSelected() {
        for (int i = 0; i < lists.size(); i++) {
            for (int j = 1; j <= 3; j++) {
                boolean isSelected = getSideIconStatus(j, i, false);
                final PolyvDownloadInfo downloadInfo = generateDownloadInfo(i, j);
                if (isSelected && !downloadSQLiteHelper.isAdd(downloadInfo)) {
                    downloadSQLiteHelper.insert(downloadInfo);
                    PolyvDownloader downloader = PolyvDownloaderManager.getPolyvDownloader(downloadInfo.getVid(), downloadInfo.getBitrate());
                    downloader.setPolyvDownloadProressListener(new MyDownloadListener(downloadInfo));
                    downloader.start();
                }
            }
        }
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.polyv_listview_cur_item, null);
            viewHolder = new ViewHolder();
            viewHolder.iv_sel = (ImageView) convertView.findViewById(R.id.iv_sel);
            viewHolder.iv_demo = (ImageView) convertView.findViewById(R.id.iv_demo);
            viewHolder.tv_seri = (TextView) convertView.findViewById(R.id.tv_seri);
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            viewHolder.tv_added = (TextView) convertView.findViewById(R.id.tv_added);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (isVisible)
            viewHolder.iv_sel.setVisibility(View.VISIBLE);
        else
            viewHolder.iv_sel.setVisibility(View.GONE);
        if (!getSideIconStatus(currentSelcetBitrate, position, false))
            viewHolder.iv_sel.setSelected(false);
        else
            viewHolder.iv_sel.setSelected(true);
        if (isVisible && downloadSQLiteHelper.isAdd(generateDownloadInfo(position, currentSelcetBitrate)))
            viewHolder.tv_added.setVisibility(View.VISIBLE);
        else
            viewHolder.tv_added.setVisibility(View.GONE);
        PolyvVlmsHelper.CurriculumsDetail polyvCurriculum = lists.get(position);
        viewHolder.tv_seri.setText(polyvCurriculum.section_name + " " + polyvCurriculum.name);
        viewHolder.tv_title.setText(polyvCurriculum.lecture.title);
        viewHolder.tv_time.setText(polyvCurriculum.lecture.duration);
        ImageLoader.getInstance().displayImage(polyvCurriculum.cover_image, viewHolder.iv_demo, options);
        return convertView;
    }

    private class ViewHolder {
        ImageView iv_sel, iv_demo;
        TextView tv_seri, tv_title, tv_time, tv_added;
    }
}
