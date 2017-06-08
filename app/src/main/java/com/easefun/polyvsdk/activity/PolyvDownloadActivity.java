package com.easefun.polyvsdk.activity;

import java.util.ArrayList;
import java.util.List;

import com.easefun.polyvsdk.adapter.PolyvDownloadListViewAdapter;
import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.bean.PolyvDownloadInfo;
import com.easefun.polyvsdk.database.PolyvDownloadSQLiteHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PolyvDownloadActivity extends Activity {
    // 下载listView
    private ListView lv_download;
    private List<PolyvDownloadInfo> lists;
    private PolyvDownloadListViewAdapter adapter;
    // 返回按钮
    private ImageView iv_finish;
    // 底部下载全部按钮
    private RelativeLayout rl_bot;
    // 下载全部的文本控件
    private TextView tv_downloadall;

    private void findIdAndNew() {
        lv_download = (ListView) findViewById(R.id.lv_download);
        iv_finish = (ImageView) findViewById(R.id.iv_finish);
        rl_bot = (RelativeLayout) findViewById(R.id.rl_bot);
        tv_downloadall = (TextView) findViewById(R.id.tv_downloadall);
        lists = new ArrayList<>();
    }

    private void initView() {
        lists.addAll(PolyvDownloadSQLiteHelper.getInstance(this).getAll());
        adapter = new PolyvDownloadListViewAdapter(lists, this, lv_download);
        lv_download.setAdapter(adapter);
        lv_download.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(PolyvDownloadActivity.this).setTitle("提示").setMessage("是否删除该任务")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                adapter.deleteTask(position);
                            }
                        }).setNegativeButton(android.R.string.cancel, null).show();
                return true;
            }
        });
        iv_finish.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rl_bot.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!rl_bot.isSelected()) {
                    adapter.downloadAll();
                    rl_bot.setSelected(true);
                    tv_downloadall.setText("暂停全部");
                } else {
                    adapter.pauseAll();
                    rl_bot.setSelected(false);
                    tv_downloadall.setText("下载全部");
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.polyv_activity_downlaod);
        findIdAndNew();
        initView();
    }
}
