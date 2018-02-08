package com.onlyknow.app.database.bean;

/**
 * Created by Administrator on 2018/2/4.
 */

public class OKSafetyInfoBean {
    private int AUV_ID;
    private String AVU_VERSION = "";
    private String AVU_NAME = "";
    private String AVU_URL = "";
    private String AVU_DESCRIBE = "";
    private String AVU_IMAG = "";
    private String AVU_DATE = "";
    private String AVD_SIZE = "";
    private String AVD_IS_MANDATORY = "";

    public enum AVD_IS_MANDATORY {
        YES, NO;
    }

    private int UA_ID;
    private String UA_VERSION = "";
    private String UA_NAME = "";
    private String UA_CONTENT = "";
    private String UA_TYPE = "";
    private String UA_DATE = "";

    public int getAUV_ID() {
        return AUV_ID;
    }

    public void setAUV_ID(int AUV_ID) {
        this.AUV_ID = AUV_ID;
    }

    public String getAVU_VERSION() {
        return AVU_VERSION;
    }

    public void setAVU_VERSION(String AVU_VERSION) {
        this.AVU_VERSION = AVU_VERSION;
    }

    public String getAVU_NAME() {
        return AVU_NAME;
    }

    public void setAVU_NAME(String AVU_NAME) {
        this.AVU_NAME = AVU_NAME;
    }

    public String getAVU_URL() {
        return AVU_URL;
    }

    public void setAVU_URL(String AVU_URL) {
        this.AVU_URL = AVU_URL;
    }

    public String getAVU_DESCRIBE() {
        return AVU_DESCRIBE;
    }

    public void setAVU_DESCRIBE(String AVU_DESCRIBE) {
        this.AVU_DESCRIBE = AVU_DESCRIBE;
    }

    public String getAVU_IMAG() {
        return AVU_IMAG;
    }

    public void setAVU_IMAG(String AVU_IMAG) {
        this.AVU_IMAG = AVU_IMAG;
    }

    public String getAVU_DATE() {
        return AVU_DATE;
    }

    public void setAVU_DATE(String AVU_DATE) {
        this.AVU_DATE = AVU_DATE;
    }

    public int getUA_ID() {
        return UA_ID;
    }

    public void setUA_ID(int UA_ID) {
        this.UA_ID = UA_ID;
    }

    public String getUA_VERSION() {
        return UA_VERSION;
    }

    public void setUA_VERSION(String UA_VERSION) {
        this.UA_VERSION = UA_VERSION;
    }

    public String getUA_NAME() {
        return UA_NAME;
    }

    public void setUA_NAME(String UA_NAME) {
        this.UA_NAME = UA_NAME;
    }

    public String getUA_CONTENT() {
        return UA_CONTENT;
    }

    public void setUA_CONTENT(String UA_CONTENT) {
        this.UA_CONTENT = UA_CONTENT;
    }

    public String getUA_TYPE() {
        return UA_TYPE;
    }

    public void setUA_TYPE(String UA_TYPE) {
        this.UA_TYPE = UA_TYPE;
    }

    public String getUA_DATE() {
        return UA_DATE;
    }

    public void setUA_DATE(String UA_DATE) {
        this.UA_DATE = UA_DATE;
    }

    public String getAVD_SIZE() {
        return AVD_SIZE;
    }

    public void setAVD_SIZE(String AVD_SIZE) {
        this.AVD_SIZE = AVD_SIZE;
    }

    public String getAVD_IS_MANDATORY() {
        return AVD_IS_MANDATORY;
    }

    public void setAVD_IS_MANDATORY(String AVD_IS_MANDATORY) {
        this.AVD_IS_MANDATORY = AVD_IS_MANDATORY;
    }
}
