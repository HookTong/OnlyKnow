package com.onlyknow.app.ui.activity;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.onlyknow.app.GlideApp;
import com.onlyknow.app.OKConstant;
import com.onlyknow.app.R;
import com.onlyknow.app.net.OKBusinessNet;
import com.onlyknow.app.database.bean.OKSafetyInfoBean;
import com.onlyknow.app.net.OKWebService;
import com.onlyknow.app.ui.OKBaseActivity;
import com.onlyknow.app.ui.view.OKProgressButton;
import com.onlyknow.app.ui.view.OKSEImageView;
import com.onlyknow.app.utils.OKLogUtil;
import com.onlyknow.app.utils.OKMimeTypeUtil;
import com.onlyknow.app.utils.OKNetUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Request;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;

public class OKWelcomeActivity extends OKBaseActivity {
    @Bind(R.id.ok_activity_welcome_image)
    ImageView okActivityWelcomeImage;
    @Bind(R.id.ok_activity_welcome_log_image)
    ImageView okActivityWelcomeLogImage;
    @Bind(R.id.ok_activity_welcome_fastPassLog_image)
    ImageView okActivityWelcomeFastPassLogImage;

    private VersionCheckTask mVersionCheckTask;
    private Handler mHandler = new Handler();
    private long SPLASH_LENGTH = 2000;

