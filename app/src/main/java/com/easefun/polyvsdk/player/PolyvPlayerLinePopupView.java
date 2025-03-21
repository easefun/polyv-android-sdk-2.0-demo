package com.easefun.polyvsdk.player;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.easefun.polyvsdk.PolyvDownloader;
import com.easefun.polyvsdk.PolyvDownloaderErrorReason;
import com.easefun.polyvsdk.PolyvDownloaderManager;
import com.easefun.polyvsdk.PolyvSDKClient;
import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.application.PolyvSettings;
import com.easefun.polyvsdk.download.constant.PolyvDownloaderVideoStatus;
import com.easefun.polyvsdk.download.listener.IPolyvDownloaderProgressListener2;
import com.easefun.polyvsdk.download.util.PolyvDownloaderUtils;
import com.easefun.polyvsdk.download.vo.PolyvDownloaderVideoVO;
import com.easefun.polyvsdk.util.PolyvDnsUtil;
import com.easefun.polyvsdk.util.PolyvScreenUtils;
import com.easefun.polyvsdk.video.PolyvVideoUtil;
import com.easefun.polyvsdk.video.PolyvVideoView;
import com.easefun.polyvsdk.view.PLVRoundRectLayout;
import com.easefun.polyvsdk.vo.PolyvValidateLocalVideoVO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 播放线路选择弹窗(这里的播放线路包括：软硬解、线路、dns等)
 */
public class PolyvPlayerLinePopupView {
    private static final String TAG = "PlayerLinePopupView";
    private static final String[] CHINESE_NUMBERS = {"一", "二", "三", "四", "五", "六", "七", "八", "九", "十"};
    private PopupWindow popupWindow;
    private View view;
    private PLVRoundRectLayout lineLy;
    private RecyclerView decodeRv;
    private LineAdapter decodeAdapter;
    private RecyclerView routeRv;
    private LineAdapter routeAdapter;
    private RecyclerView dnsRv;
    private LineAdapter dnsAdapter;
    private RecyclerView fixRv;
    private LineAdapter fixAdapter;
    private PolyvSettings videoSetting;
    private PolyvVideoView videoView;
    private String vid;
    private int bitrate;
    private int needFixSize = 0;
    private int successFixSize = 0;
    private int failFixSize = 0;

    public PolyvPlayerLinePopupView(View anchorView) {
        videoSetting = new PolyvSettings(anchorView.getContext());
        popupWindow = new PopupWindow(anchorView.getContext());
        view = initPopupWindow(anchorView, R.layout.polyv_player_play_line_popup_view, popupWindow, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        view.findViewById(R.id.plv_close_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        lineLy = view.findViewById(R.id.plv_play_line_ly);
        lineLy.setOnOrientationChangedListener(new PLVRoundRectLayout.OnOrientationChangedListener() {
            @Override
            public void onChanged(boolean isPortrait) {
                if (isPortrait) {
                    onPortrait();
                } else {
                    onLandscape();
                }
            }
        });

        // 解码
        decodeRv = view.findViewById(R.id.plv_play_decode_ry);
        decodeRv.setLayoutManager(new GridLayoutManager(anchorView.getContext(), 2));
        List<Pair<String, String>> decodeLines = Arrays.asList(
                new Pair<>("硬解", "更省电"),
                new Pair<>("软解", "兼容性好(推荐)")
        );
        decodeAdapter = new LineAdapter(decodeLines);
        decodeRv.setAdapter(decodeAdapter);
        decodeAdapter.setOnClickListener(new LineAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {
                dismiss();
                videoSetting.setUsingMediaCodec(position == 0);
                if (videoView != null) {
                    videoView.changeMode(videoView.getPriorityMode());
                }
            }
        });

        // 线路
        routeRv = view.findViewById(R.id.plv_play_route_ry);
        routeRv.setLayoutManager(new GridLayoutManager(anchorView.getContext(), 2));
        routeAdapter = new LineAdapter(null);
        routeRv.setAdapter(routeAdapter);
        routeAdapter.setOnClickListener(new LineAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {
                dismiss();
                if (videoView != null) {
                    videoView.changeRoute(position + 1);
                }
            }
        });

        // 解析(dns)
        dnsRv = view.findViewById(R.id.plv_play_dns_ry);
        dnsRv.setLayoutManager(new GridLayoutManager(anchorView.getContext(), 2));
        List<Pair<String, String>> dnsLines = Arrays.asList(
                new Pair<>("httpdns", "一般网络下更稳定"),
                new Pair<>("localdns", "网络受限环境下使用")
        );
        dnsAdapter = new LineAdapter(dnsLines);
        dnsRv.setAdapter(dnsAdapter);
        dnsAdapter.setOnClickListener(new LineAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {
                dismiss();
                PolyvSDKClient.getInstance().enableHttpDns(position == 0);
                if (videoView != null) {
                    videoView.changeMode(videoView.getPriorityMode());
                }
            }
        });

