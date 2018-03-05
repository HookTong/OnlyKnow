package com.onlyknow.app.ui.fragement;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
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
import com.onlyknow.app.net.OKBusinessNet;
import com.onlyknow.app.api.OKLoadApproveApi;
import com.onlyknow.app.database.bean.OKCardBean;
import com.onlyknow.app.database.bean.OKUserInfoBean;
import com.onlyknow.app.ui.OKBaseFragment;
import com.onlyknow.app.ui.activity.OKHomePageActivity;
import com.onlyknow.app.ui.activity.OKLoginActivity;
import com.onlyknow.app.ui.view.OKRecyclerView;
import com.onlyknow.app.utils.OKNetUtil;
import com.scwang.smartrefresh.header.TaurusHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 卡片审批列表界面
 * 拥有免审批权限者发表的文章卡片将不移至审批队列中
 * <p>
 * Created by Administrator on 2018/3/2.
 */

public class OKApproveFragment extends OKBaseFragment implements OnRefreshListener, OnLoadMoreListener, OKLoadApproveApi.onCallBack {
    private RefreshLayout mRefreshLayout;
    private OKRecyclerView mOKRecyclerView;
    private CardViewAdapter mCardViewAdapter;

    private OKLoadApproveApi mLoadApproveApi;
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
        if (mLoadApproveApi != null) {
            mLoadApproveApi.cancelTask();
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

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        if (!OKNetUtil.isNet(getActivity())) {
            mRefreshLayout.finishLoadMore(1500);
            showSnackbar(mOKRecyclerView, "请检查网络设置!", "");
            return;
        }
        if (USER_INFO_SP.getBoolean("STATE", false)) {
            OKCardBean cardBean = mCardViewAdapter.getLastCardBean();
            if (cardBean == null) {
                mRefreshLayout.finishLoadMore(1500);
                return;
            }
            Map<String, String> map = new HashMap<>();// 请求参数
            map.put("username", USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
            map.put("max_id", Integer.toString(cardBean.getCARD_ID()));
            map.put("load_type", "APPROVE_CARD");
            if (mLoadApproveApi == null) {
                mLoadApproveApi = new OKLoadApproveApi(getActivity(), true);
            }
            mLoadApproveApi.requestCardBeanList(map, true, this);
        } else {
            mRefreshLayout.finishLoadMore(1500);
            showSnackbar(mOKRecyclerView, "登录后加载", "");
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        if (!OKNetUtil.isNet(getActivity())) {
            if (USER_INFO_SP.getBoolean("STATE", false) && mOKRecyclerView.getAdapter().getItemCount() == 0) {
                mOKCardBeanList.clear();
                mOKCardBeanList.addAll(OKConstant.getListCache(INTERFACE_APPROVE));
                mOKRecyclerView.getAdapter().notifyDataSetChanged();
            }
            mRefreshLayout.finishRefresh(1500);
            showSnackbar(mOKRecyclerView, "请检查网络设置!", "");
            return;
        }
        if (USER_INFO_SP.getBoolean("STATE", false)) {
            Map<String, String> map = new HashMap<>();// 请求参数
            map.put("username", USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
            map.put("num", OKConstant.APPROVE_LOAD_COUNT);
            if (mLoadApproveApi == null) {
                mLoadApproveApi = new OKLoadApproveApi(getActivity(), false);
            }
            mLoadApproveApi.requestCardBeanList(map, false, this);
        } else {
            mRefreshLayout.finishRefresh(1500);
            showSnackbar(rootView, "登录后查看!", "");
        }
    }

    @Override
    public void approveApiComplete(List<OKCardBean> list) {
        if (list != null) {
            if (mRefreshLayout.getState() == RefreshState.Refreshing) {
                mOKCardBeanList.clear();
                mOKCardBeanList.addAll(list);
            } else if (mRefreshLayout.getState() == RefreshState.Loading) {
                mOKCardBeanList.addAll(list);
            }
            OKConstant.putListCache(INTERFACE_APPROVE, mOKCardBeanList);
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
        private List<OKCardBean> mBeanList;
        private CardTask mCardTask;

        public CardViewAdapter(Context context, List<OKCardBean> list) {
            this.mContext = context;
            this.mBeanList = list;
        }

        private void initViews(final CardViewHolder mCardViewHolder, final OKCardBean bean, final int position) {
            mCardViewHolder.setListPosition(position);

            String cardType = bean.getCARD_TYPE();
            // 设置标题控件内容
            GlideRoundApi(mCardViewHolder.mImageViewAvatar, bean.getTITLE_IMAGE_URL(), R.drawable.touxian_placeholder, R.drawable.touxian_placeholder);
            mCardViewHolder.mTextViewTitle.setText(bean.getTITLE_TEXT());
            mCardViewHolder.mTextViewDate.setText(formatTime(bean.getCREATE_DATE()) + " 发表");
            // 设置内容控件内容
            if (cardType.equals(CARD_TYPE_TW)) {
                mCardViewHolder.mLinearLayoutContent.setVisibility(View.VISIBLE);
                mCardViewHolder.mImageViewContentImage.setVisibility(View.VISIBLE);

                GlideApi(mCardViewHolder.mImageViewContentImage, getFirstCardImageUrl(bean), R.drawable.toplayout_imag, R.drawable.toplayout_imag);

                String z = Integer.toString(bean.getZAN_NUM());
                String s = Integer.toString(bean.getSHOUCHAN_NUM());
                String p = Integer.toString(bean.getPINGLUN_NUM());
                mCardViewHolder.mTextViewContentTitle.setText(bean.getCONTENT_TITLE_TEXT());
                mCardViewHolder.mTextViewContent.setText(bean.getCONTENT_TEXT());
                mCardViewHolder.mTextViewContentPraise.setText(z + "赞同; " + s + "收藏; " + p + "评论");
            } else if (cardType.equals(CARD_TYPE_TP)) {
                mCardViewHolder.mLinearLayoutContent.setVisibility(View.GONE);
                mCardViewHolder.mImageViewContentImage.setVisibility(View.VISIBLE);

                GlideApi(mCardViewHolder.mImageViewContentImage, getFirstCardImageUrl(bean), R.drawable.toplayout_imag, R.drawable.toplayout_imag);
            } else if (cardType.equals(CARD_TYPE_WZ)) {
                mCardViewHolder.mLinearLayoutContent.setVisibility(View.VISIBLE);
                mCardViewHolder.mImageViewContentImage.setVisibility(View.GONE);

                String z = Integer.toString(bean.getZAN_NUM());
                String s = Integer.toString(bean.getSHOUCHAN_NUM());
                String p = Integer.toString(bean.getPINGLUN_NUM());

                mCardViewHolder.mTextViewContentTitle.setText(bean.getCONTENT_TITLE_TEXT());
                mCardViewHolder.mTextViewContent.setText(bean.getCONTENT_TEXT());
                mCardViewHolder.mTextViewContentPraise.setText(z + "赞同; " + s + "收藏; " + p + "评论");
            }

            mCardViewHolder.mCommentTextView.setText(bean.getAPPROVE_INFO());

            mCardViewHolder.mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSnackbar(v, "审批中的卡片暂时不能查看", "");
                }
            });

            mCardViewHolder.mImageViewAvatar.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // 查看他人主页
                    Bundle bundle = new Bundle();
                    bundle.putString(OKUserInfoBean.KEY_USERNAME, bean.getUSER_NAME());
                    bundle.putString(OKUserInfoBean.KEY_NICKNAME, bean.getTITLE_TEXT());
                    startUserActivity(bundle, OKHomePageActivity.class);
                }
            });

