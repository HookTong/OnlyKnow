package com.onlyknow.app.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.onlyknow.app.OKConstant;
import com.onlyknow.app.R;
import com.onlyknow.app.api.OKServiceResult;
import com.onlyknow.app.api.user.OKManagerUserApi;
import com.onlyknow.app.db.bean.OKUserInfoBean;
import com.onlyknow.app.ui.OKBaseActivity;
import com.onlyknow.app.ui.view.OKCircleImageView;
import com.onlyknow.app.utils.OKDateUtil;
import com.onlyknow.app.utils.OKSDCardUtil;
import com.onlyknow.mediapicker.PickerActivity;
import com.onlyknow.mediapicker.PickerConfig;
import com.onlyknow.mediapicker.bean.MediaBean;
import com.yalantis.ucrop.UCrop;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OKUserEditActivity extends OKBaseActivity implements OKManagerUserApi.onCallBack {
    private AppCompatButton mButtonCommit;
    private EditText mEditName, mEditPhone, mEditEmail, mEditTag, mEditNian, mEditYue, mEditRi;
    private OKCircleImageView mImageViewTouXian;
    private RadioGroup mRgSex;
    private RadioButton mRbNan, mRbNv;
    private TextView mTextViewQrCode;

    private String USERNAME, NICKNAME, PHONE, EMAIL, USER_TAG, SEX, BIRTH_DATE;
    private String XG_NICKNAME = "", XG_PHONE = "", XG_EMAIL = "", XG_USER_TAG = "", XG_BIRTH_DATE = "", XG_SEX = "";
    private String mFilePath;

    private OKManagerUserApi okManagerUserApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ok_activity_user_edit);
        initUserBody();
        initStatusBar();
        findView();
        loadData();
        init();
    }

    private void loadData() {
        USERNAME = USER_BODY.getString(OKUserInfoBean.KEY_USERNAME, "");
        NICKNAME = USER_BODY.getString(OKUserInfoBean.KEY_NICKNAME, "");
        PHONE = USER_BODY.getString(OKUserInfoBean.KEY_PHONE, "");
        EMAIL = USER_BODY.getString(OKUserInfoBean.KEY_EMAIL, "");
        USER_TAG = USER_BODY.getString(OKUserInfoBean.KEY_TAG, "");
        SEX = USER_BODY.getString(OKUserInfoBean.KEY_SEX, "");
        BIRTH_DATE = USER_BODY.getString(OKUserInfoBean.KEY_BIRTH_DATE, "");

        String url = USER_BODY.getString(OKUserInfoBean.KEY_HEAD_PORTRAIT_URL, "");

        GlideRoundApi(mImageViewTouXian, url, R.drawable.touxian_placeholder_hd, R.drawable.touxian_placeholder_hd);

        mEditName.setText(NICKNAME);
        mEditPhone.setText(PHONE);
        mEditEmail.setText(EMAIL);

        if (!TextUtils.isEmpty(USER_TAG)) {
            mEditTag.setText(USER_TAG);
        }

        if (!TextUtils.isEmpty(BIRTH_DATE) && !BIRTH_DATE.equals("NULL")) {
            String[] items = BIRTH_DATE.split("/");
            mEditNian.setText(items[0]);
            mEditYue.setText(items[1]);
            mEditRi.setText(items[2]);
        }

        if (!TextUtils.isEmpty(SEX) && SEX.equals("NAN")) {
            mRbNan.setChecked(true);
            mRbNv.setChecked(false);
        } else if (!TextUtils.isEmpty(SEX) && SEX.equals("NV")) {
            mRbNan.setChecked(false);
            mRbNv.setChecked(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (okManagerUserApi != null) {
            okManagerUserApi.cancelTask();
        }
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mToolbar != null) {
            mToolbar.setTitle("");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case UCrop.REQUEST_CROP:
                if (resultCode == RESULT_OK) {
                    Uri resultUri = UCrop.getOutput(data);
                    if (resultUri == null) {
                        showSnackBar(mButtonCommit, "没有URI地址", "");
                        return;
                    }
                    mFilePath = OKSDCardUtil.getFilePathByImageUri(OKUserEditActivity.this, resultUri);
                    if (TextUtils.isEmpty(mFilePath)) {
                        showSnackBar(mButtonCommit, "文件路径错误", "");
                        return;
                    }

                    OKManagerUserApi.Params params = new OKManagerUserApi.Params();
                    params.setUsername(USERNAME);
                    params.setPassword(USER_BODY.getString(OKUserInfoBean.KEY_PASSWORD, ""));
                    params.setAvatarData(mFilePath);
                    params.setType(OKManagerUserApi.Params.TYPE_UPDATE_AVATAR);

                    showProgressDialog("正在上传头像...");

                    if (okManagerUserApi != null) {
                        okManagerUserApi.cancelTask();
                    }
                    okManagerUserApi = new OKManagerUserApi(this);
                    okManagerUserApi.requestManagerUser(params, this);

                }
                break;
            case UCrop.RESULT_ERROR:
                showSnackBar(mButtonCommit, "剪裁失败", "");
                break;
            case SELECT_MEDIA_REQUEST_CODE:
                if (resultCode == PickerConfig.RESULT_CODE) {
                    mSelectMediaBean = data.getParcelableArrayListExtra(PickerConfig.EXTRA_RESULT);
                    dealWith(mSelectMediaBean);
                }
            default:
                break;
        }
    }

    private void init() {
        mButtonCommit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                XG_NICKNAME = NICKNAME;
                XG_PHONE = PHONE;
                XG_EMAIL = EMAIL;
                XG_USER_TAG = USER_TAG;
                XG_BIRTH_DATE = BIRTH_DATE;
                if (!TextUtils.isEmpty(mEditName.getText().toString()) && !mEditName.getText().toString().equals(NICKNAME)) {
                    XG_NICKNAME = mEditName.getText().toString();
                }
                if (!TextUtils.isEmpty(mEditPhone.getText().toString()) && !mEditPhone.getText().toString().equals(PHONE)) {
                    XG_PHONE = mEditPhone.getText().toString();
                }
                if (!TextUtils.isEmpty(mEditEmail.getText().toString()) && !mEditEmail.getText().toString().equals(EMAIL)) {
                    XG_EMAIL = mEditEmail.getText().toString();
                }
                if (!TextUtils.isEmpty(mEditTag.getText().toString()) && !mEditTag.getText().toString().equals(USER_TAG)) {
                    XG_USER_TAG = mEditTag.getText().toString();
                }
                if (!TextUtils.isEmpty(mEditNian.getText().toString())
                        && !TextUtils.isEmpty(mEditYue.getText().toString())
                        && !TextUtils.isEmpty(mEditRi.getText().toString())) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
                    boolean isBirth = Integer.parseInt(dateFormat.format(new Date())) > Integer.parseInt(mEditNian.getText().toString());
                    if (isBirth) {
                        XG_BIRTH_DATE = mEditNian.getText().toString() + "/" + mEditYue.getText().toString()
                                + "/" + mEditRi.getText().toString();
                    } else {
                        showSnackBar(mButtonCommit, "生日不能大于当前年份", "");
                        return;
                    }
                }
                if (TextUtils.isEmpty(XG_SEX)) {
                    XG_SEX = SEX;
                }

                OKUserInfoBean bean = new OKUserInfoBean();
                bean.setUserNickname(XG_NICKNAME);
                bean.setUserPhone(XG_PHONE);
                bean.setUserEmail(XG_EMAIL);
                bean.setTag(XG_USER_TAG);
                bean.setSex(XG_SEX);
                bean.setBirthDate(OKDateUtil.dateByString(XG_BIRTH_DATE));

                OKManagerUserApi.Params params = new OKManagerUserApi.Params();
                params.setUsername(USERNAME);
                params.setPassword(USER_BODY.getString(OKUserInfoBean.KEY_PASSWORD, ""));
                params.setType(OKManagerUserApi.Params.TYPE_UPDATE_INFO);
                params.setEntity(bean);

                showProgressDialog("正在修改资料...");

                if (okManagerUserApi != null) {
                    okManagerUserApi.cancelTask();
                }
                okManagerUserApi = new OKManagerUserApi(OKUserEditActivity.this);
                okManagerUserApi.requestManagerUser(params, OKUserEditActivity.this);

            }
        });

        mImageViewTouXian.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OKUserEditActivity.this, PickerActivity.class);
                intent.putExtra(PickerConfig.SELECT_MODE, PickerConfig.PICKER_IMAGE);//default image and video (Optional)
                intent.putExtra(PickerConfig.MAX_SELECT_SIZE, 3145728L); //default 180MB (Optional)
                intent.putExtra(PickerConfig.MAX_SELECT_COUNT, 1);  //default 40 (Optional)
                intent.putExtra(PickerConfig.DEFAULT_SELECTED_LIST, mSelectMediaBean); // (Optional)
                startActivityForResult(intent, SELECT_MEDIA_REQUEST_CODE);
            }
        });

        mToolbarLogout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(OKUserEditActivity.this);
                dialog.setIcon(R.drawable.ic_launcher);
                dialog.setTitle("用户管理");
                dialog.setMessage("是否登出当前账号?退出之后将无法使用部分功能 !");
                dialog.setPositiveButton("登出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        USER_BODY.edit().putBoolean("STATE", false).commit();
                        USER_BODY.edit().putBoolean("STATE_CHANGE", true).commit();
                        sendUserBroadcast(ACTION_MAIN_SERVICE_LOGOUT_IM, null);
                        finish();
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });
                dialog.show();
            }
        });

        mTextViewQrCode.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(OKUserInfoBean.KEY_USERNAME, USER_BODY.getString(OKUserInfoBean.KEY_USERNAME, ""));
                bundle.putString(OKUserInfoBean.KEY_NICKNAME, USER_BODY.getString(OKUserInfoBean.KEY_NICKNAME, ""));
                bundle.putString(OKUserInfoBean.KEY_HEAD_PORTRAIT_URL, USER_BODY.getString(OKUserInfoBean.KEY_HEAD_PORTRAIT_URL, ""));
                bundle.putString(OKUserInfoBean.KEY_TAG, USER_BODY.getString(OKUserInfoBean.KEY_TAG, "这个人很懒 , 什么都没有留下!"));
                startUserActivity(bundle, OKMeQrCodeActivity.class);
            }
        });

        mRgSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == mRbNan.getId()) {
                    XG_SEX = "NAN";
                } else if (checkedId == mRbNv.getId()) {
                    XG_SEX = "NV";
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
        super.findCommonToolbarView();
        setSupportActionBar(mToolbar);
        mToolbarLogout.setVisibility(View.VISIBLE);
        mToolbarBack.setVisibility(View.VISIBLE);
        mToolbarTitle.setVisibility(View.VISIBLE);
        mToolbarTitle.setText("用户信息编辑");
        mButtonCommit = (AppCompatButton) findViewById(R.id.UserEdit_btn_TiJiao);
        mEditName = (EditText) findViewById(R.id.UserEdit_input_name);
        mEditPhone = (EditText) findViewById(R.id.UserEdit_input_phone);
        mEditEmail = (EditText) findViewById(R.id.UserEdit_input_email);
        mEditTag = (EditText) findViewById(R.id.UserEdit_input_tag);
        mEditNian = (EditText) findViewById(R.id.UserEdit_input_nian);
        mEditYue = (EditText) findViewById(R.id.UserEdit_input_yue);
        mEditRi = (EditText) findViewById(R.id.UserEdit_input_ri);
        mRgSex = (RadioGroup) findViewById(R.id.UserEdit_RG_sex);
        mRbNan = (RadioButton) findViewById(R.id.UserEdit_RB_male);
        mRbNv = (RadioButton) findViewById(R.id.UserEdit_RB_female);
        mImageViewTouXian = (OKCircleImageView) findViewById(R.id.UserEdit_TouXian_Imag);
        mTextViewQrCode = (TextView) findViewById(R.id.UserEdit_link_qrcode);
    }

    private ArrayList<MediaBean> mSelectMediaBean;
    private final int SELECT_MEDIA_REQUEST_CODE = 200;

    private void dealWith(List<MediaBean> imageItems) {
        if (imageItems == null || imageItems.size() == 0) {
            showSnackBar(mToolbarAddImage, "未获选择图片", "");
            return;
        }
        String fp = imageItems.get(0).path;
        String gs = fp.substring(fp.lastIndexOf(".") + 1, fp.length());
        if (gs.equalsIgnoreCase("gif")) {
            showSnackBar(mButtonCommit, "您不能选择动图作为头像", "");
            return;
        }
        startUCrop(imageItems.get(0).path, 1, 1);
    }

    @Override
    public void managerUserComplete(OKServiceResult<Object> result, String type, int pos) {
        if (OKManagerUserApi.Params.TYPE_UPDATE_AVATAR.equals(type)) {

            if (result != null && result.isSuccess()) {

                GlideRoundApi(mImageViewTouXian, mFilePath, R.drawable.touxian_placeholder_hd, R.drawable.touxian_placeholder_hd);

                showSnackBar(mButtonCommit, "修改成功", "");
            } else {
                showSnackBar(mButtonCommit, "修改失败", "ErrorCode :" + OKConstant.SERVICE_ERROR);
            }

        } else if (OKManagerUserApi.Params.TYPE_UPDATE_INFO.equals(type)) {

            if (result != null && result.isSuccess()) {
                SharedPreferences.Editor editor = USER_BODY.edit();
                editor.putString(OKUserInfoBean.KEY_NICKNAME, XG_NICKNAME);
                editor.putString(OKUserInfoBean.KEY_PHONE, XG_PHONE);
                editor.putString(OKUserInfoBean.KEY_EMAIL, XG_EMAIL);
                editor.putString(OKUserInfoBean.KEY_TAG, XG_USER_TAG);
                editor.putString(OKUserInfoBean.KEY_SEX, XG_SEX);
                editor.putString(OKUserInfoBean.KEY_BIRTH_DATE, XG_BIRTH_DATE);
                int age = 0;
                if (!TextUtils.isEmpty(XG_BIRTH_DATE)) {
                    String[] items = XG_BIRTH_DATE.split("/");
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
                    age = Integer.parseInt(dateFormat.format(new Date())) - Integer.parseInt(items[0]);
                }
                editor.putInt(OKUserInfoBean.KEY_AGE, age);
                editor.commit();

                loadData();

                showSnackBar(mButtonCommit, "修改成功", "");
            } else {
                showSnackBar(mButtonCommit, "修改失败", "ErrorCode :" + OKConstant.SERVICE_ERROR);
            }

        }

        closeProgressDialog();
    }
}
