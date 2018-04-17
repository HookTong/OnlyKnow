package com.onlyknow.app.database.bean;

import android.os.Bundle;

import java.io.Serializable;
import java.util.Date;

public class OKCommentBean implements Serializable {
    private int comId;
    private String userName;
    private int cardId;
    private String message;
    private Integer comPraise;
    private Date comDate;

    private String nickName = "";
    private String avatar = "";
    private boolean isPraise = false;

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

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getComPraise() {
        return comPraise;
    }

    public void setComPraise(Integer comPraise) {
        this.comPraise = comPraise;
    }

    public Date getComDate() {
        return comDate;
    }

    public void setComDate(Date comDate) {
        this.comDate = comDate;
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
