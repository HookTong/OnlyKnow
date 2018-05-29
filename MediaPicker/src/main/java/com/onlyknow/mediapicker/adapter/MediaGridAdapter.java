package com.onlyknow.mediapicker.adapter;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.onlyknow.mediapicker.PickerConfig;
import com.onlyknow.mediapicker.R;
import com.onlyknow.mediapicker.bean.MediaBean;
import com.onlyknow.mediapicker.utils.FileUtil;
import com.onlyknow.mediapicker.utils.ScreenUtil;

import java.util.ArrayList;

/**
 * Created by dmcBig on 2017/7/5.
 */

public class MediaGridAdapter extends RecyclerView.Adapter<MediaGridAdapter.MediaGridViewHolder> {
    private ArrayList<MediaBean> mediaBeans;
    private Context context;
    private FileUtil fileUtil = new FileUtil();
    private ArrayList<MediaBean> selectMediaBeans = new ArrayList<>();
    private long maxSelect, maxSize;

    public MediaGridAdapter(ArrayList<MediaBean> list, Context context, ArrayList<MediaBean> select, int max, long maxSize) {
        if (select != null) {
            this.selectMediaBeans = select;
        }
        this.maxSelect = max;
        this.maxSize = maxSize;
        this.mediaBeans = list;
        this.context = context;
    }

    public class MediaGridViewHolder extends RecyclerView.ViewHolder {
        public ImageView media_image, check_image;
        public View mask_view;
        public TextView textView_size;
        public RelativeLayout video_info;

        public MediaGridViewHolder(View view) {
            super(view);
            media_image = (ImageView) view.findViewById(R.id.media_image);
            check_image = (ImageView) view.findViewById(R.id.check_image);
            mask_view = view.findViewById(R.id.mask_view);
            video_info = (RelativeLayout) view.findViewById(R.id.video_info);
            textView_size = (TextView) view.findViewById(R.id.textView_size);
            itemView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getItemWidth())); //让图片是个正方形
        }
    }

    private int getItemWidth() {
        return (ScreenUtil.getScreenWidth(context) / PickerConfig.GridSpanCount) - PickerConfig.GridSpanCount;
    }

    @Override
    public MediaGridViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_media_view, viewGroup, false);
        MediaGridViewHolder vh = new MediaGridViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final MediaGridViewHolder holder, int position) {
        final MediaBean mediaBean = mediaBeans.get(position);
        Glide.with(context).load(mediaBean.path).into(holder.media_image);
        if (mediaBean.mediaType == 3) {
            holder.video_info.setVisibility(View.VISIBLE);
            holder.textView_size.setText(fileUtil.getSizeByUnit(mediaBean.size));
        } else {
            holder.video_info.setVisibility(View.INVISIBLE);
        }

        int isSelect = isSelect(mediaBean);
        holder.mask_view.setVisibility(isSelect >= 0 ? View.VISIBLE : View.INVISIBLE);
        holder.check_image.setImageDrawable(isSelect >= 0 ? ContextCompat.getDrawable(context, R.drawable.btn_selected) : ContextCompat.getDrawable(context, R.drawable.btn_unselected));


        holder.media_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int isSelect = isSelect(mediaBean);
                if (selectMediaBeans.size() >= maxSelect && isSelect < 0) {
                    Toast.makeText(context, context.getString(R.string.msg_amount_limit), Toast.LENGTH_SHORT).show();
                } else {
                    if (mediaBean.size > maxSize) {
                        Toast.makeText(context, context.getString(R.string.msg_size_limit) + (FileUtil.fileSize(maxSize)), Toast.LENGTH_LONG).show();
                    } else {
                        holder.mask_view.setVisibility(isSelect >= 0 ? View.INVISIBLE : View.VISIBLE);
                        holder.check_image.setImageDrawable(isSelect >= 0 ? ContextCompat.getDrawable(context, R.drawable.btn_unselected) : ContextCompat.getDrawable(context, R.drawable.btn_selected));
                        setSelectMediaBeans(mediaBean);
                        mOnItemClickListener.onItemClick(v, mediaBean, selectMediaBeans);
                    }
                }

            }
        });
    }


    public void setSelectMediaBeans(MediaBean mediaBean) {
        int index = isSelect(mediaBean);
        if (index == -1) {
            selectMediaBeans.add(mediaBean);
        } else {
            selectMediaBeans.remove(index);
        }
    }

    /**
     * @param mediaBean
     * @return 大于等于0 就是表示以选择，返回的是在selectMedias中的下标
     */
    public int isSelect(MediaBean mediaBean) {
        int is = -1;
        if (selectMediaBeans.size() <= 0) {
            return is;
        }
        for (int i = 0; i < selectMediaBeans.size(); i++) {
            MediaBean m = selectMediaBeans.get(i);
            if (m.path.equals(mediaBean.path)) {
                is = i;
                break;
            }
        }
        return is;
    }

    public void updateSelectAdapter(ArrayList<MediaBean> select) {
        if (select != null) {
            this.selectMediaBeans = select;
        }
        notifyDataSetChanged();
    }

    public void updateAdapter(ArrayList<MediaBean> list) {
        this.mediaBeans = list;
        notifyDataSetChanged();
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public ArrayList<MediaBean> getSelectMediaBeans() {
        return selectMediaBeans;
    }

    @Override
    public int getItemCount() {
        return mediaBeans.size();
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, MediaBean data, ArrayList<MediaBean> selectMediaBeans);
    }
}
