package com.onlyknow.app.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.onlyknow.app.R;

/**
 * Created by Administrator on 2017/12/24.
 * <p>
 * 友盟分享窗口
 */

public class OKShareDialog extends Dialog {

    private onCallback mCallback;

    public OKShareDialog(Context context, onCallback callback) {
        this(context, R.layout.ok_dialog_share, R.style.share_dialogAnim, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        this.mCallback = callback;
    }

    public OKShareDialog(final Context context, int layout, int style, int width,
                         int height) {
        super(context, style);
        setContentView(layout);
        setCanceledOnTouchOutside(true);
        // 设置属性值
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = width;
        lp.height = height;
        getWindow().setAttributes(lp);

        setListener();
    }

    // 设置点击事件
    private void setListener() {
        findViewById(R.id.ok_share_dialog_qq_haoyou_image).setOnClickListener(
                new android.view.View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mCallback.onShare(1);
                        dismiss();
                    }
                });
        findViewById(R.id.ok_share_dialog_qq_konjian_image).setOnClickListener(
                new android.view.View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mCallback.onShare(2);
                        dismiss();
                    }
                });
        findViewById(R.id.ok_share_dialog_weixin_haoyou_image).setOnClickListener(
                new android.view.View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mCallback.onShare(3);
                        dismiss();
                    }
                });
        findViewById(R.id.ok_share_dialog_weixin_penyouquan_image).setOnClickListener(
                new android.view.View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mCallback.onShare(4);
                        dismiss();
                    }
                });
        findViewById(R.id.ok_share_dialog_xinlan_weibo_image).setOnClickListener(
                new android.view.View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mCallback.onShare(5);
                        dismiss();
                    }
                });
        findViewById(R.id.ok_share_dialog_close).setOnClickListener(
                new android.view.View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });
    }

    @Override
    public void show() {
        super.show();
        // 设置dialog显示动画
        getWindow().setWindowAnimations(R.style.share_dialogAnim);
        // 设置显示位置为底部
        getWindow().setGravity(Gravity.BOTTOM);
    }

    public interface onCallback {
        public void onShare(int id);
    }
}
