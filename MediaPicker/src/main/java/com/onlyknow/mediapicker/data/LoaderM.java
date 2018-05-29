package com.onlyknow.mediapicker.data;

import com.onlyknow.mediapicker.bean.FolderBean;

import java.util.ArrayList;

/**
 * Created by dmcBig on 2017/7/20.
 */

public class LoaderM {

    public String getParent(String path) {
        String sp[] = path.split("/");
        return sp[sp.length - 2];
    }

    public int hasDir(ArrayList<FolderBean> folderBeans, String dirName) {
        for (int i = 0; i < folderBeans.size(); i++) {
            FolderBean folderBean = folderBeans.get(i);
            if (folderBean.name.equals(dirName)) {
                return i;
            }
        }
        return -1;
    }
}
