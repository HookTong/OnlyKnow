package com.onlyknow.app;

import android.os.Environment;
import android.support.v4.util.LruCache;

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

public class OKConstant {
    public final static String APP_VERSION = "2.0.8"; // app版本号

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
    public final static String WEATHER_ID_ERROR = "0x0001"; // 因为CityID导致天气获取失败
    public final static String WEATHER_NAME_ERROR = "0x0002"; // 因为CityName导致天气获取失败
    public final static String GOODS_BUY_ERROR = "0x0003"; // 商品购买错误
    public final static String ATTENTION_CANCEL_ERROR = "0x0004"; // 关注取消错误
    public final static String ARTICLE_CANCEL_ERROR = "0x0005"; // 文章删除错误
    public final static String SERVICE_ERROR = "0x0006"; // 服务错误
    public final static String COMMENT_ERROR = "0x0007"; // 评论错误
    public final static String NOT_INIT_ERROR = "0x0008"; // 对象未实例化
    public final static String DATA_SOURCE_ERROR = "0x0009";

    public final static int EXPLORE_COUNT = 30;
    public final static String EXPLORE_LOAD_COUNT = "30";

    public final static int NEAR_COUNT = 30;
    public final static String NEAR_LOAD_COUNT = "30";

    public final static int DYNAMIC_COUNT = 20;
    public final static String DYNAMIC_LOAD_COUNT = "20";

    public final static int ATTENTION_COUNT = 30;
    public final static String ATTENTION_LOAD_COUNT = "30";

    public final static int WATCH_COUNT = 20;
    public final static String WATCH_LOAD_COUNT = "20";

    public final static int HOME_COUNT = 30;
    public final static String HOME_LOAD_COUNT = "30";

    public final static int HOT_COUNT = 20;
    public final static String HOT_LOAD_COUNT = "20";

    public final static int GOODS_COUNT = 30;
    public final static String GOODS_LOAD_COUNT = "30";

    public final static int COMMENT_COUNT = 30;
    public final static String COMMENT_LOAD_COUNT = "30";

    public final static int COMMENT_REPLY_COUNT = 30;
    public final static String COMMENT_REPLY_LOAD_COUNT = "30";

    public final static int CARD_AND_COMMENT_COUNT = 30;
    public final static String CARD_AND_COMMENT_LOAD_COUNT = "30";

    public final static int APPROVE_COUNT = 30;
    public final static String APPROVE_LOAD_COUNT = "30";

    private final static List<Map<String, Object>> HEAD_IMAGE_LIST_URL = new ArrayList<>();

    private final static List<Map<String, String>> AD_IMAGE_AND_LINK_LIST_URL = new ArrayList<>();

    public final static List<Map<String, Object>> getHeadUrls() {
        if (HEAD_IMAGE_LIST_URL.size() == 0) {
            Map<String, Object> map0 = new HashMap<>();
            map0.put("URL", ONLY_KNOW_RESOURCES_CAROUSEL_URL + "head001.jpg");
            map0.put("RES_ID", R.drawable.topgd1);

            Map<String, Object> map1 = new HashMap<>();
            map1.put("URL", ONLY_KNOW_RESOURCES_CAROUSEL_URL + "head002.jpg");
            map1.put("RES_ID", R.drawable.topgd2);

            Map<String, Object> map2 = new HashMap<>();
            map2.put("URL", ONLY_KNOW_RESOURCES_CAROUSEL_URL + "head003.jpg");
            map2.put("RES_ID", R.drawable.topgd3);

            Map<String, Object> map3 = new HashMap<>();
            map3.put("URL", ONLY_KNOW_RESOURCES_CAROUSEL_URL + "head004.jpg");
            map3.put("RES_ID", R.drawable.topgd4);

            Map<String, Object> map4 = new HashMap<>();
            map4.put("URL", ONLY_KNOW_RESOURCES_CAROUSEL_URL + "head005.jpg");
            map4.put("RES_ID", R.drawable.topgd5);

            HEAD_IMAGE_LIST_URL.add(map0);
            HEAD_IMAGE_LIST_URL.add(map1);
            HEAD_IMAGE_LIST_URL.add(map2);
            HEAD_IMAGE_LIST_URL.add(map3);
            HEAD_IMAGE_LIST_URL.add(map4);
        }
        return HEAD_IMAGE_LIST_URL;
    }

    public static boolean EXPLORE_HEAD_DATA_CHANGED = false;
    public static boolean NEAR_HEAD_DATA_CHANGED = false;
    public static boolean HISTORY_HEAD_DATA_CHANGED = false;

    public final static void setHeadUrls(List<Map<String, Object>> list) {
        if (list != null && list.size() != 0) {
            HEAD_IMAGE_LIST_URL.clear();
            HEAD_IMAGE_LIST_URL.addAll(list);
            EXPLORE_HEAD_DATA_CHANGED = true;
            NEAR_HEAD_DATA_CHANGED = true;
            HISTORY_HEAD_DATA_CHANGED = true;
        }
    }

    public final static List<Map<String, String>> getAdUrls() {
        if (AD_IMAGE_AND_LINK_LIST_URL.size() == 0) {
            Map<String, String> map = new HashMap<>();
            map.put("URL", ONLY_KNOW_RESOURCES_AD_URL + "onlyKnowAd.jpg");
            map.put("LINK", ONLY_KNOW_OFFICIAL_WEBSITE_URL);
            AD_IMAGE_AND_LINK_LIST_URL.add(map);
        }
        return AD_IMAGE_AND_LINK_LIST_URL;
    }

    public static boolean EXPLORE_AD_DATA_CHANGED = false;

    public final static void setAdUrls(List<Map<String, String>> list) {
        if (list != null && list.size() != 0) {
            AD_IMAGE_AND_LINK_LIST_URL.clear();
            AD_IMAGE_AND_LINK_LIST_URL.addAll(list);
            EXPLORE_AD_DATA_CHANGED = true;
        }
    }

    // 数据副本
    private final static LruCache<String, List> BEAN_LIST_CACHE = new LruCache<>((int) (Runtime.getRuntime().maxMemory() / 1024) / 6);

    public final static List getListCache(int interfaceType) {
        String key = Integer.toString(interfaceType);
        if (BEAN_LIST_CACHE.get(key) == null) {
            return new ArrayList();
        }
        return BEAN_LIST_CACHE.get(key);
    }

    public final static void putListCache(int interfaceType, List list) {
        if (list == null) {
            return;
        }
        String key = Integer.toString(interfaceType);
        List sourceList = BEAN_LIST_CACHE.get(key);
        if (sourceList == null) {
            sourceList = new ArrayList();
        }
        sourceList.clear();
        sourceList.addAll(list);
        BEAN_LIST_CACHE.put(key, sourceList);
    }

    public final static void removeListCache(int interfaceType, int position) {
        String key = Integer.toString(interfaceType);
        List sourceList = BEAN_LIST_CACHE.get(key);
        if (sourceList == null || position >= sourceList.size()) {
            return;
        }
        sourceList.remove(position);
    }

    public final static void clearListCache(int interfaceType) {
        String key = Integer.toString(interfaceType);
        List sourceList = BEAN_LIST_CACHE.get(key);
        if (sourceList == null) {
            return;
        }
        sourceList.clear();
    }

    public final static String getNowDateByString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd/HH/mm");
        return dateFormat.format(new Date());
    }

    public final static long getNowDateByLong() {
        return new Date().getTime();
    }
}
