package com.easefun.polyvsdk.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.sub.vlms.entity.PolyvCoursesInfo;
import com.easefun.polyvsdk.util.PolyvVlmsHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class PolyvHotCoursesGridViewAdapter extends BaseAdapter {
	private Context context;
	private List<PolyvVlmsHelper.CoursesDetail> lists;
	private LayoutInflater inflater;
	private ViewHolder viewHolder;
	private DisplayImageOptions options;

	private class ViewHolder {
		ImageView iv_demo;
		TextView tv_teac, tv_title, tv_learn, tv_money;
	}

	public PolyvHotCoursesGridViewAdapter(Context context, List<PolyvVlmsHelper.CoursesDetail> lists) {
		this.context = context;
		this.lists = lists;
		this.inflater = LayoutInflater.from(context);
		this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.polyv_demo) // resource
																								// or
																								// drawable
				.showImageForEmptyUri(R.drawable.polyv_demo) // resource or drawable
				.showImageOnFail(R.drawable.polyv_demo) // resource or drawable
				.bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true).cacheOnDisk(true).build();
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
		PolyvVlmsHelper.CoursesDetail coursesDetail = lists.get(position);
		viewHolder.tv_title.setText(coursesDetail.course.title);
		viewHolder.tv_teac.setText(coursesDetail.teacher.teacher_name);
		viewHolder.tv_learn.setText(coursesDetail.course.student_count + "人在学");
		if (coursesDetail.course.is_free.equals(PolyvCoursesInfo.IS_FREE_YES)) {
			viewHolder.tv_money.setText("免费");
			viewHolder.tv_money.setTextColor(context.getResources().getColor(R.color.center_right_text_color_green));
		} else {
			viewHolder.tv_money.setText("￥" + coursesDetail.course.price);
			viewHolder.tv_money.setTextColor(context.getResources().getColor(R.color.center_bottom_text_color_red));
		}
		ImageLoader.getInstance().displayImage(coursesDetail.course.cover_image, viewHolder.iv_demo, options);
		return convertView;
	}
}
