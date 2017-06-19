package com.easefun.polyvsdk.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.easefun.polyvsdk.PolyvBitRate;
import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.activity.PolyvDownloadActivity;
import com.easefun.polyvsdk.activity.PolyvPlayerActivity;
import com.easefun.polyvsdk.adapter.PolyvCurriculumListViewAdapter;
import com.easefun.polyvsdk.permission.PolyvPermission;
import com.easefun.polyvsdk.sub.vlms.entity.PolyvCoursesInfo;
import com.easefun.polyvsdk.util.PolyvVlmsHelper;

import java.util.ArrayList;
import java.util.List;

public class PolyvCurriculumFragment extends Fragment implements OnClickListener {
    // viewpager切换的时候，fragment执行销毁View方法，但fragment对象没有被销毁
    // 底部下载按钮
    private RelativeLayout rl_bot;
    // 底部两边下载按钮布局，中间的下载按钮布局，选择清晰度布局，顶部布局
    private LinearLayout ll_download, ll_center, ll_selbit, ll_top;
    // 课程目录的listView
    private ListView lv_cur;
    private PolyvCurriculumListViewAdapter adapter;
    private List<PolyvVlmsHelper.CurriculumsDetail> lists;
    // fragmentView
    private View view;
    // 取消按钮，下载全部按钮
    private TextView tv_cancle, tv_all;
    // 传过来课程对象
    private PolyvCoursesInfo.Course course;
    // 加载中控件
    private ProgressBar pb_loading;
    // 空数据控件,重新加载控件，选择清晰度控件
    private TextView tv_empty, tv_reload, tv_selbit;
    // popupwindow
    private PopupWindow popupWindow;
    // popupwindowView
    private View popupwindow_view;
    // 清晰度控件
    private TextView tv_hd, tv_sd, tv_flu;
    // 当前选择的下载码率
    private int currentSelcetBitrate = 3;
    private PolyvVlmsHelper vlmsHelper;

