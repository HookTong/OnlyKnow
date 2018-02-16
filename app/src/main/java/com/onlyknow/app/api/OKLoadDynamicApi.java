package com.onlyknow.app.api;

import android.content.Context;
import android.os.AsyncTask;

import com.onlyknow.app.database.OKDatabaseHelper;
import com.onlyknow.app.database.bean.OKCardBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/22.
 */

public class OKLoadDynamicApi extends OKBaseApi {
    private onCallBack mOnCallBack;
    private Context context;
    private LoadCardListTask mLoadCardListTask;
    private boolean isLoadMore = false;

    public OKLoadDynamicApi(Context con, boolean isLm) {
        this.context = con;
        this.isLoadMore = isLm;
    }

    public interface onCallBack {
        void dynamicApiComplete(List<OKCardBean> list);
    }

    public void requestCardBeanList(Map<String, String> param, boolean isLm, onCallBack mCallBack) {
        this.isLoadMore = isLm;
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

            OKBusinessApi mOKBusinessApi = new OKBusinessApi();

            List<OKCardBean> dynamicCardList = new ArrayList<>();
            if (isLoadMore) {
                dynamicCardList = mOKBusinessApi.loadMoreUserCard(params[0]);
            } else {
                dynamicCardList = mOKBusinessApi.getUserCard(params[0]);
            }

            if (dynamicCardList != null) {
                OKDatabaseHelper helper = OKDatabaseHelper.getHelper(context);
                for (OKCardBean mCardBean : dynamicCardList) {
                    try {
                        OKCardBean dbBean = helper.getCardDao().queryForId(mCardBean.getCARD_ID());
                        if (dbBean != null) {
                            mCardBean.setIS_READ(dbBean.IS_READ());
                            mCardBean.setREAD_DATE(dbBean.getREAD_DATE());
                            helper.getCardDao().createOrUpdate(mCardBean);
                        } else {
                            helper.getCardDao().create(mCardBean);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }

            return dynamicCardList;
        }

        @Override
        protected void onPostExecute(List<OKCardBean> okCardBeen) {
            if (isCancelled()) {
                return;
            }

            super.onPostExecute(okCardBeen);

            mOnCallBack.dynamicApiComplete(okCardBeen);
        }
    }
}
