package com.easefun.polyvsdk.view;

import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;

public class PolyvSimpleSwipeRefreshLayout extends SwipeRefreshLayout {
	// 包含的View
	private View view;

	public PolyvSimpleSwipeRefreshLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PolyvSimpleSwipeRefreshLayout(Context context) {
		super(context);
	}

	public void setChildView(View view) {
		this.view = view;
	}

	@Override
	public boolean canChildScrollUp() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if (view instanceof AbsListView) {
				final AbsListView absListView = (AbsListView) view;
				return absListView.getChildCount() > 0 && (absListView.getFirstVisiblePosition() > 0
						|| absListView.getChildAt(0).getTop() < absListView.getPaddingTop());
			} else {
				return view.getScrollY() > 0;
			}
		} else {
			return ViewCompat.canScrollVertically(view, -1);
		}
	}
}
