package com.onlyknow.app.database.bean;

/**
 * Created by Administrator on 2017/12/10.
 */

public class OKNoticeBean {
    public final static String KEY_USER_NAME = "USER_NAME";
    private String USER_NAME = "";

    public final static String KEY_CARD_TYPE = "CARD_TYPE";
    private String CARD_TYPE = "";

    public final static String KEY_NT_ID = "NT_ID";
    private int NT_ID = -1;

    public final static String KEY_HEAD_PORTRAIT_URL = "HEAD_PORTRAIT_URL";
    private String HEAD_PORTRAIT_URL = "";

    public final static String KEY_NOTICE_TITLE = "NOTICE_TITLE";
    private String NOTICE_TITLE = "";

    public final static String KEY_NOTICE_CONTENT = "NOTICE_CONTENT";
    private String NOTICE_CONTENT = "";

    public final static String KEY_DATE = "DATE";
    private String DATE = "";

    public final static String KEY_STATE = "STATE";
    private boolean STATE = false;

    public final static String KEY_ALL_MESSAGE_NUM = "ALL_MESSAGE_NUM";
    private int ALL_MESSAGE_NUM = 0;

    public final static String KEY_UNREAD_NUM = "UNREAD_NUM";
    private int UNREAD_NUM = 0;

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

    public String getHEAD_PORTRAIT_URL() {
        return HEAD_PORTRAIT_URL;
    }

    public void setHEAD_PORTRAIT_URL(String HEAD_PORTRAIT_URL) {
        this.HEAD_PORTRAIT_URL = HEAD_PORTRAIT_URL;
    }

    public String getNOTICE_TITLE() {
        return NOTICE_TITLE;
    }

    public void setNOTICE_TITLE(String NOTICE_TITLE) {
        this.NOTICE_TITLE = NOTICE_TITLE;
    }

    public String getNOTICE_CONTENT() {
        return NOTICE_CONTENT;
    }

    public void setNOTICE_CONTENT(String NOTICE_CONTENT) {
        this.NOTICE_CONTENT = NOTICE_CONTENT;
    }

    public String getDATE() {
        return DATE;
    }

    public void setDATE(String DATE) {
        this.DATE = DATE;
    }

    public boolean isSTATE() {
        return STATE;
    }

    public void setSTATE(boolean STATE) {
        this.STATE = STATE;
    }

    public int getNT_ID() {
        return NT_ID;
    }

    public void setNT_ID(int NT_ID) {
        this.NT_ID = NT_ID;
    }

    public int getUNREAD_NUM() {
        return UNREAD_NUM;
    }

    public void setUNREAD_NUM(int UNREAD_NUM) {
        this.UNREAD_NUM = UNREAD_NUM;
    }

    public int getALL_MESSAGE_NUM() {
        return ALL_MESSAGE_NUM;
    }

    public void setALL_MESSAGE_NUM(int ALL_MESSAGE_NUM) {
        this.ALL_MESSAGE_NUM = ALL_MESSAGE_NUM;
    }
}
