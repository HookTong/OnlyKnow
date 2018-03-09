package com.onlyknow.app.ui.activity;

import android.app.ActionBar.LayoutParams;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
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
import com.onlyknow.app.api.OKLoadCommentReplyApi;
import com.onlyknow.app.database.bean.OKCommentBean;
import com.onlyknow.app.database.bean.OKCommentReplyBean;
import com.onlyknow.app.database.bean.OKUserInfoBean;
import com.onlyknow.app.net.OKBusinessNet;
import com.onlyknow.app.ui.OKBaseActivity;
import com.onlyknow.app.ui.view.OKCircleImageView;
import com.onlyknow.app.ui.view.OKRecyclerView;
import com.onlyknow.app.ui.view.OKSEImageView;
import com.onlyknow.app.utils.OKNetUtil;
import com.scwang.smartrefresh.header.TaurusHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OKCommentReplyActivity extends OKBaseActivity implements OnRefreshListener, OnLoadMoreListener, OKLoadCommentReplyApi.onCallBack {
    private RefreshLayout mRefreshLayout;
    private OKRecyclerView mOKRecyclerView;
    private CommentReplyCardViewAdapter mCommentReplyCardViewAdapter;
    private OKCircleImageView imageViewTX;
    private TextView textViewBT, textViewNR, textViewDate;
    private EditText editTextMsg;
    private OKSEImageView sendButtonMsg;

    private OKLoadCommentReplyApi mOKLoadCommentReplyApi;
    private List<OKCommentReplyBean> mCommentReplyBeanList = new ArrayList<>();
    private SendCommentReplyTask mSendCommentReplyTask;

    private OKCommentBean mCommentBean;// 父评论

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ok_activity_comment_reply);
        initUserInfoSharedPreferences();
        initSystemBar(this);

        mCommentBean = OKCommentBean.fromBundle(getIntent().getExtras());
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
        if (mSendCommentReplyTask != null && mSendCommentReplyTask.getStatus() == AsyncTask.Status.RUNNING) {
            mSendCommentReplyTask.cancel(true); // 如果线程已经在执行则取消执行
        }
        if (mCommentReplyCardViewAdapter != null) {
            mCommentReplyCardViewAdapter.cancelTask();
        }
    }

    private void findView() {
        super.findCommonToolbarView(this);
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
        GlideRoundApi(imageViewTX, mCommentBean.getHEAD_PORTRAIT_URL(), R.drawable.touxian_placeholder, R.drawable.touxian_placeholder);

        textViewBT.setText(mCommentBean.getNICKNAME());
        textViewNR.setText(mCommentBean.getCOMMENT_CONTENT());
        textViewDate.setText(formatTime(mCommentBean.getDATE()));

        imageViewTX.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(OKUserInfoBean.KEY_USERNAME, mCommentBean.getUSER_NAME());
                bundle.putString(OKUserInfoBean.KEY_NICKNAME, mCommentBean.getNICKNAME());
                startUserActivity(bundle, OKHomePageActivity.class);
            }
        });

        sendButtonMsg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String MESSAGE = editTextMsg.getText().toString();
                if (!TextUtils.isEmpty(MESSAGE)) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd/HH/mm");
                    String date = dateFormat.format(new Date());

                    mToolBarProgressBar.setVisibility(View.VISIBLE);

                    mSendCommentReplyTask = new SendCommentReplyTask();
                    Map<String, String> map = new HashMap<>();// 请求参数
                    map.put("username", USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
                    map.put("username2", "");
                    map.put("card_id", Integer.toString(mCommentBean.getCOM_ID()));
                    map.put("message", MESSAGE);
                    map.put("date", date);
                    map.put("type", "ADD_PINLUN_REPLY");
                    mSendCommentReplyTask.executeOnExecutor(exec, map);
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

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        OKCommentReplyBean mCommentReplyBean = mCommentReplyCardViewAdapter.getLastCommentReplyBean();
        if (mCommentReplyBean == null) {
            mRefreshLayout.finishLoadMore(1500);
            return;
        }
        Map<String, String> map = new HashMap<>();// 请求参数,历史界面无需请求参数,直接获取数据库数据的
        map.put("username", USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
        map.put("tag", "" + mCommentBean.getCOM_ID());
        map.put("max_id", "" + mCommentReplyBean.getCOMR_ID());
        map.put("load_type", "COMMENT_REPLY_ENTRY");
        if (mOKLoadCommentReplyApi == null) {
            mOKLoadCommentReplyApi = new OKLoadCommentReplyApi(OKCommentReplyActivity.this, true);
        }
        mOKLoadCommentReplyApi.requestCardBeanList(map, true, this);
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        if (OKNetUtil.isNet(this)) {
            Map<String, String> map = new HashMap<>();// 请求参数
            map.put("com_id", "" + mCommentBean.getCOM_ID());
            map.put("num", OKConstant.COMMENT_REPLY_LOAD_COUNT);
            map.put("username", USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
            if (mOKLoadCommentReplyApi == null) {
                mOKLoadCommentReplyApi = new OKLoadCommentReplyApi(this, false);
            }
            mOKLoadCommentReplyApi.requestCardBeanList(map, false, this);
        } else {
            mRefreshLayout.finishRefresh(1500);
            showSnackBar(mOKRecyclerView, "没有网络连接!", "");
        }
    }

    @Override
    public void commentReplyApiComplete(List<OKCommentReplyBean> list) {
        if (list != null) {
            if (mRefreshLayout.getState() == RefreshState.Refreshing) {
                mCommentReplyBeanList.clear();
                mCommentReplyBeanList.addAll(list);
            } else if (mRefreshLayout.getState() == RefreshState.Loading) {
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

    private class CommentReplyCardViewAdapter extends RecyclerView.Adapter<CommentReplyCardViewAdapter.CommentReplyViewHolder> {
        private Context mContext;
        private List<OKCommentReplyBean> mBeanList;
        private CommentReplyTask mCommentReplyTask;

        public CommentReplyCardViewAdapter(Context context, List<OKCommentReplyBean> list) {
            this.mContext = context;
            this.mBeanList = list;
        }

        private void initViews(final CommentReplyViewHolder mViewHolder, final OKCommentReplyBean bean, final int position) {
            mViewHolder.setListPosition(position);
            GlideRoundApi(mViewHolder.mImageViewTitle, bean.getHEAD_PORTRAIT_URL(), R.drawable.touxian_placeholder, R.drawable.touxian_placeholder);
            mViewHolder.mTextViewTitle.setText(bean.getNICKNAME());
            mViewHolder.mTextViewContent.setText(bean.getCOMMENT_CONTENT());
            mViewHolder.mTextViewZ.setText("" + bean.getZAN_NUM());
            mViewHolder.mTextViewDate.setText(formatTime(bean.getDATE()));
            if (bean.IS_ZAN()) {
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
                    bundleHp.putString(OKUserInfoBean.KEY_USERNAME, bean.getUSER_NAME());
                    bundleHp.putString(OKUserInfoBean.KEY_NICKNAME, bean.getNICKNAME());
                    startUserActivity(bundleHp, OKHomePageActivity.class);
                }
            });

            mViewHolder.mImageViewZ.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!USER_INFO_SP.getBoolean("STATE", false)) {
                        startUserActivity(null, OKLoginActivity.class);
                        return;
                    }

                    if (!bean.IS_ZAN()) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd/HH/mm");
                        String date = dateFormat.format(new Date());

                        Map<String, String> param = new HashMap<String, String>();// 请求参数
                        param.put("id", Integer.toString(bean.getCOMR_ID()));
                        param.put("username", USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
                        param.put("type", "COMMENT_REPLY_EDIT");
                        param.put("date", date);
                        mCommentReplyTask = new CommentReplyTask(mViewHolder, position);
                        mCommentReplyTask.executeOnExecutor(exec, param); // 并行执行线程
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
                            bundle.putString(OKUserInfoBean.KEY_USERNAME, mCommentReplyBeanHome.getUSER_NAME());
                            bundle.putString(OKUserInfoBean.KEY_NICKNAME, mCommentReplyBeanHome.getNICKNAME());
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
                            cm.setText(mCommentReplyBeanClip.getCOMMENT_CONTENT());
                            showSnackBar(mOKRecyclerView, "信息已复制到剪切板", "");
                            popWindow.dismiss();
                            break;
                        case R.id.COMMENT_REPLY_POP_JuBao_LAYOU:
                            OKCommentReplyBean mCommentReplyBeanRePort = getCommentReplyBean(pos);
                            if (mCommentReplyBeanRePort == null) {
                                return;
                            }

                            Bundle bundle3 = new Bundle();
                            bundle3.putString("JUBAO_TYPE", "COMMENT_REPLY");
                            bundle3.putString("JUBAO_COMR_ID", Integer.toString(mCommentReplyBeanRePort.getCOMR_ID()));
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

        public OKCommentReplyBean getLastCommentReplyBean() {
            if (mBeanList != null && mBeanList.size() != 0) {
                return mBeanList.get(mBeanList.size() - 1);
            } else {
                return null;
            }
        }

        public void cancelTask() {
            if (mCommentReplyTask != null && mCommentReplyTask.getStatus() == AsyncTask.Status.RUNNING) {
                mCommentReplyTask.cancel(true); // 如果线程已经在执行则取消执行
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

        class CommentReplyTask extends AsyncTask<Map<String, String>, Void, Boolean> {
            private CommentReplyViewHolder mCommentReplyViewHolder;
            private int mPosition;

            public CommentReplyTask(CommentReplyViewHolder viewHolder, int pos) {
                mCommentReplyViewHolder = viewHolder;
                mPosition = pos;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (isCancelled() || (mCommentReplyViewHolder.getListPosition() != mPosition)) {
                    return;
                }
                if (aBoolean) {
                    OKCommentReplyBean mOKCommentReplyBean = getCommentReplyBean(mPosition);
                    if (mOKCommentReplyBean == null) {
                        return;
                    }

                    mOKCommentReplyBean.setZAN_NUM(mOKCommentReplyBean.getZAN_NUM() + 1);
                    mOKCommentReplyBean.setIS_ZAN(true);
                    mBeanList.set(mPosition, mOKCommentReplyBean);

                    GlideApi(mCommentReplyViewHolder.mImageViewZ, R.drawable.comment_red_zan, R.drawable.comment_red_zan, R.drawable.comment_red_zan);
                    mCommentReplyViewHolder.mTextViewZ.setText("" + mOKCommentReplyBean.getZAN_NUM());
                    mCommentReplyViewHolder.mTextViewZ.setTextColor(getResources().getColor(R.color.fenhon));
                } else {
                    showSnackBar(mCommentReplyViewHolder.mCardView, "服务器错误,请稍后重试!", "ErrorCode: " + OKConstant.COMMENT_ERROR);
                }
            }

            @Override
            protected Boolean doInBackground(Map<String, String>... params) {
                if (isCancelled()) {
                    return false;
                }

                OKBusinessNet mWebApi = new OKBusinessNet();
                return mWebApi.editCommentPraise(params[0]);
            }
        }
    }

    private class SendCommentReplyTask extends AsyncTask<Map<String, String>, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Map<String, String>... params) {
            if (isCancelled()) {
                return false;
            }

            return new OKBusinessNet().updateCardInfo(params[0]);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (isCancelled()) {
                return;
            }

            mToolBarProgressBar.setVisibility(View.GONE);

            if (aBoolean) {
                editTextMsg.setText("");
                if (mCommentReplyBeanList.size() == 0) {
                    mRefreshLayout.autoRefresh();
                } else {
                    mRefreshLayout.autoLoadMore();
                }
                showSnackBar(mOKRecyclerView, "发送成功", "");
            } else {
                showSnackBar(mOKRecyclerView, "发送失败", "ErrorCode :" + OKConstant.COMMENT_ERROR);
            }
        }
    }
}
