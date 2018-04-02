package com.onlyknow.app.api;

import android.content.Context;
import android.os.AsyncTask;

import com.onlyknow.app.database.bean.OKUserInfoBean;
import com.onlyknow.app.utils.OKNetUtil;

import java.util.Map;

public class OKLoadUserInfoApi extends OKBaseApi {
    private Context context;
    private onCallBack mListener;
    private LoadUserTask mLoadUserTask;

    public OKLoadUserInfoApi(Context cont) {
        this.context = cont;
    }

    public interface onCallBack {
        void userInfoApiComplete(OKUserInfoBean bean);
    }

    public void requestUserInfo(Map<String, String> map, onCallBack listener) {
        this.mListener = listener;
        if (OKNetUtil.isNet(context)) {
            cancelTask();
            mLoadUserTask = new LoadUserTask();
            mLoadUserTask.executeOnExecutor(exec, map);
        } else {
            mListener.userInfoApiComplete(null);
        }
    }

    public void cancelTask() {
        if (mLoadUserTask != null && mLoadUserTask.getStatus() == AsyncTask.Status.RUNNING) {
            mLoadUserTask.cancel(true);
        }
    }

    private class LoadUserTask extends AsyncTask<Map<String, String>, Void, OKUserInfoBean> {

        @Override
        protected void onPostExecute(OKUserInfoBean userInfoBean) {
            if (isCancelled()) {
                return;
            }

            mListener.userInfoApiComplete(userInfoBean);
        }

        @Override
        protected OKUserInfoBean doInBackground(Map<String, String>... params) {
            if (isCancelled()) {
                return null;
            }

            return new OKBusinessApi().getUserInfo(params[0]);
        }
    }
}
