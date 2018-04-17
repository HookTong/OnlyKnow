package com.onlyknow.app.database.bean;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 卡片bean,字段名不可变更,需要转Json处理
 * <p>
 * Created by Reset on 2018/03/05.
 */

@DatabaseTable(tableName = "card_table")
public class OKCardBean implements Serializable {
    @DatabaseField(columnName = KEY_CARD_ID, id = true, unique = true)
    private int cardId;
    public final static String KEY_CARD_ID = "cardId";

    @DatabaseField(columnName = KEY_CARD_TYPE)
    private String cardType;
    public final static String KEY_CARD_TYPE = "cardType";

    public enum CardType {
        IMAGE_TEXT, IMAGE, TEXT
    }

    @DatabaseField(columnName = KEY_USER_NAME)
    private String userName;
    public final static String KEY_USER_NAME = "userName";

    @DatabaseField(columnName = KEY_TITLE_TEXT)
    private String titleText;
    public final static String KEY_TITLE_TEXT = "titleText";

    @DatabaseField(columnName = KEY_TITLE_IMAGE_URL)
    private String titleImageUrl;
    public final static String KEY_TITLE_IMAGE_URL = "titleImageUrl";

    @DatabaseField(columnName = KEY_CONTENT_IMAGE_URL)
    private String contentImageUrl; // json数据需要转换成List<CardImage>
    public final static String KEY_CONTENT_IMAGE_URL = "contentImageUrl";

    @DatabaseField(columnName = KEY_CONTENT_TITLE_TEXT)
    private String contentTitleText;
    public final static String KEY_CONTENT_TITLE_TEXT = "contentTitleText";

    @DatabaseField(columnName = KEY_CONTENT_TEXT)
    private String contentText;
    public final static String KEY_CONTENT_TEXT = "contentText";

    @DatabaseField(columnName = KEY_LABELLING)
    private String labelling;
    public final static String KEY_LABELLING = "labelling";

    @DatabaseField(columnName = KEY_MESSAGE_LING)
    private String messageLink;
    public final static String KEY_MESSAGE_LING = "messageLink";

    @DatabaseField(columnName = KEY_PRAISE_COUNT)
    private Integer praiseCount;
    public final static String KEY_PRAISE_COUNT = "praiseCount";

    @DatabaseField(columnName = KEY_WATCH_COUNT)
    private Integer watchCount;
    public final static String KEY_WATCH_COUNT = "watchCount";

    @DatabaseField(columnName = KEY_COMMENT_COUNT)
    private Integer commentCount;
    public final static String KEY_COMMENT_COUNT = "commentCount";

    @DatabaseField(columnName = KEY_BROWSING_COUNT)
    private Integer browsingCount;
    public final static String KEY_BROWSING_COUNT = "browsingCount";

    @DatabaseField(columnName = KEY_APPROVE_BY)
    private int approveBy;
    public final static String KEY_APPROVE_BY = "approveBy";

    @DatabaseField(columnName = KEY_APPROVE_INFO)
    private String approveInfo;
    public final static String KEY_APPROVE_INFO = "approveInfo";

    @DatabaseField(columnName = KEY_CREATE_DATE, dataType = DataType.DATE)
    private Date createDate;
    public final static String KEY_CREATE_DATE = "createDate";

    @DatabaseField(columnName = KEY_READ_TIME)
    private long readTime = 0;
    public final static String KEY_READ_TIME = "readTime";

    @DatabaseField(columnName = KEY_IS_READ, dataType = DataType.BOOLEAN)
    private boolean isRead;
    public final static String KEY_IS_READ = "isRead";

    // 客户端自行转换contentImageUrl后的字段
    private List<CardImage> imageList;

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    public String getTitleImageUrl() {
        return titleImageUrl;
    }

    public void setTitleImageUrl(String titleImageUrl) {
        this.titleImageUrl = titleImageUrl;
    }

    public String getContentTitleText() {
        return contentTitleText;
    }

    public void setContentTitleText(String contentTitleText) {
        this.contentTitleText = contentTitleText;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public String getContentImageUrl() {
        return contentImageUrl;
    }

    public void setContentImageUrl(String contentImageUrl) {
        this.contentImageUrl = contentImageUrl;
    }

    public String getLabelling() {
        return labelling;
    }

    public void setLabelling(String labelling) {
        this.labelling = labelling;
    }

    public String getMessageLink() {
        return messageLink;
    }

    public void setMessageLink(String messageLink) {
        this.messageLink = messageLink;
    }

    public Integer getPraiseCount() {
        return praiseCount;
    }

    public void setPraiseCount(Integer praiseCount) {
        this.praiseCount = praiseCount;
    }

    public Integer getWatchCount() {
        return watchCount;
    }

    public void setWatchCount(Integer watchCount) {
        this.watchCount = watchCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Integer getBrowsingCount() {
        return browsingCount;
    }

    public void setBrowsingCount(Integer browsingCount) {
        this.browsingCount = browsingCount;
    }

    public int getApproveBy() {
        return approveBy;
    }

    public void setApproveBy(int approveBy) {
        this.approveBy = approveBy;
    }

    public String getApproveInfo() {
        return approveInfo;
    }

    public void setApproveInfo(String approveInfo) {
        this.approveInfo = approveInfo;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public long getReadTime() {
        return readTime;
    }

    public void setReadTime(long readTime) {
        this.readTime = readTime;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public void setImageList(List<CardImage> imageList) {
        this.imageList = imageList;
    }

    // 可自动反序列化
    public List<CardImage> getImageList() {
        if (imageList != null) return imageList;

        if (TextUtils.isEmpty(contentImageUrl)) return null;

        try {
            JsonParser parser = new JsonParser();
            JsonArray jsonArray = parser.parse(contentImageUrl).getAsJsonArray();

            Gson gson = new Gson();
            imageList = new ArrayList<>();

            for (JsonElement cardJson : jsonArray) {
                try {
                    CardImage cardImage = gson.fromJson(cardJson, CardImage.class);
                    imageList.add(cardImage);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return imageList;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // 工具方法
    public String getFirstCardImage() {

        if (imageList != null && imageList.size() != 0) return imageList.get(0).getUrl();

        List<CardImage> list = getImageList();

        if (list == null || list.size() == 0) {
            return "";
        }

        return imageList.get(0).getUrl();

    }

    public List<String> cardImagesToUrls() {

        getImageList();

        List<String> list = new ArrayList<>();

        if (imageList == null || imageList.size() == 0) return list;

        for (CardImage cardImage : imageList) {
            list.add(cardImage.getUrl());
        }

        return list;
    }

    public static class CardImage implements Serializable {
        private String url = "";
        private long size;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }
    }
}
