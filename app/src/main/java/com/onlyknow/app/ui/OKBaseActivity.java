package com.onlyknow.app.ui;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.onlyknow.app.GlideApp;
import com.onlyknow.app.OKConstant;
import com.onlyknow.app.R;
import com.onlyknow.app.database.bean.OKCardBean;
import com.onlyknow.app.database.bean.OKCardUrlListBean;
import com.onlyknow.app.ui.activity.OKLoginActivity;
import com.onlyknow.app.ui.activity.OKMipcaActivityCapture;
import com.onlyknow.app.ui.activity.OKSettingActivity;
import com.onlyknow.app.ui.activity.OKUserEdit;
import com.onlyknow.app.ui.view.OKSEImageView;
import com.onlyknow.app.utils.OKBarTintUtil;
import com.onlyknow.app.utils.OKBlurTransformation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 所有Activity都必须继承该基本Activity
 * <p>
 * Created by Administrator on 2017/12/8.
 */

public class OKBaseActivity extends AppCompatActivity {
    public final String INTENT_KEY_INTERFACE_TYPE = "InterfaceType";
    public final String INTENT_KEY_LIST_POSITION = "ListPosition";
    public final String INTENT_KEY_LIST_CARD_ID = "ListCardId";
    public final int INTERFACE_EXPLORE = 1; // 探索界面
    public final int INTERFACE_NEAR = 2; // 附近界面
    public final int INTERFACE_HISTORY = 3; // 历史界面
    public final int INTERFACE_DYNAMIC = 4; // 动态界面
    public final int INTERFACE_ATTENTION = 5; // 关注界面
    public final int INTERFACE_COLLECTION = 6; // 收藏界面
    public final int INTERFACE_HOT = 7; // 热门界面
    public final int INTERFACE_HOME = 8; // 用户展示界面
    public final int INTERFACE_GOODS = 9; // 商品界面
    public final int INTERFACE_NOTICE = 10; // 通知界面
    public final int INTERFACE_SESSION = 11; // 会话界面
    public final int INTERFACE_COMMENT = 12; // 卡片评论界面
    public final int INTERFACE_COMMENT_REPLY = 13; // 评论回复界面
    public final int INTERFACE_SEARCH = 14; // 搜索界面
    public final int INTERFACE_CARD_AND_COMMENT = 15;

    public final String ACTION_MAIN_SERVICE_SHOW_NOTICE = "ACTION_MAIN_SERVICE_SHOW_NOTICE";
    public final String ACTION_MAIN_SERVICE_LOGIN_IM = "ACTION_MAIN_SERVICE_LOGIN_IM";
    public final String ACTION_MAIN_SERVICE_LOGOUT_IM = "ACTION_MAIN_SERVICE_LOGOUT_IM";
    public final String ACTION_MAIN_SERVICE_CREATE_ACCOUNT_IM = "ACTION_MAIN_SERVICE_CREATE_ACCOUNT_IM";
    public final String ACTION_MAIN_SERVICE_ADD_MESSAGE_LISTENER_IM = "ACTION_MAIN_SERVICE_ADD_MESSAGE_LISTENER_IM";
    public final String ACTION_MAIN_SERVICE_REMOVE_MESSAGE_LISTENER_IM = "ACTION_MAIN_SERVICE_REMOVE_MESSAGE_LISTENER_IM";

    public final String INTENT_KEY_GAN_KIO = "";
    public final int GAN_KIO_TYPE_FL = 1;
    public final int GAN_KIO_TYPE_VIDEO = 2;
    public final int GAN_KIO_TYPE_RES = 3;
    public final int GAN_KIO_TYPE_ANDROID = 4;
    public final int GAN_KIO_TYPE_IOS = 5;
    public final int GAN_KIO_TYPE_H5 = 6;

    public final String CARD_TYPE_TW = OKCardBean.CardType.IMAGE_TEXT.toString();
    public final String CARD_TYPE_TP = OKCardBean.CardType.IMAGE.toString();
    public final String CARD_TYPE_WZ = OKCardBean.CardType.TEXT.toString();

