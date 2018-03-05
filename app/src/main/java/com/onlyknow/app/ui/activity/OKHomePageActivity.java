package com.onlyknow.app.ui.activity;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.AppBarLayout.OnOffsetChangedListener;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.onlyknow.app.OKConstant;
import com.onlyknow.app.R;
import com.onlyknow.app.net.OKBusinessNet;
import com.onlyknow.app.api.OKLoadHomeApi;
import com.onlyknow.app.database.bean.OKCardBean;
import com.onlyknow.app.database.bean.OKUserInfoBean;
import com.onlyknow.app.ui.OKBaseActivity;
import com.onlyknow.app.ui.view.OKCircleImageView;
import com.onlyknow.app.ui.view.OKRecyclerView;
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

public class OKHomePageActivity extends OKBaseActivity implements OnOffsetChangedListener, OnRefreshListener, OnLoadMoreListener, OKLoadHomeApi.onCallBack {
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private RefreshLayout mRefreshLayout;
    private OKRecyclerView mOKRecyclerView;
    private FloatingActionButton floatingActionButton;
    private ImageView mImageViewHead;
    private OKCircleImageView ImageViewTouXian;
    private TextView textViewTAG;
    private CardViewAdapter mCardViewAdapter;

    private View HeaderView;
    private TextView HeaderTextViewArticlesNum, HeaderTextViewAttentionNum, HeaderTextViewWatchNum, HeaderTextViewValueNum;
    private TextView HeaderTextViewArticlesTitle, HeaderTextViewAttentionTitle, HeaderTextViewWatchTitle, HeaderTextViewValueTitle;

    private String USERNAME, NICKNAME;
    private boolean WhetherThis = false; // 是否是我自己的主页

    private OKLoadHomeApi mOKLoadHomeApi;
    private List<OKCardBean> mCardBeanList = new ArrayList<>();

