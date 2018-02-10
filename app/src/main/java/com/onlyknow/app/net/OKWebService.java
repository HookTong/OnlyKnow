package com.onlyknow.app.net;

import android.os.Handler;
import android.os.Looper;

import com.onlyknow.app.utils.OKLogUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OKWebService {
    public static String OkHttpApiPost(String url, Map<String, String> params) {
        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            FormBody.Builder mBuilder = new FormBody.Builder();
            if (params != null) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = entry.getValue();
                    if (isChinese(value)) {
                        try {
                            value = URLEncoder.encode(value, "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            OKLogUtil.print("OKWebService 参数编码错误");
                        }
                    }
                    mBuilder.add(entry.getKey(), value);
                }
            }

            RequestBody body = mBuilder.build();
            Request request = new Request.Builder().url(url).post(body).build();
            Call call = okHttpClient.newCall(request);
            Response response = call.execute();
            if (response.isSuccessful()) {
                return bodyToString(response.body().byteStream());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            OKLogUtil.print(ex.getMessage());
        }
        return null;
    }

    public static String OKHttpApiPostFromData(String url, Map<String, String> params) {
        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            FormBody.Builder mBuilder = new FormBody.Builder();
            if (params != null) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = entry.getValue();
                    if (isChinese(value)) {
                        try {
                            value = URLEncoder.encode(value, "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            OKLogUtil.print("OKWebService 参数编码错误");
                        }
                    }
                    mBuilder.add(entry.getKey(), value);
                }
            }

            RequestBody body = mBuilder.build();
            Request request = new Request.Builder().url(url).addHeader("Content-Type", "multipart/form-data").post(body).build();
            Call call = okHttpClient.newCall(request);
            Response response = call.execute();
            if (response.isSuccessful()) {
                return bodyToString(response.body().byteStream());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            OKLogUtil.print(ex.getMessage());
        }
        return null;
    }

    public static String OKHttpApiPostFromFile(String url, Map<String, File> files, Map<String, String> params) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        for (Map.Entry<String, File> entry : files.entrySet()) {
            builder.addFormDataPart("file", entry.getKey(), RequestBody.create(MediaType.parse("application/octet-stream"), entry.getValue()));
        }
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String value = entry.getValue();
            if (isChinese(value)) {
                try {
                    value = URLEncoder.encode(value, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    OKLogUtil.print("OKWebService 参数编码错误");
                }
            }
            builder.addFormDataPart(entry.getKey(), value);
        }
        MultipartBody multipartBody = builder.build();
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).post(multipartBody).build();
        Call call = mOkHttpClient.newCall(request);
        try {
            Response mResponse = call.execute();
            if (mResponse.isSuccessful()) {
                return bodyToString(mResponse.body().byteStream());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String OKHttpApiGet(String url) {
        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder().url(url).build();

        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                return bodyToString(response.body().byteStream());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String bodyToString(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line = br.readLine();
        while (line != null) {
            sb.append(line);
            line = br.readLine();
        }

        OKLogUtil.print("WebService return : " + sb.toString());

        return sb.toString();
    }

    // 完整的判断中文汉字和符号
    private static boolean isChinese(String strName) {
        char[] ch = strName.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            if (isChinese(ch[i])) {
                return true;
            }
        }
        return false;
    }

    // 根据Unicode编码完美的判断中文汉字和符号
    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }

    private OKWebService() {
        mDelivery = new Handler(Looper.getMainLooper());
    }

    private Handler mDelivery;// 主线程返回
    private Call downCall; // 下载的call
    private OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mDiskDir = "";
    private String mFileName = "";
    private static OKWebService mWebServiceInstance; // 单例

    public static OKWebService getInstance() {
        if (mWebServiceInstance == null) {
            mWebServiceInstance = new OKWebService();
        }
        return mWebServiceInstance;
    }

    /**
     * 异步下载文件
     *
     * @param url         文件的下载地址
     * @param destFileDir 本地文件存储的文件夹
     * @param callback
     */
    private void okHttpDownload(final String url, final String destFileDir, final ResultCallback callback) {
        final Request request = new Request.Builder().url(url).build();
        downCall = mOkHttpClient.newCall(request);
        downCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendFailedStringCallback(call.request(), e, callback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    double current = 0;
                    double total = response.body().contentLength();

                    is = response.body().byteStream();
                    File file = new File(destFileDir, getFileName(url));
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        current += len;
                        fos.write(buf, 0, len);

                        sendProgressCallBack(total, current, callback);

                        OKLogUtil.print("download current------>" + current);
                    }
                    fos.flush();
                    //如果下载文件成功，第一个参数为文件的绝对路径
                    sendSuccessResultCallback(file.getAbsolutePath(), callback);
                } catch (IOException e) {
                    sendFailedStringCallback(response.request(), e, callback);
                } finally {
                    try {
                        if (is != null) is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (fos != null) fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private String getFileName(String url) {
        String ss[] = url.split("/");
        return ss[ss.length - 1];
    }

    //下载失败ui线程回调
    private void sendFailedStringCallback(final Request request, final Exception e, final ResultCallback callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null)
                    callback.onError(request, e);
            }
        });
    }

    //下载成功ui线程回调
    private void sendSuccessResultCallback(final Object object, final ResultCallback callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onResponse(object);
                }
            }
        });
    }

    //下载回调接口
    public static abstract class ResultCallback<T> {

        //下载错误
        public abstract void onError(Request request, Exception e);

        //下载成功
        public abstract void onResponse(T response);

        //下载进度
        public abstract void onProgress(double total, double current);
    }

    /**
     * 进度信息ui线程回调
     *
     * @param total    总计大小
     * @param current  当前进度
     * @param callBack
     * @param <T>
     */
    private <T> void sendProgressCallBack(final double total, final double current, final ResultCallback<T> callBack) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onProgress(total, current);
                }
            }
        });
    }

    /**
     * 下载文件
     *
     * @param url      文件链接
     * @param destDir  下载保存地址
     * @param callback 回调
     */
    public void downloadFile(String url, String destDir, ResultCallback callback) {
        mDiskDir = destDir;
        mFileName = getFileName(url);
        okHttpDownload(url, destDir, callback);
    }

    /**
     * 取消下载
     */
    public void cancelDown() {
        if (downCall != null) {
            downCall.cancel();
            File file = new File(mDiskDir, mFileName);
            if (file.exists()) {
                file.delete();
            }
        }
    }
}
