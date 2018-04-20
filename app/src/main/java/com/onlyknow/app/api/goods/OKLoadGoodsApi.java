package com.onlyknow.app.api.goods;

import android.content.Context;
import android.os.AsyncTask;

import com.onlyknow.app.api.OKBaseApi;
import com.onlyknow.app.db.bean.OKGoodsBean;

import java.util.HashMap;
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
    private LoadGoodsTask mLoadGoodsTask;

    public OKLoadGoodsApi(Context con) {
        this.context = con;
    }

    public interface onCallBack {
        void loadGoodsComplete(List<OKGoodsBean> list);
    }

    public void requestGoods(Params params, onCallBack mCallBack) {
        this.mOnCallBack = mCallBack;
        cancelTask();
        mLoadGoodsTask = new LoadGoodsTask();
        mLoadGoodsTask.executeOnExecutor(exec, params);
    }

    public void cancelTask() {
        if (mLoadGoodsTask != null && mLoadGoodsTask.getStatus() == AsyncTask.Status.RUNNING) {
            mLoadGoodsTask.cancel(true);
        }
    }

    private class LoadGoodsTask extends AsyncTask<Params, Void, List<OKGoodsBean>> {

        @Override
        protected List<OKGoodsBean> doInBackground(Params... params) {
            if (isCancelled()) {
                return null;
            }

            Params mParams = params[0];

            Map<String, String> map = new HashMap<>();
            map.put(Params.KEY_NAME, mParams.getUsername());
            map.put(Params.KEY_PAGE, String.valueOf(mParams.getPage()));
            map.put(Params.KEY_SIZE, String.valueOf(mParams.getSize()));

            return getGoodsEntry(map);
        }

        @Override
        protected void onPostExecute(List<OKGoodsBean> mOKGoodsBeanList) {
            if (isCancelled()) {
                return;
            }
            mOnCallBack.loadGoodsComplete(mOKGoodsBeanList);
            super.onPostExecute(mOKGoodsBeanList);
        }
    }

    public static class Params {
        private String username;
        private int page;
        private int size;

        public final static String KEY_NAME = "username";
        public final static String KEY_PAGE = "page";
        public final static String KEY_SIZE = "size";

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }
    }
}
