package com.easefun.polyvsdk.ppt;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.adapter.PolyvPPTDirListAdapter;
import com.easefun.polyvsdk.player.PolyvPlayerMediaController;
import com.easefun.polyvsdk.po.ppt.PolyvPptInfo;
import com.easefun.polyvsdk.po.ppt.PolyvPptPageInfo;
import com.easefun.polyvsdk.util.PolyvSPUtils;
import com.easefun.polyvsdk.util.PolyvScreenUtils;
import com.easefun.polyvsdk.video.PolyvVideoView;

import java.util.ArrayList;

/**
 * 三分屏的课程目录布局
 */
public class PolyvPPTDirLayout extends FrameLayout {
    private PolyvVideoView currentVideoView;

    private RecyclerView pptDirList;
    private PolyvPPTDirListAdapter pptDirListAdapter;

    private PolyvPPTErrorLayout pptErrorLayout, landPptErrorLayout;

    private boolean pptIsError;

    public PolyvPPTDirLayout(@NonNull Context context) {
        this(context, null);
    }

    public PolyvPPTDirLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PolyvPPTDirLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.polyv_ppt_dir_layout, this);
        initView();
    }

    private void initView() {
        if (isLandLayout()) {
            findViewById(R.id.title).setVisibility(View.GONE);
            findViewById(R.id.line).setVisibility(View.GONE);
            FrameLayout frameLayout = new FrameLayout(getContext());
            frameLayout.setBackgroundColor(Color.BLACK);
            frameLayout.setAlpha(0.7f);
            MarginLayoutParams mlp = new MarginLayoutParams(-1, -1);
            frameLayout.setLayoutParams(mlp);
            addView(frameLayout, 0);
        }
        pptDirList = (RecyclerView) findViewById(R.id.ppt_dir_list);
        pptDirList.setHasFixedSize(true);
        pptDirList.setLayoutManager(new LinearLayoutManager(getContext()));
        pptDirList.addItemDecoration(new PolyvPPTDirListAdapter.SpacesItemDecoration(PolyvScreenUtils.dip2px(getContext(), 16)));
        pptDirListAdapter = new PolyvPPTDirListAdapter(new ArrayList<PolyvPptPageInfo>());
        pptDirListAdapter.setLandLayout(isLandLayout());
        pptDirListAdapter.setOnItemClickListener(new PolyvPPTDirListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, PolyvPPTDirListAdapter.PPTViewHolder holder) {
                PolyvPptPageInfo pptPageInfo = pptDirListAdapter.getPptPageInfoList().get(position);
                if (currentVideoView != null && currentVideoView.isInPlaybackState() && currentVideoView.getCurrentVid() != null) {
                    int seekPosition = Math.min(currentVideoView.getDuration(), Math.max(0, pptPageInfo.getSec()) * 1000);
                    int dragSeekStrategy = PolyvSPUtils.getInstance(getContext(), "dragSeekStrategy").getInt("dragSeekStrategy");
                    boolean canDragSeek;
                    if (currentVideoView != null && currentVideoView.getMediaController() != null) {
                        canDragSeek = currentVideoView.getMediaController().canDragSeek(seekPosition);
                    } else {
                        if (dragSeekStrategy == PolyvPlayerMediaController.DRAG_SEEK_BAN) {
                            canDragSeek = false;
                        } else if (dragSeekStrategy == PolyvPlayerMediaController.DRAG_SEEK_PLAYED) {
                            canDragSeek = seekPosition <= PolyvSPUtils.getInstance(getContext(), "videoProgress").getInt(currentVideoView.getCurrentVid());
                        } else {
                            canDragSeek = true;
                        }
                    }
                    if (canDragSeek) {
                        currentVideoView.seekTo(seekPosition);
                        if (currentVideoView.isCompletedState()) {
                            currentVideoView.start();
                        }
                        if (isLandLayout()) {
                            setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
        pptDirList.setAdapter(pptDirListAdapter);

        pptErrorLayout = (PolyvPPTErrorLayout) findViewById(R.id.ppt_error_layout);
    }

    public void bindLandPptErrorLayout(PolyvPPTErrorLayout landPptErrorLayout) {
        this.landPptErrorLayout = landPptErrorLayout;
    }

    public void showLandLayout() {
        if (isLandLayout()) {
            if (pptIsError && landPptErrorLayout != null) {
                landPptErrorLayout.setVisibility(View.VISIBLE);
            } else {
                setVisibility(View.VISIBLE);
            }
        }
    }

    public PolyvPPTErrorLayout getPptErrorLayout() {
        return pptErrorLayout;
    }

    public void acceptPPTCallback(PolyvVideoView videoView, String vid, boolean hasPPT, PolyvPptInfo pptvo) {
        if (!isLandLayout()) {
            pptErrorLayout.acceptPPTCallback(videoView, vid, hasPPT, pptvo);
        }
        if (hasPPT) {
            if (!isLandLayout()) {
                setVisibility(View.VISIBLE);
            }
            if (pptvo == null) {
                pptIsError = true;
                if (!isLandLayout()) {
                    pptErrorLayout.setVisibility(View.VISIBLE);
                    pptDirList.setVisibility(View.GONE);
                }
                pptDirListAdapter.clear();
                pptDirListAdapter.notifyDataSetChanged();
            } else {
                pptIsError = false;
                pptErrorLayout.setVisibility(View.GONE);
                pptDirList.setVisibility(View.VISIBLE);
                pptDirListAdapter.set(pptvo.getPages());
                pptDirListAdapter.notifyDataSetChanged();
            }
        } else {
            setVisibility(View.GONE);
            pptDirListAdapter.clear();
            pptDirListAdapter.notifyDataSetChanged();
        }
        currentVideoView = videoView;
    }

    private boolean isLandLayout() {
        return getBackground() == null;
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT && isLandLayout()) {
            setVisibility(View.GONE);
        }
    }
}
