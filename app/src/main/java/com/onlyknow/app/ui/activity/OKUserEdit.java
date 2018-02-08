package com.onlyknow.app.ui.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.onlyknow.app.OKConstant;
import com.onlyknow.app.R;
import com.onlyknow.app.api.OKBusinessApi;
import com.onlyknow.app.database.bean.OKUserInfoBean;
import com.onlyknow.app.ui.OKBaseActivity;
import com.onlyknow.app.ui.view.OKCircleImageView;
import com.onlyknow.app.utils.OKBase64Util;
import com.onlyknow.app.utils.OKLogUtil;
import com.onlyknow.app.utils.OKSDCardUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OKUserEdit extends OKBaseActivity {
    private AppCompatButton butTiJiao;

    private EditText edit_Name, edit_Phone, edit_Email, edit_Tag, edit_Nian, edit_Yue, edit_Ri;
    private OKCircleImageView ImageViewTouXian;
    private RadioGroup RgSex;
    private RadioButton RbNan, RbNv;

    private TextView textViewQrCode;

    private String USERNAME, NICKNAME, PHONE, EMAIL, QIANMIN, SEX, BIRTH_DATE;

    private String XG_NICKNAME = "", XG_PHONE = "", XG_EMAIL = "", XG_QIANMIN = "", XG_BIRTHDATE = "", XG_SEX = "";

    private Uri uriPath;
    private String filePath;

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

        GlideRoundApi(ImageViewTouXian, url, R.drawable.touxian_placeholder_hd, R.drawable.touxian_placeholder_hd);

        edit_Name.setText(NICKNAME);
        edit_Phone.setText(PHONE);
        edit_Email.setText(EMAIL);

        if (!TextUtils.isEmpty(QIANMIN)) {
            edit_Tag.setText(QIANMIN);
        }

        if (!TextUtils.isEmpty(BIRTH_DATE) && !BIRTH_DATE.equals("NULL")) {
            String[] items = BIRTH_DATE.split("/");
            edit_Nian.setText(items[0]);
            edit_Yue.setText(items[1]);
            edit_Ri.setText(items[2]);
        }

        if (!TextUtils.isEmpty(SEX) && SEX.equals("NAN")) {
            RbNan.setChecked(true);
            RbNv.setChecked(false);
        } else if (!TextUtils.isEmpty(SEX) && SEX.equals("NV")) {
            RbNan.setChecked(false);
            RbNv.setChecked(true);
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
            // 从相册里面取相片的返回结果
            case 1:
                if (resultCode == RESULT_OK) {
                    uriPath = data.getData();
                    if (uriPath != null) {
                        String fp = OKSDCardUtil.getFilePathByImageUri(OKUserEdit.this.getApplicationContext(), uriPath);
                        String gs = fp.substring(fp.lastIndexOf(".") + 1, fp.length());
                        if (gs.equalsIgnoreCase("jpg") || gs.equalsIgnoreCase("png")) {
                            cropPhoto(uriPath);// 裁剪图片
                        } else {
                            uriPath = null;
                            showSnackbar(butTiJiao, "您不能上传动图作为头像", "");
                        }
                    }
                }

                break;
            // 相机拍照后的返回结果
            case 2:
                if (resultCode == RESULT_OK) {
                    File temp = new File(OKConstant.IMAGE_PATH + "camera.jpg");
                    uriPath = Uri.fromFile(temp);
                    cropPhoto(uriPath);// 裁剪图片
                }

                break;
            // 调用系统裁剪图片后
            case 3:
                if (resultCode == RESULT_OK && uriPath != null) {
                    filePath = OKSDCardUtil.getFilePathByImageUri(OKUserEdit.this.getApplicationContext(), uriPath);
                    if (filePath != null) {
                        mUserEditTask_HP = new UserEditTask("UpdateHeadPortrait");
                        Map<String, String> params = new HashMap<>();
                        params.put("username", USERNAME);
                        params.put("baseimag", filePath);
                        params.put("type", "TOUXIAN");
                        mUserEditTask_HP.executeOnExecutor(exec, params);
                        showProgressDialog("正在上传头像!请稍后...");
                    } else {
                        showSnackbar(butTiJiao, "文件错误", "");
                    }
                } else {
                    uriPath = null;
                }
                break;
            default:
                break;
        }
    }

    public void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        // 找到指定URI对应的资源图片
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 512);
        intent.putExtra("outputY", 512);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", false);
        // 进入系统裁剪图片的界面
        startActivityForResult(intent, 3);
    }

    private void init() {
        setSupportActionBar(mToolbar);

        butTiJiao.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                XG_NICKNAME = NICKNAME;
                XG_PHONE = PHONE;
                XG_EMAIL = EMAIL;
                XG_QIANMIN = QIANMIN;
                XG_BIRTHDATE = BIRTH_DATE;
                if (!TextUtils.isEmpty(edit_Name.getText().toString()) && !edit_Name.getText().toString().equals(NICKNAME)) {
                    XG_NICKNAME = edit_Name.getText().toString();
                }
                if (!TextUtils.isEmpty(edit_Phone.getText().toString()) && !edit_Phone.getText().toString().equals(PHONE)) {
                    XG_PHONE = edit_Phone.getText().toString();
                }
                if (!TextUtils.isEmpty(edit_Email.getText().toString()) && !edit_Email.getText().toString().equals(EMAIL)) {
                    XG_EMAIL = edit_Email.getText().toString();
                }
                if (!TextUtils.isEmpty(edit_Tag.getText().toString()) && !edit_Tag.getText().toString().equals(QIANMIN)) {
                    XG_QIANMIN = edit_Tag.getText().toString();
                }
                if (!TextUtils.isEmpty(edit_Nian.getText().toString())
                        && !TextUtils.isEmpty(edit_Yue.getText().toString())
                        && !TextUtils.isEmpty(edit_Ri.getText().toString())) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
                    boolean isbirth = Integer.parseInt(dateFormat.format(new Date())) > Integer.parseInt(edit_Nian.getText().toString());
                    if (isbirth) {
                        XG_BIRTHDATE = edit_Nian.getText().toString() + "/" + edit_Yue.getText().toString()
                                + "/" + edit_Ri.getText().toString();
                    } else {
                        showSnackbar(butTiJiao, "生日不能大于当前年份", "");
                        return;
                    }
                }
                if (TextUtils.isEmpty(XG_SEX)) {
                    XG_SEX = SEX;
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
                showProgressDialog("正在修改您的资料!请稍后...");

            }
        });

        ImageViewTouXian.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder DialogMenu = new AlertDialog.Builder(OKUserEdit.this);
                final View content_view = LayoutInflater.from(OKUserEdit.this).inflate(R.layout.ok_dialog_choose_image, null);
                final LinearLayout linearLayoutXiangChe = (LinearLayout) content_view
                        .findViewById(R.id.ddalog_choose_layouta_xiangche);
                final LinearLayout linearLayoutXiangJi = (LinearLayout) content_view
                        .findViewById(R.id.ddalog_choose_layouta_xiangji);
                DialogMenu.setView(content_view);

                linearLayoutXiangChe.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // 返回被选中项的URI
                        Intent mIntent = new Intent(Intent.ACTION_PICK, null);
                        // 得到所有图片的URI
                        mIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(mIntent, 1);
                    }
                });

                linearLayoutXiangJi.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        try {
                            Intent mIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            mIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(OKConstant.IMAGE_PATH, "camera.jpg")));
                            startActivityForResult(mIntent, 2);
                        } catch (Exception e) {
                            showSnackbar(v, "相机无法启动，请先开启相机权限", "");
                        }
                    }
                });

                DialogMenu.show();
            }
        });

        mToolbarLogout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
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

        textViewQrCode.setOnClickListener(new OnClickListener() {

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

        RgSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == RbNan.getId()) {
                    XG_SEX = "NAN";
                } else if (checkedId == RbNv.getId()) {
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
        mToolbarLogout.setVisibility(View.VISIBLE);
        mToolbarBack.setVisibility(View.VISIBLE);
        mToolbarTitle.setVisibility(View.VISIBLE);

        mToolbarTitle.setText("用户信息编辑");

        butTiJiao = (AppCompatButton) findViewById(R.id.UserEdit_btn_TiJiao);
        edit_Name = (EditText) findViewById(R.id.UserEdit_input_name);
        edit_Phone = (EditText) findViewById(R.id.UserEdit_input_phone);
        edit_Email = (EditText) findViewById(R.id.UserEdit_input_email);
        edit_Tag = (EditText) findViewById(R.id.UserEdit_input_tag);
        edit_Nian = (EditText) findViewById(R.id.UserEdit_input_nian);
        edit_Yue = (EditText) findViewById(R.id.UserEdit_input_yue);
        edit_Ri = (EditText) findViewById(R.id.UserEdit_input_ri);
        RgSex = (RadioGroup) findViewById(R.id.UserEdit_RG_sex);
        RbNan = (RadioButton) findViewById(R.id.UserEdit_RB_male);
        RbNv = (RadioButton) findViewById(R.id.UserEdit_RB_female);
        ImageViewTouXian = (OKCircleImageView) findViewById(R.id.UserEdit_TouXian_Imag);
        textViewQrCode = (TextView) findViewById(R.id.UserEdit_link_qrcode);
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
                    GlideRoundApi(ImageViewTouXian, filePath, R.drawable.touxian_placeholder_hd, R.drawable.touxian_placeholder_hd);
                }

                showSnackbar(butTiJiao, "修改成功", "");
            } else {
                showSnackbar(butTiJiao, "修改失败", "ErrorCode :" + OKConstant.SERVICE_ERROR);
            }

            closeProgressDialog();
        }
    }
}
