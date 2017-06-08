package com.easefun.polyvsdk.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.adapter.PolyvUploadListViewAdapter;
import com.easefun.polyvsdk.bean.PolyvUploadInfo;
import com.easefun.polyvsdk.database.PolyvUploadSQLiteHelper;
import com.easefun.polyvsdk.util.GetPathFromUri;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PolyvUploadActivity extends Activity {
    // 上传的listView
    private ListView lv_upload;
    private PolyvUploadListViewAdapter adapter;
    private List<PolyvUploadInfo> lists;
    // 返回按钮
    private ImageView iv_finish;
    // 底部选择本地视频按钮
    private RelativeLayout rl_bot;
    private PolyvUploadSQLiteHelper uploadSQLiteHelper;

    private void findIdAndNew() {
        lv_upload = (ListView) findViewById(R.id.lv_upload);
        iv_finish = (ImageView) findViewById(R.id.iv_finish);
        rl_bot = (RelativeLayout) findViewById(R.id.rl_bot);
        lists = new ArrayList<>();
        uploadSQLiteHelper = PolyvUploadSQLiteHelper.getInstance(this);
    }

    private void initView() {
        lists.addAll(PolyvUploadSQLiteHelper.getInstance(this).getAll());
        adapter = new PolyvUploadListViewAdapter(this, lists, lv_upload);
        lv_upload.setAdapter(adapter);
        lv_upload.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(PolyvUploadActivity.this).setTitle("提示").setMessage("是否从列表中移除该任务")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                adapter.removeTask(position);
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
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                startActivityForResult(Intent.createChooser(intent, "完成操作需使用"), 12);
            }
        });
    }

    // 获取视频的地址并添加到上传列表中
    private void handle(Uri... uris) {
        // 路径
        String filepath;
        for (int i = 0; i < uris.length; i++) {
            // 在图册中上传
            if (uris[i].toString().startsWith("content")) {
                filepath = GetPathFromUri.getPath(this, uris[i]);
            } else {
                // 在文件中选择
                filepath = uris[i].getPath().substring(uris[i].getPath().indexOf("/") + 1);
            }
            File file = new File(filepath);
            String fileName = file.getName();
            // 标题
            String title = fileName.substring(0, fileName.lastIndexOf("."));
            // 描述
            String desc = title;
            // 大小
            long filesize = file.length();
            PolyvUploadInfo uploadInfo = new PolyvUploadInfo(title, desc, filesize, filepath);
            if (!uploadSQLiteHelper.isAdd(uploadInfo)) {
                uploadSQLiteHelper.insert(uploadInfo);
                lists.add(uploadInfo);
                adapter.initUploader();
            } else {
                PolyvUploadActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(PolyvUploadActivity.this, "上传任务已经增加到队列", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void initData() {
        initData(null);
    }

    // 初始化分享上传的视频数据
    private void initData(Intent intent) {
        if (intent == null)
            intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("video/")) {
                Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                if (uri != null)
                    handle(uri);
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("video/") || type.startsWith("*/")) {
                ArrayList<Uri> uris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
                if (uris != null)
                    handle(uris.toArray(new Uri[uris.size()]));
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.polyv_activity_upload);
        findIdAndNew();
        initView();
        initData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initData(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 12:
                if (resultCode == RESULT_OK && data != null) {
                    // 获取文件路径
                    Uri uri = data.getData();
                    handle(uri);
                } else {
                    Toast.makeText(PolyvUploadActivity.this, "视频获取失败", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
