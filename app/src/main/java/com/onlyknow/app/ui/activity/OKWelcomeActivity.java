package com.onlyknow.app.ui.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
import com.onlyknow.app.api.app.OKLoadAppInfoApi;
import com.onlyknow.app.database.bean.OKAppInfoBean;
import com.onlyknow.app.net.OKWebService;
import com.onlyknow.app.service.OKMainService;
import com.onlyknow.app.ui.OKBaseActivity;
import com.onlyknow.app.ui.view.OKProgressButton;
import com.onlyknow.app.ui.view.OKSEImageView;
import com.onlyknow.app.utils.OKLogUtil;
import com.onlyknow.app.utils.OKMimeTypeUtil;
import com.onlyknow.app.utils.OKNetUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissionItem;
import okhttp3.Request;

public class OKWelcomeActivity extends OKBaseActivity implements PermissionCallback, OKLoadAppInfoApi.onCallBack {
    @Bind(R.id.ok_activity_welcome_image)
    ImageView okActivityWelcomeImage;
    @Bind(R.id.ok_activity_welcome_log_image)
    ImageView okActivityWelcomeLogImage;
    @Bind(R.id.ok_activity_welcome_fastPassLog_image)
    ImageView okActivityWelcomeFastPassLogImage;

    private OKLoadAppInfoApi mOKLoadAppInfoApi;
    private Handler mHandler = new Handler();
    private long SPLASH_LENGTH = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.ok_activity_welcome);
        ButterKnife.bind(this);
        initSettingSharedPreferences();
        bindWelcomeTP();

        checkSelfPermission();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mOKLoadAppInfoApi != null) {
            mOKLoadAppInfoApi.cancelTask();
        }
    }

    private void checkSelfPermission() {
        List<PermissionItem> permissionItems = new ArrayList<PermissionItem>();
        permissionItems.add(new PermissionItem(Manifest.permission.CAMERA, "照相机", R.drawable.permission_ic_camera));
        permissionItems.add(new PermissionItem(Manifest.permission.ACCESS_FINE_LOCATION, "GPS定位", R.drawable.permission_ic_location));
        permissionItems.add(new PermissionItem(Manifest.permission.ACCESS_COARSE_LOCATION, "网络定位", R.drawable.permission_ic_location));
        permissionItems.add(new PermissionItem(Manifest.permission.WRITE_EXTERNAL_STORAGE, "存储数据", R.drawable.permission_ic_storage));
        permissionItems.add(new PermissionItem(Manifest.permission.READ_EXTERNAL_STORAGE, "读取数据", R.drawable.permission_ic_storage));
        permissionItems.add(new PermissionItem(Manifest.permission.READ_PHONE_STATE, "设备信息", R.drawable.permission_ic_phone));
        HiPermission.create(this).permissions(permissionItems).checkMutiPermission(this);
    }

    private void bindWelcomeTP() {
        GlideApi(okActivityWelcomeLogImage, R.drawable.weizhilog, R.drawable.weizhilog, R.drawable.weizhilog);
        GlideApi(okActivityWelcomeFastPassLogImage, R.drawable.fastpassedge, R.drawable.fastpassedge, R.drawable.fastpassedge);
        Random mRandom = new Random();
        int pos = mRandom.nextInt(5) + 1;
        if (pos == 1) {
            okActivityWelcomeImage.setImageResource(R.drawable.welcome_01);
        } else if (pos == 2) {
            okActivityWelcomeImage.setImageResource(R.drawable.welcome_02);
        } else if (pos == 3) {
            okActivityWelcomeImage.setImageResource(R.drawable.welcome_03);
        } else if (pos == 4) {
            okActivityWelcomeImage.setImageResource(R.drawable.welcome_04);
        } else if (pos == 5) {
            okActivityWelcomeImage.setImageResource(R.drawable.welcome_05);
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

    private void startMainService() {
        Intent intent = new Intent();
        intent.setClass(this, OKMainService.class);
        startService(intent);
    }

    private void showUpdateAppDialog(final OKAppInfoBean bean) {
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
        if (bean.isAppIsMandatory()) {
            mOKSEImageViewClose.setVisibility(View.GONE);
        } else {
            mOKSEImageViewClose.setVisibility(View.VISIBLE);
        }
        mTextViewName.setText(bean.getAppName());
        mTextViewOldVer.setText("旧版本 :" + OKConstant.APP_VERSION + "版本");
        mTextViewNewVer.setText("是否更新到 " + bean.getAppVersion() + " 版本 ?");
        mTextViewSize.setText("最新版本大小 :" + bean.getAppSize());
        mTextViewInfo.setText(bean.getAppDescribe());
        GlideApp.with(OKWelcomeActivity.this).load(bean.getAppImageUrl()).error(R.drawable.topgd1).into(mImageViewBackground);
        mProgressButton.setCurrentText("更新到最新版本");

        mProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mProgressButton.getState() == OKProgressButton.NORMAL) {
                    mProgressButton.setState(OKProgressButton.DOWNLOADING);
                    mProgressButton.setMaxProgress(100);
                    String dirPath = Environment.getExternalStorageDirectory().getPath();
                    OKWebService webService = OKWebService.getInstance();
                    String array[] = bean.getAppUrl().split("/");
                    String fileName = array[array.length - 1];
                    DownloadCallback mDownloadCallback = new DownloadCallback(mProgressButton, mAlertDialog, dirPath + "/" + fileName);
                    webService.downloadFile(bean.getAppUrl(), dirPath, mDownloadCallback);
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

    // 权限申请回调
    @Override
    public void onClose() {
        Toast.makeText(this, "您关闭了权限申请,无法启动应用!", Toast.LENGTH_SHORT).show();

        finish();

        OKLogUtil.print("用户关闭权限申请");
    }

    @Override
    public void onFinish() {

        startMainService(); // 启动后台服务

        if (OKNetUtil.isNet(this)) { // 版本检查
            OKLoadAppInfoApi.Params params = new OKLoadAppInfoApi.Params();
            params.setVersion(OKConstant.APP_VERSION);
            params.setType(OKLoadAppInfoApi.Params.TYPE_CHECK);

            if (mOKLoadAppInfoApi != null) {
                mOKLoadAppInfoApi.cancelTask();
            }
            mOKLoadAppInfoApi = new OKLoadAppInfoApi(this);
            mOKLoadAppInfoApi.requestAppInfo(params, this);
        } else { // 无网络直接启动
            SPLASH_LENGTH += 2000;
            startMainActivity();
        }

        OKLogUtil.print("所有权限申请完成");
    }

    @Override
    public void onDeny(String permission, int position) {
        OKLogUtil.print("HiPermission onDeny");
    }

    @Override
    public void onGuarantee(String permission, int position) {
        OKLogUtil.print("HiPermission onGuarantee");
    }

    @Override
    public void appInfoApiComplete(OKAppInfoBean bean) {
        if (bean == null) {
            startMainActivity();
            return;
        }
        if (!OKConstant.APP_VERSION.equals(bean.getAppVersion())) {
            // 有新版本
            if (bean.isAppIsMandatory()) {
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
