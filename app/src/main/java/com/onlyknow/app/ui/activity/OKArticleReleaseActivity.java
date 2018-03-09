package com.onlyknow.app.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.dmcbig.mediapicker.PickerActivity;
import com.dmcbig.mediapicker.PickerConfig;
import com.dmcbig.mediapicker.bean.MediaBean;
import com.google.gson.Gson;
import com.onlyknow.app.OKConstant;
import com.onlyknow.app.R;
import com.onlyknow.app.net.OKBusinessNet;
import com.onlyknow.app.database.bean.OKCardBase64ListBean;
import com.onlyknow.app.database.bean.OKCardBean;
import com.onlyknow.app.database.bean.OKUserInfoBean;
import com.onlyknow.app.ui.OKBaseActivity;
import com.onlyknow.app.ui.view.OKSEImageView;
import com.onlyknow.app.utils.OKLogUtil;
import com.onlyknow.app.utils.compress.OKCompressHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;

public class OKArticleReleaseActivity extends OKBaseActivity {
    @Bind(R.id.RELEASE_input_zhengwentupian1)
    OKSEImageView mAddImage1;
    @Bind(R.id.RELEASE_clear1_imag)
    OKSEImageView mClearImage1;

    @Bind(R.id.RELEASE_input_zhengwentupian2)
    OKSEImageView mAddImage2;
    @Bind(R.id.RELEASE_clear2_imag)
    OKSEImageView mClearImage2;

    @Bind(R.id.RELEASE_input_zhengwentupian3)
    OKSEImageView mAddImage3;
    @Bind(R.id.RELEASE_clear3_imag)
    OKSEImageView mClearImage3;

    @Bind(R.id.RELEASE_input_zhengwentupian4)
    OKSEImageView mAddImage4;
    @Bind(R.id.RELEASE_clear4_imag)
    OKSEImageView mClearImage4;

    @Bind(R.id.RELEASE_input_zhengwentupian5)
    OKSEImageView mAddImage5;
    @Bind(R.id.RELEASE_clear5_imag)
    OKSEImageView mClearImage5;

    private EditText mEditTitle, mEditTag, mEditLink, mEditContent;

    private OKCardBase64ListBean mOKCardBase64ListBean = new OKCardBase64ListBean();

    private ArticleTask mArticleTask;