    private PolyvPermission polyvPermission = null;
    private String videoId = "";
    private static final int SETTING = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (view == null)
            view = inflater.inflate(R.layout.polyv_fragment_tab_cur, container, false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null)
            adapter.initSelect(currentSelcetBitrate);
    }

    private void findIdAndNew() {
        lv_cur = (ListView) view.findViewById(R.id.lv_cur);
        rl_bot = (RelativeLayout) view.findViewById(R.id.rl_bot);
        ll_center = (LinearLayout) view.findViewById(R.id.ll_center);
        ll_download = (LinearLayout) view.findViewById(R.id.ll_download);
        tv_cancle = (TextView) view.findViewById(R.id.tv_cancle);
        tv_all = (TextView) view.findViewById(R.id.tv_all);
        pb_loading = (ProgressBar) view.findViewById(R.id.pb_loading);
        tv_empty = (TextView) view.findViewById(R.id.tv_empty);
        tv_reload = (TextView) view.findViewById(R.id.tv_reload);
        tv_selbit = (TextView) view.findViewById(R.id.tv_selbit);
        ll_selbit = (LinearLayout) view.findViewById(R.id.ll_selbit);
        ll_top = (LinearLayout) view.findViewById(R.id.ll_top);
        lists = new ArrayList<>();
        vlmsHelper = new PolyvVlmsHelper();
    }

    private void getCurriculumDetail() {
        vlmsHelper.getCurriculumDetail(course.course_id, new PolyvVlmsHelper.MyCurriculumDetailListener() {
            @Override
            public void fail(Throwable t) {
                pb_loading.setVisibility(View.GONE);
                tv_reload.setVisibility(View.VISIBLE);
            }

            @Override
            public void success(final List<PolyvVlmsHelper.CurriculumsDetail> curriculumsDetails) {
                // 注：这里是从子线程回调过来的
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pb_loading.setVisibility(View.GONE);
                        PolyvCurriculumFragment.this.lists.clear();
                        PolyvCurriculumFragment.this.lists.addAll(curriculumsDetails);
                        if (PolyvCurriculumFragment.this.lists.size() == 0)
                            tv_empty.setVisibility(View.VISIBLE);
                        adapter.initSelect(currentSelcetBitrate);
                    }
                });
            }
        });
    }

    private void initView() {
        // fragment在onCreate之后才可以获取
        course = getArguments().getParcelable("course");
        if (course == null)
            return;
        getCurriculumDetail();
        initPopupWindow();
        adapter = new PolyvCurriculumListViewAdapter(lists, getContext());
        lv_cur.setAdapter(adapter);
        lv_cur.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!adapter.getSideIconVisible()) {
                    videoId = lists.get(position).lecture.vid;
                    polyvPermission.applyPermission(getActivity(), PolyvPermission.OperationType.play);
                } else {
                    adapter.setSelect(position, currentSelcetBitrate);
                }
            }
        });
        lv_cur.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (!adapter.getSideIconVisible()) {
                    ll_top.setVisibility(View.VISIBLE);
                    ll_center.setVisibility(View.GONE);
                    ll_download.setVisibility(View.VISIBLE);
                    adapter.setSideIconVisible(true);
                } else {
                    startActivity(new Intent(getContext(), PolyvDownloadActivity.class));
                }
                return true;
            }
        });
        rl_bot.setOnClickListener(this);
        tv_cancle.setOnClickListener(this);
        tv_all.setOnClickListener(this);
        tv_reload.setOnClickListener(this);
        ll_selbit.setOnClickListener(this);
    }

    private void initPopupWindow() {
        popupwindow_view = LayoutInflater.from(getContext()).inflate(R.layout.polyv_popupwindow_cur_bit, null);
        popupWindow = new PopupWindow(popupwindow_view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(R.style.popupwindow);
        tv_hd = (TextView) popupwindow_view.findViewById(R.id.tv_hd);
        tv_sd = (TextView) popupwindow_view.findViewById(R.id.tv_sd);
        tv_flu = (TextView) popupwindow_view.findViewById(R.id.tv_flu);
        tv_hd.setOnClickListener(this);
        tv_sd.setOnClickListener(this);
        tv_flu.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (course == null) {
            findIdAndNew();
            initView();
            polyvPermission = new PolyvPermission();
            polyvPermission.setResponseCallback(new PolyvPermission.ResponseCallback() {
                @Override
                public void callback() {
                    requestPermissionWriteSettings();
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (view != null)
            ((ViewGroup) view.getParent()).removeView(view);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        vlmsHelper.destroy();
    }

    // 改变选择的清晰度
    private void changeSelcet(int bitrate) {
        currentSelcetBitrate = bitrate;
        switch (bitrate) {
            case 1:
                tv_selbit.setText(tv_flu.getText());
                break;
            case 2:
                tv_selbit.setText(tv_sd.getText());
                break;
            case 3:
                tv_selbit.setText(tv_hd.getText());
                break;
        }
        adapter.initSelect(bitrate);
        popupWindow.dismiss();
    }

    public boolean isSideIconVisible() {
        if (adapter != null)
            return adapter.getSideIconVisible();
        return false;
    }

    public void setSideIconVisible(boolean visible) {
        if (adapter != null) {
            if (visible)
                sideIconVisible();
            else
                sideIconGone();
        }
    }

    private void sideIconVisible() {
        if (lists.size() == 0)
            return;
        ll_top.setVisibility(View.VISIBLE);
        ll_download.setVisibility(View.VISIBLE);
        ll_center.setVisibility(View.GONE);
        adapter.setSideIconVisible(true);
    }

    private void sideIconGone() {
        adapter.setSideIconVisible(false);
        ll_top.setVisibility(View.GONE);
        ll_download.setVisibility(View.GONE);
        ll_center.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_hd:
                changeSelcet(3);
                break;
            case R.id.tv_sd:
                changeSelcet(2);
                break;
            case R.id.tv_flu:
                changeSelcet(1);
                break;
            case R.id.rl_bot:
                sideIconVisible();
                break;
            case R.id.tv_cancle:
                sideIconGone();
                break;
            case R.id.tv_all:
                for (int i = 0; i < lists.size(); i++)
                    adapter.setSelect(i, currentSelcetBitrate);
                break;
            case R.id.tv_reload:
                pb_loading.setVisibility(View.VISIBLE);
                tv_reload.setVisibility(View.GONE);
                getCurriculumDetail();
                break;
            case R.id.ll_selbit:
                int[] location = new int[2];
                tv_selbit.getLocationOnScreen(location);
                int width = tv_selbit.getMeasuredWidth();
                int height = tv_selbit.getMeasuredHeight();
                popupWindow.showAtLocation(popupwindow_view, Gravity.NO_GRAVITY, location[0] + width * 3 / 4,
                        location[1] + height * 3 / 4);
                break;
        }
    }

    /**
     * 请求写入设置的权限
     */
    @SuppressLint("InlinedApi")
    private void requestPermissionWriteSettings() {
        if (!PolyvPermission.canMakeSmores()) {
            ((PolyvPlayerActivity) getActivity()).play(videoId, PolyvBitRate.ziDong.getNum(), true, false);
        } else if (Settings.System.canWrite(this.getActivity())) {
            ((PolyvPlayerActivity) getActivity()).play(videoId, PolyvBitRate.ziDong.getNum(), true, false);
        } else {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + this.getActivity().getPackageName()));
            startActivityForResult(intent, SETTING);
        }
    }

    @Override
    @SuppressLint("InlinedApi")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SETTING) {
            if (Settings.System.canWrite(this.getActivity())) {
                ((PolyvPlayerActivity) getActivity()).play(videoId, PolyvBitRate.ziDong.getNum(), false, false);
            } else {
                new AlertDialog.Builder(this.getActivity())
                        .setTitle("showPermissionInternet")
                        .setMessage(Settings.ACTION_MANAGE_WRITE_SETTINGS + " not granted")
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
            }
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (polyvPermission.operationHasPermission(requestCode)) {
            requestPermissionWriteSettings();
        } else {
            polyvPermission.makePostRequestSnack();
        }
    }


}