    private LoadUserTask mLoadUserTask;
    private OKUserInfoBean mBindUserInfoBean;
    private AttentionTask mAttentionTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ok_activity_home_page);
        initUserInfoSharedPreferences();

        USERNAME = getIntent().getExtras().getString(OKUserInfoBean.KEY_USERNAME);
        NICKNAME = getIntent().getExtras().getString(OKUserInfoBean.KEY_NICKNAME);
        if (USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, "").equals(USERNAME)) {
            WhetherThis = true;
        }

        findView();
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        appBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        appBarLayout.removeOnOffsetChangedListener(this);
        mRefreshLayout.finishRefresh();
        mRefreshLayout.finishLoadMore();

        if (mOKLoadHomeApi != null) {
            mOKLoadHomeApi.cancelTask();
        }

        if (mLoadUserTask != null && mLoadUserTask.getStatus() == AsyncTask.Status.RUNNING) {
            mLoadUserTask.cancel(true); // 如果线程已经在执行则取消执行
        }

        if (mAttentionTask != null && mAttentionTask.getStatus() == AsyncTask.Status.RUNNING) {
            mAttentionTask.cancel(true); // 如果线程已经在执行则取消执行
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (OKConstant.getListCache(INTERFACE_HOME) != null) {
            OKConstant.getListCache(INTERFACE_HOME).clear();
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout arg0, int i) {
        if (i == 0) {
            mRefreshLayout.setEnableRefresh(true);
            mRefreshLayout.setEnableLoadMore(false);
        } else {
            mRefreshLayout.setEnableRefresh(false);
            mRefreshLayout.setEnableLoadMore(true);
        }
    }

    private void findView() {
        super.findCollapsingToolbarView(this);
        setSupportActionBar(mToolbar);

        mToolbarBack.setVisibility(View.VISIBLE);
        mToolbarMenu.setVisibility(View.VISIBLE);
        mToolbarTitle.setVisibility(View.VISIBLE);

        appBarLayout = (AppBarLayout) findViewById(R.id.SHOW_App_Bar);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.SHOW_toolbar_layout);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.SHOW_fabtop_ButME);
        mImageViewHead = (ImageView) findViewById(R.id.SHOW_toplayout_image);
        ImageViewTouXian = (OKCircleImageView) findViewById(R.id.SHOW_touxiang_imag);
        textViewTAG = (TextView) findViewById(R.id.SHOW_TAG_TEXT);
        mOKRecyclerView = (OKRecyclerView) findViewById(R.id.ok_content_collapsing_RecyclerView);
        mRefreshLayout = (RefreshLayout) findViewById(R.id.ok_content_collapsing_refresh);

        mOKRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mOKRecyclerView.addHeaderView(initHeaderView());
        mRefreshLayout.setRefreshHeader(new TaurusHeader(this));
        mRefreshLayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale));
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setOnLoadMoreListener(this);
    }

    private void init() {
        mCardViewAdapter = new CardViewAdapter(this, mCardBeanList);
        mOKRecyclerView.setAdapter(mCardViewAdapter);

        mLoadUserTask = new LoadUserTask(); // 获取用户信息
        Map<String, String> map = new HashMap<>();// 请求参数
        map.put("username", USERNAME);
        map.put("type", "ALL");
        mLoadUserTask.executeOnExecutor(exec, map);

        mCollapsingToolbarLayout.setTitle(" ");
        mCollapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        mToolbarTitle.setText(NICKNAME);
        textViewTAG.setText("这个人很懒,什么都没有留下!");

        // 设置用户信息
        GlideRoundApi(ImageViewTouXian, R.drawable.touxian_placeholder_hd, R.drawable.touxian_placeholder_hd, R.drawable.touxian_placeholder_hd);
        GlideBlurApi(mImageViewHead, R.drawable.topgd3, R.drawable.topgd3, R.drawable.topgd3);

        floatingActionButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (USER_INFO_SP.getBoolean("STATE", false)) {
                    if (!WhetherThis) {
                        Bundle bundle = new Bundle();
                        bundle.putString(OKUserInfoBean.KEY_USERNAME, USERNAME);
                        bundle.putString(OKUserInfoBean.KEY_NICKNAME, NICKNAME);
                        startUserActivity(bundle, OKSessionActivity.class);
                    } else {
                        showSnackbar(v, "您不能与自己建立会话", "");
                    }
                } else {
                    startUserActivity(null, OKLoginActivity.class);
                }
            }
        });

        mToolbarBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mToolbarMenu.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showPopupWindow();
            }
        });

        ImageViewTouXian.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBindUserInfoBean == null) {
                    showSnackbar(mOKRecyclerView, "等一会再看吧!", "");
                    return;
                }

                int location[] = new int[2];
                ImageViewTouXian.getLocationOnScreen(location);

                Bundle mBundle = new Bundle();
                mBundle.putInt("left", location[0]);
                mBundle.putInt("top", location[1]);
                mBundle.putInt("height", ImageViewTouXian.getHeight());
                mBundle.putInt("width", ImageViewTouXian.getWidth());

                mBundle.putString("url", mBindUserInfoBean.getHEADPORTRAIT_URL());

                startUserActivity(mBundle, OKDragPhotoActivity.class);
                overridePendingTransition(0, 0);
            }
        });

        mRefreshLayout.autoRefresh();
    }

    private void showPopupWindow() {
        View parent = ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
        View popView = View.inflate(this, R.layout.ok_home_page_menu, null);
        LinearLayout linearLayoutAttention = (LinearLayout) popView.findViewById(R.id.SHOW_POP_GUANZHU_LAYOU);
        LinearLayout linearLayoutQrCode = (LinearLayout) popView.findViewById(R.id.SHOW_POP_JUBAO_LAYOU);
        Button buttonClose = (Button) popView.findViewById(R.id.SHOW_POP_GUANBI_BUT);
        final PopupWindow popWindow = new PopupWindow(popView, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        popWindow.setAnimationStyle(R.style.AnimBottom);
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(true);// 设置允许在外点击消失
        OnClickListener listener = new OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.SHOW_POP_GUANBI_BUT: // 关闭窗口
                        popWindow.dismiss();
                        break;
                    case R.id.SHOW_POP_GUANZHU_LAYOU: // 关注该用户
                        if (USER_INFO_SP.getBoolean("STATE", false)) {
                            if (!WhetherThis) {
                                String usernameThis = USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, "");

                                Date now = new Date();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd/HH/mm");
                                String date = dateFormat.format(now);

                                mAttentionTask = new AttentionTask();
                                Map<String, String> map = new HashMap<>();// 请求参数
                                map.put("username", usernameThis);
                                map.put("username2", USERNAME);
                                map.put("card_id", "");
                                map.put("message", "");
                                map.put("date", date);
                                map.put("type", "GUANZHU");
                                mAttentionTask.executeOnExecutor(exec, map);
                            } else {
                                showSnackbar(mOKRecyclerView, "您不能关注自己", "");
                            }
                        } else {
                            startUserActivity(null, OKLoginActivity.class);
                        }
                        popWindow.dismiss();
                        break;
                    case R.id.SHOW_POP_JUBAO_LAYOU:// 举报该用户
                        if (!WhetherThis) {
                            Bundle bundle = new Bundle();
                            bundle.putString("JUBAO_TYPE", "USER");
                            bundle.putString("JUBAO_NAME", USERNAME);
                            startUserActivity(bundle, OKRePortActivity.class);
                        } else {
                            showSnackbar(mOKRecyclerView, "您不能举报自己", "");
                        }
                        popWindow.dismiss();
                        break;
                }
            }
        };
        linearLayoutAttention.setOnClickListener(listener);
        linearLayoutQrCode.setOnClickListener(listener);
        buttonClose.setOnClickListener(listener);
        ColorDrawable dw = new ColorDrawable(0x30000000);
        popWindow.setBackgroundDrawable(dw);
        popWindow.showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        OKCardBean mCardBean = mCardViewAdapter.getLastCardBean();
        if (mCardBean == null) {
            mRefreshLayout.finishLoadMore(1500);
            return;
        }
        Map<String, String> map = new HashMap<>();// 请求参数,历史界面无需请求参数,直接获取数据库数据的
        map.put("username", USERNAME);
        map.put("max_id", Integer.toString(mCardBean.getCARD_ID()));
        map.put("load_type", "USER_CARD");
        if (mOKLoadHomeApi == null) {
            mOKLoadHomeApi = new OKLoadHomeApi(this, true);
        }
        mOKLoadHomeApi.requestCardBeanList(map, true, this);
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        if (OKNetUtil.isNet(this)) {
            Map<String, String> map = new HashMap<>();// 请求参数
            map.put("username", USERNAME);
            map.put("num", OKConstant.HOME_LOAD_COUNT);
            if (mOKLoadHomeApi == null) {
                mOKLoadHomeApi = new OKLoadHomeApi(this, false);
            }
            mOKLoadHomeApi.requestCardBeanList(map, false, this);
        } else {
            mRefreshLayout.finishRefresh(1500);
            showSnackbar(mOKRecyclerView, "请检查用户状态和网络设置!", "");
        }
    }

    private View initHeaderView() {
        // 获取头布局
        HeaderView = LayoutInflater.from(this).inflate(R.layout.ok_home_page_header, null);
        HeaderTextViewArticlesNum = (TextView) HeaderView.findViewById(R.id.SHOW_HEADER_WENZHAN);
        HeaderTextViewAttentionNum = (TextView) HeaderView.findViewById(R.id.SHOW_HEADER_GUANZHU);
        HeaderTextViewWatchNum = (TextView) HeaderView.findViewById(R.id.SHOW_HEADER_SHOUCHAN);
        HeaderTextViewValueNum = (TextView) HeaderView.findViewById(R.id.SHOW_HEADER_JIAZHI);

        HeaderTextViewArticlesTitle = (TextView) HeaderView.findViewById(R.id.SHOW_HEADER_WENZHAN_BIAOTI);
        HeaderTextViewAttentionTitle = (TextView) HeaderView.findViewById(R.id.SHOW_HEADER_GUANZHU_BIAOTI);
        HeaderTextViewWatchTitle = (TextView) HeaderView.findViewById(R.id.SHOW_HEADER_SHOUCHAN_BIAOTI);
        HeaderTextViewValueTitle = (TextView) HeaderView.findViewById(R.id.SHOW_HEADER_JIAZHI_BIAOTI);
        if (WhetherThis) {
            HeaderTextViewArticlesTitle.setText("我的文章数量");
            HeaderTextViewAttentionTitle.setText("我的关注人数");
            HeaderTextViewWatchTitle.setText("我的收藏数量");
            HeaderTextViewValueTitle.setText("我的账号知值");
        }
        HeaderTextViewArticlesNum.setText("0");
        HeaderTextViewAttentionNum.setText("0");
        HeaderTextViewWatchNum.setText("0");
        HeaderTextViewValueNum.setText("0");

        return HeaderView;
    }

    public void bindHeaderView(OKUserInfoBean mOKUserInfoBean) {
        if (mOKUserInfoBean == null) {
            return;
        }
        HeaderTextViewArticlesNum.setText("" + mOKUserInfoBean.getWENZHAN());
        HeaderTextViewAttentionNum.setText("" + mOKUserInfoBean.getGUANZHU());
        HeaderTextViewWatchNum.setText("" + mOKUserInfoBean.getSHOUCHAN());
        HeaderTextViewValueNum.setText("" + mOKUserInfoBean.getJIFENG());
    }

    @Override
    public void homeApiComplete(List<OKCardBean> list) {
        if (list != null) {
            if (mRefreshLayout.getState() == RefreshState.Refreshing) {
                mCardBeanList.clear();
                mCardBeanList.addAll(list);
            } else if (mRefreshLayout.getState() == RefreshState.Loading) {
                mCardBeanList.addAll(list);
            }
            OKConstant.putListCache(INTERFACE_HOME, mCardBeanList);
            mOKRecyclerView.getAdapter().notifyDataSetChanged();
        }

        if (mRefreshLayout.getState() == RefreshState.Refreshing) {
            mRefreshLayout.finishRefresh();
        } else if (mRefreshLayout.getState() == RefreshState.Loading) {
            mRefreshLayout.finishLoadMore();
        }
    }

    private class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.CardViewHolder> {
        private Context mContext;
        private List<OKCardBean> mBeanList;

        public CardViewAdapter(Context context, List<OKCardBean> mCardBeanList) {
            this.mContext = context;
            this.mBeanList = mCardBeanList;
        }

        private void initViews(final CardViewHolder mCardViewHolder, final OKCardBean okCardBean, final int position) {
            mCardViewHolder.setListPosition(position);

            String cardType = okCardBean.getCARD_TYPE();
            // 设置标题控件内容
            GlideRoundApi(mCardViewHolder.mImageViewAvatar, okCardBean.getTITLE_IMAGE_URL(), R.drawable.touxian_placeholder, R.drawable.touxian_placeholder);
            mCardViewHolder.mTextViewTitle.setText(okCardBean.getTITLE_TEXT());
            mCardViewHolder.mTextViewDate.setText(formatTime(okCardBean.getCREATE_DATE()) + " 发表");
            // 设置内容控件内容
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
                        bundle.putInt(INTENT_KEY_INTERFACE_TYPE, INTERFACE_HOME);
                        bundle.putInt(INTENT_KEY_LIST_POSITION, position);
                        bundle.putInt(INTENT_KEY_LIST_CARD_ID, okCardBean.getCARD_ID());
                        startUserActivity(bundle, OKCardTWActivity.class);
                    } else if (okCardBean.getCARD_TYPE().equals(CARD_TYPE_TP)) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(INTENT_KEY_INTERFACE_TYPE, INTERFACE_HOME);
                        bundle.putInt(INTENT_KEY_LIST_POSITION, position);
                        bundle.putInt(INTENT_KEY_LIST_CARD_ID, okCardBean.getCARD_ID());
                        startUserActivity(bundle, OKCardTPActivity.class);
                    } else if (okCardBean.getCARD_TYPE().equals(CARD_TYPE_WZ)) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(INTENT_KEY_INTERFACE_TYPE, INTERFACE_HOME);
                        bundle.putInt(INTENT_KEY_LIST_POSITION, position);
                        bundle.putInt(INTENT_KEY_LIST_CARD_ID, okCardBean.getCARD_ID());
                        startUserActivity(bundle, OKCardWZActivity.class);
                    }
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

        public OKCardBean getLastCardBean() {
            if (mBeanList != null && mBeanList.size() != 0) {
                return mBeanList.get(mBeanList.size() - 1);
            } else {
                return null;
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

                mImageViewDelete.setVisibility(View.GONE);
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

    private class LoadUserTask extends AsyncTask<Map<String, String>, Void, OKUserInfoBean> {

        @Override
        protected void onPostExecute(OKUserInfoBean userInfoBean) {
            if (isCancelled()) {
                return;
            }
            if (userInfoBean == null) {
                showSnackbar(mOKRecyclerView, "没有获取到用户信息", "");
                return;
            }
            mBindUserInfoBean = userInfoBean;
            GlideRoundApi(ImageViewTouXian, userInfoBean.getHEADPORTRAIT_URL(), R.drawable.touxian_placeholder_hd, R.drawable.touxian_placeholder_hd);
            GlideBlurApi(mImageViewHead, userInfoBean.getHEADPORTRAIT_URL(), R.drawable.topgd3, R.drawable.topgd3);
            mToolbarTitle.setText(userInfoBean.getNICKNAME());
            if (!TextUtils.isEmpty(userInfoBean.getQIANMIN()) && !userInfoBean.getQIANMIN().equals("NULL")) {
                textViewTAG.setText(userInfoBean.getQIANMIN());
            } else {
                textViewTAG.setText("这个人很懒,什么都没有留下!");
            }
            bindHeaderView(userInfoBean);
        }

        @Override
        protected OKUserInfoBean doInBackground(Map<String, String>... params) {
            if (isCancelled()) {
                return null;
            }

            OKBusinessNet mWebApi = new OKBusinessNet();
            if (isCancelled()) {
                return null;
            }
            OKUserInfoBean mUserInfoBean = mWebApi.getUserInfo(params[0]);
            if (mUserInfoBean != null) {
                return mUserInfoBean;
            }
            return null;
        }
    }

    private class AttentionTask extends AsyncTask<Map<String, String>, Void, Boolean> {

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (isCancelled()) {
                return;
            }
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                showSnackbar(mOKRecyclerView, "已关注该用户", "");
            } else {
                showSnackbar(mOKRecyclerView, "服务器错误", "ErrorCode: " + OKConstant.SERVICE_ERROR);
            }
        }

        @Override
        protected Boolean doInBackground(Map<String, String>... params) {
            if (isCancelled()) {
                return false;
            }

            OKBusinessNet mWebApi = new OKBusinessNet();
            return mWebApi.updateCardInfo(params[0]);
        }
    }
}
