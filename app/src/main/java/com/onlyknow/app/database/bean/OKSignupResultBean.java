package com.onlyknow.app.database.bean;

/**
 * Created by Administrator on 2017/12/15.
 */

public class OKSignupResultBean {
    private boolean IS_SIGNUP = false;
    private String MESSAGE = "";
    private String ERROR_INFO = "";
    private String SERVER_DATE = "";

    public boolean IS_SIGNUP() {
        return IS_SIGNUP;
    }

    public void setIS_SIGNUP(boolean IS_SIGNUP) {
        this.IS_SIGNUP = IS_SIGNUP;
    }

    public String getMESSAGE() {
        return MESSAGE;
    }

    public void setMESSAGE(String MESSAGE) {
        this.MESSAGE = MESSAGE;
    }

    public String getERROR_INFO() {
        return ERROR_INFO;
    }

    public void setERROR_INFO(String ERROR_INFO) {
        this.ERROR_INFO = ERROR_INFO;
    }

    public String getSERVER_DATE() {
        return SERVER_DATE;
    }

    public void setSERVER_DATE(String SERVER_DATE) {
        this.SERVER_DATE = SERVER_DATE;
    }
}
