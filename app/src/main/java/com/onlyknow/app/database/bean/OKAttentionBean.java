package com.onlyknow.app.database.bean;

/**
 * Created by Administrator on 2017/12/10.
 */

public class OKAttentionBean {
    public final static String KEY_USER_NAME = "USER_NAME";
    private String USER_NAME = "";

    public final static String KEY_CARD_TYPE = "CARD_TYPE";
    private String CARD_TYPE = "";

    public final static String KEY_UAT_ID = "UAT_ID";
    private int UAT_ID = -1;

    public final static String KEY_HEAD_PORTRAIT_URL = "HEAD_PORTRAIT_URL";
    private String HEAD_PORTRAIT_URL = "";

    public final static String KEY_NICKNAME = "NICKNAME";
    private String NICKNAME = "";

    public final static String KEY_AUTOGRAPH = "AUTOGRAPH";
    private String AUTOGRAPH = "";

    public String getUSER_NAME() {
        return USER_NAME;
    }

    public void setUSER_NAME(String USER_NAME) {
        this.USER_NAME = USER_NAME;
    }

    public String getCARD_TYPE() {
        return CARD_TYPE;
    }

    public void setCARD_TYPE(String CARD_TYPE) {
        this.CARD_TYPE = CARD_TYPE;
    }

    public int getUAT_ID() {
        return UAT_ID;
    }

    public void setUAT_ID(int UAT_ID) {
        this.UAT_ID = UAT_ID;
    }

    public String getHEAD_PORTRAIT_URL() {
        return HEAD_PORTRAIT_URL;
    }

    public void setHEAD_PORTRAIT_URL(String HEAD_PORTRAIT_URL) {
        this.HEAD_PORTRAIT_URL = HEAD_PORTRAIT_URL;
    }

    public String getNICKNAME() {
        return NICKNAME;
    }

    public void setNICKNAME(String NICKNAME) {
        this.NICKNAME = NICKNAME;
    }

    public String getAUTOGRAPH() {
        return AUTOGRAPH;
    }

    public void setAUTOGRAPH(String AUTOGRAPH) {
        this.AUTOGRAPH = AUTOGRAPH;
    }
}
