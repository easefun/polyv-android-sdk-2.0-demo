package com.easefun.polyvsdk.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easefun.polyvsdk.PolyvBitRate;
import com.easefun.polyvsdk.R;

import java.util.LinkedList;

/**
 * 弱网缓冲时，切换清晰度提示条
 *
 * @author suhongtao
 */
public class PolyvNetworkPoorIndicateLayout extends FrameLayout {

    // <editor-fold defaultstate="collapsed" desc="常量">

    // 计时：5秒卡顿进行一次提示
    private static final long INDICATE_BUFFERING_TIMEOUT = 5 * 1000;
    // 计次：计算10秒内卡顿次数
    private static final long INDICATE_COUNT_BUFFERING_DURATION = 10 * 1000;
    // 计次：2次卡顿进行一次提示
    private static final int INDICATE_BUFFERING_COUNT_TOO_MORE_THRESHOLD = 2;
    // 一次提示显示时长
    private static final int SHOW_INDICATE_DURATION = 5 * 1000;

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="变量">

    private View rootView;
    private LinearLayout plvPoorNetworkIndicateLayout;
    private TextView plvPoorNetworkSwitchBitrateTv;
    private TextView plvPoorNetworkIgnoreTv;

    private final LinkedList<BufferingCacheVO> bufferingCacheList = new LinkedList<>();

