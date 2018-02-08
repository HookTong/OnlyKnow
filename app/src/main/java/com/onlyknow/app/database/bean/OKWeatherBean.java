package com.onlyknow.app.database.bean;

import com.google.gson.Gson;

import java.util.List;

/**
 * 2017-12-08 Ver1.1
 * <p>
 * 天气信息Bean类
 */

public class OKWeatherBean {
    public String desc;
    public String status;
    public Data data;

    public static class Data {
        public String wendu;
        public String ganmao;
        public List<Forecast> forecast;
        public Yesterday yesterday;
        public String aqi;
        public String city;
    }

    public static class Forecast {
        public String fengxiang;
        public String fengli;
        public String high;
        public String type;
        public String low;
        public String date;
    }

    public static class Yesterday {
        public String fl;
        public String fx;
        public String hight;
        public String type;
        public String low;
        public String date;
    }

    public final static String toJson(OKWeatherBean bean) {
        return new Gson().toJson(bean);
    }

    public final static OKWeatherBean fromJson(String json) {
        try {
            return new Gson().fromJson(json, OKWeatherBean.class);
        } catch (Exception e) {
            return null;
        }
    }
}
