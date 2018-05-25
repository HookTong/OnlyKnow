package com.onlyknow.app.ui;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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
import com.onlyknow.app.GlideApp;
import com.onlyknow.app.OKConstant;
import com.onlyknow.app.R;
import com.onlyknow.app.db.bean.OKCardBean;
import com.onlyknow.app.db.bean.OKWeatherBean;
import com.onlyknow.app.ui.activity.OKLoginActivity;
import com.onlyknow.app.ui.activity.OKQrCodeRecognitionActivity;
import com.onlyknow.app.ui.activity.OKSettingActivity;
import com.onlyknow.app.ui.activity.OKUserEditActivity;
import com.onlyknow.app.ui.view.OKCatLoadingView;
import com.onlyknow.app.ui.view.OKSEImageView;
import com.onlyknow.app.utils.OKBarTintUtil;
import com.onlyknow.app.utils.OKBlurTransformation;
import com.onlyknow.app.utils.OKFileUtil;
import com.onlyknow.app.utils.OKLogUtil;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMVideo;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.File;
import java.util.List;

/**
 * 扩展Activity基本能力;
 * <p>
 * 所有Activity都必须继承该基本Activity
 * <p>
 * Created by Administrator on 2017/12/8.
 */

public class OKBaseActivity extends AppCompatActivity {
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

    protected final String ACTION_MAIN_SERVICE_LOGIN_IM = "ACTION_MAIN_SERVICE_LOGIN_IM";
    protected final String ACTION_MAIN_SERVICE_LOGOUT_IM = "ACTION_MAIN_SERVICE_LOGOUT_IM";
    protected final String ACTION_MAIN_SERVICE_CREATE_ACCOUNT_IM = "ACTION_MAIN_SERVICE_CREATE_ACCOUNT_IM";
    protected final String ACTION_MAIN_SERVICE_ADD_MESSAGE_LISTENER_IM = "ACTION_MAIN_SERVICE_ADD_MESSAGE_LISTENER_IM";
    protected final String ACTION_MAIN_SERVICE_REMOVE_MESSAGE_LISTENER_IM = "ACTION_MAIN_SERVICE_REMOVE_MESSAGE_LISTENER_IM";

    protected final String INTENT_KEY_GAN_KIO = "";
    protected final int GAN_KIO_TYPE_FL = 1;
    protected final int GAN_KIO_TYPE_VIDEO = 2;
    protected final int GAN_KIO_TYPE_RES = 3;
    protected final int GAN_KIO_TYPE_ANDROID = 4;
    protected final int GAN_KIO_TYPE_IOS = 5;
    protected final int GAN_KIO_TYPE_H5 = 6;

    protected final String CARD_TYPE_TW = OKCardBean.CardType.IMAGE_TEXT.toString();
    protected final String CARD_TYPE_TP = OKCardBean.CardType.IMAGE.toString();
    protected final String CARD_TYPE_WZ = OKCardBean.CardType.TEXT.toString();

    protected SharedPreferences USER_BODY, SETTING_BODY, WEATHER_BODY, ARTICLE_BODY;

    // toolbar控件
    protected Toolbar mToolbar;
    protected OKSEImageView mToolbarBack, mToolbarMenu, mToolbarSearch, mToolbarEdit, mToolbarSharing, mToolbarSend, mToolbarLogout, mToolbarAddImage;
    protected OKSEImageView mToolbarAdd;// 只能初始化通用toolbar才能调用
    protected TextView mToolbarTitle;
    protected ProgressBar mToolBarProgressBar; // 只能初始化通用toolbar才能调用

    protected OKCatLoadingView mCatLoadingView;

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
    protected void onDestroy() {
        super.onDestroy();
        closeProgressDialog();
        System.gc();
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        OKLogUtil.print("onConfigurationChanged : " + newConfig.toString());
    }

