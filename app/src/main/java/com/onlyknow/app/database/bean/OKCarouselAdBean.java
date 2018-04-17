package com.onlyknow.app.database.bean;

import com.onlyknow.app.OKConstant;
import com.onlyknow.app.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OKCarouselAdBean {
    public final static String KEY_GROUP_ID = "groupId";
    private int groupId;

    // 轮播图片
    private String hpImageUrl1;
    private String hpImageUrl2;
    private String hpImageUrl3;
    private String hpImageUrl4;
    private String hpImageUrl5;

    // 广告图片
    private String adImageUrl1;
    private String adImageUrl2;
    private String adImageUrl3;

    // 广告链接
    private String adLinkUrl1;
    private String adLinkUrl2;
    private String adLinkUrl3;

    private Date groupDate;

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getHpImageUrl1() {
        return hpImageUrl1;
    }

    public void setHpImageUrl1(String hpImageUrl1) {
        this.hpImageUrl1 = hpImageUrl1;
    }

    public String getHpImageUrl2() {
        return hpImageUrl2;
    }

    public void setHpImageUrl2(String hpImageUrl2) {
        this.hpImageUrl2 = hpImageUrl2;
    }

    public String getHpImageUrl3() {
        return hpImageUrl3;
    }

    public void setHpImageUrl3(String hpImageUrl3) {
        this.hpImageUrl3 = hpImageUrl3;
    }

    public String getHpImageUrl4() {
        return hpImageUrl4;
    }

    public void setHpImageUrl4(String hpImageUrl4) {
        this.hpImageUrl4 = hpImageUrl4;
    }

    public String getHpImageUrl5() {
        return hpImageUrl5;
    }

    public void setHpImageUrl5(String hpImageUrl5) {
        this.hpImageUrl5 = hpImageUrl5;
    }

    public String getAdImageUrl1() {
        return adImageUrl1;
    }

    public void setAdImageUrl1(String adImageUrl1) {
        this.adImageUrl1 = adImageUrl1;
    }

    public String getAdImageUrl2() {
        return adImageUrl2;
    }

    public void setAdImageUrl2(String adImageUrl2) {
        this.adImageUrl2 = adImageUrl2;
    }

    public String getAdImageUrl3() {
        return adImageUrl3;
    }

    public void setAdImageUrl3(String adImageUrl3) {
        this.adImageUrl3 = adImageUrl3;
    }

    public String getAdLinkUrl1() {
        return adLinkUrl1;
    }

    public void setAdLinkUrl1(String adLinkUrl1) {
        this.adLinkUrl1 = adLinkUrl1;
    }

    public String getAdLinkUrl2() {
        return adLinkUrl2;
    }

    public void setAdLinkUrl2(String adLinkUrl2) {
        this.adLinkUrl2 = adLinkUrl2;
    }

    public String getAdLinkUrl3() {
        return adLinkUrl3;
    }

    public void setAdLinkUrl3(String adLinkUrl3) {
        this.adLinkUrl3 = adLinkUrl3;
    }

    public Date getGroupDate() {
        return groupDate;
    }

    public void setGroupDate(Date groupDate) {
        this.groupDate = groupDate;
    }

    public final static String KEY_URL = "URL";
    public final static String KEY_RID = "RID";
    public final static String KEY_LINK = "LINK";

    public List<Map<String, Object>> getCarouselImage() {
        // 更新轮播图片,RES_ID为错图替代
        List<Map<String, Object>> headList = new ArrayList<>();

        Map<String, Object> map1 = new HashMap<>();
        map1.put(KEY_URL, getHpImageUrl1());
        map1.put(KEY_RID, R.drawable.topgd1);

        Map<String, Object> map2 = new HashMap<>();
        map2.put(KEY_URL, getHpImageUrl2());
        map2.put(KEY_RID, R.drawable.topgd2);

        Map<String, Object> map3 = new HashMap<>();
        map3.put(KEY_URL, getHpImageUrl3());
        map3.put(KEY_RID, R.drawable.topgd3);

        Map<String, Object> map4 = new HashMap<>();
        map4.put(KEY_URL, getHpImageUrl4());
        map4.put(KEY_RID, R.drawable.topgd4);

        Map<String, Object> map5 = new HashMap<>();
        map5.put(KEY_URL, getHpImageUrl5());
        map5.put(KEY_RID, R.drawable.topgd5);

        headList.add(map1);
        headList.add(map2);
        headList.add(map3);
        headList.add(map4);
        headList.add(map5);

        OKConstant.setCarouselImages(headList);

        return headList;
    }

    public List<Map<String, String>> getAdImage() {
        // 更新广告URL
        List<Map<String, String>> adList = new ArrayList<>();

        Map<String, String> adMap1 = new HashMap<>();
        adMap1.put(KEY_URL, getAdImageUrl1());
        adMap1.put(KEY_LINK, getAdLinkUrl1());

        Map<String, String> adMap2 = new HashMap<>();
        adMap2.put(KEY_URL, getAdImageUrl2());
        adMap2.put(KEY_LINK, getAdLinkUrl2());

        Map<String, String> adMap3 = new HashMap<>();
        adMap3.put(KEY_URL, getAdImageUrl3());
        adMap3.put(KEY_LINK, getAdLinkUrl3());

        adList.add(adMap1);
        adList.add(adMap2);
        adList.add(adMap3);

        OKConstant.setAdImages(adList);

        return adList;
    }
}
