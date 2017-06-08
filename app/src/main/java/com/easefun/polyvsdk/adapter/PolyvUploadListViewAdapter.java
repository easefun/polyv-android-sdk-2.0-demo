package com.easefun.polyvsdk.adapter;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.bean.PolyvUploadInfo;
import com.easefun.polyvsdk.database.PolyvUploadSQLiteHelper;
import com.easefun.polyvsdk.upload.IPolyvUploader;
import com.easefun.polyvsdk.upload.PolyvUploader;
import com.easefun.polyvsdk.upload.PolyvUploaderManager;

import java.lang.ref.WeakReference;
import java.util.List;

public class PolyvUploadListViewAdapter extends BaseAdapter {
    private static final int REFRESH_PROGRESS = 1;
    private static final int SUCCESS = 2;
    private static final int FAILURE = 3;
    private static final String UPLOADED = "已上传";
    private static final String UPLOADING = "正在上传";
    private static final String PAUSEED = "暂停上传";
    private static PolyvUploadSQLiteHelper uploadSQLiteHelper;
    private static Context context;
    private MyHandler handler;
    private List<PolyvUploadInfo> lists;
    private LayoutInflater inflater;
    private ViewHolder viewHolder;

    private static class MyHandler extends Handler {
        private final WeakReference<ListView> wr_lv_upload;

        public MyHandler(ListView lv_upload) {
            this.wr_lv_upload = new WeakReference<ListView>(lv_upload);
        }

