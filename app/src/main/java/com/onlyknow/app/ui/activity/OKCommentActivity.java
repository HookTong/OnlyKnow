package com.onlyknow.app.ui.activity;

import android.app.ActionBar.LayoutParams;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.onlyknow.app.OKConstant;
import com.onlyknow.app.R;
import com.onlyknow.app.api.OKServiceResult;
import com.onlyknow.app.api.comment.OKAddCommentApi;
import com.onlyknow.app.api.comment.OKLoadCommentApi;
import com.onlyknow.app.api.comment.OKManagerCommentApi;
import com.onlyknow.app.db.bean.OKCardBean;
import com.onlyknow.app.db.bean.OKCommentBean;
import com.onlyknow.app.db.bean.OKUserInfoBean;
import com.onlyknow.app.ui.OKBaseActivity;
import com.onlyknow.app.ui.view.OKRecyclerView;
import com.onlyknow.app.ui.view.OKSEImageView;
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

public class OKCommentActivity extends OKBaseActivity implements OnRefreshListener, OnLoadMoreListener, OKLoadCommentApi.onCallBack, OKAddCommentApi.onCallBack {
    private RefreshLayout mRefreshLayout;
    private OKRecyclerView mOKRecyclerView;
    private CommentCardViewAdapter mCommentCardViewAdapter;
    private EditText editTextMsg;
    private OKSEImageView sendButton;

    private OKLoadCommentApi mOKLoadCommentApi;
    private List<OKCommentBean> mCommentBeanList = new ArrayList<>();
    private OKAddCommentApi mOKAddCommentApi;

