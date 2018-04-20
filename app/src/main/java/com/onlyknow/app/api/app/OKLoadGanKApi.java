package com.onlyknow.app.api.app;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.onlyknow.app.api.OKBaseApi;
import com.onlyknow.app.db.bean.OKGanKBean;
import com.onlyknow.app.net.OKWebService;

import java.util.List;

/**
 * 干货营界面数据源加载Api
 * <p>
 * Created by Administrator on 2018/2/6.
 */

public class OKLoadGanKApi extends OKBaseApi {
    private onCallBack mOnCallBack;
    private Context context;
    private LoadGanKTask mLoadGanKTask;

    // 格式 http://gank.io/api/data/福利/20/2
    public final static String WELFARE_URL = "http://gank.io/api/data/福利/30/";
    public final static String ANDROID_URL = "http://gank.io/api/data/Android/30/";
    public final static String IOS_URL = "http://gank.io/api/data/iOS/30/";
    public final static String VIDEO_URL = "http://gank.io/api/data/休息视频/30/";
    public final static String RES_URL = "http://gank.io/api/data/拓展资源/30/";
    public final static String H5_URL = "http://gank.io/api/data/前端/30/";

    public OKLoadGanKApi(Context con) {
        this.context = con;
    }

    public interface onCallBack {
        void loadGanKComplete(List<OKGanKBean.Results> mOKCardBeanList);
    }

    public void requestGanK(String url, onCallBack mCallBack) {
        this.mOnCallBack = mCallBack;
        cancelTask();
        mLoadGanKTask = new LoadGanKTask();
        mLoadGanKTask.executeOnExecutor(exec, url);
    }

    public void cancelTask() {
        if (mLoadGanKTask != null && mLoadGanKTask.getStatus() == AsyncTask.Status.RUNNING) {
            mLoadGanKTask.cancel(true);
        }
    }

    private class LoadGanKTask extends AsyncTask<String, Void, List<OKGanKBean.Results>> {

        @Override
        protected List<OKGanKBean.Results> doInBackground(String... params) {
            if (isCancelled()) {
                return null;
            }
            OKGanKBean bean;
            String json = OKWebService.OKHttpApiGet(params[0]);
            try {
                bean = new Gson().fromJson(json, OKGanKBean.class);
                if (bean != null) {
                    return bean.getResults();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<OKGanKBean.Results> mOKCardBeanList) {
            if (isCancelled()) {
                return;
            }
            mOnCallBack.loadGanKComplete(mOKCardBeanList);
            super.onPostExecute(mOKCardBeanList);
        }
    }
}
