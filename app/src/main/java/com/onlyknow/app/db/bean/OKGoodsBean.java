package com.onlyknow.app.db.bean;

import java.util.Date;

public class OKGoodsBean {
    private int gdId;
    private String gdName;
    private String gdType;
    private String gdDescribe;
    private int gdPrice;
    private String gdIntroduction;
    private String gdIconUrl;
    private String gdImageUrl;
    private Date gdDate;

    private boolean isGoodsBy = false;

    public int getGdId() {
        return gdId;
    }

    public void setGdId(int gdId) {
        this.gdId = gdId;
    }

    public String getGdName() {
        return gdName;
    }

    public void setGdName(String gdName) {
        this.gdName = gdName;
    }

    public String getGdType() {
        return gdType;
    }

    public void setGdType(String gdType) {
        this.gdType = gdType;
    }

    public String getGdDescribe() {
        return gdDescribe;
    }

    public void setGdDescribe(String gdDescribe) {
        this.gdDescribe = gdDescribe;
    }

    public int getGdPrice() {
        return gdPrice;
    }

    public void setGdPrice(int gdPrice) {
        this.gdPrice = gdPrice;
    }

    public String getGdIntroduction() {
        return gdIntroduction;
    }

    public void setGdIntroduction(String gdIntroduction) {
        this.gdIntroduction = gdIntroduction;
    }

    public String getGdIconUrl() {
        return gdIconUrl;
    }

    public void setGdIconUrl(String gdIconUrl) {
        this.gdIconUrl = gdIconUrl;
    }

    public String getGdImageUrl() {
        return gdImageUrl;
    }

    public void setGdImageUrl(String gdImageUrl) {
        this.gdImageUrl = gdImageUrl;
    }

    public Date getGdDate() {
        return gdDate;
    }

    public void setGdDate(Date gdDate) {
        this.gdDate = gdDate;
    }

    public boolean isGoodsBy() {
        return isGoodsBy;
    }

    public void setGoodsBy(boolean goodsBy) {
        isGoodsBy = goodsBy;
    }
}
