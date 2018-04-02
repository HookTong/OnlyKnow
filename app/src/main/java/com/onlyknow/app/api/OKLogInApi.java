package com.onlyknow.app.api;

import android.content.Context;
import android.os.AsyncTask;

import com.onlyknow.app.database.bean.OKUserInfoBean;
import com.onlyknow.app.utils.OKNetUtil;

import java.util.HashMap;
import java.util.Map;

public class OKLogInApi extends OKBaseApi {
    private Context context;
    private onCallBack mListener;
    private LoginTask mLoginTask;

    public OKLogInApi(Context cont) {
        this.context = cont;
    }

    public interface onCallBack {
        void logInApiComplete(OKUserInfoBean bean, String imName, String imPass);
    }

    public void requestLogIn(String username, String pass, onCallBack listener) {
        this.mListener = listener;
        if (OKNetUtil.isNet(context)) {
            Params mParams = new Params();
            mParams.setName(username);
            mParams.setPass(pass);
            cancelTask();
            mLoginTask = new LoginTask();
            mLoginTask.executeOnExecutor(exec, mParams);
        } else {
            mListener.logInApiComplete(null, username, pass);
        }
    }

    public void cancelTask() {
        if (mLoginTask != null && mLoginTask.getStatus() == AsyncTask.Status.RUNNING) {
            mLoginTask.cancel(true);
        }
    }

    private class LoginTask extends AsyncTask<Params, Void, OKUserInfoBean> {
        String imUserName, imPassWord;

        @Override
        protected OKUserInfoBean doInBackground(Params... params) {
            if (isCancelled()) {
                return null;
            }
            Params mParams = params[0];
            imUserName = mParams.getName();
            imPassWord = mParams.getPass();
            Map<String, String> map = new HashMap<String, String>();
            map.put("username", imUserName);
            map.put("password", imPassWord);
            return new OKBusinessApi().login(map);
        }

        @Override
        protected void onPostExecute(OKUserInfoBean mUserInfoBean) {
            super.onPostExecute(mUserInfoBean);
            if (isCancelled()) {
                return;
            }

            mListener.logInApiComplete(mUserInfoBean, imUserName, imPassWord);
        }
    }

    private class Params {
        String name;
        String pass;

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
