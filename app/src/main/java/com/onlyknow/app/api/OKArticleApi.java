package com.onlyknow.app.api;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.onlyknow.app.database.bean.OKCardBase64ListBean;
import com.onlyknow.app.database.bean.OKCardBean;
import com.onlyknow.app.utils.OKLogUtil;
import com.onlyknow.app.utils.OKNetUtil;
import com.onlyknow.app.utils.compress.OKCompressHelper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OKArticleApi extends OKBaseApi {
    private Context context;
    private onCallBack mListener;
    private ArticleTask mArticleTask;

    public OKArticleApi(Context cont) {
        this.context = cont;
    }

    public interface onCallBack {
        void articleApiComplete(boolean isSuccess);
    }

    public void requestPublishArticle(OKCardBean cardBean, OKCardBase64ListBean imageListBean, onCallBack listener) {
        this.mListener = listener;
        if (OKNetUtil.isNet(context)) {
            Params mParams = new Params();
            mParams.setCardBean(cardBean);
            mParams.setImageListBean(imageListBean);
            cancelTask();
            mArticleTask = new ArticleTask();
            mArticleTask.executeOnExecutor(exec, mParams);
        } else {
            mListener.articleApiComplete(false);
        }
    }

    public void cancelTask() {
        if (mArticleTask != null && mArticleTask.getStatus() == AsyncTask.Status.RUNNING) {
            mArticleTask.cancel(true);
        }
    }

    private class ArticleTask extends AsyncTask<Params, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Params... params) {
            if (isCancelled()) {
                return false;
            }

            Params mParams = params[0];
            OKCardBean mCardBean = mParams.getCardBean(); // 文章文字内容Bean
            OKCardBase64ListBean imageListBean = mParams.getImageListBean(); // 选择的图片视频

            Map<String, String> map = new HashMap<>(); // 文章文字内容参数

            map.put("username", mCardBean.getUSER_NAME());
            map.put("title", mCardBean.getTITLE_TEXT()); // 弃用参数,用户昵称
            map.put("title_image_url", mCardBean.getTITLE_IMAGE_URL()); // 弃用参数,用户头像url
            map.put("type", mCardBean.getCARD_TYPE());
            map.put("content_title", mCardBean.getCONTENT_TITLE_TEXT());
            map.put("labelling", mCardBean.getLABELLING());
            map.put("content_text", mCardBean.getCONTENT_TEXT());
            map.put("link", mCardBean.getMESSAGE_LINK());
            map.put("date", mCardBean.getCREATE_DATE());
            map.put("content_image", ""); // 弃用参数,用户选择的图片

            Map<String, File> mFileMap = new HashMap<>(); // 文章图片视频参数
            if (imageListBean.getCount() != 0) {
                String mImage1Path = imageListBean.getBaseImage1();
                if (!TextUtils.isEmpty(mImage1Path)) {
                    File file = new File(mImage1Path);
                    if (file.exists()) {
                        OKLogUtil.print("old1 File Size :" + file.length());
                        File fileNew = new OKCompressHelper.Builder(context).setQuality(80).build().compressToFile(file);
                        OKLogUtil.print("new1 File Size :" + fileNew.length());
                        // 生成服务器文件名,并添加到Map参数中!
                        String newFileName = mCardBean.getUSER_NAME() + "_" + UUID.randomUUID().toString().replaceAll("-", "") + "." + imageListBean.getFormatImage1();
                        mFileMap.put(newFileName, fileNew);
                    }
                }
                String mImage2Path = imageListBean.getBaseImage2();
                if (!TextUtils.isEmpty(mImage2Path)) {
                    File file = new File(mImage2Path);
                    if (file.exists()) {
                        OKLogUtil.print("old2 File Size :" + file.length());
                        File fileNew = new OKCompressHelper.Builder(context).setQuality(80).build().compressToFile(file);
                        OKLogUtil.print("new2 File Size :" + fileNew.length());
                        // 生成服务器文件名,并添加到Map参数中!
                        String newFileName = mCardBean.getUSER_NAME() + "_" + UUID.randomUUID().toString().replaceAll("-", "") + "." + imageListBean.getFormatImage2();
                        mFileMap.put(newFileName, fileNew);
                    }
                }
                String mImage3Path = imageListBean.getBaseImage3();
                if (!TextUtils.isEmpty(mImage3Path)) {
                    File file = new File(mImage3Path);
                    if (file.exists()) {
                        OKLogUtil.print("old3 File Size :" + file.length());
                        File fileNew = new OKCompressHelper.Builder(context).setQuality(80).build().compressToFile(file);
                        OKLogUtil.print("new3 File Size :" + fileNew.length());
                        // 生成服务器文件名,并添加到Map参数中!
                        String newFileName = mCardBean.getUSER_NAME() + "_" + UUID.randomUUID().toString().replaceAll("-", "") + "." + imageListBean.getFormatImage3();
                        mFileMap.put(newFileName, fileNew);
                    }
                }
                String mImage4Path = imageListBean.getBaseImage4();
                if (!TextUtils.isEmpty(mImage4Path)) {
                    File file = new File(mImage4Path);
                    if (file.exists()) {
                        OKLogUtil.print("old4 File Size :" + file.length());
                        File fileNew = new OKCompressHelper.Builder(context).setQuality(80).build().compressToFile(file);
                        OKLogUtil.print("new4 File Size :" + fileNew.length());
                        // 生成服务器文件名,并添加到Map参数中!
                        String newFileName = mCardBean.getUSER_NAME() + "_" + UUID.randomUUID().toString().replaceAll("-", "") + "." + imageListBean.getFormatImage4();
                        mFileMap.put(newFileName, fileNew);
                    }
                }
                String mImage5Path = imageListBean.getBaseImage5();
                if (!TextUtils.isEmpty(mImage5Path)) {
                    File file = new File(mImage5Path);
                    if (file.exists()) {
                        OKLogUtil.print("old5 File Size :" + file.length());
                        File fileNew = new OKCompressHelper.Builder(context).setQuality(80).build().compressToFile(file);
                        OKLogUtil.print("new5 File Size :" + fileNew.length());
                        // 生成服务器文件名,并添加到Map参数中!
                        String newFileName = mCardBean.getUSER_NAME() + "_" + UUID.randomUUID().toString().replaceAll("-", "") + "." + imageListBean.getFormatImage5();
                        mFileMap.put(newFileName, fileNew);
                    }
                }
            }
            return new OKBusinessApi().addUserCard(mFileMap, map);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (isCancelled()) {
                return;
            }

            mListener.articleApiComplete(aBoolean);
        }
    }

    private class Params {
        OKCardBean cardBean;
        OKCardBase64ListBean imageListBean;

        public OKCardBean getCardBean() {
            return cardBean;
        }

        public void setCardBean(OKCardBean cardBean) {
            this.cardBean = cardBean;
        }

        public OKCardBase64ListBean getImageListBean() {
            return imageListBean;
        }

        public void setImageListBean(OKCardBase64ListBean imageListBean) {
            this.imageListBean = imageListBean;
        }
    }
}
