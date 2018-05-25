package com.onlyknow.app.api.app;

import android.content.Context;
import android.os.AsyncTask;

import com.onlyknow.app.api.OKBaseApi;
import com.onlyknow.app.db.bean.OKCarouselAdBean;
import com.onlyknow.app.utils.OKLogUtil;
import com.onlyknow.app.utils.OKNetUtil;

import java.util.HashMap;
import java.util.Map;

public class OKLoadCarouselAdApi extends OKBaseApi {
    private Context context;
    private onCallBack mListener;
    private LoadCarouselAdTask mLoadCarouselAdTask;

    public OKLoadCarouselAdApi(Context cont) {
        this.context = cont;
    }

    public interface onCallBack {
        void loadCarouselAdComplete(OKCarouselAdBean bean);
    }

    public void requestCarouselAd(Params params, onCallBack listener) {
        this.mListener = listener;
        if (OKNetUtil.isNet(context)) {
            cancelTask();
            mLoadCarouselAdTask = new LoadCarouselAdTask();
            mLoadCarouselAdTask.executeOnExecutor(exec, params);
        } else {
            mListener.loadCarouselAdComplete(null);
        }
    }

    public void cancelTask() {
        if (mLoadCarouselAdTask != null && mLoadCarouselAdTask.getStatus() == AsyncTask.Status.RUNNING) {
            mLoadCarouselAdTask.cancel(true);
        }
    }

    private class LoadCarouselAdTask extends AsyncTask<Params, Void, OKCarouselAdBean> {

        @Override
        protected OKCarouselAdBean doInBackground(Params... params) {
            if (isCancelled()) {
                return null;
            }

            Params mParams = params[0];
            mParams.setType(Params.TYPE_NEW);
            Map<String, String> map = new HashMap<>();
            map.put(Params.KEY_TYPE, mParams.getType());

            OKCarouselAdBean bean = getCarouselAd(map);

            if (bean != null) {
                bean.getCarouselImages();
                bean.getAdImages();
            }

            return bean;
        }

        @Override
        protected void onPostExecute(OKCarouselAdBean bean) {
            super.onPostExecute(bean);
            if (isCancelled()) {
                OKLogUtil.print("获取轮播图片和广告图片的线程意外结束");
                return;
            }

            mListener.loadCarouselAdComplete(bean);
        }
    }

    public static class Params {

        private String type;

        public final static String KEY_TYPE = "type";

        public final static String TYPE_NEW = "new";

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
