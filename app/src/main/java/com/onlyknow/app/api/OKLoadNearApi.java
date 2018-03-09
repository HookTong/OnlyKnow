package com.onlyknow.app.api;

import android.content.Context;
import android.os.AsyncTask;

import com.onlyknow.app.OKConstant;
import com.onlyknow.app.database.bean.OKCardBean;
import com.onlyknow.app.net.OKBusinessNet;

import java.util.List;
import java.util.Map;

/**
 * 附近界面数据源加载APi
 * <p>
 * Created by Administrator on 2017/12/22.
 */

public class OKLoadNearApi extends OKBaseApi {
    private onCallBack mOnCallBack;
    private Context context;
    private LoadCardListTask mLoadCardListTask;
    private boolean isLoadMore = false;

    public OKLoadNearApi(Context con) {
        this.context = con;
    }

    public interface onCallBack {
        void nearApiComplete(List<OKCardBean> list);
    }

    public void requestCardBeanList(Map<String, String> param, boolean isLoad, onCallBack mCallBack) {
        this.isLoadMore = isLoad;
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
            OKBusinessNet mOKBusinessNet = new OKBusinessNet();
            List<OKCardBean> nearCardList = mOKBusinessNet.getNearCard(params[0]);
            if (!isLoadMore) {
                return nearCardList;
            } else {
                return cardBeanListSorting(nearCardList);
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

        @Override
        protected void onPostExecute(List<OKCardBean> okCardBeen) {
            if (isCancelled()) {
                return;
            }

            super.onPostExecute(okCardBeen);
            mOnCallBack.nearApiComplete(okCardBeen);
        }
    }
}
