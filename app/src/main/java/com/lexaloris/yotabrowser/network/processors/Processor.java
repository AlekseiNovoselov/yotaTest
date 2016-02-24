package com.lexaloris.yotabrowser.network.processors;

import android.content.Context;

import com.lexaloris.yotabrowser.network.service.Request;
import com.lexaloris.yotabrowser.network.service.Response;

public abstract class Processor {
    protected final Context mContext;
    protected final OnProcessorResultListener mListener;

    protected Processor(Context context, OnProcessorResultListener listener) {
        mContext = context;
        mListener = listener;
    }

    public void process() {
        try {
            Request request = prepareRequest();
            Response response = request.execute();

            if (response != null) {
                saveResponse(response);
            }

            mListener.send(response);

        } catch (Exception e) {
            mListener.send(new Response(Response.RESULT_UNEXPECTED_ERROR, null));
        }

    }

    abstract protected Request prepareRequest();

    abstract protected void saveResponse(Response response);

    public interface OnProcessorResultListener {
        void send(Response response);
    }
}
