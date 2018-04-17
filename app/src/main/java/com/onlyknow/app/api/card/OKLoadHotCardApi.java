package com.onlyknow.app.api.card;

import android.content.Context;
import android.os.AsyncTask;

import com.onlyknow.app.api.OKBaseApi;
import com.onlyknow.app.database.bean.OKCardBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 热门界面数据源加载Api
 * <p>
 * Created by Administrator on 2017/12/23.
 */

public class OKLoadHotCardApi extends OKBaseApi {
    private onCallBack mOnCallBack;
    private Context context;
    private LoadHotTask mLoadHotTask;

    public OKLoadHotCardApi(Context con) {
        this.context = con;
    }

    public interface onCallBack {
        void hotApiComplete(List<OKCardBean> list);
    }

    public void requestHotCard(Params params, onCallBack mCallBack) {
        this.mOnCallBack = mCallBack;
        cancelTask();
        mLoadHotTask = new LoadHotTask();
        mLoadHotTask.executeOnExecutor(exec, params);
    }

    public void cancelTask() {
        if (mLoadHotTask != null && mLoadHotTask.getStatus() == AsyncTask.Status.RUNNING) {
            mLoadHotTask.cancel(true);
        }
    }

    private class LoadHotTask extends AsyncTask<Params, Void, List<OKCardBean>> {

        @Override
        protected List<OKCardBean> doInBackground(Params... params) {
            if (isCancelled()) {
                return null;
            }

            Params mParams = params[0];

            Map<String, String> map = new HashMap<>();

            map.put(Params.KEY_SIZE, String.valueOf(mParams.getSize()));

            return getHotCard(map);
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

    public static class Params {
        private int size;

        public final static String KEY_SIZE = "size";

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }
    }
}
