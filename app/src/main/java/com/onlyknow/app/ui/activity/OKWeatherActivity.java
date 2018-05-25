package com.onlyknow.app.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.onlyknow.app.OKConstant;
import com.onlyknow.app.R;
import com.onlyknow.app.api.app.OKLoadWeatherApi;
import com.onlyknow.app.db.bean.OKCarouselAdBean;
import com.onlyknow.app.db.bean.OKWeatherBean;
import com.onlyknow.app.ui.OKBaseActivity;
import com.onlyknow.app.ui.view.OKSEImageView;
import com.onlyknow.app.utils.OKLoadBannerImage;
import com.onlyknow.app.utils.OKLogUtil;
import com.onlyknow.app.utils.OKNetUtil;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 天气预报界面
 * 传入天气Json数据,并转换成天气对象
 * 如果天气预报天数不足5天则该界面退出
 * <p>
 * Created by ReSet on 2018/03/01.
 */

public class OKWeatherActivity extends OKBaseActivity implements OKLoadWeatherApi.onCallBack {
    @Bind(R.id.ok_collapsing_toolbar_back_image)
    OKSEImageView okCollapsingToolbarBackImage;

    @Bind(R.id.ok_collapsing_toolbar_progressBar)
    ProgressBar mProgressBar;

    @Bind(R.id.ok_collapsing_toolbar)
    Toolbar okCollapsingToolbar;

    @Bind(R.id.ok_activity_weather_head_image)
    ImageView okActivityWeatherHeadImage;
    @Bind(R.id.ok_activity_weather_head_city_text)
    TextView okActivityWeatherHeadCityText;
    @Bind(R.id.ok_activity_weather_head_type_text)
    TextView okActivityWeatherHeadTypeText;
    @Bind(R.id.ok_activity_weather_head_fengxiang_text)
    TextView okActivityWeatherHeadFengxiangText;
    @Bind(R.id.ok_activity_weather_head_wendu_text)
    TextView okActivityWeatherHeadWenduText;

    @Bind(R.id.ok_activity_weather_CollapsingToolbar_layout)
    CollapsingToolbarLayout okActivityWeatherCollapsingToolbarLayout;

    @Bind(R.id.ok_activity_weather_time_riqi_text)
    TextView okActivityWeatherTimeRiqiText;
    @Bind(R.id.ok_activity_weather_time_wendufanwei_text)
    TextView okActivityWeatherTimeWendufanweiText;
    @Bind(R.id.ok_activity_weather_time_type_image)
    ImageView okActivityWeatherTimeTypeImage;
    @Bind(R.id.ok_activity_weather_time_type_text)
    TextView okActivityWeatherTimeTypeText;
    @Bind(R.id.ok_activity_weather_time_fengxiang_text)
    TextView okActivityWeatherTimeFengxiangText;

    @Bind(R.id.ok_activity_weather_time1_riqi_text)
    TextView okActivityWeatherTime1RiqiText;
    @Bind(R.id.ok_activity_weather_time1_wendufanwei_text)
    TextView okActivityWeatherTime1WendufanweiText;
    @Bind(R.id.ok_activity_weather_time1_type_image)
    ImageView okActivityWeatherTime1TypeImage;
    @Bind(R.id.ok_activity_weather_time1_type_text)
    TextView okActivityWeatherTime1TypeText;
    @Bind(R.id.ok_activity_weather_time1_fengxiang_text)
    TextView okActivityWeatherTime1FengxiangText;

    @Bind(R.id.ok_activity_weather_time2_riqi_text)
    TextView okActivityWeatherTime2RiqiText;
    @Bind(R.id.ok_activity_weather_time2_wendufanwei_text)
    TextView okActivityWeatherTime2WendufanweiText;
    @Bind(R.id.ok_activity_weather_time2_type_image)
    ImageView okActivityWeatherTime2TypeImage;
    @Bind(R.id.ok_activity_weather_time2_type_text)
    TextView okActivityWeatherTime2TypeText;
    @Bind(R.id.ok_activity_weather_time2_fengxiang_text)
    TextView okActivityWeatherTime2FengxiangText;

