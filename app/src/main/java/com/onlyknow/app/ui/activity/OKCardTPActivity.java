package com.onlyknow.app.ui.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onlyknow.app.OKConstant;
import com.onlyknow.app.R;
import com.onlyknow.app.net.OKBusinessNet;
import com.onlyknow.app.database.OKDatabaseHelper;
import com.onlyknow.app.database.bean.OKCardAndCommentBean;
import com.onlyknow.app.database.bean.OKCardBean;
import com.onlyknow.app.database.bean.OKCardBindBean;
import com.onlyknow.app.database.bean.OKCardUrlListBean;
import com.onlyknow.app.database.bean.OKSearchBean;
import com.onlyknow.app.database.bean.OKUserInfoBean;
import com.onlyknow.app.ui.OKBaseActivity;
import com.onlyknow.app.ui.view.OKCircleImageView;
import com.onlyknow.app.ui.view.OKSEImageView;
import com.onlyknow.app.utils.OKDeviceInfoUtil;
import com.onlyknow.app.utils.OKLogUtil;
import com.onlyknow.app.utils.compress.OKFileUtil;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 图片卡片浏览界面
 * 传入启动界面标识与列表Position
 * 匹配缓存中的副本列表,如果匹配失败该界面将退出
 * Created by ReSet on 2018/03/01.
 */

public class OKCardTPActivity extends OKBaseActivity {
    @Bind(R.id.MESSAGE_top_biaoti_imag)
    OKCircleImageView MESSAGETopBiaotiImag;

    @Bind(R.id.MESSAGE_top_biaoti_text)
    TextView MESSAGETopBiaotiText;

    @Bind(R.id.MESSAGE_top_qianmin_text)
    TextView MESSAGETopQianminText;

    @Bind(R.id.MESSAGE_top_guanzhu_but)
    Button MESSAGETopGuanzhuBut;

    @Bind(R.id.MESSAGE_top_zhuye_layout)
    LinearLayout MESSAGETopZhuyeLayout;

    @Bind(R.id.MESSAGE_top_zan_imag)
    OKSEImageView MESSAGETopZanImag;

    @Bind(R.id.MESSAGE_top_zan_text)
    TextView MESSAGETopZanText;

    @Bind(R.id.MESSAGE_top_shouchang_imag)
    OKSEImageView MESSAGETopShouchangImag;

    @Bind(R.id.MESSAGE_top_shouchang_text)
    TextView MESSAGETopShouchangText;

    @Bind(R.id.MESSAGE_top_pinglun_imag)
    OKSEImageView MESSAGETopPinglunImag;

    @Bind(R.id.MESSAGE_top_pinglun_text)
    TextView MESSAGETopPinglunText;

    @Bind(R.id.MESSAGE_biaoqian_imag)
    ImageView MESSAGEBiaoqianImag;

    @Bind(R.id.MESSAGE_biaoqian_text)
    TextView MESSAGEBiaoqianText;

    @Bind(R.id.MESSAGE_top_cardView)
    CardView MESSAGETopCardView;

    @Bind(R.id.ok_activity_card_image_content_image1)
    ImageView okActivityCardImageContentImage1;

    @Bind(R.id.ok_activity_card_image_content_image2)
    ImageView okActivityCardImageContentImage2;

    @Bind(R.id.ok_activity_card_image_content_image3)
    ImageView okActivityCardImageContentImage3;

    @Bind(R.id.ok_activity_card_image_content_image4)
    ImageView okActivityCardImageContentImage4;

    @Bind(R.id.ok_activity_card_image_content_image5)
    ImageView okActivityCardImageContentImage5;

    @Bind(R.id.ok_activity_card_image_date_text)
    TextView okActivityCardImageDateText;

    private int mInterfaceType, mPosition, mCardId;
    private OKCardBean mCardBean;
    private OKCardUrlListBean mOKCardUrlListBean;

    private CardBindTask mCardBindTask;
    private OKCardBindBean mCardBindBean;
    private CardTask mCardTask;

