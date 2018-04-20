package com.onlyknow.app.api.comment;

import android.content.Context;
import android.os.AsyncTask;

import com.onlyknow.app.api.OKBaseApi;
import com.onlyknow.app.api.OKServiceResult;
import com.onlyknow.app.db.bean.OKCommentBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 卡片评论界面数据源加载Api
 * <p>
 * Created by Administrator on 2017/12/23.
 */

public class OKLoadCommentApi extends OKBaseApi {
    private onCallBack mOnCallBack;
    private Context context;
    private LoadCommentTask mLoadCommentTask;

    public OKLoadCommentApi(Context con) {
        this.context = con;
    }

    public interface onCallBack {
        void loadCommentComplete(List<OKCommentBean> list);
    }

    public void requestComment(Params params, onCallBack mCallBack) {
        this.mOnCallBack = mCallBack;
        cancelTask();
        mLoadCommentTask = new LoadCommentTask();
        mLoadCommentTask.executeOnExecutor(exec, params);
    }

    public void cancelTask() {
        if (mLoadCommentTask != null && mLoadCommentTask.getStatus() == AsyncTask.Status.RUNNING) {
            mLoadCommentTask.cancel(true);
        }
    }

    private class LoadCommentTask extends AsyncTask<Params, Void, List<OKCommentBean>> {

        @Override
        protected List<OKCommentBean> doInBackground(Params... params) {
            if (isCancelled()) {
                return null;
            }

            Params mParams = params[0];

            Map<String, String> map = new HashMap<>();
            map.put(Params.KEY_TYPE, Params.TYPE_COMMENT);
            map.put(Params.KEY_NAME, mParams.getUsername());
            map.put(Params.KEY_ID, String.valueOf(mParams.getId()));
            map.put(Params.KEY_PAGE, String.valueOf(mParams.getPage()));
            map.put(Params.KEY_SIZE, String.valueOf(mParams.getSize()));

            OKServiceResult<List<OKCommentBean>> serviceResult = getCommentOrReply(map, OKCommentBean.class);

            if (serviceResult == null || !serviceResult.isSuccess()) return null;

            return serviceResult.getData();
        }

        @Override
        protected void onPostExecute(List<OKCommentBean> result) {
            if (isCancelled()) {
                return;
            }
            mOnCallBack.loadCommentComplete(result);
            super.onPostExecute(result);
        }
    }

    public static class Params {
        private String type;
        private String username;
        private int id;
        private int page;
        private int size;

        public final static String KEY_TYPE = "type";
        public final static String KEY_NAME = "username";
        public final static String KEY_ID = "id";
        public final static String KEY_PAGE = "page";
        public final static String KEY_SIZE = "size";

        public final static String TYPE_COMMENT = "comment";
        public final static String TYPE_COMMENT_REPLY = "commentReply";

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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
