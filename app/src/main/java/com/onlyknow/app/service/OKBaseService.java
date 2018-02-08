package com.onlyknow.app.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.onlyknow.app.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/12/13.
 */

public class OKBaseService extends Service {
    public NotificationManager mNotificationManager;
    public NotificationCompat.Builder mBuilder;
    public SharedPreferences USER_INFO_SP, SETTING_SP, WEATHER_SP;
    public final String ACTION_MAIN_SERVICE_SHOW_NOTICE = "ACTION_MAIN_SERVICE_SHOW_NOTICE";
    public final String ACTION_MAIN_SERVICE_LOGIN_IM = "ACTION_MAIN_SERVICE_LOGIN_IM";
    public final String ACTION_MAIN_SERVICE_LOGOUT_IM = "ACTION_MAIN_SERVICE_LOGOUT_IM";
    public final String ACTION_MAIN_SERVICE_CREATE_ACCOUNT_IM = "ACTION_MAIN_SERVICE_CREATE_ACCOUNT_IM";
    public final String ACTION_MAIN_SERVICE_ADD_MESSAGE_LISTENER_IM = "ACTION_MAIN_SERVICE_ADD_MESSAGE_LISTENER_IM";
    public final String ACTION_MAIN_SERVICE_REMOVE_MESSAGE_LISTENER_IM = "ACTION_MAIN_SERVICE_REMOVE_MESSAGE_LISTENER_IM";

    public final ExecutorService exec = Executors.newFixedThreadPool(100);

    public SharedPreferences getUserInfoSp() {
        if (USER_INFO_SP == null) {
            USER_INFO_SP = this.getApplicationContext().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        }
        return USER_INFO_SP;
    }

    public SharedPreferences getSettingSp() {
        if (SETTING_SP == null) {
            SETTING_SP = this.getApplicationContext().getSharedPreferences("setting", Context.MODE_PRIVATE);
        }
        return SETTING_SP;
    }

    public SharedPreferences getWeatherSp() {
        if (WEATHER_SP == null) {
            WEATHER_SP = this.getApplicationContext().getSharedPreferences("weather", Context.MODE_PRIVATE);
        }
        return WEATHER_SP;
    }

    // 注册通知信息
    public void initNotice(String title, String content) {
        mBuilder = new NotificationCompat.Builder(this);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, new Intent(), Notification.FLAG_AUTO_CANCEL);
        mBuilder.setContentTitle(title).setContentText(content).setContentIntent(pendingIntent)
                .setTicker(title)// 通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
                .setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
                .setAutoCancel(true).setOngoing(false)// TURE是设置他为一个正在进行的通知
                .setDefaults(Notification.DEFAULT_VIBRATE)// 向通知添加通知效果
                .setSmallIcon(R.drawable.ic_launcher);
    }

    // 显示通知信息
    public void showNotice(String title) {
        Notification mNotification = mBuilder.build();
        // 设置通知 消息 图标
        mNotification.icon = R.drawable.ic_launcher;
        // 在通知栏上点击此通知后自动清除此通知
        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
        // 设置显示通知时的默认的发声、震动、Light效果
        mNotification.defaults = Notification.DEFAULT_VIBRATE;
        // 设置发出消息的内容
        mNotification.tickerText = title;
        // 设置发出通知的时间
        mNotification.when = System.currentTimeMillis();
        mNotificationManager.notify(100, mNotification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
