package com.onlyknow.app.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.dmcbig.mediapicker.PickerActivity;
import com.dmcbig.mediapicker.PickerConfig;
import com.dmcbig.mediapicker.bean.MediaBean;
import com.onlyknow.app.R;
import com.onlyknow.app.api.OKServiceResult;
import com.onlyknow.app.api.card.OKAddCardApi;
import com.onlyknow.app.db.bean.OKCardBean;
import com.onlyknow.app.db.bean.OKUserInfoBean;
import com.onlyknow.app.ui.OKBaseActivity;
import com.onlyknow.app.ui.view.OKSEImageView;
import com.onlyknow.app.utils.OKLogUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class OKAddCardActivity extends OKBaseActivity implements OKAddCardApi.onCallBack {
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

    private List<OKCardBean.CardImage> imageList = new ArrayList<>(5);

    private OKAddCardApi mOKAddCardApi;

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
        if (mOKAddCardApi != null) {
            mOKAddCardApi.cancelTask();
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
            AlertDialog.Builder builder = new AlertDialog.Builder(OKAddCardActivity.this);
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
                Intent intent = new Intent(OKAddCardActivity.this, PickerActivity.class);
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

                    OKCardBean cardBean = new OKCardBean();
                    cardBean.setUserName(USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
                    cardBean.setTitleText(USER_INFO_SP.getString(OKUserInfoBean.KEY_NICKNAME, ""));

                    if (imageList != null && imageList.size() != 0) {
                        cardBean.setImageList(imageList);
                        cardBean.setCardType(CARD_TYPE_TW);
                    } else {
                        cardBean.setCardType(CARD_TYPE_WZ);
                    }

                    if (!TextUtils.isEmpty(mEditLink.getText().toString()) && !mEditLink.getText().toString().equals("##")) {
                        cardBean.setMessageLink(mEditLink.getText().toString());
                    } else {
                        cardBean.setMessageLink("没有参考链接");
                    }

                    cardBean.setContentTitleText(mEditTitle.getText().toString());
                    cardBean.setContentText(mEditContent.getText().toString());
                    cardBean.setLabelling(mEditTag.getText().toString());
                    cardBean.setCreateDate(new Date());

                    OKAddCardApi.Params params = new OKAddCardApi.Params();
                    params.setCardBean(cardBean);

                    mToolbarSend.setTag(R.id.uploadButton, TAG_UPLOAD);
                    showProgressDialog("正在上传文章...");

                    if (mOKAddCardApi != null) {
                        mOKAddCardApi.cancelTask();
                    }
                    mOKAddCardApi = new OKAddCardApi(OKAddCardActivity.this);
                    mOKAddCardApi.requestAddCard(params, OKAddCardActivity.this);
                } else {
                    if (imageList != null && imageList.size() != 0) {

                        OKCardBean cardBean = new OKCardBean();
                        cardBean.setUserName(USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
                        cardBean.setTitleText(USER_INFO_SP.getString(OKUserInfoBean.KEY_NICKNAME, ""));
                        cardBean.setCardType(CARD_TYPE_TP);
                        cardBean.setCreateDate(new Date());
                        cardBean.setImageList(imageList);

                        OKAddCardApi.Params params = new OKAddCardApi.Params();
                        params.setCardBean(cardBean);

                        mToolbarSend.setTag(R.id.uploadButton, TAG_UPLOAD);
                        showProgressDialog("正在上传图片...");

                        if (mOKAddCardApi != null) {
                            mOKAddCardApi.cancelTask();
                        }
                        mOKAddCardApi = new OKAddCardApi(OKAddCardActivity.this);
                        mOKAddCardApi.requestAddCard(params, OKAddCardActivity.this);

                    } else {
                        showSnackBar(v, "您可以不写文章,但至少选择一张图片!", "");
                    }
                }
            }
        });

        mClearImage1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (imageList != null && imageList.size() > 0) {
                    imageList.set(0, null);
                    GlideApi(mAddImage1, R.drawable.add_image_black, R.drawable.add_image_black, R.drawable.add_image_black);
                    mClearImage1.setVisibility(View.GONE);
                }
            }
        });

        mClearImage2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (imageList != null && imageList.size() > 1) {
                    imageList.set(1, null);
                    GlideApi(mAddImage2, R.drawable.add_image_black, R.drawable.add_image_black, R.drawable.add_image_black);
                    mClearImage2.setVisibility(View.GONE);
                }
            }
        });

        mClearImage3.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (imageList != null && imageList.size() > 2) {
                    imageList.set(2, null);
                    GlideApi(mAddImage3, R.drawable.add_image_black, R.drawable.add_image_black, R.drawable.add_image_black);
                    mClearImage3.setVisibility(View.GONE);
                }
            }
        });

        mClearImage4.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (imageList != null && imageList.size() > 3) {
                    imageList.set(3, null);
                    GlideApi(mAddImage4, R.drawable.add_image_black, R.drawable.add_image_black, R.drawable.add_image_black);
                    mClearImage4.setVisibility(View.GONE);
                }
            }
        });

        mClearImage5.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (imageList != null && imageList.size() > 4) {
                    imageList.set(4, null);
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

    private void dealWith(ArrayList<MediaBean> medias) {
        if (medias == null || medias.size() == 0) {
            showSnackBar(mToolbarAddImage, "未获选择图片", "");
            return;
        }
        long size = 0;
        for (MediaBean item : medias) { // 文件大小检查
            size += item.size;
        }
        if (size > 15 * 1024 * 1024) {
            showSnackBar(mToolbarAddImage, "一次上传的文件总量不能超过15MB", "");
            return;
        }

        imageList.clear();

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

        OKCardBean.CardImage image = null;
        for (int i = 0; i < medias.size(); i++) {

            if (i > 4) {
                break;
            }

            MediaBean item = medias.get(i);

            if (item == null) continue;

            image = new OKCardBean.CardImage();
            image.setUrl(item.path);
            image.setSize(item.size);
            imageList.add(image);

            if (i == 0) {
                GlideApi(mAddImage1, item.path, R.drawable.add_image_black, R.drawable.add_image_black);
                mClearImage1.setVisibility(View.VISIBLE);
            }
            if (i == 1) {
                GlideApi(mAddImage2, item.path, R.drawable.add_image_black, R.drawable.add_image_black);
                mClearImage2.setVisibility(View.VISIBLE);
            }
            if (i == 2) {
                GlideApi(mAddImage3, item.path, R.drawable.add_image_black, R.drawable.add_image_black);
                mClearImage3.setVisibility(View.VISIBLE);
            }
            if (i == 3) {
                GlideApi(mAddImage4, item.path, R.drawable.add_image_black, R.drawable.add_image_black);
                mClearImage4.setVisibility(View.VISIBLE);
            }
            if (i == 4) {
                GlideApi(mAddImage5, item.path, R.drawable.add_image_black, R.drawable.add_image_black);
                mClearImage5.setVisibility(View.VISIBLE);
            }
        }
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

    @Override
    public void addCardComplete(OKServiceResult<Object> result) {
        mToolbarSend.setTag(R.id.uploadButton, TAG_NORMAL);
        closeProgressDialog();

        if (result == null || !result.isSuccess()) {

            showSnackBar(mToolbarAddImage, "上传失败", "");

            return;
        }

        mEditTitle.setText("##");
        mEditTag.setText("##");
        mEditLink.setText("##");
        mEditContent.setText("##");
        SharedPreferences.Editor editor = ARTICLE_SP.edit();
        editor.putString("TAG", "##");
        editor.putString("TITLE", "##");
        editor.putString("LINK", "##");
        editor.putString("CONTENT", "##");
        editor.commit();

        showSnackBar(mToolbarAddImage, "上传成功", "");
    }
}
