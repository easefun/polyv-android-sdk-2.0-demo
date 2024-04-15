package com.easefun.polyvsdk.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.easefun.polyvsdk.PolyvSDKClient;
import com.easefun.polyvsdk.PolyvUserClient;
import com.easefun.polyvsdk.R;

/**
 * @author df
 * @create 2019/4/16
 * @Describe
 */
public class PolyvLoginActivity extends Activity implements View.OnClickListener {

    private TextView accountId1;
    private TextView accountId2;
    private TextView logout;
    private TextView iv_gohome;

    //测试数据
    String[] viewers = new String[]{"viewerId", "viewerId1"};
    private boolean hasLogout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.polyv_activity_login);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        accountId1 = (TextView) findViewById(R.id.account_id1);
        accountId2 = (TextView) findViewById(R.id.account_id2);
        logout = (TextView) findViewById(R.id.logout);
        iv_gohome = (TextView) findViewById(R.id.gohome);

        accountId1.setOnClickListener(this);
        accountId2.setOnClickListener(this);
        logout.setOnClickListener(this);
        iv_gohome.setOnClickListener(this);
    }

    public void switchAccount(int index) {
        //测试多账户切换数据
        PolyvSDKClient.getInstance().openMultiDownloadAccount(true);
        if (index >= viewers.length) {
            index = 0;
        }

        if (hasLogout) {
            PolyvUserClient.getInstance().logout(this);
        }
        hasLogout = true;
        PolyvUserClient.getInstance().login(viewers[index++], this);
        Toast.makeText(this, "切换账户到：" + PolyvSDKClient.getInstance().getViewerId(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.account_id1:
                switchAccount(0);
                break;
            case R.id.account_id2:
                switchAccount(1);
                break;
            case R.id.logout:
                PolyvUserClient.getInstance().logout(this);
                Toast.makeText(this, "登出账户", Toast.LENGTH_SHORT).show();
                break;
            case  R.id.gohome:
                Intent intent = new Intent(this,PolyvMainActivity.class);
                startActivity(intent);
                break;
        }
    }
}
