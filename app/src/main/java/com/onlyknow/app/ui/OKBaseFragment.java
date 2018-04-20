package com.onlyknow.app.ui;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
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
import com.onlyknow.app.GlideApp;
import com.onlyknow.app.OKConstant;
import com.onlyknow.app.R;
import com.onlyknow.app.db.bean.OKCardBean;
import com.onlyknow.app.db.bean.OKWeatherBean;
import com.onlyknow.app.ui.activity.OKGanKActivity;
import com.onlyknow.app.ui.activity.OKLoginActivity;
import com.onlyknow.app.ui.activity.OKQrCodeRecognitionActivity;
import com.onlyknow.app.ui.activity.OKSettingActivity;
import com.onlyknow.app.ui.activity.OKUserEditActivity;
import com.onlyknow.app.ui.view.OKCatLoadingView;
import com.onlyknow.app.ui.view.OKSEImageView;
import com.onlyknow.app.utils.OKBarTintUtil;
import com.onlyknow.app.utils.OKBlurTransformation;
import com.onlyknow.app.utils.OKLunarUtil;

import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;

/**
 * 所有Fragment都必须继承该基本Fragment
 * <p>
 * Created by Administrator on 2017/12/8.
 */

public class OKBaseFragment extends Fragment {
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
    public final int INTERFACE_APPROVE = 16;

    public final String ACTION_MAIN_SERVICE_SHOW_NOTICE = "ACTION_MAIN_SERVICE_SHOW_NOTICE";
    public final String ACTION_MAIN_SERVICE_LOGIN_IM = "ACTION_MAIN_SERVICE_LOGIN_IM";
    public final String ACTION_MAIN_SERVICE_LOGOUT_IM = "ACTION_MAIN_SERVICE_LOGOUT_IM";
    public final String ACTION_MAIN_SERVICE_CREATE_ACCOUNT_IM = "ACTION_MAIN_SERVICE_CREATE_ACCOUNT_IM";
    public final String ACTION_MAIN_SERVICE_ADD_MESSAGE_LISTENER_IM = "ACTION_MAIN_SERVICE_ADD_MESSAGE_LISTENER_IM";
    public final String ACTION_MAIN_SERVICE_REMOVE_MESSAGE_LISTENER_IM = "ACTION_MAIN_SERVICE_REMOVE_MESSAGE_LISTENER_IM";

    public final String CARD_TYPE_TW = OKCardBean.CardType.IMAGE_TEXT.toString();
    public final String CARD_TYPE_TP = OKCardBean.CardType.IMAGE.toString();
    public final String CARD_TYPE_WZ = OKCardBean.CardType.TEXT.toString();

    public SharedPreferences USER_INFO_SP, SETTING_SP, WEATHER_SP, ARTICLE_SP;

    // toolbar控件
    public Toolbar mToolbar;
    public OKSEImageView mToolbarBack, mToolbarMenu, mToolbarSearch, mToolbarEdit, mToolbarSharing, mToolbarSend, mToolbarLogout;
    public TextView mToolbarTitle;
    public ProgressBar mToolBarProgressBar; // 只能初始化通用toolbar才能调用

    public OKCatLoadingView mCatLoadingView;

    @Override
    public void onResume() {
        super.onResume();
        Glide.with(this).resumeRequests();
    }

    @Override
    public void onPause() {
        super.onPause();
        Glide.with(this).pauseRequests();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closeProgressDialog();
    }

