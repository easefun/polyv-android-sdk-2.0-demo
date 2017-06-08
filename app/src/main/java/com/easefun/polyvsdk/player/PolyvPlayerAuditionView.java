package com.easefun.polyvsdk.player;

import java.io.IOException;
import java.util.ArrayList;

import com.easefun.polyvsdk.PolyvSDKUtil;
import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.SDKUtil;
import com.easefun.polyvsdk.ijk.IjkVideoView;
import com.easefun.polyvsdk.video.PolyvVideoView;
import com.easefun.polyvsdk.vo.PolyvQuestionVO;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 听力问答视图
 * @author TanQu 2016-1-25
 */
public class PolyvPlayerAuditionView extends RelativeLayout {
	private Context mContext = null;
	private PolyvVideoView polyvVideoView = null;
	private TextView passBtn = null;
	private TextView title = null;
	private ImageButton playPauseBtn = null;
	private ProgressBar progressBar = null;
	private TextView progressTotalText = null;
	private MediaPlayer mediaPlayer = null;
	private PolyvQuestionVO questionVO = null;

	private static final int UPDATE_PROGRESS = 1;
	private static final int CLOSE_QUESTION = 2;
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case UPDATE_PROGRESS:
					setProgress();
					handler.removeMessages(UPDATE_PROGRESS);
					handler.sendEmptyMessageDelayed(UPDATE_PROGRESS, 1000);
					break;
                case CLOSE_QUESTION:
                    polyvVideoView.answerQuestion(new ArrayList<Integer>(0));
                    hide();
                    break;
			}
		}
	};
	
	public PolyvPlayerAuditionView(Context context) {
		this(context, null);
	}

	public PolyvPlayerAuditionView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PolyvPlayerAuditionView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		initViews();
	}

	public void setPolyvVideoView(PolyvVideoView polyvVideoView) {
    	this.polyvVideoView = polyvVideoView;
    }

	private void initViews() {
		LayoutInflater.from(getContext()).inflate(R.layout.polyv_player_audition_view, this);
		passBtn = (TextView) findViewById(R.id.audition_pass_btn);
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
		title = (TextView) findViewById(R.id.title);
		playPauseBtn = (ImageButton) findViewById(R.id.audition_play_pause);
		playPauseBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mediaPlayer == null) return;
				if (mediaPlayer.isPlaying()) {
					playPauseBtn.setImageResource(R.drawable.polyv_btn_play_s);
					mediaPlayer.pause();
				} else {
					playPauseBtn.setImageResource(R.drawable.polyv_btn_pause);
					mediaPlayer.start();
				}
			}
		});
		progressBar = (ProgressBar) findViewById(R.id.audition_progress);
		progressBar.setMax(1000);
		
		progressTotalText = (TextView) findViewById(R.id.audition_progress_total_text);
		progressTotalText.setText(String.format("%s/%s", PolyvSDKUtil.getVideoDisplayTime(0), PolyvSDKUtil.getVideoDisplayTime(0)));
	}
	
	private void setProgress() {
		long currentTime = mediaPlayer.getCurrentPosition();
		long durationTime = mediaPlayer.getDuration();
		float percentage = ((float) currentTime) / durationTime;
		int progress = (int) (percentage * progressBar.getMax());
		progressBar.setProgress(progress);
		progressTotalText.setText(String.format("%s/%s", PolyvSDKUtil.getVideoDisplayTime(currentTime), PolyvSDKUtil.getVideoDisplayTime(durationTime)));
	}
	
	/**
	 * 显示
	 * @param questionVO
	 */
	public void show(PolyvQuestionVO questionVO) {
		this.questionVO = questionVO;
		if (questionVO.isSkip()) {
			passBtn.setVisibility(View.VISIBLE);
		}

		title.setText(questionVO.getQuestion());
		if (mediaPlayer != null) {
			mediaPlayer.release();
		}

		mediaPlayer = new MediaPlayer();
		try {
			mediaPlayer.setDataSource(mContext, Uri.parse(questionVO.getMp3url()));
			mediaPlayer.prepare();
		} catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
			e.printStackTrace();
		}

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                setProgress();
                if (PolyvPlayerAuditionView.this.questionVO.getWrongTime() <= 0) {
                    handler.sendEmptyMessage(CLOSE_QUESTION);
                } else {
                    handler.sendEmptyMessageDelayed(CLOSE_QUESTION, PolyvPlayerAuditionView.this.questionVO.getWrongTime() * 1000);
                }
            }
        });
		mediaPlayer.start();
		handler.removeMessages(UPDATE_PROGRESS);
		handler.sendEmptyMessageDelayed(UPDATE_PROGRESS, 1000);
		setVisibility(View.VISIBLE);
	}

	/**
	 * 隐藏
	 */
	public void hide() {
		if (mediaPlayer != null) {
			mediaPlayer.release();
		}

		if (handler != null) {
			handler.removeMessages(UPDATE_PROGRESS);
		}

		setVisibility(View.GONE);
	}
}
