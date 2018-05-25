package com.onlyknow.app.ui.fragement;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.AppBarLayout.OnOffsetChangedListener;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.onlyknow.app.OKConstant;
import com.onlyknow.app.R;
import com.onlyknow.app.api.app.OKLoadCarouselAdApi;
import com.onlyknow.app.api.app.OKLoadWeatherApi;
import com.onlyknow.app.api.card.OKLoadExploreCardApi;
import com.onlyknow.app.db.bean.OKCardBean;
import com.onlyknow.app.db.bean.OKCarouselAdBean;
import com.onlyknow.app.db.bean.OKUserInfoBean;
import com.onlyknow.app.db.bean.OKWeatherBean;
import com.onlyknow.app.service.OKMainService;
import com.onlyknow.app.ui.OKBaseFragment;
import com.onlyknow.app.ui.activity.OKBrowserActivity;
import com.onlyknow.app.ui.activity.OKCardTPActivity;
import com.onlyknow.app.ui.activity.OKCardTWActivity;
import com.onlyknow.app.ui.activity.OKCardWZActivity;
import com.onlyknow.app.ui.activity.OKGoodsActivity;
import com.onlyknow.app.ui.activity.OKHomePageActivity;
import com.onlyknow.app.ui.activity.OKHotActivity;
import com.onlyknow.app.ui.activity.OKLoginActivity;
import com.onlyknow.app.ui.activity.OKNoticeActivity;
import com.onlyknow.app.ui.activity.OKSearchActivity;
import com.onlyknow.app.ui.activity.OKSettingActivity;
import com.onlyknow.app.ui.activity.OKWeatherActivity;
import com.onlyknow.app.ui.view.OKKenBurnsView;
import com.onlyknow.app.ui.view.OKRecyclerView;
import com.onlyknow.app.utils.OKDateUtil;
import com.onlyknow.app.utils.OKLoadBannerImage;
import com.onlyknow.app.utils.OKNetUtil;
import com.scwang.smartrefresh.header.TaurusHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

