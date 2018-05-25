package com.onlyknow.app.ui;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.onlyknow.app.utils.OKBlurTransformation;
import com.onlyknow.app.utils.OKLunarUtil;

import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;

/**
 * 扩展Fragment基本能力;
 * <p>
 * 所有Fragment都必须继承该基本Fragment;
 * <p>
 * Created by Reset on 2018/05/24;
 */

public class OKBaseFragment extends Fragment {
    protected final String INTENT_KEY_INTERFACE_TYPE = "InterfaceType";
    protected final String INTENT_KEY_LIST_POSITION = "ListPosition";
    protected final String INTENT_KEY_LIST_CARD_ID = "ListCardId";

    protected final int INTERFACE_EXPLORE = 1; // 探索界面
    protected final int INTERFACE_NEAR = 2; // 附近界面
    protected final int INTERFACE_HISTORY = 3; // 历史界面
    protected final int INTERFACE_DYNAMIC = 4; // 动态界面
    protected final int INTERFACE_ATTENTION = 5; // 关注界面
    protected final int INTERFACE_COLLECTION = 6; // 收藏界面
    protected final int INTERFACE_HOT = 7; // 热门界面
    protected final int INTERFACE_HOME = 8; // 用户展示界面
    protected final int INTERFACE_GOODS = 9; // 商品界面
    protected final int INTERFACE_NOTICE = 10; // 通知界面
    protected final int INTERFACE_SESSION = 11; // 会话界面
    protected final int INTERFACE_COMMENT = 12; // 卡片评论界面
    protected final int INTERFACE_COMMENT_REPLY = 13; // 评论回复界面
    protected final int INTERFACE_SEARCH = 14; // 搜索界面
    protected final int INTERFACE_CARD_AND_COMMENT = 15;
    protected final int INTERFACE_APPROVE = 16;

    protected final String ACTION_MAIN_SERVICE_LOGIN_IM = "ACTION_MAIN_SERVICE_LOGIN_IM";
    protected final String ACTION_MAIN_SERVICE_LOGOUT_IM = "ACTION_MAIN_SERVICE_LOGOUT_IM";
    protected final String ACTION_MAIN_SERVICE_CREATE_ACCOUNT_IM = "ACTION_MAIN_SERVICE_CREATE_ACCOUNT_IM";
    protected final String ACTION_MAIN_SERVICE_ADD_MESSAGE_LISTENER_IM = "ACTION_MAIN_SERVICE_ADD_MESSAGE_LISTENER_IM";
    protected final String ACTION_MAIN_SERVICE_REMOVE_MESSAGE_LISTENER_IM = "ACTION_MAIN_SERVICE_REMOVE_MESSAGE_LISTENER_IM";

    protected final String CARD_TYPE_TW = OKCardBean.CardType.IMAGE_TEXT.toString();
    protected final String CARD_TYPE_TP = OKCardBean.CardType.IMAGE.toString();
    protected final String CARD_TYPE_WZ = OKCardBean.CardType.TEXT.toString();

    protected SharedPreferences USER_BODY, SETTING_BODY, WEATHER_BODY, ARTICLE_BODY;

    // toolbar控件
    protected Toolbar mToolbar;
    protected OKSEImageView mToolbarBack, mToolbarMenu, mToolbarSearch, mToolbarEdit, mToolbarSharing, mToolbarSend, mToolbarLogout;
    protected TextView mToolbarTitle;
    protected ProgressBar mToolBarProgressBar; // 只能初始化通用toolbar才能调用

    protected OKCatLoadingView mCatLoadingView;

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
    protected final void findCollapsingToolbarView(View rootView) {
        if (mToolbar != null) return;

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

    protected final void findCommonToolbarView(View rootView) {
        if (mToolbar != null) return;

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

    protected final void showMainMenu() {
        View parent = ((ViewGroup) getActivity().findViewById(android.R.id.content)).getChildAt(0);
        View popView = View.inflate(getActivity(), R.layout.ok_main_menu, null);
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
                        initUserBody();
                        if (USER_BODY.getBoolean("STATE", false)) {
                            startUserActivity(null, OKUserEditActivity.class);
                        } else {
                            startUserActivity(null, OKLoginActivity.class);
                        }
                        popWindow.dismiss();
                        break;
                    case R.id.POP_QRCODE_LAYOU:
                        startUserActivity(null, OKQrCodeRecognitionActivity.class);
                        popWindow.dismiss();
                        break;
                    case R.id.POP_SETTING_LAYOU:
                        startUserActivity(null, OKSettingActivity.class);
                        popWindow.dismiss();
                        break;
                }
            }
        };
        linearLayoutlogin.setOnClickListener(listener);
        linearLayoutqrcode.setOnClickListener(listener);
        linearLayoutsetting.setOnClickListener(listener);
        btnguanbi.setOnClickListener(listener);
        ColorDrawable dw = new ColorDrawable(getResources().getColor(R.color.transparency));
        popWindow.setBackgroundDrawable(dw);
        popWindow.showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    protected final void showSnackBar(View view, String message, String code) {

        if (TextUtils.isEmpty(code)) {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(view, "MSG:" + message + " CODE:" + code, Snackbar.LENGTH_SHORT).show();
        }

    }

