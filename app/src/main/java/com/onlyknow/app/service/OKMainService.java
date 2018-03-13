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
import com.onlyknow.app.database.bean.OKCarouselAndAdImageBean;
import com.onlyknow.app.database.bean.OKUserInfoBean;
import com.onlyknow.app.net.OKBusinessNet;
import com.onlyknow.app.utils.OKCityUtil;
import com.onlyknow.app.utils.OKDeviceInfoUtil;
import com.onlyknow.app.utils.OKLogUtil;
import com.onlyknow.app.utils.OKNetUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OKMainService extends OKBaseService {
    // 环信登录结果回调
    private EMCallBack mEMCallBackLogIn = new EMCallBack() {
        @Override
        public void onSuccess() {
            EMClient.getInstance().groupManager().loadAllGroups();
            EMClient.getInstance().chatManager().loadAllConversations();
            EMClient.getInstance().chatManager().addMessageListener(mMsgListener);
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
    private EMMessageListener mMsgListener = new EMMessageListener() {

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

    // 广播接收器
    private BroadcastReceiver mServiceBroadcastReceiver = new BroadcastReceiver() {
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
                EMClient.getInstance().chatManager().addMessageListener(mMsgListener);
            } else if (ACTION_MAIN_SERVICE_REMOVE_MESSAGE_LISTENER_IM.equals(action)) {
                EMClient.getInstance().chatManager().removeMessageListener(mMsgListener);
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
                    // 获取轮播图片和广告
                    if (mLoadCarouselAndAdImageTask != null && mLoadCarouselAndAdImageTask.getStatus() == AsyncTask.Status.RUNNING) {
                        mLoadCarouselAndAdImageTask.cancel(true);
                    }
                    mLoadCarouselAndAdImageTask = new LoadCarouselAndAdImageTask(OKMainService.this);
                    mLoadCarouselAndAdImageTask.executeOnExecutor(exec);
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

    private long locationInterval = 0;

    // 声明定位回调监听器
    private AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    updateUserLocation(amapLocation); //可在其中解析aMapLocation获取相应内容
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因,errInfo是错误信息,详见错误码表!
                    OKLogUtil.print("MapError", "ErrCode:" + amapLocation.getErrorCode() + ", errInfo:" + amapLocation.getErrorInfo());
                }
            }
        }
    };

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
        EMClient.getInstance().chatManager().removeMessageListener(mMsgListener);
        if (mLoadCarouselAndAdImageTask != null && mLoadCarouselAndAdImageTask.getStatus() == AsyncTask.Status.RUNNING) {
            mLoadCarouselAndAdImageTask.cancel(true);
        }
        mLocationClient.stopLocation();
        mLocationClient.onDestroy();
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
            mLoadCarouselAndAdImageTask.executeOnExecutor(exec);
        }
        mLocationClient = new AMapLocationClient(getApplicationContext()); // 初始化定位
        mLocationClient.setLocationListener(mLocationListener); // 设置定位回调监听
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
        double LONGITUDE = amapLocation.getLongitude(); // 经度
        double DIMENSION = amapLocation.getLongitude(); // 纬度
        String CityName = amapLocation.getCity();
        String CityCode = amapLocation.getCityCode();
        String CityID = new OKCityUtil(OKMainService.this.getApplicationContext()).getCityID(CityName.replace("市", ""));
        String District = amapLocation.getDistrict();
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
            map.put("date", OKConstant.getNowDateByString());

            new Thread() {
                @Override
                public void run() {
                    super.run();
                    new OKBusinessNet().addUserLocation(map);
                }
            }.start();
        }
        OKLogUtil.print("LONGITUDE: " + String.valueOf(LONGITUDE) + "DIMENSION: " + String.valueOf(DIMENSION));
    }

    private class LoadCarouselAndAdImageTask extends AsyncTask<Void, Void, OKCarouselAndAdImageBean> {
        private Context mContext;

        public LoadCarouselAndAdImageTask(Context context) {
            this.mContext = context;
        }

        @Override
        protected OKCarouselAndAdImageBean doInBackground(Void... params) {
            if (isCancelled()) {
                return null;
            }
            return new OKBusinessNet().getOKCarouselAndAdImageBean(new OKDeviceInfoUtil(OKMainService.this).getIMIE());
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
            List<Map<String, String>> adList = new ArrayList<>();
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
