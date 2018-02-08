package com.onlyknow.app.database.bean;

import android.os.Bundle;

/**
 * Created by Administrator on 2017/12/10.
 */

public class OKCommentBean {
    public final static String KEY_USER_NAME = "USER_NAME";
    private String USER_NAME = "";

    public final static String KEY_CARD_ID = "CARD_ID";
    private int CARD_ID = -1;

    public final static String KEY_COM_ID = "COM_ID";
    private int COM_ID = -1;

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

    public int getCARD_ID() {
        return CARD_ID;
    }

    public void setCARD_ID(int CARD_ID) {
        this.CARD_ID = CARD_ID;
    }

    public int getCOM_ID() {
        return COM_ID;
    }

    public void setCOM_ID(int COM_ID) {
        this.COM_ID = COM_ID;
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

    public static Bundle toBundle(OKCommentBean mOKCommentBean) {
        if (mOKCommentBean == null) {
            return null;
        }

        Bundle mBundle = new Bundle();
        mBundle.putInt(KEY_CARD_ID, mOKCommentBean.getCARD_ID());
        mBundle.putInt(KEY_COM_ID, mOKCommentBean.getCOM_ID());
        mBundle.putString(KEY_COMMENT_CONTENT, mOKCommentBean.getCOMMENT_CONTENT());
        mBundle.putString(KEY_DATE, mOKCommentBean.getDATE());
        mBundle.putString(KEY_HEAD_PORTRAIT_URL, mOKCommentBean.getHEAD_PORTRAIT_URL());
        mBundle.putBoolean(KEY_IS_ZAN, mOKCommentBean.IS_ZAN());
        mBundle.putString(KEY_NICKNAME, mOKCommentBean.getNICKNAME());
        mBundle.putString(KEY_USER_NAME, mOKCommentBean.getUSER_NAME());
        mBundle.putInt(KEY_ZAN_NUM, mOKCommentBean.getZAN_NUM());
        return mBundle;
    }

    public static OKCommentBean fromBundle(Bundle mBundle) {
        if (mBundle == null) {
            return null;
        }

        OKCommentBean mOKCommentBean = new OKCommentBean();
        mOKCommentBean.setCARD_ID(mBundle.getInt(KEY_CARD_ID, -1));
        mOKCommentBean.setCOM_ID(mBundle.getInt(KEY_COM_ID, -1));
        mOKCommentBean.setCOMMENT_CONTENT(mBundle.getString(KEY_COMMENT_CONTENT, ""));
        mOKCommentBean.setDATE(mBundle.getString(KEY_DATE, ""));
        mOKCommentBean.setHEAD_PORTRAIT_URL(mBundle.getString(KEY_HEAD_PORTRAIT_URL, ""));
        mOKCommentBean.setIS_ZAN(mBundle.getBoolean(KEY_IS_ZAN, false));
        mOKCommentBean.setNICKNAME(mBundle.getString(KEY_NICKNAME, ""));
        mOKCommentBean.setUSER_NAME(mBundle.getString(KEY_USER_NAME, ""));
        mOKCommentBean.setZAN_NUM(mBundle.getInt(KEY_ZAN_NUM, 0));
        return mOKCommentBean;
    }
}
