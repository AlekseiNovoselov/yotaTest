package com.lexaloris.yotabrowser.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

public class MyContentProvider extends ContentProvider {

    final String LOG_TAG = "myLogs";

    private static final String DB_NAME = "msu_yota_sqllite_db";
    private static final int DB_VERSION = 1;
    private static final String SOURCE_TABLE = "source_table";
    private static final String STATE_TABLE = "state_table";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TXT = "txt";
    public static final String COLUMN_IS_RECEIVED = "is_received";

    private static final String CREATE_SOURCE_TABLE =
            "create table " + SOURCE_TABLE + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_TXT + " text" +
                    ");";

    private static final String CREATE_STATE_TABLE =
            "create table " + STATE_TABLE + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_IS_RECEIVED + " integer" +
                    ");";

    // // Uri
    // authority
    static final String AUTHORITY = "com.lexaloris.yotabrowser.db.MyContentProvider";

    // path
    static final String HTML_SOURCE_PATH = "html_source";
    static final String STATE_PATH = "state_path";

    // Общий Uri
    public static final Uri HTML_SOURCE_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + HTML_SOURCE_PATH);
    public static  final  Uri STATE_CONTENT_URL = Uri.parse("content://"
            + AUTHORITY + "/" + STATE_PATH);

    // Типы данных
    // набор строк
    static final String HTML_SOURCE_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + HTML_SOURCE_PATH;
    static final String STATE_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + STATE_PATH;

    // одна строка
    static final String HTML_SOURCE_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
            + AUTHORITY + "." + HTML_SOURCE_PATH;

    static final String STATE_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
            + AUTHORITY + "." + STATE_PATH;

    //// UriMatcher
    // общий Uri
    static final int URI_HTML_SOURCES = 1;
    // Uri с указанным ID
    static final int URI_HTML_SOURCE_ID = 2;
    static final int URI_STATE_SOURCES = 3;
    static final int URI_STATE_ID = 4;

    // описание и создание UriMatcher
    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, HTML_SOURCE_PATH, URI_HTML_SOURCES);
        uriMatcher.addURI(AUTHORITY, HTML_SOURCE_PATH + "/#", URI_HTML_SOURCE_ID);
        uriMatcher.addURI(AUTHORITY, STATE_PATH, URI_STATE_SOURCES);
        uriMatcher.addURI(AUTHORITY, STATE_PATH + "/#", URI_STATE_ID);
    }

    DBHelper dbHelper;
    SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        Log.d(LOG_TAG, "onCreate");
        dbHelper = new DBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(LOG_TAG, "query, " + uri.toString());
        // проверяем Uri
        String id;
        String table;
        switch (uriMatcher.match(uri)) {
            case URI_HTML_SOURCES: // общий Uri
                Log.d(LOG_TAG, "URI_HTML_SOURCES");
                table = SOURCE_TABLE;
                break;
            case URI_HTML_SOURCE_ID: // Uri с ID
                id = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI_HTML_SOURCE_ID, " + id);
                // добавляем ID к условию выборки
                selection = COLUMN_ID + " = " + id;
                table = SOURCE_TABLE;
                break;
            case URI_STATE_SOURCES:
                Log.d(LOG_TAG, "URI_STATE_SOURCES");
                table = STATE_TABLE;
                break;
            case URI_STATE_ID:
                id = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI_STATE_ID, " + id);
                // добавляем ID к условию выборки
                selection = COLUMN_ID + " = " + id;
                table = STATE_TABLE;
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        Cursor cursor = null;
        if (table.equals(SOURCE_TABLE)) {
            cursor = db.query(SOURCE_TABLE, projection, selection,
                    selectionArgs, null, null, sortOrder);
            // просим ContentResolver уведомлять этот курсор
            // об изменениях данных в CONTACT_CONTENT_URI
            cursor.setNotificationUri(getContext().getContentResolver(),
                    HTML_SOURCE_CONTENT_URI);
        }
        if (table.equals(STATE_TABLE)) {
            cursor = db.query(STATE_TABLE, projection, selection,
                    selectionArgs, null, null, sortOrder);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        Log.d(LOG_TAG, "getType, " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case URI_HTML_SOURCES:
                return HTML_SOURCE_CONTENT_TYPE;
            case URI_HTML_SOURCE_ID:
                return HTML_SOURCE_CONTENT_ITEM_TYPE;
            case URI_STATE_SOURCES:
                return STATE_CONTENT_TYPE;
            case URI_STATE_ID:
                return STATE_CONTENT_ITEM_TYPE;
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d(LOG_TAG, "insert, " + uri.toString());
        if (uriMatcher.match(uri) != URI_HTML_SOURCES)
            throw new IllegalArgumentException("Wrong URI: " + uri);

        db = dbHelper.getWritableDatabase();
        long rowID = db.insert(SOURCE_TABLE, null, values);
        Uri resultUri = ContentUris.withAppendedId(HTML_SOURCE_CONTENT_URI, rowID);
        // уведомляем ContentResolver, что данные по адресу resultUri изменились
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d(LOG_TAG, "delete, " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case URI_HTML_SOURCES:
                Log.d(LOG_TAG, "URI_HTML_SOURCES");
                break;
            case URI_HTML_SOURCE_ID:
                String id = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI_HTML_SOURCE_ID, " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = COLUMN_ID + " = " + id;
                } else {
                    selection = selection + " AND " + COLUMN_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        int cnt = db.delete(SOURCE_TABLE, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.d(LOG_TAG, "update, " + uri.toString());
        String id;
        String table;
        switch (uriMatcher.match(uri)) {
            case URI_HTML_SOURCES:
                Log.d(LOG_TAG, "URI_HTML_SOURCES");
                table = SOURCE_TABLE;
                break;
            case URI_HTML_SOURCE_ID:
                id = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI_HTML_SOURCE_ID, " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = COLUMN_ID + " = " + id;
                } else {
                    selection = selection + " AND " + COLUMN_ID + " = " + id;
                }
                table = SOURCE_TABLE;
                break;
            case URI_STATE_SOURCES:
                Log.d(LOG_TAG, "URI_STATE_SOURCES");
                table = STATE_TABLE;
                break;
            case URI_STATE_ID:
                id = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI_STATE_ID, " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = COLUMN_ID + " = " + id;
                } else {
                    selection = selection + " AND " + COLUMN_ID + " = " + id;
                }
                table = STATE_TABLE;
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();

        int cnt = -1;
        if (table.equals(SOURCE_TABLE)) {
            cnt = db.update(SOURCE_TABLE, values, selection, selectionArgs);
            getContext().getContentResolver().notifyChange(uri, null);
        }
        if (table.equals(STATE_TABLE)) {
            cnt = db.update(STATE_TABLE, values, selection, selectionArgs);
        }
        return cnt;
    }

    // класс по созданию и управлению БД
    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        // создаем и заполняем БД
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_SOURCE_TABLE);
            db.execSQL(CREATE_STATE_TABLE);
            ContentValues cvh = new ContentValues();
            cvh.put(COLUMN_TXT, "initial text");
            db.insert(SOURCE_TABLE, null, cvh);
            ContentValues cvs = new ContentValues();
            cvs.put(COLUMN_IS_RECEIVED, 0);
            db.insert(STATE_TABLE, null, cvs);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