    private static final int MSG_WHAT_DROP_FIRST_BUFFERING = 1;
    private static final int MSG_WHAT_CHECK_BUFFERING_TOO_LONG = 2;
    private static final int MSG_WHAT_HIDE_LAYOUT = 3;
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MSG_WHAT_DROP_FIRST_BUFFERING:
                    removeFirstBufferCache();
                    break;
                case MSG_WHAT_CHECK_BUFFERING_TOO_LONG:
                    checkBufferingTooLong();
                    break;
                case MSG_WHAT_HIDE_LAYOUT:
                    show(false);
                    break;
                default:
            }
        }
    };

    private OnViewActionListener onViewActionListener;

    private long lastSeekTimestamp = 0;
    private boolean ignoreNetworkPoor = false;

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="构造方法">

    public PolyvNetworkPoorIndicateLayout(@NonNull Context context) {
        this(context, null);
    }

    public PolyvNetworkPoorIndicateLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PolyvNetworkPoorIndicateLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="初始化方法">

    private void initView() {
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.polyv_player_network_poor_indicate_layout, this);
        findView();
        setIgnoreOnClick();
    }

    private void findView() {
        plvPoorNetworkIndicateLayout = (LinearLayout) rootView.findViewById(R.id.plv_poor_network_indicate_layout);
        plvPoorNetworkSwitchBitrateTv = (TextView) rootView.findViewById(R.id.plv_poor_network_switch_bitrate_tv);
        plvPoorNetworkIgnoreTv = (TextView) rootView.findViewById(R.id.plv_poor_network_ignore_tv);
    }

    private void setIgnoreOnClick() {
        plvPoorNetworkIgnoreTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ignoreNetworkPoor = true;
                show(false);
            }
        });
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="API">

    public void notifyBufferingStart() {
        if (ignoreNetworkPoor) {
            return;
        }
        final boolean isBufferBySeek = System.currentTimeMillis() - lastSeekTimestamp < 500;
        bufferingCacheList.add(new BufferingCacheVO(System.currentTimeMillis(), isBufferBySeek));
        handler.sendEmptyMessageDelayed(MSG_WHAT_CHECK_BUFFERING_TOO_LONG, INDICATE_BUFFERING_TIMEOUT);
        handler.sendEmptyMessageDelayed(MSG_WHAT_DROP_FIRST_BUFFERING, INDICATE_COUNT_BUFFERING_DURATION);
        checkBufferingCountTooMore();
    }

    public void notifyBufferingEnd() {
        if (!bufferingCacheList.isEmpty()) {
            bufferingCacheList.getLast().bufferingEnd = true;
        }
    }

    public void notifySeek() {
        lastSeekTimestamp = System.currentTimeMillis();
    }

    public void reset() {
        bufferingCacheList.clear();
        show(false);
    }

    public void setOnViewActionListener(OnViewActionListener onViewActionListener) {
        this.onViewActionListener = onViewActionListener;
    }

    public void destroy() {
        handler.removeMessages(MSG_WHAT_DROP_FIRST_BUFFERING);
        handler.removeMessages(MSG_WHAT_CHECK_BUFFERING_TOO_LONG);
        handler.removeMessages(MSG_WHAT_HIDE_LAYOUT);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="内部逻辑">

    private void removeFirstBufferCache() {
        if (!bufferingCacheList.isEmpty()) {
            bufferingCacheList.removeFirst();
        }
    }

    private void checkBufferingTooLong() {
        if (ignoreNetworkPoor) {
            return;
        }
        if (bufferingCacheList.isEmpty()) {
            return;
        }
        if (bufferingCacheList.getLast().bufferingEnd) {
            return;
        }
        if (System.currentTimeMillis() - bufferingCacheList.getLast().timestamp < INDICATE_BUFFERING_TIMEOUT) {
            return;
        }

        if (canUpdateBitrate()) {
            show(true);
            handler.removeMessages(MSG_WHAT_HIDE_LAYOUT);
            handler.sendMessageDelayed(handler.obtainMessage(MSG_WHAT_HIDE_LAYOUT), SHOW_INDICATE_DURATION);
        }
    }

    private void checkBufferingCountTooMore() {
        while (!bufferingCacheList.isEmpty()) {
            if (System.currentTimeMillis() - bufferingCacheList.getFirst().timestamp > INDICATE_COUNT_BUFFERING_DURATION) {
                bufferingCacheList.removeFirst();
            } else {
                break;
            }
        }
        if (bufferingCacheList.size() < INDICATE_BUFFERING_COUNT_TOO_MORE_THRESHOLD) {
            return;
        }
        int countExceptSeekBuffer = 0;
        for (BufferingCacheVO vo : bufferingCacheList) {
            if (!vo.bySeek) {
                countExceptSeekBuffer++;
            }
        }
        if (countExceptSeekBuffer < INDICATE_BUFFERING_COUNT_TOO_MORE_THRESHOLD) {
            return;
        }

        if (canUpdateBitrate()) {
            show(true);
            handler.removeMessages(MSG_WHAT_HIDE_LAYOUT);
            handler.sendMessageDelayed(handler.obtainMessage(MSG_WHAT_HIDE_LAYOUT), SHOW_INDICATE_DURATION);
        }
    }

    private void show(boolean toShow) {
        if (toShow) {
            setVisibility(VISIBLE);
        } else {
            setVisibility(GONE);
        }
    }

    private boolean canUpdateBitrate() {
        if (onViewActionListener == null) {
            return false;
        }
        final int lowerBitrate = onViewActionListener.getLowerBitrate();
        if (lowerBitrate < PolyvBitRate.liuChang.getNum()) {
            return false;
        }
        final PolyvBitRate bitRate = PolyvBitRate.getBitRate(lowerBitrate);
        plvPoorNetworkSwitchBitrateTv.setText("切换到" + bitRate.getName());
        plvPoorNetworkSwitchBitrateTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                show(false);
                if (onViewActionListener != null) {
                    boolean success = onViewActionListener.changeBitrate(lowerBitrate);
                    if (success) {
                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("已为您切换为" + bitRate.getName());
                        spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#31adfe")), 6, spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        Toast.makeText(getContext(), spannableStringBuilder, Toast.LENGTH_SHORT).show();
                        PolyvNetworkPoorIndicateLayout.this.reset();
                    } else {
                        Toast.makeText(getContext(), "切换失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        return true;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="对外接口定义">

    public interface OnViewActionListener {
        int getLowerBitrate();

        boolean changeBitrate(int bitrate);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="内部类">

    private class BufferingCacheVO {
        private long timestamp;
        private boolean bufferingEnd;
        private boolean bySeek;

        public BufferingCacheVO(long timestamp, boolean bySeek) {
            this.timestamp = timestamp;
            this.bufferingEnd = false;
            this.bySeek = bySeek;
        }
    }

    // </editor-fold>

}
