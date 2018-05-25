package com.onlyknow.app.ui.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationBar.OnTabSelectedListener;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.ashokvarma.bottomnavigation.ShapeBadgeItem;
import com.onlyknow.app.R;
import com.onlyknow.app.ui.OKBaseActivity;
import com.onlyknow.app.ui.fragement.OKExploreScreen;
import com.onlyknow.app.ui.fragement.OKHistoryScreen;
import com.onlyknow.app.ui.fragement.OKMeScreen;
import com.onlyknow.app.ui.fragement.OKNearScreen;

public class OKMainActivity extends OKBaseActivity {
    private BottomNavigationBar bottomNavigationBar;
    private BottomNavigationItem itemTanSuo, itemFuJin, itemLiShi, itemWoDe;
    private FloatingActionButton but;
    private FragmentTransaction transaction;
    private ShapeBadgeItem badgeItem;

    // Fragment界面
    private Fragment fragments[] = new Fragment[4];

    private int NarPos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ok_activity_main);
        initUserBody();
        initSettingBody();
        findView();
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (NarPos != SETTING_BODY.getInt("BottomNnavigation", 1)) {
            initBottomNavigationBar();
        }
    }

    private void initBottomNavigationBar() {
        try {
            bottomNavigationBar.removeItem(itemTanSuo);
            bottomNavigationBar.removeItem(itemFuJin);
            bottomNavigationBar.removeItem(itemLiShi);
            bottomNavigationBar.removeItem(itemWoDe);
        } catch (Exception e) {
            e.printStackTrace();
        }
        NarPos = SETTING_BODY.getInt("BottomNnavigation", 1);
        if (NarPos == 0) {
            bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
            bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_RIPPLE);
        } else if (NarPos == 1) {
            bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
            bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        } else if (NarPos == 2) {
            bottomNavigationBar.setMode(BottomNavigationBar.MODE_SHIFTING);
            bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        } else if (NarPos == 3) {
            bottomNavigationBar.setMode(BottomNavigationBar.MODE_SHIFTING);
            bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_RIPPLE);
        }
        badgeItem = new ShapeBadgeItem();
        badgeItem.setHideOnSelect(true);
        bottomNavigationBar.addItem(itemTanSuo);
        bottomNavigationBar.addItem(itemFuJin);
        bottomNavigationBar.addItem(itemLiShi);
        bottomNavigationBar.addItem(itemWoDe);
        bottomNavigationBar.setFirstSelectedPosition(0);// 设置默认选择item
        bottomNavigationBar.initialise();// 初始化
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void init() {
        initBottomNavigationBar();

        setFragment(0);

        bottomNavigationBar.setTabSelectedListener(new OnTabSelectedListener() {

            @Override
            public void onTabUnselected(int arg0) {
            }

            @Override
            public void onTabSelected(int position) {
                switch (position) {
                    case 0:
                        setFragment(0);
                        break;
                    case 1:
                        setFragment(1);
                        break;
                    case 2:
                        setFragment(2);
                        break;
                    case 3:
                        setFragment(3);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabReselected(int arg0) {
            }
        });

        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (USER_BODY.getBoolean("STATE", false)) {
                    startUserActivity(null, OKAddCardActivity.class);
                } else {
                    startUserActivity(null, OKLoginActivity.class);
                }
            }
        });
    }

    private void setFragment(int index) {
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.MAIN_fragment, fragments[index]);
        transaction.commit();
    }

    private void findView() {
        but = (FloatingActionButton) findViewById(R.id.main_fabut);
        bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.MAIN_bottom_navigation_bar);
        itemTanSuo = new BottomNavigationItem(R.drawable.tansuo, R.string.item_home)
                .setActiveColorResource(R.color.md_light_green_500);
        itemFuJin = new BottomNavigationItem(R.drawable.bankuai, R.string.item_location)
                .setActiveColorResource(R.color.md_deep_purple_500);
        itemLiShi = new BottomNavigationItem(R.drawable.lishi, R.string.item_like)
                .setActiveColorResource(R.color.md_teal_500);
        itemWoDe = new BottomNavigationItem(R.drawable.wode, R.string.item_person)
                .setActiveColorResource(R.color.md_pink_500);
        OKExploreScreen mainScreen = new OKExploreScreen();
        OKNearScreen nearScreen = new OKNearScreen();
        OKHistoryScreen LiShiScreen = new OKHistoryScreen();
        OKMeScreen meScreen = new OKMeScreen();
        fragments[0] = mainScreen;
        fragments[1] = nearScreen;
        fragments[2] = LiShiScreen;
        fragments[3] = meScreen;
    }

    private long back_time = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (System.currentTimeMillis() - back_time > 2000l) {
                Toast.makeText(this, "再按一次退程序 !", Toast.LENGTH_SHORT).show();
                back_time = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
