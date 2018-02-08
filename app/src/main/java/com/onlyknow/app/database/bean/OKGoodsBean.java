package com.onlyknow.app.database.bean;

/**
 * Created by Administrator on 2017/12/10.
 */

public class OKGoodsBean {
    public final static String KEY_CML_ID = "GD_ID";
    private int GD_ID = -1;

    public final static String KEY_GD_NAME = "GD_NAME";
    private String GD_NAME = "";

    public final static String KEY_GD_TYPE = "GD_TYPE";
    private String GD_TYPE = "";

    public final static String KEY_GD_INTRODUCTION = "GD_INTRODUCTION";
    private String GD_INTRODUCTION = "";

    public final static String KEY_GD_DESCRIBE = "GD_DESCRIBE";
    private String GD_DESCRIBE = "";

    public final static String KEY_GD_ICON_URL = "GD_ICON_URL";
    private String GD_ICON_URL = "";

    public final static String KEY_GD_IMAGE_URL = "GD_IMAGE_URL";
    private String GD_IMAGE_URL = "";

    public final static String KEY_GD_PRICE = "GD_PRICE";
    private int GD_PRICE = 0;

    public final static String KEY_GD_DATE = "GD_DATE";
    private String GD_DATE = "";

    public final static String KEY_IS_BUY = "IS_BUY";
    private boolean IS_BUY;

    public int getGD_ID() {
        return GD_ID;
    }

    public void setGD_ID(int GD_ID) {
        this.GD_ID = GD_ID;
    }

    public String getGD_NAME() {
        return GD_NAME;
    }

    public void setGD_NAME(String GD_NAME) {
        this.GD_NAME = GD_NAME;
    }

    public String getGD_TYPE() {
        return GD_TYPE;
    }

    public void setGD_TYPE(String GD_TYPE) {
        this.GD_TYPE = GD_TYPE;
    }

    public String getGD_DESCRIBE() {
        return GD_DESCRIBE;
    }

    public void setGD_DESCRIBE(String GD_DESCRIBE) {
        this.GD_DESCRIBE = GD_DESCRIBE;
    }

    public int getGD_PRICE() {
        return GD_PRICE;
    }

    public void setGD_PRICE(int GD_PRICE) {
        this.GD_PRICE = GD_PRICE;
    }

    public String getGD_DATE() {
        return GD_DATE;
    }

    public void setGD_DATE(String GD_DATE) {
        this.GD_DATE = GD_DATE;
    }

    public boolean IS_BUY() {
        return IS_BUY;
    }

    public void setIS_BUY(boolean IS_BUY) {
        this.IS_BUY = IS_BUY;
    }

    public String getGD_INTRODUCTION() {
        return GD_INTRODUCTION;
    }

    public void setGD_INTRODUCTION(String GD_INTRODUCTION) {
        this.GD_INTRODUCTION = GD_INTRODUCTION;
    }

    public String getGD_ICON_URL() {
        return GD_ICON_URL;
    }

    public void setGD_ICON_URL(String GD_ICON_URL) {
        this.GD_ICON_URL = GD_ICON_URL;
    }

    public String getGD_IMAGE_URL() {
        return GD_IMAGE_URL;
    }

    public void setGD_IMAGE_URL(String GD_IMAGE_URL) {
        this.GD_IMAGE_URL = GD_IMAGE_URL;
    }
}
