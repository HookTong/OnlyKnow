package com.onlyknow.app.db.bean;

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

        public String getWendu() {
            return wendu;
        }

        public void setWendu(String wendu) {
            this.wendu = wendu;
        }

        public String getGanmao() {
            return ganmao;
        }

        public void setGanmao(String ganmao) {
            this.ganmao = ganmao;
        }

        public List<Forecast> getForecast() {
            return forecast;
        }

        public void setForecast(List<Forecast> forecast) {
            this.forecast = forecast;
        }

        public Yesterday getYesterday() {
            return yesterday;
        }

        public void setYesterday(Yesterday yesterday) {
            this.yesterday = yesterday;
        }

        public String getAqi() {
            return aqi;
        }

        public void setAqi(String aqi) {
            this.aqi = aqi;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }
    }

    public static class Forecast {
        public String fengxiang;
        public String fengli;
        public String high;
        public String type;
        public String low;
        public String date;

        public String getFengxiang() {
            return fengxiang;
        }

        public void setFengxiang(String fengxiang) {
            this.fengxiang = fengxiang;
        }

        public String getFengli() {
            return fengli;
        }

        public void setFengli(String fengli) {
            this.fengli = fengli;
        }

        public String getHigh() {
            return high;
        }

        public void setHigh(String high) {
            this.high = high;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getLow() {
            return low;
        }

        public void setLow(String low) {
            this.low = low;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }

    public static class Yesterday {
        public String fl;
        public String fx;
        public String hight;
        public String type;
        public String low;
        public String date;

        public String getFl() {
            return fl;
        }

        public void setFl(String fl) {
            this.fl = fl;
        }

        public String getFx() {
            return fx;
        }

        public void setFx(String fx) {
            this.fx = fx;
        }

        public String getHight() {
            return hight;
        }

        public void setHight(String hight) {
            this.hight = hight;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getLow() {
            return low;
        }

        public void setLow(String low) {
            this.low = low;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
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
