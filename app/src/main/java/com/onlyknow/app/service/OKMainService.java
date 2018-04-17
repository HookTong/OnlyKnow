package com.onlyknow.app.service;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.onlyknow.app.OKConstant;
import com.onlyknow.app.R;
import com.onlyknow.app.api.OKServiceResult;
import com.onlyknow.app.api.app.OKLoadCarouselAdApi;
import com.onlyknow.app.api.user.OKManagerUserApi;
import com.onlyknow.app.database.bean.OKCarouselAdBean;
import com.onlyknow.app.database.bean.OKUserInfoBean;
import com.onlyknow.app.utils.OKCityUtil;
import com.onlyknow.app.utils.OKLogUtil;
import com.onlyknow.app.utils.OKNetUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OKMainService extends OKBaseService implements AMapLocationListener, EMCallBack, EMMessageListener, OKManagerUserApi.onCallBack {
    public static boolean isEMLogIn = false;

    // 广播接收器
    BroadcastReceiver mServiceBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            OKLogUtil.print("MainService 收到广播 : " + action);

            if (ACTION_MAIN_SERVICE_SHOW_NOTICE.equals(action)) {
                Bundle mBundle = intent.getExtras();
                if (mBundle != null) {
                    String title = mBundle.getString("TITLE", "");
                    String msg = mBundle.getString("CONTENT", "");
                    initNotice(title, msg);
                    showNotice(title);
                }
            } else if (ACTION_MAIN_SERVICE_LOGIN_IM.equals(action)) {
                Bundle mBundle = intent.getExtras();
                if (mBundle == null) {
                    return;
                }
                String username = mBundle.getString(OKUserInfoBean.KEY_USERNAME, "");
                String password = mBundle.getString(OKUserInfoBean.KEY_PASSWORD, "");
                if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                    loginIm(username, password);
                } else {
                    OKLogUtil.print("用户试图登录环信但没有账号信息");
                }
            } else if (ACTION_MAIN_SERVICE_LOGOUT_IM.equals(action)) {
                logoutIm();
            } else if (ACTION_MAIN_SERVICE_CREATE_ACCOUNT_IM.equals(action)) {
                Bundle mBundle = intent.getExtras();
                if (mBundle == null) {
                    return;
                }
                String username = mBundle.getString(OKUserInfoBean.KEY_USERNAME, "");
                String password = mBundle.getString(OKUserInfoBean.KEY_PASSWORD, "");
                if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                    createIm(username, password);
                } else {
                    OKLogUtil.print("用户试图创建环信账号但没有账号信息");
                }
            } else if (ACTION_MAIN_SERVICE_ADD_MESSAGE_LISTENER_IM.equals(action)) {
                EMClient.getInstance().chatManager().addMessageListener(OKMainService.this);
            } else if (ACTION_MAIN_SERVICE_REMOVE_MESSAGE_LISTENER_IM.equals(action)) {
                EMClient.getInstance().chatManager().removeMessageListener(OKMainService.this);
            } else if (OKConstant.ACTION_SHOW_NOTICE.equals(action)) {
                Bundle mBundle = intent.getExtras();
                if (mBundle == null) {
                    return;
                }
                int type = mBundle.getInt("TYPE", -1);
                switch (type) {
                    case 0:
                        String title = mBundle.getString("TITLE", "");
                        String content = mBundle.getString("CONTENT", "");
                        initNotice(title, content);
                        showNotice(title);
                        break;
                    default:
                        break;
                }
            } else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);//获取联网状态的NetworkInfo对象
                if (info == null) {
                    return;
                }
                if (NetworkInfo.State.CONNECTED == info.getState() && info.isAvailable()) {//如果当前的网络连接成功并且网络连接可用
                    if (USER_INFO_SP.getBoolean("STATE", false)) { // 登录IM
                        createIm(USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""), USER_INFO_SP.getString(OKUserInfoBean.KEY_PASSWORD, ""));
                    }
                } else {
                    OKLogUtil.print("网络连接已断开");
                }
            } else if (OKConstant.ACTION_RESET_LOCATION.equals(action)) {
                long nowTime = new Date().getTime();
                if ((nowTime - locationInterval) > 10000) {
                    mLocationClient.startLocation();
                    locationInterval = nowTime;
                } else {
                    OKLogUtil.print("用户定位过于频繁!10秒之后再重新定位");
                }
            }
        }
    };

    // 声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient;

    private OKManagerUserApi managerUserApi;

    private long locationInterval = 0;

    // IM 方法
    private void createIm(final String username, final String password) {
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        //注册失败会抛出HyphenateException 同步方法
                        EMClient.getInstance().createAccount(username, password);
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }

                    loginIm(username, password);
                }
            }.start();
        }
    }

    private void loginIm(String username, String password) {
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password) && USER_INFO_SP.getBoolean("STATE", false)) {
            EMClient.getInstance().login(username, password, OKMainService.this);
        }
    }

    private void logoutIm() {
        EMClient.getInstance().logout(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        init();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initUserInfoSp();
        initSettingSp();
        initWeatherSp();
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(ACTION_MAIN_SERVICE_SHOW_NOTICE);
        mIntentFilter.addAction(ACTION_MAIN_SERVICE_LOGIN_IM);
        mIntentFilter.addAction(ACTION_MAIN_SERVICE_LOGOUT_IM);
        mIntentFilter.addAction(ACTION_MAIN_SERVICE_CREATE_ACCOUNT_IM);
        mIntentFilter.addAction(ACTION_MAIN_SERVICE_ADD_MESSAGE_LISTENER_IM);
        mIntentFilter.addAction(ACTION_MAIN_SERVICE_REMOVE_MESSAGE_LISTENER_IM);
        mIntentFilter.addAction(OKConstant.ACTION_SHOW_NOTICE);
        mIntentFilter.addAction(OKConstant.ACTION_RESET_LOCATION);
        mIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mServiceBroadcastReceiver, mIntentFilter);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mServiceBroadcastReceiver);

        EMClient.getInstance().chatManager().removeMessageListener(OKMainService.this);

        mLocationClient.stopLocation();

        mLocationClient.onDestroy();

        if (managerUserApi != null) {
            managerUserApi.cancelTask();
        }

        OKLogUtil.print("---OKMainService.onDestroy--- 重新启动服务");
        initNotice("OnlyKnow严重通知", "OKMainService意外终止,正在尝试重启...");
        showNotice("OnlyKnow严重通知");
        startService(new Intent(this, OKMainService.class));
    }

    private void init() {
        if (OKNetUtil.isNet(this)) {
            if (USER_INFO_SP.getBoolean("STATE", false)) {
                createIm(USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""), USER_INFO_SP.getString(OKUserInfoBean.KEY_PASSWORD, ""));
            }
        }
        mLocationClient = new AMapLocationClient(getApplicationContext()); // 初始化定位
        mLocationClient.setLocationListener(this); // 设置定位回调监听
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption(); // 声明AMapLocationClientOption对象
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy); // 设置定位模式为高精度模式
        mLocationOption.setOnceLocation(true); // 获取一次定位结果 该方法默认为false
        mLocationOption.setOnceLocationLatest(true); // 获取最近3s内精度最高的一次定位结果
        mLocationOption.setInterval(2000); // 设置定位间隔,单位毫秒,默认为2000ms,最低1000ms
        mLocationOption.setNeedAddress(true); // 设置是否返回地址信息,默认返回地址信息
        mLocationOption.setMockEnable(true); // 设置是否允许模拟位置,默认为true,允许模拟位置
        mLocationOption.setHttpTimeOut(20000); // 单位是毫秒,默认30000毫秒,建议超时时间不要低于8000毫秒
        mLocationOption.setLocationCacheEnable(true); // 开启缓存机制
        mLocationClient.setLocationOption(mLocationOption); // 给定位客户端对象设置定位参数
        mLocationClient.startLocation(); // 启动定位
    }

    // 更新用户地理位置
    private void updateUserLocation(AMapLocation amapLocation) {

        double longitude = amapLocation.getLongitude(); // 经度
        double latitude = amapLocation.getLatitude(); // 纬度

        String CityName = amapLocation.getCity();
        String CityCode = amapLocation.getCityCode();
        String CityID = new OKCityUtil(OKMainService.this.getApplicationContext()).getCityID(CityName.replace("市", ""));
        String District = amapLocation.getDistrict();

        // 地理位置保存在用户sp中
        Editor editor = USER_INFO_SP.edit();
        editor.putFloat(OKManagerUserApi.Params.KEY_LONGITUDE, (float) longitude);
        editor.putFloat(OKManagerUserApi.Params.KEY_LATITUDE, (float) latitude);
        editor.putString("CITY_NAME", CityName);
        editor.putString("CITY_CODE", CityCode);
        editor.putString("CITY_ID", CityID);
        editor.putString("DISTRICT", District);
        editor.commit();

        // 更新用户地理位置
        String name = USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, "");
        String pass = USER_INFO_SP.getString(OKUserInfoBean.KEY_PASSWORD, "");
        if (!TextUtils.isEmpty(name) && OKNetUtil.isNet(this)) {

            OKManagerUserApi.Params params = new OKManagerUserApi.Params();
            params.setUsername(name);
            params.setPassword(pass);
            params.setLongitude(longitude);
            params.setLatitude(latitude);
            params.setType(OKManagerUserApi.Params.TYPE_UPDATE_LOCATION);

            if (managerUserApi != null) {
                managerUserApi.cancelTask();
            }
            managerUserApi = new OKManagerUserApi(this);
            managerUserApi.requestManagerUser(params, this);
        }

        OKLogUtil.print("位置信息 经度:" + String.valueOf(longitude) + " 纬度:" + String.valueOf(latitude));

    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                updateUserLocation(aMapLocation); //可在其中解析aMapLocation获取相应内容
            } else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因,errInfo是错误信息,详见错误码表!
                OKLogUtil.print("MapError", "ErrCode:" + aMapLocation.getErrorCode() + ", errInfo:" + aMapLocation.getErrorInfo());
            }
        }
    }

    // 环信登录回调
    @Override
    public void onSuccess() {
        isEMLogIn = true;

        EMClient.getInstance().groupManager().loadAllGroups();
        EMClient.getInstance().chatManager().loadAllConversations();
        EMClient.getInstance().chatManager().addMessageListener(OKMainService.this);
        OKLogUtil.print("登录聊天服务器成功！");
    }

    @Override
    public void onError(int i, String s) {
        isEMLogIn = false;

        OKLogUtil.print("登录聊天服务器失败！");
    }

    @Override
    public void onProgress(int i, String s) {

    }

    // 以下是环信消息接收回调
    @Override
    public void onMessageReceived(List<EMMessage> messages) {
        //收到消息
        if (messages != null && messages.size() != 0) {
            EMClient.getInstance().chatManager().importMessages(messages);
            EMMessage msg = messages.get(messages.size() - 1);
            if (msg.getType() == EMMessage.Type.TXT) {
                String fromName = msg.getFrom();
                initNotice(fromName, ((EMTextMessageBody) msg.getBody()).getMessage());
                showNotice(fromName);
            } else if (msg.getType() == EMMessage.Type.IMAGE) {
                String fromName = msg.getFrom();
                initNotice(fromName, "给您发送了一张图片");
                showNotice(fromName);
            }
        }
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> list) {

    }

    @Override
    public void onMessageRead(List<EMMessage> list) {

    }

    @Override
    public void onMessageDelivered(List<EMMessage> list) {

    }

    @Override
    public void onMessageRecalled(List<EMMessage> list) {

    }

    @Override
    public void onMessageChanged(EMMessage emMessage, Object o) {

    }

    // 更新用户地理位置的结果回调
    @Override
    public void managerUserApiComplete(OKServiceResult<Object> serviceResult, String type, int pos) {
        if (OKManagerUserApi.Params.TYPE_UPDATE_LOCATION.equals(type)) {
            if (serviceResult != null && serviceResult.isSuccess()) {
                OKLogUtil.print("用户地理位置更新成功!");
            } else {
                OKLogUtil.print("用户地理位置更新失败!");
            }
        }
    }
}
