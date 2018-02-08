package com.onlyknow.app.database.bean;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/31.
 */

public class OKCardUrlListBean {

    private int count = 0;

    private String urlImage1 = "";

    private String urlImage2 = "";

    private String urlImage3 = "";

    private String urlImage4 = "";

    private String urlImage5 = "";


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getUrlImage1() {
        return urlImage1;
    }

    public void setUrlImage1(String urlImage1) {
        this.urlImage1 = urlImage1;
    }

    public String getUrlImage2() {
        return urlImage2;
    }

    public void setUrlImage2(String urlImage2) {
        this.urlImage2 = urlImage2;
    }

    public String getUrlImage3() {
        return urlImage3;
    }

    public void setUrlImage3(String urlImage3) {
        this.urlImage3 = urlImage3;
    }

    public String getUrlImage4() {
        return urlImage4;
    }

    public void setUrlImage4(String urlImage4) {
        this.urlImage4 = urlImage4;
    }

    public String getUrlImage5() {
        return urlImage5;
    }

    public void setUrlImage5(String urlImage5) {
        this.urlImage5 = urlImage5;
    }

    public static List<String> toList(OKCardUrlListBean bean) {
        List<String> listUrl = new ArrayList<>();

        if (!TextUtils.isEmpty(bean.getUrlImage1())) {
            listUrl.add(bean.getUrlImage1());
        }
        if (!TextUtils.isEmpty(bean.getUrlImage2())) {
            listUrl.add(bean.getUrlImage2());
        }
        if (!TextUtils.isEmpty(bean.getUrlImage3())) {
            listUrl.add(bean.getUrlImage3());
        }
        if (!TextUtils.isEmpty(bean.getUrlImage4())) {
            listUrl.add(bean.getUrlImage4());
        }
        if (!TextUtils.isEmpty(bean.getUrlImage5())) {
            listUrl.add(bean.getUrlImage5());
        }

        return listUrl;
    }
}
