package com.onlyknow.app.ui.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.onlyknow.app.GlideApp;
import com.onlyknow.app.OKConstant;
import com.onlyknow.app.R;
import com.onlyknow.app.database.bean.OKUserInfoBean;
import com.onlyknow.app.ui.OKBaseActivity;
import com.onlyknow.app.ui.view.OKCircleImageView;
import com.onlyknow.app.utils.OKBase64Util;
import com.onlyknow.app.utils.OKQRUtil;


/*
 * 该类用于生成包含用户信息的二维码
 * 需要传入的值包括用户名,昵称,签名
 * */
public class OKMeQrCodeActivity extends OKBaseActivity {
    private ImageView imageViewQrCode;
    private OKCircleImageView circleImageViewTouXian;
    private TextView textViewNiChen, textViewQianMin;
    private Button buttonSave;
    private Bundle bundleMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ok_activity_qrcode);
        initSystemBar(this);
        bundleMe = this.getIntent().getExtras();
        findView();
        init();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mToolbar != null) {
            mToolbar.setTitle("");
        }
    }

    private void init() {
        textViewNiChen.setText(bundleMe.getString(OKUserInfoBean.KEY_NICKNAME));

        String qm = bundleMe.getString(OKUserInfoBean.KEY_QIANMIN);
        if (!TextUtils.isEmpty(qm) && !qm.equals("NULL")) {
            textViewQianMin.setText(bundleMe.getString(OKUserInfoBean.KEY_QIANMIN));
        } else {
            textViewQianMin.setText("这个人很懒 , 什么都没有留下!");
        }

        String url = bundleMe.getString(OKUserInfoBean.KEY_HEADPORTRAIT_URL, "");
        GlideRoundApi(circleImageViewTouXian, url, R.drawable.touxian_placeholder, R.drawable.touxian_placeholder);

        buttonSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewQrCode.buildDrawingCache();
                OKBase64Util.saveBitmap(imageViewQrCode.getDrawingCache(), OKConstant.IMAGE_PATH,
                        bundleMe.getString(OKUserInfoBean.KEY_USERNAME) + "_qrcode.jpg");
                showSnackbar(v, "二维码保存成功,您可以到 " + OKConstant.IMAGE_PATH + " 文件夹下查看", "");
            }
        });

        mToolbarBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String content = "WeiZhiUSER=" + bundleMe.getString(OKUserInfoBean.KEY_USERNAME) + "&" + bundleMe.getString(OKUserInfoBean.KEY_NICKNAME);
        initQeCode(null, content);

        GlideApp.with(this).asBitmap().load(url).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                String content = "WeiZhiUSER=" + bundleMe.getString(OKUserInfoBean.KEY_USERNAME) + "&" + bundleMe.getString(OKUserInfoBean.KEY_NICKNAME);
                initQeCode(resource, content);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void initQeCode(Bitmap mBitmapTx, String str) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int w = outMetrics.widthPixels * 8 / 11;// 设置宽度
        ViewGroup.LayoutParams layoutParams = imageViewQrCode.getLayoutParams();
        layoutParams.height = layoutParams.width = w;// 设置高度
        imageViewQrCode.setLayoutParams(layoutParams);

        try {
            Bitmap bitmap = OKQRUtil.encodeToQRWidth(str, w);
            if (mBitmapTx != null) {
                mBitmapTx = OKBase64Util.toRoundBitmap(mBitmapTx);
                bitmap = OKQRUtil.addLogo(bitmap, mBitmapTx);
                // mBitmapTx.recycle();
            }
            imageViewQrCode.setImageBitmap(bitmap);
            // bitmap.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void findView() {
        super.findCommonToolbarView(this);
        mToolbarBack.setVisibility(View.VISIBLE);
        mToolbarTitle.setVisibility(View.VISIBLE);

        mToolbarTitle.setText("我的二维码");

        imageViewQrCode = (ImageView) findViewById(R.id.me_qrcode);
        circleImageViewTouXian = (OKCircleImageView) findViewById(R.id.me_qrcode_touxian);
        textViewNiChen = (TextView) findViewById(R.id.me_qrcode_nicheng);
        textViewQianMin = (TextView) findViewById(R.id.me_qrcode_qianmin);
        buttonSave = (Button) findViewById(R.id.me_qrcode_baocun);
    }
}