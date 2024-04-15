package com.easefun.polyvsdk.player;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.video.PolyvVideoView;

/**
 * 线路切换界面
 * @author Lionel 2019-3-20
 */
public class PolyvPlayerPlayRouteView extends RelativeLayout implements View.OnClickListener {
// <editor-fold defaultstate="collapsed" desc="成员变量">
    /**
     * 关闭按钮
     */
    private ImageView ivCloseRoute;
    /**
     * 线路1按钮
     */
    private TextView tvRoute1;
    /**
     * 线路2按钮
     */
    private TextView tvRoute2;
    /**
     * 线路3按钮
     */
    private TextView tvRoute3;
    private IChangeRouteListener changeRouteListener;
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="构造方法">
    public PolyvPlayerPlayRouteView(Context context) {
        this(context, null);
    }

    public PolyvPlayerPlayRouteView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PolyvPlayerPlayRouteView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.polyv_player_play_route_view, this);
        findIdAndNew();
        initView();
        addListener();
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="初始化方法">
    private void findIdAndNew() {
        ivCloseRoute = (ImageView) findViewById(R.id.iv_close_route);
        tvRoute1 = (TextView) findViewById(R.id.tv_route1);
        tvRoute2 = (TextView) findViewById(R.id.tv_route2);
        tvRoute3 = (TextView) findViewById(R.id.tv_route3);
    }

    private void initView() {
        tvRoute1.setTag(1);
        tvRoute2.setTag(2);
        tvRoute3.setTag(3);
    }

    private void addListener() {
        ivCloseRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });

        tvRoute1.setOnClickListener(this);
        tvRoute2.setOnClickListener(this);
        tvRoute3.setOnClickListener(this);
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="功能方法">
    /**
     * 显示界面
     * @param videoView 播放器
     */
    public void show(@NonNull PolyvVideoView videoView) {
        initSelectedRouteView(videoView);
        setVisibility(View.VISIBLE);
    }

    private void initSelectedRouteView(@NonNull PolyvVideoView videoView) {
        int routeCount = videoView.getRouteCount();
        tvRoute1.setVisibility(routeCount >= 1 ? View.VISIBLE : View.GONE);
        tvRoute2.setVisibility(routeCount >= 2 ? View.VISIBLE : View.GONE);
        tvRoute3.setVisibility(routeCount >= 3 ? View.VISIBLE : View.GONE);

        tvRoute1.setSelected(false);
        tvRoute2.setSelected(false);
        tvRoute3.setSelected(false);

        int currentRoute = videoView.getCurrentRoute();
        if (currentRoute == 1) {
            tvRoute1.setSelected(true);
        } else if (currentRoute == 2) {
            tvRoute2.setSelected(true);
        } else {
            tvRoute3.setSelected(true);
        }
    }

    /**
     * 隐藏界面
     */
    public void hide() {
        setVisibility(View.GONE);
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="设置监听回调">
    public void setChangeRouteListener(IChangeRouteListener changeRouteListener) {
        this.changeRouteListener = changeRouteListener;
    }

    @Override
    public void onClick(View v) {
        if (v.isSelected()) {
            return;
        }

        hide();
        if (changeRouteListener != null) {
            changeRouteListener.onChange((int) v.getTag());
        }
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="监听回调接口定义">
    /**
     * 切换线路监听回调
     * @author Lionel 2019-3-20
     */
    public interface IChangeRouteListener {
        /**
         * 切换回调
         * @param route 目标线路
         */
        void onChange(int route);
    }
// </editor-fold>
}