    protected final void GlideApi(ImageView mView, int id, int placeholderId, int errorId) {
        GlideApp.with(getActivity()).load(id).centerCrop().placeholder(placeholderId).error(errorId).into(mView);
    }

    protected final void GlideApi(ImageView mView, String url, int placeholderId, int errorId) {
        GlideApp.with(getActivity()).load(url).centerCrop().placeholder(placeholderId).error(errorId).into(mView);
    }

    protected final void GlideRoundApi(ImageView mView, String url, int placeholderId, int errorId) {
        GlideApp.with(getActivity()).load(url).apply(RequestOptions.circleCropTransform()).placeholder(placeholderId).error(errorId).into(mView);
    }

    protected final void GlideRoundApi(ImageView mView, int id, int placeholderId, int errorId) {
        GlideApp.with(getActivity()).load(id).apply(RequestOptions.circleCropTransform()).placeholder(placeholderId).error(errorId).into(mView);
    }

    protected final void GlideBlurApi(ImageView mView, String url, int placeholderId, int errorId) {
        GlideApp.with(getActivity()).load(url).placeholder(placeholderId).error(errorId).transform(new OKBlurTransformation(getActivity(), 25)).into(mView);
    }

    protected final void GlideBlurApi(ImageView mView, int id, int placeholderId, int errorId) {
        GlideApp.with(getActivity()).load(id).placeholder(placeholderId).error(errorId).transform(new OKBlurTransformation(getActivity(), 25)).into(mView);
    }

    protected final void initUserBody() {
        if (USER_BODY == null) {
            USER_BODY = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        }
    }

    protected final void initSettingBody() {
        if (SETTING_BODY == null) {
            SETTING_BODY = getActivity().getSharedPreferences("setting", Context.MODE_PRIVATE);
        }
    }

    protected final void initWeatherBody() {
        if (WEATHER_BODY == null) {
            WEATHER_BODY = getActivity().getSharedPreferences("weather", Context.MODE_PRIVATE);
        }
    }

    protected final void initArticleBody() {
        if (ARTICLE_BODY == null) {
            ARTICLE_BODY = getActivity().getSharedPreferences("release", Context.MODE_PRIVATE);
        }
    }

    protected final void startUserActivity(Bundle mBundle, Class mClass) {
        Intent intent = new Intent();
        if (mBundle != null) {
            intent.putExtras(mBundle);
        }
        intent.setClass(getActivity(), mClass);
        getActivity().startActivity(intent);
    }

    protected final void sendUserBroadcast(String Action, Bundle bundle) {
        Intent mIntent = new Intent(Action);
        if (bundle != null) {
            mIntent.putExtras(bundle);
        }
        getActivity().sendBroadcast(mIntent);
    }

    protected final void showProgressDialog(String content) {
        if (mCatLoadingView == null) {
            mCatLoadingView = new OKCatLoadingView();
            mCatLoadingView.setLoadText(content);
            mCatLoadingView.show(getActivity().getSupportFragmentManager(), "");
        }
    }

    protected final void closeProgressDialog() {
        if (mCatLoadingView != null) {
            mCatLoadingView.dismiss();
            mCatLoadingView = null;
        }
    }

    // EmptyView
    private View mEmptyView;
    private Button mEmptyButton;
    private TextView mEmptyTextView;

    // EmptyButTag
    protected final int TAG_NOT = -10000;
    protected final int TAG_LOGIN = 10000;
    protected final int TAG_RETRY = 10001;

    protected final View initCollapsingEmptyView(View.OnClickListener listener) {
        mEmptyView = LayoutInflater.from(getActivity()).inflate(R.layout.ok_content_collapsing_no_data, null);
        mEmptyButton = (Button) mEmptyView.findViewById(R.id.ok_content_no_data_reGet_button);
        mEmptyTextView = (TextView) mEmptyView.findViewById(R.id.ok_content_collapsing_no_data_title_text);
        mEmptyButton.setOnClickListener(listener);
        mEmptyButton.setTag(R.id.reGetButton, TAG_NOT);
        return mEmptyView;
    }

