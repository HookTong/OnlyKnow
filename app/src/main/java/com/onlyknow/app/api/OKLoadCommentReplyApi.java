package com.onlyknow.app.api;

import android.content.Context;
import android.os.AsyncTask;

import com.onlyknow.app.database.bean.OKCommentReplyBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 用户评论回复界面数据源加载Api
 * <p>
 * Created by Administrator on 2017/12/23.
 */

public class OKLoadCommentReplyApi extends OKBaseApi {
    private onCallBack mOnCallBack;
    private Context context;
    private LoadCardListTask mLoadCardListTask;
    private boolean isLoadMore = false;

    public OKLoadCommentReplyApi(Context con, boolean isLm) {
        this.context = con;
        this.isLoadMore = isLm;
    }

    public interface onCallBack {
        void commentReplyApiComplete(List<OKCommentReplyBean> list);
    }

    public void requestCardBeanList(Map<String, String> param, boolean isLm, onCallBack mCallBack) {
        this.isLoadMore = isLm;
        this.mOnCallBack = mCallBack;
        cancelTask();
        mLoadCardListTask = new LoadCardListTask();
        mLoadCardListTask.executeOnExecutor(exec, param);
    }

    public void cancelTask() {
        if (mLoadCardListTask != null && mLoadCardListTask.getStatus() == AsyncTask.Status.RUNNING) {
            mLoadCardListTask.cancel(true);
        }
    }

    private class LoadCardListTask extends AsyncTask<Map<String, String>, Void, List<OKCommentReplyBean>> {

        @Override
        protected List<OKCommentReplyBean> doInBackground(Map<String, String>... params) {
            if (isCancelled()) {
                return null;
            }
            OKBusinessApi mOKBusinessApi = new OKBusinessApi();
            List<OKCommentReplyBean> mOKCommentReplyBeanList = new ArrayList<>();
            if (isLoadMore) {
                mOKCommentReplyBeanList = mOKBusinessApi.loadMoreCommentReplyCard(params[0]);
            } else {
                mOKCommentReplyBeanList = mOKBusinessApi.getCommentReplyCard(params[0]);
            }
            return mOKCommentReplyBeanList;
        }

        @Override
        protected void onPostExecute(List<OKCommentReplyBean> mOKCommentReplyBeanList) {
            if (isCancelled()) {
                return;
            }
            mOnCallBack.commentReplyApiComplete(mOKCommentReplyBeanList);
            super.onPostExecute(mOKCommentReplyBeanList);
        }
    }
}
