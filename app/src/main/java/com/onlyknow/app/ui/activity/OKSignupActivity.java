package com.onlyknow.app.ui.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.onlyknow.app.R;
import com.onlyknow.app.api.OKBusinessApi;
import com.onlyknow.app.database.bean.OKSignupResultBean;
import com.onlyknow.app.database.bean.OKUserInfoBean;
import com.onlyknow.app.ui.OKBaseActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OKSignupActivity extends OKBaseActivity {
    private AppCompatButton buttonSignup;
    private EditText editTextUserName, editTextNickName, editTextPhone, editTextEmail, editTextPassword;
    private RadioGroup sexRg;
    private RadioButton nanRb, nvRb;
    private CheckBox box;
    private TextView textViewXieYi;
    private String strSex = "NAN";
    private TextView textView;

    private SignupTask mSignupTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ok_activity_registered);
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

        if (mSignupTask != null && mSignupTask.getStatus() == AsyncTask.Status.RUNNING) {
            mSignupTask.cancel(true);
        }
    }

    private void findView() {
        buttonSignup = (AppCompatButton) findViewById(R.id.register_btn_signup);
        editTextUserName = (EditText) findViewById(R.id.register_input_name);
        editTextNickName = (EditText) findViewById(R.id.register_input_nickname);
        editTextPhone = (EditText) findViewById(R.id.register_input_phone);
        editTextEmail = (EditText) findViewById(R.id.register_input_email);
        editTextPassword = (EditText) findViewById(R.id.register_input_password);
        sexRg = (RadioGroup) findViewById(R.id.register_RG_sex);
        nanRb = (RadioButton) findViewById(R.id.register_RB_male);
        nvRb = (RadioButton) findViewById(R.id.register_RB_female);
        box = (CheckBox) findViewById(R.id.register_checkbox_signup);
        textViewXieYi = (TextView) findViewById(R.id.register_text_xieyi);
        textView = (TextView) findViewById(R.id.register_link_login);

        nanRb.setChecked(true);
        nvRb.setChecked(false);
    }

    private void init() {
        buttonSignup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String errorMsg = legitimateTesting();
                if (TextUtils.isEmpty(errorMsg)) {
                    Date now = new Date();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd/HH/mm");
                    String date = dateFormat.format(now);

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("username", editTextUserName.getText().toString());
                    params.put("nickname", editTextNickName.getText().toString());
                    params.put("password", editTextPassword.getText().toString());
                    params.put("phone", editTextPhone.getText().toString());
                    params.put("email", editTextEmail.getText().toString());
                    params.put("sexRg", strSex);
                    params.put("age", "0");
                    params.put("redate", date);

                    if (mSignupTask != null && mSignupTask.getStatus() == AsyncTask.Status.RUNNING) {
                        mSignupTask.cancel(true);
                    }
                    mSignupTask = new SignupTask(editTextUserName.getText().toString(), editTextPassword.getText().toString());
                    mSignupTask.executeOnExecutor(exec, params);

                    showProgressDialog("正在注册账号!请稍后...");
                } else {
                    showSnackbar(v, "注册信息错误", "ErrorMsg :" + errorMsg);
                }
            }
        });

        textViewXieYi.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("TYPE", "AGREEMENT_VERSION_CHECK");
                bundle.putString("VALUE", "");
                startUserActivity(bundle, OKContentActivity.class);
            }
        });

        box.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    buttonSignup.setEnabled(true);
                } else {
                    buttonSignup.setEnabled(false);
                }
            }
        });

        textView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                OKSignupActivity.this.finish();
            }
        });

        sexRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == nanRb.getId()) {
                    strSex = "NAN";
                } else if (checkedId == nvRb.getId()) {
                    strSex = "NV";
                }
            }
        });
    }

    private String legitimateTesting() {
        String errorMsg = "";

        if (TextUtils.isEmpty(editTextUserName.getText().toString())) {
            errorMsg = "用户名不能为空";
            return errorMsg;
        }

        if (editTextUserName.getText().toString().length() < 9) {
            errorMsg = "用户名不能小于9个字符";
            return errorMsg;
        }

        if (editTextUserName.getText().toString().length() > 12) {
            errorMsg = "用户名不能超过12个字符";
            return errorMsg;
        }

        if (TextUtils.isEmpty(editTextNickName.getText().toString())) {
            errorMsg = "昵称不能为空";
            return errorMsg;
        }

        if (editTextNickName.getText().toString().length() > 12) {
            errorMsg = "昵称不能超过12个字符";
            return errorMsg;
        }

        if (TextUtils.isEmpty(editTextPhone.getText().toString())) {
            errorMsg = "Phone不能为空";
            return errorMsg;
        }

        if (editTextPhone.getText().toString().length() > 11) {
            errorMsg = "Phone不能超过11个字符";
            return errorMsg;
        }

        if (TextUtils.isEmpty(editTextEmail.getText().toString())) {
            errorMsg = "Email不能为空";
            return errorMsg;
        }

        if (TextUtils.isEmpty(editTextPassword.getText().toString())) {
            errorMsg = "密码不能为空";
            return errorMsg;
        }

        if (editTextPassword.getText().toString().length() > 12) {
            errorMsg = "密码不能超过12个字符";
            return errorMsg;
        }

        if (TextUtils.isEmpty(strSex)) {
            errorMsg = "性别不能为空";
            return errorMsg;
        }

        return errorMsg;
    }

    private class SignupTask extends AsyncTask<Map<String, String>, Void, OKSignupResultBean> {
        private String ImUserName, ImPassWord;

        public SignupTask(String username, String password) {
            this.ImUserName = username;
            this.ImPassWord = password;
        }

        @Override
        protected OKSignupResultBean doInBackground(Map<String, String>... params) {
            if (isCancelled()) {
                return null;
            }
            return new OKBusinessApi().registerUser(params[0]);
        }

        @Override
        protected void onPostExecute(OKSignupResultBean mSignupResultBean) {
            super.onPostExecute(mSignupResultBean);
            if (isCancelled()) {
                return;
            }

            closeProgressDialog();

            if (mSignupResultBean != null) {
                if (mSignupResultBean.IS_SIGNUP()) {
                    // 向环信注册账号
                    Bundle mBundle = new Bundle();
                    mBundle.putString(OKUserInfoBean.KEY_USERNAME, ImUserName);
                    mBundle.putString(OKUserInfoBean.KEY_PASSWORD, ImPassWord);
                    sendUserBroadcast(ACTION_MAIN_SERVICE_CREATE_ACCOUNT_IM, mBundle);

                    showSnackbar(buttonSignup, "注册成功", "");

                    finish();
                } else {
                    showSnackbar(buttonSignup, "注册失败," + mSignupResultBean.getERROR_INFO() + "已存在!", "");
                }
            } else {
                showSnackbar(buttonSignup, "注册失败,服务器错误!", "");
            }
        }
    }
}
