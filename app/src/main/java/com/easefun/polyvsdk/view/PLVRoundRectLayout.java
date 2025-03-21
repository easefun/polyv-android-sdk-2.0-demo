package com.easefun.polyvsdk.view;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.util.PolyvScreenUtils;


//need default bg
public class PLVRoundRectLayout extends RelativeLayout {

    private Path mPath;
    private int mRadius;

    private int mWidth;
    private int mHeight;
    private int mLastRadius;

    public static final int MODE_LEFT_TOP = 1;
    public static final int MODE_LEFT_BOTTOM = 2;
    public static final int MODE_RIGHT_TOP = 4;
    public static final int MODE_RIGHT_BOTTOM = 8;

    public static final int MODE_NONE = 0;
    public static final int MODE_ALL = MODE_LEFT_TOP | MODE_LEFT_BOTTOM | MODE_RIGHT_TOP | MODE_RIGHT_BOTTOM;
    public static final int MODE_LEFT = MODE_LEFT_TOP | MODE_LEFT_BOTTOM;
    public static final int MODE_TOP = MODE_LEFT_TOP | MODE_RIGHT_TOP;
    public static final int MODE_RIGHT = MODE_RIGHT_TOP | MODE_RIGHT_BOTTOM;
    public static final int MODE_BOTTOM = MODE_LEFT_BOTTOM | MODE_RIGHT_BOTTOM;

    private int mRoundMode = MODE_ALL;

    private OnOrientationChangedListener onOrientationChangedListener;

    public PLVRoundRectLayout(Context context) {
        this(context, null);
    }

    public PLVRoundRectLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PLVRoundRectLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PLVRoundRectLayout, defStyleAttr, 0);
        int radius = a.getDimensionPixelSize(R.styleable.PLVRoundRectLayout_radius, 10);
        int mode = a.getInt(R.styleable.PLVRoundRectLayout_mode, MODE_ALL);
        a.recycle();

        mRoundMode = mode;
        setCornerRadius(radius);
        init();
    }

    private void init() {
        /**
         * ///暂时保留该代码
         * setBackgroundDrawable(new ColorDrawable(0x33ff0000));
         * setCornerRadius(10);
         */
        mPath = new Path();
        mPath.setFillType(Path.FillType.EVEN_ODD);

    }

    /**
     * 设置是否圆角裁边
     *
     * @param roundMode
     */
    public void setRoundMode(int roundMode) {
        mRoundMode = roundMode;
    }

    /**
     * 设置圆角半径
     *
     * @param radius
     */
    public void setCornerRadius(int radius) {
        mRadius = radius;
    }

    /**
     * 设置屏幕旋转变化监听
     * @param li 监听器
     */
    public void setOnOrientationChangedListener(OnOrientationChangedListener li) {
        this.onOrientationChangedListener = li;
        li.onChanged(PolyvScreenUtils.isPortrait(getContext()));
    }

    private void checkPathChanged() {

        if (getWidth() == mWidth && getHeight() == mHeight && mLastRadius == mRadius) {
            return;
        }

        mWidth = getWidth();
        mHeight = getHeight();
        mLastRadius = mRadius;

        mPath.reset();

        final float radiusLeftTop = (mRoundMode & MODE_LEFT_TOP) != 0 ? mRadius : 0;
        final float radiusLeftBottom = (mRoundMode & MODE_LEFT_BOTTOM) != 0 ? mRadius : 0;
        final float radiusRightTop = (mRoundMode & MODE_RIGHT_TOP) != 0 ? mRadius : 0;
        final float radiusRightBottom = (mRoundMode & MODE_RIGHT_BOTTOM) != 0 ? mRadius : 0;

        mPath.addRoundRect(
                new RectF(0, 0, mWidth, mHeight),
                new float[]{radiusLeftTop, radiusLeftTop, radiusRightTop, radiusRightTop, radiusRightBottom, radiusRightBottom, radiusLeftBottom, radiusLeftBottom},
                Path.Direction.CCW
        );

    }

    @Override
    public void draw(Canvas canvas) {
        if (mRoundMode != MODE_NONE) {
            int saveCount = canvas.save();

            checkPathChanged();

            canvas.clipPath(mPath);
            super.draw(canvas);

            canvas.restoreToCount(saveCount);
        } else {
            super.draw(canvas);
        }
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (onOrientationChangedListener != null) {
            onOrientationChangedListener.onChanged(newConfig.orientation != Configuration.ORIENTATION_LANDSCAPE);
        }
    }

    public interface OnOrientationChangedListener {
        void onChanged(boolean isPortrait);
    }
}