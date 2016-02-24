package com.lexaloris.yotabrowser.network.service;

import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class Request {

    private final String mUrlRoot;

    public Request(String url) {
        mUrlRoot = url;
    }

    @Nullable
    public Response execute() {
        return doRequest();
    }

    private Response doRequest() {
        String responseString = "";
        try {
            URL url = new URL(mUrlRoot);
            HttpURLConnection httpURLConnection = null;
            try {
                httpURLConnection = (HttpURLConnection) url.openConnection();
                int code = httpURLConnection.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
                httpURLConnection.disconnect();
                return new Response(Response.RESULT_CANNOT_SEND_MESSAGE, responseString);
            }

            InputStream stream;
            try {
                stream = httpURLConnection.getInputStream();
            } catch (IOException ignored) {
                return new Response(Response.RESULT_UNEXPECTED_ERROR, responseString);
            }

            Scanner scanner = new Scanner(stream);
            while (scanner.hasNextLine())
                responseString += scanner.nextLine();
            httpURLConnection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return new Response(Response.RESULT_UNEXPECTED_ERROR, responseString);
        }
        return new Response(Response.RESULT_OK, responseString);
    }
}
