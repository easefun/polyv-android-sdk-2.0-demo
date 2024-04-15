package com.easefun.polyvsdk.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.easefun.polyvsdk.PolyvBitRate;
import com.easefun.polyvsdk.PolyvDownloader;
import com.easefun.polyvsdk.PolyvDownloaderErrorReason;
import com.easefun.polyvsdk.PolyvDownloaderManager;
import com.easefun.polyvsdk.PolyvSDKUtil;
import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.RestVO;
import com.easefun.polyvsdk.activity.PolyvMainActivity;
import com.easefun.polyvsdk.activity.PolyvPlayerActivity;
import com.easefun.polyvsdk.bean.PolyvDownloadInfo;
import com.easefun.polyvsdk.database.PolyvDownloadSQLiteHelper;
import com.easefun.polyvsdk.download.listener.IPolyvDownloaderProgressListener2;
import com.easefun.polyvsdk.util.PolyvErrorMessageUtils;
import com.easefun.polyvsdk.util.PolyvImageLoader;
import com.easefun.polyvsdk.vo.PolyvVideoVO;

import java.lang.ref.WeakReference;
import java.util.List;


public class PolyvOnlineListViewAdapter extends AbsRecyclerViewAdapter {
    private static final String TAG = PolyvOnlineListViewAdapter.class.getSimpleName();
    private Context context;
    private List<RestVO> videos;
    private static PolyvDownloadSQLiteHelper downloadSQLiteHelper;

    public PolyvOnlineListViewAdapter(RecyclerView recyclerView, Context context, List<RestVO> videos) {
        super(recyclerView);
        this.context = context;
        this.videos = videos;
        downloadSQLiteHelper = PolyvDownloadSQLiteHelper.getInstance(context);
    }

    private class ItemViewHolder extends AbsRecyclerViewAdapter.ClickableViewHolder {
        // 封面图
        ImageView iv_cover;
        // 标题，时间，下载按钮，播放按钮
        TextView tv_title, tv_time, tv_download, tv_play;

        ItemViewHolder(View itemView) {
            super(itemView);
            iv_cover = $(R.id.iv_cover);
            tv_title = $(R.id.tv_title);
            tv_time = $(R.id.tv_time);
            tv_download = $(R.id.tv_download);
            tv_play = $(R.id.tv_play);
        }
    }

