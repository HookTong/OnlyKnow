package com.onlyknow.app.api.user;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.onlyknow.app.api.OKBaseApi;
import com.onlyknow.app.api.OKServiceResult;
import com.onlyknow.app.utils.OKBase64Util;
import com.onlyknow.app.utils.OKNetUtil;

import java.util.HashMap;
import java.util.Map;

public class OKFeedBackApi extends OKBaseApi {
    private Context context;
    private onCallBack mListener;
    private FeedBackTask mFeedBackTask;

    public OKFeedBackApi(Context cont) {
        this.context = cont;
    }

    public interface onCallBack {
        void feedBackApiComplete(OKServiceResult<Object> result);
    }

    public void requestFeedBack(Params params, onCallBack listener) {
        this.mListener = listener;
        if (OKNetUtil.isNet(context)) {
            cancelTask();
            mFeedBackTask = new FeedBackTask();
            mFeedBackTask.executeOnExecutor(exec, params);
        } else {
            mListener.feedBackApiComplete(null);
        }
    }

    public void cancelTask() {
        if (mFeedBackTask != null && mFeedBackTask.getStatus() == AsyncTask.Status.RUNNING) {
            mFeedBackTask.cancel(true);
        }
    }

    private class FeedBackTask extends AsyncTask<Params, Void, OKServiceResult<Object>> {
        @Override
        protected OKServiceResult<Object> doInBackground(Params... params) {
            if (isCancelled()) {
                return null;
            }

            Params mParams = params[0];

            Map<String, String> map = new HashMap<>();

            map.put(Params.KEY_NAME, mParams.getUsername());

            map.put(Params.KEY_MSG, mParams.getMessage());

            String filePath = mParams.getImage();

            if (!TextUtils.isEmpty(filePath)) {

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPurgeable = true;
                options.inSampleSize = 1;
                Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

                map.put(Params.KEY_IMAGE, OKBase64Util.BitmapToBase64(bitmap));
            }

            return feedBack(map);
        }

        @Override
        protected void onPostExecute(OKServiceResult<Object> result) {
            super.onPostExecute(result);
            if (isCancelled()) {
                return;
            }

            mListener.feedBackApiComplete(result);
        }
    }

    public static class Params {
        private String username;
        private String message;
        private String image;

        public final static String KEY_NAME = "username";
        public final static String KEY_MSG = "message";
        public final static String KEY_IMAGE = "image";

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }
}
