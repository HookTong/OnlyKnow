package com.onlyknow.app.api.card;

import android.content.Context;
import android.os.AsyncTask;

import com.onlyknow.app.api.OKBaseApi;
import com.onlyknow.app.database.bean.OKCardBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 附近界面数据源加载APi
 * <p>
 * Created by Administrator on 2017/12/22.
 */

public class OKLoadNearCardApi extends OKBaseApi {
    private onCallBack mOnCallBack;
    private Context context;
    private LoadNearTask mLoadNearTask;

    public OKLoadNearCardApi(Context con) {
        this.context = con;
    }

    public interface onCallBack {
        void nearApiComplete(List<OKCardBean> list);
    }

    public void requestNearCard(Params params, onCallBack mCallBack) {
        this.mOnCallBack = mCallBack;
        cancelTask();
        mLoadNearTask = new LoadNearTask();
        mLoadNearTask.executeOnExecutor(exec, params);
    }

    public void cancelTask() {
        if (mLoadNearTask != null && mLoadNearTask.getStatus() == AsyncTask.Status.RUNNING) {
            mLoadNearTask.cancel(true);
        }
    }

    private class LoadNearTask extends AsyncTask<Params, Void, List<OKCardBean>> {

        @Override
        protected List<OKCardBean> doInBackground(Params... params) {
            if (isCancelled()) {
                return null;
            }
            Params mParams = params[0];

            Map<String, String> map = new HashMap<>();

            map.put(Params.KEY_NAME, mParams.getUsername());
            map.put(Params.KEY_PAGE, String.valueOf(mParams.getPage()));
            map.put(Params.KEY_SIZE, String.valueOf(mParams.getSize()));
            map.put(Params.KEY_LONGITUDE, String.valueOf(mParams.getLongitude()));
            map.put(Params.KEY_LATITUDE, String.valueOf(mParams.getLatitude()));

            return getNearCard(map);
        }

        @Override
        protected void onPostExecute(List<OKCardBean> okCardBeen) {
            if (isCancelled()) {
                return;
            }

            super.onPostExecute(okCardBeen);
            mOnCallBack.nearApiComplete(okCardBeen);
        }
    }

    public static class Params {
        private String username;
        private int page;
        private int size;
        private double longitude;
        private double latitude;

        public final static String KEY_NAME = "username";
        public final static String KEY_PAGE = "page";
        public final static String KEY_SIZE = "size";
        public final static String KEY_LONGITUDE = "longitude";
        public final static String KEY_LATITUDE = "latitude";

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

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }
    }
}
