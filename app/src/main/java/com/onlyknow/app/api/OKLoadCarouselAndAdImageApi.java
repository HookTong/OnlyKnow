package com.onlyknow.app.api;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.onlyknow.app.OKConstant;
import com.onlyknow.app.R;
import com.onlyknow.app.database.bean.OKCarouselAndAdImageBean;
import com.onlyknow.app.database.bean.OKSafetyInfoBean;
import com.onlyknow.app.service.OKMainService;
import com.onlyknow.app.utils.OKDeviceInfoUtil;
import com.onlyknow.app.utils.OKLogUtil;
import com.onlyknow.app.utils.OKNetUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OKLoadCarouselAndAdImageApi extends OKBaseApi {
    private Context context;
    private onCallBack mListener;
    private LoadCarouselAndAdImageTask mLoadCarouselAndAdImageTask;

    public OKLoadCarouselAndAdImageApi(Context cont) {
        this.context = cont;
    }

    public interface onCallBack {
        void carouselAndAdImageApiComplete(OKCarouselAndAdImageBean bean);
    }

    public void requestCarouselAndAdImage(onCallBack listener) {
        this.mListener = listener;
        if (OKNetUtil.isNet(context)) {
            cancelTask();
            mLoadCarouselAndAdImageTask = new LoadCarouselAndAdImageTask();
            mLoadCarouselAndAdImageTask.executeOnExecutor(exec);
        } else {
            mListener.carouselAndAdImageApiComplete(null);
        }
    }

    public void cancelTask() {
        if (mLoadCarouselAndAdImageTask != null && mLoadCarouselAndAdImageTask.getStatus() == AsyncTask.Status.RUNNING) {
            mLoadCarouselAndAdImageTask.cancel(true);
        }
    }

    private class LoadCarouselAndAdImageTask extends AsyncTask<Void, Void, OKCarouselAndAdImageBean> {

        @Override
        protected OKCarouselAndAdImageBean doInBackground(Void... params) {
            if (isCancelled()) {
                return null;
            }
            return new OKBusinessApi().getOKCarouselAndAdImageBean(new OKDeviceInfoUtil(context).getIMIE());
        }

        @Override
        protected void onPostExecute(OKCarouselAndAdImageBean bean) {
            super.onPostExecute(bean);
            if (isCancelled()) {
                OKLogUtil.print("获取轮播图片和广告图片的线程意外结束");
                return;
            }

            mListener.carouselAndAdImageApiComplete(bean);
        }
    }
}
