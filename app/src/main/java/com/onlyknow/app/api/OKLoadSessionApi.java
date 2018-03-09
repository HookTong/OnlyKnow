package com.onlyknow.app.api;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/9.
 */

public class OKLoadSessionApi extends OKBaseApi {
    private onCallBack mOnCallBack;
    private Context mContext;
    private LoadSessionTask mLoadSessionTask;
    private String mUserName = "";

    public OKLoadSessionApi(Context con, String name) {
        this.mContext = con;
        this.mUserName = name;
    }

    public interface onCallBack {
        void sessionApiComplete(List<EMMessage> list);
    }

    public void requestMessageList(String name, String topId, List<EMMessage> oldList, onCallBack mCallBack) {
        this.mUserName = name;
        this.mOnCallBack = mCallBack;
        cancelTask();
        mLoadSessionTask = new LoadSessionTask(mUserName, oldList);
        mLoadSessionTask.executeOnExecutor(exec, topId);
    }

    public void cancelTask() {
        if (mLoadSessionTask != null && mLoadSessionTask.getStatus() == AsyncTask.Status.RUNNING) {
            mLoadSessionTask.cancel(true);
        }
    }

    private class LoadSessionTask extends AsyncTask<String, Void, List<EMMessage>> {
        private String mSendUserName;
        private List<EMMessage> oldMsgList;

        public LoadSessionTask(String sendUserName, List<EMMessage> list) {
            this.mSendUserName = sendUserName;
            this.oldMsgList = list;
        }

        @Override
        protected List<EMMessage> doInBackground(String... params) {
            if (isCancelled()) {
                return null;
            }

            String topId = params[0];
            List<EMMessage> list = new ArrayList<>();
            if (TextUtils.isEmpty(topId)) {
                EMConversation conversation = EMClient.getInstance().chatManager().getConversation(mSendUserName);
                if (conversation == null) {
                    return null;
                }
                //获取此会话的所有消息
                list = conversation.getAllMessages();
            } else {
                EMConversation conversation = EMClient.getInstance().chatManager().getConversation(mSendUserName);
                if (conversation == null) {
                    return null;
                }
                list = conversation.loadMoreMsgFromDB(topId, 20);
            }
            if (oldMsgList != null && list != null) { // 去重复项
                for (int i = 0; i < oldMsgList.size(); i++) {
                    EMMessage oldMsg = oldMsgList.get(i);
                    for (int p = 0; p < list.size(); p++) {
                        EMMessage newMsg = list.get(p);
                        if (oldMsg.getMsgId().equals(newMsg.getMsgId())) {
                            list.remove(p);
                            break;
                        }
                    }
                }
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<EMMessage> list) {
            super.onPostExecute(list);
            if (isCancelled()) {
                return;
            }
            mOnCallBack.sessionApiComplete(list);
        }
    }
}
