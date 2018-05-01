package com.onlyknow.app.db.bean;

public class OKCardRelatedBean {
    private int cardId;
    private String userName;
    private boolean isPraise = false;
    private boolean isAttention = false;
    private boolean isWatch = false;
    private int praiseCount = 0;
    private int watchCount = 0;
    private int commentCount = 0;
    private String tag = "";
    private boolean isCardRemove = false;

    public boolean isPraise() {
        return isPraise;
    }

    public void setPraise(boolean praise) {
        isPraise = praise;
    }

    public boolean isAttention() {
        return isAttention;
    }

    public void setAttention(boolean attention) {
        isAttention = attention;
    }

    public boolean isWatch() {
        return isWatch;
    }

    public void setWatch(boolean watch) {
        isWatch = watch;
    }

    public int getPraiseCount() {
        return praiseCount;
    }

    public void setPraiseCount(int praiseCount) {
        this.praiseCount = praiseCount;
    }

    public int getWatchCount() {
        return watchCount;
    }

    public void setWatchCount(int watchCount) {
        this.watchCount = watchCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isCardRemove() {
        return isCardRemove;
    }

    public void setCardRemove(boolean cardRemove) {
        isCardRemove = cardRemove;
    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
