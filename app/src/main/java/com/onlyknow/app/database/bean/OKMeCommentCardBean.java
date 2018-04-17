package com.onlyknow.app.database.bean;

public class OKMeCommentCardBean {
    private OKCardBean cardEntity;

    private OKCommentBean commentEntity;

    public OKCardBean getCardBean() {
        return cardEntity;
    }

    public void setCardBean(OKCardBean mOKCardBean) {
        this.cardEntity = mOKCardBean;
    }

    public OKCommentBean getCommentBean() {
        return commentEntity;
    }

    public void setCommentBean(OKCommentBean mOKCommentBean) {
        this.commentEntity = mOKCommentBean;
    }
}
