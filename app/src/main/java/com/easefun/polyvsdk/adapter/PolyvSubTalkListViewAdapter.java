package com.easefun.polyvsdk.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.util.PolyvRoundDisplayerUtils;
import com.easefun.polyvsdk.util.PolyvTextImageLoader;
import com.easefun.polyvsdk.util.PolyvTimeUtils;
import com.easefun.polyvsdk.util.PolyvVlmsHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.LinkedList;
import java.util.List;

import pl.droidsonroids.gif.GifSpanTextView;

public class PolyvSubTalkListViewAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private ViewHolder viewHolder;
    private List<PolyvVlmsHelper.QuestionsDetail.AnswerDetail> lists;
    private DisplayImageOptions options;
    private PolyvTextImageLoader textImageLoader;

    public PolyvSubTalkListViewAdapter(Context context, List<PolyvVlmsHelper.QuestionsDetail.AnswerDetail> lists) {
        this.context = context;
        this.lists = lists;
        if (this.lists == null)
            this.lists = new LinkedList<>();
        this.inflater = LayoutInflater.from(context);
        this.textImageLoader = new PolyvTextImageLoader(context);
        this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.polyv_avatar_def) // resource
                // or
                // drawable
                .showImageForEmptyUri(R.drawable.polyv_avatar_def) // resource or
                // drawable
                .showImageOnFail(R.drawable.polyv_avatar_def) // resource or drawable
                .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true).cacheOnDisk(true)
                .displayer(new PolyvRoundDisplayerUtils(0)).build();
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
        // 如果是ListView，那么会由于无法测量高度，导致这个方法不断地执行
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.polyv_listview_talk_sub_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.iv_avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
            viewHolder.tv_msg = (GifSpanTextView) convertView.findViewById(R.id.tv_msg);
            viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        PolyvVlmsHelper.QuestionsDetail.AnswerDetail polyvAnswer = lists.get(position);
        SpannableStringBuilder span = polyvAnswer.content_display;
        span.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.center_bottom_text_color_blue)), 0, polyvAnswer.answer.nickname.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textImageLoader.displayTextImage(span, viewHolder.tv_msg);
        viewHolder.tv_time.setText(PolyvTimeUtils.friendlyTime(polyvAnswer.answer.last_modified));
        ImageLoader.getInstance().displayImage(polyvAnswer.answer.avatar, viewHolder.iv_avatar, options);
        return convertView;
    }

    private class ViewHolder {
        ImageView iv_avatar;
        GifSpanTextView tv_msg;
        TextView tv_time;
    }

}
