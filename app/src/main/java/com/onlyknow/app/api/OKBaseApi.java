package com.onlyknow.app.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.onlyknow.app.database.bean.OKAppInfoBean;
import com.onlyknow.app.database.bean.OKAttentionBean;
import com.onlyknow.app.database.bean.OKCardBean;
import com.onlyknow.app.database.bean.OKCarouselAdBean;
import com.onlyknow.app.database.bean.OKGoodsBean;
import com.onlyknow.app.database.bean.OKMeCommentCardBean;
import com.onlyknow.app.database.bean.OKSearchBean;
import com.onlyknow.app.database.bean.OKUserInfoBean;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.onlyknow.app.net.OKWebService.OKHttpApiGet;
import static com.onlyknow.app.net.OKWebService.OKHttpApiPostFromFile;
import static com.onlyknow.app.net.OKWebService.OkHttpApiPost;

public class OKBaseApi {
    public final ExecutorService exec = Executors.newFixedThreadPool(100);

    private final String IP = "101.132.168.25:8090";

    private final String ExploreCard_URL = "http://" + IP + "/OKExploreCardApi";

    private final String NearCard_URL = "http://" + IP + "/OKNearCardApi";

    private final String UserCard_URL = "http://" + IP + "/OKHomeCardApi";

    private final String ManagerGoods_URL = "http://" + IP + "/OKManagerGoodsApi";

    private final String ManagerCard_URL = "http://" + IP + "/OKManagerCardApi";

    private final String ManagerUser_URL = "http://" + IP + "/OKManagerUserApi";

    private final String Attention_URL = "http://" + IP + "/OKAttentionUserApi";

    private final String Watch_URL = "http://" + IP + "/OKWatchCardApi";

    private final String CardAndComment_Url = "http://" + IP + "/OKCommentCardApi";

    private final String HotCard_URL = "http://" + IP + "/OKHotCardApi";

    private final String AddComment_URL = "http://" + IP + "/OKAddCommentApi";

    private final String GoodsEntry_URL = "http://" + IP + "/OKGoodsApi";

    private final String CommentCard_URL = "http://" + IP + "/OKCommentApi";

    private final String ManagerComment_URL = "http://" + IP + "/OKManagerCommentApi";

    private final String Search_Url = "http://" + IP + "/OKSearchApi";

    private final String AppInfo_URL = "http://" + IP + "/OKAppInfoApi";

    private final String AddCard_URL = "http://" + IP + "/OKAddCardApi";

    private final String CarouselAd_URL = "http://" + IP + "/OKCarouselAdApi";

    private final String FeedBack_URL = "http://" + IP + "/OKFeedBackApi";

    private final String Report_URL = "http://" + IP + "/OKReportApi";

    protected List<OKCardBean> getExploreCard(Map<String, String> params) {
        String json = OkHttpApiPost(ExploreCard_URL, params);

        if (json == null) return null;

        Gson gson = new Gson();

        Type type = new TypeToken<OKServiceResult<List<OKCardBean>>>() {
        }.getType();

        OKServiceResult<List<OKCardBean>> okServiceResult = gson.fromJson(json, type);

        if (okServiceResult != null && okServiceResult.isSuccess()) {
            return okServiceResult.getData();
        }

        return null;
    }

    protected List<OKCardBean> getNearCard(Map<String, String> params) {
        String json = OkHttpApiPost(NearCard_URL, params);

        if (json == null) return null;

        Gson gson = new Gson();

        Type type = new TypeToken<OKServiceResult<List<OKCardBean>>>() {
        }.getType();

        OKServiceResult<List<OKCardBean>> okServiceResult = gson.fromJson(json, type);

        if (okServiceResult != null && okServiceResult.isSuccess()) {
            return okServiceResult.getData();
        }

        return null;
    }

