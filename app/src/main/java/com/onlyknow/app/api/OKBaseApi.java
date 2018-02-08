package com.onlyknow.app.api;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/12/22.
 */

public class OKBaseApi {
    public final int INTERFACE_EXPLORE = 1; // 探索界面
    public final int INTERFACE_NEAR = 2; // 附近界面
    public final int INTERFACE_HISTORY = 3; // 历史界面
    public final int INTERFACE_DYNAMIC = 4; // 动态界面
    public final int INTERFACE_ATTENTION = 5; // 关注界面
    public final int INTERFACE_COLLECTION = 6; // 收藏界面
    public final int INTERFACE_HOT = 7; // 热门界面
    public final int INTERFACE_HOME = 8; // 用户展示界面
    public final int INTERFACE_GOODS = 9; // 商品界面
    public final int INTERFACE_NOTICE = 10; // 通知界面
    public final int INTERFACE_SESSION = 11; // 会话界面
    public final int INTERFACE_COMMENT = 12; // 卡片评论界面
    public final int INTERFACE_COMMENT_REPLY = 13; // 评论回复界面
    public final int INTERFACE_SEARCH = 14; // 搜索界面
    public final int INTERFACE_CARD_AND_COMMENT = 15;

    public final ExecutorService exec = Executors.newFixedThreadPool(100);
}
