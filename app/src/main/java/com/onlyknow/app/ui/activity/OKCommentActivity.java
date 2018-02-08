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
import com.onlyknow.app.api.OKBusinessApi;
import com.onlyknow.app.api.OKLoadCommentApi;
import com.onlyknow.app.database.bean.OKCardBean;
import com.onlyknow.app.database.bean.OKCommentBean;
import com.onlyknow.app.database.bean.OKUserInfoBean;
import com.onlyknow.app.ui.OKBaseActivity;
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

public class OKCommentActivity extends OKBaseActivity implements OnRefreshListener, OnLoadMoreListener {
    private RefreshLayout mRefreshLayout;
    private OKRecyclerView mOKRecyclerView;
    private CommentCardViewAdapter mCommentCardViewAdapter;

    private EditText editTextMsg;
    private OKSEImageView sendButton;

    private OKLoadCommentApi mOKLoadCommentApi;
    private List<OKCommentBean> mCommentBeanList = new ArrayList<>();

    private SendCommentTask mSendCommentTask;

    private String UserName;
    private int CardId;

    private OKLoadCommentApi.onCallBack mOnCallBack = new OKLoadCommentApi.onCallBack() {
        @Override
        public void cardList(List<OKCommentBean> list) {
            if (list != null) {
                if (mRefreshLayout.getState() == RefreshState.Refreshing) {
                    mCommentBeanList.clear();
                    mCommentBeanList.addAll(list);
                } else if (mRefreshLayout.getState() == RefreshState.Loading) {
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
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ok_activity_comment);
        initUserInfoSharedPreferences();
        initSystemBar(this);

        UserName = getIntent().getExtras().getString(OKUserInfoBean.KEY_USERNAME);
        CardId = getIntent().getExtras().getInt(OKCardBean.KEY_CARD_ID);

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
        if (mSendCommentTask != null && mSendCommentTask.getStatus() == AsyncTask.Status.RUNNING) {
            mSendCommentTask.cancel(true); // 如果线程已经在执行则取消执行
        }
        if (mCommentCardViewAdapter != null) {
            mCommentCardViewAdapter.cancelTask();
        }
    }

    private void findView() {
        super.findCommonToolbarView(this);
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
                String MESSAGE = editTextMsg.getText().toString();
                if (!TextUtils.isEmpty(MESSAGE)) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd/HH/mm");
                    String date = dateFormat.format(new Date());

                    mToolBarProgressBar.setVisibility(View.VISIBLE);

                    mSendCommentTask = new SendCommentTask();
                    Map<String, String> map = new HashMap<>();// 请求参数
                    map.put("username", USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
                    map.put("username2", "");
                    map.put("card_id", Integer.toString(CardId));
                    map.put("message", MESSAGE);
                    map.put("date", date);
                    map.put("type", "ADD_PINLUN");
                    mSendCommentTask.executeOnExecutor(exec, map);
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

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        OKCommentBean mCommentBean = mCommentCardViewAdapter.getLastCommentBean();
        if (mCommentBean == null) {
            mRefreshLayout.finishLoadMore(1500);
            return;
        }
        Map<String, String> map = new HashMap<>();// 请求参数,历史界面无需请求参数,直接获取数据库数据的
        map.put("username", USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
        map.put("max_id", Integer.toString(mCommentBean.getCOM_ID()));
        map.put("load_type", "COMMENT_ENTRY");
        if (mOKLoadCommentApi == null) {
            mOKLoadCommentApi = new OKLoadCommentApi(this, true);
        }
        mOKLoadCommentApi.requestCommentBeanList(map, true, mOnCallBack);
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        if (OKNetUtil.isNet(this)) {
            Map<String, String> map = new HashMap<>();// 请求参数
            map.put("card_id", Integer.toString(CardId));
            map.put("num", OKConstant.COMMENT_LOAD_COUNT);
            map.put("username", USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
            if (mOKLoadCommentApi == null) {
                mOKLoadCommentApi = new OKLoadCommentApi(this, false);
            }
            mOKLoadCommentApi.requestCommentBeanList(map, false, mOnCallBack);
        } else {
            mRefreshLayout.finishRefresh(1500);
            showSnackbar(mOKRecyclerView, "没有网络连接!", "");
        }
    }

    private class CommentCardViewAdapter extends RecyclerView.Adapter<CommentCardViewAdapter.CommentViewHolder> {
        private Context mContext;
        private List<OKCommentBean> mBeanList;
        private CommentTask mCommentTask;

        public CommentCardViewAdapter(Context context, List<OKCommentBean> list) {
            this.mContext = context;
            this.mBeanList = list;
        }

        private void initViews(final CommentViewHolder mCommentViewHolder, final OKCommentBean okCommentBean, final int position) {
            mCommentViewHolder.setListPosition(position);
            GlideRoundApi(mCommentViewHolder.mImageViewTitle, okCommentBean.getHEAD_PORTRAIT_URL(), R.drawable.touxian_placeholder, R.drawable.touxian_placeholder);
            mCommentViewHolder.mTextViewTitle.setText(okCommentBean.getNICKNAME());
            mCommentViewHolder.mTextViewContent.setText("" + okCommentBean.getCOMMENT_CONTENT());
            mCommentViewHolder.mTextViewZ.setText("" + okCommentBean.getZAN_NUM());
            mCommentViewHolder.mTextViewDate.setText(formatTime(okCommentBean.getDATE()));

            if (okCommentBean.IS_ZAN()) {
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
                    String USER_NAME = okCommentBean.getUSER_NAME();
                    String USER_NICKNAME = okCommentBean.getNICKNAME();

                    Bundle bundleHp = new Bundle();
                    bundleHp.putString(OKUserInfoBean.KEY_USERNAME, USER_NAME);
                    bundleHp.putString(OKUserInfoBean.KEY_NICKNAME, USER_NICKNAME);
                    startUserActivity(bundleHp, OKHomePageActivity.class);
                }
            });

            mCommentViewHolder.mImageViewZ.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!USER_INFO_SP.getBoolean("STATE", false)) {
                        startUserActivity(null, OKLoginActivity.class);
                        return;
                    }

                    if (!okCommentBean.IS_ZAN()) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd/HH/mm");
                        String date = dateFormat.format(new Date());

                        Map<String, String> param = new HashMap<String, String>();// 请求参数
                        param.put("id", Integer.toString(okCommentBean.getCOM_ID()));
                        param.put("username", USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
                        param.put("type", "COMMENT_EDIT");
                        param.put("date", date);
                        mCommentTask = new CommentTask(mCommentViewHolder, position);
                        mCommentTask.executeOnExecutor(exec, param); // 并行执行线程
                    } else {
                        showSnackbar(v, "您已点过赞了", "");
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

                            String USER_NAME = mCommentBeanHome.getUSER_NAME();
                            String USER_NICKNAME = mCommentBeanHome.getNICKNAME();

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
                            startUserActivity(OKCommentBean.toBundle(mCommentBean), OKCommentReplyActivity.class);
                            popWindow.dismiss();
                            break;
                        case R.id.COMMENT_POP_COPY_LAYOU:
                            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            OKCommentBean mCommentBeanClip = getCommentBean(pos);
                            if (mCommentBeanClip == null) {
                                return;
                            }

                            cm.setText(mCommentBeanClip.getCOMMENT_CONTENT());
                            showSnackbar(mOKRecyclerView, "文本已复制到剪切板", "");
                            popWindow.dismiss();
                            break;
                        case R.id.COMMENT_POP_JuBao_LAYOU:
                            OKCommentBean mCommentBeanRePort = getCommentBean(pos);
                            if (mCommentBeanRePort == null) {
                                return;
                            }

                            Bundle bundle3 = new Bundle();

                            bundle3.putString("JUBAO_TYPE", "COMMENT");
                            bundle3.putString("JUBAO_COM_ID", Integer.toString(mCommentBeanRePort.getCOM_ID()));
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

        public OKCommentBean getLastCommentBean() {
            if (mBeanList != null && mBeanList.size() != 0) {
                return mBeanList.get(mBeanList.size() - 1);
            } else {
                return null;
            }
        }

        public void cancelTask() {
            if (mCommentTask != null && mCommentTask.getStatus() == AsyncTask.Status.RUNNING) {
                mCommentTask.cancel(true); // 如果线程已经在执行则取消执行
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

        class CommentTask extends AsyncTask<Map<String, String>, Void, Boolean> {
            private CommentViewHolder mCommentViewHolder;
            private int mPosition;

            public CommentTask(CommentViewHolder viewHolder, int pos) {
                mCommentViewHolder = viewHolder;
                mPosition = pos;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (isCancelled() || (mCommentViewHolder.getListPosition() != mPosition)) {
                    return;
                }
                if (aBoolean) {
                    OKCommentBean mCommentBean = getCommentBean(mPosition);
                    if (mCommentBean == null) {
                        return;
                    }

                    mCommentBean.setZAN_NUM(mCommentBean.getZAN_NUM() + 1);
                    mCommentBean.setIS_ZAN(true);
                    mBeanList.set(mPosition, mCommentBean);

                    GlideApi(mCommentViewHolder.mImageViewZ, R.drawable.comment_red_zan, R.drawable.comment_red_zan, R.drawable.comment_red_zan);
                    mCommentViewHolder.mTextViewZ.setText("" + mCommentBean.getZAN_NUM());
                    mCommentViewHolder.mTextViewZ.setTextColor(getResources().getColor(R.color.fenhon));
                } else {
                    showSnackbar(mCommentViewHolder.mCardView, "服务器错误,请稍后重试!", "ErrorCode: " + OKConstant.GOODS_BUY_ERROR);
                }
            }

            @Override
            protected Boolean doInBackground(Map<String, String>... params) {
                if (isCancelled()) {
                    return false;
                }

                OKBusinessApi mWebApi = new OKBusinessApi();
                return mWebApi.editCommentPraise(params[0]);
            }
        }
    }

    private class SendCommentTask extends AsyncTask<Map<String, String>, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Map<String, String>... params) {
            if (isCancelled()) {
                return false;
            }
            return new OKBusinessApi().updateCardInfo(params[0]);
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
                mRefreshLayout.autoRefresh();
                showSnackbar(mOKRecyclerView, "发送成功", "");
            } else {
                showSnackbar(mOKRecyclerView, "发送失败", "ErrorCode :" + OKConstant.COMMENT_ERROR);
            }
        }
    }
}
