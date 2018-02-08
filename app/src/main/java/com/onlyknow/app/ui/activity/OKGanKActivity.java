package com.onlyknow.app.ui.activity;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.onlyknow.app.R;
import com.onlyknow.app.ui.OKBaseActivity;
import com.onlyknow.app.ui.adapter.OKFragmentPagerAdapter;
import com.onlyknow.app.ui.fragement.gank.OKGanKAndroidFragment;
import com.onlyknow.app.ui.fragement.gank.OKGanKFrontEndFragment;
import com.onlyknow.app.ui.fragement.gank.OKGanKIosFragment;
import com.onlyknow.app.ui.fragement.gank.OKGanKResFragment;
import com.onlyknow.app.ui.fragement.gank.OKGanKVideoFragment;
import com.onlyknow.app.ui.fragement.gank.OKGanKWelfareFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * GanK.Io
 * <p>
 * Created by Administrator on 2018/2/6.
 */

public class OKGanKActivity extends OKBaseActivity {
    @Bind(R.id.ok_activity_gank_toolbar)
    Toolbar mGanKToolbar;

    @Bind(R.id.ok_activity_gank_tab_layout)
    TabLayout mGanKTabLayout;

    @Bind(R.id.ok_activity_gank_appBarLayout)
    AppBarLayout mGanKAppBarLayout;

    @Bind(R.id.ok_activity_gank_viewPage)
    ViewPager mGanKViewPage;

    private final List<Fragment> mFragments = new ArrayList<>();
    private final List<String> mTabName = new ArrayList<>();
    private OKFragmentPagerAdapter mFragmentPagerAdapter;

    private int page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ok_activity_gank);
        ButterKnife.bind(this);
        initSystemBar(this);
        switch (getIntent().getExtras().getInt(INTENT_KEY_GAN_KIO, -1)) {
            case GAN_KIO_TYPE_FL:
                page = 0;
                break;
            case GAN_KIO_TYPE_VIDEO:
                page = 1;
                break;
            case GAN_KIO_TYPE_RES:
                page = 2;
                break;
            case GAN_KIO_TYPE_ANDROID:
                page = 3;
                break;
            case GAN_KIO_TYPE_IOS:
                page = 4;
                break;
            case GAN_KIO_TYPE_H5:
                page = 5;
                break;
            default:
                finish();
                break;
        }
        init();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mGanKToolbar != null) {
            mGanKToolbar.setTitle("干货营喔!");
            mGanKToolbar.setLogo(R.drawable.icon_gank_work);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ok_menu_gank, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.ok_menu_gank_about:
                Bundle mBundle = new Bundle();
                mBundle.putString("WEB_LINK", "http://gank.io/");
                startUserActivity(mBundle, OKBrowserActivity.class);
        }
        return true;
    }

    private void init() {
        setSupportActionBar(mGanKToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mGanKTabLayout.setTabMode(TabLayout.MODE_FIXED);// 设置TabLayout的模式
        mGanKTabLayout.addTab(mGanKTabLayout.newTab().setText("福利"));
        mGanKTabLayout.addTab(mGanKTabLayout.newTab().setText("视频"));
        mGanKTabLayout.addTab(mGanKTabLayout.newTab().setText("资源"));
        mGanKTabLayout.addTab(mGanKTabLayout.newTab().setText("安卓"));
        mGanKTabLayout.addTab(mGanKTabLayout.newTab().setText("苹果"));
        mGanKTabLayout.addTab(mGanKTabLayout.newTab().setText("前端"));
        mFragments.clear();
        mTabName.clear();
        mFragments.add(new OKGanKWelfareFragment());
        mFragments.add(new OKGanKVideoFragment());
        mFragments.add(new OKGanKResFragment());
        mFragments.add(new OKGanKAndroidFragment());
        mFragments.add(new OKGanKIosFragment());
        mFragments.add(new OKGanKFrontEndFragment());
        mTabName.add("福利");
        mTabName.add("视频");
        mTabName.add("资源");
        mTabName.add("安卓");
        mTabName.add("苹果");
        mTabName.add("前端");

        mFragmentPagerAdapter = new OKFragmentPagerAdapter(this.getSupportFragmentManager(), mFragments, mTabName);
        mGanKViewPage.setAdapter(mFragmentPagerAdapter);
        mGanKViewPage.setOffscreenPageLimit(6);
        mGanKTabLayout.setupWithViewPager(mGanKViewPage);
        mGanKViewPage.setCurrentItem(page);
        mGanKTabLayout.setScrollPosition(page, 0f, true);
    }
}
