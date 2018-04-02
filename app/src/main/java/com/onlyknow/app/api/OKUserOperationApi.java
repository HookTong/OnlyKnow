package com.onlyknow.app.api;

import android.content.Context;
import android.os.AsyncTask;

import com.onlyknow.app.utils.OKNetUtil;

import java.util.Map;

public class OKUserOperationApi extends OKBaseApi {
    private Context context;
    private onCallBack mListener;
    private UserOperationTask mUserOperationTask;

    public final static String TYPE_ATTENTION = "ATTENTION";
    public final static String TYPE_WATCH = "WATCH";
    public final static String TYPE_ZAN = "ZAN";
    public final static String TYPE_SEND_COMMENT = "SEND_COMMENT";
    public final static String TYPE_SEND_COMMENT_REPLY = "SEND_COMMENT_REPLY";
    public final static String TYPE_REPORT = "REPORT";

    public OKUserOperationApi(Context cont) {
        this.context = cont;
    }

    public interface onCallBack {
        void userOperationApiComplete(boolean isSuccess, String type);
    }

    public void requestUserOperation(Map<String, String> map, String type, onCallBack listener) {
        this.mListener = listener;
        if (OKNetUtil.isNet(context)) {
            Params mParams = new Params();
            mParams.setReqMap(map);
            mParams.setType(type);
            cancelTask();
            mUserOperationTask = new UserOperationTask();
            mUserOperationTask.executeOnExecutor(exec, mParams);
        } else {
            mListener.userOperationApiComplete(false, type);
        }
    }

    public void cancelTask() {
        if (mUserOperationTask != null && mUserOperationTask.getStatus() == AsyncTask.Status.RUNNING) {
            mUserOperationTask.cancel(true);
        }
    }

    private class UserOperationTask extends AsyncTask<Params, Void, Boolean> {
        private String Type = "";

        @Override
        protected Boolean doInBackground(Params... params) {
            if (isCancelled()) {
                return false;
            }

            Params mParams = params[0];
            this.Type = mParams.getType();

            return new OKBusinessApi().updateCardInfo(mParams.getReqMap());
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (isCancelled()) {
                return;
            }

            mListener.userOperationApiComplete(aBoolean, this.Type);
        }
    }

    private class Params {
        Map<String, String> reqMap;
        String type;

        public Map<String, String> getReqMap() {
            return reqMap;
        }

        public void setReqMap(Map<String, String> reqMap) {
            this.reqMap = reqMap;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
