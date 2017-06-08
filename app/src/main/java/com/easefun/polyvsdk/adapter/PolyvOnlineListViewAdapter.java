package com.easefun.polyvsdk.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.easefun.polyvsdk.BitRateEnum;
import com.easefun.polyvsdk.PolyvBitRate;
import com.easefun.polyvsdk.PolyvDownloader;
import com.easefun.polyvsdk.PolyvDownloaderErrorReason;
import com.easefun.polyvsdk.PolyvDownloaderManager;
import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.RestVO;
import com.easefun.polyvsdk.Video;
import com.easefun.polyvsdk.activity.PolyvMainActivity;
import com.easefun.polyvsdk.activity.PolyvPlayerActivity;
import com.easefun.polyvsdk.bean.PolyvDownloadInfo;
import com.easefun.polyvsdk.database.PolyvDownloadSQLiteHelper;
import com.easefun.polyvsdk.download.listener.IPolyvDownloaderProgressListener;
import com.easefun.polyvsdk.player.PolyvAnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.List;


public class PolyvOnlineListViewAdapter extends BaseAdapter {
    private ViewHolder viewHolder;
    private Context context;
    private LayoutInflater inflater;
    private List<RestVO> videos;
    private DisplayImageOptions options;
    private static PolyvDownloadSQLiteHelper downloadSQLiteHelper;

    public PolyvOnlineListViewAdapter(Context context, List<RestVO> videos) {
        this.context = context;
        this.videos = videos;
        this.inflater = LayoutInflater.from(this.context);
        this.downloadSQLiteHelper = PolyvDownloadSQLiteHelper.getInstance(context);

        options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.polyv_pic_demo) // 设置图片在下载期间显示的图片
                .showImageForEmptyUri(R.drawable.polyv_pic_demo)// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.polyv_pic_demo) // 设置图片加载/解码过程中错误时候显示的图片
                .bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型//
                .cacheInMemory(true)// 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true)// 设置下载的图片是否缓存在SD卡中
                .displayer(new FadeInBitmapDisplayer(100))// 是否图片加载好后渐入的动画时间
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();// 构建完成
    }

    @Override
    public int getCount() {
        if (videos == null)
            return 0;
        return videos.size();
    }

    @Override
    public Object getItem(int position) {
        return videos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.polyv_listview_online_item, null);
            viewHolder = new ViewHolder();
            viewHolder.iv_cover = (ImageView) convertView.findViewById(R.id.iv_cover);
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            viewHolder.tv_download = (TextView) convertView.findViewById(R.id.tv_download);
            viewHolder.tv_play = (TextView) convertView.findViewById(R.id.tv_play);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        RestVO restVO = videos.get(position);
        viewHolder.tv_title.setText(restVO.getTitle());
        viewHolder.tv_time.setText(restVO.getDuration());
        viewHolder.tv_download.setOnClickListener(new DownloadListener(restVO.getVid(), restVO.getTitle()));
        viewHolder.tv_play.setOnClickListener(new PlayListener(restVO.getVid()));
        ImageLoader imageloader = ImageLoader.getInstance();
        imageloader.displayImage(restVO.getFirstImage(), viewHolder.iv_cover, options, new PolyvAnimateFirstDisplayListener());
        return convertView;
    }

    private class ViewHolder {
        // 封面图
        ImageView iv_cover;
        // 标题，时间，下载按钮，播放按钮
        TextView tv_title, tv_time, tv_download, tv_play;
    }

    class PlayListener implements View.OnClickListener {
        private String vid;

        public PlayListener(String vid) {
            this.vid = vid;
        }

        @Override
        public void onClick(View arg0) {
            Intent intent = PolyvPlayerActivity.newIntent(context, PolyvPlayerActivity.PlayMode.portrait, vid);
            intent.putExtra(PolyvMainActivity.IS_VLMS_ONLINE, false);
            context.startActivity(intent);
        }
    }

    private static class MyDownloadListener implements IPolyvDownloaderProgressListener {
        private long total;
        private PolyvDownloadInfo downloadInfo;

        public MyDownloadListener(PolyvDownloadInfo downloadInfo) {
            this.downloadInfo = downloadInfo;
        }

        @Override
        public void onDownloadSuccess() {
            if (total == 0)
                total = 1;
            downloadSQLiteHelper.update(downloadInfo, total, total);
        }

        @Override
        public void onDownloadFail(PolyvDownloaderErrorReason errorReason) {
        }

        @Override
        public void onDownload(long current, long total) {
            this.total = total;
        }
    }

    class DownloadListener implements View.OnClickListener {
        private String vid;
        private String title;

        public DownloadListener(String vid, String title) {
            this.vid = vid;
            this.title = title;
        }

        @Override
        public void onClick(View v) {
            Video.loadVideo(vid, new Video.OnVideoLoaded() {
                public void onloaded(final Video v) {
                    if (v == null) {
                        return;
                    }

                    // 码率数
                    String[] items = BitRateEnum.getBitRateNameArray(v.getDfNum());
                    // 数字2代表的是数组的下标
                    final AlertDialog.Builder selectDialog = new AlertDialog.Builder(context).setTitle("请选择下载码率").setSingleChoiceItems(items, 0,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int bitrate = which + 1;

                                    final PolyvDownloadInfo downloadInfo = new PolyvDownloadInfo(vid, v.getDuration(),
                                            v.getFileSizeMatchVideoType(bitrate), bitrate, title);
                                    Log.i("videoAdapter", downloadInfo.toString());
                                    if (downloadSQLiteHelper != null && !downloadSQLiteHelper.isAdd(downloadInfo)) {
                                        downloadSQLiteHelper.insert(downloadInfo);
                                        PolyvDownloader polyvDownloader = PolyvDownloaderManager.getPolyvDownloader(vid, bitrate);
                                        polyvDownloader.setPolyvDownloadProressListener(new MyDownloadListener(downloadInfo));
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

                    selectDialog.show().setCanceledOnTouchOutside(true);
                }
            });
        }
    }
}
