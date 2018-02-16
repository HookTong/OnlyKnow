package com.onlyknow.app.api;

import android.content.Context;
import android.os.AsyncTask;

import com.onlyknow.app.database.bean.OKCardBean;
import com.onlyknow.app.database.OKDatabaseHelper;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
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
            OKBusinessApi mOKBusinessApi = new OKBusinessApi();
            List<OKCardBean> mOKCardBeanList = mOKBusinessApi.getHotCard(params[0]);
            if (mOKCardBeanList != null) {
                OKDatabaseHelper helper = OKDatabaseHelper.getHelper(context);
                for (OKCardBean mCardBean : mOKCardBeanList) {
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
            return mOKCardBeanList;
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
