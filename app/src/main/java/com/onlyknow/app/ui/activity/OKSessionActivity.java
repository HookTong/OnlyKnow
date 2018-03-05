package com.onlyknow.app.ui.activity;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caimuhao.rxpicker.RxPicker;
import com.caimuhao.rxpicker.bean.ImageItem;
import com.caimuhao.rxpicker.utils.RxPickerImageLoader;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.onlyknow.app.GlideApp;
import com.onlyknow.app.OKConstant;
import com.onlyknow.app.R;
import com.onlyknow.app.net.OKBusinessNet;
import com.onlyknow.app.database.bean.OKUserInfoBean;
import com.onlyknow.app.ui.OKBaseActivity;
import com.onlyknow.app.ui.view.OKRecyclerView;
import com.onlyknow.app.ui.view.OKSEImageView;
import com.onlyknow.app.utils.OKLogUtil;
import com.scwang.smartrefresh.header.TaurusHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.functions.Consumer;

public class OKSessionActivity extends OKBaseActivity implements OnRefreshListener {
    private RefreshLayout mRefreshLayout;
    private OKRecyclerView mOKRecyclerView;
    private SessionAdapter mSessionAdapter;

    private EditText editTextMsg;
    private OKSEImageView sendButtonMsg, sendButtonImage;

    private String THIS_USER_NAME;
    private String THIS_USER_NICKNAME;
    private String SEND_USER_NAME;
    private String SEND_USER_NICKNAME;
    private OKUserInfoBean THE_USER_INFO;
    private OKUserInfoBean ME_USER_INFO;
    private final int UPDATE_SESSION = 3;

    private LoadSessionTask mLoadSessionTask;
    private MessageCallBack messageCallBackReceived, messageCallBackSend;
    private List<EMMessage> EMMessageList = new ArrayList<>();

