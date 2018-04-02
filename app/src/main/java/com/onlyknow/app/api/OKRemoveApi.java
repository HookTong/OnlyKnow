package com.onlyknow.app.api;

import android.content.Context;
import android.os.AsyncTask;

import com.onlyknow.app.OKConstant;
import com.onlyknow.app.ui.fragement.OKApproveFragment;
import com.onlyknow.app.utils.OKNetUtil;

import java.util.Map;

public class OKRemoveApi extends OKBaseApi {
    private Context context;
    private onCallBack mListener;
    private RemoveTask mRemoveTask;

    public OKRemoveApi(Context cont) {
        this.context = cont;
    }

    public interface onCallBack {
        void removeApiComplete(boolean isSuccess, int pos);
    }

    public void requestRemove(Map<String, String> reqMap, int pos, onCallBack listener) {
        this.mListener = listener;
        if (OKNetUtil.isNet(context)) {
            Params mParams = new Params();
            mParams.setReqMap(reqMap);
            mParams.setPos(pos);
            cancelTask();
            mRemoveTask = new RemoveTask();
            mRemoveTask.executeOnExecutor(exec, mParams);
        } else {
            mListener.removeApiComplete(false, pos);
        }
    }

    public void cancelTask() {
        if (mRemoveTask != null && mRemoveTask.getStatus() == AsyncTask.Status.RUNNING) {
            mRemoveTask.cancel(true);
        }
    }

    private class RemoveTask extends AsyncTask<Params, Void, Boolean> {
        private int mPosition;

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (isCancelled()) {
                return;
            }
            mListener.removeApiComplete(aBoolean, mPosition);
        }

        @Override
        protected Boolean doInBackground(Params... params) {
            if (isCancelled()) {
                return false;
            }
            Params mParams = params[0];
            mPosition = mParams.getPos();

            OKBusinessApi mWebApi = new OKBusinessApi();
            return mWebApi.RemoveCard(mParams.getReqMap());
        }
    }

    private class Params {
        Map<String, String> reqMap;
        int pos;

        public Map<String, String> getReqMap() {
            return reqMap;
        }

        public void setReqMap(Map<String, String> reqMap) {
            this.reqMap = reqMap;
        }

        public int getPos() {
            return pos;
        }

        public void setPos(int pos) {
            this.pos = pos;
        }
    }
}
