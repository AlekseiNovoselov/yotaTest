package com.lexaloris.yotabrowser.network.service;

public class Response {
    public static final int RESULT_UNEXPECTED_ERROR = 0;
    public static final int RESULT_CANNOT_SEND_MESSAGE = 1;

    public static final int RESULT_OK = 200;

    private final int mStatus;

    private final String responseString;
    

    public Response(int status, String responseString) {
        this.responseString = responseString;
        mStatus = status;
    }

    public int getStatus() {
        return mStatus;
    }

    public String getResponseString() {
        return responseString;
    }
}

