package com.onlyknow.app.api.comment;

import android.content.Context;
import android.os.AsyncTask;

import com.onlyknow.app.api.OKBaseApi;
import com.onlyknow.app.api.OKServiceResult;
import com.onlyknow.app.utils.OKNetUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OKAddCommentApi extends OKBaseApi {
    private Context context;
    private onCallBack mListener;
    private AddCommentTask mAddCommentTask;

    public OKAddCommentApi(Context cont) {
        this.context = cont;
    }

    public interface onCallBack {
        void addCommentApiComplete(OKServiceResult<Object> result, String type);
    }

    public void requestAddComment(Params params, onCallBack listener) {
        this.mListener = listener;
        if (OKNetUtil.isNet(context)) {
            cancelTask();
            mAddCommentTask = new AddCommentTask();
            mAddCommentTask.executeOnExecutor(exec, params);
        } else {
            OKServiceResult<Object> serviceResult = new OKServiceResult<>();
            serviceResult.setSuccess(false);
            serviceResult.setCode(20001);
            serviceResult.setData("");
            serviceResult.setMsg("没有网络连接");
            serviceResult.setTime(new Date().getTime());

            mListener.addCommentApiComplete(serviceResult, params.getType());
        }
    }

    public void cancelTask() {
        if (mAddCommentTask != null && mAddCommentTask.getStatus() == AsyncTask.Status.RUNNING) {
            mAddCommentTask.cancel(true);
        }
    }

    private class AddCommentTask extends AsyncTask<Params, Void, OKServiceResult<Object>> {
        private String mType = "";

        @Override
        protected OKServiceResult<Object> doInBackground(Params... params) {
            if (isCancelled()) {
                return null;
            }

            Params mParams = params[0];
            this.mType = mParams.getType();
            Map<String, String> map = new HashMap<>();
            map.put(Params.KEY_NAME, mParams.getUsername());
            map.put(Params.KEY_ID, String.valueOf(mParams.getId()));
            map.put(Params.KEY_TYPE, mParams.getType());
            map.put(Params.KEY_MSG, mParams.getMessage());

            return addCommentOrReply(map);
        }

        @Override
        protected void onPostExecute(OKServiceResult<Object> result) {
            super.onPostExecute(result);
            if (isCancelled()) {
                return;
            }

            mListener.addCommentApiComplete(result, this.mType);
        }
    }

    public static class Params {
        private String type;
        private String username;
        private int id;
        private String message;

        public final static String KEY_TYPE = "type";
        public final static String KEY_NAME = "username";
        public final static String KEY_ID = "id";
        public final static String KEY_MSG = "message";

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

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
