package com.onlyknow.app.api;

import com.google.gson.Gson;

public class OKServiceResult<T> {
    private boolean isSuccess = false;
    private int code = 0;
    private String msg = "";
    private T data;
    private long time = 0;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void clear() {
        isSuccess = false;
        code = 0;
        msg = "";
        data = null;
        time = 0;
    }
}
