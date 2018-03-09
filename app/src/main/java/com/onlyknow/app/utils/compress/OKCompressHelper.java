package com.onlyknow.app.utils.compress;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.onlyknow.app.utils.OKFileUtil;

import java.io.File;

/**
 * 压缩方法工具类
 * <p>
 * Author: ReSet
 * Date: 2018-03-09
 */

public class OKCompressHelper {
    private static volatile OKCompressHelper INSTANCE;

    private Context context;
    /**
     * 最大宽度，默认为720
     */
    private float maxWidth = 720.0f;
    /**
     * 最大高度,默认为960
     */
    private float maxHeight = 960.0f;
    /**
     * 默认压缩后的方式为JPEG
     */
    private Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;

    /**
     * 默认的图片处理方式是ARGB_8888
     */
    private Bitmap.Config bitmapConfig = Bitmap.Config.ARGB_8888;
    /**
     * 默认压缩质量为80
     */
    private int quality = 80;
    /**
     * 存储路径
     */
    private String destinationDirectoryPath;
    /**
     * 文件名前缀
     */
    private String fileNamePrefix;
    /**
     * 文件名
     */
    private String fileName;

    public static OKCompressHelper getDefault(Context context) {
        if (INSTANCE == null) {
            synchronized (OKCompressHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new OKCompressHelper(context);
                }
            }
        }
        return INSTANCE;
    }


    private OKCompressHelper(Context context) {
        this.context = context;
        destinationDirectoryPath = context.getCacheDir().getPath() + File.pathSeparator + OKFileUtil.FILES_PATH;
    }

    /**
     * 压缩成文件
     *
     * @param file 原始文件
     * @return 压缩后的文件
     */
    public File compressToFile(File file) {
        if (OKFileUtil.isVideoFile(file.getName()) || OKFileUtil.isGifFile(file.getName())) {
            return file;
        }

        return OKBitmapUtil.compressImage(context, Uri.fromFile(file), maxWidth, maxHeight,
                compressFormat, bitmapConfig, quality, destinationDirectoryPath,
                fileNamePrefix, fileName);
    }

    /**
     * 压缩为Bitmap
     *
     * @param file 原始文件
     * @return 压缩后的Bitmap
     */
    public Bitmap compressToBitmap(File file) {
        return OKBitmapUtil.getScaledBitmap(context, Uri.fromFile(file), maxWidth, maxHeight, bitmapConfig);
    }


    /**
     * 采用建造者模式，设置Builder
     */
    public static class Builder {
        private OKCompressHelper mOKCompressHelper;

        public Builder(Context context) {
            mOKCompressHelper = new OKCompressHelper(context);
        }

        /**
         * 设置图片最大宽度
         *
         * @param maxWidth 最大宽度
         */
        public Builder setMaxWidth(float maxWidth) {
            mOKCompressHelper.maxWidth = maxWidth;
            return this;
        }

        /**
         * 设置图片最大高度
         *
         * @param maxHeight 最大高度
         */
        public Builder setMaxHeight(float maxHeight) {
            mOKCompressHelper.maxHeight = maxHeight;
            return this;
        }

        /**
         * 设置压缩的后缀格式
         */
        public Builder setCompressFormat(Bitmap.CompressFormat compressFormat) {
            mOKCompressHelper.compressFormat = compressFormat;
            return this;
        }

        /**
         * 设置Bitmap的参数
         */
        public Builder setBitmapConfig(Bitmap.Config bitmapConfig) {
            mOKCompressHelper.bitmapConfig = bitmapConfig;
            return this;
        }

        /**
         * 设置压缩质量，建议80
         *
         * @param quality 压缩质量，[0,100]
         */
        public Builder setQuality(int quality) {
            mOKCompressHelper.quality = quality;
            return this;
        }

        /**
         * 设置目的存储路径
         *
         * @param destinationDirectoryPath 目的路径
         */
        public Builder setDestinationDirectoryPath(String destinationDirectoryPath) {
            mOKCompressHelper.destinationDirectoryPath = destinationDirectoryPath;
            return this;
        }

        /**
         * 设置文件前缀
         *
         * @param prefix 前缀
         */
        public Builder setFileNamePrefix(String prefix) {
            mOKCompressHelper.fileNamePrefix = prefix;
            return this;
        }

        /**
         * 设置文件名称
         *
         * @param fileName 文件名
         */
        public Builder setFileName(String fileName) {
            mOKCompressHelper.fileName = fileName;
            return this;
        }

        public OKCompressHelper build() {
            return mOKCompressHelper;
        }
    }
}
