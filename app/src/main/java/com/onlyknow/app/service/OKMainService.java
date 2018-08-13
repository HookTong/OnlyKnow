package com.onlyknow.app.service;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.onlyknow.app.OKConstant;
import com.onlyknow.app.api.OKServiceResult;
import com.onlyknow.app.api.app.OKLoadCarouselAdApi;
import com.onlyknow.app.api.app.OKLoadWeatherApi;
import com.onlyknow.app.api.user.OKManagerUserApi;
import com.onlyknow.app.db.bean.OKCarouselAdBean;
import com.onlyknow.app.db.bean.OKUserInfoBean;
import com.onlyknow.app.db.bean.OKWeatherBean;
import com.onlyknow.app.utils.OKCityUtil;
import com.onlyknow.app.utils.OKLogUtil;
import com.onlyknow.app.utils.OKNetUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OKMainService extends OKBaseService implements AMapLocationListener,
        OKManagerUserApi.onCallBack, OKMainBroadcastReceiver.MainReceiver,
        OKLoadCarouselAdApi.onCallBack, OKLoadWeatherApi.onCallBack, OKCoordinator.CoordinatorCallBack {
    private static final String TAG = "OKMainService";

    public interface NoticeCallBack {
        void onLocationChanged(AMapLocation location);

        void onImStatusChanged(boolean isOnline);

        void onCarouselImageChanged(OKCarouselAdBean bean);

        void onWeatherChanged(OKWeatherBean bean);
    }

    public static boolean isEMLogIn = false;

    // 广播接收器
    private OKMainBroadcastReceiver serviceReceiver;

    private OKCoordinator coordinator;

    // 声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient;

    private OKManagerUserApi managerUserApi;

    private static Map<Integer, NoticeCallBack> noticeCallBackQueue = new HashMap<>();

    public static int addNoticeCallBack(NoticeCallBack callBack) {
        noticeIndex = noticeIndex + 1;
        noticeCallBackQueue.put(noticeIndex, callBack);
        int index = noticeIndex;
        return index;
    }

    public static void removeNoticeCallBack(int index) {
        NoticeCallBack callBack = noticeCallBackQueue.get(index);
        if (callBack != null) {
            noticeCallBackQueue.remove(index);
        }
    }

    private long locationInterval = 0;

    private static int noticeIndex = -1;

    // 创建IM账号并登录
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

                        OKLogUtil.print(TAG, "创建失败(可能因为该账号已创建过), Msg: " + e.getMessage());
                    }

                    loginIm(username, password);

                    // Message ms = new Message();
                    // Bundle bundle = new Bundle();
                    // bundle.putString("username", username);
                    // bundle.putString("password", password);
                    // ms.setData(bundle);
                    // ms.what = WHAT_IM_LOGIN;
                    // coordinator.sendCoordinatorMessage(ms);
                }
            }.start();
        }
    }

    // 登录IM账号
    private void loginIm(String username, String password) {
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)
                && USER_BODY.getBoolean("STATE", false)) {
            EMClient.getInstance().login(username, password, new EMCallBack() {
                @Override
                public void onSuccess() {
                    isEMLogIn = true;

                    EMClient.getInstance().groupManager().loadAllGroups();
                    EMClient.getInstance().chatManager().loadAllConversations();
                    EMClient.getInstance().chatManager().addMessageListener(OKMainService.this);

                    Message ms = coordinator.obtainMessage();
                    ms.what = WHAT_IM_LOGIN_SUCCESS;
                    coordinator.sendCoordinatorMessage(ms);

                    OKLogUtil.print(TAG, "登录聊天服务器成功！");
                }

                @Override
                public void onError(int i, String s) {
                    isEMLogIn = false;

                    Message ms = coordinator.obtainMessage();
                    ms.what = WHAT_IM_LOGIN_FAILURE;
                    coordinator.sendCoordinatorMessage(ms);

                    OKLogUtil.print(TAG, "登录聊天服务器失败！");
                }

                @Override
                public void onProgress(int i, String s) {
                }
            });
        }
    }

    // 登出当前登录的IM账号
    private void logoutIm() {
        EMClient.getInstance().logout(true, new EMCallBack() {
            @Override
            public void onSuccess() {
                Message ms = coordinator.obtainMessage();
                ms.what = WHAT_IM_LOGOUT_SUCCESS;
                coordinator.sendCoordinatorMessage(ms);

                OKLogUtil.print(TAG, "当前环信账号登出成功");
            }

            @Override
            public void onError(int i, String s) {
                Message ms = coordinator.obtainMessage();
                ms.what = WHAT_IM_LOGOUT_FAILURE;
                coordinator.sendCoordinatorMessage(ms);

                OKLogUtil.print(TAG, "当前环信账号登出失败, Msg: " + s);
            }

            @Override
            public void onProgress(int i, String s) {
            }
        });
    }

    // 获取轮播图片
    private void loadCarouselImage() {
        if (mLoadCarouselAdApi != null) {
            mLoadCarouselAdApi.cancelTask();
        }
        mLoadCarouselAdApi = new OKLoadCarouselAdApi(this);
        OKLoadCarouselAdApi.Params params = new OKLoadCarouselAdApi.Params();
        params.setType(OKLoadCarouselAdApi.Params.TYPE_NEW);

        mLoadCarouselAdApi.requestCarouselAd(params, this);
    }

    // 获取天气信息
    private void loadWeather() {
        if (mLoadWeatherApi != null) {
            mLoadWeatherApi.cancelTask();
        }
        mLoadWeatherApi = new OKLoadWeatherApi(this);
        OKLoadWeatherApi.Params params = new OKLoadWeatherApi.Params();
        params.setCityId(USER_BODY.getString("CITY_ID", ""));
        params.setCityName(USER_BODY.getString("CITY_NAME", ""));
        params.setDistrict(USER_BODY.getString("DISTRICT", ""));
        mLoadWeatherApi.requestWeather(params, this);
    }

    // 返回位置信息到前台线程
    private void backLocationChanged(AMapLocation location) {
        if (location == null) return;

        NoticeCallBack callBack;
        for (Map.Entry<Integer, NoticeCallBack> entry : noticeCallBackQueue.entrySet()) {
            callBack = entry.getValue();
            if (callBack != null) {
                callBack.onLocationChanged(location);
            }
        }
    }

    // 返回Im状态到前台线程
    private void backImStatusChanged(boolean isOnline) {
        NoticeCallBack callBack;
        for (Map.Entry<Integer, NoticeCallBack> entry : noticeCallBackQueue.entrySet()) {
            callBack = entry.getValue();
            if (callBack != null) {
                callBack.onImStatusChanged(isOnline);
            }
        }
    }

    // 返回天气信息到前台线程
    private void backWeatherChanged(OKWeatherBean bean) {
        if (bean == null) return;

        NoticeCallBack callBack;
        for (Map.Entry<Integer, NoticeCallBack> entry : noticeCallBackQueue.entrySet()) {
            callBack = entry.getValue();
            if (callBack != null) {
                callBack.onWeatherChanged(bean);
            }
        }
    }

    // 返回轮播图片到前台线程
    private void backCarouselImageChanged(OKCarouselAdBean bean) {
        if (bean == null) return;

        NoticeCallBack callBack;
        for (Map.Entry<Integer, NoticeCallBack> entry : noticeCallBackQueue.entrySet()) {
            callBack = entry.getValue();
            if (callBack != null) {
                callBack.onCarouselImageChanged(bean);
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        initUserBody();
        initSettingBody();
        initWeatherBody();

        serviceReceiver = new OKMainBroadcastReceiver(this);

        serviceReceiver.registered(this);

        coordinator = new OKCoordinator(this);

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        init();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EMClient.getInstance().chatManager().removeMessageListener(OKMainService.this);

        serviceReceiver.unregistered();

        mLocationClient.stopLocation();

        mLocationClient.onDestroy();

        coordinator = null;

        if (managerUserApi != null) {
            managerUserApi.cancelTask();
        }

        if (mLoadCarouselAdApi != null) {
            mLoadCarouselAdApi.cancelTask();
        }

        if (mLoadWeatherApi != null) {
            mLoadWeatherApi.cancelTask();
        }

        OKLogUtil.print(TAG, "---OKMainService.onDestroy--- 重新启动服务");
        initNotice("OnlyKnow严重通知", "OKMainService意外终止,正在尝试重启...");
        showNotice("OnlyKnow严重通知");
        startService(new Intent(this, OKMainService.class));
    }

    // 服务初始化
    private void init() {
        if (OKNetUtil.isNet(this)) {
            if (USER_BODY.getBoolean("STATE", false)) {
                String name = USER_BODY.getString(OKUserInfoBean.KEY_USERNAME, "");
                String pass = USER_BODY.getString(OKUserInfoBean.KEY_PASSWORD, "");
                createIm(name, pass);
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

        loadCarouselImage();
    }

    private AMapLocation mapLocation;

    // 定位结果回调
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation == null) {
            OKLogUtil.print(TAG, "onLocationChanged AMapLocation is Null");
            return;
        }
        if (amapLocation.getErrorCode() == 0) {
            mapLocation = amapLocation;

            double longitude = amapLocation.getLongitude(); // 经度
            double latitude = amapLocation.getLatitude(); // 纬度

            String cityName = amapLocation.getCity();
            String cityCode = amapLocation.getCityCode();
            String district = amapLocation.getDistrict();

            // 地理位置保存在用户sp中
            Editor editor = USER_BODY.edit();
            editor.putFloat(OKManagerUserApi.Params.KEY_LONGITUDE, (float) longitude);
            editor.putFloat(OKManagerUserApi.Params.KEY_LATITUDE, (float) latitude);
            editor.putString("CITY_NAME", cityName);
            editor.putString("CITY_CODE", cityCode);
            editor.putString("DISTRICT", district);
            editor.commit();

            Editor weatherEditor = WEATHER_BODY.edit();
            weatherEditor.putFloat(OKManagerUserApi.Params.KEY_LONGITUDE, (float) longitude);
            weatherEditor.putFloat(OKManagerUserApi.Params.KEY_LATITUDE, (float) latitude);
            weatherEditor.putString("CITY_NAME", cityName);
            weatherEditor.putString("CITY_CODE", cityCode);
            weatherEditor.putString("DISTRICT", district);
            weatherEditor.commit();

            // 更新用户地理位置
            String name = USER_BODY.getString(OKUserInfoBean.KEY_USERNAME, "");
            String pass = USER_BODY.getString(OKUserInfoBean.KEY_PASSWORD, "");
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

            // 城市名称需要移除后缀
            String cityNameTmp = cityName.replace("市", "").replace(" ", "");
            // 请求异步获取CityId
            OKCityUtil.requestCityID(this, cityNameTmp, coordinator);

            OKLogUtil.print(TAG, "位置信息: " + mapLocation.toString());
        } else {
            //定位失败时,可通过ErrCode(错误码)信息来确定失败的原因,errInfo是错误信息,详见错误码表.
            OKLogUtil.print(TAG, "定位失败, ErrorCode: " + amapLocation.getErrorCode()
                    + ", ErrorInfo: " + amapLocation.getErrorInfo());
        }
    }

    // 以下是环信消息接收回调
    @Override
    public void onMessageReceived(List<EMMessage> messages) {
        super.onMessageReceived(messages);

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

        OKLogUtil.print(TAG, "收到新消息");
    }

    // 更新用户地理位置的结果回调
    @Override
    public void managerUserComplete(OKServiceResult<Object> result, String type, int pos) {
        if (OKManagerUserApi.Params.TYPE_UPDATE_LOCATION.equals(type)) {
            if (result != null && result.isSuccess()) {
                backLocationChanged(mapLocation);

                OKLogUtil.print(TAG, "用户地理位置更新成功!");
            } else {
                OKLogUtil.print(TAG, "用户地理位置更新失败!");
            }
        }
    }

    @Override
    public void onMainReceiver(Context context, Intent intent) {
        String action = intent.getAction();
        if (ACTION_MAIN_SERVICE_LOGIN_IM.equals(action)) {
            Bundle mBundle = intent.getExtras();
            if (mBundle == null) {
                return;
            }
            String username = mBundle.getString(OKUserInfoBean.KEY_USERNAME, "");
            String password = mBundle.getString(OKUserInfoBean.KEY_PASSWORD, "");
            if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                loginIm(username, password);

                OKLogUtil.print(TAG, "用户开始登录环信账号");
            } else {
                OKLogUtil.print(TAG, "用户试图登录环信但没有账号信息");
            }
        } else if (ACTION_MAIN_SERVICE_LOGOUT_IM.equals(action)) {
            logoutIm();

            OKLogUtil.print(TAG, "用户开始登出环信账号");
        } else if (ACTION_MAIN_SERVICE_CREATE_ACCOUNT_IM.equals(action)) {
            Bundle mBundle = intent.getExtras();
            if (mBundle == null) {
                return;
            }
            String username = mBundle.getString(OKUserInfoBean.KEY_USERNAME, "");
            String password = mBundle.getString(OKUserInfoBean.KEY_PASSWORD, "");
            if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                createIm(username, password);

                OKLogUtil.print(TAG, "用户开始创建环信账号");
            } else {
                OKLogUtil.print(TAG, "用户试图创建环信账号但没有账号信息");
            }
        } else if (ACTION_MAIN_SERVICE_ADD_MESSAGE_LISTENER_IM.equals(action)) {
            EMClient.getInstance().chatManager().addMessageListener(OKMainService.this);

            OKLogUtil.print(TAG, "用户添加了环信消息监听器");
        } else if (ACTION_MAIN_SERVICE_REMOVE_MESSAGE_LISTENER_IM.equals(action)) {
            EMClient.getInstance().chatManager().removeMessageListener(OKMainService.this);

            OKLogUtil.print(TAG, "用户移除了环信消息监听器");
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

                    OKLogUtil.print(TAG, "用户发送了一个通知");
                    break;
                default:
                    break;
            }
        } else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);//获取联网状态的NetworkInfo对象
            if (info == null) return;
            if (NetworkInfo.State.CONNECTED == info.getState() && info.isAvailable()) {//如果当前的网络连接成功并且网络连接可用
                if (!EMClient.getInstance().isConnected()) {
                    if (USER_BODY.getBoolean("STATE", false)) { // 登录IM
                        String name = USER_BODY.getString(OKUserInfoBean.KEY_USERNAME, "");
                        String pass = USER_BODY.getString(OKUserInfoBean.KEY_PASSWORD, "");
                        createIm(name, pass);
                    }
                }

                OKLogUtil.print(TAG, "网络已连接");
            } else {
                OKLogUtil.print(TAG, "网络连接已断开");
            }
        } else if (OKConstant.ACTION_RESET_LOCATION.equals(action)) {
            long nowTime = new Date().getTime();
            if ((nowTime - locationInterval) > 10000) {
                mLocationClient.startLocation();
                locationInterval = nowTime;

                OKLogUtil.print(TAG, "开始定位");
            } else {
                OKLogUtil.print(TAG, "用户定位过于频繁!10秒之后再重新定位");
            }
        } else if (ACTION_MAIN_SERVICE_GET_CAROUSE_IMAGE.equals(action)) {
            if (mCarouselAdBean == null) {
                loadCarouselImage();
            } else {
                if (((new Date().getTime()) - mCarouselAdBean.getRequestTime() >= 120000)) {
                    loadCarouselImage();
                } else {
                    backCarouselImageChanged(mCarouselAdBean);
                }
            }

            OKLogUtil.print(TAG, "用户请求获取轮播图片");
        } else if (ACTION_MAIN_SERVICE_GET_WEATHER.equals(action)) {
            if (mWeatherBean == null) {
                loadWeather();
            } else {
                if (((new Date().getTime()) - mWeatherBean.getRequestTime() >= 120000)) {
                    loadWeather();
                } else {
                    backWeatherChanged(mWeatherBean);
                }
            }

            OKLogUtil.print(TAG, "用户请求获取天气信息");
        }
    }

    private OKLoadCarouselAdApi mLoadCarouselAdApi;

    private OKCarouselAdBean mCarouselAdBean;

    @Override
    public void loadCarouselAdComplete(OKCarouselAdBean bean) {
        if (bean != null) {
            bean.getCarouselImages();
            bean.getAdImages();
            mCarouselAdBean = bean;
            mCarouselAdBean.setRequestTime(new Date().getTime());
            backCarouselImageChanged(mCarouselAdBean);

            OKLogUtil.print(TAG, "轮播图片获取成功, RequestTime: " + mCarouselAdBean.getRequestTime());
        } else {
            OKLogUtil.print(TAG, "轮播图片获取失败");
        }
    }

    private OKLoadWeatherApi mLoadWeatherApi;

    private OKWeatherBean mWeatherBean;

    @Override
    public void loadWeatherComplete(OKWeatherBean weather) {
        if (weather != null) {
            mWeatherBean = weather;
            mWeatherBean.setRequestTime(new Date().getTime());
            backWeatherChanged(mWeatherBean);

            OKLogUtil.print(TAG, "天气信息获取成功, RequestTime: " + mWeatherBean.getRequestTime());
        } else {
            OKLogUtil.print(TAG, "天气信息获取失败");
        }
    }

    @Override
    public void coordinatorMessage(Message msg) {
        switch (msg.what) {
            case WHAT_IM_LOGIN:
                Bundle bundle = msg.getData();
                String username = bundle.getString("username");
                String password = bundle.getString("password");
                loginIm(username, password);

                OKLogUtil.print(TAG, "执行登录协调消息");
                break;
            case WHAT_IM_LOGIN_SUCCESS:
                backImStatusChanged(true);

                OKLogUtil.print(TAG, "执行登录成功协调消息");
                break;
            case WHAT_IM_LOGIN_FAILURE:
                backImStatusChanged(false);

                OKLogUtil.print(TAG, "执行登录失败协调消息");
                break;
            case WHAT_IM_LOGOUT_SUCCESS:
                backImStatusChanged(false);

                OKLogUtil.print(TAG, "执行登出成功协调消息");
                break;
            case WHAT_IM_LOGOUT_FAILURE:
                backImStatusChanged(true);

                OKLogUtil.print(TAG, "执行登出失败协调消息");
                break;
            case WHAT_CITY_ID_GET:
                String cityID = (String) msg.obj;
                USER_BODY.edit().putString("CITY_ID", cityID).commit();
                WEATHER_BODY.edit().putString("CITY_ID", cityID).commit();
                // 请求获取天气信息
                loadWeather();

                OKLogUtil.print(TAG, "执行CityId获取结果协调消息");
                break;
            default:
                break;
        }

        OKLogUtil.print(TAG, "收到协调消息: " + msg.what);
    }
}
