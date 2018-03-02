package com.onlyknow.app.api;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.onlyknow.app.database.bean.OKAttentionBean;
import com.onlyknow.app.database.bean.OKCardAndCommentBean;
import com.onlyknow.app.database.bean.OKCardBean;
import com.onlyknow.app.database.bean.OKCardBindBean;
import com.onlyknow.app.database.bean.OKCarouselAndAdImageBean;
import com.onlyknow.app.database.bean.OKCommentBean;
import com.onlyknow.app.database.bean.OKCommentReplyBean;
import com.onlyknow.app.database.bean.OKGoodsBean;
import com.onlyknow.app.database.bean.OKSafetyInfoBean;
import com.onlyknow.app.database.bean.OKSearchBean;
import com.onlyknow.app.database.bean.OKSignupResultBean;
import com.onlyknow.app.database.bean.OKUserInfoBean;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.onlyknow.app.net.OKWebService.OKHttpApiGet;
import static com.onlyknow.app.net.OKWebService.OKHttpApiPostFromFile;
import static com.onlyknow.app.net.OKWebService.OkHttpApiPost;

/**
 * 所有接口:Ver1.2
 * <p>
 * Created by Administrator on 2017/12/10.
 */

public class OKBusinessApi extends OKBaseApi {
    private final String IP = "101.132.168.25:8090";
    private final String ExploreCard_URL = "http://" + IP + "/WeiZhiService/ExploreCardInquiry";
    private final String NearCard_URL = "http://" + IP + "/WeiZhiService/NearCardInquiry";
    private final String UserCard_URL = "http://" + IP + "/WeiZhiService/UserCardInquiry";
    private final String GoodsBuy_URL = "http://" + IP + "/WeiZhiService/GoodsBuy";
    private final String RemoveCard_URL = "http://" + IP + "/WeiZhiService/RemoveCard";
    private final String UserInfo_URL = "http://" + IP + "/WeiZhiService/GetUserInfo";
    private final String LoadMore_URL = "http://" + IP + "/WeiZhiService/LoadMoreEntry";
    private final String Attention_URL = "http://" + IP + "/WeiZhiService/AttentionEntryInquiry";
    private final String Watch_URL = "http://" + IP + "/WeiZhiService/WatchCardInquiry";
    private final String UpdateCardInfo_URL = "http://" + IP + "/WeiZhiService/UpdateCardInfo";
    private final String HotCard_URL = "http://" + IP + "/WeiZhiService/HotCardInquiry";
    private final String GoodsEntry_URL = "http://" + IP + "/WeiZhiService/GoodsEntryInquiry";
    private final String CommentEdit_URL = "http://" + IP + "/WeiZhiService/CommentPraiseEdit";
    private final String CommentCard_URL = "http://" + IP + "/WeiZhiService/CommentCardInquiry";
    private final String CommentReplyCard_URL = "http://" + IP + "/WeiZhiService/CommentReplyCardInquiry";
    private final String AddCardBrowsing_URL = "http://" + IP + "/WeiZhiService/CardBrowsing";
    private final String CardBind_URL = "http://" + IP + "/WeiZhiService/CardCheck";
    private final String UserInfoEdit_URL = "http://" + IP + "/WeiZhiService/UserEdit";
    private final String UpdateHeadPortrait_URL = "http://" + IP + "/WeiZhiService/UpdateUserImage";
    private final String Register_URL = "http://" + IP + "/WeiZhiService/RegisterUser";
    private final String Login_URL = "http://" + IP + "/WeiZhiService/LogLet";
    private final String AddCard_URL = "http://" + IP + "/WeiZhiService/AddUserCard";
    private final String FeedBack_URL = "http://" + IP + "/WeiZhiService/FeedBack";
    private final String SecurityCheck_URL = "http://" + IP + "/WeiZhiService/SecurityCheck";
    private final String UserLocation_URL = "http://" + IP + "/WeiZhiService/UserLocation";
    private final String CarouselAndAdImage_URL = "http://" + IP + "/WeiZhiService/OKCarouselAndAdImageInquiry";
    private final String CardAndComment_Url = "http://" + IP + "/WeiZhiService/OKCardAndCommentInquiry";
    private final String Search_Url = "http://" + IP + "/WeiZhiService/OKSearchInquiry";
    private final String Approve_Url = "http://" + IP + "/WeiZhiService/OKApproveCardInquire";