    private final int TAG_UPLOAD = 1010;
    private final int TAG_NORMAL = 1011;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ok_activity_release);
        ButterKnife.bind(this);
        initUserInfoSharedPreferences();
        initArticleSharedPreferences();
        initSystemBar(this);
        findView();
        loadData();
        init();
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
        if (mArticleTask != null && mArticleTask.getStatus() == AsyncTask.Status.RUNNING) {
            mArticleTask.cancel(true);
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            backFinish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void init() {
        mToolbarSend.setTag(R.id.uploadButton, TAG_NORMAL);

        if (ARTICLE_SP.getBoolean("NEW_ARTICLE", true)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(OKArticleReleaseActivity.this);
            builder.setTitle("您必须要知道");
            builder.setMessage(getResources().getString(R.string.articleDialog));
            builder.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ARTICLE_SP.edit().putBoolean("NEW_ARTICLE", false).commit();
                }
            });
            AlertDialog dialog = builder.show();
            dialog.setCancelable(false);
        }

        mToolbarAddImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OKArticleReleaseActivity.this, PickerActivity.class);
                intent.putExtra(PickerConfig.SELECT_MODE, PickerConfig.PICKER_IMAGE_VIDEO);//default image and video (Optional)
                intent.putExtra(PickerConfig.MAX_SELECT_SIZE, 15728640L); //default 180MB (Optional)
                intent.putExtra(PickerConfig.MAX_SELECT_COUNT, 5);  //default 40 (Optional)
                intent.putExtra(PickerConfig.DEFAULT_SELECTED_LIST, mSelectMediaBean); // (Optional)
                startActivityForResult(intent, SELECT_MEDIA_REQUEST_CODE);
            }
        });

        mToolbarSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((int) mToolbarSend.getTag(R.id.uploadButton) == TAG_UPLOAD) {
                    showSnackBar(v, "您当前有文章正在上传,请等待上传完成!", "");
                    return;
                }
                if (isUpload()) {
                    OKCardBean mCardBean = new OKCardBean();
                    mCardBean.setUSER_NAME(USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
                    mCardBean.setTITLE_TEXT(USER_INFO_SP.getString(OKUserInfoBean.KEY_NICKNAME, ""));
                    mCardBean.setTITLE_IMAGE_URL("");
                    if (mOKCardBase64ListBean != null && mOKCardBase64ListBean.getCount() != 0) {
                        mCardBean.setCARD_TYPE(CARD_TYPE_TW);
                    } else {
                        mCardBean.setCARD_TYPE(CARD_TYPE_WZ);
                    }
                    if (!TextUtils.isEmpty(mEditLink.getText().toString()) && !mEditLink.getText().toString().equals("##")) {
                        mCardBean.setMESSAGE_LINK(mEditLink.getText().toString());
                    } else {
                        mCardBean.setMESSAGE_LINK("没有参考链接");
                    }
                    mCardBean.setCONTENT_TITLE_TEXT(mEditTitle.getText().toString());
                    mCardBean.setCONTENT_TEXT(mEditContent.getText().toString());
                    mCardBean.setLABELLING(mEditTag.getText().toString());
                    mCardBean.setCREATE_DATE(OKConstant.getNowDateByString());
                    if (mArticleTask != null && mArticleTask.getStatus() == AsyncTask.Status.RUNNING) {
                        mArticleTask.cancel(true);
                    }
                    mArticleTask = new ArticleTask(mOKCardBase64ListBean);
                    mArticleTask.executeOnExecutor(exec, mCardBean);
                    mToolbarSend.setTag(R.id.uploadButton, TAG_UPLOAD);
                    showProgressDialog("正在上传文章...");
                } else {
                    if (mOKCardBase64ListBean != null && mOKCardBase64ListBean.getCount() != 0) {
                        OKCardBean mCardBean = new OKCardBean();
                        mCardBean.setUSER_NAME(USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
                        mCardBean.setTITLE_TEXT(USER_INFO_SP.getString(OKUserInfoBean.KEY_NICKNAME, ""));
                        mCardBean.setTITLE_IMAGE_URL("");
                        mCardBean.setCARD_TYPE(CARD_TYPE_TP);
                        mCardBean.setCONTENT_IMAGE_URL(new Gson().toJson(mOKCardBase64ListBean));
                        mCardBean.setCREATE_DATE(OKConstant.getNowDateByString());
                        if (mArticleTask != null && mArticleTask.getStatus() == AsyncTask.Status.RUNNING) {
                            mArticleTask.cancel(true);
                        }
                        mArticleTask = new ArticleTask(mOKCardBase64ListBean);
                        mArticleTask.executeOnExecutor(exec, mCardBean);
                        mToolbarSend.setTag(R.id.uploadButton, TAG_UPLOAD);
                        showProgressDialog("正在上传图片...");
                    } else {
                        showSnackBar(v, "您可以不写文章,但至少选择一张图片!", "");
                    }
                }
            }
        });

        mClearImage1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mOKCardBase64ListBean != null && mOKCardBase64ListBean.getCount() != 0) {
                    mOKCardBase64ListBean.setBaseImage1("");
                    mOKCardBase64ListBean.setFormatImage1("");
                    mOKCardBase64ListBean.setCount(mOKCardBase64ListBean.getCount() - 1);
                    GlideApi(mAddImage1, R.drawable.add_image_black, R.drawable.add_image_black, R.drawable.add_image_black);
                    mClearImage1.setVisibility(View.GONE);
                }
            }
        });

        mClearImage2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mOKCardBase64ListBean != null && mOKCardBase64ListBean.getCount() != 0
                        && !TextUtils.isEmpty(mOKCardBase64ListBean.getBaseImage2())
                        && !TextUtils.isEmpty(mOKCardBase64ListBean.getFormatImage2())) {
                    mOKCardBase64ListBean.setBaseImage2("");
                    mOKCardBase64ListBean.setFormatImage2("");
                    mOKCardBase64ListBean.setCount(mOKCardBase64ListBean.getCount() - 1);
                    GlideApi(mAddImage2, R.drawable.add_image_black, R.drawable.add_image_black, R.drawable.add_image_black);
                    mClearImage2.setVisibility(View.GONE);
                }
            }
        });

        mClearImage3.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mOKCardBase64ListBean != null && mOKCardBase64ListBean.getCount() != 0
                        && !TextUtils.isEmpty(mOKCardBase64ListBean.getBaseImage3())
                        && !TextUtils.isEmpty(mOKCardBase64ListBean.getFormatImage3())) {
                    mOKCardBase64ListBean.setBaseImage3("");
                    mOKCardBase64ListBean.setFormatImage3("");
                    mOKCardBase64ListBean.setCount(mOKCardBase64ListBean.getCount() - 1);
                    GlideApi(mAddImage3, R.drawable.add_image_black, R.drawable.add_image_black, R.drawable.add_image_black);
                    mClearImage3.setVisibility(View.GONE);
                }
            }
        });

        mClearImage4.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mOKCardBase64ListBean != null && mOKCardBase64ListBean.getCount() != 0
                        && !TextUtils.isEmpty(mOKCardBase64ListBean.getBaseImage4())
                        && !TextUtils.isEmpty(mOKCardBase64ListBean.getFormatImage4())) {
                    mOKCardBase64ListBean.setBaseImage4("");
                    mOKCardBase64ListBean.setFormatImage4("");
                    mOKCardBase64ListBean.setCount(mOKCardBase64ListBean.getCount() - 1);
                    GlideApi(mAddImage4, R.drawable.add_image_black, R.drawable.add_image_black, R.drawable.add_image_black);
                    mClearImage4.setVisibility(View.GONE);
                }
            }
        });

        mClearImage5.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mOKCardBase64ListBean != null && mOKCardBase64ListBean.getCount() != 0
                        && !TextUtils.isEmpty(mOKCardBase64ListBean.getBaseImage5())
                        && !TextUtils.isEmpty(mOKCardBase64ListBean.getFormatImage5())) {
                    mOKCardBase64ListBean.setBaseImage5("");
                    mOKCardBase64ListBean.setFormatImage5("");
                    mOKCardBase64ListBean.setCount(mOKCardBase64ListBean.getCount() - 1);
                    GlideApi(mAddImage5, R.drawable.add_image_black, R.drawable.add_image_black, R.drawable.add_image_black);
                    mClearImage5.setVisibility(View.GONE);
                }
            }
        });

        mToolbarBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                backFinish();
            }
        });
    }

    private boolean isUpload() {
        return (!mEditTitle.getText().toString().equals("##")
                && !mEditTitle.getText().toString().equals(""))
                && (!mEditTag.getText().toString().equals("##")
                && !mEditTag.getText().toString().equals(""))
                && (!mEditContent.getText().toString().equals("##")
                && !mEditContent.getText().toString().equals(""));
    }

    private void findView() {
        super.findCommonToolbarView(this);
        setSupportActionBar(mToolbar);
        mToolbarBack.setVisibility(View.VISIBLE);
        mToolbarSend.setVisibility(View.VISIBLE);
        mToolbarAddImage.setVisibility(View.VISIBLE);
        mToolbarTitle.setVisibility(View.VISIBLE);
        mToolbarTitle.setText("发表文章");
        mEditTitle = (EditText) findViewById(R.id.RELEASE_input_biaoti);
        mEditTag = (EditText) findViewById(R.id.RELEASE_input_tag);
        mEditLink = (EditText) findViewById(R.id.RELEASE_input_link);
        mEditContent = (EditText) findViewById(R.id.RELEASE_input_zhengwen);
    }

    private void loadData() {
        mEditTag.setText(ARTICLE_SP.getString("TAG", "##"));
        mEditTitle.setText(ARTICLE_SP.getString("TITLE", "##"));
        mEditLink.setText(ARTICLE_SP.getString("LINK", "##"));
        mEditContent.setText(ARTICLE_SP.getString("CONTENT", "##"));
    }

    private void backFinish() {
        if ((int) mToolbarSend.getTag(R.id.uploadButton) == TAG_UPLOAD) {
            showAlertDialog("文章发表", "当前有文章正在后台上传,确定要退出?", "退出", "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            return;
        }

        if ((!TextUtils.isEmpty(mEditContent.getText().toString())) && (!mEditContent.getText().toString().equals("##"))) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setIcon(R.drawable.ic_launcher);
            alertDialog.setTitle("保存文章");
            alertDialog.setMessage("是否保存当前编辑的文章(无法保存选择的图片) ?");
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Editor editor = ARTICLE_SP.edit();
                    if (!TextUtils.isEmpty(mEditTitle.getText().toString()) && mEditTitle.getText().toString().equals("##")) {
                        editor.putString("TITLE", mEditTitle.getText().toString());
                    }
                    if (!TextUtils.isEmpty(mEditTag.getText().toString()) && !mEditTag.getText().toString().equals("##")) {
                        editor.putString("TAG", mEditTag.getText().toString());
                    }
                    if (!TextUtils.isEmpty(mEditLink.getText().toString()) && !mEditLink.getText().toString().equals("##")) {
                        editor.putString("LINK", mEditLink.getText().toString());
                    }
                    editor.putString("CONTENT", mEditContent.getText().toString());
                    editor.commit();
                    finish();
                }
            });
            alertDialog.show();
        } else {
            finish();
        }
    }

    private ArrayList<MediaBean> mSelectMediaBean;
    private final int SELECT_MEDIA_REQUEST_CODE = 200;

    private void dealWith(ArrayList<MediaBean> listMediaBean) {
        if (listMediaBean == null || listMediaBean.size() == 0) {
            showSnackBar(mToolbarAddImage, "未获选择图片", "");
            return;
        }
        long size = 0;
        for (MediaBean item : listMediaBean) { // 文件大小检查
            size += item.size;
        }
        if (size > 15 * 1024 * 1024) {
            showSnackBar(mToolbarAddImage, "一次上传的文件总量不能超过15MB", "");
            return;
        }
        mOKCardBase64ListBean.clear();
        int count = 0;
        GlideApi(mAddImage1, R.drawable.add_image_black, R.drawable.add_image_black, R.drawable.add_image_black);
        GlideApi(mAddImage2, R.drawable.add_image_black, R.drawable.add_image_black, R.drawable.add_image_black);
        GlideApi(mAddImage3, R.drawable.add_image_black, R.drawable.add_image_black, R.drawable.add_image_black);
        GlideApi(mAddImage4, R.drawable.add_image_black, R.drawable.add_image_black, R.drawable.add_image_black);
        GlideApi(mAddImage5, R.drawable.add_image_black, R.drawable.add_image_black, R.drawable.add_image_black);
        mClearImage1.setVisibility(View.GONE);
        mClearImage2.setVisibility(View.GONE);
        mClearImage3.setVisibility(View.GONE);
        mClearImage4.setVisibility(View.GONE);
        mClearImage5.setVisibility(View.GONE);
        for (int i = 0; i < listMediaBean.size(); i++) {
            MediaBean item = listMediaBean.get(i);
            String path = item.path; // 文件路径
            String gs = path.substring(path.lastIndexOf(".") + 1, path.length()); // 文件格式

            OKLogUtil.print("Select File Path:" + path);
            OKLogUtil.print("Select File Format:" + gs);
            OKLogUtil.print("Select File Size:" + item.size);

            if (i == 0) {
                mOKCardBase64ListBean.setFormatImage1(gs);
                mOKCardBase64ListBean.setBaseImage1(path);
                GlideApi(mAddImage1, path, R.drawable.add_image_black, R.drawable.add_image_black);
                mClearImage1.setVisibility(View.VISIBLE);
            }
            if (i == 1) {
                mOKCardBase64ListBean.setFormatImage2(gs);
                mOKCardBase64ListBean.setBaseImage2(path);
                GlideApi(mAddImage2, path, R.drawable.add_image_black, R.drawable.add_image_black);
                mClearImage2.setVisibility(View.VISIBLE);
            }
            if (i == 2) {
                mOKCardBase64ListBean.setFormatImage3(gs);
                mOKCardBase64ListBean.setBaseImage3(path);
                GlideApi(mAddImage3, path, R.drawable.add_image_black, R.drawable.add_image_black);
                mClearImage3.setVisibility(View.VISIBLE);
            }
            if (i == 3) {
                mOKCardBase64ListBean.setFormatImage4(gs);
                mOKCardBase64ListBean.setBaseImage4(path);
                GlideApi(mAddImage4, path, R.drawable.add_image_black, R.drawable.add_image_black);
                mClearImage4.setVisibility(View.VISIBLE);
            }
            if (i == 4) {
                mOKCardBase64ListBean.setFormatImage5(gs);
                mOKCardBase64ListBean.setBaseImage5(path);
                GlideApi(mAddImage5, path, R.drawable.add_image_black, R.drawable.add_image_black);
                mClearImage5.setVisibility(View.VISIBLE);
            }
            count++;
        }
        mOKCardBase64ListBean.setCount(count);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECT_MEDIA_REQUEST_CODE:
                if (resultCode == PickerConfig.RESULT_CODE) {
                    mSelectMediaBean = data.getParcelableArrayListExtra(PickerConfig.EXTRA_RESULT);
                    dealWith(mSelectMediaBean);
                }
                break;
            default:
                break;
        }
    }

    private class ArticleTask extends AsyncTask<OKCardBean, Void, Boolean> {
        private OKCardBase64ListBean imageListBean; // 选择的图片视频

        public ArticleTask(OKCardBase64ListBean bean) {
            this.imageListBean = bean;
        }

        @Override
        protected Boolean doInBackground(OKCardBean... params) {
            if (isCancelled()) {
                return false;
            }

            OKCardBean mCardBean = params[0]; // 文章文字内容Bean
            Map<String, String> map = new HashMap<>(); // 文章文字内容参数

            map.put("username", mCardBean.getUSER_NAME());
            map.put("title", mCardBean.getTITLE_TEXT()); // 弃用参数,用户昵称
            map.put("title_image_url", mCardBean.getTITLE_IMAGE_URL()); // 弃用参数,用户头像url
            map.put("type", mCardBean.getCARD_TYPE());
            map.put("content_title", mCardBean.getCONTENT_TITLE_TEXT());
            map.put("labelling", mCardBean.getLABELLING());
            map.put("content_text", mCardBean.getCONTENT_TEXT());
            map.put("link", mCardBean.getMESSAGE_LINK());
            map.put("date", mCardBean.getCREATE_DATE());
            map.put("content_image", ""); // 弃用参数,用户选择的图片

            Map<String, File> mFileMap = new HashMap<>(); // 文章图片视频参数
            if (imageListBean.getCount() != 0) {
                String mImage1Path = imageListBean.getBaseImage1();
                if (!TextUtils.isEmpty(mImage1Path)) {
                    File file = new File(mImage1Path);
                    if (file.exists()) {
                        OKLogUtil.print("old1 File Size :" + file.length());
                        File fileNew = new OKCompressHelper.Builder(OKArticleReleaseActivity.this).setQuality(80).build().compressToFile(file);
                        OKLogUtil.print("new1 File Size :" + fileNew.length());
                        // 生成服务器文件名,并添加到Map参数中!
                        String newFileName = mCardBean.getUSER_NAME() + "_" + UUID.randomUUID().toString().replaceAll("-", "") + "." + imageListBean.getFormatImage1();
                        mFileMap.put(newFileName, fileNew);
                    }
                }
                String mImage2Path = imageListBean.getBaseImage2();
                if (!TextUtils.isEmpty(mImage2Path)) {
                    File file = new File(mImage2Path);
                    if (file.exists()) {
                        OKLogUtil.print("old2 File Size :" + file.length());
                        File fileNew = new OKCompressHelper.Builder(OKArticleReleaseActivity.this).setQuality(80).build().compressToFile(file);
                        OKLogUtil.print("new2 File Size :" + fileNew.length());
                        // 生成服务器文件名,并添加到Map参数中!
                        String newFileName = mCardBean.getUSER_NAME() + "_" + UUID.randomUUID().toString().replaceAll("-", "") + "." + imageListBean.getFormatImage2();
                        mFileMap.put(newFileName, fileNew);
                    }
                }
                String mImage3Path = imageListBean.getBaseImage3();
                if (!TextUtils.isEmpty(mImage3Path)) {
                    File file = new File(mImage3Path);
                    if (file.exists()) {
                        OKLogUtil.print("old3 File Size :" + file.length());
                        File fileNew = new OKCompressHelper.Builder(OKArticleReleaseActivity.this).setQuality(80).build().compressToFile(file);
                        OKLogUtil.print("new3 File Size :" + fileNew.length());
                        // 生成服务器文件名,并添加到Map参数中!
                        String newFileName = mCardBean.getUSER_NAME() + "_" + UUID.randomUUID().toString().replaceAll("-", "") + "." + imageListBean.getFormatImage3();
                        mFileMap.put(newFileName, fileNew);
                    }
                }
                String mImage4Path = imageListBean.getBaseImage4();
                if (!TextUtils.isEmpty(mImage4Path)) {
                    File file = new File(mImage4Path);
                    if (file.exists()) {
                        OKLogUtil.print("old4 File Size :" + file.length());
                        File fileNew = new OKCompressHelper.Builder(OKArticleReleaseActivity.this).setQuality(80).build().compressToFile(file);
                        OKLogUtil.print("new4 File Size :" + fileNew.length());
                        // 生成服务器文件名,并添加到Map参数中!
                        String newFileName = mCardBean.getUSER_NAME() + "_" + UUID.randomUUID().toString().replaceAll("-", "") + "." + imageListBean.getFormatImage4();
                        mFileMap.put(newFileName, fileNew);
                    }
                }
                String mImage5Path = imageListBean.getBaseImage5();
                if (!TextUtils.isEmpty(mImage5Path)) {
                    File file = new File(mImage5Path);
                    if (file.exists()) {
                        OKLogUtil.print("old5 File Size :" + file.length());
                        File fileNew = new OKCompressHelper.Builder(OKArticleReleaseActivity.this).setQuality(80).build().compressToFile(file);
                        OKLogUtil.print("new5 File Size :" + fileNew.length());
                        // 生成服务器文件名,并添加到Map参数中!
                        String newFileName = mCardBean.getUSER_NAME() + "_" + UUID.randomUUID().toString().replaceAll("-", "") + "." + imageListBean.getFormatImage5();
                        mFileMap.put(newFileName, fileNew);
                    }
                }
            }
            return new OKBusinessNet().addUserCard(mFileMap, map);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (isCancelled()) {
                return;
            }
            if (aBoolean) {
                mEditTitle.setText("##");
                mEditTag.setText("##");
                mEditLink.setText("##");
                mEditContent.setText("##");
                Editor editor = ARTICLE_SP.edit();
                editor.putString("TAG", "##");
                editor.putString("TITLE", "##");
                editor.putString("LINK", "##");
                editor.putString("CONTENT", "##");
                editor.commit();
                showSnackBar(mToolbarAddImage, "上传成功", "");
            } else {
                showSnackBar(mToolbarAddImage, "上传失败", "");
            }
            mToolbarSend.setTag(R.id.uploadButton, TAG_NORMAL);
            closeProgressDialog();
        }
    }
}
