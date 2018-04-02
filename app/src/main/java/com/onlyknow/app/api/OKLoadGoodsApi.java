package com.onlyknow.app.api;

import android.content.Context;
import android.os.AsyncTask;

import com.onlyknow.app.database.bean.OKGoodsBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 商品界面数据源加载Api
 * <p>
 * Created by Administrator on 2017/12/23.
 */

public class OKLoadGoodsApi extends OKBaseApi {
    private onCallBack mOnCallBack;
    private Context context;
    private LoadEntryListTask mLoadEntryListTask;
    private boolean isLoadMore = false;

    public OKLoadGoodsApi(Context con, boolean isLm) {
        this.context = con;
        this.isLoadMore = isLm;
    }

    public interface onCallBack {
        void goodsApiComplete(List<OKGoodsBean> list);
    }

    public void requestCardBeanList(Map<String, String> param, boolean isLm, onCallBack mCallBack) {
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

    private class LoadEntryListTask extends AsyncTask<Map<String, String>, Void, List<OKGoodsBean>> {

        @Override
        protected List<OKGoodsBean> doInBackground(Map<String, String>... params) {
            if (isCancelled()) {
                return null;
            }
            OKBusinessApi mOKBusinessApi = new OKBusinessApi();
            List<OKGoodsBean> mOKGoodsBeanList = new ArrayList<>();
            if (isLoadMore) {
                mOKGoodsBeanList = mOKBusinessApi.loadMoreGoodsEntry(params[0]);
            } else {
                mOKGoodsBeanList = mOKBusinessApi.getGoodsEntry(params[0]);
            }
            return mOKGoodsBeanList;
        }

        @Override
        protected void onPostExecute(List<OKGoodsBean> mOKGoodsBeanList) {
            if (isCancelled()) {
                return;
            }
            mOnCallBack.goodsApiComplete(mOKGoodsBeanList);
            super.onPostExecute(mOKGoodsBeanList);
        }
    }
}
