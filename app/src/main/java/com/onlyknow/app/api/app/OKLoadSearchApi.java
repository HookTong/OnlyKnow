package com.onlyknow.app.api.app;

import android.content.Context;
import android.os.AsyncTask;

import com.onlyknow.app.api.OKBaseApi;
import com.onlyknow.app.database.bean.OKSearchBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 搜索界面数据源加载Api
 * <p>
 * Created by Administrator on 2018/2/2.
 */

public class OKLoadSearchApi extends OKBaseApi {
    private onCallBack mOnCallBack;
    private Context context;
    private LoadSearchTask mLoadSearchTask;

    public OKLoadSearchApi(Context con) {
        this.context = con;
    }

    public interface onCallBack {
        void searchApiComplete(List<OKSearchBean> list);
    }

    public void requestSearch(Params params, onCallBack mCallBack) {
        this.mOnCallBack = mCallBack;
        cancelTask();
        mLoadSearchTask = new LoadSearchTask();
        mLoadSearchTask.executeOnExecutor(exec, params);
    }

    public void cancelTask() {
        if (mLoadSearchTask != null && mLoadSearchTask.getStatus() == AsyncTask.Status.RUNNING) {
            mLoadSearchTask.cancel(true);
        }
    }

    private class LoadSearchTask extends AsyncTask<Params, Void, List<OKSearchBean>> {

        @Override
        protected List<OKSearchBean> doInBackground(Params... params) {
            if (isCancelled()) {
                return null;
            }
            Params mParams = params[0];
            Map<String, String> map = new HashMap<>();
            map.put(Params.KEY_TYPE, mParams.getType());
            map.put(Params.KEY_SEARCH, mParams.getSearch());
            map.put(Params.KEY_PAGE, String.valueOf(mParams.getPage()));
            map.put(Params.KEY_SIZE, String.valueOf(mParams.getSize()));

            return getSearchEntry(map);
        }

        @Override
        protected void onPostExecute(List<OKSearchBean> list) {
            if (isCancelled()) {
                return;
            }
            mOnCallBack.searchApiComplete(list);
            super.onPostExecute(list);
        }
    }

    public static class Params {
        private String type;
        private String search;
        private int page;
        private int size;

        public final static String KEY_TYPE = "type";
        public final static String KEY_SEARCH = "search";
        public final static String KEY_PAGE = "page";
        public final static String KEY_SIZE = "size";

        public final static String TYPE_CARD = "card";
        public final static String TYPE_USER = "user";

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getSearch() {
            return search;
        }

        public void setSearch(String search) {
            this.search = search;
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
