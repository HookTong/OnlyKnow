package com.onlyknow.app.database.bean;

import java.util.Date;

public class OKUserInfoBean {
    public final static String KEY_USER_ID = "userId";
    private int userId;

    public final static String KEY_USERNAME = "userName";
    private String userName;

    public final static String KEY_NICKNAME = "userNickname";
    private String userNickname;

    public final static String KEY_USER_TYPE = "userType";
    private String userType;

    public final static String KEY_PASSWORD = "userPassword";
    private String userPassword;

    public final static String KEY_PHONE = "userPhone";
    private String userPhone;

    public final static String KEY_EMAIL = "userEmail";
    private String userEmail;

    public final static String KEY_SEX = "sex";
    private String sex;

    public final static String KEY_AGE = "age";
    private Integer age;

    public final static String KEY_BIRTH_DATE = "birthDate";
    private Date birthDate;

    public final static String KEY_HEAD_PORTRAIT_URL = "headPortraitUrl";
    private String headPortraitUrl;

    public final static String KEY_HOME_PAGE_URL = "homepageUrl";
    private String homepageUrl;

    public final static String KEY_TAG = "tag";
    private String tag;

    public final static String KEY_ME_WATCH = "meWatch";
    private Integer meWatch;

    public final static String KEY_ME_ATTENTION = "meAttention";
    private Integer meAttention;

    public final static String KEY_INTEGRAL = "meIntegral";
    private Integer meIntegral;

    public final static String KEY_ARTICLE = "meArticle";
    private Integer meArticle;

    public final static String KEY_WITHOUT_APPROVE = "withoutApprova";
    private boolean withoutApprova;

    public final static String KEY_EDIT_DATE = "editDate";
    private Date editDate;

    public final static String KEY_RE_DATE = "reDate";
    private Date reDate;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getHeadPortraitUrl() {
        return headPortraitUrl;
    }

    public void setHeadPortraitUrl(String headPortraitUrl) {
        this.headPortraitUrl = headPortraitUrl;
    }

    public String getHomepageUrl() {
        return homepageUrl;
    }

    public void setHomepageUrl(String homepageUrl) {
        this.homepageUrl = homepageUrl;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Integer getMeWatch() {
        return meWatch;
    }

    public void setMeWatch(Integer meWatch) {
        this.meWatch = meWatch;
    }

    public Integer getMeAttention() {
        return meAttention;
    }

    public void setMeAttention(Integer meAttention) {
        this.meAttention = meAttention;
    }

    public Integer getMeIntegral() {
        return meIntegral;
    }

    public void setMeIntegral(Integer meIntegral) {
        this.meIntegral = meIntegral;
    }

    public Integer getMeArticle() {
        return meArticle;
    }

    public void setMeArticle(Integer meArticle) {
        this.meArticle = meArticle;
    }

    public boolean isWithoutApprova() {
        return withoutApprova;
    }

    public void setWithoutApprova(boolean withoutApprova) {
        this.withoutApprova = withoutApprova;
    }

    public Date getEditDate() {
        return editDate;
    }

    public void setEditDate(Date editDate) {
        this.editDate = editDate;
    }

    public Date getReDate() {
        return reDate;
    }

    public void setReDate(Date reDate) {
        this.reDate = reDate;
    }
}
