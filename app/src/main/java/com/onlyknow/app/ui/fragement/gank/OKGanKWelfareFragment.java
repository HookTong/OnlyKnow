package com.onlyknow.app.ui.fragement.gank;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.onlyknow.app.R;
import com.onlyknow.app.api.app.OKLoadGanKApi;
import com.onlyknow.app.db.bean.OKGanKBean;
import com.onlyknow.app.ui.OKBaseFragment;
import com.onlyknow.app.ui.activity.OKDragPhotoActivity;
import com.onlyknow.app.ui.view.OKRecyclerView;
import com.onlyknow.app.utils.OKNetUtil;
import com.scwang.smartrefresh.header.TaurusHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/2/6.
 */

public class OKGanKWelfareFragment extends OKBaseFragment implements OnRefreshListener, OnLoadMoreListener, OKLoadGanKApi.onCallBack {
    @Bind(R.id.ok_content_collapsing_RecyclerView)
    OKRecyclerView mOKRecyclerView;
    @Bind(R.id.ok_content_collapsing_refresh)
    SmartRefreshLayout mRefreshLayout;

    private GanKViewAdapter mGanKViewAdapter;

    private OKLoadGanKApi mOKLoadGanKApi;
    private List<OKGanKBean.Results> mGanKBeanList = new ArrayList<>();

    private View rootView;

    private int page = 1;

    private boolean isPause = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.ok_fragment_universal, container, false);
            ButterKnife.bind(this, rootView);
            init();
            return rootView;
        } else {
            ButterKnife.bind(this, rootView);
            return rootView;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != rootView) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
        ButterKnife.unbind(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        isPause = true;
        mRefreshLayout.finishRefresh();
        mRefreshLayout.finishLoadMore();
        if (mOKLoadGanKApi != null) {
            mOKLoadGanKApi.cancelTask();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isPause = false;
    }

    private void init() {
        mGanKViewAdapter = new GanKViewAdapter(getActivity(), mGanKBeanList);
        mOKRecyclerView.setAdapter(mGanKViewAdapter);
        mOKLoadGanKApi = new OKLoadGanKApi(getActivity());
        mOKRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mRefreshLayout.setRefreshHeader(new TaurusHeader(getActivity()));
        mRefreshLayout.setRefreshFooter(new BallPulseFooter(getActivity()).setSpinnerStyle(SpinnerStyle.Scale));
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setOnLoadMoreListener(this);
        mRefreshLayout.autoRefresh();
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        if (OKNetUtil.isNet(getActivity())) {
            page++;
            mOKLoadGanKApi.requestGanK(OKLoadGanKApi.WELFARE_URL + page, this);
        } else {
            mRefreshLayout.finishLoadMore(1500);
            showSnackBar(mOKRecyclerView, "没有网络连接!", "");
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        if (OKNetUtil.isNet(getActivity())) {
            mOKLoadGanKApi.requestGanK(OKLoadGanKApi.WELFARE_URL + "1", this);
        } else {
            mRefreshLayout.finishRefresh(1500);
            showSnackBar(mOKRecyclerView, "没有网络连接!", "");
        }
    }

    public void stickTop() {
        if (!isPause && mOKRecyclerView.getAdapter().getItemCount() != 0) {
            mOKRecyclerView.scrollToPosition(0);
        }
    }

    @Override
    public void loadGanKComplete(List<OKGanKBean.Results> list) {
        if (list != null) {
            if (mRefreshLayout.getState() == RefreshState.Refreshing) {
                mGanKBeanList.clear();
                mGanKBeanList.addAll(list);
                page = 1;
            } else if (mRefreshLayout.getState() == RefreshState.Loading) {
                mGanKBeanList.addAll(list);
            }
            mOKRecyclerView.getAdapter().notifyDataSetChanged();
        } else {
            if (mRefreshLayout.getState() == RefreshState.Loading) {
                page--;
            }
        }

        if (mRefreshLayout.getState() == RefreshState.Refreshing) {
            mRefreshLayout.finishRefresh();
        } else if (mRefreshLayout.getState() == RefreshState.Loading) {
            mRefreshLayout.finishLoadMore();
        }
    }

    private class GanKViewAdapter extends RecyclerView.Adapter<GanKViewAdapter.GanKViewHolder> {
        private Context mContext;
        private List<OKGanKBean.Results> mBeanList;

        public GanKViewAdapter(Context context, List<OKGanKBean.Results> list) {
            this.mContext = context;
            this.mBeanList = list;
        }

        @Override
        public GanKViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.ok_item_gank_welfare, parent, false);
            return new GanKViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final GanKViewHolder holder, final int position) {
            final OKGanKBean.Results bean = mBeanList.get(position);
            GlideApi(holder.mImageView, bean.getUrl(), R.drawable.topgd2, R.drawable.topgd2);
            holder.mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int location[] = new int[2];
                    holder.mImageView.getLocationOnScreen(location);
                    Bundle mBundle = new Bundle();
                    mBundle.putInt("left", location[0]);
                    mBundle.putInt("top", location[1]);
                    mBundle.putInt("height", holder.mImageView.getHeight());
                    mBundle.putInt("width", holder.mImageView.getWidth());
                    mBundle.putString("url", bean.getUrl());
                    startUserActivity(mBundle, OKDragPhotoActivity.class);
                    getActivity().overridePendingTransition(0, 0);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mBeanList.size();
        }

        class GanKViewHolder extends RecyclerView.ViewHolder {
            public CardView mCardView;
            public ImageView mImageView;

            public GanKViewHolder(View itemView) {
                super(itemView);
                mCardView = itemView.findViewById(R.id.ok_item_gank_welfare_cardView);
                mImageView = itemView.findViewById(R.id.ok_item_gank_welfare_image);
            }
        }
    }
}
