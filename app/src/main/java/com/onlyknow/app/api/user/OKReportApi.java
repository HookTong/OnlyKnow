package com.onlyknow.app.api.user;

import android.content.Context;
import android.os.AsyncTask;

import com.onlyknow.app.api.OKBaseApi;
import com.onlyknow.app.api.OKServiceResult;
import com.onlyknow.app.utils.OKNetUtil;

import java.util.HashMap;
import java.util.Map;

public class OKReportApi extends OKBaseApi {
    private Context context;
    private onCallBack mListener;
    private ReportTask mReportTask;

    public OKReportApi(Context cont) {
        this.context = cont;
    }

    public interface onCallBack {
        void reportComplete(OKServiceResult<Object> result, String type);
    }

    public void requestReport(Params params, onCallBack listener) {
        this.mListener = listener;
        if (OKNetUtil.isNet(context)) {
            cancelTask();
            mReportTask = new ReportTask();
            mReportTask.executeOnExecutor(exec, params);
        } else {
            mListener.reportComplete(null, params.getType());
        }
    }

    public void cancelTask() {
        if (mReportTask != null && mReportTask.getStatus() == AsyncTask.Status.RUNNING) {
            mReportTask.cancel(true);
        }
    }

    private class ReportTask extends AsyncTask<Params, Void, OKServiceResult<Object>> {
        private String mType;

        @Override
        protected OKServiceResult<Object> doInBackground(Params... params) {
            if (isCancelled()) {
                return null;
            }

            Params mParams = params[0];

            mType = mParams.getType();

            Map<String, String> map = new HashMap<>();

            map.put(Params.KEY_NAME, mParams.getUsername());

            map.put(Params.KEY_ID, String.valueOf(mParams.getId()));

            map.put(Params.KEY_REPORT_NAME, mParams.getReportUsername());

            map.put(Params.KEY_TYPE, mParams.getType());

            map.put(Params.KEY_MSG, mParams.getMessage());

            return report(map, Object.class);
        }

        @Override
        protected void onPostExecute(OKServiceResult<Object> result) {
            super.onPostExecute(result);
            if (isCancelled()) {
                return;
            }

            mListener.reportComplete(result, mType);
        }
    }

    public static class Params {
        private String type;
        private String username;
        private String reportUsername;
        private int id;
        private String message;

        public final static String KEY_TYPE = "type";
        public final static String KEY_NAME = "username";
        public final static String KEY_REPORT_NAME = "reportUsername";
        public final static String KEY_ID = "id";
        public final static String KEY_MSG = "message";

        public final static String TYPE_REPORT_CARD = "reportCard";
        public final static String TYPE_REPORT_USER = "reportUser";
        public final static String TYPE_REPORT_COMMENT = "reportComment";
        public final static String TYPE_REPORT_COMMENT_REPLY = "reportCommentReply";

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getReportUsername() {
            return reportUsername;
        }

        public void setReportUsername(String reportUsername) {
            this.reportUsername = reportUsername;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
