package com.onlyknow.app.ui.fragement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onlyknow.app.R;
import com.onlyknow.app.api.OKLoadUserInfoApi;
import com.onlyknow.app.database.bean.OKUserInfoBean;
import com.onlyknow.app.ui.OKBaseFragment;
import com.onlyknow.app.ui.activity.OKDragPhotoActivity;
import com.onlyknow.app.ui.activity.OKGoodsActivity;
import com.onlyknow.app.ui.activity.OKLoginActivity;
import com.onlyknow.app.ui.activity.OKSettingActivity;
import com.onlyknow.app.ui.activity.OKUserEditActivity;
import com.onlyknow.app.ui.adapter.OKFragmentPagerAdapter;
import com.onlyknow.app.ui.view.OKCircleImageView;
import com.onlyknow.app.ui.view.OKSEImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OKMeScreen extends OKBaseFragment implements AppBarLayout.OnOffsetChangedListener, NavigationView.OnNavigationItemSelectedListener, OKLoadUserInfoApi.onCallBack {
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private ImageView mImageViewHead;
    private OKCircleImageView imageViewTX;
    private ImageView imageViewSex;
    private TextView textViewName, textViewQianMin, textViewGuanZhu, textViewShouChan, textViewJiFeng;
    private LinearLayout linearLayoutJiFeng;
    private OKSEImageView menu_OKSEImageView, edit_OKSEImageView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mBarToggle;
    private NavigationView mNavigationView;

    private OKFragmentPagerAdapter mFragmentPagerAdapter;
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mTabNameList = new ArrayList<>();

    // 动态,关注,收藏 fragment
    private final OKDynamicFragment mDynamicFragment = new OKDynamicFragment();
    private final OKAttentionFragment mAttentionFragment = new OKAttentionFragment();
    private final OKWatchFragment mWatchFragment = new OKWatchFragment();
    private final OKCommentFragment mCommentFragment = new OKCommentFragment();
    private final OKApproveFragment mApproveFragment = new OKApproveFragment();

    private View rootView;

    private OKLoadUserInfoApi mOKLoadUserInfoApi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.ok_fragment_me, container, false);
            initUserInfoSharedPreferences();
            findView(rootView);

            init();
            bindUserInfo();
            return rootView;
        } else {
            init();
            return rootView;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        appBarLayout.addOnOffsetChangedListener(this);

        if (USER_INFO_SP.getBoolean("STATE_CHANGE", false)) {
            bindUserInfo();
            USER_INFO_SP.edit().putBoolean("STATE_CHANGE", false).commit();
        }

        if (USER_INFO_SP.getBoolean("STATE", false) && !TextUtils.isEmpty(USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""))) {
            mOKLoadUserInfoApi = new OKLoadUserInfoApi(getActivity());
            Map<String, String> map = new HashMap<>();// 请求参数
            map.put("username", USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, ""));
            map.put("type", "ALL");
            mOKLoadUserInfoApi.requestUserInfo(map, this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //如果异步任务不为空并且状态是运行时,就把他取消这个加载任务
        if (mOKLoadUserInfoApi != null) {
            mOKLoadUserInfoApi.cancelTask();
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        appBarLayout.removeOnOffsetChangedListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (rootView != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
    }

    private void bindUserInfo() {
        if (USER_INFO_SP.getBoolean("STATE", false)) {
            // 用户信息设置
            String url = USER_INFO_SP.getString(OKUserInfoBean.KEY_HEADPORTRAIT_URL, "");
            GlideRoundApi(imageViewTX, url, R.drawable.touxian_placeholder_hd, R.drawable.touxian_placeholder_hd);
            GlideBlurApi(mImageViewHead, url, R.drawable.topgd3, R.drawable.topgd3);
            if (USER_INFO_SP.getString(OKUserInfoBean.KEY_SEX, "").equals("NAN")) {
                imageViewSex.setVisibility(View.VISIBLE);
                GlideApi(imageViewSex, R.drawable.nan, R.drawable.nan, R.drawable.nan);
            } else {
                imageViewSex.setVisibility(View.VISIBLE);
                GlideApi(imageViewSex, R.drawable.nv, R.drawable.nv, R.drawable.nv);
            }
            textViewName.setText(USER_INFO_SP.getString(OKUserInfoBean.KEY_NICKNAME, "您还未登录"));
            String qm = USER_INFO_SP.getString(OKUserInfoBean.KEY_QIANMIN, "");
            if (!TextUtils.isEmpty(qm) && !qm.equals("NULL")) {
                textViewQianMin.setText(qm);
            } else {
                textViewQianMin.setText("这个人很懒，什么都没有留下 !");
            }
            textViewGuanZhu.setText("" + USER_INFO_SP.getInt(OKUserInfoBean.KEY_GUANZHU, 0));
            textViewShouChan.setText("" + USER_INFO_SP.getInt(OKUserInfoBean.KEY_SHOUCHAN, 0));
            textViewJiFeng.setText("" + USER_INFO_SP.getInt(OKUserInfoBean.KEY_JIFENG, 0));
        } else {
            imageViewSex.setVisibility(View.INVISIBLE);
            textViewName.setText("您还未登录");
            textViewQianMin.setText("这个人很懒，什么都没有留下 !");
            textViewGuanZhu.setText("0");
            textViewShouChan.setText("0");
            textViewJiFeng.setText("0");
            GlideRoundApi(imageViewTX, R.drawable.touxian_placeholder_hd, R.drawable.touxian_placeholder_hd, R.drawable.touxian_placeholder_hd);
            GlideBlurApi(mImageViewHead, R.drawable.topgd3, R.drawable.topgd3, R.drawable.topgd3);
        }
    }

    @SuppressLint("ResourceType")
    private void init() {
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);// 设置TabLayout的模式
        mTabLayout.addTab(mTabLayout.newTab().setText("动态"));
        mTabLayout.addTab(mTabLayout.newTab().setText("关注"));
        mTabLayout.addTab(mTabLayout.newTab().setText("收藏"));
        mTabLayout.addTab(mTabLayout.newTab().setText("评论"));
        mTabLayout.addTab(mTabLayout.newTab().setText("审批"));
        mFragmentList.clear();
        mTabNameList.clear();
        // 简单创建一个FragmentPagerAdapter
        mFragmentList.add(mDynamicFragment);
        mFragmentList.add(mAttentionFragment);
        mFragmentList.add(mWatchFragment);
        mFragmentList.add(mCommentFragment);
        mFragmentList.add(mApproveFragment);
        mTabNameList.add("动态");
        mTabNameList.add("关注");
        mTabNameList.add("收藏");
        mTabNameList.add("评论");
        mTabNameList.add("审批");
        mFragmentPagerAdapter = new OKFragmentPagerAdapter(getChildFragmentManager(), mFragmentList, mTabNameList);
        mViewPager.setAdapter(mFragmentPagerAdapter);
        mViewPager.setOffscreenPageLimit(5);
        mTabLayout.setupWithViewPager(mViewPager);
        mNavigationView.setNavigationItemSelectedListener(this);
        mBarToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, toolbar, R.drawable.ok_toolbar_menu, R.drawable.ok_toolbar_back);
        mBarToggle.syncState();
        mDrawerLayout.addDrawerListener(mBarToggle);
        bindNavigationHeadView(mNavigationView.getHeaderView(0));

        // 监听器
        imageViewTX.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (USER_INFO_SP.getBoolean("STATE", false)) {
                    int location[] = new int[2];
                    imageViewTX.getLocationOnScreen(location);

                    Bundle mBundle = new Bundle();
                    mBundle.putInt("left", location[0]);
                    mBundle.putInt("top", location[1]);
                    mBundle.putInt("height", imageViewTX.getHeight());
                    mBundle.putInt("width", imageViewTX.getWidth());
                    String url = USER_INFO_SP.getString(OKUserInfoBean.KEY_HEADPORTRAIT_URL, "");
                    mBundle.putString("url", url);

                    startUserActivity(mBundle, OKDragPhotoActivity.class);
                    getActivity().overridePendingTransition(0, 0);
                } else {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), OKLoginActivity.class);
                    getActivity().startActivity(intent);
                }
            }
        });

        linearLayoutJiFeng.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (USER_INFO_SP.getBoolean("STATE", false)) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), OKGoodsActivity.class);
                    getActivity().startActivity(intent);
                } else {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), OKLoginActivity.class);
                    getActivity().startActivity(intent);
                }
            }
        });

        edit_OKSEImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (USER_INFO_SP.getBoolean("STATE", false)) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), OKUserEditActivity.class);
                    getActivity().startActivity(intent);
                } else {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), OKLoginActivity.class);
                    getActivity().startActivity(intent);
                }
            }
        });

        menu_OKSEImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showMenu(getActivity());
            }
        });
    }

    private void findView(View rootView) {
        toolbar = (Toolbar) rootView.findViewById(R.id.ME_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        appBarLayout = (AppBarLayout) rootView.findViewById(R.id.ME_app_bar);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) rootView.findViewById(R.id.ME_toolbar_layout);
        mImageViewHead = (ImageView) rootView.findViewById(R.id.ME_toplayout_image);
        imageViewTX = (OKCircleImageView) rootView.findViewById(R.id.ME_touxiang_imag);
        imageViewSex = (ImageView) rootView.findViewById(R.id.ME_sex_imag);
        textViewName = (TextView) rootView.findViewById(R.id.ME_name_text);
        textViewQianMin = (TextView) rootView.findViewById(R.id.ME_qianmin_text);
        textViewGuanZhu = (TextView) rootView.findViewById(R.id.ME_guanzhu_num_text);
        textViewShouChan = (TextView) rootView.findViewById(R.id.ME_shouchan_num_text);
        textViewJiFeng = (TextView) rootView.findViewById(R.id.ME_jifeng_text);
        linearLayoutJiFeng = (LinearLayout) rootView.findViewById(R.id.ME_jifeng_layout);
        edit_OKSEImageView = (OKSEImageView) rootView.findViewById(R.id.ME_topbtnRight);
        menu_OKSEImageView = (OKSEImageView) rootView.findViewById(R.id.ME_topbtnSet);
        mViewPager = (ViewPager) rootView.findViewById(R.id.ME_viewpager);
        mTabLayout = (TabLayout) rootView.findViewById(R.id.ME_tabs);
        mDrawerLayout = (DrawerLayout) rootView.findViewById(R.id.ok_fragment_me_drawerLayout);
        mNavigationView = (NavigationView) rootView.findViewById(R.id.ok_fragment_me_NavigationView);

        mCollapsingToolbarLayout.setTitle(" ");
    }

    @Override
    public void onOffsetChanged(AppBarLayout arg0, int pos) {
        // 解决与刷新控件的冲突
        if (pos == 0) {
            mDynamicFragment.setSwipeRefreshEnabled(true);
            mAttentionFragment.setSwipeRefreshEnabled(true);
            mWatchFragment.setSwipeRefreshEnabled(true);
            mCommentFragment.setSwipeRefreshEnabled(true);
            mApproveFragment.setSwipeRefreshEnabled(true);
        } else {
            mDynamicFragment.setSwipeRefreshEnabled(false);
            mAttentionFragment.setSwipeRefreshEnabled(false);
            mWatchFragment.setSwipeRefreshEnabled(false);
            mCommentFragment.setSwipeRefreshEnabled(false);
            mApproveFragment.setSwipeRefreshEnabled(false);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.ok_menu_drawer_FuLi:
                startGanKioActivity(GAN_KIO_TYPE_FL);
                break;
            case R.id.ok_menu_drawer_Video:
                startGanKioActivity(GAN_KIO_TYPE_VIDEO);
                break;
            case R.id.ok_menu_drawer_ExtensionRes:
                startGanKioActivity(GAN_KIO_TYPE_RES);
                break;
            case R.id.ok_menu_drawer_Android:
                startGanKioActivity(GAN_KIO_TYPE_ANDROID);
                break;
            case R.id.ok_menu_drawer_IOS:
                startGanKioActivity(GAN_KIO_TYPE_IOS);
                break;
            case R.id.ok_menu_drawer_QianDuan:
                startGanKioActivity(GAN_KIO_TYPE_H5);
                break;
            case R.id.ok_menu_drawer_Setting:
                startUserActivity(null, OKSettingActivity.class);
                break;
            default:
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void userInfoApiComplete(OKUserInfoBean userInfoBean) {

        if (userInfoBean == null) {
            showSnackBar(rootView, "没有获取到用户信息", "");
            return;
        }

        if (!userInfoBean.getHEADPORTRAIT_URL().equals(USER_INFO_SP.getString(OKUserInfoBean.KEY_HEADPORTRAIT_URL, ""))) {
            GlideRoundApi(imageViewTX, userInfoBean.getHEADPORTRAIT_URL(), R.drawable.touxian_placeholder_hd, R.drawable.touxian_placeholder_hd);
            GlideBlurApi(mImageViewHead, userInfoBean.getHEADPORTRAIT_URL(), R.drawable.topgd3, R.drawable.topgd3);
        }

        textViewName.setText(userInfoBean.getNICKNAME());

        if (!TextUtils.isEmpty(userInfoBean.getQIANMIN()) && !userInfoBean.getQIANMIN().equals("NULL")) {
            textViewQianMin.setText(userInfoBean.getQIANMIN());
        } else {
            textViewQianMin.setText("这个人很懒，什么都没有留下 !");
        }

        imageViewSex.setVisibility(View.VISIBLE);
        if (userInfoBean.getSEX().equals("NAN")) {
            GlideApi(imageViewSex, R.drawable.nan, R.drawable.nan, R.drawable.nan);
        } else {
            GlideApi(imageViewSex, R.drawable.nv, R.drawable.nv, R.drawable.nv);
        }

        textViewShouChan.setText("" + userInfoBean.getSHOUCHAN());
        textViewGuanZhu.setText("" + userInfoBean.getGUANZHU());
        textViewJiFeng.setText("" + userInfoBean.getJIFENG());

        // 保存用户信息
        SharedPreferences.Editor editor = USER_INFO_SP.edit();
        editor.putInt(OKUserInfoBean.KEY_USERID, userInfoBean.getUSERID());
        editor.putString(OKUserInfoBean.KEY_USERNAME, userInfoBean.getUSERNAME());
        editor.putString(OKUserInfoBean.KEY_NICKNAME, userInfoBean.getNICKNAME());
        editor.putString(OKUserInfoBean.KEY_PHONE, userInfoBean.getPHONE());
        editor.putString(OKUserInfoBean.KEY_EMAIL, userInfoBean.getEMAIL());
        editor.putString(OKUserInfoBean.KEY_QIANMIN, userInfoBean.getQIANMIN());
        editor.putString(OKUserInfoBean.KEY_SEX, userInfoBean.getSEX());
        editor.putString(OKUserInfoBean.KEY_BIRTH_DATE, userInfoBean.getBIRTH_DATE());
        editor.putInt(OKUserInfoBean.KEY_AGE, userInfoBean.getAGE());
        editor.putString(OKUserInfoBean.KEY_RE_DATE, userInfoBean.getRE_DATE());
        editor.putInt(OKUserInfoBean.KEY_SHOUCHAN, userInfoBean.getSHOUCHAN());
        editor.putInt(OKUserInfoBean.KEY_GUANZHU, userInfoBean.getGUANZHU());
        editor.putInt(OKUserInfoBean.KEY_JIFENG, userInfoBean.getJIFENG());
        editor.putInt(OKUserInfoBean.KEY_WENZHAN, userInfoBean.getWENZHAN());
        editor.putString(OKUserInfoBean.KEY_HEADPORTRAIT_URL, userInfoBean.getHEADPORTRAIT_URL());
        editor.putString(OKUserInfoBean.KEY_HEAD_URL, userInfoBean.getHEAD_URL());
        editor.putString(OKUserInfoBean.KEY_EDIT_DATE, userInfoBean.getEDIT_DATE());
        editor.commit();
    }
}
