package com.onlyknow.app.ui.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2018/1/4.
 */

public class OKRecyclerView extends RecyclerView {
    private View mHeaderView;
    private View mFooterView;
    private View mEmptyView;
    private OKBaseAdapter mOKBaseAdapter;

    public OKRecyclerView(Context context) {
        super(context);
    }

    public OKRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OKRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void addHeaderView(View view) {
        mHeaderView = view;
        if (mOKBaseAdapter != null) {
            mOKBaseAdapter.notifyItemInserted(0);
        }
    }

    public void addFooterView(View view) {
        mFooterView = view;
        if (mOKBaseAdapter != null) {
            mOKBaseAdapter.notifyItemInserted(mOKBaseAdapter.getItemCount() - 1);
        }
    }

    public void setEmptyView(View view) {
        mEmptyView = view;
        if (mOKBaseAdapter != null) {
            mOKBaseAdapter.notifyDataSetChanged();
        }
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    public View getFooterView() {
        return mFooterView;
    }

    public View getEmptyView() {
        return mEmptyView;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (adapter != null) {
            mOKBaseAdapter = new OKBaseAdapter(adapter);
        }
        super.setAdapter(mOKBaseAdapter);
    }

    private class OKBaseAdapter extends Adapter<ViewHolder> {
        private Adapter mOriginalAdapter;
        private int ITEM_TYPE_NORMAL = 0;
        private int ITEM_TYPE_HEADER = 1;
        private int ITEM_TYPE_FOOTER = 2;
        private int ITEM_TYPE_EMPTY = 3;

        public OKBaseAdapter(Adapter originalAdapter) {
            mOriginalAdapter = originalAdapter;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == ITEM_TYPE_HEADER) {
                return new OKBaseViewHolder(mHeaderView);
            } else if (viewType == ITEM_TYPE_EMPTY) {
                return new OKBaseViewHolder(mEmptyView);
            } else if (viewType == ITEM_TYPE_FOOTER) {
                return new OKBaseViewHolder(mFooterView);
            } else {
                return mOriginalAdapter.onCreateViewHolder(parent, viewType);
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            int type = getItemViewType(position);
            if (type == ITEM_TYPE_HEADER || type == ITEM_TYPE_FOOTER || type == ITEM_TYPE_EMPTY) {
                return;
            }
            int realPosition = getRealItemPosition(position);
            mOriginalAdapter.onBindViewHolder(holder, realPosition);
        }

        @Override
        public int getItemCount() {
            int itemCount = mOriginalAdapter.getItemCount();
            //加上其他各种View
            if (null != mEmptyView && itemCount == 0) itemCount++;
            if (null != mHeaderView) itemCount++;
            if (null != mFooterView) itemCount++;
            return itemCount;
        }

        @Override
        public int getItemViewType(int position) {
            if (null != mHeaderView && position == 0) return ITEM_TYPE_HEADER;
            if (null != mFooterView && position == getItemCount() - 1) return ITEM_TYPE_FOOTER;
            if (null != mEmptyView && mOriginalAdapter.getItemCount() == 0) return ITEM_TYPE_EMPTY;
            return ITEM_TYPE_NORMAL;
        }

        private int getRealItemPosition(int position) {
            if (null != mHeaderView) {
                return position - 1;
            }
            return position;
        }

        /**
         * ViewHolder 是一个抽象类
         */
        class OKBaseViewHolder extends ViewHolder {

            OKBaseViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
