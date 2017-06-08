package com.easefun.polyvsdk.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.activity.PolyvTalkEdittextActivity;
import com.easefun.polyvsdk.sub.vlms.entity.PolyvQuestionInfo;
import com.easefun.polyvsdk.util.PolyvClipboardUtils;
import com.easefun.polyvsdk.util.PolyvRoundDisplayerUtils;
import com.easefun.polyvsdk.util.PolyvTextImageLoader;
import com.easefun.polyvsdk.util.PolyvTimeUtils;
import com.easefun.polyvsdk.util.PolyvVlmsHelper;
import com.easefun.polyvsdk.view.LinearListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import pl.droidsonroids.gif.GifSpanTextView;


public class PolyvTalkListViewAdapter extends BaseAdapter {
    private Activity context;
    private List<PolyvVlmsHelper.QuestionsDetail> lists;
    private LayoutInflater inflater;
    private ViewHolder viewHolder;
    private DisplayImageOptions options;
    private PolyvTextImageLoader textImageLoader;

    public PolyvTalkListViewAdapter(Activity context, List<PolyvVlmsHelper.QuestionsDetail> lists) {
        this.context = context;
        this.lists = lists;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.polyv_listview_talk_item, null);
            viewHolder = new ViewHolder();
            viewHolder.iv_avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
            viewHolder.tv_msg = (GifSpanTextView) convertView.findViewById(R.id.tv_msg);
            viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            viewHolder.tv_topic = (TextView) convertView.findViewById(R.id.tv_topic);
            viewHolder.sublv_talk = (LinearListView) convertView.findViewById(R.id.sublv_talk);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final int pPosition = position;
        final PolyvQuestionInfo.Question polyvQuestion = lists.get(pPosition).question;
        final List<PolyvVlmsHelper.QuestionsDetail.AnswerDetail> polyvAnswers = lists.get(pPosition).answers;
        if (polyvAnswers.size() > 0) {
            viewHolder.adapter = new PolyvSubTalkListViewAdapter(context, polyvAnswers);
            viewHolder.sublv_talk.setVisibility(View.VISIBLE);
            viewHolder.sublv_talk.setAdapter(viewHolder.adapter);
            viewHolder.sublv_talk.setOnItemLongClickListener(new LinearListView.OnItemLongClickListener() {

                @Override
                public boolean onItemLongClick(LinearListView parent, View view, int position, long id) {
                    PolyvVlmsHelper.QuestionsDetail.AnswerDetail polyvAnswer = polyvAnswers.get(position);
                    PolyvClipboardUtils.copyToClipboard(context, Html.fromHtml(polyvAnswer.answer.content));
                    Toast.makeText(context, "已复制" + polyvAnswer.answer.nickname + "的评论", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
            viewHolder.sublv_talk.setOnItemClickListener(new LinearListView.OnItemClickListener() {

                @Override
                public void onItemClick(LinearListView parent, View view, int position, long id) {
                    Intent intent = new Intent(context, PolyvTalkEdittextActivity.class);
                    intent.putExtra("question_id", polyvQuestion.question_id);
                    intent.putExtra("position", pPosition);
                    // 由于接口只能回复发表讨论的人，故这里使用发表讨论的人的昵称
                    intent.putExtra("nickname", polyvQuestion.nickname);
                    context.startActivityForResult(intent, 13);
                }
            });
        } else {
            viewHolder.sublv_talk.setVisibility(View.GONE);
        }
        SpannableStringBuilder span = lists.get(pPosition).content_display;
        span.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.center_bottom_text_color_blue)), 0, polyvQuestion.nickname.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textImageLoader.displayTextImage(span, viewHolder.tv_msg);
        viewHolder.tv_topic.setText(polyvQuestion.title);
        viewHolder.tv_time.setText(PolyvTimeUtils.friendlyTime(polyvQuestion.last_modified));
        ImageLoader.getInstance().displayImage(polyvQuestion.avatar, viewHolder.iv_avatar, options);
        return convertView;
    }

    private class ViewHolder {
        ImageView iv_avatar;
        GifSpanTextView tv_msg;
        TextView tv_topic, tv_time;
        LinearListView sublv_talk;
        PolyvSubTalkListViewAdapter adapter;
    }
}
