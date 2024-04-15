package com.easefun.polyvsdk.player;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import androidx.annotation.IntRange;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.fragment.PolyvPlayerDanmuFragment;
import com.easefun.polyvsdk.util.PolyvImageLoader;
import com.easefun.polyvsdk.util.PolyvScreenUtils;
import com.easefun.polyvsdk.video.PolyvVideoView;
import com.easefun.polyvsdk.vo.PolyvADMatterVO;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * 图片广告和图片片头视图，视频广告和视频片头在{@link com.easefun.polyvsdk.video.PolyvVideoView}中已经处理
 * @author TanQu 2016-11-15
 */
public class PolyvPlayerAuxiliaryView extends RelativeLayout {
	private Context mContext = null;
	private PolyvVideoView mVideoView = null;
	private ImageView mAdvertisementImage = null;
	private ImageButton mStartBtn = null;
	private PolyvADMatterVO mADMatter = null;
	private PolyvPlayerDanmuFragment danmuFragment = null;

	private int preferImageWidthPercent = 0;
	private int preferImageHeightPercent = 0;
	private int preferImageCenterToLeftPercent = 0;
	private int preferImageCenterToTopPercent = 0;

	public PolyvPlayerAuxiliaryView(Context context) {
		this(context, null);
	}

	public PolyvPlayerAuxiliaryView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PolyvPlayerAuxiliaryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initViews();
    }
    
    public void setPolyvVideoView(PolyvVideoView mVideoView) {
    	this.mVideoView = mVideoView;
    }

	public void setDanmakuFragment(PolyvPlayerDanmuFragment danmakuFragment){
		this.danmuFragment=danmakuFragment;
	}
    
    private void initViews() {
    	LayoutInflater.from(getContext()).inflate(R.layout.polyv_player_auxiliary_view, this);
    	mAdvertisementImage = (ImageView) findViewById(R.id.advertisement_image);
    	mAdvertisementImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mADMatter == null) return;
				String path = mADMatter.getAddrUrl();
				if (TextUtils.isEmpty(path) == false) {
					try {
						new URL(path);
					} catch (MalformedURLException e) {
						return;
					}
					
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(path));
					mContext.startActivity(intent);
				}
			}
		});

    	mStartBtn = (ImageButton) findViewById(R.id.advertisement_start_btn);
    	mStartBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mVideoView.start();
				danmuFragment.resume();
				hide();
			}
		});

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		return true;
	}

	@Override
	protected void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		updateAuxiliaryImageSizePosition();
	}

	/**
	 * 设置图片并显示
	 *
	 * @param adMatter
	 */
	public void show(PolyvADMatterVO adMatter) {
		mADMatter = adMatter;
		PolyvImageLoader.getInstance().loadImageOrigin(mContext, mADMatter.getMatterUrl(), mAdvertisementImage, R.drawable.polyv_loading);

		//暂停图片广告不需要倒计时，是点击开始按钮继续
		if (PolyvADMatterVO.LOCATION_PAUSE.equals(adMatter.getLocation())) {
			mStartBtn.setVisibility(View.VISIBLE);
    	} else {
    		mStartBtn.setVisibility(View.GONE);
    	}

		setVisibility(View.VISIBLE);
    }

	/**
	 * 设置图片并显示
	 * @param url
	 */
	public void show(String url) {
		mADMatter = null;
		PolyvImageLoader.getInstance().loadImageOrigin(mContext, url, mAdvertisementImage, R.drawable.polyv_loading);

		mStartBtn.setVisibility(View.GONE);
		setVisibility(View.VISIBLE);
	}

	public boolean isPauseAdvert() {
		return mADMatter != null && PolyvADMatterVO.LOCATION_PAUSE.equals(mADMatter.getLocation());
	}

	/**
	 * 隐藏
	 */
	public void hide() {
		setVisibility(View.GONE);
	}

	/**
	 * 设置片头图片和暂停图片的显示大小
	 * 取值单位为相对于播放器大小的百分比，范围为[0, 100]
	 * 当widthPercent取值为0，heightPercent为1-100时，宽度随高度自适应
	 * 当heightPercent取值为0，widthPercent为1-100时，高度随宽度自适应
	 * 当两个取值均为0时，大小为默认的铺满播放器
	 *
	 * @param widthPercent  宽度比例 范围0-100
	 * @param heightPercent 高度比例 范围0-100
	 */
	public void setAuxiliaryImageSize(@IntRange(from = 0, to = 100) int widthPercent, @IntRange(from = 0, to = 100) int heightPercent) {
		preferImageWidthPercent = clampInt(widthPercent, 0, 100);
		preferImageHeightPercent = clampInt(heightPercent, 0, 100);
		post(new Runnable() {
			@Override
			public void run() {
				updateAuxiliaryImageSizePosition();
			}
		});
	}

	/**
	 * 设置片头图片和暂停图片的中心点位置
	 * 取值单位为相对于播放器大小的百分比，范围为[0, 100]
	 * 当图片任一侧边超出播放器范围，或范围设置不正确时，图片居中显示
	 *
	 * @param centerToLeftPercent 中心点距离左侧边百分比
	 * @param centerToTopPercent  中心点距离顶边百分比
	 */
	public void setAuxiliaryImagePosition(@IntRange(from = 0, to = 100) int centerToLeftPercent, @IntRange(from = 0, to = 100) int centerToTopPercent) {
		preferImageCenterToLeftPercent = clampInt(centerToLeftPercent, 0, 100);
		preferImageCenterToTopPercent = clampInt(centerToTopPercent, 0, 100);
		post(new Runnable() {
			@Override
			public void run() {
				updateAuxiliaryImageSizePosition();
			}
		});
	}

	private void updateAuxiliaryImageSizePosition() {
		if (!(getContext() instanceof Activity)) {
			return;
		}
		final int[] screenWH = PolyvScreenUtils.getNormalWH((Activity) getContext());
		final int playerWidth = screenWH[0];
		final int playerHeight = PolyvScreenUtils.isLandscape(getContext()) ? screenWH[1] : PolyvScreenUtils.getHeight16_9();

		int imageWidth = (int) (playerWidth * (preferImageWidthPercent / 100F));
		int imageHeight = (int) (playerHeight * (preferImageHeightPercent / 100F));
		if (imageWidth == 0 && imageHeight == 0) {
			// 宽高均为0，充满播放器
			imageWidth = playerWidth;
			imageHeight = playerHeight;
		} else if (imageWidth == 0) {
			// 宽度根据高度的比例自适应
			imageWidth = (int) (playerWidth * (preferImageHeightPercent / 100F));
		} else if (imageHeight == 0) {
			// 高度根据宽度的比例自适应
			imageHeight = (int) (playerHeight * (preferImageWidthPercent / 100F));
		}

		final int imageLeft = (int) (playerWidth * (preferImageCenterToLeftPercent / 100F) - imageWidth / 2);
		final int imageTop = (int) (playerHeight * (preferImageCenterToTopPercent / 100F) - imageHeight / 2);

		final boolean fillPlayer = imageWidth == playerWidth && imageHeight == playerHeight;

		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mAdvertisementImage.getLayoutParams();
		if (fillPlayer) {
			lp.width = LayoutParams.MATCH_PARENT;
			lp.height = LayoutParams.MATCH_PARENT;
		} else {
			lp.width = imageWidth;
			lp.height = imageHeight;
		}

		if (imageLeft < 0
				|| imageTop < 0
				|| imageLeft > playerWidth - imageWidth
				|| imageTop > playerHeight - imageHeight) {
			// 边距设置不正确时 居中显示
			lp.addRule(RelativeLayout.CENTER_IN_PARENT);
			lp.leftMargin = 0;
			lp.topMargin = 0;
		} else {
			// subject == 0, remove rule
			lp.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
			lp.leftMargin = imageLeft;
			lp.topMargin = imageTop;
		}

		mAdvertisementImage.setLayoutParams(lp);
	}

	private static int clampInt(int value, int min, int max) {
		if (value > max) {
			return max;
		}
		if (value < min) {
			return min;
		}
		return value;
	}

}
