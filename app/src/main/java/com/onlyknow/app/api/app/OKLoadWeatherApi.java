package com.onlyknow.app.api.app;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.onlyknow.app.api.OKBaseApi;
import com.onlyknow.app.db.bean.OKWeatherBean;
import com.onlyknow.app.net.OKWebService;
import com.onlyknow.app.utils.OKCityUtil;
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

    public void requestWeather(Params params, onCallBack listener) {
        this.mListener = listener;

        if (params == null) return;

        if (OKNetUtil.isNet(context)) {
            cancelTask();
            mLoadWeatherTask = new LoadWeatherTask();
            mLoadWeatherTask.executeOnExecutor(exec, params);
        }
    }

    public void cancelTask() {
        if (mLoadWeatherTask != null && mLoadWeatherTask.getStatus() == AsyncTask.Status.RUNNING) {
            mLoadWeatherTask.cancel(true);
        }
    }

    private class LoadWeatherTask extends AsyncTask<Params, Void, OKWeatherBean> {

        @Override
        protected OKWeatherBean doInBackground(Params... params) {
            if (isCancelled()) {
                return null;
            }

            Params mParams = params[0];

            String cityId = mParams.getCityId();

            if (TextUtils.isEmpty(cityId)) {
                cityId = OKCityUtil.getCityID(context, mParams.getDistrict()
                        .replace("区", "")
                        .replace("县", "")
                        .replace(" ", ""));
                if (TextUtils.isEmpty(cityId)) {
                    cityId = OKCityUtil.getCityID(context, mParams.getCityName()
                            .replace("市", "")
                            .replace(" ", ""));
                    if (TextUtils.isEmpty(cityId)) return null;
                }
            }

            String json = OKWebService.OKHttpApiGet(WEATHER_URL + cityId);

            if (TextUtils.isEmpty(json)) return null;

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

    public static class Params {
        private String cityId = "";
        private String cityName = "";
        private String district = "";

        public String getCityId() {
            return cityId;
        }

        public void setCityId(String cityId) {
            this.cityId = cityId;
        }

        public String getCityName() {
            return cityName;
        }

        public void setCityName(String cityName) {
            this.cityName = cityName;
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }
    }
}
