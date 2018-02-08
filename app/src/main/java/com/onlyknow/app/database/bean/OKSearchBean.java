package com.onlyknow.app.database.bean;

/**
 * Created by Administrator on 2018/2/2.
 */

public class OKSearchBean {

    public enum SEARCH_TYPE {
        USER, CARD, ALL;
    }

    private SEARCH_TYPE Type;

    private OKUserInfoBean userInfoBean;

    private OKCardBean cardBean;

    public SEARCH_TYPE getType() {
        return Type;
    }

    public void setType(SEARCH_TYPE type) {
        Type = type;
    }

    public OKUserInfoBean getUserInfoBean() {
        return userInfoBean;
    }

    public void setUserInfoBean(OKUserInfoBean userInfoBean) {
        this.userInfoBean = userInfoBean;
    }

    public OKCardBean getCardBean() {
        return cardBean;
    }

    public void setCardBean(OKCardBean cardBean) {
        this.cardBean = cardBean;
    }
}
