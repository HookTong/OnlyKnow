package com.onlyknow.app.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.onlyknow.app.OKConstant;
import com.onlyknow.app.R;
import com.onlyknow.app.net.OKWebService;
import com.onlyknow.app.ui.OKBaseActivity;
import com.onlyknow.app.ui.view.OKProgressButton;
import com.onlyknow.app.ui.view.OKSEImageView;
import com.xiao.nicevideoplayer.NiceVideoPlayer;
import com.xiao.nicevideoplayer.NiceVideoPlayerManager;
import com.xiao.nicevideoplayer.TxVideoPlayerController;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Request;

/**
 * Created by Administrator on 2018/3/8.
 */

public class OKVideoActivity extends OKBaseActivity {

    @Bind(R.id.ok_activity_player)
    NiceVideoPlayer okActivityPlayer;
    @Bind(R.id.ok_activity_player_down)
    OKSEImageView okActivityPlayerDown;

    private String mVideo_Url = "";
    private String mTitle = "";

    private OKWebService mWebService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ok_activity_video);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
            return;
        }
        mVideo_Url = bundle.getString("URL");
        mTitle = bundle.getString("TITLE");
        init();
    }

    private void init() {
        okActivityPlayerDown.setTag(R.id.downButton, OKProgressButton.NORMAL);
        if (isFileExists(mVideo_Url)) {
            okActivityPlayerDown.setEnabled(false);
            File file = new File(OKConstant.IMAGE_PATH, getFileName(mVideo_Url));
            if (!file.exists()) return;
            mVideo_Url = file.getAbsolutePath();
        } else {
            okActivityPlayerDown.setEnabled(true);
        }
        okActivityPlayer.setPlayerType(NiceVideoPlayer.TYPE_IJK); // or NiceVideoPlayer.TYPE_NATIVE
        okActivityPlayer.setUp(mVideo_Url, null);
        TxVideoPlayerController controller = new TxVideoPlayerController(this);
        controller.setTitle(mTitle);
        controller.setLenght(0);
        GlideApi(controller.imageView(), mVideo_Url, R.drawable.topgd2, R.drawable.topgd2);
        okActivityPlayer.setController(controller);

        okActivityPlayerDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((int) okActivityPlayerDown.getTag(R.id.downButton) == OKProgressButton.DOWNLOADING) {
                    showSnackbar(v, "您当前正在下载该视频", "");
                    return;
                }
                String filePath = OKConstant.IMAGE_PATH;
                mWebService = OKWebService.getInstance();
                mWebService.downloadFile(mVideo_Url, filePath, new DownloadCallback());
                okActivityPlayerDown.setTag(R.id.downButton, OKProgressButton.DOWNLOADING);
                showProgressDialog("正在下载中...");
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 在onStop时释放掉播放器
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
    }

    @Override
    public void onBackPressed() {
        // 在全屏或者小窗口时按返回键要先退出全屏或小窗口，
        // 所以在Activity中onBackPress要交给NiceVideoPlayer先处理。
        if (NiceVideoPlayerManager.instance().onBackPressd()) return;
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if ((int) okActivityPlayerDown.getTag(R.id.downButton) == OKProgressButton.DOWNLOADING) {
            if (mWebService != null) {
                mWebService.cancelDown();
            }
            Toast.makeText(this, "下载已取消", Toast.LENGTH_SHORT).show();
        }
        super.onDestroy();
    }

    private boolean isFileExists(String url) {
        File file = new File(OKConstant.IMAGE_PATH, getFileName(url));
        if (file.exists()) {
            return true;
        }
        return false;
    }

    private String getFileName(String url) {
        String ss[] = url.split("/");
        return ss[ss.length - 1];
    }

    // 下载回调类
    private class DownloadCallback extends OKWebService.ResultCallback {
        @Override
        public void onError(Request request, Exception e) {
            closeProgressDialog();
            okActivityPlayerDown.setTag(R.id.downButton, OKProgressButton.NORMAL);
            okActivityPlayerDown.setEnabled(true);
            showSnackbar(okActivityPlayerDown, "下载失败", "");
        }

        @Override
        public void onResponse(Object response) {
            closeProgressDialog();
            okActivityPlayerDown.setTag(R.id.downButton, OKProgressButton.NORMAL);
            okActivityPlayerDown.setEnabled(false);
            showSnackbar(okActivityPlayerDown, "下载完成,您可以到 " + OKConstant.IMAGE_PATH + " 下查看", "");
        }

        @Override
        public void onProgress(double total, double current) {
        }
    }
}
