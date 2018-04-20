package com.onlyknow.app.api.card;

import android.content.Context;
import android.os.AsyncTask;

import com.onlyknow.app.api.OKBaseApi;
import com.onlyknow.app.db.OKDatabaseHelper;
import com.onlyknow.app.db.bean.OKCardBean;
import com.onlyknow.app.utils.OKNetUtil;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 探索界面数据源加载Api
 * <p>
 * Created by Administrator on 2017/12/22.
 */

public class OKLoadExploreCardApi extends OKBaseApi {
    private onCallBack mOnCallBack;
    private Context context;
    private LoadExploreTask mLoadExploreTask;

    public OKLoadExploreCardApi(Context con) {
        this.context = con;
    }

    public interface onCallBack {
        void loadExploreComplete(List<OKCardBean> list);
    }

    public void requestExploreCard(Params params, onCallBack mCallBack) {
        this.mOnCallBack = mCallBack;
        cancelTask();
        mLoadExploreTask = new LoadExploreTask();
        mLoadExploreTask.executeOnExecutor(exec, params);
    }

    public void cancelTask() {
        if (mLoadExploreTask != null && mLoadExploreTask.getStatus() == AsyncTask.Status.RUNNING) {
            mLoadExploreTask.cancel(true);
        }
    }

    private class LoadExploreTask extends AsyncTask<Params, Void, List<OKCardBean>> {

        @Override
        protected List<OKCardBean> doInBackground(Params... params) {
            if (isCancelled()) {
                return null;
            }

            Params mParams = params[0];

            List<OKCardBean> list = null;

            if (OKNetUtil.isNet(context)) {

                Map<String, String> map = new HashMap<>();

                map.put(Params.KEY_PAGE, String.valueOf(mParams.getPage()));

                map.put(Params.KEY_SIZE, String.valueOf(mParams.getSize()));

                list = getExploreCard(map);

            } else if (mParams.getPage() == 1) {

                list = getDBCard(mParams.getSize());

            }
            return list;
        }

        private List<OKCardBean> getDBCard(long size) {
            // 加载本地数据 随机加载num条数据
            OKDatabaseHelper helper = OKDatabaseHelper.getHelper(context);
            try {
                return helper.getCardDao().queryBuilder().limit(size).query();
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<OKCardBean> okCardBeen) {
            if (isCancelled()) {
                return;
            }

            super.onPostExecute(okCardBeen);
            mOnCallBack.loadExploreComplete(okCardBeen);
        }
    }

    public static class Params {
        private int page;
        private int size;

        public final static String KEY_PAGE = "page";
        public final static String KEY_SIZE = "size";

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