    public ProgressDialog mDialog;
    public SharedPreferences USER_INFO_SP, SETTING_SP, WEATHER_SP, ARTICLE_SP;

    public final ExecutorService exec = Executors.newFixedThreadPool(100);

    // toolbar控件
    public Toolbar mToolbar;
    public OKSEImageView mToolbarBack, mToolbarMenu, mToolbarSearch, mToolbarEdit, mToolbarSharing, mToolbarSend, mToolbarLogout, mToolbarAddImage;
    public OKSEImageView mToolbarAdd;// 只能初始化通用toolbar才能调用
    public TextView mToolbarTitle;
    public ProgressBar mToolBarProgressBar; // 只能初始化通用toolbar才能调用

    @Override
    protected void onResume() {
        super.onResume();
        Glide.with(this).resumeRequests();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Glide.with(this).pauseRequests();
    }

    @Override
    public Resources getResources() {
        // 设置android app 的字体大小不受系统字体大小改变的影响
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    // find两个不同的toolbar,在一个界面中只能find一个
    public void findCollapsingToolbarView(Activity activity) {
        mToolbar = (Toolbar) activity.findViewById(R.id.ok_collapsing_toolbar);
        mToolbarBack = (OKSEImageView) activity.findViewById(R.id.ok_collapsing_toolbar_back_image);
        mToolbarMenu = (OKSEImageView) activity.findViewById(R.id.ok_collapsing_toolbar_menu_image);
        mToolbarSearch = (OKSEImageView) activity.findViewById(R.id.ok_collapsing_toolbar_search_image);
        mToolbarEdit = (OKSEImageView) activity.findViewById(R.id.ok_collapsing_toolbar_edit_image);
        mToolbarSharing = (OKSEImageView) activity.findViewById(R.id.ok_collapsing_toolbar_sharing_image);
        mToolbarSend = (OKSEImageView) activity.findViewById(R.id.ok_collapsing_toolbar_sendmessage_image);
        mToolbarLogout = (OKSEImageView) activity.findViewById(R.id.ok_collapsing_toolbar_logout_image);
        mToolbarTitle = (TextView) activity.findViewById(R.id.ok_collapsing_toolbar_title_text);
        mToolBarProgressBar = (ProgressBar) activity.findViewById(R.id.ok_collapsing_toolbar_progressBar);
    }

    public void findCommonToolbarView(Activity activity) {
        mToolbar = (Toolbar) activity.findViewById(R.id.ok_common_toolbar);
        mToolbarBack = (OKSEImageView) activity.findViewById(R.id.ok_common_toolbar_back_image);
        mToolbarMenu = (OKSEImageView) activity.findViewById(R.id.ok_common_toolbar_menu_image);
        mToolbarSearch = (OKSEImageView) activity.findViewById(R.id.ok_common_toolbar_search_image);
        mToolbarEdit = (OKSEImageView) activity.findViewById(R.id.ok_common_toolbar_edit_image);
        mToolbarSharing = (OKSEImageView) activity.findViewById(R.id.ok_common_toolbar_sharing_image);
        mToolbarSend = (OKSEImageView) activity.findViewById(R.id.ok_common_toolbar_sendmessage_image);
        mToolbarLogout = (OKSEImageView) activity.findViewById(R.id.ok_common_toolbar_logout_image);
        mToolbarTitle = (TextView) activity.findViewById(R.id.ok_common_toolbar_title_text);
        mToolbarAddImage = (OKSEImageView) activity.findViewById(R.id.ok_common_toolbar_addTp_image);
        mToolbarAdd = (OKSEImageView) activity.findViewById(R.id.ok_common_toolbar_add_image);
        mToolBarProgressBar = (ProgressBar) activity.findViewById(R.id.ok_common_toolbar_progressBar);
    }

    public void showMenu(final Activity activity) {
        View parent = ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
        View popView = View.inflate(activity, R.layout.ok_main_menu, null);
        LinearLayout linearLayoutLogin = (LinearLayout) popView.findViewById(R.id.POP_LOGIN_LAYOU);
        LinearLayout linearLayoutQrCode = (LinearLayout) popView.findViewById(R.id.POP_QRCODE_LAYOU);
        LinearLayout linearLayoutSetting = (LinearLayout) popView.findViewById(R.id.POP_SETTING_LAYOU);
        Button buttonClose = (Button) popView.findViewById(R.id.POP_GUANBI_BUT);
        final PopupWindow popWindow = new PopupWindow(popView, ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
        popWindow.setAnimationStyle(R.style.AnimBottom);
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(true);// 设置允许在外点击消失
        View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.POP_GUANBI_BUT:
                        popWindow.dismiss();
                        break;
                    case R.id.POP_LOGIN_LAYOU:
                        if (initUserInfoSharedPreferences().getBoolean("STATE", false)) {
                            Intent intent = new Intent();
                            intent.setClass(activity, OKUserEdit.class);
                            activity.startActivity(intent);
                        } else {
                            Intent intent = new Intent();
                            intent.setClass(activity, OKLoginActivity.class);
                            activity.startActivity(intent);
                        }
                        popWindow.dismiss();
                        break;
                    case R.id.POP_QRCODE_LAYOU:
                        Intent intent = new Intent();
                        intent.setClass(activity, OKMipcaActivityCapture.class);
                        activity.startActivity(intent);
                        popWindow.dismiss();
                        break;
                    case R.id.POP_SETTING_LAYOU:
                        Intent intent2 = new Intent();
                        intent2.setClass(activity, OKSettingActivity.class);
                        activity.startActivity(intent2);
                        popWindow.dismiss();
                        break;
                }
            }
        };
        linearLayoutLogin.setOnClickListener(listener);
        linearLayoutQrCode.setOnClickListener(listener);
        linearLayoutSetting.setOnClickListener(listener);
        buttonClose.setOnClickListener(listener);
        ColorDrawable dw = new ColorDrawable(0x30000000);
        popWindow.setBackgroundDrawable(dw);
        popWindow.showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    public void showSnackbar(View view, String message, String errorCode) {
        Snackbar.make(view, message + errorCode, Snackbar.LENGTH_SHORT).show();
    }

