package com.onlyknow.app.ui.activity;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.onlyknow.app.R;
import com.onlyknow.app.api.OKLogInApi;
import com.onlyknow.app.database.bean.OKUserInfoBean;
import com.onlyknow.app.ui.OKBaseActivity;

import java.util.HashMap;
import java.util.Map;

public class OKLoginActivity extends OKBaseActivity implements OKLogInApi.onCallBack {
    private AppCompatButton but;
    private EditText editTextUserName, editTextPssword;
    private TextView textView;

    private OKLogInApi mOKLogInApi;

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
        if (mOKLogInApi != null) {
            mOKLogInApi.cancelTask();
        }
    }

    private void init() {

        textView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startUserActivity(null, OKSigNupActivity.class);
            }
        });

        but.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String name = editTextUserName.getText().toString();
                String pass = editTextPssword.getText().toString();
                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(pass)) {
                    if (mOKLogInApi != null) {
                        mOKLogInApi.cancelTask();
                    }
                    mOKLogInApi = new OKLogInApi(OKLoginActivity.this);
                    mOKLogInApi.requestLogIn(name, pass, OKLoginActivity.this);

                    showProgressDialog("正在登录...");
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
    public void logInApiComplete(OKUserInfoBean mUserInfoBean, String imName, String imPass) {
        closeProgressDialog();

        if (mUserInfoBean != null) {
            SharedPreferences.Editor editor = USER_INFO_SP.edit();
            editor.putInt(OKUserInfoBean.KEY_USERID, mUserInfoBean.getUSERID());
            editor.putString(OKUserInfoBean.KEY_USERNAME, mUserInfoBean.getUSERNAME());
            editor.putString(OKUserInfoBean.KEY_PASSWORD, imPass);
            editor.putString(OKUserInfoBean.KEY_NICKNAME, mUserInfoBean.getNICKNAME());
            editor.putString(OKUserInfoBean.KEY_HEADPORTRAIT_URL, mUserInfoBean.getHEADPORTRAIT_URL());
            editor.putString(OKUserInfoBean.KEY_HEAD_URL, mUserInfoBean.getHEAD_URL());
            editor.putString(OKUserInfoBean.KEY_PHONE, mUserInfoBean.getPHONE());
            editor.putString(OKUserInfoBean.KEY_EMAIL, mUserInfoBean.getEMAIL());
            editor.putString(OKUserInfoBean.KEY_QIANMIN, mUserInfoBean.getQIANMIN());
            editor.putString(OKUserInfoBean.KEY_SEX, mUserInfoBean.getSEX());
            editor.putString(OKUserInfoBean.KEY_BIRTH_DATE, mUserInfoBean.getBIRTH_DATE());
            editor.putInt(OKUserInfoBean.KEY_AGE, mUserInfoBean.getAGE());
            editor.putString(OKUserInfoBean.KEY_RE_DATE, mUserInfoBean.getRE_DATE());
            editor.putInt(OKUserInfoBean.KEY_SHOUCHAN, mUserInfoBean.getSHOUCHAN());
            editor.putInt(OKUserInfoBean.KEY_GUANZHU, mUserInfoBean.getGUANZHU());
            editor.putInt(OKUserInfoBean.KEY_JIFENG, mUserInfoBean.getJIFENG());
            editor.putString(OKUserInfoBean.KEY_EDIT_DATE, mUserInfoBean.getEDIT_DATE());

            editor.putBoolean("STATE", true);
            editor.putBoolean("STATE_CHANGE", true);

            editor.commit();

            // 登录环信账号
            Bundle mBundle = new Bundle();
            mBundle.putString(OKUserInfoBean.KEY_USERNAME, imName);
            mBundle.putString(OKUserInfoBean.KEY_PASSWORD, imPass);
            sendUserBroadcast(ACTION_MAIN_SERVICE_LOGIN_IM, mBundle);

            showSnackBar(but, "登录成功", "");

            finish();
        } else {
            showSnackBar(but, "登录失败,请检查用户名和密码以及网络设置!", "");
        }
    }
}
