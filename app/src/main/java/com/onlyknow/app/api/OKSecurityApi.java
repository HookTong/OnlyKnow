package com.onlyknow.app.api;

import android.content.Context;
import android.os.AsyncTask;

import com.onlyknow.app.OKConstant;
import com.onlyknow.app.database.bean.OKCardBase64ListBean;
import com.onlyknow.app.database.bean.OKCardBean;
import com.onlyknow.app.database.bean.OKSafetyInfoBean;
import com.onlyknow.app.utils.OKLogUtil;
import com.onlyknow.app.utils.OKNetUtil;

import java.util.Map;

public class OKSecurityApi extends OKBaseApi {
    private Context context;
    private onCallBack mListener;
    private SecurityTask mSecurityTask;

    public OKSecurityApi(Context cont) {
        this.context = cont;
    }

    public interface onCallBack {
        void securityApiComplete(OKSafetyInfoBean bean);
    }

    public void requestSecurityCheck(Map<String, String> reqMap, onCallBack listener) {
        this.mListener = listener;
        if (OKNetUtil.isNet(context)) {
            cancelTask();
            mSecurityTask = new SecurityTask();
            mSecurityTask.executeOnExecutor(exec, reqMap);
        } else {
            mListener.securityApiComplete(null);
        }
    }

    public void cancelTask() {
        if (mSecurityTask != null && mSecurityTask.getStatus() == AsyncTask.Status.RUNNING) {
            mSecurityTask.cancel(true);
        }
    }

    // app 版本检查
    private class SecurityTask extends AsyncTask<Map<String, String>, Void, OKSafetyInfoBean> {
        @Override
        protected OKSafetyInfoBean doInBackground(Map<String, String>... params) {
            if (isCancelled()) {
                return null;
            }
            return new OKBusinessApi().securityCheck(params[0]);
        }

        @Override
        protected void onPostExecute(final OKSafetyInfoBean bean) {
            super.onPostExecute(bean);
            if (isCancelled()) {
                return;
            }
            mListener.securityApiComplete(bean);
        }
    }
}
