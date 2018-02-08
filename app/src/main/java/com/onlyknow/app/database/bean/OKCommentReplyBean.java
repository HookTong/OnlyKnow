package com.onlyknow.app.database.bean;

/**
 * Created by Administrator on 2017/12/10.
 */

public class OKCommentReplyBean {
    public final static String KEY_USER_NAME = "USER_NAME";
    private String USER_NAME = "";

    public final static String KEY_COM_ID = "COM_ID";// 上级ID
    private int COM_ID = -1;

    public final static String KEY_COMR_ID = "COMR_ID";// 本ID
    private int COMR_ID = -1;

    public final static String KEY_HEAD_PORTRAIT_URL = "HEAD_PORTRAIT_URL";
    private String HEAD_PORTRAIT_URL = "";

    public final static String KEY_NICKNAME = "NICKNAME";
    private String NICKNAME = "";

    public final static String KEY_COMMENT_CONTENT = "COMMENT_CONTENT";
    private String COMMENT_CONTENT = "";

    public final static String KEY_ZAN_NUM = "ZAN_NUM";
    private int ZAN_NUM = 0;

    public final static String KEY_DATE = "DATE";
    private String DATE = "";

    public final static String KEY_IS_ZAN = "IS_ZAN";
    private boolean IS_ZAN = false;

    public String getUSER_NAME() {
        return USER_NAME;
    }

    public void setUSER_NAME(String USER_NAME) {
        this.USER_NAME = USER_NAME;
    }

    public int getCOM_ID() {
        return COM_ID;
    }

    public void setCOM_ID(int COM_ID) {
        this.COM_ID = COM_ID;
    }

    public int getCOMR_ID() {
        return COMR_ID;
    }

    public void setCOMR_ID(int COMR_ID) {
        this.COMR_ID = COMR_ID;
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

    public String getCOMMENT_CONTENT() {
        return COMMENT_CONTENT;
    }

    public void setCOMMENT_CONTENT(String COMMENT_CONTENT) {
        this.COMMENT_CONTENT = COMMENT_CONTENT;
    }

    public int getZAN_NUM() {
        return ZAN_NUM;
    }

    public void setZAN_NUM(int ZAN_NUM) {
        this.ZAN_NUM = ZAN_NUM;
    }

    public String getDATE() {
        return DATE;
    }

    public void setDATE(String DATE) {
        this.DATE = DATE;
    }

    public boolean IS_ZAN() {
        return IS_ZAN;
    }

    public void setIS_ZAN(boolean IS_ZAN) {
        this.IS_ZAN = IS_ZAN;
    }
}
