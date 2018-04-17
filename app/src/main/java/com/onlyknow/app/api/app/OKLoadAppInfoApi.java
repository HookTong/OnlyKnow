package com.onlyknow.app.api.app;

import android.content.Context;
import android.os.AsyncTask;

import com.onlyknow.app.api.OKBaseApi;
import com.onlyknow.app.database.bean.OKAppInfoBean;
import com.onlyknow.app.utils.OKNetUtil;

import java.util.HashMap;
import java.util.Map;

public class OKLoadAppInfoApi extends OKBaseApi {
    private Context context;
    private onCallBack mListener;
    private LoadAppInfoTask mLoadAppInfoTask;

    public OKLoadAppInfoApi(Context cont) {
        this.context = cont;
    }

    public interface onCallBack {
        void appInfoApiComplete(OKAppInfoBean bean);
    }

    public void requestAppInfo(Params params, onCallBack listener) {
        this.mListener = listener;
        if (OKNetUtil.isNet(context)) {
            cancelTask();
            mLoadAppInfoTask = new LoadAppInfoTask();
            mLoadAppInfoTask.executeOnExecutor(exec, params);
        } else {
            mListener.appInfoApiComplete(null);
        }
    }

    public void cancelTask() {
        if (mLoadAppInfoTask != null && mLoadAppInfoTask.getStatus() == AsyncTask.Status.RUNNING) {
            mLoadAppInfoTask.cancel(true);
        }
    }

    private class LoadAppInfoTask extends AsyncTask<Params, Void, OKAppInfoBean> {
        @Override
        protected OKAppInfoBean doInBackground(Params... params) {
            if (isCancelled()) {
                return null;
            }

            Params mParams = params[0];
            Map<String, String> map = new HashMap<>();
            map.put(Params.KEY_TYPE, Params.TYPE_CHECK);
            map.put(Params.KEY_VERSION, mParams.getVersion());

            return getAppInfo(map);
        }

        @Override
        protected void onPostExecute(final OKAppInfoBean bean) {
            super.onPostExecute(bean);
            if (isCancelled()) {
                return;
            }
            mListener.appInfoApiComplete(bean);
        }
    }

    public static class Params {
        private String version;
        private String type;

        public final static String KEY_VERSION = "version";
        public final static String KEY_TYPE = "type";

        public final static String TYPE_CHECK = "check";

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
