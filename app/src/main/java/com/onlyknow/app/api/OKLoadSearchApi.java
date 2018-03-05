package com.onlyknow.app.api;

import android.content.Context;
import android.os.AsyncTask;

import com.onlyknow.app.database.bean.OKSearchBean;
import com.onlyknow.app.net.OKBusinessNet;

import java.util.List;
import java.util.Map;

/**
 * 搜索界面数据源加载Api
 * <p>
 * Created by Administrator on 2018/2/2.
 */

public class OKLoadSearchApi extends OKBaseApi {
    private onCallBack mOnCallBack;
    private Context context;
    private LoadSearchBeanListTask mLoadSearchBeanListTask;

    public OKLoadSearchApi(Context con) {
        this.context = con;
    }

    public interface onCallBack {
        void searchApiComplete(List<OKSearchBean> list);
    }

    public void requestSearchBeanList(Map<String, String> param, onCallBack mCallBack) {
        this.mOnCallBack = mCallBack;
        cancelTask();
        mLoadSearchBeanListTask = new LoadSearchBeanListTask();
        mLoadSearchBeanListTask.executeOnExecutor(exec, param);
    }

    public void cancelTask() {
        if (mLoadSearchBeanListTask != null && mLoadSearchBeanListTask.getStatus() == AsyncTask.Status.RUNNING) {
            mLoadSearchBeanListTask.cancel(true);
        }
    }

    private class LoadSearchBeanListTask extends AsyncTask<Map<String, String>, Void, List<OKSearchBean>> {

        @Override
        protected List<OKSearchBean> doInBackground(Map<String, String>... params) {
            if (isCancelled()) {
                return null;
            }
            OKBusinessNet mOKBusinessNet = new OKBusinessNet();
            return mOKBusinessNet.getSearchBean(params[0]);
        }

        @Override
        protected void onPostExecute(List<OKSearchBean> list) {
            if (isCancelled()) {
                return;
            }
            mOnCallBack.searchApiComplete(list);
            super.onPostExecute(list);
        }
    }
}