    public ArrayList<OKCardBean> getExploreCard(Map<String, String> params) {
        String json = OkHttpApiPost(ExploreCard_URL, params);
        if (json == null || json.equals("Explore_ForFailure")) {
            return null;
        }
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(json).getAsJsonArray();

        Gson gson = new Gson();
        ArrayList<OKCardBean> mCardBeanList = new ArrayList<OKCardBean>();

        for (JsonElement cardJson : jsonArray) {
            try {
                OKCardBean mCardBean = gson.fromJson(cardJson, OKCardBean.class);
                mCardBeanList.add(mCardBean);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return mCardBeanList;
    }

    public ArrayList<OKCardBean> getNearCard(Map<String, String> params) {
        String json = OkHttpApiPost(NearCard_URL, params);
        if (json == null || json.equals("Near_ForFailure")) {
            return null;
        }
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(json).getAsJsonArray();

        Gson gson = new Gson();
        ArrayList<OKCardBean> mCardBeanList = new ArrayList<OKCardBean>();

        for (JsonElement cardJson : jsonArray) {
            try {
                OKCardBean mCardBean = gson.fromJson(cardJson, OKCardBean.class);
                mCardBeanList.add(mCardBean);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return mCardBeanList;
    }

    public ArrayList<OKCardBean> getUserCard(Map<String, String> params) {
        String json = OkHttpApiPost(UserCard_URL, params);
        if (json == null || json.equals("UserCard_ForFailure")) {
            return null;
        }
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(json).getAsJsonArray();

        Gson gson = new Gson();
        ArrayList<OKCardBean> mCardBeanList = new ArrayList<OKCardBean>();

        for (JsonElement cardJson : jsonArray) {
            try {
                OKCardBean mCardBean = gson.fromJson(cardJson, OKCardBean.class);
                mCardBeanList.add(mCardBean);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return mCardBeanList;
    }

    public ArrayList<OKCardBean> getApproveCard(Map<String, String> params) {
        String json = OkHttpApiPost(Approve_Url, params);
        if (json == null || json.equals("OKApproveCardInquire_ForFailure")) {
            return null;
        }
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(json).getAsJsonArray();

        Gson gson = new Gson();
        ArrayList<OKCardBean> mCardBeanList = new ArrayList<OKCardBean>();

        for (JsonElement cardJson : jsonArray) {
            try {
                OKCardBean mCardBean = gson.fromJson(cardJson, OKCardBean.class);
                mCardBeanList.add(mCardBean);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return mCardBeanList;
    }

    public ArrayList<OKCardBean> getWatchCard(Map<String, String> params) {
        String json = OkHttpApiPost(Watch_URL, params);
        if (json == null || json.equals("Watch_ForFailure")) {
            return null;
        }
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(json).getAsJsonArray();

        Gson gson = new Gson();
        ArrayList<OKCardBean> mCardBeanList = new ArrayList<OKCardBean>();

        for (JsonElement cardJson : jsonArray) {
            try {
                OKCardBean mCardBean = gson.fromJson(cardJson, OKCardBean.class);
                mCardBeanList.add(mCardBean);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return mCardBeanList;
    }

    // 包括动态卡片,收藏卡片和个人主页卡片,所有数据类型为CardBean的ListView界面的加载更多操作统一请求该接口
    public ArrayList<OKCardBean> loadMoreUserCard(Map<String, String> params) {
        String json = OkHttpApiPost(LoadMore_URL, params);
        if (json == null || json.equals("LoadMoreEntry_ForFailure")) {
            return null;
        }
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(json).getAsJsonArray();

        Gson gson = new Gson();
        ArrayList<OKCardBean> mCardBeanList = new ArrayList<OKCardBean>();

        for (JsonElement cardJson : jsonArray) {
            try {
                OKCardBean mCardBean = gson.fromJson(cardJson, OKCardBean.class);
                mCardBeanList.add(mCardBean);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return mCardBeanList;
    }

    public ArrayList<OKAttentionBean> getAttentionEntry(Map<String, String> params) {
        String json = OkHttpApiPost(Attention_URL, params);
        if (json == null || json.equals("Attention_ForFailure")) {
            return null;
        }
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(json).getAsJsonArray();

        Gson gson = new Gson();
        ArrayList<OKAttentionBean> mGuanzhuBeanList = new ArrayList<OKAttentionBean>();

        for (JsonElement cardJson : jsonArray) {
            try {
                OKAttentionBean mGuanzhuBean = gson.fromJson(cardJson, OKAttentionBean.class);
                mGuanzhuBeanList.add(mGuanzhuBean);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return mGuanzhuBeanList;
    }

    public ArrayList<OKAttentionBean> loadMoreAttentionEntry(Map<String, String> params) {
        String json = OkHttpApiPost(LoadMore_URL, params);
        if (json == null || json.equals("LoadMoreEntry_ForFailure")) {
            return null;
        }
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(json).getAsJsonArray();

        Gson gson = new Gson();
        ArrayList<OKAttentionBean> mGuanzhuBeanList = new ArrayList<OKAttentionBean>();

        for (JsonElement cardJson : jsonArray) {
            try {
                OKAttentionBean mGuanzhuBean = gson.fromJson(cardJson, OKAttentionBean.class);
                mGuanzhuBeanList.add(mGuanzhuBean);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return mGuanzhuBeanList;
    }

    public ArrayList<OKGoodsBean> getGoodsEntry(Map<String, String> params) {
        String json = OkHttpApiPost(GoodsEntry_URL, params);
        if (json == null || json.equals("GOODS_ForFailure")) {
            return null;
        }
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(json).getAsJsonArray();

        Gson gson = new Gson();
        ArrayList<OKGoodsBean> mGoodsBeanList = new ArrayList<OKGoodsBean>();

        for (JsonElement cardJson : jsonArray) {
            try {
                OKGoodsBean mGoodsBean = gson.fromJson(cardJson, OKGoodsBean.class);
                mGoodsBeanList.add(mGoodsBean);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return mGoodsBeanList;
    }

    public ArrayList<OKGoodsBean> loadMoreGoodsEntry(Map<String, String> params) {
        String json = OkHttpApiPost(LoadMore_URL, params);
        if (json == null || json.equals("LoadMoreEntry_ForFailure")) {
            return null;
        }
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(json).getAsJsonArray();

        Gson gson = new Gson();
        ArrayList<OKGoodsBean> mGoodsBeanList = new ArrayList<OKGoodsBean>();

        for (JsonElement cardJson : jsonArray) {
            try {
                OKGoodsBean mGoodsBean = gson.fromJson(cardJson, OKGoodsBean.class);
                mGoodsBeanList.add(mGoodsBean);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return mGoodsBeanList;
    }

    public ArrayList<OKCardBean> getHotCard(Map<String, String> params) {
        String json = OkHttpApiPost(HotCard_URL, params);
        if (json == null || json.equals("HotCardInquiry_ForFailure")) {
            return null;
        }
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(json).getAsJsonArray();

        Gson gson = new Gson();
        ArrayList<OKCardBean> mCardBeanList = new ArrayList<OKCardBean>();

        for (JsonElement cardJson : jsonArray) {
            try {
                OKCardBean mCardBean = gson.fromJson(cardJson, OKCardBean.class);
                mCardBeanList.add(mCardBean);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return mCardBeanList;
    }

    public ArrayList<OKCommentBean> getCommentCard(Map<String, String> params) {
        String json = OkHttpApiPost(CommentCard_URL, params);
        if (json == null || json.equals("Comment_ForFailure")) {
            return null;
        }
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(json).getAsJsonArray();

        Gson gson = new Gson();
        ArrayList<OKCommentBean> mCommentBeanList = new ArrayList<OKCommentBean>();

        for (JsonElement cardJson : jsonArray) {
            try {
                OKCommentBean mCommentBean = gson.fromJson(cardJson, OKCommentBean.class);
                mCommentBeanList.add(mCommentBean);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return mCommentBeanList;
    }

    public ArrayList<OKCommentBean> loadMoreCommentCard(Map<String, String> params) {
        String json = OkHttpApiPost(LoadMore_URL, params);
        if (json == null || json.equals("LoadMoreEntry_ForFailure")) {
            return null;
        }
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(json).getAsJsonArray();

        Gson gson = new Gson();
        ArrayList<OKCommentBean> mCommentBeanList = new ArrayList<OKCommentBean>();

        for (JsonElement cardJson : jsonArray) {
            try {
                OKCommentBean mCommentBean = gson.fromJson(cardJson, OKCommentBean.class);
                mCommentBeanList.add(mCommentBean);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return mCommentBeanList;
    }

    public ArrayList<OKCommentReplyBean> getCommentReplyCard(Map<String, String> params) {
        String json = OkHttpApiPost(CommentReplyCard_URL, params);
        if (json == null || json.equals("CommentReply_ForFailure")) {
            return null;
        }
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(json).getAsJsonArray();

        Gson gson = new Gson();
        ArrayList<OKCommentReplyBean> mCommentReplyBeanList = new ArrayList<OKCommentReplyBean>();

        for (JsonElement cardJson : jsonArray) {
            try {
                OKCommentReplyBean mCommentReplyBean = gson.fromJson(cardJson, OKCommentReplyBean.class);
                mCommentReplyBeanList.add(mCommentReplyBean);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return mCommentReplyBeanList;
    }

    public ArrayList<OKCommentReplyBean> loadMoreCommentReplyCard(Map<String, String> params) {
        String json = OkHttpApiPost(LoadMore_URL, params);
        if (json == null || json.equals("LoadMoreEntry_ForFailure")) {
            return null;
        }
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(json).getAsJsonArray();

        Gson gson = new Gson();
        ArrayList<OKCommentReplyBean> mCommentReplyBeanList = new ArrayList<OKCommentReplyBean>();

        for (JsonElement cardJson : jsonArray) {
            try {
                OKCommentReplyBean mCommentReplyBean = gson.fromJson(cardJson, OKCommentReplyBean.class);
                mCommentReplyBeanList.add(mCommentReplyBean);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return mCommentReplyBeanList;
    }

    public OKUserInfoBean getUserInfo(Map<String, String> params) {
        String json = OkHttpApiPost(UserInfo_URL, params);
        if (json == null || json.equals("UserInfo_ForFailure")) {
            return null;
        }
        Gson gson = new Gson();
        try {
            OKUserInfoBean mUserInfoBean = gson.fromJson(json, OKUserInfoBean.class);
            return mUserInfoBean;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public OKCardBindBean getCardBind(Map<String, String> params) {
        String json = OkHttpApiPost(CardBind_URL, params);
        if (json == null || json.equals("CardCheck_ForFailure")) {
            return null;
        }
        return new Gson().fromJson(json, OKCardBindBean.class);
    }

    public boolean GoodsBuy(Map<String, String> params) {
        String json = OkHttpApiPost(GoodsBuy_URL, params);
        if (json == null) {
            return false;
        } else if (!json.equals("GoodsBuy_Failure")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean RemoveCard(Map<String, String> params) {
        String json = OkHttpApiPost(RemoveCard_URL, params);
        if (json == null) {
            return false;
        } else if (!json.equals("RemoveCard_Failure")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean updateCardInfo(Map<String, String> params) {
        String json = OkHttpApiPost(UpdateCardInfo_URL, params);
        if (json == null) {
            return false;
        } else if (!json.equals("UpdateCardInfo_ForFailure")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean editCommentPraise(Map<String, String> params) {
        String json = OkHttpApiPost(CommentEdit_URL, params);
        if (json == null) {
            return false;
        } else if (json.equals("CommentPraiseEdit_EditSuccess")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean updateUserInfo(Map<String, String> params) {
        String json = OkHttpApiPost(UserInfoEdit_URL, params);
        if (json == null) {
            return false;
        } else if (json.equals("Edit_Success")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean updateHeadPortrait(Map<String, String> params) {
        String json = OkHttpApiPost(UpdateHeadPortrait_URL, params);
        if (json == null) {
            return false;
        } else if (!json.equals("UpdateUserImage_Failure")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean addUserCard(Map<String, File> fileMap, Map<String, String> params) {
        String json = OKHttpApiPostFromFile(AddCard_URL, fileMap, params);
        if (json == null) {
            return false;
        } else if (!json.equals("ADD_CardFailed")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean feedBack(Map<String, String> params) {
        String json = OkHttpApiPost(FeedBack_URL, params);
        if (json == null) {
            return false;
        } else if (!json.equals("FeedBack_Failed")) {
            return true;
        } else {
            return false;
        }
    }

    public OKUserInfoBean login(Map<String, String> params) {
        String json = OkHttpApiPost(Login_URL, params);
        if (json == null) {
            return null;
        } else if (json.equals("LG_Success")) {

            Map<String, String> map = new HashMap<>();
            map.put("username", params.get("username"));
            map.put("type", "ALL");
            OKUserInfoBean mUserInfoBean = getUserInfo(map);
            if (mUserInfoBean == null) {
                return null;
            }
            return mUserInfoBean;
        } else {
            return null;
        }
    }

    public OKSignupResultBean registerUser(Map<String, String> params) {
        String json = OkHttpApiPost(Register_URL, params);
        if (json == null) {
            return null;
        }

        return new Gson().fromJson(json, OKSignupResultBean.class);
    }

    public OKSafetyInfoBean securityCheck(Map<String, String> params) {
        String json = OkHttpApiPost(SecurityCheck_URL, params);
        if (json == null) {
            return null;
        } else if (!json.equals("SecurityCheck_Failure")) {
            return new Gson().fromJson(json, OKSafetyInfoBean.class);
        } else {
            return null;
        }
    }

    public OKCarouselAndAdImageBean getOKCarouselAndAdImageBean(String equipment) {
        String url = CarouselAndAdImage_URL + "?equipment=" + equipment;

        String json = OKHttpApiGet(url);
        if (json == null) {
            return null;
        } else if (!json.equals("CarouselAndAdImageInquiry_ForFailure")) {
            return new Gson().fromJson(json, OKCarouselAndAdImageBean.class);
        } else {
            return null;
        }
    }

    public boolean addCardBrowsing(Map<String, String> params) {
        String json = OkHttpApiPost(AddCardBrowsing_URL, params);

        if (!TextUtils.isEmpty(json)) {
            if (json.equals("CardBrowsing_AddFailure")) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public void addUserLocation(Map<String, String> params) {
        OkHttpApiPost(UserLocation_URL, params);
    }

    public List<OKCardAndCommentBean> getCardAndComment(Map<String, String> param) {
        String json = OkHttpApiPost(CardAndComment_Url, param);
        if (json == null || json.equals("OKCardAndCommentInquiry_ForFailure")) {
            return null;
        }
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(json).getAsJsonArray();

        Gson gson = new Gson();
        List<OKCardAndCommentBean> list = new ArrayList<>();

        for (JsonElement cardJson : jsonArray) {
            try {
                OKCardAndCommentBean bean = gson.fromJson(cardJson, OKCardAndCommentBean.class);
                list.add(bean);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return list;
    }

    public List<OKCardAndCommentBean> loadMoreCardAndComment(Map<String, String> param) {
        String json = OkHttpApiPost(LoadMore_URL, param);
        if (json == null || json.equals("LoadMoreEntry_ForFailure")) {
            return null;
        }
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(json).getAsJsonArray();

        Gson gson = new Gson();
        List<OKCardAndCommentBean> list = new ArrayList<>();

        for (JsonElement cardJson : jsonArray) {
            try {
                OKCardAndCommentBean bean = gson.fromJson(cardJson, OKCardAndCommentBean.class);
                list.add(bean);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return list;
    }

    public List<OKSearchBean> getSearchBean(Map<String, String> param) {
        String json = OkHttpApiPost(Search_Url, param);
        if (json == null || json.equals("OKSearchInquiry_ForFailure")) {
            return null;
        }
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(json).getAsJsonArray();

        Gson gson = new Gson();
        List<OKSearchBean> list = new ArrayList<>();

        for (JsonElement cardJson : jsonArray) {
            try {
                OKSearchBean bean = gson.fromJson(cardJson, OKSearchBean.class);
                list.add(bean);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return list;
    }
}
