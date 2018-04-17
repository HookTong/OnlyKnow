package com.onlyknow.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.dmcbig.mediapicker.PickerActivity;
import com.dmcbig.mediapicker.PickerConfig;
import com.dmcbig.mediapicker.bean.MediaBean;
import com.onlyknow.app.OKConstant;
import com.onlyknow.app.R;
import com.onlyknow.app.api.OKServiceResult;
import com.onlyknow.app.api.user.OKFeedBackApi;
import com.onlyknow.app.database.bean.OKUserInfoBean;
import com.onlyknow.app.ui.OKBaseActivity;
import com.onlyknow.app.ui.view.OKSEImageView;
import com.onlyknow.app.utils.OKDeviceInfoUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OKFeedBackActivity extends OKBaseActivity implements OKFeedBackApi.onCallBack {
    private AppCompatButton mAppCompatButtonSend;
    private OKSEImageView mImageViewAddTuPian, mImageViewClear;
    private EditText mEditTextNeiRon;

    private String mFilePath = "";

    private OKFeedBackApi mOKFeedBackApi;

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
        if (mOKFeedBackApi != null) {
            mOKFeedBackApi.cancelTask();
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

    private void init() {
        mImageViewAddTuPian.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OKFeedBackActivity.this, PickerActivity.class);
                intent.putExtra(PickerConfig.SELECT_MODE, PickerConfig.PICKER_IMAGE);//default image and video (Optional)
                intent.putExtra(PickerConfig.MAX_SELECT_SIZE, 3145728L); //default 180MB (Optional)
                intent.putExtra(PickerConfig.MAX_SELECT_COUNT, 1);  //default 40 (Optional)
                intent.putExtra(PickerConfig.DEFAULT_SELECTED_LIST, mSelectMediaBean); // (Optional)
                startActivityForResult(intent, SELECT_MEDIA_REQUEST_CODE);
            }
        });

        mAppCompatButtonSend.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (USER_INFO_SP.getBoolean("STATE", false)) {
                    if (mEditTextNeiRon.getText().toString().length() >= 100) {

                        OKFeedBackApi.Params params = new OKFeedBackApi.Params();
                        params.setUsername(USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, "Anonymous"));
                        params.setMessage(mEditTextNeiRon.getText().toString());
                        params.setImage(mFilePath);

                        showProgressDialog("正在提交信息...");

                        if (mOKFeedBackApi != null) {
                            mOKFeedBackApi.cancelTask();
                        }
                        mOKFeedBackApi = new OKFeedBackApi(OKFeedBackActivity.this);
                        mOKFeedBackApi.requestFeedBack(params, OKFeedBackActivity.this);
                    } else {
                        showSnackBar(v, "反馈意见必须大于100字符", "");
                    }
                } else {
                    startUserActivity(null, OKLoginActivity.class);
                }
            }
        });

        mImageViewClear.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mFilePath = "";
                GlideApi(mImageViewAddTuPian, R.drawable.add_image_black, R.drawable.add_image_black, R.drawable.add_image_black);
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
        mToolbarTitle.setVisibility(View.VISIBLE);
        mToolbarTitle.setText("意见反馈");
        mAppCompatButtonSend = (AppCompatButton) findViewById(R.id.Feedback_TiJiaoBtn);
        mImageViewAddTuPian = (OKSEImageView) findViewById(R.id.Feedback_input_imag);
        mImageViewClear = (OKSEImageView) findViewById(R.id.Feedback_clear_imag);
        mEditTextNeiRon = (EditText) findViewById(R.id.Feedback_input_text);
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
            showSnackBar(mAppCompatButtonSend, "您不能选择动图", "");
            return;
        }
        mFilePath = imageItems.get(0).path;
        GlideApi(mImageViewAddTuPian, mFilePath, R.drawable.add_image_black, R.drawable.add_image_black);
    }

    @Override
    public void feedBackApiComplete(OKServiceResult<Object> result) {

        closeProgressDialog();

        if (result == null || !result.isSuccess()) {
            showSnackBar(mAppCompatButtonSend, "反馈失败,请检查网络", "");
            return;
        }

        showSnackBar(mAppCompatButtonSend, "反馈成功", "");
    }
}
