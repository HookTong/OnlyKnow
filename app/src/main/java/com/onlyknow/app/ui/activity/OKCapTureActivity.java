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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        String str = textview.getText().toString();
        if (isUrl(str)) {
            butdk.setText("打开链接");
        } else {
            butdk.setText("复制文本");
        }

        butdk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (butdk.getText().toString().equals("打开链接")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("WEB_LINK", textview.getText().toString());
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

    private boolean isUrl(String url) {
        boolean b;
        String regex = "(((https|http)?://)?([a-z0-9]+[.])|(www.))" + "\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\u4E00-\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)";//设置正则表达式
        Pattern pat = Pattern.compile(regex.trim());
        Matcher mat = pat.matcher(url.trim());
        b = mat.matches();//判断是否匹配
        boolean b2 = url.startsWith("http://") || url.startsWith("https://") || url.startsWith("file://") || url.startsWith("ftp://");
        return b || b2;
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
