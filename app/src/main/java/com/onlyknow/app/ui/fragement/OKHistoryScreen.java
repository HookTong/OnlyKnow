package com.onlyknow.app.ui.fragement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.onlyknow.app.OKConstant;
import com.onlyknow.app.R;
import com.onlyknow.app.api.app.OKLoadCarouselAdApi;
import com.onlyknow.app.api.card.OKLoadHistoryCardApi;
import com.onlyknow.app.database.OKDatabaseHelper;
import com.onlyknow.app.database.bean.OKCardBean;
import com.onlyknow.app.database.bean.OKCarouselAdBean;
import com.onlyknow.app.ui.OKBaseFragment;
import com.onlyknow.app.ui.activity.OKCardTPActivity;
import com.onlyknow.app.ui.activity.OKCardTWActivity;
import com.onlyknow.app.ui.activity.OKCardWZActivity;
import com.onlyknow.app.ui.activity.OKSearchActivity;
import com.onlyknow.app.ui.activity.OKSettingActivity;
import com.onlyknow.app.ui.view.OKKenBurnsView;
import com.onlyknow.app.ui.view.OKRecyclerView;
import com.onlyknow.app.utils.OKDateUtil;
import com.onlyknow.app.utils.OKLogUtil;
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

public class OKHistoryScreen extends OKBaseFragment implements OnOffsetChangedListener, OnRefreshListener, OnLoadMoreListener,
        NavigationView.OnNavigationItemSelectedListener, OKLoadHistoryCardApi.onCallBack, OKLoadCarouselAdApi.onCallBack {
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private OKKenBurnsView mHeaderPicture;
    private RefreshLayout mRefreshLayout;
    private OKRecyclerView mRecyclerView;
    private FloatingActionButton floatingActionButton;
    private DrawerLayout mDrawerLayout;
    private BarToggle mBarToggle;
    private NavigationView mNavigationView;
    private EntryViewAdapter mEntryViewAdapter;

    private OKLoadHistoryCardApi mOKLoadHistoryCardApi;
    private List<OKCardBean> mCardBeanList = new ArrayList<>();

    private OKLoadCarouselAdApi carouselAdApi;

    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.ok_fragment_history, container, false);
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

        if (mOKLoadHistoryCardApi != null) {
            mOKLoadHistoryCardApi.cancelTask();
        }

        if (carouselAdApi != null) {
            carouselAdApi.cancelTask();
        }

        if (null != rootView) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout arg0, int i) {
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

        appBarLayout = (AppBarLayout) rootView.findViewById(R.id.LiShi_app_bar);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) rootView.findViewById(R.id.LiShi_toolbar_layout);
        mHeaderPicture = (OKKenBurnsView) rootView.findViewById(R.id.LiShi_header_picture_imag);
        floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.LiShi_fabtop);
        mRecyclerView = (OKRecyclerView) rootView.findViewById(R.id.ok_content_collapsing_RecyclerView);
        mRefreshLayout = (RefreshLayout) rootView.findViewById(R.id.ok_content_collapsing_refresh);
        mDrawerLayout = (DrawerLayout) rootView.findViewById(R.id.ok_fragment_history_drawerLayout);
        mNavigationView = (NavigationView) rootView.findViewById(R.id.ok_fragment_history_NavigationView);

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
        mCollapsingToolbarLayout.setTitle("历史记录");
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

        mEntryViewAdapter = new EntryViewAdapter(getActivity(), mCardBeanList);
        mRecyclerView.setAdapter(mEntryViewAdapter);

        floatingActionButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showAlertDialog("历史记录", "确定清空所有历史记录?", "确认", "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (mCardBeanList.size() == 0) {
                            showSnackBar(mRecyclerView, "没有历史记录", "");
                            return;
                        }
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                OKDatabaseHelper helper = OKDatabaseHelper.getHelper(getActivity());
                                try {
                                    List<OKCardBean> mList = helper.getCardDao().queryForAll();
                                    for (OKCardBean mCardBean : mList) {
                                        mCardBean.setRead(false);
                                        helper.getCardDao().createOrUpdate(mCardBean);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                        mCardBeanList.clear();
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                    }
                });
            }
        });

        mToolbarSearch.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt(INTENT_KEY_INTERFACE_TYPE, INTERFACE_HISTORY);
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

    int page = 0;
    int size = 30;

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        OKCardBean lastBean = mEntryViewAdapter.getLastBean();
        if (lastBean == null) {
            mRefreshLayout.finishLoadMore(1500);
            showSnackBar(mRecyclerView, "没有历史记录!", "");
            return;
        }

        OKLoadHistoryCardApi.Params params = new OKLoadHistoryCardApi.Params();
        params.setPage(page + 1);
        params.setSize(size);

        if (mOKLoadHistoryCardApi == null) {
            mOKLoadHistoryCardApi = new OKLoadHistoryCardApi(getActivity());
        }
        mOKLoadHistoryCardApi.requestHistoryCard(params, this);
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {

        OKLoadHistoryCardApi.Params params = new OKLoadHistoryCardApi.Params();
        params.setPage(1);
        params.setSize(size);

        if (mOKLoadHistoryCardApi == null) {
            mOKLoadHistoryCardApi = new OKLoadHistoryCardApi(getActivity());
        }
        mOKLoadHistoryCardApi.requestHistoryCard(params, this);
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
    public void historyApiComplete(List<OKCardBean> list) {
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

    private class EntryViewAdapter extends RecyclerView.Adapter<EntryViewAdapter.EntryViewHolder> {
        private Context mContext;
        private List<OKCardBean> mBeanList;

        public EntryViewAdapter(Context context, List<OKCardBean> list) {
            this.mContext = context;
            this.mBeanList = list;
        }

        private void initViews(final EntryViewHolder mEntryViewHolder, final OKCardBean okCardBean, final int position) {
            GlideApi(mEntryViewHolder.mImageViewTitle, R.drawable.lish_card, R.drawable.lish_card, R.drawable.lish_card);
            if (okCardBean.getCardType().equals(CARD_TYPE_TP)) {
                mEntryViewHolder.mTextViewTitle.setText("精彩图片");
                mEntryViewHolder.mTextViewContent.setText(okCardBean.getTitleText() + " 发表");
                mEntryViewHolder.mTextViewDate.setText(OKDateUtil.formatTime(new Date(okCardBean.getReadTime())));
            } else {
                mEntryViewHolder.mTextViewTitle.setText(okCardBean.getContentTitleText());
                mEntryViewHolder.mTextViewContent.setText(okCardBean.getContentText());
                mEntryViewHolder.mTextViewDate.setText(OKDateUtil.formatTime(new Date(okCardBean.getReadTime())));
            }

            mEntryViewHolder.mCardView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (okCardBean.getCardType().equals(CARD_TYPE_TW)) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(INTENT_KEY_INTERFACE_TYPE, INTERFACE_HISTORY);
                        bundle.putSerializable(OKCardTWActivity.KEY_INTENT_IMAGE_AND_TEXT_CARD, okCardBean);
                        startUserActivity(bundle, OKCardTWActivity.class);
                    } else if (okCardBean.getCardType().equals(CARD_TYPE_TP)) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(INTENT_KEY_INTERFACE_TYPE, INTERFACE_HISTORY);
                        bundle.putSerializable(OKCardTPActivity.KEY_INTENT_IMAGE_CARD, okCardBean);
                        startUserActivity(bundle, OKCardTPActivity.class);
                    } else if (okCardBean.getCardType().equals(CARD_TYPE_WZ)) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(INTENT_KEY_INTERFACE_TYPE, INTERFACE_HISTORY);
                        bundle.putSerializable(OKCardWZActivity.KEY_INTENT_TEXT_CARD, okCardBean);
                        startUserActivity(bundle, OKCardWZActivity.class);
                    }
                }
            });
        }

        @Override
        public EntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.ok_item_entry, parent, false);
            return new EntryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(EntryViewHolder holder, int position) {
            initViews(holder, mBeanList.get(position), position);
        }

        @Override
        public int getItemCount() {
            return mBeanList.size();
        }

        public OKCardBean getLastBean() {
            if (mBeanList.size() != 0) {
                return mBeanList.get(mBeanList.size() - 1);
            } else {
                return null;
            }
        }

        class EntryViewHolder extends RecyclerView.ViewHolder {
            public CardView mCardView;
            public ImageView mImageViewTitle;
            public TextView mTextViewTitle, mTextViewContent, mTextViewDate;
            public Button mButtonOpt;

            public EntryViewHolder(View itemView) {
                super(itemView);
                mCardView = itemView.findViewById(R.id.entryView);
                mImageViewTitle = itemView.findViewById(R.id.entryView_biaoti_imag);
                mTextViewTitle = itemView.findViewById(R.id.entryView_biaoti_text);
                mTextViewContent = itemView.findViewById(R.id.entryView_fubiaoti_text);
                mTextViewDate = itemView.findViewById(R.id.entryView_date_text);
                mButtonOpt = itemView.findViewById(R.id.entryView_caozuo_but);

                mTextViewDate.setVisibility(View.VISIBLE);
                mButtonOpt.setVisibility(View.GONE);
            }
        }
    }
}