    private UMShareListener mShareListener = new UMShareListener() {

        @Override
        public void onStart(SHARE_MEDIA share_media) {
        }

        @Override
        public void onResult(SHARE_MEDIA share_media) {
            showSnackbar(MESSAGETopCardView, "分享成功", "");
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            showSnackbar(MESSAGETopCardView, "分享失败", "ErrorCode: " + throwable.getMessage());
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {
            showSnackbar(MESSAGETopCardView, "分享取消", "");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ok_activity_card_image);
        ButterKnife.bind(this);
        initUserInfoSharedPreferences();
        initSystemBar(this);
        mInterfaceType = getIntent().getExtras().getInt(INTENT_KEY_INTERFACE_TYPE);
        mPosition = getIntent().getExtras().getInt(INTENT_KEY_LIST_POSITION);
        mCardId = getIntent().getExtras().getInt(INTENT_KEY_LIST_CARD_ID);
        mCardBean = loadCardBean(); // 启动该Activity时首先加载数据源

        findView();
        init();
        addCardBrowsing();
    }

    private OKCardBean loadCardBean() {
        if (mInterfaceType == INTERFACE_CARD_AND_COMMENT) {
            List<OKCardAndCommentBean> list = OKConstant.getListCache(INTERFACE_CARD_AND_COMMENT);
            if (list.size() == 0 || mPosition >= list.size()) {
                finish();
                showSnackbar(MESSAGETopCardView, "数据源错误", "ErrorCode :" + OKConstant.DATA_SOURCE_ERROR);
                return null;
            }
            OKCardAndCommentBean bean = list.get(mPosition);
            if (bean == null || bean.getOKCardBean() == null) {
                finish();
                showSnackbar(MESSAGETopCardView, "数据源错误", "ErrorCode :" + OKConstant.DATA_SOURCE_ERROR);
                return null;
            }
            mCardBean = bean.getOKCardBean();
            if (mCardBean == null || mCardBean.getCARD_ID() != mCardId) {
                finish();
                showSnackbar(MESSAGETopCardView, "数据源错误", "ErrorCode :" + OKConstant.DATA_SOURCE_ERROR);
                return null;
            }
            mCardBean.setIS_READ(true);
            mCardBean.setREAD_DATE_LONG(OKConstant.getNowDateByLong());
            bean.setOKCardBean(mCardBean);
            list.set(mPosition, bean);
        } else if (mInterfaceType == INTERFACE_SEARCH) {
            List<OKSearchBean> list = OKConstant.getListCache(INTERFACE_SEARCH);
            if (list.size() == 0 || mPosition >= list.size()) {
                finish();
                showSnackbar(MESSAGETopCardView, "数据源错误", "ErrorCode :" + OKConstant.DATA_SOURCE_ERROR);
                return null;
            }
            OKSearchBean searchBean = list.get(mPosition);
            if (searchBean == null || searchBean.getCardBean() == null) {
                finish();
                showSnackbar(MESSAGETopCardView, "数据源错误", "ErrorCode :" + OKConstant.DATA_SOURCE_ERROR);
                return null;
            }
            mCardBean = searchBean.getCardBean();
            if (mCardBean == null || mCardBean.getCARD_ID() != mCardId) {
                finish();
                showSnackbar(MESSAGETopCardView, "数据源错误", "ErrorCode :" + OKConstant.DATA_SOURCE_ERROR);
                return null;
            }
            mCardBean.setIS_READ(true);
            mCardBean.setREAD_DATE_LONG(OKConstant.getNowDateByLong());
            searchBean.setCardBean(mCardBean);
            list.set(mPosition, searchBean);
        } else {
            List<OKCardBean> list = OKConstant.getListCache(mInterfaceType);
            if (list.size() == 0 || mPosition >= list.size()) {
                finish();
                showSnackbar(MESSAGETopCardView, "数据源错误", "ErrorCode :" + OKConstant.DATA_SOURCE_ERROR);
                return null;
            }
            mCardBean = list.get(mPosition);
            if (mCardBean == null || mCardBean.getCARD_ID() != mCardId) {
                finish();
                showSnackbar(MESSAGETopCardView, "数据源错误", "ErrorCode :" + OKConstant.DATA_SOURCE_ERROR);
                return null;
            }
            mCardBean.setIS_READ(true);
            mCardBean.setREAD_DATE_LONG(OKConstant.getNowDateByLong());
            list.set(mPosition, mCardBean);
        }

        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    OKDatabaseHelper helper = OKDatabaseHelper.getHelper(OKCardTPActivity.this);
                    helper.getCardDao().createOrUpdate(mCardBean);
                } catch (SQLException e) {
                    e.printStackTrace();
                    OKLogUtil.print("卡片记录更新错误 ErrorMsg :" + e.getMessage());
                }
            }
        }.start();
        return mCardBean;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCardBindTask = new CardBindTask();
        Map<String, String> map = new HashMap<>();
        map.put("username", USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
        map.put("username2", mCardBean.getUSER_NAME());
        map.put("card_id", Integer.toString(mCardBean.getCARD_ID()));
        mCardBindTask.executeOnExecutor(exec, map);

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mCardBindTask != null && mCardBindTask.getStatus() == AsyncTask.Status.RUNNING) {
            mCardBindTask.cancel(true); // 如果线程已经在执行则取消执行
        }

        if (mCardTask != null && mCardTask.getStatus() == AsyncTask.Status.RUNNING) {
            mCardTask.cancel(true); // 如果线程已经在执行则取消执行
        }
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // mToolbar 必须在onCreate()之后设置标题文本，否则默认标签将覆盖我们的设置
        if (mToolbar != null) {
            mToolbar.setTitle("");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.ok_menu_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_message_jubao) {
            Intent intent = new Intent();
            intent.setClass(OKCardTPActivity.this, OKRePortActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("JUBAO_TYPE", "CARD");
            bundle.putString("JUBAO_CARD_ID", Integer.toString(mCardBean.getCARD_ID()));
            intent.putExtras(bundle);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    private void findView() {
        super.findCommonToolbarView(this);
        setSupportActionBar(mToolbar);

        mToolbarBack.setVisibility(View.VISIBLE);
        mToolbarSharing.setVisibility(View.VISIBLE);
        mToolbarTitle.setVisibility(View.VISIBLE);

        mToolbarTitle.setText("精彩图片");
    }

    private void init() {
        if (mCardBean.getBean() != null) {
            mOKCardUrlListBean = mCardBean.getBean();
        } else {
            mOKCardUrlListBean = fromCardUrlJson(mCardBean.getCONTENT_IMAGE_URL());
            if (mOKCardUrlListBean == null) {
                mOKCardUrlListBean = new OKCardUrlListBean();
                mOKCardUrlListBean.setCount(1);
                mOKCardUrlListBean.setUrlImage1(mCardBean.getCONTENT_IMAGE_URL());
            }
            mCardBean.setBean(mOKCardUrlListBean);
        }

        int count = mOKCardUrlListBean.getCount();

        if (count == 0) showSnackbar(MESSAGETopCardView, "没有图片地址", "");

        if (count == 1) {
            okActivityCardImageContentImage2.setVisibility(View.GONE);
            okActivityCardImageContentImage3.setVisibility(View.GONE);
            okActivityCardImageContentImage4.setVisibility(View.GONE);
            okActivityCardImageContentImage5.setVisibility(View.GONE);
        } else if (count == 2) {
            okActivityCardImageContentImage3.setVisibility(View.GONE);
            okActivityCardImageContentImage4.setVisibility(View.GONE);
            okActivityCardImageContentImage5.setVisibility(View.GONE);
        } else if (count == 3) {
            okActivityCardImageContentImage4.setVisibility(View.GONE);
            okActivityCardImageContentImage5.setVisibility(View.GONE);
        } else if (count == 4) {
            okActivityCardImageContentImage5.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(mOKCardUrlListBean.getUrlImage1())) {
            GlideApi(okActivityCardImageContentImage1, mOKCardUrlListBean.getUrlImage1(), R.drawable.topgd1, R.drawable.topgd1);
        }

        if (!TextUtils.isEmpty(mOKCardUrlListBean.getUrlImage2())) {
            GlideApi(okActivityCardImageContentImage2, mOKCardUrlListBean.getUrlImage2(), R.drawable.topgd1, R.drawable.topgd1);
        }

        if (!TextUtils.isEmpty(mOKCardUrlListBean.getUrlImage3())) {
            GlideApi(okActivityCardImageContentImage3, mOKCardUrlListBean.getUrlImage3(), R.drawable.topgd1, R.drawable.topgd1);
        }

        if (!TextUtils.isEmpty(mOKCardUrlListBean.getUrlImage4())) {
            GlideApi(okActivityCardImageContentImage4, mOKCardUrlListBean.getUrlImage4(), R.drawable.topgd1, R.drawable.topgd1);
        }

        if (!TextUtils.isEmpty(mOKCardUrlListBean.getUrlImage5())) {
            GlideApi(okActivityCardImageContentImage5, mOKCardUrlListBean.getUrlImage5(), R.drawable.topgd1, R.drawable.topgd1);
        }

        // 加载标题图片
        GlideRoundApi(MESSAGETopBiaotiImag, mCardBean.getTITLE_IMAGE_URL(), R.drawable.touxian_placeholder, R.drawable.touxian_placeholder);

        // 加载内容图片
        MESSAGETopBiaotiText.setText(mCardBean.getTITLE_TEXT());
        MESSAGETopQianminText.setText("这个人很懒 , 什么都没有留下!");
        MESSAGETopZanText.setText("" + mCardBean.getZAN_NUM());
        MESSAGETopShouchangText.setText("" + mCardBean.getSHOUCHAN_NUM());
        MESSAGETopPinglunText.setText("" + mCardBean.getPINGLUN_NUM());
        MESSAGEBiaoqianText.setText("精彩图片");
        okActivityCardImageDateText.setText(formatTime(mCardBean.getCREATE_DATE()) + " 发表");

        mToolbarSharing.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareAction mShareAction = new ShareAction(OKCardTPActivity.this);
                mShareAction.setDisplayList(SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.WEIXIN);
                UMImage image = new UMImage(OKCardTPActivity.this, getFirstCardImageUrl(mCardBean));
                UMImage thumb = new UMImage(OKCardTPActivity.this, R.drawable.ic_launcher);
                image.setThumb(thumb);
                image.compressStyle = UMImage.CompressStyle.SCALE;
                image.compressStyle = UMImage.CompressStyle.QUALITY;
                mShareAction.withText("唯知空间-图片分享").withMedia(image).setCallback(mShareListener).open();
            }
        });

        mToolbarBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                OKCardTPActivity.this.finish();
            }
        });

        MESSAGETopGuanzhuBut.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (USER_INFO_SP.getBoolean("STATE", false)) {
                    mCardTask = new CardTask("ATTENTION");
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("username", USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
                    params.put("username2", mCardBean.getUSER_NAME());
                    params.put("card_id", "");
                    params.put("message", "");
                    params.put("date", OKConstant.getNowDateByString());
                    params.put("type", "GUANZHU");
                    mCardTask.executeOnExecutor(exec, params);
                } else {
                    startUserActivity(null, OKLoginActivity.class);
                }
            }
        });

        MESSAGETopZhuyeLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mInterfaceType != INTERFACE_HOME) {
                    Bundle bundle = new Bundle();
                    bundle.putString(OKUserInfoBean.KEY_USERNAME, mCardBean.getUSER_NAME());
                    bundle.putString(OKUserInfoBean.KEY_NICKNAME, mCardBean.getTITLE_TEXT());
                    startUserActivity(bundle, OKHomePageActivity.class);
                } else {
                    finish();
                }
            }
        });

        MESSAGETopZanImag.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (USER_INFO_SP.getBoolean("STATE", false)) {
                    if (mCardBindBean != null && mCardBindBean.IS_ZAN()) {
                        showSnackbar(v, "您已经点赞了", "");
                        return;
                    }

                    mCardTask = new CardTask("ZAN");
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("username", USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
                    params.put("username2", "");
                    params.put("card_id", Integer.toString(mCardBean.getCARD_ID()));
                    params.put("message", "");
                    params.put("date", OKConstant.getNowDateByString());
                    params.put("type", "ZAN");
                    mCardTask.executeOnExecutor(exec, params);
                } else {
                    startUserActivity(null, OKLoginActivity.class);
                }
            }
        });

        MESSAGETopShouchangImag.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (USER_INFO_SP.getBoolean("STATE", false)) {
                    if (mCardBindBean != null && mCardBindBean.IS_WATCH()) {
                        showSnackbar(v, "您已经收藏了", "");
                        return;
                    }

                    mCardTask = new CardTask("WATCH");
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("username", USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
                    params.put("username2", "");
                    params.put("card_id", Integer.toString(mCardBean.getCARD_ID()));
                    params.put("message", "");
                    params.put("date", OKConstant.getNowDateByString());
                    params.put("type", "SHOUCHAN");
                    mCardTask.executeOnExecutor(exec, params);
                } else {
                    startUserActivity(null, OKLoginActivity.class);
                }
            }
        });

        MESSAGETopPinglunImag.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(OKUserInfoBean.KEY_USERNAME, mCardBean.getUSER_NAME());
                bundle.putInt(OKCardBean.KEY_CARD_ID, mCardBean.getCARD_ID());
                bundle.putInt("DH_ID", mInterfaceType);
                bundle.putInt("POS", mPosition);
                startUserActivity(bundle, OKCommentActivity.class);
            }
        });

        okActivityCardImageContentImage1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String name[] = mOKCardUrlListBean.getUrlImage1().split("/");
                if (name.length != 0 && OKFileUtil.isVideoFile(name[name.length - 1])) {
                    Bundle bundle = new Bundle();
                    bundle.putString("URL", mOKCardUrlListBean.getUrlImage1());
                    bundle.putString("TITLE", mCardBean.getTITLE_TEXT() + "发表的视频");
                    startUserActivity(bundle, OKVideoActivity.class);
                } else {
                    int location[] = new int[2];
                    okActivityCardImageContentImage1.getLocationOnScreen(location);

                    Bundle mBundle = new Bundle();
                    mBundle.putInt("left", location[0]);
                    mBundle.putInt("top", location[1]);
                    mBundle.putInt("height", okActivityCardImageContentImage1.getHeight());
                    mBundle.putInt("width", okActivityCardImageContentImage1.getWidth());

                    mBundle.putString("url", mOKCardUrlListBean.getUrlImage1());

                    startUserActivity(mBundle, OKDragPhotoActivity.class);
                    overridePendingTransition(0, 0);
                }
            }
        });

        okActivityCardImageContentImage2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String name[] = mOKCardUrlListBean.getUrlImage2().split("/");
                if (name.length != 0 && OKFileUtil.isVideoFile(name[name.length - 1])) {
                    Bundle bundle = new Bundle();
                    bundle.putString("URL", mOKCardUrlListBean.getUrlImage2());
                    bundle.putString("TITLE", mCardBean.getTITLE_TEXT() + "发表的视频");
                    startUserActivity(bundle, OKVideoActivity.class);
                } else {
                    int location[] = new int[2];
                    okActivityCardImageContentImage2.getLocationOnScreen(location);

                    Bundle mBundle = new Bundle();
                    mBundle.putInt("left", location[0]);
                    mBundle.putInt("top", location[1]);
                    mBundle.putInt("height", okActivityCardImageContentImage2.getHeight());
                    mBundle.putInt("width", okActivityCardImageContentImage2.getWidth());

                    mBundle.putString("url", mOKCardUrlListBean.getUrlImage2());

                    startUserActivity(mBundle, OKDragPhotoActivity.class);
                    overridePendingTransition(0, 0);
                }
            }
        });

        okActivityCardImageContentImage3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String name[] = mOKCardUrlListBean.getUrlImage3().split("/");
                if (name.length != 0 && OKFileUtil.isVideoFile(name[name.length - 1])) {
                    Bundle bundle = new Bundle();
                    bundle.putString("URL", mOKCardUrlListBean.getUrlImage3());
                    bundle.putString("TITLE", mCardBean.getTITLE_TEXT() + "发表的视频");
                    startUserActivity(bundle, OKVideoActivity.class);
                } else {
                    int location[] = new int[2];
                    okActivityCardImageContentImage3.getLocationOnScreen(location);

                    Bundle mBundle = new Bundle();
                    mBundle.putInt("left", location[0]);
                    mBundle.putInt("top", location[1]);
                    mBundle.putInt("height", okActivityCardImageContentImage3.getHeight());
                    mBundle.putInt("width", okActivityCardImageContentImage3.getWidth());

                    mBundle.putString("url", mOKCardUrlListBean.getUrlImage3());

                    startUserActivity(mBundle, OKDragPhotoActivity.class);
                    overridePendingTransition(0, 0);
                }
            }
        });

        okActivityCardImageContentImage4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String name[] = mOKCardUrlListBean.getUrlImage4().split("/");
                if (name.length != 0 && OKFileUtil.isVideoFile(name[name.length - 1])) {
                    Bundle bundle = new Bundle();
                    bundle.putString("URL", mOKCardUrlListBean.getUrlImage4());
                    bundle.putString("TITLE", mCardBean.getTITLE_TEXT() + "发表的视频");
                    startUserActivity(bundle, OKVideoActivity.class);
                } else {
                    int location[] = new int[2];
                    okActivityCardImageContentImage4.getLocationOnScreen(location);

                    Bundle mBundle = new Bundle();
                    mBundle.putInt("left", location[0]);
                    mBundle.putInt("top", location[1]);
                    mBundle.putInt("height", okActivityCardImageContentImage4.getHeight());
                    mBundle.putInt("width", okActivityCardImageContentImage4.getWidth());

                    mBundle.putString("url", mOKCardUrlListBean.getUrlImage4());

                    startUserActivity(mBundle, OKDragPhotoActivity.class);
                    overridePendingTransition(0, 0);
                }
            }
        });

        okActivityCardImageContentImage5.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String name[] = mOKCardUrlListBean.getUrlImage5().split("/");
                if (name.length != 0 && OKFileUtil.isVideoFile(name[name.length - 1])) {
                    Bundle bundle = new Bundle();
                    bundle.putString("URL", mOKCardUrlListBean.getUrlImage5());
                    bundle.putString("TITLE", mCardBean.getTITLE_TEXT() + "发表的视频");
                    startUserActivity(bundle, OKVideoActivity.class);
                } else {
                    int location[] = new int[2];
                    okActivityCardImageContentImage5.getLocationOnScreen(location);

                    Bundle mBundle = new Bundle();
                    mBundle.putInt("left", location[0]);
                    mBundle.putInt("top", location[1]);
                    mBundle.putInt("height", okActivityCardImageContentImage5.getHeight());
                    mBundle.putInt("width", okActivityCardImageContentImage5.getWidth());

                    mBundle.putString("url", mOKCardUrlListBean.getUrlImage5());

                    startUserActivity(mBundle, OKDragPhotoActivity.class);
                    overridePendingTransition(0, 0);
                }
            }
        });
    }

    private void addCardBrowsing() {
        new Thread() {
            @Override
            public void run() {
                OKDeviceInfoUtil equipmentInformation = new OKDeviceInfoUtil(OKCardTPActivity.this);
                String equipment = equipmentInformation.getIMIE();

                Date now = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd/HH/mm");

                Map<String, String> map = new HashMap<String, String>();
                map.put("card_id", Integer.toString(mCardBean.getCARD_ID()));
                map.put("username", USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, "Anonymous"));
                map.put("equipment", equipment);
                map.put("date", dateFormat.format(now));
                new OKBusinessNet().addCardBrowsing(map);
            }
        }.start();
    }

    private class CardTask extends AsyncTask<Map<String, String>, Void, Boolean> {
        private String Type = "";

        public CardTask(String type) {
            this.Type = type;
        }

        @Override
        protected Boolean doInBackground(Map<String, String>... params) {
            if (isCancelled()) {
                return false;
            }
            return new OKBusinessNet().updateCardInfo(params[0]);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (isCancelled()) {
                return;
            }

            if (aBoolean) {
                if (Type.equals("ATTENTION")) {
                    if (mCardBindBean == null) {
                        mCardBindBean = new OKCardBindBean();
                    }
                    mCardBindBean.setIS_ATTENTION(true);

                    MESSAGETopGuanzhuBut.setText("已关注");
                    MESSAGETopGuanzhuBut.setEnabled(false);
                } else if (Type.equals("WATCH")) {
                    if (mCardBindBean == null) {
                        mCardBindBean = new OKCardBindBean();
                    }
                    mCardBindBean.setIS_WATCH(true);

                    int count = mCardBean.getSHOUCHAN_NUM() + 1;
                    MESSAGETopShouchangText.setText("" + count);
                    MESSAGETopShouchangText.setTextColor(getResources().getColor(R.color.fenhon));
                } else if (Type.equals("ZAN")) {
                    if (mCardBindBean == null) {
                        mCardBindBean = new OKCardBindBean();
                    }
                    mCardBindBean.setIS_ZAN(true);

                    int count = mCardBean.getZAN_NUM() + 1;
                    MESSAGETopZanText.setText("" + count);
                    MESSAGETopZanText.setTextColor(getResources().getColor(R.color.fenhon));
                }
            } else {
                showSnackbar(MESSAGETopCardView, "操作失败,请重试", "");
            }
        }
    }

    private class CardBindTask extends AsyncTask<Map<String, String>, Void, OKCardBindBean> {

        @Override
        protected OKCardBindBean doInBackground(Map<String, String>... params) {
            if (isCancelled()) {
                return null;
            }

            return new OKBusinessNet().getCardBind(params[0]);
        }

        @Override
        protected void onPostExecute(OKCardBindBean cardBindBean) {
            super.onPostExecute(cardBindBean);
            if (isCancelled() || cardBindBean == null) {
                return;
            }

            mCardBindBean = cardBindBean;

            if (mCardBindBean.isCardRemove()) {
                MESSAGETopZanImag.setEnabled(false);
                MESSAGETopShouchangImag.setEnabled(false);
                MESSAGETopPinglunImag.setEnabled(false);
                mToolbarTitle.setText("该卡片已被用户删除");
                showSnackbar(MESSAGETopCardView, "该卡片已被用户删除", "");
            }

            if (mCardBindBean.IS_ATTENTION()) {
                MESSAGETopGuanzhuBut.setText("已关注");
                MESSAGETopGuanzhuBut.setEnabled(false);
            }

            if (mCardBindBean.IS_WATCH()) {
                MESSAGETopShouchangText.setTextColor(getResources().getColor(R.color.fenhon));
            }
            if (mCardBindBean.IS_ZAN()) {
                MESSAGETopZanText.setTextColor(getResources().getColor(R.color.fenhon));
            }
            if (TextUtils.isEmpty(mCardBindBean.getQIANMIN()) || mCardBindBean.getQIANMIN().equals("NULL")) {
                MESSAGETopQianminText.setText("这个人很懒 , 什么都没有留下!");
            } else {
                MESSAGETopQianminText.setText(mCardBindBean.getQIANMIN());
            }
            MESSAGETopZanText.setText("" + mCardBindBean.getZAN_COUNT());
            MESSAGETopShouchangText.setText("" + mCardBindBean.getWATCH_COUNT());
            MESSAGETopPinglunText.setText("" + mCardBindBean.getPINLUN_COUNT());
        }
    }
}