    @Override
    public ClickableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        bindContext(parent.getContext());
        return new ItemViewHolder(LayoutInflater.from(getContext()).
                inflate(R.layout.polyv_listview_online_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ClickableViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder mHolder = (ItemViewHolder) holder;
            RestVO restVO = videos.get(position);
            mHolder.tv_title.setText(restVO.getTitle());
            mHolder.tv_time.setText(restVO.getDuration());
            mHolder.tv_download.setOnClickListener(new DownloadListener(restVO.getVid(), restVO.getTitle()));
            mHolder.tv_play.setOnClickListener(new PlayListener(restVO.getVid()));
            PolyvImageLoader.getInstance()
                    .loadImageWithCache(context, restVO.getFirstImage().trim(),
                            mHolder.iv_cover,R.drawable.polyv_pic_demo );
        }
        super.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    private class PlayListener implements View.OnClickListener {
        private String vid;

        PlayListener(String vid) {
            this.vid = vid;
        }

        @Override
        public void onClick(View arg0) {
            Intent intent = PolyvPlayerActivity.newIntent(context, PolyvPlayerActivity.PlayMode.portrait, vid);
            intent.putExtra(PolyvMainActivity.IS_VLMS_ONLINE, false);
            context.startActivity(intent);
        }
    }

    private static class MyDownloadListener implements IPolyvDownloaderProgressListener2 {
        private long total;
        private WeakReference<Context> contextWeakReference;
        private PolyvDownloadInfo downloadInfo;

        MyDownloadListener(Context context, PolyvDownloadInfo downloadInfo) {
            this.contextWeakReference = new WeakReference<>(context);
            this.downloadInfo = downloadInfo;
        }

        @Override
        public void onDownloadSuccess(int bitrate) {
            if (total == 0)
                total = 1;

            downloadInfo.setBitrate(bitrate);
            downloadSQLiteHelper.update(downloadInfo, total, total);
        }

        @Override
        public void onDownloadFail(@NonNull PolyvDownloaderErrorReason errorReason) {
            String errorMsg = PolyvErrorMessageUtils.getDownloaderErrorMessage(errorReason.getType(), downloadInfo.getFileType());
            errorMsg += "(error code " + errorReason.getType().getCode() + ")";
            Log.e(TAG, errorMsg);
            if (contextWeakReference.get() != null) {
                Toast.makeText(contextWeakReference.get(), errorMsg, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onDownload(long current, long total) {
            this.total = total;
        }
    }

    private class DownloadListener implements View.OnClickListener {
        private String vid;
        private String title;

        DownloadListener(String vid, String title) {
            this.vid = vid;
            this.title = title;
        }

        @Override
        public void onClick(final View view) {
            LoadVideoInfoTask loadVideoInfoTask = new LoadVideoInfoTask(new ILoadVideoInfoListener() {

                @Override
                public void onloaded(final PolyvVideoVO v) {
                    if (v == null) {
                        Toast.makeText(context, "获取下载信息失败，请重试", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (v.getPlayerErrorCode()!=null && !v.getPlayerErrorCode().equals("0")) {
                        String tipsZhCn = v.getPlayerErrorTipsZhCn();
                        String tipsEn = v.getPlayerErrorTipsEn();

                        Log.e(TAG, "视频错误状态码: " + v.getPlayerErrorCode());
                        Log.e(TAG, "视频错误提示: " + tipsZhCn);

                        String errorMessage = TextUtils.isEmpty(tipsZhCn) ? tipsEn : tipsZhCn;
                            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                        return;
                    }

                    // 码率数
                    String[] items = PolyvBitRate.getBitRateNameArray(v.getDfNum());
                    final AlertDialog.Builder selectDialog = new AlertDialog.Builder(context).setTitle("请选择下载码率").setSingleChoiceItems(items, 0,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int bitrate = which + 1;

                                    final PolyvDownloadInfo downloadInfo = new PolyvDownloadInfo(vid, v.getDuration(),
                                            v.getFileSizeMatchVideoType(bitrate,PolyvDownloader.FILE_VIDEO), bitrate, title);
                                    //设置下载的文件类型，视频/音频，可以用v.hasAudioPath()判断是否有音频
                                    downloadInfo.setFileType(PolyvDownloader.FILE_VIDEO);
                                    Log.i("videoAdapter", downloadInfo.toString());
                                    if (downloadSQLiteHelper != null && !downloadSQLiteHelper.isAdd(downloadInfo)) {
                                        downloadSQLiteHelper.insert(downloadInfo);
                                        PolyvDownloader polyvDownloader = PolyvDownloaderManager.getPolyvDownloader(vid, bitrate, downloadInfo.getFileType());
                                        polyvDownloader.setPolyvDownloadProressListener2(new MyDownloadListener(context, downloadInfo));
                                        polyvDownloader.start(context);
                                    } else {
                                        ((Activity) context).runOnUiThread(new Runnable() {

                                            @Override
                                            public void run() {
                                                Toast.makeText(context, "下载任务已经增加到队列", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                    dialog.dismiss();
                                }
                            });

                    if (view.getWindowToken() != null)
                        selectDialog.show().setCanceledOnTouchOutside(true);
                }
            });

            loadVideoInfoTask.execute(vid);
        }
    }

    private static class LoadVideoInfoTask extends AsyncTask<String, Void, PolyvVideoVO> {

        private final ILoadVideoInfoListener l;

        LoadVideoInfoTask(ILoadVideoInfoListener l) {
            this.l = l;
        }

        @Override
        protected PolyvVideoVO doInBackground(String... params) {
            try {
                return PolyvSDKUtil.loadVideoJSON2Video(params[0]);
            } catch (Exception e) {
                Log.e(TAG, PolyvSDKUtil.getExceptionFullMessage(e, -1));
                return null;
            }
        }

        @Override
        protected void onPostExecute(PolyvVideoVO v) {
            super.onPostExecute(v);
            l.onloaded(v);
        }
    }

    private interface ILoadVideoInfoListener {
        void onloaded(PolyvVideoVO v);
    }
}
