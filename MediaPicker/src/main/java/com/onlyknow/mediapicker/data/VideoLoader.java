package com.onlyknow.mediapicker.data;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import com.onlyknow.mediapicker.R;
import com.onlyknow.mediapicker.bean.FolderBean;
import com.onlyknow.mediapicker.bean.MediaBean;

import java.util.ArrayList;


/**
 * Created by dmcBig on 2017/6/9.
 */

public class VideoLoader extends LoaderM implements LoaderManager.LoaderCallbacks {
    String[] MEDIA_PROJECTION = {
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.PARENT};

    Context mContext;
    DataCallback mLoader;

    public VideoLoader(Context context, DataCallback loader) {
        this.mContext = context;
        this.mLoader = loader;
    }

    @Override
    public Loader onCreateLoader(int picker_type, Bundle bundle) {
        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

        Uri queryUri = MediaStore.Files.getContentUri("external");
        CursorLoader cursorLoader = new CursorLoader(
                mContext,
                queryUri,
                MEDIA_PROJECTION,
                selection,
                null, // Selection args (none).
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC" // Sort order.
        );
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader loader, Object o) {
        ArrayList<FolderBean> folderBeans = new ArrayList<>();
        FolderBean allFolderBean = new FolderBean(mContext.getResources().getString(R.string.all_video));
        folderBeans.add(allFolderBean);
        Cursor cursor = (Cursor) o;
        Log.e("dmc", cursor.getCount() + "数量数量");
        while (cursor.moveToNext()) {
            String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME));
            long dateTime = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED));
            int mediaType = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE));
            long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE));
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID));

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
