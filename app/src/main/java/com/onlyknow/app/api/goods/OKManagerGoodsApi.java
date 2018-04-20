package com.onlyknow.app.api.goods;

import android.content.Context;
import android.os.AsyncTask;

import com.onlyknow.app.api.OKBaseApi;
import com.onlyknow.app.api.OKServiceResult;
import com.onlyknow.app.utils.OKNetUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 商品管理接口 (商品购买相关操作请调用此接口).
 * <p>
 * 商品购买!
 * <p>
 * Created by Reset on 2018/04/16.
 */

public class OKManagerGoodsApi extends OKBaseApi {
    private Context context;
    private onCallBack mListener;
    private ManagerGoodsTask mManagerGoodsTask;

    public OKManagerGoodsApi(Context cont) {
        this.context = cont;
    }

    public interface onCallBack {
        void managerGoodsComplete(OKServiceResult<Object> result, String type, int pos);
    }

    public void requestManagerGoods(Params params, onCallBack listener) {
        this.mListener = listener;
        if (OKNetUtil.isNet(context)) {
            cancelTask();
            mManagerGoodsTask = new ManagerGoodsTask();
            mManagerGoodsTask.executeOnExecutor(exec, params);
        } else {
            OKServiceResult<Object> serviceResult = new OKServiceResult<>();
            serviceResult.setSuccess(false);
            serviceResult.setCode(20001);
            serviceResult.setData("");
            serviceResult.setMsg("没有网络连接");
            serviceResult.setTime(new Date().getTime());
            mListener.managerGoodsComplete(serviceResult, params.getType(), params.getPos());
        }
    }

    public void cancelTask() {
        if (mManagerGoodsTask != null && mManagerGoodsTask.getStatus() == AsyncTask.Status.RUNNING) {
            mManagerGoodsTask.cancel(true);
        }
    }

    private class ManagerGoodsTask extends AsyncTask<Params, Void, OKServiceResult<Object>> {
        private int mPosition;
        private String mType;

        @Override
        protected void onPostExecute(OKServiceResult<Object> result) {
            super.onPostExecute(result);
            if (isCancelled()) {
                return;
            }

            mListener.managerGoodsComplete(result, mType, mPosition);
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
            map.put(Params.KEY_TYPE, mParams.getType());
            map.put(Params.KEY_ID, String.valueOf(mParams.getGdId()));
            map.put(Params.KEY_NAME, mParams.getUsername());
            map.put(Params.KEY_PASS, mParams.getPassword());

            return managerGoods(map, Object.class);
        }
    }

    public static class Params {
        private String type;
        private int gdId;
        private String username;
        private String password;
        private int pos;

        public final static String KEY_TYPE = "type";
        public final static String KEY_ID = "gdId";
        public final static String KEY_NAME = "username";
        public final static String KEY_PASS = "password";

        public final static String TYPE_BUY = "goodsBuy";

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getGdId() {
            return gdId;
        }

        public void setGdId(int gdId) {
            this.gdId = gdId;
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

        public int getPos() {
            return pos;
        }

        public void setPos(int pos) {
            this.pos = pos;
        }
    }
}
