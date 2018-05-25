package com.onlyknow.app.ui.fragement.me;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.onlyknow.app.OKConstant;
import com.onlyknow.app.R;
import com.onlyknow.app.api.OKServiceResult;
import com.onlyknow.app.api.card.OKLoadWatchCardApi;
import com.onlyknow.app.api.card.OKManagerCardApi;
import com.onlyknow.app.db.bean.OKCardBean;
import com.onlyknow.app.db.bean.OKUserInfoBean;
import com.onlyknow.app.ui.OKBaseFragment;
import com.onlyknow.app.ui.activity.OKCardTPActivity;
import com.onlyknow.app.ui.activity.OKCardTWActivity;
import com.onlyknow.app.ui.activity.OKCardWZActivity;
import com.onlyknow.app.ui.activity.OKHomePageActivity;
import com.onlyknow.app.ui.activity.OKLoginActivity;
import com.onlyknow.app.ui.view.OKRecyclerView;
import com.onlyknow.app.utils.OKDateUtil;
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

public class OKWatchFragment extends OKBaseFragment implements OnRefreshListener, OnLoadMoreListener, OKLoadWatchCardApi.onCallBack {
    private RefreshLayout mRefreshLayout;
    private OKRecyclerView mOKRecyclerView;
    private CardViewAdapter mCardViewAdapter;

    private OKLoadWatchCardApi mOKLoadWatchCardApi;
    private List<OKCardBean> mCardBeanList = new ArrayList<>();

