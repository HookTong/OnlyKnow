package com.onlyknow.app.ui.activity;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.onlyknow.app.OKConstant;
import com.onlyknow.app.R;
import com.onlyknow.app.database.bean.OKNoticeBean;
import com.onlyknow.app.database.bean.OKUserInfoBean;
import com.onlyknow.app.ui.OKBaseActivity;
import com.onlyknow.app.ui.view.OKRecyclerView;
import com.onlyknow.app.utils.OKNetUtil;
import com.scwang.smartrefresh.header.TaurusHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class OKNoticeActivity extends OKBaseActivity implements OnRefreshListener {
    private RefreshLayout mRefreshLayout;
    private OKRecyclerView mOKRecyclerView;
    private EntryViewAdapter mEntryViewAdapter;

    private LoadNoticeTask mLoadNoticeTask;
    private List<OKNoticeBean> mNoticeBeanList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ok_activity_notice);
        initSystemBar(this);
        initUserInfoSharedPreferences();

        EMClient.getInstance().groupManager().loadAllGroups();
        EMClient.getInstance().chatManager().loadAllConversations();

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
        if (mLoadNoticeTask != null && mLoadNoticeTask.getStatus() == AsyncTask.Status.RUNNING) {
            mLoadNoticeTask.cancel(true);
        }
    }

    private void findView() {
        super.findCommonToolbarView(this);
        setSupportActionBar(mToolbar);

        mToolbarBack.setVisibility(View.VISIBLE);
        mToolbarTitle.setVisibility(View.VISIBLE);
        mToolbarAdd.setVisibility(View.VISIBLE);

        mToolbarTitle.setText("消息通知");

        mRefreshLayout = (RefreshLayout) findViewById(R.id.ok_content_collapsing_refresh);
        mOKRecyclerView = (OKRecyclerView) findViewById(R.id.ok_content_collapsing_RecyclerView);

        mOKRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRefreshLayout.setRefreshHeader(new TaurusHeader(this));
        mRefreshLayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale));
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setEnableLoadMore(false);
    }

    private void init() {
        mEntryViewAdapter = new EntryViewAdapter(this, mNoticeBeanList);
        mOKRecyclerView.setAdapter(mEntryViewAdapter);

        mToolbarAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle mBundle = new Bundle();
                mBundle.putInt(INTENT_KEY_INTERFACE_TYPE, INTERFACE_NOTICE);
                startUserActivity(mBundle, OKSearchActivity.class);
            }
        });

        mToolbarBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                OKNoticeActivity.this.finish();
            }
        });

        mOKRecyclerView.setEmptyView(initCommonEmptyView(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mRefreshLayout.autoRefresh();
            }
        }));

        setEmptyTextTitle("您还没有会话消息");

        mRefreshLayout.autoRefresh();
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        if (OKNetUtil.isNet(this) && USER_INFO_SP.getBoolean("STATE", false)) {
            if (mLoadNoticeTask != null && mLoadNoticeTask.getStatus() == AsyncTask.Status.RUNNING) {
                mLoadNoticeTask.cancel(true);
            }
            mLoadNoticeTask = new LoadNoticeTask();
            mLoadNoticeTask.executeOnExecutor(exec);
        } else {
            mRefreshLayout.finishRefresh(1500);
            showSnackbar(mOKRecyclerView, "请检查用户状态和网络设置!", "");
        }
    }

    private class EntryViewAdapter extends RecyclerView.Adapter<EntryViewAdapter.EntryViewHolder> {
        private Context mContext;
        private List<OKNoticeBean> mBeanList;

        public EntryViewAdapter(Context context, List<OKNoticeBean> list) {
            this.mContext = context;
            this.mBeanList = list;
        }

        private void initViews(final EntryViewHolder mEntryViewHolder, final OKNoticeBean okNoticeBean, final int position) {
            mEntryViewHolder.setListPosition(position);
            // 控件内容设置
            String url = okNoticeBean.getHEAD_PORTRAIT_URL();
            GlideRoundApi(mEntryViewHolder.mImageViewTitle, url, R.drawable.touxian_placeholder, R.drawable.touxian_placeholder);
            mEntryViewHolder.mTextViewTitle.setText(okNoticeBean.getNOTICE_TITLE());
            mEntryViewHolder.mTextViewContent.setText(okNoticeBean.getNOTICE_CONTENT());
            mEntryViewHolder.mTextViewDate.setText(formatTime(okNoticeBean.getDATE()));

            mEntryViewHolder.mCardView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString(OKUserInfoBean.KEY_USERNAME, okNoticeBean.getUSER_NAME());
                    bundle.putString(OKUserInfoBean.KEY_NICKNAME, okNoticeBean.getNOTICE_TITLE());
                    startUserActivity(bundle, OKSessionActivity.class);
                }
            });

            mEntryViewHolder.mImageViewTitle.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString(OKUserInfoBean.KEY_USERNAME, okNoticeBean.getUSER_NAME());
                    bundle.putString(OKUserInfoBean.KEY_NICKNAME, okNoticeBean.getNOTICE_TITLE());
                    startUserActivity(bundle, OKHomePageActivity.class);
                }
            });

            mEntryViewHolder.mCardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showAlertDialog("删除会话", getResources().getString(R.string.dialog_remove_session), "删除", "取消",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    new Thread() {
                                        @Override
                                        public void run() {
                                            super.run();
                                            EMClient.getInstance().chatManager().deleteConversation(okNoticeBean.getUSER_NAME(), true);
                                        }
                                    }.start();

                                    mBeanList.remove(position);
                                    OKConstant.removeListCache(INTERFACE_NOTICE, position);
                                    mOKRecyclerView.getAdapter().notifyDataSetChanged();
                                }
                            });
                    return false;
                }
            });
        }

        public OKNoticeBean getNoticeBean(int i) {
            if (mBeanList != null && mBeanList.size() != 0 && i <= mBeanList.size()) {
                return mBeanList.get(i);
            } else {
                return null;
            }
        }

        public OKNoticeBean getLastNoticeBean() {
            if (mBeanList != null && mBeanList.size() != 0) {
                return mBeanList.get(mBeanList.size() - 1);
            } else {
                return null;
            }
        }

        @Override
        public EntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.ok_item_entry, parent, false);
            return new EntryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(EntryViewHolder holder, int position) {
            initViews(holder, mBeanList.get(position), position);
        }

        @Override
        public int getItemCount() {
            return mBeanList.size();
        }

        class EntryViewHolder extends RecyclerView.ViewHolder {
            public CardView mCardView;
            public ImageView mImageViewTitle;
            public TextView mTextViewTitle, mTextViewContent, mTextViewDate;
            public Button mButtonOpt;

            public EntryViewHolder(View itemView) {
                super(itemView);
                mCardView = itemView.findViewById(R.id.entryView);
                mImageViewTitle = itemView.findViewById(R.id.entryView_biaoti_imag);
                mTextViewTitle = itemView.findViewById(R.id.entryView_biaoti_text);
                mTextViewContent = itemView.findViewById(R.id.entryView_fubiaoti_text);
                mTextViewDate = itemView.findViewById(R.id.entryView_date_text);
                mButtonOpt = itemView.findViewById(R.id.entryView_caozuo_but);

                mTextViewDate.setVisibility(View.VISIBLE);
                mButtonOpt.setVisibility(View.GONE);
            }

            private int position;

            public void setListPosition(int pos) {
                this.position = pos;
            }

            public int getListPosition() {
                return position;
            }
        }
    }

    private class LoadNoticeTask extends AsyncTask<Void, Void, List<OKNoticeBean>> {
        @Override
        protected List<OKNoticeBean> doInBackground(Void... params) {
            if (isCancelled()) {
                return null;
            }
            Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
            if (conversations == null || conversations.size() == 0) {
                return null;
            }
            List<OKNoticeBean> list = new ArrayList<>();
            for (Map.Entry<String, EMConversation> entry : conversations.entrySet()) {
                EMConversation mEMConversation = entry.getValue();
                EMMessage LastMsg = mEMConversation.getLastMessage(); // 会话的最后一条消息
                EMMessage LatestMsg = mEMConversation.getLatestMessageFromOthers(); // 会话接收到的最后一条消息
                OKNoticeBean mNoticeBean = new OKNoticeBean();
                if (LatestMsg == null) {
                    mNoticeBean.setUSER_NAME(LastMsg.getTo());
                    mNoticeBean.setNOTICE_TITLE(LastMsg.getStringAttribute("TO_" + OKUserInfoBean.KEY_NICKNAME, ""));
                    mNoticeBean.setHEAD_PORTRAIT_URL(LastMsg.getStringAttribute(OKUserInfoBean.KEY_HEADPORTRAIT_URL, ""));
                } else {
                    mNoticeBean.setUSER_NAME(LatestMsg.getFrom());
                    mNoticeBean.setNOTICE_TITLE(LatestMsg.getStringAttribute("FROM_" + OKUserInfoBean.KEY_NICKNAME, ""));
                    mNoticeBean.setHEAD_PORTRAIT_URL(LatestMsg.getStringAttribute(OKUserInfoBean.KEY_HEADPORTRAIT_URL, ""));
                }
                if (LastMsg.getType() == EMMessage.Type.TXT) {
                    mNoticeBean.setNOTICE_CONTENT(((EMTextMessageBody) LastMsg.getBody()).getMessage());
                } else if (LastMsg.getType() == EMMessage.Type.IMAGE) {
                    mNoticeBean.setNOTICE_CONTENT("[图片]");
                } else {
                    mNoticeBean.setNOTICE_CONTENT("[其他消息]");
                }
                mNoticeBean.setSTATE(mEMConversation.getUnreadMsgCount() > 0 ? true : false);
                mNoticeBean.setUNREAD_NUM(mEMConversation.getUnreadMsgCount());
                mNoticeBean.setALL_MESSAGE_NUM(mEMConversation.getAllMsgCount());
                mNoticeBean.setDATE(new SimpleDateFormat("yyyy/MM/dd/HH/mm").format(new Date(LastMsg.getMsgTime())));

                list.add(mNoticeBean);
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<OKNoticeBean> list) {
            super.onPostExecute(list);
            if (isCancelled()) {
                return;
            }
            if (list != null && list.size() != 0) {
                mNoticeBeanList.clear();
                mNoticeBeanList.addAll(list);
                OKConstant.putListCache(INTERFACE_NOTICE, mNoticeBeanList);
                mOKRecyclerView.getAdapter().notifyDataSetChanged();
                mRefreshLayout.finishRefresh();
            } else {
                mRefreshLayout.finishRefresh();
                showSnackbar(mOKRecyclerView, "没有新的通知", "");
            }
        }
    }
}
