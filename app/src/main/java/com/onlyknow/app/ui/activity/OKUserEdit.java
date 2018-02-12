package com.onlyknow.app.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.caimuhao.rxpicker.RxPicker;
import com.caimuhao.rxpicker.bean.ImageItem;
import com.caimuhao.rxpicker.utils.RxPickerImageLoader;
import com.onlyknow.app.GlideApp;
import com.onlyknow.app.OKConstant;
import com.onlyknow.app.R;
import com.onlyknow.app.api.OKBusinessApi;
import com.onlyknow.app.database.bean.OKUserInfoBean;
import com.onlyknow.app.ui.OKBaseActivity;
import com.onlyknow.app.ui.view.OKCircleImageView;
import com.onlyknow.app.utils.OKBase64Util;
import com.onlyknow.app.utils.OKLogUtil;
import com.onlyknow.app.utils.OKSDCardUtil;
import com.yalantis.ucrop.UCrop;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class OKUserEdit extends OKBaseActivity {
    private AppCompatButton mButtonCommit;
    private EditText mEditName, mEditPhone, mEditEmail, mEditTag, mEditNian, mEditYue, mEditRi;
    private OKCircleImageView mImageViewTouXian;
    private RadioGroup mRgSex;
    private RadioButton mRbNan, mRbNv;
    private TextView mTextViewQrCode;

    private String USERNAME, NICKNAME, PHONE, EMAIL, QIANMIN, SEX, BIRTH_DATE;
    private String XG_NICKNAME = "", XG_PHONE = "", XG_EMAIL = "", XG_QIANMIN = "", XG_BIRTHDATE = "", XG_SEX = "";
    private String mFilePath;

    private UserEditTask mUserEditTask_HP, mUserEditTask_INFO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ok_activity_user_edit);
        initUserInfoSharedPreferences();
        initSystemBar(this);
        findView();
        loadData();
        init();
    }

    private void loadData() {
        USERNAME = USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, "");
        NICKNAME = USER_INFO_SP.getString(OKUserInfoBean.KEY_NICKNAME, "");
        PHONE = USER_INFO_SP.getString(OKUserInfoBean.KEY_PHONE, "");
        EMAIL = USER_INFO_SP.getString(OKUserInfoBean.KEY_EMAIL, "");
        QIANMIN = USER_INFO_SP.getString(OKUserInfoBean.KEY_QIANMIN, "");
        SEX = USER_INFO_SP.getString(OKUserInfoBean.KEY_SEX, "");
        BIRTH_DATE = USER_INFO_SP.getString(OKUserInfoBean.KEY_BIRTH_DATE, "");

        String url = USER_INFO_SP.getString(OKUserInfoBean.KEY_HEADPORTRAIT_URL, "");

        GlideRoundApi(mImageViewTouXian, url, R.drawable.touxian_placeholder_hd, R.drawable.touxian_placeholder_hd);

        mEditName.setText(NICKNAME);
        mEditPhone.setText(PHONE);
        mEditEmail.setText(EMAIL);

        if (!TextUtils.isEmpty(QIANMIN)) {
            mEditTag.setText(QIANMIN);
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
        if (mUserEditTask_INFO != null && mUserEditTask_INFO.getStatus() == AsyncTask.Status.RUNNING) {
            mUserEditTask_INFO.cancel(true); // 如果线程已经在执行则取消执行
        }

        if (mUserEditTask_HP != null && mUserEditTask_HP.getStatus() == AsyncTask.Status.RUNNING) {
            mUserEditTask_HP.cancel(true); // 如果线程已经在执行则取消执行
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUserEditTask_INFO != null && mUserEditTask_INFO.getStatus() == AsyncTask.Status.RUNNING) {
            mUserEditTask_INFO.cancel(true);
        }
        if (mUserEditTask_HP != null && mUserEditTask_HP.getStatus() == AsyncTask.Status.RUNNING) {
            mUserEditTask_HP.cancel(true);
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
                        showSnackbar(mButtonCommit, "没有URI地址", "");
                        return;
                    }
                    mFilePath = OKSDCardUtil.getFilePathByImageUri(OKUserEdit.this, resultUri);
                    if (TextUtils.isEmpty(mFilePath)) {
                        showSnackbar(mButtonCommit, "文件路径错误", "");
                        return;
                    }
                    if (mUserEditTask_HP != null && mUserEditTask_HP.getStatus() == AsyncTask.Status.RUNNING) {
                        mUserEditTask_HP.cancel(true);
                    }
                    mUserEditTask_HP = new UserEditTask("UpdateHeadPortrait");
                    Map<String, String> params = new HashMap<>();
                    params.put("username", USERNAME);
                    params.put("baseimag", mFilePath);
                    params.put("type", "TOUXIAN");
                    mUserEditTask_HP.executeOnExecutor(exec, params);
                    showProgressDialog("正在上传头像...");
                }
                break;
            case UCrop.RESULT_ERROR:
                showSnackbar(mButtonCommit, "剪裁失败", "");
                break;
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
                XG_QIANMIN = QIANMIN;
                XG_BIRTHDATE = BIRTH_DATE;
                if (!TextUtils.isEmpty(mEditName.getText().toString()) && !mEditName.getText().toString().equals(NICKNAME)) {
                    XG_NICKNAME = mEditName.getText().toString();
                }
                if (!TextUtils.isEmpty(mEditPhone.getText().toString()) && !mEditPhone.getText().toString().equals(PHONE)) {
                    XG_PHONE = mEditPhone.getText().toString();
                }
                if (!TextUtils.isEmpty(mEditEmail.getText().toString()) && !mEditEmail.getText().toString().equals(EMAIL)) {
                    XG_EMAIL = mEditEmail.getText().toString();
                }
                if (!TextUtils.isEmpty(mEditTag.getText().toString()) && !mEditTag.getText().toString().equals(QIANMIN)) {
                    XG_QIANMIN = mEditTag.getText().toString();
                }
                if (!TextUtils.isEmpty(mEditNian.getText().toString())
                        && !TextUtils.isEmpty(mEditYue.getText().toString())
                        && !TextUtils.isEmpty(mEditRi.getText().toString())) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
                    boolean isBirth = Integer.parseInt(dateFormat.format(new Date())) > Integer.parseInt(mEditNian.getText().toString());
                    if (isBirth) {
                        XG_BIRTHDATE = mEditNian.getText().toString() + "/" + mEditYue.getText().toString()
                                + "/" + mEditRi.getText().toString();
                    } else {
                        showSnackbar(mButtonCommit, "生日不能大于当前年份", "");
                        return;
                    }
                }
                if (TextUtils.isEmpty(XG_SEX)) {
                    XG_SEX = SEX;
                }
                if (mUserEditTask_INFO != null && mUserEditTask_INFO.getStatus() == AsyncTask.Status.RUNNING) {
                    mUserEditTask_INFO.cancel(true);
                }
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", USERNAME);
                params.put("nickname", XG_NICKNAME);
                params.put("phone", XG_PHONE);
                params.put("email", XG_EMAIL);
                params.put("qianmin", XG_QIANMIN);
                params.put("birth", XG_BIRTHDATE);
                params.put("sex", XG_SEX);
                mUserEditTask_INFO = new UserEditTask("UpdateEditInfo");
                mUserEditTask_INFO.executeOnExecutor(exec, params);
                showProgressDialog("正在修改资料...");
            }
        });

        mImageViewTouXian.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                RxPicker.init(new LoadImage());
                RxPicker.of().single(false).camera(true).limit(1, 1).start(OKUserEdit.this).subscribe(new ImageSelectResult());
            }
        });

        mToolbarLogout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog("用户管理", "是否退出当前账号?退出之后将无法使用部分功能!", "退出", "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        USER_INFO_SP.edit().putBoolean("STATE", false).commit();
                        USER_INFO_SP.edit().putBoolean("STATE_CHANGE", true).commit();
                        OKConstant.clearListCache(INTERFACE_NOTICE);
                        OKConstant.clearListCache(INTERFACE_DYNAMIC);
                        OKConstant.clearListCache(INTERFACE_ATTENTION);
                        OKConstant.clearListCache(INTERFACE_COLLECTION);
                        OKConstant.clearListCache(INTERFACE_CARD_AND_COMMENT);
                        sendUserBroadcast(ACTION_MAIN_SERVICE_LOGOUT_IM, null);
                        finish();
                    }
                });
            }
        });

        mTextViewQrCode.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(OKUserInfoBean.KEY_USERNAME, USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
                bundle.putString(OKUserInfoBean.KEY_NICKNAME, USER_INFO_SP.getString(OKUserInfoBean.KEY_NICKNAME, ""));
                bundle.putString(OKUserInfoBean.KEY_HEADPORTRAIT_URL, USER_INFO_SP.getString(OKUserInfoBean.KEY_HEADPORTRAIT_URL, ""));
                bundle.putString(OKUserInfoBean.KEY_QIANMIN, USER_INFO_SP.getString(OKUserInfoBean.KEY_QIANMIN, "这个人很懒 , 什么都没有留下!"));
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
        super.findCommonToolbarView(this);
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

    private class UserEditTask extends AsyncTask<Map<String, String>, Void, Boolean> {

        private String Type = "";

        public UserEditTask(String type) {
            this.Type = type;
        }

        @Override
        protected Boolean doInBackground(Map<String, String>... params) {
            if (isCancelled()) {
                return false;
            }

            if (this.Type.equals("UpdateEditInfo")) {

                return new OKBusinessApi().updateUserInfo(params[0]);

            } else if (this.Type.equals("UpdateHeadPortrait")) {

                Map<String, String> map = params[0];

                String filePath = map.get("baseimag");

                BitmapFactory.Options options = new BitmapFactory.Options();

                options.inPurgeable = true;

                options.inSampleSize = 1; // 表示不压缩

                Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

                map.put("baseimag", OKBase64Util.BitmapToBase64(bitmap));

                return new OKBusinessApi().updateHeadPortrait(map);
            } else {
                OKLogUtil.print("OKUserEdit 无效的执行类型");
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (isCancelled()) {
                return;
            }

            if (aBoolean) {
                if (this.Type.equals("UpdateEditInfo")) {
                    Editor editor = USER_INFO_SP.edit();
                    editor.putString(OKUserInfoBean.KEY_NICKNAME, XG_NICKNAME);
                    editor.putString(OKUserInfoBean.KEY_PHONE, XG_PHONE);
                    editor.putString(OKUserInfoBean.KEY_EMAIL, XG_EMAIL);
                    editor.putString(OKUserInfoBean.KEY_QIANMIN, XG_QIANMIN);
                    editor.putString(OKUserInfoBean.KEY_SEX, XG_SEX);
                    editor.putString(OKUserInfoBean.KEY_BIRTH_DATE, XG_BIRTHDATE);
                    int age = 0;
                    if (!TextUtils.isEmpty(XG_BIRTHDATE)) {
                        String[] items = XG_BIRTHDATE.split("/");
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
                        age = Integer.parseInt(dateFormat.format(new Date())) - Integer.parseInt(items[0]);
                    }
                    editor.putInt(OKUserInfoBean.KEY_AGE, age);
                    editor.commit();

                    loadData();
                } else if (this.Type.equals("UpdateHeadPortrait")) {
                    GlideRoundApi(mImageViewTouXian, mFilePath, R.drawable.touxian_placeholder_hd, R.drawable.touxian_placeholder_hd);
                }

                showSnackbar(mButtonCommit, "修改成功", "");
            } else {
                showSnackbar(mButtonCommit, "修改失败", "ErrorCode :" + OKConstant.SERVICE_ERROR);
            }

            closeProgressDialog();
        }
    }

    private class LoadImage implements RxPickerImageLoader {
        @Override
        public void display(ImageView imageView, String path, int width, int height) {
            GlideApp.with(imageView.getContext()).load(path).error(R.drawable.add_image_black).centerCrop().override(width, height).into(imageView);
        }
    }

    private class ImageSelectResult implements Consumer<List<ImageItem>> {
        @Override
        public void accept(@NonNull List<ImageItem> imageItems) throws Exception {
            if (imageItems == null || imageItems.size() == 0) {
                showSnackbar(mToolbarAddImage, "未获选择图片", "");
                return;
            }
            String fp = imageItems.get(0).getPath();
            String gs = fp.substring(fp.lastIndexOf(".") + 1, fp.length());
            if (gs.equalsIgnoreCase("gif")) {
                showSnackbar(mButtonCommit, "您不能选择动图作为头像", "");
                return;
            }
            startUCrop(imageItems.get(0).getPath(), 1, 1);
        }
    }
}
