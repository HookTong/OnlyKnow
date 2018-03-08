package com.onlyknow.app.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.onlyknow.app.R;
import com.onlyknow.app.ui.OKBaseActivity;
import com.xiao.nicevideoplayer.NiceVideoPlayer;
import com.xiao.nicevideoplayer.NiceVideoPlayerManager;
import com.xiao.nicevideoplayer.TxVideoPlayerController;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/3/8.
 */

public class OKVideoActivity extends OKBaseActivity {

    @Bind(R.id.ok_activity_player)
    NiceVideoPlayer okActivityPlayer;

    private String mVideo_Url = "";
    private String mTitle = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ok_activity_video);
        ButterKnife.bind(this);
        mVideo_Url = getIntent().getExtras().getString("URL");
        mTitle = getIntent().getExtras().getString("TITLE");

        init();
    }

    private void init() {
        okActivityPlayer.setPlayerType(NiceVideoPlayer.TYPE_IJK); // or NiceVideoPlayer.TYPE_NATIVE
        okActivityPlayer.setUp(mVideo_Url, null);
        TxVideoPlayerController controller = new TxVideoPlayerController(this);
        controller.setTitle(mTitle);
        controller.setLenght(117000);
        GlideApi(controller.imageView(), mVideo_Url, R.drawable.topgd2, R.drawable.topgd2);
        okActivityPlayer.setController(controller);
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
}
