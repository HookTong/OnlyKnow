package com.onlyknow.app.api;

import android.content.Context;
import android.os.AsyncTask;

import com.onlyknow.app.utils.OKNetUtil;

import java.util.Map;

public class OKGoodsBuyApi extends OKBaseApi {
    private Context context;
    private onCallBack mListener;
    private GoodsBuyTask mGoodsBuyTask;

    public OKGoodsBuyApi(Context cont) {
        this.context = cont;
    }

    public interface onCallBack {
        void goodsBuyApiComplete(boolean isSuccess, int pos);
    }

    public void requestGoodsBuyApi(Map<String, String> map, int pos, onCallBack listener) {
        this.mListener = listener;
        if (OKNetUtil.isNet(context)) {
            Params mParams = new Params();
            mParams.setReqMap(map);
            mParams.setPos(pos);
            cancelTask();
            mGoodsBuyTask = new GoodsBuyTask();
            mGoodsBuyTask.executeOnExecutor(exec, mParams);
        } else {
            mListener.goodsBuyApiComplete(false, pos);
        }
    }

    public void cancelTask() {
        if (mGoodsBuyTask != null && mGoodsBuyTask.getStatus() == AsyncTask.Status.RUNNING) {
            mGoodsBuyTask.cancel(true);
        }
    }

    private class GoodsBuyTask extends AsyncTask<Params, Void, Boolean> {
        private int mPosition;

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (isCancelled()) {
                return;
            }

            mListener.goodsBuyApiComplete(aBoolean, mPosition);
        }

        @Override
        protected Boolean doInBackground(Params... params) {
            if (isCancelled()) {
                return false;
            }
            Params mParams = params[0];
            mPosition = mParams.getPos();

            OKBusinessApi mWebApi = new OKBusinessApi();
            return mWebApi.GoodsBuy(mParams.getReqMap());
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
