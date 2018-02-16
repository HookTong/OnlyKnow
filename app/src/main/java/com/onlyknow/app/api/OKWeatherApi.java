package com.onlyknow.app.api;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.onlyknow.app.database.bean.OKWeatherBean;
import com.onlyknow.app.net.OKWebService;
import com.onlyknow.app.utils.OKNetUtil;

import java.io.IOException;

public class OKWeatherApi extends OKBaseApi {
    private Context context;
    private onCallBack mListener;

    public final String WEATHER_URL = "http://wthrcdn.etouch.cn/weather_mini?citykey="; // 天气接口url

    public OKWeatherApi(Context cont) {
        this.context = cont;
    }

    public interface onCallBack {
        void weatherApiComplete(OKWeatherBean weather);
    }

    /**
     * 异步完成网络中JSON解析
     */
    public void requestWeather(String cityId, onCallBack listener) {
        this.mListener = listener;

        if (OKNetUtil.isNet(context)) {
            new LoadWeatherTask().execute(WEATHER_URL + cityId);
        }
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

    // 异步获取天气数据
    private class LoadWeatherTask extends AsyncTask<String, Void, OKWeatherBean> {

        @Override
        protected OKWeatherBean doInBackground(String... params) {
            return getWeatherBean(params[0]);
        }

        @Override
        protected void onPostExecute(OKWeatherBean result) {
            super.onPostExecute(result);

            if (mListener != null) {
                mListener.weatherApiComplete(result);
            }
        }
    }
}
