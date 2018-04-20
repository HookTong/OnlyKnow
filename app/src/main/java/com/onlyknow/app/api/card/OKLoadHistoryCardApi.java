package com.onlyknow.app.api.card;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.stmt.QueryBuilder;
import com.onlyknow.app.api.OKBaseApi;
import com.onlyknow.app.db.OKDatabaseHelper;
import com.onlyknow.app.db.bean.OKCardBean;

import java.sql.SQLException;
import java.util.List;

/**
 * 历史浏览界面数据源加载Api
 * <p>
 * Created by Administrator on 2017/12/22.
 */

public class OKLoadHistoryCardApi extends OKBaseApi {
    private onCallBack mOnCallBack;
    private Context context;
    private LoadHistoryTask mLoadHistoryTask;

    public OKLoadHistoryCardApi(Context con) {
        this.context = con;
    }

    public interface onCallBack {
        void loadHistoryComplete(List<OKCardBean> list);
    }

    public void requestHistoryCard(Params params, onCallBack mCallBack) {
        this.mOnCallBack = mCallBack;
        cancelTask();
        mLoadHistoryTask = new LoadHistoryTask();
        mLoadHistoryTask.executeOnExecutor(exec, params);
    }

    public void cancelTask() {
        if (mLoadHistoryTask != null && mLoadHistoryTask.getStatus() == AsyncTask.Status.RUNNING) {
            mLoadHistoryTask.cancel(true);
        }
    }

    private class LoadHistoryTask extends AsyncTask<Params, Void, List<OKCardBean>> {

        @Override
        protected List<OKCardBean> doInBackground(Params... params) {
            if (isCancelled()) {
                return null;
            }

            Params mParams = params[0];

            long page = mParams.getPage();

            long size = mParams.getSize();

            OKDatabaseHelper helper = OKDatabaseHelper.getHelper(context);

            List<OKCardBean> dbList = null;

            try {

                QueryBuilder<OKCardBean, Integer> query = helper.getCardDao().queryBuilder();

                dbList = query.orderBy(OKCardBean.KEY_READ_TIME, false).offset((page * size) - size).limit(size).where().eq(OKCardBean.KEY_IS_READ, true).query();

                return dbList;

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
            mOnCallBack.loadHistoryComplete(okCardBeen);
            super.onPostExecute(okCardBeen);
        }
    }

    public static class Params {

        private int page;

        private int size;

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
