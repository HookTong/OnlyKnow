package com.onlyknow.app.db.bean;

import com.onlyknow.app.OKConstant;
import com.onlyknow.app.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OKCarouselAdBean {
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

    // 需要转化成可直接使用的List;
    private List<CarouselImage> carouselImages;
    private List<ADImage> adImages;

    private long requestTime = 0;

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

    public long getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(long requestTime) {
        this.requestTime = requestTime;
    }

    public List<CarouselImage> getCarouselImages() {
        if (carouselImages != null) return carouselImages;

        carouselImages = new ArrayList<>();

        CarouselImage carouselImage1 = new CarouselImage();
        carouselImage1.setUrl(getHpImageUrl1());
        carouselImage1.setResId(R.drawable.topgd1);

        CarouselImage carouselImage2 = new CarouselImage();
        carouselImage2.setUrl(getHpImageUrl2());
        carouselImage2.setResId(R.drawable.topgd2);

        CarouselImage carouselImage3 = new CarouselImage();
        carouselImage3.setUrl(getHpImageUrl3());
        carouselImage3.setResId(R.drawable.topgd3);

        CarouselImage carouselImage4 = new CarouselImage();
        carouselImage4.setUrl(getHpImageUrl4());
        carouselImage4.setResId(R.drawable.topgd4);

        CarouselImage carouselImage5 = new CarouselImage();
        carouselImage5.setUrl(getHpImageUrl5());
        carouselImage5.setResId(R.drawable.topgd5);

        carouselImages.add(carouselImage1);
        carouselImages.add(carouselImage2);
        carouselImages.add(carouselImage3);
        carouselImages.add(carouselImage4);
        carouselImages.add(carouselImage5);

        OKConstant.setCarouselImages(carouselImages);

        return carouselImages;
    }

    public List<ADImage> getAdImages() {
        if (adImages != null) return adImages;

        adImages = new ArrayList<>();

        ADImage adImage1 = new ADImage();
        adImage1.setUrl(getAdImageUrl1());
        adImage1.setLink(getAdLinkUrl1());

        ADImage adImage2 = new ADImage();
        adImage2.setUrl(getAdImageUrl2());
        adImage2.setLink(getAdLinkUrl2());

        ADImage adImage3 = new ADImage();
        adImage3.setUrl(getAdImageUrl3());
        adImage3.setLink(getAdLinkUrl3());

        adImages.add(adImage1);
        adImages.add(adImage2);
        adImages.add(adImage3);

        OKConstant.setAdImages(adImages);

        return adImages;
    }

    public static class CarouselImage {
        private String url = "";
        private int resId;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getResId() {
            return resId;
        }

        public void setResId(int resId) {
            this.resId = resId;
        }
    }

    public static class ADImage {
        private String url = "";
        private String link = "";

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }
    }
}
