package com.onlyknow.app.ui.activity;

import android.content.Intent;
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

import com.google.gson.Gson;
import com.onlyknow.app.OKConstant;
import com.onlyknow.app.R;
import com.onlyknow.app.api.OKServiceResult;
import com.onlyknow.app.api.card.OKManagerCardApi;
import com.onlyknow.app.api.user.OKManagerUserApi;
import com.onlyknow.app.database.OKDatabaseHelper;
import com.onlyknow.app.database.bean.OKCardBean;
import com.onlyknow.app.database.bean.OKCardRelatedBean;
import com.onlyknow.app.database.bean.OKUserInfoBean;
import com.onlyknow.app.ui.OKBaseActivity;
import com.onlyknow.app.ui.view.OKCircleImageView;
import com.onlyknow.app.ui.view.OKSEImageView;
import com.onlyknow.app.utils.OKDateUtil;
import com.onlyknow.app.utils.OKFileUtil;
import com.onlyknow.app.utils.OKLogUtil;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 图片卡片浏览界面
 * 传入启动界面标识与列表Position
 * 匹配缓存中的副本列表,如果匹配失败该界面将退出
 * Created by ReSet on 2018/03/01.
 */

public class OKCardTPActivity extends OKBaseActivity implements OKManagerCardApi.onCallBack, OKManagerUserApi.onCallBack {
    public final static String KEY_INTENT_IMAGE_CARD = "IMAGE_CARD";

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

    private OKCardBean mCardBean;
    private List<OKCardBean.CardImage> imageList;
    private int mStartInterfaceType;

    private OKManagerCardApi okManagerCardApi;
    private OKCardRelatedBean mCardBindBean;

    private OKManagerUserApi okManagerUserApi;

