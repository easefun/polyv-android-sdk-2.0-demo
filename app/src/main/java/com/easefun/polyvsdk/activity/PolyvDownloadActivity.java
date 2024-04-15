package com.easefun.polyvsdk.activity;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.adapter.PolyvPlayerFragmentAdapter;
import com.easefun.polyvsdk.fragment.PolyvDownloadFragment;

import java.util.ArrayList;
import java.util.List;

public class PolyvDownloadActivity extends FragmentActivity {
    private ViewPager vp_download;
    private List<Fragment> downloadFragments;
    private PolyvPlayerFragmentAdapter downloadAdapter;
    // 返回按钮
    private ImageView iv_finish;
    private TextView tv_downloaded, tv_downloading;
    private View v_tabline;

    private PolyvDownloadFragment downloadedFragment;

    private void findIdAndNew() {
        vp_download = (ViewPager) findViewById(R.id.vp_download);
        iv_finish = (ImageView) findViewById(R.id.iv_finish);
        tv_downloaded = (TextView) findViewById(R.id.tv_downloaded);
        tv_downloading = (TextView) findViewById(R.id.tv_downloading);
        v_tabline = findViewById(R.id.v_tabline);
        downloadFragments = new ArrayList<>();
    }

    public PolyvDownloadFragment getDownloadedFragment() {
        return downloadedFragment;
    }

    private void initView() {
        Bundle bundle = new Bundle();
        downloadedFragment = new PolyvDownloadFragment();
        bundle.putBoolean("isFinished", true);
        downloadedFragment.setArguments(bundle);
        PolyvDownloadFragment downloadingFragment = new PolyvDownloadFragment();
        bundle = new Bundle();
        bundle.putBoolean("isFinished", false);
        downloadingFragment.setArguments(bundle);
        downloadFragments.add(downloadedFragment);
        downloadFragments.add(downloadingFragment);
        downloadAdapter = new PolyvPlayerFragmentAdapter(getSupportFragmentManager(), downloadFragments);
        vp_download.setAdapter(downloadAdapter);
        vp_download.setOffscreenPageLimit(1);
        vp_download.setPageMargin(30);
        vp_download.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                tv_downloading.setSelected(false);
                tv_downloaded.setSelected(false);
                if (position == 0) {
                    tv_downloaded.setSelected(true);
                } else if (position == 1) {
                    tv_downloading.setSelected(true);
                }
                setLineLocation(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        iv_finish.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_downloaded.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                vp_download.setCurrentItem(0);
            }
        });
        tv_downloading.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                vp_download.setCurrentItem(1);
            }
        });

        final boolean isStarting = getIntent().getBooleanExtra("isStarting", false);
        if (isStarting) {
            tv_downloading.setSelected(true);
        } else {
            tv_downloaded.setSelected(true);
        }
        vp_download.setCurrentItem(isStarting ? 1 : 0);
        v_tabline.post(new Runnable() {
            @Override
            public void run() {
                setLineLocation(isStarting ? 1 : 0);
            }
        });
    }

    private void setLineLocation(int position) {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v_tabline.getLayoutParams();
        lp.width = tv_downloaded.getWidth();
        int[] wh = new int[2];
        if (position == 0) {
            tv_downloaded.getLocationInWindow(wh);
        } else if (position == 1) {
            tv_downloading.getLocationInWindow(wh);
        }
        lp.leftMargin = wh[0];
        v_tabline.setLayoutParams(lp);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.polyv_activity_downlaod);
        findIdAndNew();
        initView();
    }
}
