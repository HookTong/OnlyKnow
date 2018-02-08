package com.onlyknow.app.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class OKSDCardUtil {
    public static String SDCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;

    /**
     * 检查是否存在SDCard
     *
     * @return
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获得文章图片保存路径
     *
     * @return
     */
    public static String getPictureDir() {
        String imageCacheUrl = SDCardRoot + "XRichText" + File.separator;
        File file = new File(imageCacheUrl);
        if (!file.exists())
            file.mkdir();  //如果不存在则创建
        return imageCacheUrl;
    }

    /**
     * 图片保存到SD卡
     *
     * @param bitmap
     * @return
     */
    public static String saveToSdCard(Bitmap bitmap) {
        String imageUrl = getPictureDir() + System.currentTimeMillis() + "-";
        File file = new File(imageUrl);
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    /**
     * 保存到指定路径，笔记中插入图片
     *
     * @param bitmap
     * @param path
     * @return
     */
    public static String saveToSdCard(Bitmap bitmap, String path) {
        File file = new File(path);
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println("文件保存路径："+ file.getAbsolutePath());
        return file.getAbsolutePath();
    }

    /**
     * 删除文件
     **/
    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists())
            file.delete(); // 删除文件
    }

    /**
     * 根据Uri获取图片文件的绝对路径
     */
    public static String getFilePathByImageUri(Context context, final Uri uri) {
        try {
            if (null == uri) {
                return null;
            }
            final String scheme = uri.getScheme();
            String data = "";
            if (scheme == null) {
                data = uri.getPath();
            } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
                data = uri.getPath();
            } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
                Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
                if (null != cursor) {
                    if (cursor.moveToFirst()) {
                        int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                        if (index > -1) {
                            data = cursor.getString(index);
                        }
                    }
                    cursor.close();
                }
            }
            return data;
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public static Uri getUriByFilePath(Context context, String filePath) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ", new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (new File(filePath).exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public static boolean copyFile(String oldPath, String newPath) {
        try {
            int byteRead = 0;
            File oldFile = new File(oldPath);
            if (oldFile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                while ((byteRead = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteRead);
                }
                inStream.close();
                if (new File(newPath).exists()) {
                    return true;
                }
            }
        } catch (Exception e) {
            OKLogUtil.print("复制文件操作出错");
            e.printStackTrace();
        }
        return false;
    }
}
