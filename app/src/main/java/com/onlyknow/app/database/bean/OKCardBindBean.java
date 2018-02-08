package com.onlyknow.app.database.bean;

/**
 * Created by Administrator on 2017/12/14.
 */

public class OKCardBindBean {
    private boolean IS_ZAN = false;
    private boolean IS_ATTENTION = false;
    private boolean IS_WATCH = false;
    private int ZAN_COUNT = 0;
    private int WATCH_COUNT = 0;
    private int PINLUN_COUNT = 0;
    private String QIANMIN = "";
    private boolean isCardRemove = false;

    public boolean IS_ZAN() {
        return IS_ZAN;
    }

    public void setIS_ZAN(boolean IS_ZAN) {
        this.IS_ZAN = IS_ZAN;
    }

    public boolean IS_ATTENTION() {
        return IS_ATTENTION;
    }

    public void setIS_ATTENTION(boolean IS_ATTENTION) {
        this.IS_ATTENTION = IS_ATTENTION;
    }

    public boolean IS_WATCH() {
        return IS_WATCH;
    }

    public void setIS_WATCH(boolean IS_WATCH) {
        this.IS_WATCH = IS_WATCH;
    }

    public int getZAN_COUNT() {
        return ZAN_COUNT;
    }

    public void setZAN_COUNT(int ZAN_COUNT) {
        this.ZAN_COUNT = ZAN_COUNT;
    }

    public int getWATCH_COUNT() {
        return WATCH_COUNT;
    }

    public void setWATCH_COUNT(int WATCH_COUNT) {
        this.WATCH_COUNT = WATCH_COUNT;
    }

    public int getPINLUN_COUNT() {
        return PINLUN_COUNT;
    }

    public void setPINLUN_COUNT(int PINLUN_COUNT) {
        this.PINLUN_COUNT = PINLUN_COUNT;
    }

    public String getQIANMIN() {
        return QIANMIN;
    }

    public void setQIANMIN(String QIANMIN) {
        this.QIANMIN = QIANMIN;
    }

    public boolean isCardRemove() {
        return isCardRemove;
    }

    public void setCardRemove(boolean cardRemove) {
        isCardRemove = cardRemove;
    }
}
