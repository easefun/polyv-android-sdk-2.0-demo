package com.easefun.polyvsdk.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.easefun.polyvsdk.PolyvSDKClient;
import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.RestVO;
import com.easefun.polyvsdk.adapter.EndlessRecyclerOnScrollListener;
import com.easefun.polyvsdk.adapter.HeaderViewRecyclerAdapter;
import com.easefun.polyvsdk.adapter.PolyvOnlineListViewAdapter;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class PolyvOnlineVideoActivity extends Activity implements View.OnClickListener {
    private ImageView iv_finish;
    private RecyclerView lv_online;
    private PolyvOnlineListViewAdapter lv_online_adapter;
    private HeaderViewRecyclerAdapter mAdapter;
    private List<RestVO> data;
    private View loadMoreView;
    private int pageNum = 1, pageSize = 20;
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.polyv_activity_online_video);
        findIdAndNew();
        initView();
    }

    private void findIdAndNew() {
        iv_finish = (ImageView) findViewById(R.id.iv_finish);
        lv_online = (RecyclerView) findViewById(R.id.lv_online);
        data = new ArrayList<>();
    }

    private void initView() {
        lv_online_adapter = new PolyvOnlineListViewAdapter(lv_online, this, data);
        lv_online.setHasFixedSize(true);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        lv_online.setLayoutManager(mLinearLayoutManager);
        mAdapter = new HeaderViewRecyclerAdapter(lv_online_adapter);
        lv_online.setAdapter(mAdapter);
        createLoadMoreView();
        lv_online.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLinearLayoutManager) {

            @Override
            public void onLoadMore(int i) {
                pageNum++;
                loadMoreView.setVisibility(View.VISIBLE);
                new LoadVideoList().execute();
            }
        });
        new LoadVideoList().execute();
        iv_finish.setOnClickListener(this);
    }

    private void createLoadMoreView() {
        loadMoreView = LayoutInflater.from(this).inflate(R.layout.polyv_bottom_loadmorelayout, lv_online, false);
        mAdapter.addFooterView(loadMoreView);
        loadMoreView.setVisibility(View.GONE);
    }

    class LoadVideoList extends AsyncTask<String, String, List<RestVO>> {

        @Override
        protected List<RestVO> doInBackground(String... arg0) {
            try {
                return PolyvSDKClient.getInstance().getVideoList(pageNum, pageSize);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<RestVO> result) {
            super.onPostExecute(result);
            loadMoreView.setVisibility(View.GONE);
            if (result == null) {
                mAdapter.removeFootView();
                return;
            }
            if (result.size() < pageSize)
                mAdapter.removeFootView();
            data.addAll(result);
            if (pageNum * pageSize - pageSize - 1 > 0) {
                mAdapter.notifyItemRangeChanged(pageNum * pageSize - pageSize - 1, pageSize);
            } else {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_finish:
                finish();
                break;
        }
    }
}
