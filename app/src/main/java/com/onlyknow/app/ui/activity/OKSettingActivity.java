package com.onlyknow.app.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.onlyknow.app.OKConstant;
import com.onlyknow.app.R;
import com.onlyknow.app.api.app.OKLoadAppInfoApi;
import com.onlyknow.app.db.OKDatabaseHelper;
import com.onlyknow.app.db.bean.OKAppInfoBean;
import com.onlyknow.app.net.OKWebService;
import com.onlyknow.app.ui.OKBaseActivity;
import com.onlyknow.app.ui.view.OKProgressButton;
import com.onlyknow.app.ui.view.OKSEImageView;
import com.onlyknow.app.utils.OKGlideCacheUtil;
import com.onlyknow.app.utils.OKMimeTypeUtil;

import okhttp3.Request;

public class OKSettingActivity extends OKBaseActivity implements OKLoadAppInfoApi.onCallBack {
    private LinearLayout layoutFontSet, layoutCacheQk, layoutVersionUpdate, layoutVersionJieSao, layoutUserXieYi;
    private LinearLayout layoutYiJianFanKui, layoutDiBuDaoHan, mLinearLayoutAbout;
    private SwitchCompat switchCompat;
    private TextView textViewVersionID, textViewCacheSize;

    private int dbPos = 0;

