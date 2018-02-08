package com.onlyknow.app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.bumptech.glide.request.target.ViewTarget;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.onlyknow.app.service.OKMainService;
import com.onlyknow.app.utils.OKLogUtil;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2017/12/8.
 */

public class OKOnlyKnowApplication extends Application {
    {
        PlatformConfig.setWeixin("wx967daebe835fbeac", "5bb696d9ccd75a38c8a0bfe0675559b3");
        PlatformConfig.setQQZone("100424468", "c7394704798a158208a74ab60104f0ba");
        PlatformConfig.setSinaWeibo("3921700954", "04b48b094faeb16683c32669824ebdad", "http://sns.whalecloud.com");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        OKLogUtil.print("OKOnlyKnowApplication Start!");

        init();

        startMainService();

        fileMkdirs();

        // OKDatabaseHelper helper = OKDatabaseHelper.getHelper(this);
        // helper.onUpgrade(helper.getWritableDatabase(), helper.getConnectionSource(), 2, 3);
    }

    private void init() {
        ViewTarget.setTagId(R.id.glide_tag);

        // 初始化环信SDK
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(true);
        // 是否自动将消息附件上传到环信服务器，默认为True是使用环信服务器上传下载，如果设为 false，需要开发者自己处理附件消息的上传和下载
        options.setAutoTransferMessageAttachments(true);
        // 是否自动下载附件类消息的缩略图等，默认为 true 这里和上边这个参数相关联
        options.setAutoDownloadThumbnail(true);
        options.setAutoLogin(false);

        // 如果APP启用了远程的service，此application:onCreate会被调用2次
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
        // 默认的APP会在以包名为默认的process name下运行，如果查到的process name不是APP的process name就立即返回
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        if (processAppName == null || !processAppName.equalsIgnoreCase(this.getPackageName())) {
            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }

        //初始化
        EMClient.getInstance().init(this, options);
        //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(true);

        UMShareAPI.get(this);
    }

    private void startMainService() {
        Intent intent = new Intent();
        intent.setClass(this, OKMainService.class);
        startService(intent);
    }

    private void fileMkdirs() {
        File imageFile = new File(OKConstant.IMAGE_PATH);
        File glideFile = new File(OKConstant.GLIDE_PATH);

        if (!imageFile.exists()) {
            imageFile.mkdirs();
        }

        if (!glideFile.exists()) {
            glideFile.mkdirs();
        }
    }

    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return processName;
    }
}