    @Bind(R.id.ok_activity_weather_time3_riqi_text)
    TextView okActivityWeatherTime3RiqiText;
    @Bind(R.id.ok_activity_weather_time3_wendufanwei_text)
    TextView okActivityWeatherTime3WendufanweiText;
    @Bind(R.id.ok_activity_weather_time3_type_image)
    ImageView okActivityWeatherTime3TypeImage;
    @Bind(R.id.ok_activity_weather_time3_type_text)
    TextView okActivityWeatherTime3TypeText;
    @Bind(R.id.ok_activity_weather_time3_fengxiang_text)
    TextView okActivityWeatherTime3FengxiangText;

    @Bind(R.id.ok_activity_weather_time4_riqi_text)
    TextView okActivityWeatherTime4RiqiText;
    @Bind(R.id.ok_activity_weather_time4_wendufanwei_text)
    TextView okActivityWeatherTime4WendufanweiText;
    @Bind(R.id.ok_activity_weather_time4_type_image)
    ImageView okActivityWeatherTime4TypeImage;
    @Bind(R.id.ok_activity_weather_time4_type_text)
    TextView okActivityWeatherTime4TypeText;
    @Bind(R.id.ok_activity_weather_time4_fengxiang_text)
    TextView okActivityWeatherTime4FengxiangText;

    @Bind(R.id.ok_activity_weather_reGet)
    FloatingActionButton reGetFloatingActionButton;

    @Bind(R.id.ok_activity_weather_ganmao_text)
    TextView okActivityWeatherGanmaoText;

    @Bind(R.id.ok_activity_weather_banner)
    Banner mBanner;

