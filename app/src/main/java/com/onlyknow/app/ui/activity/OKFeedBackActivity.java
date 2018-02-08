package com.onlyknow.app.ui.activity;

import android.app.AlertDialog;
import android.content.Intent;
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

import com.onlyknow.app.OKConstant;
import com.onlyknow.app.R;
import com.onlyknow.app.api.OKBusinessApi;
import com.onlyknow.app.database.bean.OKUserInfoBean;
import com.onlyknow.app.ui.OKBaseActivity;
import com.onlyknow.app.ui.view.OKSEImageView;
import com.onlyknow.app.utils.OKBase64Util;
import com.onlyknow.app.utils.OKDeviceInfoUtil;
import com.onlyknow.app.utils.OKSDCardUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class OKFeedBackActivity extends OKBaseActivity {
    private AppCompatButton appCompatButtonSend;
    private OKSEImageView imageViewAddTuPian, imageViewClear;
    private EditText editTextNeiRon;

    private Uri uriPath;
    private String filePath = "";

    private FeedBackTask mFeedBackTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ok_activity_feedback);
        initUserInfoSharedPreferences();
        initSystemBar(this);
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
        if (mFeedBackTask != null && mFeedBackTask.getStatus() == AsyncTask.Status.RUNNING) {
            mFeedBackTask.cancel(true);
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
                        String fp = OKSDCardUtil.getFilePathByImageUri(OKFeedBackActivity.this.getApplicationContext(), uriPath);
                        String gs = fp.substring(fp.lastIndexOf(".") + 1, fp.length());
                        if (gs.equalsIgnoreCase("jpg") || gs.equalsIgnoreCase("png")) {
                            cropPhoto(uriPath);// 裁剪图片
                        } else {
                            uriPath = null;
                            showSnackbar(editTextNeiRon, "您不能上传动图", "");
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
                    filePath = OKSDCardUtil.getFilePathByImageUri(OKFeedBackActivity.this.getApplicationContext(), uriPath);
                    GlideApi(imageViewAddTuPian, filePath, R.drawable.add_image_black, R.drawable.add_image_black);
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
        intent.putExtra("outputX", 1024);
        intent.putExtra("outputY", 768);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", false);
        // 进入系统裁剪图片的界面
        startActivityForResult(intent, 3);
    }

    private void init() {
        setSupportActionBar(mToolbar);

        imageViewAddTuPian.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder DialogMenu = new AlertDialog.Builder(OKFeedBackActivity.this);
                final View content_view = LayoutInflater.from(OKFeedBackActivity.this).inflate(R.layout.ok_dialog_choose_image,
                        null);
                final LinearLayout linearLayoutXiangChe = (LinearLayout) content_view
                        .findViewById(R.id.ddalog_choose_layouta_xiangche);
                final LinearLayout linearLayoutXiangJi = (LinearLayout) content_view
                        .findViewById(R.id.ddalog_choose_layouta_xiangji);
                DialogMenu.setView(content_view);

                linearLayoutXiangChe.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent mIntent = new Intent(Intent.ACTION_PICK, null);
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

        appCompatButtonSend.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (USER_INFO_SP.getBoolean("STATE", false)) {
                    if (editTextNeiRon.getText().toString().length() >= 100) {

                        Map<String, String> map = new HashMap<String, String>();
                        map.put("username", USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, "Anonymous"));
                        map.put("equipment", new OKDeviceInfoUtil(OKFeedBackActivity.this).getIMIE());
                        map.put("message", editTextNeiRon.getText().toString());
                        map.put("baseimag", filePath);
                        map.put("date", OKConstant.getNowDate());

                        mFeedBackTask = new FeedBackTask();
                        mFeedBackTask.executeOnExecutor(exec, map);

                        showProgressDialog("正在提交意见!请稍后...");
                    } else {
                        showSnackbar(v, "反馈意见必须大于100字符", "");
                    }
                } else {
                    startUserActivity(null, OKLoginActivity.class);
                }

            }
        });

        imageViewClear.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                filePath = "";
                GlideApi(imageViewAddTuPian, R.drawable.add_image_black, R.drawable.add_image_black, R.drawable.add_image_black);
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

        mToolbarTitle.setText("意见反馈");

        appCompatButtonSend = (AppCompatButton) findViewById(R.id.Feedback_TiJiaoBtn);
        imageViewAddTuPian = (OKSEImageView) findViewById(R.id.Feedback_input_imag);
        imageViewClear = (OKSEImageView) findViewById(R.id.Feedback_clear_imag);
        editTextNeiRon = (EditText) findViewById(R.id.Feedback_input_text);
    }

    private class FeedBackTask extends AsyncTask<Map<String, String>, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Map<String, String>... params) {
            if (isCancelled()) {
                return false;
            }

            Map<String, String> map = params[0];

            String filePath = map.get("baseimag");

            if (!TextUtils.isEmpty(filePath)) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPurgeable = true;
                options.inSampleSize = 1;
                Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
                map.put("baseimag", OKBase64Util.BitmapToBase64(bitmap));
            }
            return new OKBusinessApi().feedBack(map);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (isCancelled()) {
                return;
            }

            if (aBoolean) {
                showSnackbar(appCompatButtonSend, "反馈成功", "");
            } else {
                showSnackbar(appCompatButtonSend, "反馈失败,请检查网络", "");
            }
            closeProgressDialog();
        }
    }
}
