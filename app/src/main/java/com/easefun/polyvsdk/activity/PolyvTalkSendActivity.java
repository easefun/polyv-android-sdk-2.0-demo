package com.easefun.polyvsdk.activity;

import com.easefun.polyvsdk.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class PolyvTalkSendActivity extends Activity {
	// 返回按钮
	private ImageView iv_finish;
	// 发送按钮
	private TextView tv_send;
	// 话题，内容
	private EditText et_topic, et_msg;

	private void findIdAndNew() {
		iv_finish = (ImageView) findViewById(R.id.iv_finish);
		tv_send = (TextView) findViewById(R.id.tv_send);
		et_topic = (EditText) findViewById(R.id.et_topic);
		et_msg = (EditText) findViewById(R.id.et_msg);
	}

	private void initView() {
		iv_finish.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		et_topic.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.toString().trim().length() > 0 && et_msg.getText().toString().trim().length() > 0) {
					tv_send.setEnabled(true);
					tv_send.setTextColor(getResources().getColorStateList(R.color.polyv_send_text_color));
				} else {
					tv_send.setEnabled(false);
					tv_send.setTextColor(getResources().getColor(R.color.top_right_text_color_gray));
				}
			}
		});
		et_msg.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.toString().trim().length() > 0 && et_topic.getText().toString().trim().length() > 0) {
					tv_send.setEnabled(true);
					tv_send.setTextColor(getResources().getColorStateList(R.color.polyv_send_text_color));
				} else {
					tv_send.setEnabled(false);
					tv_send.setTextColor(getResources().getColor(R.color.top_right_text_color_gray));
				}
			}
		});
		tv_send.setEnabled(false);
		tv_send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("topic", et_topic.getText().toString());
				intent.putExtra("sendMsg", et_msg.getText().toString());
				setResult(12, intent);
				finish();
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.polyv_activity_talk_send);
		findIdAndNew();
		initView();
	}
}
