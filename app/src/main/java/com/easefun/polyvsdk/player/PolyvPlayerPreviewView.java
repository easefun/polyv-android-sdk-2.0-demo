package com.easefun.polyvsdk.player;

import java.io.File;

import com.easefun.polyvsdk.PolyvSDKClient;
import com.easefun.polyvsdk.PolyvSDKUtil;
import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.vo.PolyvVideoVO;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * 预览图视图
 * @author TanQu 2016-3-3
 */
public class PolyvPlayerPreviewView extends RelativeLayout {
	private static final String TAG = PolyvPlayerPreviewView.class.getSimpleName();
	private Context mContext = null;
	private ImageView mPreviewImage = null;
	private ImageButton mStartBtn = null;
	private Callback mCallback = null;
	private DisplayImageOptions mOptions = null;

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
    	
    	if (mOptions == null) {
    		mOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.polyv_avatar_def) // 设置图片在下载期间显示的图片
    				.showImageForEmptyUri(R.drawable.polyv_avatar_def)// 设置图片Uri为空或是错误的时候显示的图片
    				.showImageOnFail(R.drawable.polyv_avatar_def) // 设置图片加载/解码过程中错误时候显示的图片
    				.bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型
    				.cacheInMemory(true)// 设置下载的图片是否缓存在内存中
    				.cacheOnDisk(true)// 设置下载的图片是否缓存在SD卡中
    				.displayer(new FadeInBitmapDisplayer(100))// 是否图片加载好后渐入的动画时间
    				.imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();// 构建完成
		}
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
			if (v == null) return;
			File dir = PolyvSDKClient.getInstance().getVideoDownloadExtraResourceDir(mVid);
			String fileName = v.getFirstImage().substring(v.getFirstImage().lastIndexOf("/"));
			File file = new File(dir, fileName);
			if (file.exists()) {
				mPreviewImage.setImageURI(Uri.parse(file.getAbsolutePath()));
			} else {
				ImageLoader.getInstance().displayImage(v.getFirstImage(), mPreviewImage, mOptions, new PolyvAnimateFirstDisplayListener());
			}
		}
	}

    /**
     * 隐藏
     */
    public void hide() {
    	Drawable drawable = mPreviewImage.getDrawable();
    	if (drawable != null && drawable instanceof BitmapDrawable) {
    		Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
    		if (bitmap != null && !bitmap.isRecycled()) {
    			bitmap.recycle();
    			bitmap = null;
    		}
    	}

		mPreviewImage.setImageBitmap(null);
    	System.gc();
        setVisibility(View.GONE);
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
