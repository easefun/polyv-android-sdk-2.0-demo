package com.easefun.polyvsdk.ppt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.po.ppt.PolyvPptInfo;
import com.easefun.polyvsdk.po.ppt.PolyvPptPageInfo;
import com.easefun.polyvsdk.util.PolyvImageLoader;
import com.easefun.polyvsdk.video.PolyvVideoView;
import com.easefun.polyvsdk.view.PolyvCircleProgressView;

import java.util.ArrayList;

/**
 * 三分屏布局的ppt控件
 */
public class PolyvPPTView extends FrameLayout {
    private PolyvCircleProgressView loadView;
    private TextView textView;
    private ImageView imageView;
    private PolyvVideoView currentVideoView;
    private String currentImg;
    private String currentVid;
    private PolyvPptInfo currentPPTVO;

    private int preloadCount = 2;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                int position = Math.max(0, currentVideoView.getCurrentPosition() / 1000);
                PolyvPptPageInfo pageBean = findClosestPPT(position, currentPPTVO.getPages());
                if (pageBean != null && pageBean.getImg() != null && !pageBean.getImg().equals(currentImg)) {
                    PolyvImageLoader.getInstance().loadImageOrigin(getContext(), currentImg = pageBean.getImg(), imageView, imageView.getDrawable());
                    if (Build.VERSION.SDK_INT <29){
                        for (int i = 1; i <= preloadCount; i++) {
                            if (pageBean.getIndex() + i < currentPPTVO.getPages().size()) {
                                PolyvImageLoader.getInstance().preloadImage(getContext(), currentPPTVO.getPages().get(pageBean.getIndex() + i).getImg());
                            }
                        }
                    }
                }
                handler.sendEmptyMessageDelayed(1, 1000);
            }
        }
    };

    public PolyvPPTView(Context context) {
        this(context, null);
    }

    public PolyvPPTView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PolyvPPTView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        addView(LayoutInflater.from(context).inflate(R.layout.polyv_ppt_content, this, false));
        imageView = (ImageView) findViewById(R.id.ppt_img);
        textView = (TextView) findViewById(R.id.no_ppt);
        loadView = (PolyvCircleProgressView) findViewById(R.id.load_view);
    }

    public Bitmap getImg() {
        if (imageView != null) {
            return drawableToBitmap(imageView.getDrawable());
        }
        return null;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        //注意，下面三行代码要用到，否则在View或者SurfaceView里的canvas.drawBitmap会看不到图
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    public void acceptProgress(int loadPPTProgress) {
        if (loadView != null) {
            loadView.setVisibility(View.VISIBLE);
            loadView.setmCurrent(loadPPTProgress);
        }
        if (textView != null) {
            textView.setVisibility(View.GONE);
        }
    }

    public void acceptPPTCallback(final PolyvVideoView videoView, String vid, boolean hasPPT, PolyvPptInfo pptvo) {
        handler.removeMessages(1);
        if (currentVid != null && !currentVid.equals(vid) || (hasPPT && pptvo == null)) {
            imageView.setImageDrawable(null);
            loadView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
        }
        currentVid = vid;
        if (videoView != null && hasPPT && pptvo != null) {
            textView.setVisibility(View.GONE);
            loadView.setVisibility(View.GONE);
            currentVideoView = videoView;
            currentPPTVO = pptvo;

            handler.sendEmptyMessage(1);
        }
    }

    private PolyvPptPageInfo findClosestPPT(int position, ArrayList<PolyvPptPageInfo> page) {
        PolyvPptPageInfo closestPageBean = null;
        int closestSec = -1;
        if (page != null) {
            for (int i = 0; i < page.size(); i++) {
                PolyvPptPageInfo pageBean = page.get(i);
                if (pageBean.getSec() == position) {
                    return pageBean;
                } else if (pageBean.getSec() < position) {
                    if (closestSec == -1 || position - pageBean.getSec() < closestSec) {
                        closestSec = position - pageBean.getSec();
                        closestPageBean = pageBean;
                    }
                }
            }
            if (closestPageBean == null && page.size() > 0) {
                closestPageBean = page.get(0);
            }
        }
        return closestPageBean;
    }

    public void destroy() {
        handler.removeMessages(1);
    }
}
