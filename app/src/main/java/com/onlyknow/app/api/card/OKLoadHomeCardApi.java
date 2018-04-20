package com.onlyknow.app.api.card;

import android.content.Context;
import android.os.AsyncTask;

import com.onlyknow.app.api.OKBaseApi;
import com.onlyknow.app.db.bean.OKCardBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户主页数据源加载Api
 * <p>
 * Created by Administrator on 2017/12/23.
 */

public class OKLoadHomeCardApi extends OKBaseApi {
    private onCallBack mOnCallBack;
    private Context context;
    private LoadHomeTask mLoadHomeTask;

    public OKLoadHomeCardApi(Context con) {
        this.context = con;
    }

    public interface onCallBack {
        void loadHomeComplete(List<OKCardBean> list);
    }

    public void requestHomeCard(Params params, onCallBack mCallBack) {
        this.mOnCallBack = mCallBack;
        cancelTask();
        mLoadHomeTask = new LoadHomeTask();
        mLoadHomeTask.executeOnExecutor(exec, params);
    }

    public void cancelTask() {
        if (mLoadHomeTask != null && mLoadHomeTask.getStatus() == AsyncTask.Status.RUNNING) {
            mLoadHomeTask.cancel(true);
        }
    }

    private class LoadHomeTask extends AsyncTask<Params, Void, List<OKCardBean>> {

        @Override
        protected List<OKCardBean> doInBackground(Params... params) {
            if (isCancelled()) {
                return null;
            }
            Params mParams = params[0];

            Map<String, String> map = new HashMap<>();
            map.put(Params.KEY_NAME, mParams.getUsername());
            map.put(Params.KEY_PAGE, String.valueOf(mParams.getPage()));
            map.put(Params.KEY_SIZE, String.valueOf(mParams.getSize()));
            map.put(Params.KEY_IS_APPROVE_IN, Boolean.toString(mParams.isApproveIn()));

            return getUserCard(map);
        }

        @Override
        protected void onPostExecute(List<OKCardBean> mOKCardBeanList) {
            if (isCancelled()) {
                return;
            }
            mOnCallBack.loadHomeComplete(mOKCardBeanList);
            super.onPostExecute(mOKCardBeanList);
        }
    }

    public static class Params {
        private String username;
        private int page;
        private int size;
        private boolean isApproveIn;

        public final static String KEY_NAME = "username";
        public final static String KEY_PAGE = "page";
        public final static String KEY_SIZE = "size";
        public final static String KEY_IS_APPROVE_IN = "isApproveIn";

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

        public boolean isApproveIn() {
            return isApproveIn;
        }

        public void setApproveIn(boolean approveIn) {
            isApproveIn = approveIn;
        }
    }
}
