package com.easefun.polyvsdk.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.activity.PolyvTalkEdittextActivity;
import com.easefun.polyvsdk.activity.PolyvTalkSendActivity;
import com.easefun.polyvsdk.adapter.PolyvTalkListViewAdapter;
import com.easefun.polyvsdk.sub.vlms.auxiliary.PolyvVlmsTransfer;
import com.easefun.polyvsdk.sub.vlms.entity.PolyvAddAnswerInfo;
import com.easefun.polyvsdk.sub.vlms.entity.PolyvAddQuestionInfo;
import com.easefun.polyvsdk.sub.vlms.entity.PolyvCoursesInfo;
import com.easefun.polyvsdk.sub.vlms.entity.PolyvQuestionInfo;
import com.easefun.polyvsdk.sub.vlms.listener.PolyvVlmsApiListener;
import com.easefun.polyvsdk.util.PolyvClipboardUtils;
import com.easefun.polyvsdk.util.PolyvVlmsHelper;

import java.util.LinkedList;
import java.util.List;

public class PolyvTalkFragment extends Fragment {
    // fragmentView
    private View view;
    // 讨论的listView
    private ListView lv_talk;
    private PolyvTalkListViewAdapter adapter;
    private LinkedList<PolyvVlmsHelper.QuestionsDetail> lists;
    // 讨论的布局
    private RelativeLayout rl_bot;
    // 点击的父索引
    private int position;
    // 话题，发送的信息
    private String topic, sendMsg;
    // 传过来课程对象
    private PolyvCoursesInfo.Course course;
    // 加载中控件
    private ProgressBar pb_loading;
    // 空数据控件,重新加载控件
    private TextView tv_empty, tv_reload;
    // question_id
    private String question_id;
    private PolyvVlmsHelper vlmsHelper;

    private void getQuestionsDetail() {
        vlmsHelper.getQuesionsDetail(course.course_id, new PolyvVlmsHelper.MyQuestionDetailListener() {
            @Override
            public void fail(Throwable t) {
                pb_loading.setVisibility(View.GONE);
                tv_reload.setVisibility(View.VISIBLE);
            }

            @Override
            public void success(List<PolyvVlmsHelper.QuestionsDetail> questionsDetails) {
                pb_loading.setVisibility(View.GONE);
                PolyvTalkFragment.this.lists.clear();
                PolyvTalkFragment.this.lists.addAll(questionsDetails);
                if (PolyvTalkFragment.this.lists.size() == 0)
                    tv_empty.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void addNewQuestion() {
        String transferMsg = PolyvVlmsTransfer.toHtmlSign(sendMsg);
        vlmsHelper.addNewQuestion(course.course_id, topic, transferMsg, new PolyvVlmsApiListener.AddNewQuestionListener() {
            @Override
            public void fail(Throwable t) {
                Toast.makeText(getContext(), "发表讨论失败，请重试！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void success(PolyvAddQuestionInfo polyvAddQuestionInfo) {
                tv_empty.setVisibility(View.GONE);
                PolyvVlmsHelper.QuestionsDetail questionsDetail = vlmsHelper.addQuestionInfoToQuestionDetail(polyvAddQuestionInfo);
                lists.addFirst(questionsDetail);
                adapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "发送成功！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addNewAnswer() {
        String transferMsg = PolyvVlmsTransfer.toHtmlSign(sendMsg);
        vlmsHelper.addNewAnswer(course.course_id, question_id, transferMsg, new PolyvVlmsApiListener.AddNewAnswerListener() {
            @Override
            public void fail(Throwable t) {
                Toast.makeText(getContext(), "回复失败，请重试！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void success(PolyvAddAnswerInfo polyvAddAnswerInfo) {
                PolyvVlmsHelper.QuestionsDetail.AnswerDetail answerDetail = vlmsHelper.addAnswerInfoToAnswerDetail(polyvAddAnswerInfo);
                lists.get(position).answers.addFirst(answerDetail);
                adapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "回复成功！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void findIdAndNew() {
        lv_talk = (ListView) view.findViewById(R.id.lv_talk);
        rl_bot = (RelativeLayout) view.findViewById(R.id.rl_bot);
        pb_loading = (ProgressBar) view.findViewById(R.id.pb_loading);
        tv_empty = (TextView) view.findViewById(R.id.tv_empty);
        tv_reload = (TextView) view.findViewById(R.id.tv_reload);
        lists = new LinkedList<>();
        vlmsHelper = new PolyvVlmsHelper();
    }

    private void initView() {
        // fragment在onCreate之后才可以获取
        course = getArguments().getParcelable("course");
        if (course == null)
            return;
        getQuestionsDetail();
        adapter = new PolyvTalkListViewAdapter(getActivity(), lists);
        lv_talk.setAdapter(adapter);
        lv_talk.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                PolyvQuestionInfo.Question polyvQuestion = lists.get(position).question;
                PolyvClipboardUtils.copyToClipboard(getContext(), Html.fromHtml(polyvQuestion.content));
                Toast.makeText(getContext(), "已复制" + polyvQuestion.nickname + "的评论", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        lv_talk.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PolyvQuestionInfo.Question polyvQuestion = lists.get(position).question;
                Intent intent = new Intent(getActivity(), PolyvTalkEdittextActivity.class);
                intent.putExtra("question_id", polyvQuestion.question_id);
                intent.putExtra("position", position);
                intent.putExtra("nickname", polyvQuestion.nickname);
                getActivity().startActivityForResult(intent, 13);
                getActivity().overridePendingTransition(R.anim.polyv_activity_alpha_in, 0);
            }
        });

        rl_bot.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PolyvTalkSendActivity.class);
                startActivityForResult(intent, 13);
            }
        });
        tv_reload.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                pb_loading.setVisibility(View.VISIBLE);
                tv_reload.setVisibility(View.GONE);
                getQuestionsDetail();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (view == null)
            view = inflater.inflate(R.layout.polyv_fragment_tab_talk, container, false);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case 19:
                // 回答
                question_id = data.getStringExtra("question_id");
                position = data.getIntExtra("position", -1);
                sendMsg = data.getStringExtra("sendMsg");
                addNewAnswer();
                break;

            case 12:
                // 问题
                topic = data.getStringExtra("topic");
                sendMsg = data.getStringExtra("sendMsg");
                addNewQuestion();
                break;
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
}
