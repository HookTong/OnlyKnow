package com.onlyknow.app.service;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
import com.onlyknow.app.api.OKBusinessApi;
import com.onlyknow.app.database.bean.OKCarouselAndAdImageBean;
import com.onlyknow.app.database.bean.OKUserInfoBean;
import com.onlyknow.app.utils.OKCityUtil;
import com.onlyknow.app.utils.OKDeviceInfoUtil;
import com.onlyknow.app.utils.OKLogUtil;
import com.onlyknow.app.utils.OKNetUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OKMainService extends OKBaseService {

    private EMCallBack mEMCallBackLogIn = new EMCallBack() {
        @Override
        public void onSuccess() {
            EMClient.getInstance().groupManager().loadAllGroups();
            EMClient.getInstance().chatManager().loadAllConversations();
            EMClient.getInstance().chatManager().addMessageListener(msgListener);
            OKLogUtil.print("登录聊天服务器成功！");
        }

        @Override
        public void onError(int i, String s) {
            OKLogUtil.print("登录聊天服务器失败！");
        }

        @Override
        public void onProgress(int i, String s) {
        }
    };

    // 环信消息接收监听器
    private EMMessageListener msgListener = new EMMessageListener() {

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
        public void onCmdMessageReceived(List<EMMessage> messages) {
            //收到透传消息
        }

        @Override
        public void onMessageRead(List<EMMessage> messages) {
            //收到已读回执
        }

        @Override
        public void onMessageDelivered(List<EMMessage> message) {
            //收到已送达回执
        }

        @Override
        public void onMessageRecalled(List<EMMessage> messages) {
            //消息被撤回
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            //消息状态变动
        }
    };

    private BroadcastReceiver serviceBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            OKLogUtil.print("MainService 收到广播 : " + action);
            if (ACTION_MAIN_SERVICE_SHOW_NOTICE.equals(action)) {
                Bundle mBundle = intent.getExtras();
                if (mBundle != null) {
                    String title = mBundle.getString("");
                    String msg = mBundle.getString("");
                    initNotice(title, msg);
                    showNotice(title);
                }
            } else if (ACTION_MAIN_SERVICE_LOGIN_IM.equals(action)) {
                Bundle mBundle = intent.getExtras();
                String username;
                String password;
                if (mBundle != null) {
                    username = mBundle.getString(OKUserInfoBean.KEY_USERNAME);
                    password = mBundle.getString(OKUserInfoBean.KEY_PASSWORD);
                } else {
                    OKLogUtil.print("用户试图登录环信但没有账号信息");
                    return;
                }
                loginIm(username, password);
            } else if (ACTION_MAIN_SERVICE_LOGOUT_IM.equals(action)) {
                logoutIm();
            } else if (ACTION_MAIN_SERVICE_CREATE_ACCOUNT_IM.equals(action)) {
                Bundle mBundle = intent.getExtras();
                if (mBundle != null) {
                    String username = mBundle.getString(OKUserInfoBean.KEY_USERNAME);
                    String password = mBundle.getString(OKUserInfoBean.KEY_PASSWORD);
                    createIm(username, password);
                }
            } else if (ACTION_MAIN_SERVICE_ADD_MESSAGE_LISTENER_IM.equals(action)) {
                EMClient.getInstance().chatManager().addMessageListener(msgListener);
            } else if (ACTION_MAIN_SERVICE_REMOVE_MESSAGE_LISTENER_IM.equals(action)) {
                EMClient.getInstance().chatManager().removeMessageListener(msgListener);
            } else if (OKConstant.ACTION_SHOW_NOTICE.equals(action)) {
                Bundle mBundle = intent.getExtras();
                int type = mBundle.getInt("TYPE");
                switch (type) {
                    case 0:
                        String title = mBundle.getString("TITLE");
                        String content = mBundle.getString("CONTENT");
                        initNotice(title, content);
                        showNotice(title);
                        break;
                    default:
                        break;
                }
            } else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                //获取联网状态的NetworkInfo对象
                NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                if (info != null) {
                    //如果当前的网络连接成功并且网络连接可用
                    if (NetworkInfo.State.CONNECTED == info.getState() && info.isAvailable()) {
                        if (USER_INFO_SP.getBoolean("STATE", false)) { // 登录IM
                            createIm(USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""), USER_INFO_SP.getString(OKUserInfoBean.KEY_PASSWORD, ""));
                        }
                        // 获取轮播图片和广告
                        if (mLoadCarouselAndAdImageTask != null && mLoadCarouselAndAdImageTask.getStatus() == AsyncTask.Status.RUNNING) {
                            mLoadCarouselAndAdImageTask.cancel(true);
                        }
                        mLoadCarouselAndAdImageTask = new LoadCarouselAndAdImageTask(OKMainService.this);
                        mLoadCarouselAndAdImageTask.executeOnExecutor(exec, new OKDeviceInfoUtil(OKMainService.this).getIMIE());
                    } else {
                        OKLogUtil.print("网络连接已断开");
                    }
                }
            }
        }
    };

    // 声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;

    // 声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    //可在其中解析amapLocation获取相应内容
                    updateUserLocation(amapLocation);
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    OKLogUtil.print("MapError", "ErrCode:" + amapLocation.getErrorCode() + ", errInfo:" + amapLocation.getErrorInfo());
                }
            }
        }
    };

    // 声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
    private LoadCarouselAndAdImageTask mLoadCarouselAndAdImageTask;

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
            EMClient.getInstance().login(username, password, mEMCallBackLogIn);
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
        getUserInfoSp();
        getSettingSp();
        getWeatherSp();
        mNotificationManager = (NotificationManager) this.getSystemService(this.NOTIFICATION_SERVICE);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_MAIN_SERVICE_SHOW_NOTICE);
        intentFilter.addAction(ACTION_MAIN_SERVICE_LOGIN_IM);
        intentFilter.addAction(ACTION_MAIN_SERVICE_LOGOUT_IM);
        intentFilter.addAction(ACTION_MAIN_SERVICE_CREATE_ACCOUNT_IM);
        intentFilter.addAction(ACTION_MAIN_SERVICE_ADD_MESSAGE_LISTENER_IM);
        intentFilter.addAction(ACTION_MAIN_SERVICE_REMOVE_MESSAGE_LISTENER_IM);
        intentFilter.addAction(OKConstant.ACTION_SHOW_NOTICE);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(serviceBroadcastReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(serviceBroadcastReceiver);
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
        if (mLoadCarouselAndAdImageTask != null && mLoadCarouselAndAdImageTask.getStatus() == AsyncTask.Status.RUNNING) {
            mLoadCarouselAndAdImageTask.cancel(true);
        }
    }

    private void init() {
        if (OKNetUtil.isNet(this)) {
            if (USER_INFO_SP.getBoolean("STATE", false)) {
                createIm(USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""), USER_INFO_SP.getString(OKUserInfoBean.KEY_PASSWORD, ""));
            }
            // 获取轮播图片和广告
            if (mLoadCarouselAndAdImageTask != null && mLoadCarouselAndAdImageTask.getStatus() == AsyncTask.Status.RUNNING) {
                mLoadCarouselAndAdImageTask.cancel(true);
            }
            mLoadCarouselAndAdImageTask = new LoadCarouselAndAdImageTask(this);
            mLoadCarouselAndAdImageTask.executeOnExecutor(exec, new OKDeviceInfoUtil(this).getIMIE());
        }
        // 初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        // 设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        // 设置定位模式为AMapLocationMode.Hight_Accuracy,高精度模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        // 获取一次定位结果 该方法默认为false
        mLocationOption.setOnceLocation(true);
        // 获取最近3s内精度最高的一次定位结果
        mLocationOption.setOnceLocationLatest(true);
        // 设置定位间隔,单位毫秒,默认为2000ms,最低1000ms
        mLocationOption.setInterval(1000);
        // 设置是否返回地址信息,默认返回地址信息
        mLocationOption.setNeedAddress(true);
        // 设置是否允许模拟位置,默认为true,允许模拟位置
        mLocationOption.setMockEnable(true);
        // 单位是毫秒,默认30000毫秒,建议超时时间不要低于8000毫秒
        mLocationOption.setHttpTimeOut(20000);
        // 开启缓存机制
        mLocationOption.setLocationCacheEnable(true);
        // 给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        // 启动定位
        mLocationClient.startLocation();
    }

    // 更新用户地理位置
    private void updateUserLocation(AMapLocation amapLocation) {
        double LONGITUDE = amapLocation.getLongitude(); // 经度
        double DIMENSION = amapLocation.getLongitude(); // 纬度
        String CityName = amapLocation.getCity();
        String CityCode = amapLocation.getCityCode();
        String CityID = new OKCityUtil(OKMainService.this.getApplicationContext()).getCityID(CityName.replace("市", ""));
        String District = amapLocation.getDistrict();

        OKLogUtil.print("LONGITUDE: " + String.valueOf(LONGITUDE) + "DIMENSION: " + String.valueOf(DIMENSION));

        // 地理位置保存在用户sp中
        Editor editor = USER_INFO_SP.edit();
        editor.putFloat("LONGITUDE", (float) LONGITUDE);
        editor.putFloat("DIMENSION", (float) DIMENSION);
        editor.putString("CITY_NAME", CityName);
        editor.putString("CITY_CODE", CityCode);
        editor.putString("CITY_ID", CityID);
        editor.putString("DISTRICT", District);
        editor.commit();

        // 更新用户地理位置
        if (!TextUtils.isEmpty(USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, "")) && OKNetUtil.isNet(OKMainService.this)) {
            final Map<String, String> map = new HashMap<>();
            map.put("username", USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
            map.put("longitude", Double.toString(LONGITUDE));
            map.put("dimension", Double.toString(DIMENSION));
            map.put("date", OKConstant.getNowDate());

            new Thread() {
                @Override
                public void run() {
                    super.run();
                    new OKBusinessApi().addUserLocation(map);
                }
            }.start();
        }
    }

    private class LoadCarouselAndAdImageTask extends AsyncTask<String, Void, OKCarouselAndAdImageBean> {
        private Context mContext;

        public LoadCarouselAndAdImageTask(Context context) {
            this.mContext = context;
        }

        @Override
        protected OKCarouselAndAdImageBean doInBackground(String... params) {
            if (isCancelled()) {
                return null;
            }
            return new OKBusinessApi().getOKCarouselAndAdImageBean(params[0]);
        }

        @Override
        protected void onPostExecute(OKCarouselAndAdImageBean bean) {
            super.onPostExecute(bean);
            if (isCancelled()) {
                OKLogUtil.print("获取轮播图片和广告图片的线程意外结束");
                return;
            }
            if (bean == null) {
                OKLogUtil.print("获取轮播图片和广告图片失败");
                return;
            }
            // 更新轮播图片,RES_ID为错图替代
            List<Map<String, Object>> headList = new ArrayList<>();
            Map<String, Object> map1 = new HashMap<>();
            map1.put("URL", bean.getHP_IMAGE_URL1());
            map1.put("RES_ID", R.drawable.topgd1);
            Map<String, Object> map2 = new HashMap<>();
            map2.put("URL", bean.getHP_IMAGE_URL2());
            map2.put("RES_ID", R.drawable.topgd2);
            Map<String, Object> map3 = new HashMap<>();
            map3.put("URL", bean.getHP_IMAGE_URL3());
            map3.put("RES_ID", R.drawable.topgd3);
            Map<String, Object> map4 = new HashMap<>();
            map4.put("URL", bean.getHP_IMAGE_URL4());
            map4.put("RES_ID", R.drawable.topgd4);
            Map<String, Object> map5 = new HashMap<>();
            map5.put("URL", bean.getHP_IMAGE_URL5());
            map5.put("RES_ID", R.drawable.topgd5);
            headList.add(map1);
            headList.add(map2);
            headList.add(map3);
            headList.add(map4);
            headList.add(map5);
            OKConstant.setHeadUrls(headList);

            // 更新广告URL
            ArrayList<Map<String, String>> adList = new ArrayList<>();
            Map<String, String> adMap1 = new HashMap<>();
            adMap1.put("URL", bean.getAD_IMAGE_URL1());
            adMap1.put("LINK", bean.getAD_LINK_URL1());
            Map<String, String> adMap2 = new HashMap<>();
            adMap2.put("URL", bean.getAD_IMAGE_URL2());
            adMap2.put("LINK", bean.getAD_LINK_URL2());
            Map<String, String> adMap3 = new HashMap<>();
            adMap3.put("URL", bean.getAD_IMAGE_URL3());
            adMap3.put("LINK", bean.getAD_LINK_URL3());
            adList.add(adMap1);
            adList.add(adMap2);
            adList.add(adMap3);
            OKConstant.setAdUrls(adList);

            Intent mIntent = new Intent(OKConstant.ACTION_UPDATE_CAROUSE_AND_AD_IMAGE);
            mContext.sendBroadcast(mIntent);
        }
    }
}