        // 修复
        fixRv = view.findViewById(R.id.plv_play_fix_ry);
        fixRv.setLayoutManager(new GridLayoutManager(anchorView.getContext(), 2));
        List<Pair<String, String>> fixLines = Arrays.asList(
                new Pair<>("修复视频", "尝试修复离线视频"),
                new Pair<>("修复全部", "尝试修复离线视频")
        );
        fixAdapter = new LineAdapter(fixLines);
        fixRv.setAdapter(fixAdapter);
        fixAdapter.setOnClickListener(new LineAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {
                dismiss();
                if (position == 0) {
                    fixOne(vid, bitrate);
                } else {
                    fixAll();
                }
            }
        });
        ((ViewGroup) fixRv.getParent()).setVisibility(View.GONE);
    }

    private static class LineAdapter extends RecyclerView.Adapter<LineAdapter.ViewHolder> {
        private List<Pair<String, String>> lines;
        private int selectedPosition;
        private OnClickListener onClickListener;

        public LineAdapter(List<Pair<String, String>> lines) {
            this.lines = lines;
        }

        public void updateLines(List<Pair<String, String>> lines) {
            if (this.lines == null || lines == null || this.lines.size() != lines.size()) {
                this.lines = lines;
                notifyDataSetChanged();
            }
        }

        public void updateSelected(int position) {
            if (this.selectedPosition != position) {
                this.selectedPosition = position;
                notifyDataSetChanged();
            }
        }

        public void setOnClickListener(OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }

        @NonNull
        @Override
        public LineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.polyv_player_play_line_popup_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull LineAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            holder.mainTv.setText(lines.get(position).first);
            holder.subTv.setText(lines.get(position).second);
            ((ViewGroup) holder.mainTv.getParent()).setSelected(position == selectedPosition);
            ((ViewGroup) holder.mainTv.getParent()).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (position != selectedPosition) {
                        updateSelected(position);
                        if (onClickListener != null) {
                            onClickListener.onClick(position);
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return lines == null ? 0 : lines.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            public TextView mainTv;
            public TextView subTv;

            public ViewHolder(View itemView) {
                super(itemView);
                mainTv = itemView.findViewById(R.id.plv_play_main_tv);
                subTv = itemView.findViewById(R.id.plv_play_sub_tv);
            }
        }

        public interface OnClickListener {
            void onClick(int position);
        }
    }

    public void onPortrait() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.update();
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) lineLy.getLayoutParams();
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.gravity = Gravity.BOTTOM;
        lineLy.setLayoutParams(layoutParams);
    }

    public void onLandscape() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.update();
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) lineLy.getLayoutParams();
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.width = Math.min(getScreenHeight(view), getScreenWidth(view));
        layoutParams.gravity = Gravity.RIGHT;
        lineLy.setLayoutParams(layoutParams);
    }

    public void show(@NonNull PolyvVideoView videoView, String vid, int bitrate) {
        show(videoView, vid, bitrate, false);
    }

    public void show(@NonNull PolyvVideoView videoView, String vid, int bitrate, boolean showByError) {
        if (PolyvScreenUtils.isPortrait(view.getContext())) {
            onPortrait();
        } else {
            onLandscape();
        }
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);

        this.videoView = videoView;
        this.vid = vid;
        this.bitrate = bitrate;
        // 更新view状态
        decodeAdapter.updateSelected(videoSetting.getUsingMediaCodec() ? 0 : 1);
        if (videoView.getRouteCount() > 1) {
            List<Pair<String, String>> lines = new ArrayList<>();
            for (int i = 0; i < videoView.getRouteCount(); i++) {
                String number = (i < CHINESE_NUMBERS.length) ? CHINESE_NUMBERS[i] : String.valueOf(i + 1);
                String lineName = "线路" + number;
                String description;
                if (i == 0) {
                    description = "推荐线路，首选尝试";
                } else {
                    description = "主线路不畅时切换";
                }
                lines.add(new Pair<>(lineName, description));
            }
            routeAdapter.updateLines(lines);
            routeAdapter.updateSelected(videoView.getCurrentRoute() - 1);
        } else {
            ((ViewGroup) routeRv.getParent()).setVisibility(View.GONE);
        }
        dnsAdapter.updateSelected(PolyvDnsUtil.checkEnableHttpDns() ? 0 : 1);
        if (videoView.isLocalPlay()) {
            ((ViewGroup) dnsRv.getParent()).setVisibility(View.GONE);

            PolyvValidateLocalVideoVO localVideoVO = PolyvVideoUtil.validateLocalVideo(vid, bitrate);
            Log.e(TAG, "是否有本地视频：" + localVideoVO.hasLocalVideo());
            if (localVideoVO.hasLocalVideo() && showByError) {
                ((ViewGroup) fixRv.getParent()).setVisibility(View.VISIBLE);
                fixAdapter.updateSelected(-1);
            }
        }
    }

    public void dismiss() {
        popupWindow.dismiss();
    }

    public void fixOne(String vid, int bitrate) {
        PolyvDownloader downloader = PolyvDownloaderManager.getPolyvDownloader(vid, bitrate);
        downloader.setPolyvDownloadSpeedListener(null);
        downloader.setPolyvDownloadStartListener2(null);
        downloader.setPolyvDownloadWaitingListener(null);
        downloader.setPolyvDownloadProressListener2(new IPolyvDownloaderProgressListener2() {
            @Override
            public void onDownload(long current, long total) {

            }

            @Override
            public void onDownloadSuccess(int bitrate) {
                Toast.makeText(view.getContext(), "修复成功", Toast.LENGTH_SHORT).show();
                videoView.changeMode(videoView.getPriorityMode());
            }

            @Override
            public void onDownloadFail(@NonNull PolyvDownloaderErrorReason errorReason) {
                Toast.makeText(view.getContext(), "修复失败", Toast.LENGTH_SHORT).show();
            }
        });
        downloader.startFixKeyFile(view.getContext());
    }

    public void fixAll() {
        needFixSize = 0;
        successFixSize = 0;
        failFixSize = 0;
        ArrayList<PolyvDownloaderVideoVO> downloadVideoList = PolyvDownloaderUtils.getDownloadVideoList();
        Log.e(TAG, "本地视频列表：" + downloadVideoList);
        for (PolyvDownloaderVideoVO polyvDownloaderVideoVO : downloadVideoList) {
            if (polyvDownloaderVideoVO.getDownloaderVideoStatus() == PolyvDownloaderVideoStatus.VIDEO_CORRECT) {
                needFixSize++;
                PolyvDownloader downloader = PolyvDownloaderManager.getPolyvDownloader(polyvDownloaderVideoVO.getVideoId(), polyvDownloaderVideoVO.getBitrate());
                downloader.setPolyvDownloadSpeedListener(null);
                downloader.setPolyvDownloadStartListener2(null);
                downloader.setPolyvDownloadWaitingListener(null);
                downloader.setPolyvDownloadProressListener2(new IPolyvDownloaderProgressListener2() {
                    @Override
                    public void onDownload(long current, long total) {

                    }

                    @Override
                    public void onDownloadSuccess(int bitrate) {
                        successFixSize++;
                        if ((successFixSize + failFixSize) >= needFixSize) {
                            Toast.makeText(view.getContext(), "修复成功：" + successFixSize + "/" + needFixSize, Toast.LENGTH_SHORT).show();
                            videoView.changeMode(videoView.getPriorityMode());
                        }
                    }

                    @Override
                    public void onDownloadFail(@NonNull PolyvDownloaderErrorReason errorReason) {
                        failFixSize++;
                        if (failFixSize >= needFixSize) {
                            Toast.makeText(view.getContext(), "修复失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                downloader.startFixKeyFile(view.getContext());
            }
        }
    }

    private static View initPopupWindow(View v, @LayoutRes int resource, final PopupWindow popupWindow, View.OnClickListener listener) {
        return initPopupWindow(LayoutInflater.from(v.getContext()).inflate(resource, null, false), popupWindow, listener);
    }

    private static View initPopupWindow(View resource, final PopupWindow popupWindow, View.OnClickListener listener) {
        popupWindow.setContentView(resource);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        resource.setOnClickListener(listener);
        return resource;
    }

    private static int getScreenWidth(View view) {
        return view.getResources().getDisplayMetrics().widthPixels;
    }

    private static int getScreenHeight(View view) {
        return view.getResources().getDisplayMetrics().heightPixels;
    }
}
