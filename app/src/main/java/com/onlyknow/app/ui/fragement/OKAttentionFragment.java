package com.onlyknow.app.ui.fragement;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
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
import com.onlyknow.app.net.OKBusinessNet;
import com.onlyknow.app.api.OKLoadAttentionApi;
import com.onlyknow.app.database.bean.OKAttentionBean;
import com.onlyknow.app.database.bean.OKUserInfoBean;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            setEmptyButtonTitle("重  试");
            setEmptyTextTitle(getResources().getString(R.string.ListView_NoData));
        } else {
            mAttentionBeanList.clear();
            mRecyclerView.getAdapter().notifyDataSetChanged();
            setEmptyButtonTag(LOG_IN);
            setEmptyButtonTitle("登  录");
            setEmptyTextTitle("未登录,登录后查看!");
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

    public void setSwipeRefreshEnabled(boolean b) {
        if (!isPause) {
            mRefreshLayout.setEnableRefresh(b);
            mRefreshLayout.setEnableLoadMore(!b);
        }
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        if (!OKNetUtil.isNet(getActivity())) {
            mRefreshLayout.finishLoadMore(1500);
            showSnackBar(mRecyclerView, "请检查网络设置!", "");
            return;
        }
        if (USER_INFO_SP.getBoolean("STATE", false)) {
            OKAttentionBean mAttentionBean = mEntryViewAdapter.getLastAttentionBean();
            if (mAttentionBean == null) {
                mRefreshLayout.finishLoadMore(1500);
                return;
            }
            Map<String, String> map = new HashMap<>();// 请求参数
            map.put("username", USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
            map.put("max_id", Integer.toString(mAttentionBean.getUAT_ID()));
            map.put("load_type", "ATTENTION_ENTRY");
            if (mOKLoadAttentionApi == null) {
                mOKLoadAttentionApi = new OKLoadAttentionApi(getActivity(), true);
            }
            mOKLoadAttentionApi.requestEntryBeanList(map, true, this);
        } else {
            mRefreshLayout.finishLoadMore(1500);
            showSnackBar(mRecyclerView, "登录后加载", "");
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        if (!OKNetUtil.isNet(getActivity())) {
            if (USER_INFO_SP.getBoolean("STATE", false) && mRecyclerView.getAdapter().getItemCount() == 0) {
                mAttentionBeanList.clear();
                mAttentionBeanList.addAll(OKConstant.getListCache(INTERFACE_ATTENTION));
                mRecyclerView.getAdapter().notifyDataSetChanged();
            }
            mRefreshLayout.finishRefresh(1500);
            showSnackBar(mRecyclerView, "请检查网络设置!", "");
            return;
        }
        if (USER_INFO_SP.getBoolean("STATE", false)) {
            Map<String, String> map = new HashMap<>();// 请求参数
            map.put("username", USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
            map.put("num", OKConstant.ATTENTION_LOAD_COUNT);
            if (mOKLoadAttentionApi == null) {
                mOKLoadAttentionApi = new OKLoadAttentionApi(getActivity(), false);
            }
            mOKLoadAttentionApi.requestEntryBeanList(map, false, this);
        } else {
            mRefreshLayout.finishRefresh(1500);
            showSnackBar(rootView, "登录后查看!", "");
        }
    }

    @Override
    public void attentionApiComplete(List<OKAttentionBean> list) {
        if (list != null) {
            if (mRefreshLayout.getState() == RefreshState.Refreshing) {
                mAttentionBeanList.clear();
                mAttentionBeanList.addAll(list);
            } else if (mRefreshLayout.getState() == RefreshState.Loading) {
                mAttentionBeanList.addAll(list);
            }
            OKConstant.putListCache(INTERFACE_ATTENTION, mAttentionBeanList);
            mRecyclerView.getAdapter().notifyDataSetChanged();
        }

        if (mRefreshLayout.getState() == RefreshState.Refreshing) {
            mRefreshLayout.finishRefresh();
        } else if (mRefreshLayout.getState() == RefreshState.Loading) {
            mRefreshLayout.finishLoadMore();
        }
        isInitLoad = false;
    }

    private class EntryViewAdapter extends RecyclerView.Adapter<EntryViewAdapter.EntryViewHolder> {
        private Context mContext;
        private List<OKAttentionBean> mBeanList;
        private EntryTask mEntryTask;

        public EntryViewAdapter(Context context, List<OKAttentionBean> list) {
            this.mContext = context;
            this.mBeanList = list;
        }

        private void initViews(final EntryViewHolder mEntryViewHolder, final OKAttentionBean okAttentionBean, final int position) {
            mEntryViewHolder.setListPosition(position);
            String url = okAttentionBean.getHEAD_PORTRAIT_URL();
            GlideRoundApi(mEntryViewHolder.mImageViewTitle, url, R.drawable.touxian_placeholder, R.drawable.touxian_placeholder);
            mEntryViewHolder.mTextViewTitle.setText(okAttentionBean.getNICKNAME());
            if (!TextUtils.isEmpty(okAttentionBean.getAUTOGRAPH()) && !"NULL".equalsIgnoreCase(okAttentionBean.getAUTOGRAPH())) {
                mEntryViewHolder.mTextViewContent.setText(okAttentionBean.getAUTOGRAPH());
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
                    mBundle.putString("url", okAttentionBean.getHEAD_PORTRAIT_URL());
                    startUserActivity(mBundle, OKDragPhotoActivity.class);
                    getActivity().overridePendingTransition(0, 0);
                }
            });

            mEntryViewHolder.mButtonOpt.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAlertDialog("取消关注", "是否取消关注该用户 ?", "取消关注", "继续关注", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            if (USER_INFO_SP.getBoolean("STATE", false)) {
                                Map<String, String> param = new HashMap<String, String>();// 请求参数
                                param.put("username_main", USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
                                param.put("username_rete", okAttentionBean.getUSER_NAME());
                                param.put("type", "CANCEL_ATTENTION");
                                mEntryTask = new EntryTask(mEntryViewHolder, position);
                                mEntryTask.executeOnExecutor(exec, param); // 并行执行线程
                            } else {
                                startUserActivity(null, OKLoginActivity.class);
                            }
                        }
                    });
                }
            });

            mEntryViewHolder.mCardView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString(OKUserInfoBean.KEY_USERNAME, okAttentionBean.getUSER_NAME());
                    bundle.putString(OKUserInfoBean.KEY_NICKNAME, okAttentionBean.getNICKNAME());
                    startUserActivity(bundle, OKHomePageActivity.class);
                }
            });
        }

        public void removeAttentionBean(int i) {
            mBeanList.remove(i);
            OKConstant.removeListCache(INTERFACE_ATTENTION, i);
            mRecyclerView.getAdapter().notifyDataSetChanged();
        }

        public OKAttentionBean getLastAttentionBean() {
            if (mBeanList != null && mBeanList.size() != 0) {
                return mBeanList.get(mBeanList.size() - 1);
            } else {
                return null;
            }
        }

        public void cancelTask() {
            if (mEntryTask != null && mEntryTask.getStatus() == AsyncTask.Status.RUNNING) {
                mEntryTask.cancel(true); // 如果线程已经在执行则取消执行
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

        class EntryTask extends AsyncTask<Map<String, String>, Void, Boolean> {
            private EntryViewHolder mEntryViewHolder;
            private int mPosition;

            public EntryTask(EntryViewHolder viewHolder, int pos) {
                mEntryViewHolder = viewHolder;
                mPosition = pos;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (isCancelled() || (mEntryViewHolder.getListPosition() != mPosition)) {
                    return;
                }
                if (aBoolean) {
                    removeAttentionBean(mPosition);
                    showSnackBar(mEntryViewHolder.mCardView, "已取消关注", "");
                } else {
                    showSnackBar(mEntryViewHolder.mCardView, "取消关注失败", "ErrorCode: " + OKConstant.ATTENTION_CANCEL_ERROR);
                    mEntryViewHolder.mButtonOpt.setText("取消关注");
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
