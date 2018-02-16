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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onlyknow.app.OKConstant;
import com.onlyknow.app.R;
import com.onlyknow.app.api.OKBusinessApi;
import com.onlyknow.app.api.OKLoadDynamicApi;
import com.onlyknow.app.database.bean.OKCardBean;
import com.onlyknow.app.database.bean.OKUserInfoBean;
import com.onlyknow.app.ui.OKBaseFragment;
import com.onlyknow.app.ui.activity.OKCardTPActivity;
import com.onlyknow.app.ui.activity.OKCardTWActivity;
import com.onlyknow.app.ui.activity.OKCardWZActivity;
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

public class OKDynamicFragment extends OKBaseFragment implements OnRefreshListener, OnLoadMoreListener, OKLoadDynamicApi.onCallBack {
    private RefreshLayout mRefreshLayout;
    private OKRecyclerView mRecyclerView;
    private CardViewAdapter mCardViewAdapter;

    private OKLoadDynamicApi mOKLoadDynamicApi;
    private List<OKCardBean> mCardBeanList = new ArrayList<>();

    private View rootView;
    public boolean isPause = true;
    public boolean isInitLoad = true;

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
            if (isInitLoad && mRefreshLayout.getState() != RefreshState.Refreshing && mRecyclerView.getAdapter().getItemCount() == 0) {
                mRefreshLayout.autoRefresh();
            }
            setEmptyButtonTag(RE_GET);
            setEmptyButtonTitle("重 试");
            setEmptyTextTitle("别急,小二可能在忙!");
        } else {
            mCardBeanList.clear();
            mRecyclerView.getAdapter().notifyDataSetChanged();
            setEmptyButtonTag(LOG_IN);
            setEmptyButtonTitle("登 录");
            setEmptyTextTitle("未登录,登录后查看!");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOKLoadDynamicApi != null) {
            mOKLoadDynamicApi.cancelTask();
        }

        if (mCardViewAdapter != null) {
            mCardViewAdapter.cancelTask();
        }
        mRefreshLayout.finishRefresh();
        mRefreshLayout.finishLoadMore();
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
        mRecyclerView = (OKRecyclerView) rootView.findViewById(R.id.ok_content_collapsing_RecyclerView);
        mRefreshLayout = (RefreshLayout) rootView.findViewById(R.id.ok_content_collapsing_refresh);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRefreshLayout.setRefreshHeader(new TaurusHeader(getActivity()));
        mRefreshLayout.setRefreshFooter(new BallPulseFooter(getActivity()).setSpinnerStyle(SpinnerStyle.Scale));
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setOnLoadMoreListener(this);
    }

    private void init() {
        mCardViewAdapter = new CardViewAdapter(getActivity(), mCardBeanList);
        mRecyclerView.setAdapter(mCardViewAdapter);

        mRecyclerView.setEmptyView(initCollapsingEmptyView(new OnClickListener() {
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
            showSnackbar(mRecyclerView, "请检查网络设置!", "");
            return;
        }
        if (USER_INFO_SP.getBoolean("STATE", false)) {
            OKCardBean mCardBean = mCardViewAdapter.getLastCardBean();
            if (mCardBean == null) {
                mRefreshLayout.finishLoadMore(1500);
                showSnackbar(mRecyclerView, "数据错误!", "");
                return;
            }
            Map<String, String> map = new HashMap<>();// 请求参数
            map.put("username", USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
            map.put("max_id", Integer.toString(mCardBean.getCARD_ID()));
            map.put("load_type", "USER_CARD");
            if (mOKLoadDynamicApi == null) {
                mOKLoadDynamicApi = new OKLoadDynamicApi(getActivity(), true);
            }
            mOKLoadDynamicApi.requestCardBeanList(map, true, this);
        } else {
            mRefreshLayout.finishLoadMore(1500);
            showSnackbar(mRecyclerView, "登录后加载", "");
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        if (!OKNetUtil.isNet(getActivity())) {
            if (USER_INFO_SP.getBoolean("STATE", false) && mRecyclerView.getAdapter().getItemCount() == 0) {
                mCardBeanList.clear();
                mCardBeanList.addAll(OKConstant.getListCache(INTERFACE_DYNAMIC));
                mRecyclerView.getAdapter().notifyDataSetChanged();
            }
            mRefreshLayout.finishRefresh(1500);
            showSnackbar(mRecyclerView, "请检查网络设置!", "");
            return;
        }
        if (USER_INFO_SP.getBoolean("STATE", false)) {
            Map<String, String> map = new HashMap<>();
            map.put("username", USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
            map.put("num", OKConstant.DYNAMIC_LOAD_COUNT);
            if (mOKLoadDynamicApi == null) {
                mOKLoadDynamicApi = new OKLoadDynamicApi(getActivity(), false);
            }
            mOKLoadDynamicApi.requestCardBeanList(map, false, this);
        } else {
            mRefreshLayout.finishRefresh(1500);
            showSnackbar(mRecyclerView, "登录后查看", "");
        }
    }

    @Override
    public void dynamicApiComplete(List<OKCardBean> list) {
        if (list != null) {
            if (mRefreshLayout.getState() == RefreshState.Refreshing) {
                mCardBeanList.clear();
                mCardBeanList.addAll(list);
            } else if (mRefreshLayout.getState() == RefreshState.Loading) {
                mCardBeanList.addAll(list);
            }
            OKConstant.putListCache(INTERFACE_DYNAMIC, mCardBeanList);
            mRecyclerView.getAdapter().notifyDataSetChanged();
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

        private void initViews(final CardViewHolder mCardViewHolder, final OKCardBean okCardBean, final int position) {
            mCardViewHolder.setListPosition(position);
            if (okCardBean.getUSER_NAME().equals(USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""))) {
                mCardViewHolder.mImageViewDelete.setVisibility(View.VISIBLE);
            } else {
                mCardViewHolder.mImageViewDelete.setVisibility(View.GONE);
            }
            GlideRoundApi(mCardViewHolder.mImageViewAvatar, okCardBean.getTITLE_IMAGE_URL(), R.drawable.touxian_placeholder, R.drawable.touxian_placeholder);
            mCardViewHolder.mTextViewTitle.setText(okCardBean.getTITLE_TEXT());
            mCardViewHolder.mTextViewDate.setText(formatTime(okCardBean.getCREATE_DATE()) + " 发表");
            String cardType = okCardBean.getCARD_TYPE();
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
                        bundle.putInt(INTENT_KEY_INTERFACE_TYPE, INTERFACE_DYNAMIC);
                        bundle.putInt(INTENT_KEY_LIST_POSITION, position);
                        bundle.putInt(INTENT_KEY_LIST_CARD_ID, okCardBean.getCARD_ID());
                        startUserActivity(bundle, OKCardTWActivity.class);
                    } else if (okCardBean.getCARD_TYPE().equals(CARD_TYPE_TP)) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(INTENT_KEY_INTERFACE_TYPE, INTERFACE_DYNAMIC);
                        bundle.putInt(INTENT_KEY_LIST_POSITION, position);
                        bundle.putInt(INTENT_KEY_LIST_CARD_ID, okCardBean.getCARD_ID());
                        startUserActivity(bundle, OKCardTPActivity.class);
                    } else if (okCardBean.getCARD_TYPE().equals(CARD_TYPE_WZ)) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(INTENT_KEY_INTERFACE_TYPE, INTERFACE_DYNAMIC);
                        bundle.putInt(INTENT_KEY_LIST_POSITION, position);
                        bundle.putInt(INTENT_KEY_LIST_CARD_ID, okCardBean.getCARD_ID());
                        startUserActivity(bundle, OKCardWZActivity.class);
                    }
                }
            });

            mCardViewHolder.mImageViewDelete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!okCardBean.getUSER_NAME().equals(USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""))) {
                        showSnackbar(v, "这不是您的卡片", "");
                        return;
                    }
                    showAlertDialog("动态", "是否删除该条动态 ?", "确定", "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            if (USER_INFO_SP.getBoolean("STATE", false)) {
                                Map<String, String> param = new HashMap<String, String>();// 请求参数
                                param.put("username_main", USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
                                param.put("card_id", Integer.toString(okCardBean.getCARD_ID()));
                                param.put("type", "CANCEL_DYNAMIC");
                                mCardTask = new CardTask(mCardViewHolder, position);
                                mCardTask.executeOnExecutor(exec, param); // 并行执行线程
                            } else {
                                startUserActivity(null, OKLoginActivity.class);
                            }
                        }
                    });
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

        public void removeCardBean(int i) {
            if (i >= mBeanList.size()) {
                return;
            }
            mBeanList.remove(i);
            OKConstant.removeListCache(INTERFACE_DYNAMIC, i);
            mRecyclerView.getAdapter().notifyDataSetChanged();
        }

        public OKCardBean getLastCardBean() {
            if (mBeanList != null && mBeanList.size() != 0) {
                return mBeanList.get(mBeanList.size() - 1);
            } else {
                return null;
            }
        }

        public void cancelTask() {
            if (mCardTask != null && mCardTask.getStatus() == AsyncTask.Status.RUNNING) {
                mCardTask.cancel(true); // 如果线程已经在执行则取消执行
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
                OKBusinessApi mWebApi = new OKBusinessApi();
                return mWebApi.RemoveCard(params[0]);
            }
        }
    }
}
