package com.onlyknow.app.ui.fragement.me;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.onlyknow.app.OKConstant;
import com.onlyknow.app.R;
import com.onlyknow.app.api.OKServiceResult;
import com.onlyknow.app.api.user.OKLoadAttentionApi;
import com.onlyknow.app.api.user.OKManagerUserApi;
import com.onlyknow.app.db.bean.OKAttentionBean;
import com.onlyknow.app.db.bean.OKUserInfoBean;
import com.onlyknow.app.ui.OKBaseFragment;
import com.onlyknow.app.ui.activity.OKDragPhotoActivity;
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
import java.util.List;

public class OKAttentionFragment extends OKBaseFragment implements OnRefreshListener, OnLoadMoreListener, OKLoadAttentionApi.onCallBack {
    private RefreshLayout mRefreshLayout;
    private OKRecyclerView mRecyclerView;
    private EntryViewAdapter mEntryViewAdapter;
    private View rootView;

    public boolean isPause = true;

    private boolean isInitLoad = true;
    private OKLoadAttentionApi mOKLoadAttentionApi;
    private List<OKAttentionBean> mAttentionBeanList = new ArrayList<>();

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
            if (isInitLoad && mRefreshLayout.getState() != RefreshState.Refreshing && mRecyclerView.getAdapter().getItemCount() == 0) {
                mRefreshLayout.autoRefresh();
            }
            setEmptyTag(TAG_RETRY);
            setEmptyButTitle("重  试");
            setEmptyTxtTitle(getResources().getString(R.string.ListView_NoData));
        } else {
            mAttentionBeanList.clear();
            mRecyclerView.getAdapter().notifyDataSetChanged();
            setEmptyTag(TAG_LOGIN);
            setEmptyButTitle("登  录");
            setEmptyTxtTitle("未登录,登录后查看!");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOKLoadAttentionApi != null) {
            mOKLoadAttentionApi.cancelTask();
        }

        if (mEntryViewAdapter != null) {
            mEntryViewAdapter.cancelTask();
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

    private void findView(View rootView) {
        mRefreshLayout = (RefreshLayout) rootView.findViewById(R.id.ok_content_collapsing_refresh);
        mRecyclerView = (OKRecyclerView) rootView.findViewById(R.id.ok_content_collapsing_RecyclerView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRefreshLayout.setRefreshHeader(new TaurusHeader(getActivity()));
        mRefreshLayout.setRefreshFooter(new BallPulseFooter(getActivity()).setSpinnerStyle(SpinnerStyle.Scale));
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setOnLoadMoreListener(this);
    }

    private void init() {
        mEntryViewAdapter = new EntryViewAdapter(getActivity(), mAttentionBeanList);
        mRecyclerView.setAdapter(mEntryViewAdapter);

        mRecyclerView.setEmptyView(initCollapsingEmptyView(new OnClickListener() {
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

    public void setSwipeRefreshEnabled(boolean b) {
        if (!isPause) {
            mRefreshLayout.setEnableRefresh(b);
            mRefreshLayout.setEnableLoadMore(!b);
        }
    }

    int page = 0;
    int size = 30;

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        if (!OKNetUtil.isNet(getActivity())) {
            mRefreshLayout.finishLoadMore(1500);
            showSnackBar(mRecyclerView, "请检查网络设置!", "");
            return;
        }
        if (USER_BODY.getBoolean("STATE", false)) {
            OKLoadAttentionApi.Params params = new OKLoadAttentionApi.Params();
            params.setUsername(USER_BODY.getString(OKUserInfoBean.KEY_USERNAME, ""));
            params.setPage(page + 1);
            params.setSize(size);

            if (mOKLoadAttentionApi == null) {
                mOKLoadAttentionApi = new OKLoadAttentionApi(getActivity());
            }
            mOKLoadAttentionApi.requestAttention(params, this);
        } else {
            mRefreshLayout.finishLoadMore(1500);
            showSnackBar(mRecyclerView, "登录后加载", "");
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        if (!OKNetUtil.isNet(getActivity())) {
            mRefreshLayout.finishRefresh(1500);
            showSnackBar(mRecyclerView, "请检查网络设置!", "");
            return;
        }
        if (USER_BODY.getBoolean("STATE", false)) {
            OKLoadAttentionApi.Params params = new OKLoadAttentionApi.Params();
            params.setUsername(USER_BODY.getString(OKUserInfoBean.KEY_USERNAME, ""));
            params.setPage(1);
            params.setSize(size);

            if (mOKLoadAttentionApi == null) {
                mOKLoadAttentionApi = new OKLoadAttentionApi(getActivity());
            }
            mOKLoadAttentionApi.requestAttention(params, this);
        } else {
            mRefreshLayout.finishRefresh(1500);
            showSnackBar(rootView, "登录后查看!", "");
        }
    }

    @Override
    public void loadAttentionComplete(List<OKAttentionBean> list) {
        if (list != null) {
            if (mRefreshLayout.getState() == RefreshState.Refreshing) {
                page = 1;

                mAttentionBeanList.clear();
                mAttentionBeanList.addAll(list);
            } else if (mRefreshLayout.getState() == RefreshState.Loading) {
                page++;

                mAttentionBeanList.addAll(list);
            }
            mRecyclerView.getAdapter().notifyDataSetChanged();
        }

        if (mRefreshLayout.getState() == RefreshState.Refreshing) {
            mRefreshLayout.finishRefresh();
        } else if (mRefreshLayout.getState() == RefreshState.Loading) {
            mRefreshLayout.finishLoadMore();
        }
        isInitLoad = false;
    }

    private class EntryViewAdapter extends RecyclerView.Adapter<EntryViewAdapter.EntryViewHolder> implements OKManagerUserApi.onCallBack {
        private Context mContext;
        private List<OKAttentionBean> mBeanList;
        private OKManagerUserApi okManagerUserApi;
        private EntryViewHolder viewHolder;

        public EntryViewAdapter(Context context, List<OKAttentionBean> list) {
            this.mContext = context;
            this.mBeanList = list;
        }

        private void initViews(final EntryViewHolder mEntryViewHolder, final OKAttentionBean okAttentionBean, final int position) {
            mEntryViewHolder.setListPosition(position);
            GlideRoundApi(mEntryViewHolder.mImageViewTitle, okAttentionBean.getAvatar(), R.drawable.touxian_placeholder, R.drawable.touxian_placeholder);
            mEntryViewHolder.mTextViewTitle.setText(okAttentionBean.getNickName());
            if (!TextUtils.isEmpty(okAttentionBean.getTag())) {
                mEntryViewHolder.mTextViewContent.setText(okAttentionBean.getTag());
            } else {
                mEntryViewHolder.mTextViewContent.setText("这个人很懒,什么都没有留下!");
            }
            mEntryViewHolder.mButtonOpt.setText("取消关注");

            mEntryViewHolder.mImageViewTitle.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int location[] = new int[2];
                    mEntryViewHolder.mImageViewTitle.getLocationOnScreen(location);
                    Bundle mBundle = new Bundle();
                    mBundle.putInt("left", location[0]);
                    mBundle.putInt("top", location[1]);
                    mBundle.putInt("height", mEntryViewHolder.mImageViewTitle.getHeight());
                    mBundle.putInt("width", mEntryViewHolder.mImageViewTitle.getWidth());
                    mBundle.putString("url", okAttentionBean.getAvatar());
                    startUserActivity(mBundle, OKDragPhotoActivity.class);
                    getActivity().overridePendingTransition(0, 0);
                }
            });

            mEntryViewHolder.mButtonOpt.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setIcon(R.drawable.ic_launcher);
                    dialog.setTitle("取消关注");
                    dialog.setMessage("是否取消关注该用户 ?");
                    dialog.setPositiveButton("取消关注", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (USER_BODY.getBoolean("STATE", false)) {

                                viewHolder = mEntryViewHolder;

                                OKManagerUserApi.Params params = new OKManagerUserApi.Params();
                                params.setType(OKManagerUserApi.Params.TYPE_REMOVE_ATTENTION);
                                params.setUsername(USER_BODY.getString(OKUserInfoBean.KEY_USERNAME, ""));
                                params.setPassword(USER_BODY.getString(OKUserInfoBean.KEY_PASSWORD, ""));
                                params.setAttentionUsername(okAttentionBean.getUserNameRete());
                                params.setPos(position);

                                cancelTask();
                                okManagerUserApi = new OKManagerUserApi(getActivity());
                                okManagerUserApi.requestManagerUser(params, EntryViewAdapter.this); // 并行执行线程
                            } else {
                                startUserActivity(null, OKLoginActivity.class);
                            }
                        }
                    });
                    dialog.setNegativeButton("继续关注", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                        }
                    });
                    dialog.show();
                }
            });

            mEntryViewHolder.mCardView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString(OKUserInfoBean.KEY_USERNAME, okAttentionBean.getUserNameRete());
                    bundle.putString(OKUserInfoBean.KEY_NICKNAME, okAttentionBean.getNickName());
                    startUserActivity(bundle, OKHomePageActivity.class);
                }
            });
        }

        public void removeAttentionBean(int i) {
            mBeanList.remove(i);
            mRecyclerView.getAdapter().notifyDataSetChanged();
        }

        public void cancelTask() {
            if (okManagerUserApi != null) {
                okManagerUserApi.cancelTask(); // 如果线程已经在执行则取消执行
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

        @Override
        public void managerUserComplete(OKServiceResult<Object> result, String type, int pos) {
            if (!OKManagerUserApi.Params.TYPE_REMOVE_ATTENTION.equals(type)) return;

            if (viewHolder == null || viewHolder.getListPosition() != pos) return;

            if (result != null && result.isSuccess()) {
                removeAttentionBean(pos);
                showSnackBar(viewHolder.mCardView, "已取消关注", "");
            } else {
                showSnackBar(viewHolder.mCardView, "取消关注失败", "ErrorCode: " + OKConstant.ATTENTION_CANCEL_ERROR);
                viewHolder.mButtonOpt.setText("取消关注");
            }
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

                mTextViewDate.setVisibility(View.GONE);
                mButtonOpt.setVisibility(View.VISIBLE);
                mButtonOpt.setText("取消关注");
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
}
