package com.onlyknow.app.api;

import android.content.Context;
import android.os.AsyncTask;

import com.onlyknow.app.OKConstant;
import com.onlyknow.app.database.bean.OKCardBean;

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

    public OKLoadNearApi(Context con) {
        this.context = con;
    }

    public interface onCallBack {
        void nearApiComplete(List<OKCardBean> list);
    }

    public void requestCardBeanList(Map<String, String> param, List<OKCardBean> source, boolean isLoad, onCallBack mCallBack) {
        this.mOnCallBack = mCallBack;
        Params mParams = new Params();
        mParams.setReqMap(param);
        mParams.setSource(source);
        mParams.setLoadMore(isLoad);
        cancelTask();
        mLoadCardListTask = new LoadCardListTask();
        mLoadCardListTask.executeOnExecutor(exec, mParams);
    }

    public void cancelTask() {
        if (mLoadCardListTask != null && mLoadCardListTask.getStatus() == AsyncTask.Status.RUNNING) {
            mLoadCardListTask.cancel(true);
        }
    }

    private class LoadCardListTask extends AsyncTask<Params, Void, List<OKCardBean>> {

        @Override
        protected List<OKCardBean> doInBackground(Params... params) {
            if (isCancelled()) {
                return null;
            }
            Params mParams = params[0];
            OKBusinessApi mOKBusinessApi = new OKBusinessApi();
            List<OKCardBean> nearCardList = mOKBusinessApi.getNearCard(mParams.getReqMap());
            if (mParams.isLoadMore()) {
                return cardBeanListSorting(nearCardList, mParams.getSource());
            }
            return nearCardList;
        }

        private List<OKCardBean> cardBeanListSorting(List<OKCardBean> aims, List<OKCardBean> source) {
            if (source == null) {
                return aims;
            }
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

    private class Params {
        Map<String, String> reqMap;
        List<OKCardBean> source;
        boolean isLoadMore = false;

        public Map<String, String> getReqMap() {
            return reqMap;
        }

        public void setReqMap(Map<String, String> reqMap) {
            this.reqMap = reqMap;
        }

        public List<OKCardBean> getSource() {
            return source;
        }

        public void setSource(List<OKCardBean> source) {
            this.source = source;
        }

        public boolean isLoadMore() {
            return isLoadMore;
        }

        public void setLoadMore(boolean loadMore) {
            isLoadMore = loadMore;
        }
    }
}
