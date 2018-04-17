package com.onlyknow.app.api.card;

import android.content.Context;
import android.os.AsyncTask;

import com.onlyknow.app.api.OKBaseApi;
import com.onlyknow.app.database.bean.OKMeCommentCardBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户总评论界面数据源加载Api
 * <p>
 * Created by Reset on 2018/2/1.
 */

public class OKLoadMeCommentCardApi extends OKBaseApi {
    private onCallBack mOnCallBack;
    private Context context;
    private LoadMeCommentTask mLoadMeCommentTask;

    public OKLoadMeCommentCardApi(Context con) {
        this.context = con;
    }

    public interface onCallBack {
        void cardAndCommentApiComplete(List<OKMeCommentCardBean> list);
    }

    public void requestMeCommentCard(Params params, onCallBack mCallBack) {
        this.mOnCallBack = mCallBack;
        cancelTask();
        mLoadMeCommentTask = new LoadMeCommentTask();
        mLoadMeCommentTask.executeOnExecutor(exec, params);
    }

    public void cancelTask() {
        if (mLoadMeCommentTask != null && mLoadMeCommentTask.getStatus() == AsyncTask.Status.RUNNING) {
            mLoadMeCommentTask.cancel(true);
        }
    }

    private class LoadMeCommentTask extends AsyncTask<Params, Void, List<OKMeCommentCardBean>> {

        @Override
        protected List<OKMeCommentCardBean> doInBackground(Params... params) {
            if (isCancelled()) {
                return null;
            }

            Params mParams = params[0];

            Map<String, String> map = new HashMap<>();

            map.put(Params.KEY_NAME, mParams.getUsername());
            map.put(Params.KEY_PAGE, String.valueOf(mParams.getPage()));
            map.put(Params.KEY_SIZE, String.valueOf(mParams.getSize()));

            return getMeCommentCard(map);
        }

        @Override
        protected void onPostExecute(List<OKMeCommentCardBean> list) {
            if (isCancelled()) {
                return;
            }
            mOnCallBack.cardAndCommentApiComplete(list);
            super.onPostExecute(list);
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
