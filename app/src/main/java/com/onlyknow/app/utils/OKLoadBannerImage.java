package com.onlyknow.app.utils;

import android.content.Context;
import android.widget.ImageView;

import com.onlyknow.app.GlideApp;
import com.onlyknow.app.R;
import com.onlyknow.app.db.bean.OKCarouselAdBean;
import com.youth.banner.loader.ImageLoader;

public class OKLoadBannerImage extends ImageLoader {
    private boolean isLink = false;

    public OKLoadBannerImage(boolean b) {
        this.isLink = b;
    }

    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        if (isLink) {
            try {
                OKCarouselAdBean.ADImage adImage = (OKCarouselAdBean.ADImage) path;// 广告轮播图片加载
                GlideApp.with(context).load(adImage.getUrl()).error(R.drawable.topgd2).placeholder(R.drawable.topgd2).into(imageView);
            } catch (Exception ex) {
                GlideApp.with(context).load(R.drawable.topgd2).into(imageView);
                ex.printStackTrace();
            }
        } else {
            GlideApp.with(context).load(path.toString()).error(R.drawable.topgd1).placeholder(R.drawable.topgd1).into(imageView);
        }
    }
}