    public void GlideApi(ImageView mView, int id, int placeholderId, int errorId) {
        GlideApp.with(this).load(id).centerCrop().placeholder(placeholderId).error(errorId).into(mView);
    }

    public void GlideApi(ImageView mView, String url, int placeholderId, int errorId) {
        GlideApp.with(this).load(url).centerCrop().placeholder(placeholderId).error(errorId).into(mView);
    }

    public void GlideRoundApi(ImageView mView, String url, int placeholderId, int errorId) {
        GlideApp.with(this).load(url).apply(RequestOptions.circleCropTransform()).placeholder(placeholderId).error(errorId).into(mView);
    }

    public void GlideRoundApi(ImageView mView, int id, int placeholderId, int errorId) {
        GlideApp.with(this).load(id).apply(RequestOptions.circleCropTransform()).placeholder(placeholderId).error(errorId).into(mView);
    }

    public void GlideBlurApi(ImageView mView, String url, int placeholderId, int errorId) {
        GlideApp.with(this).load(url).placeholder(placeholderId).error(errorId).transform(new OKBlurTransformation(this, 25)).into(mView);
    }

    public void GlideBlurApi(ImageView mView, int id, int placeholderId, int errorId) {
        GlideApp.with(this).load(id).placeholder(placeholderId).error(errorId).transform(new OKBlurTransformation(this, 25)).into(mView);
    }

    public SharedPreferences initUserInfoSharedPreferences() {
        if (USER_INFO_SP == null) {
            USER_INFO_SP = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        }
        return USER_INFO_SP;
    }

    public SharedPreferences initSettingSharedPreferences() {
        if (SETTING_SP == null) {
            SETTING_SP = this.getSharedPreferences("setting", Context.MODE_PRIVATE);
        }
        return SETTING_SP;
    }

