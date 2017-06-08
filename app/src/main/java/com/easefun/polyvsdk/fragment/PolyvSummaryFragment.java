package com.easefun.polyvsdk.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Layout;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.TextView;

import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.sub.vlms.entity.PolyvCoursesInfo;

public class PolyvSummaryFragment extends Fragment {
    // 标题,价格,学习人数,简介,其他,展开
    private TextView tv_title, tv_money, tv_learn, tv_sum, tv_other, tv_expand;
    // fragmentView
    private View view;
    // 课程对象
    private PolyvCoursesInfo.Course course;

    private void findIdAndNew() {
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_money = (TextView) view.findViewById(R.id.tv_money);
        tv_learn = (TextView) view.findViewById(R.id.tv_learn);
        tv_sum = (TextView) view.findViewById(R.id.tv_sum);
        tv_other = (TextView) view.findViewById(R.id.tv_other);
        tv_expand = (TextView) view.findViewById(R.id.tv_expand);
    }

    private void initView() {
        course = getArguments().getParcelable("course");
        if (course == null)
            return;
        tv_title.setText(course.title);
        tv_learn.setText(course.student_count + "人在学");
        String summary = course.summary;
        if (TextUtils.isEmpty(summary))
            tv_sum.setText("暂无");
        else
            tv_sum.setText(Html.fromHtml(summary));
        tv_other.setText("暂无");
        if (PolyvCoursesInfo.IS_FREE_YES.equals(course.is_free)) {
            tv_money.setText("免费");
            tv_money.setTextColor(getResources().getColor(R.color.center_right_text_color_green));
        } else {
            tv_money.setText("￥" + course.price);
            tv_money.setTextColor(getResources().getColor(R.color.center_bottom_text_color_red));
        }
        tv_sum.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                Layout layout = tv_sum.getLayout();
                int lines = layout.getLineCount();
                if (lines > 3 || (lines > 0 && layout.getEllipsisCount(lines - 1) > 0)) {
                    tv_expand.setVisibility(View.VISIBLE);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    tv_sum.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                else
                    tv_sum.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
        tv_sum.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (tv_expand.getVisibility() == View.GONE)
                    return;
                expandOrCollapse();
            }
        });
        tv_expand.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                expandOrCollapse();
            }
        });
    }

    private void expandOrCollapse() {
        if (!tv_expand.getText().equals("收起")) {
            tv_sum.setMaxLines(Integer.MAX_VALUE);
            tv_sum.setEllipsize(null);
            tv_expand.setText("收起");
        } else {
            tv_sum.setMaxLines(3);
            tv_sum.setEllipsize(TruncateAt.END);
            tv_expand.setText("展开");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (view == null)
            view = inflater.inflate(R.layout.polyv_fragment_tab_sum, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (course == null) {
            findIdAndNew();
            initView();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (view != null)
            ((ViewGroup) view.getParent()).removeView(view);
    }
}
