package com.onlyknow.app.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.onlyknow.app.R;
import com.onlyknow.app.ui.OKBaseActivity;
import com.onlyknow.app.ui.view.OKSEImageView;
import com.onlyknow.app.utils.OKBase64Util;
import com.onlyknow.app.utils.OKLogUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

public class OKQrCodeRecognitionActivity extends OKBaseActivity implements QRCodeView.Delegate {
    @Bind(R.id.ok_common_toolbar_back_image)
    OKSEImageView okCommonToolbarBackImage;
    @Bind(R.id.ok_common_toolbar_title_text)
    TextView okCommonToolbarTitleText;
    @Bind(R.id.ok_common_toolbar)
    Toolbar okCommonToolbar;
    @Bind(R.id.zxingview)
    ZXingView zxingview;
    @Bind(R.id.kaideng_image)
    OKSEImageView openDenImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ok_activity_qrcode_rec);
        ButterKnife.bind(this);
        initStatusBar();

        init();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (okCommonToolbar != null) {
            okCommonToolbar.setTitle("");
        }
    }

    private void init() {
        setSupportActionBar(okCommonToolbar);
        okCommonToolbarBackImage.setVisibility(View.VISIBLE);
        okCommonToolbarTitleText.setVisibility(View.VISIBLE);

        zxingview.setDelegate(this);
        okCommonToolbarTitleText.setText("二维码识别");

        okCommonToolbarBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        openDenImage.setTag(R.id.image_tag, 0);
        openDenImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = (int) openDenImage.getTag(R.id.image_tag);
                if (i == 0) {
                    zxingview.openFlashlight();
                    openDenImage.setTag(R.id.image_tag, 1);
                    openDenImage.setImageResource(R.drawable.ok_kaideng_off);
                } else if (i == 1) {
                    zxingview.closeFlashlight();
                    openDenImage.setTag(R.id.image_tag, 0);
                    openDenImage.setImageResource(R.drawable.ok_kaideng_on);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        zxingview.startSpotAndShowRect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        zxingview.stopCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        zxingview.onDestroy();
    }

    private void handleDecode(String result) {
        if (!TextUtils.isEmpty(result)) {
            if (result.startsWith(OKMeQrCodeActivity.QR_CODE_START_MSG)) {
                String userInfo = result.substring(result.indexOf(":") + 1);
                if (!TextUtils.isEmpty(userInfo)) {
                    String[] items = userInfo.split("&");
                    if (items.length == 2) {
                        String userNameBase64 = items[0];
                        String nickNameBase64 = items[1];

                        String userName = OKBase64Util.base64ToString(userNameBase64);
                        String nickName = OKBase64Util.base64ToString(nickNameBase64);

                        if (!TextUtils.isEmpty(userName)) {
                            Bundle bundle = new Bundle();
                            bundle.putString("USERNAME", userName);
                            bundle.putString("NICKNAME", nickName);
                            startUserActivity(bundle, OKHomePageActivity.class);
                            finish();
                            return;
                        }
                    }
                }
            }
            Bundle bundle = new Bundle();
            bundle.putString("RESULT", result);
            startUserActivity(bundle, OKQrCodeResultActivity.class);
            finish();
        } else {
            showSnackBar(okCommonToolbar, "没有识别到信息!", "");
        }
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        handleDecode(result);
        OKLogUtil.print("二维码识别结果!" + result);
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        OKLogUtil.print("二维码识别错误!");
    }
}