    // find两个不同的toolbar,在一个界面中只能find一个
    public void findCollapsingToolbarView(View rootView) {
        mToolbar = (Toolbar) rootView.findViewById(R.id.ok_collapsing_toolbar);
        mToolbarBack = (OKSEImageView) rootView.findViewById(R.id.ok_collapsing_toolbar_back_image);
        mToolbarMenu = (OKSEImageView) rootView.findViewById(R.id.ok_collapsing_toolbar_menu_image);
        mToolbarSearch = (OKSEImageView) rootView.findViewById(R.id.ok_collapsing_toolbar_search_image);
        mToolbarEdit = (OKSEImageView) rootView.findViewById(R.id.ok_collapsing_toolbar_edit_image);
        mToolbarSharing = (OKSEImageView) rootView.findViewById(R.id.ok_collapsing_toolbar_sharing_image);
        mToolbarSend = (OKSEImageView) rootView.findViewById(R.id.ok_collapsing_toolbar_sendmessage_image);
        mToolbarLogout = (OKSEImageView) rootView.findViewById(R.id.ok_collapsing_toolbar_logout_image);
        mToolbarTitle = (TextView) rootView.findViewById(R.id.ok_collapsing_toolbar_title_text);
        mToolBarProgressBar = (ProgressBar) rootView.findViewById(R.id.ok_collapsing_toolbar_progressBar);
    }

    public void findCommonToolbarView(View rootView) {
        mToolbar = (Toolbar) rootView.findViewById(R.id.ok_common_toolbar);
        mToolbarBack = (OKSEImageView) rootView.findViewById(R.id.ok_common_toolbar_back_image);
        mToolbarMenu = (OKSEImageView) rootView.findViewById(R.id.ok_common_toolbar_menu_image);
        mToolbarSearch = (OKSEImageView) rootView.findViewById(R.id.ok_common_toolbar_search_image);
        mToolbarEdit = (OKSEImageView) rootView.findViewById(R.id.ok_common_toolbar_edit_image);
        mToolbarSharing = (OKSEImageView) rootView.findViewById(R.id.ok_common_toolbar_sharing_image);
        mToolbarSend = (OKSEImageView) rootView.findViewById(R.id.ok_common_toolbar_sendmessage_image);
        mToolbarLogout = (OKSEImageView) rootView.findViewById(R.id.ok_common_toolbar_logout_image);
        mToolbarTitle = (TextView) rootView.findViewById(R.id.ok_common_toolbar_title_text);
        mToolBarProgressBar = (ProgressBar) rootView.findViewById(R.id.ok_common_toolbar_progressBar);
    }

