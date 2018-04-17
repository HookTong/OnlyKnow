package com.onlyknow.app.ui.fragement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.AppBarLayout.OnOffsetChangedListener;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onlyknow.app.OKConstant;
import com.onlyknow.app.R;
import com.onlyknow.app.api.app.OKLoadCarouselAdApi;
import com.onlyknow.app.api.card.OKLoadNearCardApi;
import com.onlyknow.app.api.user.OKManagerUserApi;
import com.onlyknow.app.database.bean.OKCardBean;
import com.onlyknow.app.database.bean.OKCarouselAdBean;
import com.onlyknow.app.database.bean.OKUserInfoBean;
import com.onlyknow.app.ui.OKBaseFragment;
import com.onlyknow.app.ui.activity.OKCardTPActivity;
import com.onlyknow.app.ui.activity.OKCardTWActivity;
import com.onlyknow.app.ui.activity.OKCardWZActivity;
import com.onlyknow.app.ui.activity.OKHomePageActivity;
import com.onlyknow.app.ui.activity.OKSearchActivity;
import com.onlyknow.app.ui.activity.OKSettingActivity;
import com.onlyknow.app.ui.view.OKKenBurnsView;
import com.onlyknow.app.ui.view.OKRecyclerView;
import com.onlyknow.app.utils.OKDateUtil;
import com.onlyknow.app.utils.OKLogUtil;
import com.onlyknow.app.utils.OKNetUtil;
import com.scwang.smartrefresh.header.TaurusHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OKNearScreen extends OKBaseFragment implements OnOffsetChangedListener, OnRefreshListener, OnLoadMoreListener,
        NavigationView.OnNavigationItemSelectedListener, OKLoadNearCardApi.onCallBack, OKLoadCarouselAdApi.onCallBack {
    private AppBarLayout appBarLayout;
    private FloatingActionButton fabReGet;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private OKKenBurnsView mHeaderPicture;
    private RefreshLayout mRefreshLayout;
    private OKRecyclerView mRecyclerView;
    private DrawerLayout mDrawerLayout;
    private BarToggle mBarToggle;
    private NavigationView mNavigationView;
    private CardViewAdapter mCardViewAdapter;

    private OKLoadNearCardApi mOKLoadNearCardApi;
    private List<OKCardBean> mCardBeanList = new ArrayList<>();

    private OKLoadCarouselAdApi carouselAdApi;

    private long locationInterval = 0;

    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.ok_fragment_near, container, false);
            initUserInfoSharedPreferences();
            initWeatherSharedPreferences();

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
    }

    @Override
    public void onPause() {
        super.onPause();

        mRefreshLayout.finishRefresh();

        mRefreshLayout.finishLoadMore();

        appBarLayout.removeOnOffsetChangedListener(this);

        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mOKLoadNearCardApi != null) {
            mOKLoadNearCardApi.cancelTask();
        }

        if (carouselAdApi != null) {
            carouselAdApi.cancelTask();
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

        appBarLayout = (AppBarLayout) rootView.findViewById(R.id.NEAR_AppBar);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) rootView.findViewById(R.id.NEAR_Toolbar_Layout);
        fabReGet = (FloatingActionButton) rootView.findViewById(R.id.NEAR_fabtop);
        mHeaderPicture = (OKKenBurnsView) rootView.findViewById(R.id.NEAR_header_picture_imag);
        mRecyclerView = (OKRecyclerView) rootView.findViewById(R.id.ok_content_collapsing_RecyclerView);
        mRefreshLayout = (RefreshLayout) rootView.findViewById(R.id.ok_content_collapsing_refresh);
        mDrawerLayout = (DrawerLayout) rootView.findViewById(R.id.ok_fragment_near_drawerLayout);
        mNavigationView = (NavigationView) rootView.findViewById(R.id.ok_fragment_near_NavigationView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRefreshLayout.setRefreshHeader(new TaurusHeader(getActivity()));
        mRefreshLayout.setRefreshFooter(new BallPulseFooter(getActivity()).setSpinnerStyle(SpinnerStyle.Scale));
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setOnLoadMoreListener(this);
        mNavigationView.setNavigationItemSelectedListener(this);
        mBarToggle = new BarToggle(getActivity(), mDrawerLayout, R.drawable.ok_toolbar_menu, R.drawable.ok_toolbar_back);
        mBarToggle.syncState();
        mDrawerLayout.addDrawerListener(mBarToggle);
        bindNavigationHeadView(mNavigationView.getHeaderView(0));
    }

    private void init() {
        mCollapsingToolbarLayout.setTitle("世界就在身边");
        mCollapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);

        OKLoadCarouselAdApi.Params params = new OKLoadCarouselAdApi.Params();
        params.setType(OKLoadCarouselAdApi.Params.TYPE_NEW);
        if (carouselAdApi != null) {
            carouselAdApi.cancelTask();
        }
        carouselAdApi = new OKLoadCarouselAdApi(getActivity());
        carouselAdApi.requestCarouselAd(params, this);

        mHeaderPicture.setUrl(this.getActivity(), OKConstant.getCarouselImages());

        mCardViewAdapter = new CardViewAdapter(getActivity(), mCardBeanList);
        mRecyclerView.setAdapter(mCardViewAdapter);

        fabReGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long nowTime = new Date().getTime();
                if (nowTime - locationInterval > 10000) {
                    sendUserBroadcast(OKConstant.ACTION_RESET_LOCATION, null);
                    locationInterval = nowTime;
                    showSnackBar(mRecyclerView, "重新定位我的位置", "");
                } else {
                    showSnackBar(mRecyclerView, "您定位过于频繁,请稍后再试!", "");
                }
            }
        });

        mToolbarSearch.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt(INTENT_KEY_INTERFACE_TYPE, INTERFACE_NEAR);
                startUserActivity(bundle, OKSearchActivity.class);
            }
        });

        mToolbarMenu.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showMenu(getActivity());
            }
        });

        mRecyclerView.setEmptyView(initCollapsingEmptyView(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mRefreshLayout.autoRefresh();
            }
        }));

        mRefreshLayout.autoRefresh();
    }

    private int page = 0;
    private int size = 30;

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        if (OKNetUtil.isNet(getActivity())) {
            OKLoadNearCardApi.Params params = new OKLoadNearCardApi.Params();
            params.setUsername(USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, "anonymous"));
            params.setPage(page + 1);
            params.setSize(size);
            params.setLongitude(USER_INFO_SP.getFloat(OKManagerUserApi.Params.KEY_LONGITUDE, -1));
            params.setLatitude(USER_INFO_SP.getFloat(OKManagerUserApi.Params.KEY_LATITUDE, -1));

            if (mOKLoadNearCardApi == null) {
                mOKLoadNearCardApi = new OKLoadNearCardApi(getActivity());
            }
            mOKLoadNearCardApi.requestNearCard(params, this);
        } else {
            mRefreshLayout.finishLoadMore(1500);
            showSnackBar(rootView, "没有网络连接!", "");
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        if (OKNetUtil.isNet(getActivity())) {
            OKLoadNearCardApi.Params params = new OKLoadNearCardApi.Params();
            params.setUsername(USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, "anonymous"));
            params.setPage(1);
            params.setSize(size);
            params.setLongitude(USER_INFO_SP.getFloat(OKManagerUserApi.Params.KEY_LONGITUDE, -1));
            params.setLatitude(USER_INFO_SP.getFloat(OKManagerUserApi.Params.KEY_LATITUDE, -1));

            if (mOKLoadNearCardApi == null) {
                mOKLoadNearCardApi = new OKLoadNearCardApi(getActivity());
            }
            mOKLoadNearCardApi.requestNearCard(params, this);
        } else {
            mRefreshLayout.finishLoadMore(1500);
            showSnackBar(rootView, "没有网络连接!", "");
        }
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
    public void nearApiComplete(List<OKCardBean> list) {
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
    public void carouselAdApiComplete(OKCarouselAdBean bean) {
        if (bean == null) return;

        mHeaderPicture.setUrl(this.getActivity(), bean.getCarouselImage());
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
                        bundle.putInt(INTENT_KEY_INTERFACE_TYPE, INTERFACE_NEAR);
                        bundle.putSerializable(OKCardTWActivity.KEY_INTENT_IMAGE_AND_TEXT_CARD, okCardBean);
                        startUserActivity(bundle, OKCardTWActivity.class);
                    } else if (okCardBean.getCardType().equals(CARD_TYPE_TP)) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(INTENT_KEY_INTERFACE_TYPE, INTERFACE_NEAR);
                        bundle.putSerializable(OKCardTPActivity.KEY_INTENT_IMAGE_CARD, okCardBean);
                        startUserActivity(bundle, OKCardTPActivity.class);
                    } else if (okCardBean.getCardType().equals(CARD_TYPE_WZ)) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(INTENT_KEY_INTERFACE_TYPE, INTERFACE_NEAR);
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
