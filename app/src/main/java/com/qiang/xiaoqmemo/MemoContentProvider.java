package com.qiang.xiaoqmemo;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * @author xiaoqiang
 * @date 19-3-18
 */
public class MemoContentProvider extends ContentProvider {

    private MyDatabaseHelper dbHelper;

    private static final String AUTHORITY = "com.qiang.xiaoqmemo";

    private static final String TABLE_MEMO = "memo";

    private static final int MEMO_DIR = 0;

    private static UriMatcher sUriMatcher;

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/memo");

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, "memo", MEMO_DIR);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new MyDatabaseHelper(getContext(), "Memo.db", null, 1);
        return true;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int deleteRows = 0;
        switch (sUriMatcher.match(uri)) {
            case MEMO_DIR:
                deleteRows = db.delete(TABLE_MEMO, selection, selectionArgs);
                break;
            default:
        }
        return deleteRows;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case MEMO_DIR:
                return "vnd.android.cursor.dir/vnd.com.qiang.xiaoqmemo.memo";
            default:
                return null;
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri ret = null;
        switch (sUriMatcher.match(uri)) {
            case MEMO_DIR:
                long memoRowId = db.insert(TABLE_MEMO, null, values);
                ret = Uri.parse("content://" + AUTHORITY + "memo/" + memoRowId);
                break;
            default:
        }
        return ret;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        switch (sUriMatcher.match(uri)) {
            case MEMO_DIR:
                cursor = db.query(TABLE_MEMO, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
        }
        return cursor;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int updateRows = 0;
        switch (sUriMatcher.match(uri)) {
            case MEMO_DIR:
                updateRows = db.update(TABLE_MEMO, values, selection, selectionArgs);
                break;
            default:
        }
        return updateRows;
    }
}