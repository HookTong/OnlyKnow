package com.onlyknow.mediapicker.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.onlyknow.mediapicker.R;
import com.onlyknow.mediapicker.bean.FolderBean;
import com.onlyknow.mediapicker.bean.MediaBean;

import java.util.ArrayList;

/**
 * Created by dmcBig on 2017/7/19.
 */

public class FolderAdapter extends BaseAdapter {
    ArrayList<FolderBean> folderBeans;
    private LayoutInflater mInflater;
    private Context mContext;
    int lastSelected = 0;

    public FolderAdapter(ArrayList<FolderBean> folderBeans, Context context) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.folderBeans = folderBeans;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return folderBeans.size();
    }

    @Override
    public FolderBean getItem(int position) {
        return folderBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void updateAdapter(ArrayList<FolderBean> list) {
        this.folderBeans = list;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = mInflater.inflate(R.layout.item_folders_view, viewGroup, false);
            holder = new ViewHolder(view);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        FolderBean folderBean = getItem(position);
        MediaBean mediaBean;
        if (folderBean.getMediaBeans().size() > 0) {
            mediaBean = folderBean.getMediaBeans().get(0);
            Glide.with(mContext)
                    .load(Uri.parse("file://" + mediaBean.path))
                    .into(holder.cover);
        } else {
            holder.cover.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.default_image));
        }

        holder.name.setText(folderBean.name);

        holder.size.setText(folderBean.getMediaBeans().size() + "" + mContext.getString(R.string.count_string));
        holder.indicator.setVisibility(lastSelected == position ? View.VISIBLE : View.INVISIBLE);
        return view;
    }

    public void setSelectIndex(int i) {
        if (lastSelected == i) return;
        lastSelected = i;
        notifyDataSetChanged();
    }

    public ArrayList<MediaBean> getSelectMedias() {
        return folderBeans.get(lastSelected).getMediaBeans();
    }

    class ViewHolder {
        ImageView cover, indicator;
        TextView name, path, size;

        ViewHolder(View view) {
            cover = (ImageView) view.findViewById(R.id.cover);
            name = (TextView) view.findViewById(R.id.name);
            path = (TextView) view.findViewById(R.id.path);
            size = (TextView) view.findViewById(R.id.size);
            indicator = (ImageView) view.findViewById(R.id.indicator);
            view.setTag(this);
        }
    }
}
