package com.onlyknow.app.ui.fragement.me;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onlyknow.app.R;
import com.onlyknow.app.api.card.OKLoadMeCommentCardApi;
import com.onlyknow.app.db.bean.OKMeCommentCardBean;
import com.onlyknow.app.db.bean.OKCardBean;
import com.onlyknow.app.db.bean.OKCommentBean;
import com.onlyknow.app.db.bean.OKUserInfoBean;
import com.onlyknow.app.ui.OKBaseFragment;
import com.onlyknow.app.ui.activity.OKCardTPActivity;
import com.onlyknow.app.ui.activity.OKCardTWActivity;
import com.onlyknow.app.ui.activity.OKCardWZActivity;
import com.onlyknow.app.ui.activity.OKHomePageActivity;
import com.onlyknow.app.ui.activity.OKLoginActivity;
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
import java.util.List;

public class OKCommentFragment extends OKBaseFragment implements OnRefreshListener, OnLoadMoreListener, OKLoadMeCommentCardApi.onCallBack {
    private RefreshLayout mRefreshLayout;
    private OKRecyclerView mOKRecyclerView;
    private CardViewAdapter mCardViewAdapter;

    private OKLoadMeCommentCardApi mOKLoadMeCommentCardApi;
    private List<OKMeCommentCardBean> mOKMeCommentCardBeanList = new ArrayList<>();

