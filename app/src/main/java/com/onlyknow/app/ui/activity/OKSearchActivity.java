package com.onlyknow.app.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.onlyknow.app.OKConstant;
import com.onlyknow.app.R;
import com.onlyknow.app.api.OKLoadSearchApi;
import com.onlyknow.app.database.bean.OKCardBean;
import com.onlyknow.app.database.bean.OKSearchBean;
import com.onlyknow.app.database.bean.OKUserInfoBean;
import com.onlyknow.app.ui.OKBaseActivity;
import com.onlyknow.app.ui.view.OKRecyclerView;
import com.onlyknow.app.ui.view.OKSEImageView;
import com.onlyknow.app.utils.OKLogUtil;
import com.onlyknow.app.utils.OKNetUtil;
import com.scwang.smartrefresh.header.TaurusHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OKSearchActivity extends OKBaseActivity implements OnRefreshListener {
    private Toolbar searchToolbar;
    private OKSEImageView buttonBack;
    private EditText searchEditText;
    private OKSEImageView searchOKSEImageView;
    private TextView textViewTS;
    private RefreshLayout mRefreshLayout;
    private OKRecyclerView mOKRecyclerView;

    private OKLoadSearchApi mOKLoadSearchApi;
    private EntryViewAdapter mEntryViewAdapter;
    private List<OKSearchBean> mOKSearchBeanList = new ArrayList<>();
    private OKSearchBean.SEARCH_TYPE mSearchType;
    private String mSearchMsg;
    private int interfaceType;

    private OKLoadSearchApi.onCallBack mOnCallBack = new OKLoadSearchApi.onCallBack() {
        @Override
        public void cardList(List<OKSearchBean> list) {
            if (list != null) {
                mOKSearchBeanList.clear();
                mOKSearchBeanList.addAll(list);
                OKConstant.putListCache(INTERFACE_SEARCH, mOKSearchBeanList);
                mOKRecyclerView.getAdapter().notifyDataSetChanged();

                mSearchMsg = "";
                searchEditText.setText("");
            } else {
                mOKSearchBeanList.clear();
                OKConstant.putListCache(INTERFACE_SEARCH, mOKSearchBeanList);
                mOKRecyclerView.getAdapter().notifyDataSetChanged();
                showSnackbar(searchEditText, "没有搜索到数据", "");
            }
            mRefreshLayout.finishRefresh();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ok_activity_search);
        initSystemBar(this);
        initUserInfoSharedPreferences();
        interfaceType = getIntent().getExtras().getInt(INTENT_KEY_INTERFACE_TYPE);

        findView();
        init();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (searchToolbar != null) {
            searchToolbar.setTitle("");
        }
    }

    private void findView() {
        searchToolbar = (Toolbar) findViewById(R.id.SEARCH_toolbar);
        buttonBack = (OKSEImageView) findViewById(R.id.SEARCH_huitui_but);
        searchEditText = (EditText) findViewById(R.id.ok_activity_search_edit);
        searchOKSEImageView = (OKSEImageView) findViewById(R.id.ok_activity_search_image);
        textViewTS = (TextView) findViewById(R.id.SEARCH_textview);
        mOKRecyclerView = (OKRecyclerView) findViewById(R.id.ok_content_collapsing_RecyclerView);
        mRefreshLayout = (RefreshLayout) findViewById(R.id.ok_content_collapsing_refresh);

        setSupportActionBar(searchToolbar);
        mOKRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRefreshLayout.setRefreshHeader(new TaurusHeader(this));
        mRefreshLayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale));
        mRefreshLayout.setOnRefreshListener(this);

        switch (interfaceType) {
            case INTERFACE_EXPLORE:
                mSearchType = OKSearchBean.SEARCH_TYPE.ALL;
                textViewTS.setText("当前搜索位置: 探索模块");
                break;
            case INTERFACE_NEAR:
                mSearchType = OKSearchBean.SEARCH_TYPE.ALL;
                textViewTS.setText("当前搜索位置: 附近模块");
                break;
            case INTERFACE_HISTORY:
                mSearchType = OKSearchBean.SEARCH_TYPE.ALL;
                textViewTS.setText("当前搜索位置: 历史模块");
                break;
            case INTERFACE_NOTICE:
                mSearchType = OKSearchBean.SEARCH_TYPE.USER;
                textViewTS.setText("当前搜索位置: 用户查找");
                break;
            default:
                finish();
                return;
        }
    }

    private void init() {
        mEntryViewAdapter = new EntryViewAdapter(this, mOKSearchBeanList);
        mOKRecyclerView.setAdapter(mEntryViewAdapter);

        buttonBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        searchOKSEImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(searchEditText.getText().toString())) {
                    mRefreshLayout.autoRefresh();
                } else {
                    showSnackbar(searchEditText, "请输入要搜索的信息", "");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mRefreshLayout.finishRefresh();
        if (mOKLoadSearchApi != null) {
            mOKLoadSearchApi.cancelTask();
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        mSearchMsg = searchEditText.getText().toString();
        if (TextUtils.isEmpty(mSearchMsg)) {
            mRefreshLayout.finishRefresh(2000);
            return;
        }
        if (OKNetUtil.isNet(this)) {
            Map<String, String> map = new HashMap<>();
            map.put("username", "");
            if (USER_INFO_SP.getBoolean("STATE", false)) {
                map.put("username", USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
            }
            map.put("searchMsg", mSearchMsg);
            map.put("type", mSearchType.toString());

            if (mOKLoadSearchApi != null) {
                mOKLoadSearchApi.cancelTask();
            }
            mOKLoadSearchApi = new OKLoadSearchApi(this);
            mOKLoadSearchApi.requestSearchBeanList(map, mOnCallBack);
        } else {
            mRefreshLayout.finishRefresh(1500);
            showSnackbar(searchEditText, "没有网络连接", "");
        }
    }

    private class EntryViewAdapter extends RecyclerView.Adapter<EntryViewAdapter.EntryViewHolder> {
        private Context mContext;
        private List<OKSearchBean> mBeanList;

        public EntryViewAdapter(Context context, List<OKSearchBean> list) {
            this.mContext = context;
            this.mBeanList = list;
        }

        private void initViews(final EntryViewHolder mEntryViewHolder, final OKSearchBean okSearchBean, final int position) {
            mEntryViewHolder.setListPosition(position);

            if (okSearchBean.getType() == OKSearchBean.SEARCH_TYPE.CARD) {
                OKCardBean bean = okSearchBean.getCardBean();
                if (bean == null) {
                    OKLogUtil.print("数据为空,类型不匹配!");
                    return;
                }
                if (bean.getCARD_TYPE().equals(OKCardBean.CardType.IMAGE.toString())) {
                    mEntryViewHolder.mTextViewTitle.setText("精彩图片");
                    mEntryViewHolder.mTextViewContent.setText(bean.getTITLE_TEXT() + " 发表");
                    mEntryViewHolder.mTextViewDate.setText(formatTime(bean.getCREATE_DATE()));
                } else {
                    mEntryViewHolder.mTextViewTitle.setText(bean.getCONTENT_TITLE_TEXT());
                    mEntryViewHolder.mTextViewContent.setText(bean.getCONTENT_TEXT());
                    mEntryViewHolder.mTextViewDate.setText(formatTime(bean.getCREATE_DATE()));
                }
                GlideApi(mEntryViewHolder.mImageViewTitle, R.drawable.search_card, R.drawable.search_card, R.drawable.search_card);
            } else if (okSearchBean.getType() == OKSearchBean.SEARCH_TYPE.USER) {
                OKUserInfoBean bean = okSearchBean.getUserInfoBean();
                if (bean == null) {
                    OKLogUtil.print("数据为空,类型不匹配!");
                    return;
                }
                mEntryViewHolder.mTextViewTitle.setText(bean.getNICKNAME());
                if (TextUtils.isEmpty(bean.getQIANMIN()) || bean.getQIANMIN().equals("NULL")) {
                    mEntryViewHolder.mTextViewContent.setText("这个人很懒,什么都没留下!");
                } else {
                    mEntryViewHolder.mTextViewContent.setText(bean.getQIANMIN());
                }
                mEntryViewHolder.mTextViewDate.setVisibility(View.GONE);
                String url = bean.getHEADPORTRAIT_URL();
                GlideRoundApi(mEntryViewHolder.mImageViewTitle, url, R.drawable.touxian_placeholder, R.drawable.touxian_placeholder);
            }

            mEntryViewHolder.mCardView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (okSearchBean.getType() == OKSearchBean.SEARCH_TYPE.CARD) {
                        OKCardBean bean = okSearchBean.getCardBean();
                        if (bean == null) {
                            showSnackbar(mOKRecyclerView, "数据错误,无法查看", "");
                            return;
                        }
                        if (bean.getCARD_TYPE().equals(CARD_TYPE_TW)) {
                            Bundle bundle = new Bundle();
                            bundle.putInt(INTENT_KEY_INTERFACE_TYPE, INTERFACE_SEARCH);
                            bundle.putInt(INTENT_KEY_LIST_POSITION, position);
                            bundle.putInt(INTENT_KEY_LIST_CARD_ID, bean.getCARD_ID());
                            startUserActivity(bundle, OKCardTWActivity.class);
                        } else if (bean.getCARD_TYPE().equals(CARD_TYPE_TP)) {
                            Bundle bundle = new Bundle();
                            bundle.putInt(INTENT_KEY_INTERFACE_TYPE, INTERFACE_SEARCH);
                            bundle.putInt(INTENT_KEY_LIST_POSITION, position);
                            bundle.putInt(INTENT_KEY_LIST_CARD_ID, bean.getCARD_ID());
                            startUserActivity(bundle, OKCardTPActivity.class);
                        } else if (bean.getCARD_TYPE().equals(CARD_TYPE_WZ)) {
                            Bundle bundle = new Bundle();
                            bundle.putInt(INTENT_KEY_INTERFACE_TYPE, INTERFACE_SEARCH);
                            bundle.putInt(INTENT_KEY_LIST_POSITION, position);
                            bundle.putInt(INTENT_KEY_LIST_CARD_ID, bean.getCARD_ID());
                            startUserActivity(bundle, OKCardWZActivity.class);
                        }
                    } else if (okSearchBean.getType() == OKSearchBean.SEARCH_TYPE.USER) {
                        OKUserInfoBean bean = okSearchBean.getUserInfoBean();
                        if (bean == null) {
                            showSnackbar(mOKRecyclerView, "数据错误,无法查看", "");
                            return;
                        }
                        Bundle mBundle = new Bundle();
                        mBundle.putString(OKUserInfoBean.KEY_USERNAME, bean.getUSERNAME());
                        mBundle.putString(OKUserInfoBean.KEY_NICKNAME, bean.getNICKNAME());
                        startUserActivity(mBundle, OKHomePageActivity.class);
                    }
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
}
