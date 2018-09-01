package com.easefun.polyvsdk.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.easefun.polyvsdk.PolyvSDKClient;
import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.adapter.PolyvHotCoursesGridViewAdapter;
import com.easefun.polyvsdk.permission.PolyvPermission;
import com.easefun.polyvsdk.sub.vlms.entity.PolyvAddOrderInfo;
import com.easefun.polyvsdk.sub.vlms.entity.PolyvCoursesInfo;
import com.easefun.polyvsdk.sub.vlms.listener.PolyvVlmsApiListener;
import com.easefun.polyvsdk.sub.vlms.main.PolyvVlmsTestData;
import com.easefun.polyvsdk.util.PolyvVlmsHelper;
import com.easefun.polyvsdk.view.PolyvSimpleSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class PolyvMainActivity extends Activity implements OnClickListener {
    public static final String IS_VLMS_ONLINE = "isVlmsOnline";
    // 热门课程的gridView
    private GridView gv_hc;
    private PolyvHotCoursesGridViewAdapter adapter;
    private List<PolyvVlmsHelper.CoursesDetail> lists;
    // 在线视频按钮,上传按钮,缓存按钮
    private ImageView iv_online, iv_uplaod, iv_download;
    // 加载中控件
    private ProgressBar pb_loading;
    // 空数据控件,重新加载控件
    private TextView tv_empty, tv_reload;
    // 下拉刷新控件
    private PolyvSimpleSwipeRefreshLayout srl_bot;
    private TextView tv_guide;
    private PolyvVlmsHelper vlmsHelper;
    private PolyvPermission polyvPermission = null;
    private PolyvCoursesInfo.Course course = null;

    private void findIdAndNew() {
        gv_hc = (GridView) findViewById(R.id.gv_hc);
        iv_online = (ImageView) findViewById(R.id.iv_online);
        iv_uplaod = (ImageView) findViewById(R.id.iv_upload);
        iv_download = (ImageView) findViewById(R.id.iv_download);
        pb_loading = (ProgressBar) findViewById(R.id.pb_loading);
        tv_empty = (TextView) findViewById(R.id.tv_empty);
        tv_reload = (TextView) findViewById(R.id.tv_reload);
        srl_bot = (PolyvSimpleSwipeRefreshLayout) findViewById(R.id.srl_bot);
        tv_guide = (TextView) findViewById(R.id.tv_guide);
        lists = new ArrayList<>();
        vlmsHelper = new PolyvVlmsHelper();
    }

    private void getCoursesDetail() {
        // 获取课程信息和老师的名称
        vlmsHelper.getCoursesDetail(20, PolyvCoursesInfo.IS_FREE_YES, new PolyvVlmsHelper.GetCoursesDetailListener() {

            @Override
            public void fail(Throwable t) {
                pb_loading.setVisibility(View.GONE);
                tv_empty.setVisibility(View.GONE);
                tv_reload.setVisibility(View.GONE);
                srl_bot.setRefreshing(false);

                srl_bot.setEnabled(false);
                tv_reload.setVisibility(View.VISIBLE);
                if (PolyvMainActivity.this.lists.size() > 0) {
                    PolyvMainActivity.this.lists.clear();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void success(List<PolyvVlmsHelper.CoursesDetail> coursesDetails) {
                pb_loading.setVisibility(View.GONE);
                tv_empty.setVisibility(View.GONE);
                tv_reload.setVisibility(View.GONE);
                srl_bot.setRefreshing(false);

                srl_bot.setEnabled(true);
                if (coursesDetails.size() == 0)
                    tv_empty.setVisibility(View.VISIBLE);
                PolyvMainActivity.this.lists.clear();
                PolyvMainActivity.this.lists.addAll(coursesDetails);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void addOrder(String course_id) {
        // 已购买的课程课程才能评论
        vlmsHelper.addOrder(course_id, new PolyvVlmsApiListener.AddOrderListener() {
            @Override
            public void fail(Throwable t) {
                if ("已购买课程".equals(t.getMessage()))
                    Toast.makeText(PolyvMainActivity.this, "购买课程成功", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(PolyvMainActivity.this, "购买课程失败\n" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void success(PolyvAddOrderInfo polyvAddOrderInfo) {
                Toast.makeText(PolyvMainActivity.this, "购买课程成功", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        if (!PolyvVlmsTestData.USERID.equals(PolyvSDKClient.getInstance().getUserId())) {
            srl_bot.setVisibility(View.GONE);
            tv_guide.setVisibility(View.VISIBLE);
            tv_guide.setText("您的userId是：" + PolyvSDKClient.getInstance().getUserId() + "\n请点击左上角的按钮进入视频列表页查看您的视频");
        } else {
            getCoursesDetail();
        }
        adapter = new PolyvHotCoursesGridViewAdapter(this, lists);
        gv_hc.setAdapter(adapter);
        gv_hc.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                course = lists.get(position).course;
                polyvPermission.applyPermission(PolyvMainActivity.this, PolyvPermission.OperationType.playAndDownload);
            }
        });
        srl_bot.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light);
        srl_bot.setEnabled(false);
        srl_bot.setChildView(gv_hc);
        srl_bot.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                getCoursesDetail();
            }
        });
        iv_online.setOnClickListener(this);
        iv_uplaod.setOnClickListener(this);
        iv_download.setOnClickListener(this);
        tv_reload.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.polyv_activity_main);
        findIdAndNew();
        initView();

        polyvPermission = new PolyvPermission();
        polyvPermission.setResponseCallback(new PolyvPermission.ResponseCallback() {
            @Override
            public void callback(@NonNull PolyvPermission.OperationType type) {
                gotoActivity(type.getNum());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        vlmsHelper.destroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_reload:
                tv_reload.setVisibility(View.GONE);
                pb_loading.setVisibility(View.VISIBLE);
                getCoursesDetail();
                break;
            case R.id.iv_online:
                polyvPermission.applyPermission(this, PolyvPermission.OperationType.play);
                break;
            case R.id.iv_download:
                polyvPermission.applyPermission(this, PolyvPermission.OperationType.download);
                break;
            case R.id.iv_upload:
                polyvPermission.applyPermission(this, PolyvPermission.OperationType.upload);
                break;
        }
    }

    private void gotoActivity(int type) {
        PolyvPermission.OperationType OperationType = PolyvPermission.OperationType.getOperationType(type);
        switch (OperationType) {
            case play:
                startActivity(new Intent(PolyvMainActivity.this, PolyvOnlineVideoActivity.class));
                break;
            case download:
                startActivity(new Intent(PolyvMainActivity.this, PolyvDownloadActivity.class));
                break;
            case upload:
                startActivity(new Intent(PolyvMainActivity.this, PolyvUploadActivity.class));
                break;
            case playAndDownload:
                // 为免费的课程添加订单
                addOrder(course.course_id);
                Intent intent = new Intent(PolyvMainActivity.this, PolyvPlayerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean(PolyvMainActivity.IS_VLMS_ONLINE, true);
                bundle.putParcelable("course", course);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }

    /**
     * This is the method that is hit after the user accepts/declines the
     * permission you requested. For the purpose of this example I am showing a "success" header
     * when the user accepts the permission and a snackbar when the user declines it.  In your application
     * you will want to handle the accept/decline in a way that makes sense.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(final int requestCode, String[] permissions, int[] grantResults) {
        if (polyvPermission.operationHasPermission(requestCode)) {
            gotoActivity(requestCode);
        } else {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("提示");
                        builder.setMessage("需要权限被拒绝，是否跳转到权限设置？");
                        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Toast.makeText(PolyvMainActivity.this, "点击权限，并打开全部权限", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.fromParts("package", getPackageName(), null));
                                startActivityForResult(intent, requestCode);
                                dialog.dismiss();
                            }
                        });

                        builder.setNegativeButton("取消", null);
                        builder.setCancelable(true);
                        builder.show();
                        return;
                    }
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (polyvPermission.operationHasPermission(requestCode)) {
            gotoActivity(requestCode);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("请开启功能需要的权限，再使用该功能。");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.dismiss();
                }
            });

            builder.setCancelable(true);
            builder.show();
        }
    }
}