    private View rootView;
    public boolean isPause = true;
    private boolean isInitLoad = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.ok_fragment_universal, container, false);
            initUserInfoSharedPreferences();

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
        isPause = false;
        if (USER_INFO_SP.getBoolean("STATE", false)) {
            if (isInitLoad && mRefreshLayout.getState() != RefreshState.Refreshing && mOKRecyclerView.getAdapter().getItemCount() == 0) {
                mRefreshLayout.autoRefresh();
            }
            setEmptyButtonTag(RE_GET);
            setEmptyButtonTitle("重  试");
            setEmptyTextTitle(getResources().getString(R.string.ListView_NoData));
        } else {
            mOKMeCommentCardBeanList.clear();
            mOKRecyclerView.getAdapter().notifyDataSetChanged();
            setEmptyButtonTag(LOG_IN);
            setEmptyButtonTitle("登  录");
            setEmptyTextTitle("未登录,登录后查看!");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mRefreshLayout.finishRefresh();
        mRefreshLayout.finishLoadMore();
        if (mOKLoadMeCommentCardApi != null) {
            mOKLoadMeCommentCardApi.cancelTask();
        }
        isPause = true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (rootView != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
    }

    public void setSwipeRefreshEnabled(boolean b) {
        if (!isPause) {
            mRefreshLayout.setEnableRefresh(b);
            mRefreshLayout.setEnableLoadMore(!b);
        }
    }

    private void findView(View rootView) {
        mOKRecyclerView = (OKRecyclerView) rootView.findViewById(R.id.ok_content_collapsing_RecyclerView);
        mRefreshLayout = (RefreshLayout) rootView.findViewById(R.id.ok_content_collapsing_refresh);

        mOKRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRefreshLayout.setRefreshHeader(new TaurusHeader(getActivity()));
        mRefreshLayout.setRefreshFooter(new BallPulseFooter(getActivity()).setSpinnerStyle(SpinnerStyle.Scale));
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setOnLoadMoreListener(this);
    }

    private void init() {
        mCardViewAdapter = new CardViewAdapter(getActivity(), mOKMeCommentCardBeanList);
        mOKRecyclerView.setAdapter(mCardViewAdapter);

        mOKRecyclerView.setEmptyView(initCollapsingEmptyView(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int tag = getEmptyButtonTag();
                if (tag == RE_GET) {
                    mRefreshLayout.autoRefresh();
                } else if (tag == LOG_IN) {
                    startUserActivity(null, OKLoginActivity.class);
                }
            }
        }));

        if (USER_INFO_SP.getBoolean("STATE", false)) {
            mRefreshLayout.autoRefresh();
        }
    }

    int page = 0;
    int size = 30;

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        if (!OKNetUtil.isNet(getActivity())) {
            mRefreshLayout.finishLoadMore(1500);
            showSnackBar(mOKRecyclerView, "请检查网络设置!", "");
            return;
        }
        if (USER_INFO_SP.getBoolean("STATE", false)) {

            OKLoadMeCommentCardApi.Params params = new OKLoadMeCommentCardApi.Params();
            params.setUsername(USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
            params.setPage(page + 1);
            params.setSize(size);

            if (mOKLoadMeCommentCardApi == null) {
                mOKLoadMeCommentCardApi = new OKLoadMeCommentCardApi(getActivity());
            }
            mOKLoadMeCommentCardApi.requestMeCommentCard(params, this);
        } else {
            mRefreshLayout.finishLoadMore(1500);
            showSnackBar(mOKRecyclerView, "登录后加载", "");
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        if (!OKNetUtil.isNet(getActivity())) {
            mRefreshLayout.finishRefresh(1500);
            showSnackBar(mOKRecyclerView, "请检查网络设置!", "");
            return;
        }
        if (USER_INFO_SP.getBoolean("STATE", false)) {

            OKLoadMeCommentCardApi.Params params = new OKLoadMeCommentCardApi.Params();
            params.setUsername(USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
            params.setPage(1);
            params.setSize(size);

            if (mOKLoadMeCommentCardApi == null) {
                mOKLoadMeCommentCardApi = new OKLoadMeCommentCardApi(getActivity());
            }
            mOKLoadMeCommentCardApi.requestMeCommentCard(params, this);
        } else {
            mRefreshLayout.finishRefresh(1500);
            showSnackBar(rootView, "登录后查看!", "");
        }
    }

    @Override
    public void loadMeCommentComplete(List<OKMeCommentCardBean> list) {
        if (list != null) {
            if (mRefreshLayout.getState() == RefreshState.Refreshing) {
                page = 1;

                mOKMeCommentCardBeanList.clear();
                mOKMeCommentCardBeanList.addAll(list);
            } else if (mRefreshLayout.getState() == RefreshState.Loading) {
                page++;

                mOKMeCommentCardBeanList.addAll(list);
            }
            mOKRecyclerView.getAdapter().notifyDataSetChanged();
        }

        if (mRefreshLayout.getState() == RefreshState.Refreshing) {
            mRefreshLayout.finishRefresh();
        } else if (mRefreshLayout.getState() == RefreshState.Loading) {
            mRefreshLayout.finishLoadMore();
        }
        isInitLoad = false;
    }

    private class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.CardViewHolder> {
        private Context mContext;
        private List<OKMeCommentCardBean> mBeanList;

        public CardViewAdapter(Context context, List<OKMeCommentCardBean> list) {
            this.mContext = context;
            this.mBeanList = list;
        }

        private void initViews(final CardViewHolder mCardViewHolder, final OKMeCommentCardBean okMeCommentCardBean, final int position) {
            final OKCardBean mOKCardBean = okMeCommentCardBean.getCardBean();
            final OKCommentBean mOKCommentBean = okMeCommentCardBean.getCommentBean();
            if (mOKCardBean == null || mOKCommentBean == null) {
                OKLogUtil.print("CardAndCommentBean Get OKCardBean and OKCommentBean Not Null");
                return;
            }

            String cardType = mOKCardBean.getCardType();
            // 设置标题控件内容
            GlideRoundApi(mCardViewHolder.mImageViewAvatar, mOKCardBean.getTitleImageUrl(), R.drawable.touxian_placeholder, R.drawable.touxian_placeholder);
            mCardViewHolder.mTextViewTitle.setText(mOKCardBean.getTitleText());
            mCardViewHolder.mTextViewDate.setText(OKDateUtil.formatTime(mOKCardBean.getCreateDate()) + " 发表");
            // 设置内容控件内容
            if (cardType.equals(CARD_TYPE_TW)) {
                mCardViewHolder.mLinearLayoutContent.setVisibility(View.VISIBLE);
                mCardViewHolder.mImageViewContentImage.setVisibility(View.VISIBLE);

                GlideApi(mCardViewHolder.mImageViewContentImage, mOKCardBean.getFirstCardImage(), R.drawable.toplayout_imag, R.drawable.toplayout_imag);

                String z = Integer.toString(mOKCardBean.getPraiseCount());
                String s = Integer.toString(mOKCardBean.getWatchCount());
                String p = Integer.toString(mOKCardBean.getCommentCount());
                mCardViewHolder.mTextViewContentTitle.setText(mOKCardBean.getContentTitleText());
                mCardViewHolder.mTextViewContent.setText(mOKCardBean.getContentText());
                mCardViewHolder.mTextViewContentPraise.setText(z + "赞同; " + s + "收藏; " + p + "评论");
            } else if (cardType.equals(CARD_TYPE_TP)) {
                mCardViewHolder.mLinearLayoutContent.setVisibility(View.GONE);
                mCardViewHolder.mImageViewContentImage.setVisibility(View.VISIBLE);

                GlideApi(mCardViewHolder.mImageViewContentImage, mOKCardBean.getFirstCardImage(), R.drawable.toplayout_imag, R.drawable.toplayout_imag);
            } else if (cardType.equals(CARD_TYPE_WZ)) {
                mCardViewHolder.mLinearLayoutContent.setVisibility(View.VISIBLE);
                mCardViewHolder.mImageViewContentImage.setVisibility(View.GONE);

                String z = Integer.toString(mOKCardBean.getPraiseCount());
                String s = Integer.toString(mOKCardBean.getWatchCount());
                String p = Integer.toString(mOKCardBean.getCommentCount());
                mCardViewHolder.mTextViewContentTitle.setText(mOKCardBean.getContentTitleText());
                mCardViewHolder.mTextViewContent.setText(mOKCardBean.getContentText());
                mCardViewHolder.mTextViewContentPraise.setText(z + "赞同; " + s + "收藏; " + p + "评论");
            }

            mCardViewHolder.mCommentTextView.setText(mOKCommentBean.getMessage());

            mCardViewHolder.mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOKCardBean.getCardType().equals(CARD_TYPE_TW)) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(INTENT_KEY_INTERFACE_TYPE, INTERFACE_CARD_AND_COMMENT);
                        bundle.putSerializable(OKCardTWActivity.KEY_INTENT_IMAGE_AND_TEXT_CARD, mOKCardBean);
                        startUserActivity(bundle, OKCardTWActivity.class);
                    } else if (mOKCardBean.getCardType().equals(CARD_TYPE_TP)) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(INTENT_KEY_INTERFACE_TYPE, INTERFACE_CARD_AND_COMMENT);
                        bundle.putSerializable(OKCardTPActivity.KEY_INTENT_IMAGE_CARD, mOKCardBean);
                        startUserActivity(bundle, OKCardTPActivity.class);
                    } else if (mOKCardBean.getCardType().equals(CARD_TYPE_WZ)) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(INTENT_KEY_INTERFACE_TYPE, INTERFACE_CARD_AND_COMMENT);
                        bundle.putSerializable(OKCardWZActivity.KEY_INTENT_TEXT_CARD, mOKCardBean);
                        startUserActivity(bundle, OKCardWZActivity.class);
                    }
                }
            });

            mCardViewHolder.mImageViewAvatar.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // 查看他人主页
                    Bundle bundle = new Bundle();
                    bundle.putString(OKUserInfoBean.KEY_USERNAME, mOKCardBean.getUserName());
                    bundle.putString(OKUserInfoBean.KEY_NICKNAME, mOKCardBean.getTitleText());
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

            public LinearLayout mCommentLinearLayout;
            public TextView mCommentTextView;

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
                mCommentLinearLayout = itemView.findViewById(R.id.ok_item_card_comment_layout);
                mCommentTextView = itemView.findViewById(R.id.ok_item_card_comment_text);

                mCommentLinearLayout.setVisibility(View.VISIBLE);
                mCommentTextView.setVisibility(View.VISIBLE);
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
