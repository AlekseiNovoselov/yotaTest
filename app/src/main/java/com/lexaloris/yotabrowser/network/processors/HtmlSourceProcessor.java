package com.lexaloris.yotabrowser.network.processors;

import android.content.Context;

import com.lexaloris.yotabrowser.db.DBHelper;
import com.lexaloris.yotabrowser.network.result_receivers.HtmlSourceProcessorResultListener;
import com.lexaloris.yotabrowser.network.service.Request;
import com.lexaloris.yotabrowser.network.service.Response;

public class HtmlSourceProcessor extends Processor {
    private final String mUrl;

    public HtmlSourceProcessor(Context context, HtmlSourceProcessorResultListener listener, String extraUrl) {
        super(context, listener);
        mUrl = extraUrl;
    }

    @Override
    protected Request prepareRequest() {
        return new Request(mUrl);
    }

    @Override
    protected void saveResponse(Response response) {
        DBHelper dbHelper = new DBHelper();
        dbHelper.saveSource(mContext, response);
        dbHelper.saveState(mContext, DBHelper.RECEIVED);
    }
}
