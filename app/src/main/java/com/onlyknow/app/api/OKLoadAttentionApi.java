package com.onlyknow.app.api;

import android.content.Context;
import android.os.AsyncTask;

import com.onlyknow.app.database.bean.OKAttentionBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 关注界面数据源加载Api
 * <p>
 * Created by Administrator on 2017/12/22.
 */

public class OKLoadAttentionApi extends OKBaseApi {
    private onCallBack mOnCallBack;
    private Context context;
    private LoadEntryListTask mLoadEntryListTask;
    private boolean isLoadMore = false;

    public OKLoadAttentionApi(Context con, boolean isLm) {
        this.context = con;
        this.isLoadMore = isLm;
    }

    public interface onCallBack {
        void attentionApiComplete(List<OKAttentionBean> list);
    }

    public void requestEntryBeanList(Map<String, String> param, boolean isLm, onCallBack mCallBack) {
        this.isLoadMore = isLm;
        this.mOnCallBack = mCallBack;
        cancelTask();
        mLoadEntryListTask = new LoadEntryListTask();
        mLoadEntryListTask.executeOnExecutor(exec, param);
    }

    public void cancelTask() {
        if (mLoadEntryListTask != null && mLoadEntryListTask.getStatus() == AsyncTask.Status.RUNNING) {
            mLoadEntryListTask.cancel(true);
        }
    }

    private class LoadEntryListTask extends AsyncTask<Map<String, String>, Void, List<OKAttentionBean>> {

        @Override
        protected List<OKAttentionBean> doInBackground(Map<String, String>... params) {
            if (isCancelled()) {
                return null;
            }
            OKBusinessApi mOKBusinessApi = new OKBusinessApi();
            List<OKAttentionBean> attentionEntryList = new ArrayList<>();
            if (isLoadMore) {
                attentionEntryList = mOKBusinessApi.loadMoreAttentionEntry(params[0]);
            } else {
                attentionEntryList = mOKBusinessApi.getAttentionEntry(params[0]);
            }
            return attentionEntryList;
        }

        @Override
        protected void onPostExecute(List<OKAttentionBean> okAttentionBeenList) {
            if (isCancelled()) {
                return;
            }
            mOnCallBack.attentionApiComplete(okAttentionBeenList);
            super.onPostExecute(okAttentionBeenList);
        }
    }
}
