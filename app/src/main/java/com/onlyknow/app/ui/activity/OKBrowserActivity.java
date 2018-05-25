package com.onlyknow.app.ui.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.onlyknow.app.OKConstant;
import com.onlyknow.app.R;
import com.onlyknow.app.ui.OKBaseActivity;
import com.onlyknow.app.utils.OKMimeTypeUtil;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.xwalk.core.XWalkDownloadListener;
import org.xwalk.core.XWalkNavigationHistory;
import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkSettings;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OKBrowserActivity extends OKBaseActivity {
    @Bind(R.id.ok_activity_web_input_link_edit)
    EditText mEditTextLink;
    @Bind(R.id.ok_activity_web_reGet_image)
    ImageView mImageViewReGet;
    @Bind(R.id.ok_activity_web_cardView)
    CardView mCardView;
    @Bind(R.id.ok_activity_web_toolbar)
    Toolbar mWebToolbar;
    @Bind(R.id.ok_activity_web_progressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.ok_activity_web_xWalkView)
    XWalkView xWalkWebView;

    private String mWebLink = "";
    private final String mBaiDuSearchUrl = "http://www.baidu.com/#wd=";

    private UMShareListener mShareListener = new UMShareListener() {
        /**
         * @descrption 分享开始的回调
         * @param platform 平台类型
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {

        }

        /**
         * @descrption 分享成功的回调
         * @param platform 平台类型
         */
        @Override
        public void onResult(SHARE_MEDIA platform) {
            showSnackBar(mWebToolbar, "分享成功了", "");
        }

        /**
         * @descrption 分享失败的回调
         * @param platform 平台类型
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            showSnackBar(mWebToolbar, "分享失败", "ErrorCode :" + t.getMessage());
        }

        /**
         * @descrption 分享取消的回调
         * @param platform 平台类型
         */
        @Override
        public void onCancel(SHARE_MEDIA platform) {
            showSnackBar(mWebToolbar, "分享取消了", "");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ok_activity_browser);
        ButterKnife.bind(this);
        initStatusBar();
        setSupportActionBar(mWebToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mProgressBar.setVisibility(View.GONE);
        mWebLink = getIntent().getExtras().getString("WEB_LINK", OKConstant.ONLY_KNOW_OFFICIAL_WEBSITE_URL);

        setWebStyle();
        init();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mWebToolbar != null) {
            mWebToolbar.setTitle("");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (xWalkWebView != null) {
            xWalkWebView.pauseTimers();
            xWalkWebView.onHide();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (xWalkWebView != null) {
            xWalkWebView.resumeTimers();
            xWalkWebView.onShow();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (xWalkWebView != null) {
            xWalkWebView.onDestroy();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (xWalkWebView != null) {
            xWalkWebView.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (xWalkWebView != null) {
            xWalkWebView.onNewIntent(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ok_menu_web, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
            case R.id.ok_menu_web_CopyLink:
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(mEditTextLink.getText().toString());
                Toast.makeText(this, "链接已复制", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ok_menu_web_BrowserOpen:
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(mEditTextLink.getText().toString());
                intent.setData(content_url);
                startActivity(intent);
                break;
            case R.id.ok_menu_web_FenXianLink:
                UMWeb web = new UMWeb(mEditTextLink.getText().toString());
                web.setTitle("来自唯知的链接分享!");
                UMImage thumb = new UMImage(this, R.drawable.ic_launcher);
                web.setThumb(thumb);
                new ShareAction(OKBrowserActivity.this)
                        .withMedia(web)
                        .setDisplayList(SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.WEIXIN)
                        .setCallback(mShareListener)
                        .open();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        if (!isUrlAgreement(mWebLink)) {
            mWebLink = mBaiDuSearchUrl + mWebLink;
        }
        xWalkWebView.load(mWebLink, null);

        mImageViewReGet.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String url = mEditTextLink.getText().toString();
                if (!TextUtils.isEmpty(url) && isUrlAgreement(url)) {
                    if (isUrlAgreement(url)) {
                        xWalkWebView.load(url, null);
                    } else {
                        xWalkWebView.load(mBaiDuSearchUrl + url, null);
                    }
                } else {
                    showSnackBar(mWebToolbar, "输入为空", "");
                }
            }
        });
    }

    private void setWebStyle() {
        XWalkPreferences.setValue("enable-javascript", true);
        XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);
        XWalkPreferences.setValue(XWalkPreferences.ALLOW_UNIVERSAL_ACCESS_FROM_FILE, true);
        XWalkPreferences.setValue(XWalkPreferences.JAVASCRIPT_CAN_OPEN_WINDOW, true);
        XWalkPreferences.setValue(XWalkPreferences.SUPPORT_MULTIPLE_WINDOWS, true);
        xWalkWebView.setHorizontalScrollBarEnabled(false);
        xWalkWebView.setVerticalScrollBarEnabled(false);
        xWalkWebView.setScrollBarStyle(XWalkView.SCROLLBARS_OUTSIDE_INSET);
        xWalkWebView.setScrollbarFadingEnabled(true);
        // xWalkWebView.setDrawingCacheEnabled(false);//不使用缓存
        setSettings(xWalkWebView.getSettings());
        xWalkWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        xWalkWebView.setUIClient(new OKWebUIClient(xWalkWebView));
        //webView.setWebChromeClient(new OKWebChromeClient());
        xWalkWebView.setDownloadListener(new OKDownLoadListener(this));
    }

    private void setSettings(XWalkSettings mMSettings) {
        mMSettings.setSupportSpatialNavigation(true);
        mMSettings.setBuiltInZoomControls(true);
        mMSettings.setSupportZoom(true);
    }

    private boolean isUrlAgreement(String mUrl) {
        boolean b;
        String regex = "(((https|http)?://)?([a-z0-9]+[.])|(www.))" + "\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\u4E00-\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)";//设置正则表达式
        Pattern pat = Pattern.compile(regex.trim());
        Matcher mat = pat.matcher(mUrl.trim());
        b = mat.matches();//判断是否匹配
        boolean b2 = mUrl.startsWith("http://") || mUrl.startsWith("https://") || mUrl.startsWith("file://") || mUrl.startsWith("ftp://");
        return b || b2;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (xWalkWebView.getNavigationHistory().canGoBack()) {
                xWalkWebView.getNavigationHistory().navigate(XWalkNavigationHistory.Direction.BACKWARD, 1);
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class OKWebUIClient extends XWalkUIClient {

        public OKWebUIClient(XWalkView view) {
            super(view);
        }

        @Override
        public void onPageLoadStarted(XWalkView view, String url) {
            super.onPageLoadStarted(view, url);
            mEditTextLink.setText(url);
            Animation anim = AnimationUtils.loadAnimation(OKBrowserActivity.this, R.anim.ok_rotate_anim);
            if (anim != null) {
                LinearInterpolator interpolator = new LinearInterpolator(); // 设置匀速旋转,在xml文件中设置会出现卡顿
                anim.setInterpolator(interpolator);
                mImageViewReGet.startAnimation(anim); // 开始动画
                mImageViewReGet.setEnabled(false);
            }
        }

        @Override
        public void onPageLoadStopped(XWalkView view, String url, LoadStatus status) {
            super.onPageLoadStopped(view, url, status);
            mEditTextLink.setText(url);
            mImageViewReGet.clearAnimation();
            mImageViewReGet.setEnabled(true);
        }
    }

    private class OKDownLoadListener extends XWalkDownloadListener {

        public OKDownLoadListener(Context context) {
            super(context);
        }

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                showSnackBar(mWebToolbar, "没有可用的存储设备", "");
                return;
            }
            OKDownloaderTask task = new OKDownloaderTask();
            task.execute(url);
        }
    }

    private class OKDownloaderTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            String fileName = url.substring(url.lastIndexOf("/") + 1);
            fileName = URLDecoder.decode(fileName);
            File directory = Environment.getExternalStorageDirectory();
            File file = new File(directory, fileName);
            if (file.exists()) {
                return fileName;
            }
            try {
                OkHttpClient okHttpClient = new OkHttpClient();
                Request request = new Request.Builder().get().url(url).build();
                Call call = okHttpClient.newCall(request);
                Response mResponse = call.execute();
                InputStream input = mResponse.body().byteStream();
                writeToSDCard(fileName, input);
                input.close();
                return fileName;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            closeProgressDialog();
            if (result == null) {
                showSnackBar(mWebToolbar, "连接错误! 请稍后再试!", "");
                return;
            }
            showSnackBar(mWebToolbar, "已保存到SD卡", "");

            File directory = Environment.getExternalStorageDirectory();
            File file = new File(directory, result);
            new OKMimeTypeUtil().openFile(OKBrowserActivity.this, file.getAbsolutePath());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("正在下载文件...");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        public void writeToSDCard(String fileName, InputStream input) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File directory = Environment.getExternalStorageDirectory();
                File file = new File(directory, fileName);
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    byte[] b = new byte[2048];
                    int p = 0;
                    while ((p = input.read(b)) != -1) {
                        fos.write(b, 0, p);
                    }
                    fos.flush();
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Log.i("tag", "NO SDCard.");
            }
        }
    }
}
