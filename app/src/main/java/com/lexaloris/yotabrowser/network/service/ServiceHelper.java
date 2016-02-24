package com.lexaloris.yotabrowser.network.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.lexaloris.yotabrowser.db.DBHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class ServiceHelper {

    private static final String QUERY_HTML_SOURCE = "QUERY_HTML_SOURCE";
    public static final String ACTION_REQUEST_RESULT = "ACTION_REQUEST_RESULT";

    private static ServiceHelper instance;
    private final Context mContext;
    private Map<String, Long> mPendingRequests = new HashMap<>();
    private AtomicLong mRequestIdGenerator = new AtomicLong();


    private ServiceHelper(Context context) {
        mContext = context.getApplicationContext();
    }

    public static ServiceHelper get(Context context) {
        if (instance == null)
            instance = new ServiceHelper(context);
        return instance;
    }

    public long getSourceText(String urlText) {
        long requestId;
        if (mPendingRequests.containsKey(QUERY_HTML_SOURCE)) {
            requestId = mPendingRequests.get(QUERY_HTML_SOURCE);
        } else {
            requestId = mRequestIdGenerator.incrementAndGet();
            mPendingRequests.put(QUERY_HTML_SOURCE, requestId);
            ResultReceiver serviceCallback = new HTMLSourceResultReceiver(null, QUERY_HTML_SOURCE);
            NetworkService.startHtmlSourceService(mContext, serviceCallback, requestId, urlText);
        }
        setReceivedFalse();
        return requestId;
    }

    private void setReceivedFalse() {
        DBHelper dbHelper = new DBHelper();
        dbHelper.saveState(mContext, DBHelper.NOT_RECEIVED);
    }

    private class HTMLSourceResultReceiver extends ResultReceiver {

        private final String mResource;

        /**
         * Create a new ResultReceive to receive results.  Your
         * {@link #onReceiveResult} method will be called from the thread running
         * <var>handler</var> if given, or from an arbitrary thread if null.
         *
         * @param handler
         */
        public HTMLSourceResultReceiver(Handler handler, String resource) {
            super(handler);
            mResource = resource;
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            Intent originalRequestIntent = resultData.getParcelable(NetworkService.EXTRA_ORIGINAL_INTENT);
            if (originalRequestIntent != null) {
                long requestId = originalRequestIntent.getLongExtra(NetworkService.EXTRA_REQUEST_ID, 0);
                mPendingRequests.remove(mResource);
                Intent result = new Intent(ACTION_REQUEST_RESULT);
                result.putExtra(NetworkService.EXTRA_REQUEST_ID, requestId);
                result.putExtra(NetworkService.EXTRA_RESULT_CODE, resultCode);
                result.putExtras(resultData);
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(result);
                Log.d("ServiceHelper", "onReceiveResult");
            }
        }
    }
}
