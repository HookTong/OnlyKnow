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
import com.onlyknow.app.api.comment.OKLoadCommentReplyApi;
import com.onlyknow.app.api.comment.OKManagerCommentApi;
import com.onlyknow.app.db.bean.OKCommentBean;
import com.onlyknow.app.db.bean.OKCommentReplyBean;
import com.onlyknow.app.db.bean.OKUserInfoBean;
import com.onlyknow.app.ui.OKBaseActivity;
import com.onlyknow.app.ui.view.OKCircleImageView;
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

public class OKCommentReplyActivity extends OKBaseActivity implements OnRefreshListener, OnLoadMoreListener, OKLoadCommentReplyApi.onCallBack, OKAddCommentApi.onCallBack {
    private RefreshLayout mRefreshLayout;
    private OKRecyclerView mOKRecyclerView;
    private CommentReplyCardViewAdapter mCommentReplyCardViewAdapter;
    private OKCircleImageView imageViewTX;
    private TextView textViewBT, textViewNR, textViewDate;
    private EditText editTextMsg;
    private OKSEImageView sendButtonMsg;

    private OKLoadCommentReplyApi mOKLoadCommentReplyApi;
    private List<OKCommentReplyBean> mCommentReplyBeanList = new ArrayList<>();
    private OKAddCommentApi mOKAddCommentApi;

