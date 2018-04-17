package com.onlyknow.app.database.bean;

import java.util.Date;

public class OKNoticeBean {
    public final static String KEY_USER_NAME = "userName";
    private String userName = "";

    public final static String KEY_CARD_TYPE = "cardType";
    private String cardType = "";

    public final static String KEY_NT_ID = "ntId";
    private int ntId = -1;

    public final static String KEY_HEAD_PORTRAIT_URL = "avatarUrl";
    private String avatarUrl = "";

    public final static String KEY_NOTICE_TITLE = "noticeTitle";
    private String noticeTitle = "";

    public final static String KEY_NOTICE_CONTENT = "noticeContent";
    private String noticeContent = "";

    public final static String KEY_DATE = "date";
    private Date date;

    public final static String KEY_STATE = "state";
    private boolean state = false;

    public final static String KEY_ALL_MESSAGE_NUM = "allMessageNum";
    private int allMessageNum = 0;

    public final static String KEY_UNREAD_NUM = "unreadNum";
    private int unreadNum = 0;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getNoticeTitle() {
        return noticeTitle;
    }

    public void setNoticeTitle(String noticeTitle) {
        this.noticeTitle = noticeTitle;
    }

    public String getNoticeContent() {
        return noticeContent;
    }

    public void setNoticeContent(String noticeContent) {
        this.noticeContent = noticeContent;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public int getNtId() {
        return ntId;
    }

    public void setNtId(int ntId) {
        this.ntId = ntId;
    }

    public int getUnreadNum() {
        return unreadNum;
    }

    public void setUnreadNum(int unreadNum) {
        this.unreadNum = unreadNum;
    }

    public int getAllMessageNum() {
        return allMessageNum;
    }

    public void setAllMessageNum(int allMessageNum) {
        this.allMessageNum = allMessageNum;
    }
}