    private OKWeatherBean mOKWeatherBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ok_activity_weather);
        ButterKnife.bind(this);
        mOKWeatherBean = OKWeatherBean.fromJson(getIntent().getExtras().getString("JSON_DATA"));
        if (mOKWeatherBean == null) {
            finish();
            return;
        }
        initUserBody();
        initWeatherBody();
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startBanner();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopBanner();
    }

    private void init() {
        setSupportActionBar(okCollapsingToolbar);
        okCollapsingToolbarBackImage.setVisibility(View.VISIBLE);
        okActivityWeatherCollapsingToolbarLayout.setTitle("天气详情");
        okActivityWeatherCollapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.md_pink_200));
        okActivityWeatherCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);

        mBanner.setImageLoader(new OKLoadBannerImage(true));
        mBanner.setBannerAnimation(Transformer.DepthPage);
        mBanner.isAutoPlay(true);
        mBanner.setDelayTime(5000);
        mBanner.setIndicatorGravity(BannerConfig.CENTER);
        mBanner.setImages(OKConstant.getAdImages());
        startBanner();

        reGetFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressBar.setVisibility(View.VISIBLE);
                getWeatherInfo();
            }
        });

        mBanner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                OKCarouselAdBean.ADImage adImage = OKConstant.getAdImages().get(position);
                if (!TextUtils.isEmpty(adImage.getLink())) {
                    Bundle bundle = new Bundle();
                    bundle.putString("WEB_LINK", adImage.getLink());
                    startUserActivity(bundle, OKBrowserActivity.class);
                } else {
                    showSnackBar(okCollapsingToolbar, "没有发现链接", "");
                }
            }
        });

        okCollapsingToolbarBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        bindWeatherInfo();
    }

    private void bindWeatherInfo() {
        List<OKWeatherBean.Forecast> mForecastList = mOKWeatherBean.getData().getForecast();
        if (mForecastList == null || mForecastList.size() < 5) {
            OKLogUtil.print("天气信息错误,请重新获取!");
            finish();
            return;
        }

        int i = new Random().nextInt(5);
        if (i < OKConstant.getCarouselImages().size()) {
            GlideBlurApi(okActivityWeatherHeadImage, OKConstant.getCarouselImages().get(i).getUrl(), R.drawable.topgd1, R.drawable.topgd1);
        } else if (OKConstant.getCarouselImages().size() != 0) {
            GlideBlurApi(okActivityWeatherHeadImage, OKConstant.getCarouselImages().get(0).getUrl(), R.drawable.topgd1, R.drawable.topgd1);
        } else {
            GlideBlurApi(okActivityWeatherHeadImage, R.drawable.topgd1, R.drawable.topgd1, R.drawable.topgd1);
        }

        OKWeatherBean.Forecast forecast1 = mForecastList.get(0);
        okActivityWeatherHeadCityText.setText(mOKWeatherBean.data.city + "市");
        okActivityWeatherHeadTypeText.setText(forecast1.type);
        okActivityWeatherHeadFengxiangText.setText(forecast1.fengxiang);
        okActivityWeatherHeadWenduText.setText(mOKWeatherBean.data.wendu + " ℃");

        String date = forecast1.date;
        int ri = date.indexOf("日");
        date = date.substring(ri + 1, date.length());
        okActivityWeatherTimeRiqiText.setText(date);
        okActivityWeatherTimeWendufanweiText.setText(forecast1.low.replace("低温 ", "") + " / " + forecast1.high.replace("高温 ", ""));
        bindWeatherView(okActivityWeatherTimeTypeImage, forecast1.type);
        okActivityWeatherTimeTypeText.setText(forecast1.type);
        okActivityWeatherTimeFengxiangText.setText(forecast1.fengxiang + windPowerFormat(forecast1.fengli));

        OKWeatherBean.Forecast forecast2 = mForecastList.get(1);
        String date2 = forecast2.date;
        int ri2 = date2.indexOf("日");
        date2 = date2.substring(ri2 + 1, date2.length());
        okActivityWeatherTime1RiqiText.setText(date2);
        okActivityWeatherTime1WendufanweiText.setText(forecast2.low.replace("低温 ", "") + " / " + forecast2.high.replace("高温 ", ""));
        bindWeatherView(okActivityWeatherTime1TypeImage, forecast2.type);
        okActivityWeatherTime1TypeText.setText(forecast2.type);
        okActivityWeatherTime1FengxiangText.setText(forecast2.fengxiang + windPowerFormat(forecast2.fengli));

        OKWeatherBean.Forecast forecast3 = mForecastList.get(2);
        String date3 = forecast3.date;
        int ri3 = date3.indexOf("日");
        date3 = date3.substring(ri3 + 1, date3.length());
        okActivityWeatherTime2RiqiText.setText(date3);
        okActivityWeatherTime2WendufanweiText.setText(forecast3.low.replace("低温 ", "") + " / " + forecast3.high.replace("高温 ", ""));
        bindWeatherView(okActivityWeatherTime2TypeImage, forecast3.type);
        okActivityWeatherTime2TypeText.setText(forecast3.type);
        okActivityWeatherTime2FengxiangText.setText(forecast3.fengxiang + windPowerFormat(forecast3.fengli));

        OKWeatherBean.Forecast forecast4 = mForecastList.get(3);
        String date4 = forecast4.date;
        int ri4 = date4.indexOf("日");
        date4 = date4.substring(ri4 + 1, date4.length());
        okActivityWeatherTime3RiqiText.setText(date4);
        okActivityWeatherTime3WendufanweiText.setText(forecast4.low.replace("低温 ", "") + " / " + forecast4.high.replace("高温 ", ""));
        bindWeatherView(okActivityWeatherTime3TypeImage, forecast4.type);
        okActivityWeatherTime3TypeText.setText(forecast4.type);
        okActivityWeatherTime3FengxiangText.setText(forecast4.fengxiang + windPowerFormat(forecast4.fengli));

        OKWeatherBean.Forecast forecast5 = mForecastList.get(4);
        String date5 = forecast5.date;
        int ri5 = date5.indexOf("日");
        date5 = date5.substring(ri5 + 1, date5.length());
        okActivityWeatherTime4RiqiText.setText(date5);
        okActivityWeatherTime4WendufanweiText.setText(forecast5.low.replace("低温 ", "") + " / " + forecast5.high.replace("高温 ", ""));
        bindWeatherView(okActivityWeatherTime4TypeImage, forecast5.type);
        okActivityWeatherTime4TypeText.setText(forecast5.type);
        okActivityWeatherTime4FengxiangText.setText(forecast5.fengxiang + windPowerFormat(forecast5.fengli));

        okActivityWeatherGanmaoText.setText(mOKWeatherBean.data.ganmao);
    }

    private String windPowerFormat(String wp) {
        wp = wp.replace("[", "");
        wp = wp.replace("]", "");
        wp = wp.replace("<", "");
        wp = wp.replace(">", "");
        wp = wp.replace("!", "");
        wp = wp.replace("CDATA", "");
        return wp;
    }

    private void startBanner() {
        if (mBanner != null) {
            mBanner.start();
        }
    }

    private void stopBanner() {
        if (mBanner != null) {
            mBanner.stopAutoPlay();
        }
    }

    private void bindWeatherIcon(ImageView view, int resId) {
        GlideApi(view, resId, R.drawable.tianqi_other, R.drawable.tianqi_other);
    }

    private void bindWeatherView(ImageView view, String Type) {
        if (Type.indexOf("晴") != -1) {
            bindWeatherIcon(view, R.drawable.tianqi_qing);
        } else if (Type.indexOf("阴") != -1) {
            bindWeatherIcon(view, R.drawable.tianqi_ying);
        } else if (Type.indexOf("多云") != -1) {
            bindWeatherIcon(view, R.drawable.tianqi_duoyun);
        } else if (Type.indexOf("大雨") != -1) {
            bindWeatherIcon(view, R.drawable.tianqi_dayu);
        } else if (Type.indexOf("小雨") != -1) {
            bindWeatherIcon(view, R.drawable.tianqi_xiaoyu);
        } else if (Type.indexOf("中雨") != -1) {
            bindWeatherIcon(view, R.drawable.tianqi_zhonyu);
        } else if (Type.indexOf("雷阵雨") != -1) {
            bindWeatherIcon(view, R.drawable.tianqi_leizhenyu);
        } else if (Type.indexOf("大雪") != -1) {
            bindWeatherIcon(view, R.drawable.tianqi_daxue);
        } else if (Type.indexOf("小雪") != -1) {
            bindWeatherIcon(view, R.drawable.tianqi_xiaoxue);
        } else if (Type.indexOf("暴雨") != -1) {
            bindWeatherIcon(view, R.drawable.tianqi_baoyu);
        } else if (Type.indexOf("阵雨") != -1) {
            bindWeatherIcon(view, R.drawable.tianqi_zhenyu);
        } else if (Type.indexOf("雨夹雪") != -1) {
            bindWeatherIcon(view, R.drawable.tianqi_yujaxue);
        } else if (Type.indexOf("沙尘暴") != -1) {
            bindWeatherIcon(view, R.drawable.tianqi_shachenbao);
        } else if (Type.indexOf("浮尘") != -1) {
            bindWeatherIcon(view, R.drawable.tianqi_fucheng);
        } else if (Type.indexOf("雾霾") != -1) {
            bindWeatherIcon(view, R.drawable.tianqi_wumai);
        } else if (Type.indexOf("雾") != -1) {
            bindWeatherIcon(view, R.drawable.tianqi_wu);
        } else if (Type.indexOf("台风") != -1) {
            bindWeatherIcon(view, R.drawable.tianqi_taifeng);
        } else if (Type.indexOf("龙卷风") != -1) {
            bindWeatherIcon(view, R.drawable.tianqi_lonjuanfeng);
        } else if (Type.indexOf("大风") != -1) {
            bindWeatherIcon(view, R.drawable.tianqi_dafeng);
        } else if (Type.indexOf("风") != -1) {
            bindWeatherIcon(view, R.drawable.tianqi_feng);
        } else {
            bindWeatherIcon(view, R.drawable.tianqi_other);
        }
    }

    private OKLoadWeatherApi mWeatherApi;

    private void getWeatherInfo() {
        if (OKNetUtil.isNet(this)) {
            if (mWeatherApi != null) {
                mWeatherApi.cancelTask();
            }

            OKLoadWeatherApi.Params params = new OKLoadWeatherApi.Params();
            params.setCityId(USER_BODY.getString("CITY_ID", ""));
            params.setCityName(USER_BODY.getString("CITY_NAME", ""));
            params.setDistrict(USER_BODY.getString("DISTRICT", ""));

            mWeatherApi = new OKLoadWeatherApi(this);
            mWeatherApi.requestWeather(params, this);
        } else {
            showSnackBar(okActivityWeatherCollapsingToolbarLayout, "没有网络连接", "");
        }
    }

    @Override
    public void loadWeatherComplete(OKWeatherBean weatherBean) {
        if (weatherBean == null) {
            mProgressBar.setVisibility(View.GONE);
            showSnackBar(okActivityWeatherCollapsingToolbarLayout, "天气获取失败", "ErrorCode: " + OKConstant.WEATHER_BEAN_ERROR);
            return;
        }

        mOKWeatherBean = weatherBean;

        saveWeatherInfo(mOKWeatherBean);

        bindWeatherInfo();

        mProgressBar.setVisibility(View.GONE);
    }
}
