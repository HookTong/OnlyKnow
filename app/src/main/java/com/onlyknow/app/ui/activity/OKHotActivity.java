package com.onlyknow.app.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onlyknow.app.R;
import com.onlyknow.app.api.card.OKLoadHotCardApi;
import com.onlyknow.app.db.bean.OKCardBean;
import com.onlyknow.app.db.bean.OKUserInfoBean;
import com.onlyknow.app.ui.OKBaseActivity;
import com.onlyknow.app.ui.view.OKRecyclerView;
import com.onlyknow.app.utils.OKDateUtil;
import com.onlyknow.app.utils.OKNetUtil;
import com.scwang.smartrefresh.header.TaurusHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

public class OKHotActivity extends OKBaseActivity implements OnRefreshListener, OKLoadHotCardApi.onCallBack {
    private RefreshLayout mRefreshLayout;
    private OKRecyclerView mOKRecyclerView;
    private CardViewAdapter mCardViewAdapter;

    private OKLoadHotCardApi mOKLoadHotCardApi;
    private List<OKCardBean> mCardBeanList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ok_activity_hot);
        initSystemBar(this);
        findView();
        init();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mToolbar != null) {
            mToolbar.setTitle("");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mRefreshLayout.finishRefresh();
        mRefreshLayout.finishLoadMore();
        if (mOKLoadHotCardApi != null) {
            mOKLoadHotCardApi.cancelTask();
        }
    }

    private void findView() {
        super.findCommonToolbarView(this);
        setSupportActionBar(mToolbar);

        mToolbarBack.setVisibility(View.VISIBLE);
        mToolbarTitle.setVisibility(View.VISIBLE);
        mToolbarTitle.setText("热门卡片 TOP20");

        mRefreshLayout = (RefreshLayout) findViewById(R.id.ok_content_collapsing_refresh);
        mOKRecyclerView = (OKRecyclerView) findViewById(R.id.ok_content_collapsing_RecyclerView);

        mOKRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRefreshLayout.setRefreshHeader(new TaurusHeader(this));
        mRefreshLayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale));
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setEnableLoadMore(false);
    }

    private void init() {
        mCardViewAdapter = new CardViewAdapter(this, mCardBeanList);
        mOKRecyclerView.setAdapter(mCardViewAdapter);

        mToolbarBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                OKHotActivity.this.finish();
            }
        });

        mOKRecyclerView.setEmptyView(initCommonEmptyView(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mRefreshLayout.autoRefresh();
            }
        }));

        mRefreshLayout.autoRefresh();
    }

    int size = 20;

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        if (OKNetUtil.isNet(this)) {
            OKLoadHotCardApi.Params params = new OKLoadHotCardApi.Params();
            params.setSize(size);

            if (mOKLoadHotCardApi == null) {
                mOKLoadHotCardApi = new OKLoadHotCardApi(this);
            }
            mOKLoadHotCardApi.requestHotCard(params, this);
        } else {
            mRefreshLayout.finishRefresh(1500);
            showSnackBar(mOKRecyclerView, "没有网络连接!", "");
        }
    }

    @Override
    public void loadHotComplete(List<OKCardBean> list) {
        if (list != null) {
            mCardBeanList.clear();
            mCardBeanList.addAll(list);
            mOKRecyclerView.getAdapter().notifyDataSetChanged();
        }
        mRefreshLayout.finishRefresh();
    }

    private class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.CardViewHolder> {
        private Context mContext;
        private List<OKCardBean> mBeanList;

        public CardViewAdapter(Context context, List<OKCardBean> list) {
            this.mContext = context;
            this.mBeanList = list;
        }

        private void initViews(final CardViewHolder mCardViewHolder, final OKCardBean okCardBean, final int position) {
            mCardViewHolder.setListPosition(position);

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

                String z = Integer.toString(okCardBean.getPraiseCount());
                String s = Integer.toString(okCardBean.getWatchCount());
                String p = Integer.toString(okCardBean.getCommentCount());
                mCardViewHolder.mTextViewContentTitle.setText(okCardBean.getContentTitleText());
                mCardViewHolder.mTextViewContent.setText(okCardBean.getContentText());
                mCardViewHolder.mTextViewContentPraise.setText(z + "赞同; " + s + "收藏; " + p + "评论");
            } else if (cardType.equals(CARD_TYPE_TP)) {
                mCardViewHolder.mLinearLayoutContent.setVisibility(View.GONE);
                mCardViewHolder.mImageViewContentImage.setVisibility(View.VISIBLE);
                GlideApi(mCardViewHolder.mImageViewContentImage, okCardBean.getFirstCardImage(), R.drawable.toplayout_imag, R.drawable.toplayout_imag);
            } else if (cardType.equals(CARD_TYPE_WZ)) {
                mCardViewHolder.mLinearLayoutContent.setVisibility(View.VISIBLE);
                mCardViewHolder.mImageViewContentImage.setVisibility(View.GONE);

                String z = Integer.toString(okCardBean.getPraiseCount());
                String s = Integer.toString(okCardBean.getWatchCount());
                String p = Integer.toString(okCardBean.getCommentCount());
                mCardViewHolder.mTextViewContentTitle.setText(okCardBean.getContentTitleText());
                mCardViewHolder.mTextViewContent.setText(okCardBean.getContentText());
                mCardViewHolder.mTextViewContentPraise.setText(z + "赞同; " + s + "收藏; " + p + "评论");
            }

            mCardViewHolder.mCardView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (okCardBean.getCardType().equals(CARD_TYPE_TW)) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(INTENT_KEY_INTERFACE_TYPE, INTERFACE_HOT);
                        bundle.putSerializable(OKCardTWActivity.KEY_INTENT_IMAGE_AND_TEXT_CARD, okCardBean);
                        startUserActivity(bundle, OKCardTWActivity.class);
                    } else if (okCardBean.getCardType().equals(CARD_TYPE_TP)) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(INTENT_KEY_INTERFACE_TYPE, INTERFACE_HOT);
                        bundle.putSerializable(OKCardTPActivity.KEY_INTENT_IMAGE_CARD, okCardBean);
                        startUserActivity(bundle, OKCardTPActivity.class);
                    } else if (okCardBean.getCardType().equals(CARD_TYPE_WZ)) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(INTENT_KEY_INTERFACE_TYPE, INTERFACE_HOT);
                        bundle.putSerializable(OKCardWZActivity.KEY_INTENT_TEXT_CARD, okCardBean);
                        startUserActivity(bundle, OKCardWZActivity.class);
                    }
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
                mImageViewDelete.setVisibility(View.GONE);
            }

            int position;

            public void setListPosition(int pos) {
                this.position = pos;
            }

            public int getListPosition() {
                return position;
            }
        }
    }
}
