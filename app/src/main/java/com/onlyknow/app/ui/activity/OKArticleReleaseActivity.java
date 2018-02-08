package com.onlyknow.app.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.caimuhao.rxpicker.RxPicker;
import com.caimuhao.rxpicker.bean.ImageItem;
import com.caimuhao.rxpicker.utils.RxPickerImageLoader;
import com.google.gson.Gson;
import com.onlyknow.app.GlideApp;
import com.onlyknow.app.OKConstant;
import com.onlyknow.app.R;
import com.onlyknow.app.api.OKBusinessApi;
import com.onlyknow.app.database.bean.OKCardBase64ListBean;
import com.onlyknow.app.database.bean.OKCardBean;
import com.onlyknow.app.database.bean.OKUserInfoBean;
import com.onlyknow.app.ui.OKBaseActivity;
import com.onlyknow.app.ui.view.OKRelativeLayout;
import com.onlyknow.app.ui.view.OKSEImageView;
import com.onlyknow.app.utils.OKBase64Util;
import com.onlyknow.app.utils.OKSDCardUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

public class OKArticleReleaseActivity extends OKBaseActivity {
    @Bind(R.id.RELEASE_input_zhengwentupian1)
    OKSEImageView RELEASEInputZhengwentupian1;
    @Bind(R.id.RELEASE_clear1_imag)
    OKSEImageView RELEASEClear1Imag;

    @Bind(R.id.RELEASE_input_zhengwentupian2)
    OKSEImageView RELEASEInputZhengwentupian2;
    @Bind(R.id.RELEASE_clear2_imag)
    OKSEImageView RELEASEClear2Imag;

    @Bind(R.id.RELEASE_input_zhengwentupian3)
    OKSEImageView RELEASEInputZhengwentupian3;
    @Bind(R.id.RELEASE_clear3_imag)
    OKSEImageView RELEASEClear3Imag;

    @Bind(R.id.RELEASE_input_zhengwentupian4)
    OKSEImageView RELEASEInputZhengwentupian4;
    @Bind(R.id.RELEASE_clear4_imag)
    OKSEImageView RELEASEClear4Imag;

    @Bind(R.id.RELEASE_input_zhengwentupian5)
    OKSEImageView RELEASEInputZhengwentupian5;
    @Bind(R.id.RELEASE_clear5_imag)
    OKSEImageView RELEASEClear5Imag;

    @Bind(R.id.ok_activity_addImage1_layout)
    OKRelativeLayout okActivityAddImage1Layout;

    @Bind(R.id.ok_activity_addImage2_layout)
    OKRelativeLayout okActivityAddImage2Layout;

    @Bind(R.id.ok_activity_addImage3_layout)
    OKRelativeLayout okActivityAddImage3Layout;

    @Bind(R.id.ok_activity_addImage4_layout)
    OKRelativeLayout okActivityAddImage4Layout;

    @Bind(R.id.ok_activity_addImage5_layout)
    OKRelativeLayout okActivityAddImage5Layout;

    private EditText editTextTitle, editTextTag, editTextLink, editTextContent;

    private OKCardBase64ListBean mOKCardBase64ListBean;

    private ArticleTask mArticleTask;

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
            case 1: // 调用系统裁剪图片后
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    if (uri == null) {
                        showSnackbar(mToolbarAddImage, "未获取到URI地址", "");
                        return;
                    }
                    OKCardBase64ListBean bean = new OKCardBase64ListBean();
                    String path = OKSDCardUtil.getFilePathByImageUri(OKArticleReleaseActivity.this, uri);
                    String gs = path.substring(path.lastIndexOf(".") + 1, path.length());
                    bean.setFormatImage1(gs);
                    bean.setBaseImage1(path);
                    bean.setCount(1);
                    mOKCardBase64ListBean = bean;

