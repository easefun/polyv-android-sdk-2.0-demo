package com.easefun.polyvsdk.fragment;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.util.PolyvScreenUtils;

public class PolyvPlayerTabFragment extends Fragment implements View.OnClickListener {
    //fragmentView
    private View view;
    //viewpagerFragment
    private PolyvPlayerViewPagerFragment viewPagerFragment;
    // tab文本的TextView，用于改变颜色
    private TextView tv_cur, tv_sum, tv_talk;
    // tab的导航线
    private View v_line;
    private int screenWidth;
    private int length;
    private int eLength;
    private LinearLayout.LayoutParams lp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null)
            view = inflater.inflate(R.layout.polyv_fragment_player_tab, container, false);
        return view;
    }

    private void findIdAndNew() {
        tv_cur = (TextView) view.findViewById(R.id.tv_cur);
        tv_sum = (TextView) view.findViewById(R.id.tv_sum);
        tv_talk = (TextView) view.findViewById(R.id.tv_talk);
        v_line = view.findViewById(R.id.v_line);
        viewPagerFragment = (PolyvPlayerViewPagerFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fl_viewpager);
    }

    private void initView() {
        initLineSetting();
        tv_cur.setSelected(true);
        tv_cur.setOnClickListener(this);
        tv_sum.setOnClickListener(this);
        tv_talk.setOnClickListener(this);
    }

    private void initLineSetting() {
        final DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        lp = (LinearLayout.LayoutParams) v_line.getLayoutParams();
        v_line.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(PolyvScreenUtils.isLandscape(getContext()))
                    screenWidth = dm.heightPixels;
                else
                    screenWidth = dm.widthPixels;
                length = v_line.getWidth();
                int sLength = screenWidth / 3;
                eLength = (sLength - length) / 2;
                if (viewPagerFragment.getCurrentIndex() == 0)
                    resetViewStatus(0);
                if (Build.VERSION.SDK_INT >= 16)
                    getView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                else
                    getView().getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    public void resetViewStatus(int arg0) {
        tv_cur.setSelected(false);
        tv_sum.setSelected(false);
        tv_talk.setSelected(false);
        tv_cur.setTextColor(Color.BLACK);
        tv_sum.setTextColor(Color.BLACK);
        tv_talk.setTextColor(Color.BLACK);
        switch (arg0) {
            case 0:
                tv_cur.setSelected(true);
                tv_cur.setTextColor(getResources().getColor(R.color.polyv_tab_text_color));
                lp.leftMargin = (int) (arg0 * (length)) + 1 * eLength;
                break;
            case 1:
                tv_sum.setSelected(true);
                tv_sum.setTextColor(getResources().getColor(R.color.polyv_tab_text_color));
                lp.leftMargin = (int) (arg0 * (length)) + 3 * eLength;
                break;
            case 2:
                tv_talk.setSelected(true);
                tv_talk.setTextColor(getResources().getColor(R.color.polyv_tab_text_color));
                lp.leftMargin = (int) (arg0 * (length)) + 5 * eLength;
                break;
        }
        v_line.setLayoutParams(lp);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findIdAndNew();
        initView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_talk:
                viewPagerFragment.setCurrentItem(2);
                break;
            case R.id.tv_sum:
                viewPagerFragment.setCurrentItem(1);
                break;
            case R.id.tv_cur:
                viewPagerFragment.setCurrentItem(0);
                break;
        }
    }
}
