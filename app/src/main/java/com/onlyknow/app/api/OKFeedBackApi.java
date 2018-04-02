package com.onlyknow.app.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.onlyknow.app.utils.OKBase64Util;
import com.onlyknow.app.utils.OKNetUtil;

import java.util.Map;

public class OKFeedBackApi extends OKBaseApi {
    private Context context;
    private onCallBack mListener;
    private FeedBackTask mFeedBackTask;

    public OKFeedBackApi(Context cont) {
        this.context = cont;
    }

    public interface onCallBack {
        void feedBackApiComplete(boolean isSuccess);
    }

    public void requestFeedBack(Map<String, String> reqMap, onCallBack listener) {
        this.mListener = listener;
        if (OKNetUtil.isNet(context)) {
            cancelTask();
            mFeedBackTask = new FeedBackTask();
            mFeedBackTask.executeOnExecutor(exec, reqMap);
        } else {
            mListener.feedBackApiComplete(false);
        }
    }

    public void cancelTask() {
        if (mFeedBackTask != null && mFeedBackTask.getStatus() == AsyncTask.Status.RUNNING) {
            mFeedBackTask.cancel(true);
        }
    }

    private class FeedBackTask extends AsyncTask<Map<String, String>, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Map<String, String>... params) {
            if (isCancelled()) {
                return false;
            }

            Map<String, String> map = params[0];
            String filePath = map.get("baseimag");
            if (!TextUtils.isEmpty(filePath)) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPurgeable = true;
                options.inSampleSize = 1;
                Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
                map.put("baseimag", OKBase64Util.BitmapToBase64(bitmap));
            }
            return new OKBusinessApi().feedBack(map);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (isCancelled()) {
                return;
            }

            mListener.feedBackApiComplete(aBoolean);
        }
    }
}
