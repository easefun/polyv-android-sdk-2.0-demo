package com.easefun.polyvsdk.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.easefun.polyvsdk.PolyvSDKClient;
import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.RestVO;
import com.easefun.polyvsdk.adapter.PolyvOnlineListViewAdapter;

import org.json.JSONException;

import java.util.List;

public class PolyvOnlineVideoActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "VideoList";
    private ImageView iv_finish;
    private ListView lv_online;
    private PolyvOnlineListViewAdapter lv_online_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.polyv_activity_online_video);
        findIdAndNew();
        initView();
    }

    private void findIdAndNew() {
        iv_finish = (ImageView) findViewById(R.id.iv_finish);
        lv_online = (ListView) findViewById(R.id.lv_online);
    }

    private void initView() {
        new LoadVideoList().execute();
        iv_finish.setOnClickListener(this);
    }

    class LoadVideoList extends AsyncTask<String, String, List<RestVO>> {

        @Override
        protected List<RestVO> doInBackground(String... arg0) {
            try {
                return PolyvSDKClient.getInstance().getVideoList(1, 10);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<RestVO> result) {
            super.onPostExecute(result);
            if (result == null) return;
            lv_online_adapter = new PolyvOnlineListViewAdapter(PolyvOnlineVideoActivity.this, result);
            lv_online.setAdapter(lv_online_adapter);
            String a = result.toString();
            Log.i(TAG, a);
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