        @Override
        public void handleMessage(Message msg) {
            ListView lv_upload = wr_lv_upload.get();
            if (lv_upload != null) {
                int position = msg.arg1;
                View view = lv_upload.getChildAt(position - lv_upload.getFirstVisiblePosition());
                if (view == null)
                    return;
                TextView tv_status = null;
                ImageView iv_start = null;
                FrameLayout fl_start = null;
                ProgressBar pb_progress = null;
                TextView tv_speed = null;
                switch (msg.what) {
                    case REFRESH_PROGRESS:
                        pb_progress = (ProgressBar) view.findViewById(R.id.pb_progress);
                        tv_speed = (TextView) view.findViewById(R.id.tv_speed);
                        int progress = msg.getData().getInt("progress");
                        long uploaded = msg.getData().getLong("uploaded");
                        pb_progress.setProgress(progress);
                        tv_speed.setText(Formatter.formatFileSize(context, uploaded));
                        break;
                    case SUCCESS:
                        tv_status = (TextView) view.findViewById(R.id.tv_status);
                        iv_start = (ImageView) view.findViewById(R.id.iv_start);
                        fl_start = (FrameLayout) view.findViewById(R.id.fl_start);
                        pb_progress = (ProgressBar) view.findViewById(R.id.pb_progress);
                        tv_speed = (TextView) view.findViewById(R.id.tv_speed);
                        tv_status.setText(UPLOADED);
                        tv_status.setSelected(false);
                        iv_start.setImageResource(R.drawable.polyv_btn_play);
                        fl_start.setEnabled(false);
                        pb_progress.setVisibility(View.GONE);
                        tv_speed.setVisibility(View.GONE);
                        Toast.makeText(context, "第" + (position + 1) + "个任务上传成功", Toast.LENGTH_SHORT).show();
                        break;
                    case FAILURE:
                        tv_status = (TextView) view.findViewById(R.id.tv_status);
                        iv_start = (ImageView) view.findViewById(R.id.iv_start);
                        tv_status.setText(PAUSEED);
                        tv_status.setSelected(true);
                        iv_start.setImageResource(R.drawable.polyv_btn_upload);
                        int category = (int) msg.obj;
                        switch (category) {
                            case PolyvUploader.FFILE:
                                Toast.makeText(context, "第" + (position + 1) + "个任务文件不存在，或者大小为0", Toast.LENGTH_SHORT).show();
                                break;
                            case PolyvUploader.FVIDEO:
                                Toast.makeText(context, "第" + (position + 1) + "个任务不是支持上传的视频格式", Toast.LENGTH_SHORT).show();
                                break;
                            case PolyvUploader.NETEXCEPTION:
                                Toast.makeText(context, "第" + (position + 1) + "个任务网络异常，请重试", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        break;
                }
            }
        }
    }

    private class ViewHolder {
        FrameLayout fl_start;
        ImageView iv_start;
        TextView tv_seri, tv_title, tv_size, tv_status, tv_speed;
        ProgressBar pb_progress;
    }

    public PolyvUploadListViewAdapter(Context context, List<PolyvUploadInfo> lists, ListView lv_upload) {
        this.context = context.getApplicationContext();
        this.lists = lists;
        this.inflater = LayoutInflater.from(this.context);
        this.handler = new MyHandler(lv_upload);
        this.uploadSQLiteHelper = PolyvUploadSQLiteHelper.getInstance(this.context);
        initUploader();
    }

    private static class MyUploadListener implements PolyvUploader.UploadListener {
        private final WeakReference<MyHandler> wr_handler;
        private PolyvUploadInfo uploadInfo;
        private int position;

        public MyUploadListener(MyHandler myHandler, PolyvUploadInfo uploadInfo, int position) {
            this.wr_handler = new WeakReference<MyHandler>(myHandler);
            this.uploadInfo = uploadInfo;
            this.position = position;
        }

        @Override
        public void fail(int category) {
            MyHandler myHandler = wr_handler.get();
            if (myHandler != null) {
                Message message = myHandler.obtainMessage(FAILURE);
                message.arg1 = position;
                message.obj = category;
                myHandler.sendMessage(message);
            }
        }

        @Override
        public void upCount(long count, long total) {
            // 已下载的百分比
            int progress = (int) (count * 100 / total);
            // 已下载的文件大小
            long uploaded = uploadInfo.getFilesize() * progress / 100;
            uploadInfo.setPercent(count);
            uploadInfo.setTotal(total);
            uploadSQLiteHelper.update(uploadInfo, count, total);
            MyHandler myHandler = wr_handler.get();
            if (myHandler != null) {
                Message message = myHandler.obtainMessage(REFRESH_PROGRESS);
                message.arg1 = position;
                Bundle bundle = new Bundle();
                bundle.putInt("progress", progress);
                bundle.putLong("uploaded", uploaded);
                message.setData(bundle);
                myHandler.sendMessage(message);
            }
        }

        @Override
        public void success(long total, String vid) {
            uploadInfo.setPercent(total);
            uploadInfo.setTotal(total);
            uploadSQLiteHelper.update(uploadInfo, total, total);
            MyHandler myHandler = wr_handler.get();
            if (myHandler != null) {
                Message message = myHandler.obtainMessage(SUCCESS);
                message.arg1 = position;
                myHandler.sendMessage(message);
            }
        }
    }

    // 初始化上传器的监听器
    public void initUploader() {
        for (int i = 0; i < lists.size(); i++) {
            PolyvUploadInfo uploadInfo = lists.get(i);
            String filepath = uploadInfo.getFilepath();
            String title = uploadInfo.getTitle();
            String desc = uploadInfo.getDesc();
            IPolyvUploader uploader = PolyvUploaderManager.getPolyvUploader(filepath, title, desc);
            uploader.setUploadListener(new MyUploadListener(handler, uploadInfo, i));
        }
    }

    /**
     * 把任务从列表中移除
     */
    public void removeTask(int position) {
        PolyvUploadInfo uploadInfo = lists.remove(position);
        // 该方法会先暂停上传再移除任务
        PolyvUploaderManager.removePolyvUpload(uploadInfo.getFilepath());
        initUploader();
        uploadSQLiteHelper.delete(uploadInfo);
        notifyDataSetChanged();
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
            convertView = inflater.inflate(R.layout.polyv_listview_upload_item, null);
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
        PolyvUploadInfo uploadInfo = lists.get(position);
        long percent = uploadInfo.getPercent();
        long total = uploadInfo.getTotal();
        String title = uploadInfo.getTitle();
        String filepath = uploadInfo.getFilepath();
        String desc = uploadInfo.getDesc();
        long filesize = uploadInfo.getFilesize();
        // 已上传的百分比
        int progress = 0;
        if (total != 0)
            progress = (int) (percent * 100 / total);
        IPolyvUploader uploader = PolyvUploaderManager.getPolyvUploader(filepath, title, desc);
        viewHolder.pb_progress.setVisibility(View.VISIBLE);
        viewHolder.tv_speed.setVisibility(View.VISIBLE);
        viewHolder.tv_status.setSelected(false);
        viewHolder.fl_start.setEnabled(true);
        if (progress == 100) {
            viewHolder.iv_start.setImageResource(R.drawable.polyv_btn_play);
            viewHolder.fl_start.setEnabled(false);
            viewHolder.tv_status.setText(UPLOADED);
            viewHolder.pb_progress.setVisibility(View.GONE);
            viewHolder.tv_speed.setVisibility(View.GONE);
        } else if (uploader.isUploading()) {
            viewHolder.iv_start.setImageResource(R.drawable.polyv_btn_dlpause);
            viewHolder.tv_status.setText(UPLOADING);
        } else {
            viewHolder.iv_start.setImageResource(R.drawable.polyv_btn_upload);
            viewHolder.tv_status.setText(PAUSEED);
            viewHolder.tv_status.setSelected(true);
        }
        if (position + 1 < 10)
            viewHolder.tv_seri.setText("0" + (position + 1));
        else
            viewHolder.tv_seri.setText("" + (position + 1));
        viewHolder.tv_title.setText(title);
        viewHolder.tv_size.setText(Formatter.formatFileSize(context, filesize));
        viewHolder.tv_speed.setText(Formatter.formatFileSize(context, filesize * progress / 100));
        viewHolder.pb_progress.setProgress(progress);
        viewHolder.fl_start.setOnClickListener(new UploadOnClickListener(uploadInfo, viewHolder.iv_start, viewHolder.tv_status));
        return convertView;
    }

    private class UploadOnClickListener implements View.OnClickListener {
        private PolyvUploadInfo uploadInfo;
        private ImageView iv_start;
        private TextView tv_status;

        public UploadOnClickListener(PolyvUploadInfo uploadInfo, ImageView iv_start, TextView tv_status) {
            this.uploadInfo = uploadInfo;
            this.iv_start = iv_start;
            this.tv_status = tv_status;
        }

        @Override
        public void onClick(View v) {
            String filepath = uploadInfo.getFilepath();
            String title = uploadInfo.getTitle();
            String desc = uploadInfo.getDesc();
            IPolyvUploader uploader = PolyvUploaderManager.getPolyvUploader(filepath, title, desc);
            if (tv_status.getText().equals(UPLOADED)) {
                //...
            } else if (tv_status.getText().equals(UPLOADING)) {
                tv_status.setText(PAUSEED);
                tv_status.setSelected(true);
                iv_start.setImageResource(R.drawable.polyv_btn_upload);
                uploader.pause();
            } else {
                tv_status.setText(UPLOADING);
                tv_status.setSelected(false);
                iv_start.setImageResource(R.drawable.polyv_btn_dlpause);
                uploader.start();
            }
        }
    }
}
