package com.onlyknow.app.database.bean;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 卡片bean,字段名不可变更,需要转Json处理
 * <p>
 * Created by Reset on 2018/03/05.
 */

@DatabaseTable(tableName = "card_table")
public class OKCardBean {
    @DatabaseField(columnName = KEY_CARD_ID, id = true, unique = true)
    private int CARD_ID = -1;
    public final static String KEY_CARD_ID = "CARD_ID";

    @DatabaseField(columnName = KEY_USER_NAME)
    private String USER_NAME = ""; // 用户名
    public final static String KEY_USER_NAME = "USER_NAME";

    @DatabaseField(columnName = KEY_TITLE_TEXT)
    private String TITLE_TEXT = "";// 卡片标题,在探索,附近,动态,收藏,热门及主页中为用户昵称
    public final static String KEY_TITLE_TEXT = "TITLE_TEXT";

    @DatabaseField(columnName = KEY_TITLE_IMAGE_URL)
    private String TITLE_IMAGE_URL = "";// 卡片标题图片,在探索,附近,动态,收藏,热门及主页中为用户头像
    public final static String KEY_TITLE_IMAGE_URL = "TITLE_IMAGE_URL";

    @DatabaseField(columnName = KEY_CARD_TYPE)
    private String CARD_TYPE = "";
    public final static String KEY_CARD_TYPE = "CARD_TYPE";

    public enum CardType {
        IMAGE_TEXT, IMAGE, TEXT;
    }

    @DatabaseField(columnName = KEY_CONTENT_IMAGE_URL)
    private String CONTENT_IMAGE_URL = ""; // 文章图片地址
    public final static String KEY_CONTENT_IMAGE_URL = "CONTENT_IMAGE_URL";

    @DatabaseField(columnName = KEY_CONTENT_TITLE_TEXT)
    private String CONTENT_TITLE_TEXT = ""; // 文章标题
    public final static String KEY_CONTENT_TITLE_TEXT = "CONTENT_TITLE_TEXT";

    @DatabaseField(columnName = KEY_CONTENT_TEXT)
    private String CONTENT_TEXT = ""; // 文章内容
    public final static String KEY_CONTENT_TEXT = "CONTENT_TEXT";

    @DatabaseField(columnName = KEY_CREATE_DATE)
    private String CREATE_DATE = "";
    public final static String KEY_CREATE_DATE = "CREATE_DATE";

    @DatabaseField(columnName = KEY_LABELLING)
    private String LABELLING = ""; // 卡片标签
    public final static String KEY_LABELLING = "LABELLING";

    @DatabaseField(columnName = KEY_ZAN_NUM)
    private int ZAN_NUM = 0;
    public final static String KEY_ZAN_NUM = "ZAN_NUM";

    @DatabaseField(columnName = KEY_SHOUCHAN_NUM)
    private int SHOUCHAN_NUM = 0;
    public final static String KEY_SHOUCHAN_NUM = "SHOUCHAN_NUM";

    @DatabaseField(columnName = KEY_PINGLUN_NUM)
    private int PINGLUN_NUM = 0;
    public final static String KEY_PINGLUN_NUM = "PINGLUN_NUM";

    @DatabaseField(columnName = KEY_MESSAGE_LINK)
    private String MESSAGE_LINK = "";
    public final static String KEY_MESSAGE_LINK = "MESSAGE_LINK";

    @DatabaseField(columnName = KEY_IS_READ, dataType = DataType.BOOLEAN)
    private boolean IS_READ;
    public final static String KEY_IS_READ = "IS_READ";

    private String READ_DATE = "";
    public final static String KEY_READ_DATE = "READ_DATE";

    @DatabaseField(columnName = KEY_READ_DATE_LONG)
    private long READ_DATE_LONG = 0;
    public final static String KEY_READ_DATE_LONG = "READ_DATE_LONG";

    private int APPROVE_BY = 1; // 审批状态 -1:未通过 0:审批中 1:通过
    public final static String KEY_APPROVE_BY = "APPROVE_BY";

    private String APPROVE_INFO = "";
    public final static String KEY_APPROVE_INFO = "APPROVE_INFO";

    private OKCardUrlListBean bean;

    public int getCARD_ID() {
        return CARD_ID;
    }

