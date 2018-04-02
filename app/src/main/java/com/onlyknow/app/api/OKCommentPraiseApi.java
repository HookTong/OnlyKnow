package com.onlyknow.app.api;

import android.content.Context;
import android.os.AsyncTask;

import com.onlyknow.app.utils.OKNetUtil;

import java.util.Map;

public class OKCommentPraiseApi extends OKBaseApi {
    private Context context;
    private onCallBack mListener;
    private CommentPraiseTask mCommentPraiseTask;

    public OKCommentPraiseApi(Context cont) {
        this.context = cont;
    }

    public interface onCallBack {
        void commentPraiseApiComplete(boolean isSuccess, int pos);
    }

    public void requestCommentPraiseApi(Map<String, String> map, int pos, onCallBack listener) {
        this.mListener = listener;
        if (OKNetUtil.isNet(context)) {
            Params mParams = new Params();
            mParams.setReqMap(map);
            mParams.setPos(pos);
            cancelTask();
            mCommentPraiseTask = new CommentPraiseTask();
            mCommentPraiseTask.executeOnExecutor(exec, mParams);
        } else {
            mListener.commentPraiseApiComplete(false, pos);
        }
    }

    public void cancelTask() {
        if (mCommentPraiseTask != null && mCommentPraiseTask.getStatus() == AsyncTask.Status.RUNNING) {
            mCommentPraiseTask.cancel(true);
        }
    }

    private class CommentPraiseTask extends AsyncTask<Params, Void, Boolean> {
        private int mPosition;

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (isCancelled()) {
                return;
            }

            mListener.commentPraiseApiComplete(aBoolean, mPosition);
        }

        @Override
        protected Boolean doInBackground(Params... params) {
            if (isCancelled()) {
                return false;
            }
            Params mParams = params[0];
            mPosition = mParams.getPos();

            OKBusinessApi mWebApi = new OKBusinessApi();
            return mWebApi.editCommentPraise(mParams.getReqMap());
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
