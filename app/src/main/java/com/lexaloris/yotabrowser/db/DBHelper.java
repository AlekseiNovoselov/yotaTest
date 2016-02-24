package com.lexaloris.yotabrowser.db;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.lexaloris.yotabrowser.network.service.Response;

public class DBHelper {

    public static final int RECEIVED = 1;
    public static final int NOT_RECEIVED = 0;

    public void saveSource(Context ctx, Response response) {
        ContentValues cv = new ContentValues();
        cv.put(MyContentProvider.COLUMN_TXT, response.getResponseString());
        Uri uri = ContentUris.withAppendedId(MyContentProvider.HTML_SOURCE_CONTENT_URI, 1);
        int cnt = ctx.getContentResolver().update(uri, cv, null, null);
        Log.d("Update", "update, count = " + cnt);
    }

    public void saveState(Context ctx, int value) {
        ContentValues cv = new ContentValues();
        cv.put(MyContentProvider.COLUMN_IS_RECEIVED, value);
        Uri uri = ContentUris.withAppendedId(MyContentProvider.STATE_CONTENT_URL, 1);
        int cnt = ctx.getContentResolver().update(uri, cv, null, null);
        Log.d("Update", "update, count = " + cnt);
    }

}
