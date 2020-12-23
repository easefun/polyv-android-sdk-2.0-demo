package com.easefun.polyvsdk.player;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easefun.polyvsdk.PolyvQuestionUtil;
import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.util.PolyvImageLoader;
import com.easefun.polyvsdk.video.PolyvVideoView;
import com.easefun.polyvsdk.vo.PolyvQAFormatVO;
import com.easefun.polyvsdk.vo.PolyvQuestionChoicesVO;
import com.easefun.polyvsdk.vo.PolyvQuestionVO;

import java.util.ArrayList;
import java.util.List;

/**
 * 问答视图，已经废弃
 * @author TanQu 2016-1-25
 * @see PolyvPlayerAnswerView
 */
@Deprecated
public class PolyvPlayerQuestionView extends RelativeLayout implements OnCheckedChangeListener {

	private Context context = null;
	private PolyvVideoView polyvVideoView = null;
	private TextView passBtn = null;
	private LinearLayout questionLayout = null;
	private LinearLayout choicesRadioLayout = null;
	private LinearLayout choicesCheckLayout = null;
	private List<LinearLayout> answerRadioLayoutList = null;
	private List<RadioButton> answerRadioList = null;
	private List<LinearLayout> answerCheckLayoutList = null;
	private List<CheckBox> answerCheckList = null;
	private int rightAnswerNum = 0;
	private static final int PLEASE_SELECT_MSG = 1;
	private static final int ANSWER_TIPS_MSG = 2;
	private Handler handler = new Handler(Looper.getMainLooper()) {

		@Override
		public void handleMessage(Message msg) {
			if (!((Activity) context).isFinishing()) {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setMessage(msg.getData().getString("msg"));
				builder.setCancelable(false);
				
				switch (msg.what) {
					case PLEASE_SELECT_MSG:
						builder.setTitle("提示");
						builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								dialog.dismiss();
							}
						});
						break;
					case ANSWER_TIPS_MSG:
						builder.setTitle("答案提示");
						builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								dialog.dismiss();
								//重要，调用此方法，才会继续问答逻辑
								polyvVideoView.answerQuestionFault();
							}
						});
						break;
				}
				
				builder.show();
			}
		}
	};

    public PolyvPlayerQuestionView(Context context) {
        this(context, null);
    }
    
    public PolyvPlayerQuestionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    public PolyvPlayerQuestionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        initViews();
    }
    
    public void setPolyvVideoView(PolyvVideoView polyvVideoView) {
    	this.polyvVideoView = polyvVideoView;
    }
    
    @SuppressLint("ShowToast")
	private void initViews(){
    	LayoutInflater.from(getContext()).inflate(R.layout.polyv_player_question_view, this);
    	passBtn = (TextView) findViewById(R.id.pass_btn);
    	passBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				polyvVideoView.skipQuestion();
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						hide();
					}
				});
			}
		});

		Button submitBtn = (Button) findViewById(R.id.submit_btn);
    	submitBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (rightAnswerNum == 0) return;
				List<Integer> indexList = new ArrayList<>();
				if (rightAnswerNum > 1) {
					int index = 0;
					for (CheckBox answerCheckBox : answerCheckList) {
						if (answerCheckBox.isChecked()) {
							indexList.add(index);
						}
						
			    		index++;
			    	}
				} else {
					int index = 0;
					for (RadioButton answerRadioBtn : answerRadioList) {
			    		if (answerRadioBtn.isChecked()) {
			    			indexList.add(index);
			    		}
			    		
			    		index++;
			    	}
				}
				
				if (indexList.size() == 0) {
					Message message = new Message();
					message.what = PLEASE_SELECT_MSG;
					
					Bundle bundle = new Bundle();
					bundle.putString("msg", "请选择一个答案");
					message.setData(bundle);
					
					handler.sendMessage(message);
					return;
				}

				polyvVideoView.answerQuestion(indexList);
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						hide();
					}
				});
			}
		});
    	
    	questionLayout = (LinearLayout) findViewById(R.id.question_layout);
    	choicesRadioLayout = (LinearLayout) findViewById(R.id.choices_radio_layout);
    	choicesCheckLayout = (LinearLayout) findViewById(R.id.choices_check_layout);
    	
    	answerRadioLayoutList = new ArrayList<>();
    	LinearLayout answerRadioLayout1 = (LinearLayout) findViewById(R.id.answer_radio_layout_1);
    	answerRadioLayoutList.add(answerRadioLayout1);
    	LinearLayout answerRadioLayout2 = (LinearLayout) findViewById(R.id.answer_radio_layout_2);
    	answerRadioLayoutList.add(answerRadioLayout2);
    	LinearLayout answerRadioLayout3 = (LinearLayout) findViewById(R.id.answer_radio_layout_3);
    	answerRadioLayoutList.add(answerRadioLayout3);
    	LinearLayout answerRadioLayout4 = (LinearLayout) findViewById(R.id.answer_radio_layout_4);
    	answerRadioLayoutList.add(answerRadioLayout4);
    	
    	answerRadioList = new ArrayList<>();
    	RadioButton answerRadio1 = (RadioButton) findViewById(R.id.answer_radio_1);
    	answerRadio1.setOnCheckedChangeListener(this);
    	answerRadioList.add(answerRadio1);
    	RadioButton answerRadio2 = (RadioButton) findViewById(R.id.answer_radio_2);
    	answerRadio2.setOnCheckedChangeListener(this);
    	answerRadioList.add(answerRadio2);
    	RadioButton answerRadio3 = (RadioButton) findViewById(R.id.answer_radio_3);
    	answerRadio3.setOnCheckedChangeListener(this);
    	answerRadioList.add(answerRadio3);
    	RadioButton answerRadio4 = (RadioButton) findViewById(R.id.answer_radio_4);
    	answerRadio4.setOnCheckedChangeListener(this);
    	answerRadioList.add(answerRadio4);
    	
    	answerCheckLayoutList = new ArrayList<>();
    	LinearLayout answerCheckLayout1 = (LinearLayout) findViewById(R.id.answer_check_layout_1);
    	answerCheckLayoutList.add(answerCheckLayout1);
    	LinearLayout answerCheckLayout2 = (LinearLayout) findViewById(R.id.answer_check_layout_2);
    	answerCheckLayoutList.add(answerCheckLayout2);
    	LinearLayout answerCheckLayout3 = (LinearLayout) findViewById(R.id.answer_check_layout_3);
    	answerCheckLayoutList.add(answerCheckLayout3);
    	LinearLayout answerCheckLayout4 = (LinearLayout) findViewById(R.id.answer_check_layout_4);
    	answerCheckLayoutList.add(answerCheckLayout4);
    	
    	answerCheckList = new ArrayList<>();
    	CheckBox answerCheck1 = (CheckBox) findViewById(R.id.answer_check_1);
    	answerCheckList.add(answerCheck1);
    	CheckBox answerCheck2 = (CheckBox) findViewById(R.id.answer_check_2);
    	answerCheckList.add(answerCheck2);
    	CheckBox answerCheck3 = (CheckBox) findViewById(R.id.answer_check_3);
    	answerCheckList.add(answerCheck3);
    	CheckBox answerCheck4 = (CheckBox) findViewById(R.id.answer_check_4);
    	answerCheckList.add(answerCheck4);
    }

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			for (RadioButton radioButton : answerRadioList) {
				if (radioButton.getId() != buttonView.getId()) {
					radioButton.setChecked(false);
				}
			}
		}
	}
	
	/**
	 * 显示
	 * @param questionVO
	 */
	public void show(PolyvQuestionVO questionVO) {
		if (questionVO.isSkip()) {
			passBtn.setVisibility(View.VISIBLE);
		}

		questionLayout.removeAllViews();

		for (RadioButton answerRadioBtn : answerRadioList) {
			answerRadioBtn.setVisibility(View.GONE);
			answerRadioBtn.setChecked(false);
		}

		for (CheckBox answerCheckBox : answerCheckList) {
			answerCheckBox.setVisibility(View.GONE);
			answerCheckBox.setChecked(false);
		}

		for (LinearLayout answerRadioLayout : answerRadioLayoutList) {
			answerRadioLayout.setVisibility(View.GONE);
			for (int i = 1, length = answerRadioLayout.getChildCount() ; i < length ; i++) {
				answerRadioLayout.removeViewAt(1);
			}
		}

		for (LinearLayout answerCheckLayout : answerCheckLayoutList) {
			answerCheckLayout.setVisibility(View.GONE);
			for (int i = 1, length = answerCheckLayout.getChildCount() ; i < length ; i++) {
				answerCheckLayout.removeViewAt(1);
			}
		}

		List<PolyvQAFormatVO> list = PolyvQuestionUtil.parseQA2(questionVO.getQuestion());
		TextView textView;
		ImageView imageView;
		for (PolyvQAFormatVO qaFormatVO : list) {
			switch (qaFormatVO.getStringType()) {
				case STRING:
					textView = new TextView(context);
					textView.setText(qaFormatVO.getStr());
					textView.setTextColor(Color.parseColor("#ffffff"));
					questionLayout.addView(textView);
					break;
				case URL:
					imageView = new ImageView(context);
					PolyvImageLoader.getInstance().loadImageOrigin(context, qaFormatVO.getStr(),
							imageView, R.drawable.polyv_loading);
					questionLayout.addView(imageView);
					break;
			}
		}

		//单个正确答案是单选
		//多个正确答案是多选
		int rightAnswerNum = 0;
		List<PolyvQuestionChoicesVO> choicesList = questionVO.getChoicesList2();
		for (PolyvQuestionChoicesVO choicesVO : choicesList) {
			if (choicesVO.getRightAnswer() == 1) {
				rightAnswerNum++;
			}
		}

		this.rightAnswerNum = rightAnswerNum;
		if (rightAnswerNum > 1) {
			choicesCheckLayout.setVisibility(View.VISIBLE);
			choicesRadioLayout.setVisibility(View.GONE);
		} else {
			choicesCheckLayout.setVisibility(View.GONE);
			choicesRadioLayout.setVisibility(View.VISIBLE);
		}

		LinearLayout answerLayout = null;
		RadioButton answerRadioBtn = null;
		CheckBox answerCheckBox = null;
		int index = 0;
		for (PolyvQuestionChoicesVO choicesVO : choicesList) {
			if (rightAnswerNum > 1) {
				answerLayout = answerCheckLayoutList.get(index);
				answerCheckBox = answerCheckList.get(index);
				answerCheckBox.setVisibility(View.VISIBLE);
			} else {
				answerLayout = answerRadioLayoutList.get(index);
				answerRadioBtn = answerRadioList.get(index);
				answerRadioBtn.setVisibility(View.VISIBLE);
			}

			answerLayout.setVisibility(View.VISIBLE);
			list = PolyvQuestionUtil.parseQA2(choicesVO.getAnswer());

			for (PolyvQAFormatVO qaFormatVO : list) {
				switch (qaFormatVO.getStringType()) {
					case STRING:
						textView = new TextView(context);
						textView.setText(qaFormatVO.getStr());
						textView.setTextColor(Color.parseColor("#ffffff"));
						answerLayout.addView(textView);
						break;
					case URL:
						imageView = new ImageView(context);
						PolyvImageLoader.getInstance().loadImageOrigin(context, qaFormatVO.getStr(),
								imageView, R.drawable.polyv_loading);
						answerLayout.addView(imageView);
						break;
				}
			}

			index++;
		}

		setVisibility(View.VISIBLE);
	}

	/**
	 * 隐藏
	 */
	public void hide() {
		setVisibility(View.GONE);
	}
	
	/**
	 * 显示答案提示
	 * @param msg
	 */
	public void showAnswerTips(String msg) {
		Message message = new Message();
		message.what = ANSWER_TIPS_MSG;
		
		Bundle bundle = new Bundle();
		bundle.putString("msg", msg);
		message.setData(bundle);
		
		handler.sendMessage(message);
	}
}