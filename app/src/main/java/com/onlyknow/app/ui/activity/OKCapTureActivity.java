package com.onlyknow.app.ui.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.onlyknow.app.R;
import com.onlyknow.app.ui.OKBaseActivity;

public class OKCapTureActivity extends OKBaseActivity {
    private TextView textview;
    private Button butdk;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ok_activity_cap_ture);
        initSystemBar(this);
        bundle = this.getIntent().getExtras();
        findView();
        init();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mToolbar != null) {
            mToolbar.setTitle("");
        }
    }

    private void init() {
        textview.setText(bundle.getString("RESULT").toString());
        if (textview.getText().toString().startsWith("http://") || textview.getText().toString().startsWith("https://")) {
            butdk.setText("打开链接");
        } else {
            butdk.setText("复制文本");
        }

        butdk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (butdk.getText().toString().equals("打开链接")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("WEBLINK", textview.getText().toString());
                    Intent intent = new Intent();
                    intent.setClass(OKCapTureActivity.this, OKBrowserActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    cm.setText(textview.getText().toString());
                }
            }
        });

        mToolbarBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                OKCapTureActivity.this.finish();
            }
        });
    }

    private void findView() {
        super.findCommonToolbarView(this);

        mToolbarBack.setVisibility(View.VISIBLE);
        mToolbarTitle.setVisibility(View.VISIBLE);

        mToolbarTitle.setText("扫描结果");

        textview = (TextView) findViewById(R.id.CAP_qrcode_text);
        butdk = (Button) findViewById(R.id.CAP_qrcode_but);
    }
}