    // find两个不同的toolbar,在一个界面中只能find一个
    protected final void findCollapsingToolbarView() {
        if (mToolbar != null) return;

        mToolbar = (Toolbar) findViewById(R.id.ok_collapsing_toolbar);
        mToolbarBack = (OKSEImageView) findViewById(R.id.ok_collapsing_toolbar_back_image);
        mToolbarMenu = (OKSEImageView) findViewById(R.id.ok_collapsing_toolbar_menu_image);
        mToolbarSearch = (OKSEImageView) findViewById(R.id.ok_collapsing_toolbar_search_image);
        mToolbarEdit = (OKSEImageView) findViewById(R.id.ok_collapsing_toolbar_edit_image);
        mToolbarSharing = (OKSEImageView) findViewById(R.id.ok_collapsing_toolbar_sharing_image);
        mToolbarSend = (OKSEImageView) findViewById(R.id.ok_collapsing_toolbar_sendmessage_image);
        mToolbarLogout = (OKSEImageView) findViewById(R.id.ok_collapsing_toolbar_logout_image);
        mToolbarTitle = (TextView) findViewById(R.id.ok_collapsing_toolbar_title_text);
        mToolBarProgressBar = (ProgressBar) findViewById(R.id.ok_collapsing_toolbar_progressBar);
    }

    protected final void findCommonToolbarView() {
        if (mToolbar != null) return;

        mToolbar = (Toolbar) findViewById(R.id.ok_common_toolbar);
        mToolbarBack = (OKSEImageView) findViewById(R.id.ok_common_toolbar_back_image);
        mToolbarMenu = (OKSEImageView) findViewById(R.id.ok_common_toolbar_menu_image);
        mToolbarSearch = (OKSEImageView) findViewById(R.id.ok_common_toolbar_search_image);
        mToolbarEdit = (OKSEImageView) findViewById(R.id.ok_common_toolbar_edit_image);
        mToolbarSharing = (OKSEImageView) findViewById(R.id.ok_common_toolbar_sharing_image);
        mToolbarSend = (OKSEImageView) findViewById(R.id.ok_common_toolbar_sendmessage_image);
        mToolbarLogout = (OKSEImageView) findViewById(R.id.ok_common_toolbar_logout_image);
        mToolbarTitle = (TextView) findViewById(R.id.ok_common_toolbar_title_text);
        mToolbarAddImage = (OKSEImageView) findViewById(R.id.ok_common_toolbar_addTp_image);
        mToolbarAdd = (OKSEImageView) findViewById(R.id.ok_common_toolbar_add_image);
        mToolBarProgressBar = (ProgressBar) findViewById(R.id.ok_common_toolbar_progressBar);
    }

    protected final void showMainMenu() {
        View parent = ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
        View popView = View.inflate(this, R.layout.ok_main_menu, null);
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
        linearLayoutLogin.setOnClickListener(listener);
        linearLayoutQrCode.setOnClickListener(listener);
        linearLayoutSetting.setOnClickListener(listener);
        buttonClose.setOnClickListener(listener);
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
        GlideApp.with(this).load(id).centerCrop().placeholder(placeholderId).error(errorId).into(mView);
    }

    protected final void GlideApi(ImageView mView, String url, int placeholderId, int errorId) {
        GlideApp.with(this).load(url).centerCrop().placeholder(placeholderId).error(errorId).into(mView);
    }

    protected final void GlideRoundApi(ImageView mView, String url, int placeholderId, int errorId) {
        GlideApp.with(this).load(url).apply(RequestOptions.circleCropTransform()).placeholder(placeholderId).error(errorId).into(mView);
    }

    protected final void GlideRoundApi(ImageView mView, int id, int placeholderId, int errorId) {
        GlideApp.with(this).load(id).apply(RequestOptions.circleCropTransform()).placeholder(placeholderId).error(errorId).into(mView);
    }

    protected final void GlideBlurApi(ImageView mView, String url, int placeholderId, int errorId) {
        GlideApp.with(this).load(url).placeholder(placeholderId).error(errorId).transform(new OKBlurTransformation(this, 25)).into(mView);
    }

    protected final void GlideBlurApi(ImageView mView, int id, int placeholderId, int errorId) {
        GlideApp.with(this).load(id).placeholder(placeholderId).error(errorId).transform(new OKBlurTransformation(this, 25)).into(mView);
    }

    protected final void initUserBody() {
        if (USER_BODY == null) {
            USER_BODY = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        }
    }

    protected final void initSettingBody() {
        if (SETTING_BODY == null) {
            SETTING_BODY = this.getSharedPreferences("setting", Context.MODE_PRIVATE);
        }
    }

    protected final void initWeatherBody() {
        if (WEATHER_BODY == null) {
            WEATHER_BODY = this.getSharedPreferences("weather", Context.MODE_PRIVATE);
        }
    }