    public final static String KEY_BUNDLE = "OKCommentReplyActivity";
    private OKCommentBean mCommentBean;// 父评论

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ok_activity_comment_reply);
        initUserBody();
        initStatusBar();

        mCommentBean = (OKCommentBean) getIntent().getExtras().getSerializable(KEY_BUNDLE);
        if (mCommentBean == null) {
            showSnackBar(mOKRecyclerView, "父评论为空", "ErrorCode: " + OKConstant.COMMENT_ERROR);
            finish();
        }

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
        if (mOKLoadCommentReplyApi != null) {
            mOKLoadCommentReplyApi.cancelTask();
        }
        if (mOKAddCommentApi != null) {
            mOKAddCommentApi.cancelTask(); // 如果线程已经在执行则取消执行
        }
        if (mCommentReplyCardViewAdapter != null) {
            mCommentReplyCardViewAdapter.cancelTask();
        }
    }

    private void findView() {
        super.findCommonToolbarView();
        setSupportActionBar(mToolbar);

        mToolbarBack.setVisibility(View.VISIBLE);
        mToolbarTitle.setVisibility(View.VISIBLE);

        mToolbarTitle.setText("评论回复");

        mRefreshLayout = (RefreshLayout) findViewById(R.id.pinlun_reply_SwipeRefresh);
        mOKRecyclerView = (OKRecyclerView) findViewById(R.id.pinlun_reply_Nolistview);

        sendButtonMsg = (OKSEImageView) findViewById(R.id.pinlun_reply_send_but);
        imageViewTX = (OKCircleImageView) findViewById(R.id.pinlun_reply_biaoti_imag);
        textViewBT = (TextView) findViewById(R.id.pinlun_reply_biaoti_text);
        textViewNR = (TextView) findViewById(R.id.pinlun_reply_neiron_text);
        textViewDate = (TextView) findViewById(R.id.pinlun_reply_date_text);
        editTextMsg = (EditText) findViewById(R.id.pinlun_reply_input_message);

        mOKRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRefreshLayout.setRefreshHeader(new TaurusHeader(this));
        mRefreshLayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale));
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setOnLoadMoreListener(this);
    }

    private void init() {
        mCommentReplyCardViewAdapter = new CommentReplyCardViewAdapter(this, mCommentReplyBeanList);
        mOKRecyclerView.setAdapter(mCommentReplyCardViewAdapter);

        // 获取标题图片信息
        GlideRoundApi(imageViewTX, mCommentBean.getAvatar(), R.drawable.touxian_placeholder, R.drawable.touxian_placeholder);

        textViewBT.setText(mCommentBean.getNickName());
        textViewNR.setText(mCommentBean.getMessage());
        textViewDate.setText(OKDateUtil.formatTime(mCommentBean.getComDate()));

        imageViewTX.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(OKUserInfoBean.KEY_USERNAME, mCommentBean.getUserName());
                bundle.putString(OKUserInfoBean.KEY_NICKNAME, mCommentBean.getNickName());
                startUserActivity(bundle, OKHomePageActivity.class);
            }
        });

        sendButtonMsg.setOnClickListener(new OnClickListener() {

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
                    params.setUsername(USER_BODY.getString(OKUserInfoBean.KEY_USERNAME, ""));
                    params.setId(mCommentBean.getComId());
                    params.setType(OKAddCommentApi.Params.TYPE_COMMENT_REPLY);
                    params.setMessage(Msg);

                    if (mOKAddCommentApi != null) {
                        mOKAddCommentApi.cancelTask();
                    }
                    mOKAddCommentApi = new OKAddCommentApi(OKCommentReplyActivity.this);
                    mOKAddCommentApi.requestAddComment(params, OKCommentReplyActivity.this);
                } else {
                    showSnackBar(v, "请输入要发送的评论!", "");
                }
            }
        });

        mToolbarBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                OKCommentReplyActivity.this.finish();
            }
        });

        mOKRecyclerView.setEmptyView(initCollapsingEmptyView(new OnClickListener() {
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

        OKLoadCommentReplyApi.Params params = new OKLoadCommentReplyApi.Params();
        params.setId(mCommentBean.getComId());
        params.setUsername(USER_BODY.getString(OKUserInfoBean.KEY_USERNAME, ""));
        params.setType(OKLoadCommentReplyApi.Params.TYPE_COMMENT_REPLY);
        params.setPage(page + 1);
        params.setSize(size);

        if (mOKLoadCommentReplyApi == null) {
            mOKLoadCommentReplyApi = new OKLoadCommentReplyApi(this);
        }
        mOKLoadCommentReplyApi.requestCommentReply(params, this);
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        if (OKNetUtil.isNet(this)) {

            OKLoadCommentReplyApi.Params params = new OKLoadCommentReplyApi.Params();
            params.setId(mCommentBean.getComId());
            params.setUsername(USER_BODY.getString(OKUserInfoBean.KEY_USERNAME, ""));
            params.setType(OKLoadCommentReplyApi.Params.TYPE_COMMENT_REPLY);
            params.setPage(1);
            params.setSize(size);

            if (mOKLoadCommentReplyApi == null) {
                mOKLoadCommentReplyApi = new OKLoadCommentReplyApi(this);
            }
            mOKLoadCommentReplyApi.requestCommentReply(params, this);

        } else {
            mRefreshLayout.finishRefresh(1500);
            showSnackBar(mOKRecyclerView, "没有网络连接!", "");
        }
    }

    @Override
    public void loadCommentReplyComplete(List<OKCommentReplyBean> list) {
        if (list != null) {
            if (mRefreshLayout.getState() == RefreshState.Refreshing) {
                page = 1;

                mCommentReplyBeanList.clear();
                mCommentReplyBeanList.addAll(list);
            } else if (mRefreshLayout.getState() == RefreshState.Loading) {
                page++;

                mCommentReplyBeanList.addAll(list);
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
            if (mCommentReplyBeanList.size() < size) {
                mRefreshLayout.autoRefresh();
            } else {
                mRefreshLayout.autoLoadMore();
            }
            showSnackBar(mOKRecyclerView, "发送成功", "");
        } else {
            showSnackBar(mOKRecyclerView, "发送失败", "ErrorCode :" + OKConstant.COMMENT_ERROR);
        }
    }

    private class CommentReplyCardViewAdapter extends RecyclerView.Adapter<CommentReplyCardViewAdapter.CommentReplyViewHolder> implements OKManagerCommentApi.onCallBack {
        private Context mContext;
        private List<OKCommentReplyBean> mBeanList;
        private OKManagerCommentApi mOKManagerCommentApi;
        private CommentReplyViewHolder viewHolder;

        public CommentReplyCardViewAdapter(Context context, List<OKCommentReplyBean> list) {
            this.mContext = context;
            this.mBeanList = list;
        }

        private void initViews(final CommentReplyViewHolder mViewHolder, final OKCommentReplyBean bean, final int position) {
            mViewHolder.setListPosition(position);
            GlideRoundApi(mViewHolder.mImageViewTitle, bean.getAvatar(), R.drawable.touxian_placeholder, R.drawable.touxian_placeholder);
            mViewHolder.mTextViewTitle.setText(bean.getNickName());
            mViewHolder.mTextViewContent.setText(bean.getMessage());
            mViewHolder.mTextViewZ.setText("" + bean.getComrPraise());
            mViewHolder.mTextViewDate.setText(OKDateUtil.formatTime(bean.getComrDate()));
            if (bean.isPraise()) {
                GlideApi(mViewHolder.mImageViewZ, R.drawable.comment_red_zan, R.drawable.comment_red_zan, R.drawable.comment_red_zan);
                mViewHolder.mTextViewZ.setTextColor(getResources().getColor(R.color.fenhon));
            } else {
                GlideApi(mViewHolder.mImageViewZ, R.drawable.comment_zan, R.drawable.comment_zan, R.drawable.comment_zan);
                mViewHolder.mTextViewZ.setTextColor(getResources().getColor(R.color.md_grey_500));
            }

            mViewHolder.mCardView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopupWindow(position);
                }
            });

            mViewHolder.mImageViewTitle.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundleHp = new Bundle();
                    bundleHp.putString(OKUserInfoBean.KEY_USERNAME, bean.getUserName());
                    bundleHp.putString(OKUserInfoBean.KEY_NICKNAME, bean.getNickName());
                    startUserActivity(bundleHp, OKHomePageActivity.class);
                }
            });

            mViewHolder.mImageViewZ.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!USER_BODY.getBoolean("STATE", false)) {
                        startUserActivity(null, OKLoginActivity.class);
                        return;
                    }

                    if (!bean.isPraise()) {
                        viewHolder = mViewHolder;

                        OKManagerCommentApi.Params params = new OKManagerCommentApi.Params();
                        params.setUsername(USER_BODY.getString(OKUserInfoBean.KEY_USERNAME, ""));
                        params.setPassword(USER_BODY.getString(OKUserInfoBean.KEY_PASSWORD, ""));
                        params.setId(bean.getComrId());
                        params.setType(OKManagerCommentApi.Params.TYPE_PRAISE_COMMENT_REPLY);
                        params.setPos(position);

                        cancelTask();
                        mOKManagerCommentApi = new OKManagerCommentApi(OKCommentReplyActivity.this);
                        mOKManagerCommentApi.requestManagerComment(params, CommentReplyCardViewAdapter.this); // 并行执行线程
                    } else {
                        showSnackBar(v, "您已点过赞了", "");
                    }
                }
            });
        }

        private void showPopupWindow(final int pos) {
            View parent = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
            View popView = View.inflate(mContext, R.layout.ok_menu_comment_reply, null);
            LinearLayout linearLayoutUser = (LinearLayout) popView.findViewById(R.id.COMMENT_REPLY_POP_USERINFO_LAYOU);
            LinearLayout linearLayoutCopy = (LinearLayout) popView.findViewById(R.id.COMMENT_REPLY_POP_COPY_LAYOU);
            LinearLayout linearLayoutRepot = (LinearLayout) popView.findViewById(R.id.COMMENT_REPLY_POP_JuBao_LAYOU);
            Button btnClose = (Button) popView.findViewById(R.id.COMMENT_REPLY_POP_GUANBI_BUT);
            final PopupWindow popWindow = new PopupWindow(popView, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
            popWindow.setAnimationStyle(R.style.AnimBottom);
            popWindow.setFocusable(true);
            popWindow.setOutsideTouchable(true);// 设置允许在外点击消失
            OnClickListener listener = new OnClickListener() {
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.COMMENT_REPLY_POP_GUANBI_BUT:
                            popWindow.dismiss();
                            break;
                        case R.id.COMMENT_REPLY_POP_USERINFO_LAYOU:
                            OKCommentReplyBean mCommentReplyBeanHome = getCommentReplyBean(pos);
                            if (mCommentReplyBeanHome == null) {
                                return;
                            }

                            Bundle bundle = new Bundle();
                            bundle.putString(OKUserInfoBean.KEY_USERNAME, mCommentReplyBeanHome.getUserName());
                            bundle.putString(OKUserInfoBean.KEY_NICKNAME, mCommentReplyBeanHome.getNickName());
                            startUserActivity(bundle, OKHomePageActivity.class);
                            popWindow.dismiss();
                            break;
                        case R.id.COMMENT_REPLY_POP_COPY_LAYOU:
                            OKCommentReplyBean mCommentReplyBeanClip = getCommentReplyBean(pos);
                            if (mCommentReplyBeanClip == null) {
                                return;
                            }

                            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            // 将文本数据复制到剪贴板
                            cm.setText(mCommentReplyBeanClip.getMessage());
                            showSnackBar(mOKRecyclerView, "信息已复制到剪切板", "");
                            popWindow.dismiss();
                            break;
                        case R.id.COMMENT_REPLY_POP_JuBao_LAYOU:
                            OKCommentReplyBean mCommentReplyBeanRePort = getCommentReplyBean(pos);
                            if (mCommentReplyBeanRePort == null) {
                                return;
                            }

                            Bundle bundle3 = new Bundle();
                            bundle3.putString(OKRePortActivity.KEY_TYPE, OKRePortActivity.TYPE_COMMENT_REPLY);
                            bundle3.putString(OKRePortActivity.KEY_ID, Integer.toString(mCommentReplyBeanRePort.getComrId()));
                            startUserActivity(bundle3, OKRePortActivity.class);
                            popWindow.dismiss();
                            break;
                    }
                }
            };
            linearLayoutUser.setOnClickListener(listener);
            linearLayoutCopy.setOnClickListener(listener);
            linearLayoutRepot.setOnClickListener(listener);
            btnClose.setOnClickListener(listener);
            ColorDrawable dw = new ColorDrawable(0x30000000);
            popWindow.setBackgroundDrawable(dw);
            popWindow.showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        }

        public OKCommentReplyBean getCommentReplyBean(int i) {
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
        public CommentReplyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.ok_item_comment, parent, false);
            return new CommentReplyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CommentReplyViewHolder holder, int position) {
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
                OKCommentReplyBean mOKCommentReplyBean = getCommentReplyBean(pos);
                if (mOKCommentReplyBean == null) {
                    return;
                }

                mOKCommentReplyBean.setComrPraise(mOKCommentReplyBean.getComrPraise() + 1);
                mOKCommentReplyBean.setPraise(true);
                mBeanList.set(pos, mOKCommentReplyBean);

                GlideApi(viewHolder.mImageViewZ, R.drawable.comment_red_zan, R.drawable.comment_red_zan, R.drawable.comment_red_zan);
                viewHolder.mTextViewZ.setText("" + mOKCommentReplyBean.getComrPraise());
                viewHolder.mTextViewZ.setTextColor(getResources().getColor(R.color.fenhon));
            } else {
                showSnackBar(viewHolder.mCardView, "操作失败!", "ErrorCode: " + OKConstant.COMMENT_ERROR);
            }
        }

        class CommentReplyViewHolder extends RecyclerView.ViewHolder {
            public CardView mCardView;
            public ImageView mImageViewTitle, mImageViewZ;
            public TextView mTextViewTitle;
            public TextView mTextViewContent, mTextViewDate, mTextViewZ;

            public CommentReplyViewHolder(View itemView) {
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
