package com.onlyknow.app.service;

import android.annotation.SuppressLint;
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

import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMMessage;
import com.onlyknow.app.R;
import com.onlyknow.app.utils.OKLogUtil;

import java.util.List;

/**
 * 服务基类(基本服务能力);
 * <p>
 * Created by Reset on 2018/05/24.
 */

public class OKBaseService extends Service implements EMMessageListener {
    private final String TAG = "OKBaseService";

    protected NotificationManager mNotificationManager;
    protected NotificationCompat.Builder mBuilder;

    protected SharedPreferences USER_BODY, SETTING_BODY, WEATHER_BODY;

    public final static int WHAT_IM_LOGIN = 990;
    public final static int WHAT_IM_LOGIN_SUCCESS = 991;
    public final static int WHAT_IM_LOGIN_FAILURE = 992;
    public final static int WHAT_IM_LOGOUT_SUCCESS = 993;
    public final static int WHAT_IM_LOGOUT_FAILURE = 994;
    public final static int WHAT_CITY_ID_GET = 995;

    public final static String ACTION_MAIN_SERVICE_LOGIN_IM = "ACTION_MAIN_SERVICE_LOGIN_IM";
    public final static String ACTION_MAIN_SERVICE_LOGOUT_IM = "ACTION_MAIN_SERVICE_LOGOUT_IM";
    public final static String ACTION_MAIN_SERVICE_CREATE_ACCOUNT_IM = "ACTION_MAIN_SERVICE_CREATE_ACCOUNT_IM";
    public final static String ACTION_MAIN_SERVICE_ADD_MESSAGE_LISTENER_IM = "ACTION_MAIN_SERVICE_ADD_MESSAGE_LISTENER_IM";
    public final static String ACTION_MAIN_SERVICE_REMOVE_MESSAGE_LISTENER_IM = "ACTION_MAIN_SERVICE_REMOVE_MESSAGE_LISTENER_IM";
    public final static String ACTION_MAIN_SERVICE_GET_CAROUSE_IMAGE = "ACTION_MAIN_SERVICE_GET_CAROUSE_IMAGE";
    public final static String ACTION_MAIN_SERVICE_GET_WEATHER = "ACTION_MAIN_SERVICE_GET_WEATHER";

    protected final void initUserBody() {
        if (USER_BODY == null) {
            USER_BODY = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        }
    }

    protected final void initSettingBody() {
        if (SETTING_BODY == null) {
            SETTING_BODY = this.getSharedPreferences("setting", Context.MODE_PRIVATE);
        }
    }

    protected final void initWeatherBody() {
        if (WEATHER_BODY == null) {
            WEATHER_BODY = this.getSharedPreferences("weather", Context.MODE_PRIVATE);
        }
    }

    // 注册通知信息
    protected final void initNotice(String title, String content) {

        mBuilder = new NotificationCompat.Builder(this);

        @SuppressLint("WrongConstant")
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, new Intent(), Notification.FLAG_AUTO_CANCEL);

        mBuilder.setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setTicker(title)// 通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
                .setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
                .setAutoCancel(true)
                .setOngoing(false)// TURE是设置他为一个正在进行的通知
                .setDefaults(Notification.DEFAULT_VIBRATE)// 向通知添加通知效果
                .setSmallIcon(R.drawable.ic_launcher);

    }

    // 显示通知信息
    protected final void showNotice(String title) {
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

    @Override
    public void onMessageReceived(List<EMMessage> list) {
        OKLogUtil.print(TAG, "onMessageReceived");
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> list) {
        OKLogUtil.print(TAG, "onCmdMessageReceived");
    }

    @Override
    public void onMessageRead(List<EMMessage> list) {
        OKLogUtil.print(TAG, "onMessageRead");
    }

    @Override
    public void onMessageDelivered(List<EMMessage> list) {
        OKLogUtil.print(TAG, "onMessageDelivered");
    }

    @Override
    public void onMessageRecalled(List<EMMessage> list) {
        OKLogUtil.print(TAG, "onMessageRecalled");
    }

    @Override
    public void onMessageChanged(EMMessage emMessage, Object o) {
        OKLogUtil.print(TAG, "onMessageChanged");
    }
}
