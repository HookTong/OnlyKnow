package com.onlyknow.app.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
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
import com.onlyknow.app.api.OKServiceResult;
import com.onlyknow.app.api.card.OKManagerCardApi;
import com.onlyknow.app.api.goods.OKManagerGoodsApi;
import com.onlyknow.app.api.goods.OKLoadGoodsApi;
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

import java.util.ArrayList;
import java.util.List;

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

    int page = 0;
    int size = 30;

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        if (!OKNetUtil.isNet(this)) {
            mRefreshLayout.finishLoadMore(1500);
            showSnackBar(mOKRecyclerView, "没有网络连接!", "");
            return;
        }

        OKLoadGoodsApi.Params params = new OKLoadGoodsApi.Params();
        params.setUsername(USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
        params.setPage(page + 1);
        params.setSize(size);

        if (mOKLoadGoodsApi == null) {
            mOKLoadGoodsApi = new OKLoadGoodsApi(OKGoodsActivity.this);
        }
        mOKLoadGoodsApi.requestGoods(params, this);
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        if (OKNetUtil.isNet(this)) {
            OKLoadGoodsApi.Params params = new OKLoadGoodsApi.Params();
            params.setUsername(USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
            params.setPage(1);
            params.setSize(size);

            if (mOKLoadGoodsApi == null) {
                mOKLoadGoodsApi = new OKLoadGoodsApi(OKGoodsActivity.this);
            }
            mOKLoadGoodsApi.requestGoods(params, this);
        } else {
            mRefreshLayout.finishRefresh(1500);
            showSnackBar(mOKRecyclerView, "没有网络连接!", "");
        }
    }

    @Override
    public void goodsApiComplete(List<OKGoodsBean> list) {
        if (list != null) {
            if (mRefreshLayout.getState() == RefreshState.Refreshing) {
                page = 1;

                mOKGoodsBeanList.clear();
                mOKGoodsBeanList.addAll(list);
            } else if (mRefreshLayout.getState() == RefreshState.Loading) {
                page++;

                mOKGoodsBeanList.addAll(list);
            }
            mOKRecyclerView.getAdapter().notifyDataSetChanged();
        }

        if (mRefreshLayout.getState() == RefreshState.Refreshing) {
            mRefreshLayout.finishRefresh();
        } else if (mRefreshLayout.getState() == RefreshState.Loading) {
            mRefreshLayout.finishLoadMore();
        }
    }

    private class EntryViewAdapter extends RecyclerView.Adapter<EntryViewAdapter.EntryViewHolder> implements OKManagerGoodsApi.onCallBack {
        private Context mContext;
        private List<OKGoodsBean> mBeanList;
        private OKManagerGoodsApi mOKManagerGoodsApi;
        private EntryViewHolder viewHolder;

        public EntryViewAdapter(Context context, List<OKGoodsBean> list) {
            this.mContext = context;
            this.mBeanList = list;
        }

        private void initViews(final EntryViewHolder mEntryViewHolder, final OKGoodsBean okGoodsBean, final int position) {
            mEntryViewHolder.setListPosition(position);
            GlideApi(mEntryViewHolder.mImageViewTitle, okGoodsBean.getGdIconUrl(), R.drawable.goods, R.drawable.goods);
            mEntryViewHolder.mTextViewTitle.setText(okGoodsBean.getGdName());
            mEntryViewHolder.mTextViewContent.setText("该商品的价格是 : " + Integer.toString(okGoodsBean.getGdPrice()) + " 知值");
            if (!okGoodsBean.isGoodsBy()) {
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

                    mBundle.putString("url", okGoodsBean.getGdIconUrl());

                    startUserActivity(mBundle, OKDragPhotoActivity.class);
                    overridePendingTransition(0, 0);
                }
            });

            mEntryViewHolder.mButtonOpt.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!okGoodsBean.isGoodsBy()) {
                        showAlertDialog("购买商品", "使用积分购买该商品,是否购买 ?", "购买", "取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                if (USER_INFO_SP.getBoolean("STATE", false)) {
                                    if (USER_INFO_SP.getInt(OKUserInfoBean.KEY_INTEGRAL, 0) >= okGoodsBean.getGdPrice()) {

                                        viewHolder = mEntryViewHolder;

                                        OKManagerGoodsApi.Params params = new OKManagerGoodsApi.Params();
                                        params.setGdId(okGoodsBean.getGdId());
                                        params.setUsername(USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
                                        params.setPassword(USER_INFO_SP.getString(OKUserInfoBean.KEY_PASSWORD, ""));
                                        params.setType(OKManagerGoodsApi.Params.TYPE_BUY);
                                        params.setPos(position);

                                        cancelTask();
                                        mOKManagerGoodsApi = new OKManagerGoodsApi(OKGoodsActivity.this);
                                        mOKManagerGoodsApi.requestManagerGoods(params, EntryViewAdapter.this); // 并行执行线程
                                    } else {
                                        showSnackBar(mEntryViewHolder.mCardView, "您没有足够的积分以购买该商品!", "");
                                    }
                                } else {
                                    startUserActivity(null, OKLoginActivity.class);
                                }
                            }
                        });
                    } else {
                        showSnackBar(mEntryViewHolder.mCardView, "您已经购买过该商品了不能重复购买!", "");
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

        public void cancelTask() {
            if (mOKManagerGoodsApi != null) {
                mOKManagerGoodsApi.cancelTask(); // 如果线程已经在执行则取消执行
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
            GlideApi(mImageViewBackground, bean.getGdImageUrl(), R.drawable.topgd1, R.drawable.topgd1);
            GlideApp.with(OKGoodsActivity.this).load(bean.getGdIconUrl()).transform(new RoundedCorners(10)).error(R.drawable.goods).into(mImageViewIcon);
            mTextViewName.setText(bean.getGdName());
            mTextViewJiaGe.setText("" + bean.getGdPrice() + " 知值");
            mTextViewJianJIe.setText(bean.getGdIntroduction());
            mTextViewMiaoShu.setText(bean.getGdDescribe());
            DialogMenu.setView(dialogView);
            final AlertDialog mAlertDialog = DialogMenu.show();

            mImageViewBackground.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (bean.getGdType().startsWith("http://") || bean.getGdType().startsWith("https://")) {
                        Bundle mBundle = new Bundle();
                        mBundle.putString("WEB_LINK", bean.getGdType());
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

        @Override
        public void managerGoodsApiComplete(OKServiceResult<Object> serviceResult, String type, int pos) {
            if (!OKManagerGoodsApi.Params.TYPE_BUY.equals(type)) return;

            if (viewHolder == null || viewHolder.getListPosition() != pos) return;

            if (serviceResult != null && serviceResult.isSuccess()) {
                OKGoodsBean mGoodsBean = getGoodsBean(pos);
                if (mGoodsBean == null) {
                    return;
                }

                mGoodsBean.setGoodsBy(true);
                mBeanList.set(pos, mGoodsBean);
                viewHolder.mButtonOpt.setText("已购买");
                viewHolder.mButtonOpt.setTextColor(getResources().getColor(R.color.fenhon));
            } else {
                showSnackBar(viewHolder.mCardView, "购买失败", "ErrorCode: " + OKConstant.GOODS_BUY_ERROR);
                viewHolder.mButtonOpt.setText("购买");
                viewHolder.mButtonOpt.setTextColor(getResources().getColor(R.color.md_white_1000));
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