    public void showMenu(final Activity activity) {
        View parent = ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
        View popView = View.inflate(activity, R.layout.ok_main_menu, null);
        LinearLayout linearLayoutlogin = (LinearLayout) popView.findViewById(R.id.POP_LOGIN_LAYOU);
        LinearLayout linearLayoutqrcode = (LinearLayout) popView.findViewById(R.id.POP_QRCODE_LAYOU);
        LinearLayout linearLayoutsetting = (LinearLayout) popView.findViewById(R.id.POP_SETTING_LAYOU);
        Button btnguanbi = (Button) popView.findViewById(R.id.POP_GUANBI_BUT);
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
                            intent.setClass(activity, OKUserEditActivity.class);
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
                        intent.setClass(activity, OKQrCodeRecognitionActivity.class);
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
        linearLayoutlogin.setOnClickListener(listener);
        linearLayoutqrcode.setOnClickListener(listener);
        linearLayoutsetting.setOnClickListener(listener);
        btnguanbi.setOnClickListener(listener);
        ColorDrawable dw = new ColorDrawable(0x30000000);
        popWindow.setBackgroundDrawable(dw);
        popWindow.showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    public void showSnackBar(View view, String message, String mAction) {
        Snackbar.make(view, message + " " + mAction, Snackbar.LENGTH_SHORT).show();
    }

    public void GlideApi(ImageView mView, int id, int placeholderId, int errorId) {
        GlideApp.with(getActivity()).load(id).centerCrop().placeholder(placeholderId).error(errorId).into(mView);
    }

    public void GlideApi(ImageView mView, String url, int placeholderId, int errorId) {
        GlideApp.with(getActivity()).load(url).centerCrop().placeholder(placeholderId).error(errorId).into(mView);
    }

    public void GlideRoundApi(ImageView mView, String url, int placeholderId, int errorId) {
        GlideApp.with(getActivity()).load(url).apply(RequestOptions.circleCropTransform()).placeholder(placeholderId).error(errorId).into(mView);
    }

    public void GlideRoundApi(ImageView mView, int id, int placeholderId, int errorId) {
        GlideApp.with(getActivity()).load(id).apply(RequestOptions.circleCropTransform()).placeholder(placeholderId).error(errorId).into(mView);
    }

    public void GlideBlurApi(ImageView mView, String url, int placeholderId, int errorId) {
        GlideApp.with(getActivity()).load(url).placeholder(placeholderId).error(errorId).transform(new OKBlurTransformation(getActivity(), 25)).into(mView);
    }

    public void GlideBlurApi(ImageView mView, int id, int placeholderId, int errorId) {
        GlideApp.with(getActivity()).load(id).placeholder(placeholderId).error(errorId).transform(new OKBlurTransformation(getActivity(), 25)).into(mView);
    }

    public SharedPreferences initUserInfoSharedPreferences() {
        if (USER_INFO_SP == null) {
            USER_INFO_SP = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        }
        return USER_INFO_SP;
    }

    public SharedPreferences initSettingSharedPreferences() {
        if (SETTING_SP == null) {
            SETTING_SP = getActivity().getSharedPreferences("setting", Context.MODE_PRIVATE);
        }
        return SETTING_SP;
    }

    public SharedPreferences initWeatherSharedPreferences() {
        if (WEATHER_SP == null) {
            WEATHER_SP = getActivity().getSharedPreferences("weather", Context.MODE_PRIVATE);
        }
        return WEATHER_SP;
    }

    public SharedPreferences initArticleSharedPreferences() {
        if (ARTICLE_SP == null) {
            ARTICLE_SP = getActivity().getSharedPreferences("release", Context.MODE_PRIVATE);
        }
        return ARTICLE_SP;
    }

    public void startUserActivity(Bundle mBundle, Class mClass) {
        Intent intent = new Intent();
        if (mBundle != null) {
            intent.putExtras(mBundle);
        }
        intent.setClass(getActivity(), mClass);
        getActivity().startActivity(intent);
    }

    public void sendUserBroadcast(String Action, Bundle bundle) {
        Intent mIntent = new Intent(Action);
        if (bundle != null) {
            mIntent.putExtras(bundle);
        }
        getActivity().sendBroadcast(mIntent);
    }

    public void showProgressDialog(String content) {
        if (mCatLoadingView == null) {
            mCatLoadingView = new OKCatLoadingView();
            mCatLoadingView.setLoadText(content);
            mCatLoadingView.show(getActivity().getSupportFragmentManager(), "");
        }
    }

    public void closeProgressDialog() {
        if (mCatLoadingView != null) {
            mCatLoadingView.dismiss();
            mCatLoadingView = null;
        }
    }

    public void showAlertDialog(String title, String msg, String okButTitle, String closeButTitle, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(getActivity());
        mAlertDialog.setIcon(R.drawable.ic_launcher);
        mAlertDialog.setTitle(title);
        mAlertDialog.setMessage(msg);
        mAlertDialog.setPositiveButton(okButTitle, listener);
        mAlertDialog.setNegativeButton(closeButTitle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        mAlertDialog.show();
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
    public static void setTranslucentStatus(Activity activity, boolean on) {
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

    private View mEmptyView;
    private Button mEmptyReGetButton;
    private TextView mEmptyTextViewTitle;
    private final int NO_TAG = -10000;
    public final int LOG_IN = 10000;
    public final int RE_GET = 10001;

    public View initCollapsingEmptyView(View.OnClickListener listener) {
        mEmptyView = LayoutInflater.from(getActivity()).inflate(R.layout.ok_content_collapsing_no_data, null);
        mEmptyReGetButton = (Button) mEmptyView.findViewById(R.id.ok_content_no_data_reGet_button);
        mEmptyTextViewTitle = (TextView) mEmptyView.findViewById(R.id.ok_content_collapsing_no_data_title_text);
        mEmptyReGetButton.setOnClickListener(listener);
        return mEmptyView;
    }

    public View initCommonEmptyView(View.OnClickListener listener) {
        mEmptyView = LayoutInflater.from(getActivity()).inflate(R.layout.ok_content_common_no_data, null);
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
            return mEmptyReGetButton.getText().toString();
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

    public void saveWeatherInfo(OKWeatherBean weatherBean) {
        initWeatherSharedPreferences();
        SharedPreferences.Editor editor = WEATHER_SP.edit();
        OKWeatherBean.Forecast mForecast = weatherBean.data.forecast.get(0);
        editor.putString("CITY_NAME", USER_INFO_SP.getString("CITY_NAME", ""));
        editor.putString("CITY_ID", USER_INFO_SP.getString("CITY_ID", ""));
        editor.putString("DISTRICT", USER_INFO_SP.getString("DISTRICT", ""));
        editor.putString("TEMPERATURE", weatherBean.data.wendu + " ℃");
        editor.putString("TEMPERATURE_LOW", mForecast.low.replace(" ", ""));
        editor.putString("TEMPERATURE_HIG", mForecast.high.replace(" ", ""));
        editor.putString("WEATHER_TYPE", mForecast.type);
        editor.putString("WIND_DIRECTION", mForecast.fengxiang);
        editor.putString("GAN_MAO", weatherBean.data.ganmao);
        editor.putString("WEATHER_DATE", mForecast.date);
        editor.putString("WEATHER_DATE_WEEK", mForecast.date.substring(mForecast.date.indexOf("日") + 1)); // 星期几
        editor.commit();
    }

    public final String INTENT_KEY_GAN_KIO = "";
    public final int GAN_KIO_TYPE_FL = 1;
    public final int GAN_KIO_TYPE_VIDEO = 2;
    public final int GAN_KIO_TYPE_RES = 3;
    public final int GAN_KIO_TYPE_ANDROID = 4;
    public final int GAN_KIO_TYPE_IOS = 5;
    public final int GAN_KIO_TYPE_H5 = 6;

    public void startGanKioActivity(int type) {
        Bundle mBundle = new Bundle();
        mBundle.putInt(INTENT_KEY_GAN_KIO, type);
        startUserActivity(mBundle, OKGanKActivity.class);
    }

    public void bindNavigationHeadView(View headerView) {
        initWeatherSharedPreferences();
        ImageView mImageViewBackground = headerView.findViewById(R.id.ok_menu_head_drawer_image);
        ImageView mImageViewWeatherType = headerView.findViewById(R.id.ok_menu_head_drawer_tianqi_image);
        TextView mTextViewCity = headerView.findViewById(R.id.ok_menu_head_drawer_city_text);
        TextView mTextViewType = headerView.findViewById(R.id.ok_menu_head_drawer_weather_type_text);
        TextView mTextViewWenDu = headerView.findViewById(R.id.ok_menu_head_drawer_weather_wendu_text);
        TextView mTextViewNonLi = headerView.findViewById(R.id.ok_menu_head_drawer_nonli_text);
        int i = new Random().nextInt(5);
        GlideBlurApi(mImageViewBackground, OKConstant.getCarouselImages().get(i).get("URL").toString(), R.drawable.topgd2, R.drawable.topgd2);
        GlideApi(mImageViewWeatherType, WEATHER_SP.getInt("WEATHER_ICON_ID", R.drawable.tianqi_other), R.drawable.tianqi_other, R.drawable.tianqi_other);
        mTextViewCity.setText(WEATHER_SP.getString("CITY_NAME", "未获取到城市"));
        mTextViewType.setText(WEATHER_SP.getString("WEATHER_TYPE", "N") + " / " + WEATHER_SP.getString("TEMPERATURE", "A"));
        mTextViewWenDu.setText(WEATHER_SP.getString("TEMPERATURE_LOW", "N") + " / " + WEATHER_SP.getString("TEMPERATURE_HIG", "A"));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        OKLunarUtil lunar = new OKLunarUtil(calendar);
        mTextViewNonLi.setText(lunar.toString() + " " + WEATHER_SP.getString("WEATHER_DATE_WEEK", ""));
    }

    public class BarToggle extends ActionBarDrawerToggle {

        public BarToggle(Activity activity, DrawerLayout drawerLayout, int openRes, int closeRes) {
            super(activity, drawerLayout, mToolbar, openRes, closeRes);
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            super.onDrawerClosed(drawerView);
        }
    }
}
