package com.onlyknow.mediapicker.data;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.onlyknow.mediapicker.R;
import com.onlyknow.mediapicker.bean.FolderBean;
import com.onlyknow.mediapicker.bean.MediaBean;

import java.util.ArrayList;

/**
 * Created by dmcBig on 2017/7/3.
 */

public class ImageLoader extends LoaderM implements LoaderManager.LoaderCallbacks {

    String[] IMAGE_PROJECTION = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media._ID};

    Context mContext;
    DataCallback mLoader;

    public ImageLoader(Context context, DataCallback loader) {
        this.mContext = context;
        this.mLoader = loader;
    }

    @Override
    public Loader onCreateLoader(int picker_type, Bundle bundle) {
        Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        CursorLoader cursorLoader = new CursorLoader(
                mContext,
                queryUri,
                IMAGE_PROJECTION,
                null,
                null, // Selection args (none).
                MediaStore.Images.Media.DATE_ADDED + " DESC" // Sort order.
        );
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader loader, Object o) {
        ArrayList<FolderBean> folderBeans = new ArrayList<>();
        FolderBean allFolderBean = new FolderBean(mContext.getResources().getString(R.string.all_image));
        folderBeans.add(allFolderBean);
        Cursor cursor = (Cursor) o;
        while (cursor.moveToNext()) {

            String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
            long dateTime = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED));
            int mediaType = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE));
            long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));

            if (size < 1) continue;
            String dirName = getParent(path);
            MediaBean mediaBean = new MediaBean(path, name, dateTime, mediaType, size, id, dirName);
            allFolderBean.addMedias(mediaBean);

            int index = hasDir(folderBeans, dirName);
            if (index != -1) {
                folderBeans.get(index).addMedias(mediaBean);
            } else {
                FolderBean folderBean = new FolderBean(dirName);
                folderBean.addMedias(mediaBean);
                folderBeans.add(folderBean);
            }
        }
        mLoader.onData(folderBeans);
        cursor.close();
    }

    @Override
    public void onLoaderReset(Loader loader) {
    }
}