    private UMShareListener mShareListener = new UMShareListener() {

        @Override
        public void onStart(SHARE_MEDIA share_media) {
        }

        @Override
        public void onResult(SHARE_MEDIA share_media) {
            showSnackBar(MESSAGETopCardView, "分享成功", "");
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            showSnackBar(MESSAGETopCardView, "分享失败", "ErrorCode: " + throwable.getMessage());
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {
            showSnackBar(MESSAGETopCardView, "分享取消", "");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ok_activity_card_image);
        ButterKnife.bind(this);
        initUserInfoSharedPreferences();
        initSystemBar(this);
        mStartInterfaceType = getIntent().getExtras().getInt(INTENT_KEY_INTERFACE_TYPE);
        mCardBean = (OKCardBean) getIntent().getExtras().getSerializable(KEY_INTENT_IMAGE_CARD);

        if (mCardBean == null) {
            finish();
            return;
        }

        findView();
        init();
        addCardBrowsing();
    }

    @Override
    protected void onResume() {
        super.onResume();

        OKManagerCardApi.Params params = new OKManagerCardApi.Params();
        params.setUsername(USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
        params.setCardId(mCardBean.getCardId());
        params.setType(OKManagerCardApi.Params.TYPE_BIND_CHECK);

        if (okManagerCardApi != null) {
            okManagerCardApi.cancelTask();
        }
        okManagerCardApi = new OKManagerCardApi(this);
        okManagerCardApi.requestManagerCard(params, this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (okManagerCardApi != null) {
            okManagerCardApi.cancelTask(); // 如果线程已经在执行则取消执行
        }

        if (okManagerUserApi != null) {
            okManagerUserApi.cancelTask(); // 如果线程已经在执行则取消执行
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
            Bundle bundle = new Bundle();
            bundle.putString(OKRePortActivity.KEY_TYPE, OKRePortActivity.TYPE_CARD);
            bundle.putString(OKRePortActivity.KEY_ID, Integer.toString(mCardBean.getCardId()));
            startUserActivity(bundle, OKRePortActivity.class);
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
        imageList = mCardBean.getImageList();
        if (imageList == null || imageList.size() == 0) {
            imageList = new ArrayList<>();
        }

        int count = imageList.size();

        if (count == 0) showSnackBar(MESSAGETopCardView, "没有图片地址", "");

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

        for (int i = 0; i < imageList.size(); i++) {
            OKCardBean.CardImage image = imageList.get(i);
            if (i == 0) {
                GlideApi(okActivityCardImageContentImage1, image.getUrl(), R.drawable.topgd1, R.drawable.topgd1);
                okActivityCardImageContentImage1.setTag(R.id.image_tag, image.getUrl());
            } else if (i == 1) {
                GlideApi(okActivityCardImageContentImage2, image.getUrl(), R.drawable.topgd1, R.drawable.topgd1);
                okActivityCardImageContentImage2.setTag(R.id.image_tag, image.getUrl());
            } else if (i == 2) {
                GlideApi(okActivityCardImageContentImage3, image.getUrl(), R.drawable.topgd1, R.drawable.topgd1);
                okActivityCardImageContentImage3.setTag(R.id.image_tag, image.getUrl());
            } else if (i == 3) {
                GlideApi(okActivityCardImageContentImage4, image.getUrl(), R.drawable.topgd1, R.drawable.topgd1);
                okActivityCardImageContentImage4.setTag(R.id.image_tag, image.getUrl());
            } else if (i == 4) {
                GlideApi(okActivityCardImageContentImage5, image.getUrl(), R.drawable.topgd1, R.drawable.topgd1);
                okActivityCardImageContentImage5.setTag(R.id.image_tag, image.getUrl());
            }
        }

        // 加载标题图片
        GlideRoundApi(MESSAGETopBiaotiImag, mCardBean.getTitleImageUrl(), R.drawable.touxian_placeholder, R.drawable.touxian_placeholder);

        // 加载内容图片
        MESSAGETopBiaotiText.setText(mCardBean.getTitleText());
        MESSAGETopQianminText.setText("这个人很懒 , 什么都没有留下!");
        MESSAGETopZanText.setText("" + mCardBean.getPraiseCount());
        MESSAGETopShouchangText.setText("" + mCardBean.getWatchCount());
        MESSAGETopPinglunText.setText("" + mCardBean.getCommentCount());
        MESSAGEBiaoqianText.setText("精彩图片");
        okActivityCardImageDateText.setText(OKDateUtil.formatTime(mCardBean.getCreateDate()) + " 发表");

        mToolbarSharing.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                shareImage(imageList, mShareListener);
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
                    if (mCardBindBean != null && mCardBindBean.isAttention()) {
                        showSnackBar(v, "您已经关注了", "");
                        return;
                    }

                    OKManagerUserApi.Params params = new OKManagerUserApi.Params();
                    params.setUsername(USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
                    params.setAttentionUsername(mCardBean.getUserName());
                    params.setType(OKManagerUserApi.Params.TYPE_ADD_ATTENTION);

                    if (okManagerUserApi != null) {
                        okManagerUserApi.cancelTask();
                    }
                    okManagerUserApi = new OKManagerUserApi(OKCardTPActivity.this);
                    okManagerUserApi.requestManagerUser(params, OKCardTPActivity.this);
                } else {
                    startUserActivity(null, OKLoginActivity.class);
                }
            }
        });

        MESSAGETopZhuyeLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mStartInterfaceType != INTERFACE_HOME) {
                    Bundle bundle = new Bundle();
                    bundle.putString(OKUserInfoBean.KEY_USERNAME, mCardBean.getUserName());
                    bundle.putString(OKUserInfoBean.KEY_NICKNAME, mCardBean.getTitleText());
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
                    if (mCardBindBean != null && mCardBindBean.isPraise()) {
                        showSnackBar(v, "您已经点赞了", "");
                        return;
                    }

                    OKManagerCardApi.Params params = new OKManagerCardApi.Params();
                    params.setUsername(USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
                    params.setCardId(mCardBean.getCardId());
                    params.setType(OKManagerCardApi.Params.TYPE_PRAISE);

                    if (okManagerCardApi != null) {
                        okManagerCardApi.cancelTask();
                    }
                    okManagerCardApi = new OKManagerCardApi(OKCardTPActivity.this);
                    okManagerCardApi.requestManagerCard(params, OKCardTPActivity.this);
                } else {
                    startUserActivity(null, OKLoginActivity.class);
                }
            }
        });

        MESSAGETopShouchangImag.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (USER_INFO_SP.getBoolean("STATE", false)) {
                    if (mCardBindBean != null && mCardBindBean.isWatch()) {
                        showSnackBar(v, "您已经收藏了", "");
                        return;
                    }

                    OKManagerCardApi.Params params = new OKManagerCardApi.Params();
                    params.setUsername(USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
                    params.setCardId(mCardBean.getCardId());
                    params.setType(OKManagerCardApi.Params.TYPE_WATCH);

                    if (okManagerCardApi != null) {
                        okManagerCardApi.cancelTask();
                    }
                    okManagerCardApi = new OKManagerCardApi(OKCardTPActivity.this);
                    okManagerCardApi.requestManagerCard(params, OKCardTPActivity.this);
                } else {
                    startUserActivity(null, OKLoginActivity.class);
                }
            }
        });

        MESSAGETopPinglunImag.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(OKUserInfoBean.KEY_USERNAME, mCardBean.getUserName());
                bundle.putInt(OKCardBean.KEY_CARD_ID, mCardBean.getCardId());
                startUserActivity(bundle, OKCommentActivity.class);
            }
        });

        okActivityCardImageContentImage1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = (String) okActivityCardImageContentImage1.getTag(R.id.image_tag);
                if (TextUtils.isEmpty(url)) return;

                if (OKFileUtil.isVideoUrl(url)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("URL", url);
                    bundle.putString("TITLE", mCardBean.getTitleText() + "发表的视频");
                    startUserActivity(bundle, OKVideoActivity.class);
                } else {
                    int location[] = new int[2];
                    okActivityCardImageContentImage1.getLocationOnScreen(location);

                    Bundle mBundle = new Bundle();
                    mBundle.putInt("left", location[0]);
                    mBundle.putInt("top", location[1]);
                    mBundle.putInt("height", okActivityCardImageContentImage1.getHeight());
                    mBundle.putInt("width", okActivityCardImageContentImage1.getWidth());

                    mBundle.putString("url", url);

                    startUserActivity(mBundle, OKDragPhotoActivity.class);
                    overridePendingTransition(0, 0);
                }
            }
        });

        okActivityCardImageContentImage2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = (String) okActivityCardImageContentImage2.getTag(R.id.image_tag);
                if (TextUtils.isEmpty(url)) return;
                if (OKFileUtil.isVideoUrl(url)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("URL", url);
                    bundle.putString("TITLE", mCardBean.getTitleText() + "发表的视频");
                    startUserActivity(bundle, OKVideoActivity.class);
                } else {
                    int location[] = new int[2];
                    okActivityCardImageContentImage2.getLocationOnScreen(location);

                    Bundle mBundle = new Bundle();
                    mBundle.putInt("left", location[0]);
                    mBundle.putInt("top", location[1]);
                    mBundle.putInt("height", okActivityCardImageContentImage2.getHeight());
                    mBundle.putInt("width", okActivityCardImageContentImage2.getWidth());

                    mBundle.putString("url", url);

                    startUserActivity(mBundle, OKDragPhotoActivity.class);
                    overridePendingTransition(0, 0);
                }
            }
        });

        okActivityCardImageContentImage3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = (String) okActivityCardImageContentImage3.getTag(R.id.image_tag);
                if (TextUtils.isEmpty(url)) return;
                if (OKFileUtil.isVideoUrl(url)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("URL", url);
                    bundle.putString("TITLE", mCardBean.getTitleText() + "发表的视频");
                    startUserActivity(bundle, OKVideoActivity.class);
                } else {
                    int location[] = new int[2];
                    okActivityCardImageContentImage3.getLocationOnScreen(location);

                    Bundle mBundle = new Bundle();
                    mBundle.putInt("left", location[0]);
                    mBundle.putInt("top", location[1]);
                    mBundle.putInt("height", okActivityCardImageContentImage3.getHeight());
                    mBundle.putInt("width", okActivityCardImageContentImage3.getWidth());

                    mBundle.putString("url", url);

                    startUserActivity(mBundle, OKDragPhotoActivity.class);
                    overridePendingTransition(0, 0);
                }
            }
        });

        okActivityCardImageContentImage4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = (String) okActivityCardImageContentImage4.getTag(R.id.image_tag);
                if (TextUtils.isEmpty(url)) return;
                if (OKFileUtil.isVideoUrl(url)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("URL", url);
                    bundle.putString("TITLE", mCardBean.getTitleText() + "发表的视频");
                    startUserActivity(bundle, OKVideoActivity.class);
                } else {
                    int location[] = new int[2];
                    okActivityCardImageContentImage4.getLocationOnScreen(location);

                    Bundle mBundle = new Bundle();
                    mBundle.putInt("left", location[0]);
                    mBundle.putInt("top", location[1]);
                    mBundle.putInt("height", okActivityCardImageContentImage4.getHeight());
                    mBundle.putInt("width", okActivityCardImageContentImage4.getWidth());

                    mBundle.putString("url", url);

                    startUserActivity(mBundle, OKDragPhotoActivity.class);
                    overridePendingTransition(0, 0);
                }
            }
        });

        okActivityCardImageContentImage5.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = (String) okActivityCardImageContentImage5.getTag(R.id.image_tag);
                if (TextUtils.isEmpty(url)) return;
                if (OKFileUtil.isVideoUrl(url)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("URL", url);
                    bundle.putString("TITLE", mCardBean.getTitleText() + "发表的视频");
                    startUserActivity(bundle, OKVideoActivity.class);
                } else {
                    int location[] = new int[2];
                    okActivityCardImageContentImage5.getLocationOnScreen(location);

                    Bundle mBundle = new Bundle();
                    mBundle.putInt("left", location[0]);
                    mBundle.putInt("top", location[1]);
                    mBundle.putInt("height", okActivityCardImageContentImage5.getHeight());
                    mBundle.putInt("width", okActivityCardImageContentImage5.getWidth());

                    mBundle.putString("url", url);

                    startUserActivity(mBundle, OKDragPhotoActivity.class);
                    overridePendingTransition(0, 0);
                }
            }
        });
    }

    private void addCardBrowsing() {

        OKManagerCardApi.Params params = new OKManagerCardApi.Params();
        params.setUsername(USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
        params.setCardId(mCardBean.getCardId());
        params.setType(OKManagerCardApi.Params.TYPE_BROWSING);

        if (okManagerCardApi != null) {
            okManagerCardApi.cancelTask();
        }
        okManagerCardApi = new OKManagerCardApi(OKCardTPActivity.this);
        okManagerCardApi.requestManagerCard(params, OKCardTPActivity.this);

        mCardBean.setRead(true);
        mCardBean.setReadTime(OKConstant.getNowDateByLong());
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
    }

    @Override
    public void managerCardApiComplete(OKServiceResult<Object> serviceResult, String type, int pos) {
        if (OKManagerCardApi.Params.TYPE_BIND_CHECK.equals(type)) {
            if (serviceResult == null || !serviceResult.isSuccess()) return;

            mCardBindBean = new Gson().fromJson((String) serviceResult.getData(), OKCardRelatedBean.class);

            if (mCardBindBean == null) return;

            if (mCardBindBean.isCardRemove()) {
                MESSAGETopZanImag.setEnabled(false);
                MESSAGETopShouchangImag.setEnabled(false);
                MESSAGETopPinglunImag.setEnabled(false);
                mToolbarTitle.setText("该卡片已被用户删除");
                showSnackBar(MESSAGETopCardView, "该卡片已被用户删除", "");
            }

            if (mCardBindBean.isAttention()) {
                MESSAGETopGuanzhuBut.setText("已关注");
                MESSAGETopGuanzhuBut.setEnabled(false);
            }

            if (mCardBindBean.isWatch()) {
                MESSAGETopShouchangText.setTextColor(getResources().getColor(R.color.fenhon));
            }
            if (mCardBindBean.isPraise()) {
                MESSAGETopZanText.setTextColor(getResources().getColor(R.color.fenhon));
            }
            if (TextUtils.isEmpty(mCardBindBean.getTag())) {
                MESSAGETopQianminText.setText("这个人很懒 , 什么都没有留下!");
            } else {
                MESSAGETopQianminText.setText(mCardBindBean.getTag());
            }
            MESSAGETopZanText.setText("" + mCardBindBean.getPraiseCount());
            MESSAGETopShouchangText.setText("" + mCardBindBean.getWatchCount());
            MESSAGETopPinglunText.setText("" + mCardBindBean.getCommentCount());

        } else if (OKManagerCardApi.Params.TYPE_WATCH.equals(type)) {

            if (serviceResult == null || !serviceResult.isSuccess()) {
                showSnackBar(MESSAGETopCardView, "操作失败,请重试", "");
                return;
            }

            if (mCardBindBean == null) {
                mCardBindBean = new OKCardRelatedBean();
            }
            mCardBindBean.setWatch(true);

            int count = mCardBean.getWatchCount() + 1;
            MESSAGETopShouchangText.setText("" + count);
            MESSAGETopShouchangText.setTextColor(getResources().getColor(R.color.fenhon));

        } else if (OKManagerCardApi.Params.TYPE_PRAISE.equals(type)) {

            if (serviceResult == null || !serviceResult.isSuccess()) {
                showSnackBar(MESSAGETopCardView, "操作失败,请重试", "");
                return;
            }

            if (mCardBindBean == null) {
                mCardBindBean = new OKCardRelatedBean();
            }
            mCardBindBean.setPraise(true);

            int count = mCardBean.getPraiseCount() + 1;
            MESSAGETopZanText.setText("" + count);
            MESSAGETopZanText.setTextColor(getResources().getColor(R.color.fenhon));

        } else if (OKManagerCardApi.Params.TYPE_BROWSING.equals(type)) {
            if (serviceResult == null || !serviceResult.isSuccess()) {
                showSnackBar(MESSAGETopCardView, "PutBrowsingFailure", "");
            }
        }
    }

    @Override
    public void managerUserApiComplete(OKServiceResult<Object> serviceResult, String type, int pos) {
        if (OKManagerUserApi.Params.TYPE_ADD_ATTENTION.equals(type)) {

            if (serviceResult == null || !serviceResult.isSuccess()) {
                showSnackBar(MESSAGETopCardView, "操作失败,请重试", "");
                return;
            }

            if (mCardBindBean == null) {
                mCardBindBean = new OKCardRelatedBean();
            }
            mCardBindBean.setAttention(true);

            MESSAGETopGuanzhuBut.setText("已关注");
            MESSAGETopGuanzhuBut.setEnabled(false);
        }
    }
}