    public SharedPreferences initWeatherSharedPreferences() {
        if (WEATHER_SP == null) {
            WEATHER_SP = this.getSharedPreferences("weather", Context.MODE_PRIVATE);
        }
        return WEATHER_SP;
    }

    public SharedPreferences initArticleSharedPreferences() {
        if (ARTICLE_SP == null) {
            ARTICLE_SP = this.getSharedPreferences("release", Context.MODE_PRIVATE);
        }
        return ARTICLE_SP;
    }

    public void initSystemBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(activity, true);
        }
        OKBarTintUtil tintManager = new OKBarTintUtil(activity);
        tintManager.setStatusBarTintEnabled(true);
        // 使用颜色资源
        tintManager.setStatusBarTintResource(R.color.md_light_green_600);
    }

    @TargetApi(19)
    public void setTranslucentStatus(Activity activity, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public void showProgressDialog(String content) {
        if (mDialog == null) {
            mDialog = new ProgressDialog(this);
            mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置风格为圆形进度条
            mDialog.setMessage(content);
            mDialog.setIndeterminate(false);// 设置进度条是否为不明确
            mDialog.setCancelable(true);// 设置进度条是否可以按退回键取消
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    mDialog = null;
                }
            });
            mDialog.show();

        }
    }

    public void closeProgressDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    public void showAlertDialog(String title, String msg, String okButTitle, String closeButTitle, DialogInterface.OnClickListener mOnClickListener) {
        AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(this);
        mAlertDialog.setIcon(R.drawable.ic_launcher);
        mAlertDialog.setTitle(title);
        mAlertDialog.setMessage(msg);
        mAlertDialog.setPositiveButton(okButTitle, mOnClickListener);
        mAlertDialog.setNegativeButton(closeButTitle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        mAlertDialog.show();
    }

    public void startUserActivity(Bundle mBundle, Class mClass) {
        Intent intent = new Intent();
        if (mBundle != null) {
            intent.putExtras(mBundle);
        }
        intent.setClass(this, mClass);
        startActivity(intent);
    }

    public void sendUserBroadcast(String Action, Bundle bundle) {
        Intent intentB = new Intent(Action);
        if (bundle != null) {
            intentB.putExtras(bundle);
        }
        sendBroadcast(intentB);
    }

    public OKCardUrlListBean fromCardUrlJson(String json) {

        try {
            OKCardUrlListBean bean = new Gson().fromJson(json, OKCardUrlListBean.class);
            return bean;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public String getFirstCardImageUrl(OKCardUrlListBean bean) {
        if (!TextUtils.isEmpty(bean.getUrlImage1())) {
            return bean.getUrlImage1();
        }
        if (!TextUtils.isEmpty(bean.getUrlImage2())) {
            return bean.getUrlImage2();
        }
        if (!TextUtils.isEmpty(bean.getUrlImage3())) {
            return bean.getUrlImage3();
        }
        if (!TextUtils.isEmpty(bean.getUrlImage4())) {
            return bean.getUrlImage4();
        }
        if (!TextUtils.isEmpty(bean.getUrlImage5())) {
            return bean.getUrlImage5();
        }
        return "";
    }

    public String getFirstCardImageUrl(OKCardBean cardBean) {
        if (cardBean == null) {
            return "";
        }

        if (cardBean.getBean() == null) {
            OKCardUrlListBean bean = fromCardUrlJson(cardBean.getCONTENT_IMAGE_URL());
            if (bean != null) {
                cardBean.setBean(bean);
                return getFirstCardImageUrl(bean);
            } else {
                return cardBean.getCONTENT_IMAGE_URL();
            }
        } else {
            return getFirstCardImageUrl(cardBean.getBean());
        }
    }

    public String getLastCardImageUrl(OKCardUrlListBean bean) {

        if (!TextUtils.isEmpty(bean.getUrlImage5())) {
            return bean.getUrlImage5();
        }
        if (!TextUtils.isEmpty(bean.getUrlImage4())) {
            return bean.getUrlImage4();
        }
        if (!TextUtils.isEmpty(bean.getUrlImage3())) {
            return bean.getUrlImage3();
        }
        if (!TextUtils.isEmpty(bean.getUrlImage2())) {
            return bean.getUrlImage2();
        }
        if (!TextUtils.isEmpty(bean.getUrlImage1())) {
            return bean.getUrlImage1();
        }
        return "";
    }

    public String getLastCardImageUrl(OKCardBean cardBean) {
        if (cardBean == null) {
            return "";
        }

        if (cardBean.getBean() == null) {
            OKCardUrlListBean bean = fromCardUrlJson(cardBean.getCONTENT_IMAGE_URL());
            if (bean != null) {
                cardBean.setBean(bean);
                return getLastCardImageUrl(bean);
            } else {
                return cardBean.getCONTENT_IMAGE_URL();
            }
        } else {
            return getLastCardImageUrl(cardBean.getBean());
        }
    }

    private View mEmptyView;
    private Button mEmptyReGetButton;
    private TextView mEmptyTextViewTitle;
    private final int NO_TAG = -10000;
    public final int LOG_IN = 10000;
    public final int RE_GET = 10001;

    public View initCollapsingEmptyView(View.OnClickListener listener) {
        mEmptyView = LayoutInflater.from(this).inflate(R.layout.ok_content_collapsing_no_data, null);
        mEmptyReGetButton = (Button) mEmptyView.findViewById(R.id.ok_content_no_data_reGet_button);
        mEmptyTextViewTitle = (TextView) mEmptyView.findViewById(R.id.ok_content_collapsing_no_data_title_text);
        mEmptyReGetButton.setOnClickListener(listener);
        return mEmptyView;
    }

    public View initCommonEmptyView(View.OnClickListener listener) {
        mEmptyView = LayoutInflater.from(this).inflate(R.layout.ok_content_common_no_data, null);
        mEmptyReGetButton = (Button) mEmptyView.findViewById(R.id.ok_content_no_data_reGet_button);
        mEmptyTextViewTitle = (TextView) mEmptyView.findViewById(R.id.ok_common_no_data_title_text);
        mEmptyReGetButton.setOnClickListener(listener);
        return mEmptyView;
    }

    public void setEmptyButtonTitle(String title) {
        if (mEmptyReGetButton != null) {
            mEmptyReGetButton.setText(title);
        }
    }

    public String getEmptyButtonTitle() {
        if (mEmptyReGetButton != null) {
            mEmptyReGetButton.getText().toString();
        }
        return "";
    }

    public void setEmptyTextTitle(String title) {
        if (mEmptyTextViewTitle != null) {
            mEmptyTextViewTitle.setText(title);
        }
    }

    public String getEmptyTextTitle() {
        if (mEmptyTextViewTitle != null) {
            return mEmptyTextViewTitle.getText().toString();
        }
        return "";
    }

    public void setEmptyButtonTag(int value) {
        if (mEmptyReGetButton != null) {
            mEmptyReGetButton.setTag(R.id.reGetButton, value);
        }
    }

    public int getEmptyButtonTag() {
        if (mEmptyReGetButton != null) {
            return (int) mEmptyReGetButton.getTag(R.id.reGetButton);
        }
        return NO_TAG;
    }

    public String formatTime(String date) {
        String time[] = date.split("/");
        if (time.length != 5) {
            return date;
        }
        String nowDate = OKConstant.getNowDate();
        String nowTime[] = nowDate.split("/");
        if ((time[0].equals(nowTime[0])) && (time[1].equals(nowTime[1])) && (time[2].equals(nowTime[2]))) {
            return "今天 " + time[3] + ":" + time[4];
        } else if ((time[0].equals(nowTime[0])) && (time[1].equals(nowTime[1])) && (Integer.parseInt(nowTime[2]) - Integer.parseInt(time[2]) == 1)) {
            return "昨天 " + time[3] + ":" + time[4];
        } else if (time[0].equals(nowTime[0])) {
            return time[1] + "月" + time[2] + "日" + " " + time[3] + ":" + time[4];
        } else {
            return time[0] + "年" + time[1] + "月" + time[2] + "日" + " " + time[3] + ":" + time[4];
        }
    }
}
