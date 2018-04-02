package com.onlyknow.app.api;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.onlyknow.app.R;
import com.onlyknow.app.database.bean.OKCardBindBean;
import com.onlyknow.app.database.bean.OKCarouselAndAdImageBean;
import com.onlyknow.app.utils.OKNetUtil;

import java.util.Map;

public class OKCardBindApi extends OKBaseApi {
    private Context context;
    private onCallBack mListener;
    private CardBindTask mCardBindTask;

    public OKCardBindApi(Context cont) {
        this.context = cont;
    }

    public interface onCallBack {
        void cardBindApiComplete(OKCardBindBean bean);
    }

    public void requestCardBindCheck(Map<String, String> map, onCallBack listener) {
        this.mListener = listener;
        if (OKNetUtil.isNet(context)) {
            cancelTask();
            mCardBindTask = new CardBindTask();
            mCardBindTask.executeOnExecutor(exec, map);
        } else {
            mListener.cardBindApiComplete(null);
        }
    }

    public void cancelTask() {
        if (mCardBindTask != null && mCardBindTask.getStatus() == AsyncTask.Status.RUNNING) {
            mCardBindTask.cancel(true);
        }
    }

    private class CardBindTask extends AsyncTask<Map<String, String>, Void, OKCardBindBean> {

        @Override
        protected OKCardBindBean doInBackground(Map<String, String>... params) {
            if (isCancelled()) {
                return null;
            }

            return new OKBusinessApi().getCardBind(params[0]);
        }

        @Override
        protected void onPostExecute(OKCardBindBean cardBindBean) {
            super.onPostExecute(cardBindBean);
            if (isCancelled() || cardBindBean == null) {
                return;
            }

            mListener.cardBindApiComplete(cardBindBean);
        }
    }
}
