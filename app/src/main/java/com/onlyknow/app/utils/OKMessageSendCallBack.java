package com.onlyknow.app.utils;

import android.os.Handler;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/9.
 */

public class OKMessageSendCallBack implements EMCallBack {

    public final static int SEND_MESSAGE_SUCCESS = 100;
    public final static int SEND_MESSAGE_FAILURE = 110;

    private List<EMMessage> mEMMessageList;
    private Handler mHandler;
    private EMMessage mSendMsg;

    public OKMessageSendCallBack(List<EMMessage> list, EMMessage msg, Handler handler) {
        this.mEMMessageList = list;
        this.mSendMsg = msg;
        this.mHandler = handler;
    }

    @Override
    public void onSuccess() {
        List<EMMessage> saveMsg = new ArrayList<>();
        saveMsg.add(mSendMsg);
        EMClient.getInstance().chatManager().importMessages(saveMsg);
        mEMMessageList.add(mSendMsg);
        mHandler.sendEmptyMessage(SEND_MESSAGE_SUCCESS);
    }

    @Override
    public void onError(int i, String s) {
        mHandler.sendEmptyMessage(SEND_MESSAGE_FAILURE);
    }

    @Override
    public void onProgress(int i, String s) {
    }
}
