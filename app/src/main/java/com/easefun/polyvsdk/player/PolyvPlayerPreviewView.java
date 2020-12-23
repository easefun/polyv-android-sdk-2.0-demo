package com.easefun.polyvsdk.player;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.easefun.polyvsdk.PolyvSDKUtil;
import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.util.PolyvDownloadDirUtil;
import com.easefun.polyvsdk.util.PolyvImageLoader;
import com.easefun.polyvsdk.vo.PolyvVideoVO;

import java.io.File;

/**
 * 预览图视图
 * @author Lion 2016-3-3
 */
public class PolyvPlayerPreviewView extends RelativeLayout {
	private static final String TAG = PolyvPlayerPreviewView.class.getSimpleName();
	private Context mContext = null;
	private ImageView mPreviewImage = null;
	private ImageButton mStartBtn = null;
	private Callback mCallback = null;

    public PolyvPlayerPreviewView(Context context) {
        this(context, null);
    }
    
    public PolyvPlayerPreviewView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    public PolyvPlayerPreviewView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initViews();
    }
    
    private void initViews() {
    	LayoutInflater.from(getContext()).inflate(R.layout.polyv_player_preview_view, this);
		mPreviewImage = (ImageView) findViewById(R.id.preview_image);
		mStartBtn = (ImageButton) findViewById(R.id.start_btn);
		mStartBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mCallback != null) {
					mCallback.onClickStart();
				}

                hide();
			}
		});
    }

    /**
     * 设置图片并显示
     * @param vid
     */
    public void show(String vid) {
		new LoadVideoJson(vid).execute();
        setVisibility(View.VISIBLE);
    }

	private class LoadVideoJson extends AsyncTask<String, Void, PolyvVideoVO> {

		private final String mVid;

		LoadVideoJson(String vid) {
			mVid = vid;
		}

		@Override
		protected PolyvVideoVO doInBackground(String... params) {
			PolyvVideoVO video = null;
			try {
				video = PolyvSDKUtil.loadVideoJSON2Video(mVid);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return video;
		}

		@Override
		protected void onPostExecute(PolyvVideoVO v) {
			super.onPostExecute(v);
			if (v == null) {
				return;
			}

			if (TextUtils.isEmpty(v.getFirstImage())) {
				return;
			}

			if(isDestroy((Activity) getContext())){
				return;
			}

			int index = 0;
			if (v.getFirstImage().contains("/")) {
				index = v.getFirstImage().lastIndexOf("/");
			}

			String fileName = v.getFirstImage().substring(index);
			File file = PolyvDownloadDirUtil.getFileFromExtraResourceDir(mVid, fileName);
			if (file != null) {
				mPreviewImage.setImageURI(Uri.parse(file.getAbsolutePath()));
			} else {
				PolyvImageLoader.getInstance().loadImageOrigin(getContext(), v.getFirstImage(), mPreviewImage, R.drawable.polyv_loading);
			}
		}
	}

    /**
     * 隐藏
     */
    public void hide() {
        setVisibility(View.GONE);
    }

	private boolean isDestroy(Activity activity){
    	if(activity == null || activity.isFinishing() ||
				(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed())){
    		return true;
		} else {
    		return false;
		}
	}
    
    @Override
	public boolean onTouchEvent(MotionEvent event) {
    	super.onTouchEvent(event);
		return true;
	}

    public void setCallback(Callback callback) {
    	mCallback = callback;
    }

    public interface Callback {
    	public void onClickStart();
    }
}
