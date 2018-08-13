package com.onlyknow.app.ui.activity;

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
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.onlyknow.app.R;
import com.onlyknow.app.api.OKServiceResult;
import com.onlyknow.app.api.user.OKManagerUserApi;
import com.onlyknow.app.db.bean.OKCarouselAdBean;
import com.onlyknow.app.db.bean.OKUserInfoBean;
import com.onlyknow.app.db.bean.OKWeatherBean;
import com.onlyknow.app.service.OKMainService;
import com.onlyknow.app.ui.OKBaseActivity;

import java.util.Date;

public class OKRegisteredActivity extends OKBaseActivity implements OKManagerUserApi.onCallBack,
        OKMainService.NoticeCallBack {
    private AppCompatButton buttonSigNup;
    private EditText editTextUserName, editTextNickName, editTextPhone, editTextEmail, editTextPassword;
    private RadioGroup sexRg;
    private RadioButton nanRb, nvRb;
    private CheckBox box;
    private TextView textViewXieYi;
    private String strSex = "NAN";
    private TextView textView;

    private OKManagerUserApi okManagerUserApi;

    private int noticeIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ok_activity_registered);

        noticeIndex = OKMainService.addNoticeCallBack(this);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        OKMainService.removeNoticeCallBack(noticeIndex);
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

                    imName = editTextUserName.getText().toString();
                    imPass = editTextPassword.getText().toString();

                    OKUserInfoBean userInfoBean = new OKUserInfoBean();
                    userInfoBean.setUserName(imName);
                    userInfoBean.setUserPassword(imPass);
                    userInfoBean.setUserNickname(editTextNickName.getText().toString());
                    userInfoBean.setUserPhone(editTextPhone.getText().toString());
                    userInfoBean.setUserEmail(editTextEmail.getText().toString());
                    userInfoBean.setSex(strSex);
                    userInfoBean.setAge(0);
                    userInfoBean.setReDate(new Date());

                    OKManagerUserApi.Params params = new OKManagerUserApi.Params();
                    params.setType(OKManagerUserApi.Params.TYPE_REGISTERED);
                    params.setEntity(userInfoBean);

                    showProgressDialog("正在注册账号...");

                    if (okManagerUserApi != null) {
                        okManagerUserApi.cancelTask();
                    }
                    okManagerUserApi = new OKManagerUserApi(OKRegisteredActivity.this);
                    okManagerUserApi.requestManagerUser(params, OKRegisteredActivity.this);

                } else {
                    showSnackBar(v, "注册信息错误", "ErrorMsg :" + errorMsg);
                }
            }
        });

        textViewXieYi.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(OKContentActivity.KEY_TYPE, OKContentActivity.TYPE_AGREEMENT);
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
                OKRegisteredActivity.this.finish();
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

    String imName, imPass;

    @Override
    public void managerUserComplete(OKServiceResult<Object> result, String type, int pos) {
        if (OKManagerUserApi.Params.TYPE_REGISTERED.equals(type)) {
            if (result == null) {
                closeProgressDialog();
                showSnackBar(buttonSigNup, "注册失败,服务器未返回数据!", "");
                return;
            }
            if (result.isSuccess()) {
                // 向环信注册账号
                Bundle mBundle = new Bundle();
                mBundle.putString(OKUserInfoBean.KEY_USERNAME, imName);
                mBundle.putString(OKUserInfoBean.KEY_PASSWORD, imPass);
                sendUserBroadcast(ACTION_MAIN_SERVICE_CREATE_ACCOUNT_IM, mBundle);

                showSnackBar(buttonSigNup, "注册成功,后台正在注册并登录IM聊天服务器.", "");

                // finish();
            } else {
                closeProgressDialog();
                showSnackBar(buttonSigNup, "注册失败," + result.getMsg(), "");
            }
        }
    }

    @Override
    public void onLocationChanged(AMapLocation location) {

    }

    @Override
    public void onImStatusChanged(boolean isOnline) {
        closeProgressDialog();

        showSnackBar(buttonSigNup, "IM聊天服务器登录" + (isOnline ? "成功" : "失败"), "");

        finish();
    }

    @Override
    public void onCarouselImageChanged(OKCarouselAdBean bean) {

    }

    @Override
    public void onWeatherChanged(OKWeatherBean bean) {

    }
}
