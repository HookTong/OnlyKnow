package com.onlyknow.app.api.user;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.onlyknow.app.api.OKBaseApi;
import com.onlyknow.app.api.OKServiceResult;
import com.onlyknow.app.utils.OKBase64Util;
import com.onlyknow.app.utils.OKNetUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户管理接口 (用户相关操作请调用此接口).
 * <p>
 * 用户登录注册,添加关注,取消关注!
 * 获取用户信息,更新用户信息与头像以及地理位置!
 * <p>
 * Created by Reset on 2018/04/16.
 */

public class OKManagerUserApi extends OKBaseApi {
    private Context context;
    private onCallBack mListener;
    private ManagerUserTask mManagerCardTask;

    public OKManagerUserApi(Context cont) {
        this.context = cont;
    }

    public interface onCallBack {
        void managerUserApiComplete(OKServiceResult<Object> serviceResult, String type, int pos);
    }

    public void requestManagerUser(Params params, onCallBack listener) {
        this.mListener = listener;
        if (OKNetUtil.isNet(context)) {
            cancelTask();
            mManagerCardTask = new ManagerUserTask();
            mManagerCardTask.executeOnExecutor(exec, params);
        } else {
            OKServiceResult<Object> serviceResult = new OKServiceResult<>();
            serviceResult.setSuccess(false);
            serviceResult.setCode(20001);
            serviceResult.setData("");
            serviceResult.setMsg("没有网络连接");
            serviceResult.setTime(new Date().getTime());
            mListener.managerUserApiComplete(serviceResult, params.getType(), params.getPos());
        }
    }

    public void cancelTask() {
        if (mManagerCardTask != null && mManagerCardTask.getStatus() == AsyncTask.Status.RUNNING) {
            mManagerCardTask.cancel(true);
        }
    }

    private class ManagerUserTask extends AsyncTask<Params, Void, OKServiceResult<Object>> {
        private int mPosition;
        private String mType;

        @Override
        protected void onPostExecute(OKServiceResult<Object> serviceResult) {
            super.onPostExecute(serviceResult);
            if (isCancelled()) {
                return;
            }
            mListener.managerUserApiComplete(serviceResult, mType, mPosition);
        }

        @Override
        protected OKServiceResult<Object> doInBackground(Params... params) {
            if (isCancelled()) {
                return null;
            }
            Params mParams = params[0];
            mType = mParams.getType();
            mPosition = mParams.getPos();

            if (Params.TYPE_UPDATE_AVATAR.equals(mType) && !TextUtils.isEmpty(mParams.getAvatarData())) {
                String filePath = mParams.getAvatarData();

                BitmapFactory.Options options = new BitmapFactory.Options();

                options.inPurgeable = true;

                options.inSampleSize = 1; // 表示不压缩

                Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

                mParams.setAvatarData(OKBase64Util.BitmapToBase64(bitmap));
            }

            Map<String, String> map = new HashMap<>();
            map.put(Params.KEY_TYPE, mParams.getType());
            map.put(Params.KEY_NAME, mParams.getUsername());
            map.put(Params.KEY_PASS, mParams.getPassword());

            map.put(Params.KEY_ATTENTION_USER, mParams.getAttentionUsername());
            map.put(Params.KEY_LONGITUDE, String.valueOf(mParams.getLongitude()));
            map.put(Params.KEY_LATITUDE, String.valueOf(mParams.getLatitude()));
            map.put(Params.KEY_AVATAR_DATA, mParams.getAvatarData());
            map.put(Params.KEY_ENTITY, new Gson().toJson(mParams.getEntity()));

            return managerUser(map);
        }
    }

    public static class Params {
        private String type;
        private String username;
        private String password;
        private String attentionUsername;
        private double longitude;
        private double latitude;
        private String avatarData;
        private Object entity;

        private int pos;

        // 必要参数
        public final static String KEY_TYPE = "type";
        public final static String KEY_NAME = "username";
        public final static String KEY_PASS = "password";

        // 可选参数,视type类型而定
        public final static String KEY_ATTENTION_USER = "attentionUsername";
        public final static String KEY_LONGITUDE = "longitude";
        public final static String KEY_LATITUDE = "latitude";
        public final static String KEY_AVATAR_DATA = "avatarData";
        public final static String KEY_ENTITY = "entity";

        // type类型约束
        public final static String TYPE_LOGIN = "login"; // 登录
        public final static String TYPE_REGISTERED = "registered"; // 注册
        public final static String TYPE_ADD_ATTENTION = "addAttention"; //添加关注
        public final static String TYPE_REMOVE_ATTENTION = "deleteAttention"; // 删除关注
        public final static String TYPE_GET_INFO = "getInfo"; // 获取用户信息,无密码
        public final static String TYPE_UPDATE_INFO = "updateInfo"; // 更新用户信息
        public final static String TYPE_UPDATE_AVATAR = "updateAvatar"; // 更新用户头像
        public final static String TYPE_UPDATE_LOCATION = "updateLocation"; // 更新用户位置

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getAttentionUsername() {
            return attentionUsername;
        }

        public void setAttentionUsername(String attentionUsername) {
            this.attentionUsername = attentionUsername;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public String getAvatarData() {
            return avatarData;
        }

        public void setAvatarData(String avatarData) {
            this.avatarData = avatarData;
        }

        public Object getEntity() {
            return entity;
        }

        public void setEntity(Object entity) {
            this.entity = entity;
        }

        public int getPos() {
            return pos;
        }

        public void setPos(int pos) {
            this.pos = pos;
        }
    }
}
