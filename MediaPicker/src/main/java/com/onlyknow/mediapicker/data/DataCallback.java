package com.onlyknow.mediapicker.data;

import com.onlyknow.mediapicker.bean.FolderBean;

import java.util.ArrayList;


/**
 * Created by dmcBig on 2017/7/3.
 */

public interface DataCallback {
    void onData(ArrayList<FolderBean> list);
}
