package com.onlyknow.app.ui.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.onlyknow.app.R;
import com.onlyknow.app.api.OKServiceResult;
import com.onlyknow.app.api.user.OKManagerUserApi;
import com.onlyknow.app.database.bean.OKUserInfoBean;
import com.onlyknow.app.ui.OKBaseActivity;
import com.onlyknow.app.utils.OKDateUtil;

public class OKLoginActivity extends OKBaseActivity implements OKManagerUserApi.onCallBack {
    private AppCompatButton but;
    private EditText editTextUserName, editTextPssword;
    private TextView textView;

    private OKManagerUserApi okManagerUserApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ok_activity_login);
        initUserInfoSharedPreferences();
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
        if (okManagerUserApi != null) {
            okManagerUserApi.cancelTask();
        }
    }

    private void init() {

        textView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startUserActivity(null, OKRegisteredActivity.class);
            }
        });

        but.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String name = editTextUserName.getText().toString();
                String pass = editTextPssword.getText().toString();
                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(pass)) {
                    OKManagerUserApi.Params params = new OKManagerUserApi.Params();
                    params.setUsername(name);
                    params.setPassword(pass);
                    params.setType(OKManagerUserApi.Params.TYPE_LOGIN);

                    showProgressDialog("正在登录...");

                    if (okManagerUserApi != null) {
                        okManagerUserApi.cancelTask();
                    }
                    okManagerUserApi = new OKManagerUserApi(OKLoginActivity.this);
                    okManagerUserApi.requestManagerUser(params, OKLoginActivity.this);

                } else {
                    showSnackBar(v, "用户名和密码不能为空!", "");
                }
            }
        });
    }

    private void findView() {
        but = (AppCompatButton) findViewById(R.id.login_btn);
        editTextUserName = (EditText) findViewById(R.id.login_input_email);
        editTextPssword = (EditText) findViewById(R.id.login_input_password);
        textView = (TextView) findViewById(R.id.login_link_signup);
    }

    @Override
    public void managerUserApiComplete(OKServiceResult<Object> serviceResult, String type, int pos) {
        closeProgressDialog();

        if (OKManagerUserApi.Params.TYPE_LOGIN.equals(type)) {

            if (serviceResult == null || !serviceResult.isSuccess()) {
                showSnackBar(but, "登录失败,请检查用户名和密码以及网络设置!", "");
                return;
            }

            OKUserInfoBean userInfoBean = new Gson().fromJson((String) serviceResult.getData(), OKUserInfoBean.class);

            if (userInfoBean == null) {
                showSnackBar(but, "登录失败,请检查用户名和密码以及网络设置!", "");
                return;
            }

            SharedPreferences.Editor editor = USER_INFO_SP.edit();
            editor.putInt(OKUserInfoBean.KEY_USER_ID, userInfoBean.getUserId());
            editor.putString(OKUserInfoBean.KEY_USERNAME, userInfoBean.getUserName());
            editor.putString(OKUserInfoBean.KEY_PASSWORD, userInfoBean.getUserPassword());
            editor.putString(OKUserInfoBean.KEY_NICKNAME, userInfoBean.getUserNickname());
            editor.putString(OKUserInfoBean.KEY_HEAD_PORTRAIT_URL, userInfoBean.getHeadPortraitUrl());
            editor.putString(OKUserInfoBean.KEY_HOME_PAGE_URL, userInfoBean.getHomepageUrl());
            editor.putString(OKUserInfoBean.KEY_PHONE, userInfoBean.getUserPhone());
            editor.putString(OKUserInfoBean.KEY_EMAIL, userInfoBean.getUserEmail());
            editor.putString(OKUserInfoBean.KEY_TAG, userInfoBean.getTag());
            editor.putString(OKUserInfoBean.KEY_SEX, userInfoBean.getSex());
            editor.putString(OKUserInfoBean.KEY_BIRTH_DATE, OKDateUtil.stringByDate(userInfoBean.getBirthDate()));
            editor.putInt(OKUserInfoBean.KEY_AGE, userInfoBean.getAge());
            editor.putString(OKUserInfoBean.KEY_RE_DATE, OKDateUtil.stringByDate(userInfoBean.getReDate()));
            editor.putInt(OKUserInfoBean.KEY_ME_WATCH, userInfoBean.getMeWatch());
            editor.putInt(OKUserInfoBean.KEY_ME_ATTENTION, userInfoBean.getMeAttention());
            editor.putInt(OKUserInfoBean.KEY_INTEGRAL, userInfoBean.getMeIntegral());
            editor.putInt(OKUserInfoBean.KEY_ARTICLE, userInfoBean.getMeArticle());
            editor.putString(OKUserInfoBean.KEY_EDIT_DATE, OKDateUtil.stringByDate(userInfoBean.getEditDate()));

            editor.putBoolean("STATE", true);
            editor.putBoolean("STATE_CHANGE", true);

            editor.commit();

            // 登录环信账号
            Bundle mBundle = new Bundle();
            mBundle.putString(OKUserInfoBean.KEY_USERNAME, userInfoBean.getUserName());
            mBundle.putString(OKUserInfoBean.KEY_PASSWORD, userInfoBean.getUserPassword());
            sendUserBroadcast(ACTION_MAIN_SERVICE_LOGIN_IM, mBundle);

            showSnackBar(but, "登录成功", "");

            finish();
        } else {
            showSnackBar(but, "异常操作类型!", "");
        }
    }
}
