package com.onlyknow.mediapicker.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by dmcBig on 2017/7/4.
 */

public class FolderBean implements Parcelable {
    public String name;

    public int count;

    ArrayList<MediaBean> mediaBeans = new ArrayList<>();

    public void addMedias(MediaBean mediaBean) {
        mediaBeans.add(mediaBean);
    }

    public FolderBean(String name) {
        this.name = name;
    }

    public ArrayList<MediaBean> getMediaBeans() {
        return this.mediaBeans;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeInt(this.count);
        dest.writeTypedList(this.mediaBeans);
    }


    protected FolderBean(Parcel in) {
        this.name = in.readString();
        this.count = in.readInt();
        this.mediaBeans = in.createTypedArrayList(MediaBean.CREATOR);
    }

    public static final Parcelable.Creator<FolderBean> CREATOR = new Parcelable.Creator<FolderBean>() {
        @Override
        public FolderBean createFromParcel(Parcel source) {
            return new FolderBean(source);
        }

        @Override
        public FolderBean[] newArray(int size) {
            return new FolderBean[size];
        }
    };
}