    protected final void initArticleBody() {
        if (ARTICLE_BODY == null) {
            ARTICLE_BODY = this.getSharedPreferences("release", Context.MODE_PRIVATE);
        }
    }

    protected final void initStatusBar() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window win = this.getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            winParams.flags |= bits;
            win.setAttributes(winParams);
        }

        OKBarTintUtil tintManager = new OKBarTintUtil(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.md_light_green_600);

    }

    protected final void showProgressDialog(String content) {
        if (mCatLoadingView == null) {
            mCatLoadingView = new OKCatLoadingView();
            mCatLoadingView.setLoadText(content);
            mCatLoadingView.show(getSupportFragmentManager(), "");
        }
    }

    protected final void closeProgressDialog() {
        if (mCatLoadingView != null) {
            mCatLoadingView.dismiss();
            mCatLoadingView = null;
        }
    }

    protected final void startUserActivity(Bundle mBundle, Class mClass) {
        Intent intent = new Intent();
        if (mBundle != null) {
            intent.putExtras(mBundle);
        }
        intent.setClass(this, mClass);
        startActivity(intent);
    }

    protected final void sendUserBroadcast(String Action, Bundle bundle) {
        Intent intentB = new Intent(Action);
        if (bundle != null) {
            intentB.putExtras(bundle);
        }
        sendBroadcast(intentB);
    }

    /**
     * 启动裁剪;
     *
     * @param sourceFilePath 需要裁剪图片的绝对路径;
     * @param aspectRatioX   裁剪图片宽高比;
     * @param aspectRatioY   裁剪图片宽高比;
     */
    protected final void startUCrop(String sourceFilePath, float aspectRatioX, float aspectRatioY) {

        Uri sourceUri = Uri.fromFile(new File(sourceFilePath));

        File outDir = new File(OKConstant.IMAGE_PATH);

        if (!outDir.exists()) outDir.mkdirs();

        File outFile = new File(outDir, System.currentTimeMillis() + ".jpg");

        Uri destinationUri = Uri.fromFile(outFile); // 输出uri;

        UCrop uCrop = UCrop.of(sourceUri, destinationUri); // 初始化,第一个参数:需要裁剪的图片,第二个参数:裁剪后图片;

        UCrop.Options options = new UCrop.Options(); // 初始化UCrop配置;

        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL); // 设置裁剪图片可操作的手势;
        options.setHideBottomControls(false); // 是否隐藏底部容器,默认显示;
        options.setToolbarColor(ActivityCompat.getColor(this, R.color.md_light_green_500)); // 设置toolbar颜色;
        options.setStatusBarColor(ActivityCompat.getColor(this, R.color.md_light_green_600)); // 设置状态栏颜色;
        options.setFreeStyleCropEnabled(true); // 是否能调整裁剪框;

        uCrop.withOptions(options); // UCrop配置;

        uCrop.withAspectRatio(aspectRatioX, aspectRatioY); // 设置裁剪图片的宽高比,比如16：9;

        uCrop.start(this, UCrop.REQUEST_CROP); // 跳转裁剪页面;

    }

    private View mEmptyView;
    private Button mEmptyButton;
    private TextView mEmptyTextView;

    // EmptyButTag
    protected final int TAG_NOT = -10000;
    protected final int TAG_LOGIN = 10000;
    protected final int TAG_RETRY = 10001;

    protected final View initCollapsingEmptyView(View.OnClickListener listener) {
        mEmptyView = LayoutInflater.from(this).inflate(R.layout.ok_content_collapsing_no_data, null);
        mEmptyButton = (Button) mEmptyView.findViewById(R.id.ok_content_no_data_reGet_button);
        mEmptyTextView = (TextView) mEmptyView.findViewById(R.id.ok_content_collapsing_no_data_title_text);
        mEmptyButton.setOnClickListener(listener);
        mEmptyButton.setTag(R.id.reGetButton, TAG_NOT);
        return mEmptyView;
    }

    protected final View initCommonEmptyView(View.OnClickListener listener) {
        mEmptyView = LayoutInflater.from(this).inflate(R.layout.ok_content_common_no_data, null);
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

    protected final void shareImage(final List<OKCardBean.CardImage> list, final UMShareListener mShareListener) {
        if (list == null || list.size() == 0) return;

        if (list.size() == 1) {
            if (OKFileUtil.isVideoUrl(list.get(0).getUrl())) {
                UMVideo video = new UMVideo(list.get(0).getUrl());
                UMImage thumb = new UMImage(this, R.drawable.ic_launcher);
                video.setTitle("唯知空间-视频分享");//视频的标题
                video.setThumb(thumb);//视频的缩略图

                ShareAction mShareAction = new ShareAction(this);
                mShareAction.setDisplayList(SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.WEIXIN);
                mShareAction.withText("唯知空间-视频分享").withMedia(video).setCallback(mShareListener).open();
            } else {
                UMImage image = new UMImage(this, list.get(0).getUrl());
                UMImage thumb = new UMImage(this, R.drawable.ic_launcher);
                image.setTitle("唯知空间-图片分享");
                image.setThumb(thumb);
                image.compressStyle = UMImage.CompressStyle.SCALE;
                image.compressStyle = UMImage.CompressStyle.QUALITY;

                ShareAction mShareAction = new ShareAction(this);
                mShareAction.setDisplayList(SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.WEIXIN);
                mShareAction.withText("唯知空间-图片分享").withMedia(image).setCallback(mShareListener).open();
            }
            return;
        }

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        final View dialogView = LayoutInflater.from(this).inflate(R.layout.ok_dialog_select_image, null);
        final LinearLayout layout[] = new LinearLayout[5];
        final ImageView image[] = new ImageView[5];

        layout[0] = (LinearLayout) dialogView.findViewById(R.id.ok_dialog_select_image_layout1);
        layout[1] = (LinearLayout) dialogView.findViewById(R.id.ok_dialog_select_image_layout2);
        layout[2] = (LinearLayout) dialogView.findViewById(R.id.ok_dialog_select_image_layout3);
        layout[3] = (LinearLayout) dialogView.findViewById(R.id.ok_dialog_select_image_layout4);
        layout[4] = (LinearLayout) dialogView.findViewById(R.id.ok_dialog_select_image_layout5);

        image[0] = (ImageView) dialogView.findViewById(R.id.ok_dialog_select_image1);
        image[1] = (ImageView) dialogView.findViewById(R.id.ok_dialog_select_image2);
        image[2] = (ImageView) dialogView.findViewById(R.id.ok_dialog_select_image3);
        image[3] = (ImageView) dialogView.findViewById(R.id.ok_dialog_select_image4);
        image[4] = (ImageView) dialogView.findViewById(R.id.ok_dialog_select_image5);

        mBuilder.setView(dialogView);
        final AlertDialog mAlertDialog = mBuilder.show();

        for (int i = 0; i < list.size(); i++) {
            GlideApi(image[i], list.get(i).getUrl(), R.drawable.topgd1, R.drawable.topgd1);
            layout[i].setVisibility(View.VISIBLE);
        }
        for (int i = 0; i < 5; i++) {
            layout[i].setTag(R.id.select_image, i);
            layout[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int i = (int) v.getTag(R.id.select_image);
                    if (i >= list.size()) return;

                    mAlertDialog.dismiss();

                    if (OKFileUtil.isVideoUrl(list.get(i).getUrl())) {
                        UMVideo video = new UMVideo(list.get(0).getUrl());
                        UMImage thumb = new UMImage(OKBaseActivity.this, R.drawable.ic_launcher);
                        video.setTitle("唯知空间-视频分享");//视频的标题
                        video.setThumb(thumb);//视频的缩略图

                        ShareAction mShareAction = new ShareAction(OKBaseActivity.this);
                        mShareAction.setDisplayList(SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.WEIXIN);
                        mShareAction.withText("唯知空间-视频分享").withMedia(video).setCallback(mShareListener).open();
                    } else {
                        UMImage image = new UMImage(OKBaseActivity.this, list.get(i).getUrl());
                        UMImage thumb = new UMImage(OKBaseActivity.this, R.drawable.ic_launcher);
                        image.setTitle("唯知空间-图片分享");
                        image.setThumb(thumb);
                        image.compressStyle = UMImage.CompressStyle.SCALE;
                        image.compressStyle = UMImage.CompressStyle.QUALITY;

                        ShareAction mShareAction = new ShareAction(OKBaseActivity.this);
                        mShareAction.setDisplayList(SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.WEIXIN);
                        mShareAction.withText("唯知空间-图片分享").withMedia(image).setCallback(mShareListener).open();
                    }
                }
            });
        }
    }
}
