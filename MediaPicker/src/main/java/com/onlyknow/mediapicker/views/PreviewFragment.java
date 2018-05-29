package com.onlyknow.mediapicker.views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.onlyknow.mediapicker.R;
import com.onlyknow.mediapicker.bean.MediaBean;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by dmcBig on 2017/8/16.
 */

public class PreviewFragment extends Fragment {
    private PhotoView mPhotoView;
    ImageView play_view;
    private PhotoViewAttacher mAttacher;

    public static PreviewFragment newInstance(MediaBean mediaBean, String label) {
        PreviewFragment f = new PreviewFragment();
        Bundle b = new Bundle();
        b.putParcelable("mediaBean", mediaBean);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.item_preview_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MediaBean mediaBean = getArguments().getParcelable("mediaBean");
        play_view = (ImageView) view.findViewById(R.id.play_view);
        mPhotoView = (PhotoView) view.findViewById(R.id.photoview);
        mAttacher = new PhotoViewAttacher(mPhotoView);
        mAttacher.setRotatable(true);
        mAttacher.setToRightAngle(true);

        setPlayView(mediaBean);
        Glide.with(getActivity())
                .load(mediaBean.path)
                .into(mPhotoView);
    }

    void setPlayView(final MediaBean mediaBean) {
        if (mediaBean.mediaType == 3) {
            play_view.setVisibility(View.VISIBLE);
            play_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse(mediaBean.path);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "video/*");
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        mAttacher.cleanup();
        super.onDestroyView();
    }
}
