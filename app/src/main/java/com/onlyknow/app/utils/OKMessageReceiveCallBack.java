package com.onlyknow.app.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.onlyknow.app.OKConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/9.
 */

public class OKMessageReceiveCallBack implements EMMessageListener {
    public final static int RECEIVED_MESSAGE = 120;

    private Context mContext;
    private List<EMMessage> mEMMessageList;
    private String mSendUserName = "";
    private Handler mHandler;

    public OKMessageReceiveCallBack(Context context, List<EMMessage> list, String name, Handler handler) {
        this.mContext = context;
        this.mEMMessageList = list;
        this.mSendUserName = name;
        this.mHandler = handler;
    }

    @Override
    public void onMessageReceived(List<EMMessage> receivedList) {
        EMClient.getInstance().chatManager().importMessages(receivedList);

        List<EMMessage> addList = new ArrayList<>();
        boolean isSendNotice = true;
        for (EMMessage mEMMessage : receivedList) {
            String username = mEMMessage.getFrom();
            if (username.equals(mSendUserName)) {
                addList.add(mEMMessage);
            } else if (isSendNotice) {
                if (mEMMessage.getType() == EMMessage.Type.TXT) {
                    Bundle mBundle = new Bundle();
                    mBundle.putInt("TYPE", 0);
                    mBundle.putString("TITLE", username);
                    mBundle.putString("CONTENT", ((EMTextMessageBody) mEMMessage.getBody()).getMessage());
                    Intent intent = new Intent(OKConstant.ACTION_SHOW_NOTICE);
                    intent.putExtras(mBundle);
                    mContext.sendBroadcast(intent);
                    isSendNotice = false;
                } else if (mEMMessage.getType() == EMMessage.Type.IMAGE) {
                    Bundle mBundle = new Bundle();
                    mBundle.putInt("TYPE", 0);
                    mBundle.putString("TITLE", username);
                    mBundle.putString("CONTENT", "给您发送了一张图片");
                    Intent intent = new Intent(OKConstant.ACTION_SHOW_NOTICE);
                    intent.putExtras(mBundle);
                    mContext.sendBroadcast(intent);
                    isSendNotice = false;
                }
            }
        }

        if (addList.size() == 0) {
            OKLogUtil.print("接收到其他会话的消息");
            return;
        }
        mEMMessageList.addAll(addList);
        mHandler.sendEmptyMessage(RECEIVED_MESSAGE);
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> list) {
    }

    @Override
    public void onMessageRead(List<EMMessage> list) {
    }

    @Override
    public void onMessageDelivered(List<EMMessage> list) {
    }

    @Override
    public void onMessageRecalled(List<EMMessage> list) {
    }

    @Override
    public void onMessageChanged(EMMessage emMessage, Object o) {
    }
}
