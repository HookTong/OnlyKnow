package com.onlyknow.app.database.bean;

import java.util.Date;

public class OKCommentReplyBean {
    private int comrId;
    private int comId;
    private String userName;
    private String message;
    private Integer comrPraise;
    private Date comrDate;

    private String nickName = "";
    private String avatar = "";
    private boolean isPraise = false;

    public int getComrId() {
        return comrId;
    }

    public void setComrId(int comrId) {
        this.comrId = comrId;
    }

    public int getComId() {
        return comId;
    }

    public void setComId(int comId) {
        this.comId = comId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getComrPraise() {
        return comrPraise;
    }

    public void setComrPraise(Integer comrPraise) {
        this.comrPraise = comrPraise;
    }

    public Date getComrDate() {
        return comrDate;
    }

    public void setComrDate(Date comrDate) {
        this.comrDate = comrDate;
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

    public boolean isPraise() {
        return isPraise;
    }

    public void setPraise(boolean praise) {
        isPraise = praise;
    }
}
