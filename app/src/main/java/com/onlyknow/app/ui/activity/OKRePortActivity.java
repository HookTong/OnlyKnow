package com.onlyknow.app.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;

import com.onlyknow.app.R;
import com.onlyknow.app.api.OKServiceResult;
import com.onlyknow.app.api.user.OKReportApi;
import com.onlyknow.app.db.bean.OKUserInfoBean;
import com.onlyknow.app.ui.OKBaseActivity;

public class OKRePortActivity extends OKBaseActivity implements OKReportApi.onCallBack {
    private CheckBox checkBox1, checkBox2, checkBox3, checkBox4;
    private AppCompatButton appCompatButton;

    public final static String KEY_TYPE = "type";
    public final static String KEY_ID = "id";
    public final static String KEY_NAME = "username";

    public final static String TYPE_CARD = OKReportApi.Params.TYPE_REPORT_CARD;
    public final static String TYPE_USER = OKReportApi.Params.TYPE_REPORT_USER;
    public final static String TYPE_COMMENT = OKReportApi.Params.TYPE_REPORT_COMMENT;
    public final static String TYPE_COMMENT_REPLY = OKReportApi.Params.TYPE_REPORT_COMMENT_REPLY;

    private final String item1 = "report_content_entry_item1";
    private final String item2 = "report_content_entry_item2";
    private final String item3 = "report_content_entry_item3";
    private final String item4 = "report_content_entry_item4";

    private Bundle mBundle;
    private String RePortType = "";

    private OKReportApi okReportApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ok_activity_report);
        initUserInfoSharedPreferences();
        initSystemBar(this);
        mBundle = getIntent().getExtras();

        RePortType = mBundle.getString(KEY_TYPE);

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
        if (okReportApi != null) {
            okReportApi.cancelTask();
        }
    }

    private void init() {
        appCompatButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String msg = "";
                if (checkBox1.isChecked()) {
                    msg = msg + item1 + "/";
                }
                if (checkBox2.isChecked()) {
                    msg = msg + item2 + "/";
                }
                if (checkBox3.isChecked()) {
                    msg = msg + item3 + "/";
                }
                if (checkBox4.isChecked()) {
                    msg = msg + item4 + "";
                }

                if (!TextUtils.isEmpty(msg)) {
                    if (USER_INFO_SP.getBoolean("STATE", false)) {
                        if (TYPE_USER.equals(RePortType)) {

                            report(TYPE_USER, msg, -1, mBundle.getString(KEY_NAME));

                        } else if (TYPE_CARD.equals(RePortType)) {

                            report(TYPE_CARD, msg, Integer.parseInt(mBundle.getString(KEY_ID)), "");

                        } else if (TYPE_COMMENT.equals(RePortType)) {

                            report(TYPE_COMMENT, msg, Integer.parseInt(mBundle.getString(KEY_ID)), "");

                        } else if (TYPE_COMMENT_REPLY.equals(RePortType)) {

                            report(TYPE_COMMENT_REPLY, msg, Integer.parseInt(mBundle.getString(KEY_ID)), "");

                        } else {
                            showSnackBar(appCompatButton, "非法的举报类型!", "");
                        }
                    } else {
                        startUserActivity(null, OKLoginActivity.class);
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

    private void report(String type, String msg, int id, String name) {
        OKReportApi.Params params = new OKReportApi.Params();

        params.setId(id);
        params.setReportUsername(name);
        params.setType(type);
        params.setMessage(msg);

        params.setUsername(USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));

        showProgressDialog("正在提交举报信息");

        if (okReportApi != null) {
            okReportApi.cancelTask();
        }
        okReportApi = new OKReportApi(OKRePortActivity.this);
        okReportApi.requestReport(params, OKRePortActivity.this);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mToolbar != null) {
            mToolbar.setTitle("");
        }
    }

    @Override
    public void reportComplete(OKServiceResult<Object> result, String type) {
        closeProgressDialog();

        if (result == null || !result.isSuccess()) {
            showSnackBar(appCompatButton, "举报失败,请检查网络!", "");
            return;
        }

        showSnackBar(appCompatButton, "举报成功", "");
    }
}
