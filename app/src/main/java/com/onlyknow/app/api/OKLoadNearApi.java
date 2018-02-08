package com.onlyknow.app.api;

import android.content.Context;
import android.os.AsyncTask;

import com.onlyknow.app.OKConstant;
import com.onlyknow.app.database.bean.OKCardBean;
import com.onlyknow.app.database.OKDatabaseHelper;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/22.
 */

public class OKLoadNearApi extends OKBaseApi {
    private onCallBack mOnCallBack;
    private Context context;
    private LoadCardListTask mLoadCardListTask;

    public OKLoadNearApi(Context con) {
        this.context = con;
    }

    public interface onCallBack {
        public void cardList(List<OKCardBean> mOKCardBeanList);
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

            List<OKCardBean> nearCardList = mOKBusinessApi.getNearCard(params[0]);

            if (nearCardList != null) {
                OKDatabaseHelper helper = OKDatabaseHelper.getHelper(context);
                for (OKCardBean mCardBean : nearCardList) {
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
            return cardBeanListSorting(nearCardList);
        }

        @Override
        protected void onPostExecute(List<OKCardBean> okCardBeen) {
            if (isCancelled()) {
                return;
            }

            super.onPostExecute(okCardBeen);
            mOnCallBack.cardList(okCardBeen);
        }
    }

    private List<OKCardBean> cardBeanListSorting(List<OKCardBean> aims) {
        if (OKConstant.getListCache(INTERFACE_NEAR) == null) {
            return aims;
        }
        List<OKCardBean> source = OKConstant.getListCache(INTERFACE_NEAR);
        for (int i = 0; i < source.size(); i++) {
            OKCardBean sourceBean = source.get(i);
            for (int p = 0; p < aims.size(); p++) {
                OKCardBean aimsBean = aims.get(p);
                if (sourceBean.getCARD_ID() == aimsBean.getCARD_ID()) {
                    source.set(i, aimsBean);
                    aims.remove(p);
                    break;
                }
            }
        }
        return aims;
    }
}
