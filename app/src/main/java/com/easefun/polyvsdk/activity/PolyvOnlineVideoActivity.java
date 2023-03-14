package com.easefun.polyvsdk.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    private TextView tv_search;
    private RecyclerView lv_online;
    private PolyvOnlineListViewAdapter lv_online_adapter;
    private HeaderViewRecyclerAdapter mAdapter;
    private List<RestVO> data;
    private View loadMoreView;
    private int pageNum = 1, pageSize = 20;

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
        tv_search = (TextView) findViewById(R.id.tv_search);
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
        tv_search.setOnClickListener(this);
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
            synchronized (PolyvOnlineVideoActivity.this) {
                loadMoreView.setVisibility(View.GONE);
                if (result == null) {
                    mAdapter.removeFootView();
                    return;
                }
                if (result.size() < pageSize)
                    mAdapter.removeFootView();
                data.addAll(result);

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
            case R.id.tv_search:
                gotoSetVid();
                break;
        }
    }

    private void gotoSetVid() {
        final EditText etConfig = new EditText(PolyvOnlineVideoActivity.this);
        new AlertDialog.Builder(PolyvOnlineVideoActivity.this).setTitle("设置Vid")
                .setView(etConfig)
                .setPositiveButton("保存并跳转", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String vid = etConfig.getText().toString().trim();
                        if (!TextUtils.isEmpty(vid)) {
                            Intent intent = PolyvPlayerActivity.newIntent(PolyvOnlineVideoActivity.this, PolyvPlayerActivity.PlayMode.portrait, vid);
                            intent.putExtra(PolyvMainActivity.IS_VLMS_ONLINE, false);
                            PolyvOnlineVideoActivity.this.startActivity(intent);
                        } else {
                            Toast.makeText(PolyvOnlineVideoActivity.this,"请输入Vid", Toast.LENGTH_LONG).show();
                        }
                    }
                }).setNegativeButton("取消",null).show();
    }
}
