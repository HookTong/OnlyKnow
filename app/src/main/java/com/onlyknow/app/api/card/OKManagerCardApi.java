package com.onlyknow.app.api.card;

import android.content.Context;
import android.os.AsyncTask;

import com.onlyknow.app.api.OKBaseApi;
import com.onlyknow.app.api.OKServiceResult;
import com.onlyknow.app.utils.OKNetUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 卡片管理接口 (卡片相关操作请调用此接口).
 * <p>
 * 卡片点赞,卡片收藏,卡片浏览记录,卡片bind检查!
 * 卡牌移除,卡片收藏移除!
 * <p>
 * Created by Reset on 2018/04/16.
 */

public class OKManagerCardApi extends OKBaseApi {
    private Context context;
    private onCallBack mListener;
    private ManagerCardTask mManagerCardTask;

    public OKManagerCardApi(Context cont) {
        this.context = cont;
    }

    public interface onCallBack {
        void managerCardApiComplete(OKServiceResult<Object> serviceResult, String type, int pos);
    }

    public void requestManagerCard(Params params, onCallBack listener) {
        this.mListener = listener;
        if (OKNetUtil.isNet(context)) {
            cancelTask();
            mManagerCardTask = new ManagerCardTask();
            mManagerCardTask.executeOnExecutor(exec, params);
        } else {
            OKServiceResult<Object> serviceResult = new OKServiceResult<>();
            serviceResult.setSuccess(false);
            serviceResult.setCode(20001);
            serviceResult.setData("");
            serviceResult.setMsg("没有网络连接");
            serviceResult.setTime(new Date().getTime());
            mListener.managerCardApiComplete(serviceResult, params.getType(), params.getPos());
        }
    }

    public void cancelTask() {
        if (mManagerCardTask != null && mManagerCardTask.getStatus() == AsyncTask.Status.RUNNING) {
            mManagerCardTask.cancel(true);
        }
    }

    private class ManagerCardTask extends AsyncTask<Params, Void, OKServiceResult<Object>> {
        private int mPosition;
        private String mType;

        @Override
        protected void onPostExecute(OKServiceResult<Object> serviceResult) {
            super.onPostExecute(serviceResult);
            if (isCancelled()) {
                return;
            }
            mListener.managerCardApiComplete(serviceResult, mType, mPosition);
        }

        @Override
        protected OKServiceResult<Object> doInBackground(Params... params) {
            if (isCancelled()) {
                return null;
            }
            Params mParams = params[0];
            mPosition = mParams.getPos();
            mType = mParams.getType();

            Map<String, String> map = new HashMap<>();
            map.put(Params.KEY_NAME, mParams.getUsername());
            map.put(Params.KEY_PASS, mParams.getPassword());
            map.put(Params.KEY_CARD_ID, String.valueOf(mParams.getCardId()));
            map.put(Params.KEY_TYPE, mParams.getType());
            map.put(Params.KEY_MSG, mParams.getMsg());

            return managerCard(map);
        }
    }

    public static class Params {
        private String type;
        private int cardId;
        private String username;
        private String password;
        private String msg;

        private int pos;

        public final static String KEY_TYPE = "type";
        public final static String KEY_CARD_ID = "cardId";
        public final static String KEY_NAME = "username";
        public final static String KEY_PASS = "password";
        public final static String KEY_MSG = "message";

        public final static String TYPE_REMOVE_CARD = "removeCard";
        public final static String TYPE_PRAISE = "praiseCard";
        public final static String TYPE_WATCH = "watchCard";
        public final static String TYPE_REMOVE_WATCH = "removeWatch";
        public final static String TYPE_BIND_CHECK = "cardBindCheck";
        public final static String TYPE_BROWSING = "browsingCard";

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getCardId() {
            return cardId;
        }

        public void setCardId(int cardId) {
            this.cardId = cardId;
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

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public int getPos() {
            return pos;
        }

        public void setPos(int pos) {
            this.pos = pos;
        }
    }
}
