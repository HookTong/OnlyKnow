package com.onlyknow.app.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.onlyknow.app.OKConstant;
import com.onlyknow.app.R;
import com.onlyknow.app.api.app.OKLoadAppInfoApi;
import com.onlyknow.app.db.bean.OKAppInfoBean;
import com.onlyknow.app.ui.OKBaseActivity;

public class OKContentActivity extends OKBaseActivity implements OKLoadAppInfoApi.onCallBack {
    private TextView textViewContent;

    private Bundle mBundle;

    private OKLoadAppInfoApi mOKLoadAppInfoApi;

    public final static String KEY_TYPE = "type";
    public final static String TYPE_APP = "app";
    public final static String TYPE_AGREEMENT = "agreement";

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

        OKLoadAppInfoApi.Params params = new OKLoadAppInfoApi.Params();
        params.setVersion(OKConstant.APP_VERSION);
        params.setType(OKLoadAppInfoApi.Params.TYPE_CHECK);

        showProgressDialog("正在获取内容...");
        if (mOKLoadAppInfoApi != null) {
            mOKLoadAppInfoApi.cancelTask();
        }
        mOKLoadAppInfoApi = new OKLoadAppInfoApi(this);
        mOKLoadAppInfoApi.requestAppInfo(params, this);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mOKLoadAppInfoApi != null) {
            mOKLoadAppInfoApi.cancelTask();
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

    @Override
    public void loadAppInfoComplete(OKAppInfoBean bean) {
        closeProgressDialog();

        if (bean == null) {
            showSnackBar(textViewContent, "获取失败!", "");
            return;
        }

        if (TYPE_APP.equals(mBundle.getString(KEY_TYPE, ""))) {
            textViewContent.setText(bean.getAppDescribe());
        } else if (TYPE_AGREEMENT.equals(mBundle.getString(KEY_TYPE, ""))) {
            textViewContent.setText(bean.getAppUa());
        }
    }
}
