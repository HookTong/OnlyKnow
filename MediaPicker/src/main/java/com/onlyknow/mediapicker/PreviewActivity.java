package com.onlyknow.mediapicker;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.onlyknow.mediapicker.bean.MediaBean;
import com.onlyknow.mediapicker.utils.BarTintUtil;
import com.onlyknow.mediapicker.views.PreviewFragment;
import com.onlyknow.mediapicker.views.SEImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * 媒体选择预览界面
 * <p>
 * Created by ReSet on 2018/03/08.
 */

public class PreviewActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private SEImageView back;
    private Button done;
    private CheckBox checkBox;
    private ViewPager viewpager;
    private TextView bar_title;
    private ArrayList<MediaBean> preRawList, selects;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_preview);
        initStatusBar();
        init();
    }

    private void init() {
        back = (SEImageView) findViewById(R.id.btn_back);
        checkBox = (CheckBox) findViewById(R.id.check_checkbox);
        bar_title = (TextView) findViewById(R.id.bar_title);
        done = (Button) findViewById(R.id.done);
        viewpager = (ViewPager) findViewById(R.id.viewpager);

        back.setOnClickListener(this);
        done.setOnClickListener(this);
        checkBox.setOnClickListener(this);

        preRawList = getIntent().getParcelableArrayListExtra(PickerConfig.PRE_RAW_LIST);
        selects = new ArrayList<>();
        selects.addAll(preRawList);

        setView(preRawList);
    }

    private void setView(ArrayList<MediaBean> default_list) {
        setDoneView(default_list.size());
        bar_title.setText(1 + "/" + preRawList.size());
        ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
        for (MediaBean mediaBean : default_list) {
            fragmentArrayList.add(PreviewFragment.newInstance(mediaBean, ""));
        }
        AdapterFragment adapterFragment = new AdapterFragment(getSupportFragmentManager(), fragmentArrayList);
        viewpager.setAdapter(adapterFragment);
        viewpager.addOnPageChangeListener(this);
    }

    private void setDoneView(int num1) {
        done.setText(getString(R.string.done) + "(" + num1 + "/" + getIntent().getIntExtra(PickerConfig.MAX_SELECT_COUNT, PickerConfig.DEFAULT_SELECTED_MAX_COUNT) + ")");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_back) {
            done(selects, PickerConfig.RESULT_UPDATE_CODE);
        } else if (id == R.id.done) {
            done(selects, PickerConfig.RESULT_CODE);
        } else if (id == R.id.check_checkbox) {
            boolean isChecked = checkBox.isChecked();
            MediaBean mediaBean = preRawList.get(viewpager.getCurrentItem());
            if (isChecked) {
                selects.add(mediaBean);
            } else {
                int select = isSelect(mediaBean, selects);
                selects.remove(select);
            }
            setDoneView(selects.size());
        }
    }

    public int isSelect(MediaBean mediaBean, ArrayList<MediaBean> list) {
        int is = -1;
        if (list.size() <= 0) {
            return is;
        }
        for (int i = 0; i < list.size(); i++) {
            MediaBean m = list.get(i);
            if (m.path.equals(mediaBean.path)) {
                is = i;
                break;
            }
        }
        return is; // 大于等于0 就是表示已选择,返回的是在selectMedias中的下标
    }

    public void done(ArrayList<MediaBean> list, int code) {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(PickerConfig.EXTRA_RESULT, list);
        setResult(code, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        done(selects, PickerConfig.RESULT_UPDATE_CODE);
        super.onBackPressed();
    }

    public class AdapterFragment extends FragmentStatePagerAdapter {
        private List<Fragment> mFragments;

        public AdapterFragment(FragmentManager fm, List<Fragment> mFragments) {
            super(fm);
            this.mFragments = mFragments;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        bar_title.setText((position + 1) + "/" + preRawList.size());
        if (isSelect(preRawList.get(position), selects) < 0) {
            checkBox.setChecked(false);
        } else {
            checkBox.setChecked(true);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }


    public void initStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window win = getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            winParams.flags |= bits;
            win.setAttributes(winParams);
        }
        BarTintUtil tintManager = new BarTintUtil(this);
        tintManager.setStatusBarTintEnabled(true);
        // 使用颜色资源
        tintManager.setStatusBarTintResource(R.color.md_light_green_600);
    }
}