    protected List<OKCardBean> getUserCard(Map<String, String> params) {
        String json = OkHttpApiPost(UserCard_URL, params);

        if (json == null) return null;

        Gson gson = new Gson();

        Type type = new TypeToken<OKServiceResult<List<OKCardBean>>>() {
        }.getType();

        OKServiceResult<List<OKCardBean>> okServiceResult = gson.fromJson(json, type);

        if (okServiceResult != null && okServiceResult.isSuccess()) {
            return okServiceResult.getData();
        }

        return null;
    }

    protected List<OKCardBean> getWatchCard(Map<String, String> params) {
        String json = OkHttpApiPost(Watch_URL, params);

        if (json == null) return null;

        Gson gson = new Gson();

        Type type = new TypeToken<OKServiceResult<List<OKCardBean>>>() {
        }.getType();

        OKServiceResult<List<OKCardBean>> okServiceResult = gson.fromJson(json, type);

        if (okServiceResult != null && okServiceResult.isSuccess()) {
            return okServiceResult.getData();
        }

        return null;
    }

    protected List<OKAttentionBean> getAttentionEntry(Map<String, String> params) {
        String json = OkHttpApiPost(Attention_URL, params);

        if (json == null) return null;

        Gson gson = new Gson();

        Type type = new TypeToken<OKServiceResult<List<OKCardBean>>>() {
        }.getType();

        OKServiceResult<List<OKAttentionBean>> okServiceResult = gson.fromJson(json, type);

        if (okServiceResult != null && okServiceResult.isSuccess()) {
            return okServiceResult.getData();
        }

        return null;
    }

    protected List<OKMeCommentCardBean> getMeCommentCard(Map<String, String> param) {
        String json = OkHttpApiPost(CardAndComment_Url, param);

        if (json == null) return null;

        Gson gson = new Gson();

        Type type = new TypeToken<OKServiceResult<List<OKCardBean>>>() {
        }.getType();

        OKServiceResult<List<OKMeCommentCardBean>> okServiceResult = gson.fromJson(json, type);

        if (okServiceResult != null && okServiceResult.isSuccess()) {
            return okServiceResult.getData();
        }

        return null;
    }

    protected List<OKGoodsBean> getGoodsEntry(Map<String, String> params) {
        String json = OkHttpApiPost(GoodsEntry_URL, params);

        if (json == null) return null;

        Gson gson = new Gson();

        Type type = new TypeToken<OKServiceResult<List<OKGoodsBean>>>() {
        }.getType();

        OKServiceResult<List<OKGoodsBean>> okServiceResult = gson.fromJson(json, type);

        if (okServiceResult != null && okServiceResult.isSuccess()) {
            return okServiceResult.getData();
        }

        return null;
    }

    protected List<OKCardBean> getHotCard(Map<String, String> params) {
        String json = OkHttpApiPost(HotCard_URL, params);

        if (json == null) return null;

        Gson gson = new Gson();

        Type type = new TypeToken<OKServiceResult<List<OKCardBean>>>() {
        }.getType();

        OKServiceResult<List<OKCardBean>> okServiceResult = gson.fromJson(json, type);

        if (okServiceResult != null && okServiceResult.isSuccess()) {
            return okServiceResult.getData();
        }

        return null;
    }

    protected List<OKSearchBean> getSearchEntry(Map<String, String> param) {
        String json = OkHttpApiPost(Search_Url, param);

        if (json == null) return null;

        Gson gson = new Gson();

        Type type = new TypeToken<OKServiceResult<List<OKSearchBean>>>() {
        }.getType();

        OKServiceResult<List<OKSearchBean>> serviceResult = gson.fromJson(json, type);

        if (serviceResult != null && serviceResult.isSuccess()) {
            serviceResult.getData();
        }

        return null;
    }

    // --------------------------管理接口方法--------------------------

    protected OKServiceResult<Object> managerGoods(Map<String, String> params) {
        String json = OkHttpApiPost(ManagerGoods_URL, params);

        if (json == null) return null;

        Gson gson = new Gson();

        Type type = new TypeToken<OKServiceResult<Object>>() {
        }.getType();

        return gson.fromJson(json, type);
    }

