package com.onlyknow.app.db.bean;

import java.util.Date;

public class OKAppInfoBean {
    public final static String KEY_APP_ID = "appId";
    private int appId;
    private String appVersion;
    private String appName;
    private String appUrl;
    private String appDescribe;
    private String appUa;
    private String appImageUrl;
    private String appSize;
    private boolean appIsMandatory;
    private Date appDate;

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public String getAppDescribe() {
        return appDescribe;
    }

    public void setAppDescribe(String appDescribe) {
        this.appDescribe = appDescribe;
    }

    public String getAppUa() {
        return appUa;
    }

    public void setAppUa(String appUa) {
        this.appUa = appUa;
    }

    public String getAppImageUrl() {
        return appImageUrl;
    }

    public void setAppImageUrl(String appImageUrl) {
        this.appImageUrl = appImageUrl;
    }

    public String getAppSize() {
        return appSize;
    }

    public void setAppSize(String appSize) {
        this.appSize = appSize;
    }

    public boolean isAppIsMandatory() {
        return appIsMandatory;
    }

    public void setAppIsMandatory(boolean appIsMandatory) {
        this.appIsMandatory = appIsMandatory;
    }

    public Date getAppDate() {
        return appDate;
    }

    public void setAppDate(Date appDate) {
        this.appDate = appDate;
    }
}
