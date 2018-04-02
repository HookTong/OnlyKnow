package com.onlyknow.app.api;

import android.content.Context;
import android.os.AsyncTask;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.onlyknow.app.database.bean.OKNoticeBean;
import com.onlyknow.app.database.bean.OKUserInfoBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/9.
 */

public class OKLoadNoticeApi extends OKBaseApi {
    private onCallBack mOnCallBack;
    private Context mContext;
    private LoadNoticeTask mLoadNoticeTask;

    public OKLoadNoticeApi(Context con) {
        this.mContext = con;
    }

    public interface onCallBack {
        void noticeApiComplete(List<OKNoticeBean> list);
    }

    public void requestNoticeBeanList(onCallBack mCallBack) {
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
                    mNoticeBean.setUSER_NAME(receiveLastMsg.getFrom());
                    mNoticeBean.setNOTICE_TITLE(receiveLastMsg.getStringAttribute("FROM_" + OKUserInfoBean.KEY_NICKNAME, ""));
                    mNoticeBean.setHEAD_PORTRAIT_URL(receiveLastMsg.getStringAttribute(OKUserInfoBean.KEY_HEADPORTRAIT_URL, ""));

                    OKUserInfoBean bean = getUserHeadPortrait(receiveLastMsg.getFrom());
                    if (bean != null) {
                        mNoticeBean.setNOTICE_TITLE(bean.getNICKNAME());
                        mNoticeBean.setHEAD_PORTRAIT_URL(bean.getHEADPORTRAIT_URL());
                    }
                } else {
                    mNoticeBean.setUSER_NAME(lastMsg.getTo());
                    mNoticeBean.setNOTICE_TITLE(lastMsg.getStringAttribute("TO_" + OKUserInfoBean.KEY_NICKNAME, ""));
                    mNoticeBean.setHEAD_PORTRAIT_URL(lastMsg.getStringAttribute(OKUserInfoBean.KEY_HEADPORTRAIT_URL, ""));

                    OKUserInfoBean bean = getUserHeadPortrait(lastMsg.getTo());
                    if (bean != null) {
                        mNoticeBean.setNOTICE_TITLE(bean.getNICKNAME());
                        mNoticeBean.setHEAD_PORTRAIT_URL(bean.getHEADPORTRAIT_URL());
                    }
                }

                if (lastMsg.getType() == EMMessage.Type.TXT) {
                    mNoticeBean.setNOTICE_CONTENT(((EMTextMessageBody) lastMsg.getBody()).getMessage());
                } else if (lastMsg.getType() == EMMessage.Type.IMAGE) {
                    mNoticeBean.setNOTICE_CONTENT("[图片]");
                } else {
                    mNoticeBean.setNOTICE_CONTENT("[其他消息]");
                }

                mNoticeBean.setSTATE(mEMConversation.getUnreadMsgCount() > 0 ? true : false);
                mNoticeBean.setUNREAD_NUM(mEMConversation.getUnreadMsgCount());
                mNoticeBean.setALL_MESSAGE_NUM(mEMConversation.getAllMsgCount());
                mNoticeBean.setDATE(new SimpleDateFormat("yyyy/MM/dd/HH/mm").format(new Date(lastMsg.getMsgTime())));

                list.add(mNoticeBean);
            }
            return list;
        }

        private OKUserInfoBean getUserHeadPortrait(String userName) {
            Map<String, String> map = new HashMap<>();// 请求参数
            map.put("username", userName);
            map.put("type", "HEAD_PORTRAIT");
            return new OKBusinessApi().getUserInfo(map);
        }

        @Override
        protected void onPostExecute(List<OKNoticeBean> list) {
            if (isCancelled()) {
                return;
            }
            mOnCallBack.noticeApiComplete(list);
            super.onPostExecute(list);
        }
    }
}