                    GlideApi(RELEASEInputZhengwentupian1, path, R.drawable.add_image_black, R.drawable.add_image_black);
                }
                break;
            default:
                break;

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if ((!TextUtils.isEmpty(editTextContent.getText().toString())) && (!editTextContent.getText().toString().equals("##"))) {
                AlertDialog alertDialog = new AlertDialog.Builder(OKArticleReleaseActivity.this).create();
                alertDialog.setTitle("保存文章");
                alertDialog.setMessage("是否保存当前编辑的文章 ?");

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
                        if (!TextUtils.isEmpty(editTextTitle.getText().toString()) && editTextTitle.getText().toString().equals("##")) {
                            editor.putString("BIAOTI", editTextTitle.getText().toString());
                        }
                        if (!TextUtils.isEmpty(editTextTag.getText().toString()) && !editTextTag.getText().toString().equals("##")) {
                            editor.putString("TAG", editTextTag.getText().toString());
                        }
                        if (!TextUtils.isEmpty(editTextLink.getText().toString()) && !editTextLink.getText().toString().equals("##")) {
                            editor.putString("LINK", editTextLink.getText().toString());
                        }
                        editor.putString("NEIRON", editTextContent.getText().toString());
                        editor.commit();

                        finish();
                    }
                });
                alertDialog.show();
            } else {
                OKArticleReleaseActivity.this.finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
        intent.putExtra("outputX", 800);
        intent.putExtra("outputY", 800);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", false);
        // 进入系统裁剪图片的界面
        startActivityForResult(intent, 1);
    }

    private void init() {
        mToolbarAddImage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                RxPicker.init(new LoadImage());
                RxPicker.of().single(false).camera(true).limit(1, 5).start(OKArticleReleaseActivity.this).subscribe(new Consumer<List<ImageItem>>() {
                    @Override
                    public void accept(List<ImageItem> imageItems) throws Exception {
                        if (imageItems == null || imageItems.size() == 0) {
                            showSnackbar(mToolbarAddImage, "未获选择图片", "");
                            return;
                        }
                        OKCardBase64ListBean bean = new OKCardBase64ListBean();
                        if (imageItems.size() == 1) {
                            ImageItem item = imageItems.get(0);
                            String path = item.getPath();
                            String gs = path.substring(path.lastIndexOf(".") + 1, path.length());
                            if ("jpg".equals(gs) || "png".equals(gs)) {
                                Uri uri = OKSDCardUtil.getUriByFilePath(OKArticleReleaseActivity.this, path);
                                if (uri == null) {
                                    showSnackbar(mToolbarAddImage, "未获取到URI地址", "");
                                    return;
                                }
                                cropPhoto(uri);
                            } else {
                                bean.setFormatImage1(gs);
                                bean.setBaseImage1(path);
                                bean.setCount(1);
                                mOKCardBase64ListBean = bean;
                                GlideApi(RELEASEInputZhengwentupian1, path, R.drawable.add_image_black, R.drawable.add_image_black);
                            }
                            return;
                        }

                        int count = 0;
                        for (int i = 0; i < imageItems.size(); i++) {
                            ImageItem item = imageItems.get(i);
                            String path = item.getPath();
                            String gs = path.substring(path.lastIndexOf(".") + 1, path.length());
                            if (i == 0) {
                                bean.setFormatImage1(gs);
                                bean.setBaseImage1(path);
                                GlideApi(RELEASEInputZhengwentupian1, path, R.drawable.add_image_black, R.drawable.add_image_black);
                                RELEASEClear1Imag.setVisibility(View.VISIBLE);
                            }
                            if (i == 1) {
                                bean.setFormatImage2(gs);
                                bean.setBaseImage2(path);
                                GlideApi(RELEASEInputZhengwentupian2, path, R.drawable.add_image_black, R.drawable.add_image_black);
                                RELEASEClear2Imag.setVisibility(View.VISIBLE);
                            }
                            if (i == 2) {
                                bean.setFormatImage3(gs);
                                bean.setBaseImage3(path);
                                GlideApi(RELEASEInputZhengwentupian3, path, R.drawable.add_image_black, R.drawable.add_image_black);
                                RELEASEClear3Imag.setVisibility(View.VISIBLE);
                            }
                            if (i == 3) {
                                bean.setFormatImage4(gs);
                                bean.setBaseImage4(path);
                                GlideApi(RELEASEInputZhengwentupian4, path, R.drawable.add_image_black, R.drawable.add_image_black);
                                RELEASEClear4Imag.setVisibility(View.VISIBLE);
                            }
                            if (i == 4) {
                                bean.setFormatImage5(gs);
                                bean.setBaseImage5(path);
                                GlideApi(RELEASEInputZhengwentupian5, path, R.drawable.add_image_black, R.drawable.add_image_black);
                                RELEASEClear5Imag.setVisibility(View.VISIBLE);
                            }
                            count++;
                        }
                        bean.setCount(count);
                        mOKCardBase64ListBean = bean;
                    }
                });
            }
        });

        mToolbarSend.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                if ((!editTextTitle.getText().toString().equals("##")
                        && !editTextTitle.getText().toString().equals(""))
                        && (!editTextTag.getText().toString().equals("##")
                        && !editTextTag.getText().toString().equals(""))
                        && (!editTextContent.getText().toString().equals("##")
                        && !editTextContent.getText().toString().equals(""))) {

                    OKCardBean mCardBean = new OKCardBean();
                    mCardBean.setUSER_NAME(USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
                    mCardBean.setTITLE_TEXT(USER_INFO_SP.getString(OKUserInfoBean.KEY_NICKNAME, ""));

                    Toast.makeText(OKArticleReleaseActivity.this, mCardBean.getTITLE_TEXT(), Toast.LENGTH_LONG).show();

                    mCardBean.setTITLE_IMAGE_URL("");

                    if (mOKCardBase64ListBean != null && mOKCardBase64ListBean.getCount() != 0) {
                        mCardBean.setCARD_TYPE(CARD_TYPE_TW);
                        mCardBean.setCONTENT_IMAGE_URL(new Gson().toJson(mOKCardBase64ListBean));
                    } else {
                        mCardBean.setCARD_TYPE(CARD_TYPE_WZ);
                    }
                    if (!editTextLink.getText().toString().equals("##")
                            && !editTextLink.getText().toString().equals("")) {
                        mCardBean.setMESSAGE_LINK(editTextLink.getText().toString());
                    } else {
                        mCardBean.setMESSAGE_LINK("没有参考链接");
                    }
                    mCardBean.setCONTENT_TITLE_TEXT(editTextTitle.getText().toString());
                    mCardBean.setCONTENT_TEXT(editTextContent.getText().toString());
                    mCardBean.setLABELLING(editTextTag.getText().toString());
                    mCardBean.setCREATE_DATE(OKConstant.getNowDate());

                    if (mArticleTask != null && mArticleTask.getStatus() == AsyncTask.Status.RUNNING) {
                        mArticleTask.cancel(true);
                    }
                    mArticleTask = new ArticleTask();
                    mArticleTask.executeOnExecutor(exec, mCardBean);
                    showProgressDialog("正在上传文章...");
                } else {
                    if (mOKCardBase64ListBean != null && mOKCardBase64ListBean.getCount() != 0) {
                        OKCardBean mCardBean = new OKCardBean();
                        mCardBean.setUSER_NAME(USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
                        mCardBean.setTITLE_TEXT(USER_INFO_SP.getString(OKUserInfoBean.KEY_NICKNAME, ""));
                        mCardBean.setTITLE_IMAGE_URL("");
                        mCardBean.setCARD_TYPE(CARD_TYPE_TP);
                        mCardBean.setCONTENT_IMAGE_URL(new Gson().toJson(mOKCardBase64ListBean));
                        mCardBean.setCREATE_DATE(OKConstant.getNowDate());

                        if (mArticleTask != null && mArticleTask.getStatus() == AsyncTask.Status.RUNNING) {
                            mArticleTask.cancel(true);
                        }
                        mArticleTask = new ArticleTask();
                        mArticleTask.executeOnExecutor(exec, mCardBean);
                        showProgressDialog("正在上传图片...");
                    } else {
                        showSnackbar(v, "您可以不写文章,但请至少选择一张图片!", "");
                    }
                }
            }
        });

        RELEASEClear1Imag.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mOKCardBase64ListBean != null && mOKCardBase64ListBean.getCount() != 0) {
                    mOKCardBase64ListBean.setBaseImage1("");
                    mOKCardBase64ListBean.setFormatImage1("");
                    mOKCardBase64ListBean.setCount(mOKCardBase64ListBean.getCount() - 1);
                    GlideApi(RELEASEInputZhengwentupian1, R.drawable.add_image_black, R.drawable.add_image_black, R.drawable.add_image_black);
                    RELEASEClear1Imag.setVisibility(View.GONE);
                }
            }
        });

        RELEASEClear2Imag.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mOKCardBase64ListBean != null && mOKCardBase64ListBean.getCount() != 0
                        && !TextUtils.isEmpty(mOKCardBase64ListBean.getBaseImage2())
                        && !TextUtils.isEmpty(mOKCardBase64ListBean.getFormatImage2())) {
                    mOKCardBase64ListBean.setBaseImage2("");
                    mOKCardBase64ListBean.setFormatImage2("");
                    mOKCardBase64ListBean.setCount(mOKCardBase64ListBean.getCount() - 1);
                    GlideApi(RELEASEInputZhengwentupian2, R.drawable.add_image_black, R.drawable.add_image_black, R.drawable.add_image_black);
                    RELEASEClear2Imag.setVisibility(View.GONE);
                }
            }
        });

        RELEASEClear3Imag.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mOKCardBase64ListBean != null && mOKCardBase64ListBean.getCount() != 0
                        && !TextUtils.isEmpty(mOKCardBase64ListBean.getBaseImage3())
                        && !TextUtils.isEmpty(mOKCardBase64ListBean.getFormatImage3())) {
                    mOKCardBase64ListBean.setBaseImage3("");
                    mOKCardBase64ListBean.setFormatImage3("");
                    mOKCardBase64ListBean.setCount(mOKCardBase64ListBean.getCount() - 1);
                    GlideApi(RELEASEInputZhengwentupian3, R.drawable.add_image_black, R.drawable.add_image_black, R.drawable.add_image_black);
                    RELEASEClear3Imag.setVisibility(View.GONE);
                }
            }
        });

        RELEASEClear4Imag.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mOKCardBase64ListBean != null && mOKCardBase64ListBean.getCount() != 0
                        && !TextUtils.isEmpty(mOKCardBase64ListBean.getBaseImage4())
                        && !TextUtils.isEmpty(mOKCardBase64ListBean.getFormatImage4())) {
                    mOKCardBase64ListBean.setBaseImage4("");
                    mOKCardBase64ListBean.setFormatImage4("");
                    mOKCardBase64ListBean.setCount(mOKCardBase64ListBean.getCount() - 1);
                    GlideApi(RELEASEInputZhengwentupian4, R.drawable.add_image_black, R.drawable.add_image_black, R.drawable.add_image_black);
                    RELEASEClear4Imag.setVisibility(View.GONE);
                }
            }
        });

        RELEASEClear5Imag.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mOKCardBase64ListBean != null && mOKCardBase64ListBean.getCount() != 0
                        && !TextUtils.isEmpty(mOKCardBase64ListBean.getBaseImage5())
                        && !TextUtils.isEmpty(mOKCardBase64ListBean.getFormatImage5())) {
                    mOKCardBase64ListBean.setBaseImage5("");
                    mOKCardBase64ListBean.setFormatImage5("");
                    mOKCardBase64ListBean.setCount(mOKCardBase64ListBean.getCount() - 1);
                    GlideApi(RELEASEInputZhengwentupian5, R.drawable.add_image_black, R.drawable.add_image_black, R.drawable.add_image_black);
                    RELEASEClear5Imag.setVisibility(View.GONE);
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

        mToolbarBack.setVisibility(View.VISIBLE);
        mToolbarSend.setVisibility(View.VISIBLE);
        mToolbarAddImage.setVisibility(View.VISIBLE);
        mToolbarTitle.setVisibility(View.VISIBLE);
        mToolbarTitle.setText("发表文章");

        editTextTitle = (EditText) findViewById(R.id.RELEASE_input_biaoti);
        editTextTag = (EditText) findViewById(R.id.RELEASE_input_tag);
        editTextLink = (EditText) findViewById(R.id.RELEASE_input_link);
        editTextContent = (EditText) findViewById(R.id.RELEASE_input_zhengwen);
    }

    private void loadData() {
        editTextTag.setText(ARTICLE_SP.getString("TAG", "##"));
        editTextTitle.setText(ARTICLE_SP.getString("BIAOTI", "##"));
        editTextLink.setText(ARTICLE_SP.getString("LINK", "##"));
        editTextContent.setText(ARTICLE_SP.getString("NEIRON", "##"));
    }

    private class ArticleTask extends AsyncTask<OKCardBean, Void, Boolean> {

        @Override
        protected Boolean doInBackground(OKCardBean... params) {
            if (isCancelled()) {
                return false;
            }

            OKCardBean mCardBean = params[0];

            Map<String, String> map = new HashMap<>();

            map.put("content_image", "");

            String imageJson = mCardBean.getCONTENT_IMAGE_URL();

            if (!TextUtils.isEmpty(imageJson)) {
                OKCardBase64ListBean bean = new Gson().fromJson(imageJson, OKCardBase64ListBean.class);
                if (bean == null) {
                    return false;
                }

                String baseImage1 = bean.getBaseImage1();
                if (!TextUtils.isEmpty(baseImage1)) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPurgeable = true;
                    options.inSampleSize = 1; // 表示不压缩
                    Bitmap bitmap = BitmapFactory.decodeFile(baseImage1, options);
                    if (bitmap != null) {
                        bean.setBaseImage1(OKBase64Util.BitmapToBase64(bitmap));
                    }

                    bitmap.recycle();
                    bitmap = null;
                }

                String baseImage2 = bean.getBaseImage2();
                if (!TextUtils.isEmpty(baseImage2)) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPurgeable = true;
                    options.inSampleSize = 1; // 表示不压缩
                    Bitmap bitmap = BitmapFactory.decodeFile(baseImage2, options);
                    if (bitmap != null) {
                        bean.setBaseImage2(OKBase64Util.BitmapToBase64(bitmap));
                    }

                    bitmap.recycle();
                    bitmap = null;
                }

                String baseImage3 = bean.getBaseImage3();
                if (!TextUtils.isEmpty(baseImage3)) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPurgeable = true;
                    options.inSampleSize = 1; // 表示不压缩
                    Bitmap bitmap = BitmapFactory.decodeFile(baseImage3, options);
                    if (bitmap != null) {
                        bean.setBaseImage3(OKBase64Util.BitmapToBase64(bitmap));
                    }
                    bitmap.recycle();
                    bitmap = null;
                }

                String baseImage4 = bean.getBaseImage4();
                if (!TextUtils.isEmpty(baseImage4)) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPurgeable = true;
                    options.inSampleSize = 1; // 表示不压缩
                    Bitmap bitmap = BitmapFactory.decodeFile(baseImage4, options);
                    if (bitmap != null) {
                        bean.setBaseImage4(OKBase64Util.BitmapToBase64(bitmap));
                    }

                    bitmap.recycle();
                    bitmap = null;
                }

                String baseImage5 = bean.getBaseImage5();
                if (!TextUtils.isEmpty(baseImage5)) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPurgeable = true;
                    options.inSampleSize = 1; // 表示不压缩
                    Bitmap bitmap = BitmapFactory.decodeFile(baseImage5, options);
                    if (bitmap != null) {
                        bean.setBaseImage5(OKBase64Util.BitmapToBase64(bitmap));
                    }
                    bitmap.recycle();
                    bitmap = null;
                }

                map.put("content_image", new Gson().toJson(bean));
            }

            // 封装请求参数
            map.put("username", mCardBean.getUSER_NAME());
            map.put("title", mCardBean.getTITLE_TEXT());
            map.put("title_image_url", mCardBean.getTITLE_IMAGE_URL());
            map.put("type", mCardBean.getCARD_TYPE());
            map.put("content_title", mCardBean.getCONTENT_TITLE_TEXT());
            map.put("labelling", mCardBean.getLABELLING());
            map.put("content_text", mCardBean.getCONTENT_TEXT());
            map.put("link", mCardBean.getMESSAGE_LINK());
            map.put("date", mCardBean.getCREATE_DATE());

            return new OKBusinessApi().addUserCard(map);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (isCancelled()) {
                return;
            }

            if (aBoolean) {
                editTextTitle.setText("##");
                editTextTag.setText("##");
                editTextLink.setText("##");
                editTextContent.setText("##");
                Editor editor = ARTICLE_SP.edit();
                editor.putString("TAG", "##");
                editor.putString("BIAOTI", "##");
                editor.putString("LINK", "##");
                editor.putString("NEIRON", "##");
                editor.putString("BASEIMAG", "");
                editor.commit();
                showSnackbar(mToolbarAddImage, "上传成功", "");
            } else {
                showSnackbar(mToolbarAddImage, "上传失败", "");
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
}