    private int mCardId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ok_activity_comment);
        initUserBody();
        initStatusBar();

        mCardId = getIntent().getExtras().getInt(OKCardBean.KEY_CARD_ID);

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
        if (mOKLoadCommentApi != null) {
            mOKLoadCommentApi.cancelTask();
        }
        if (mOKAddCommentApi != null) {
            mOKAddCommentApi.cancelTask(); // 如果线程已经在执行则取消执行
        }
        if (mCommentCardViewAdapter != null) {
            mCommentCardViewAdapter.cancelTask();
        }
    }

    private void findView() {
        super.findCommonToolbarView();
        setSupportActionBar(mToolbar);

        mToolbarBack.setVisibility(View.VISIBLE);
        mToolbarTitle.setVisibility(View.VISIBLE);

        mToolbarTitle.setText("用户评论");

        editTextMsg = (EditText) findViewById(R.id.pinlun_input_message);
        sendButton = (OKSEImageView) findViewById(R.id.pinlun_send_but);
        mRefreshLayout = (RefreshLayout) findViewById(R.id.pinlun_SwipeRefresh);
        mOKRecyclerView = (OKRecyclerView) findViewById(R.id.pinlun_Nolistview);

        mOKRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRefreshLayout.setRefreshHeader(new TaurusHeader(this));
        mRefreshLayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale));
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setOnLoadMoreListener(this);
    }

    private void init() {
        mCommentCardViewAdapter = new CommentCardViewAdapter(this, mCommentBeanList);
        mOKRecyclerView.setAdapter(mCommentCardViewAdapter);

        sendButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String Msg = editTextMsg.getText().toString();
                if (!TextUtils.isEmpty(Msg)) {
                    if (!USER_BODY.getBoolean("STATE", false)) {
                        startUserActivity(null, OKLoginActivity.class);
                        return;
                    }

                    mToolBarProgressBar.setVisibility(View.VISIBLE);

                    OKAddCommentApi.Params params = new OKAddCommentApi.Params();
                    params.setId(mCardId);
                    params.setUsername(USER_BODY.getString(OKUserInfoBean.KEY_USERNAME, ""));
                    params.setType(OKAddCommentApi.Params.TYPE_COMMENT);
                    params.setMessage(Msg);

                    if (mOKAddCommentApi != null) {
                        mOKAddCommentApi.cancelTask();
                    }
                    mOKAddCommentApi = new OKAddCommentApi(OKCommentActivity.this);
                    mOKAddCommentApi.requestAddComment(params, OKCommentActivity.this);
                } else {
                    showSnackBar(v, "请输入要发送的评论!", "");
                }
            }
        });

        mToolbarBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                OKCommentActivity.this.finish();
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

    int page = 0;
    int size = 30;

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        if (!OKNetUtil.isNet(this)) {
            mRefreshLayout.finishLoadMore(1500);
            showSnackBar(mOKRecyclerView, "没有网络连接!", "");
            return;
        }

        OKLoadCommentApi.Params params = new OKLoadCommentApi.Params();

        params.setId(mCardId);
        params.setUsername(USER_BODY.getString(OKUserInfoBean.KEY_USERNAME, ""));
        params.setPage(page + 1);
        params.setSize(size);

        if (mOKLoadCommentApi == null) {
            mOKLoadCommentApi = new OKLoadCommentApi(this);
        }
        mOKLoadCommentApi.requestComment(params, this);
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        if (OKNetUtil.isNet(this)) {
            OKLoadCommentApi.Params params = new OKLoadCommentApi.Params();

            params.setId(mCardId);
            params.setUsername(USER_BODY.getString(OKUserInfoBean.KEY_USERNAME, ""));
            params.setPage(1);
            params.setSize(size);

            if (mOKLoadCommentApi == null) {
                mOKLoadCommentApi = new OKLoadCommentApi(this);
            }
            mOKLoadCommentApi.requestComment(params, this);
        } else {
            mRefreshLayout.finishRefresh(1500);
            showSnackBar(mOKRecyclerView, "没有网络连接!", "");
        }
    }

    @Override
    public void loadCommentComplete(List<OKCommentBean> list) {
        if (list != null) {
            if (mRefreshLayout.getState() == RefreshState.Refreshing) {
                page = 1;

                mCommentBeanList.clear();
                mCommentBeanList.addAll(list);
            } else if (mRefreshLayout.getState() == RefreshState.Loading) {
                page++;

                mCommentBeanList.addAll(list);
            }
            mOKRecyclerView.getAdapter().notifyDataSetChanged();
            mOKRecyclerView.scrollToPosition(mOKRecyclerView.getAdapter().getItemCount() - 1);
        }

        if (mRefreshLayout.getState() == RefreshState.Refreshing) {
            mRefreshLayout.finishRefresh();
        } else if (mRefreshLayout.getState() == RefreshState.Loading) {
            mRefreshLayout.finishLoadMore();
        }
    }

    @Override
    public void addCommentComplete(OKServiceResult<Object> result, String type) {
        mToolBarProgressBar.setVisibility(View.GONE);
        if (result != null && result.isSuccess()) {
            editTextMsg.setText("");
            if (mCommentBeanList.size() < size) {
                mRefreshLayout.autoRefresh();
            } else {
                mRefreshLayout.autoLoadMore();
            }
            showSnackBar(mOKRecyclerView, "发送成功", "");
        } else {
            showSnackBar(mOKRecyclerView, "发送失败", "ErrorCode :" + OKConstant.COMMENT_ERROR);
        }
    }

    private class CommentCardViewAdapter extends RecyclerView.Adapter<CommentCardViewAdapter.CommentViewHolder> implements OKManagerCommentApi.onCallBack {
        private Context mContext;
        private List<OKCommentBean> mBeanList;
        private OKManagerCommentApi mOKManagerCommentApi;
        private CommentViewHolder viewHolder;

        public CommentCardViewAdapter(Context context, List<OKCommentBean> list) {
            this.mContext = context;
            this.mBeanList = list;
        }

        private void initViews(final CommentViewHolder mCommentViewHolder, final OKCommentBean bean, final int position) {
            mCommentViewHolder.setListPosition(position);

            GlideRoundApi(mCommentViewHolder.mImageViewTitle, bean.getAvatar(), R.drawable.touxian_placeholder, R.drawable.touxian_placeholder);
            mCommentViewHolder.mTextViewTitle.setText(bean.getNickName());
            mCommentViewHolder.mTextViewContent.setText(bean.getMessage());
            mCommentViewHolder.mTextViewZ.setText("" + bean.getComPraise());
            mCommentViewHolder.mTextViewDate.setText(OKDateUtil.formatTime(bean.getComDate()));
            if (bean.isPraise()) {
                GlideApi(mCommentViewHolder.mImageViewZ, R.drawable.comment_red_zan, R.drawable.comment_red_zan, R.drawable.comment_red_zan);
                mCommentViewHolder.mTextViewZ.setTextColor(getResources().getColor(R.color.fenhon));
            } else {
                GlideApi(mCommentViewHolder.mImageViewZ, R.drawable.comment_zan, R.drawable.comment_zan, R.drawable.comment_zan);
                mCommentViewHolder.mTextViewZ.setTextColor(getResources().getColor(R.color.md_grey_500));
            }

            mCommentViewHolder.mCardView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopupWindow(position);
                }
            });

            mCommentViewHolder.mImageViewTitle.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String USER_NAME = bean.getUserName();
                    String USER_NICKNAME = bean.getNickName();

                    Bundle bundleHp = new Bundle();
                    bundleHp.putString(OKUserInfoBean.KEY_USERNAME, USER_NAME);
                    bundleHp.putString(OKUserInfoBean.KEY_NICKNAME, USER_NICKNAME);
                    startUserActivity(bundleHp, OKHomePageActivity.class);
                }
            });

            mCommentViewHolder.mImageViewZ.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!USER_BODY.getBoolean("STATE", false)) {
                        startUserActivity(null, OKLoginActivity.class);
                        return;
                    }

                    if (!bean.isPraise()) {
                        viewHolder = mCommentViewHolder;

                        OKManagerCommentApi.Params params = new OKManagerCommentApi.Params();
                        params.setId(bean.getComId());
                        params.setUsername(USER_BODY.getString(OKUserInfoBean.KEY_USERNAME, ""));
                        params.setPassword(USER_BODY.getString(OKUserInfoBean.KEY_PASSWORD, ""));
                        params.setType(OKManagerCommentApi.Params.TYPE_PRAISE_COMMENT);
                        params.setPos(position);

                        cancelTask();
                        mOKManagerCommentApi = new OKManagerCommentApi(OKCommentActivity.this);
                        mOKManagerCommentApi.requestManagerComment(params, CommentCardViewAdapter.this); // 并行执行线程
                    } else {
                        showSnackBar(v, "您已点过赞了", "");
                    }
                }
            });
        }

        private void showPopupWindow(final int pos) {
            View parent = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
            View popView = View.inflate(OKCommentActivity.this, R.layout.ok_menu_comment, null);
            LinearLayout linearLayoutUser = (LinearLayout) popView.findViewById(R.id.COMMENT_POP_USERINFO_LAYOU);
            LinearLayout linearLayoutList = (LinearLayout) popView.findViewById(R.id.COMMENT_POP_LIST_LAYOU);
            LinearLayout linearLayoutCopy = (LinearLayout) popView.findViewById(R.id.COMMENT_POP_COPY_LAYOU);
            LinearLayout linearLayoutRepot = (LinearLayout) popView.findViewById(R.id.COMMENT_POP_JuBao_LAYOU);
            Button btnClose = (Button) popView.findViewById(R.id.COMMENT_POP_GUANBI_BUT);
            final PopupWindow popWindow = new PopupWindow(popView, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
            popWindow.setAnimationStyle(R.style.AnimBottom);
            popWindow.setFocusable(true);
            popWindow.setOutsideTouchable(true);// 设置允许在外点击消失
            OnClickListener listener = new OnClickListener() {
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.COMMENT_POP_GUANBI_BUT:
                            popWindow.dismiss();
                            break;
                        case R.id.COMMENT_POP_USERINFO_LAYOU:
                            OKCommentBean mCommentBeanHome = getCommentBean(pos);
                            if (mCommentBeanHome == null) {
                                return;
                            }

                            String USER_NAME = mCommentBeanHome.getUserName();
                            String USER_NICKNAME = mCommentBeanHome.getNickName();

                            Bundle bundleHp = new Bundle();
                            bundleHp.putString(OKUserInfoBean.KEY_USERNAME, USER_NAME);
                            bundleHp.putString(OKUserInfoBean.KEY_NICKNAME, USER_NICKNAME);
                            startUserActivity(bundleHp, OKHomePageActivity.class);
                            popWindow.dismiss();
                            break;
                        case R.id.COMMENT_POP_LIST_LAYOU:
                            OKCommentBean mCommentBean = getCommentBean(pos);
                            if (mCommentBean == null) {
                                return;
                            }

                            Bundle bundle = new Bundle();
                            bundle.putSerializable(OKCommentReplyActivity.KEY_BUNDLE, mCommentBean);
                            startUserActivity(bundle, OKCommentReplyActivity.class);
                            popWindow.dismiss();
                            break;
                        case R.id.COMMENT_POP_COPY_LAYOU:
                            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            OKCommentBean mCommentBeanClip = getCommentBean(pos);
                            if (mCommentBeanClip == null) {
                                return;
                            }

                            cm.setText(mCommentBeanClip.getMessage());
                            showSnackBar(mOKRecyclerView, "文本已复制到剪切板", "");
                            popWindow.dismiss();
                            break;
                        case R.id.COMMENT_POP_JuBao_LAYOU:
                            OKCommentBean mCommentBeanRePort = getCommentBean(pos);
                            if (mCommentBeanRePort == null) {
                                return;
                            }

                            Bundle bundle3 = new Bundle();
                            bundle3.putString(OKRePortActivity.KEY_TYPE, OKRePortActivity.TYPE_COMMENT);
                            bundle3.putString(OKRePortActivity.KEY_ID, Integer.toString(mCommentBeanRePort.getComId()));
                            startUserActivity(bundle3, OKRePortActivity.class);
                            break;
                    }
                }
            };
            linearLayoutUser.setOnClickListener(listener);
            linearLayoutList.setOnClickListener(listener);
            linearLayoutCopy.setOnClickListener(listener);
            linearLayoutRepot.setOnClickListener(listener);
            btnClose.setOnClickListener(listener);
            ColorDrawable dw = new ColorDrawable(0x30000000);
            popWindow.setBackgroundDrawable(dw);
            popWindow.showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        }

        public OKCommentBean getCommentBean(int i) {
            if (mBeanList != null && mBeanList.size() != 0 && i <= mBeanList.size()) {
                return mBeanList.get(i);
            } else {
                return null;
            }
        }

        public void cancelTask() {
            if (mOKManagerCommentApi != null) {
                mOKManagerCommentApi.cancelTask(); // 如果线程已经在执行则取消执行
            }
        }

        @Override
        public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.ok_item_comment, parent, false);
            return new CommentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CommentViewHolder holder, int position) {
            initViews(holder, mBeanList.get(position), position);
        }

        @Override
        public int getItemCount() {
            return mBeanList.size();
        }

        @Override
        public void managerCommentComplete(OKServiceResult<Object> result, String type, int pos) {
            if (viewHolder == null || viewHolder.getListPosition() != pos) return;

            if (result != null && result.isSuccess()) {
                OKCommentBean mCommentBean = getCommentBean(pos);
                if (mCommentBean == null) {
                    return;
                }

                mCommentBean.setComPraise(mCommentBean.getComPraise() + 1);
                mCommentBean.setPraise(true);
                mBeanList.set(pos, mCommentBean);

                GlideApi(viewHolder.mImageViewZ, R.drawable.comment_red_zan, R.drawable.comment_red_zan, R.drawable.comment_red_zan);
                viewHolder.mTextViewZ.setText("" + mCommentBean.getComPraise());
                viewHolder.mTextViewZ.setTextColor(getResources().getColor(R.color.fenhon));
            } else {
                showSnackBar(viewHolder.mCardView, "服务器错误,请稍后重试!", "ErrorCode: " + OKConstant.GOODS_BUY_ERROR);
            }
        }

        class CommentViewHolder extends RecyclerView.ViewHolder {
            public CardView mCardView;
            public ImageView mImageViewTitle, mImageViewZ;
            public TextView mTextViewTitle;
            public TextView mTextViewContent, mTextViewDate, mTextViewZ;

            public CommentViewHolder(View itemView) {
                super(itemView);
                mCardView = itemView.findViewById(R.id.COMMENT_cardView);
                mImageViewTitle = itemView.findViewById(R.id.COMMENT_biaoti_imag);
                mImageViewZ = itemView.findViewById(R.id.COMMENT_zan_imag);
                mTextViewTitle = itemView.findViewById(R.id.COMMENT_biaoti_text);
                mTextViewContent = itemView.findViewById(R.id.COMMENT_neiron_text);
                mTextViewZ = itemView.findViewById(R.id.COMMENT_zansum_text);
                mTextViewDate = itemView.findViewById(R.id.COMMENT_date_text);
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
