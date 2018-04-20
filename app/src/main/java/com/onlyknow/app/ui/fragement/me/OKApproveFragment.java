package com.onlyknow.app.ui.fragement.me;

import android.content.Context;
import android.content.DialogInterface;
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

import com.onlyknow.app.OKConstant;
import com.onlyknow.app.R;
import com.onlyknow.app.api.OKServiceResult;
import com.onlyknow.app.api.card.OKLoadHomeCardApi;
import com.onlyknow.app.api.card.OKManagerCardApi;
import com.onlyknow.app.db.bean.OKCardBean;
import com.onlyknow.app.db.bean.OKUserInfoBean;
import com.onlyknow.app.ui.OKBaseFragment;
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

/**
 * 卡片审批列表界面
 * 拥有免审批权限者发表的文章卡片将不移至审批队列中
 * <p>
 * Created by Administrator on 2018/3/2.
 */

public class OKApproveFragment extends OKBaseFragment implements OnRefreshListener, OnLoadMoreListener, OKLoadHomeCardApi.onCallBack {
    private RefreshLayout mRefreshLayout;
    private OKRecyclerView mOKRecyclerView;
    private CardViewAdapter mCardViewAdapter;

    private OKLoadHomeCardApi okLoadHomeCardApi;
    private List<OKCardBean> mOKCardBeanList = new ArrayList<>();

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
            mOKCardBeanList.clear();
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
        if (okLoadHomeCardApi != null) {
            okLoadHomeCardApi.cancelTask();
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
        mCardViewAdapter = new CardViewAdapter(getActivity(), mOKCardBeanList);
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
            OKLoadHomeCardApi.Params params = new OKLoadHomeCardApi.Params();
            params.setUsername(USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
            params.setPage(page + 1);
            params.setSize(size);
            params.setApproveIn(true);

            if (okLoadHomeCardApi == null) {
                okLoadHomeCardApi = new OKLoadHomeCardApi(getActivity());
            }
            okLoadHomeCardApi.requestHomeCard(params, this);
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
            OKLoadHomeCardApi.Params params = new OKLoadHomeCardApi.Params();
            params.setUsername(USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
            params.setPage(1);
            params.setSize(size);
            params.setApproveIn(true);

            if (okLoadHomeCardApi == null) {
                okLoadHomeCardApi = new OKLoadHomeCardApi(getActivity());
            }
            okLoadHomeCardApi.requestHomeCard(params, this);
        } else {
            mRefreshLayout.finishRefresh(1500);
            showSnackBar(rootView, "登录后查看!", "");
        }
    }

    @Override
    public void loadHomeComplete(List<OKCardBean> list) {
        if (list != null) {
            if (mRefreshLayout.getState() == RefreshState.Refreshing) {
                page = 1;

                mOKCardBeanList.clear();
                mOKCardBeanList.addAll(list);
            } else if (mRefreshLayout.getState() == RefreshState.Loading) {
                page++;

                mOKCardBeanList.addAll(list);
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

        private void initViews(final CardViewHolder mCardViewHolder, final OKCardBean bean, final int position) {
            mCardViewHolder.setListPosition(position);

            String cardType = bean.getCardType();
            // 设置标题控件内容
            GlideRoundApi(mCardViewHolder.mImageViewAvatar, bean.getTitleImageUrl(), R.drawable.touxian_placeholder, R.drawable.touxian_placeholder);
            mCardViewHolder.mTextViewTitle.setText(bean.getTitleText());
            mCardViewHolder.mTextViewDate.setText(OKDateUtil.formatTime(bean.getCreateDate()) + " 发表");
            // 设置内容控件内容
            if (cardType.equals(CARD_TYPE_TW)) {
                mCardViewHolder.mLinearLayoutContent.setVisibility(View.VISIBLE);
                mCardViewHolder.mImageViewContentImage.setVisibility(View.VISIBLE);

                GlideApi(mCardViewHolder.mImageViewContentImage, bean.getFirstCardImage(), R.drawable.toplayout_imag, R.drawable.toplayout_imag);

                String z = Integer.toString(bean.getPraiseCount());
                String s = Integer.toString(bean.getWatchCount());
                String p = Integer.toString(bean.getCommentCount());
                mCardViewHolder.mTextViewContentTitle.setText(bean.getContentTitleText());
                mCardViewHolder.mTextViewContent.setText(bean.getContentText());
                mCardViewHolder.mTextViewContentPraise.setText(z + "赞同; " + s + "收藏; " + p + "评论");
            } else if (cardType.equals(CARD_TYPE_TP)) {
                mCardViewHolder.mLinearLayoutContent.setVisibility(View.GONE);
                mCardViewHolder.mImageViewContentImage.setVisibility(View.VISIBLE);

                GlideApi(mCardViewHolder.mImageViewContentImage, bean.getFirstCardImage(), R.drawable.toplayout_imag, R.drawable.toplayout_imag);
            } else if (cardType.equals(CARD_TYPE_WZ)) {
                mCardViewHolder.mLinearLayoutContent.setVisibility(View.VISIBLE);
                mCardViewHolder.mImageViewContentImage.setVisibility(View.GONE);

                String z = Integer.toString(bean.getPraiseCount());
                String s = Integer.toString(bean.getWatchCount());
                String p = Integer.toString(bean.getCommentCount());
                mCardViewHolder.mTextViewContentTitle.setText(bean.getContentTitleText());
                mCardViewHolder.mTextViewContent.setText(bean.getContentText());
                mCardViewHolder.mTextViewContentPraise.setText(z + "赞同; " + s + "收藏; " + p + "评论");
            }

            mCardViewHolder.mCommentTextView.setText(bean.getApproveInfo());

            mCardViewHolder.mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSnackBar(v, "审批中的卡片暂时不能查看", "");
                }
            });

            mCardViewHolder.mImageViewAvatar.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // 查看他人主页
                    Bundle bundle = new Bundle();
                    bundle.putString(OKUserInfoBean.KEY_USERNAME, bean.getUserName());
                    bundle.putString(OKUserInfoBean.KEY_NICKNAME, bean.getTitleText());
                    startUserActivity(bundle, OKHomePageActivity.class);
                }
            });

            mCardViewHolder.mImageViewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!bean.getUserName().equals(USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""))) {
                        showSnackBar(v, "这不是您的卡片", "");
                        return;
                    }
                    showAlertDialog("审批卡片", "是否删除该审批卡片 ?", "确定", "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            if (USER_INFO_SP.getBoolean("STATE", false)) {

                                viewHolder = mCardViewHolder;

                                OKManagerCardApi.Params params = new OKManagerCardApi.Params();
                                params.setType(OKManagerCardApi.Params.TYPE_REMOVE_CARD);
                                params.setUsername(USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
                                params.setPassword(USER_INFO_SP.getString(OKUserInfoBean.KEY_PASSWORD, ""));
                                params.setCardId(bean.getCardId());
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
            if (!OKManagerCardApi.Params.TYPE_REMOVE_CARD.equals(type)) return;

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

            public LinearLayout mCommentLinearLayout;
            public TextView mCommentTextView, mCommentTitleTextView;

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
                mCommentTitleTextView = itemView.findViewById(R.id.ok_item_card_comment_title_text);

                mCommentLinearLayout.setVisibility(View.VISIBLE);
                mCommentTextView.setVisibility(View.VISIBLE);
                mImageViewDelete.setVisibility(View.VISIBLE);
                mCommentTitleTextView.setText("审批状态 :");
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
