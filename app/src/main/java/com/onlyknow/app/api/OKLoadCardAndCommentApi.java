package com.onlyknow.app.api;

import android.content.Context;
import android.os.AsyncTask;

import com.onlyknow.app.database.bean.OKCardAndCommentBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 用户总评论界面数据源加载Api
 * <p>
 * Created by Administrator on 2018/2/1.
 */

public class OKLoadCardAndCommentApi extends OKBaseApi {
    private onCallBack mOnCallBack;
    private Context context;
    private LoadCardAndCommentListTask mLoadCardAndCommentListTask;
    private boolean isLoadMore = false;

    public OKLoadCardAndCommentApi(Context con, boolean isLm) {
        this.context = con;
        this.isLoadMore = isLm;
    }

    public interface onCallBack {
        void cardAndCommentApiComplete(List<OKCardAndCommentBean> list);
    }

    public void requestCardAndCommentBeanList(Map<String, String> param, boolean isLm, onCallBack mCallBack) {
        this.isLoadMore = isLm;
        this.mOnCallBack = mCallBack;
        cancelTask();
        mLoadCardAndCommentListTask = new LoadCardAndCommentListTask();
        mLoadCardAndCommentListTask.executeOnExecutor(exec, param);
    }

    public void cancelTask() {
        if (mLoadCardAndCommentListTask != null && mLoadCardAndCommentListTask.getStatus() == AsyncTask.Status.RUNNING) {
            mLoadCardAndCommentListTask.cancel(true);
        }
    }

    private class LoadCardAndCommentListTask extends AsyncTask<Map<String, String>, Void, List<OKCardAndCommentBean>> {

        @Override
        protected List<OKCardAndCommentBean> doInBackground(Map<String, String>... params) {
            if (isCancelled()) {
                return null;
            }
            OKBusinessApi mOKBusinessApi = new OKBusinessApi();
            List<OKCardAndCommentBean> list = new ArrayList<>();
            if (isLoadMore) {
                list = mOKBusinessApi.loadMoreCardAndComment(params[0]);
            } else {
                list = mOKBusinessApi.getCardAndComment(params[0]);
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<OKCardAndCommentBean> list) {
            if (isCancelled()) {
                return;
            }
            mOnCallBack.cardAndCommentApiComplete(list);
            super.onPostExecute(list);
        }
    }
}
