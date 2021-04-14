package com.easefun.polyvsdk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.sub.vlms.entity.PolyvVlmsCoursesInfo;
import com.easefun.polyvsdk.util.PolyvImageLoader;

import java.util.List;

public class PolyvHotCoursesGridViewAdapter extends BaseAdapter {
	private Context context;
	private List<PolyvVlmsCoursesInfo> lists;
	private LayoutInflater inflater;
	private ViewHolder viewHolder;

	private class ViewHolder {
		ImageView iv_demo;
		TextView tv_teac, tv_title, tv_learn, tv_money;
	}

	public PolyvHotCoursesGridViewAdapter(Context context, List<PolyvVlmsCoursesInfo> lists) {
		this.context = context;
		this.lists = lists;
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return lists.size();
	}

	@Override
	public Object getItem(int position) {
		return lists.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.polyv_gridview_hc_item, null);
			viewHolder = new ViewHolder();
			viewHolder.iv_demo = (ImageView) convertView.findViewById(R.id.iv_demo);
			viewHolder.tv_learn = (TextView) convertView.findViewById(R.id.tv_learn);
			viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
			viewHolder.tv_teac = (TextView) convertView.findViewById(R.id.tv_tv_teac);
			viewHolder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		PolyvVlmsCoursesInfo coursesDetail = lists.get(position);
		viewHolder.tv_title.setText(coursesDetail.getTitle());
		viewHolder.tv_teac.setText(coursesDetail.getTeacherName());
		viewHolder.tv_learn.setText(coursesDetail.getStudentNum() + "人在学");
		if (coursesDetail.getIsFree().equals(PolyvVlmsCoursesInfo.IS_FREE_YES)) {
			viewHolder.tv_money.setText("免费");
			viewHolder.tv_money.setTextColor(context.getResources().getColor(R.color.center_right_text_color_green));
		} else {
			viewHolder.tv_money.setText("￥" + coursesDetail.getPrice());
			viewHolder.tv_money.setTextColor(context.getResources().getColor(R.color.center_bottom_text_color_red));
		}
		PolyvImageLoader.getInstance().loadImageWithCache(context, coursesDetail.getCoverImage(), viewHolder.iv_demo, R.drawable.polyv_demo);
		return convertView;
	}
}
