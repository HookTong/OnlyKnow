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
import android.text.TextUtils;
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
import com.onlyknow.app.api.OKLoadNearApi;
import com.onlyknow.app.database.bean.OKCardBean;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OKNearScreen extends OKBaseFragment implements OnOffsetChangedListener, OnRefreshListener, OnLoadMoreListener, NavigationView.OnNavigationItemSelectedListener, OKLoadNearApi.onCallBack {
    private AppBarLayout appBarLayout;
    private FloatingActionButton fabReGet;
    private TextView textViewGanMao;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private OKKenBurnsView mHeaderPicture;
    private RefreshLayout mRefreshLayout;
    private OKRecyclerView mRecyclerView;
    private DrawerLayout mDrawerLayout;
    private BarToggle mBarToggle;
    private NavigationView mNavigationView;
    private CardViewAdapter mCardViewAdapter;

    private OKLoadNearApi mOKLoadNearApi;
    private List<OKCardBean> mCardBeanList = new ArrayList<>();

    private View rootView;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            OKLogUtil.print("Near 收到广播 :" + action);
            if (action.equals(OKConstant.ACTION_UPDATE_CAROUSE_AND_AD_IMAGE)) {
                mHeaderPicture.setUrl(getActivity(), OKConstant.getHeadUrls());
                OKConstant.NEAR_HEAD_DATA_CHANGED = false;
            }
        }
    };

    private long locationInterval = 0;

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
        if (OKConstant.NEAR_HEAD_DATA_CHANGED) {
            mHeaderPicture.setUrl(getActivity(), OKConstant.getHeadUrls());
            OKConstant.NEAR_HEAD_DATA_CHANGED = false;
        }
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(OKConstant.ACTION_UPDATE_CAROUSE_AND_AD_IMAGE);
        getActivity().registerReceiver(mBroadcastReceiver, mIntentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOKLoadNearApi != null) {
            mOKLoadNearApi.cancelTask();
        }
        mRefreshLayout.finishRefresh();
        mRefreshLayout.finishLoadMore();
        appBarLayout.removeOnOffsetChangedListener(this);
        mDrawerLayout.closeDrawer(GravityCompat.START);
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
        textViewGanMao = (TextView) rootView.findViewById(R.id.NEAR_toplayout_ganmao_text);
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
        mHeaderPicture.setUrl(this.getActivity(), OKConstant.getHeadUrls());
        bindWeatherView();

        mCardViewAdapter = new CardViewAdapter(getActivity(), mCardBeanList);
        mRecyclerView.setAdapter(mCardViewAdapter);

        fabReGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long nowTime = new Date().getTime();
                if (nowTime - locationInterval > 10000) {
                    sendUserBroadcast(OKConstant.ACTION_RESET_LOCATION, null);
                    locationInterval = nowTime;
                    showSnackbar(mRecyclerView, "重新定位我的位置", "");
                } else {
                    showSnackbar(mRecyclerView, "您定位过于频繁,请稍后再试!", "");
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

    private void bindWeatherView() {
        String strGanMao = WEATHER_SP.getString("GAN_MAO", "");
        if (!TextUtils.isEmpty(strGanMao)) {
            textViewGanMao.setText("天气问候语 : \r\n" + strGanMao);
        } else {
            textViewGanMao.setText(getText(R.string.moRen_Title));
        }
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        if (OKNetUtil.isNet(getActivity())) {
            Map<String, String> map = new HashMap<>();// 请求参数
            map.put("username", USER_INFO_SP.getString("USERNAME", "Anonymous"));
            map.put("longitude", Float.toString(USER_INFO_SP.getFloat("LONGITUDE", -1)));
            map.put("dimension", Float.toString(USER_INFO_SP.getFloat("DIMENSION", -1)));
            map.put("num", OKConstant.NEAR_LOAD_COUNT);
            if (mOKLoadNearApi == null) {
                mOKLoadNearApi = new OKLoadNearApi(getActivity());
            }
            mOKLoadNearApi.requestCardBeanList(map, true, this);
        } else {
            mRefreshLayout.finishLoadMore(1500);
            showSnackbar(rootView, "没有网络连接!", "");
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        if (OKNetUtil.isNet(getActivity())) {
            Map<String, String> map = new HashMap<>();// 请求参数
            map.put("username", USER_INFO_SP.getString("USERNAME", "Anonymous"));
            map.put("longitude", Float.toString(USER_INFO_SP.getFloat("LONGITUDE", -1)));
            map.put("dimension", Float.toString(USER_INFO_SP.getFloat("DIMENSION", -1)));
            map.put("num", OKConstant.NEAR_LOAD_COUNT);
            if (mOKLoadNearApi == null) {
                mOKLoadNearApi = new OKLoadNearApi(getActivity());
            }
            mOKLoadNearApi.requestCardBeanList(map, false, this);
        } else {
            if (mRecyclerView.getAdapter().getItemCount() == 0) {
                mCardBeanList.addAll(OKConstant.getListCache(INTERFACE_NEAR));
                mRecyclerView.getAdapter().notifyDataSetChanged();
            }
            mRefreshLayout.finishRefresh(1500);
            showSnackbar(rootView, "没有网络连接!", "");
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
                mCardBeanList.clear();
                mCardBeanList.addAll(list);
            } else if (mRefreshLayout.getState() == RefreshState.Loading) {
                mCardBeanList.addAll(list);
            }
            OKConstant.putListCache(INTERFACE_NEAR, mCardBeanList);
            mRecyclerView.getAdapter().notifyDataSetChanged();
        }
        if (mRefreshLayout.getState() == RefreshState.Refreshing) {
            mRefreshLayout.finishRefresh();
        } else if (mRefreshLayout.getState() == RefreshState.Loading) {
            mRefreshLayout.finishLoadMore();
        }
    }

    private class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.CardViewHolder> {
        private Context mContext;
        private List<OKCardBean> mBeanList;

        public CardViewAdapter(Context context, List<OKCardBean> list) {
            this.mContext = context;
            this.mBeanList = list;
        }

        private void initViews(final CardViewHolder mCardViewHolder, final OKCardBean okCardBean, final int position) {
            String cardType = okCardBean.getCARD_TYPE();
            // 设置标题控件内容
            GlideRoundApi(mCardViewHolder.mImageViewAvatar, okCardBean.getTITLE_IMAGE_URL(), R.drawable.touxian_placeholder, R.drawable.touxian_placeholder);
            mCardViewHolder.mTextViewTitle.setText(okCardBean.getTITLE_TEXT());
            mCardViewHolder.mTextViewDate.setText(formatTime(okCardBean.getCREATE_DATE()) + " 发表");
            // 设置内容控件内容
            if (cardType.equals(CARD_TYPE_TW)) {
                mCardViewHolder.mLinearLayoutContent.setVisibility(View.VISIBLE);
                mCardViewHolder.mImageViewContentImage.setVisibility(View.VISIBLE);

                GlideApi(mCardViewHolder.mImageViewContentImage, getFirstCardImageUrl(okCardBean), R.drawable.toplayout_imag, R.drawable.toplayout_imag);

                String z = Integer.toString(okCardBean.getZAN_NUM());
                String s = Integer.toString(okCardBean.getSHOUCHAN_NUM());
                String p = Integer.toString(okCardBean.getPINGLUN_NUM());
                mCardViewHolder.mTextViewContentTitle.setText(okCardBean.getCONTENT_TITLE_TEXT());
                mCardViewHolder.mTextViewContent.setText(okCardBean.getCONTENT_TEXT());
                mCardViewHolder.mTextViewContentPraise.setText(z + "赞同; " + s + "收藏; " + p + "评论");
            } else if (cardType.equals(CARD_TYPE_TP)) {
                mCardViewHolder.mLinearLayoutContent.setVisibility(View.GONE);
                mCardViewHolder.mImageViewContentImage.setVisibility(View.VISIBLE);
                GlideApi(mCardViewHolder.mImageViewContentImage, getFirstCardImageUrl(okCardBean), R.drawable.toplayout_imag, R.drawable.toplayout_imag);
            } else if (cardType.equals(CARD_TYPE_WZ)) {
                mCardViewHolder.mLinearLayoutContent.setVisibility(View.VISIBLE);
                mCardViewHolder.mImageViewContentImage.setVisibility(View.GONE);

                String z = Integer.toString(okCardBean.getZAN_NUM());
                String s = Integer.toString(okCardBean.getSHOUCHAN_NUM());
                String p = Integer.toString(okCardBean.getPINGLUN_NUM());

                mCardViewHolder.mTextViewContentTitle.setText(okCardBean.getCONTENT_TITLE_TEXT());
                mCardViewHolder.mTextViewContent.setText(okCardBean.getCONTENT_TEXT());
                mCardViewHolder.mTextViewContentPraise.setText(z + "赞同; " + s + "收藏; " + p + "评论");
            }

            mCardViewHolder.mCardView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (okCardBean.getCARD_TYPE().equals(CARD_TYPE_TW)) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(INTENT_KEY_INTERFACE_TYPE, INTERFACE_NEAR);
                        bundle.putInt(INTENT_KEY_LIST_POSITION, position);
                        bundle.putInt(INTENT_KEY_LIST_CARD_ID, okCardBean.getCARD_ID());
                        startUserActivity(bundle, OKCardTWActivity.class);
                    } else if (okCardBean.getCARD_TYPE().equals(CARD_TYPE_TP)) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(INTENT_KEY_INTERFACE_TYPE, INTERFACE_NEAR);
                        bundle.putInt(INTENT_KEY_LIST_POSITION, position);
                        bundle.putInt(INTENT_KEY_LIST_CARD_ID, okCardBean.getCARD_ID());
                        startUserActivity(bundle, OKCardTPActivity.class);
                    } else if (okCardBean.getCARD_TYPE().equals(CARD_TYPE_WZ)) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(INTENT_KEY_INTERFACE_TYPE, INTERFACE_NEAR);
                        bundle.putInt(INTENT_KEY_LIST_POSITION, position);
                        bundle.putInt(INTENT_KEY_LIST_CARD_ID, okCardBean.getCARD_ID());
                        startUserActivity(bundle, OKCardWZActivity.class);
                    }
                }
            });

            mCardViewHolder.mImageViewDelete.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    mBeanList.remove(position);
                    OKConstant.removeListCache(INTERFACE_NEAR, position);
                    mRecyclerView.getAdapter().notifyDataSetChanged();
                }
            });

            mCardViewHolder.mImageViewAvatar.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // 查看他人主页
                    Bundle bundle = new Bundle();
                    bundle.putString(OKUserInfoBean.KEY_USERNAME, okCardBean.getUSER_NAME());
                    bundle.putString(OKUserInfoBean.KEY_NICKNAME, okCardBean.getTITLE_TEXT());
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
