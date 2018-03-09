package com.onlyknow.app.ui.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.onlyknow.app.R;
import com.onlyknow.app.database.bean.OKSafetyInfoBean;
import com.onlyknow.app.net.OKBusinessNet;
import com.onlyknow.app.ui.OKBaseActivity;

import java.util.HashMap;
import java.util.Map;

public class OKContentActivity extends OKBaseActivity {
    private TextView textViewContent;

    private Bundle mBundle;

    private ContentTask mContentTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ok_activity_content);
        initSystemBar(this);
        mBundle = this.getIntent().getExtras();
        findView();
        init();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mToolbar != null) {
            mToolbar.setTitle("");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Map<String, String> map = new HashMap<>();
        map.put("type", mBundle.getString("TYPE"));
        map.put("value", mBundle.getString("VALUE"));

        mContentTask = new ContentTask();
        mContentTask.executeOnExecutor(exec, map);

        showProgressDialog("正在获取内容...");
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mContentTask != null && mContentTask.getStatus() == AsyncTask.Status.RUNNING) {
            mContentTask.cancel(true);
        }
    }

    private void init() {
        setSupportActionBar(mToolbar);

        mToolbarBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                OKContentActivity.this.finish();
            }
        });
    }

    private void findView() {
        super.findCommonToolbarView(this);
        mToolbarBack.setVisibility(View.VISIBLE);
        mToolbarTitle.setVisibility(View.VISIBLE);
        mToolbarTitle.setText("内容查看器");

        textViewContent = (TextView) findViewById(R.id.content_text_message);
    }

    private class ContentTask extends AsyncTask<Map<String, String>, Void, OKSafetyInfoBean> {

        @Override
        protected OKSafetyInfoBean doInBackground(Map<String, String>... params) {
            if (isCancelled()) {
                return null;
            }

            return new OKBusinessNet().securityCheck(params[0]);
        }

        @Override
        protected void onPostExecute(OKSafetyInfoBean bean) {
            super.onPostExecute(bean);

            closeProgressDialog();

            if (isCancelled()) {
                return;
            }

            if (bean == null) {
                showSnackBar(textViewContent, "获取失败!", "");
                return;
            }

            if (!TextUtils.isEmpty(bean.getAVU_DESCRIBE())) {
                textViewContent.setText(bean.getAVU_DESCRIBE());
            } else if (!TextUtils.isEmpty(bean.getUA_CONTENT())) {
                textViewContent.setText(bean.getUA_CONTENT());
            }
        }
    }
}
