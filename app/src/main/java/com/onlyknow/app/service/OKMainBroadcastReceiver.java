package com.onlyknow.app.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;

import com.hyphenate.chat.EMClient;
import com.onlyknow.app.OKConstant;
import com.onlyknow.app.db.bean.OKUserInfoBean;
import com.onlyknow.app.utils.OKLogUtil;

import java.util.Date;

import static com.onlyknow.app.service.OKBaseService.*;

public class OKMainBroadcastReceiver extends BroadcastReceiver {

    public interface MainReceiver {
        void onMainReceiver(Context context, Intent intent);
    }

    private Context context;

    private MainReceiver mainReceiver;

    public OKMainBroadcastReceiver(Context con) {
        this.context = con;
    }

    public void registered(MainReceiver receiver) {
        this.mainReceiver = receiver;

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_MAIN_SERVICE_LOGIN_IM);
        intentFilter.addAction(ACTION_MAIN_SERVICE_LOGOUT_IM);
        intentFilter.addAction(ACTION_MAIN_SERVICE_CREATE_ACCOUNT_IM);
        intentFilter.addAction(ACTION_MAIN_SERVICE_ADD_MESSAGE_LISTENER_IM);
        intentFilter.addAction(ACTION_MAIN_SERVICE_REMOVE_MESSAGE_LISTENER_IM);
        intentFilter.addAction(ACTION_MAIN_SERVICE_GET_CAROUSE_IMAGE);
        intentFilter.addAction(ACTION_MAIN_SERVICE_GET_WEATHER);
        intentFilter.addAction(OKConstant.ACTION_SHOW_NOTICE);
        intentFilter.addAction(OKConstant.ACTION_RESET_LOCATION);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        this.context.registerReceiver(this, intentFilter);
    }

    public void unregistered() {
        this.mainReceiver = null;
        this.context.unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (ACTION_MAIN_SERVICE_LOGIN_IM.equals(action)) {
        } else if (ACTION_MAIN_SERVICE_LOGOUT_IM.equals(action)) {
        } else if (ACTION_MAIN_SERVICE_CREATE_ACCOUNT_IM.equals(action)) {
        } else if (ACTION_MAIN_SERVICE_ADD_MESSAGE_LISTENER_IM.equals(action)) {
        } else if (ACTION_MAIN_SERVICE_REMOVE_MESSAGE_LISTENER_IM.equals(action)) {
        } else if (ACTION_MAIN_SERVICE_GET_CAROUSE_IMAGE.equals(action)) {
        } else if (ACTION_MAIN_SERVICE_GET_WEATHER.equals(action)) {
        } else if (OKConstant.ACTION_SHOW_NOTICE.equals(action)) {
        } else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
        } else if (OKConstant.ACTION_RESET_LOCATION.equals(action)) {
        }

        if (this.mainReceiver != null) {
            this.mainReceiver.onMainReceiver(context, intent);
        }

        OKLogUtil.print("OKMainService 收到广播 : " + action);
    }
}
