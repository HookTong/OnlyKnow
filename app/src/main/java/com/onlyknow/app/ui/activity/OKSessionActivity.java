package com.onlyknow.app.ui.activity;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.dmcbig.mediapicker.PickerActivity;
import com.dmcbig.mediapicker.PickerConfig;
import com.dmcbig.mediapicker.bean.MediaBean;
import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.onlyknow.app.R;
import com.onlyknow.app.api.OKServiceResult;
import com.onlyknow.app.api.user.OKLoadSessionApi;
import com.onlyknow.app.api.user.OKManagerUserApi;
import com.onlyknow.app.db.bean.OKUserInfoBean;
import com.onlyknow.app.service.OKMainService;
import com.onlyknow.app.ui.OKBaseActivity;
import com.onlyknow.app.ui.view.OKRecyclerView;
import com.onlyknow.app.ui.view.OKSEImageView;
import com.onlyknow.app.utils.OKFileUtil;
import com.onlyknow.app.utils.OKMessageReceiveCallBack;
import com.onlyknow.app.utils.OKMessageSendCallBack;
import com.onlyknow.app.utils.OKNetUtil;
import com.scwang.smartrefresh.header.TaurusHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OKSessionActivity extends OKBaseActivity implements OnRefreshListener, OKLoadSessionApi.onCallBack, OKManagerUserApi.onCallBack {
    private RefreshLayout mRefreshLayout;
    private OKRecyclerView mOKRecyclerView;
    private SessionAdapter mSessionAdapter;

    private EditText editTextMsg;
    private OKSEImageView sendButtonMsg, sendButtonImage;

    private String meUserName;
    private String meNickName;

    private String theUserName;
    private String theNickName;
    private OKUserInfoBean theUserInfoBean;

    private OKLoadSessionApi mOKLoadSessionApi;
    private OKMessageReceiveCallBack mOKMessageReceiveCallBack;
    private OKMessageSendCallBack mOKMessageSendCallBack;
    private List<EMMessage> emMessageList = new ArrayList<>();

    private OKManagerUserApi managerUserApi;

    @SuppressLint("HandlerLeak")
    private Handler mMsgHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case OKMessageSendCallBack.SEND_MESSAGE_SUCCESS:
                    editTextMsg.setText("");
                    mToolBarProgressBar.setVisibility(View.GONE);
                    mOKRecyclerView.getAdapter().notifyDataSetChanged();
                    mOKRecyclerView.scrollToPosition(mOKRecyclerView.getAdapter().getItemCount() - 1);
                    break;
                case OKMessageSendCallBack.SEND_MESSAGE_FAILURE:
                    mToolBarProgressBar.setVisibility(View.GONE);
                    showSnackBar(mOKRecyclerView, "消息发送失败", "");
                    break;
                case OKMessageReceiveCallBack.RECEIVED_MESSAGE:
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

        if (!OKMainService.isEMLogIn || !OKNetUtil.isNet(this)) {
            Toast.makeText(this, "您未登录聊天服务器,请重新登录账号!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initSystemBar(this);
        initUserInfoSharedPreferences();

        sendUserBroadcast(ACTION_MAIN_SERVICE_REMOVE_MESSAGE_LISTENER_IM, null); // 移除服务中的消息接收监听器

        meUserName = USER_INFO_SP.getString(OKUserInfoBean.KEY_USERNAME, "");
        meNickName = USER_INFO_SP.getString(OKUserInfoBean.KEY_NICKNAME, "");
        theUserName = getIntent().getExtras().getString(OKUserInfoBean.KEY_USERNAME);
        theNickName = getIntent().getExtras().getString(OKUserInfoBean.KEY_NICKNAME);

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mOKLoadSessionApi != null) {
            mOKLoadSessionApi.cancelTask();
        }

        if (mOKMessageReceiveCallBack != null) {
            EMClient.getInstance().chatManager().removeMessageListener(mOKMessageReceiveCallBack);
        }

        sendUserBroadcast(ACTION_MAIN_SERVICE_ADD_MESSAGE_LISTENER_IM, null);// 添加服务中的消息监听器
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECT_MEDIA_REQUEST_CODE:
                if (resultCode == PickerConfig.RESULT_CODE) {
                    mSelectMediaBean = data.getParcelableArrayListExtra(PickerConfig.EXTRA_RESULT);
                    dealWith(mSelectMediaBean);
                }
                break;
            default:
                break;
        }
    }

    private void findView() {
        super.findCommonToolbarView(this);
        setSupportActionBar(mToolbar);

        mToolbarBack.setVisibility(View.VISIBLE);
        mToolbarTitle.setVisibility(View.VISIBLE);
        mToolbarTitle.setText("与 " + theNickName + " 的会话");

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
        mSessionAdapter = new SessionAdapter(this, emMessageList);
        mOKRecyclerView.setAdapter(mSessionAdapter);

        mOKMessageReceiveCallBack = new OKMessageReceiveCallBack(this, emMessageList, theUserName, mMsgHandler);
        EMClient.getInstance().chatManager().addMessageListener(mOKMessageReceiveCallBack); // 添加当前Activity的消息接收监听器

        OKManagerUserApi.Params params = new OKManagerUserApi.Params();
        params.setUsername(theUserName);
        params.setType(OKManagerUserApi.Params.TYPE_GET_INFO);
        if (managerUserApi != null) {
            managerUserApi.cancelTask();
        }
        managerUserApi = new OKManagerUserApi(this);
        managerUserApi.requestManagerUser(params, this);

        sendButtonMsg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                final String content = editTextMsg.getText().toString();
                if (!TextUtils.isEmpty(content)) {
                    final String headPortraitUrl = USER_INFO_SP.getString(OKUserInfoBean.KEY_HEAD_PORTRAIT_URL, "");
                    mToolBarProgressBar.setVisibility(View.VISIBLE);
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            //创建一条文本消息,content为消息文字内容,toChatUsername为对方用户或者群聊的id,后文皆是如此
                            EMMessage sendMessage = EMMessage.createTxtSendMessage(content, theUserName);
                            mOKMessageSendCallBack = new OKMessageSendCallBack(emMessageList, sendMessage, mMsgHandler);
                            sendMessage.setMessageStatusCallback(mOKMessageSendCallBack); // 设置消息发送状态监听
                            sendMessage.setFrom(meUserName); // 设置消息发送者
                            sendMessage.setTo(theUserName); // 设置消息的接收者
                            sendMessage.setAttribute("FROM_" + OKUserInfoBean.KEY_NICKNAME, meNickName); // 设置FROM昵称属性
                            sendMessage.setAttribute("TO_" + OKUserInfoBean.KEY_NICKNAME, theNickName); // 设置TO昵称属性
                            sendMessage.setAttribute(OKUserInfoBean.KEY_HEAD_PORTRAIT_URL, headPortraitUrl);// 设置头像属性
                            EMClient.getInstance().chatManager().sendMessage(sendMessage);//发送消息
                        }
                    }.start();
                }
            }
        });

        sendButtonImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OKSessionActivity.this, PickerActivity.class);
                intent.putExtra(PickerConfig.SELECT_MODE, PickerConfig.PICKER_IMAGE);//default image and video (Optional)
                intent.putExtra(PickerConfig.MAX_SELECT_SIZE, 3145728L); //default 180MB (Optional)
                intent.putExtra(PickerConfig.MAX_SELECT_COUNT, 1);  //default 40 (Optional)
                intent.putExtra(PickerConfig.DEFAULT_SELECTED_LIST, mSelectMediaBean); // (Optional)
                startActivityForResult(intent, SELECT_MEDIA_REQUEST_CODE);
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
        if (mOKLoadSessionApi != null) {
            mOKLoadSessionApi.cancelTask();
        }
        String topId = "";
        if (emMessageList.size() != 0) {
            topId = emMessageList.get(0).getMsgId();
        }
        mOKLoadSessionApi = new OKLoadSessionApi(this, theUserName);
        mOKLoadSessionApi.requestMessage(theUserName, topId, emMessageList, this);
    }

    private ArrayList<MediaBean> mSelectMediaBean;
    private final int SELECT_MEDIA_REQUEST_CODE = 200;

    private void dealWith(List<MediaBean> imageItems) {
        if (imageItems == null || imageItems.size() == 0) {
            showSnackBar(mToolbarAddImage, "未获选择图片", "");
            return;
        }

        final MediaBean item = imageItems.get(0);
        final String headPortraitUrl = USER_INFO_SP.getString(OKUserInfoBean.KEY_HEAD_PORTRAIT_URL, "");
        mToolBarProgressBar.setVisibility(View.VISIBLE);
        new Thread() {
            @Override
            public void run() {
                //imagePath为图片本地路径,false为不发送原图,默认超过100k的图片会压缩后发给对方,需要发送原图传true
                EMMessage sendMessage;
                if (OKFileUtil.isGifFile(item.path)) {
                    sendMessage = EMMessage.createImageSendMessage(item.path, true, theUserName);
                } else {
                    sendMessage = EMMessage.createImageSendMessage(item.path, false, theUserName);
                }
                mOKMessageSendCallBack = new OKMessageSendCallBack(emMessageList, sendMessage, mMsgHandler);
                sendMessage.setMessageStatusCallback(mOKMessageSendCallBack); // 设置消息发送状态监听
                sendMessage.setFrom(meUserName); // 设置消息发送者
                sendMessage.setTo(theUserName); // 设置消息的接收者
                sendMessage.setAttribute("FROM_" + OKUserInfoBean.KEY_NICKNAME, meNickName); // 设置FROM昵称属性
                sendMessage.setAttribute("TO_" + OKUserInfoBean.KEY_NICKNAME, theNickName); // 设置TO昵称属性
                sendMessage.setAttribute(OKUserInfoBean.KEY_HEAD_PORTRAIT_URL, headPortraitUrl);// 设置头像属性
                EMClient.getInstance().chatManager().sendMessage(sendMessage);
            }
        }.start();
    }

    @Override
    public void loadSessionComplete(List<EMMessage> list) {
        if (list == null || list.size() == 0) {// 判断是否加载到数据,若没有则直接返回
            mRefreshLayout.finishRefresh();
            showSnackBar(mOKRecyclerView, "没有新的消息了", "");
            return;
        }
        list.addAll(emMessageList);
        emMessageList.clear();
        emMessageList.addAll(list);
        mOKRecyclerView.getAdapter().notifyDataSetChanged();
        mRefreshLayout.finishRefresh();
    }

    @Override
    public void managerUserComplete(OKServiceResult<Object> result, String type, int pos) {
        if (OKManagerUserApi.Params.TYPE_GET_INFO.equals(type)) {

            if (result == null || !result.isSuccess()) return;

            theUserInfoBean = (OKUserInfoBean) result.getData();

            if (theUserInfoBean == null) return;

            theNickName = theUserInfoBean.getUserNickname();

            mToolbarTitle.setText("与 " + theNickName + " 的会话");
            mOKRecyclerView.getAdapter().notifyDataSetChanged();
            mOKRecyclerView.scrollToPosition(mOKRecyclerView.getAdapter().getItemCount() - 1);
        }
    }

    private class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.SessionViewHolder> {
        private Context mContext;
        private List<EMMessage> mBeanList;

        public SessionAdapter(Context context, List<EMMessage> list) {
            this.mContext = context;
            this.mBeanList = list;
        }

        private void initLeftViews(final SessionViewHolder viewHolder, final EMMessage mMessage, final int position) {
            if (theUserInfoBean == null) {
                String url = mMessage.getStringAttribute(OKUserInfoBean.KEY_HEAD_PORTRAIT_URL, "");
                GlideRoundApi(viewHolder.leftImageViewTitle, url, R.drawable.touxian_placeholder, R.drawable.touxian_placeholder);
            } else {
                GlideRoundApi(viewHolder.leftImageViewTitle, theUserInfoBean.getHeadPortraitUrl(), R.drawable.touxian_placeholder, R.drawable.touxian_placeholder);
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
                        showSnackBar(v, "文本已复制!", "");
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
                    bundle.putString(OKUserInfoBean.KEY_USERNAME, theUserName);
                    bundle.putString(OKUserInfoBean.KEY_NICKNAME, theNickName);
                    startUserActivity(bundle, OKHomePageActivity.class);
                }
            });
        }

        public void initRightViews(final SessionViewHolder viewHolder, final EMMessage mMessage, final int position) {
            String url = USER_INFO_SP.getString(OKUserInfoBean.KEY_HEAD_PORTRAIT_URL, "");

            if (TextUtils.isEmpty(url)) {
                url = mMessage.getStringAttribute(OKUserInfoBean.KEY_HEAD_PORTRAIT_URL, "");
                GlideRoundApi(viewHolder.rightImageViewTitle, url, R.drawable.touxian_placeholder, R.drawable.touxian_placeholder);
            } else {
                GlideRoundApi(viewHolder.rightImageViewTitle, url, R.drawable.touxian_placeholder, R.drawable.touxian_placeholder);
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
                        showSnackBar(v, "文本已复制!", "");
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
                    bundle.putString(OKUserInfoBean.KEY_USERNAME, meUserName);
                    bundle.putString(OKUserInfoBean.KEY_NICKNAME, meNickName);
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
            if (emMessage.getFrom().equals(theUserName)) {
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
}
