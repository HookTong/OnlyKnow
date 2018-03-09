package com.onlyknow.app.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.onlyknow.app.R;

import java.security.MessageDigest;

/**
 * Created by Administrator on 2018/3/9.
 */

public class OKAddVideoIconTransformation implements Transformation<Bitmap> {
    private Context mContext;
    private BitmapPool mBitmapPool;

    private OKAddVideoIconTransformation(Context context) {
        this.mContext = context;
        this.mBitmapPool = Glide.get(mContext).getBitmapPool();
    }

    private Bitmap addIcon(Bitmap toTransform) {
        if (toTransform == null) {
            return null;
        }

        Bitmap iconBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ok_video_play);
        if (iconBitmap == null) {
            return toTransform;
        }

        // 获取图片的宽高
        int srcWidth = toTransform.getWidth();
        int srcHeight = toTransform.getHeight();
        int iconWidth = iconBitmap.getWidth();
        int iconHeight = iconBitmap.getHeight();

        if (iconWidth == 0 || iconHeight == 0) {
            return toTransform;
        }

        // logo大小为二维码整体大小的1/5
        float scaleFactor = srcWidth * 1.0f / 5 / iconWidth;
        Bitmap bitmap = mBitmapPool.get(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        }
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(toTransform, 0, 0, null);
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
            canvas.drawBitmap(iconBitmap, (srcWidth - iconWidth) / 2, (srcHeight - iconHeight) / 2, null);

            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }

        return bitmap;
    }

    @NonNull
    @Override
    public Resource<Bitmap> transform(@NonNull Context context, @NonNull Resource<Bitmap> resource, int outWidth, int outHeight) {
        return BitmapResource.obtain(addIcon(resource.get()), mBitmapPool);
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
    }
}
