package com.onlyknow.app.api;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.onlyknow.app.database.bean.OKGanKBean;
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
    private LoadGanKListTask mLoadGanKListTask;

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
        void ganKioApiComplete(List<OKGanKBean.Results> mOKCardBeanList);
    }

    public void requestGanKBeanList(String url, onCallBack mCallBack) {
        this.mOnCallBack = mCallBack;
        cancelTask();
        mLoadGanKListTask = new LoadGanKListTask();
        mLoadGanKListTask.executeOnExecutor(exec, url);
    }

    public void cancelTask() {
        if (mLoadGanKListTask != null && mLoadGanKListTask.getStatus() == AsyncTask.Status.RUNNING) {
            mLoadGanKListTask.cancel(true);
        }
    }

    private class LoadGanKListTask extends AsyncTask<String, Void, List<OKGanKBean.Results>> {

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
            mOnCallBack.ganKioApiComplete(mOKCardBeanList);
            super.onPostExecute(mOKCardBeanList);
        }
    }
}
