package com.lexaloris.yotabrowser.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.concurrent.atomic.AtomicBoolean;

public class NetworkHelper {
    private static AtomicBoolean isInternet = null;

    public static synchronized boolean isInternet(final Context context) {
        if (isInternet == null)
            isInternet = new AtomicBoolean(hasInternet(context));
        return isInternet.get();
    }

    private static synchronized boolean hasInternet(final Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }
}