    protected OKServiceResult<Object> managerCard(Map<String, String> params) {
        String json = OkHttpApiPost(ManagerCard_URL, params);

        if (json == null) return null;

        Gson gson = new Gson();

        Type type = new TypeToken<OKServiceResult<Object>>() {
        }.getType();

        return gson.fromJson(json, type);
    }

    protected OKServiceResult<Object> managerUser(Map<String, String> params) {
        String json = OkHttpApiPost(ManagerUser_URL, params);

        if (json == null) return null;

        Gson gson = new Gson();

        Type type = new TypeToken<OKServiceResult<Object>>() {
        }.getType();

        return gson.fromJson(json, type);
    }

    protected OKServiceResult<Object> managerComment(Map<String, String> params) {
        String json = OkHttpApiPost(ManagerComment_URL, params);

        if (json == null) return null;

        Gson gson = new Gson();

        Type type = new TypeToken<OKServiceResult<Object>>() {
        }.getType();

        return gson.fromJson(json, type);
    }

    // --------------------------管理接口方法--------------------------

    protected OKServiceResult<Object> getCommentOrReply(Map<String, String> params) {
        String json = OkHttpApiPost(CommentCard_URL, params);

        if (json == null) return null;

        Gson gson = new Gson();

        Type type = new TypeToken<OKServiceResult<Object>>() {
        }.getType();

        return gson.fromJson(json, type);
    }

    protected OKServiceResult<Object> addCard(Map<String, File> fileMap, Map<String, String> params) {
        String json = OKHttpApiPostFromFile(AddCard_URL, fileMap, params);

        if (json == null) return null;

        Gson gson = new Gson();

        Type type = new TypeToken<OKServiceResult<Object>>() {
        }.getType();

        return gson.fromJson(json, type);
    }

    protected OKServiceResult<Object> addCommentOrReply(Map<String, String> params) {
        String json = OkHttpApiPost(AddComment_URL, params);

        if (json == null) return null;

        Gson gson = new Gson();

        Type type = new TypeToken<OKServiceResult<Object>>() {
        }.getType();

        return gson.fromJson(json, type);
    }

    protected OKServiceResult<Object> feedBack(Map<String, String> params) {
        String json = OkHttpApiPost(FeedBack_URL, params);

        if (json == null) return null;

        Gson gson = new Gson();

        Type type = new TypeToken<OKServiceResult<Object>>() {
        }.getType();

        return gson.fromJson(json, type);
    }

    protected OKServiceResult<Object> report(Map<String, String> params) {
        String json = OkHttpApiPost(Report_URL, params);

        if (json == null) return null;

        Gson gson = new Gson();

        Type type = new TypeToken<OKServiceResult<Object>>() {
        }.getType();

        return gson.fromJson(json, type);
    }

    protected OKAppInfoBean getAppInfo(Map<String, String> params) {
        String json = OkHttpApiPost(AppInfo_URL, params);

        if (json == null) return null;

        Gson gson = new Gson();

        Type type = new TypeToken<OKServiceResult<OKAppInfoBean>>() {
        }.getType();

        OKServiceResult<OKAppInfoBean> serviceResult = gson.fromJson(json, type);

        if (serviceResult == null || !serviceResult.isSuccess()) return null;

        return serviceResult.getData();
    }

    protected OKCarouselAdBean getCarouselAd(Map<String, String> params) {
        String json = OkHttpApiPost(CarouselAd_URL, params);

        if (json == null) return null;

        Gson gson = new Gson();

        Type type = new TypeToken<OKServiceResult<OKCarouselAdBean>>() {
        }.getType();

        OKServiceResult<OKCarouselAdBean> serviceResult = gson.fromJson(json, type);

        if (serviceResult == null || !serviceResult.isSuccess()) return null;

        return serviceResult.getData();
    }
}
