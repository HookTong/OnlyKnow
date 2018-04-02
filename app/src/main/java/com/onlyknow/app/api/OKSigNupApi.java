package com.onlyknow.app.api;

import android.content.Context;
import android.os.AsyncTask;

import com.onlyknow.app.database.bean.OKSignupResultBean;
import com.onlyknow.app.utils.OKNetUtil;

import java.util.Map;

public class OKSigNupApi extends OKBaseApi {
    private Context context;
    private onCallBack mListener;
    private SigNupTask mSigNupTask;

    public OKSigNupApi(Context cont) {
        this.context = cont;
    }

    public interface onCallBack {
        void sigNupApiComplete(OKSignupResultBean bean, String imName, String imPass);
    }

    public void requestSigNup(Map<String, String> map, String name, String pass, onCallBack listener) {
        this.mListener = listener;
        if (OKNetUtil.isNet(context)) {
            Params mParams = new Params();
            mParams.setReqMap(map);
            mParams.setName(name);
            mParams.setPass(pass);
            cancelTask();
            mSigNupTask = new SigNupTask();
            mSigNupTask.executeOnExecutor(exec, mParams);
        } else {
            mListener.sigNupApiComplete(null, name, pass);
        }
    }

    public void cancelTask() {
        if (mSigNupTask != null && mSigNupTask.getStatus() == AsyncTask.Status.RUNNING) {
            mSigNupTask.cancel(true);
        }
    }

    private class SigNupTask extends AsyncTask<Params, Void, OKSignupResultBean> {
        private String imUserName, imPassWord;

        @Override
        protected OKSignupResultBean doInBackground(Params... params) {
            if (isCancelled()) {
                return null;
            }
            Params mParams = params[0];

            imUserName = mParams.getName();
            imPassWord = mParams.getPass();

            return new OKBusinessApi().registerUser(mParams.getReqMap());
        }

        @Override
        protected void onPostExecute(OKSignupResultBean mSignupResultBean) {
            super.onPostExecute(mSignupResultBean);
            if (isCancelled()) {
                return;
            }

            mListener.sigNupApiComplete(mSignupResultBean, imUserName, imPassWord);
        }
    }

    private class Params {
        Map<String, String> reqMap;
        String name;
        String pass;

        public Map<String, String> getReqMap() {
            return reqMap;
        }

        public void setReqMap(Map<String, String> reqMap) {
            this.reqMap = reqMap;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPass() {
            return pass;
        }

        public void setPass(String pass) {
            this.pass = pass;
        }
    }
}
