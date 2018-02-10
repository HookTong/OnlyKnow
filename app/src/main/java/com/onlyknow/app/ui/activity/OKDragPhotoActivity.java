package com.onlyknow.app.ui.activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.onlyknow.app.GlideApp;
import com.onlyknow.app.OKConstant;
import com.onlyknow.app.R;
import com.onlyknow.app.net.OKWebService;
import com.onlyknow.app.ui.OKBaseActivity;
import com.onlyknow.app.ui.view.OKDragPhotoView;
import com.onlyknow.app.ui.view.OKProgressButton;
import com.onlyknow.app.ui.view.OKSEImageView;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Request;

/**
 * 图片查看界面,提供动画效果
 * <p>
 * Created by Administrator on 2018/1/31.
 */

public class OKDragPhotoActivity extends OKBaseActivity {
    @Bind(R.id.ok_activity_drag_photo)
    OKDragPhotoView okActivityDragPhoto;

    @Bind(R.id.ok_activity_drag_photo_down)
    OKSEImageView okActivityDragPhotoDown;

    int mOriginLeft;
    int mOriginTop;
    int mOriginHeight;
    int mOriginWidth;
    int mOriginCenterX;
    int mOriginCenterY;
    private float mTargetHeight;
    private float mTargetWidth;
    private float mScaleX;
    private float mScaleY;
    private float mTranslationX;
    private float mTranslationY;

    private OKWebService mWebService;