public class OKExploreScreen extends OKBaseFragment implements OnOffsetChangedListener, OnRefreshListener, OnLoadMoreListener,
        OKLoadExploreCardApi.onCallBack, OKLoadWeatherApi.onCallBack, OnNavigationItemSelectedListener, OKLoadCarouselAdApi.onCallBack {
    private AppBarLayout appBarLayout;
    private FloatingActionButton fab;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private OKKenBurnsView mHeaderPicture;
    private TextView textViewCityName, textViewDistrict, textViewTianQi, textViewWenDu, textViewWenDuLow, textViewWenDuHig, textViewDate;
    private ImageView imageViewWeatherIcon;// 天气图标
    private Banner mBanner;
    private RefreshLayout mRefreshLayout;
    private OKRecyclerView mRecyclerView;
    private DrawerLayout mDrawerLayout;
    private DrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;
    private CardViewAdapter mCardViewAdapter;

    private OKLoadExploreCardApi mOKLoadExploreCardApi;
    private List<OKCardBean> mCardBeanList = new ArrayList<>();

    private OKLoadWeatherApi mWeatherApi;
    private OKWeatherBean mOKWeatherBean;

    private OKLoadCarouselAdApi carouselAdApi;

    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.ok_fragment_explore, container, false);
            initUserBody();
            initWeatherBody();
            initSettingBody();

            findView(rootView);
            init();
            return rootView;
        } else {
            return rootView;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        appBarLayout.addOnOffsetChangedListener(this);

        startBanner();
    }

    @Override
    public void onPause() {
        super.onPause();

        stopBanner();

        mRefreshLayout.finishRefresh();

        mRefreshLayout.finishLoadMore();

        appBarLayout.removeOnOffsetChangedListener(this);

        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mOKLoadExploreCardApi != null) {
            mOKLoadExploreCardApi.cancelTask();
        }

        if (mWeatherApi != null) {
            mWeatherApi.cancelTask();
        }

        if (null != rootView) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (i == 0) {
            mRefreshLayout.setEnableRefresh(true);
            mRefreshLayout.setEnableLoadMore(false);
        } else {
            mRefreshLayout.setEnableRefresh(false);
            mRefreshLayout.setEnableLoadMore(true);
        }
    }

    private void findView(View rootView) {
        super.findCollapsingToolbarView(rootView);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);

        mToolbarMenu.setVisibility(View.VISIBLE);
        mToolbarSearch.setVisibility(View.VISIBLE);

        appBarLayout = (AppBarLayout) rootView.findViewById(R.id.app_bar);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) rootView.findViewById(R.id.toolbar_layout);
        fab = (FloatingActionButton) rootView.findViewById(R.id.home_fabtop);
        mHeaderPicture = (OKKenBurnsView) rootView.findViewById(R.id.main_header_picture_imag);
        textViewCityName = (TextView) rootView.findViewById(R.id.main_toplayout_cityname_text);
        textViewDistrict = (TextView) rootView.findViewById(R.id.main_toplayout_date_text);
        textViewTianQi = (TextView) rootView.findViewById(R.id.main_toplayout_tianqi_text);
        textViewWenDu = (TextView) rootView.findViewById(R.id.main_toplayout_wendu_text);
        textViewWenDuLow = (TextView) rootView.findViewById(R.id.main_toplayout_wenduLow_text);
        textViewWenDuHig = (TextView) rootView.findViewById(R.id.main_toplayout_wenduHig_text);
        textViewDate = (TextView) rootView.findViewById(R.id.main_toplayout_district_Textview);
        imageViewWeatherIcon = (ImageView) rootView.findViewById(R.id.main_toplayout_tianqiTb_Imagview);
        mRefreshLayout = (RefreshLayout) rootView.findViewById(R.id.ok_content_collapsing_refresh);
        mRecyclerView = (OKRecyclerView) rootView.findViewById(R.id.ok_content_collapsing_RecyclerView);
        mDrawerLayout = (DrawerLayout) rootView.findViewById(R.id.ok_fragment_explore_drawerLayout);
        mNavigationView = (NavigationView) rootView.findViewById(R.id.ok_fragment_explore_NavigationView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addHeaderView(initHeaderView());
        mRefreshLayout.setRefreshHeader(new TaurusHeader(getActivity()));//设置Header为Material样式
        mRefreshLayout.setRefreshFooter(new BallPulseFooter(getActivity()).setSpinnerStyle(SpinnerStyle.Scale));//设置Footer为球脉冲
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setOnLoadMoreListener(this);
        mNavigationView.setNavigationItemSelectedListener(this);
        mDrawerToggle = new DrawerToggle(mDrawerLayout);
        mDrawerToggle.syncState();
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        bindNavigationHeadView(mNavigationView.getHeaderView(0));
    }

    private void init() {
        mCollapsingToolbarLayout.setTitle("探索唯知世界");
        mCollapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);

        mHeaderPicture.setCarouselByUrl(getActivity(), OKConstant.getCarouselImages());

        bindWeatherView();

        mCardViewAdapter = new CardViewAdapter(getActivity(), mCardBeanList);
        mRecyclerView.setAdapter(mCardViewAdapter);

        // 请求轮播广告
        OKLoadCarouselAdApi.Params params = new OKLoadCarouselAdApi.Params();
        params.setType(OKLoadCarouselAdApi.Params.TYPE_NEW);
        if (carouselAdApi != null) {
            carouselAdApi.cancelTask();
        }
        carouselAdApi = new OKLoadCarouselAdApi(getActivity());
        carouselAdApi.requestCarouselAd(params, this);

        // 请求天气信息
        if (mWeatherApi != null) {
            mWeatherApi.cancelTask();
        }
        OKLoadWeatherApi.Params weatherParams = new OKLoadWeatherApi.Params();
        weatherParams.setCityId(USER_BODY.getString("CITY_ID", ""));
        weatherParams.setCityName(USER_BODY.getString("CITY_NAME", ""));
        weatherParams.setDistrict(USER_BODY.getString("DISTRICT", ""));
        mWeatherApi = new OKLoadWeatherApi(getActivity());
        mWeatherApi.requestWeather(weatherParams, this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (USER_BODY.getBoolean("STATE", false)) {
                    if (OKMainService.isEMLogIn && OKNetUtil.isNet(getActivity())) {
                        startUserActivity(null, OKNoticeActivity.class);
                    } else {
                        showSnackBar(view, "您未登录聊天服务器,请重新登录账号!", "");
                    }
                } else {
                    startUserActivity(null, OKLoginActivity.class);
                }
            }
        });

        mToolbarSearch.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt(INTENT_KEY_INTERFACE_TYPE, INTERFACE_EXPLORE);
                startUserActivity(bundle, OKSearchActivity.class);
            }
        });

        mToolbarMenu.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showMainMenu();
            }
        });

        imageViewWeatherIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWeatherApi != null) {
                    mWeatherApi.cancelTask();
                }

                OKLoadWeatherApi.Params weatherParams = new OKLoadWeatherApi.Params();
                weatherParams.setCityId(USER_BODY.getString("CITY_ID", ""));
                weatherParams.setCityName(USER_BODY.getString("CITY_NAME", ""));
                weatherParams.setDistrict(USER_BODY.getString("DISTRICT", ""));

                mWeatherApi = new OKLoadWeatherApi(getActivity());
                mWeatherApi.requestWeather(weatherParams, OKExploreScreen.this);

                // 请求轮播广告
                OKLoadCarouselAdApi.Params params = new OKLoadCarouselAdApi.Params();
                params.setType(OKLoadCarouselAdApi.Params.TYPE_NEW);
                if (carouselAdApi != null) {
                    carouselAdApi.cancelTask();
                }
                carouselAdApi = new OKLoadCarouselAdApi(getActivity());
                carouselAdApi.requestCarouselAd(params, OKExploreScreen.this);

                showSnackBar(v, "重新获取天气与轮播图片!", "");
            }
        });

        mHeaderPicture.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOKWeatherBean != null) {
                    Bundle mBundle = new Bundle();
                    mBundle.putString("JSON_DATA", new Gson().toJson(mOKWeatherBean));
                    startUserActivity(mBundle, OKWeatherActivity.class);
                }
            }
        });

        mRefreshLayout.autoRefresh();
    }

    private View initHeaderView() {
        View mHeaderView = LayoutInflater.from(getActivity()).inflate(R.layout.ok_main_header, null);
        mBanner = (Banner) mHeaderView.findViewById(R.id.MAIN_top_header_guangaotp_banner);
        ImageView imageViewRM = (ImageView) mHeaderView.findViewById(R.id.MAIN_top_header_remen_imag);
        ImageView imageViewSC = (ImageView) mHeaderView.findViewById(R.id.MAIN_top_header_shangcheng_imag);
        ImageView imageViewSD = (ImageView) mHeaderView.findViewById(R.id.MAIN_top_header_shudian_imag);
        ImageView imageViewFX = (ImageView) mHeaderView.findViewById(R.id.MAIN_top_header_faxian_imag);

        mBanner.setImageLoader(new OKLoadBannerImage(true));
        mBanner.setBannerAnimation(Transformer.DepthPage);
        mBanner.isAutoPlay(true);
        mBanner.setDelayTime(5000);
        mBanner.setIndicatorGravity(BannerConfig.CENTER);
        mBanner.setImages(OKConstant.getAdImages());
        startBanner();

        mBanner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                OKCarouselAdBean.ADImage adImage = OKConstant.getAdImages().get(position);
                if (!TextUtils.isEmpty(adImage.getLink())) {
                    Bundle bundle = new Bundle();
                    bundle.putString("WEB_LINK", adImage.getLink());
                    startUserActivity(bundle, OKBrowserActivity.class);
                } else {
                    showSnackBar(rootView, "没有发现链接", "");
                }
            }
        });

        imageViewRM.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startUserActivity(null, OKHotActivity.class);
            }
        });

        imageViewSC.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startUserActivity(null, OKGoodsActivity.class);
            }
        });

        imageViewSD.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showSnackBar(rootView, "查看书店,唯知书店暂未开放!", "");
            }
        });

        imageViewFX.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("WEB_LINK", OKConstant.EXPLORE_FIND_URL);
                startUserActivity(bundle, OKBrowserActivity.class);
            }
        });
        return mHeaderView;
    }

    private void startBanner() {
        if (mBanner != null) {
            mBanner.start();
        }
    }

    private void stopBanner() {
        if (mBanner != null) {
            mBanner.stopAutoPlay();
        }
    }

    private void bindWeatherView() {
        String Type = WEATHER_BODY.getString("WEATHER_TYPE", "N/A");// 天气类型;如雷阵雨

        textViewCityName.setText(WEATHER_BODY.getString("CITY_NAME", "未获取到城市"));
        textViewDate.setText(WEATHER_BODY.getString("WEATHER_DATE_WEEK", "N/A"));
        textViewTianQi.setText(Type);
        textViewWenDu.setText(WEATHER_BODY.getString("TEMPERATURE", "N/A"));
        textViewWenDuLow.setText(WEATHER_BODY.getString("TEMPERATURE_LOW", "N/A"));
        textViewWenDuHig.setText(WEATHER_BODY.getString("TEMPERATURE_HIG", "N/A"));
        textViewDistrict.setText(WEATHER_BODY.getString("DISTRICT", "N/A"));

        int resId;
        if (Type.indexOf("晴") != -1) {
            resId = R.drawable.tianqi_qing;
        } else if (Type.indexOf("阴") != -1) {
            resId = R.drawable.tianqi_ying;
        } else if (Type.indexOf("多云") != -1) {
            resId = R.drawable.tianqi_duoyun;
        } else if (Type.indexOf("大雨") != -1) {
            resId = R.drawable.tianqi_dayu;
        } else if (Type.indexOf("小雨") != -1) {
            resId = R.drawable.tianqi_xiaoyu;
        } else if (Type.indexOf("中雨") != -1) {
            resId = R.drawable.tianqi_zhonyu;
        } else if (Type.indexOf("雷阵雨") != -1) {
            resId = R.drawable.tianqi_leizhenyu;
        } else if (Type.indexOf("大雪") != -1) {
            resId = R.drawable.tianqi_daxue;
        } else if (Type.indexOf("小雪") != -1) {
            resId = R.drawable.tianqi_xiaoxue;
        } else if (Type.indexOf("暴雨") != -1) {
            resId = R.drawable.tianqi_baoyu;
        } else if (Type.indexOf("阵雨") != -1) {
            resId = R.drawable.tianqi_zhenyu;
        } else if (Type.indexOf("雨夹雪") != -1) {
            resId = R.drawable.tianqi_yujaxue;
        } else if (Type.indexOf("沙尘暴") != -1) {
            resId = R.drawable.tianqi_shachenbao;
        } else if (Type.indexOf("浮尘") != -1) {
            resId = R.drawable.tianqi_fucheng;
        } else if (Type.indexOf("雾霾") != -1) {
            resId = R.drawable.tianqi_wumai;
        } else if (Type.indexOf("雾") != -1) {
            resId = R.drawable.tianqi_wu;
        } else if (Type.indexOf("台风") != -1) {
            resId = R.drawable.tianqi_taifeng;
        } else if (Type.indexOf("龙卷风") != -1) {
            resId = R.drawable.tianqi_lonjuanfeng;
        } else if (Type.indexOf("大风") != -1) {
            resId = R.drawable.tianqi_dafeng;
        } else if (Type.indexOf("风") != -1) {
            resId = R.drawable.tianqi_feng;
        } else {
            resId = R.drawable.tianqi_other;
        }

        GlideApi(imageViewWeatherIcon, resId, R.drawable.tianqi_other, R.drawable.tianqi_other);
        WEATHER_BODY.edit().putInt("WEATHER_ICON_ID", resId).commit();
    }

    private int page = 0;
    private int size = 30;

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        OKLoadExploreCardApi.Params params = new OKLoadExploreCardApi.Params();

        params.setPage(page + 1);
        params.setSize(size);

        if (mOKLoadExploreCardApi == null) {
            mOKLoadExploreCardApi = new OKLoadExploreCardApi(getActivity());
        }
        mOKLoadExploreCardApi.requestExploreCard(params, this);
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        OKLoadExploreCardApi.Params params = new OKLoadExploreCardApi.Params();


        params.setPage(1);
        params.setSize(size);

        if (mOKLoadExploreCardApi == null) {
            mOKLoadExploreCardApi = new OKLoadExploreCardApi(getActivity());
        }
        mOKLoadExploreCardApi.requestExploreCard(params, this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.ok_menu_drawer_FuLi:
                startGanKioActivity(GAN_KIO_TYPE_FL);
                break;
            case R.id.ok_menu_drawer_Video:
                startGanKioActivity(GAN_KIO_TYPE_VIDEO);
                break;
            case R.id.ok_menu_drawer_ExtensionRes:
                startGanKioActivity(GAN_KIO_TYPE_RES);
                break;
            case R.id.ok_menu_drawer_Android:
                startGanKioActivity(GAN_KIO_TYPE_ANDROID);
                break;
            case R.id.ok_menu_drawer_IOS:
                startGanKioActivity(GAN_KIO_TYPE_IOS);
                break;
            case R.id.ok_menu_drawer_QianDuan:
                startGanKioActivity(GAN_KIO_TYPE_H5);
                break;
            case R.id.ok_menu_drawer_Setting:
                startUserActivity(null, OKSettingActivity.class);
                break;
            default:
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void loadWeatherComplete(OKWeatherBean weatherBean) {
        if (weatherBean == null) {
            showSnackBar(rootView, "天气获取失败", "ErrorCode: " + OKConstant.WEATHER_BEAN_ERROR);
            return;
        }
        mOKWeatherBean = weatherBean;
        saveWeatherInfo(mOKWeatherBean);
        bindWeatherView();
        bindNavigationHeadView(mNavigationView.getHeaderView(0));
    }

    @Override
    public void loadExploreComplete(List<OKCardBean> list) {
        if (list != null) {
            if (mRefreshLayout.getState() == RefreshState.Refreshing) {
                page = 1;

                mCardBeanList.clear();
                mCardBeanList.addAll(list);
            } else if (mRefreshLayout.getState() == RefreshState.Loading) {
                page++;

                mCardBeanList.addAll(list);
            }
            mRecyclerView.getAdapter().notifyDataSetChanged();
        }
        if (mRefreshLayout.getState() == RefreshState.Refreshing) {
            mRefreshLayout.finishRefresh();
        } else if (mRefreshLayout.getState() == RefreshState.Loading) {
            mRefreshLayout.finishLoadMore();
        }
    }

    @Override
    public void loadCarouselAdComplete(OKCarouselAdBean bean) {
        if (bean == null) return;

        mHeaderPicture.setCarouselByUrl(getActivity(), bean.getCarouselImages());
        stopBanner();
        mBanner.setImages(bean.getAdImages());
        startBanner();
    }

    private class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.CardViewHolder> {
        private Context mContext;
        private List<OKCardBean> mBeanList;

        public CardViewAdapter(Context context, List<OKCardBean> list) {
            this.mContext = context;
            this.mBeanList = list;
        }

        private void initViews(final CardViewHolder mCardViewHolder, final OKCardBean okCardBean, final int position) {
            String cardType = okCardBean.getCardType();
            // 设置标题控件内容
            GlideRoundApi(mCardViewHolder.mImageViewAvatar, okCardBean.getTitleImageUrl(), R.drawable.touxian_placeholder, R.drawable.touxian_placeholder);
            mCardViewHolder.mTextViewTitle.setText(okCardBean.getTitleText());
            mCardViewHolder.mTextViewDate.setText(OKDateUtil.formatTime(okCardBean.getCreateDate()) + " 发表");
            // 设置内容控件内容
            if (cardType.equals(CARD_TYPE_TW)) {
                mCardViewHolder.mLinearLayoutContent.setVisibility(View.VISIBLE);
                mCardViewHolder.mImageViewContentImage.setVisibility(View.VISIBLE);

                GlideApi(mCardViewHolder.mImageViewContentImage, okCardBean.getFirstCardImage(), R.drawable.toplayout_imag, R.drawable.toplayout_imag);

                mCardViewHolder.mTextViewContentTitle.setText(okCardBean.getContentTitleText());
                mCardViewHolder.mTextViewContent.setText(okCardBean.getContentText());

                String z = Integer.toString(okCardBean.getPraiseCount());
                String s = Integer.toString(okCardBean.getWatchCount());
                String p = Integer.toString(okCardBean.getCommentCount());
                mCardViewHolder.mTextViewContentPraise.setText(z + "赞同; " + s + "收藏; " + p + "评论");
            } else if (cardType.equals(CARD_TYPE_TP)) {
                mCardViewHolder.mLinearLayoutContent.setVisibility(View.GONE);
                mCardViewHolder.mImageViewContentImage.setVisibility(View.VISIBLE);
                GlideApi(mCardViewHolder.mImageViewContentImage, okCardBean.getFirstCardImage(), R.drawable.toplayout_imag, R.drawable.toplayout_imag);
            } else if (cardType.equals(CARD_TYPE_WZ)) {
                mCardViewHolder.mLinearLayoutContent.setVisibility(View.VISIBLE);
                mCardViewHolder.mImageViewContentImage.setVisibility(View.GONE);

                mCardViewHolder.mTextViewContentTitle.setText(okCardBean.getContentTitleText());
                mCardViewHolder.mTextViewContent.setText(okCardBean.getContentText());

                String z = Integer.toString(okCardBean.getPraiseCount());
                String s = Integer.toString(okCardBean.getWatchCount());
                String p = Integer.toString(okCardBean.getCommentCount());
                mCardViewHolder.mTextViewContentPraise.setText(z + "赞同; " + s + "收藏; " + p + "评论");
            }

            mCardViewHolder.mCardView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (okCardBean.getCardType().equals(CARD_TYPE_TW)) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(INTENT_KEY_INTERFACE_TYPE, INTERFACE_EXPLORE);
                        bundle.putSerializable(OKCardTWActivity.KEY_INTENT_IMAGE_AND_TEXT_CARD, okCardBean);
                        startUserActivity(bundle, OKCardTWActivity.class);
                    } else if (okCardBean.getCardType().equals(CARD_TYPE_TP)) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(INTENT_KEY_INTERFACE_TYPE, INTERFACE_EXPLORE);
                        bundle.putSerializable(OKCardTPActivity.KEY_INTENT_IMAGE_CARD, okCardBean);
                        startUserActivity(bundle, OKCardTPActivity.class);
                    } else if (okCardBean.getCardType().equals(CARD_TYPE_WZ)) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(INTENT_KEY_INTERFACE_TYPE, INTERFACE_EXPLORE);
                        bundle.putSerializable(OKCardWZActivity.KEY_INTENT_TEXT_CARD, okCardBean);
                        startUserActivity(bundle, OKCardWZActivity.class);
                    }
                }
            });

            mCardViewHolder.mImageViewDelete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBeanList.remove(position);
                    mRecyclerView.getAdapter().notifyDataSetChanged();
                }
            });

            mCardViewHolder.mImageViewAvatar.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // 查看他人主页
                    Bundle bundle = new Bundle();
                    bundle.putString(OKUserInfoBean.KEY_USERNAME, okCardBean.getUserName());
                    bundle.putString(OKUserInfoBean.KEY_NICKNAME, okCardBean.getTitleText());
                    startUserActivity(bundle, OKHomePageActivity.class);
                }
            });
        }

        @Override
        public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.ok_item_card, parent, false);
            return new CardViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CardViewHolder holder, int position) {
            initViews(holder, mBeanList.get(position), position);
        }

        @Override
        public int getItemCount() {
            return mBeanList.size();
        }

        class CardViewHolder extends RecyclerView.ViewHolder {
            public CardView mCardView;
            // 标题信息
            public ImageView mImageViewAvatar, mImageViewDelete;
            public TextView mTextViewTitle, mTextViewDate;
            // 内容信息
            public ImageView mImageViewContentImage;
            public TextView mTextViewContent, mTextViewContentTitle, mTextViewContentPraise;
            public LinearLayout mLinearLayoutContent;

            public CardViewHolder(View itemView) {
                super(itemView);
                mCardView = itemView.findViewById(R.id.article_card);
                mImageViewAvatar = itemView.findViewById(R.id.yuedu_touxian_imag);
                mImageViewDelete = itemView.findViewById(R.id.yuedu_shanchu_imag);
                mTextViewTitle = itemView.findViewById(R.id.yuedu_yonhumin_text);
                mTextViewDate = itemView.findViewById(R.id.yuedu_date_text);
                mImageViewContentImage = itemView.findViewById(R.id.yuedu_neiron_imag);
                mTextViewContent = itemView.findViewById(R.id.yuedu_neiron_text);
                mTextViewContentTitle = itemView.findViewById(R.id.yuedu_neiron_biaoti_text);
                mTextViewContentPraise = itemView.findViewById(R.id.yuedu_zan_text);
                mLinearLayoutContent = itemView.findViewById(R.id.yuedu_neiron_layout);
            }
        }
    }
}
