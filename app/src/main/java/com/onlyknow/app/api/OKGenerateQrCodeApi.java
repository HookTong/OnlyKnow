package com.onlyknow.app.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.onlyknow.app.utils.OKBase64Util;
import com.onlyknow.app.utils.OKNetUtil;
import com.onlyknow.app.utils.OKQRUtil;

import java.util.Map;

public class OKGenerateQrCodeApi extends OKBaseApi {
    private Context context;
    private onCallBack mListener;
    private QrCodeTask mQrCodeTask;

    public OKGenerateQrCodeApi(Context cont) {
        this.context = cont;
    }

    public interface onCallBack {
        void generateQrCodeApiComplete(Bitmap bitmap);
    }

    public void requestGenerateQrCodeApi(Bitmap bitmapTx, int mWidth, String msg, onCallBack listener) {
        this.mListener = listener;
        Params mParams = new Params();
        mParams.setBitmapTx(bitmapTx);
        mParams.setWidth(mWidth);
        mParams.setMsg(msg);
        cancelTask();
        mQrCodeTask = new QrCodeTask();
        mQrCodeTask.executeOnExecutor(exec, mParams);
    }

    public void cancelTask() {
        if (mQrCodeTask != null && mQrCodeTask.getStatus() == AsyncTask.Status.RUNNING) {
            mQrCodeTask.cancel(true);
        }
    }

    private class QrCodeTask extends AsyncTask<Params, Void, Bitmap> {
        private Bitmap mBitmapTx;
        private int mWidth;

        @Override
        protected Bitmap doInBackground(Params... param) {
            try {
                Params mParams = param[0];
                mBitmapTx = mParams.getBitmapTx();
                mWidth = mParams.getWidth();

                Bitmap mBitmapQrCode = OKQRUtil.encodeToQRWidth(mParams.getMsg(), mWidth);
                if (mBitmapTx != null) {
                    mBitmapTx = OKBase64Util.toRoundBitmap(mBitmapTx);
                    mBitmapQrCode = OKQRUtil.addLogo(mBitmapQrCode, mBitmapTx);
                    mBitmapTx.recycle();
                    mBitmapTx = null;
                }

                return mBitmapQrCode;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (isCancelled()) {
                return;
            }
            mListener.generateQrCodeApiComplete(bitmap);
        }
    }

    private class Params {
        Bitmap bitmapTx;
        int width;
        String msg;

        public Bitmap getBitmapTx() {
            return bitmapTx;
        }

        public void setBitmapTx(Bitmap bitmapTx) {
            this.bitmapTx = bitmapTx;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }
}
