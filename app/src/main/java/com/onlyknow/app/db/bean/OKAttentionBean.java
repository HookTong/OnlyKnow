package com.onlyknow.app.db.bean;

import java.util.Date;

public class OKAttentionBean {
    private int uatId;
    private String userNameMain;
    private String userNameRete;
    private Date uatDate;

    private String nickName = "";
    private String avatar = "";
    private String tag = "";

    public int getUatId() {
        return uatId;
    }

    public void setUatId(int uatId) {
        this.uatId = uatId;
    }

    public String getUserNameMain() {
        return userNameMain;
    }

    public void setUserNameMain(String userNameMain) {
        this.userNameMain = userNameMain;
    }

    public String getUserNameRete() {
        return userNameRete;
    }

    public void setUserNameRete(String userNameRete) {
        this.userNameRete = userNameRete;
    }

    public Date getUatDate() {
        return uatDate;
    }

    public void setUatDate(Date uatDate) {
        this.uatDate = uatDate;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
