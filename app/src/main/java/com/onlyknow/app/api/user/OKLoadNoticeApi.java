package com.onlyknow.app.api.user;

import android.content.Context;
import android.os.AsyncTask;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.onlyknow.app.api.OKBaseApi;
import com.onlyknow.app.api.OKServiceResult;
import com.onlyknow.app.db.bean.OKNoticeBean;
import com.onlyknow.app.db.bean.OKUserInfoBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OKLoadNoticeApi extends OKBaseApi {
    private onCallBack mOnCallBack;
    private Context mContext;
    private LoadNoticeTask mLoadNoticeTask;

    public OKLoadNoticeApi(Context con) {
        this.mContext = con;
    }

    public interface onCallBack {
        void loadNoticeComplete(List<OKNoticeBean> list);
    }

    public void requestNotice(onCallBack mCallBack) {
        this.mOnCallBack = mCallBack;
        cancelTask();
        mLoadNoticeTask = new LoadNoticeTask();
        mLoadNoticeTask.executeOnExecutor(exec);
    }

    public void cancelTask() {
        if (mLoadNoticeTask != null && mLoadNoticeTask.getStatus() == AsyncTask.Status.RUNNING) {
            mLoadNoticeTask.cancel(true);
        }
    }

    private class LoadNoticeTask extends AsyncTask<Void, Void, List<OKNoticeBean>> {
        @Override
        protected List<OKNoticeBean> doInBackground(Void... params) {
            if (isCancelled()) return null; // 线程已终止

            Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
            if (conversations == null || conversations.size() == 0) return null; // 没有会话

            List<OKNoticeBean> list = new ArrayList<>();
            for (Map.Entry<String, EMConversation> entry : conversations.entrySet()) {
                EMConversation mEMConversation = entry.getValue();
                EMMessage lastMsg = mEMConversation.getLastMessage(); // 会话的最后一条消息
                EMMessage receiveLastMsg = mEMConversation.getLatestMessageFromOthers(); // 会话接收到的最后一条消息

                OKNoticeBean mNoticeBean = new OKNoticeBean();
                if (receiveLastMsg != null) {
                    mNoticeBean.setUserName(receiveLastMsg.getFrom());
                    mNoticeBean.setNoticeTitle(receiveLastMsg.getStringAttribute("FROM_" + OKUserInfoBean.KEY_NICKNAME, ""));
                    mNoticeBean.setAvatarUrl(receiveLastMsg.getStringAttribute(OKUserInfoBean.KEY_HEAD_PORTRAIT_URL, ""));

                    OKUserInfoBean bean = getUserHeadPortrait(receiveLastMsg.getFrom());
                    if (bean != null) {
                        mNoticeBean.setNoticeTitle(bean.getUserNickname());
                        mNoticeBean.setAvatarUrl(bean.getHeadPortraitUrl());
                    }
                } else {
                    mNoticeBean.setUserName(lastMsg.getTo());
                    mNoticeBean.setNoticeTitle(lastMsg.getStringAttribute("TO_" + OKUserInfoBean.KEY_NICKNAME, ""));
                    mNoticeBean.setAvatarUrl(lastMsg.getStringAttribute(OKUserInfoBean.KEY_HEAD_PORTRAIT_URL, ""));

                    OKUserInfoBean bean = getUserHeadPortrait(lastMsg.getTo());
                    if (bean != null) {
                        mNoticeBean.setNoticeTitle(bean.getUserNickname());
                        mNoticeBean.setAvatarUrl(bean.getHeadPortraitUrl());
                    }
                }

                if (lastMsg.getType() == EMMessage.Type.TXT) {
                    mNoticeBean.setNoticeContent(((EMTextMessageBody) lastMsg.getBody()).getMessage());
                } else if (lastMsg.getType() == EMMessage.Type.IMAGE) {
                    mNoticeBean.setNoticeContent("[图片]");
                } else {
                    mNoticeBean.setNoticeContent("[其他消息]");
                }

                mNoticeBean.setState(mEMConversation.getUnreadMsgCount() > 0 ? true : false);
                mNoticeBean.setUnreadNum(mEMConversation.getUnreadMsgCount());
                mNoticeBean.setAllMessageNum(mEMConversation.getAllMsgCount());
                mNoticeBean.setDate(new Date(lastMsg.getMsgTime()));

                list.add(mNoticeBean);
            }
            return list;
        }

        private OKUserInfoBean getUserHeadPortrait(String userName) {

            Map<String, String> map = new HashMap<>();// 请求参数
            map.put(OKManagerUserApi.Params.KEY_NAME, userName);
            map.put(OKManagerUserApi.Params.KEY_TYPE, OKManagerUserApi.Params.TYPE_GET_INFO);

            OKServiceResult<OKUserInfoBean> result = managerUser(map, OKUserInfoBean.class);

            if (result == null || !result.isSuccess()) return null;

            return result.getData();
        }

        @Override
        protected void onPostExecute(List<OKNoticeBean> list) {
            if (isCancelled()) {
                return;
            }
            mOnCallBack.loadNoticeComplete(list);
            super.onPostExecute(list);
        }
    }
}
