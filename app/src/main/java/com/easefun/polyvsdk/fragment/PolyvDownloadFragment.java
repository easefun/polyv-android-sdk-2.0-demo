package com.easefun.polyvsdk.fragment;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.activity.PolyvDownloadActivity;
import com.easefun.polyvsdk.adapter.PolyvDownloadListViewAdapter;
import com.easefun.polyvsdk.bean.PolyvDownloadInfo;
import com.easefun.polyvsdk.database.PolyvDownloadSQLiteHelper;

import java.util.ArrayList;
import java.util.List;

public class PolyvDownloadFragment extends Fragment {
    private View view;
    private ListView lv_download;
    private List<PolyvDownloadInfo> downloadInfos;
    private PolyvDownloadListViewAdapter downloadAdapter;

    private RelativeLayout rl_bot;
    // 底部下载全部按钮
    private LinearLayout ll_downloadall, ll_deleteall;
    // 下载全部的文本控件
    private TextView tv_downloadall;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return view == null ? view = inflater.inflate(R.layout.polyv_fragment_download, null) : view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
        boolean isFinished = getArguments().getBoolean("isFinished");
        lv_download = (ListView) view.findViewById(R.id.lv_download);
        downloadInfos = new ArrayList<>();
        downloadInfos.addAll(getTask(PolyvDownloadSQLiteHelper.getInstance(getContext()).getAll(), isFinished));
        downloadAdapter = new PolyvDownloadListViewAdapter(downloadInfos, getContext(), lv_download);
        if (!isFinished) {
            downloadAdapter.setDownloadSuccessListener(new PolyvDownloadListViewAdapter.DownloadSuccessListener() {
                @Override
                public void onDownloadSuccess(PolyvDownloadInfo downloadInfo) {
                    ((PolyvDownloadActivity) getActivity()).getDownloadedFragment().addTask(downloadInfo);
                }
            });
        }
        lv_download.setAdapter(downloadAdapter);
        lv_download.setEmptyView(view.findViewById(R.id.iv_empty));

        rl_bot = (RelativeLayout) view.findViewById(R.id.rl_bot);
        ll_deleteall = (LinearLayout) view.findViewById(R.id.ll_deleteall);
        ll_downloadall = (LinearLayout) view.findViewById(R.id.ll_downloadall);
        tv_downloadall = (TextView) view.findViewById(R.id.tv_downloadall);
        DrawableCompat.setTint(DrawableCompat.wrap(((ImageView) view.findViewById(R.id.iv_trash)).getDrawable().mutate()), getResources().getColor(R.color.center_bottom_text_color_red));
        if (isFinished) {
            rl_bot.setVisibility(View.GONE);
        }
        ll_downloadall.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ll_downloadall.isSelected()) {
                    downloadAdapter.downloadAll();
                    ll_downloadall.setSelected(true);
                    tv_downloadall.setText("暂停全部");
                } else {
                    downloadAdapter.pauseAll();
                    ll_downloadall.setSelected(false);
                    tv_downloadall.setText("下载全部");
                }
            }
        });
        ll_deleteall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (downloadInfos.size() == 0) {
                    return;
                }
                new AlertDialog.Builder(getContext())
                        .setTitle("提示")
                        .setMessage("是否要清空所有下载中的任务?")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                downloadAdapter.deleteAllTask();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();

            }
        });
    }

    public void addTask(PolyvDownloadInfo downloadInfo) {
        downloadInfos.add(downloadInfo);
        downloadAdapter.notifyDataSetChanged();
    }

    private List<PolyvDownloadInfo> getTask(List<PolyvDownloadInfo> downloadInfos, boolean isFinished) {
        if (downloadInfos == null) {
            return null;
        }
        List<PolyvDownloadInfo> infos = new ArrayList<>();
        for (PolyvDownloadInfo downloadInfo : downloadInfos) {
            long percent = downloadInfo.getPercent();
            long total = downloadInfo.getTotal();
            // 已下载的百分比
            int progress = 0;
            if (total != 0) {
                progress = (int) (percent * 100 / total);
            }
            if (progress == 100) {
                if (isFinished) {
                    infos.add(downloadInfo);
                }
            } else if (!isFinished) {
                infos.add(downloadInfo);
            }
        }
        return infos;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        downloadAdapter.setDownloadSuccessListener(null);
    }
}
