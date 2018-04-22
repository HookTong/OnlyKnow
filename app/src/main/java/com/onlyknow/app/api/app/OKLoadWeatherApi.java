package com.onlyknow.app.api.app;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.onlyknow.app.api.OKBaseApi;
import com.onlyknow.app.db.bean.OKWeatherBean;
import com.onlyknow.app.net.OKWebService;
import com.onlyknow.app.utils.OKGsonUtil;
import com.onlyknow.app.utils.OKNetUtil;

public class OKLoadWeatherApi extends OKBaseApi {
    private Context context;
    private onCallBack mListener;
    private LoadWeatherTask mLoadWeatherTask;

    private final String WEATHER_URL = "http://wthrcdn.etouch.cn/weather_mini?citykey="; // 天气接口url

    public OKLoadWeatherApi(Context cont) {
        this.context = cont;
    }

    public interface onCallBack {
        void loadWeatherComplete(OKWeatherBean weather);
    }

    public void requestWeather(String cityId, onCallBack listener) {
        this.mListener = listener;

        if (TextUtils.isEmpty(cityId)) return;

        if (OKNetUtil.isNet(context)) {
            cancelTask();
            mLoadWeatherTask = new LoadWeatherTask();
            mLoadWeatherTask.executeOnExecutor(exec, WEATHER_URL + cityId);
        }
    }

    public void cancelTask() {
        if (mLoadWeatherTask != null && mLoadWeatherTask.getStatus() == AsyncTask.Status.RUNNING) {
            mLoadWeatherTask.cancel(true);
        }
    }

    private class LoadWeatherTask extends AsyncTask<String, Void, OKWeatherBean> {

        @Override
        protected OKWeatherBean doInBackground(String... params) {
            if (isCancelled()) {
                return null;
            }

            String json = OKWebService.OKHttpApiGet(params[0]);
            if (TextUtils.isEmpty(json)) {
                return null;
            }

            return OKGsonUtil.fromJson(json, OKWeatherBean.class);
        }

        @Override
        protected void onPostExecute(OKWeatherBean result) {
            if (mListener != null) {
                mListener.loadWeatherComplete(result);
            }
            super.onPostExecute(result);
        }
    }
}