            mCardViewHolder.mImageViewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!bean.getUSER_NAME().equals(USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""))) {
                        showSnackbar(v, "这不是您的卡片", "");
                        return;
                    }
                    showAlertDialog("审批卡片", "是否删除该审批卡片 ?", "确定", "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            if (USER_INFO_SP.getBoolean("STATE", false)) {
                                Map<String, String> param = new HashMap<String, String>();// 请求参数
                                param.put("username_main", USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
                                param.put("card_id", Integer.toString(bean.getCARD_ID()));
                                param.put("type", "CANCEL_APPROVE");
                                mCardTask = new CardTask(mCardViewHolder, position);
                                mCardTask.executeOnExecutor(exec, param); // 并行执行线程
                            } else {
                                startUserActivity(null, OKLoginActivity.class);
                            }
                        }
                    });
                }
            });
        }

        public OKCardBean getLastCardBean() {
            if (mBeanList != null && mBeanList.size() != 0) {
                return mBeanList.get(mBeanList.size() - 1);
            } else {
                return null;
            }
        }


        public void removeCardBean(int i) {
            if (i >= mBeanList.size()) {
                return;
            }
            mBeanList.remove(i);
            OKConstant.removeListCache(INTERFACE_APPROVE, i);
            mOKRecyclerView.getAdapter().notifyDataSetChanged();
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

        class CardTask extends AsyncTask<Map<String, String>, Void, Boolean> {
            private CardViewHolder mCardViewHolder;
            private int mPosition;

            public CardTask(CardViewHolder viewHolder, int pos) {
                this.mCardViewHolder = viewHolder;
                this.mPosition = pos;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (isCancelled() || (mCardViewHolder.getListPosition() != mPosition)) {
                    return;
                }
                if (aBoolean) {
                    removeCardBean(mPosition);
                    showSnackbar(mCardViewHolder.mCardView, "您已移除该卡片", "");
                } else {
                    showSnackbar(mCardViewHolder.mCardView, "卡片移除失败", "ErrorCode: " + OKConstant.ARTICLE_CANCEL_ERROR);
                }
            }

            @Override
            protected Boolean doInBackground(Map<String, String>... params) {
                if (isCancelled()) {
                    return false;
                }
                OKBusinessNet mWebApi = new OKBusinessNet();
                return mWebApi.RemoveCard(params[0]);
            }
        }
    }
}
