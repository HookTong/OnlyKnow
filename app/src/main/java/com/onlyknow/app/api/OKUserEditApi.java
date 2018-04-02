package com.onlyknow.app.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.onlyknow.app.utils.OKBase64Util;
import com.onlyknow.app.utils.OKLogUtil;
import com.onlyknow.app.utils.OKNetUtil;

import java.util.Map;

public class OKUserEditApi extends OKBaseApi {
    private Context context;
    private onCallBack mListener;
    private UserEditTask mUserEditTask;

    public final static String TYPE_UPDATE_USER_INFO = "UpdateEditInfo";
    public final static String TYPE_UPDATE_HEAD_PORTRAIT = "UpdateHeadPortrait";

    public OKUserEditApi(Context cont) {
        this.context = cont;
    }

    public interface onCallBack {
        void userEditApiComplete(boolean b, String type);
    }

    public void requestUserEdit(Map<String, String> map, String type, onCallBack listener) {
        this.mListener = listener;
        if (OKNetUtil.isNet(context)) {
            Params mParams = new Params();
            mParams.setMap(map);
            mParams.setType(type);
            cancelTask();
            mUserEditTask = new UserEditTask();
            mUserEditTask.executeOnExecutor(exec, mParams);
        } else {
            mListener.userEditApiComplete(false, type);
        }
    }

    public void cancelTask() {
        if (mUserEditTask != null && mUserEditTask.getStatus() == AsyncTask.Status.RUNNING) {
            mUserEditTask.cancel(true);
        }
    }

    private class UserEditTask extends AsyncTask<Params, Void, Boolean> {

        private String Type = "";

        @Override
        protected Boolean doInBackground(Params... params) {
            if (isCancelled()) {
                return false;
            }

            Params mParams = params[0];
            this.Type = mParams.getType();
            Map<String, String> reqParams = mParams.getMap();

            if (this.Type.equals(TYPE_UPDATE_USER_INFO)) {

                return new OKBusinessApi().updateUserInfo(reqParams);

            } else if (this.Type.equals(TYPE_UPDATE_HEAD_PORTRAIT)) {
                String filePath = reqParams.get("baseimag");

                BitmapFactory.Options options = new BitmapFactory.Options();

                options.inPurgeable = true;

                options.inSampleSize = 1; // 表示不压缩

                Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

                reqParams.put("baseimag", OKBase64Util.BitmapToBase64(bitmap));

                return new OKBusinessApi().updateHeadPortrait(reqParams);
            } else {
                OKLogUtil.print("OKUserEditActivity 无效的执行类型");
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (isCancelled()) {
                return;
            }
            mListener.userEditApiComplete(aBoolean, this.Type);
        }
    }

    private class Params {
        Map<String, String> map;
        String Type;

        public Map<String, String> getMap() {
            return map;
        }

        public void setMap(Map<String, String> map) {
            this.map = map;
        }

        public String getType() {
            return Type;
        }

        public void setType(String type) {
            Type = type;
        }
    }
}
