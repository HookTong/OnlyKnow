package com.onlyknow.app.api;

import com.onlyknow.app.db.bean.OKAppInfoBean;
import com.onlyknow.app.db.bean.OKAttentionBean;
import com.onlyknow.app.db.bean.OKCardBean;
import com.onlyknow.app.db.bean.OKCarouselAdBean;
import com.onlyknow.app.db.bean.OKGoodsBean;
import com.onlyknow.app.db.bean.OKMeCommentCardBean;
import com.onlyknow.app.db.bean.OKSearchBean;
import com.onlyknow.app.utils.OKGsonUtil;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.onlyknow.app.net.OKWebService.OKHttpApiPostFromFile;
import static com.onlyknow.app.net.OKWebService.OkHttpApiPost;

public class OKBaseApi {
    public final ExecutorService exec = Executors.newFixedThreadPool(100);

    private final String IP = "101.132.168.25:8090/onlyknow";

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

        OKServiceResult<List<OKCardBean>> okServiceResult = OKGsonUtil.fromServiceResultJsonByList(json, OKCardBean.class);

        if (okServiceResult != null && okServiceResult.isSuccess()) {
            return okServiceResult.getData();
        }

        return null;
    }

    protected List<OKCardBean> getNearCard(Map<String, String> params) {
        String json = OkHttpApiPost(NearCard_URL, params);

        if (json == null) return null;

        OKServiceResult<List<OKCardBean>> okServiceResult = OKGsonUtil.fromServiceResultJsonByList(json, OKCardBean.class);

        if (okServiceResult != null && okServiceResult.isSuccess()) {
            return okServiceResult.getData();
        }

        return null;
    }

    protected List<OKCardBean> getUserCard(Map<String, String> params) {
        String json = OkHttpApiPost(UserCard_URL, params);

        if (json == null) return null;

        OKServiceResult<List<OKCardBean>> okServiceResult = OKGsonUtil.fromServiceResultJsonByList(json, OKCardBean.class);

        if (okServiceResult != null && okServiceResult.isSuccess()) {
            return okServiceResult.getData();
        }

        return null;
    }

    protected List<OKCardBean> getWatchCard(Map<String, String> params) {
        String json = OkHttpApiPost(Watch_URL, params);

        if (json == null) return null;

        OKServiceResult<List<OKCardBean>> okServiceResult = OKGsonUtil.fromServiceResultJsonByList(json, OKCardBean.class);

        if (okServiceResult != null && okServiceResult.isSuccess()) {
            return okServiceResult.getData();
        }

        return null;
    }

    protected List<OKAttentionBean> getAttentionEntry(Map<String, String> params) {
        String json = OkHttpApiPost(Attention_URL, params);

        if (json == null) return null;

        OKServiceResult<List<OKAttentionBean>> okServiceResult = OKGsonUtil.fromServiceResultJsonByList(json, OKAttentionBean.class);

        if (okServiceResult != null && okServiceResult.isSuccess()) {
            return okServiceResult.getData();
        }

        return null;
    }

    protected List<OKMeCommentCardBean> getMeCommentCard(Map<String, String> param) {
        String json = OkHttpApiPost(CardAndComment_Url, param);

        if (json == null) return null;

        OKServiceResult<List<OKMeCommentCardBean>> okServiceResult = OKGsonUtil.fromServiceResultJsonByList(json, OKMeCommentCardBean.class);

        if (okServiceResult != null && okServiceResult.isSuccess()) {
            return okServiceResult.getData();
        }

        return null;
    }

    protected List<OKGoodsBean> getGoodsEntry(Map<String, String> params) {
        String json = OkHttpApiPost(GoodsEntry_URL, params);

        if (json == null) return null;

        OKServiceResult<List<OKGoodsBean>> okServiceResult = OKGsonUtil.fromServiceResultJsonByList(json, OKGoodsBean.class);

        if (okServiceResult != null && okServiceResult.isSuccess()) {
            return okServiceResult.getData();
        }

        return null;
    }

    protected List<OKCardBean> getHotCard(Map<String, String> params) {
        String json = OkHttpApiPost(HotCard_URL, params);

        if (json == null) return null;

        OKServiceResult<List<OKCardBean>> okServiceResult = OKGsonUtil.fromServiceResultJsonByList(json, OKCardBean.class);

        if (okServiceResult != null && okServiceResult.isSuccess()) {
            return okServiceResult.getData();
        }

        return null;
    }

    protected List<OKSearchBean> getSearchEntry(Map<String, String> param) {
        String json = OkHttpApiPost(Search_Url, param);

        if (json == null) return null;

        OKServiceResult<List<OKSearchBean>> okServiceResult = OKGsonUtil.fromServiceResultJsonByList(json, OKSearchBean.class);

        if (okServiceResult != null && okServiceResult.isSuccess()) {
            return okServiceResult.getData();
        }

        return null;
    }

    // --------------------------管理接口方法--------------------------

    protected <T> OKServiceResult<T> managerGoods(Map<String, String> params, Class<T> clazz) {
        String json = OkHttpApiPost(ManagerGoods_URL, params);

        if (json == null) return null;

        return OKGsonUtil.fromServiceResultJsonByObject(json, clazz);
    }

    protected <T> OKServiceResult<T> managerCard(Map<String, String> params, Class<T> clazz) {
        String json = OkHttpApiPost(ManagerCard_URL, params);

        if (json == null) return null;

        return OKGsonUtil.fromServiceResultJsonByObject(json, clazz);
    }

    protected <T> OKServiceResult<T> managerUser(Map<String, String> params, Class<T> clazz) {
        String json = OkHttpApiPost(ManagerUser_URL, params);

        if (json == null) return null;

        return OKGsonUtil.fromServiceResultJsonByObject(json, clazz);
    }

    protected <T> OKServiceResult<T> managerComment(Map<String, String> params, Class<T> clazz) {
        String json = OkHttpApiPost(ManagerComment_URL, params);

        if (json == null) return null;

        return OKGsonUtil.fromServiceResultJsonByObject(json, clazz);
    }

    // --------------------------管理接口方法--------------------------

    protected <T> OKServiceResult<List<T>> getCommentOrReply(Map<String, String> params, Class<T> clazz) {
        String json = OkHttpApiPost(CommentCard_URL, params);

        if (json == null) return null;

        return OKGsonUtil.fromServiceResultJsonByList(json, clazz);
    }

    protected <T> OKServiceResult<T> addCard(Map<String, File> fileMap, Map<String, String> params, Class<T> clazz) {
        String json = OKHttpApiPostFromFile(AddCard_URL, fileMap, params);

        if (json == null) return null;

        return OKGsonUtil.fromServiceResultJsonByObject(json, clazz);
    }

    protected <T> OKServiceResult<T> addCommentOrReply(Map<String, String> params, Class<T> clazz) {
        String json = OkHttpApiPost(AddComment_URL, params);

        if (json == null) return null;

        return OKGsonUtil.fromServiceResultJsonByObject(json, clazz);
    }

    protected <T> OKServiceResult<T> feedBack(Map<String, String> params, Class<T> clazz) {
        String json = OkHttpApiPost(FeedBack_URL, params);

        if (json == null) return null;

        return OKGsonUtil.fromServiceResultJsonByObject(json, clazz);
    }

    protected <T> OKServiceResult<T> report(Map<String, String> params, Class<T> clazz) {
        String json = OkHttpApiPost(Report_URL, params);

        if (json == null) return null;

        return OKGsonUtil.fromServiceResultJsonByObject(json, clazz);
    }

    protected OKAppInfoBean getAppInfo(Map<String, String> params) {
        String json = OkHttpApiPost(AppInfo_URL, params);

        if (json == null) return null;

        OKServiceResult<OKAppInfoBean> serviceResult = OKGsonUtil.fromServiceResultJsonByObject(json, OKAppInfoBean.class);

        if (serviceResult == null || !serviceResult.isSuccess()) return null;

        return serviceResult.getData();
    }

    protected OKCarouselAdBean getCarouselAd(Map<String, String> params) {
        String json = OkHttpApiPost(CarouselAd_URL, params);

        if (json == null) return null;

        OKServiceResult<OKCarouselAdBean> serviceResult = OKGsonUtil.fromServiceResultJsonByObject(json, OKCarouselAdBean.class);

        if (serviceResult == null || !serviceResult.isSuccess()) return null;

        return serviceResult.getData();
    }
}
