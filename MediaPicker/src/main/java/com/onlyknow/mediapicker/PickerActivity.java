package com.onlyknow.mediapicker;

import android.Manifest;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.onlyknow.mediapicker.adapter.FolderAdapter;
import com.onlyknow.mediapicker.adapter.MediaGridAdapter;
import com.onlyknow.mediapicker.bean.FolderBean;
import com.onlyknow.mediapicker.bean.MediaBean;
import com.onlyknow.mediapicker.data.DataCallback;
import com.onlyknow.mediapicker.data.ImageLoader;
import com.onlyknow.mediapicker.data.MediaLoader;
import com.onlyknow.mediapicker.data.VideoLoader;
import com.onlyknow.mediapicker.utils.BarTintUtil;
import com.onlyknow.mediapicker.utils.ScreenUtil;
import com.onlyknow.mediapicker.utils.SpacingDecoration;
import com.onlyknow.mediapicker.views.SEImageView;

import java.util.ArrayList;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 图片选择界面
 * <p>
 * Created by ReSet on 2017/03/08.
 */

public class PickerActivity extends AppCompatActivity implements DataCallback, View.OnClickListener {
    private Intent argsIntent;
    private SEImageView back;
    private TextView preview;
    private RecyclerView recyclerView;
    private Button done, category_btn;
    private MediaGridAdapter gridAdapter;
    private ListPopupWindow mFolderPopupWindow;
    private FolderAdapter mFolderAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        argsIntent = getIntent();
        setContentView(R.layout.media_picker);
        initStatusBar();
        init();
    }

    private void init() {
        back = (SEImageView) findViewById(R.id.btn_back);
        done = (Button) findViewById(R.id.done);
        category_btn = (Button) findViewById(R.id.category_btn);
        preview = (TextView) findViewById(R.id.preview);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        setTitleBar();

        back.setOnClickListener(this);
        done.setOnClickListener(this);
        category_btn.setOnClickListener(this);
        preview.setOnClickListener(this);

        //get view end
        createAdapter();
        createFolderAdapter();
        getMediaData();
    }

    public void setTitleBar() {
        int type = argsIntent.getIntExtra(PickerConfig.SELECT_MODE, PickerConfig.PICKER_IMAGE_VIDEO);
        if (type == PickerConfig.PICKER_IMAGE_VIDEO) {
            ((TextView) findViewById(R.id.bar_title)).setText(getString(R.string.select_title));
        } else if (type == PickerConfig.PICKER_IMAGE) {
            ((TextView) findViewById(R.id.bar_title)).setText(getString(R.string.select_image_title));
        } else if (type == PickerConfig.PICKER_VIDEO) {
            ((TextView) findViewById(R.id.bar_title)).setText(getString(R.string.select_video_title));
        }
    }

    private void createAdapter() {
        //创建默认的线性LayoutManager
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, PickerConfig.GridSpanCount);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new SpacingDecoration(PickerConfig.GridSpanCount, PickerConfig.GridSpace));
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        recyclerView.setHasFixedSize(true);
        //创建并设置Adapter
        ArrayList<MediaBean> mediaBeans = new ArrayList<>();
        ArrayList<MediaBean> select = argsIntent.getParcelableArrayListExtra(PickerConfig.DEFAULT_SELECTED_LIST);
        int maxSelect = argsIntent.getIntExtra(PickerConfig.MAX_SELECT_COUNT, PickerConfig.DEFAULT_SELECTED_MAX_COUNT);
        long maxSize = argsIntent.getLongExtra(PickerConfig.MAX_SELECT_SIZE, PickerConfig.DEFAULT_SELECTED_MAX_SIZE);
        gridAdapter = new MediaGridAdapter(mediaBeans, this, select, maxSelect, maxSize);
        recyclerView.setAdapter(gridAdapter);
    }

    private void createFolderAdapter() {
        ArrayList<FolderBean> folderBeans = new ArrayList<>();
        mFolderAdapter = new FolderAdapter(folderBeans, this);
        mFolderPopupWindow = new ListPopupWindow(this);
        mFolderPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        mFolderPopupWindow.setAdapter(mFolderAdapter);
        mFolderPopupWindow.setHeight((int) (ScreenUtil.getScreenHeight(this) * 0.6));
        mFolderPopupWindow.setAnchorView(findViewById(R.id.footer));
        mFolderPopupWindow.setModal(true);
        mFolderPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mFolderAdapter.setSelectIndex(position);
                category_btn.setText(mFolderAdapter.getItem(position).name);
                gridAdapter.updateAdapter(mFolderAdapter.getSelectMedias());
                mFolderPopupWindow.dismiss();
            }
        });
    }

    @AfterPermissionGranted(119)
    private void getMediaData() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            int type = argsIntent.getIntExtra(PickerConfig.SELECT_MODE, PickerConfig.PICKER_IMAGE_VIDEO);
            if (type == PickerConfig.PICKER_IMAGE_VIDEO) {
                getLoaderManager().initLoader(type, null, new MediaLoader(this, this));
            } else if (type == PickerConfig.PICKER_IMAGE) {
                getLoaderManager().initLoader(type, null, new ImageLoader(this, this));
            } else if (type == PickerConfig.PICKER_VIDEO) {
                getLoaderManager().initLoader(type, null, new VideoLoader(this, this));
            }
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.READ_EXTERNAL_STORAGE), 119, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onData(ArrayList<FolderBean> list) {
        setView(list);
        category_btn.setText(list.get(0).name);
        mFolderAdapter.updateAdapter(list);
    }

    private void setView(ArrayList<FolderBean> list) {
        gridAdapter.updateAdapter(list.get(0).getMediaBeans());
        setButtonText();
        gridAdapter.setOnItemClickListener(new MediaGridAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, MediaBean data, ArrayList<MediaBean> selectMediaBeans) {
                setButtonText();
            }
        });
    }

    private void setButtonText() {
        int max = argsIntent.getIntExtra(PickerConfig.MAX_SELECT_COUNT, PickerConfig.DEFAULT_SELECTED_MAX_COUNT);
        done.setText(getString(R.string.done) + "(" + gridAdapter.getSelectMediaBeans().size() + "/" + max + ")");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_back) {
            finish();
        } else if (id == R.id.category_btn) {
            if (mFolderPopupWindow.isShowing()) {
                mFolderPopupWindow.dismiss();
            } else {
                mFolderPopupWindow.show();
            }
        } else if (id == R.id.done) {
            done(gridAdapter.getSelectMediaBeans());
        } else if (id == R.id.preview) {
            if (gridAdapter.getSelectMediaBeans().size() <= 0) {
                Toast.makeText(this, getString(R.string.select_null), Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, PreviewActivity.class);
            intent.putExtra(PickerConfig.MAX_SELECT_COUNT, argsIntent.getIntExtra(PickerConfig.MAX_SELECT_COUNT, PickerConfig.DEFAULT_SELECTED_MAX_COUNT));
            intent.putExtra(PickerConfig.PRE_RAW_LIST, gridAdapter.getSelectMediaBeans());
            this.startActivityForResult(intent, 200);
        }
    }

    public void done(ArrayList<MediaBean> selects) {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(PickerConfig.EXTRA_RESULT, selects);
        setResult(PickerConfig.RESULT_CODE, intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        Glide.get(this).clearMemory();
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            ArrayList<MediaBean> selects = data.getParcelableArrayListExtra(PickerConfig.EXTRA_RESULT);
            if (resultCode == PickerConfig.RESULT_UPDATE_CODE) {
                gridAdapter.updateSelectAdapter(selects);
                setButtonText();
            } else if (resultCode == PickerConfig.RESULT_CODE) {
                done(selects);
            }
        }
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
