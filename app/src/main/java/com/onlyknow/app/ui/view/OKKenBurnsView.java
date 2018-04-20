package com.onlyknow.app.ui.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.onlyknow.app.GlideApp;
import com.onlyknow.app.R;
import com.onlyknow.app.db.bean.OKCarouselAdBean;
import com.onlyknow.app.utils.OKLogUtil;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 图片轮播控件
 * <p>
 * Created by f.laurent on 21/11/13.
 */
public class OKKenBurnsView extends FrameLayout {
    private static final String TAG = "KenBurnsView";

    private Context context;
    private final Handler mHandler;
    private int[] mResourceIds;
    private List<Map<String, Object>> mapArrayList;
    private ImageView[] mImageViews;
    private int mActiveImageIndex = -1;

    private final Random random = new Random();
    private int mSwapMs = 10000;
    private int mFadeInOutMs = 400;

    private float maxScaleFactor = 1.5F;
    private float minScaleFactor = 1.2F;

    private Runnable mSwapImageRunnable = new Runnable() {
        @Override
        public void run() {
            swapImage();
            mHandler.postDelayed(mSwapImageRunnable, mSwapMs - mFadeInOutMs * 2);
        }
    };

    public OKKenBurnsView(Context context) {
        this(context, null);
    }

    public OKKenBurnsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OKKenBurnsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mHandler = new Handler();
    }

    public void setResourceIds(Context context, int... resourceIds) {
        this.context = context;
        this.mResourceIds = resourceIds;
        fillImageViewsResIds();
    }

    private void fillImageViewsResIds() {
        for (int i = 0; i < mResourceIds.length; i++) {
            GlideApi(mImageViews[i], mResourceIds[i]);
        }
    }

    public void setUrl(Context context, List<Map<String, Object>> UrlList) {
        this.context = context;
        this.mapArrayList = UrlList;
        fillImageViewsUrls();
    }

    private void fillImageViewsUrls() {
        for (int i = 0; i < mapArrayList.size(); i++) {
            Map<String, Object> map = this.mapArrayList.get(i);
            String url = map.get(OKCarouselAdBean.KEY_URL).toString();
            int placeholderId = Integer.parseInt(map.get(OKCarouselAdBean.KEY_RID).toString());
            GlideApi(mImageViews[i], url, placeholderId);
        }
    }

    public void GlideApi(final ImageView mView, String url, final int placeholderId) {
        GlideApp.with(this.context).load(url).centerCrop().placeholder(placeholderId).error(placeholderId).into(mView);
    }

    public void GlideApi(final ImageView mView, final int id) {
        GlideApp.with(this.context).load(id).centerCrop().into(mView);
    }

    private void swapImage() {
        OKLogUtil.print(TAG, "swapImage active=" + mActiveImageIndex);
        if (mActiveImageIndex == -1) {
            mActiveImageIndex = 1;
            animate(mImageViews[mActiveImageIndex]);
            return;
        }

        int inactiveIndex = mActiveImageIndex;
        mActiveImageIndex = (1 + mActiveImageIndex) % mImageViews.length;
        OKLogUtil.print(TAG, "new active=" + mActiveImageIndex);

        final ImageView activeImageView = mImageViews[mActiveImageIndex];
        activeImageView.setAlpha(0.0f);
        ImageView inactiveImageView = mImageViews[inactiveIndex];

        animate(activeImageView);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(mFadeInOutMs);
        animatorSet.playTogether(ObjectAnimator.ofFloat(inactiveImageView, "alpha", 1.0f, 0.0f), ObjectAnimator.ofFloat(activeImageView, "alpha", 0.0f, 1.0f));
        animatorSet.start();
    }

    private void start(View view, long duration, float fromScale, float toScale, float fromTranslationX, float fromTranslationY, float toTranslationX, float toTranslationY) {
        view.setScaleX(fromScale);
        view.setScaleY(fromScale);
        view.setTranslationX(fromTranslationX);
        view.setTranslationY(fromTranslationY);
        ViewPropertyAnimator propertyAnimator = view.animate().translationX(toTranslationX).translationY(toTranslationY)
                .scaleX(toScale).scaleY(toScale).setDuration(duration);
        propertyAnimator.start();
        OKLogUtil.print(TAG, "starting Ken Burns animation " + propertyAnimator);
    }

    private float pickScale() {
        return this.minScaleFactor + this.random.nextFloat() * (this.maxScaleFactor - this.minScaleFactor);
    }

    private float pickTranslation(int value, float ratio) {
        return value * (ratio - 1.0f) * (this.random.nextFloat() - 0.5f);
    }

    public void animate(View view) {
        float fromScale = pickScale();
        float toScale = pickScale();
        float fromTranslationX = pickTranslation(view.getWidth(), fromScale);
        float fromTranslationY = pickTranslation(view.getHeight(), fromScale);
        float toTranslationX = pickTranslation(view.getWidth(), toScale);
        float toTranslationY = pickTranslation(view.getHeight(), toScale);
        start(view, this.mSwapMs, fromScale, toScale, fromTranslationX, fromTranslationY, toTranslationX,
                toTranslationY);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startKenBurnsAnimation();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeCallbacks(mSwapImageRunnable);
    }

    private void startKenBurnsAnimation() {
        mHandler.post(mSwapImageRunnable);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View view = inflate(getContext(), R.layout.ok_kenburns_view, this);
        mImageViews = new ImageView[5];
        mImageViews[0] = (ImageView) view.findViewById(R.id.image0);
        mImageViews[1] = (ImageView) view.findViewById(R.id.image1);
        mImageViews[2] = (ImageView) view.findViewById(R.id.image2);
        mImageViews[3] = (ImageView) view.findViewById(R.id.image3);
        mImageViews[4] = (ImageView) view.findViewById(R.id.image4);
    }
}