    private String mPermission[] = new String[]{CAMERA, ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.ok_activity_welcome);
        ButterKnife.bind(this);
        initSettingSharedPreferences();
        bindWelcomeTP();
        if (ContextCompat.checkSelfPermission(this, mPermission[0]) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{mPermission[0]}, 1); // 请求权限
        }
        if (ContextCompat.checkSelfPermission(this, mPermission[1]) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{mPermission[1]}, 1); // 请求权限
        }
        if (ContextCompat.checkSelfPermission(this, mPermission[2]) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{mPermission[2]}, 1); // 请求权限
        }
        if (OKNetUtil.isNet(this)) { // 版本检查
            if (mVersionCheckTask != null && mVersionCheckTask.getStatus() == AsyncTask.Status.RUNNING) {
                mVersionCheckTask.cancel(true);
            }
            Map<String, String> map = new HashMap<>();
            map.put("type", "APP_VERSION_CHECK");
            map.put("value", OKConstant.APP_VERSION);
            mVersionCheckTask = new VersionCheckTask();
            mVersionCheckTask.executeOnExecutor(exec, map);
        } else { // 无网络直接启动
            SPLASH_LENGTH += 2000;
            startMainActivity();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "权限 " + permissions[i] + " 申请失败,部分功能可能无法正常运行,请在设置中手动开启该权限", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void bindWelcomeTP() {
        GlideApi(okActivityWelcomeLogImage, R.drawable.weizhilog, R.drawable.weizhilog, R.drawable.weizhilog);
        GlideApi(okActivityWelcomeFastPassLogImage, R.drawable.fastpassedge, R.drawable.fastpassedge, R.drawable.fastpassedge);
        Random mRandom = new Random();
        int pos = mRandom.nextInt(5) + 1;
        if (pos == 1) {
            GlideApi(okActivityWelcomeImage, R.drawable.welcome_01, R.drawable.welcome_01, R.drawable.welcome_01);
        } else if (pos == 2) {
            GlideApi(okActivityWelcomeImage, R.drawable.welcome_02, R.drawable.welcome_02, R.drawable.welcome_02);
        } else if (pos == 3) {
            GlideApi(okActivityWelcomeImage, R.drawable.welcome_03, R.drawable.welcome_03, R.drawable.welcome_03);
        } else if (pos == 4) {
            GlideApi(okActivityWelcomeImage, R.drawable.welcome_04, R.drawable.welcome_04, R.drawable.welcome_04);
        } else if (pos == 5) {
            GlideApi(okActivityWelcomeImage, R.drawable.welcome_05, R.drawable.welcome_05, R.drawable.welcome_05);
        }
    }

    private void startMainActivity() {
        mHandler.postDelayed(new Runnable() {
            public void run() {
                startUserActivity(null, OKMainActivity.class);
                finish();
            }
        }, SPLASH_LENGTH);
    }

    private void showUpdateAppDialog(final OKSafetyInfoBean bean) {
        final AlertDialog.Builder DialogMenu = new AlertDialog.Builder(OKWelcomeActivity.this);
        final View dialogView = LayoutInflater.from(OKWelcomeActivity.this).inflate(R.layout.ok_dialog_update_app, null);
        final ImageView mImageViewBackground = (ImageView) dialogView.findViewById(R.id.ok_dialog_update_app_background_image);
        final OKSEImageView mOKSEImageViewClose = (OKSEImageView) dialogView.findViewById(R.id.ok_dialog_update_app_close_image);
        final OKProgressButton mProgressButton = (OKProgressButton) dialogView.findViewById(R.id.ok_dialog_update_app_down_button);
        final TextView mTextViewName = (TextView) dialogView.findViewById(R.id.ok_dialog_update_app_name_text);
        final TextView mTextViewOldVer = (TextView) dialogView.findViewById(R.id.ok_dialog_update_app_oldVer_text);
        final TextView mTextViewNewVer = (TextView) dialogView.findViewById(R.id.ok_dialog_update_app_ver_text);
        final TextView mTextViewSize = (TextView) dialogView.findViewById(R.id.ok_dialog_update_app_size_text);
        final TextView mTextViewInfo = (TextView) dialogView.findViewById(R.id.ok_dialog_update_app_miaoshu_text);
        DialogMenu.setView(dialogView);
        DialogMenu.setCancelable(false);
        final AlertDialog mAlertDialog = DialogMenu.show();
        if (OKSafetyInfoBean.AVD_IS_MANDATORY.YES.toString().equals(bean.getAVD_IS_MANDATORY())) {
            mOKSEImageViewClose.setVisibility(View.GONE);
        } else {
            mOKSEImageViewClose.setVisibility(View.VISIBLE);
        }
        mTextViewName.setText(bean.getAVU_NAME());
        mTextViewOldVer.setText("旧版本 :" + OKConstant.APP_VERSION + "版本");
        mTextViewNewVer.setText("是否更新到 " + bean.getAVU_VERSION() + " 版本 ?");
        mTextViewSize.setText("最新版本大小 :" + bean.getAVD_SIZE());
        mTextViewInfo.setText(bean.getAVU_DESCRIBE());
        GlideApp.with(OKWelcomeActivity.this).load(bean.getAVU_IMAG()).error(R.drawable.topgd1).into(mImageViewBackground);
        mProgressButton.setCurrentText("更新到最新版本");

        mProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mProgressButton.getState() == OKProgressButton.NORMAL) {
                    mProgressButton.setState(OKProgressButton.DOWNLOADING);
                    mProgressButton.setMaxProgress(100);
                    String dirPath = Environment.getExternalStorageDirectory().getPath();
                    OKWebService webService = OKWebService.getInstance();
                    String array[] = bean.getAVU_URL().split("/");
                    String fileName = array[array.length - 1];
                    DownloadCallback mDownloadCallback = new DownloadCallback(mProgressButton, mAlertDialog, dirPath + "/" + fileName);
                    webService.downloadFile(bean.getAVU_URL(), dirPath, mDownloadCallback);
                }
            }
        });

        mOKSEImageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mProgressButton.getState() == OKProgressButton.DOWNLOADING) {
                    AlertDialog.Builder AlertDialog = new AlertDialog.Builder(OKWelcomeActivity.this);
                    AlertDialog.setTitle("版本更新");
                    AlertDialog.setMessage("正在更新中,如果关闭将在后台为您下载,确定要关闭对话框 ?");
                    AlertDialog.setPositiveButton("关闭", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mAlertDialog.dismiss();
                            startMainActivity();
                        }
                    });
                    AlertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                        }
                    });
                    AlertDialog.show();
                } else {
                    mAlertDialog.dismiss();
                    startMainActivity();
                }
            }
        });
    }

    // app 版本检查
    private class VersionCheckTask extends AsyncTask<Map<String, String>, Void, OKSafetyInfoBean> {
        @Override
        protected OKSafetyInfoBean doInBackground(Map<String, String>... params) {
            if (isCancelled()) {
                return null;
            }
            return new OKBusinessNet().securityCheck(params[0]);
        }

        @Override
        protected void onPostExecute(final OKSafetyInfoBean bean) {
            super.onPostExecute(bean);
            if (isCancelled()) {
                startMainActivity();
                return;
            }
            if (bean == null) {
                startMainActivity();
                return;
            }
            if (!OKConstant.APP_VERSION.equals(bean.getAVU_VERSION())) {
                // 有新版本
                if (OKSafetyInfoBean.AVD_IS_MANDATORY.YES.toString().equals(bean.getAVD_IS_MANDATORY())) {
                    showUpdateAppDialog(bean); // 强制更新
                    return;
                } else if (SETTING_SP.getBoolean("AUTO_UPDATE", true)) {
                    showUpdateAppDialog(bean);
                    return;
                }
            } else {
                OKLogUtil.print("APP_VERSION_CHECK :已经是最新版本!");
            }
            startMainActivity();
        }
    }

    // 下载回调类
    private class DownloadCallback extends OKWebService.ResultCallback {
        private OKProgressButton button;
        private AlertDialog mAlertDialog;
        private String filePath;

        public DownloadCallback(OKProgressButton but, AlertDialog dialog, String path) {
            this.button = but;
            this.filePath = path;
            this.mAlertDialog = dialog;
        }

        @Override
        public void onError(Request request, Exception e) {
            button.setState(OKProgressButton.NORMAL);
            button.setCurrentText("重试");
        }

        @Override
        public void onResponse(Object response) {
            button.setState(OKProgressButton.NORMAL);
            button.setCurrentText("安装");
            new OKMimeTypeUtil().openFile(OKWelcomeActivity.this.getApplicationContext(), filePath);
            mAlertDialog.dismiss();
            startMainActivity();
        }

        @Override
        public void onProgress(double total, double current) {
            button.setProgress((int) ((current / total) * 100));
            button.setProgressText("下载中", button.getProgress());
        }
    }
}
