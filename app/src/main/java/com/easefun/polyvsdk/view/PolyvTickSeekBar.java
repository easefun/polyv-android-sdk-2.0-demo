package com.easefun.polyvsdk.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PolyvTickSeekBar extends AppCompatSeekBar {
    private int height, width;
    //画笔
    private Paint mStockPaint;
    private List<TickData> mTickDataList;
    private boolean isMoved;
    private int moveCount;
    private final float fingerRadius = 45;//触摸点的45px范围的点视为点中打点

    public PolyvTickSeekBar(Context context) {
        this(context, null);
    }

    public PolyvTickSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PolyvTickSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initStrokePaint();
    }

    //初始化画笔
    private void initStrokePaint() {
        mStockPaint = new Paint();
        mStockPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mStockPaint.setAntiAlias(true);
    }

    private void initSeekBarInfo() {
        height = getHeight();
        width = getWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initSeekBarInfo();
        drawTicks(canvas);
    }

    private void drawTicks(Canvas canvas) {
        if (mTickDataList == null || mTickDataList.size() == 0) {
            return;
        }
        for (int i = 0; i < mTickDataList.size(); i++) {
            TickData tickData = mTickDataList.get(i);
            if (tickData.progress > getMax() || tickData.progress < 0)
                continue;
            mStockPaint.setColor(tickData.color);
            int thumbHeight = height - getPaddingTop() - getPaddingBottom();//thumb的高度
            float cy = thumbHeight / 2.0f + getPaddingTop() - getPaddingBottom();//打点的圆心y轴位置
            float percent = tickData.progress / (getMax() * 1.0f);
            float cx = (width - getPaddingLeft() - getPaddingRight()) * percent;//打点的圆心x轴位置
            cx += getPaddingLeft();
            float radius = thumbHeight / 3.6f;//打点的半径
            //圆心在端点上
//            if (cx < getPaddingLeft())
//                cx = getPaddingLeft();
//            else if (cx > width - getPaddingRight())
//                cx = width - getPaddingRight();
            //圆心在端点内
            if (cx < getPaddingLeft() + radius)
                cx = getPaddingLeft() + radius;
            else if (cx > width - getPaddingRight() - radius)
                cx = width - getPaddingRight() - radius;
            //调整两端的位置
            if (cx + getPaddingLeft() < radius)
                cx = radius - getPaddingLeft();
            else if (width - cx + getPaddingRight() < radius)
                cx = cx - (radius - (width - cx) - getPaddingRight());
            tickData.cx = cx;//设置打点的圆心位置
            canvas.drawCircle(cx, cy, radius, mStockPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean hasTick = mTickDataList != null && mTickDataList.size() > 0;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                isMoved = false;
                moveCount = 0;
                return hasTick || super.onTouchEvent(event);
            case MotionEvent.ACTION_MOVE:
                if (++moveCount >= 4) {
                    isMoved = true;
                } else {
                    return hasTick || super.onTouchEvent(event);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (!isMoved) {
                    float ex = event.getX();
                    //查找点击的附近是否有打点
                    int fitPosition;
                    if (hasTick && (fitPosition = halfSearch(mTickDataList, 0, mTickDataList.size() - 1, ex, fingerRadius)) != -1) {
                        if (onTickClickListener != null) {
                            onTickClickListener.onTickClick(mTickDataList.get(fitPosition));
                        }
                    } else {
                        if (onTickClickListener != null) {
                            boolean result = onTickClickListener.onSeekBarClick();
                            return hasTick ? (result ? super.onTouchEvent(event) : !result) : super.onTouchEvent(event);
                        }
                    }
                    return hasTick || super.onTouchEvent(event);
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private int findCloserPoint(List<TickData> tickDataList, int position, float ex, boolean isFirst, float fingerRadius) {
        if (position < 0 || position > tickDataList.size() - 1) {
            return -1;
        }
        float cxExDistance = Math.abs(tickDataList.get(position).cx - ex);
        if (cxExDistance <= fingerRadius) {//满足条件，继续查找是否有离触摸点更近的打点
            int preMiddle = position - 1;
            int sufMiddle = position + 1;
            int fitPosition = position;
            if (preMiddle >= 0) {
                float preCxExDistance = Math.abs(tickDataList.get(preMiddle).cx - ex);
                if (preCxExDistance < cxExDistance) {
                    cxExDistance = preCxExDistance;
                    fitPosition = preMiddle;
                }
            }
            if (sufMiddle <= tickDataList.size() - 1) {
                float sufCxExDistance = Math.abs(tickDataList.get(sufMiddle).cx - ex);
                if (sufCxExDistance <= cxExDistance) {//相同位置的两点，取后续的
                    fitPosition = sufMiddle;
                }
            }
            if (fitPosition != position) {
                return findCloserPoint(tickDataList, fitPosition, ex, false, fingerRadius);
            }
        } else if (isFirst) {//第一次查找不满足条件时返回-1
            return -1;
        }
        return position;
    }

    private int halfSearch(List<TickData> tickDataList, int startPosition, int endPosition, float ex, float fingerRadius) {
        if (tickDataList == null || tickDataList.size() == 0) {
            return -1;
        }
        int length = tickDataList.size();
        if (length < startPosition || length < endPosition || startPosition > endPosition)
            return -1;
        while (startPosition <= endPosition) {
            int middle = startPosition + ((endPosition - startPosition) >> 1);//偶数middle取前面的
            float cx = tickDataList.get(middle).cx;
            int fitPosition;
            if ((fitPosition = findCloserPoint(tickDataList, middle, ex, true, fingerRadius)) != -1) {
                return fitPosition;
            } else if (ex - cx > 0) {//中间点在触摸点的前面
                startPosition = middle + 1;
            } else {//中间点在触摸点的后面
                endPosition = middle - 1;
            }
        }
        return -1;
    }

    //设置时间点
    public void setTicks(List<TickData> tickDataList) {
        this.mTickDataList = tickDataList;
        Collections.sort(this.mTickDataList, new Comparator<TickData>() {
            @Override
            public int compare(TickData lhs, TickData rhs) {
                return (int) (lhs.progress - rhs.progress);//自然排序
            }
        });
        invalidate();
    }

    public interface OnTickClickListener {
        void onTickClick(TickData tickData);

        /**
         * @return 有打点数据时，点击非打点处抬起(中途没有移动)，是否会触发onProgressChanged
         */
        boolean onSeekBarClick();
    }

    private OnTickClickListener onTickClickListener;

    public void setOnTickClickListener(OnTickClickListener l) {
        this.onTickClickListener = l;
    }

    public static class TickData {
        private float progress;
        private int color;
        private Object tag;
        //位置
        private float cx;

        public TickData(float progress, int color, Object tag) {
            this.progress = progress;
            this.color = color;
            this.tag = tag;
        }

        public float getProgress() {
            return progress;
        }

        public void setProgress(float progress) {
            this.progress = progress;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public Object getTag() {
            return tag;
        }

        public void setTag(Object tag) {
            this.tag = tag;
        }

        public float getCx() {
            return cx;
        }

        public void setCx(float cx) {
            this.cx = cx;
        }

        @Override
        public String toString() {
            return "TickData{" +
                    "progress=" + progress +
                    ", color=" + color +
                    ", tag=" + tag +
                    ", cx=" + cx +
                    '}';
        }
    }
}