    public void setCARD_ID(int CARD_ID) {
        this.CARD_ID = CARD_ID;
    }

    public String getUSER_NAME() {
        return USER_NAME;
    }

    public void setUSER_NAME(String USER_NAME) {
        this.USER_NAME = USER_NAME;
    }

    public String getTITLE_TEXT() {
        return TITLE_TEXT;
    }

    public void setTITLE_TEXT(String TITLE_TEXT) {
        this.TITLE_TEXT = TITLE_TEXT;
    }

    public String getTITLE_IMAGE_URL() {
        return TITLE_IMAGE_URL;
    }

    public void setTITLE_IMAGE_URL(String TITLE_IMAG_URL) {
        this.TITLE_IMAGE_URL = TITLE_IMAG_URL;
    }

    public String getCARD_TYPE() {
        return CARD_TYPE;
    }

    public void setCARD_TYPE(String CARD_TYPE) {
        this.CARD_TYPE = CARD_TYPE;
    }

    public String getCONTENT_IMAGE_URL() {
        return CONTENT_IMAGE_URL;
    }

    public void setCONTENT_IMAGE_URL(String CONTENT_IMAGE_URL) {
        this.CONTENT_IMAGE_URL = CONTENT_IMAGE_URL;
    }

    public String getCONTENT_TITLE_TEXT() {
        return CONTENT_TITLE_TEXT;
    }

    public void setCONTENT_TITLE_TEXT(String CONTENT_TITLE_TEXT) {
        this.CONTENT_TITLE_TEXT = CONTENT_TITLE_TEXT;
    }

    public String getCONTENT_TEXT() {
        return CONTENT_TEXT;
    }

    public void setCONTENT_TEXT(String CONTENT_TEXT) {
        this.CONTENT_TEXT = CONTENT_TEXT;
    }

    public String getCREATE_DATE() {
        return CREATE_DATE;
    }

    public void setCREATE_DATE(String CREATE_DATE) {
        this.CREATE_DATE = CREATE_DATE;
    }

    public String getLABELLING() {
        return LABELLING;
    }

    public void setLABELLING(String LABELLING) {
        this.LABELLING = LABELLING;
    }

    public int getZAN_NUM() {
        return ZAN_NUM;
    }

    public void setZAN_NUM(int ZAN_NUM) {
        this.ZAN_NUM = ZAN_NUM;
    }

    public int getSHOUCHAN_NUM() {
        return SHOUCHAN_NUM;
    }

    public void setSHOUCHAN_NUM(int SHOUCHAN_NUM) {
        this.SHOUCHAN_NUM = SHOUCHAN_NUM;
    }

    public int getPINGLUN_NUM() {
        return PINGLUN_NUM;
    }

    public void setPINGLUN_NUM(int PINGLUN_NUM) {
        this.PINGLUN_NUM = PINGLUN_NUM;
    }

    public String getMESSAGE_LINK() {
        return MESSAGE_LINK;
    }

    public void setMESSAGE_LINK(String MESSAGE_LINK) {
        this.MESSAGE_LINK = MESSAGE_LINK;
    }

    public boolean IS_READ() {
        return IS_READ;
    }

    public void setIS_READ(boolean IS_READ) {
        this.IS_READ = IS_READ;
    }

    public long getREAD_DATE_LONG() {
        return READ_DATE_LONG;
    }

    public void setREAD_DATE_LONG(long READ_DATE_LONG) {
        this.READ_DATE_LONG = READ_DATE_LONG;
    }

    public OKCardUrlListBean getBean() {
        if (bean != null) {
            return bean;
        } else {
            return null;
        }
    }

    public void setBean(OKCardUrlListBean bean) {
        this.bean = bean;
    }

    public int getAPPROVE_BY() {
        return APPROVE_BY;
    }

    public void setAPPROVE_BY(int APPROVE_BY) {
        this.APPROVE_BY = APPROVE_BY;
    }

    public String getAPPROVE_INFO() {
        return APPROVE_INFO;
    }

    public void setAPPROVE_INFO(String APPROVE_INFO) {
        this.APPROVE_INFO = APPROVE_INFO;
    }

    public String toStringReadDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd/HH/mm");
        return dateFormat.format(new Date(READ_DATE_LONG));
    }
}