    private View rootView;
    public boolean isPause = true;
    private boolean isInitLoad = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.ok_fragment_universal, container, false);
            initUserBody();

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
        if (USER_BODY.getBoolean("STATE", false)) {
            if (isInitLoad && mRefreshLayout.getState() != RefreshState.Refreshing && mOKRecyclerView.getAdapter().getItemCount() == 0) {
                mRefreshLayout.autoRefresh();
            }
            setEmptyTag(TAG_RETRY);
            setEmptyButTitle("重  试");
            setEmptyTxtTitle(getResources().getString(R.string.ListView_NoData));
        } else {
            mCardBeanList.clear();
            mOKRecyclerView.getAdapter().notifyDataSetChanged();
            setEmptyTag(TAG_LOGIN);
            setEmptyButTitle("登  录");
            setEmptyTxtTitle("未登录,登录后查看!");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mRefreshLayout.finishRefresh();
        mRefreshLayout.finishLoadMore();
        if (mOKLoadWatchCardApi != null) {
            mOKLoadWatchCardApi.cancelTask();
        }

        if (mCardViewAdapter != null) {
            mCardViewAdapter.cancelTask();
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
        mCardViewAdapter = new CardViewAdapter(getActivity(), mCardBeanList);
        mOKRecyclerView.setAdapter(mCardViewAdapter);

        mOKRecyclerView.setEmptyView(initCollapsingEmptyView(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int tag = getEmptyTag();
                if (tag == TAG_RETRY) {
                    mRefreshLayout.autoRefresh();
                } else if (tag == TAG_LOGIN) {
                    startUserActivity(null, OKLoginActivity.class);
                }
            }
        }));

        if (USER_BODY.getBoolean("STATE", false)) {
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
        if (USER_BODY.getBoolean("STATE", false)) {
            OKLoadWatchCardApi.Params params = new OKLoadWatchCardApi.Params();
            params.setUsername(USER_BODY.getString(OKUserInfoBean.KEY_USERNAME, ""));
            params.setPage(page + 1);
            params.setSize(size);

            if (mOKLoadWatchCardApi == null) {
                mOKLoadWatchCardApi = new OKLoadWatchCardApi(getActivity());
            }
            mOKLoadWatchCardApi.requestWatchCard(params, this);
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
        if (USER_BODY.getBoolean("STATE", false)) {
            OKLoadWatchCardApi.Params params = new OKLoadWatchCardApi.Params();
            params.setUsername(USER_BODY.getString(OKUserInfoBean.KEY_USERNAME, ""));
            params.setPage(1);
            params.setSize(size);

            if (mOKLoadWatchCardApi == null) {
                mOKLoadWatchCardApi = new OKLoadWatchCardApi(getActivity());
            }
            mOKLoadWatchCardApi.requestWatchCard(params, this);
        } else {
            mRefreshLayout.finishRefresh(1500);
            showSnackBar(rootView, "登录后查看!", "");
        }
    }

    @Override
    public void loadWatchComplete(List<OKCardBean> list) {
        if (list != null) {
            if (mRefreshLayout.getState() == RefreshState.Refreshing) {
                page = 1;

                mCardBeanList.clear();
                mCardBeanList.addAll(list);
            } else if (mRefreshLayout.getState() == RefreshState.Loading) {
                page++;

                mCardBeanList.addAll(list);
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

    private class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.CardViewHolder> implements OKManagerCardApi.onCallBack {
        private Context mContext;
        private List<OKCardBean> mBeanList;
        private OKManagerCardApi mOKManagerCardApi;
        private CardViewHolder viewHolder;

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
                        bundle.putInt(INTENT_KEY_INTERFACE_TYPE, INTERFACE_COLLECTION);
                        bundle.putSerializable(OKCardTWActivity.KEY_INTENT_IMAGE_AND_TEXT_CARD, okCardBean);
                        startUserActivity(bundle, OKCardTWActivity.class);
                    } else if (okCardBean.getCardType().equals(CARD_TYPE_TP)) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(INTENT_KEY_INTERFACE_TYPE, INTERFACE_COLLECTION);
                        bundle.putSerializable(OKCardTPActivity.KEY_INTENT_IMAGE_CARD, okCardBean);
                        startUserActivity(bundle, OKCardTPActivity.class);
                    } else if (okCardBean.getCardType().equals(CARD_TYPE_WZ)) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(INTENT_KEY_INTERFACE_TYPE, INTERFACE_COLLECTION);
                        bundle.putSerializable(OKCardWZActivity.KEY_INTENT_TEXT_CARD, okCardBean);
                        startUserActivity(bundle, OKCardWZActivity.class);
                    }
                }
            });

            mCardViewHolder.mImageViewDelete.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setIcon(R.drawable.ic_launcher);
                    dialog.setTitle("删除收藏");
                    dialog.setMessage("是否删除该条收藏 ?");
                    dialog.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (USER_BODY.getBoolean("STATE", false)) {

                                viewHolder = mCardViewHolder;

                                OKManagerCardApi.Params params = new OKManagerCardApi.Params();
                                params.setType(OKManagerCardApi.Params.TYPE_REMOVE_WATCH);
                                params.setUsername(USER_BODY.getString(OKUserInfoBean.KEY_USERNAME, ""));
                                params.setPassword(USER_BODY.getString(OKUserInfoBean.KEY_PASSWORD, ""));
                                params.setCardId(okCardBean.getCardId());
                                params.setPos(position);
                                params.setMsg("");

                                cancelTask();
                                mOKManagerCardApi = new OKManagerCardApi(getActivity());
                                mOKManagerCardApi.requestManagerCard(params, CardViewAdapter.this); // 并行执行线程
                            } else {
                                startUserActivity(null, OKLoginActivity.class);
                            }
                        }
                    });
                    dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                        }
                    });
                    dialog.show();
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

        public void removeCardBean(int i) {
            if (i >= mBeanList.size()) {
                return;
            }
            mBeanList.remove(i);
            mOKRecyclerView.getAdapter().notifyDataSetChanged();
        }

        public void cancelTask() {
            if (mOKManagerCardApi != null) {
                mOKManagerCardApi.cancelTask(); // 如果线程已经在执行则取消执行
            }
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

        @Override
        public void managerCardComplete(OKServiceResult<Object> result, String type, int pos) {
            if (!OKManagerCardApi.Params.TYPE_REMOVE_WATCH.equals(type)) return;

            if (viewHolder == null || viewHolder.getListPosition() != pos) return;

            if (result != null && result.isSuccess()) {
                removeCardBean(pos);
                showSnackBar(viewHolder.mCardView, "您已移除该卡片", "");
            } else {
                showSnackBar(viewHolder.mCardView, "卡片移除失败", "ErrorCode: " + OKConstant.ARTICLE_CANCEL_ERROR);
            }
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
