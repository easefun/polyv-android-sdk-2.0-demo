package com.easefun.polyvsdk.fragment;

import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.sub.vlms.entity.PolyvCoursesInfo;
import com.easefun.polyvsdk.util.PolyvScreenUtils;
import com.easefun.polyvsdk.util.PolyvShareUtils;

public class PolyvPlayerTopFragment extends Fragment implements View.OnClickListener {
    //fragmentView
    private View view;
    // 返回按钮,分享按钮
    private ImageView iv_finish, iv_share;
    // 顶部布局
    private RelativeLayout rl_top;
    // popupwindowView
    private View popupwindow_view;
    // popupwindow
    private PopupWindow popupWindow;
    // 分享控件
    private LinearLayout ll_shareqq, ll_sharewechat, ll_shareweibo;
    private TextView tv_title;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null)
            view = inflater.inflate(R.layout.polyv_fragment_player_top, container, false);
        return view;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (PolyvScreenUtils.isLandscape(getActivity())) {
            rl_top.setVisibility(View.GONE);
        } else {
            rl_top.setVisibility(View.VISIBLE);
        }
    }

    private void findIdAndNew() {
        iv_finish = (ImageView) view.findViewById(R.id.iv_finish);
        iv_share = (ImageView) view.findViewById(R.id.iv_share);
        rl_top = (RelativeLayout) view.findViewById(R.id.rl_top);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
    }

    private void initView() {
        initPopupWindow();
        PolyvCoursesInfo.Course course = getArguments().getParcelable("course");
        String title = "";
        if (course != null)
            title = course.title;
        tv_title.setText(title);
        tv_title.requestFocus();
        iv_finish.setOnClickListener(this);
        iv_share.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findIdAndNew();
        initView();
    }

    private void initPopupWindow() {
        popupwindow_view = LayoutInflater.from(getActivity()).inflate(R.layout.polyv_popupwindow_player_share, null);
        popupWindow = new PopupWindow(popupwindow_view, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(R.style.popupwindow);
        ll_shareqq = (LinearLayout) popupwindow_view.findViewById(R.id.ll_shareqq);
        ll_sharewechat = (LinearLayout) popupwindow_view.findViewById(R.id.ll_sharewechat);
        ll_shareweibo = (LinearLayout) popupwindow_view.findViewById(R.id.ll_shareweibo);
        ll_shareqq.setOnClickListener(this);
        ll_sharewechat.setOnClickListener(this);
        ll_shareweibo.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_share:
                popupWindow.showAsDropDown(iv_share, 0, 1);
                break;
            case R.id.iv_finish:
                getActivity().finish();
                break;
            case R.id.ll_shareqq:
                PolyvShareUtils.shareQQFriend(getActivity(), "", "test", PolyvShareUtils.TEXT, null);
                break;
            case R.id.ll_sharewechat:
                PolyvShareUtils.shareWeChatFriend(getActivity(), "", "test", PolyvShareUtils.TEXT, null);
                break;
            case R.id.ll_shareweibo:
                PolyvShareUtils.shareWeiBo(getActivity(), "", "test", PolyvShareUtils.TEXT, null);
                break;
        }
    }
}
