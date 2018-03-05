package com.onlyknow.app.api;

import android.content.Context;
import android.os.AsyncTask;

import com.onlyknow.app.database.bean.OKCardBean;
import com.onlyknow.app.database.OKDatabaseHelper;
import com.onlyknow.app.net.OKBusinessNet;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 热门界面数据源加载Api
 * <p>
 * Created by Administrator on 2017/12/23.
 */

public class OKLoadHotApi extends OKBaseApi {
    private onCallBack mOnCallBack;
    private Context context;
    private LoadCardListTask mLoadCardListTask;

    public OKLoadHotApi(Context con) {
        this.context = con;
    }

    public interface onCallBack {
        void hotApiComplete(List<OKCardBean> list);
    }

    public void requestCardBeanList(Map<String, String> param, onCallBack mCallBack) {
        this.mOnCallBack = mCallBack;
        cancelTask();
        mLoadCardListTask = new LoadCardListTask();
        mLoadCardListTask.executeOnExecutor(exec, param);
    }

    public void cancelTask() {
        if (mLoadCardListTask != null && mLoadCardListTask.getStatus() == AsyncTask.Status.RUNNING) {
            mLoadCardListTask.cancel(true);
        }
    }

    private class LoadCardListTask extends AsyncTask<Map<String, String>, Void, List<OKCardBean>> {

        @Override
        protected List<OKCardBean> doInBackground(Map<String, String>... params) {
            if (isCancelled()) {
                return null;
            }
            OKBusinessNet mOKBusinessNet = new OKBusinessNet();
            return mOKBusinessNet.getHotCard(params[0]);
        }

        @Override
        protected void onPostExecute(List<OKCardBean> mOKCardBeanList) {
            if (isCancelled()) {
                return;
            }
            mOnCallBack.hotApiComplete(mOKCardBeanList);
            super.onPostExecute(mOKCardBeanList);
        }
    }
}