    @SuppressLint("HandlerLeak")
    private Handler mMsgHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MessageCallBack.SEND_MESSAGE_SUCCESS:
                    editTextMsg.setText("");
                    mToolBarProgressBar.setVisibility(View.GONE);
                    mOKRecyclerView.getAdapter().notifyDataSetChanged();
                    mOKRecyclerView.scrollToPosition(mOKRecyclerView.getAdapter().getItemCount() - 1);
                    break;
                case MessageCallBack.SEND_MESSAGE_FAILURE:
                    mToolBarProgressBar.setVisibility(View.GONE);
                    showSnackbar(mOKRecyclerView, "消息发送失败", "");
                    break;
                case MessageCallBack.RECEIVED_MESSAGE:
                    mOKRecyclerView.getAdapter().notifyDataSetChanged();
                    mOKRecyclerView.scrollToPosition(mOKRecyclerView.getAdapter().getItemCount() - 1);
                    break;
                case UPDATE_SESSION:
                    mToolbarTitle.setText("与 " + SEND_USER_NICKNAME + " 的会话");
                    mOKRecyclerView.getAdapter().notifyDataSetChanged();
                    mOKRecyclerView.scrollToPosition(mOKRecyclerView.getAdapter().getItemCount() - 1);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ok_activity_session);
        initSystemBar(this);
        initUserInfoSharedPreferences();

        sendUserBroadcast(ACTION_MAIN_SERVICE_REMOVE_MESSAGE_LISTENER_IM, null); // 移除服务中的消息接收监听器

        THIS_USER_NAME = USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, "");
        THIS_USER_NICKNAME = USER_INFO_SP.getString(OKUserInfoBean.KEY_NICKNAME, "");
        SEND_USER_NAME = getIntent().getExtras().getString(OKUserInfoBean.KEY_USERNAME);
        SEND_USER_NICKNAME = getIntent().getExtras().getString(OKUserInfoBean.KEY_NICKNAME);

        EMClient.getInstance().groupManager().loadAllGroups();
        EMClient.getInstance().chatManager().loadAllConversations();

        findView();
        init();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mToolbar != null) {
            mToolbar.setTitle("");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mLoadSessionTask != null && mLoadSessionTask.getStatus() == AsyncTask.Status.RUNNING) {
            mLoadSessionTask.cancel(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sendUserBroadcast(ACTION_MAIN_SERVICE_ADD_MESSAGE_LISTENER_IM, null);// 添加服务中的消息监听器
        if (messageCallBackReceived != null) {
            EMClient.getInstance().chatManager().removeMessageListener(messageCallBackReceived);
        }
    }

    private void findView() {
        super.findCommonToolbarView(this);
        setSupportActionBar(mToolbar);

        mToolbarBack.setVisibility(View.VISIBLE);
        mToolbarTitle.setVisibility(View.VISIBLE);
        mToolbarTitle.setText("与 " + SEND_USER_NICKNAME + " 的会话");

        mRefreshLayout = (RefreshLayout) findViewById(R.id.Session_SwipeRefresh);
        mOKRecyclerView = (OKRecyclerView) findViewById(R.id.Session_Nolistview);

        editTextMsg = (EditText) findViewById(R.id.Session_input_message);
        sendButtonMsg = (OKSEImageView) findViewById(R.id.Session_send_but);
        sendButtonImage = (OKSEImageView) findViewById(R.id.Session_send_image);

        mOKRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRefreshLayout.setRefreshHeader(new TaurusHeader(this));
        mRefreshLayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale));
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setEnableLoadMore(false);
    }

    private void init() {
        mSessionAdapter = new SessionAdapter(this, EMMessageList);
        mOKRecyclerView.setAdapter(mSessionAdapter);

        messageCallBackReceived = new MessageCallBack(mMsgHandler);
        EMClient.getInstance().chatManager().addMessageListener(messageCallBackReceived); // 添加当前Activity的消息接收监听器

        new Thread() {
            @Override
            public void run() {
                super.run();
                Map<String, String> map = new HashMap<>();
                map.put("username", THIS_USER_NAME);
                map.put("type", "HEAD_PORTRAIT");
                ME_USER_INFO = new OKBusinessNet().getUserInfo(map);
                THIS_USER_NICKNAME = ME_USER_INFO.getNICKNAME();

                Map<String, String> map2 = new HashMap<>();
                map2.put("username", SEND_USER_NAME);
                map2.put("type", "HEAD_PORTRAIT");
                THE_USER_INFO = new OKBusinessNet().getUserInfo(map2);
                SEND_USER_NICKNAME = THE_USER_INFO.getNICKNAME();

                mMsgHandler.sendEmptyMessage(UPDATE_SESSION);
            }
        }.start();

        sendButtonMsg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                final String content = editTextMsg.getText().toString();
                if (!TextUtils.isEmpty(content)) {
                    final String headPortraitUrl = USER_INFO_SP.getString(OKUserInfoBean.KEY_HEADPORTRAIT_URL, "");
                    mToolBarProgressBar.setVisibility(View.VISIBLE);
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            //创建一条文本消息,content为消息文字内容,toChatUsername为对方用户或者群聊的id,后文皆是如此
                            EMMessage sendMessage = EMMessage.createTxtSendMessage(content, SEND_USER_NAME);
                            messageCallBackSend = new MessageCallBack(mMsgHandler, sendMessage);
                            sendMessage.setMessageStatusCallback(messageCallBackSend); // 设置消息发送状态监听
                            sendMessage.setFrom(THIS_USER_NAME); // 设置消息发送者
                            sendMessage.setTo(SEND_USER_NAME); // 设置消息的接收者
                            sendMessage.setAttribute("FROM_" + OKUserInfoBean.KEY_NICKNAME, THIS_USER_NICKNAME); // 设置FROM昵称属性
                            sendMessage.setAttribute("TO_" + OKUserInfoBean.KEY_NICKNAME, SEND_USER_NICKNAME); // 设置TO昵称属性
                            sendMessage.setAttribute(OKUserInfoBean.KEY_HEADPORTRAIT_URL, headPortraitUrl);// 设置头像属性
                            EMClient.getInstance().chatManager().sendMessage(sendMessage);//发送消息
                        }
                    }.start();
                }
            }
        });

        sendButtonImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                RxPicker.init(new LoadImage());
                RxPicker.of().single(false).camera(true).limit(1, 1).start(OKSessionActivity.this).subscribe(new Consumer<List<ImageItem>>() {
                    @Override
                    public void accept(List<ImageItem> imageItems) throws Exception {
                        if (imageItems == null || imageItems.size() == 0) {
                            showSnackbar(mToolbarAddImage, "未获选择图片", "");
                            return;
                        }

                        final ImageItem item = imageItems.get(0);
                        final String headPortraitUrl = USER_INFO_SP.getString(OKUserInfoBean.KEY_HEADPORTRAIT_URL, "");
                        mToolBarProgressBar.setVisibility(View.VISIBLE);
                        new Thread() {
                            @Override
                            public void run() {
                                //imagePath为图片本地路径,false为不发送原图,默认超过100k的图片会压缩后发给对方,需要发送原图传true
                                EMMessage sendMessage = EMMessage.createImageSendMessage(item.getPath(), false, SEND_USER_NAME);
                                messageCallBackSend = new MessageCallBack(mMsgHandler, sendMessage);
                                sendMessage.setMessageStatusCallback(messageCallBackSend); // 设置消息发送状态监听
                                sendMessage.setFrom(THIS_USER_NAME); // 设置消息发送者
                                sendMessage.setTo(SEND_USER_NAME); // 设置消息的接收者
                                sendMessage.setAttribute("FROM_" + OKUserInfoBean.KEY_NICKNAME, THIS_USER_NICKNAME); // 设置FROM昵称属性
                                sendMessage.setAttribute("TO_" + OKUserInfoBean.KEY_NICKNAME, SEND_USER_NICKNAME); // 设置TO昵称属性
                                sendMessage.setAttribute(OKUserInfoBean.KEY_HEADPORTRAIT_URL, headPortraitUrl);// 设置头像属性
                                EMClient.getInstance().chatManager().sendMessage(sendMessage);
                            }
                        }.start();
                    }
                });
            }
        });

        mToolbarBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mRefreshLayout.autoRefresh();
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        if (mLoadSessionTask != null && mLoadSessionTask.getStatus() == AsyncTask.Status.RUNNING) {
            mLoadSessionTask.cancel(true);
        }
        String msgId = "";
        if (EMMessageList.size() != 0) {
            msgId = EMMessageList.get(EMMessageList.size() - 1).getMsgId();
        }
        mLoadSessionTask = new LoadSessionTask(SEND_USER_NAME, true);
        mLoadSessionTask.executeOnExecutor(exec, msgId);
    }

    private class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.SessionViewHolder> {
        private Context mContext;
        private List<EMMessage> mBeanList;

        public SessionAdapter(Context context, List<EMMessage> list) {
            this.mContext = context;
            this.mBeanList = list;
        }

        private void initLeftViews(final SessionViewHolder viewHolder, final EMMessage mMessage, final int position) {
            if (THE_USER_INFO == null) {
                String url = mMessage.getStringAttribute(OKUserInfoBean.KEY_HEADPORTRAIT_URL, "");
                GlideRoundApi(viewHolder.leftImageViewTitle, url, R.drawable.touxian_placeholder, R.drawable.touxian_placeholder);
            } else {
                GlideRoundApi(viewHolder.leftImageViewTitle, THE_USER_INFO.getHEADPORTRAIT_URL(), R.drawable.touxian_placeholder, R.drawable.touxian_placeholder);
            }
            if (mMessage.getType() == EMMessage.Type.TXT) {
                viewHolder.leftImageViewContent.setVisibility(View.GONE);
                viewHolder.leftTextViewContent.setVisibility(View.VISIBLE);
                viewHolder.leftTextViewContent.setText(((EMTextMessageBody) mMessage.getBody()).getMessage());
                viewHolder.leftTextViewContent.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        cm.setText(viewHolder.leftTextViewContent.getText().toString());
                        showSnackbar(v, "文本已复制!", "");
                    }
                });
            } else if (mMessage.getType() == EMMessage.Type.IMAGE) {
                viewHolder.leftTextViewContent.setVisibility(View.GONE);
                viewHolder.leftImageViewContent.setVisibility(View.VISIBLE);
                String contentUrl = ((EMImageMessageBody) mMessage.getBody()).getRemoteUrl();
                GlideApi(viewHolder.leftImageViewContent, contentUrl, R.drawable.topgd1, R.drawable.topgd1);
                viewHolder.leftImageViewContent.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int location[] = new int[2];
                        viewHolder.leftImageViewContent.getLocationOnScreen(location);
                        Bundle mBundle = new Bundle();
                        mBundle.putInt("left", location[0]);
                        mBundle.putInt("top", location[1]);
                        mBundle.putInt("height", viewHolder.leftImageViewContent.getHeight());
                        mBundle.putInt("width", viewHolder.leftImageViewContent.getWidth());
                        mBundle.putString("url", ((EMImageMessageBody) mMessage.getBody()).getRemoteUrl());
                        startUserActivity(mBundle, OKDragPhotoActivity.class);
                        overridePendingTransition(0, 0);
                    }
                });
            }

            viewHolder.leftImageViewTitle.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString(OKUserInfoBean.KEY_USERNAME, SEND_USER_NAME);
                    bundle.putString(OKUserInfoBean.KEY_NICKNAME, SEND_USER_NICKNAME);
                    startUserActivity(bundle, OKHomePageActivity.class);
                }
            });
        }

        public void initRightViews(final SessionViewHolder viewHolder, final EMMessage mMessage, final int position) {
            if (ME_USER_INFO == null) {
                String url = mMessage.getStringAttribute(OKUserInfoBean.KEY_HEADPORTRAIT_URL, "");
                GlideRoundApi(viewHolder.rightImageViewTitle, url, R.drawable.touxian_placeholder, R.drawable.touxian_placeholder);
            } else {
                GlideRoundApi(viewHolder.rightImageViewTitle, ME_USER_INFO.getHEADPORTRAIT_URL(), R.drawable.touxian_placeholder, R.drawable.touxian_placeholder);
            }
            if (mMessage.getType() == EMMessage.Type.TXT) {
                viewHolder.rightImageViewContent.setVisibility(View.GONE);
                viewHolder.rightTextViewContent.setVisibility(View.VISIBLE);
                viewHolder.rightTextViewContent.setText(((EMTextMessageBody) mMessage.getBody()).getMessage());
                viewHolder.rightTextViewContent.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        cm.setText(viewHolder.rightTextViewContent.getText().toString());
                        showSnackbar(v, "文本已复制!", "");
                    }
                });
            } else if (mMessage.getType() == EMMessage.Type.IMAGE) {
                viewHolder.rightTextViewContent.setVisibility(View.GONE);
                viewHolder.rightImageViewContent.setVisibility(View.VISIBLE);
                String contentUrl = ((EMImageMessageBody) mMessage.getBody()).getRemoteUrl();
                GlideApi(viewHolder.rightImageViewContent, contentUrl, R.drawable.topgd1, R.drawable.topgd1);

                viewHolder.rightImageViewContent.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int location[] = new int[2];
                        viewHolder.rightImageViewContent.getLocationOnScreen(location);
                        Bundle mBundle = new Bundle();
                        mBundle.putInt("left", location[0]);
                        mBundle.putInt("top", location[1]);
                        mBundle.putInt("height", viewHolder.rightImageViewContent.getHeight());
                        mBundle.putInt("width", viewHolder.rightImageViewContent.getWidth());
                        mBundle.putString("url", ((EMImageMessageBody) mMessage.getBody()).getRemoteUrl());
                        startUserActivity(mBundle, OKDragPhotoActivity.class);
                        overridePendingTransition(0, 0);
                    }
                });
            }

            viewHolder.rightImageViewTitle.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString(OKUserInfoBean.KEY_USERNAME, THIS_USER_NAME);
                    bundle.putString(OKUserInfoBean.KEY_NICKNAME, THIS_USER_NICKNAME);
                    startUserActivity(bundle, OKHomePageActivity.class);
                }
            });
        }

        @Override
        public SessionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.ok_item_session, parent, false);
            return new SessionViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SessionViewHolder viewHolder, final int position) {
            viewHolder.setListPosition(position);
            EMMessage emMessage = mBeanList.get(position);
            viewHolder.setListPosition(position);
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(emMessage.getMsgTime()));
            viewHolder.mTextViewDate.setText(date);
            if (emMessage.getFrom().equals(SEND_USER_NAME)) {
                viewHolder.rightRelativeLayout.setVisibility(View.GONE);
                viewHolder.leftRelativeLayout.setVisibility(View.VISIBLE);
                initLeftViews(viewHolder, emMessage, position);
            } else {
                viewHolder.leftRelativeLayout.setVisibility(View.GONE);
                viewHolder.rightRelativeLayout.setVisibility(View.VISIBLE);
                initRightViews(viewHolder, emMessage, position);
            }
        }

        @Override
        public int getItemCount() {
            return mBeanList.size();
        }

        class SessionViewHolder extends RecyclerView.ViewHolder {
            public RelativeLayout leftRelativeLayout, rightRelativeLayout;
            public ImageView leftImageViewTitle, leftImageViewContent, rightImageViewTitle, rightImageViewContent;
            public TextView leftTextViewContent, rightTextViewContent, mTextViewDate;

            public SessionViewHolder(View itemView) {
                super(itemView);
                leftRelativeLayout = itemView.findViewById(R.id.ok_item_session_left_layout);
                rightRelativeLayout = itemView.findViewById(R.id.ok_item_session_right_layout);

                leftImageViewTitle = itemView.findViewById(R.id.ok_item_session_left_touxian_imag);
                leftImageViewContent = itemView.findViewById(R.id.ok_item_session_left_neiron_image);
                leftTextViewContent = itemView.findViewById(R.id.ok_item_session_left_neiron_text);

                rightImageViewTitle = itemView.findViewById(R.id.ok_item_session_right_touxian_imag);
                rightImageViewContent = itemView.findViewById(R.id.ok_item_session_right_neiron_image);
                rightTextViewContent = itemView.findViewById(R.id.ok_item_session_right_neiron_text);

                mTextViewDate = itemView.findViewById(R.id.ok_item_session_date_text);
            }

            private int position;

            public void setListPosition(int pos) {
                this.position = pos;
            }

            public int getListPosition() {
                return position;
            }
        }
    }

    private class LoadSessionTask extends AsyncTask<String, Void, List<EMMessage>> {
        private String mSendUserName;
        private boolean isLoadMore;

        public LoadSessionTask(String sendUserName, boolean b) {
            this.mSendUserName = sendUserName;
            this.isLoadMore = b;
        }

        @Override
        protected List<EMMessage> doInBackground(String... params) {
            if (isCancelled()) {
                return null;
            }
            List<EMMessage> list = new ArrayList<>();
            if (TextUtils.isEmpty(params[0])) {
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
                list = conversation.loadMoreMsgFromDB(params[0], 20);
            }
            if (EMMessageList != null && list != null) { // 去重复项
                for (int i = 0; i < EMMessageList.size(); i++) {
                    EMMessage oldMsg = EMMessageList.get(i);
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
            // 判断是否加载到数据,若没有则直接返回
            if (list == null || list.size() == 0) {
                mRefreshLayout.finishRefresh();
                showSnackbar(mOKRecyclerView, "没有新的消息了", "");
                return;
            }

            if (isLoadMore) {
                list.addAll(EMMessageList);
            }
            EMMessageList.clear();
            EMMessageList.addAll(list);
            mOKRecyclerView.getAdapter().notifyDataSetChanged();
            mRefreshLayout.finishRefresh();
        }
    }

    private class MessageCallBack implements EMCallBack, EMMessageListener {
        private Handler mHandler;
        private EMMessage mSendMsg;
        public final static int SEND_MESSAGE_SUCCESS = 0;
        public final static int SEND_MESSAGE_FAILURE = 1;
        public final static int RECEIVED_MESSAGE = 2;

        // 发送消息构造方法
        public MessageCallBack(Handler handler, EMMessage sendMsg) {
            this.mHandler = handler;
            this.mSendMsg = sendMsg;
        }

        // 接收消息构造方法
        public MessageCallBack(Handler handler) {
            this.mHandler = handler;
        }

        @Override
        public void onSuccess() {
            List<EMMessage> saveMsg = new ArrayList<>();
            saveMsg.add(mSendMsg);
            EMClient.getInstance().chatManager().importMessages(saveMsg);

            EMMessageList.add(mSendMsg);

            mHandler.sendEmptyMessage(SEND_MESSAGE_SUCCESS);
        }

        @Override
        public void onError(int i, String s) {
            mHandler.sendEmptyMessage(SEND_MESSAGE_FAILURE);
        }

        @Override
        public void onProgress(int i, String s) {
        }

        // 以下为消息接收回调
        @Override
        public void onMessageReceived(List<EMMessage> receivedList) {
            EMClient.getInstance().chatManager().importMessages(receivedList);
            List<EMMessage> addList = new ArrayList<>();

            boolean isSendNotice = true;
            for (EMMessage mEMMessage : receivedList) {
                String username = mEMMessage.getFrom();
                if (SEND_USER_NAME.equals(username)) {
                    addList.add(mEMMessage);
                } else if (isSendNotice) {
                    if (mEMMessage.getType() == EMMessage.Type.TXT) {
                        Bundle mBundle = new Bundle();
                        mBundle.putInt("TYPE", 0);
                        mBundle.putString("TITLE", username);
                        mBundle.putString("CONTENT", ((EMTextMessageBody) mEMMessage.getBody()).getMessage());
                        sendUserBroadcast(OKConstant.ACTION_SHOW_NOTICE, mBundle);
                        isSendNotice = false;
                    } else if (mEMMessage.getType() == EMMessage.Type.IMAGE) {
                        Bundle mBundle = new Bundle();
                        mBundle.putInt("TYPE", 0);
                        mBundle.putString("TITLE", username);
                        mBundle.putString("CONTENT", "给您发送了一张图片");
                        sendUserBroadcast(OKConstant.ACTION_SHOW_NOTICE, mBundle);
                        isSendNotice = false;
                    }
                }
            }

            if (addList.size() == 0) {
                OKLogUtil.print("接收到其他会话的消息");
                return;
            }
            EMMessageList.addAll(addList);
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

    private class LoadImage implements RxPickerImageLoader {

        @Override
        public void display(ImageView imageView, String path, int width, int height) {
            GlideApp.with(imageView.getContext()).load(path).error(R.drawable.add_image_black).centerCrop().override(width, height).into(imageView);
        }
    }
}
