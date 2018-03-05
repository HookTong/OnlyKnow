package com.onlyknow.app.ui.activity;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.onlyknow.app.R;
import com.onlyknow.app.net.OKBusinessNet;
import com.onlyknow.app.database.bean.OKUserInfoBean;
import com.onlyknow.app.ui.OKBaseActivity;

import java.util.HashMap;
import java.util.Map;

public class OKLoginActivity extends OKBaseActivity {
    private AppCompatButton but;
    private EditText editTextUserName, editTextPssword;
    private TextView textView;

    private LoginTask mLoginTask;

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
        if (mLoginTask != null && mLoginTask.getStatus() == AsyncTask.Status.RUNNING) {
            mLoginTask.cancel(true);
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
                if (!editTextUserName.getText().toString().equals("") && !editTextPssword.getText().toString().equals("")) {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("username", editTextUserName.getText().toString());
                    params.put("password", editTextPssword.getText().toString());

                    if (mLoginTask != null && mLoginTask.getStatus() == AsyncTask.Status.RUNNING) {
                        mLoginTask.cancel(true);
                    }
                    mLoginTask = new LoginTask(editTextUserName.getText().toString(), editTextPssword.getText().toString());
                    mLoginTask.executeOnExecutor(exec, params);

                    showProgressDialog("正在登录...");
                } else {
                    showSnackbar(v, "用户名和密码不能为空!", "");
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

    private class LoginTask extends AsyncTask<Map<String, String>, Void, OKUserInfoBean> {
        String ImUserName, ImPassWord;

        public LoginTask(String username, String password) {
            this.ImUserName = username;
            this.ImPassWord = password;
        }

        @Override
        protected OKUserInfoBean doInBackground(Map<String, String>... params) {
            if (isCancelled()) {
                return null;
            }

            return new OKBusinessNet().login(params[0]);
        }

        @Override
        protected void onPostExecute(OKUserInfoBean mUserInfoBean) {
            super.onPostExecute(mUserInfoBean);
            if (isCancelled()) {
                return;
            }

            closeProgressDialog();

            if (mUserInfoBean != null) {
                SharedPreferences.Editor editor = USER_INFO_SP.edit();
                editor.putInt(OKUserInfoBean.KEY_USERID, mUserInfoBean.getUSERID());
                editor.putString(OKUserInfoBean.KEY_USERNAME, mUserInfoBean.getUSERNAME());
                editor.putString(OKUserInfoBean.KEY_PASSWORD, ImPassWord);
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
                mBundle.putString(OKUserInfoBean.KEY_USERNAME, ImUserName);
                mBundle.putString(OKUserInfoBean.KEY_PASSWORD, ImPassWord);
                sendUserBroadcast(ACTION_MAIN_SERVICE_LOGIN_IM, mBundle);

                showSnackbar(but, "登录成功", "");

                finish();
            } else {
                showSnackbar(but, "登录失败,请检查用户名和密码!", "");
            }
        }
    }
}
