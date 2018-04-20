package com.onlyknow.app.api.comment;

import android.content.Context;
import android.os.AsyncTask;

import com.onlyknow.app.api.OKBaseApi;
import com.onlyknow.app.api.OKServiceResult;
import com.onlyknow.app.utils.OKNetUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 评论管理接口 (评论相关操作请调用此接口).
 * <p>
 * 评论及评论回复点赞,评论及评论回复移除!
 * <p>
 * Created by Reset on 2018/04/16.
 */

public class OKManagerCommentApi extends OKBaseApi {
    private Context context;
    private onCallBack mListener;
    private ManagerCommentTask mManagerCommentTask;

    public OKManagerCommentApi(Context cont) {
        this.context = cont;
    }

    public interface onCallBack {
        void managerCommentComplete(OKServiceResult<Object> result, String type, int pos);
    }

    public void requestManagerComment(Params params, onCallBack listener) {
        this.mListener = listener;
        if (OKNetUtil.isNet(context)) {
            cancelTask();
            mManagerCommentTask = new ManagerCommentTask();
            mManagerCommentTask.executeOnExecutor(exec, params);
        } else {
            OKServiceResult<Object> serviceResult = new OKServiceResult<>();
            serviceResult.setSuccess(false);
            serviceResult.setCode(20001);
            serviceResult.setData("");
            serviceResult.setMsg("没有网络连接");
            serviceResult.setTime(new Date().getTime());
            mListener.managerCommentComplete(serviceResult, params.getType(), params.getPos());
        }
    }

    public void cancelTask() {
        if (mManagerCommentTask != null && mManagerCommentTask.getStatus() == AsyncTask.Status.RUNNING) {
            mManagerCommentTask.cancel(true);
        }
    }

    private class ManagerCommentTask extends AsyncTask<Params, Void, OKServiceResult<Object>> {
        private int mPosition;
        private String mType;

        @Override
        protected void onPostExecute(OKServiceResult<Object> result) {
            super.onPostExecute(result);
            if (isCancelled()) {
                return;
            }

            mListener.managerCommentComplete(result, mType, mPosition);
        }

        @Override
        protected OKServiceResult<Object> doInBackground(Params... params) {
            if (isCancelled()) {
                return null;
            }
            Params mParams = params[0];
            mPosition = mParams.getPos();
            mType = mParams.getType();

            Map<String, String> map = new HashMap<>();
            map.put(Params.KEY_TYPE, mParams.getType());
            map.put(Params.KEY_NAME, mParams.getUsername());
            map.put(Params.KEY_PASS, mParams.getPassword());
            map.put(Params.KEY_ID, String.valueOf(mParams.getId()));

            return managerComment(map, Object.class);
        }
    }

    public static class Params {
        private String type;
        private int id;
        private String username;
        private String password;

        private int pos;

        public final static String KEY_TYPE = "type";
        public final static String KEY_ID = "id";
        public final static String KEY_NAME = "username";
        public final static String KEY_PASS = "password";

        public final static String TYPE_PRAISE_COMMENT = "praiseComment";
        public final static String TYPE_PRAISE_COMMENT_REPLY = "praiseCommentReply";
        public final static String TYPE_REMOVE_COMMENT = "removeComment";
        public final static String TYPE_REMOVE_COMMENT_REPLY = "removeCommentReply";

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getPos() {
            return pos;
        }

        public void setPos(int pos) {
            this.pos = pos;
        }
    }
}
