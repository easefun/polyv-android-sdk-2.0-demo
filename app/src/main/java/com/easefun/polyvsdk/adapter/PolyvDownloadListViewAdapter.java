package com.easefun.polyvsdk.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.easefun.polyvsdk.PolyvDownloader;
import com.easefun.polyvsdk.PolyvDownloaderErrorReason;
import com.easefun.polyvsdk.PolyvDownloaderManager;
import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.activity.PolyvMainActivity;
import com.easefun.polyvsdk.activity.PolyvPlayerActivity;
import com.easefun.polyvsdk.bean.PolyvDownloadInfo;
import com.easefun.polyvsdk.database.PolyvDownloadSQLiteHelper;
import com.easefun.polyvsdk.download.listener.IPolyvDownloaderProgressListener;
import com.easefun.polyvsdk.download.listener.IPolyvDownloaderSpeedListener;
import com.easefun.polyvsdk.download.listener.IPolyvDownloaderStartListener;
import com.easefun.polyvsdk.util.PolyvErrorMessageUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class PolyvDownloadListViewAdapter extends BaseAdapter {
    private static final String TAG = PolyvDownloadListViewAdapter.class.getSimpleName();
    private static final String DOWNLOADED = "已下载";
    private static final String DOWNLOADING = "正在下载";
    private static final String PAUSEED = "暂停下载";
    private static final String WAITED = "等待下载";
    private static PolyvDownloadSQLiteHelper downloadSQLiteHelper;
    private static Context appContext;
    private Context context;
    private ListView lv_download;
    private List<PolyvDownloadInfo> lists;
    private LayoutInflater inflater;
    private ViewHolder viewHolder;

    public PolyvDownloadListViewAdapter(List<PolyvDownloadInfo> lists, Context context, ListView lv_download) {
        this.lists = lists;
        this.context = context;
        appContext = context.getApplicationContext();
        this.inflater = LayoutInflater.from(this.context);
        this.lv_download = lv_download;
        downloadSQLiteHelper = PolyvDownloadSQLiteHelper.getInstance(this.context);
        init();
    }

    private void init() {
        for (int i = 0; i < lists.size(); i++) {
            PolyvDownloadInfo downloadInfo = lists.get(i);
            String vid = downloadInfo.getVid();
            int bitrate = downloadInfo.getBitrate();
            PolyvDownloaderManager.getPolyvDownloader(vid, bitrate);
        }
    }

    /**
     * 下载全部任务
     */
    public void downloadAll() {
        // 已完成的任务key集合
        List<String> finishKey = new ArrayList<>();
        for (int i = 0; i < lists.size(); i++) {
            PolyvDownloadInfo downloadInfo = lists.get(i);
            long percent = downloadInfo.getPercent();
            long total = downloadInfo.getTotal();
            int progress = 0;
            if (total != 0)
                progress = (int) (percent * 100 / total);
            if (progress == 100)
                finishKey.add(PolyvDownloaderManager.getKey(downloadInfo.getVid(), downloadInfo.getBitrate()));
        }
        updateButtonStatus(false);
        PolyvDownloaderManager.startUnfinished(finishKey, context);
    }

    /**
     * 暂停全部任务
     */
    public void pauseAll() {
        PolyvDownloaderManager.stopAll();
        updateButtonStatus(true);
        for (int i = 0; i < lists.size(); i++) {
            View child = null;
            if ((child = lv_download.getChildAt(i - lv_download.getFirstVisiblePosition())) != null) {
                TextView tv_speed = (TextView) child.findViewById(R.id.tv_speed);
                showPauseSpeeView(lists.get(i), tv_speed);
            }
        }
    }

    /**
     * 删除任务
     */
    public void deleteTask(int position) {
        PolyvDownloadInfo downloadInfo = lists.remove(position);
        //移除任务
        PolyvDownloader downloader = PolyvDownloaderManager.clearPolyvDownload(downloadInfo.getVid(), downloadInfo.getBitrate());
        //删除文件
        downloader.deleteVideo();
        //移除数据库的下载信息
        downloadSQLiteHelper.delete(downloadInfo);
        notifyDataSetChanged();
    }

    /**
     * 更新按钮状态
     *
     * @param isPause 之后的状态是否是暂停状态
     */
    private void updateButtonStatus(boolean isPause) {
        for (int i = 0; i < lv_download.getChildCount(); i++) {
            TextView tv_status = (TextView) lv_download.getChildAt(i).findViewById(R.id.tv_status);
            ImageView iv_start = (ImageView) lv_download.getChildAt(i).findViewById(R.id.iv_start);
            if (!tv_status.getText().equals(DOWNLOADED)) {
                if (isPause) {
                    tv_status.setText(PAUSEED);
                    tv_status.setSelected(true);
                    iv_start.setImageResource(R.drawable.polyv_btn_download);
                } else {
                    if (!tv_status.getText().equals(DOWNLOADING)) {
                        tv_status.setText(WAITED);
                        tv_status.setSelected(true);
                        iv_start.setImageResource(R.drawable.polyv_btn_download);
                    }
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
            convertView = inflater.inflate(R.layout.polyv_listview_download_item, null);
            viewHolder = new ViewHolder();
            viewHolder.fl_start = (FrameLayout) convertView.findViewById(R.id.fl_start);
            viewHolder.iv_start = (ImageView) convertView.findViewById(R.id.iv_start);
            viewHolder.tv_seri = (TextView) convertView.findViewById(R.id.tv_seri);
            viewHolder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
            viewHolder.tv_speed = (TextView) convertView.findViewById(R.id.tv_speed);
            viewHolder.tv_status = (TextView) convertView.findViewById(R.id.tv_status);
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.pb_progress = (ProgressBar) convertView.findViewById(R.id.pb_progress);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        PolyvDownloadInfo downloadInfo = lists.get(position);
        String vid = downloadInfo.getVid();
        int bitrate = downloadInfo.getBitrate();
        long percent = downloadInfo.getPercent();
        long total = downloadInfo.getTotal();
        String title = downloadInfo.getTitle();
        long filesize = downloadInfo.getFilesize();
        // 已下载的百分比
        int progress = 0;
        if (total != 0)
            progress = (int) (percent * 100 / total);
        PolyvDownloader downloader = PolyvDownloaderManager.getPolyvDownloader(vid, bitrate);
        viewHolder.pb_progress.setVisibility(View.VISIBLE);
        viewHolder.tv_speed.setVisibility(View.VISIBLE);
        viewHolder.tv_status.setSelected(false);
        if (progress == 100) {
            viewHolder.iv_start.setImageResource(R.drawable.polyv_btn_play);
            viewHolder.tv_status.setText(DOWNLOADED);
            viewHolder.pb_progress.setVisibility(View.GONE);
            viewHolder.tv_speed.setVisibility(View.GONE);
        } else if (downloader.isDownloading()) {
            viewHolder.iv_start.setImageResource(R.drawable.polyv_btn_dlpause);
            viewHolder.tv_status.setText(DOWNLOADING);
            viewHolder.tv_speed.setText("0.00B/S");
        } else if (PolyvDownloaderManager.isWaitingDownload(vid, bitrate)) {
            viewHolder.iv_start.setImageResource(R.drawable.polyv_btn_download);
            viewHolder.tv_status.setText(WAITED);
            viewHolder.tv_status.setSelected(true);
            viewHolder.tv_speed.setText(Formatter.formatFileSize(context, filesize * progress / 100));
        } else {
            viewHolder.iv_start.setImageResource(R.drawable.polyv_btn_download);
            viewHolder.tv_status.setText(PAUSEED);
            viewHolder.tv_status.setSelected(true);
            viewHolder.tv_speed.setText(Formatter.formatFileSize(context, filesize * progress / 100));
        }
        if (position + 1 < 10)
            viewHolder.tv_seri.setText("0" + (position + 1));
        else
            viewHolder.tv_seri.setText("" + (position + 1));
        viewHolder.tv_title.setText(title);
        viewHolder.tv_size.setText(Formatter.formatFileSize(context, filesize));
        viewHolder.pb_progress.setProgress(progress);
        viewHolder.fl_start.setOnClickListener(new DownloadOnClickListener(downloadInfo, viewHolder.iv_start, viewHolder.tv_status, viewHolder.tv_speed));
        viewHolder.setDownloadListener(downloader, downloadInfo, position);
        return convertView;
    }

    private static class MyDownloadListener implements IPolyvDownloaderProgressListener {
        private WeakReference<Context> contextWeakReference;
        private WeakReference<ListView> wr_lv_download;
        private WeakReference<ViewHolder> viewHolder;
        private PolyvDownloadInfo downloadInfo;
        private int position;
        private long total;

        MyDownloadListener(Context context, ListView lv_download, ViewHolder viewHolder, PolyvDownloadInfo downloadInfo, int position) {
            this.contextWeakReference = new WeakReference<>(context);
            this.wr_lv_download = new WeakReference<>(lv_download);
            this.viewHolder = new WeakReference<>(viewHolder);
            this.downloadInfo = downloadInfo;
            this.position = position;
        }

        private boolean canUpdateView() {
            ListView lv_download = wr_lv_download.get();
            return lv_download != null && viewHolder.get() != null && lv_download.getChildAt(position - lv_download.getFirstVisiblePosition()) != null;
        }

        @Override
        public void onDownload(long current, long total) {
            this.total = total;
            downloadInfo.setPercent(current);
            downloadInfo.setTotal(total);
            downloadSQLiteHelper.update(downloadInfo, current, total);
            if (canUpdateView()) {
                // 已下载的百分比
                int progress = (int) (current * 100 / total);
                viewHolder.get().pb_progress.setProgress(progress);
            }
        }

        @Override
        public void onDownloadSuccess() {
            if (total == 0)
                total = 1;
            downloadInfo.setPercent(total);
            downloadInfo.setTotal(total);
            downloadSQLiteHelper.update(downloadInfo, total, total);
            if (canUpdateView()) {
                viewHolder.get().tv_status.setText(DOWNLOADED);
                viewHolder.get().tv_status.setSelected(false);
                viewHolder.get().iv_start.setImageResource(R.drawable.polyv_btn_play);
                viewHolder.get().pb_progress.setVisibility(View.GONE);
                viewHolder.get().tv_speed.setVisibility(View.GONE);
                Toast.makeText(appContext, "第" + (position + 1) + "个任务下载成功", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onDownloadFail(@NonNull PolyvDownloaderErrorReason errorReason) {
            if (canUpdateView()) {
                viewHolder.get().tv_status.setText(PAUSEED);
                viewHolder.get().tv_status.setSelected(true);
                viewHolder.get().iv_start.setImageResource(R.drawable.polyv_btn_download);
                showPauseSpeeView(downloadInfo, viewHolder.get().tv_speed);
                String message = "第" + (position + 1) + "个任务";
                message += PolyvErrorMessageUtils.getDownloaderErrorMessage(errorReason.getType());
                message += "(error code " + errorReason.getType().getCode() + ")";

//                Toast.makeText(appContext, message, Toast.LENGTH_LONG).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(contextWeakReference.get());
                builder.setTitle("错误");
                builder.setMessage(message);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

                builder.show();
            }
        }
    }

    private static class MyDownloadSpeedListener implements IPolyvDownloaderSpeedListener {
        private WeakReference<ListView> wr_lv_download;
        private WeakReference<ViewHolder> viewHolder;
        private PolyvDownloader downloader;
        private int position;

        public MyDownloadSpeedListener(ListView lv_download, ViewHolder viewHolder, PolyvDownloader downloader, int position) {
            this.wr_lv_download = new WeakReference<ListView>(lv_download);
            this.viewHolder = new WeakReference<ViewHolder>(viewHolder);
            this.downloader = downloader;
            this.position = position;
        }

        private boolean canUpdateView() {
            ListView lv_download = wr_lv_download.get();
            return lv_download != null && viewHolder.get() != null && lv_download.getChildAt(position - lv_download.getFirstVisiblePosition()) != null;
        }

        @Override
        public void onSpeed(int speed) {
            if (canUpdateView() && downloader.isDownloading()) {
                viewHolder.get().tv_speed.setText(Formatter.formatShortFileSize(appContext, speed) + "/S");
            }
        }
    }

    private static class MyDownloaderStartListener implements IPolyvDownloaderStartListener {
        private WeakReference<ListView> wr_lv_download;
        private WeakReference<ViewHolder> viewHolder;
        private int position;

        public MyDownloaderStartListener(ListView lv_download, ViewHolder viewHolder, int position) {
            this.wr_lv_download = new WeakReference<ListView>(lv_download);
            this.viewHolder = new WeakReference<ViewHolder>(viewHolder);
            this.position = position;
        }

        private boolean canUpdateView() {
            ListView lv_download = wr_lv_download.get();
            return lv_download != null && viewHolder.get() != null && lv_download.getChildAt(position - lv_download.getFirstVisiblePosition()) != null;
        }

        @Override
        public void onStart() {
            if (canUpdateView()) {
                viewHolder.get().iv_start.setImageResource(R.drawable.polyv_btn_dlpause);
                viewHolder.get().tv_status.setText(DOWNLOADING);
                viewHolder.get().tv_status.setSelected(false);
            }
        }
    }

    private static void showPauseSpeeView(PolyvDownloadInfo downloadInfo, TextView tv_speed) {
        long percent = downloadInfo.getPercent();
        long total = downloadInfo.getTotal();
        int progress = 0;
        if (total != 0)
            progress = (int) (percent * 100 / total);
        // 已下载的文件大小
        long downloaded = downloadInfo.getFilesize() * progress / 100;
        tv_speed.setText(Formatter.formatFileSize(appContext, downloaded));
    }

    private class ViewHolder {
        FrameLayout fl_start;
        ImageView iv_start;
        TextView tv_seri, tv_title, tv_size, tv_status, tv_speed;
        ProgressBar pb_progress;

        public void setDownloadListener(PolyvDownloader downloader, final PolyvDownloadInfo downloadInfo, final int position) {
            downloader.setPolyvDownloadSpeedListener(new MyDownloadSpeedListener(lv_download, this, downloader, position));
            downloader.setPolyvDownloadProressListener(new MyDownloadListener(context, lv_download, this, downloadInfo, position));
            downloader.setPolyvDownloadStartListener(new MyDownloaderStartListener(lv_download, this, position));
        }
    }

    private class DownloadOnClickListener implements View.OnClickListener {
        private PolyvDownloadInfo downloadInfo;
        private ImageView iv_start;
        private TextView tv_status;
        private TextView tv_speed;

        public DownloadOnClickListener(PolyvDownloadInfo downloadInfo, ImageView iv_start, TextView tv_status, TextView tv_speed) {
            this.downloadInfo = downloadInfo;
            this.iv_start = iv_start;
            this.tv_status = tv_status;
            this.tv_speed = tv_speed;
        }

        @Override
        public void onClick(View v) {
            String vid = downloadInfo.getVid();
            int bitrate = downloadInfo.getBitrate();
            final PolyvDownloader downloader = PolyvDownloaderManager.getPolyvDownloader(vid, bitrate);
            if (tv_status.getText().equals(DOWNLOADED)) {
                Intent intent = PolyvPlayerActivity.newIntent(context, PolyvPlayerActivity.PlayMode.portrait, vid, bitrate, true, true);
                // 在线视频和下载的视频播放的时候只显示播放器窗口，用该参数来控制
                intent.putExtra(PolyvMainActivity.IS_VLMS_ONLINE, false);
                context.startActivity(intent);
            } else if (tv_status.getText().equals(DOWNLOADING) || tv_status.getText().equals(WAITED)) {
                tv_status.setText(PAUSEED);
                tv_status.setSelected(true);
                iv_start.setImageResource(R.drawable.polyv_btn_download);
                downloader.stop();
                showPauseSpeeView(downloadInfo, tv_speed);
            } else {
                tv_status.setText(DOWNLOADING);
                tv_status.setSelected(false);
                iv_start.setImageResource(R.drawable.polyv_btn_dlpause);
                downloader.start(context);
                if (!downloader.isDownloading() && PolyvDownloaderManager.isWaitingDownload(vid, bitrate)) {
                    iv_start.setImageResource(R.drawable.polyv_btn_download);
                    tv_status.setText(WAITED);
                    tv_status.setSelected(true);
                }
            }
        }
    }
}
