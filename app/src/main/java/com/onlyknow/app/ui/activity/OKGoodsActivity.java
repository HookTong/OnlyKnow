package com.onlyknow.app.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.onlyknow.app.GlideApp;
import com.onlyknow.app.OKConstant;
import com.onlyknow.app.R;
import com.onlyknow.app.api.OKBusinessApi;
import com.onlyknow.app.api.OKLoadGoodsApi;
import com.onlyknow.app.database.bean.OKGoodsBean;
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

public class OKGoodsActivity extends OKBaseActivity implements OnRefreshListener, OnLoadMoreListener, OKLoadGoodsApi.onCallBack {
    private RefreshLayout mRefreshLayout;
    private OKRecyclerView mOKRecyclerView;
    private EntryViewAdapter mEntryViewAdapter;

    private OKLoadGoodsApi mOKLoadGoodsApi;
    private List<OKGoodsBean> mOKGoodsBeanList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ok_activity_goods);
        initSystemBar(this);
        initUserInfoSharedPreferences();
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
        if (mOKLoadGoodsApi != null) {
            mOKLoadGoodsApi.cancelTask();
        }

        if (mEntryViewAdapter != null) {
            mEntryViewAdapter.cancelTask();
        }
    }

    private void init() {
        mEntryViewAdapter = new EntryViewAdapter(this, mOKGoodsBeanList);
        mOKRecyclerView.setAdapter(mEntryViewAdapter);

        mToolbarBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                OKGoodsActivity.this.finish();
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

    private void findView() {
        super.findCommonToolbarView(this);
        setSupportActionBar(mToolbar);

        mToolbarBack.setVisibility(View.VISIBLE);
        mToolbarTitle.setVisibility(View.VISIBLE);
        mToolbarTitle.setText("唯知商城");

        mRefreshLayout = (RefreshLayout) findViewById(R.id.ok_content_collapsing_refresh);
        mOKRecyclerView = (OKRecyclerView) findViewById(R.id.ok_content_collapsing_RecyclerView);

        mOKRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRefreshLayout.setRefreshHeader(new TaurusHeader(this));
        mRefreshLayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale));
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setOnLoadMoreListener(this);
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        OKGoodsBean mGoodsBean = mEntryViewAdapter.getLastGoodsBean();
        if (mGoodsBean == null) {
            mRefreshLayout.finishLoadMore(1500);
            return;
        }
        Map<String, String> map = new HashMap<>();// 请求参数,历史界面无需请求参数,直接获取数据库数据的
        map.put("username", USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
        map.put("max_id", Integer.toString(mGoodsBean.getGD_ID()));
        map.put("load_type", "GOODS_ENTRY");
        if (mOKLoadGoodsApi == null) {
            mOKLoadGoodsApi = new OKLoadGoodsApi(OKGoodsActivity.this, true);
        }
        mOKLoadGoodsApi.requestCardBeanList(map, true, this);
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        if (OKNetUtil.isNet(this)) {
            Map<String, String> map = new HashMap<>();// 请求参数
            map.put("num", OKConstant.GOODS_LOAD_COUNT);
            if (USER_INFO_SP.getBoolean("STATE", false)) {
                map.put("username", USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
            } else {
                map.put("username", "");
            }

            if (mOKLoadGoodsApi == null) {
                mOKLoadGoodsApi = new OKLoadGoodsApi(this, false);
            }
            mOKLoadGoodsApi.requestCardBeanList(map, false, this);
            mOKRecyclerView.setEnabled(false);
        } else {
            mRefreshLayout.finishRefresh(1500);
            showSnackbar(mOKRecyclerView, "没有网络连接!", "");
        }
    }

    @Override
    public void goodsApiComplete(List<OKGoodsBean> list) {
        if (list != null) {
            if (mRefreshLayout.getState() == RefreshState.Refreshing) {
                mOKGoodsBeanList.clear();
                mOKGoodsBeanList.addAll(list);
            } else if (mRefreshLayout.getState() == RefreshState.Loading) {
                mOKGoodsBeanList.addAll(list);
            }
            OKConstant.putListCache(INTERFACE_GOODS, mOKGoodsBeanList);
            mOKRecyclerView.getAdapter().notifyDataSetChanged();
        }

        if (mRefreshLayout.getState() == RefreshState.Refreshing) {
            mRefreshLayout.finishRefresh();
        } else if (mRefreshLayout.getState() == RefreshState.Loading) {
            mRefreshLayout.finishLoadMore();
        }
    }

    private class EntryViewAdapter extends RecyclerView.Adapter<EntryViewAdapter.EntryViewHolder> {
        private Context mContext;
        private List<OKGoodsBean> mBeanList;
        private EntryTask mEntryTask;

        public EntryViewAdapter(Context context, List<OKGoodsBean> list) {
            this.mContext = context;
            this.mBeanList = list;
        }

        private void initViews(final EntryViewHolder mEntryViewHolder, final OKGoodsBean okGoodsBean, final int position) {
            mEntryViewHolder.setListPosition(position);
            GlideApi(mEntryViewHolder.mImageViewTitle, okGoodsBean.getGD_ICON_URL(), R.drawable.goods, R.drawable.goods);
            mEntryViewHolder.mTextViewTitle.setText(okGoodsBean.getGD_NAME());
            mEntryViewHolder.mTextViewContent.setText("该商品的价格是 : " + Integer.toString(okGoodsBean.getGD_PRICE()) + " 知值");
            if (!okGoodsBean.IS_BUY()) {
                mEntryViewHolder.mButtonOpt.setText("购买");
                mEntryViewHolder.mButtonOpt.setTextColor(getResources().getColor(R.color.md_white_1000));
            } else {
                mEntryViewHolder.mButtonOpt.setText("已购买");
                mEntryViewHolder.mButtonOpt.setTextColor(getResources().getColor(R.color.fenhon));
            }

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

                    mBundle.putString("url", okGoodsBean.getGD_ICON_URL());

                    startUserActivity(mBundle, OKDragPhotoActivity.class);
                    overridePendingTransition(0, 0);
                }
            });

            mEntryViewHolder.mButtonOpt.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!okGoodsBean.IS_BUY()) {
                        showAlertDialog("购买商品", "使用积分购买该商品,是否购买 ?", "购买", "取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                if (USER_INFO_SP.getBoolean("STATE", false)) {
                                    if (USER_INFO_SP.getInt(OKUserInfoBean.KEY_JIFENG, 0) >= okGoodsBean.getGD_PRICE()) {
                                        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd/HH/mm");
                                        String date = df.format(new Date());
                                        Map<String, String> param = new HashMap<String, String>();// 请求参数
                                        param.put("username", USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
                                        param.put("gdid", Integer.toString(okGoodsBean.getGD_ID()));
                                        param.put("date", date);
                                        mEntryTask = new EntryTask(mEntryViewHolder, position);
                                        mEntryTask.executeOnExecutor(exec, param); // 并行执行线程
                                    } else {
                                        showSnackbar(mEntryViewHolder.mCardView, "您没有足够的积分以购买该商品!", "");
                                    }
                                } else {
                                    startUserActivity(null, OKLoginActivity.class);
                                }
                            }
                        });
                    } else {
                        showSnackbar(mEntryViewHolder.mCardView, "您已经购买过该商品了不能重复购买!", "");
                    }
                }
            });

            mEntryViewHolder.mCardView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog(okGoodsBean);
                }
            });
        }

        public OKGoodsBean getGoodsBean(int i) {
            if (mBeanList != null && mBeanList.size() != 0 && i <= mBeanList.size()) {
                return mBeanList.get(i);
            } else {
                return null;
            }
        }

        public OKGoodsBean getLastGoodsBean() {
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

        public void showDialog(final OKGoodsBean bean) {
            AlertDialog.Builder DialogMenu = new AlertDialog.Builder(OKGoodsActivity.this);
            final View dialogView = LayoutInflater.from(OKGoodsActivity.this).inflate(R.layout.ok_dialog_goods, null);
            final ImageView mImageViewBackground = (ImageView) dialogView.findViewById(R.id.ok_dialog_goods_background_image);
            final OKSEImageView mOKSEImageViewClose = (OKSEImageView) dialogView.findViewById(R.id.ok_dialog_goods_close_image);
            final ImageView mImageViewIcon = (ImageView) dialogView.findViewById(R.id.ok_dialog_goods_icon_image);
            final TextView mTextViewName = (TextView) dialogView.findViewById(R.id.ok_dialog_goods_goods_name_text);
            final TextView mTextViewJiaGe = (TextView) dialogView.findViewById(R.id.ok_dialog_goods_jiage_text);
            final TextView mTextViewJianJIe = (TextView) dialogView.findViewById(R.id.ok_dialog_goods_jianjie_text);
            final TextView mTextViewMiaoShu = (TextView) dialogView.findViewById(R.id.ok_dialog_goods_miaoshu_text);
            GlideApi(mImageViewBackground, bean.getGD_IMAGE_URL(), R.drawable.topgd1, R.drawable.topgd1);
            GlideApp.with(OKGoodsActivity.this).load(bean.getGD_ICON_URL()).transform(new RoundedCorners(10)).error(R.drawable.goods).into(mImageViewIcon);
            mTextViewName.setText(bean.getGD_NAME());
            mTextViewJiaGe.setText("" + bean.getGD_PRICE() + " 知值");
            mTextViewJianJIe.setText(bean.getGD_INTRODUCTION());
            mTextViewMiaoShu.setText(bean.getGD_DESCRIBE());
            DialogMenu.setView(dialogView);
            final AlertDialog mAlertDialog = DialogMenu.show();

            mImageViewBackground.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (bean.getGD_TYPE().startsWith("http://") || bean.getGD_TYPE().startsWith("https://")) {
                        Bundle mBundle = new Bundle();
                        mBundle.putString("WEB_LINK", bean.getGD_TYPE());
                        startUserActivity(mBundle, OKBrowserActivity.class);
                    }
                }
            });

            mOKSEImageViewClose.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAlertDialog.dismiss();
                }
            });
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
                    OKGoodsBean mGoodsBean = getGoodsBean(mPosition);
                    if (mGoodsBean == null) {
                        return;
                    }

                    mGoodsBean.setIS_BUY(true);
                    mBeanList.set(mPosition, mGoodsBean);
                    mEntryViewHolder.mButtonOpt.setText("已购买");
                    mEntryViewHolder.mButtonOpt.setTextColor(getResources().getColor(R.color.fenhon));
                } else {
                    showSnackbar(mEntryViewHolder.mCardView, "购买失败", "ErrorCode: " + OKConstant.GOODS_BUY_ERROR);
                    mEntryViewHolder.mButtonOpt.setText("购买");
                    mEntryViewHolder.mButtonOpt.setTextColor(getResources().getColor(R.color.md_white_1000));
                }
            }

            @Override
            protected Boolean doInBackground(Map<String, String>... params) {
                if (isCancelled()) {
                    return false;
                }

                OKBusinessApi mWebApi = new OKBusinessApi();
                return mWebApi.GoodsBuy(params[0]);
            }
        }
    }
}
