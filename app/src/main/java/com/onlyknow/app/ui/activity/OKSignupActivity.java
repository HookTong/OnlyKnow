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
import com.onlyknow.app.api.OKSigNupApi;
import com.onlyknow.app.database.bean.OKSignupResultBean;
import com.onlyknow.app.database.bean.OKUserInfoBean;
import com.onlyknow.app.ui.OKBaseActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OKSigNupActivity extends OKBaseActivity implements OKSigNupApi.onCallBack {
    private AppCompatButton buttonSigNup;
    private EditText editTextUserName, editTextNickName, editTextPhone, editTextEmail, editTextPassword;
    private RadioGroup sexRg;
    private RadioButton nanRb, nvRb;
    private CheckBox box;
    private TextView textViewXieYi;
    private String strSex = "NAN";
    private TextView textView;

    private OKSigNupApi mOKSigNupApi;

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

        if (mOKSigNupApi != null) {
            mOKSigNupApi.cancelTask();
        }
    }

    private void findView() {
        buttonSigNup = (AppCompatButton) findViewById(R.id.register_btn_signup);
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
        buttonSigNup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String errorMsg = legitimateTesting();
                if (TextUtils.isEmpty(errorMsg)) {
                    Date now = new Date();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd/HH/mm");
                    String date = dateFormat.format(now);

                    String name = editTextUserName.getText().toString();
                    String pass = editTextPassword.getText().toString();
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("username", name);
                    params.put("nickname", editTextNickName.getText().toString());
                    params.put("password", pass);
                    params.put("phone", editTextPhone.getText().toString());
                    params.put("email", editTextEmail.getText().toString());
                    params.put("sexRg", strSex);
                    params.put("age", "0");
                    params.put("redate", date);

                    if (mOKSigNupApi != null) {
                        mOKSigNupApi.cancelTask();
                    }
                    mOKSigNupApi = new OKSigNupApi(OKSigNupActivity.this);
                    mOKSigNupApi.requestSigNup(params, name, pass, OKSigNupActivity.this);

                    showProgressDialog("正在注册账号...");
                } else {
                    showSnackBar(v, "注册信息错误", "ErrorMsg :" + errorMsg);
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
                    buttonSigNup.setEnabled(true);
                } else {
                    buttonSigNup.setEnabled(false);
                }
            }
        });

        textView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                OKSigNupActivity.this.finish();
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

    @Override
    public void sigNupApiComplete(OKSignupResultBean mSignupResultBean, String imName, String imPass) {
        closeProgressDialog();
        if (mSignupResultBean != null) {
            if (mSignupResultBean.IS_SIGNUP()) {
                // 向环信注册账号
                Bundle mBundle = new Bundle();
                mBundle.putString(OKUserInfoBean.KEY_USERNAME, imName);
                mBundle.putString(OKUserInfoBean.KEY_PASSWORD, imPass);
                sendUserBroadcast(ACTION_MAIN_SERVICE_CREATE_ACCOUNT_IM, mBundle);

                showSnackBar(buttonSigNup, "注册成功", "");

                finish();
            } else {
                showSnackBar(buttonSigNup, "注册失败," + mSignupResultBean.getERROR_INFO() + "已存在!", "");
            }
        } else {
            showSnackBar(buttonSigNup, "注册失败,服务器错误!", "");
        }
    }
}
