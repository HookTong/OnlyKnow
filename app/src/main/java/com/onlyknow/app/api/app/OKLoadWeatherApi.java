package com.onlyknow.app.api.app;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.onlyknow.app.api.OKBaseApi;
import com.onlyknow.app.database.bean.OKWeatherBean;
import com.onlyknow.app.net.OKWebService;
import com.onlyknow.app.utils.OKNetUtil;

import java.io.IOException;

public class OKLoadWeatherApi extends OKBaseApi {
    private Context context;
    private onCallBack mListener;
    private LoadWeatherTask mLoadWeatherTask;

    private final String WEATHER_URL = "http://wthrcdn.etouch.cn/weather_mini?citykey="; // 天气接口url

    public OKLoadWeatherApi(Context cont) {
        this.context = cont;
    }

    public interface onCallBack {
        void weatherApiComplete(OKWeatherBean weather);
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
            return getWeatherBean(params[0]);
        }

        /**
         * 通过网络，得到JSON，解析成对象
         *
         * @throws IOException
         */
        private OKWeatherBean getWeatherBean(String webUrl) {
            String json = OKWebService.OKHttpApiGet(webUrl);
            if (json == null) {
                return null;
            }

            try {
                Gson gson = new Gson();
                return gson.fromJson(json, OKWeatherBean.class);
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(OKWeatherBean result) {
            if (mListener != null) {
                mListener.weatherApiComplete(result);
            }
            super.onPostExecute(result);
        }
    }
}