    protected final View initCommonEmptyView(View.OnClickListener listener) {
        mEmptyView = LayoutInflater.from(getActivity()).inflate(R.layout.ok_content_common_no_data, null);
        mEmptyButton = (Button) mEmptyView.findViewById(R.id.ok_content_no_data_reGet_button);
        mEmptyTextView = (TextView) mEmptyView.findViewById(R.id.ok_common_no_data_title_text);
        mEmptyButton.setOnClickListener(listener);
        mEmptyButton.setTag(R.id.reGetButton, TAG_NOT);
        return mEmptyView;
    }

    protected final void setEmptyButTitle(String title) {
        if (mEmptyButton != null) {
            mEmptyButton.setText(title);
        }
    }

    protected final void setEmptyTxtTitle(String title) {
        if (mEmptyTextView != null) {
            mEmptyTextView.setText(title);
        }
    }

    protected final void setEmptyTag(int value) {
        if (mEmptyButton != null) {
            mEmptyButton.setTag(R.id.reGetButton, value);
        }
    }

    protected final int getEmptyTag() {
        if (mEmptyButton != null) {
            return (int) mEmptyButton.getTag(R.id.reGetButton);
        }
        return TAG_NOT;
    }

    protected final void saveWeatherInfo(OKWeatherBean weatherBean) {
        initWeatherBody();
        initUserBody();

        OKWeatherBean.Forecast mForecast = weatherBean.data.forecast.get(0);

        SharedPreferences.Editor editor = WEATHER_BODY.edit();
        editor.putString("CITY_NAME", USER_BODY.getString("CITY_NAME", ""));
        editor.putString("CITY_ID", USER_BODY.getString("CITY_ID", ""));
        editor.putString("DISTRICT", USER_BODY.getString("DISTRICT", ""));
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

    // GanIo启动配置项
    protected final String INTENT_KEY_GAN_KIO = "";
    protected final int GAN_KIO_TYPE_FL = 1;
    protected final int GAN_KIO_TYPE_VIDEO = 2;
    protected final int GAN_KIO_TYPE_RES = 3;
    protected final int GAN_KIO_TYPE_ANDROID = 4;
    protected final int GAN_KIO_TYPE_IOS = 5;
    protected final int GAN_KIO_TYPE_H5 = 6;

    protected final void startGanKioActivity(int type) {
        Bundle mBundle = new Bundle();
        mBundle.putInt(INTENT_KEY_GAN_KIO, type);
        startUserActivity(mBundle, OKGanKActivity.class);
    }

    protected final void bindNavigationHeadView(View headerView) {
        initWeatherBody();

        ImageView mImageViewBackground = headerView.findViewById(R.id.ok_menu_head_drawer_image);
        ImageView mImageViewWeatherType = headerView.findViewById(R.id.ok_menu_head_drawer_tianqi_image);
        TextView mTextViewCity = headerView.findViewById(R.id.ok_menu_head_drawer_city_text);
        TextView mTextViewType = headerView.findViewById(R.id.ok_menu_head_drawer_weather_type_text);
        TextView mTextViewWenDu = headerView.findViewById(R.id.ok_menu_head_drawer_weather_wendu_text);
        TextView mTextViewNonLi = headerView.findViewById(R.id.ok_menu_head_drawer_nonli_text);

        int i = new Random().nextInt(5);
        GlideBlurApi(mImageViewBackground, OKConstant.getCarouselImages().get(i).getUrl(), R.drawable.topgd2, R.drawable.topgd2);
        GlideApi(mImageViewWeatherType, WEATHER_BODY.getInt("WEATHER_ICON_ID", R.drawable.tianqi_other), R.drawable.tianqi_other, R.drawable.tianqi_other);

        mTextViewCity.setText(WEATHER_BODY.getString("CITY_NAME", "未获取到城市"));
        mTextViewType.setText(WEATHER_BODY.getString("WEATHER_TYPE", "N") + " / " + WEATHER_BODY.getString("TEMPERATURE", "A"));
        mTextViewWenDu.setText(WEATHER_BODY.getString("TEMPERATURE_LOW", "N") + " / " + WEATHER_BODY.getString("TEMPERATURE_HIG", "A"));

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        OKLunarUtil lunar = new OKLunarUtil(calendar);
        mTextViewNonLi.setText(lunar.toString() + " " + WEATHER_BODY.getString("WEATHER_DATE_WEEK", ""));
    }

    protected final class DrawerToggle extends ActionBarDrawerToggle {

        @SuppressLint("ResourceType")
        public DrawerToggle(DrawerLayout drawerLayout) {
            super(getActivity(), drawerLayout, mToolbar, R.drawable.ok_toolbar_menu, R.drawable.ok_toolbar_back);
        }

        @SuppressLint("ResourceType")
        public DrawerToggle(DrawerLayout drawerLayout, Toolbar toolbar) {
            super(getActivity(), drawerLayout, toolbar, R.drawable.ok_toolbar_menu, R.drawable.ok_toolbar_back);
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