    private Bundle mBundle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.ok_activity_drag_photo);
        ButterKnife.bind(this);
        mBundle = getIntent().getExtras();
        if (mBundle == null || TextUtils.isEmpty(mBundle.getString("url", ""))) {
            showSnackbar(okActivityDragPhotoDown, "没有的url地址", "");
            finish();
        }
        init();
    }

    private void init() {
        GlideApp.with(this).load(mBundle.getString("url", "")).placeholder(R.drawable.topgd1).listener(new GlideRequest()).into(okActivityDragPhoto);
        okActivityDragPhotoDown.setTag(R.id.downButton, OKProgressButton.NORMAL);
        if (isFileExists(mBundle.getString("url", ""))) {
            okActivityDragPhotoDown.setEnabled(false);
        } else {
            okActivityDragPhotoDown.setEnabled(true);
        }

        okActivityDragPhoto.setOnTapListener(new OKDragPhotoView.OnTapListener() {
            @Override
            public void onTap(OKDragPhotoView view) {
                finishWithAnimation(view);
            }
        });

        okActivityDragPhoto.setOnExitListener(new OKDragPhotoView.OnExitListener() {
            @Override
            public void onExit(OKDragPhotoView view, float x, float y, float w, float h) {
                performExitAnimation(view, x, y, w, h);
            }
        });

        okActivityDragPhotoDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((int) okActivityDragPhotoDown.getTag(R.id.downButton) == OKProgressButton.DOWNLOADING) {
                    showSnackbar(view, "您当前正在下载该图片", "");
                    return;
                }
                String filePath = OKConstant.IMAGE_PATH;
                mWebService = OKWebService.getInstance();
                mWebService.downloadFile(mBundle.getString("url", ""), filePath, new DownloadCallback());
                okActivityDragPhotoDown.setTag(R.id.downButton, OKProgressButton.DOWNLOADING);
                showProgressDialog("正在下载中...");
            }
        });
    }

    @Override
    public void onBackPressed() {
        finishWithAnimation(okActivityDragPhoto);
    }

    @Override
    protected void onDestroy() {
        if ((int) okActivityDragPhotoDown.getTag(R.id.downButton) == OKProgressButton.DOWNLOADING) {
            if (mWebService != null) {
                mWebService.cancelDown();
            }
            Toast.makeText(this, "下载已取消", Toast.LENGTH_SHORT).show();
        }
        super.onDestroy();
    }

    private void performExitAnimation(final OKDragPhotoView view, float x, float y, float w, float h) {
        view.finishAnimationCallBack();
        float viewX = mTargetWidth / 2 + x - mTargetWidth * mScaleX / 2;
        float viewY = mTargetHeight / 2 + y - mTargetHeight * mScaleY / 2;
        view.setX(viewX);
        view.setY(viewY);

        float centerX = view.getX() + mOriginWidth / 2;
        float centerY = view.getY() + mOriginHeight / 2;

        float translateX = mOriginCenterX - centerX;
        float translateY = mOriginCenterY - centerY;

        ValueAnimator translateXAnimator = ValueAnimator.ofFloat(view.getX(), view.getX() + translateX);
        translateXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.setX((Float) valueAnimator.getAnimatedValue());
            }
        });
        translateXAnimator.setDuration(300);
        translateXAnimator.start();
        ValueAnimator translateYAnimator = ValueAnimator.ofFloat(view.getY(), view.getY() + translateY);
        translateYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.setY((Float) valueAnimator.getAnimatedValue());
            }
        });
        translateYAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                animator.removeAllListeners();
                finish();
                overridePendingTransition(0, 0);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        translateYAnimator.setDuration(300);
        translateYAnimator.start();
    }

    private void finishWithAnimation(final OKDragPhotoView view) {
        ValueAnimator translateXAnimator = ValueAnimator.ofFloat(0, mTranslationX);
        translateXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.setX((Float) valueAnimator.getAnimatedValue());
            }
        });
        translateXAnimator.setDuration(300);
        translateXAnimator.start();

        ValueAnimator translateYAnimator = ValueAnimator.ofFloat(0, mTranslationY);
        translateYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.setY((Float) valueAnimator.getAnimatedValue());
            }
        });
        translateYAnimator.setDuration(300);
        translateYAnimator.start();

        ValueAnimator scaleYAnimator = ValueAnimator.ofFloat(1, mScaleY);
        scaleYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.setScaleY((Float) valueAnimator.getAnimatedValue());
            }
        });
        scaleYAnimator.setDuration(300);
        scaleYAnimator.start();

        ValueAnimator scaleXAnimator = ValueAnimator.ofFloat(1, mScaleX);
        scaleXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.setScaleX((Float) valueAnimator.getAnimatedValue());
            }
        });

        scaleXAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                animator.removeAllListeners();
                finish();
                overridePendingTransition(0, 0);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        scaleXAnimator.setDuration(300);
        scaleXAnimator.start();
    }

    private void performEnterAnimation(final OKDragPhotoView view) {
        ValueAnimator translateXAnimator = ValueAnimator.ofFloat(view.getX(), 0);
        translateXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.setX((Float) valueAnimator.getAnimatedValue());
            }
        });
        translateXAnimator.setDuration(300);
        translateXAnimator.start();

        ValueAnimator translateYAnimator = ValueAnimator.ofFloat(view.getY(), 0);
        translateYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.setY((Float) valueAnimator.getAnimatedValue());
            }
        });
        translateYAnimator.setDuration(300);
        translateYAnimator.start();

        ValueAnimator scaleYAnimator = ValueAnimator.ofFloat(mScaleY, 1);
        scaleYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.setScaleY((Float) valueAnimator.getAnimatedValue());
            }
        });
        scaleYAnimator.setDuration(300);
        scaleYAnimator.start();

        ValueAnimator scaleXAnimator = ValueAnimator.ofFloat(mScaleX, 1);
        scaleXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.setScaleX((Float) valueAnimator.getAnimatedValue());
            }
        });
        scaleXAnimator.setDuration(300);
        scaleXAnimator.start();
    }

    private boolean isFileExists(String url) {
        File file = new File(OKConstant.IMAGE_PATH, getFileName(url));
        if (file.exists()) {
            return true;
        }
        return false;
    }

    private String getFileName(String url) {
        String ss[] = url.split("/");
        return ss[ss.length - 1];
    }

    private class GlideRequest implements RequestListener {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
            showSnackbar(okActivityDragPhoto, "图片加载失败", "");
            return false;
        }

        @Override
        public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
            mOriginLeft = mBundle.getInt("left", 0);
            mOriginTop = mBundle.getInt("top", 0);
            mOriginHeight = mBundle.getInt("height", 0);
            mOriginWidth = mBundle.getInt("width", 0);
            mOriginCenterX = mOriginLeft + mOriginWidth / 2;
            mOriginCenterY = mOriginTop + mOriginHeight / 2;

            int[] location = new int[2];
            okActivityDragPhoto.getLocationOnScreen(location);

            mTargetHeight = (float) okActivityDragPhoto.getHeight();
            mTargetWidth = (float) okActivityDragPhoto.getWidth();
            mScaleX = (float) mOriginWidth / mTargetWidth;
            mScaleY = (float) mOriginHeight / mTargetHeight;

            float targetCenterX = location[0] + mTargetWidth / 2;
            float targetCenterY = location[1] + mTargetHeight / 2;

            mTranslationX = mOriginCenterX - targetCenterX;
            mTranslationY = mOriginCenterY - targetCenterY;
            okActivityDragPhoto.setTranslationX(mTranslationX);
            okActivityDragPhoto.setTranslationY(mTranslationY);

            okActivityDragPhoto.setScaleX(mScaleX);
            okActivityDragPhoto.setScaleY(mScaleY);

            performEnterAnimation(okActivityDragPhoto);

            okActivityDragPhoto.setMinScale(mScaleX);

            return false;
        }
    }

    // 下载回调类
    private class DownloadCallback extends OKWebService.ResultCallback {
        @Override
        public void onError(Request request, Exception e) {
            closeProgressDialog();
            okActivityDragPhotoDown.setTag(R.id.downButton, OKProgressButton.NORMAL);
            okActivityDragPhotoDown.setEnabled(true);
            showSnackbar(okActivityDragPhotoDown, "下载失败", "");
        }

        @Override
        public void onResponse(Object response) {
            closeProgressDialog();
            okActivityDragPhotoDown.setTag(R.id.downButton, OKProgressButton.NORMAL);
            okActivityDragPhotoDown.setEnabled(false);
            showSnackbar(okActivityDragPhotoDown, "下载完成,您可以到 " + OKConstant.IMAGE_PATH + " 下查看", "");
        }

        @Override
        public void onProgress(double total, double current) {
        }
    }
}
