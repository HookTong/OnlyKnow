package com.onlyknow.app.api.card;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.onlyknow.app.api.OKBaseApi;
import com.onlyknow.app.api.OKServiceResult;
import com.onlyknow.app.db.bean.OKCardBean;
import com.onlyknow.app.utils.OKFileUtil;
import com.onlyknow.app.utils.OKLogUtil;
import com.onlyknow.app.utils.OKNetUtil;
import com.onlyknow.app.utils.compress.OKCompressHelper;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class OKAddCardApi extends OKBaseApi {
    private Context context;
    private onCallBack mListener;
    private AddCardTask mAddCardTask;

    public OKAddCardApi(Context cont) {
        this.context = cont;
    }

    public interface onCallBack {
        void addCardComplete(OKServiceResult<Object> result);
    }

    public void requestAddCard(Params params, onCallBack listener) {
        this.mListener = listener;
        if (OKNetUtil.isNet(context)) {
            cancelTask();
            mAddCardTask = new AddCardTask();
            mAddCardTask.executeOnExecutor(exec, params);
        } else {
            mListener.addCardComplete(null);
        }
    }

    public void cancelTask() {
        if (mAddCardTask != null && mAddCardTask.getStatus() == AsyncTask.Status.RUNNING) {
            mAddCardTask.cancel(true);
        }
    }

    private class AddCardTask extends AsyncTask<Params, Void, OKServiceResult<Object>> {

        @Override
        protected OKServiceResult<Object> doInBackground(Params... params) {
            if (isCancelled()) {
                return null;
            }

            Params mParams = params[0];
            OKCardBean mCardBean = mParams.getCardBean();

            Map<String, File> fileMap = new HashMap<>(); // 文章图片视频参数
            List<OKCardBean.CardImage> images = mCardBean.getImageList();
            if (images != null && images.size() != 0) {
                for (int i = 0; i < images.size(); i++) {
                    OKCardBean.CardImage image = images.get(i);

                    if (image == null) continue;

                    String path = image.getUrl(); // 客户端生成的url为文件路径

                    if (TextUtils.isEmpty(path)) continue;

                    File file = new File(path);
                    if (file.exists()) {
                        OKLogUtil.print("old1 File Size :" + file.length());
                        File fileNew = new OKCompressHelper.Builder(context).setQuality(80).build().compressToFile(file);
                        OKLogUtil.print("new1 File Size :" + fileNew.length());
                        // 生成服务器文件名,并添加到Map参数中!
                        String newFileName = mCardBean.getUserName() + "_" + UUID.randomUUID().toString().replaceAll("-", "") + "." + OKFileUtil.getFileFormat(path);
                        fileMap.put(newFileName, fileNew);
                    }
                }
            }

            mCardBean.setImageList(null);
            mCardBean.setContentImageUrl("");

            Map<String, String> textMap = new HashMap<>(); // 文章文字内容参数
            textMap.put(Params.KEY_ENTITY, new Gson().toJson(mCardBean));

            return addCard(fileMap, textMap, Object.class);
        }

        @Override
        protected void onPostExecute(OKServiceResult<Object> result) {
            super.onPostExecute(result);
            if (isCancelled()) {
                return;
            }

            mListener.addCardComplete(result);
        }
    }

    public static class Params {
        OKCardBean cardBean;

        public final static String KEY_ENTITY = "entity";

        public OKCardBean getCardBean() {
            return cardBean;
        }

        public void setCardBean(OKCardBean cardBean) {
            this.cardBean = cardBean;
        }
    }
}
