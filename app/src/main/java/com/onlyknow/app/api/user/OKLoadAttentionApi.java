package com.onlyknow.app.api.user;

import android.content.Context;
import android.os.AsyncTask;

import com.onlyknow.app.api.OKBaseApi;
import com.onlyknow.app.database.bean.OKAttentionBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 关注界面数据源加载Api
 * <p>
 * Created by Administrator on 2017/12/22.
 */

public class OKLoadAttentionApi extends OKBaseApi {
    private onCallBack mOnCallBack;
    private Context context;
    private LoadAttentionTask mLoadAttentionTask;

    public OKLoadAttentionApi(Context con) {
        this.context = con;
    }

    public interface onCallBack {
        void attentionApiComplete(List<OKAttentionBean> list);
    }

    public void requestAttention(Params params, onCallBack mCallBack) {
        this.mOnCallBack = mCallBack;
        cancelTask();
        mLoadAttentionTask = new LoadAttentionTask();
        mLoadAttentionTask.executeOnExecutor(exec, params);
    }

    public void cancelTask() {
        if (mLoadAttentionTask != null && mLoadAttentionTask.getStatus() == AsyncTask.Status.RUNNING) {
            mLoadAttentionTask.cancel(true);
        }
    }

    private class LoadAttentionTask extends AsyncTask<Params, Void, List<OKAttentionBean>> {

        @Override
        protected List<OKAttentionBean> doInBackground(Params... params) {
            if (isCancelled()) {
                return null;
            }

            Params mParams = params[0];

            Map<String, String> map = new HashMap<>();

            map.put(Params.KEY_NAME, mParams.getUsername());
            map.put(Params.KEY_PAGE, String.valueOf(mParams.getPage()));
            map.put(Params.KEY_SIZE, String.valueOf(mParams.getSize()));

            return getAttentionEntry(map);
        }

        @Override
        protected void onPostExecute(List<OKAttentionBean> okAttentionBeenList) {
            if (isCancelled()) {
                return;
            }
            mOnCallBack.attentionApiComplete(okAttentionBeenList);
            super.onPostExecute(okAttentionBeenList);
        }
    }

    public static class Params {
        private String username;
        private int page;
        private int size;

        public final static String KEY_NAME = "username";
        public final static String KEY_PAGE = "page";
        public final static String KEY_SIZE = "size";

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
    }
}
