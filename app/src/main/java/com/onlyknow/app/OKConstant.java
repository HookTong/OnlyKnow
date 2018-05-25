package com.onlyknow.app;

import android.os.Environment;

import com.onlyknow.app.db.bean.OKCarouselAdBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
*  2018-02-05 15:14
*  全局静态数据
* */

public final class OKConstant {
    public final static String APP_VERSION = "2.0.9"; // app版本号

    // 广播动作
    public final static String ACTION_UPDATE_CAROUSE_AND_AD_IMAGE = "com.onlyknow.app.ACTION_UPDATE_CAROUSE_AND_AD_IMAGE";
    public final static String ACTION_SHOW_NOTICE = "com.onlyknow.app.ACTION_SHOW_NOTICE";
    public final static String ACTION_RESET_LOCATION = "com.onlyknow.app.ACTION_RE_LOCATION";

    // app本地路径
    public final static String IMAGE_PATH = Environment.getExternalStorageDirectory().getPath() + "/OnlyKnow/Image/";
    public final static String GLIDE_PATH = Environment.getExternalStorageDirectory().getPath() + "/OnlyKnow/Cache/";

    // app网址
    public final static String SERVICE_ROOT_URL = "http://101.132.168.25:8090/onlyknow/";
    public final static String ONLY_KNOW_RESOURCES_CARD_IMAGE_URL = SERVICE_ROOT_URL + "CardImage/";
    public final static String ONLY_KNOW_RESOURCES_USER_HEAD_PORTRAIT_URL = SERVICE_ROOT_URL + "UserHeadPortrait/";
    public final static String ONLY_KNOW_RESOURCES_USER_HEAD_URL = SERVICE_ROOT_URL + "UserHead/";
    public final static String ONLY_KNOW_RESOURCES_CAROUSEL_URL = SERVICE_ROOT_URL + "Carousel/";
    public final static String ONLY_KNOW_RESOURCES_FEEDBACK_URL = SERVICE_ROOT_URL + "FeedBack/";
    public final static String ONLY_KNOW_RESOURCES_APP_URL = SERVICE_ROOT_URL + "APP/";
    public final static String ONLY_KNOW_RESOURCES_AD_URL = SERVICE_ROOT_URL + "AD/";

    public final static String ONLY_KNOW_OFFICIAL_WEBSITE_URL = SERVICE_ROOT_URL + "/index.jsp"; // 唯知官网
    public final static String ONLY_KNOW_SOURCE_CODE_URL = "https://github.com/TongXingWen22/OnlyKnow"; // 源代码GitHub主页

    public final static String EXPLORE_FIND_URL = "http://101.132.168.25:8090/onlyknow/Page/index.html"; // 发现url

    // 错误对照码
    public final static String WEATHER_BEAN_ERROR = "0x00000";
    public final static String WEATHER_ID_ERROR = "0x00001"; // 因为CityID导致天气获取失败
    public final static String WEATHER_NAME_ERROR = "0x00002"; // 因为CityName导致天气获取失败
    public final static String GOODS_BUY_ERROR = "0x00003"; // 商品购买错误
    public final static String ATTENTION_CANCEL_ERROR = "0x00004"; // 关注取消错误
    public final static String ARTICLE_CANCEL_ERROR = "0x00005"; // 文章删除错误
    public final static String SERVICE_ERROR = "0x00006"; // 服务错误
    public final static String COMMENT_ERROR = "0x00007"; // 评论错误
    public final static String NOT_INIT_ERROR = "0x00008"; // 对象未实例化
    public final static String DATA_SOURCE_ERROR = "0x00009";

    private final static List<OKCarouselAdBean.CarouselImage> carouselImages = new ArrayList<>();

    private final static List<OKCarouselAdBean.ADImage> adImages = new ArrayList<>();

    // 操作方法
    public static List<OKCarouselAdBean.CarouselImage> getCarouselImages() {
        if (carouselImages.size() == 0) {
            OKCarouselAdBean.CarouselImage carouselImage1 = new OKCarouselAdBean.CarouselImage();
            carouselImage1.setUrl(ONLY_KNOW_RESOURCES_CAROUSEL_URL + "def001.jpg");
            carouselImage1.setResId(R.drawable.topgd1);

            OKCarouselAdBean.CarouselImage carouselImage2 = new OKCarouselAdBean.CarouselImage();
            carouselImage2.setUrl(ONLY_KNOW_RESOURCES_CAROUSEL_URL + "def002.jpg");
            carouselImage2.setResId(R.drawable.topgd2);

            OKCarouselAdBean.CarouselImage carouselImage3 = new OKCarouselAdBean.CarouselImage();
            carouselImage3.setUrl(ONLY_KNOW_RESOURCES_CAROUSEL_URL + "def003.jpg");
            carouselImage3.setResId(R.drawable.topgd3);

            OKCarouselAdBean.CarouselImage carouselImage4 = new OKCarouselAdBean.CarouselImage();
            carouselImage4.setUrl(ONLY_KNOW_RESOURCES_CAROUSEL_URL + "def004.jpg");
            carouselImage4.setResId(R.drawable.topgd4);

            OKCarouselAdBean.CarouselImage carouselImage5 = new OKCarouselAdBean.CarouselImage();
            carouselImage5.setUrl(ONLY_KNOW_RESOURCES_CAROUSEL_URL + "def005.jpg");
            carouselImage5.setResId(R.drawable.topgd5);

            carouselImages.add(carouselImage1);
            carouselImages.add(carouselImage2);
            carouselImages.add(carouselImage3);
            carouselImages.add(carouselImage4);
            carouselImages.add(carouselImage5);
        }
        return carouselImages;
    }

    public static void setCarouselImages(List<OKCarouselAdBean.CarouselImage> list) {
        carouselImages.clear();
        carouselImages.addAll(list);
    }

    public static List<OKCarouselAdBean.ADImage> getAdImages() {
        if (adImages.size() == 0) {
            OKCarouselAdBean.ADImage adImage = new OKCarouselAdBean.ADImage();
            adImage.setUrl(ONLY_KNOW_RESOURCES_AD_URL + "onlyKnowAd.jpg");
            adImage.setLink(ONLY_KNOW_OFFICIAL_WEBSITE_URL);

            adImages.add(adImage);
        }
        return adImages;
    }

    public static void setAdImages(List<OKCarouselAdBean.ADImage> list) {
        adImages.clear();
        adImages.addAll(list);
    }

    public static String getNowDateByString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd/HH/mm");
        return dateFormat.format(new Date());
    }

    public static long getNowDateByLong() {
        return new Date().getTime();
    }
}
