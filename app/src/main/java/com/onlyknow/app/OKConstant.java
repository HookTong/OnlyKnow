package com.onlyknow.app;

import android.os.Environment;

import com.onlyknow.app.database.bean.OKCarouselAdBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public final static String IMAGE_PATH = Environment.getExternalStorageDirectory().getPath() + "/WeiZhi/Image/";
    public final static String GLIDE_PATH = Environment.getExternalStorageDirectory().getPath() + "/WeiZhi/Cache/";

    // app网址
    public final static String SERVICE_ROOT_URL = "http://101.132.168.25:8090/WeiZhiService/";
    public final static String ONLY_KNOW_RESOURCES_CARD_IMAGE_URL = SERVICE_ROOT_URL + "CardImage/";
    public final static String ONLY_KNOW_RESOURCES_USER_HEAD_PORTRAIT_URL = SERVICE_ROOT_URL + "UserHeadPortrait/";
    public final static String ONLY_KNOW_RESOURCES_USER_HEAD_URL = SERVICE_ROOT_URL + "UserHead/";
    public final static String ONLY_KNOW_RESOURCES_CAROUSEL_URL = SERVICE_ROOT_URL + "Carousel/";
    public final static String ONLY_KNOW_RESOURCES_FEEDBACK_URL = SERVICE_ROOT_URL + "FeedBack/";
    public final static String ONLY_KNOW_RESOURCES_APP_URL = SERVICE_ROOT_URL + "APP/";
    public final static String ONLY_KNOW_RESOURCES_AD_URL = SERVICE_ROOT_URL + "AD/";

    public final static String ONLY_KNOW_OFFICIAL_WEBSITE_URL = SERVICE_ROOT_URL + "/index.jsp"; // 唯知官网
    public final static String ONLY_KNOW_SOURCE_CODE_URL = "https://github.com/TongXingWen22/OnlyKnow"; // 源代码GitHub主页

    public final static String EXPLORE_FIND_URL = "http://m.neihanshequ.com/"; // 发现url

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

    private final static List<Map<String, Object>> carouselImages = new ArrayList<>();

    private final static List<Map<String, String>> adImages = new ArrayList<>();

    public static List<Map<String, Object>> getCarouselImages() {
        if (carouselImages.size() == 0) {
            Map<String, Object> map0 = new HashMap<>();
            map0.put(OKCarouselAdBean.KEY_URL, ONLY_KNOW_RESOURCES_CAROUSEL_URL + "head001.jpg");
            map0.put(OKCarouselAdBean.KEY_RID, R.drawable.topgd1);

            Map<String, Object> map1 = new HashMap<>();
            map1.put(OKCarouselAdBean.KEY_URL, ONLY_KNOW_RESOURCES_CAROUSEL_URL + "head002.jpg");
            map1.put(OKCarouselAdBean.KEY_RID, R.drawable.topgd2);

            Map<String, Object> map2 = new HashMap<>();
            map2.put(OKCarouselAdBean.KEY_URL, ONLY_KNOW_RESOURCES_CAROUSEL_URL + "head003.jpg");
            map2.put(OKCarouselAdBean.KEY_RID, R.drawable.topgd3);

            Map<String, Object> map3 = new HashMap<>();
            map3.put(OKCarouselAdBean.KEY_URL, ONLY_KNOW_RESOURCES_CAROUSEL_URL + "head004.jpg");
            map3.put(OKCarouselAdBean.KEY_RID, R.drawable.topgd4);

            Map<String, Object> map4 = new HashMap<>();
            map4.put(OKCarouselAdBean.KEY_URL, ONLY_KNOW_RESOURCES_CAROUSEL_URL + "head005.jpg");
            map4.put(OKCarouselAdBean.KEY_RID, R.drawable.topgd5);

            carouselImages.add(map0);
            carouselImages.add(map1);
            carouselImages.add(map2);
            carouselImages.add(map3);
            carouselImages.add(map4);
        }
        return carouselImages;
    }

    public static void setCarouselImages(List<Map<String, Object>> list) {
        if (list != null && list.size() != 0) {
            carouselImages.clear();
            carouselImages.addAll(list);
        }
    }

    public static List<Map<String, String>> getAdImages() {
        if (adImages.size() == 0) {
            Map<String, String> map = new HashMap<>();
            map.put(OKCarouselAdBean.KEY_URL, ONLY_KNOW_RESOURCES_AD_URL + "onlyKnowAd.jpg");
            map.put(OKCarouselAdBean.KEY_LINK, ONLY_KNOW_OFFICIAL_WEBSITE_URL);
            adImages.add(map);
        }
        return adImages;
    }

    public static void setAdImages(List<Map<String, String>> list) {
        if (list != null && list.size() != 0) {
            adImages.clear();
            adImages.addAll(list);
        }
    }

    public static String getNowDateByString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd/HH/mm");
        return dateFormat.format(new Date());
    }

    public static long getNowDateByLong() {
        return new Date().getTime();
    }
}
