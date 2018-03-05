package com.onlyknow.app.api;

import android.content.Context;
import android.os.AsyncTask;

import com.onlyknow.app.database.OKDatabaseHelper;
import com.onlyknow.app.database.bean.OKCardBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 历史浏览界面数据源加载Api
 * <p>
 * Created by Administrator on 2017/12/22.
 */

public class OKLoadHistoryApi extends OKBaseApi {
    private onCallBack mOnCallBack;
    private Context context;
    private LoadCardListTask mLoadCardListTask;
    private boolean isLoadMore = false;

    public OKLoadHistoryApi(Context con) {
        this.context = con;
    }

    public interface onCallBack {
        void historyApiComplete(List<OKCardBean> list);
    }

    public void requestCardBeanList(boolean isLoad, long minTime, onCallBack mCallBack) {
        this.isLoadMore = isLoad;
        this.mOnCallBack = mCallBack;
        cancelTask();
        mLoadCardListTask = new LoadCardListTask();
        mLoadCardListTask.executeOnExecutor(exec, minTime);
    }

    public void cancelTask() {
        if (mLoadCardListTask != null && mLoadCardListTask.getStatus() == AsyncTask.Status.RUNNING) {
            mLoadCardListTask.cancel(true);
        }
    }

    private class LoadCardListTask extends AsyncTask<Long, Void, List<OKCardBean>> {

        @Override
        protected List<OKCardBean> doInBackground(Long... params) {
            if (isCancelled()) {
                return null;
            }
            return getDBIsReadCard(params[0]);
        }

        private List<OKCardBean> getDBIsReadCard(long minTime) {
            OKDatabaseHelper helper = OKDatabaseHelper.getHelper(context);
            try {
                List<OKCardBean> dbList = new ArrayList<>();
                if (isLoadMore) {
                    dbList = helper.getCardDao().queryBuilder().orderBy(OKCardBean.KEY_READ_DATE_LONG, false).limit(30L).where().eq(OKCardBean.KEY_IS_READ, true).and().lt(OKCardBean.KEY_READ_DATE_LONG, minTime).query();
                    return dbList;
                } else {
                    dbList = helper.getCardDao().queryBuilder().orderBy(OKCardBean.KEY_READ_DATE_LONG, false).limit(30L).where().eq(OKCardBean.KEY_IS_READ, true).query();
                    return dbList;
                }
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
            mOnCallBack.historyApiComplete(okCardBeen);
            super.onPostExecute(okCardBeen);
        }
    }
}