    private OKLoadAppInfoApi mOKLoadAppInfoApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ok_activity_setting);
        initSettingSharedPreferences();
        initSystemBar(this);
        findView();
        init();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mToolbar != null) {
            mToolbar.setTitle("");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOKLoadAppInfoApi != null) {
            mOKLoadAppInfoApi.cancelTask();
        }
    }

    private void init() {
        setSupportActionBar(mToolbar);

        textViewCacheSize.setText(OKGlideCacheUtil.getCacheSize(this));

        textViewVersionID.setText("当前APP版本 :" + OKConstant.APP_VERSION);
        if (SETTING_SP.getBoolean("AUTO_UPDATE", true)) {
            switchCompat.setChecked(true);
        } else {
            switchCompat.setChecked(false);
        }

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SETTING_SP.edit().putBoolean("AUTO_UPDATE", true).commit();
                } else {
                    SETTING_SP.edit().putBoolean("AUTO_UPDATE", false).commit();
                }
            }
        });

        layoutFontSet.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder DialogMenu = new AlertDialog.Builder(OKSettingActivity.this);
                final View dialogview = LayoutInflater.from(OKSettingActivity.this).inflate(R.layout.ok_dialog_font_setting, null);
                final RadioGroup radioGroup = (RadioGroup) dialogview.findViewById(R.id.setting_ddalog_rg);
                final RadioButton radioButton = (RadioButton) dialogview.findViewById(R.id.setting_ddalog_rb1);
                final RadioButton radioButton2 = (RadioButton) dialogview.findViewById(R.id.setting_ddalog_rb2);
                final RadioButton radioButton3 = (RadioButton) dialogview.findViewById(R.id.setting_ddalog_rb3);
                final RadioButton radioButton4 = (RadioButton) dialogview.findViewById(R.id.setting_ddalog_rb4);
                DialogMenu.setView(dialogview);
                if (SETTING_SP.getString("FONT", "NORM").equals("NORM")) {
                    radioButton.setChecked(true);
                } else if (SETTING_SP.getString("FONT", "NORM").equals("MAX")) {
                    radioButton2.setChecked(true);
                } else if (SETTING_SP.getString("FONT", "NORM").equals("CENTRE")) {
                    radioButton3.setChecked(true);
                } else if (SETTING_SP.getString("FONT", "NORM").equals("MIN")) {
                    radioButton4.setChecked(true);
                }
                radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {

                        if (checkedId == R.id.setting_ddalog_rb1) {
                            SETTING_SP.edit().putString("FONT", "NORM").commit();
                        } else if (checkedId == R.id.setting_ddalog_rb2) {
                            SETTING_SP.edit().putString("FONT", "MAX").commit();
                        } else if (checkedId == R.id.setting_ddalog_rb3) {
                            SETTING_SP.edit().putString("FONT", "CENTRE").commit();
                        } else if (checkedId == R.id.setting_ddalog_rb4) {
                            SETTING_SP.edit().putString("FONT", "MIN").commit();
                        }
                    }
                });
                DialogMenu.show();
            }
        });

        layoutCacheQk.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder AlertDialog = new AlertDialog.Builder(OKSettingActivity.this);
                AlertDialog.setTitle("清空本地缓存");
                AlertDialog.setMessage("是否清空本地数据库缓存 ?");
                AlertDialog.setIcon(R.drawable.ic_launcher);
                AlertDialog.setPositiveButton("清空", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        OKGlideCacheUtil.clearImageDiskCache(OKSettingActivity.this);

                        OKDatabaseHelper helper = OKDatabaseHelper.getHelper(OKSettingActivity.this);
                        helper.onUpgrade(helper.getWritableDatabase(), helper.getConnectionSource(), 2, 3);

                        textViewCacheSize.setText(OKGlideCacheUtil.getCacheSize(OKSettingActivity.this));
                    }
                });
                AlertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
                AlertDialog.show();
            }
        });

        layoutVersionUpdate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                OKLoadAppInfoApi.Params params = new OKLoadAppInfoApi.Params();
                params.setVersion(OKConstant.APP_VERSION);
                params.setType(OKLoadAppInfoApi.Params.TYPE_CHECK);

                if (mOKLoadAppInfoApi != null) {
                    mOKLoadAppInfoApi.cancelTask();
                }
                mOKLoadAppInfoApi = new OKLoadAppInfoApi(OKSettingActivity.this);
                mOKLoadAppInfoApi.requestAppInfo(params, OKSettingActivity.this);
            }
        });

        layoutVersionJieSao.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(OKContentActivity.KEY_TYPE, OKContentActivity.TYPE_APP);
                startUserActivity(bundle, OKContentActivity.class);
            }
        });

        layoutUserXieYi.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(OKContentActivity.KEY_TYPE, OKContentActivity.TYPE_AGREEMENT);
                startUserActivity(bundle, OKContentActivity.class);
            }
        });

        layoutYiJianFanKui.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startUserActivity(null, OKFeedBackActivity.class);
            }
        });

        layoutDiBuDaoHan.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dbPos = SETTING_SP.getInt("BottomNnavigation", 0);
                AlertDialog.Builder builder = new AlertDialog.Builder(OKSettingActivity.this);
                builder.setTitle("选择底部导航样式");
                builder.setSingleChoiceItems(new String[]{"FIXED+RIPPLE 效果", "FIXED+STATIC 效果", "SHIFTING+STATIC 效果",
                        "SHIFTING+RIPPLE 效果"}, dbPos, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int pos) {
                        dbPos = pos;
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        SETTING_SP.edit().putInt("BottomNnavigation", dbPos).commit();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });
                builder.show();
            }
        });

        mLinearLayoutAbout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle mBundle = new Bundle();
                mBundle.putString("WEB_LINK", OKConstant.ONLY_KNOW_SOURCE_CODE_URL);
                startUserActivity(mBundle, OKBrowserActivity.class);
            }
        });

        mToolbarBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void findView() {
        super.findCommonToolbarView(this);
        mToolbarBack.setVisibility(View.VISIBLE);
        mToolbarTitle.setVisibility(View.VISIBLE);

        mToolbarTitle.setText("程序设置");

        switchCompat = (SwitchCompat) findViewById(R.id.setting_switchcompat_update);
        layoutFontSet = (LinearLayout) findViewById(R.id.setting_layout_zhitidaxiao);
        layoutCacheQk = (LinearLayout) findViewById(R.id.setting_layout_qingkonhuanchun);
        layoutVersionUpdate = (LinearLayout) findViewById(R.id.setting_layout_banbengenxin);
        layoutVersionJieSao = (LinearLayout) findViewById(R.id.setting_layout_banbenjiesao);
        layoutUserXieYi = (LinearLayout) findViewById(R.id.setting_layout_yonhuxieyi);
        layoutYiJianFanKui = (LinearLayout) findViewById(R.id.setting_layout_yijianfankui);
        layoutDiBuDaoHan = (LinearLayout) findViewById(R.id.setting_layout_shezhidibudaohan);
        mLinearLayoutAbout = (LinearLayout) findViewById(R.id.setting_layout_about);
        textViewVersionID = (TextView) findViewById(R.id.setting_text_banbenID);
        textViewCacheSize = (TextView) findViewById(R.id.setting_cache_size_text);
    }

    private void showUpdateAppDialog(final OKAppInfoBean bean) {
        final AlertDialog.Builder DialogMenu = new AlertDialog.Builder(this);
        final View dialogView = LayoutInflater.from(this).inflate(R.layout.ok_dialog_update_app, null);
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
        mTextViewOldVer.setText("旧版本 :" + OKConstant.APP_VERSION + " 版本");
        mTextViewNewVer.setText("是否更新到 " + bean.getAppVersion() + " 版本 ?");
        mTextViewSize.setText("最新版本大小 :" + bean.getAppSize());
        mTextViewInfo.setText(bean.getAppDescribe());
        GlideApi(mImageViewBackground, bean.getAppImageUrl(), R.drawable.topgd1, R.drawable.topgd1);
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
                    showAlertDialog("版本更新", "正在更新中,确定要关闭对话框?", "关闭", "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mAlertDialog.dismiss();
                        }
                    });
                } else {
                    mAlertDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void loadAppInfoComplete(OKAppInfoBean bean) {
        if (bean == null) {
            showSnackBar(layoutUserXieYi, "检查失败", "");
            return;
        }
        if (!OKConstant.APP_VERSION.equals(bean.getAppVersion())) {
            showUpdateAppDialog(bean);
        } else {
            showSnackBar(layoutUserXieYi, "已经是最新版本!", "");
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
            new OKMimeTypeUtil().openFile(OKSettingActivity.this, filePath);
            mAlertDialog.dismiss();
        }

        @Override
        public void onProgress(double total, double current) {
            button.setProgress((int) ((current / total) * 100));
            button.setProgressText("下载中", button.getProgress());
        }
    }
}
