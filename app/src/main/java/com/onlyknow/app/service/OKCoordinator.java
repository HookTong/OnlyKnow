package com.onlyknow.app.service;

import android.os.Handler;
import android.os.Message;

public class OKCoordinator extends Handler {

    public interface CoordinatorCallBack {
        void coordinatorMessage(Message msg);
    }

    private CoordinatorCallBack coordinatorCallBack;

    public OKCoordinator(CoordinatorCallBack callBack) {
        this.coordinatorCallBack = callBack;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (this.coordinatorCallBack != null) {
            this.coordinatorCallBack.coordinatorMessage(msg);
        }
    }

    public void sendCoordinatorMessage(Message ms) {
        this.sendMessage(ms);
    }
}
