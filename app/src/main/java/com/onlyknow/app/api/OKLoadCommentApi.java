package com.onlyknow.app.api;

import android.content.Context;
import android.os.AsyncTask;

import com.onlyknow.app.database.bean.OKCommentBean;

import java.util.ArrayList;
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
    private LoadCommentListTask mLoadCommentListTask;
    private boolean isLoadMore = false;

    public OKLoadCommentApi(Context con, boolean isLm) {
        this.context = con;
        this.isLoadMore = isLm;
    }

    public interface onCallBack {
        void commentApiComplete(List<OKCommentBean> list);
    }

    public void requestCommentBeanList(Map<String, String> param, boolean isLm, onCallBack mCallBack) {
        this.isLoadMore = isLm;
        this.mOnCallBack = mCallBack;
        cancelTask();
        mLoadCommentListTask = new LoadCommentListTask();
        mLoadCommentListTask.executeOnExecutor(exec, param);
    }

    public void cancelTask() {
        if (mLoadCommentListTask != null && mLoadCommentListTask.getStatus() == AsyncTask.Status.RUNNING) {
            mLoadCommentListTask.cancel(true);
        }
    }

    private class LoadCommentListTask extends AsyncTask<Map<String, String>, Void, List<OKCommentBean>> {

        @Override
        protected List<OKCommentBean> doInBackground(Map<String, String>... params) {
            if (isCancelled()) {
                return null;
            }
            OKBusinessApi mOKBusinessApi = new OKBusinessApi();
            List<OKCommentBean> mOKCommentBeanList = new ArrayList<>();
            if (isLoadMore) {
                mOKCommentBeanList = mOKBusinessApi.loadMoreCommentCard(params[0]);
            } else {
                mOKCommentBeanList = mOKBusinessApi.getCommentCard(params[0]);
            }
            return mOKCommentBeanList;
        }

        @Override
        protected void onPostExecute(List<OKCommentBean> mOKCommentBeanList) {
            if (isCancelled()) {
                return;
            }
            mOnCallBack.commentApiComplete(mOKCommentBeanList);
            super.onPostExecute(mOKCommentBeanList);
        }
    }
}
