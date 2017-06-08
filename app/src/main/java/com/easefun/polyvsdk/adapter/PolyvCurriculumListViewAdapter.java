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
    private SparseBooleanArray selPositions;
    private DisplayImageOptions options;
    private PolyvDownloadSQLiteHelper downloadSQLiteHelper;

    public PolyvCurriculumListViewAdapter(List<PolyvVlmsHelper.CurriculumsDetail> lists, Context context) {
        // lists所指向的对象更新时，这里也会更新
        this.lists = lists;
        this.appContext = context.getApplicationContext();
        this.inflater = LayoutInflater.from(context);
        this.selPositions = new SparseBooleanArray();
        this.downloadSQLiteHelper = PolyvDownloadSQLiteHelper.getInstance(context);
        this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.polyv_demo) // resource
                // or
                // drawable
                .showImageForEmptyUri(R.drawable.polyv_demo) // resource or drawable
                .showImageOnFail(R.drawable.polyv_demo) // resource or drawable
                .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true).cacheOnDisk(true).build();
    }

    //初始化选择状态
    public void initSelect(int bitrate) {
        for (int i = 0; i < lists.size(); i++) {
            if (downloadSQLiteHelper.isAdd(generateDownloadInfo(i, bitrate))) {
                selPositions.put(i, true);
            } else {
                selPositions.put(i, false);
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

    //获取侧边图标是否是选择状态
    public boolean getSelect(int position) {
        return selPositions.get(position, false);
    }

    //生成下载信息对象
    private PolyvDownloadInfo generateDownloadInfo(int position, int bitrate) {
        PolyvVlmsHelper.CurriculumsDetail curriculum = lists.get(position);
        PolyvVideoVO videoVO = curriculum.videoVO;
        long filesize = -1;
        // 获取可以下载码率的文件大小
        while (filesize <= 0 && bitrate > 0)
            filesize = videoVO.getFileSize(bitrate--);
        return new PolyvDownloadInfo(videoVO.getVid(), videoVO.getDuration(), filesize, bitrate + 1, curriculum.lecture.title);
    }

    private static class MyDownloadListener implements IPolyvDownloaderProgressListener{
        private PolyvDownloadInfo downloadInfo;
        private long total;

        public MyDownloadListener(PolyvDownloadInfo downloadInfo){
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

    //设置侧边图标为选择状态和添加到数据库并开始下载任务
    public void setSelect(int position, int bitrate) {
        final PolyvDownloadInfo downloadInfo = generateDownloadInfo(position, bitrate);
        if (!downloadSQLiteHelper.isAdd(downloadInfo)) {
            downloadSQLiteHelper.insert(downloadInfo);
            PolyvDownloader downloader = PolyvDownloaderManager.getPolyvDownloader(downloadInfo.getVid(), downloadInfo.getBitrate());
            downloader.setPolyvDownloadProressListener(new MyDownloadListener(downloadInfo));
            downloader.start();
            // 遍历集合，有相同vid(/其他约束条件)的项都设为选择状态
            for (int i = 0; i < lists.size(); i++) {
                if (lists.get(i).lecture.vid.equals(downloadInfo.getVid()))
                    selPositions.put(i, true);
            }
            notifyDataSetChanged();
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
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (isVisible)
            viewHolder.iv_sel.setVisibility(View.VISIBLE);
        else
            viewHolder.iv_sel.setVisibility(View.GONE);
        if (selPositions.get(position, false) == false)
            viewHolder.iv_sel.setSelected(false);
        else
            viewHolder.iv_sel.setSelected(true);
        PolyvVlmsHelper.CurriculumsDetail polyvCurriculum = lists.get(position);
        viewHolder.tv_seri.setText(polyvCurriculum.section_name + " " + polyvCurriculum.name);
        viewHolder.tv_title.setText(polyvCurriculum.lecture.title);
        viewHolder.tv_time.setText(polyvCurriculum.lecture.duration);
        ImageLoader.getInstance().displayImage(polyvCurriculum.cover_image, viewHolder.iv_demo, options);
        return convertView;
    }

    private class ViewHolder {
        ImageView iv_sel, iv_demo;
        TextView tv_seri, tv_title, tv_time;
    }
}
