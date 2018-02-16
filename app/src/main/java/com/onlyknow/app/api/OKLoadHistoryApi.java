package com.onlyknow.app.api;

import android.content.Context;
import android.os.AsyncTask;

import com.onlyknow.app.database.OKDatabaseHelper;
import com.onlyknow.app.database.bean.OKCardBean;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Administrator on 2017/12/22.
 */

public class OKLoadHistoryApi extends OKBaseApi {
    private onCallBack mOnCallBack;
    private Context context;
    private LoadCardListTask mLoadCardListTask;

    public OKLoadHistoryApi(Context con) {
        this.context = con;
    }

    public interface onCallBack {
        void historyApiComplete(List<OKCardBean> list);
    }

    public void requestCardBeanList(boolean b, onCallBack mCallBack) {
        this.mOnCallBack = mCallBack;
        cancelTask();
        mLoadCardListTask = new LoadCardListTask();
        mLoadCardListTask.executeOnExecutor(exec, b);
    }

    public void cancelTask() {
        if (mLoadCardListTask != null && mLoadCardListTask.getStatus() == AsyncTask.Status.RUNNING) {
            mLoadCardListTask.cancel(true);
        }
    }

    private class LoadCardListTask extends AsyncTask<Boolean, Void, List<OKCardBean>> {

        @Override
        protected List<OKCardBean> doInBackground(Boolean... params) {
            if (isCancelled()) {
                return null;
            }

            return getDBIsReadCard(params[0]);
        }

        @Override
        protected void onPostExecute(List<OKCardBean> okCardBeen) {
            if (isCancelled()) {
                return;
            }
            super.onPostExecute(okCardBeen);

            mOnCallBack.historyApiComplete(okCardBeen);
        }
    }

    private List<OKCardBean> getDBIsReadCard(boolean b) {
        // 加载本地已阅读数据
        OKDatabaseHelper helper = OKDatabaseHelper.getHelper(context);
        try {
            List<OKCardBean> dbList = helper.getCardDao().queryBuilder().where().eq(OKCardBean.KEY_IS_READ, b).query();
            return dbList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
