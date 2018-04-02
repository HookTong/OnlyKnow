package com.onlyknow.app.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;

import com.onlyknow.app.OKConstant;
import com.onlyknow.app.R;
import com.onlyknow.app.api.OKUserOperationApi;
import com.onlyknow.app.database.bean.OKUserInfoBean;
import com.onlyknow.app.ui.OKBaseActivity;

import java.util.HashMap;
import java.util.Map;

public class OKRePortActivity extends OKBaseActivity implements OKUserOperationApi.onCallBack {
    private CheckBox checkBox1, checkBox2, checkBox3, checkBox4;
    private AppCompatButton appCompatButton;

    private Bundle mBundle;
    private String RePortType = "";

    private OKUserOperationApi mOKUserOperationApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ok_activity_report);
        initUserInfoSharedPreferences();
        initSystemBar(this);
        mBundle = getIntent().getExtras();
        RePortType = mBundle.getString("JUBAO_TYPE");
        findView();
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOKUserOperationApi != null) {
            mOKUserOperationApi.cancelTask();
        }
    }

    private void init() {
        appCompatButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String msg = "";
                if (checkBox1.isChecked()) {
                    msg = msg + "JuBao_itme1/";
                }
                if (checkBox2.isChecked()) {
                    msg = msg + "JuBao_itme2/";
                }
                if (checkBox3.isChecked()) {
                    msg = msg + "JuBao_itme3/";
                }
                if (checkBox4.isChecked()) {
                    msg = msg + "JuBao_itme4";
                }

                if (!TextUtils.isEmpty(msg)) {
                    if (USER_INFO_SP.getBoolean("STATE", false)) {
                        if (RePortType.equals("USER")) {
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("username", USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
                            map.put("username2", mBundle.getString("JUBAO_NAME"));
                            map.put("card_id", "");
                            map.put("message", msg);
                            map.put("date", OKConstant.getNowDateByString());
                            map.put("type", "JUBAO_USER");
                            if (mOKUserOperationApi != null) {
                                mOKUserOperationApi.cancelTask();
                            }
                            mOKUserOperationApi = new OKUserOperationApi(OKRePortActivity.this);
                            mOKUserOperationApi.requestUserOperation(map, OKUserOperationApi.TYPE_REPORT, OKRePortActivity.this);
                            showProgressDialog("正在提交举报信息");
                        } else if (RePortType.equals("CARD")) {
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("username", USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
                            map.put("username2", "");
                            map.put("card_id", mBundle.getString("JUBAO_CARD_ID"));
                            map.put("message", msg);
                            map.put("date", OKConstant.getNowDateByString());
                            map.put("type", "JUBAO_CARD");
                            if (mOKUserOperationApi != null) {
                                mOKUserOperationApi.cancelTask();
                            }
                            mOKUserOperationApi = new OKUserOperationApi(OKRePortActivity.this);
                            mOKUserOperationApi.requestUserOperation(map, OKUserOperationApi.TYPE_REPORT, OKRePortActivity.this);
                            showProgressDialog("正在提交举报信息");
                        } else if (RePortType.equals("COMMENT")) {
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("username", USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
                            map.put("username2", "");
                            map.put("card_id", mBundle.getString("JUBAO_COM_ID"));
                            map.put("message", msg);
                            map.put("date", OKConstant.getNowDateByString());
                            map.put("type", "JUBAO_COMMENT");
                            if (mOKUserOperationApi != null) {
                                mOKUserOperationApi.cancelTask();
                            }
                            mOKUserOperationApi = new OKUserOperationApi(OKRePortActivity.this);
                            mOKUserOperationApi.requestUserOperation(map, OKUserOperationApi.TYPE_REPORT, OKRePortActivity.this);
                            showProgressDialog("正在提交举报信息");
                        } else if (RePortType.equals("COMMENT_REPLY")) {
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("username", USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
                            map.put("username2", "");
                            map.put("card_id", mBundle.getString("JUBAO_COMR_ID"));
                            map.put("message", msg);
                            map.put("date", OKConstant.getNowDateByString());
                            map.put("type", "JUBAO_COMMENT_REPLY");
                            if (mOKUserOperationApi != null) {
                                mOKUserOperationApi.cancelTask();
                            }
                            mOKUserOperationApi = new OKUserOperationApi(OKRePortActivity.this);
                            mOKUserOperationApi.requestUserOperation(map, OKUserOperationApi.TYPE_REPORT, OKRePortActivity.this);
                            showProgressDialog("正在提交举报信息");
                        } else {
                            startUserActivity(null, OKLoginActivity.class);
                        }
                    }
                }
            }
        });

        mToolbarBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void findView() {
        super.findCommonToolbarView(this);
        mToolbarBack.setVisibility(View.VISIBLE);
        mToolbarTitle.setVisibility(View.VISIBLE);

        mToolbarTitle.setText("举报");

        appCompatButton = (AppCompatButton) findViewById(R.id.REPORT_TiJiaoBut);
        checkBox1 = (CheckBox) findViewById(R.id.REPORT_CheckBox1);
        checkBox2 = (CheckBox) findViewById(R.id.REPORT_CheckBox2);
        checkBox3 = (CheckBox) findViewById(R.id.REPORT_CheckBox3);
        checkBox4 = (CheckBox) findViewById(R.id.REPORT_CheckBox4);
        setSupportActionBar(mToolbar);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mToolbar != null) {
            mToolbar.setTitle("");
        }
    }

    @Override
    public void userOperationApiComplete(boolean isSuccess, String type) {
        if (isSuccess) {
            showSnackBar(appCompatButton, "举报成功", "");
        } else {
            showSnackBar(appCompatButton, "举报失败,请检查网络!", "");
        }
        closeProgressDialog();
    }
}
