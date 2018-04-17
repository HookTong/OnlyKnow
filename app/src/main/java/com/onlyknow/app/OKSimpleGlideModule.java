package com.onlyknow.app;

import android.content.Context;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.AppGlideModule;

@GlideModule
public class OKSimpleGlideModule extends AppGlideModule {
    private static final int DISK_CACHE_SIZE = 100 * 1024 * 1024;
    public static final int MAX_MEMORY_CACHE_SIZE = 20 * 1024 * 1024;

    @Override
    public void applyOptions(Context context, GlideBuilder glideBuilder) {
        glideBuilder.setDiskCache(new InternalCacheDiskCacheFactory(context, DISK_CACHE_SIZE));
        //设置内存缓存大小，一般默认使用glide内部的默认值
        glideBuilder.setMemoryCache(new LruResourceCache(MAX_MEMORY_CACHE_SIZE));
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
