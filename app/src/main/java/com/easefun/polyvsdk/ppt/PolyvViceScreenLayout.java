package com.easefun.polyvsdk.ppt;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import com.easefun.polyvsdk.player.PolyvPlayerAudioCoverView;
import com.easefun.polyvsdk.po.ppt.PolyvPptInfo;
import com.easefun.polyvsdk.util.PolyvScreenUtils;
import com.easefun.polyvsdk.video.PolyvVideoView;
import com.easefun.polyvsdk.view.PolyvLoadingLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 三分屏的副屏布局
 */
public class PolyvViceScreenLayout extends FrameLayout {
    // 点击的位置
    private float lastX, lastY;
    private static int width = 125, height = 70;
    private boolean isFromUserHide;
    private boolean isPPTInMinScreen;

    private Runnable configChangedTask;

    private GestureDetector gestureDetector;


    private PolyvVideoView videoView;
    private PolyvPPTView pptView;

    //横/竖屏位置
    private static int portraitTopMargin = -1, portraitLeftMargin = -1, landscapeTopMargin = -1, landscapeLeftMargin = -1;

    public PolyvViceScreenLayout(@NonNull Context context) {
        this(context, null);
    }

    public PolyvViceScreenLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PolyvViceScreenLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                switchLocation();
                return super.onSingleTapUp(e);
            }
        });
    }

    public static PolyvViceScreenLayout addViceLayoutInWindow(Activity activity, int marginTop) {
        //add ViceScreenLayout
        ViewGroup contentView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
        PolyvViceScreenLayout viceScreenLayout = new PolyvViceScreenLayout(activity);
        viceScreenLayout.setBackgroundColor(Color.BLACK);
        LayoutParams mlp = new LayoutParams(PolyvScreenUtils.dip2px(activity, width), PolyvScreenUtils.dip2px(activity, height));
        if (PolyvScreenUtils.isPortrait(activity)) {
            mlp.topMargin = portraitTopMargin = marginTop;
            mlp.leftMargin = portraitLeftMargin = contentView.getWidth() - PolyvScreenUtils.dip2px(activity, width);
        } else {
            mlp.topMargin = landscapeTopMargin = 0;
            mlp.leftMargin = landscapeLeftMargin = 0;//contentView.getWidth() - PolyvScreenUtils.dip2px(activity, width);
        }
        viceScreenLayout.setLayoutParams(mlp);
        contentView.addView(viceScreenLayout);
        //hide
        viceScreenLayout.hide();
        return viceScreenLayout;
    }

    public void acceptPPTCallback(String vid, boolean hasPPT, PolyvPptInfo pptvo) {
        if (hasPPT) {
            if (!isFromUserHide) {
                show();
            }
            if (pptView != null) {
                pptView.acceptPPTCallback(videoView, vid, hasPPT, pptvo);
            }
        } else {
            hide();
            if (isPPTInMinScreen) {
                switchLocation(false);
            }
        }
    }

    public void bindView(PolyvVideoView videoView, PolyvPPTView pptView) {
        this.videoView = videoView;
        this.pptView = pptView;
    }

    public PolyvPPTView getPPTView() {
        return pptView;
    }

    public boolean isPPTInMinScreen() {
        return isPPTInMinScreen;
    }

    /**
     * 切换主副屏的位置
     */
    public void switchLocation() {
        switchLocation(!isPPTInMinScreen);
    }

    /**
     * 切换主副屏的位置
     *
     * @param isPPTInMinScreen 是否把ppt切到主屏
     */
    public void switchLocation(boolean isPPTInMinScreen) {
        if (this.isPPTInMinScreen == isPPTInMinScreen || pptView == null || videoView == null)
            return;
        this.isPPTInMinScreen = isPPTInMinScreen;

        List<View> pptChilds = new ArrayList<>();
        for (int i = 0; i < pptView.getChildCount(); i++) {
            pptChilds.add(pptView.getChildAt(i));
        }
        pptView.removeAllViews();
        List<View> videoChilds = new ArrayList<>();
        for (int i = 0; i < videoView.getChildCount(); i++) {
            videoChilds.add(videoView.getChildAt(i));
        }
        videoView.removeAllViews();

        for (int i = 0; i < pptChilds.size(); i++) {
            videoView.addView(pptChilds.get(i));
        }
        for (int i = 0; i < videoChilds.size(); i++) {
            pptView.addView(videoChilds.get(i));
        }

        //适配主副屏里的控件大小
        pptChilds.addAll(videoChilds);
        for (View view : pptChilds) {
            if (view instanceof PolyvPlayerAudioCoverView) {
                ((PolyvPlayerAudioCoverView)view).fitLocationChange(!isPPTInMinScreen);
            } else if (view instanceof PolyvLoadingLayout) {
                ((PolyvLoadingLayout) view).fitLocationChange(!isPPTInMinScreen);
            }
        }
    }

    /**
     * 隐藏副屏
     */
    public void hide() {
        setVisibility(View.GONE);
    }

    /**
     * 通过用户点击按钮的方式隐藏副屏
     */
    public void fromUserHide() {
        isFromUserHide = true;
        hide();
    }

    /**
     * 通过用户点击按钮的方式显示副屏
     */
    public void fromUserShow() {
        isFromUserHide = false;
        show();
    }

    /**
     * 显示副屏
     */
    public void show() {
        setVisibility(View.VISIBLE);
    }

    /**
     * 是否显示
     */
    public boolean isVisibible() {
        return getVisibility() == View.VISIBLE;
    }

    /**
     * 销毁
     */
    public void destroy() {
        if (pptView != null) {
            pptView.destroy();
        }
    }

    @Override
    protected void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getHandler() != null) {
            getHandler().removeCallbacks(configChangedTask);
        }
        post(configChangedTask = new Runnable() {
            @Override
            public void run() {
                if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    if (landscapeLeftMargin != -1 && landscapeTopMargin != -1) {
                        setLocation(landscapeTopMargin, landscapeLeftMargin);
                    } else {
                        landscapeTopMargin = 0;
                        landscapeLeftMargin = 0;//((Activity) getContext()).findViewById(Window.ID_ANDROID_CONTENT).getWidth() - PolyvScreenUtils.dip2px(getContext(), width);
                        setLocation(landscapeTopMargin, landscapeLeftMargin);
                    }
                } else {
                    if (portraitLeftMargin != -1 && portraitTopMargin != -1) {
                        setLocation(portraitTopMargin, portraitLeftMargin);
                    } else {
                        if (videoView != null) {
                            portraitTopMargin = videoView.getBottom();
                            portraitLeftMargin = ((Activity) getContext()).findViewById(Window.ID_ANDROID_CONTENT).getWidth() - PolyvScreenUtils.dip2px(getContext(), width);
                            setLocation(portraitTopMargin, portraitLeftMargin);
                        }
                    }
                }
            }
        });
    }

    private void setLocation(int marginTop, int marginLeft) {
        LayoutParams mlp = (LayoutParams) getLayoutParams();
        mlp.leftMargin = marginLeft;
        mlp.topMargin = marginTop;
        setLayoutParams(mlp);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //子view为invisible时(即非visible)不要拦截点击事件
        boolean firstChildIsVisible = getChildAt(0) == null || (getChildAt(0).getVisibility() == View.VISIBLE);
        if (/*getVisibility() != View.VISIBLE || */!firstChildIsVisible)
            return super.onTouchEvent(event);

        gestureDetector.onTouchEvent(event);

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            lastX = event.getX();
            lastY = event.getY();
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            // 计算移动的距离
            float x = event.getX();
            float y = event.getY();
            // 偏移量
            int offX = (int) (x - lastX);
            int offY = (int) (y - lastY);
            View view = this;
            int left = view.getLeft() + offX;
            int top = view.getTop() + offY;
            int parentWidth = ((View) view.getParent()).getMeasuredWidth();
            int parentHeight = ((View) view.getParent()).getMeasuredHeight();
            if (offX < 0 && left < 0)
                left = 0;
            if (offY < 0 && top < 0)
                top = 0;
            if (offX > 0 && view.getRight() + offX > parentWidth)
                left = view.getLeft() + (parentWidth - view.getRight());
            if (offY > 0 && view.getBottom() + offY > parentHeight)
                top = view.getTop() + (parentHeight - view.getBottom());

            ViewGroup.MarginLayoutParams rlp = (MarginLayoutParams) view.getLayoutParams();
            rlp.leftMargin = left;
            rlp.topMargin = top;
            view.setLayoutParams(rlp);
        }
        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            lastX = 0;
            lastY = 0;
        }
        return true;
    }
}
